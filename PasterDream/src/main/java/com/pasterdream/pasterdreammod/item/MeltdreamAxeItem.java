package com.pasterdream.pasterdreammod.item;

import com.pasterdream.pasterdreammod.registry.PDItems;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.SimpleTier;

import java.util.List;

/**
 * 融梦水晶斧 (meltdream_axe) — 融梦修补
 * <p>
 * 对应原版 {@code MeltdreamAxeItem}（属性与新模组既有 ItemAPI 注册逐项一致：
 * 耐久 250 / 挖速 6 / 伤害+7 / 攻速 -3.0 / 铁级不适用标签 / 附魔 5）。
 * 手持时每 10 tick 消耗 0.01 融梦能量修复 1 点耐久（见 {@link MeltdreamToolHelper}）；
 * 铁砧修复材料为融梦水晶碎片（原版行为）。按原版补 fireResistant（不会熔毁）。
 */
public class MeltdreamAxeItem extends AxeItem {

    /** 与既有 ItemAPI 注册等值的 tier（修复材料为融梦水晶碎片） */
    private static final SimpleTier TIER = new SimpleTier(
            BlockTags.INCORRECT_FOR_IRON_TOOL, 250, 6.0f, 7.0f, 5,
            () -> Ingredient.of(PDItems.MELTDREAM_CRYSTAL_0.get()));

    /**
     * 构造融梦水晶斧。
     *
     * @param properties 物品属性
     */
    public MeltdreamAxeItem(Properties properties) {
        // 原版 fireResistant；攻击伤害由 tier 承载（伤害参数 0，与 ItemAPI 方案一致）
        super(TIER, properties.fireResistant().attributes(AxeItem.createAttributes(TIER, 0, -3.0f)));
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, level, entity, slot, selected);
        MeltdreamToolHelper.tickRepair(stack, entity, selected);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        MeltdreamToolHelper.appendRepairTooltip(tooltip);
    }
}
