package com.pasterdream.pasterdreammod.registry;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.api.block.BlockAPI;
import com.pasterdream.pasterdreammod.api.block.BlockConfig;
import com.pasterdream.pasterdreammod.api.block.builder.VariantSetResult;
import com.pasterdream.pasterdreammod.block.AaroncosArenaPortalsBlock;
import com.pasterdream.pasterdreammod.block.AaroncosHandChestBlock;
import com.pasterdream.pasterdreammod.block.AaroncosHandSpawnBlock;
import com.pasterdream.pasterdreammod.block.DreamAccumulatorBlock;
import com.pasterdream.pasterdreammod.block.DreamTrainStructureBlock;
import com.pasterdream.pasterdreammod.block.DyedreamCrackBlock;
import com.pasterdream.pasterdreammod.block.DyedreamDeskBlock;
import com.pasterdream.pasterdreammod.block.DyedreamLilyPadBlock;
import com.pasterdream.pasterdreammod.block.DyedreamLotusBlock;
import com.pasterdream.pasterdreammod.block.DyedreamPlanksPaneBlock;
import com.pasterdream.pasterdreammod.block.DyedreamLarternBlock;
import com.pasterdream.pasterdreammod.block.DyedreamSaplingBlock;
import com.pasterdream.pasterdreammod.block.DyedreamSeagrassBlock;
import com.pasterdream.pasterdreammod.block.CloudBlock;
import com.pasterdream.pasterdreammod.block.DarkCloudBlock;
import com.pasterdream.pasterdreammod.block.TheEndlessBookOfDreamSeekersBlock;
import com.pasterdream.pasterdreammod.block.DyedreamBudBlock;
import com.pasterdream.pasterdreammod.block.DyedreamDoublePlantBlock;
import com.pasterdream.pasterdreammod.block.DyedreamFlowerBlock;
import com.pasterdream.pasterdreammod.block.DyedreamGrassBlock;
import com.pasterdream.pasterdreammod.block.DyedreamLogBlock;
import com.pasterdream.pasterdreammod.block.DyedreamLeavesBlock;
import com.pasterdream.pasterdreammod.block.IceBudBlock;
import com.pasterdream.pasterdreammod.block.LifeCrystalBlock;
import com.pasterdream.pasterdreammod.block.PinkagaricBlock;
import com.pasterdream.pasterdreammod.block.MeltdreamChestBlock;
import com.pasterdream.pasterdreammod.block.MeltdreamChestOpenBlock;
import com.pasterdream.pasterdreammod.block.DreamCauldronBlock;
import com.pasterdream.pasterdreammod.block.ShadowChestBlock;
import com.pasterdream.pasterdreammod.block.ShadowDungeonDoorBlock;
import com.pasterdream.pasterdreammod.block.ShadowDungeonKeyBlock;
import com.pasterdream.pasterdreammod.block.ShadowVortexBlock;
import com.pasterdream.pasterdreammod.block.ShadowshelfBlock;
import com.pasterdream.pasterdreammod.block.ThickCloudBlock;
import com.pasterdream.pasterdreammod.block.Pebble0Block;
import com.pasterdream.pasterdreammod.block.ShadowLight0Block;
import com.pasterdream.pasterdreammod.block.Vine0Block;
import com.pasterdream.pasterdreammod.block.GoldenFoxSculptureBlock;
import com.pasterdream.pasterdreammod.block.GoldenrodBlock;
import com.pasterdream.pasterdreammod.block.QymDoll0Block;
import com.pasterdream.pasterdreammod.block.UuzDoll0Block;
import com.pasterdream.pasterdreammod.block.Crop0ABlock;
import com.pasterdream.pasterdreammod.block.Crop1ABlock;
import com.pasterdream.pasterdreammod.block.Crop3ABlock;
import com.pasterdream.pasterdreammod.block.Crop2ABlock;
import com.pasterdream.pasterdreammod.block.Crop4ABlock;
import com.pasterdream.pasterdreammod.block.GoldenFoxSculptureBlock;
import com.pasterdream.pasterdreammod.block.MeltdreamLiquidBlock;
import com.pasterdream.pasterdreammod.block.QymDoll0Block;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import com.pasterdream.pasterdreammod.registry.blocks.*;

import java.util.Map;

/**
 * 方块注册类
 * 使用 DeferredRegister 模式注册所有方块
 */
public class PDBlocks {

    /**
     * 方块注册器
     */
    public static final DeferredRegister.Blocks BLOCKS = BlockAPI.REGISTRY;

