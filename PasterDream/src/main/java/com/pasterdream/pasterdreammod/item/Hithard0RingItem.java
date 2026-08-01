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
 * Hithard0 Ring Item (Curio Item)
 */
public class Hithard0RingItem extends Item implements ICurioItem {

    public Hithard0RingItem() {
        super(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));
}


    @Override
    public void appendHoverText(ItemStack itemstack, Item.TooltipContext context, List<Component> list, TooltipFlag flag) {
        super.appendHoverText(itemstack, context, list, flag);
        list.add(Component.translatable("tooltip.pasterdream.hithard0_ring.quality"));
}

    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        if (slotContext.entity() != null) {
            return top.theillusivec4.curios.api.CuriosApi.getCuriosInventory(slotContext.entity())
                    .map(handler -> {
                        var curios = handler.getCurios();
                        for (var sh : curios.values()) {
                            var h = sh.getStacks();
                            for (int i = 0; i < h.getSlots(); i++) {
                                if (h.getStackInSlot(i).is(stack.getItem())) return false;
                            }
                        }
                        return true;
                    })
                    .orElse(true);
        }
        return true;
    }

    @Override
    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext slotContext, ResourceLocation id, ItemStack stack) {
        Multimap<Holder<Attribute>, AttributeModifier> attributeModifiers = HashMultimap.create();
        // modifier id 由槽位 id+索引派生，避免同属性多槽位冲突
        ResourceLocation slotKey = id.withSuffix("/" + slotContext.index());
        attributeModifiers.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(slotKey.withSuffix("_attack"), 0.5, AttributeModifier.Operation.ADD_VALUE));
        return attributeModifiers;
    }
}
