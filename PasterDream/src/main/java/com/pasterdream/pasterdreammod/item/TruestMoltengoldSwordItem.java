package com.pasterdream.pasterdreammod.item;

import com.pasterdream.pasterdreammod.registry.PDItems;
import com.pasterdream.pasterdreammod.registry.PDParticles;
import com.pasterdream.pasterdreammod.registry.PDSounds;
import com.pasterdream.pasterdreammod.util.PasterItemData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.SimpleTier;

import java.util.List;

/**
 * 至真熔金剑 (truest_moltengold_sword) — 战技"融骸斩击" + 叠火被动
 * <p>
 * 还原自原版 TruestMoltengoldSwordItem + TruestMoltengoldSwordPr0/Pr1Procedure：
 * <ul>
 *   <li>剑属性与既有 ItemAPI 注册逐项一致（耐久 1255、攻击加成 6、攻速 -2.15、附魔 23），
 *       修复材料熔金锭，按原版补 fireResistant（不会熔毁）</li>
 *   <li>战技（右键，原版 Pr1）：写入 paster_atk/skill_multiplier，播放 sword1 音效、
 *       强化粒子、挥剑，战技标签冷却 200 × 冷却属性（10 秒）</li>
 *   <li>战技结算（原版 Pr0 skill 分支）：skill2 + 龙息爆炸音效、熔岩粒子 ×64，
 *       造成 (2 + paster_atk + 目标剩余引燃 tick×0.03) × 倍率 的熔岩类型伤害；
 *       剩余引燃 ≥10 tick 追加缓慢 II，随后清除目标引燃并复位战技</li>
 *   <li>被动（原版 Pr0 else 分支）：未引燃点燃 4 秒；已引燃在其剩余时间上 +3 秒，可叠加</li>
 * </ul>
 */
public class TruestMoltengoldSwordItem extends SwordItem {

    /** 物品战技标记键（原版 NBT skill，布尔） */
    private static final String KEY_SKILL = "skill";

    /** 被动未引燃时的点燃时长（秒），原版 4 */
    private static final int PASSIVE_IGNITE_SECONDS = 4;

    /** 被动叠加引燃的固定增量（秒），原版 +3（比真熔金剑的 +2 更高） */
    private static final double PASSIVE_STACK_BONUS_SECONDS = 3;

    /** 战技结算的固定伤害项，原版 2 */
    private static final double SKILL_BASE_DAMAGE = 2;

    /** 剩余引燃 tick 的伤害换算系数，原版 ×0.03（≈每秒引燃 0.6 伤害） */
    private static final double REMAINING_FIRE_DAMAGE_FACTOR = 0.03;

    /** 剩余引燃达到该 tick 数时追加缓慢 II，原版 10 tick */
    private static final int SLOWDOWN_FIRE_THRESHOLD_TICKS = 10;

    /** 战技冷却基数（tick），原版 200 × SKILLCD = 10 秒 */
    private static final int SKILL_BASE_COOLDOWN_TICKS = 200;

    /** 与既有 ItemAPI 注册等值的 tier（修复材料为熔金锭） */
    private static final SimpleTier TIER = new SimpleTier(
            BlockTags.INCORRECT_FOR_WOODEN_TOOL, 1255, 2.0f, 6.0f, 23,
            () -> Ingredient.of(PDItems.MOLTENGOLD_INGOT.get()));

    public TruestMoltengoldSwordItem(Properties properties) {
        // 原版 fireResistant；攻击伤害由 tier 承载（伤害参数 0，与 ItemAPI 方案一致）
        super(TIER, properties.fireResistant().attributes(SwordItem.createAttributes(TIER, 0, -2.15f)));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        InteractionResultHolder<ItemStack> result = super.use(level, player, hand);
        // 原版 TruestMoltengoldSwordPr1Procedure：右键开启战技"融骸斩击"
        startSkill(level, player, result.getObject());
        return result;
    }

