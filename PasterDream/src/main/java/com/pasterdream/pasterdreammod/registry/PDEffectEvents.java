package com.pasterdream.pasterdreammod.registry;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.api.util.ServerScheduler;
import com.pasterdream.pasterdreammod.api.effect.base.PasterDreamEffect;
import com.pasterdream.pasterdreammod.registry.items.PDItemsCurios;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import top.theillusivec4.curios.api.CuriosApi;

/**
 * 状态效果生命周期与战斗联动事件处理类
 * <p>
 * 对应原版的两套散落逻辑，集中在一个游戏总线订阅类中：
 * <ol>
 *   <li><b>效果生效/移除回调派发</b>：原版 MCreator 把生效/移除逻辑写在
 *       {@code MobEffect#addAttributeModifiers / removeAttributeModifiers} 重写里（1.20.1 专有钩子）；
 *       1.21.1 中经 NeoForge {@link MobEffectEvent.Added}/{@link MobEffectEvent.Remove}/
 *       {@link MobEffectEvent.Expired} 事件统一派发到
 *       {@link PasterDreamEffect#onApply}/{@link PasterDreamEffect#onRemove}。</li>
 *   <li><b>回避/易伤伤害联动</b>：原版 event/PDEntityEvent 的 {@code LivingDamageEvent} 处理，
 *       1.21.1 对应 {@link LivingDamageEvent.Pre}（护甲结算后、生效前，可改写最终伤害）。</li>
 *   <li><b>免疫牛奶清除</b>：原版 6 个效果重写 {@code getCurativeItems()} 返回空列表；
 *       1.21.1 改为在效果实例上清空 NeoForge EffectCure 集合。</li>
 * </ol>
 *
 * @see PDEffects
 */
@EventBusSubscriber(modid = PasterDreamMod.MOD_ID)
public final class PDEffectEvents {

    private PDEffectEvents() {
    }

    // ==================== 效果生效派发 + 免疫治愈 ====================

