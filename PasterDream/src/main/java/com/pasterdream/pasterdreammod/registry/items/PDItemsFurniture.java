package com.pasterdream.pasterdreammod.registry.items;

import com.pasterdream.pasterdreammod.item.W4GeoDisplayItem;
import com.pasterdream.pasterdreammod.registry.PDItems;
import com.pasterdream.pasterdreammod.registry.blocks.PDBlocksFurniture;
import com.pasterdream.pasterdreammod.registry.blocks.PDBlocksStructure;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 容器/家具/杂项方块组方块物品注册（[分区F]，波次 W4）。
 * <p>
 * GeckoLib 方块（罐/篮/灯/陷阱/唤醒台等）使用 {@link W4GeoDisplayItem}
 * 做 3D 手持渲染（item model 走 displaysettings 的 builtin/entity 路线），
 * 其余为普通贴图 BlockItem。
 *
 * @see PDItems
 */
public class PDItemsFurniture {

    // ==================== 结构生成方块物品（structure_block_0..23） ====================

    /** structure_block_0..23 物品（按下标索引） */
    public static final List<DeferredItem<BlockItem>> STRUCTURE_BLOCK_ITEMS;

    static {
        List<DeferredItem<BlockItem>> list = new ArrayList<>(24);
        for (int i = 0; i < 24; i++) {
            list.add(PDItems.ITEMS.registerSimpleBlockItem("structure_block_" + i,
                    PDBlocksStructure.STRUCTURE_BLOCKS.get(i)));
        }
        STRUCTURE_BLOCK_ITEMS = Collections.unmodifiableList(list);
    }

    // ==================== 结构方块命名引用 ====================

    public static final DeferredItem<BlockItem> STRUCTURE_BLOCK_0 = STRUCTURE_BLOCK_ITEMS.get(0);
    public static final DeferredItem<BlockItem> STRUCTURE_BLOCK_1 = STRUCTURE_BLOCK_ITEMS.get(1);
    public static final DeferredItem<BlockItem> STRUCTURE_BLOCK_2 = STRUCTURE_BLOCK_ITEMS.get(2);
    public static final DeferredItem<BlockItem> STRUCTURE_BLOCK_3 = STRUCTURE_BLOCK_ITEMS.get(3);
    public static final DeferredItem<BlockItem> STRUCTURE_BLOCK_4 = STRUCTURE_BLOCK_ITEMS.get(4);
    public static final DeferredItem<BlockItem> STRUCTURE_BLOCK_5 = STRUCTURE_BLOCK_ITEMS.get(5);
    public static final DeferredItem<BlockItem> STRUCTURE_BLOCK_6 = STRUCTURE_BLOCK_ITEMS.get(6);
    public static final DeferredItem<BlockItem> STRUCTURE_BLOCK_7 = STRUCTURE_BLOCK_ITEMS.get(7);
    public static final DeferredItem<BlockItem> STRUCTURE_BLOCK_8 = STRUCTURE_BLOCK_ITEMS.get(8);
    public static final DeferredItem<BlockItem> STRUCTURE_BLOCK_9 = STRUCTURE_BLOCK_ITEMS.get(9);
    public static final DeferredItem<BlockItem> STRUCTURE_BLOCK_10 = STRUCTURE_BLOCK_ITEMS.get(10);
    public static final DeferredItem<BlockItem> STRUCTURE_BLOCK_11 = STRUCTURE_BLOCK_ITEMS.get(11);
    public static final DeferredItem<BlockItem> STRUCTURE_BLOCK_12 = STRUCTURE_BLOCK_ITEMS.get(12);
    public static final DeferredItem<BlockItem> STRUCTURE_BLOCK_13 = STRUCTURE_BLOCK_ITEMS.get(13);
    public static final DeferredItem<BlockItem> STRUCTURE_BLOCK_14 = STRUCTURE_BLOCK_ITEMS.get(14);
    public static final DeferredItem<BlockItem> STRUCTURE_BLOCK_15 = STRUCTURE_BLOCK_ITEMS.get(15);
    public static final DeferredItem<BlockItem> STRUCTURE_BLOCK_16 = STRUCTURE_BLOCK_ITEMS.get(16);
    public static final DeferredItem<BlockItem> STRUCTURE_BLOCK_17 = STRUCTURE_BLOCK_ITEMS.get(17);
    public static final DeferredItem<BlockItem> STRUCTURE_BLOCK_18 = STRUCTURE_BLOCK_ITEMS.get(18);
    public static final DeferredItem<BlockItem> STRUCTURE_BLOCK_19 = STRUCTURE_BLOCK_ITEMS.get(19);
    public static final DeferredItem<BlockItem> STRUCTURE_BLOCK_20 = STRUCTURE_BLOCK_ITEMS.get(20);
    public static final DeferredItem<BlockItem> STRUCTURE_BLOCK_21 = STRUCTURE_BLOCK_ITEMS.get(21);
    public static final DeferredItem<BlockItem> STRUCTURE_BLOCK_22 = STRUCTURE_BLOCK_ITEMS.get(22);
    public static final DeferredItem<BlockItem> STRUCTURE_BLOCK_23 = STRUCTURE_BLOCK_ITEMS.get(23);


