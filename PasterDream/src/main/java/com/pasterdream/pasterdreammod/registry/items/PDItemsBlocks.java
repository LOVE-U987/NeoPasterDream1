package com.pasterdream.pasterdreammod.registry.items;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.api.curio.CurioAPI;
import com.pasterdream.pasterdreammod.api.curio.model.CurioSlot;
import com.pasterdream.pasterdreammod.api.effect.MobEffectAPI;
import com.pasterdream.pasterdreammod.api.entity.EntityAPI;
import com.pasterdream.pasterdreammod.api.item.ItemAPI;
import com.pasterdream.pasterdreammod.api.item.model.MigrationCategory;
import com.pasterdream.pasterdreammod.api.item.model.ToolSpec.ToolType;
import com.pasterdream.pasterdreammod.item.*;
import com.pasterdream.pasterdreammod.registry.PDBlocks;
import com.pasterdream.pasterdreammod.registry.PDEntities;
import com.pasterdream.pasterdreammod.registry.PDItems;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PlaceOnWaterBlockItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.food.FoodProperties;
import net.neoforged.neoforge.registries.DeferredItem;


/**
 * 方块物品注册（BlockItem）。
 *
 * @see PDItems
 */
public class PDItemsBlocks {


    // ==================== 染梦世界方块物品 ====================

