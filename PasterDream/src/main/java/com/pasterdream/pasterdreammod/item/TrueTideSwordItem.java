package com.pasterdream.pasterdreammod.item;

import com.pasterdream.pasterdreammod.registry.PDItems;
import com.pasterdream.pasterdreammod.registry.PDSounds;
import com.pasterdream.pasterdreammod.util.PasterItemData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.SimpleTier;

import java.util.List;

/**
 * 真潮汐剑 (true_tide_sword) — 战技"涌流剑技" + 水下呼吸被动
 * <p>
 * 还原自原版 TrueTideSwordItem + TrueTideSwordPr0/Pr1Procedure + TideSwordPr0Procedure：
 * <ul>
 *   <li>剑属性与既有 ItemAPI 注册逐项一致（耐久 1561、攻击加成 8、攻速 -2.8、附魔 11），
 *       修复材料海晶晶粒</li>
 *   <li>战技（右键，原版 Pr1）：在水中时向视线方向冲刺（初速 = 视线×2）并获得
 *       短时抗性 IV（免疫 80% 伤害，无粒子）、挥剑，战技标签冷却 50 × 冷却属性（2.5 秒）；
 *       战技未开启时写入 paster_atk/skill_multiplier、播放 sword1 音效与强化粒子
 *       （冲刺分支独立于战技开启状态，忠实原版结构）</li>
 *   <li>战技结算（原版 Pr0 skill 分支）：水柱 + skill1 音效、气泡/气泡破裂粒子 ×64，
 *       目标在水中时造成 (3 + 1.2×paster_atk) × 倍率 伤害，否则造成 paster_atk 伤害，复位战技</li>
 *   <li>被动（原版 TideSwordPr0）：手持水下呼吸（无粒子）</li>
 * </ul>
 */
public class TrueTideSwordItem extends SwordItem {

    /** 物品战技标记键（原版 NBT skill，布尔） */
    private static final String KEY_SKILL = "skill";

    /** 冲刺期间抗性时长（tick），原版 18 tick（无粒子） */
    private static final int RESISTANCE_TICKS = 18;

    /** 冲刺期间抗性等级，原版 amplifier 3（免疫 80% 伤害） */
    private static final int RESISTANCE_AMPLIFIER = 3;

    /** 冲刺初速系数（视线方向 ×），原版 2 */
    private static final double DASH_SPEED_FACTOR = 2;

    /** 战技冷却基数（tick），原版 50 × SKILLCD = 2.5 秒 */
    private static final int SKILL_BASE_COOLDOWN_TICKS = 50;

    /** 与既有 ItemAPI 注册等值的 tier（修复材料为海晶晶粒） */
    private static final SimpleTier TIER = new SimpleTier(
            BlockTags.INCORRECT_FOR_WOODEN_TOOL, 1561, 2.0f, 8.0f, 11,
            () -> Ingredient.of(net.minecraft.world.item.Items.PRISMARINE_CRYSTALS));

    public TrueTideSwordItem(Properties properties) {
        // 攻击伤害由 tier 承载（伤害参数 0，与 ItemAPI 方案一致）
        super(TIER, properties.attributes(SwordItem.createAttributes(TIER, 0, -2.8f)));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        InteractionResultHolder<ItemStack> result = super.use(level, player, hand);
        // 原版 TrueTideSwordPr1Procedure：水中冲刺 + 战技开启
        startSkill(level, player, result.getObject());
        return result;
    }