    static {
        BlockAPI.putConfig("dyedream_log", BlockConfig.of()
                .mineable("axe").model("cube_column")
                .tex("end", "pasterdream:block/dyedream_log_top")
                .tex("side", "pasterdream:block/dyedream_log"));
        BlockAPI.putConfig("dyedream_wood", BlockConfig.of()
                .mineable("axe").model("cube_all")
                .tex("all", "pasterdream:block/dyedream_log"));
        BlockAPI.putConfig("pillar_dyedreamquartz_block", BlockConfig.of()
                .mineable("pickaxe").model("cube_column")
                .tex("end", "pasterdream:block/dyedreamquartz_pillar_top")
                .tex("side", "pasterdream:block/dyedreamquartz_pillar"));
        BlockAPI.putConfig("dyedream_leaves", BlockConfig.of()
                .mineable("hoe").model("cube_all")
                .tex("all", "pasterdream:block/dyedream_leaves"));
        BlockAPI.putConfig("dyedream_glowing_leaves", BlockConfig.of()
                .mineable("hoe").model("cube_all")
                .tex("all", "pasterdream:block/dyedream_glowing_leaves"));
        BlockAPI.putConfig("dyedream_hanging_vine", BlockConfig.of()
                .mineable("hoe"));
        BlockAPI.putConfig("dyedream_fallen_leaves", BlockConfig.of()
                .mineable("hoe"));
        BlockAPI.putConfig("dyedream_grass", BlockConfig.of()
                .mineable("shovel").model("cube_top_bottom")
                .tex("top", "pasterdream:block/dyedream_grass_top")
                .tex("side", "pasterdream:block/dyedream_grass_side")
                .tex("bottom", "pasterdream:block/dyedream_dirt"));
        BlockAPI.putConfig("dyedream_lartern", BlockConfig.of()
                .mineable("pickaxe"));
        BlockAPI.putConfig("dyedream_desk", BlockConfig.of()
                .mineable("axe"));

        // ========== Phase 1 移植方块 ==========
        BlockAPI.putConfig("titanium_block", BlockConfig.of().mineable("pickaxe"));
        BlockAPI.putConfig("raw_titanium_block", BlockConfig.of().mineable("pickaxe"));
        BlockAPI.putConfig("moltengold_block", BlockConfig.of().mineable("pickaxe"));
        BlockAPI.putConfig("blackmetal_block", BlockConfig.of().mineable("pickaxe"));
        BlockAPI.putConfig("charged_amethyst_block", BlockConfig.of().mineable("pickaxe"));
        BlockAPI.putConfig("wind_iron_block", BlockConfig.of().mineable("pickaxe"));
        BlockAPI.putConfig("deepslate_titanium_ore", BlockConfig.of().mineable("pickaxe"));
        BlockAPI.putConfig("moltengold_ore", BlockConfig.of().mineable("pickaxe"));
        BlockAPI.putConfig("soul_ore", BlockConfig.of().mineable("pickaxe"));

        // ========== 手动注册的 requiresCorrectToolForDrops 方块 ==========
        BlockAPI.putConfig("dream_accumulator", BlockConfig.of().mineable("pickaxe"));
        BlockAPI.putConfig("dyedream_bud_0", BlockConfig.of().mineable("pickaxe"));
        BlockAPI.putConfig("dyedream_bud_1", BlockConfig.of().mineable("pickaxe"));
        BlockAPI.putConfig("dyedream_bud_2", BlockConfig.of().mineable("pickaxe"));
        BlockAPI.putConfig("ice_bud_0", BlockConfig.of().mineable("pickaxe"));
        BlockAPI.putConfig("shadow_light_0", BlockConfig.of().mineable("pickaxe"));

        // ========== 玻璃面板 ==========
        BlockAPI.putConfig("dyedream_glasspane", BlockConfig.of().mineable("pickaxe"));
        BlockAPI.putConfig("carve_dyedream_glasspane", BlockConfig.of().mineable("pickaxe"));
        BlockAPI.putConfig("gold_carve_dyedream_glasspane", BlockConfig.of().mineable("pickaxe"));

        // ========== 木板屏风 ==========
        BlockAPI.putConfig("dyedream_planks_pane", BlockConfig.of().mineable("axe"));

        // ========== 石英/花蕾变体系列 ==========
        BlockAPI.putConfig("dyedream_bud_stairs", BlockConfig.of().mineable("pickaxe"));
        BlockAPI.putConfig("dyedream_bud_slab", BlockConfig.of().mineable("pickaxe"));
        BlockAPI.putConfig("dyedream_bud_wall", BlockConfig.of().mineable("pickaxe"));
        BlockAPI.putConfig("dyedreamquartz_block_stairs", BlockConfig.of().mineable("pickaxe"));
        BlockAPI.putConfig("dyedreamquartz_block_slab", BlockConfig.of().mineable("pickaxe"));
        BlockAPI.putConfig("dyedreamquartz_block_wall", BlockConfig.of().mineable("pickaxe"));

        // ========== 钙华变体系列 ==========
        BlockAPI.putConfig("calcite_tiles", BlockConfig.of().mineable("pickaxe"));
        BlockAPI.putConfig("calcite_tiles_stairs", BlockConfig.of().mineable("pickaxe"));
        BlockAPI.putConfig("calcite_tiles_slab", BlockConfig.of().mineable("pickaxe"));
        BlockAPI.putConfig("calcite_tiles_wall", BlockConfig.of().mineable("pickaxe"));
        BlockAPI.putConfig("polished_calcite_slab", BlockConfig.of().mineable("pickaxe"));
        BlockAPI.putConfig("polished_calcite_wall", BlockConfig.of().mineable("pickaxe"));
        BlockAPI.putConfig("polished_calcite_stairs", BlockConfig.of().mineable("pickaxe"));

        // ========== 梦境列车结构方块 ==========
        BlockAPI.putConfig("dream_train_structure", BlockConfig.of().mineable("pickaxe"));

        // ========== 融梦水晶箱 ==========
        BlockAPI.putConfig("meltdream_chest", BlockConfig.of().mineable("pickaxe"));

        // ========== 寻梦者的永恒书卷 & 梦境炼药锅 ==========
        BlockAPI.putConfig("the_endless_book_of_dream_seekers", BlockConfig.of().mineable("axe"));
        BlockAPI.putConfig("dream_cauldron", BlockConfig.of().mineable("pickaxe"));

        // ========== BOSS 相关方块 ==========
        BlockAPI.putConfig("aaroncos_arena_portals", BlockConfig.of().mineable("pickaxe"));
        BlockAPI.putConfig("aaroncos_hand_chest", BlockConfig.of().mineable("pickaxe"));
        BlockAPI.putConfig("aaroncoshandspawnblock", BlockConfig.of().mineable("pickaxe"));

        // ========== 玩偶/雕像方块 ==========
        BlockAPI.putConfig("qin_doll_0", BlockConfig.of());
        BlockAPI.putConfig("little_purple_doll_0", BlockConfig.of());
        BlockAPI.putConfig("golden_fox_sculpture", BlockConfig.of());

        // ========== [分区R] 研究台组 ==========
        BlockAPI.putConfig("research_table", BlockConfig.of().mineable("axe"));
        BlockAPI.putConfig("shadow_blast_furnace", BlockConfig.of().mineable("pickaxe"));
        BlockAPI.putConfig("forced_tower", BlockConfig.of().mineable("pickaxe"));
        BlockAPI.putConfig("dream_spawner_0", BlockConfig.of().mineable("pickaxe"));
    }

    // ==================== 子文件聚合引用 ====================

    // --- PDBlocksCustom ---
    public static final DeferredBlock<?> DREAM_ACCUMULATOR = PDBlocksCustom.DREAM_ACCUMULATOR;
    public static final DeferredBlock<?> DYEDREAM_DESK = PDBlocksCustom.DYEDREAM_DESK;
    public static final DeferredBlock<?> DREAM_TRAIN_STRUCTURE = PDBlocksCustom.DREAM_TRAIN_STRUCTURE;
    public static final DeferredBlock<?> LIFE_CRYSTAL = PDBlocksCustom.LIFE_CRYSTAL;

    // --- PDBlocksDolls ---
    public static final DeferredBlock<?> QIN_DOLL_0 = PDBlocksDolls.QIN_DOLL_0;
    public static final DeferredBlock<?> LITTLE_PURPLE_DOLL_0 = PDBlocksDolls.LITTLE_PURPLE_DOLL_0;
    public static final DeferredBlock<?> GOLDEN_FOX_SCULPTURE = PDBlocksDolls.GOLDEN_FOX_SCULPTURE;
    public static final DeferredBlock<?> LOVE_U_DOLL = PDBlocksDolls.LOVE_U_DOLL;
    public static final DeferredBlock<?> EOUL_DOLL = PDBlocksDolls.EOUL_DOLL;
    public static final DeferredBlock<?> SHADOW_CHEST = PDBlocksDolls.SHADOW_CHEST;

    // --- PDBlocksFunctional ---
    public static final DeferredBlock<?> THE_ENDLESS_BOOK_OF_DREAM_SEEKERS = PDBlocksFunctional.THE_ENDLESS_BOOK_OF_DREAM_SEEKERS;
    public static final DeferredBlock<?> DREAM_CAULDRON = PDBlocksFunctional.DREAM_CAULDRON;
    public static final DeferredBlock<?> MELTDREAM_CHEST = PDBlocksFunctional.MELTDREAM_CHEST;
    public static final DeferredBlock<?> MELTDREAM_CHEST_OPEN = PDBlocksFunctional.MELTDREAM_CHEST_OPEN;

