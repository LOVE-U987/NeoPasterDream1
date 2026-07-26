package com.pasterdream.pasterdreammod.network;

import com.pasterdream.pasterdreammod.attachment.MeltDreamEnergyData;
import com.pasterdream.pasterdreammod.attachment.PDAttachments;
import com.pasterdream.pasterdreammod.attachment.SanData;
import com.pasterdream.pasterdreammod.registry.PDAttributes;
import com.pasterdream.pasterdreammod.registry.PDEffects;
import com.pasterdream.pasterdreammod.registry.PDParticles;
import com.pasterdream.pasterdreammod.registry.PDSounds;
import com.pasterdream.pasterdreammod.registry.items.PDItemsCurios;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import top.theillusivec4.curios.api.CuriosApi;

/**
 * 网络包注册与处理器
 * <p>
 * 以 1.21.1 的 {@code CustomPacketPayload} + {@code PayloadRegistrar} 重建原版
 * SimpleChannel（{@code communication/ChannelEventTracker.java}）中与玩家数据层相关的消息：
 * <ul>
 *   <li>S2C：{@link SanDataPayload}（San 同步）、{@link MeltDreamEnergyPayload}（融梦能量同步）</li>
 *   <li>C2S：{@link TeleportationPayload}（瞬身术按键）、{@link CloakActivatePayload}（斗篷激活按键）</li>
 * </ul>
 * 由主类构造器 {@code modEventBus.addListener(PDNetwork::registerPayloads)} 接线（MOD 总线）。
 * <p>
 * 注：PayloadRegistrar 默认在主线程执行处理器，无需再手动 enqueueWork；
 * S2C 处理器通过 {@code context.player()} 取本地玩家，不直接引用客户端类，双端类加载安全。
 */
public class PDNetwork {

    /** 网络协议版本（原版 SimpleChannel PROTOCOL_VERSION = "1.0"） */
    public static final String PROTOCOL_VERSION = "1.0";

    /**
     * 注册全部网络包（MOD 总线事件）
     *
     * @param event 载荷处理器注册事件
     */
    public static void registerPayloads(final RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(PROTOCOL_VERSION);

        // ==================== S2C：玩家变量同步 ====================
        registrar.playToClient(SanDataPayload.TYPE, SanDataPayload.STREAM_CODEC,
                PDNetwork::handleSanDataOnClient);
        registrar.playToClient(MeltDreamEnergyPayload.TYPE, MeltDreamEnergyPayload.STREAM_CODEC,
                PDNetwork::handleMeltDreamEnergyOnClient);

        // ==================== C2S：按键消息 ====================
        registrar.playToServer(TeleportationPayload.TYPE, TeleportationPayload.STREAM_CODEC,
                PDNetwork::handleTeleportationOnServer);
        registrar.playToServer(CloakActivatePayload.TYPE, CloakActivatePayload.STREAM_CODEC,
                PDNetwork::handleCloakActivateOnServer);
    }

    /**
     * 客户端处理 San 同步包（对应原版 SanDataMessage.Handler）
     * <p>
     * kind=0 仅更新数值；kind=1 仅更新开关；kind=2 全量覆盖。
     *
     * @param payload 包数据
     * @param context 载荷上下文（客户端侧 player() 为本地玩家）
     */
    public static void handleSanDataOnClient(final SanDataPayload payload, final IPayloadContext context) {
        Player player = context.player();
        SanData current = player.getData(PDAttachments.PLAYER_SAN);
        SanData updated = switch (payload.kind()) {
            case SanDataPayload.KIND_VALUE_ONLY -> current.withSanValue(payload.sanValue());
            case SanDataPayload.KIND_CHECK_ONLY -> current.withSanCheck(payload.sanCheck());
            default -> new SanData(payload.sanValue(), payload.sanCheck());
        };
        player.setData(PDAttachments.PLAYER_SAN, updated);
    }

