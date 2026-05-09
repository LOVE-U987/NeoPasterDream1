package com.pasterdream.pasterdreammod.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

/**
 * 钛锭物品 (titanium_ingot)
 * 基础材料，稀有度为 UNCOMMON
 */
public class TitaniumIngotItem extends Item {
    public TitaniumIngotItem() {
        super(new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON));
    }
}