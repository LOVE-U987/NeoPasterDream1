package com.pasterdream.pasterdreammod.item;

import top.theillusivec4.curios.api.type.capability.ICurioItem;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.network.chat.Component;
import java.util.List;

/**
 * Qym Head Item (Curio Item)
 */
public class QymHeadItem extends Item implements ICurioItem {

    public QymHeadItem() {
        // 与原版一致：神迹品质饰品，防火
        super(new Item.Properties().stacksTo(1).fireResistant().rarity(Rarity.COMMON));
    }

    @Override
    public void appendHoverText(ItemStack itemstack, Item.TooltipContext context, List<Component> list, TooltipFlag flag) {
        super.appendHoverText(itemstack, context, list, flag);
        list.add(Component.translatable("tooltip.pasterdream.qym_head.quality"));
        list.add(Component.translatable("tooltip.pasterdream.qym_head.effect_1"));
        list.add(Component.translatable("tooltip.pasterdream.qym_head.effect_2"));
        list.add(Component.translatable("tooltip.pasterdream.qym_head.flavor_1"));
}

}