    public static final DeferredItem<BlockItem> DYEDREAM_BLOCK = PDItems.ITEMS.registerSimpleBlockItem("dyedream_block", PDBlocks.DYEDREAM_BLOCK);
    public static final DeferredItem<BlockItem> DYEDREAM_DIRT = PDItems.ITEMS.registerSimpleBlockItem("dyedream_dirt", PDBlocks.DYEDREAM_DIRT);
    public static final DeferredItem<BlockItem> DYEDREAM_SAND = PDItems.ITEMS.registerSimpleBlockItem("dyedream_sand", PDBlocks.DYEDREAM_SAND);
    public static final DeferredItem<BlockItem> DYEDREAM_PLANKS = PDItems.ITEMS.registerSimpleBlockItem("dyedream_planks", PDBlocks.DYEDREAM_PLANKS);
    public static final DeferredItem<BlockItem> DYEDREAM_GLASS = PDItems.ITEMS.registerSimpleBlockItem("dyedream_glass", PDBlocks.DYEDREAM_GLASS);
    public static final DeferredItem<BlockItem> DYEDREAM_ICE = PDItems.ITEMS.registerSimpleBlockItem("dyedream_ice", PDBlocks.DYEDREAM_ICE);
    public static final DeferredItem<BlockItem> DYEDREAM_PACKED_ICE = PDItems.ITEMS.registerSimpleBlockItem("dyedream_packed_ice", PDBlocks.DYEDREAM_PACKED_ICE);
    public static final DeferredItem<BlockItem> DYEDREAMQUARTZ_BLOCK = PDItems.ITEMS.registerSimpleBlockItem("dyedreamquartz_block", PDBlocks.DYEDREAMQUARTZ_BLOCK);
    public static final DeferredItem<BlockItem> SMOOTH_DYEDREAMQUARTZ_BLOCK = PDItems.ITEMS.registerSimpleBlockItem("smooth_dyedreamquartz_block", PDBlocks.SMOOTH_DYEDREAMQUARTZ_BLOCK);
    public static final DeferredItem<BlockItem> BRICKS_DYEDREAMQUARTZ_BLOCK = PDItems.ITEMS.registerSimpleBlockItem("bricks_dyedreamquartz_block", PDBlocks.BRICKS_DYEDREAMQUARTZ_BLOCK);
    public static final DeferredItem<BlockItem> MELTDREAM_CRYSTAL_LAMP = PDItems.ITEMS.registerSimpleBlockItem("meltdream_crystal_lamp", PDBlocks.MELTDREAM_CRYSTAL_LAMP);
    public static final DeferredItem<BlockItem> CHISELED_DYEDREAMQUARTZ_BLOCK = PDItems.ITEMS.registerSimpleBlockItem("chiseled_dyedreamquartz_block", PDBlocks.CHISELED_DYEDREAMQUARTZ_BLOCK);
    public static final DeferredItem<BlockItem> DYEDREAM_BUD_BLOCK = PDItems.ITEMS.registerSimpleBlockItem("dyedream_bud_block", PDBlocks.DYEDREAM_BUD_BLOCK);
    public static final DeferredItem<BlockItem> PINKSLIME_BLOCK = PDItems.ITEMS.registerSimpleBlockItem("pinkslime_block", PDBlocks.PINKSLIME_BLOCK);
    public static final DeferredItem<BlockItem> ICESTONE = PDItems.ITEMS.registerSimpleBlockItem("icestone", PDBlocks.ICESTONE);
    public static final DeferredItem<BlockItem> DYEDREAM_LEAVES = PDItems.ITEMS.registerSimpleBlockItem("dyedream_leaves", PDBlocks.DYEDREAM_LEAVES);
    public static final DeferredItem<BlockItem> DYEDREAM_WORLDTREE_LEAVES = PDItems.ITEMS.registerSimpleBlockItem("dyedream_worldtree_leaves", PDBlocks.DYEDREAM_WORLDTREE_LEAVES);
    public static final DeferredItem<BlockItem> DYEDREAMQUARTZ_ORE = PDItems.ITEMS.registerSimpleBlockItem("dyedreamquartz_ore", PDBlocks.DYEDREAMQUARTZ_ORE);
    public static final DeferredItem<BlockItem> DYEDREAMDUST_ORE = PDItems.ITEMS.registerSimpleBlockItem("dyedreamdust_ore", PDBlocks.DYEDREAMDUST_ORE);
    public static final DeferredItem<BlockItem> AMBER_CANDY_ORE = PDItems.ITEMS.registerSimpleBlockItem("amber_candy_ore", PDBlocks.AMBER_CANDY_ORE);
    public static final DeferredItem<BlockItem> TITANIUM_ORE = PDItems.ITEMS.registerSimpleBlockItem("titanium_ore", PDBlocks.TITANIUM_ORE);
    public static final DeferredItem<BlockItem> WINDRUNNER_CRYSTAL_ORE = PDItems.ITEMS.registerSimpleBlockItem("windrunner_crystal_ore", PDBlocks.WINDRUNNER_CRYSTAL_ORE);
    public static final DeferredItem<BlockItem> CONGEAL_WIND_ORE = PDItems.ITEMS.registerSimpleBlockItem("congeal_wind_ore", PDBlocks.CONGEAL_WIND_ORE);
    public static final DeferredItem<BlockItem> CARVE_DYEDREAM_GLASS = PDItems.ITEMS.registerSimpleBlockItem("carve_dyedream_glass", PDBlocks.CARVE_DYEDREAM_GLASS);
    public static final DeferredItem<BlockItem> GOLD_CARVE_DYEDREAM_GLASS = PDItems.ITEMS.registerSimpleBlockItem("gold_carve_dyedream_glass", PDBlocks.GOLD_CARVE_DYEDREAM_GLASS);
    public static final DeferredItem<BlockItem> DYEDREAM_GRASS = PDItems.ITEMS.registerSimpleBlockItem("dyedream_grass", PDBlocks.DYEDREAM_GRASS);
    public static final DeferredItem<BlockItem> DYEDREAM_LOG = PDItems.ITEMS.registerSimpleBlockItem("dyedream_log", PDBlocks.DYEDREAM_LOG);
    public static final DeferredItem<BlockItem> DYEDREAM_WOOD = PDItems.ITEMS.registerSimpleBlockItem("dyedream_wood", PDBlocks.DYEDREAM_WOOD);
    public static final DeferredItem<BlockItem> PILLAR_DYEDREAMQUARTZ_BLOCK = PDItems.ITEMS.registerSimpleBlockItem("pillar_dyedreamquartz_block", PDBlocks.PILLAR_DYEDREAMQUARTZ_BLOCK);
    public static final DeferredItem<BlockItem> DYEDREAM_PLANKS_STAIRS = PDItems.ITEMS.registerSimpleBlockItem("dyedream_planks_stairs", PDBlocks.DYEDREAM_PLANKS_STAIRS);
    public static final DeferredItem<BlockItem> DYEDREAM_BUD_STAIRS = PDItems.ITEMS.registerSimpleBlockItem("dyedream_bud_stairs", PDBlocks.DYEDREAM_BUD_STAIRS);
    public static final DeferredItem<BlockItem> DYEDREAMQUARTZ_BLOCK_STAIRS = PDItems.ITEMS.registerSimpleBlockItem("dyedreamquartz_block_stairs", PDBlocks.DYEDREAMQUARTZ_BLOCK_STAIRS);
    public static final DeferredItem<BlockItem> DYEDREAM_PLANKS_SLAB = PDItems.ITEMS.registerSimpleBlockItem("dyedream_planks_slab", PDBlocks.DYEDREAM_PLANKS_SLAB);
    public static final DeferredItem<BlockItem> DYEDREAM_BUD_SLAB = PDItems.ITEMS.registerSimpleBlockItem("dyedream_bud_slab", PDBlocks.DYEDREAM_BUD_SLAB);
    public static final DeferredItem<BlockItem> DYEDREAMQUARTZ_BLOCK_SLAB = PDItems.ITEMS.registerSimpleBlockItem("dyedreamquartz_block_slab", PDBlocks.DYEDREAMQUARTZ_BLOCK_SLAB);
    public static final DeferredItem<BlockItem> DYEDREAM_BUD_WALL = PDItems.ITEMS.registerSimpleBlockItem("dyedream_bud_wall", PDBlocks.DYEDREAM_BUD_WALL);
    public static final DeferredItem<BlockItem> DYEDREAMQUARTZ_BLOCK_WALL = PDItems.ITEMS.registerSimpleBlockItem("dyedreamquartz_block_wall", PDBlocks.DYEDREAMQUARTZ_BLOCK_WALL);
    public static final DeferredItem<BlockItem> DYEDREAM_PLANKS_FENCE = PDItems.ITEMS.registerSimpleBlockItem("dyedream_planks_fence", PDBlocks.DYEDREAM_PLANKS_FENCE);
    public static final DeferredItem<BlockItem> DYEDREAM_PLANKS_FENCEGATE = PDItems.ITEMS.registerSimpleBlockItem("dyedream_planks_fencegate", PDBlocks.DYEDREAM_PLANKS_FENCEGATE);
    public static final DeferredItem<BlockItem> DYEDREAM_PLANKS_DOOR = PDItems.ITEMS.registerSimpleBlockItem("dyedream_planks_door", PDBlocks.DYEDREAM_PLANKS_DOOR);
    public static final DeferredItem<BlockItem> DYEDREAM_PLANKS_TRAPDOOR = PDItems.ITEMS.registerSimpleBlockItem("dyedream_planks_trapdoor", PDBlocks.DYEDREAM_PLANKS_TRAPDOOR);
    public static final DeferredItem<BlockItem> DYEDREAM_PLANKS_PRESSURE_PLATE = PDItems.ITEMS.registerSimpleBlockItem("dyedream_planks_pressure_plate", PDBlocks.DYEDREAM_PLANKS_PRESSURE_PLATE);
    public static final DeferredItem<BlockItem> DYEDREAM_PLANKS_BUTTON = PDItems.ITEMS.registerSimpleBlockItem("dyedream_planks_button", PDBlocks.DYEDREAM_PLANKS_BUTTON);
    public static final DeferredItem<BlockItem> DYEDREAM_GLASSPANE = PDItems.ITEMS.registerSimpleBlockItem("dyedream_glasspane", PDBlocks.DYEDREAM_GLASSPANE);
    public static final DeferredItem<BlockItem> CARVE_DYEDREAM_GLASSPANE = PDItems.ITEMS.registerSimpleBlockItem("carve_dyedream_glasspane", PDBlocks.CARVE_DYEDREAM_GLASSPANE);
    public static final DeferredItem<BlockItem> GOLD_CARVE_DYEDREAM_GLASSPANE = PDItems.ITEMS.registerSimpleBlockItem("gold_carve_dyedream_glasspane", PDBlocks.GOLD_CARVE_DYEDREAM_GLASSPANE);
    public static final DeferredItem<BlockItem> DYEDREAM_LARTERN = PDItems.ITEMS.registerSimpleBlockItem("dyedream_lartern", PDBlocks.DYEDREAM_LARTERN);