    // --- PDBlocksSimple ---
    public static final DeferredBlock<?> DYEDREAM_BLOCK = PDBlocksSimple.DYEDREAM_BLOCK;
    public static final DeferredBlock<?> DYEDREAM_DIRT = PDBlocksSimple.DYEDREAM_DIRT;
    public static final DeferredBlock<?> DYEDREAM_SAND = PDBlocksSimple.DYEDREAM_SAND;
    public static final DeferredBlock<?> DYEDREAM_PLANKS = PDBlocksSimple.DYEDREAM_PLANKS;
    public static final DeferredBlock<?> DYEDREAM_GLASS = PDBlocksSimple.DYEDREAM_GLASS;
    public static final DeferredBlock<?> DYEDREAM_ICE = PDBlocksSimple.DYEDREAM_ICE;
    public static final DeferredBlock<?> DYEDREAM_PACKED_ICE = PDBlocksSimple.DYEDREAM_PACKED_ICE;
    public static final DeferredBlock<?> DYEDREAMQUARTZ_BLOCK = PDBlocksSimple.DYEDREAMQUARTZ_BLOCK;
    public static final DeferredBlock<?> SMOOTH_DYEDREAMQUARTZ_BLOCK = PDBlocksSimple.SMOOTH_DYEDREAMQUARTZ_BLOCK;
    public static final DeferredBlock<?> BRICKS_DYEDREAMQUARTZ_BLOCK = PDBlocksSimple.BRICKS_DYEDREAMQUARTZ_BLOCK;
    public static final DeferredBlock<?> MELTDREAM_CRYSTAL_LAMP = PDBlocksSimple.MELTDREAM_CRYSTAL_LAMP;
    public static final DeferredBlock<?> CHISELED_DYEDREAMQUARTZ_BLOCK = PDBlocksSimple.CHISELED_DYEDREAMQUARTZ_BLOCK;
    public static final DeferredBlock<?> DYEDREAM_BUD_BLOCK = PDBlocksSimple.DYEDREAM_BUD_BLOCK;
    public static final DeferredBlock<?> PINKSLIME_BLOCK = PDBlocksSimple.PINKSLIME_BLOCK;
    public static final DeferredBlock<?> ICESTONE = PDBlocksSimple.ICESTONE;
    public static final DeferredBlock<?> DYEDREAM_WORLDTREE_LEAVES = PDBlocksSimple.DYEDREAM_WORLDTREE_LEAVES;
    public static final DeferredBlock<?> DYEDREAMQUARTZ_ORE = PDBlocksSimple.DYEDREAMQUARTZ_ORE;
    public static final DeferredBlock<?> DYEDREAMDUST_ORE = PDBlocksSimple.DYEDREAMDUST_ORE;
    public static final DeferredBlock<?> AMBER_CANDY_ORE = PDBlocksSimple.AMBER_CANDY_ORE;
    public static final DeferredBlock<?> TITANIUM_ORE = PDBlocksSimple.TITANIUM_ORE;
    public static final DeferredBlock<?> WINDRUNNER_CRYSTAL_ORE = PDBlocksSimple.WINDRUNNER_CRYSTAL_ORE;
    public static final DeferredBlock<?> CONGEAL_WIND_ORE = PDBlocksSimple.CONGEAL_WIND_ORE;
    public static final DeferredBlock<?> CARVE_DYEDREAM_GLASS = PDBlocksSimple.CARVE_DYEDREAM_GLASS;
    public static final DeferredBlock<?> GOLD_CARVE_DYEDREAM_GLASS = PDBlocksSimple.GOLD_CARVE_DYEDREAM_GLASS;
    public static final DeferredBlock<?> POLISHED_CALCITE = PDBlocksSimple.POLISHED_CALCITE;
    public static final DeferredBlock<?> CALCITE_TILES = PDBlocksSimple.CALCITE_TILES;
    public static final DeferredBlock<?> DYEDREAM_DEEPSTONE = PDBlocksSimple.DYEDREAM_DEEPSTONE;
    public static final DeferredBlock<?> DYEDREAM_SANDSTONE = PDBlocksSimple.DYEDREAM_SANDSTONE;
    public static final DeferredBlock<?> DYEDREAM_LEAVES = PDBlocksSimple.DYEDREAM_LEAVES;
    public static final DeferredBlock<?> DYEDREAM_GRASS = PDBlocksSimple.DYEDREAM_GRASS;
    public static final DeferredBlock<?> DYEDREAM_LOG = PDBlocksSimple.DYEDREAM_LOG;
    public static final DeferredBlock<?> DYEDREAM_WOOD = PDBlocksSimple.DYEDREAM_WOOD;
    public static final DeferredBlock<?> PILLAR_DYEDREAMQUARTZ_BLOCK = PDBlocksSimple.PILLAR_DYEDREAMQUARTZ_BLOCK;
    public static final DeferredBlock<?> DYEDREAM_PLANKS_STAIRS = PDBlocksSimple.DYEDREAM_PLANKS_STAIRS;
    public static final DeferredBlock<?> DYEDREAM_PLANKS_SLAB = PDBlocksSimple.DYEDREAM_PLANKS_SLAB;
    public static final DeferredBlock<?> DYEDREAM_PLANKS_FENCE = PDBlocksSimple.DYEDREAM_PLANKS_FENCE;
    public static final DeferredBlock<?> DYEDREAM_PLANKS_FENCEGATE = PDBlocksSimple.DYEDREAM_PLANKS_FENCEGATE;
    public static final DeferredBlock<?> DYEDREAM_PLANKS_DOOR = PDBlocksSimple.DYEDREAM_PLANKS_DOOR;
    public static final DeferredBlock<?> DYEDREAM_PLANKS_TRAPDOOR = PDBlocksSimple.DYEDREAM_PLANKS_TRAPDOOR;
    public static final DeferredBlock<?> DYEDREAM_PLANKS_PRESSURE_PLATE = PDBlocksSimple.DYEDREAM_PLANKS_PRESSURE_PLATE;
    public static final DeferredBlock<?> DYEDREAM_PLANKS_BUTTON = PDBlocksSimple.DYEDREAM_PLANKS_BUTTON;
    public static final DeferredBlock<?> DYEDREAM_BUD_STAIRS = PDBlocksSimple.DYEDREAM_BUD_STAIRS;
    public static final DeferredBlock<?> DYEDREAMQUARTZ_BLOCK_STAIRS = PDBlocksSimple.DYEDREAMQUARTZ_BLOCK_STAIRS;
    public static final DeferredBlock<?> DYEDREAM_BUD_SLAB = PDBlocksSimple.DYEDREAM_BUD_SLAB;
    public static final DeferredBlock<?> DYEDREAMQUARTZ_BLOCK_SLAB = PDBlocksSimple.DYEDREAMQUARTZ_BLOCK_SLAB;
    public static final DeferredBlock<?> DYEDREAM_BUD_WALL = PDBlocksSimple.DYEDREAM_BUD_WALL;
    public static final DeferredBlock<?> DYEDREAMQUARTZ_BLOCK_WALL = PDBlocksSimple.DYEDREAMQUARTZ_BLOCK_WALL;
    public static final DeferredBlock<?> CALCITE_TILES_STAIRS = PDBlocksSimple.CALCITE_TILES_STAIRS;
    public static final DeferredBlock<?> CALCITE_TILES_SLAB = PDBlocksSimple.CALCITE_TILES_SLAB;
    public static final DeferredBlock<?> POLISHED_CALCITE_SLAB = PDBlocksSimple.POLISHED_CALCITE_SLAB;
    public static final DeferredBlock<?> POLISHED_CALCITE_WALL = PDBlocksSimple.POLISHED_CALCITE_WALL;
    public static final DeferredBlock<?> CALCITE_TILES_WALL = PDBlocksSimple.CALCITE_TILES_WALL;
    public static final DeferredBlock<?> POLISHED_CALCITE_STAIRS = PDBlocksSimple.POLISHED_CALCITE_STAIRS;
    public static final DeferredBlock<?> DYEDREAM_GLASSPANE = PDBlocksSimple.DYEDREAM_GLASSPANE;
    public static final DeferredBlock<?> CARVE_DYEDREAM_GLASSPANE = PDBlocksSimple.CARVE_DYEDREAM_GLASSPANE;
    public static final DeferredBlock<?> GOLD_CARVE_DYEDREAM_GLASSPANE = PDBlocksSimple.GOLD_CARVE_DYEDREAM_GLASSPANE;
    public static final DeferredBlock<?> DYEDREAM_LARTERN = PDBlocksSimple.DYEDREAM_LARTERN;

