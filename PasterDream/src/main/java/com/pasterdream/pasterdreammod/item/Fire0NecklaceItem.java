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
 * Fire0 Necklace Item (Curio Item)
 */
public class Fire0NecklaceItem extends Item implements ICurioItem {

    public Fire0NecklaceItem() {
        super(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));
}

    @Override
    public void appendHoverText(ItemStack itemstack, Item.TooltipContext context, List<Component> list, TooltipFlag flag) {
        super.appendHoverText(itemstack, context, list, flag);
        list.add(Component.literal("\u54C1\u8D28\uFF1A\u00A7f\u666E\u901A \u2605"));
        list.add(Component.literal("\u00A77\u25AA \u00A79\u884C\u8D70\u4F1A\u7559\u4E0B\u706B\u7130"));
        list.add(Component.literal("\u00A77\u25AA \u00A79\u5904\u4E8E\u71C3\u70E7\u72B6\u6001\u4E0B\u83B7\u5F97\u6025\u8FEBI\u6548\u679C"));
}


}