    // ==================== Phase 1: 移植方块物品 ====================

    public static final DeferredItem<BlockItem> TITANIUM_BLOCK = PDItems.ITEMS.registerSimpleBlockItem("titanium_block", PDBlocks.TITANIUM_BLOCK);
    public static final DeferredItem<BlockItem> RAW_TITANIUM_BLOCK = PDItems.ITEMS.registerSimpleBlockItem("raw_titanium_block", PDBlocks.RAW_TITANIUM_BLOCK);
    public static final DeferredItem<BlockItem> MOLTENGOLD_BLOCK = PDItems.ITEMS.registerSimpleBlockItem("moltengold_block", PDBlocks.MOLTENGOLD_BLOCK);
    public static final DeferredItem<BlockItem> BLACKMETAL_BLOCK = PDItems.ITEMS.registerSimpleBlockItem("blackmetal_block", PDBlocks.BLACKMETAL_BLOCK);
    public static final DeferredItem<BlockItem> CHARGED_AMETHYST_BLOCK = PDItems.ITEMS.registerSimpleBlockItem("charged_amethyst_block", PDBlocks.CHARGED_AMETHYST_BLOCK);
    public static final DeferredItem<BlockItem> WIND_IRON_BLOCK = PDItems.ITEMS.registerSimpleBlockItem("wind_iron_block", PDBlocks.WIND_IRON_BLOCK);
    public static final DeferredItem<BlockItem> DEEPSLATE_TITANIUM_ORE = PDItems.ITEMS.registerSimpleBlockItem("deepslate_titanium_ore", PDBlocks.DEEPSLATE_TITANIUM_ORE);
    public static final DeferredItem<BlockItem> MOLTENGOLD_ORE = PDItems.ITEMS.registerSimpleBlockItem("moltengold_ore", PDBlocks.MOLTENGOLD_ORE);
    public static final DeferredItem<BlockItem> SOUL_ORE = PDItems.ITEMS.registerSimpleBlockItem("soul_ore", PDBlocks.SOUL_ORE);
    public static final DeferredItem<BlockItem> PEBBLE_0 = PDItems.ITEMS.registerSimpleBlockItem("pebble_0", PDBlocks.PEBBLE_0);
    public static final DeferredItem<BlockItem> SHADOW_LIGHT_0 = PDItems.ITEMS.registerSimpleBlockItem("shadow_light_0", PDBlocks.SHADOW_LIGHT_0);
    public static final DeferredItem<BlockItem> VINE_0 = PDItems.ITEMS.registerSimpleBlockItem("vine_0", PDBlocks.VINE_0);
    public static final DeferredItem<BlockItem> GOLDENROD = PDItems.ITEMS.registerSimpleBlockItem("goldenrod", PDBlocks.GOLDENROD);
    public static final DeferredItem<BlockItem> CROP_0A = PDItems.ITEMS.registerSimpleBlockItem("crop_0a", PDBlocks.CROP_0A);
    public static final DeferredItem<BlockItem> CROP_1A = PDItems.ITEMS.registerSimpleBlockItem("crop_1a", PDBlocks.CROP_1A);
    public static final DeferredItem<BlockItem> CROP_2A = PDItems.ITEMS.registerSimpleBlockItem("crop_2a", PDBlocks.CROP_2A);
    public static final DeferredItem<BlockItem> CROP_3A = PDItems.ITEMS.registerSimpleBlockItem("crop_3a", PDBlocks.CROP_3A);
    public static final DeferredItem<BlockItem> CROP_4A = PDItems.ITEMS.registerSimpleBlockItem("crop_4a", PDBlocks.CROP_4A);
    public static final DeferredItem<BlockItem> DREAM_TRAIN_STRUCTURE = PDItems.ITEMS.registerSimpleBlockItem("dream_train_structure", PDBlocks.DREAM_TRAIN_STRUCTURE);


