package com.pasterdream.pasterdreammod.item;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;

/**
 * 钳子 - 剪刀子类，耐久160
 */
public class PliersItem extends ShearsItem {
    public PliersItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean hasCraftingRemainingItem(ItemStack stack) {
        return true;
    }

    @Override
    public ItemStack getCraftingRemainingItem(ItemStack itemStack) {
        ItemStack container = itemStack.copy();
        int damage = container.getDamageValue() + 1;
        if (damage >= container.getMaxDamage()) {
            return ItemStack.EMPTY;
        }
        container.setDamageValue(damage);
        return container;
    }
}