    /**
     * 客户端处理融梦能量同步包（对应原版 MeltDreamEnergyDataMessage.Handler，全量覆盖）
     *
     * @param payload 包数据
     * @param context 载荷上下文
     */
    public static void handleMeltDreamEnergyOnClient(final MeltDreamEnergyPayload payload, final IPayloadContext context) {
        Player player = context.player();
        player.setData(PDAttachments.PLAYER_MELTDREAM_ENERGY,
                new MeltDreamEnergyData(payload.meltDreamEnergy(), payload.noNeedConsume()));
    }

    /**
     * 服务端处理瞬身术按键消息（对应原版 TeleportationMessage#pressAction + TeleportationPr0Procedure）
     * <p>
     * 原版按键在客户端本地预执行 + 发包服务端重执行；新版为服务端权威：
     * 仅服务端执行一次，冲刺速度经 {@code hurtMarked} 标记由
     * {@code ClientboundSetEntityMotionPacket} 下发客户端（等价原版击退同步路径）。
     *
     * @param payload 包数据
     * @param context 载荷上下文（服务端侧 player() 为 ServerPlayer）
     */
    public static void handleTeleportationOnServer(final TeleportationPayload payload, final IPayloadContext context) {
        Player player = context.player();
        // 安全校验：防止任意区块生成（移植自原版 pressAction）
        if (!player.level().hasChunkAt(player.blockPosition())) {
            return;
        }
        if (payload.action() == 0) {
            executeTeleportation(player);
        }
    }

    /**
     * 瞬身术主流程（对应原版 TeleportationPr0Procedure，数值逐项一致）
     * <p>
     * 有 restrainmove_block_buff（禁锢）时整段跳过；否则：
     * <ul>
     *   <li>非创造模式：饥饿值 &gt; 6 且无 teleportation_buff（技能冷却）时——
     *       执行视线方向冲刺（Pr1）、按属性 teleportationconsume 扣 3 倍饥饿消耗、
     *       施加 teleportation_buff 冷却（时长 = 50 × teleportationcd 属性；
     *       胸甲为鞘翅且佩戴啵啵鸡饰品时翻倍为 100 × teleportationcd）、
     *       施加瞬身后置增益（Pr2）</li>
     *   <li>创造模式：直接执行冲刺（Pr1）与后置增益（Pr2），无消耗无冷却</li>
     *   <li>两种模式最后都写入持久化 NBT {@code evasion=true}
     *       （供 PDEffectEvents 的回避结算读取）</li>
     * </ul>
     * 原版瞬身术本身无粒子与音效；闪避演出（音效/粒子/拖尾）由 evasion_buff
     * 在受击结算时触发（见 {@code PDEffectEvents#runEvasionDodge}）。
     *
     * @param player 玩家（服务端）
     */
    private static void executeTeleportation(Player player) {
        if (player.hasEffect(PDEffects.RESTRAINMOVE_BLOCK_BUFF.holder())) {
            return;
        }
        if (!player.getAbilities().instabuild) {
            if (player.getFoodData().getFoodLevel() > 6
                    && !player.hasEffect(PDEffects.TELEPORTATION_BUFF.holder())) {
                applyTeleportationDash(player);
                // 饥饿消耗：3 × teleportationconsume 属性（原版 causeFoodExhaustion）
                player.causeFoodExhaustion(
                        (float) (3 * player.getAttributeValue(PDAttributes.TELEPORTATIONCONSUME)));
                // 冷却：鞘翅胸甲 + 啵啵鸡饰品 → 100×teleportationcd，否则 50×teleportationcd
                boolean elytraWithBoboji =
                        player.getItemBySlot(EquipmentSlot.CHEST).getItem() == Items.ELYTRA
                                && isCurioEquipped(player, PDItemsCurios.BOBO_PLUME.get());
                int cooldownTicks = (int) ((elytraWithBoboji ? 100 : 50)
                        * player.getAttributeValue(PDAttributes.TELEPORTATIONCD));
                if (!player.level().isClientSide) {
                    player.addEffect(new MobEffectInstance(
                            PDEffects.TELEPORTATION_BUFF.holder(), cooldownTicks, 0, false, false));
                }
                applyTeleportationAfterBuffs(player);
            }
        } else {
            // 创造模式：无饥饿/冷却门槛，直接冲刺 + 后置增益
            applyTeleportationDash(player);
            applyTeleportationAfterBuffs(player);
        }
        player.getPersistentData().putBoolean("evasion", true);
    }