    // ==================== 钙华变体补充方块物品 ====================

    public static final DeferredItem<BlockItem> POLISHED_CALCITE = PDItems.ITEMS.registerSimpleBlockItem("polished_calcite", PDBlocks.POLISHED_CALCITE);
    public static final DeferredItem<BlockItem> CALCITE_TILES = PDItems.ITEMS.registerSimpleBlockItem("calcite_tiles", PDBlocks.CALCITE_TILES);
    public static final DeferredItem<BlockItem> CALCITE_TILES_STAIRS = PDItems.ITEMS.registerSimpleBlockItem("calcite_tiles_stairs", PDBlocks.CALCITE_TILES_STAIRS);
    public static final DeferredItem<BlockItem> CALCITE_TILES_SLAB = PDItems.ITEMS.registerSimpleBlockItem("calcite_tiles_slab", PDBlocks.CALCITE_TILES_SLAB);
    public static final DeferredItem<BlockItem> POLISHED_CALCITE_SLAB = PDItems.ITEMS.registerSimpleBlockItem("polished_calcite_slab", PDBlocks.POLISHED_CALCITE_SLAB);
    public static final DeferredItem<BlockItem> POLISHED_CALCITE_WALL = PDItems.ITEMS.registerSimpleBlockItem("polished_calcite_wall", PDBlocks.POLISHED_CALCITE_WALL);
    public static final DeferredItem<BlockItem> CALCITE_TILES_WALL = PDItems.ITEMS.registerSimpleBlockItem("calcite_tiles_wall", PDBlocks.CALCITE_TILES_WALL);
    public static final DeferredItem<BlockItem> POLISHED_CALCITE_STAIRS = PDItems.ITEMS.registerSimpleBlockItem("polished_calcite_stairs", PDBlocks.POLISHED_CALCITE_STAIRS);


    // ==================== 自定义模型方块 BlockItem ====================

    /**
     * 寻梦者的永恒书卷 (the_endless_book_of_dream_seekers)
     * 使用 TheEndlessBookOfDreamSeekersDisplayItem 实现手持 3D 渲染
     */
    public static final DeferredItem<TheEndlessBookOfDreamSeekersDisplayItem> THE_ENDLESS_BOOK_OF_DREAM_SEEKERS = PDItems.ITEMS.register("the_endless_book_of_dream_seekers",
            () -> new TheEndlessBookOfDreamSeekersDisplayItem(new Item.Properties()));

    public static final DeferredItem<BlockItem> DYEDREAM_PLANKS_PANE = PDItems.ITEMS.registerSimpleBlockItem("dyedream_planks_pane", PDBlocks.DYEDREAM_PLANKS_PANE);
    public static final DeferredItem<BlockItem> PINKAGARIC_0 = PDItems.ITEMS.registerSimpleBlockItem("pinkagaric_0", PDBlocks.PINKAGARIC_0);
    public static final DeferredItem<BlockItem> PINKAGARIC_1 = PDItems.ITEMS.registerSimpleBlockItem("pinkagaric_1", PDBlocks.PINKAGARIC_1);
    public static final DeferredItem<BlockItem> PINKAGARIC_2 = PDItems.ITEMS.registerSimpleBlockItem("pinkagaric_2", PDBlocks.PINKAGARIC_2);
    public static final DeferredItem<BlockItem> PINKAGARIC_3 = PDItems.ITEMS.registerSimpleBlockItem("pinkagaric_3", PDBlocks.PINKAGARIC_3);
    public static final DeferredItem<BlockItem> DYEDREAM_BUD_0 = PDItems.ITEMS.registerSimpleBlockItem("dyedream_bud_0", PDBlocks.DYEDREAM_BUD_0);
    public static final DeferredItem<BlockItem> DYEDREAM_BUD_1 = PDItems.ITEMS.registerSimpleBlockItem("dyedream_bud_1", PDBlocks.DYEDREAM_BUD_1);
    public static final DeferredItem<BlockItem> DYEDREAM_BUD_2 = PDItems.ITEMS.registerSimpleBlockItem("dyedream_bud_2", PDBlocks.DYEDREAM_BUD_2);
    public static final DeferredItem<BlockItem> ICE_BUD_0 = PDItems.ITEMS.registerSimpleBlockItem("ice_bud_0", PDBlocks.ICE_BUD_0);
    public static final DeferredItem<BlockItem> DYEDREAM_LILY_PAD = PDItems.ITEMS.registerItem("dyedream_lily_pad",
            p -> new PlaceOnWaterBlockItem(PDBlocks.DYEDREAM_LILY_PAD.get(), p));
    public static final DeferredItem<BlockItem> DYEDREAM_LOTUS = PDItems.ITEMS.registerItem("dyedream_lotus",
            p -> new PlaceOnWaterBlockItem(PDBlocks.DYEDREAM_LOTUS.get(), p));
    public static final DeferredItem<BlockItem> DYEDREAM_SEAGRASS = PDItems.ITEMS.registerSimpleBlockItem("dyedream_seagrass", PDBlocks.DYEDREAM_SEAGRASS);
    public static final DeferredItem<BlockItem> DYEDREAM_SAPLING = PDItems.ITEMS.registerSimpleBlockItem("dyedream_sapling", PDBlocks.DYEDREAM_SAPLING);
    public static final DeferredItem<BlockItem> DYEDREAM_CRACK = PDItems.ITEMS.registerSimpleBlockItem("dyedream_crack", PDBlocks.DYEDREAM_CRACK);


