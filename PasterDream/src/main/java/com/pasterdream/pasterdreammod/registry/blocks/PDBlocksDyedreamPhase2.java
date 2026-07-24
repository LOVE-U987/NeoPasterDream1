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
 * 染梦维度 Phase 2 剩余方块注册。
 *
 * @see PDBlocks
 */
public class PDBlocksDyedreamPhase2 {


    // ==================== 染梦维度剩余方块（Phase 2） ====================

    /** 大气泡方块 */
    public static final DeferredBlock<Block> BIG_BUBBLE = PDBlocks.BLOCKS.registerBlock("big_bubble",
            Block::new, BlockBehaviour.Properties.of()
                    .sound(SoundType.WET_GRASS)
                    .strength(0.1f)
                    .noOcclusion()
                    .isRedstoneConductor((bs, br, bp) -> false));

    /** 风行者水晶方块 */
    public static final DeferredBlock<Block> WINDRUNNER_CRYSTAL_BLOCK = PDBlocks.BLOCKS.registerBlock("windrunner_crystal_block",
            Block::new, BlockBehaviour.Properties.of()
                    .sound(SoundType.GLASS)
                    .strength(1.5f)
                    .lightLevel(s -> 10)
                    .requiresCorrectToolForDrops());

    /** 凝结风方块 */
    public static final DeferredBlock<Block> CONGEAL_WIND_BLOCK = PDBlocks.BLOCKS.registerBlock("congeal_wind_block",
            Block::new, BlockBehaviour.Properties.of()
                    .sound(SoundType.GLASS)
                    .strength(0.5f)
                    .noOcclusion());

    /** 星呼方块 */
    public static final DeferredBlock<Block> STARCALL_BLOCK = PDBlocks.BLOCKS.registerBlock("starcall_block",
            Block::new, BlockBehaviour.Properties.of()
                    .sound(SoundType.GLASS)
                    .strength(1.5f)
                    .lightLevel(s -> 15)
                    .requiresCorrectToolForDrops());

    /** 星呼裂纹 */
    public static final DeferredBlock<Block> STARCALL_CRACK = PDBlocks.BLOCKS.registerBlock("starcall_crack",
            Block::new, BlockBehaviour.Properties.of()
                    .sound(SoundType.GLASS)
                    .strength(-1.0f, 3600000.0f)
                    .lightLevel(s -> 15)
                    .noOcclusion()
                    .emissiveRendering((bs, br, bp) -> true)
                    .hasPostProcess((bs, br, bp) -> true));

    /** 青色石头 */
    public static final DeferredBlock<Block> CYAN_STONE = PDBlocks.BLOCKS.registerBlock("cyan_stone",
            Block::new, BlockBehaviour.Properties.of()
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .sound(SoundType.STONE)
                    .strength(1.5f)
                    .requiresCorrectToolForDrops());

    /** 青色苔藓石头 */
    public static final DeferredBlock<Block> CYAN_MOSS_STONE = PDBlocks.BLOCKS.registerBlock("cyan_moss_stone",
            Block::new, BlockBehaviour.Properties.of()
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .sound(SoundType.STONE)
                    .strength(1.0f)
                    .requiresCorrectToolForDrops());

    /** 白砂 */
    public static final DeferredBlock<Block> WHITE_SAND = PDBlocks.BLOCKS.registerBlock("white_sand",
            Block::new, BlockBehaviour.Properties.of()
                    .sound(SoundType.SAND)
                    .strength(0.5f));

    /** 盐块 */
    public static final DeferredBlock<Block> SALT_BLOCK = PDBlocks.BLOCKS.registerBlock("salt_block",
            Block::new, BlockBehaviour.Properties.of()
                    .sound(SoundType.SAND)
                    .strength(0.5f));

    /** 透明玻璃 */
    public static final DeferredBlock<Block> CLARITY_GLASS = PDBlocks.BLOCKS.registerBlock("clarity_glass",
            Block::new, BlockBehaviour.Properties.of()
                    .sound(SoundType.GLASS)
                    .strength(0.3f)
                    .noOcclusion());

    /** 雕刻透明玻璃 */
    public static final DeferredBlock<Block> CARVE_CLARITY_GLASS = PDBlocks.BLOCKS.registerBlock("carve_clarity_glass",
            Block::new, BlockBehaviour.Properties.of()
                    .sound(SoundType.GLASS)
                    .strength(0.3f)
                    .noOcclusion());

    /** 边框透明玻璃 */
    public static final DeferredBlock<Block> FRAME_CLARITY_GLASS = PDBlocks.BLOCKS.registerBlock("frame_clarity_glass",
            Block::new, BlockBehaviour.Properties.of()
                    .sound(SoundType.GLASS)
                    .strength(0.3f)
                    .noOcclusion());

