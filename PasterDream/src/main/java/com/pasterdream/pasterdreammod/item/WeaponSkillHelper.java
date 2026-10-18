package com.pasterdream.pasterdreammod.item;

import com.pasterdream.pasterdreammod.registry.PDAttributes;
import com.pasterdream.pasterdreammod.registry.PDParticles;
import com.pasterdream.pasterdreammod.registry.PDSounds;
import com.pasterdream.pasterdreammod.util.PasterItemData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;

/**
 * 战技武器共享逻辑 —— AtkPr0 结算 / 开启演出 / 战技冷却
 * <p>
 * 对应原版 {@code AtkPr0Procedure} 与各战技武器 Pr1 的公共段落：
 * <ul>
 *   <li>{@link #applyAtkProcedure}：写入 {@code paster_atk}（攻击力属性 + 锋利×0.5）与
 *       {@code skill_multiplier}（战技倍率基础值，沿用 TerraSwordItem 移植口径）</li>
 *   <li>{@link #playActivationFx}：脚下强化粒子（BUFF_0 ×20）+ sword1 音效</li>
 *   <li>{@link #applySkillCooldown}：给战技标签（pasterdream:skill）内物品施加
 *       基数 × SKILLCD 属性的冷却</li>
 * </ul>
 * 供 truest_moltengold_sword / true_desert_sword / true_tide_sword / dyedream_sword_0
 * 等战技武器复用（terra_sword / true_grass_sword 为先期移植，保持不动）。
 */
public final class WeaponSkillHelper {

    private WeaponSkillHelper() {
    }

    /**
     * 原版 AtkPr0Procedure：写入战技攻击加成与倍率。
     *
     * @param player 开启战技的玩家
     * @param stack  战技武器物品栈
     */
    public static void applyAtkProcedure(ServerPlayer player, ItemStack stack) {
        int sharpness = stack.getEnchantmentLevel(player.serverLevel().registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(Enchantments.SHARPNESS));
        // 原版公式：paster_atk = 攻击力属性值 + 锋利等级 × 0.5
        PasterItemData.putDouble(stack, "paster_atk",
                player.getAttributeValue(Attributes.ATTACK_DAMAGE) + sharpness * 0.5);
        AttributeInstance skillMultiplier = player.getAttribute(PDAttributes.SKILLMULTIPLIER);
        PasterItemData.putDouble(stack, "skill_multiplier",
                skillMultiplier != null ? skillMultiplier.getBaseValue() : 1);
    }

    /**
     * 战技开启公共演出：脚下强化粒子（原版 BUFF_0 ×20，y - 0.5）+ sword1 音效（PLAYERS 0.8/1）。
     *
     * @param player 开启战技的玩家
     */
    public static void playActivationFx(ServerPlayer player) {
        if (player.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles((SimpleParticleType) PDParticles.BUFF_0_PARTICLE.particleType(),
                    player.getX(), player.getY() - 0.5, player.getZ(), 20, 0.5, 1, 0.5, 1);
        }
        player.level().playSound(null,
                BlockPos.containing(player.getX(), player.getY(), player.getZ()),
                PDSounds.SWORD1.get(), SoundSource.PLAYERS, 0.8f, 1f);
    }

    /**
     * 战技标签冷却：给 pasterdream:skill 标签内所有物品施加 基数 × SKILLCD 属性 的冷却。
     * 沿用 TerraSwordItem 移植口径取 SKILLCD 基础值。
     *
     * @param player    开启战技的玩家
     * @param baseTicks 冷却基数（tick），如 truest 200 / true_desert 80 / true_tide 50
     */
    public static void applySkillCooldown(ServerPlayer player, int baseTicks) {
        AttributeInstance skillCd = player.getAttribute(PDAttributes.SKILLCD);
        double cdFactor = skillCd != null ? skillCd.getBaseValue() : 1;
        WandSupport.applyTaggedCooldown(player, WandSupport.SKILL_TAG, (int) (baseTicks * cdFactor));
    }
}
