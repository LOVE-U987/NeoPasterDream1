package com.pasterdream.pasterdreammod.item;

import com.pasterdream.pasterdreammod.registry.PDAttributes;
import com.pasterdream.pasterdreammod.registry.PDItems;
import com.pasterdream.pasterdreammod.registry.PDParticles;
import com.pasterdream.pasterdreammod.registry.PDSounds;
import com.pasterdream.pasterdreammod.util.PasterItemData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
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
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.SimpleTier;

import java.util.List;

/**
 * 『天丛云』草薙 (true_grass_sword)
 * <p>
 * 还原自原版 TrueGrassSwordItem + TrueGrassSwordPr0/Pr1Procedure：
 * <ul>
 *   <li>剑属性与既有 ItemAPI 注册逐项一致（耐久 1311、攻击加成 6.5、攻速 -2.5、附魔能力 16），
 *       并还原原版修复材料丛林孢子</li>
 *   <li>被动：攻击命中附加中毒 5 秒，比草薙的 3 秒更长（原版 TrueGrassSwordPr0 else 分支）</li>
 *   <li>战技"极致锋利"（右键，原版 TrueGrassSwordPr1）：写入 paster_atk =
 *       7.5 × 锋利等级 × 0.5 + 攻击力属性值、skill_multiplier = 战技倍率基础值，
 *       播放 sword1 音效、喷发强化粒子、挥剑，并给战技标签内物品施加 80 × 冷却属性的冷却</li>
 *   <li>战技结算（原版 TrueGrassSwordPr0 skill 分支）：命中时播放海豚音效 + skill1 音效、
 *       喷发尘雾/孢子粒子，对首次命中的目标造成 paster_atk × skill_multiplier 的额外伤害
 *       （每个目标只触发一次），随后战技标记复位</li>
 * </ul>
 */
public class TrueGrassSwordItem extends SwordItem {

    /** 物品战技标记键（原版 NBT skill，布尔） */
    private static final String KEY_SKILL = "skill";

    /** 目标实体标记键：已吃过战技额外伤害的生物不再触发（原版 persistentData true_grass_sword） */
    private static final String KEY_TARGET_MARKED = "true_grass_sword";

    /** 被动附加的中毒时长（tick），原版 100 tick = 5 秒（比草薙更长） */
    private static final int PASSIVE_POISON_DURATION_TICKS = 100;

    /** 战技冷却基础 tick 数（原版 80 × SKILLCD 属性） */
    private static final int SKILL_BASE_COOLDOWN_TICKS = 80;

    /** 与既有 ItemAPI 注册等值的 tier：耐久 1311 / 挖速 2 / 伤害加成 6.5 / 附魔 16，修复材料丛林孢子 */
    private static final SimpleTier TIER = new SimpleTier(
            BlockTags.INCORRECT_FOR_WOODEN_TOOL, 1311, 2.0f, 6.5f, 16,
            () -> Ingredient.of(new ItemStack(PDItems.JUNGLE_SPORE.get())));

    public TrueGrassSwordItem(Properties properties) {
        // 攻击伤害由 tier 承载（伤害参数 0，与 ItemAPI 方案一致）
        super(TIER, properties.attributes(SwordItem.createAttributes(TIER, 0, -2.5f)));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        InteractionResultHolder<ItemStack> result = super.use(level, player, hand);
        // 原版 TrueGrassSwordPr1Procedure：右键开启战技"极致锋利"
        startSkill(level, player, result.getObject());
        return result;
    }