    // --- PDBlocksVegetation ---
    public static final DeferredBlock<?> DYEDREAM_PLANKS_PANE = PDBlocksVegetation.DYEDREAM_PLANKS_PANE;
    public static final DeferredBlock<?> PINKAGARIC_0 = PDBlocksVegetation.PINKAGARIC_0;
    public static final DeferredBlock<?> PINKAGARIC_1 = PDBlocksVegetation.PINKAGARIC_1;
    public static final DeferredBlock<?> PINKAGARIC_2 = PDBlocksVegetation.PINKAGARIC_2;
    public static final DeferredBlock<?> PINKAGARIC_3 = PDBlocksVegetation.PINKAGARIC_3;
    public static final DeferredBlock<?> DYEDREAM_BUD_0 = PDBlocksVegetation.DYEDREAM_BUD_0;
    public static final DeferredBlock<?> DYEDREAM_BUD_1 = PDBlocksVegetation.DYEDREAM_BUD_1;
    public static final DeferredBlock<?> DYEDREAM_BUD_2 = PDBlocksVegetation.DYEDREAM_BUD_2;
    public static final DeferredBlock<?> ICE_BUD_0 = PDBlocksVegetation.ICE_BUD_0;
    public static final DeferredBlock<?> DYEDREAM_LILY_PAD = PDBlocksVegetation.DYEDREAM_LILY_PAD;
    public static final DeferredBlock<?> DYEDREAM_LOTUS = PDBlocksVegetation.DYEDREAM_LOTUS;
    public static final DeferredBlock<?> DYEDREAM_SEAGRASS = PDBlocksVegetation.DYEDREAM_SEAGRASS;
    public static final DeferredBlock<?> DYEDREAM_SAPLING = PDBlocksVegetation.DYEDREAM_SAPLING;
    public static final DeferredBlock<?> DYEDREAM_CRACK = PDBlocksVegetation.DYEDREAM_CRACK;
    public static final DeferredBlock<?> DYEDREAM_GLOWING_LEAVES = PDBlocksVegetation.DYEDREAM_GLOWING_LEAVES;
    public static final DeferredBlock<?> DYEDREAM_HANGING_VINE = PDBlocksVegetation.DYEDREAM_HANGING_VINE;
    public static final DeferredBlock<?> DYEDREAM_FALLEN_LEAVES = PDBlocksVegetation.DYEDREAM_FALLEN_LEAVES;
    public static final DeferredBlock<?> CLOUD = PDBlocksVegetation.CLOUD;
    public static final DeferredBlock<?> DARK_CLOUD = PDBlocksVegetation.DARK_CLOUD;
    public static final DeferredBlock<?> THICK_CLOUD = PDBlocksVegetation.THICK_CLOUD;
    public static final DeferredBlock<?> FLOWER_1 = PDBlocksVegetation.FLOWER_1;
    public static final DeferredBlock<?> FLOWER_2 = PDBlocksVegetation.FLOWER_2;
    public static final DeferredBlock<?> FLOWER_3 = PDBlocksVegetation.FLOWER_3;
    public static final DeferredBlock<?> FLOWER_5 = PDBlocksVegetation.FLOWER_5;
    public static final DeferredBlock<?> FLOWER_6 = PDBlocksVegetation.FLOWER_6;
    public static final DeferredBlock<?> FLOWER_7 = PDBlocksVegetation.FLOWER_7;
    public static final DeferredBlock<?> FLOWER_8 = PDBlocksVegetation.FLOWER_8;
    public static final DeferredBlock<?> FLOWER_9 = PDBlocksVegetation.FLOWER_9;
    public static final DeferredBlock<?> FLOWER_10 = PDBlocksVegetation.FLOWER_10;
    public static final DeferredBlock<?> FLOWER_11 = PDBlocksVegetation.FLOWER_11;
    public static final DeferredBlock<?> FLOWER_12 = PDBlocksVegetation.FLOWER_12;
    public static final DeferredBlock<?> FLOWER_13 = PDBlocksVegetation.FLOWER_13;
    public static final DeferredBlock<?> FLOWER_14 = PDBlocksVegetation.FLOWER_14;
    public static final DeferredBlock<?> FLOWER_15 = PDBlocksVegetation.FLOWER_15;
    public static final DeferredBlock<?> FLOWER_16 = PDBlocksVegetation.FLOWER_16;
    public static final DeferredBlock<?> FLOWER_17 = PDBlocksVegetation.FLOWER_17;
    public static final DeferredBlock<?> FLOWER_18 = PDBlocksVegetation.FLOWER_18;
    public static final DeferredBlock<?> GRASS_1 = PDBlocksVegetation.GRASS_1;
    public static final DeferredBlock<?> GRASS_2 = PDBlocksVegetation.GRASS_2;
    public static final DeferredBlock<?> GRASS_3 = PDBlocksVegetation.GRASS_3;
    public static final DeferredBlock<?> GRASS_4 = PDBlocksVegetation.GRASS_4;
    public static final DeferredBlock<?> GRASS_5 = PDBlocksVegetation.GRASS_5;
    public static final DeferredBlock<?> GRASS_6 = PDBlocksVegetation.GRASS_6;
    public static final DeferredBlock<?> GRASS_7 = PDBlocksVegetation.GRASS_7;
    public static final DeferredBlock<?> GRASS_8 = PDBlocksVegetation.GRASS_8;
    public static final DeferredBlock<?> GRASS_9 = PDBlocksVegetation.GRASS_9;
    public static final DeferredBlock<?> GRASS_10 = PDBlocksVegetation.GRASS_10;
    public static final DeferredBlock<?> GRASS_11 = PDBlocksVegetation.GRASS_11;
    public static final DeferredBlock<?> GRASS_12 = PDBlocksVegetation.GRASS_12;
    public static final DeferredBlock<?> GRASS_13 = PDBlocksVegetation.GRASS_13;
    public static final DeferredBlock<?> GRASS_14 = PDBlocksVegetation.GRASS_14;
    public static final DeferredBlock<?> GRASS_15 = PDBlocksVegetation.GRASS_15;
    public static final DeferredBlock<?> TITANIUM_BLOCK = PDBlocksVegetation.TITANIUM_BLOCK;
    public static final DeferredBlock<?> RAW_TITANIUM_BLOCK = PDBlocksVegetation.RAW_TITANIUM_BLOCK;
    public static final DeferredBlock<?> MOLTENGOLD_BLOCK = PDBlocksVegetation.MOLTENGOLD_BLOCK;
    public static final DeferredBlock<?> BLACKMETAL_BLOCK = PDBlocksVegetation.BLACKMETAL_BLOCK;
    public static final DeferredBlock<?> CHARGED_AMETHYST_BLOCK = PDBlocksVegetation.CHARGED_AMETHYST_BLOCK;
    public static final DeferredBlock<?> WIND_IRON_BLOCK = PDBlocksVegetation.WIND_IRON_BLOCK;
    public static final DeferredBlock<?> DEEPSLATE_TITANIUM_ORE = PDBlocksVegetation.DEEPSLATE_TITANIUM_ORE;
    public static final DeferredBlock<?> MOLTENGOLD_ORE = PDBlocksVegetation.MOLTENGOLD_ORE;
    public static final DeferredBlock<?> SOUL_ORE = PDBlocksVegetation.SOUL_ORE;
    public static final DeferredBlock<?> PEBBLE_0 = PDBlocksVegetation.PEBBLE_0;
    public static final DeferredBlock<?> SHADOW_LIGHT_0 = PDBlocksVegetation.SHADOW_LIGHT_0;
    public static final DeferredBlock<?> VINE_0 = PDBlocksVegetation.VINE_0;
    public static final DeferredBlock<?> GOLDENROD = PDBlocksVegetation.GOLDENROD;
    public static final DeferredBlock<?> CROP_0A = PDBlocksVegetation.CROP_0A;
    public static final DeferredBlock<?> CROP_1A = PDBlocksVegetation.CROP_1A;
    public static final DeferredBlock<?> CROP_3A = PDBlocksVegetation.CROP_3A;
    public static final DeferredBlock<?> CROP_4A = PDBlocksVegetation.CROP_4A;
    public static final DeferredBlock<?> CROP_2A = PDBlocksVegetation.CROP_2A;