    /**
     * 瞬身冲刺（对应原版 TeleportationPr1Procedure，速度公式逐项一致）
     * <p>
     * 距离系数取属性 teleportationrange：
     * <ul>
     *   <li>非潜行 + 水中：速度 = (视线x × range, 视线y, 视线z × range)</li>
     *   <li>非潜行 + 陆上：速度 = (视线x × range, 视线y × 0.1 + 0.4, 视线z × range)</li>
     *   <li>潜行（后撤步）：速度 = (-视线x × range, -视线y × 0.1 + 0.4, -视线z × range)</li>
     * </ul>
     * 原版依赖客户端本地执行获得位移；服务端权威模式下置 {@code hurtMarked}
     * 使运动向量下发到客户端。
     *
     * @param player 玩家
     */
    private static void applyTeleportationDash(Player player) {
        double range = player.getAttributeValue(PDAttributes.TELEPORTATIONRANGE);
        Vec3 look = player.getLookAngle();
        if (!player.isShiftKeyDown()) {
            if (player.isInWater()) {
                player.setDeltaMovement(new Vec3(look.x * range, look.y, look.z * range));
            } else {
                player.setDeltaMovement(new Vec3(look.x * range, look.y * 0.1 + 0.4, look.z * range));
            }
        } else {
            player.setDeltaMovement(new Vec3(
                    look.x * (-1) * range, look.y * (-0.1) + 0.4, look.z * (-1) * range));
        }
        // 服务端设置玩家速度必须标记 hurtMarked，经 ClientboundSetEntityMotionPacket 同步客户端
        player.hurtMarked = true;
    }

    /**
     * 瞬身后置增益（对应原版 TeleportationPr2Procedure，时长逐项一致）
     * <ul>
     *   <li>基础：evasion_buff（回避）2 tick</li>
     *   <li>佩戴十字项链：回避刷新为 6 tick</li>
     *   <li>佩戴啵啵鸡饰品：boboji_buff 16 tick</li>
     *   <li>处于 evasion_cloak_buff（闪避斗篷激活）：回避刷新为 12 tick</li>
     * </ul>
     *
     * @param player 玩家
     */
    private static void applyTeleportationAfterBuffs(Player player) {
        if (player.level().isClientSide) {
            return;
        }
        player.addEffect(new MobEffectInstance(PDEffects.EVASION_BUFF.holder(), 2, 0, false, false));
        if (isCurioEquipped(player, PDItemsCurios.CROSS_NECKLACE.get())) {
            player.addEffect(new MobEffectInstance(PDEffects.EVASION_BUFF.holder(), 6, 0, false, false));
        }
        if (isCurioEquipped(player, PDItemsCurios.BOBO_PLUME.get())) {
            player.addEffect(new MobEffectInstance(PDEffects.BOBOJI_BUFF.holder(), 16, 0, false, false));
        }
        if (player.hasEffect(PDEffects.EVASION_CLOAK_BUFF.holder())) {
            player.addEffect(new MobEffectInstance(PDEffects.EVASION_BUFF.holder(), 12, 0, false, false));
        }
    }

    /**
     * 服务端处理斗篷激活按键消息（对应原版 CloakActivateMessage#pressAction + CloakActivatePr0Procedure）
     *
     * @param payload 包数据
     * @param context 载荷上下文
     */
    public static void handleCloakActivateOnServer(final CloakActivatePayload payload, final IPayloadContext context) {
        Player player = context.player();
        // 安全校验：防止任意区块生成（移植自原版 pressAction）
        if (!player.level().hasChunkAt(player.blockPosition())) {
            return;
        }
        if (payload.action() == 0) {
            executeCloakActivate(player);
        }
    }

