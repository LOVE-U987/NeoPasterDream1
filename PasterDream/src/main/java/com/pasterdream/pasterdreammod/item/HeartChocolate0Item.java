package com.pasterdream.pasterdreammod.item;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

/**
 * heart_chocolate_0 物品类
 * 原版稀有度: COMMON
 */
public class HeartChocolate0Item extends Item {

    /**
     * 构造方法
     *
     * @param properties 物品属性
     */
    public HeartChocolate0Item(Item.Properties properties) {
        super(properties.stacksTo(64).food(new FoodProperties.Builder()
                    .nutrition(0).saturationModifier(0f).build()));
}

}