    // --- PDBlocksMaterials ---
    public static final DeferredBlock<?> MELTDREAM_LIQUID = PDBlocksMaterials.MELTDREAM_LIQUID;

    // --- PDBlocksBoss ---
    public static final DeferredBlock<?> AARONCOS_ARENA_PORTALS = PDBlocksBoss.AARONCOS_ARENA_PORTALS;
    public static final DeferredBlock<?> AARONCOS_HAND_CHEST = PDBlocksBoss.AARONCOS_HAND_CHEST;
    public static final DeferredBlock<?> SHADOW_VORTEX = PDBlocksBoss.SHADOW_VORTEX;
    public static final DeferredBlock<?> AARONCOSHANDSPAWNBLOCK = PDBlocksBoss.AARONCOSHANDSPAWNBLOCK;

    // --- PDBlocksShadow ---
    public static final DeferredBlock<?> SHADOW_BLOCK = PDBlocksShadow.SHADOW_BLOCK;
    public static final DeferredBlock<?> THICK_SHADOW_BLOCK = PDBlocksShadow.THICK_SHADOW_BLOCK;
    public static final DeferredBlock<?> SHADOW_STONE = PDBlocksShadow.SHADOW_STONE;
    public static final DeferredBlock<?> SHADOW_STONE_BRICK = PDBlocksShadow.SHADOW_STONE_BRICK;
    public static final DeferredBlock<?> SHADOW_STONE_BRICKS = PDBlocksShadow.SHADOW_STONE_BRICKS;
    public static final DeferredBlock<?> SHADOW_STONE_TILES = PDBlocksShadow.SHADOW_STONE_TILES;
    public static final DeferredBlock<?> CHISELED_SHADOW_STONE_BRICK = PDBlocksShadow.CHISELED_SHADOW_STONE_BRICK;
    public static final DeferredBlock<?> CRACKED_SHADOW_STONE_BRICK = PDBlocksShadow.CRACKED_SHADOW_STONE_BRICK;
    public static final DeferredBlock<?> SHADOW_NYLIUM = PDBlocksShadow.SHADOW_NYLIUM;
    public static final DeferredBlock<?> SHADOW_SHROOMLIGHT = PDBlocksShadow.SHADOW_SHROOMLIGHT;
    public static final DeferredBlock<?> SHADOW_WART_BLOCK = PDBlocksShadow.SHADOW_WART_BLOCK;
    public static final DeferredBlock<?> SHADOW_STEM = PDBlocksShadow.SHADOW_STEM;
    public static final DeferredBlock<?> SHADOW_HYPHAE = PDBlocksShadow.SHADOW_HYPHAE;
    public static final DeferredBlock<?> STRIPPED_SHADOW_STEM = PDBlocksShadow.STRIPPED_SHADOW_STEM;
    public static final DeferredBlock<?> STRIPPED_SHADOW_HYPHAE = PDBlocksShadow.STRIPPED_SHADOW_HYPHAE;
    public static final DeferredBlock<?> SHADOW_PLANKS = PDBlocksShadow.SHADOW_PLANKS;
    public static final DeferredBlock<?> SHADOW_STONE_BRICK_STAIRS = PDBlocksShadow.SHADOW_STONE_BRICK_STAIRS;
    public static final DeferredBlock<?> SHADOW_STONE_BRICK_SLAB = PDBlocksShadow.SHADOW_STONE_BRICK_SLAB;
    public static final DeferredBlock<?> SHADOW_STONE_BRICK_WALL = PDBlocksShadow.SHADOW_STONE_BRICK_WALL;
    public static final DeferredBlock<?> SHADOW_STONE_BRICKS_STAIRS = PDBlocksShadow.SHADOW_STONE_BRICKS_STAIRS;
    public static final DeferredBlock<?> SHADOW_STONE_BRICKS_SLAB = PDBlocksShadow.SHADOW_STONE_BRICKS_SLAB;
    public static final DeferredBlock<?> SHADOW_STONE_BRICKS_WALL = PDBlocksShadow.SHADOW_STONE_BRICKS_WALL;
    public static final DeferredBlock<?> SHADOW_STONE_TILES_STAIRS = PDBlocksShadow.SHADOW_STONE_TILES_STAIRS;
    public static final DeferredBlock<?> SHADOW_STONE_TILES_SLAB = PDBlocksShadow.SHADOW_STONE_TILES_SLAB;
    public static final DeferredBlock<?> SHADOW_STONE_TILES_WALL = PDBlocksShadow.SHADOW_STONE_TILES_WALL;
    public static final DeferredBlock<?> SHADOW_PLANKS_STAIRS = PDBlocksShadow.SHADOW_PLANKS_STAIRS;
    public static final DeferredBlock<?> SHADOW_PLANKS_SLAB = PDBlocksShadow.SHADOW_PLANKS_SLAB;
    public static final DeferredBlock<?> SHADOW_PLANKS_FENCE = PDBlocksShadow.SHADOW_PLANKS_FENCE;
    public static final DeferredBlock<?> SHADOW_PLANKS_FENCEGATE = PDBlocksShadow.SHADOW_PLANKS_FENCEGATE;
    public static final DeferredBlock<?> SHADOW_PLANKS_DOOR = PDBlocksShadow.SHADOW_PLANKS_DOOR;
    public static final DeferredBlock<?> SHADOW_PLANKS_TRAPDOOR = PDBlocksShadow.SHADOW_PLANKS_TRAPDOOR;
    public static final DeferredBlock<?> SHADOW_PLANKS_PRESSURE_PLATE = PDBlocksShadow.SHADOW_PLANKS_PRESSURE_PLATE;
    public static final DeferredBlock<?> SHADOW_PLANKS_BUTTON = PDBlocksShadow.SHADOW_PLANKS_BUTTON;
    public static final DeferredBlock<?> SHADOW_PLANKS_PANE = PDBlocksShadow.SHADOW_PLANKS_PANE;

