package com.pasterdream.pasterdreammod.item;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;

/**
 * 熔金工具共享逻辑 —— 右键急迫
 * <p>
 * 对应原版 {@code MoltengoldPickaxePr0Procedure}（熔金四件套共用）与
 * {@code TrueMoltengoldPickaxePr0Procedure}（真熔金镐，急迫更长、冷却更短）：
 * 右键使用消耗 4 点饱食度（饥饿消耗），获得限时急迫（挖掘速度提升）效果，
 * 并给该物品施加冷却。仅在服务端执行。
 * </p>
 */
public final class MoltengoldToolHelper {

    /** 右键使用消耗的饱食度（饥饿消耗值，原版 4） */
    private static final double FOOD_EXHAUSTION = 4;

    private MoltengoldToolHelper() {
    }

    /**
     * 原版 Pr0 行为：消耗饱食度、施加急迫、给物品施加冷却。
     *
     * @param player      使用者
     * @param stack       熔金工具物品栈
     * @param hasteTicks  急迫效果时长（tick），四件套 400 / 真熔金镐 600
     * @param cooldownTicks 物品冷却时长（tick），四件套 200 / 真熔金镐 160
     */
    public static void applyHaste(Player player, ItemStack stack, int hasteTicks, int cooldownTicks) {
        if (player.level().isClientSide) {
            return;
        }
        // 原版 causeFoodExhaustion(4)
        player.causeFoodExhaustion((float) FOOD_EXHAUSTION);
        player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, hasteTicks, 0));
        player.getCooldowns().addCooldown(stack.getItem(), cooldownTicks);
    }
}
