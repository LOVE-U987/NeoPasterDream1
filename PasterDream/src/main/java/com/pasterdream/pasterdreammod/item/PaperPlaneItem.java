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
 * Paper Plane Item (Curio Item)
 */
public class PaperPlaneItem extends Item implements ICurioItem {

    public PaperPlaneItem() {
        super(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));
}

    @Override
    public void appendHoverText(ItemStack itemstack, Item.TooltipContext context, List<Component> list, TooltipFlag flag) {
        super.appendHoverText(itemstack, context, list, flag);
        list.add(Component.literal("\u54C1\u8D28\uFF1A\u00A7f\u666E\u901A \u2605"));
        list.add(Component.literal("\u00A77\u25AA \u00A79\u589E\u5927\u98CE\u5411\u5BF9\u81EA\u8EAB\u7684\u5F71\u54CD"));
        list.add(Component.literal("\u00A77\u00A7o-- \u6211\u4EEC\u6240\u901D\u53BB\u7684\u7AE5\u5E74\u65F6\u5149"));
}

}
