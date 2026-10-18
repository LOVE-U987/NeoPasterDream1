package com.pasterdream.pasterdreammod.item;

import com.pasterdream.pasterdreammod.api.util.ServerScheduler;
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
 * 创造调试剑 (creative_sword) — 强制击杀并清除实体
 * <p>
 * 对应原版 {@code CreativeSwordItem}（属性与新模组既有 ItemAPI 注册逐项一致：
 * 耐久 100 / 挖速 2 / 伤害+9 / 攻速 +6.0（极高攻速）/ 附魔 2）。
 * 攻击命中以虚空跌落伤害源造成 1 亿点伤害，并在 1 tick 后清除目标实体
 * （原版 CreativeSwordPr0Procedure，创造模式调试工具）；
 * 修复材料为空；按原版补 fireResistant。
 */
public class CreativeSwordItem extends SwordItem {

    /** 命中伤害值，原版 100000000（1 亿） */
    private static final float KILL_DAMAGE = 100000000f;

    /** 与既有 ItemAPI 注册等值的 tier（修复材料为空） */
    private static final SimpleTier TIER = new SimpleTier(
            BlockTags.INCORRECT_FOR_WOODEN_TOOL, 100, 2.0f, 9.0f, 2,
            () -> Ingredient.EMPTY);

    public CreativeSwordItem(Properties properties) {
        // 原版 fireResistant；攻击伤害由 tier 承载（伤害参数 0，与 ItemAPI 方案一致；攻速为正 6.0）
        super(TIER, properties.fireResistant().attributes(SwordItem.createAttributes(TIER, 0, 6.0f)));
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean result = super.hurtEnemy(stack, target, attacker);
        if (!target.level().isClientSide()) {
            // 原版 CreativeSwordPr0Procedure：虚空伤害强制击杀
            target.hurt(target.level().damageSources().fellOutOfWorld(), KILL_DAMAGE);
            // 原版 queueServerWork(1)：1 tick 后清除目标实体
            ServerScheduler.schedule(1, () -> {
                if (!target.level().isClientSide()) {
                    target.discard();
                }
            });
        }
        return result;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context,
                                List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        // 原版 appendHoverText 逐行还原
        tooltip.add(Component.translatable("tooltip.pasterdream.creative_sword.debug_tool"));
        tooltip.add(Component.translatable("tooltip.pasterdream.creative_sword.debug_kill"));
    }
}