    /**
     * 原版 TrueTideSwordPr1Procedure：水中冲刺与战技开启
     * <p>
     * 玩家在水中/气泡中时：获得 18 tick 抗性 IV（无粒子）、按视线方向 ×2 冲刺、
     * 挥剑，并给战技标签物品施加 50 × 冷却属性的冷却（独立于战技开启状态）；
     * 战技未开启时：写入 paster_atk/skill_multiplier（AtkPr0 等价）、播放 sword1 音效
     * 与强化粒子（无挥手，忠实原版结构）。
     *
     * @param level  世界
     * @param player 玩家
     * @param stack  真潮汐剑物品栈
     */
    private static void startSkill(Level level, Player player, ItemStack stack) {
        if (level.isClientSide() || !(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        // 水中冲刺分支：无论战技状态是否开启均执行（忠实原版）
        if (serverPlayer.isInWaterOrBubble()) {
            serverPlayer.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE,
                    RESISTANCE_TICKS, RESISTANCE_AMPLIFIER, false, false));
            Vec3 look = serverPlayer.getLookAngle();
            serverPlayer.setDeltaMovement(look.x * DASH_SPEED_FACTOR, look.y * DASH_SPEED_FACTOR,
                    look.z * DASH_SPEED_FACTOR);
            // 服务端对玩家 setDeltaMovement 不会广播速度包（客户端物理直接覆盖），
            // 置 hurtMarked 让服务端下一 tick 发送运动包，冲刺冲量才能到达客户端
            // （原版未置位，玩家侧冲刺实际无效，属对原版的有意修复）
            serverPlayer.hurtMarked = true;
            serverPlayer.swing(InteractionHand.MAIN_HAND, true);
            WeaponSkillHelper.applySkillCooldown(serverPlayer, SKILL_BASE_COOLDOWN_TICKS);
        }
        // 战技开启分支
        if (!PasterItemData.getBoolean(stack, KEY_SKILL)) {
            PasterItemData.putBoolean(stack, KEY_SKILL, true);
            WeaponSkillHelper.applyAtkProcedure(serverPlayer, stack);
            WeaponSkillHelper.playActivationFx(serverPlayer);
        }
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean result = super.hurtEnemy(stack, target, attacker);
        // 原版 TrueTideSwordPr0Procedure：战技结算（无被动分支）
        applySkillSettle(target.level(), target, stack);
        return result;
    }

    /**
     * 原版 TrueTideSwordPr0Procedure：战技结算
     * <p>
     * 战技已开启时：播放水柱与 skill1 音效、喷发气泡/气泡破裂粒子 ×64；
     * 目标在水中/气泡中时造成 (3 + 1.2×paster_atk) × 倍率 伤害，否则造成 paster_atk
     * 伤害（原版无倍率项）；结算后复位战技标记。
     *
     * @param level  世界
     * @param target 被攻击目标
     * @param stack  真潮汐剑物品栈
     */
    private static void applySkillSettle(Level level, LivingEntity target, ItemStack stack) {
        if (level.isClientSide() || !PasterItemData.getBoolean(stack, KEY_SKILL)) {
            return;
        }
        BlockPos pos = BlockPos.containing(target.getX(), target.getY(), target.getZ());
        level.playSound(null, pos, SoundEvents.BUBBLE_COLUMN_UPWARDS_INSIDE, SoundSource.NEUTRAL, 1.5f, 1f);
        level.playSound(null, pos, PDSounds.SKILL1.get(), SoundSource.NEUTRAL, 1f, 1f);
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.BUBBLE,
                    target.getX(), target.getY(), target.getZ(), 64, 1.5, 1.8, 1.5, 0.2);
            serverLevel.sendParticles(ParticleTypes.BUBBLE_POP,
                    target.getX(), target.getY(), target.getZ(), 64, 1.5, 1.8, 1.5, 0.2);
        }
        double damage;
        if (target.isInWaterOrBubble()) {
            damage = (3 + 1.2 * PasterItemData.getDouble(stack, "paster_atk"))
                    * PasterItemData.getDouble(stack, "skill_multiplier");
        } else {
            damage = PasterItemData.getDouble(stack, "paster_atk");
        }
        target.hurt(level.damageSources().generic(), (float) damage);
        PasterItemData.putBoolean(stack, KEY_SKILL, false);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);
        // 原版 TideSwordPr0Procedure：手持时刷新水下呼吸（与潮汐剑共用）
        if (isSelected && entity instanceof LivingEntity living && !level.isClientSide()) {
            TideSwordItem.applyTidePassive(living);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context,
                                List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        // 原版 appendHoverText 逐行还原
        tooltip.add(Component.translatable("tooltip.pasterdream.true_tide_sword.inlay"));
        tooltip.add(Component.translatable("tooltip.pasterdream.true_tide_sword.skill_name"));
        tooltip.add(Component.translatable("tooltip.pasterdream.true_tide_sword.dash"));
        tooltip.add(Component.translatable("tooltip.pasterdream.true_tide_sword.water_condition"));
        tooltip.add(Component.translatable("tooltip.pasterdream.true_tide_sword.damage"));
        tooltip.add(Component.translatable("tooltip.pasterdream.true_tide_sword.cooldown"));
        tooltip.add(Component.translatable("tooltip.pasterdream.true_tide_sword.passive"));
        tooltip.add(Component.translatable("tooltip.pasterdream.true_tide_sword.water_breathing"));
    }
}
