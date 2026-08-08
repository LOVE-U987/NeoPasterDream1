package com.pasterdream.pasterdreammod.registry.items;

import com.pasterdream.pasterdreammod.registry.PDItems;
import com.pasterdream.pasterdreammod.registry.blocks.PDBlocksColdDomain;
import net.minecraft.world.item.BlockItem;
import net.neoforged.neoforge.registries.DeferredItem;

/**
 * 冷域维度方块物品注册（BlockItem）。
 * <p>
 * 与 {@link PDBlocksColdDomain} 一一对应，为冷域新方块提供可放置物品。
 *
 * @see PDItems
 */
public class PDItemsColdDomain {

    /** 冷域泥土物品 */
    public static final DeferredItem<BlockItem> COLD_DOMAIN_DIRT =
            PDItems.ITEMS.registerSimpleBlockItem("cold_domain_dirt", PDBlocksColdDomain.COLD_DOMAIN_DIRT);
    /** 冷域木头物品 */
    public static final DeferredItem<BlockItem> COLD_DOMAIN_LOG =
            PDItems.ITEMS.registerSimpleBlockItem("cold_domain_log", PDBlocksColdDomain.COLD_DOMAIN_LOG);
    /** 去皮冷域木头物品 */
    public static final DeferredItem<BlockItem> STRIPPED_COLD_DOMAIN_LOG =
            PDItems.ITEMS.registerSimpleBlockItem("stripped_cold_domain_log", PDBlocksColdDomain.STRIPPED_COLD_DOMAIN_LOG);
    /** 雪地草坪物品 */
    public static final DeferredItem<BlockItem> SNOWY_COLD_DOMAIN_GRASS =
            PDItems.ITEMS.registerSimpleBlockItem("snowy_cold_domain_grass", PDBlocksColdDomain.SNOWY_COLD_DOMAIN_GRASS);
    /** 冷域树叶物品 */
    public static final DeferredItem<BlockItem> COLD_DOMAIN_LEAVES =
            PDItems.ITEMS.registerSimpleBlockItem("cold_domain_leaves", PDBlocksColdDomain.COLD_DOMAIN_LEAVES);
}
