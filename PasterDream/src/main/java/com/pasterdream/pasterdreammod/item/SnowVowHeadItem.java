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
 * Snow Vow Head Item (Curio Item)
 */
public class SnowVowHeadItem extends Item implements ICurioItem {

    public SnowVowHeadItem() {
        super(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));
}

    @Override
    public void appendHoverText(ItemStack itemstack, Item.TooltipContext context, List<Component> list, TooltipFlag flag) {
        super.appendHoverText(itemstack, context, list, flag);
        list.add(Component.literal("\u54C1\u8D28\uFF1A\u00A7b\u7CBE\u826F \u2605\u2605\u2605"));
        list.add(Component.literal("\u00A77\u25AA \u00A79\u4F7F\u9644\u8FD1\u76F4\u5F847\u683C\u5185\u7684\u73A9\u5BB6\u83B7\u5F97\u6548\u679C"));
        list.add(Component.literal("\u00A77\u25AA \u00A79\u5E78\u8FD0+3 \u514D\u75AB\u71C3\u70E7\u4E0E\u51BB\u7ED3"));
}

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
}

}
