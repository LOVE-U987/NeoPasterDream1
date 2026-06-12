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
        list.add(Component.literal("\u54C1\u8D28\uFF1A\u00A7d\u53F2\u8BD7 \u2605\u2605\u2605\u2605\u2605\u2605"));
        list.add(Component.literal("\u00A77\u25AA \u00A7f\u6761\u4EF6:\u6697\u5F71\u4EC6\u4ECE"));
        list.add(Component.literal("\u00A77\u25AA \u00A79\u7CBE\u795E\u503C\u5F520\u65F6\u4E0D\u518D\u635F\u5931\u751F\u547D\u503C"));
        list.add(Component.literal("\u00A77\u25AA \u00A79\u53EF\u4EE5\u4F7F\u7528\u6697\u5F71\u6CD5\u672F"));
        list.add(Component.literal("\u00A77\u00A7o-- \u6211\u5C06\u6210\u4E3A\u4F60\u7684\u9634\u5F71 \u4E00\u5E76\u4F53\u4F1A\u4F60\u7684\u75DB\u82E6"));
}

    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        if (slotContext.entity() != null) {
            return top.theillusivec4.curios.api.CuriosApi.getCuriosInventory(slotContext.entity())
                    .map(handler -> handler.findFirstCurio(stack.getItem()).isEmpty())
                    .orElse(true);
        }
        return true;
    }
}
