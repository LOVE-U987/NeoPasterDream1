package com.pasterdream.pasterdreammod.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

/**
 * 魔法石物品 (magic_stone)
 * 基础材料，带有特殊描述文本
 */
public class MagicStoneItem extends Item {
    public MagicStoneItem() {
        super(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON));
    }

    @Override
    public void appendHoverText(ItemStack itemstack, Item.TooltipContext context, List<Component> list, TooltipFlag flag) {
        super.appendHoverText(itemstack, context, list, flag);
        list.add(Component.literal("§7§o哪个法师的兜里不会踹几块魔法石呢？"));
    }
}