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

    /**
     * 音乐唱片标签 —— 收录本模组全部 13 张唱片。
     * <p>
     * 融梦水晶箱的唱片附加掉落按此标签取样（优先选玩家未拥有的），
     * 新增唱片只需在 {@code data/pasterdream/tags/item/music_discs.json} 中追加。
     * 注意：不可复用原版 {@code minecraft:music_discs}（含全部原版唱片）。
     */
    public static final TagKey<Item> MUSIC_DISCS = TagKey.create(
            Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath("pasterdream", "music_discs")
    );

    /**
     * 融梦水晶碎片标签 —— 用于判定"弹出时生成水晶实体"的物品。
     * <p>
     * 定义在 {@code data/pasterdream/tags/item/meltdream_chest_crystal.json}；
     * 附加战利品表提供该物品，代码据此将其归位到 slot 8 并在弹出时生成实体。
     */
    public static final TagKey<Item> MELTDREAM_CHEST_CRYSTAL = TagKey.create(
            Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath("pasterdream", "meltdream_chest_crystal")
    );
}
