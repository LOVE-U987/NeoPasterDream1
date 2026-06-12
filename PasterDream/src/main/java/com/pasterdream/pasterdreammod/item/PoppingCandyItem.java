package com.pasterdream.pasterdreammod.item;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

/**
 * popping_candy 物品类
 * 原版稀有度: COMMON
 */
public class PoppingCandyItem extends Item {

    /**
     * 构造方法
     *
     * @param properties 物品属性
     */
    public PoppingCandyItem(Item.Properties properties) {
        super(properties.stacksTo(64).food(new FoodProperties.Builder()
                    .nutrition(0).saturationModifier(0f).build()));
}

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("\u00A77\u98DF\u7528\u540E\u83B7\u5F97\u6548\u679C:"));
        tooltipComponents.add(Component.literal("\u00A77\u25AA \u00A79\u8DF3\u8DC3\u63D0\u5347IV (0:20)"));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
