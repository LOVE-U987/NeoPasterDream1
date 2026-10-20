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
import net.minecraft.world.entity.Entity;
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
 * 真沙漠剑 (true_desert_sword) — 战技"绝地反击" + 沙漠被动
 * <p>
 * 还原自原版 TrueDesertSwordItem + TrueDesertSwordPr0/Pr1Procedure + DesertSwordPr0Procedure：
 * <ul>
 *   <li>剑属性与既有 ItemAPI 注册逐项一致（耐久 1561、攻击加成 11、攻速 -3.1、附魔 8），
 *       修复材料为空（原版不可铁砧修复）</li>
 *   <li>战技（右键，原版 Pr1）：记录已损失生命比例（number），写入 paster_atk/skill_multiplier，
 *       获得 3 秒生命吸收（无粒子），播放 sword1 音效与强化粒子、挥剑，
 *       战技标签冷却 80 × 冷却属性（4 秒）</li>
 *   <li>战技结算（原版 Pr0 skill 分支）：沙粒碎裂 + skill1 音效、篝火烟 + 尘雾粒子 ×64，
 *       造成 (5 + (已损失生命比例×2+1) × paster_atk) × 倍率 的伤害（越残血越痛），复位战技</li>
 *   <li>被动（原版 DesertSwordPr0）：手持缓速 -15% / 抗性 +20%（代价型，无粒子）</li>
 * </ul>
 */
public class TrueDesertSwordItem extends SwordItem {

    /** 物品战技标记键（原版 NBT skill，布尔） */
    private static final String KEY_SKILL = "skill";

    /** 物品已损失生命比例记录键（原版 NBT number） */
    private static final String KEY_LOST_HEALTH_RATIO = "number";

    /** 战技开启时的生命吸收时长（tick），原版 60 tick = 3 秒（无粒子） */
    private static final int ABSORPTION_TICKS = 60;

    /** 战技结算的固定伤害项，原版 5 */
    private static final double SKILL_BASE_DAMAGE = 5;

    /** 已损失生命比例的伤害系数，原版 ×2 */
    private static final double LOST_HEALTH_RATIO_FACTOR = 2;

    /** 战技冷却基数（tick），原版 80 × SKILLCD = 4 秒 */
    private static final int SKILL_BASE_COOLDOWN_TICKS = 80;

    /** 与既有 ItemAPI 注册等值的 tier（修复材料为空） */
    private static final SimpleTier TIER = new SimpleTier(
            BlockTags.INCORRECT_FOR_WOODEN_TOOL, 1561, 2.0f, 11.0f, 8,
            () -> Ingredient.EMPTY);

    public TrueDesertSwordItem(Properties properties) {
        // 攻击伤害由 tier 承载（伤害参数 0，与 ItemAPI 方案一致）
        super(TIER, properties.attributes(SwordItem.createAttributes(TIER, 0, -3.1f)));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        InteractionResultHolder<ItemStack> result = super.use(level, player, hand);
        // 原版 TrueDesertSwordPr1Procedure：右键开启战技"绝地反击"
        startSkill(level, player, result.getObject());
        return result;
    }

    /**
     * 原版 TrueDesertSwordPr1Procedure：开启战技
     * <p>
     * 战技未开启时：记录已损失生命比例（(上限-当前)/上限）、写入 paster_atk/skill_multiplier、
     * 获得 3 秒生命吸收（无粒子）、播放 sword1 音效与强化粒子、挥剑，并给战技标签物品
     * 施加 80 × 冷却属性的冷却。
     *
     * @param level  世界
     * @param player 玩家
     * @param stack  真沙漠剑物品栈
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
        // 原版公式：number = (最大生命 - 当前生命) / 最大生命
        double maxHealth = serverPlayer.getMaxHealth();
        double lostRatio = (maxHealth - serverPlayer.getHealth()) / maxHealth;
        PasterItemData.putDouble(stack, KEY_LOST_HEALTH_RATIO, lostRatio);
        serverPlayer.addEffect(new MobEffectInstance(MobEffects.ABSORPTION,
                ABSORPTION_TICKS, 0, false, false));
        WeaponSkillHelper.playActivationFx(serverPlayer);
        serverPlayer.swing(InteractionHand.MAIN_HAND, true);
        WeaponSkillHelper.applySkillCooldown(serverPlayer, SKILL_BASE_COOLDOWN_TICKS);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean result = super.hurtEnemy(stack, target, attacker);
        // 原版 TrueDesertSwordPr0Procedure：战技结算（无被动分支）
        applySkillSettle(target.level(), target, stack);
        return result;
    }

    /**
     * 原版 TrueDesertSwordPr0Procedure：战技结算
     * <p>
     * 战技已开启时：播放沙粒碎裂与 skill1 音效、喷发篝火烟与尘雾粒子 ×64，
     * 造成 (5 + (已损失生命比例×2+1) × paster_atk) × 倍率 的伤害后复位战技标记。
     *
     * @param level  世界
     * @param target 被攻击目标
     * @param stack  真沙漠剑物品栈
     */
    private static void applySkillSettle(Level level, LivingEntity target, ItemStack stack) {
        if (level.isClientSide() || !PasterItemData.getBoolean(stack, KEY_SKILL)) {
            return;
        }
        BlockPos pos = BlockPos.containing(target.getX(), target.getY(), target.getZ());
        level.playSound(null, pos, SoundEvents.SAND_BREAK, SoundSource.NEUTRAL, 1.5f, 1f);
        level.playSound(null, pos, PDSounds.SKILL1.get(), SoundSource.NEUTRAL, 1f, 1f);
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                    target.getX(), target.getY(), target.getZ(), 64, 1.5, 1.8, 1.5, 0.2);
            serverLevel.sendParticles((SimpleParticleType) PDParticles.DUST_0_PARTICLE.particleType(),
                    target.getX(), target.getY(), target.getZ(), 64, 1.5, 1.8, 1.5, 0.2);
        }
        double damage = (SKILL_BASE_DAMAGE
                + (PasterItemData.getDouble(stack, KEY_LOST_HEALTH_RATIO) * LOST_HEALTH_RATIO_FACTOR + 1)
                * PasterItemData.getDouble(stack, "paster_atk"))
                * PasterItemData.getDouble(stack, "skill_multiplier");
        target.hurt(level.damageSources().generic(), (float) damage);
        PasterItemData.putBoolean(stack, KEY_SKILL, false);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);
        // 原版 DesertSwordPr0Procedure：手持时刷新沙漠被动（与沙漠剑共用）
        if (isSelected && entity instanceof LivingEntity living && !level.isClientSide()) {
            DesertSwordItem.applyDesertPassive(living);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context,
                                List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        // 原版 appendHoverText 逐行还原
        tooltip.add(Component.translatable("tooltip.pasterdream.true_desert_sword.inlay"));
        tooltip.add(Component.translatable("tooltip.pasterdream.true_desert_sword.skill_name"));
        tooltip.add(Component.translatable("tooltip.pasterdream.true_desert_sword.absorption"));
        tooltip.add(Component.translatable("tooltip.pasterdream.true_desert_sword.damage"));
        tooltip.add(Component.translatable("tooltip.pasterdream.true_desert_sword.cooldown"));
        tooltip.add(Component.translatable("tooltip.pasterdream.true_desert_sword.passive"));
        tooltip.add(Component.translatable("tooltip.pasterdream.true_desert_sword.passive_buff"));
    }
}
