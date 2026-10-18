package com.pasterdream.pasterdreammod.item;

import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.SimpleTier;

import java.util.List;

/**
 * 潮汐剑 (tide_sword) — 水下呼吸被动
 * <p>
 * 对应原版 {@code TideSwordItem}（属性与新模组既有 ItemAPI 注册逐项一致：
 * 耐久 1561 / 挖速 2 / 伤害+7.5 / 攻速 -2.8 / 附魔 11）。
 * 手持时每 tick 刷新 1 秒水下呼吸（原版 TideSwordPr0Procedure，无粒子显示）。
 * 修复材料为原版海晶晶粒（Items.PRISMARINE_CRYSTALS）。
 */
public class TideSwordItem extends SwordItem {

    /** 水下呼吸刷新时长（tick），原版 20 tick = 1 秒（短时长持续刷新，不显示粒子） */
    private static final int WATER_BREATHING_TICKS = 20;

    /** 与既有 ItemAPI 注册等值的 tier（修复材料为海晶晶粒） */
    private static final SimpleTier TIER = new SimpleTier(
            BlockTags.INCORRECT_FOR_WOODEN_TOOL, 1561, 2.0f, 7.5f, 11,
            () -> Ingredient.of(net.minecraft.world.item.Items.PRISMARINE_CRYSTALS));

    public TideSwordItem(Properties properties) {
        // 攻击伤害由 tier 承载（伤害参数 0，与 ItemAPI 方案一致）
        super(TIER, properties.attributes(SwordItem.createAttributes(TIER, 0, -2.8f)));
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);
        // 原版 TideSwordPr0Procedure：手持时刷新水下呼吸（ambient/visible 均 false，无粒子）
        if (isSelected && entity instanceof LivingEntity living && !level.isClientSide()) {
            applyTidePassive(living);
        }
    }

    /**
     * 潮汐被动：刷新水下呼吸（无粒子短时长）。
     * 包内静态方法，供真潮汐剑（true_tide_sword）被动复用。
     *
     * @param living 手持者
     */
    static void applyTidePassive(LivingEntity living) {
        living.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING,
                WATER_BREATHING_TICKS, 0, false, false));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context,
                                List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        // 原版 appendHoverText 逐行还原
        tooltip.add(Component.translatable("tooltip.pasterdream.tide_sword.inlay"));
        tooltip.add(Component.translatable("tooltip.pasterdream.tide_sword.water_breathing"));
    }
}
