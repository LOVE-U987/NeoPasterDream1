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
 * Degenerate Bodys Item (Curio Item)
 */
public class DegenerateBodysItem extends Item implements ICurioItem {

    /**
     * 构造函数，设置物品属性
     */
    public DegenerateBodysItem() {
        super(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));
}


    @Override
    public void appendHoverText(ItemStack itemstack, Item.TooltipContext context, List<Component> list, TooltipFlag flag) {
        super.appendHoverText(itemstack, context, list, flag);
        list.add(Component.translatable("tooltip.pasterdream.degenerate_bodys.quality"));
        list.add(Component.translatable("tooltip.pasterdream.degenerate_bodys.effect_1"));
        list.add(Component.translatable("tooltip.pasterdream.degenerate_bodys.effect_2"));
        list.add(Component.translatable("tooltip.pasterdream.degenerate_bodys.effect_3"));
        list.add(Component.translatable("tooltip.pasterdream.degenerate_bodys.flavor_1"));
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
        attributeModifiers.put(Attributes.MAX_HEALTH, new AttributeModifier(slotKey.withSuffix("_health"), -4.0, AttributeModifier.Operation.ADD_VALUE));
        attributeModifiers.put(Attributes.ENTITY_INTERACTION_RANGE, new AttributeModifier(slotKey.withSuffix("_entity_reach"), 0.2, AttributeModifier.Operation.ADD_VALUE));
        attributeModifiers.put(Attributes.BLOCK_INTERACTION_RANGE, new AttributeModifier(slotKey.withSuffix("_block_reach"), 1.0, AttributeModifier.Operation.ADD_VALUE));
        attributeModifiers.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(slotKey.withSuffix("_attack"), 2.0, AttributeModifier.Operation.ADD_VALUE));
        return attributeModifiers;
    }
}
