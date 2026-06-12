package com.pasterdream.pasterdreammod.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Mth;
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
import java.util.UUID;
import net.minecraft.world.entity.EquipmentSlot;

/**
 * Garland Item (Curio Item)
 */
public class GarlandItem extends Item implements ICurioItem {

    public GarlandItem() {
        super(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));
}


    @Override
    public void appendHoverText(ItemStack itemstack, Item.TooltipContext context, List<Component> list, TooltipFlag flag) {
        super.appendHoverText(itemstack, context, list, flag);
        list.add(Component.literal("\u54C1\u8D28\uFF1A\u00A7f\u666E\u901A \u2605"));
}
    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (Mth.nextDouble(slotContext.entity().getRandom(),0,1) < 0.004) {
        stack.hurtAndBreak(1, slotContext.entity(), EquipmentSlot.MAINHAND); {
        stack.shrink(1);
        stack.setDamageValue(0);
        }
        }
    }

}