    // --- PDBlocksDungeon ---
    public static final DeferredBlock<?> SHADOW_DUNGEON_BLOCK_0 = PDBlocksDungeon.SHADOW_DUNGEON_BLOCK_0;
    public static final DeferredBlock<?> SHADOW_DUNGEON_BLOCK_1 = PDBlocksDungeon.SHADOW_DUNGEON_BLOCK_1;
    public static final DeferredBlock<?> SHADOW_DUNGEON_BLOCK_2 = PDBlocksDungeon.SHADOW_DUNGEON_BLOCK_2;
    public static final DeferredBlock<?> SHADOW_DUNGEON_BLOCK_3 = PDBlocksDungeon.SHADOW_DUNGEON_BLOCK_3;
    public static final DeferredBlock<?> SHADOW_DUNGEON_BLOCK_4 = PDBlocksDungeon.SHADOW_DUNGEON_BLOCK_4;
    public static final DeferredBlock<?> SHADOW_DUNGEON_BLOCK_5 = PDBlocksDungeon.SHADOW_DUNGEON_BLOCK_5;
    public static final DeferredBlock<?> SHADOW_DUNGEON_BLOCK_6 = PDBlocksDungeon.SHADOW_DUNGEON_BLOCK_6;
    public static final DeferredBlock<?> SHADOW_ARENA_BLOCK_0 = PDBlocksDungeon.SHADOW_ARENA_BLOCK_0;
    public static final DeferredBlock<?> LOOSE_SHADOW_DUNGEON_BLOCK = PDBlocksDungeon.LOOSE_SHADOW_DUNGEON_BLOCK;
    public static final DeferredBlock<?> SHADOW_DUNGEON_DOOR_0 = PDBlocksDungeon.SHADOW_DUNGEON_DOOR_0;
    public static final DeferredBlock<?> SHADOW_DUNGEON_DOOR_1 = PDBlocksDungeon.SHADOW_DUNGEON_DOOR_1;
    public static final DeferredBlock<?> SHADOWDUNGEONDOOR_2 = PDBlocksDungeon.SHADOWDUNGEONDOOR_2;
    public static final DeferredBlock<?> SHADOWDUNGEONDOOR_3 = PDBlocksDungeon.SHADOWDUNGEONDOOR_3;
    public static final DeferredBlock<?> SHADOW_DUNGEON_KEY_0 = PDBlocksDungeon.SHADOW_DUNGEON_KEY_0;
    public static final DeferredBlock<?> SHADOW_DUNGEON_KEY_1 = PDBlocksDungeon.SHADOW_DUNGEON_KEY_1;
    public static final DeferredBlock<?> SHADOWCANDLE = PDBlocksDungeon.SHADOWCANDLE;
    public static final DeferredBlock<?> SHADOW_BLAST_FURNACE_CORE = PDBlocksDungeon.SHADOW_BLAST_FURNACE_CORE;
    public static final DeferredBlock<?> SHADOWSHELF_0 = PDBlocksDungeon.SHADOWSHELF_0;
    public static final DeferredBlock<?> SHADOWSHELF_1 = PDBlocksDungeon.SHADOWSHELF_1;
    public static final DeferredBlock<?> SHADOWSHELF_2 = PDBlocksDungeon.SHADOWSHELF_2;
    public static final DeferredBlock<?> SHADOWSHELF_3 = PDBlocksDungeon.SHADOWSHELF_3;
    public static final DeferredBlock<?> SHADOW_FISSURE_0 = PDBlocksDungeon.SHADOW_FISSURE_0;
    public static final DeferredBlock<?> SHADOW_FISSURE_1 = PDBlocksDungeon.SHADOW_FISSURE_1;
    public static final DeferredBlock<?> SHADOW_FISSURE_2 = PDBlocksDungeon.SHADOW_FISSURE_2;
    public static final DeferredBlock<?> SHADOW_FISSURE_3 = PDBlocksDungeon.SHADOW_FISSURE_3;
    public static final DeferredBlock<?> SHADOW_FISSURE_4 = PDBlocksDungeon.SHADOW_FISSURE_4;
    public static final DeferredBlock<?> SHADOW_FISSURE_5 = PDBlocksDungeon.SHADOW_FISSURE_5;

    // --- PDBlocksDyedreamPhase2 ---
    public static final DeferredBlock<?> BIG_BUBBLE = PDBlocksDyedreamPhase2.BIG_BUBBLE;
    public static final DeferredBlock<?> WINDRUNNER_CRYSTAL_BLOCK = PDBlocksDyedreamPhase2.WINDRUNNER_CRYSTAL_BLOCK;
    public static final DeferredBlock<?> CONGEAL_WIND_BLOCK = PDBlocksDyedreamPhase2.CONGEAL_WIND_BLOCK;
    public static final DeferredBlock<?> STARCALL_BLOCK = PDBlocksDyedreamPhase2.STARCALL_BLOCK;
    public static final DeferredBlock<?> STARCALL_CRACK = PDBlocksDyedreamPhase2.STARCALL_CRACK;
    public static final DeferredBlock<?> CYAN_STONE = PDBlocksDyedreamPhase2.CYAN_STONE;
    public static final DeferredBlock<?> CYAN_MOSS_STONE = PDBlocksDyedreamPhase2.CYAN_MOSS_STONE;
    public static final DeferredBlock<?> WHITE_SAND = PDBlocksDyedreamPhase2.WHITE_SAND;
    public static final DeferredBlock<?> SALT_BLOCK = PDBlocksDyedreamPhase2.SALT_BLOCK;
    public static final DeferredBlock<?> CLARITY_GLASS = PDBlocksDyedreamPhase2.CLARITY_GLASS;
    public static final DeferredBlock<?> CARVE_CLARITY_GLASS = PDBlocksDyedreamPhase2.CARVE_CLARITY_GLASS;
    public static final DeferredBlock<?> FRAME_CLARITY_GLASS = PDBlocksDyedreamPhase2.FRAME_CLARITY_GLASS;
    public static final DeferredBlock<?> CLARITY_GLASSPANE = PDBlocksDyedreamPhase2.CLARITY_GLASSPANE;
    public static final DeferredBlock<?> CARVE_CLARITY_GLASSPANE = PDBlocksDyedreamPhase2.CARVE_CLARITY_GLASSPANE;
    public static final DeferredBlock<?> FRAME_CLARITY_GLASSPANE = PDBlocksDyedreamPhase2.FRAME_CLARITY_GLASSPANE;
    public static final DeferredBlock<?> BREAKWIND_CURTAIN = PDBlocksDyedreamPhase2.BREAKWIND_CURTAIN;
    public static final DeferredBlock<?> WINDIRON_BARS = PDBlocksDyedreamPhase2.WINDIRON_BARS;
    public static final DeferredBlock<?> CYAN_STONE_BRICKS = PDBlocksDyedreamPhase2.CYAN_STONE_BRICKS;
    public static final DeferredBlock<?> CYAN_STONE_BRICK_STAIRS = PDBlocksDyedreamPhase2.CYAN_STONE_BRICK_STAIRS;
    public static final DeferredBlock<?> CYAN_STONE_BRICK_SLAB = PDBlocksDyedreamPhase2.CYAN_STONE_BRICK_SLAB;
    public static final DeferredBlock<?> CYAN_STONE_BRICK_WALL = PDBlocksDyedreamPhase2.CYAN_STONE_BRICK_WALL;
    public static final DeferredBlock<?> MOSSY_CYAN_STONE_BRICKS = PDBlocksDyedreamPhase2.MOSSY_CYAN_STONE_BRICKS;
    public static final DeferredBlock<?> MOSSY_CYAN_STONE_BRICK_STAIRS = PDBlocksDyedreamPhase2.MOSSY_CYAN_STONE_BRICK_STAIRS;
    public static final DeferredBlock<?> MOSSY_CYAN_STONE_BRICK_SLAB = PDBlocksDyedreamPhase2.MOSSY_CYAN_STONE_BRICK_SLAB;
    public static final DeferredBlock<?> MOSSY_CYAN_STONE_BRICK_WALL = PDBlocksDyedreamPhase2.MOSSY_CYAN_STONE_BRICK_WALL;
    public static final DeferredBlock<?> CHISELED_CYAN_STONE_BRICKS = PDBlocksDyedreamPhase2.CHISELED_CYAN_STONE_BRICKS;
    public static final DeferredBlock<?> CYAN_STONE_PILLAR = PDBlocksDyedreamPhase2.CYAN_STONE_PILLAR;
    public static final DeferredBlock<?> CYAN_STONE_PRESSURE_PLATE = PDBlocksDyedreamPhase2.CYAN_STONE_PRESSURE_PLATE;
    public static final DeferredBlock<?> CYAN_STONE_BUTTON = PDBlocksDyedreamPhase2.CYAN_STONE_BUTTON;

    // ==================== 暗影方块族补全（波次C：阴影植被/流体 + 风之旅残余 + 杂项） ====================

    // --- PDBlocksShadow（续）：阴影植被与流体 ---
    public static final DeferredBlock<?> SHADOW_FUNGUS = PDBlocksShadow.SHADOW_FUNGUS;
    public static final DeferredBlock<?> SHADOW_LIQUID = PDBlocksShadow.SHADOW_LIQUID;

