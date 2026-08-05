package com.pasterdream.pasterdreammod.item;

import com.pasterdream.pasterdreammod.client.sky.SkyboxRenderer;
import com.pasterdream.pasterdreammod.client.sky.math.SkyPoint;
import com.pasterdream.pasterdreammod.registry.PDAdvancements;
import com.pasterdream.pasterdreammod.registry.PDEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * 羽星占卜图录 (memento_item_03) 物品类 —— 对天体进行占卜
 * <p>
 * 仅夜晚可用（白天提示晚上再用），且<b>必须先对准天空中的天体</b>
 * （星座星点、行星或玩家连线星体）再右键使用，否则提示"需要对准天体"
 * 且不消耗冷却。对准后随机触发占卜事件，各事件给予对应祝福效果并
 * 在快捷栏上方播报占卜结果：
 * <ul>
 *   <li>"今晚是个好梦~" → 梦境祝福 (dreamwish_buff)</li>
 *   <li>"运气真好~" → 寻梦者的祈愿 (memento_buff，幸运 +10)</li>
 *   <li>"星光在你指尖流转~" → 染梦附魔 (dyedreamup_buff)</li>
 * </ul>
 * 冷却 5 秒（100 tick），BUFF 持续 10 秒（200 tick）。
 * 客户端检测夜晚+对准（未通过返回 PASS 不发包），服务端夜晚兜底并执行随机与效果。
 */
public class DivinationItem extends Item {

    /** 冷却时长（tick）——5 秒 */
    private static final int COOLDOWN_TICKS = 100;
    /** BUFF 持续时长（tick）——10 秒 */
    private static final int BUFF_DURATION_TICKS = 200;
    /** 占卜随机事件数 */
    private static final int EVENT_COUNT = 3;
    /** 占卜对准天体夹角阈值（弧度）——视线需真正对准天体本体（收紧，杜绝"扫到即中"） */
    private static final float AIM_THRESHOLD = 0.08F;
    /** 视线世界空间 Y 分量下限（低于此值视为看向地面，不可占卜） */
    private static final float MIN_LOOK_Y = -0.05F;

    /**
     * @param properties 物品属性
     */
    public DivinationItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        // 使用描述 + 获取方式（宝箱开出）
        tooltipComponents.add(Component.translatable("tooltip.pasterdream.memento_item_03.effect"));
        tooltipComponents.add(Component.translatable("tooltip.pasterdream.memento_item_03.source"));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide()) {
            // 1. 仅夜晚可用：白天提示晚上再用
            if (!SkyboxRenderer.isNight()) {
                player.displayClientMessage(Component.translatable("message.pasterdream.divination.night_only"), true);
                player.playSound(SoundEvents.NOTE_BLOCK_BASS.value(), 0.6F, 0.7F);
                return InteractionResultHolder.pass(stack);
            }
            // 2. 未对准天体：提示并放弃（返回 PASS 不触发服务端占卜）
            if (!isAimingAtCelestial()) {
                player.displayClientMessage(Component.translatable("message.pasterdream.divination.aim_first"), true);
                player.playSound(SoundEvents.NOTE_BLOCK_BASS.value(), 0.6F, 0.7F);
                return InteractionResultHolder.pass(stack);
            }
            // 3. 通过校验：本地加冷却（UI 显示），返回 SUCCESS 触发服务端占卜
            player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
            return InteractionResultHolder.success(stack);
        }
        // 服务端：夜晚 + 抬头兜底校验（防止客户端异常发包导致白天/对地占卜生效），通过后执行
        if (!isServerNight(level) || player.getLookAngle().y < MIN_LOOK_Y) {
            return InteractionResultHolder.pass(stack);
        }
        player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
        if (player instanceof ServerPlayer serverPlayer) {
            divinate(serverPlayer);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    /**
     * 服务端夜晚判定（兜底，防止客户端异常发包时白天占卜生效）
     *
     * @param level 服务端世界
     * @return 是否夜晚
     */
    private static boolean isServerNight(Level level) {
        if (level.dimension() == Level.END) {
            return true;
        }
        // 与客户端夜晚因子一致：太阳低于地平线（cos < 0）即夜晚
        float sunHeight = Mth.cos(level.getSunAngle(0.0F));
        return -sunHeight * 4.0F + 0.2F > 0.5F;
    }

    /**
     * 检测玩家视线是否对准了天空中的天体（客户端）
     * <p>
     * 视线方向需逆变换为天空球局部坐标（与星空枕渲染约定一致），
     * 再查询当前候选天空中的星座星点、行星与玩家连线星体。
     *
     * @return 是否对准某天体
     */
    private static boolean isAimingAtCelestial() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) {
            return false;
        }
        // 相机视线（世界空间）
        Vec3 look = mc.player.getLookAngle();
        // 看向地面（或水平以下）不可占卜 —— 天体都在天上
        if (look.y < MIN_LOOK_Y) {
            return false;
        }
        float lookX = (float) look.x;
        float lookY = (float) look.y;
        float lookZ = (float) look.z;
        // 世界 → 天空局部（逆变换 = Y(-A)·X(90°)·look，与星空枕/SkyLinkItem 一致）
        float timeOfDay = mc.level.getTimeOfDay(1.0F);
        float rad = (float) Math.toRadians(timeOfDay * 360.0F);
        float cosA = Mth.cos(rad);
        float sinA = Mth.sin(rad);
        float sx = lookX * cosA - lookY * sinA;
        float sy = -lookZ;
        float sz = lookX * sinA + lookY * cosA;
        float length = Mth.sqrt(sx * sx + sy * sy + sz * sz);
        if (length < 0.001F) {
            return false;
        }
        SkyPoint localLook = new SkyPoint(sx, sy, sz);
        return SkyboxRenderer.isCelestialTargeted(localLook, AIM_THRESHOLD);
    }

    /**
     * 执行占卜：随机挑选一个事件并给予对应祝福
     *
     * @param player 服务端玩家
     */
    private static void divinate(ServerPlayer player) {
        int roll = player.getRandom().nextInt(EVENT_COUNT);
        switch (roll) {
            case 0 -> grant(player, PDEffects.DREAMWISH_BUFF.holder(), "message.pasterdream.divination.good_dream");
            case 1 -> grant(player, PDEffects.MEMENTO_BUFF.holder(), "message.pasterdream.divination.lucky");
            default -> grant(player, PDEffects.DYEDREAMUP_BUFF.holder(), "message.pasterdream.divination.starlight");
        }
        // 首次占卜成功 → 授予占卜成就
        PDAdvancements.award(player, "achievement_divination");
    }

    /**
     * 给予玩家祝福效果并在快捷栏上方播报占卜结果（与"请先对准天体"提示同位置）
     *
     * @param player     服务端玩家
     * @param effect     效果类型句柄
     * @param messageKey 占卜结果消息键
     */
    private static void grant(ServerPlayer player, Holder<MobEffect> effect, String messageKey) {
        player.addEffect(new MobEffectInstance(effect, BUFF_DURATION_TICKS, 0));
        player.displayClientMessage(Component.translatable(messageKey), true);
    }
}
