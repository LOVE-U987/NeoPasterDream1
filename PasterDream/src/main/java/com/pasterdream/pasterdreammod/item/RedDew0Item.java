package com.pasterdream.pasterdreammod.item;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

/**
 * red_dew_0 物品类
 * 原版稀有度: COMMON
 */
public class RedDew0Item extends Item {

    /**
     * 构造方法
     *
     * @param properties 物品属性
     */
    public RedDew0Item(Item.Properties properties) {
        super(properties.stacksTo(64).food(new FoodProperties.Builder()
                    .nutrition(0).saturationModifier(0f).build()));
}

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("tooltip.pasterdream.red_dew_0.on_drink"));
        tooltipComponents.add(Component.translatable("tooltip.pasterdream.red_dew_0.instant_heal"));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