    // ==================== 云朵方块 BlockItem ====================
    public static final DeferredItem<BlockItem> CLOUD = PDItems.ITEMS.registerSimpleBlockItem("cloud", PDBlocks.CLOUD);
    public static final DeferredItem<BlockItem> DARK_CLOUD = PDItems.ITEMS.registerSimpleBlockItem("dark_cloud", PDBlocks.DARK_CLOUD);
    public static final DeferredItem<BlockItem> THICK_CLOUD = PDItems.ITEMS.registerSimpleBlockItem("thick_cloud", PDBlocks.THICK_CLOUD);


    // ==================== 染梦花草 BlockItem ====================
    public static final DeferredItem<BlockItem> FLOWER_1 = PDItems.ITEMS.registerSimpleBlockItem("flower_1", PDBlocks.FLOWER_1);
    public static final DeferredItem<BlockItem> FLOWER_2 = PDItems.ITEMS.registerSimpleBlockItem("flower_2", PDBlocks.FLOWER_2);
    public static final DeferredItem<BlockItem> FLOWER_3 = PDItems.ITEMS.registerSimpleBlockItem("flower_3", PDBlocks.FLOWER_3);
    public static final DeferredItem<BlockItem> FLOWER_5 = PDItems.ITEMS.registerSimpleBlockItem("flower_5", PDBlocks.FLOWER_5);
    public static final DeferredItem<BlockItem> FLOWER_6 = PDItems.ITEMS.registerSimpleBlockItem("flower_6", PDBlocks.FLOWER_6);
    public static final DeferredItem<BlockItem> FLOWER_7 = PDItems.ITEMS.registerSimpleBlockItem("flower_7", PDBlocks.FLOWER_7);
    public static final DeferredItem<BlockItem> FLOWER_8 = PDItems.ITEMS.registerSimpleBlockItem("flower_8", PDBlocks.FLOWER_8);
    public static final DeferredItem<BlockItem> FLOWER_9 = PDItems.ITEMS.registerSimpleBlockItem("flower_9", PDBlocks.FLOWER_9);
    public static final DeferredItem<BlockItem> FLOWER_10 = PDItems.ITEMS.registerSimpleBlockItem("flower_10", PDBlocks.FLOWER_10);
    public static final DeferredItem<BlockItem> FLOWER_11 = PDItems.ITEMS.registerSimpleBlockItem("flower_11", PDBlocks.FLOWER_11);
    public static final DeferredItem<BlockItem> FLOWER_12 = PDItems.ITEMS.registerSimpleBlockItem("flower_12", PDBlocks.FLOWER_12);
    public static final DeferredItem<BlockItem> FLOWER_13 = PDItems.ITEMS.registerSimpleBlockItem("flower_13", PDBlocks.FLOWER_13);
    public static final DeferredItem<BlockItem> FLOWER_14 = PDItems.ITEMS.registerSimpleBlockItem("flower_14", PDBlocks.FLOWER_14);
    public static final DeferredItem<BlockItem> FLOWER_15 = PDItems.ITEMS.registerSimpleBlockItem("flower_15", PDBlocks.FLOWER_15);
    public static final DeferredItem<BlockItem> FLOWER_16 = PDItems.ITEMS.registerSimpleBlockItem("flower_16", PDBlocks.FLOWER_16);
    public static final DeferredItem<BlockItem> FLOWER_17 = PDItems.ITEMS.registerSimpleBlockItem("flower_17", PDBlocks.FLOWER_17);
    public static final DeferredItem<BlockItem> FLOWER_18 = PDItems.ITEMS.registerSimpleBlockItem("flower_18", PDBlocks.FLOWER_18);
    public static final DeferredItem<BlockItem> GRASS_1 = PDItems.ITEMS.registerSimpleBlockItem("grass_1", PDBlocks.GRASS_1);
    public static final DeferredItem<BlockItem> GRASS_2 = PDItems.ITEMS.registerSimpleBlockItem("grass_2", PDBlocks.GRASS_2);
    public static final DeferredItem<BlockItem> GRASS_3 = PDItems.ITEMS.registerSimpleBlockItem("grass_3", PDBlocks.GRASS_3);
    public static final DeferredItem<BlockItem> GRASS_4 = PDItems.ITEMS.registerSimpleBlockItem("grass_4", PDBlocks.GRASS_4);
    public static final DeferredItem<BlockItem> GRASS_5 = PDItems.ITEMS.registerSimpleBlockItem("grass_5", PDBlocks.GRASS_5);
    public static final DeferredItem<BlockItem> GRASS_6 = PDItems.ITEMS.registerSimpleBlockItem("grass_6", PDBlocks.GRASS_6);
    public static final DeferredItem<BlockItem> GRASS_7 = PDItems.ITEMS.registerSimpleBlockItem("grass_7", PDBlocks.GRASS_7);
    public static final DeferredItem<BlockItem> GRASS_8 = PDItems.ITEMS.registerSimpleBlockItem("grass_8", PDBlocks.GRASS_8);
    public static final DeferredItem<BlockItem> GRASS_9 = PDItems.ITEMS.registerSimpleBlockItem("grass_9", PDBlocks.GRASS_9);
    public static final DeferredItem<BlockItem> GRASS_10 = PDItems.ITEMS.registerSimpleBlockItem("grass_10", PDBlocks.GRASS_10);
    public static final DeferredItem<BlockItem> GRASS_11 = PDItems.ITEMS.registerSimpleBlockItem("grass_11", PDBlocks.GRASS_11);
    public static final DeferredItem<BlockItem> GRASS_12 = PDItems.ITEMS.registerSimpleBlockItem("grass_12", PDBlocks.GRASS_12);
    public static final DeferredItem<BlockItem> GRASS_13 = PDItems.ITEMS.registerSimpleBlockItem("grass_13", PDBlocks.GRASS_13);
    public static final DeferredItem<BlockItem> GRASS_14 = PDItems.ITEMS.registerSimpleBlockItem("grass_14", PDBlocks.GRASS_14);
    public static final DeferredItem<BlockItem> GRASS_15 = PDItems.ITEMS.registerSimpleBlockItem("grass_15", PDBlocks.GRASS_15);


