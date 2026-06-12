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

/**
 * Worldtree Seedpod Item (Curio Item)
 */
public class WorldtreeSeedpodItem extends Item implements ICurioItem {

    public WorldtreeSeedpodItem() {
        super(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));
}

    @Override
    public void appendHoverText(ItemStack itemstack, Item.TooltipContext context, List<Component> list, TooltipFlag flag) {
        super.appendHoverText(itemstack, context, list, flag);
        list.add(Component.literal("\u54C1\u8D28\uFF1A\u00A7d\u53F2\u8BD7 \u2605\u2605\u2605\u2605\u2605\u2605"));
        list.add(Component.literal("\u00A77\u25AA \u00A79\u5728\u67D3\u68A6\u4E16\u754C \u9732\u5929\u73AF\u5883 \u8EAB\u5904\u9AD8\u5EA6y>160 \u767D\u5929 \u811A\u4E0B\u4E3A\u67D3\u68A6\u4E16\u754C\u6811\u53F6"));
        list.add(Component.literal("\u00A77\u25AA \u00A74\u6301\u7EED\u964D\u4F4E\u9971\u98DF\u5EA6"));
        list.add(Component.literal("\u00A77\u25AA \u00A79\u878D\u68A6\u80FD\u91CF+360/h"));
        list.add(Component.literal("\u00A77\u00A7o-- \u843D\u53F6\u5F52\u6839 \u88C2\u835A\u5F52\u51A0"));
}


}
