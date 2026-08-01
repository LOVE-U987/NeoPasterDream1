package com.pasterdream.pasterdreammod.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import top.theillusivec4.curios.api.type.capability.ICurioItem;
import top.theillusivec4.curios.api.SlotContext;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.network.chat.Component;
import java.util.List;

/**
 * Evasion Cloak Item (Curio Item)
 */
public class EvasionCloakItem extends Item implements ICurioItem {

    public EvasionCloakItem() {
        super(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));
}

    @Override
    public void appendHoverText(ItemStack itemstack, Item.TooltipContext context, List<Component> list, TooltipFlag flag) {
        super.appendHoverText(itemstack, context, list, flag);
        list.add(Component.translatable("tooltip.pasterdream.evasion_cloak.quality"));
        list.add(Component.translatable("tooltip.pasterdream.evasion_cloak.effect_1"));
        list.add(Component.translatable("tooltip.pasterdream.evasion_cloak.effect_2"));
        list.add(Component.translatable("tooltip.pasterdream.evasion_cloak.effect_3"));
        list.add(Component.translatable("tooltip.pasterdream.evasion_cloak.effect_4"));
        list.add(Component.translatable("tooltip.pasterdream.evasion_cloak.effect_5"));
}

}
