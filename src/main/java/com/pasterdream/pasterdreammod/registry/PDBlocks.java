package com.pasterdream.pasterdreammod.registry;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.api.block.BlockAPI;
import com.pasterdream.pasterdreammod.api.block.BlockConfig;
import com.pasterdream.pasterdreammod.api.block.builder.VariantSetResult;
import com.pasterdream.pasterdreammod.block.DreamAccumulatorBlock;
import com.pasterdream.pasterdreammod.block.DyedreamCrackBlock;
import com.pasterdream.pasterdreammod.block.DyedreamDeskBlock;
import com.pasterdream.pasterdreammod.block.DyedreamLilyPadBlock;
import com.pasterdream.pasterdreammod.block.DyedreamLotusBlock;
import com.pasterdream.pasterdreammod.block.DyedreamPlanksPaneBlock;
import com.pasterdream.pasterdreammod.block.DyedreamSaplingBlock;
import com.pasterdream.pasterdreammod.block.DyedreamSeagrassBlock;
import com.pasterdream.pasterdreammod.block.CloudBlock;
import com.pasterdream.pasterdreammod.block.DarkCloudBlock;
import com.pasterdream.pasterdreammod.block.DyedreamBudBlock;
import com.pasterdream.pasterdreammod.block.DyedreamDoublePlantBlock;
import com.pasterdream.pasterdreammod.block.DyedreamFlowerBlock;
import com.pasterdream.pasterdreammod.block.DyedreamGrassBlock;
import com.pasterdream.pasterdreammod.block.DyedreamLogBlock;
import com.pasterdream.pasterdreammod.block.DyedreamLeavesBlock;
import com.pasterdream.pasterdreammod.block.IceBudBlock;
import com.pasterdream.pasterdreammod.block.LifeCrystalBlock;
import com.pasterdream.pasterdreammod.block.PinkagaricBlock;
import com.pasterdream.pasterdreammod.block.ShadowChestBlock;
import com.pasterdream.pasterdreammod.block.ThickCloudBlock;
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

    // ==================== 简单换皮方块（API 批量注册） ====================

    private static final Map<String, DeferredBlock<Block>> SIMPLE_BLOCKS = BlockAPI.registerSimpleBlocks()
            .add("dyedream_dirt", Blocks.DIRT, BlockConfig.of()
                    .mineable("shovel").model("cube_all").tex("all", "pasterdream:block/dyedream_dirt"))
            .add("dyedream_sand", Blocks.SAND, BlockConfig.of()
                    .mineable("shovel").model("cube_all").tex("all", "pasterdream:block/dyedream_sand"))
            .add("dyedream_planks", Blocks.OAK_PLANKS, BlockConfig.of()
                    .mineable("axe").model("cube_all").tex("all", "pasterdream:block/dyedream_planks"))
            .add("dyedream_glass", Blocks.GLASS, BlockConfig.of()
                    .mineable("pickaxe").model("cube_all").tex("all", "pasterdream:block/dyedream_glass"))
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
            .addCustom("chiseled_dyedreamquartz_block",
                    BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).lightLevel(s -> 10).requiresCorrectToolForDrops(),
                    BlockConfig.of().mineable("pickaxe").model("cube_all").tex("all", "pasterdream:block/dyedreamquartz_chiseled"))
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
            .add("carve_dyedream_glass", Blocks.GLASS, BlockConfig.of()
                    .mineable("pickaxe").model("cube_all").tex("all", "pasterdream:block/carve_dyedream_glass"))
            .add("gold_carve_dyedream_glass", Blocks.GLASS, BlockConfig.of()
                    .mineable("pickaxe").model("cube_all").tex("all", "pasterdream:block/gold_carve_dyedream_glass"))
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
    public static final DeferredBlock<Block> CARVE_DYEDREAM_GLASS = SIMPLE_BLOCKS.get("carve_dyedream_glass");
    public static final DeferredBlock<Block> GOLD_CARVE_DYEDREAM_GLASS = SIMPLE_BLOCKS.get("gold_carve_dyedream_glass");

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
    public static final DeferredBlock<DyedreamLogBlock> STRIPPED_DYEDREAM_LOG = BLOCKS.registerBlock("stripped_dyedream_log",
            DyedreamLogBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LOG));
    public static final DeferredBlock<DyedreamLogBlock> STRIPPED_DYEDREAM_WOOD = BLOCKS.registerBlock("stripped_dyedream_wood",
            DyedreamLogBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LOG));
    public static final DeferredBlock<RotatedPillarBlock> PILLAR_DYEDREAMQUARTZ_BLOCK = BLOCKS.registerBlock("pillar_dyedreamquartz_block",
            RotatedPillarBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).requiresCorrectToolForDrops());

    // ==================== 建筑变体族（API 批量注册） ====================

    private static final VariantSetResult PLANKS_VARIANTS = BlockAPI.createVariantSet("dyedream_planks", () -> DYEDREAM_PLANKS.get())
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

    // ==================== 手动注册方块的 BlockConfig 初始化 ====================
    static {
        BlockAPI.putConfig("dyedream_log", BlockConfig.of()
                .mineable("axe").model("cube_column")
                .tex("end", "pasterdream:block/dyedream_log_top")
                .tex("side", "pasterdream:block/dyedream_log"));
        BlockAPI.putConfig("dyedream_wood", BlockConfig.of()
                .mineable("axe").model("cube_all")
                .tex("all", "pasterdream:block/dyedream_log"));
        BlockAPI.putConfig("stripped_dyedream_log", BlockConfig.of()
                .mineable("axe").model("cube_column")
                .tex("end", "pasterdream:block/dyedream_log_top")
                .tex("side", "pasterdream:block/dyedream_log"));
        BlockAPI.putConfig("stripped_dyedream_wood", BlockConfig.of()
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
    }

    // ==================== 玻璃面板和灯笼 ====================

    public static final DeferredBlock<IronBarsBlock> DYEDREAM_GLASSPANE = BLOCKS.registerBlock("dyedream_glasspane",
            IronBarsBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS_PANE));
    public static final DeferredBlock<IronBarsBlock> CARVE_DYEDREAM_GLASSPANE = BLOCKS.registerBlock("carve_dyedream_glasspane",
            IronBarsBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS_PANE));
    public static final DeferredBlock<IronBarsBlock> GOLD_CARVE_DYEDREAM_GLASSPANE = BLOCKS.registerBlock("gold_carve_dyedream_glasspane",
            IronBarsBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS_PANE));

    public static final DeferredBlock<LanternBlock> DYEDREAM_LARTERN = BLOCKS.registerBlock("dyedream_lartern",
            LanternBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.LANTERN).lightLevel(s -> 15));

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

}
