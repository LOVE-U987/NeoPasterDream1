package com.pasterdream.pasterdreammod.item;

import com.pasterdream.pasterdreammod.registry.PDEffects;
import com.pasterdream.pasterdreammod.registry.PDItems;
import com.pasterdream.pasterdreammod.registry.PDParticles;
import com.pasterdream.pasterdreammod.registry.PDSounds;
import com.pasterdream.pasterdreammod.util.PasterItemData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
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
 * 极锋染梦合金剑 (dyedream_sword_0) — 战技"凌空斩"
 * <p>
 * 还原自原版 DyedreamSword0Item + DyedreamSwordPr0/Pr1Procedure：
 * <ul>
 *   <li>剑属性与既有 ItemAPI 注册逐项一致（耐久 1314、攻击加成 8、攻速 -2.4、附魔 22），
 *       修复材料染梦锭；无被动</li>
 *   <li>战技（右键，原版 Pr1）：写入 paster_atk/skill_multiplier（AtkPr0 等价）、播放
 *       sword1 音效与强化粒子、挥剑；拥有染梦守护效果时战技标签冷却 60 × 冷却属性（3 秒），
 *       否则 100 × 冷却属性（5 秒）</li>
 *   <li>战技结算（原版 Pr0 skill 分支）：斩击粒子 ×40 + skill1 音效，造成
 *       (2 + paster_atk×1.2) × 倍率 伤害并将目标向上击飞（速度 0.8），复位战技</li>
 * </ul>
 */
public class DyedreamSword0Item extends SwordItem {

    /** 物品战技标记键（原版 NBT skill，布尔） */
    private static final String KEY_SKILL = "skill";

    /** 战技结算的固定伤害项，原版 2 */
    private static final double SKILL_BASE_DAMAGE = 2;

    /** 战技攻击加成系数，原版 paster_atk ×1.2 */
    private static final double SKILL_ATK_FACTOR = 1.2;

    /** 目标上击飞速度，原版 y +0.8 */
    private static final double LAUNCH_SPEED = 0.8;

    /** 拥有染梦守护时的冷却基数（tick），原版 60 × SKILLCD = 3 秒 */
    private static final int COOLDOWN_WITH_BUFF_TICKS = 60;

    /** 无染梦守护时的冷却基数（tick），原版 100 × SKILLCD = 5 秒 */
    private static final int COOLDOWN_BASE_TICKS = 100;

    /** 与既有 ItemAPI 注册等值的 tier（修复材料为染梦锭） */
    private static final SimpleTier TIER = new SimpleTier(
            BlockTags.INCORRECT_FOR_WOODEN_TOOL, 1314, 2.0f, 8.0f, 22,
            () -> Ingredient.of(PDItems.DYEDREAM_INGOT.get()));

    public DyedreamSword0Item(Properties properties) {
        // 攻击伤害由 tier 承载（伤害参数 0，与 ItemAPI 方案一致）
        super(TIER, properties.attributes(SwordItem.createAttributes(TIER, 0, -2.4f)));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        InteractionResultHolder<ItemStack> result = super.use(level, player, hand);
        // 原版 DyedreamSwordPr1Procedure：右键开启战技"凌空斩"
        startSkill(level, player, result.getObject());
        return result;
    }

    /**
     * 原版 DyedreamSwordPr1Procedure：开启战技
     * <p>
     * 战技未开启时：置位战技标记、播放 sword1 音效与强化粒子、写入 paster_atk/skill_multiplier、
     * 挥剑；拥有染梦守护（DYEDREAM_ARMOR_BUFF）时给战技标签物品施加 60 × 冷却属性的冷却，
     * 否则 100 ×（原版分支结构）。
     *
     * @param level  世界
     * @param player 玩家
     * @param stack  极锋染梦合金剑物品栈
     */
    private static void startSkill(Level level, Player player, ItemStack stack) {
        if (level.isClientSide() || !(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        if (PasterItemData.getBoolean(stack, KEY_SKILL)) {
            return;
        }
        PasterItemData.putBoolean(stack, KEY_SKILL, true);
        WeaponSkillHelper.playActivationFx(serverPlayer);
        WeaponSkillHelper.applyAtkProcedure(serverPlayer, stack);
        serverPlayer.swing(InteractionHand.MAIN_HAND, true);
        // 原版分支：染梦守护缩短战技冷却
        int cooldownBase = serverPlayer.hasEffect(PDEffects.DYEDREAM_ARMOR_BUFF.holder())
                ? COOLDOWN_WITH_BUFF_TICKS : COOLDOWN_BASE_TICKS;
        WeaponSkillHelper.applySkillCooldown(serverPlayer, cooldownBase);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean result = super.hurtEnemy(stack, target, attacker);
        // 原版 DyedreamSwordPr0Procedure：战技结算（无被动分支）
        applySkillSettle(target.level(), target, stack);
        return result;
    }

    /**
     * 原版 DyedreamSwordPr0Procedure：战技结算
     * <p>
     * 战技已开启时：在目标处喷发斩击粒子 ×40（y +0.3，窄范围）、播放 skill1 音效
     * （原版声源为 PLAYERS），造成 (2 + paster_atk×1.2) × 倍率 伤害并将目标向上击飞
     * （速度 0.8），复位战技标记。
     *
     * @param level  世界
     * @param target 被攻击目标
     * @param stack  极锋染梦合金剑物品栈
     */
    private static void applySkillSettle(Level level, LivingEntity target, ItemStack stack) {
        if (level.isClientSide() || !PasterItemData.getBoolean(stack, KEY_SKILL)) {
            return;
        }
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles((SimpleParticleType) PDParticles.ATTACK_0_PARTICLE.particleType(),
                    target.getX(), target.getY() + 0.3, target.getZ(), 40, 0.05, 0.3, 0.05, 0.4);
        }
        level.playSound(null, BlockPos.containing(target.getX(), target.getY(), target.getZ()),
                PDSounds.SKILL1.get(), SoundSource.PLAYERS, 1.5f, 1f);
        double damage = (SKILL_BASE_DAMAGE
                + PasterItemData.getDouble(stack, "paster_atk") * SKILL_ATK_FACTOR)
                * PasterItemData.getDouble(stack, "skill_multiplier");
        target.hurt(level.damageSources().generic(), (float) damage);
        target.setDeltaMovement(new Vec3(0, LAUNCH_SPEED, 0));
        PasterItemData.putBoolean(stack, KEY_SKILL, false);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context,
                                List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        // 原版 appendHoverText 逐行还原
        tooltip.add(Component.translatable("tooltip.pasterdream.dyedream_sword_0.inlay"));
        tooltip.add(Component.translatable("tooltip.pasterdream.dyedream_sword_0.skill_name"));
        tooltip.add(Component.translatable("tooltip.pasterdream.dyedream_sword_0.launch"));
        tooltip.add(Component.translatable("tooltip.pasterdream.dyedream_sword_0.damage"));
        tooltip.add(Component.translatable("tooltip.pasterdream.dyedream_sword_0.cooldown"));
        tooltip.add(Component.translatable("tooltip.pasterdream.dyedream_sword_0.buff_cooldown"));
        tooltip.add(Component.translatable("tooltip.pasterdream.dyedream_sword_0.use_hint"));
    }
}