    // ==================== 阴影维度方块物品 ====================

    public static final DeferredItem<BlockItem> SHADOW_BLOCK = PDItems.ITEMS.registerSimpleBlockItem("shadow_block", PDBlocks.SHADOW_BLOCK);
    public static final DeferredItem<BlockItem> THICK_SHADOW_BLOCK = PDItems.ITEMS.registerSimpleBlockItem("thick_shadow_block", PDBlocks.THICK_SHADOW_BLOCK);
    public static final DeferredItem<BlockItem> SHADOW_STONE = PDItems.ITEMS.registerSimpleBlockItem("shadow_stone", PDBlocks.SHADOW_STONE);
    public static final DeferredItem<BlockItem> SHADOW_STONE_BRICK = PDItems.ITEMS.registerSimpleBlockItem("shadow_stone_brick", PDBlocks.SHADOW_STONE_BRICK);
    public static final DeferredItem<BlockItem> SHADOW_STONE_BRICKS = PDItems.ITEMS.registerSimpleBlockItem("shadow_stone_bricks", PDBlocks.SHADOW_STONE_BRICKS);
    public static final DeferredItem<BlockItem> SHADOW_STONE_TILES = PDItems.ITEMS.registerSimpleBlockItem("shadow_stone_tiles", PDBlocks.SHADOW_STONE_TILES);
    public static final DeferredItem<BlockItem> CHISELED_SHADOW_STONE_BRICK = PDItems.ITEMS.registerSimpleBlockItem("chiseled_shadow_stone_brick", PDBlocks.CHISELED_SHADOW_STONE_BRICK);
    public static final DeferredItem<BlockItem> CRACKED_SHADOW_STONE_BRICK = PDItems.ITEMS.registerSimpleBlockItem("cracked_shadow_stone_brick", PDBlocks.CRACKED_SHADOW_STONE_BRICK);
    public static final DeferredItem<BlockItem> SHADOW_NYLIUM = PDItems.ITEMS.registerSimpleBlockItem("shadow_nylium", PDBlocks.SHADOW_NYLIUM);
    public static final DeferredItem<BlockItem> SHADOW_SHROOMLIGHT = PDItems.ITEMS.registerSimpleBlockItem("shadow_shroomlight", PDBlocks.SHADOW_SHROOMLIGHT);
    public static final DeferredItem<BlockItem> SHADOW_WART_BLOCK = PDItems.ITEMS.registerSimpleBlockItem("shadow_wart_block", PDBlocks.SHADOW_WART_BLOCK);
    public static final DeferredItem<BlockItem> SHADOW_STEM = PDItems.ITEMS.registerSimpleBlockItem("shadow_stem", PDBlocks.SHADOW_STEM);
    public static final DeferredItem<BlockItem> SHADOW_HYPHAE = PDItems.ITEMS.registerSimpleBlockItem("shadow_hyphae", PDBlocks.SHADOW_HYPHAE);
    public static final DeferredItem<BlockItem> STRIPPED_SHADOW_STEM = PDItems.ITEMS.registerSimpleBlockItem("stripped_shadow_stem", PDBlocks.STRIPPED_SHADOW_STEM);
    public static final DeferredItem<BlockItem> STRIPPED_SHADOW_HYPHAE = PDItems.ITEMS.registerSimpleBlockItem("stripped_shadow_hyphae", PDBlocks.STRIPPED_SHADOW_HYPHAE);
    public static final DeferredItem<BlockItem> SHADOW_PLANKS = PDItems.ITEMS.registerSimpleBlockItem("shadow_planks", PDBlocks.SHADOW_PLANKS);

