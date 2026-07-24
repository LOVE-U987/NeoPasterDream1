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
 * 功能性方块注册（书卷、炼药锅、水晶箱）。
 *
 * @see PDBlocks
 */
public class PDBlocksFunctional {


    // ==================== 寻梦者的永恒书卷 ====================

    /**
     * 寻梦者的永恒书卷 (the_endless_book_of_dream_seekers)
     * GeckoLib 3D 书籍模型，1 格库存，支持 GUI 交互
     */
    public static final DeferredBlock<TheEndlessBookOfDreamSeekersBlock> THE_ENDLESS_BOOK_OF_DREAM_SEEKERS = PDBlocks.BLOCKS.register("the_endless_book_of_dream_seekers",
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
    public static final DeferredBlock<DreamCauldronBlock> DREAM_CAULDRON = PDBlocks.BLOCKS.register("dream_cauldron",
            () -> new DreamCauldronBlock(BlockBehaviour.Properties.of()
                    .sound(SoundType.STONE)
                    .strength(1.0f, 0.5f)
                    .noOcclusion()));

    // ==================== 融梦水晶箱（GeckoLib 动画） ====================

    /**
     * 融梦水晶箱（关闭状态）- 使用 GeckoLib 动画的三级随机宝藏箱
     * animation 属性 0-3：闲置/普通/稀有/传说
     */
    public static final DeferredBlock<MeltdreamChestBlock> MELTDREAM_CHEST = PDBlocks.BLOCKS.register("meltdream_chest",
            () -> new MeltdreamChestBlock(BlockBehaviour.Properties.of()
                    .sound(SoundType.GLASS)
                    .strength(1.0f, 0.5f)
                    .noOcclusion()
                    .lightLevel(state -> state.getValue(MeltdreamChestBlock.ANIMATION) > 0 ? 8 : 0)));

    /**
     * 融梦水晶箱（打开状态）- 无动画，右键可打开 GUI
     */
    public static final DeferredBlock<MeltdreamChestOpenBlock> MELTDREAM_CHEST_OPEN = PDBlocks.BLOCKS.register("meltdream_chest_open",
            () -> new MeltdreamChestOpenBlock(BlockBehaviour.Properties.of()
                    .sound(SoundType.GLASS)
                    .strength(1.0f, 0.5f)
                    .noOcclusion()
                    .lightLevel(state -> 8)));
}
