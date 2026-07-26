package com.pasterdream.pasterdreammod.registry.blocks;

import com.pasterdream.pasterdreammod.block.ArmorWreckBlock4Block;
import com.pasterdream.pasterdreammod.block.FireflyNestBlock;
import com.pasterdream.pasterdreammod.block.SmallStoneSpiritBlock;
import com.pasterdream.pasterdreammod.registry.PDBlocks;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.neoforged.neoforge.registries.DeferredBlock;


/**
 * 风之旅途维度残余方块注册（风泊木族 / 锈黑金属族 / 甲胄残骸族 / 弹射装置 / 地表装饰）。
 * <p>
 * 注册名与原版完全一致（注意风泊木变体不带 planks 前缀，如 windmoor_stairs），
 * 属性逐一对照原版 block/*.java 的 Properties 链。
 *
 * @see PDBlocks
 */
public class PDBlocksWindJourney {


    // ==================== 风泊木族（windmoor 木系 16 项） ====================

    /** 风泊木通用属性：可被岩浆点燃、BASS 音色、木头音效、硬度 2/3 */
    private static BlockBehaviour.Properties windmoorWoodProps() {
        return BlockBehaviour.Properties.of()
                .ignitedByLava()
                .instrument(NoteBlockInstrument.BASS)
                .sound(SoundType.WOOD)
                .strength(2f, 3f);
    }

    /** 风泊原木（轴向柱方块） */
    public static final DeferredBlock<RotatedPillarBlock> WINDMOOR_LOG = PDBlocks.BLOCKS.registerBlock("windmoor_log",
            RotatedPillarBlock::new, windmoorWoodProps());

    /** 风泊木（六面树皮，轴向柱方块） */
    public static final DeferredBlock<RotatedPillarBlock> WINDMOOR_WOOD = PDBlocks.BLOCKS.registerBlock("windmoor_wood",
            RotatedPillarBlock::new, windmoorWoodProps());

    /** 去皮风泊原木 */
    public static final DeferredBlock<RotatedPillarBlock> STRIPPED_WINDMOOR_LOG = PDBlocks.BLOCKS.registerBlock("stripped_windmoor_log",
            RotatedPillarBlock::new, windmoorWoodProps());

    /** 去皮风泊木 */
    public static final DeferredBlock<RotatedPillarBlock> STRIPPED_WINDMOOR_WOOD = PDBlocks.BLOCKS.registerBlock("stripped_windmoor_wood",
            RotatedPillarBlock::new, windmoorWoodProps());

    /** 风泊木板 */
    public static final DeferredBlock<Block> WINDMOOR_PLANKS = PDBlocks.BLOCKS.registerBlock("windmoor_planks",
            Block::new, windmoorWoodProps());

    /** 风泊木楼梯（注册名不带 planks 前缀，与原版一致） */
    public static final DeferredBlock<StairBlock> WINDMOOR_STAIRS = PDBlocks.BLOCKS.registerBlock("windmoor_stairs",
            p -> new StairBlock(WINDMOOR_PLANKS.get().defaultBlockState(), p),
            windmoorWoodProps().dynamicShape());

    /** 风泊木台阶 */
    public static final DeferredBlock<SlabBlock> WINDMOOR_SLAB = PDBlocks.BLOCKS.registerBlock("windmoor_slab",
            SlabBlock::new, windmoorWoodProps()
                    .noOcclusion()
                    .isRedstoneConductor((bs, br, bp) -> false)
                    .dynamicShape());

    /** 风泊木栅栏 */
    public static final DeferredBlock<FenceBlock> WINDMOOR_FENCE = PDBlocks.BLOCKS.registerBlock("windmoor_fence",
            FenceBlock::new, windmoorWoodProps()
                    .noOcclusion()
                    .isRedstoneConductor((bs, br, bp) -> false)
                    .dynamicShape()
                    .forceSolidOn());

    /** 风泊木栅栏门（注册名带下划线 fence_gate，与原版一致） */
    public static final DeferredBlock<FenceGateBlock> WINDMOOR_FENCE_GATE = PDBlocks.BLOCKS.registerBlock("windmoor_fence_gate",
            p -> new FenceGateBlock(WoodType.OAK, p), windmoorWoodProps()
                    .noOcclusion()
                    .isRedstoneConductor((bs, br, bp) -> false)
                    .dynamicShape()
                    .forceSolidOn());

    /** 风泊木门 */
    public static final DeferredBlock<DoorBlock> WINDMOOR_DOOR = PDBlocks.BLOCKS.registerBlock("windmoor_door",
            p -> new DoorBlock(BlockSetType.OAK, p), windmoorWoodProps()
                    .noOcclusion()
                    .isRedstoneConductor((bs, br, bp) -> false)
                    .dynamicShape());