    /**
     * 斗篷激活主流程（对应原版 CloakActivatePr0Procedure，数值逐项一致）
     * <p>
     * 对回身斗篷 / 闪避斗篷各自独立判定：物品不在冷却中、饰品栏已佩戴、
     * 且成功消耗 10 点融梦能量时激活——
     * <ul>
     *   <li>回身斗篷：物品冷却 7800 tick，金色粒子 32 枚，
     *       施加 turnback_cloak_buff 2400 tick + evasion_buff(V级) 2400 tick</li>
     *   <li>闪避斗篷：物品冷却 7800 tick，银色粒子 32 枚，
     *       施加 evasion_cloak_buff 1800 tick</li>
     * </ul>
     * 两者激活时都播放 cloak 与 evasion 音效（NEUTRAL 频道，音量/音调 1）。
     * 原版客户端分支仅做本地音效预播，服务端权威模式下省略
     * （playSound(null,...) 广播已覆盖发起者）。
     *
     * @param player 玩家（服务端）
     */
    private static void executeCloakActivate(Player player) {
        Level world = player.level();
        double x = player.getX();
        double y = player.getY();
        double z = player.getZ();
        CuriosApi.getCuriosInventory(player).ifPresent(handler -> {
            // ---- 回身斗篷 ----
            Item turnbackCloak = PDItemsCurios.TURNBACK_CLOAK.get();
            if (!player.getCooldowns().isOnCooldown(turnbackCloak)) {
                handler.findFirstCurio(turnbackCloak).ifPresent(slot -> {
                    if (PDAttachments.consumePlayerMeltDreamEnergy(player, 10) && !world.isClientSide) {
                        player.getCooldowns().addCooldown(turnbackCloak, 7800);
                        playCloakActivateSounds(world, x, y, z);
                        ((ServerLevel) world).sendParticles(
                                (SimpleParticleType) PDParticles.GOLDEN_PARTICLE.particleType(),
                                x, y + 1, z, 32, 0.4, 0.5, 0.4, 0.01);
                        player.addEffect(new MobEffectInstance(
                                PDEffects.TURNBACK_CLOAK_BUFF.holder(), 2400, 0, false, false));
                        player.addEffect(new MobEffectInstance(
                                PDEffects.EVASION_BUFF.holder(), 2400, 5, false, false));
                    }
                });
            }
            // ---- 闪避斗篷 ----
            Item evasionCloak = PDItemsCurios.EVASION_CLOAK.get();
            if (!player.getCooldowns().isOnCooldown(evasionCloak)) {
                handler.findFirstCurio(evasionCloak).ifPresent(slot -> {
                    if (PDAttachments.consumePlayerMeltDreamEnergy(player, 10) && !world.isClientSide) {
                        player.getCooldowns().addCooldown(evasionCloak, 7800);
                        playCloakActivateSounds(world, x, y, z);
                        ((ServerLevel) world).sendParticles(
                                (SimpleParticleType) PDParticles.SILVER_PARTICLE.particleType(),
                                x, y + 1, z, 32, 0.4, 0.5, 0.4, 0.01);
                        player.addEffect(new MobEffectInstance(
                                PDEffects.EVASION_CLOAK_BUFF.holder(), 1800, 0, false, false));
                    }
                });
            }
        });
    }

    /** 斗篷激活音效：cloak + evasion 各一次（原版两次 playSound 参数一致） */
    private static void playCloakActivateSounds(Level world, double x, double y, double z) {
        world.playSound(null, BlockPos.containing(x, y, z), PDSounds.CLOAK.get(),
                SoundSource.NEUTRAL, 1, 1);
        world.playSound(null, BlockPos.containing(x, y, z), PDSounds.EVASION.get(),
                SoundSource.NEUTRAL, 1, 1);
    }

    /**
     * 判断玩家饰品栏是否佩戴指定物品（对应原版 CuriosApi.getCuriosHelper().findEquippedCurio）
     *
     * @param entity 目标实体
     * @param item   要查找的饰品物品
     * @return 已佩戴返回 true
     */
    private static boolean isCurioEquipped(LivingEntity entity, Item item) {
        return CuriosApi.getCuriosInventory(entity)
                .map(handler -> handler.findFirstCurio(item).isPresent())
                .orElse(false);
    }
}