    // 阴影石砖变体
    public static final DeferredItem<BlockItem> SHADOW_STONE_BRICK_STAIRS = PDItems.ITEMS.registerSimpleBlockItem("shadow_stone_brick_stairs", PDBlocks.SHADOW_STONE_BRICK_STAIRS);
    public static final DeferredItem<BlockItem> SHADOW_STONE_BRICK_SLAB = PDItems.ITEMS.registerSimpleBlockItem("shadow_stone_brick_slab", PDBlocks.SHADOW_STONE_BRICK_SLAB);
    public static final DeferredItem<BlockItem> SHADOW_STONE_BRICK_WALL = PDItems.ITEMS.registerSimpleBlockItem("shadow_stone_brick_wall", PDBlocks.SHADOW_STONE_BRICK_WALL);
    public static final DeferredItem<BlockItem> SHADOW_STONE_BRICKS_STAIRS = PDItems.ITEMS.registerSimpleBlockItem("shadow_stone_bricks_stairs", PDBlocks.SHADOW_STONE_BRICKS_STAIRS);
    public static final DeferredItem<BlockItem> SHADOW_STONE_BRICKS_SLAB = PDItems.ITEMS.registerSimpleBlockItem("shadow_stone_bricks_slab", PDBlocks.SHADOW_STONE_BRICKS_SLAB);
    public static final DeferredItem<BlockItem> SHADOW_STONE_BRICKS_WALL = PDItems.ITEMS.registerSimpleBlockItem("shadow_stone_bricks_wall", PDBlocks.SHADOW_STONE_BRICKS_WALL);
    public static final DeferredItem<BlockItem> SHADOW_STONE_TILES_STAIRS = PDItems.ITEMS.registerSimpleBlockItem("shadow_stone_tiles_stairs", PDBlocks.SHADOW_STONE_TILES_STAIRS);
    public static final DeferredItem<BlockItem> SHADOW_STONE_TILES_SLAB = PDItems.ITEMS.registerSimpleBlockItem("shadow_stone_tiles_slab", PDBlocks.SHADOW_STONE_TILES_SLAB);
    public static final DeferredItem<BlockItem> SHADOW_STONE_TILES_WALL = PDItems.ITEMS.registerSimpleBlockItem("shadow_stone_tiles_wall", PDBlocks.SHADOW_STONE_TILES_WALL);

    // 阴影木板变体
    public static final DeferredItem<BlockItem> SHADOW_PLANKS_STAIRS = PDItems.ITEMS.registerSimpleBlockItem("shadow_planks_stairs", PDBlocks.SHADOW_PLANKS_STAIRS);
    public static final DeferredItem<BlockItem> SHADOW_PLANKS_SLAB = PDItems.ITEMS.registerSimpleBlockItem("shadow_planks_slab", PDBlocks.SHADOW_PLANKS_SLAB);
    public static final DeferredItem<BlockItem> SHADOW_PLANKS_FENCE = PDItems.ITEMS.registerSimpleBlockItem("shadow_planks_fence", PDBlocks.SHADOW_PLANKS_FENCE);
    public static final DeferredItem<BlockItem> SHADOW_PLANKS_FENCEGATE = PDItems.ITEMS.registerSimpleBlockItem("shadow_planks_fencegate", PDBlocks.SHADOW_PLANKS_FENCEGATE);
    public static final DeferredItem<BlockItem> SHADOW_PLANKS_DOOR = PDItems.ITEMS.registerSimpleBlockItem("shadow_planks_door", PDBlocks.SHADOW_PLANKS_DOOR);
    public static final DeferredItem<BlockItem> SHADOW_PLANKS_TRAPDOOR = PDItems.ITEMS.registerSimpleBlockItem("shadow_planks_trapdoor", PDBlocks.SHADOW_PLANKS_TRAPDOOR);
    public static final DeferredItem<BlockItem> SHADOW_PLANKS_PRESSURE_PLATE = PDItems.ITEMS.registerSimpleBlockItem("shadow_planks_pressure_plate", PDBlocks.SHADOW_PLANKS_PRESSURE_PLATE);
    public static final DeferredItem<BlockItem> SHADOW_PLANKS_BUTTON = PDItems.ITEMS.registerSimpleBlockItem("shadow_planks_button", PDBlocks.SHADOW_PLANKS_BUTTON);
    public static final DeferredItem<BlockItem> SHADOW_PLANKS_PANE = PDItems.ITEMS.registerSimpleBlockItem("shadow_planks_pane", PDBlocks.SHADOW_PLANKS_PANE);

    // 暗影地牢 / 竞技场地砖
    public static final DeferredItem<BlockItem> SHADOW_DUNGEON_BLOCK_0 = PDItems.ITEMS.registerSimpleBlockItem("shadow_dungeon_block_0", PDBlocks.SHADOW_DUNGEON_BLOCK_0);
    public static final DeferredItem<BlockItem> SHADOW_DUNGEON_BLOCK_1 = PDItems.ITEMS.registerSimpleBlockItem("shadow_dungeon_block_1", PDBlocks.SHADOW_DUNGEON_BLOCK_1);
    public static final DeferredItem<BlockItem> SHADOW_DUNGEON_BLOCK_2 = PDItems.ITEMS.registerSimpleBlockItem("shadow_dungeon_block_2", PDBlocks.SHADOW_DUNGEON_BLOCK_2);
    public static final DeferredItem<BlockItem> SHADOW_DUNGEON_BLOCK_3 = PDItems.ITEMS.registerSimpleBlockItem("shadow_dungeon_block_3", PDBlocks.SHADOW_DUNGEON_BLOCK_3);
    public static final DeferredItem<BlockItem> SHADOW_DUNGEON_BLOCK_4 = PDItems.ITEMS.registerSimpleBlockItem("shadow_dungeon_block_4", PDBlocks.SHADOW_DUNGEON_BLOCK_4);
    public static final DeferredItem<BlockItem> SHADOW_DUNGEON_BLOCK_5 = PDItems.ITEMS.registerSimpleBlockItem("shadow_dungeon_block_5", PDBlocks.SHADOW_DUNGEON_BLOCK_5);
    public static final DeferredItem<BlockItem> SHADOW_DUNGEON_BLOCK_6 = PDItems.ITEMS.registerSimpleBlockItem("shadow_dungeon_block_6", PDBlocks.SHADOW_DUNGEON_BLOCK_6);
    public static final DeferredItem<BlockItem> SHADOW_ARENA_BLOCK_0 = PDItems.ITEMS.registerSimpleBlockItem("shadow_arena_block_0", PDBlocks.SHADOW_ARENA_BLOCK_0);

