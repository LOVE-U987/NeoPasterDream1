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
 * 阴影维度基础方块与变体注册。
 *
 * @see PDBlocks
 */
public class PDBlocksShadow {


    // ==================== 阴影维度基础方块 ====================

    /** 阴影方块（下落方块，类似沙子） */
    public static final DeferredBlock<Block> SHADOW_BLOCK = PDBlocks.BLOCKS.registerBlock("shadow_block",
            Block::new, BlockBehaviour.Properties.of()
                    .sound(SoundType.WOOL)
                    .strength(0.45f, 0.5f)
                    .noOcclusion()
                    .isRedstoneConductor((bs, br, bp) -> false)
                    .requiresCorrectToolForDrops());

    /** 厚阴影方块（不下落，硬度更高） */
    public static final DeferredBlock<Block> THICK_SHADOW_BLOCK = PDBlocks.BLOCKS.registerBlock("thick_shadow_block",
            Block::new, BlockBehaviour.Properties.of()
                    .sound(SoundType.WOOL)
                    .strength(1.0f, 0.75f)
                    .noOcclusion()
                    .isRedstoneConductor((bs, br, bp) -> false)
                    .requiresCorrectToolForDrops());

    /** 阴影石 */
    public static final DeferredBlock<Block> SHADOW_STONE = PDBlocks.BLOCKS.registerBlock("shadow_stone",
            Block::new, BlockBehaviour.Properties.of()
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .sound(SoundType.DEEPSLATE)
                    .strength(1.5f, 1.0f)
                    .requiresCorrectToolForDrops());

    /** 阴影石砖 */
    public static final DeferredBlock<Block> SHADOW_STONE_BRICK = PDBlocks.BLOCKS.registerBlock("shadow_stone_brick",
            Block::new, BlockBehaviour.Properties.of()
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .sound(SoundType.DEEPSLATE)
                    .strength(1.5f, 1.0f)
                    .requiresCorrectToolForDrops());

    /** 阴影石砖块（复数形式，纹理不同） */
    public static final DeferredBlock<Block> SHADOW_STONE_BRICKS = PDBlocks.BLOCKS.registerBlock("shadow_stone_bricks",
            Block::new, BlockBehaviour.Properties.of()
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .sound(SoundType.DEEPSLATE)
                    .strength(1.5f, 1.0f)
                    .requiresCorrectToolForDrops());

    /** 阴影石瓦砖 */
    public static final DeferredBlock<Block> SHADOW_STONE_TILES = PDBlocks.BLOCKS.registerBlock("shadow_stone_tiles",
            Block::new, BlockBehaviour.Properties.of()
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .sound(SoundType.DEEPSLATE)
                    .strength(1.5f, 1.0f)
                    .requiresCorrectToolForDrops());

    /** 雕凿阴影石砖 */
    public static final DeferredBlock<Block> CHISELED_SHADOW_STONE_BRICK = PDBlocks.BLOCKS.registerBlock("chiseled_shadow_stone_brick",
            Block::new, BlockBehaviour.Properties.of()
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .sound(SoundType.DEEPSLATE)
                    .strength(1.5f, 1.0f)
                    .requiresCorrectToolForDrops());

    /** 裂纹阴影石砖 */
    public static final DeferredBlock<Block> CRACKED_SHADOW_STONE_BRICK = PDBlocks.BLOCKS.registerBlock("cracked_shadow_stone_brick",
            Block::new, BlockBehaviour.Properties.of()
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .sound(SoundType.DEEPSLATE)
                    .strength(1.5f, 1.0f)
                    .requiresCorrectToolForDrops());

    /** 阴影菌丝 */
    public static final DeferredBlock<Block> SHADOW_NYLIUM = PDBlocks.BLOCKS.registerBlock("shadow_nylium",
            Block::new, BlockBehaviour.Properties.of()
                    .sound(SoundType.NYLIUM)
                    .strength(0.4f)
                    .requiresCorrectToolForDrops());

    /** 阴影菌光体（发光等级 12） */
    public static final DeferredBlock<Block> SHADOW_SHROOMLIGHT = PDBlocks.BLOCKS.registerBlock("shadow_shroomlight",
            Block::new, BlockBehaviour.Properties.of()
                    .sound(SoundType.WART_BLOCK)
                    .strength(1.0f)
                    .lightLevel(s -> 12)
                    .requiresCorrectToolForDrops());

    /** 阴影疣块 */
    public static final DeferredBlock<Block> SHADOW_WART_BLOCK = PDBlocks.BLOCKS.registerBlock("shadow_wart_block",
            Block::new, BlockBehaviour.Properties.of()
                    .sound(SoundType.WART_BLOCK)
                    .strength(1.0f)
                    .requiresCorrectToolForDrops());

    /** 阴影菌柄（朝向轴） */
    public static final DeferredBlock<RotatedPillarBlock> SHADOW_STEM = PDBlocks.BLOCKS.registerBlock("shadow_stem",
            RotatedPillarBlock::new, BlockBehaviour.Properties.of()
                    .sound(SoundType.STEM)
                    .strength(2.0f, 3.0f)
                    .requiresCorrectToolForDrops());

    /** 阴影菌核（6 面皮） */
    public static final DeferredBlock<RotatedPillarBlock> SHADOW_HYPHAE = PDBlocks.BLOCKS.registerBlock("shadow_hyphae",
            RotatedPillarBlock::new, BlockBehaviour.Properties.of()
                    .sound(SoundType.STEM)
                    .strength(2.0f, 3.0f)
                    .requiresCorrectToolForDrops());