    // --- PDBlocksWindJourney：风泊木族 ---
    public static final DeferredBlock<?> WINDMOOR_LOG = PDBlocksWindJourney.WINDMOOR_LOG;
    public static final DeferredBlock<?> WINDMOOR_WOOD = PDBlocksWindJourney.WINDMOOR_WOOD;
    public static final DeferredBlock<?> STRIPPED_WINDMOOR_LOG = PDBlocksWindJourney.STRIPPED_WINDMOOR_LOG;
    public static final DeferredBlock<?> STRIPPED_WINDMOOR_WOOD = PDBlocksWindJourney.STRIPPED_WINDMOOR_WOOD;
    public static final DeferredBlock<?> WINDMOOR_PLANKS = PDBlocksWindJourney.WINDMOOR_PLANKS;
    public static final DeferredBlock<?> WINDMOOR_STAIRS = PDBlocksWindJourney.WINDMOOR_STAIRS;
    public static final DeferredBlock<?> WINDMOOR_SLAB = PDBlocksWindJourney.WINDMOOR_SLAB;
    public static final DeferredBlock<?> WINDMOOR_FENCE = PDBlocksWindJourney.WINDMOOR_FENCE;
    public static final DeferredBlock<?> WINDMOOR_FENCE_GATE = PDBlocksWindJourney.WINDMOOR_FENCE_GATE;
    public static final DeferredBlock<?> WINDMOOR_DOOR = PDBlocksWindJourney.WINDMOOR_DOOR;
    public static final DeferredBlock<?> WINDMOOR_TRAPDOOR = PDBlocksWindJourney.WINDMOOR_TRAPDOOR;
    public static final DeferredBlock<?> WINDMOOR_PRESSURE_PLATE = PDBlocksWindJourney.WINDMOOR_PRESSURE_PLATE;
    public static final DeferredBlock<?> WINDMOOR_BUTTON = PDBlocksWindJourney.WINDMOOR_BUTTON;
    public static final DeferredBlock<?> WINDMOOR_LEAVES_0 = PDBlocksWindJourney.WINDMOOR_LEAVES_0;
    public static final DeferredBlock<?> WINDMOOR_LEAVES_1 = PDBlocksWindJourney.WINDMOOR_LEAVES_1;
    public static final DeferredBlock<?> WINDMOOR_LEAVES_2 = PDBlocksWindJourney.WINDMOOR_LEAVES_2;

    // --- PDBlocksWindJourney：锈黑金属族 / 甲胄残骸族 / 弹射装置 / 地表装饰 ---
    public static final DeferredBlock<?> RUST_BLACK_METAL_BLOCK = PDBlocksWindJourney.RUST_BLACK_METAL_BLOCK;
    public static final DeferredBlock<?> RUST_BLACK_METAL_BLOCK_WALL = PDBlocksWindJourney.RUST_BLACK_METAL_BLOCK_WALL;
    public static final DeferredBlock<?> RUST_BLACK_METAL_BLOCK_BARS = PDBlocksWindJourney.RUST_BLACK_METAL_BLOCK_BARS;
    public static final DeferredBlock<?> ARMOR_WRECK_BLOCK_0 = PDBlocksWindJourney.ARMOR_WRECK_BLOCK_0;
    public static final DeferredBlock<?> ARMOR_WRECK_BLOCK_1 = PDBlocksWindJourney.ARMOR_WRECK_BLOCK_1;
    public static final DeferredBlock<?> ARMOR_WRECK_BLOCK_2 = PDBlocksWindJourney.ARMOR_WRECK_BLOCK_2;
    public static final DeferredBlock<?> ARMOR_WRECK_BLOCK_3 = PDBlocksWindJourney.ARMOR_WRECK_BLOCK_3;
    public static final DeferredBlock<?> ARMOR_WRECK_BLOCK_4 = PDBlocksWindJourney.ARMOR_WRECK_BLOCK_4;
    public static final DeferredBlock<?> EJECTION_PRESSURE_BLOCK = PDBlocksWindJourney.EJECTION_PRESSURE_BLOCK;
    public static final DeferredBlock<?> EJECTION_PRESSURE_PLATE = PDBlocksWindJourney.EJECTION_PRESSURE_PLATE;
    public static final DeferredBlock<?> ANGEL_BLOCK = PDBlocksWindJourney.ANGEL_BLOCK;
    public static final DeferredBlock<?> FIREFLY_NEST = PDBlocksWindJourney.FIREFLY_NEST;
    public static final DeferredBlock<?> SMALL_STONE_SPIRIT_BLOCK = PDBlocksWindJourney.SMALL_STONE_SPIRIT_BLOCK;

    // --- PDBlocksMisc：杂项补全 ---
    public static final DeferredBlock<?> LIGHTBALL = PDBlocksMisc.LIGHTBALL;
    public static final DeferredBlock<?> CLAY_POT_0 = PDBlocksMisc.CLAY_POT_0;
    public static final DeferredBlock<?> CLAYPAN_0 = PDBlocksMisc.CLAYPAN_0;
    public static final DeferredBlock<?> CLAYPAN_2 = PDBlocksMisc.CLAYPAN_2;
    public static final DeferredBlock<?> CALLE_CARD_BLOCK = PDBlocksMisc.CALLE_CARD_BLOCK;
    public static final DeferredBlock<?> CHRISTMAS_LIGHTS = PDBlocksMisc.CHRISTMAS_LIGHTS;
    public static final DeferredBlock<?> DREAM_SPAWNER_1 = PDBlocksMisc.DREAM_SPAWNER_1;
    public static final DeferredBlock<?> CROP_0B = PDBlocksMisc.CROP_0B;
    public static final DeferredBlock<?> CROP_1B = PDBlocksMisc.CROP_1B;
    public static final DeferredBlock<?> CROP_2B = PDBlocksMisc.CROP_2B;
    public static final DeferredBlock<?> CROP_3B = PDBlocksMisc.CROP_3B;
    public static final DeferredBlock<?> CROP_4B = PDBlocksMisc.CROP_4B;
    public static final DeferredBlock<?> JUNGLE_SPORE_PLANT = PDBlocksMisc.JUNGLE_SPORE_PLANT;
    public static final DeferredBlock<?> FOURLEAF_CLOVER = PDBlocksMisc.FOURLEAF_CLOVER;
    public static final DeferredBlock<?> FIG_VINE = PDBlocksMisc.FIG_VINE;
    public static final DeferredBlock<?> MEMENTO_ITEM_11 = PDBlocksMisc.MEMENTO_ITEM_11;

    // ==================== [分区W] 武器工坊群 re-export（在此分区内追加） ====================

    // --- PDBlocksWorkshop：激活核心 / 锻造核心 / 四座卫星工位 ---
    public static final DeferredBlock<?> WEAPON_TABLE = PDBlocksWorkshop.WEAPON_TABLE;
    public static final DeferredBlock<?> WEAPON_WORKSHOP = PDBlocksWorkshop.WEAPON_WORKSHOP;
    public static final DeferredBlock<?> WORKSHOP_CAULDEON = PDBlocksWorkshop.WORKSHOP_CAULDEON;
    public static final DeferredBlock<?> WORKSHOP_BLAST = PDBlocksWorkshop.WORKSHOP_BLAST;
    public static final DeferredBlock<?> WORKSHOP_ANVIL = PDBlocksWorkshop.WORKSHOP_ANVIL;
    public static final DeferredBlock<?> WORKSHOP_GRIND = PDBlocksWorkshop.WORKSHOP_GRIND;

    // ==================== [分区R] 研究台组 re-export（在此分区内追加） ====================