    /**
     * 效果添加事件：
     * <ul>
     *   <li>对原版"无解药"效果清空治愈途径（禁止牛奶清除）；</li>
     *   <li>向 {@link PasterDreamEffect} 派发 onApply 回调——
     *       仅在效果确定实际生效时派发（新挂载必然生效；等级提升必然生效，且先按旧等级
     *       派发 onRemove 再按新等级派发 onApply，对应原版 1.20.1 onEffectUpdated 的
     *       removeAttributeModifiers→addAttributeModifiers 序列）；纯延时刷新不重复派发
     *       （原版该路径为等值先撤销后重挂，净效果为零）。</li>
     * </ul>
     */
    @SubscribeEvent
    public static void onEffectAdded(MobEffectEvent.Added event) {
        MobEffectInstance instance = event.getEffectInstance();
        if (instance == null) {
            return;
        }
        // 原版 getCurativeItems() 返回空列表 → 清空治愈集合（牛奶不可清除）
        // 含 guard/restrainmove/shadow_spyon/teleportation + oppression + confusion
        MobEffect effect = instance.getEffect().value();
        if (effect == PDEffects.GUARD_BLOCK_BUFF.effect()
                || effect == PDEffects.RESTRAINMOVE_BLOCK_BUFF.effect()
                || effect == PDEffects.SHADOW_SPYON_BUFF.effect()
                || effect == PDEffects.TELEPORTATION_BUFF.effect()
                || effect == PDEffects.OPPRESSION_BUFF.get()
                || effect == PDEffects.CONFUSION_BUFF.effect()) {
            instance.getCures().clear();
        }

        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide) {
            return;
        }
        if (effect instanceof PasterDreamEffect pde) {
            MobEffectInstance old = event.getOldEffectInstance();
            if (old == null) {
                // 全新挂载（addEffect 对无同类效果的实体必然生效）
                pde.onApply(entity, instance.getAmplifier());
            } else if (instance.getAmplifier() > old.getAmplifier()) {
                // 等级提升（update 必然返回 true）：先撤销旧等级效果再按新等级生效
                pde.onRemove(entity, old.getAmplifier());
                pde.onApply(entity, instance.getAmplifier());
            }
        }
    }

    /**
     * 效果被移除事件（喝牛奶/指令/代码 removeEffect）：派发 onRemove。
     * 使用最低优先级，确保运行时移除未被其他监听者取消。
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onEffectRemoved(MobEffectEvent.Remove event) {
        dispatchRemove(event.getEntity(), event.getEffectInstance());
    }

    /** 效果自然到期事件：派发 onRemove */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onEffectExpired(MobEffectEvent.Expired event) {
        dispatchRemove(event.getEntity(), event.getEffectInstance());
    }

    /** 统一的 onRemove 派发入口（服务端） */
    private static void dispatchRemove(LivingEntity entity, MobEffectInstance instance) {
        if (instance == null || entity.level().isClientSide) {
            return;
        }
        if (instance.getEffect().value() instanceof PasterDreamEffect pde) {
            pde.onRemove(entity, instance.getAmplifier());
        }
    }

    // ==================== 回避 / 易伤 伤害联动（原版 PDEntityEvent.onEntityDamage） ====================

    /**
     * 伤害结算前置事件（护甲/魔法减免之后、生效之前，对应原版 1.20.1 LivingDamageEvent）：
     * <ol>
     *   <li><b>回避</b>：玩家持有回避效果时本次伤害归零，效果等级 -1（0 级直接移除），
     *       并执行原版 EvasionBuffPr0Procedure 的闪避演出（音效/粒子/冲刺位移/反击判定）。</li>
     *   <li><b>易伤</b>：伤害改写为 原伤害 × 0.1 × 效果等级（数值与原版一致）。</li>
     * </ol>
     */
    @SubscribeEvent
    public static void onLivingDamagePre(LivingDamageEvent.Pre event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide) {
            return;
        }
        // ---- 回避（仅玩家，原版逻辑） ----
        if (entity instanceof Player player && player.hasEffect(PDEffects.EVASION_BUFF.holder())) {
            MobEffectInstance evasion = player.getEffect(PDEffects.EVASION_BUFF.holder());
            player.removeEffect(PDEffects.EVASION_BUFF.holder());
            int level = evasion.getAmplifier();
            if (level > 0) {
                player.addEffect(new MobEffectInstance(
                        PDEffects.EVASION_BUFF.holder(), evasion.getDuration(), level - 1));
            }
            runEvasionDodge(player);
            event.setNewDamage(0.0F);
            return;
        }
        // ---- 易伤 ----
        // 原版 Fix 公式为 damage * 0.1 * amp，amp=0 时无增伤，与 tooltip「20%易伤」不符。
        // 此处按等级语义修正：amp N → +(N+1)*20% 伤害（amp0 = ×1.2，amp1 = ×1.4 …）。
        if (entity.hasEffect(PDEffects.VULNERABILITY_BUFF.holder())) {
            MobEffectInstance vulnerability = entity.getEffect(PDEffects.VULNERABILITY_BUFF.holder());
            if (vulnerability != null) {
                float mult = 1.0F + 0.2F * (vulnerability.getAmplifier() + 1);
                event.setNewDamage(event.getNewDamage() * mult);
            }
        }
    }

    // ==================== 闪避演出（EvasionBuffPr0Procedure 还原） ====================

    /**
     * 回避成功演出：音效 + 三重粒子爆发 + 反击饰品判定 + 向视线方向冲刺 +
     * 短暂速度提升 + 5 段 2tick 间隔粒子拖尾（原版 queueServerWork 链经 TickTask 等价还原）。
     */
    private static void runEvasionDodge(Player player) {
        if (!(player.level() instanceof ServerLevel level)) {
            return;
        }
        double x = player.getX(), y = player.getY(), z = player.getZ();
        // 闪避音效（sounds.json: evasion）
        level.playSound(null, BlockPos.containing(x, y, z), PDSounds.EVASION.get(),
                SoundSource.PLAYERS, 0.7F, 1.0F);
        // 首段粒子爆发
        level.sendParticles(ParticleTypes.CLOUD, x, y + 1.5, z, 12, 0.1, 0.4, 0.1, 0.05);
        level.sendParticles((SimpleParticleType) PDParticles.DUST_0_PARTICLE.particleType(),
                x, y + 0.8, z, 32, 0.3, 0.5, 0.3, 0.1);
        level.sendParticles(ParticleTypes.ELECTRIC_SPARK, x, y + 0.8, z, 24, 0.3, 0.5, 0.3, 0.1);
        player.getPersistentData().putBoolean("evasion", true);
        // 客户端 playerAnimator 闪避姿势（原版 EvasionAnimationProcedure）
        com.pasterdream.pasterdreammod.network.PDNetwork.sendEvasionPose(player);
        // 装备任一反击系饰品（反击戒指/转身斗篷/回避斗篷）→ 获得反击效果 2 秒
        // （原版为三段相同 addEffect，效果等价合并为一次判定）
        if (hasCurioEquipped(player, PDItemsCurios.COUNTER_RING.get())
                || hasCurioEquipped(player, PDItemsCurios.TURNBACK_CLOAK.get())
                || hasCurioEquipped(player, PDItemsCurios.EVASION_CLOAK.get())) {
            player.addEffect(new MobEffectInstance(PDEffects.COUNTERATTACK_BUFF, 40, 0));
        }
        // 向视线方向冲刺
        player.setSprinting(true);
        player.setDeltaMovement(new Vec3(
                player.getLookAngle().x * 0.4, player.getLookAngle().y, player.getLookAngle().z * 0.4));
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 7, 1, false, false));
        MinecraftServer server = level.getServer();
        // 7 tick 后取消冲刺（原版 queueServerWork(7)）
        ServerScheduler.schedule(7, () -> player.setSprinting(false));
        // 2/4/6/8/10 tick 的 5 段粒子拖尾（原版 5 层嵌套 queueServerWork(2)）
        for (int i = 1; i <= 5; i++) {
            ServerScheduler.schedule(i * 2, () -> {
                level.sendParticles(ParticleTypes.CLOUD,
                        player.getX(), player.getY() + 1.5, player.getZ(), 6, 0.1, 0.4, 0.1, 0.05);
                level.sendParticles((SimpleParticleType) PDParticles.DUST_0_PARTICLE.particleType(),
                        player.getX(), player.getY() + 0.8, player.getZ(), 16, 0.3, 0.5, 0.3, 0.1);
                level.sendParticles((SimpleParticleType) PDParticles.DUST_0_PARTICLE.particleType(),
                        player.getX(), player.getY() + 0.8, player.getZ(), 12, 0.3, 0.5, 0.3, 0.1);
            });
        }
    }

    /** 判断实体是否装备了指定 Curios 饰品 */
    private static boolean hasCurioEquipped(LivingEntity entity, Item item) {
        return CuriosApi.getCuriosInventory(entity)
                .map(handler -> handler.findFirstCurio(item).isPresent())
                .orElse(false);
    }
}
