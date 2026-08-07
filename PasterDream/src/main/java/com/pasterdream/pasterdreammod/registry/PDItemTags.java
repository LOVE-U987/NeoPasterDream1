package com.pasterdream.pasterdreammod.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

/**
 * PasterDream 物品标签常量。
 * <p>
 * 所有与工具、配方、掉落判定相关的自定义物品标签统一在此声明，
 * 避免各业务类中重复定义 TagKey 常量。
 */
public final class PDItemTags {

    private PDItemTags() {}

    /**
     * 剪刀类工具标签 —— 包含原版剪刀（minecraft:shears）与园艺钳（pasterdream:pliers）。
     * <p>
     * 需要「剪刀效果」的方块掉落（花草、海草、树叶等）统一检查此标签，
     * 而非逐个硬编码具体物品；新增同类工具只需在
     * {@code data/pasterdream/tags/item/shears.json} 中追加即可。
     */
    public static final TagKey<Item> SHEARS = TagKey.create(
            Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath("pasterdream", "shears")
    );
}
