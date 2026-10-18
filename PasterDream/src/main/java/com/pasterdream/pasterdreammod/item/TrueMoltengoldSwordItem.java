package com.pasterdream.pasterdreammod.item;

import com.pasterdream.pasterdreammod.registry.PDItems;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.SimpleTier;

import java.util.List;

/**
 * 真熔金剑 (true_moltengold_sword) — 可叠加引燃被动
 * <p>
 * 对应原版 {@code TrueMoltengoldSwordItem}（属性与新模组既有 ItemAPI 注册逐项一致：
 * 耐久 1255 / 挖速 2 / 伤害+6 / 攻速 -2.2 / 附魔 23）。
 * 攻击命中：目标已引燃时在其剩余引燃时间上增加（剩余 tick×0.05+2）秒，可无限叠加；
 * 未引燃时直接引燃 4 秒（原版 TrueMoltengoldSwordPr0Procedure）。
 * 按原版补 fireResistant（不会熔毁）。
 */
public class TrueMoltengoldSwordItem extends SwordItem {

    /** 未引燃时的引燃时长（秒），原版 setSecondsOnFire(4) */
    private static final int IGNITE_SECONDS = 4;

    /** 叠加引燃的固定增量（秒），原版 +2 */
    private static final double STACK_BONUS_SECONDS = 2;

    /** 剩余引燃 tick 换算系数，原版 ×0.05 */
    private static final double REMAINING_FIRE_FACTOR = 0.05;

    /** 与既有 ItemAPI 注册等值的 tier（修复材料为熔金锭） */
    private static final SimpleTier TIER = new SimpleTier(
            BlockTags.INCORRECT_FOR_WOODEN_TOOL, 1255, 2.0f, 6.0f, 23,
            () -> Ingredient.of(PDItems.MOLTENGOLD_INGOT.get()));

    public TrueMoltengoldSwordItem(Properties properties) {
        // 原版 fireResistant；攻击伤害由 tier 承载（伤害参数 0，与 ItemAPI 方案一致）
        super(TIER, properties.fireResistant().attributes(SwordItem.createAttributes(TIER, 0, -2.2f)));
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean result = super.hurtEnemy(stack, target, attacker);
        if (!target.level().isClientSide()) {
            // 原版 TrueMoltengoldSwordPr0Procedure：已引燃则按剩余时间叠加，否则引燃 4 秒
            if (target.isOnFire()) {
                int stackSeconds = (int) (target.getRemainingFireTicks() * REMAINING_FIRE_FACTOR
                        + STACK_BONUS_SECONDS);
                target.igniteForSeconds(stackSeconds);
            } else {
                target.igniteForSeconds(IGNITE_SECONDS);
            }
        }
        return result;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context,
                                List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        // 原版 appendHoverText 逐行还原
        tooltip.add(Component.translatable("tooltip.pasterdream.true_moltengold_sword.inlay"));
        tooltip.add(Component.translatable("tooltip.pasterdream.true_moltengold_sword.ignite"));
        tooltip.add(Component.translatable("tooltip.pasterdream.true_moltengold_sword.ignite_stack"));
    }
}