    /** 去皮阴影菌柄 */
    public static final DeferredBlock<RotatedPillarBlock> STRIPPED_SHADOW_STEM = PDBlocks.BLOCKS.registerBlock("stripped_shadow_stem",
            RotatedPillarBlock::new, BlockBehaviour.Properties.of()
                    .sound(SoundType.STEM)
                    .strength(2.0f, 3.0f)
                    .requiresCorrectToolForDrops());

    /** 去皮阴影菌核 */
    public static final DeferredBlock<RotatedPillarBlock> STRIPPED_SHADOW_HYPHAE = PDBlocks.BLOCKS.registerBlock("stripped_shadow_hyphae",
            RotatedPillarBlock::new, BlockBehaviour.Properties.of()
                    .sound(SoundType.STEM)
                    .strength(2.0f, 3.0f)
                    .requiresCorrectToolForDrops());

    /** 阴影木板 */
    public static final DeferredBlock<Block> SHADOW_PLANKS = PDBlocks.BLOCKS.registerBlock("shadow_planks",
            Block::new, BlockBehaviour.Properties.of()
                    .sound(SoundType.WOOD)
                    .strength(2.0f, 3.0f)
                    .requiresCorrectToolForDrops());

    // ==================== 阴影石砖变体族（API 批量注册） ====================

    private static final VariantSetResult SHADOW_STONE_BRICK_VARIANTS = BlockAPI.createVariantSet("shadow_stone_brick", () -> SHADOW_STONE_BRICK.get())
            .mineable("pickaxe")
            .withStairs()
            .withSlab()
            .withWall()
            .build();

    public static final DeferredBlock<StairBlock> SHADOW_STONE_BRICK_STAIRS = SHADOW_STONE_BRICK_VARIANTS.stairs();
    public static final DeferredBlock<SlabBlock> SHADOW_STONE_BRICK_SLAB = SHADOW_STONE_BRICK_VARIANTS.slab();
    public static final DeferredBlock<WallBlock> SHADOW_STONE_BRICK_WALL = SHADOW_STONE_BRICK_VARIANTS.wall();

    private static final VariantSetResult SHADOW_STONE_BRICKS_VARIANTS = BlockAPI.createVariantSet("shadow_stone_bricks", () -> SHADOW_STONE_BRICKS.get())
            .mineable("pickaxe")
            .withStairs()
            .withSlab()
            .withWall()
            .build();

    public static final DeferredBlock<StairBlock> SHADOW_STONE_BRICKS_STAIRS = SHADOW_STONE_BRICKS_VARIANTS.stairs();
    public static final DeferredBlock<SlabBlock> SHADOW_STONE_BRICKS_SLAB = SHADOW_STONE_BRICKS_VARIANTS.slab();
    public static final DeferredBlock<WallBlock> SHADOW_STONE_BRICKS_WALL = SHADOW_STONE_BRICKS_VARIANTS.wall();

    private static final VariantSetResult SHADOW_STONE_TILES_VARIANTS = BlockAPI.createVariantSet("shadow_stone_tiles", () -> SHADOW_STONE_TILES.get())
            .mineable("pickaxe")
            .withStairs()
            .withSlab()
            .withWall()
            .build();

    public static final DeferredBlock<StairBlock> SHADOW_STONE_TILES_STAIRS = SHADOW_STONE_TILES_VARIANTS.stairs();
    public static final DeferredBlock<SlabBlock> SHADOW_STONE_TILES_SLAB = SHADOW_STONE_TILES_VARIANTS.slab();
    public static final DeferredBlock<WallBlock> SHADOW_STONE_TILES_WALL = SHADOW_STONE_TILES_VARIANTS.wall();

    // ==================== 阴影木板变体族（API 批量注册） ====================

    private static final VariantSetResult SHADOW_PLANKS_VARIANTS = BlockAPI.createVariantSet("shadow_planks", () -> SHADOW_PLANKS.get())
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

    public static final DeferredBlock<StairBlock> SHADOW_PLANKS_STAIRS = SHADOW_PLANKS_VARIANTS.stairs();
    public static final DeferredBlock<SlabBlock> SHADOW_PLANKS_SLAB = SHADOW_PLANKS_VARIANTS.slab();
    public static final DeferredBlock<FenceBlock> SHADOW_PLANKS_FENCE = SHADOW_PLANKS_VARIANTS.fence();
    public static final DeferredBlock<FenceGateBlock> SHADOW_PLANKS_FENCEGATE = SHADOW_PLANKS_VARIANTS.fenceGate();
    public static final DeferredBlock<DoorBlock> SHADOW_PLANKS_DOOR = SHADOW_PLANKS_VARIANTS.door();
    public static final DeferredBlock<TrapDoorBlock> SHADOW_PLANKS_TRAPDOOR = SHADOW_PLANKS_VARIANTS.trapdoor();
    public static final DeferredBlock<PressurePlateBlock> SHADOW_PLANKS_PRESSURE_PLATE = SHADOW_PLANKS_VARIANTS.pressurePlate();
    public static final DeferredBlock<ButtonBlock> SHADOW_PLANKS_BUTTON = SHADOW_PLANKS_VARIANTS.button();

    /** 阴影玻璃板（继承 IronBarsBlock，玻璃板式连接渲染） */
    public static final DeferredBlock<IronBarsBlock> SHADOW_PLANKS_PANE = PDBlocks.BLOCKS.registerBlock("shadow_planks_pane",
            IronBarsBlock::new, BlockBehaviour.Properties.of()
                    .sound(SoundType.WOOD)
                    .strength(2.0f, 3.0f));
}
