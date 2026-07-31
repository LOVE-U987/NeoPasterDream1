package com.pasterdream.pasterdreammod.registry.blocks;

import com.pasterdream.pasterdreammod.api.block.BlockAPI;
import com.pasterdream.pasterdreammod.api.block.BlockConfig;
import com.pasterdream.pasterdreammod.api.block.builder.VariantSetResult;
import com.pasterdream.pasterdreammod.block.*;
import com.pasterdream.pasterdreammod.registry.PDBlocks;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.neoforged.neoforge.registries.DeferredBlock;

import java.util.Map;


/**
 * 简单换皮方块及建筑变体注册（泥土、沙、木板、矿石、楼梯/台阶/墙等）。
 *
 * @see PDBlocks
 */
public class PDBlocksSimple {


    // ==================== 简单换皮方块（API 批量注册） ====================

    private static final Map<String, DeferredBlock<Block>> SIMPLE_BLOCKS = BlockAPI.registerSimpleBlocks()
            .add("dyedream_dirt", Blocks.DIRT, BlockConfig.of()
                    .mineable("shovel").model("cube_all").tex("all", "pasterdream:block/dyedream_dirt"))
            .add("dyedream_sand", Blocks.SAND, BlockConfig.of()
                    .mineable("shovel").model("cube_all").tex("all", "pasterdream:block/dyedream_sand").plantable())
            .add("dyedream_planks", Blocks.OAK_PLANKS, BlockConfig.of()
                    .mineable("axe").model("cube_all").tex("all", "pasterdream:block/dyedream_planks"))
            .add("dyedream_glass", Blocks.GLASS, BlockConfig.of()
                    .mineable("pickaxe").model("cube_all").tex("all", "pasterdream:block/dyedream_glass").renderType("translucent").blockFactory(TransparentBlock::new))
            .add("dyedream_ice", Blocks.ICE, BlockConfig.of()
                    .mineable("pickaxe").model("cube_all").tex("all", "pasterdream:block/dyedream_ice"))
            .add("dyedream_packed_ice", Blocks.PACKED_ICE, BlockConfig.of()
                    .mineable("pickaxe").model("cube_all").tex("all", "pasterdream:block/dyedream_packed_ice"))
            .add("pinkslime_block", Blocks.SLIME_BLOCK, BlockConfig.of()
                    .model("cube_all").tex("all", "pasterdream:block/pinkslime_block"))
            .addCustom("dyedream_block",
                    BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).requiresCorrectToolForDrops(),
                    BlockConfig.of().mineable("pickaxe").model("cube_all").tex("all", "pasterdream:block/dyedream_block").plantable())
            .addCustom("dyedreamquartz_block",
                    BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).requiresCorrectToolForDrops(),
                    BlockConfig.of().mineable("pickaxe").model("cube_all").tex("all", "pasterdream:block/dyedreamquartz"))
            .addCustom("smooth_dyedreamquartz_block",
                    BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).requiresCorrectToolForDrops(),
                    BlockConfig.of().mineable("pickaxe").model("cube_all").tex("all", "pasterdream:block/dyedreamquartz"))
            .addCustom("bricks_dyedreamquartz_block",
                    BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).requiresCorrectToolForDrops(),
                    BlockConfig.of().mineable("pickaxe").model("cube_all").tex("all", "pasterdream:block/dyedreamquartz_brick"))
            .add("chiseled_dyedreamquartz_block", Blocks.STONE, BlockConfig.of()
                    .mineable("pickaxe").model("cube_column").tex("end", "pasterdream:block/dyedreamquartz_chiseled_top").tex("side", "pasterdream:block/dyedreamquartz_chiseled_side"))
            .addCustom("dyedream_bud_block",
                    BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).requiresCorrectToolForDrops(),
                    BlockConfig.of().mineable("pickaxe").model("cube_all").tex("all", "pasterdream:block/dyedream_bud"))
            .addCustom("icestone",
                    BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).requiresCorrectToolForDrops(),
                    BlockConfig.of().mineable("pickaxe").model("cube_all").tex("all", "pasterdream:block/icestone"))
            .add("dyedream_worldtree_leaves", Blocks.OAK_LEAVES, BlockConfig.of()
                    .mineable("hoe").model("cube_all").tex("all", "pasterdream:block/dyedream_worldtree"))
            .addCustom("dyedreamquartz_ore",
                    BlockBehaviour.Properties.ofFullCopy(Blocks.COAL_ORE).requiresCorrectToolForDrops(),
                    BlockConfig.of().mineable("pickaxe").model("cube_all").tex("all", "pasterdream:block/dyedreamquartz_ore"))
            .addCustom("dyedreamdust_ore",
                    BlockBehaviour.Properties.ofFullCopy(Blocks.COAL_ORE).requiresCorrectToolForDrops(),
                    BlockConfig.of().mineable("pickaxe").model("cube_all").tex("all", "pasterdream:block/dyedreamdust_ore"))
            .addCustom("amber_candy_ore",
                    BlockBehaviour.Properties.ofFullCopy(Blocks.COAL_ORE).requiresCorrectToolForDrops(),
                    BlockConfig.of().mineable("pickaxe").model("cube_all").tex("all", "pasterdream:block/amber_candy_ore"))
            .addCustom("titanium_ore",
                    BlockBehaviour.Properties.ofFullCopy(Blocks.DIAMOND_ORE).requiresCorrectToolForDrops(),
                    BlockConfig.of().mineable("pickaxe").model("cube_all").tex("all", "pasterdream:block/titanium_ore"))
            .addCustom("windrunner_crystal_ore",
                    BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_ORE).requiresCorrectToolForDrops(),
                    BlockConfig.of().mineable("pickaxe").model("cube_all").tex("all", "pasterdream:block/windrunner_crystal_ore"))
            .addCustom("congeal_wind_ore",
                    BlockBehaviour.Properties.ofFullCopy(Blocks.COAL_ORE).requiresCorrectToolForDrops(),
                    BlockConfig.of().mineable("pickaxe").model("cube_all").tex("all", "pasterdream:block/congeal_wind_ore"))
            .add("carve_dyedream_glass", Blocks.GLASS, BlockConfig.of()
                    .mineable("pickaxe").model("cube_all").tex("all", "pasterdream:block/carve_dyedream_glass").renderType("translucent").blockFactory(TransparentBlock::new))
            .add("gold_carve_dyedream_glass", Blocks.GLASS, BlockConfig.of()
                    .mineable("pickaxe").model("cube_all").tex("all", "pasterdream:block/gold_carve_dyedream_glass").renderType("translucent").blockFactory(TransparentBlock::new))
            .addCustom("polished_calcite",
                    BlockBehaviour.Properties.ofFullCopy(Blocks.CALCITE),
                    BlockConfig.of().mineable("pickaxe").model("cube_all").tex("all", "pasterdream:block/polished_calcite"))
            .addCustom("calcite_tiles",
                    BlockBehaviour.Properties.ofFullCopy(Blocks.CALCITE),
                    BlockConfig.of().mineable("pickaxe").model("cube_all").tex("all", "pasterdream:block/polished_calcite"))
            .addCustom("dyedream_deepstone",
                    BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE).requiresCorrectToolForDrops(),
                    BlockConfig.of().mineable("pickaxe").model("cube_all").tex("all", "pasterdream:block/dyedream_deepstone"))
            .addCustom("dyedream_sandstone",
                    BlockBehaviour.Properties.ofFullCopy(Blocks.SANDSTONE).requiresCorrectToolForDrops(),
                    BlockConfig.of().mineable("pickaxe").model("cube_all").tex("all", "pasterdream:block/dyedream_sandstone"))
            .build();

    // ==================== 简单方块公开引用 ====================

    public static final DeferredBlock<Block> DYEDREAM_BLOCK = SIMPLE_BLOCKS.get("dyedream_block");
    public static final DeferredBlock<Block> DYEDREAM_DIRT = SIMPLE_BLOCKS.get("dyedream_dirt");
    public static final DeferredBlock<Block> DYEDREAM_SAND = SIMPLE_BLOCKS.get("dyedream_sand");
    public static final DeferredBlock<Block> DYEDREAM_PLANKS = SIMPLE_BLOCKS.get("dyedream_planks");
    public static final DeferredBlock<Block> DYEDREAM_GLASS = SIMPLE_BLOCKS.get("dyedream_glass");
    public static final DeferredBlock<Block> DYEDREAM_ICE = SIMPLE_BLOCKS.get("dyedream_ice");
    public static final DeferredBlock<Block> DYEDREAM_PACKED_ICE = SIMPLE_BLOCKS.get("dyedream_packed_ice");
    public static final DeferredBlock<Block> DYEDREAMQUARTZ_BLOCK = SIMPLE_BLOCKS.get("dyedreamquartz_block");
    public static final DeferredBlock<Block> SMOOTH_DYEDREAMQUARTZ_BLOCK = SIMPLE_BLOCKS.get("smooth_dyedreamquartz_block");
    public static final DeferredBlock<Block> BRICKS_DYEDREAMQUARTZ_BLOCK = SIMPLE_BLOCKS.get("bricks_dyedreamquartz_block");
    public static final DeferredBlock<Block> CHISELED_DYEDREAMQUARTZ_BLOCK = SIMPLE_BLOCKS.get("chiseled_dyedreamquartz_block");
    public static final DeferredBlock<Block> DYEDREAM_BUD_BLOCK = SIMPLE_BLOCKS.get("dyedream_bud_block");
    public static final DeferredBlock<Block> PINKSLIME_BLOCK = SIMPLE_BLOCKS.get("pinkslime_block");
    public static final DeferredBlock<Block> ICESTONE = SIMPLE_BLOCKS.get("icestone");
    public static final DeferredBlock<Block> DYEDREAM_WORLDTREE_LEAVES = SIMPLE_BLOCKS.get("dyedream_worldtree_leaves");
    public static final DeferredBlock<Block> DYEDREAMQUARTZ_ORE = SIMPLE_BLOCKS.get("dyedreamquartz_ore");
    public static final DeferredBlock<Block> DYEDREAMDUST_ORE = SIMPLE_BLOCKS.get("dyedreamdust_ore");
    public static final DeferredBlock<Block> AMBER_CANDY_ORE = SIMPLE_BLOCKS.get("amber_candy_ore");
    public static final DeferredBlock<Block> TITANIUM_ORE = SIMPLE_BLOCKS.get("titanium_ore");
    public static final DeferredBlock<Block> WINDRUNNER_CRYSTAL_ORE = SIMPLE_BLOCKS.get("windrunner_crystal_ore");
    public static final DeferredBlock<Block> CONGEAL_WIND_ORE = SIMPLE_BLOCKS.get("congeal_wind_ore");
    public static final DeferredBlock<Block> CARVE_DYEDREAM_GLASS = SIMPLE_BLOCKS.get("carve_dyedream_glass");
    public static final DeferredBlock<Block> GOLD_CARVE_DYEDREAM_GLASS = SIMPLE_BLOCKS.get("gold_carve_dyedream_glass");
    public static final DeferredBlock<Block> POLISHED_CALCITE = SIMPLE_BLOCKS.get("polished_calcite");
    public static final DeferredBlock<Block> CALCITE_TILES = SIMPLE_BLOCKS.get("calcite_tiles");
    public static final DeferredBlock<Block> DYEDREAM_DEEPSTONE = SIMPLE_BLOCKS.get("dyedream_deepstone");
    public static final DeferredBlock<Block> DYEDREAM_SANDSTONE = SIMPLE_BLOCKS.get("dyedream_sandstone");

    // ==================== 特殊方块（保持手动注册） ====================

    public static final DeferredBlock<DyedreamLeavesBlock> DYEDREAM_LEAVES = PDBlocks.BLOCKS.registerBlock("dyedream_leaves",
            DyedreamLeavesBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LEAVES));
    public static final DeferredBlock<DyedreamGrassBlock> DYEDREAM_GRASS = PDBlocks.BLOCKS.registerBlock("dyedream_grass",
            DyedreamGrassBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.GRASS_BLOCK));

    public static final DeferredBlock<DyedreamLogBlock> DYEDREAM_LOG = PDBlocks.BLOCKS.registerBlock("dyedream_log",
            DyedreamLogBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LOG));
    public static final DeferredBlock<DyedreamLogBlock> DYEDREAM_WOOD = PDBlocks.BLOCKS.registerBlock("dyedream_wood",
            DyedreamLogBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LOG));
    public static final DeferredBlock<RotatedPillarBlock> PILLAR_DYEDREAMQUARTZ_BLOCK = PDBlocks.BLOCKS.registerBlock("pillar_dyedreamquartz_block",
            RotatedPillarBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).requiresCorrectToolForDrops());

    // ==================== 建筑变体族（API 批量注册） ====================

    private static final VariantSetResult PLANKS_VARIANTS = BlockAPI.createVariantSet("dyedream_planks", () -> DYEDREAM_PLANKS.get())
            .mineable("axe")
            .withStairs()
            .withSlab()
            .withFence()
            .withFenceGate(WoodType.OAK)
            .withDoor(BlockSetType.OAK)
            .withTrapdoor(BlockSetType.OAK)
            .withPressurePlate(BlockSetType.OAK)
            .withButton(BlockSetType.OAK, 30)
            .build();

    public static final DeferredBlock<StairBlock> DYEDREAM_PLANKS_STAIRS = PLANKS_VARIANTS.stairs();
    public static final DeferredBlock<SlabBlock> DYEDREAM_PLANKS_SLAB = PLANKS_VARIANTS.slab();
    public static final DeferredBlock<FenceBlock> DYEDREAM_PLANKS_FENCE = PLANKS_VARIANTS.fence();
    public static final DeferredBlock<FenceGateBlock> DYEDREAM_PLANKS_FENCEGATE = PLANKS_VARIANTS.fenceGate();
    public static final DeferredBlock<DoorBlock> DYEDREAM_PLANKS_DOOR = PLANKS_VARIANTS.door();
    public static final DeferredBlock<TrapDoorBlock> DYEDREAM_PLANKS_TRAPDOOR = PLANKS_VARIANTS.trapdoor();
    public static final DeferredBlock<PressurePlateBlock> DYEDREAM_PLANKS_PRESSURE_PLATE = PLANKS_VARIANTS.pressurePlate();
    public static final DeferredBlock<ButtonBlock> DYEDREAM_PLANKS_BUTTON = PLANKS_VARIANTS.button();

    // ==================== 其他变体（手动注册） ====================

    public static final DeferredBlock<StairBlock> DYEDREAM_BUD_STAIRS = PDBlocks.BLOCKS.registerBlock("dyedream_bud_stairs",
            p -> new StairBlock(DYEDREAM_BUD_BLOCK.get().defaultBlockState(), p),
            BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_STAIRS));
    public static final DeferredBlock<StairBlock> DYEDREAMQUARTZ_BLOCK_STAIRS = PDBlocks.BLOCKS.registerBlock("dyedreamquartz_block_stairs",
            p -> new StairBlock(DYEDREAMQUARTZ_BLOCK.get().defaultBlockState(), p),
            BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_STAIRS));

    public static final DeferredBlock<SlabBlock> DYEDREAM_BUD_SLAB = PDBlocks.BLOCKS.registerBlock("dyedream_bud_slab",
            SlabBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_SLAB));
    public static final DeferredBlock<SlabBlock> DYEDREAMQUARTZ_BLOCK_SLAB = PDBlocks.BLOCKS.registerBlock("dyedreamquartz_block_slab",
            SlabBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_SLAB));

    public static final DeferredBlock<WallBlock> DYEDREAM_BUD_WALL = PDBlocks.BLOCKS.registerBlock("dyedream_bud_wall",
            WallBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.COBBLESTONE_WALL));
    public static final DeferredBlock<WallBlock> DYEDREAMQUARTZ_BLOCK_WALL = PDBlocks.BLOCKS.registerBlock("dyedreamquartz_block_wall",
            WallBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.COBBLESTONE_WALL));

    // ==================== 钙华变体系列（缺失方块补全） ====================

    public static final DeferredBlock<StairBlock> CALCITE_TILES_STAIRS = PDBlocks.BLOCKS.registerBlock("calcite_tiles_stairs",
            p -> new StairBlock(POLISHED_CALCITE.get().defaultBlockState(), p),
            BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_STAIRS));
    public static final DeferredBlock<SlabBlock> CALCITE_TILES_SLAB = PDBlocks.BLOCKS.registerBlock("calcite_tiles_slab",
            SlabBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_SLAB));
    public static final DeferredBlock<SlabBlock> POLISHED_CALCITE_SLAB = PDBlocks.BLOCKS.registerBlock("polished_calcite_slab",
            SlabBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_SLAB));
    public static final DeferredBlock<WallBlock> POLISHED_CALCITE_WALL = PDBlocks.BLOCKS.registerBlock("polished_calcite_wall",
            WallBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.COBBLESTONE_WALL));
    public static final DeferredBlock<WallBlock> CALCITE_TILES_WALL = PDBlocks.BLOCKS.registerBlock("calcite_tiles_wall",
            WallBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.COBBLESTONE_WALL));
    public static final DeferredBlock<StairBlock> POLISHED_CALCITE_STAIRS = PDBlocks.BLOCKS.registerBlock("polished_calcite_stairs",
            p -> new StairBlock(POLISHED_CALCITE.get().defaultBlockState(), p),
            BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_STAIRS));


    // ==================== 玻璃面板和灯笼 ====================

    public static final DeferredBlock<IronBarsBlock> DYEDREAM_GLASSPANE = PDBlocks.BLOCKS.registerBlock("dyedream_glasspane",
            IronBarsBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS_PANE));
    public static final DeferredBlock<IronBarsBlock> CARVE_DYEDREAM_GLASSPANE = PDBlocks.BLOCKS.registerBlock("carve_dyedream_glasspane",
            IronBarsBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS_PANE));
    public static final DeferredBlock<IronBarsBlock> GOLD_CARVE_DYEDREAM_GLASSPANE = PDBlocks.BLOCKS.registerBlock("gold_carve_dyedream_glasspane",
            IronBarsBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS_PANE));

    public static final DeferredBlock<DyedreamLarternBlock> DYEDREAM_LARTERN = PDBlocks.BLOCKS.registerBlock("dyedream_lartern",
            DyedreamLarternBlock::new, DyedreamLarternBlock.larternProps());
}