    /** 透明玻璃板 */
    public static final DeferredBlock<IronBarsBlock> CLARITY_GLASSPANE = PDBlocks.BLOCKS.registerBlock("clarity_glasspane",
            IronBarsBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS_PANE));

    /** 雕刻透明玻璃板 */
    public static final DeferredBlock<IronBarsBlock> CARVE_CLARITY_GLASSPANE = PDBlocks.BLOCKS.registerBlock("carve_clarity_glasspane",
            IronBarsBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS_PANE));

    /** 边框透明玻璃板 */
    public static final DeferredBlock<IronBarsBlock> FRAME_CLARITY_GLASSPANE = PDBlocks.BLOCKS.registerBlock("frame_clarity_glasspane",
            IronBarsBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS_PANE));

    /** 破风幕 */
    public static final DeferredBlock<Block> BREAKWIND_CURTAIN = PDBlocks.BLOCKS.registerBlock("breakwind_curtain",
            Block::new, BlockBehaviour.Properties.of()
                    .sound(SoundType.WOOL)
                    .strength(0.1f)
                    .noOcclusion()
                    .noCollission());

    /** 风铁栏杆 */
    public static final DeferredBlock<IronBarsBlock> WINDIRON_BARS = PDBlocks.BLOCKS.registerBlock("windiron_bars",
            IronBarsBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BARS));

    /** 青色石头砖 */
    public static final DeferredBlock<Block> CYAN_STONE_BRICKS = PDBlocks.BLOCKS.registerBlock("cyan_stone_bricks",
            Block::new, BlockBehaviour.Properties.of()
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .sound(SoundType.STONE)
                    .strength(1.5f)
                    .requiresCorrectToolForDrops());

    /** 青色石头砖楼梯 */
    public static final DeferredBlock<StairBlock> CYAN_STONE_BRICK_STAIRS = PDBlocks.BLOCKS.registerBlock("cyan_stone_brick_stairs",
            p -> new StairBlock(CYAN_STONE_BRICKS.get().defaultBlockState(), p),
            BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_STAIRS));

    /** 青色石头砖台阶 */
    public static final DeferredBlock<SlabBlock> CYAN_STONE_BRICK_SLAB = PDBlocks.BLOCKS.registerBlock("cyan_stone_brick_slab",
            SlabBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_SLAB));

    /** 青色石头砖墙 */
    public static final DeferredBlock<WallBlock> CYAN_STONE_BRICK_WALL = PDBlocks.BLOCKS.registerBlock("cyan_stone_brick_wall",
            WallBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.COBBLESTONE_WALL));

    /** 苔藓青色石头砖 */
    public static final DeferredBlock<Block> MOSSY_CYAN_STONE_BRICKS = PDBlocks.BLOCKS.registerBlock("mossy_cyan_stone_bricks",
            Block::new, BlockBehaviour.Properties.of()
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .sound(SoundType.STONE)
                    .strength(1.5f)
                    .requiresCorrectToolForDrops());

    /** 苔藓青色石头砖楼梯 */
    public static final DeferredBlock<StairBlock> MOSSY_CYAN_STONE_BRICK_STAIRS = PDBlocks.BLOCKS.registerBlock("mossy_cyan_stone_brick_stairs",
            p -> new StairBlock(MOSSY_CYAN_STONE_BRICKS.get().defaultBlockState(), p),
            BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_STAIRS));

    /** 苔藓青色石头砖台阶 */
    public static final DeferredBlock<SlabBlock> MOSSY_CYAN_STONE_BRICK_SLAB = PDBlocks.BLOCKS.registerBlock("mossy_cyan_stone_brick_slab",
            SlabBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_SLAB));

    /** 苔藓青色石头砖墙 */
    public static final DeferredBlock<WallBlock> MOSSY_CYAN_STONE_BRICK_WALL = PDBlocks.BLOCKS.registerBlock("mossy_cyan_stone_brick_wall",
            WallBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.COBBLESTONE_WALL));

    /** 雕凿青色石头砖 */
    public static final DeferredBlock<Block> CHISELED_CYAN_STONE_BRICKS = PDBlocks.BLOCKS.registerBlock("chiseled_cyan_stone_bricks",
            Block::new, BlockBehaviour.Properties.of()
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .sound(SoundType.STONE)
                    .strength(1.5f)
                    .requiresCorrectToolForDrops());

    /** 青色石头柱 */
    public static final DeferredBlock<RotatedPillarBlock> CYAN_STONE_PILLAR = PDBlocks.BLOCKS.registerBlock("cyan_stone_pillar",
            RotatedPillarBlock::new, BlockBehaviour.Properties.of()
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .sound(SoundType.STONE)
                    .strength(1.5f)
                    .requiresCorrectToolForDrops());

    /** 青色石头压力板 */
    public static final DeferredBlock<PressurePlateBlock> CYAN_STONE_PRESSURE_PLATE = PDBlocks.BLOCKS.registerBlock("cyan_stone_pressure_plate",
            p -> new PressurePlateBlock(BlockSetType.STONE, p), BlockBehaviour.Properties.of()
                    .sound(SoundType.STONE)
                    .strength(0.5f)
                    .noOcclusion());

    /** 青色石头按钮 */
    public static final DeferredBlock<ButtonBlock> CYAN_STONE_BUTTON = PDBlocks.BLOCKS.registerBlock("cyan_stone_button",
            p -> new ButtonBlock(BlockSetType.STONE, 20, p),
            BlockBehaviour.Properties.of()
                    .sound(SoundType.STONE)
                    .strength(0.5f)
                    .noOcclusion());
}
