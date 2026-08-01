package com.pasterdream.pasterdreammod.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import top.theillusivec4.curios.api.type.capability.ICurioItem;
import top.theillusivec4.curios.api.SlotContext;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.network.chat.Component;
import java.util.List;
import java.util.UUID;

/**
 * Boboji Curio Item (Curio Item)
 */
public class BobojiCurioItem extends Item implements ICurioItem {

    public BobojiCurioItem() {
        super(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));
}


    @Override
    public void appendHoverText(ItemStack itemstack, Item.TooltipContext context, List<Component> list, TooltipFlag flag) {
        super.appendHoverText(itemstack, context, list, flag);
        list.add(Component.translatable("tooltip.pasterdream.boboji_curio.quality"));
        list.add(Component.translatable("tooltip.pasterdream.boboji_curio.effect_1"));
        list.add(Component.translatable("tooltip.pasterdream.boboji_curio.effect_2"));
        list.add(Component.translatable("tooltip.pasterdream.boboji_curio.effect_3"));
        list.add(Component.translatable("tooltip.pasterdream.boboji_curio.effect_4"));
        list.add(Component.translatable("tooltip.pasterdream.boboji_curio.flavor_1"));
        list.add(Component.translatable("tooltip.pasterdream.boboji_curio.flavor_2"));
}

    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        return CurioHelper.canEquipSingleton(slotContext, stack);
    }

    @Override
    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext slotContext, ResourceLocation id, ItemStack stack) {
        Multimap<Holder<Attribute>, AttributeModifier> attributeModifiers = HashMultimap.create();
        // modifier id 由槽位 id+索引派生，避免同属性多槽位冲突
        ResourceLocation slotKey = id.withSuffix("/" + slotContext.index());
        // 原版为 -0.2（teleportationCd）与 -0.4 两个 consume 修饰符，净效果 -0.6；合并为单一 -0.6 语义等价
        attributeModifiers.put(com.pasterdream.pasterdreammod.registry.PDAttributes.TELEPORTATIONCONSUME, new AttributeModifier(slotKey.withSuffix("_tp_consume"), -0.6, AttributeModifier.Operation.ADD_VALUE));
        attributeModifiers.put(com.pasterdream.pasterdreammod.registry.PDAttributes.TELEPORTATIONRANGE, new AttributeModifier(slotKey.withSuffix("_tp_range"), 0.1, AttributeModifier.Operation.ADD_VALUE));
        attributeModifiers.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(slotKey.withSuffix("_speed"), 0.05, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        return attributeModifiers;
    }
}