    /** 风泊木活板门 */
    public static final DeferredBlock<TrapDoorBlock> WINDMOOR_TRAPDOOR = PDBlocks.BLOCKS.registerBlock("windmoor_trapdoor",
            p -> new TrapDoorBlock(BlockSetType.OAK, p), windmoorWoodProps()
                    .noOcclusion()
                    .isRedstoneConductor((bs, br, bp) -> false)
                    .dynamicShape());

    /** 风泊木压力板（EVERYTHING 灵敏度，由 OAK BlockSetType 提供） */
    public static final DeferredBlock<PressurePlateBlock> WINDMOOR_PRESSURE_PLATE = PDBlocks.BLOCKS.registerBlock("windmoor_pressure_plate",
            p -> new PressurePlateBlock(BlockSetType.OAK, p), BlockBehaviour.Properties.of()
                    .ignitedByLava()
                    .instrument(NoteBlockInstrument.BASS)
                    .sound(SoundType.WOOD)
                    .strength(1f, 0f)
                    .noOcclusion()
                    .isRedstoneConductor((bs, br, bp) -> false)
                    .dynamicShape()
                    .forceSolidOn());

    /** 风泊木按钮（30 tick 时长） */
    public static final DeferredBlock<ButtonBlock> WINDMOOR_BUTTON = PDBlocks.BLOCKS.registerBlock("windmoor_button",
            p -> new ButtonBlock(BlockSetType.OAK, 30, p), BlockBehaviour.Properties.of()
                    .ignitedByLava()
                    .instrument(NoteBlockInstrument.BASS)
                    .sound(SoundType.WOOD)
                    .strength(1f, 0f)
                    .noOcclusion()
                    .isRedstoneConductor((bs, br, bp) -> false)
                    .dynamicShape());

    /** 风泊树叶通用属性 */
    private static BlockBehaviour.Properties windmoorLeavesProps() {
        return BlockBehaviour.Properties.of()
                .ignitedByLava()
                .sound(SoundType.GRASS)
                .strength(0.01f, 0.1f)
                .noOcclusion()
                .isRedstoneConductor((bs, br, bp) -> false);
    }

    /** 风泊树叶 0 号（原版为普通方块而非 LeavesBlock，无凋落逻辑，保持一致） */
    public static final DeferredBlock<Block> WINDMOOR_LEAVES_0 = PDBlocks.BLOCKS.registerBlock("windmoor_leaves_0",
            Block::new, windmoorLeavesProps());

    /** 风泊树叶 1 号 */
    public static final DeferredBlock<Block> WINDMOOR_LEAVES_1 = PDBlocks.BLOCKS.registerBlock("windmoor_leaves_1",
            Block::new, windmoorLeavesProps());

    /** 风泊树叶 2 号（无碰撞体积，可穿行） */
    public static final DeferredBlock<Block> WINDMOOR_LEAVES_2 = PDBlocks.BLOCKS.registerBlock("windmoor_leaves_2",
            Block::new, windmoorLeavesProps().noCollission());

    // ==================== 锈黑金属族（3 项） ====================

    /** 锈黑金属块 */
    public static final DeferredBlock<Block> RUST_BLACK_METAL_BLOCK = PDBlocks.BLOCKS.registerBlock("rust_black_metal_block",
            Block::new, BlockBehaviour.Properties.of()
                    .sound(SoundType.COPPER)
                    .strength(8f, 5f)
                    .requiresCorrectToolForDrops());

    /** 锈黑金属墙 */
    public static final DeferredBlock<WallBlock> RUST_BLACK_METAL_BLOCK_WALL = PDBlocks.BLOCKS.registerBlock("rust_black_metal_block_wall",
            WallBlock::new, BlockBehaviour.Properties.of()
                    .sound(SoundType.COPPER)
                    .strength(8f, 5f)
                    .requiresCorrectToolForDrops()
                    .noOcclusion()
                    .isRedstoneConductor((bs, br, bp) -> false)
                    .dynamicShape()
                    .forceSolidOn());

    /** 锈黑金属栏杆（铁栏杆式连接渲染） */
    public static final DeferredBlock<IronBarsBlock> RUST_BLACK_METAL_BLOCK_BARS = PDBlocks.BLOCKS.registerBlock("rust_black_metal_block_bars",
            IronBarsBlock::new, BlockBehaviour.Properties.of()
                    .sound(SoundType.COPPER)
                    .strength(7f, 5f)
                    .requiresCorrectToolForDrops()
                    .noOcclusion()
                    .isRedstoneConductor((bs, br, bp) -> false));

    // ==================== 甲胄残骸族（5 项） ====================

    /** 甲胄残骸通用属性：远古残骸音效、硬度 10/6、需正确工具 */
    private static BlockBehaviour.Properties armorWreckProps() {
        return BlockBehaviour.Properties.of()
                .sound(SoundType.ANCIENT_DEBRIS)
                .strength(10f, 6f)
                .requiresCorrectToolForDrops();
    }

    /** 甲胄残骸方块 0 号（完整方块） */
    public static final DeferredBlock<Block> ARMOR_WRECK_BLOCK_0 = PDBlocks.BLOCKS.registerBlock("armor_wreck_block_0",
            Block::new, armorWreckProps());

