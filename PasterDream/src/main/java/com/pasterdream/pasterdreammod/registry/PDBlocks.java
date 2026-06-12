package com.pasterdream.pasterdreammod.registry;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.api.block.BlockAPI;
import com.pasterdream.pasterdreammod.api.block.BlockConfig;
import com.pasterdream.pasterdreammod.api.block.builder.VariantSetResult;
import com.pasterdream.pasterdreammod.block.DreamAccumulatorBlock;
import com.pasterdream.pasterdreammod.block.DreamTrainStructureBlock;
import com.pasterdream.pasterdreammod.block.DyedreamCrackBlock;
import com.pasterdream.pasterdreammod.block.DyedreamDeskBlock;
import com.pasterdream.pasterdreammod.block.DyedreamLilyPadBlock;
import com.pasterdream.pasterdreammod.block.DyedreamLotusBlock;
import com.pasterdream.pasterdreammod.block.DyedreamPlanksPaneBlock;
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
import com.pasterdream.pasterdreammod.block.ThickCloudBlock;
import com.pasterdream.pasterdreammod.block.Pebble0Block;
import com.pasterdream.pasterdreammod.block.ShadowLight0Block;
import com.pasterdream.pasterdreammod.block.Vine0Block;
import com.pasterdream.pasterdreammod.block.GoldenrodBlock;
import com.pasterdream.pasterdreammod.block.Crop0ABlock;
import com.pasterdream.pasterdreammod.block.Crop1ABlock;
import com.pasterdream.pasterdreammod.block.Crop3ABlock;
import com.pasterdream.pasterdreammod.block.Crop2ABlock;
import com.pasterdream.pasterdreammod.block.Crop4ABlock;
import com.pasterdream.pasterdreammod.block.MeltdreamLiquidBlock;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

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

    // ==================== 自定义方块（保持手动注册） ====================

    public static final DeferredBlock<DreamAccumulatorBlock> DREAM_ACCUMULATOR = BLOCKS.register("dream_accumulator",
            () -> new DreamAccumulatorBlock(BlockBehaviour.Properties.of()
                    .sound(SoundType.CALCITE)
                    .strength(1.0f)
                    .requiresCorrectToolForDrops()
                    .noOcclusion()));

    public static final DeferredBlock<DyedreamDeskBlock> DYEDREAM_DESK = BLOCKS.register("dyedream_desk",
            () -> new DyedreamDeskBlock(BlockBehaviour.Properties.of()
                    .sound(SoundType.WOOD)
                    .strength(1.0f)
                    .requiresCorrectToolForDrops()
                    .noOcclusion()));

    /**
     * 梦境列车结构方块 (dream_train_structure)
     * 装饰性方块，右键点击时发送列车到站提示消息
     */
    public static final DeferredBlock<DreamTrainStructureBlock> DREAM_TRAIN_STRUCTURE = BLOCKS.register("dream_train_structure",
            () -> new DreamTrainStructureBlock(BlockBehaviour.Properties.of()
                    .sound(SoundType.METAL)
                    .strength(2.0f)
                    .requiresCorrectToolForDrops()
                    .noOcclusion()));

    public static final DeferredBlock<LifeCrystalBlock> LIFE_CRYSTAL = BLOCKS.register("life_crystal",
            () -> new LifeCrystalBlock(BlockBehaviour.Properties.of()
                    .sound(SoundType.GLASS)
                    .strength(1.0f)
                    .lightLevel(state -> 12)
                    .noOcclusion()));

    public static final DeferredBlock<ShadowChestBlock> SHADOW_CHEST = BLOCKS.register("shadow_chest",
            () -> new ShadowChestBlock(BlockBehaviour.Properties.of()
                    .sound(SoundType.DEEPSLATE_TILES)
                    .strength(1.0f, 0.5f)
                    .noOcclusion()));

    // ==================== 寻梦者的永恒书卷 ====================

    /**
     * 寻梦者的永恒书卷 (the_endless_book_of_dream_seekers)
     * GeckoLib 3D 书籍模型，1 格库存，支持 GUI 交互
     */
    public static final DeferredBlock<TheEndlessBookOfDreamSeekersBlock> THE_ENDLESS_BOOK_OF_DREAM_SEEKERS = BLOCKS.register("the_endless_book_of_dream_seekers",
            () -> new TheEndlessBookOfDreamSeekersBlock(BlockBehaviour.Properties.of()
                    .sound(SoundType.WOOD)
                    .strength(1.0f)
                    .requiresCorrectToolForDrops()
                    .noOcclusion()
                    .lightLevel(state -> 8)));

    // ==================== 梦境炼药锅（Dream Factory/Cauldron） ====================

    /**
     * 梦境炼药锅 (dream_cauldron)
     * GeckoLib 3D 模型，支持方向放置、GUI 交互、水浸属性
     * 3 输入槽 + 1 输出槽，右键打开炼药界面
     */
    public static final DeferredBlock<DreamCauldronBlock> DREAM_CAULDRON = BLOCKS.register("dream_cauldron",
            () -> new DreamCauldronBlock(BlockBehaviour.Properties.of()
                    .sound(SoundType.STONE)
                    .strength(1.0f, 0.5f)
                    .noOcclusion()));

    // ==================== 融梦水晶箱（GeckoLib 动画） ====================

    /**
     * 融梦水晶箱（关闭状态）- 使用 GeckoLib 动画的三级随机宝藏箱
     * animation 属性 0-3：闲置/普通/稀有/传说
     */
    public static final DeferredBlock<MeltdreamChestBlock> MELTDREAM_CHEST = BLOCKS.register("meltdream_chest",
            () -> new MeltdreamChestBlock(BlockBehaviour.Properties.of()
                    .sound(SoundType.GLASS)
                    .strength(1.0f, 0.5f)
                    .noOcclusion()
                    .lightLevel(state -> state.getValue(MeltdreamChestBlock.ANIMATION) > 0 ? 8 : 0)));

    /**
     * 融梦水晶箱（打开状态）- 无动画，右键可打开 GUI
     */
    public static final DeferredBlock<MeltdreamChestOpenBlock> MELTDREAM_CHEST_OPEN = BLOCKS.register("meltdream_chest_open",
            () -> new MeltdreamChestOpenBlock(BlockBehaviour.Properties.of()
                    .sound(SoundType.GLASS)
                    .strength(1.0f, 0.5f)
                    .noOcclusion()
                    .lightLevel(state -> 8)));

    // ==================== 简单换皮方块（API 批量注册） ====================

    private static final Map<String, DeferredBlock<Block>> SIMPLE_BLOCKS = BlockAPI.registerSimpleBlocks()
            .add("dyedream_dirt", Blocks.DIRT, BlockConfig.of()
                    .mineable("shovel").model("cube_all").tex("all", "pasterdream:block/dyedream_dirt"))
            .add("dyedream_sand", Blocks.SAND, BlockConfig.of()
                    .mineable("shovel").model("cube_all").tex("all", "pasterdream:block/dyedream_sand"))
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
                    BlockConfig.of().mineable("pickaxe").model("cube_all").tex("all", "pasterdream:block/dyedream_block"))
            .addCustom("dyedreamquartz_block",
                    BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).requiresCorrectToolForDrops(),
                    BlockConfig.of().mineable("pickaxe").model("cube_all").tex("all", "pasterdream:block/dyedreamquartz"))
            .addCustom("smooth_dyedreamquartz_block",
                    BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).requiresCorrectToolForDrops(),
                    BlockConfig.of().mineable("pickaxe").model("cube_all").tex("all", "pasterdream:block/dyedreamquartz"))
            .addCustom("bricks_dyedreamquartz_block",
                    BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).requiresCorrectToolForDrops(),
                    BlockConfig.of().mineable("pickaxe").model("cube_all").tex("all", "pasterdream:block/dyedreamquartz_brick"))
            .addCustom("meltdream_crystal_lamp",
                    BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).lightLevel(s -> 15).requiresCorrectToolForDrops(),
                    BlockConfig.of().mineable("pickaxe").model("cube_all").tex("all", "pasterdream:block/meltdream_crystal_lamp").renderType("translucent"))
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
            .add("dyedreamquartz_ore", Blocks.IRON_ORE, BlockConfig.of()
                    .mineable("pickaxe").model("cube_all").tex("all", "pasterdream:block/dyedreamquartz_ore"))
            .add("dyedreamdust_ore", Blocks.IRON_ORE, BlockConfig.of()
                    .mineable("pickaxe").model("cube_all").tex("all", "pasterdream:block/dyedreamdust_ore"))
            .add("amber_candy_ore", Blocks.IRON_ORE, BlockConfig.of()
                    .mineable("pickaxe").model("cube_all").tex("all", "pasterdream:block/amber_candy_ore"))
            .add("titanium_ore", Blocks.IRON_ORE, BlockConfig.of()
                    .mineable("pickaxe").model("cube_all").tex("all", "pasterdream:block/titanium_ore"))
            .add("windrunner_crystal_ore", Blocks.IRON_ORE, BlockConfig.of()
                    .mineable("pickaxe").model("cube_all").tex("all", "pasterdream:block/windrunner_crystal_ore"))
            .add("congeal_wind_ore", Blocks.IRON_ORE, BlockConfig.of()
                    .mineable("pickaxe").model("cube_all").tex("all", "pasterdream:block/congeal_wind_ore"))
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
    public static final DeferredBlock<Block> MELTDREAM_CRYSTAL_LAMP = SIMPLE_BLOCKS.get("meltdream_crystal_lamp");
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

    // ==================== 特殊方块（保持手动注册） ====================

    public static final DeferredBlock<DyedreamLeavesBlock> DYEDREAM_LEAVES = BLOCKS.registerBlock("dyedream_leaves",
            DyedreamLeavesBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LEAVES));
    public static final DeferredBlock<DyedreamGrassBlock> DYEDREAM_GRASS = BLOCKS.registerBlock("dyedream_grass",
            DyedreamGrassBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.GRASS_BLOCK));

    public static final DeferredBlock<DyedreamLogBlock> DYEDREAM_LOG = BLOCKS.registerBlock("dyedream_log",
            DyedreamLogBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LOG));
    public static final DeferredBlock<DyedreamLogBlock> DYEDREAM_WOOD = BLOCKS.registerBlock("dyedream_wood",
            DyedreamLogBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LOG));
    public static final DeferredBlock<RotatedPillarBlock> PILLAR_DYEDREAMQUARTZ_BLOCK = BLOCKS.registerBlock("pillar_dyedreamquartz_block",
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

    public static final DeferredBlock<StairBlock> DYEDREAM_BUD_STAIRS = BLOCKS.registerBlock("dyedream_bud_stairs",
            p -> new StairBlock(DYEDREAM_BUD_BLOCK.get().defaultBlockState(), p),
            BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_STAIRS));
    public static final DeferredBlock<StairBlock> DYEDREAMQUARTZ_BLOCK_STAIRS = BLOCKS.registerBlock("dyedreamquartz_block_stairs",
            p -> new StairBlock(DYEDREAMQUARTZ_BLOCK.get().defaultBlockState(), p),
            BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_STAIRS));

    public static final DeferredBlock<SlabBlock> DYEDREAM_BUD_SLAB = BLOCKS.registerBlock("dyedream_bud_slab",
            SlabBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_SLAB));
    public static final DeferredBlock<SlabBlock> DYEDREAMQUARTZ_BLOCK_SLAB = BLOCKS.registerBlock("dyedreamquartz_block_slab",
            SlabBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_SLAB));

    public static final DeferredBlock<WallBlock> DYEDREAM_BUD_WALL = BLOCKS.registerBlock("dyedream_bud_wall",
            WallBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.COBBLESTONE_WALL));
    public static final DeferredBlock<WallBlock> DYEDREAMQUARTZ_BLOCK_WALL = BLOCKS.registerBlock("dyedreamquartz_block_wall",
            WallBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.COBBLESTONE_WALL));

    // ==================== 钙华变体系列（缺失方块补全） ====================

    public static final DeferredBlock<StairBlock> CALCITE_TILES_STAIRS = BLOCKS.registerBlock("calcite_tiles_stairs",
            p -> new StairBlock(POLISHED_CALCITE.get().defaultBlockState(), p),
            BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_STAIRS));
    public static final DeferredBlock<SlabBlock> CALCITE_TILES_SLAB = BLOCKS.registerBlock("calcite_tiles_slab",
            SlabBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_SLAB));
    public static final DeferredBlock<SlabBlock> POLISHED_CALCITE_SLAB = BLOCKS.registerBlock("polished_calcite_slab",
            SlabBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_SLAB));
    public static final DeferredBlock<WallBlock> POLISHED_CALCITE_WALL = BLOCKS.registerBlock("polished_calcite_wall",
            WallBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.COBBLESTONE_WALL));
    public static final DeferredBlock<WallBlock> CALCITE_TILES_WALL = BLOCKS.registerBlock("calcite_tiles_wall",
            WallBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.COBBLESTONE_WALL));
    public static final DeferredBlock<StairBlock> POLISHED_CALCITE_STAIRS = BLOCKS.registerBlock("polished_calcite_stairs",
            p -> new StairBlock(POLISHED_CALCITE.get().defaultBlockState(), p),
            BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_STAIRS));

    // ==================== 手动注册方块的 BlockConfig 初始化 ====================
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
    }

    // ==================== 玻璃面板和灯笼 ====================

    public static final DeferredBlock<IronBarsBlock> DYEDREAM_GLASSPANE = BLOCKS.registerBlock("dyedream_glasspane",
            IronBarsBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS_PANE));
    public static final DeferredBlock<IronBarsBlock> CARVE_DYEDREAM_GLASSPANE = BLOCKS.registerBlock("carve_dyedream_glasspane",
            IronBarsBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS_PANE));
    public static final DeferredBlock<IronBarsBlock> GOLD_CARVE_DYEDREAM_GLASSPANE = BLOCKS.registerBlock("gold_carve_dyedream_glasspane",
            IronBarsBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS_PANE));

    public static final DeferredBlock<LanternBlock> DYEDREAM_LARTERN = BLOCKS.registerBlock("dyedream_lartern",
            LanternBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.LANTERN).lightLevel(s -> 14));

    // ==================== 自定义模型方块 ====================

    /**
     * 木板屏风 (dyedream_planks_pane)
     * 继承 IronBarsBlock，类似玻璃板的连接逻辑，木质纹理
     */
    public static final DeferredBlock<DyedreamPlanksPaneBlock> DYEDREAM_PLANKS_PANE = BLOCKS.registerBlock("dyedream_planks_pane",
            p -> new DyedreamPlanksPaneBlock());

    /**
     * 粉丁菇 0~3 号变种 (pinkagaric_0/1/2/3)
     * 粉色蘑菇，不同变种有不同的发光等级
     */
    public static final DeferredBlock<Block> PINKAGARIC_0 = BLOCKS.registerBlock("pinkagaric_0",
            p -> new PinkagaricBlock(p, () -> 0), pinkagaricProps());
    public static final DeferredBlock<Block> PINKAGARIC_1 = BLOCKS.registerBlock("pinkagaric_1",
            p -> new PinkagaricBlock(p, () -> 8), pinkagaricProps());
    public static final DeferredBlock<Block> PINKAGARIC_2 = BLOCKS.registerBlock("pinkagaric_2",
            p -> new PinkagaricBlock(p.noOcclusion(), () -> 0), pinkagaricProps());
    public static final DeferredBlock<Block> PINKAGARIC_3 = BLOCKS.registerBlock("pinkagaric_3",
            p -> new PinkagaricBlock(p.lightLevel(s -> 15), () -> 15), pinkagaricProps());

    private static BlockBehaviour.Properties pinkagaricProps() {
        return BlockBehaviour.Properties.of()
                .ignitedByLava()
                .instrument(NoteBlockInstrument.BASS)
                .sound(SoundType.WART_BLOCK)
                .strength(0.3f, 0.1f)
                .jumpFactor(1.2f);
    }

    /**
     * 花蕾 0~2 号变种 (dyedream_bud_0/1/2)
     * SimpleWaterloggedBlock，AXIS 轴向旋转，发光等级10
     */
    public static final DeferredBlock<DyedreamBudBlock> DYEDREAM_BUD_0 = BLOCKS.registerBlock("dyedream_bud_0",
            p -> new DyedreamBudBlock(p, 0), budProps());
    public static final DeferredBlock<DyedreamBudBlock> DYEDREAM_BUD_1 = BLOCKS.registerBlock("dyedream_bud_1",
            p -> new DyedreamBudBlock(p, 1), budProps());
    public static final DeferredBlock<DyedreamBudBlock> DYEDREAM_BUD_2 = BLOCKS.registerBlock("dyedream_bud_2",
            p -> new DyedreamBudBlock(p, 2), budProps());

    private static BlockBehaviour.Properties budProps() {
        return BlockBehaviour.Properties.of()
                .sound(SoundType.AMETHYST_CLUSTER)
                .strength(1f, 0f)
                .lightLevel(s -> 6)
                .requiresCorrectToolForDrops()
                .noOcclusion()
                .hasPostProcess((bs, br, bp) -> true)
                .emissiveRendering((bs, br, bp) -> true)
                .isRedstoneConductor((bs, br, bp) -> false);
    }

    /**
     * 冰蕾 (ice_bud_0)
     * SimpleWaterloggedBlock，FACING 六面朝向，发光等级9
     */
    public static final DeferredBlock<IceBudBlock> ICE_BUD_0 = BLOCKS.registerBlock("ice_bud_0",
            IceBudBlock::new, BlockBehaviour.Properties.of()
                    .sound(SoundType.AMETHYST_CLUSTER)
                    .strength(1f, 0f)
                    .lightLevel(s -> 5)
                    .requiresCorrectToolForDrops()
                    .noOcclusion()
                    .hasPostProcess((bs, br, bp) -> true)
                    .emissiveRendering((bs, br, bp) -> true)
                    .isRedstoneConductor((bs, br, bp) -> false));

    /**
     * 染梦荷叶 (dyedream_lily_pad)
     * 水面植物，继承 FlowerBlock，只能放在水上
     */
    public static final DeferredBlock<DyedreamLilyPadBlock> DYEDREAM_LILY_PAD = BLOCKS.registerBlock("dyedream_lily_pad",
            p -> new DyedreamLilyPadBlock());

    /**
     * 染梦莲花 (dyedream_lotus)
     * 水面植物，继承 FlowerBlock，只能放在水上
     */
    public static final DeferredBlock<DyedreamLotusBlock> DYEDREAM_LOTUS = BLOCKS.registerBlock("dyedream_lotus",
            p -> new DyedreamLotusBlock());

    /**
     * 染梦海草 (dyedream_seagrass)
     * SimpleWaterloggedBlock，水下植物，XZ 偏移
     */
    public static final DeferredBlock<DyedreamSeagrassBlock> DYEDREAM_SEAGRASS = BLOCKS.registerBlock("dyedream_seagrass",
            p -> new DyedreamSeagrassBlock());

    /**
     * 染梦树苗 (dyedream_sapling)
     * 简化版，继承 FlowerBlock，无 EntityBlock
     */
    public static final DeferredBlock<DyedreamSaplingBlock> DYEDREAM_SAPLING = BLOCKS.registerBlock("dyedream_sapling",
            p -> new DyedreamSaplingBlock());

    /**
     * 染梦裂纹 (dyedream_crack)
     * 简化版，保留 FACING+WATERLOGGED 属性，发光等级14，无 EntityBlock
     */
    public static final DeferredBlock<DyedreamCrackBlock> DYEDREAM_CRACK = BLOCKS.registerBlock("dyedream_crack",
            p -> new DyedreamCrackBlock());

    // ==================== 云朵方块 ====================
    public static final DeferredBlock<CloudBlock> CLOUD = BLOCKS.registerBlock("cloud", p -> new CloudBlock());
    public static final DeferredBlock<DarkCloudBlock> DARK_CLOUD = BLOCKS.registerBlock("dark_cloud", p -> new DarkCloudBlock());
    public static final DeferredBlock<ThickCloudBlock> THICK_CLOUD = BLOCKS.registerBlock("thick_cloud", p -> new ThickCloudBlock());

    // ==================== 染梦花草（移植自原版模组） ====================

    private static BlockBehaviour.Properties flowerProps() {
        return BlockBehaviour.Properties.ofFullCopy(Blocks.DANDELION);
    }

    private static BlockBehaviour.Properties doublePlantProps() {
        return BlockBehaviour.Properties.ofFullCopy(Blocks.SUNFLOWER);
    }

    // ========== API 批量注册：花（单格 + 双层） ==========

    private static final Map<String, DeferredBlock<Block>> FLOWERS_SINGLE = BlockAPI.batchRegister("flower")
            .indexList(1, 2, 3, 5, 6, 8, 9, 13, 14, 15, 16, 17)
            .factory((index, props) -> new DyedreamFlowerBlock(MobEffects.HUNGER, 100, props))
            .withProperties(flowerProps())
            .build();

    private static final Map<String, DeferredBlock<Block>> FLOWERS_DOUBLE = BlockAPI.batchRegister("flower")
            .indexList(7, 10, 11, 12, 18)
            .factory((index, props) -> new DyedreamDoublePlantBlock())
            .withProperties(doublePlantProps())
            .build();

    public static final DeferredBlock<Block> FLOWER_1 = FLOWERS_SINGLE.get("flower_1");
    public static final DeferredBlock<Block> FLOWER_2 = FLOWERS_SINGLE.get("flower_2");
    public static final DeferredBlock<Block> FLOWER_3 = FLOWERS_SINGLE.get("flower_3");
    public static final DeferredBlock<Block> FLOWER_5 = FLOWERS_SINGLE.get("flower_5");
    public static final DeferredBlock<Block> FLOWER_6 = FLOWERS_SINGLE.get("flower_6");
    public static final DeferredBlock<Block> FLOWER_7 = FLOWERS_DOUBLE.get("flower_7");
    public static final DeferredBlock<Block> FLOWER_8 = FLOWERS_SINGLE.get("flower_8");
    public static final DeferredBlock<Block> FLOWER_9 = FLOWERS_SINGLE.get("flower_9");
    public static final DeferredBlock<Block> FLOWER_10 = FLOWERS_DOUBLE.get("flower_10");
    public static final DeferredBlock<Block> FLOWER_11 = FLOWERS_DOUBLE.get("flower_11");
    public static final DeferredBlock<Block> FLOWER_12 = FLOWERS_DOUBLE.get("flower_12");
    public static final DeferredBlock<Block> FLOWER_13 = FLOWERS_SINGLE.get("flower_13");
    public static final DeferredBlock<Block> FLOWER_14 = FLOWERS_SINGLE.get("flower_14");
    public static final DeferredBlock<Block> FLOWER_15 = FLOWERS_SINGLE.get("flower_15");
    public static final DeferredBlock<Block> FLOWER_16 = FLOWERS_SINGLE.get("flower_16");
    public static final DeferredBlock<Block> FLOWER_17 = FLOWERS_SINGLE.get("flower_17");
    public static final DeferredBlock<Block> FLOWER_18 = FLOWERS_DOUBLE.get("flower_18");

    // ========== API 批量注册：草（单格 + 双层） ==========

    private static final Map<String, DeferredBlock<Block>> GRASSES_SINGLE = BlockAPI.batchRegister("grass")
            .indexList(1, 2, 3, 5, 6, 7, 8, 9, 11, 12, 13, 14)
            .factory((index, props) -> new DyedreamFlowerBlock(MobEffects.MOVEMENT_SLOWDOWN, 100, props))
            .withProperties(flowerProps())
            .build();

    private static final Map<String, DeferredBlock<Block>> GRASSES_DOUBLE = BlockAPI.batchRegister("grass")
            .indexList(4, 10, 15)
            .factory((index, props) -> new DyedreamDoublePlantBlock())
            .withProperties(doublePlantProps())
            .build();

    public static final DeferredBlock<Block> GRASS_1 = GRASSES_SINGLE.get("grass_1");
    public static final DeferredBlock<Block> GRASS_2 = GRASSES_SINGLE.get("grass_2");
    public static final DeferredBlock<Block> GRASS_3 = GRASSES_SINGLE.get("grass_3");
    public static final DeferredBlock<Block> GRASS_4 = GRASSES_DOUBLE.get("grass_4");
    public static final DeferredBlock<Block> GRASS_5 = GRASSES_SINGLE.get("grass_5");
    public static final DeferredBlock<Block> GRASS_6 = GRASSES_SINGLE.get("grass_6");
    public static final DeferredBlock<Block> GRASS_7 = GRASSES_SINGLE.get("grass_7");
    public static final DeferredBlock<Block> GRASS_8 = GRASSES_SINGLE.get("grass_8");
    public static final DeferredBlock<Block> GRASS_9 = GRASSES_SINGLE.get("grass_9");
    public static final DeferredBlock<Block> GRASS_10 = GRASSES_DOUBLE.get("grass_10");
    public static final DeferredBlock<Block> GRASS_11 = GRASSES_SINGLE.get("grass_11");
    public static final DeferredBlock<Block> GRASS_12 = GRASSES_SINGLE.get("grass_12");
    public static final DeferredBlock<Block> GRASS_13 = GRASSES_SINGLE.get("grass_13");
    public static final DeferredBlock<Block> GRASS_14 = GRASSES_SINGLE.get("grass_14");
    public static final DeferredBlock<Block> GRASS_15 = GRASSES_DOUBLE.get("grass_15");

    // ==================== Phase 1: 移植物块材料 ====================

    // ========== 存储方块 ==========

    public static final DeferredBlock<Block> TITANIUM_BLOCK = BLOCKS.registerBlock("titanium_block", Block::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK));

    public static final DeferredBlock<Block> RAW_TITANIUM_BLOCK = BLOCKS.registerBlock("raw_titanium_block", Block::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.RAW_IRON_BLOCK));

    public static final DeferredBlock<Block> MOLTENGOLD_BLOCK = BLOCKS.registerBlock("moltengold_block", Block::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).lightLevel(s -> 15));

    public static final DeferredBlock<Block> BLACKMETAL_BLOCK = BLOCKS.registerBlock("blackmetal_block", Block::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK));

    public static final DeferredBlock<Block> CHARGED_AMETHYST_BLOCK = BLOCKS.registerBlock("charged_amethyst_block", Block::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.AMETHYST_BLOCK));

    public static final DeferredBlock<Block> WIND_IRON_BLOCK = BLOCKS.registerBlock("wind_iron_block", Block::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK));

    // ========== 矿石方块 ==========

    public static final DeferredBlock<Block> DEEPSLATE_TITANIUM_ORE = BLOCKS.registerBlock("deepslate_titanium_ore", Block::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE_IRON_ORE));

    public static final DeferredBlock<Block> MOLTENGOLD_ORE = BLOCKS.registerBlock("moltengold_ore", Block::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.NETHER_GOLD_ORE));

    public static final DeferredBlock<Block> SOUL_ORE = BLOCKS.registerBlock("soul_ore", Block::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.SOUL_SAND).strength(3f, 3f).requiresCorrectToolForDrops());

    // ========== 装饰/植物方块（自定义类） ==========

    public static final DeferredBlock<Pebble0Block> PEBBLE_0 = BLOCKS.registerBlock("pebble_0", Pebble0Block::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion()
                    .isRedstoneConductor((bs, br, bp) -> false));

    public static final DeferredBlock<ShadowLight0Block> SHADOW_LIGHT_0 = BLOCKS.registerBlock("shadow_light_0", ShadowLight0Block::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS).lightLevel(s -> 15)
                    .requiresCorrectToolForDrops()
                    .hasPostProcess((bs, br, bp) -> true)
                    .emissiveRendering((bs, br, bp) -> true));

    public static final DeferredBlock<Vine0Block> VINE_0 = BLOCKS.registerBlock("vine_0", Vine0Block::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.VINE).noCollission().lightLevel(s -> 14));

    public static final DeferredBlock<GoldenrodBlock> GOLDENROD = BLOCKS.registerBlock("goldenrod",
            GoldenrodBlock::new, flowerProps());

    public static final DeferredBlock<Crop0ABlock> CROP_0A = BLOCKS.registerBlock("crop_0a",
            Crop0ABlock::new, flowerProps());

    public static final DeferredBlock<Crop1ABlock> CROP_1A = BLOCKS.registerBlock("crop_1a",
            Crop1ABlock::new, flowerProps());

    public static final DeferredBlock<Crop3ABlock> CROP_3A = BLOCKS.registerBlock("crop_3a",
            Crop3ABlock::new, flowerProps());

    public static final DeferredBlock<Crop4ABlock> CROP_4A = BLOCKS.registerBlock("crop_4a",
            Crop4ABlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.SWEET_BERRY_BUSH).noCollission());

    // ========== 作物系列补全 ==========

    public static final DeferredBlock<Crop2ABlock> CROP_2A = BLOCKS.registerBlock("crop_2a",
            Crop2ABlock::new, flowerProps());

    // ==================== 流体方块 ====================

    /**
     * 融梦涌泉流体方块 (meltdream_liquid)
     * 使用 MeltdreamLiquidBlock 自定义实现，含粒子效果和发光渲染
     */
    public static final DeferredBlock<MeltdreamLiquidBlock> MELTDREAM_LIQUID = BLOCKS.registerBlock("meltdream_liquid",
            p -> new MeltdreamLiquidBlock());

}