    // ==================== 风之骑士唤醒台物品（GeckoLib 3D） ====================

    public static final DeferredItem<BlockItem> WIND_KNIGHT_SPAWNBLOCK_0 = PDItems.ITEMS.register("wind_knight_spawnblock_0",
            () -> new W4GeoDisplayItem(PDBlocksFurniture.WIND_KNIGHT_SPAWNBLOCK_0.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> WIND_KNIGHT_SPAWNBLOCK_1 = PDItems.ITEMS.register("wind_knight_spawnblock_1",
            () -> new W4GeoDisplayItem(PDBlocksFurniture.WIND_KNIGHT_SPAWNBLOCK_1.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> WIND_KNIGHT_SPAWNBLOCK_2 = PDItems.ITEMS.register("wind_knight_spawnblock_2",
            () -> new W4GeoDisplayItem(PDBlocksFurniture.WIND_KNIGHT_SPAWNBLOCK_2.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> WIND_KNIGHT_SPAWNBLOCK_3 = PDItems.ITEMS.register("wind_knight_spawnblock_3",
            () -> new W4GeoDisplayItem(PDBlocksFurniture.WIND_KNIGHT_SPAWNBLOCK_3.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> WIND_KNIGHT_SPAWNBLOCK_4 = PDItems.ITEMS.register("wind_knight_spawnblock_4",
            () -> new W4GeoDisplayItem(PDBlocksFurniture.WIND_KNIGHT_SPAWNBLOCK_4.get(), new Item.Properties()));

    // ==================== 玻璃罐物品（GeckoLib 3D） ====================

    public static final DeferredItem<BlockItem> ECOLOGY_GLASS_JAR = PDItems.ITEMS.register("ecology_glass_jar",
            () -> new W4GeoDisplayItem(PDBlocksFurniture.ECOLOGY_GLASS_JAR.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> FIREFLY_GLASS_JAR = PDItems.ITEMS.register("firefly_glass_jar",
            () -> new W4GeoDisplayItem(PDBlocksFurniture.FIREFLY_GLASS_JAR.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> LIGHT_FIREFLY_GLASS_JAR = PDItems.ITEMS.register("light_firefly_glass_jar",
            () -> new W4GeoDisplayItem(PDBlocksFurniture.LIGHT_FIREFLY_GLASS_JAR.get(), new Item.Properties()));

    // ==================== 容器/家具物品 ====================

    /** 野餐篮（GeckoLib 3D） */
    public static final DeferredItem<BlockItem> PICNIC_BASKET = PDItems.ITEMS.register("picnic_basket",
            () -> new W4GeoDisplayItem(PDBlocksFurniture.PICNIC_BASKET.get(), new Item.Properties()));
    /** 影之桌（贴图方块物品） */
    public static final DeferredItem<BlockItem> SHADOW_DESK =
            PDItems.ITEMS.registerSimpleBlockItem("shadow_desk", PDBlocksFurniture.SHADOW_DESK);
    /** 风泊木箱（贴图方块物品） */
    public static final DeferredItem<BlockItem> WINDMOOR_CRATE =
            PDItems.ITEMS.registerSimpleBlockItem("windmoor_crate", PDBlocksFurniture.WINDMOOR_CRATE);
    /** 影之床（贴图方块物品） */
    public static final DeferredItem<BlockItem> SHADOW_BED =
            PDItems.ITEMS.registerSimpleBlockItem("shadow_bed", PDBlocksFurniture.SHADOW_BED);
    /** 真·影之床（贴图方块物品） */
    public static final DeferredItem<BlockItem> TRUE_SHADOW_BED =
            PDItems.ITEMS.registerSimpleBlockItem("true_shadow_bed", PDBlocksFurniture.TRUE_SHADOW_BED);

    // ==================== 杂项物品 ====================

    /** 鸟巢（GeckoLib 3D） */
    public static final DeferredItem<BlockItem> BIRDS_NEST = PDItems.ITEMS.register("birds_nest",
            () -> new W4GeoDisplayItem(PDBlocksFurniture.BIRDS_NEST.get(), new Item.Properties()));
    /** 破损的暗影地牢传送门（GeckoLib 3D） */
    public static final DeferredItem<BlockItem> BROKEN_SHADOW_DUNGEON_PROTAL = PDItems.ITEMS.register("broken_shadow_dungeon_protal",
            () -> new W4GeoDisplayItem(PDBlocksFurniture.BROKEN_SHADOW_DUNGEON_PROTAL.get(), new Item.Properties()));
    /** 完整的暗影地牢传送门（GeckoLib 3D） */
    public static final DeferredItem<BlockItem> SHADOW_DUNGEON_PORTAL = PDItems.ITEMS.register("shadow_dungeon_portal",
            () -> new W4GeoDisplayItem(PDBlocksFurniture.SHADOW_DUNGEON_PORTAL.get(), new Item.Properties()));
    /** 干裂粘土层·湿润（贴图方块物品） */
    public static final DeferredItem<BlockItem> CLAYPAN_1 =
            PDItems.ITEMS.registerSimpleBlockItem("claypan_1", PDBlocksFurniture.CLAYPAN_1);
    /** 荒漠英雄之墓（GeckoLib 3D） */
    public static final DeferredItem<BlockItem> DESERT_HERO_TOMB = PDItems.ITEMS.register("desert_hero_tomb",
            () -> new W4GeoDisplayItem(PDBlocksFurniture.DESERT_HERO_TOMB.get(), new Item.Properties()));
    /** 守护者方块（贴图方块物品） */
    public static final DeferredItem<BlockItem> GUARD_BLOCK =
            PDItems.ITEMS.registerSimpleBlockItem("guard_block", PDBlocksFurniture.GUARD_BLOCK);
    /** 行动抑制方块（贴图方块物品） */
    public static final DeferredItem<BlockItem> RESTRAINMOVE_BLOCK =
            PDItems.ITEMS.registerSimpleBlockItem("restrainmove_block", PDBlocksFurniture.RESTRAINMOVE_BLOCK);
    /** 守护者水晶（GeckoLib 3D） */
    public static final DeferredItem<BlockItem> GUARD_CRYSTAL = PDItems.ITEMS.register("guard_crystal",
            () -> new W4GeoDisplayItem(PDBlocksFurniture.GUARD_CRYSTAL.get(), new Item.Properties()));
    /** 遗落之剑（贴图方块物品） */
    public static final DeferredItem<BlockItem> LOST_SWORD_BLOCK =
            PDItems.ITEMS.registerSimpleBlockItem("lost_sword_block", PDBlocksFurniture.LOST_SWORD_BLOCK);
    /** 阴影火盆（GeckoLib 3D） */
    public static final DeferredItem<BlockItem> SHADOW_BRAZIER = PDItems.ITEMS.register("shadow_brazier",
            () -> new W4GeoDisplayItem(PDBlocksFurniture.SHADOW_BRAZIER.get(), new Item.Properties()));
    /** 阴影陷阱（GeckoLib 3D） */
    public static final DeferredItem<BlockItem> SHADOW_TRAP_0 = PDItems.ITEMS.register("shadow_trap_0",
            () -> new W4GeoDisplayItem(PDBlocksFurniture.SHADOW_TRAP_0.get(), new Item.Properties()));
    /** 暮影之笼（GeckoLib 3D） */
    public static final DeferredItem<BlockItem> TWILIGHT_LANTERN = PDItems.ITEMS.register("twilight_lantern",
            () -> new W4GeoDisplayItem(PDBlocksFurniture.TWILIGHT_LANTERN.get(), new Item.Properties()));
}