    /**
     * 原版 TrueGrassSwordPr1Procedure：开启战技"极致锋利"
     * <p>
     * 战技未开启时：写入 paster_atk（原版公式 7.5 × 锋利等级 × 0.5 + 攻击力属性值）与
     * skill_multiplier（战技倍率基础值，沿用 TerraSwordItem 既有移植口径取 baseValue），
     * 播放 sword1 音效、在脚下喷发强化粒子、挥剑，并给战技标签
     * （pasterdream:skill）内所有物品施加 80 × 冷却属性的冷却。
     *
     * @param level  世界
     * @param player 玩家
     * @param stack  『天丛云』草薙物品栈
     */
    private static void startSkill(Level level, Player player, ItemStack stack) {
        if (level.isClientSide() || !(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        if (PasterItemData.getBoolean(stack, KEY_SKILL)) {
            return;
        }
        PasterItemData.putBoolean(stack, KEY_SKILL, true);
        int sharpness = stack.getEnchantmentLevel(level.registryAccess().registryOrThrow(Registries.ENCHANTMENT)
                .getHolderOrThrow(Enchantments.SHARPNESS));
        // 原版公式：paster_atk = 7.5 × 锋利等级 × 0.5 + 攻击力属性值
        PasterItemData.putDouble(stack, "paster_atk",
                7.5 * sharpness * 0.5 + serverPlayer.getAttributeValue(Attributes.ATTACK_DAMAGE));
        AttributeInstance skillMultiplier = serverPlayer.getAttribute(PDAttributes.SKILLMULTIPLIER);
        PasterItemData.putDouble(stack, "skill_multiplier",
                skillMultiplier != null ? skillMultiplier.getBaseValue() : 1);
        // 脚下强化粒子（原版 BUFF_0 ×20，y - 0.5）
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles((SimpleParticleType) PDParticles.BUFF_0_PARTICLE.particleType(),
                    serverPlayer.getX(), serverPlayer.getY() - 0.5, serverPlayer.getZ(),
                    20, 0.5, 1, 0.5, 1);
        }
        level.playSound(null, BlockPos.containing(serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ()),
                PDSounds.SWORD1.get(), SoundSource.PLAYERS, 0.8f, 1f);
        serverPlayer.swing(InteractionHand.MAIN_HAND, true);
        // 战技标签冷却：80 × SKILLCD 属性（沿用 TerraSwordItem 移植口径取 baseValue）
        AttributeInstance skillCd = serverPlayer.getAttribute(PDAttributes.SKILLCD);
        double cdFactor = skillCd != null ? skillCd.getBaseValue() : 1;
        WandSupport.applyTaggedCooldown(serverPlayer, WandSupport.SKILL_TAG,
                (int) (SKILL_BASE_COOLDOWN_TICKS * cdFactor));
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean result = super.hurtEnemy(stack, target, attacker);
        // 原版 TrueGrassSwordPr0Procedure：战技结算或被动中毒
        applySkillOrPoison(target.level(), target, stack);
        return result;
    }

    /**
     * 原版 TrueGrassSwordPr0Procedure：战技结算或被动中毒
     * <p>
     * 战技已开启时：播放海豚音效与 skill1 音效、在目标处喷发尘雾/孢子粒子，
     * 对首次命中的目标造成 paster_atk × skill_multiplier 的额外伤害
     * （原版伤害类型 GENERIC，每个目标只触发一次），随后复位战技标记；
     * 否则走被动：附加更长时间（5 秒）的中毒。
     *
     * @param level  世界
     * @param target 被攻击目标
     * @param stack  『天丛云』草薙物品栈
     */
    private static void applySkillOrPoison(Level level, LivingEntity target, ItemStack stack) {
        if (level.isClientSide()) {
            return;
        }
        if (PasterItemData.getBoolean(stack, KEY_SKILL)) {
            BlockPos pos = BlockPos.containing(target.getX(), target.getY(), target.getZ());
            level.playSound(null, pos, SoundEvents.DOLPHIN_AMBIENT_WATER, SoundSource.NEUTRAL, 1.5f, 1f);
            level.playSound(null, pos, PDSounds.SKILL1.get(), SoundSource.NEUTRAL, 1f, 1f);
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles((SimpleParticleType) PDParticles.DUST_0_PARTICLE.particleType(),
                        target.getX(), target.getY(), target.getZ(), 64, 1.5, 1.8, 1.5, 0.2);
                serverLevel.sendParticles((SimpleParticleType) PDParticles.SPORE_PARTICLE.particleType(),
                        target.getX(), target.getY(), target.getZ(), 64, 1.5, 1.8, 1.5, 0.2);
            }
            // 原版：目标没有 true_grass_sword 标记时才造成额外伤害（每个目标仅一次）
            if (!target.getPersistentData().getBoolean(KEY_TARGET_MARKED)) {
                target.hurt(level.damageSources().generic(),
                        (float) (PasterItemData.getDouble(stack, "paster_atk")
                                * PasterItemData.getDouble(stack, "skill_multiplier")));
            }
            PasterItemData.putBoolean(stack, KEY_SKILL, false);
            target.getPersistentData().putBoolean(KEY_TARGET_MARKED, true);
        } else {
            // 被动：更长时间的中毒（5 秒）
            target.addEffect(new MobEffectInstance(MobEffects.POISON, PASSIVE_POISON_DURATION_TICKS, 0));
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context,
                                List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        // 原版 appendHoverText 逐行还原
        tooltip.add(Component.translatable("tooltip.pasterdream.true_grass_sword.inlay"));
        tooltip.add(Component.translatable("tooltip.pasterdream.true_grass_sword.skill_name"));
        tooltip.add(Component.translatable("tooltip.pasterdream.true_grass_sword.first_hit"));
        tooltip.add(Component.translatable("tooltip.pasterdream.true_grass_sword.damage"));
        tooltip.add(Component.translatable("tooltip.pasterdream.true_grass_sword.cooldown"));
        tooltip.add(Component.translatable("tooltip.pasterdream.true_grass_sword.passive"));
        tooltip.add(Component.translatable("tooltip.pasterdream.true_grass_sword.poison_longer"));
    }
}