    /** 甲胄残骸方块 1 号（台阶型） */
    public static final DeferredBlock<SlabBlock> ARMOR_WRECK_BLOCK_1 = PDBlocks.BLOCKS.registerBlock("armor_wreck_block_1",
            SlabBlock::new, armorWreckProps()
                    .noOcclusion()
                    .isRedstoneConductor((bs, br, bp) -> false)
                    .dynamicShape());

    /** 甲胄残骸方块 2 号（楼梯型，原版以空气状态为基底） */
    public static final DeferredBlock<StairBlock> ARMOR_WRECK_BLOCK_2 = PDBlocks.BLOCKS.registerBlock("armor_wreck_block_2",
            p -> new StairBlock(Blocks.AIR.defaultBlockState(), p), armorWreckProps()
                    .noOcclusion()
                    .isRedstoneConductor((bs, br, bp) -> false)
                    .dynamicShape());

    /** 甲胄残骸方块 3 号（墙型） */
    public static final DeferredBlock<WallBlock> ARMOR_WRECK_BLOCK_3 = PDBlocks.BLOCKS.registerBlock("armor_wreck_block_3",
            WallBlock::new, armorWreckProps()
                    .noOcclusion()
                    .isRedstoneConductor((bs, br, bp) -> false)
                    .dynamicShape()
                    .forceSolidOn());

    /** 甲胄残骸方块 4 号（六向薄板 + 可含水，自定义类） */
    public static final DeferredBlock<ArmorWreckBlock4Block> ARMOR_WRECK_BLOCK_4 = PDBlocks.BLOCKS.registerBlock("armor_wreck_block_4",
            ArmorWreckBlock4Block::new, armorWreckProps()
                    .noOcclusion()
                    .isRedstoneConductor((bs, br, bp) -> false));

    // ==================== 弹射装置（2 项） ====================

    /** 弹射方块（跳跃系数 3.2 倍） */
    public static final DeferredBlock<Block> EJECTION_PRESSURE_BLOCK = PDBlocks.BLOCKS.registerBlock("ejection_pressure_block",
            Block::new, BlockBehaviour.Properties.of()
                    .sound(SoundType.METAL)
                    .strength(7f, 5f)
                    .requiresCorrectToolForDrops()
                    .jumpFactor(3.2f));

    /**
     * 弹射压力板
     * 原版 Sensitivity.MOBS + entityInside 延迟 2 tick 上抛 0.8
     */
    public static final DeferredBlock<PressurePlateBlock> EJECTION_PRESSURE_PLATE = PDBlocks.BLOCKS.registerBlock("ejection_pressure_plate",
            p -> new com.pasterdream.pasterdreammod.block.EjectionPressurePlateBlock(BlockSetType.STONE, p), BlockBehaviour.Properties.of()
                    .sound(SoundType.METAL)
                    .strength(0.5f)
                    .requiresCorrectToolForDrops()
                    .noOcclusion()
                    .isRedstoneConductor((bs, br, bp) -> false)
                    .dynamicShape()
                    .forceSolidOn());

    // ==================== 风之旅途地表装饰 ====================

    /** 天使方块（羊毛音效轻质方块） */
    public static final DeferredBlock<Block> ANGEL_BLOCK = PDBlocks.BLOCKS.registerBlock("angel_block",
            Block::new, BlockBehaviour.Properties.of()
                    .ignitedByLava()
                    .sound(SoundType.WOOL)
                    .strength(0.5f)
                    .noOcclusion()
                    .isRedstoneConductor((bs, br, bp) -> false));

    /**
     * 萤火虫巢（世界生成硬依赖：ground_feature_wind_journey_4 引用）
     * 日间蓄能、夜间召唤萤火虫（FireflyNestBlock + W4Data BE）
     */
    public static final DeferredBlock<FireflyNestBlock> FIREFLY_NEST = PDBlocks.BLOCKS.registerBlock("firefly_nest",
            FireflyNestBlock::new, BlockBehaviour.Properties.of()
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .sound(SoundType.CALCITE)
                    .strength(1f, 10f)
                    .noOcclusion()
                    .randomTicks()
                    .isRedstoneConductor((bs, br, bp) -> false));

    /**
     * 小石灵方块（世界生成硬依赖：ground_feature_wind_journey_6 引用）
     * 玩家破坏时召唤小石灵实体
     */
    public static final DeferredBlock<SmallStoneSpiritBlock> SMALL_STONE_SPIRIT_BLOCK = PDBlocks.BLOCKS.registerBlock("small_stone_spirit_block",
            SmallStoneSpiritBlock::new, BlockBehaviour.Properties.of()
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .sound(SoundType.CALCITE)
                    .strength(0.5f)
                    .requiresCorrectToolForDrops()
                    .noOcclusion()
                    .isRedstoneConductor((bs, br, bp) -> false));
}