    // --- PDBlocksResearch：研究台 / 暗影高炉 / 强征传送塔 / 构梦刷怪笼 ---
    public static final DeferredBlock<?> RESEARCH_TABLE = PDBlocksResearch.RESEARCH_TABLE;
    public static final DeferredBlock<?> SHADOW_BLAST_FURNACE = PDBlocksResearch.SHADOW_BLAST_FURNACE;
    public static final DeferredBlock<?> FORCED_TOWER = PDBlocksResearch.FORCED_TOWER;
    public static final DeferredBlock<?> DREAM_SPAWNER_0 = PDBlocksResearch.DREAM_SPAWNER_0;

    // ==================== [分区F] 容器/家具/杂项（W4） re-export ====================

    // --- PDBlocksStructure：structure_block_0..23 ---
    public static final DeferredBlock<?> STRUCTURE_BLOCK_0 = PDBlocksStructure.STRUCTURE_BLOCK_0;
    public static final DeferredBlock<?> STRUCTURE_BLOCK_1 = PDBlocksStructure.STRUCTURE_BLOCK_1;
    public static final DeferredBlock<?> STRUCTURE_BLOCK_2 = PDBlocksStructure.STRUCTURE_BLOCK_2;
    public static final DeferredBlock<?> STRUCTURE_BLOCK_3 = PDBlocksStructure.STRUCTURE_BLOCK_3;
    public static final DeferredBlock<?> STRUCTURE_BLOCK_4 = PDBlocksStructure.STRUCTURE_BLOCK_4;
    public static final DeferredBlock<?> STRUCTURE_BLOCK_5 = PDBlocksStructure.STRUCTURE_BLOCK_5;
    public static final DeferredBlock<?> STRUCTURE_BLOCK_6 = PDBlocksStructure.STRUCTURE_BLOCK_6;
    public static final DeferredBlock<?> STRUCTURE_BLOCK_7 = PDBlocksStructure.STRUCTURE_BLOCK_7;
    public static final DeferredBlock<?> STRUCTURE_BLOCK_8 = PDBlocksStructure.STRUCTURE_BLOCK_8;
    public static final DeferredBlock<?> STRUCTURE_BLOCK_9 = PDBlocksStructure.STRUCTURE_BLOCK_9;
    public static final DeferredBlock<?> STRUCTURE_BLOCK_10 = PDBlocksStructure.STRUCTURE_BLOCK_10;
    public static final DeferredBlock<?> STRUCTURE_BLOCK_11 = PDBlocksStructure.STRUCTURE_BLOCK_11;
    public static final DeferredBlock<?> STRUCTURE_BLOCK_12 = PDBlocksStructure.STRUCTURE_BLOCK_12;
    public static final DeferredBlock<?> STRUCTURE_BLOCK_13 = PDBlocksStructure.STRUCTURE_BLOCK_13;
    public static final DeferredBlock<?> STRUCTURE_BLOCK_14 = PDBlocksStructure.STRUCTURE_BLOCK_14;
    public static final DeferredBlock<?> STRUCTURE_BLOCK_15 = PDBlocksStructure.STRUCTURE_BLOCK_15;
    public static final DeferredBlock<?> STRUCTURE_BLOCK_16 = PDBlocksStructure.STRUCTURE_BLOCK_16;
    public static final DeferredBlock<?> STRUCTURE_BLOCK_17 = PDBlocksStructure.STRUCTURE_BLOCK_17;
    public static final DeferredBlock<?> STRUCTURE_BLOCK_18 = PDBlocksStructure.STRUCTURE_BLOCK_18;
    public static final DeferredBlock<?> STRUCTURE_BLOCK_19 = PDBlocksStructure.STRUCTURE_BLOCK_19;
    public static final DeferredBlock<?> STRUCTURE_BLOCK_20 = PDBlocksStructure.STRUCTURE_BLOCK_20;
    public static final DeferredBlock<?> STRUCTURE_BLOCK_21 = PDBlocksStructure.STRUCTURE_BLOCK_21;
    public static final DeferredBlock<?> STRUCTURE_BLOCK_22 = PDBlocksStructure.STRUCTURE_BLOCK_22;
    public static final DeferredBlock<?> STRUCTURE_BLOCK_23 = PDBlocksStructure.STRUCTURE_BLOCK_23;

    // --- PDBlocksFurniture：风骑士台 / 玻璃罐 / 容器床 / 杂项功能方块 ---
    public static final DeferredBlock<?> WIND_KNIGHT_SPAWNBLOCK_0 = PDBlocksFurniture.WIND_KNIGHT_SPAWNBLOCK_0;
    public static final DeferredBlock<?> WIND_KNIGHT_SPAWNBLOCK_1 = PDBlocksFurniture.WIND_KNIGHT_SPAWNBLOCK_1;
    public static final DeferredBlock<?> WIND_KNIGHT_SPAWNBLOCK_2 = PDBlocksFurniture.WIND_KNIGHT_SPAWNBLOCK_2;
    public static final DeferredBlock<?> WIND_KNIGHT_SPAWNBLOCK_3 = PDBlocksFurniture.WIND_KNIGHT_SPAWNBLOCK_3;
    public static final DeferredBlock<?> WIND_KNIGHT_SPAWNBLOCK_4 = PDBlocksFurniture.WIND_KNIGHT_SPAWNBLOCK_4;
    public static final DeferredBlock<?> ECOLOGY_GLASS_JAR = PDBlocksFurniture.ECOLOGY_GLASS_JAR;
    public static final DeferredBlock<?> FIREFLY_GLASS_JAR = PDBlocksFurniture.FIREFLY_GLASS_JAR;
    public static final DeferredBlock<?> LIGHT_FIREFLY_GLASS_JAR = PDBlocksFurniture.LIGHT_FIREFLY_GLASS_JAR;
    public static final DeferredBlock<?> PICNIC_BASKET = PDBlocksFurniture.PICNIC_BASKET;
    public static final DeferredBlock<?> SHADOW_DESK = PDBlocksFurniture.SHADOW_DESK;
    public static final DeferredBlock<?> WINDMOOR_CRATE = PDBlocksFurniture.WINDMOOR_CRATE;
    public static final DeferredBlock<?> SHADOW_BED = PDBlocksFurniture.SHADOW_BED;
    public static final DeferredBlock<?> TRUE_SHADOW_BED = PDBlocksFurniture.TRUE_SHADOW_BED;
    public static final DeferredBlock<?> BIRDS_NEST = PDBlocksFurniture.BIRDS_NEST;
    public static final DeferredBlock<?> BROKEN_SHADOW_DUNGEON_PROTAL = PDBlocksFurniture.BROKEN_SHADOW_DUNGEON_PROTAL;
    public static final DeferredBlock<?> CLAYPAN_1 = PDBlocksFurniture.CLAYPAN_1;
    public static final DeferredBlock<?> DESERT_HERO_TOMB = PDBlocksFurniture.DESERT_HERO_TOMB;
    public static final DeferredBlock<?> GUARD_BLOCK = PDBlocksFurniture.GUARD_BLOCK;
    public static final DeferredBlock<?> RESTRAINMOVE_BLOCK = PDBlocksFurniture.RESTRAINMOVE_BLOCK;
    public static final DeferredBlock<?> GUARD_CRYSTAL = PDBlocksFurniture.GUARD_CRYSTAL;
    public static final DeferredBlock<?> LOST_SWORD_BLOCK = PDBlocksFurniture.LOST_SWORD_BLOCK;
    public static final DeferredBlock<?> SHADOW_BRAZIER = PDBlocksFurniture.SHADOW_BRAZIER;
    public static final DeferredBlock<?> SHADOW_TRAP_0 = PDBlocksFurniture.SHADOW_TRAP_0;
    public static final DeferredBlock<?> TWILIGHT_LANTERN = PDBlocksFurniture.TWILIGHT_LANTERN;

}