    /**
     * 原版 TruestMoltengoldSwordPr1Procedure：开启战技
     * <p>
     * 战技未开启时：写入 paster_atk/skill_multiplier（AtkPr0 等价）、播放 sword1 音效与
     * 强化粒子、挥剑，并给战技标签物品施加 200 × 冷却属性的冷却。
     *
     * @param level  世界
     * @param player 玩家
     * @param stack  至真熔金剑物品栈
     */
    private static void startSkill(Level level, Player player, ItemStack stack) {
        if (level.isClientSide() || !(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        if (PasterItemData.getBoolean(stack, KEY_SKILL)) {
            return;
        }
        PasterItemData.putBoolean(stack, KEY_SKILL, true);
        WeaponSkillHelper.applyAtkProcedure(serverPlayer, stack);
        WeaponSkillHelper.playActivationFx(serverPlayer);
        serverPlayer.swing(InteractionHand.MAIN_HAND, true);
        WeaponSkillHelper.applySkillCooldown(serverPlayer, SKILL_BASE_COOLDOWN_TICKS);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean result = super.hurtEnemy(stack, target, attacker);
        // 原版 TruestMoltengoldSwordPr0Procedure：战技结算或被动叠火
        applySkillOrPassive(target.level(), target, stack);
        return result;
    }

    /**
     * 原版 TruestMoltengoldSwordPr0Procedure：战技结算或被动叠火
     * <p>
     * 战技已开启时：播放 skill2 与龙息爆炸音效、喷发熔岩粒子 ×64，造成
     * (2 + paster_atk + 剩余引燃 tick×0.03) × 倍率 的熔岩类型伤害；剩余引燃
     * ≥10 tick 时追加缓慢 II；结算后清除目标引燃并复位战技标记。
     * 被动分支：未引燃点燃 4 秒；已引燃在剩余时间上 +3 秒（可叠加）。
     *
     * @param level  世界
     * @param target 被攻击目标
     * @param stack  至真熔金剑物品栈
     */
    private static void applySkillOrPassive(Level level, LivingEntity target, ItemStack stack) {
        if (level.isClientSide()) {
            return;
        }
        if (PasterItemData.getBoolean(stack, KEY_SKILL)) {
            BlockPos pos = BlockPos.containing(target.getX(), target.getY(), target.getZ());
            level.playSound(null, pos, PDSounds.SKILL2.get(), SoundSource.NEUTRAL, 1.5f, 1f);
            level.playSound(null, pos, SoundEvents.DRAGON_FIREBALL_EXPLODE, SoundSource.NEUTRAL, 1f, 1f);
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.LAVA,
                        target.getX(), target.getY(), target.getZ(), 64, 1.5, 1.8, 1.5, 0.5);
            }
            double damage = (SKILL_BASE_DAMAGE
                    + PasterItemData.getDouble(stack, "paster_atk")
                    + target.getRemainingFireTicks() * REMAINING_FIRE_DAMAGE_FACTOR)
                    * PasterItemData.getDouble(stack, "skill_multiplier");
            target.hurt(level.damageSources().lava(), (float) damage);
            if (target.getRemainingFireTicks() >= SLOWDOWN_FIRE_THRESHOLD_TICKS) {
                target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 1));
            }
            target.clearFire();
            PasterItemData.putBoolean(stack, KEY_SKILL, false);
        } else {
            // 被动：可叠加引燃（+3 秒）
            if (target.isOnFire()) {
                int stackSeconds = (int) (target.getRemainingFireTicks() * 0.05 + PASSIVE_STACK_BONUS_SECONDS);
                target.igniteForSeconds(stackSeconds);
            } else {
                target.igniteForSeconds(PASSIVE_IGNITE_SECONDS);
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context,
                                List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        // 原版 appendHoverText 逐行还原
        tooltip.add(Component.translatable("tooltip.pasterdream.truest_moltengold_sword.inlay"));
        tooltip.add(Component.translatable("tooltip.pasterdream.truest_moltengold_sword.skill_name"));
        tooltip.add(Component.translatable("tooltip.pasterdream.truest_moltengold_sword.lava_type"));
        tooltip.add(Component.translatable("tooltip.pasterdream.truest_moltengold_sword.damage"));
        tooltip.add(Component.translatable("tooltip.pasterdream.truest_moltengold_sword.slowdown"));
        tooltip.add(Component.translatable("tooltip.pasterdream.truest_moltengold_sword.clear_fire"));
        tooltip.add(Component.translatable("tooltip.pasterdream.truest_moltengold_sword.cooldown"));
        tooltip.add(Component.translatable("tooltip.pasterdream.truest_moltengold_sword.use_hint"));
        tooltip.add(Component.translatable("tooltip.pasterdream.truest_moltengold_sword.passive"));
        tooltip.add(Component.translatable("tooltip.pasterdream.truest_moltengold_sword.ignite"));
        tooltip.add(Component.translatable("tooltip.pasterdream.truest_moltengold_sword.ignite_stack"));
    }
}
