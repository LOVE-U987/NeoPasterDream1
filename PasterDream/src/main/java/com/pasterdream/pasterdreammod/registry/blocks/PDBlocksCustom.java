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
 * 自定义交互方块注册（蓄梦池、书桌、列车结构、生命水晶）。
 *
 * @see PDBlocks
 */
public class PDBlocksCustom {


    // ==================== 自定义方块（保持手动注册） ====================

    public static final DeferredBlock<DreamAccumulatorBlock> DREAM_ACCUMULATOR = PDBlocks.BLOCKS.register("dream_accumulator",
            () -> new DreamAccumulatorBlock(BlockBehaviour.Properties.of()
                    .sound(SoundType.CALCITE)
                    .strength(1.0f)
                    .requiresCorrectToolForDrops()
                    .noOcclusion()));

    public static final DeferredBlock<DyedreamDeskBlock> DYEDREAM_DESK = PDBlocks.BLOCKS.register("dyedream_desk",
            () -> new DyedreamDeskBlock(BlockBehaviour.Properties.of()
                    .sound(SoundType.WOOD)
                    .strength(1.0f)
                    .requiresCorrectToolForDrops()
                    .noOcclusion()));

    /**
     * 梦境列车结构方块 (dream_train_structure)
     * 装饰性方块，右键点击时发送列车到站提示消息
     */
    public static final DeferredBlock<DreamTrainStructureBlock> DREAM_TRAIN_STRUCTURE = PDBlocks.BLOCKS.register("dream_train_structure",
            () -> new DreamTrainStructureBlock(BlockBehaviour.Properties.of()
                    .sound(SoundType.METAL)
                    .strength(2.0f)
                    .requiresCorrectToolForDrops()
                    .noOcclusion()));

    public static final DeferredBlock<LifeCrystalBlock> LIFE_CRYSTAL = PDBlocks.BLOCKS.register("life_crystal",
            () -> new LifeCrystalBlock(BlockBehaviour.Properties.of()
                    .sound(SoundType.GLASS)
                    .strength(1.0f)
                    .lightLevel(state -> 12)
                    .noOcclusion()));
}