    // 暗影地牢功能性方块
    public static final DeferredItem<BlockItem> LOOSE_SHADOW_DUNGEON_BLOCK = PDItems.ITEMS.registerSimpleBlockItem("loose_shadow_dungeon_block", PDBlocks.LOOSE_SHADOW_DUNGEON_BLOCK);
    public static final DeferredItem<BlockItem> SHADOW_DUNGEON_DOOR_0 = PDItems.ITEMS.registerSimpleBlockItem("shadow_dungeon_door_0", PDBlocks.SHADOW_DUNGEON_DOOR_0);
    public static final DeferredItem<BlockItem> SHADOW_DUNGEON_DOOR_1 = PDItems.ITEMS.registerSimpleBlockItem("shadow_dungeon_door_1", PDBlocks.SHADOW_DUNGEON_DOOR_1);
    public static final DeferredItem<BlockItem> SHADOWDUNGEONDOOR_2 = PDItems.ITEMS.registerSimpleBlockItem("shadowdungeondoor_2", PDBlocks.SHADOWDUNGEONDOOR_2);
    public static final DeferredItem<BlockItem> SHADOWDUNGEONDOOR_3 = PDItems.ITEMS.registerSimpleBlockItem("shadowdungeondoor_3", PDBlocks.SHADOWDUNGEONDOOR_3);
    public static final DeferredItem<BlockItem> SHADOW_DUNGEON_KEY_0 = PDItems.ITEMS.registerSimpleBlockItem("shadow_dungeon_key_0", PDBlocks.SHADOW_DUNGEON_KEY_0);
    public static final DeferredItem<BlockItem> SHADOW_DUNGEON_KEY_1 = PDItems.ITEMS.registerSimpleBlockItem("shadow_dungeon_key_1", PDBlocks.SHADOW_DUNGEON_KEY_1);
    public static final DeferredItem<BlockItem> SHADOWCANDLE = PDItems.ITEMS.registerSimpleBlockItem("shadowcandle", PDBlocks.SHADOWCANDLE);
    public static final DeferredItem<BlockItem> SHADOW_BLAST_FURNACE_CORE = PDItems.ITEMS.registerSimpleBlockItem("shadow_blast_furnace_core", PDBlocks.SHADOW_BLAST_FURNACE_CORE);

    // 暗影书架系列
    public static final DeferredItem<BlockItem> SHADOWSHELF_0 = PDItems.ITEMS.registerSimpleBlockItem("shadowshelf_0", PDBlocks.SHADOWSHELF_0);
    public static final DeferredItem<BlockItem> SHADOWSHELF_1 = PDItems.ITEMS.registerSimpleBlockItem("shadowshelf_1", PDBlocks.SHADOWSHELF_1);
    public static final DeferredItem<BlockItem> SHADOWSHELF_2 = PDItems.ITEMS.registerSimpleBlockItem("shadowshelf_2", PDBlocks.SHADOWSHELF_2);
    public static final DeferredItem<BlockItem> SHADOWSHELF_3 = PDItems.ITEMS.registerSimpleBlockItem("shadowshelf_3", PDBlocks.SHADOWSHELF_3);

    // 暗影裂隙系列
    public static final DeferredItem<BlockItem> SHADOW_FISSURE_0 = PDItems.ITEMS.registerSimpleBlockItem("shadow_fissure_0", PDBlocks.SHADOW_FISSURE_0);
    public static final DeferredItem<BlockItem> SHADOW_FISSURE_1 = PDItems.ITEMS.registerSimpleBlockItem("shadow_fissure_1", PDBlocks.SHADOW_FISSURE_1);
    public static final DeferredItem<BlockItem> SHADOW_FISSURE_2 = PDItems.ITEMS.registerSimpleBlockItem("shadow_fissure_2", PDBlocks.SHADOW_FISSURE_2);
    public static final DeferredItem<BlockItem> SHADOW_FISSURE_3 = PDItems.ITEMS.registerSimpleBlockItem("shadow_fissure_3", PDBlocks.SHADOW_FISSURE_3);
    public static final DeferredItem<BlockItem> SHADOW_FISSURE_4 = PDItems.ITEMS.registerSimpleBlockItem("shadow_fissure_4", PDBlocks.SHADOW_FISSURE_4);
    public static final DeferredItem<BlockItem> SHADOW_FISSURE_5 = PDItems.ITEMS.registerSimpleBlockItem("shadow_fissure_5", PDBlocks.SHADOW_FISSURE_5);

}
