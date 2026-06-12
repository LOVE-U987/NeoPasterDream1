package com.pasterdream.pasterdreammod.item;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;

/**
 * 丛林孢子 - 可食用（营养1）
 */
public class JungleSporeItem extends Item {
    public JungleSporeItem(Properties properties) {
        super(properties);
    }

    public static FoodProperties createFoodProperties() {
        return new FoodProperties.Builder()
                .nutrition(1)
                .saturationModifier(0f)
                .alwaysEdible()
                .build();
    }
}