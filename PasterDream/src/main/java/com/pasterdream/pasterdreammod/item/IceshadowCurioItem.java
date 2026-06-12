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
 * Iceshadow Curio Item (Curio Item)
 */
public class IceshadowCurioItem extends Item implements ICurioItem {

    public IceshadowCurioItem() {
        super(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));
}

    @Override
    public void appendHoverText(ItemStack itemstack, Item.TooltipContext context, List<Component> list, TooltipFlag flag) {
        super.appendHoverText(itemstack, context, list, flag);
        list.add(Component.literal("\u54C1\u8D28\uFF1A\u00A71\u5927\u5E08 \u2605\u2605\u2605\u2605"));
        list.add(Component.literal("\u00A77\u25AA \u00A79\u51B0\u5F71\u6218\u9524\u7684\u6218\u6280\u5C06\u5411\u9762\u671D\u65B9\u5411\u989D\u5916\u91CA\u653E2\u4E2A\u64BC\u5730\u6C34\u6676"));
        list.add(Component.literal("\u00A77\u25AA \u00A79\u91CA\u653E\u7684\u64BC\u5730\u6C34\u6676\u95F4\u9694\u53D7\u91CA\u653E\u8DDD\u79BB\u5F71\u54CD"));
}

}
