package com.pasterdream.pasterdreammod.block;

import com.pasterdream.pasterdreammod.registry.PDBlocks;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.core.BlockPos;

import java.util.Collections;
import java.util.List;

/**
 * 染梦双层植物通用方块
 * 用于所有双格高的花和草（如 flower_7, flower_10, grass_4, grass_10, grass_15 等）
 * 必须种在染梦土壤上，只有下半部分掉落自身
 */
public class DyedreamDoublePlantBlock extends DoublePlantBlock {
    /**
     * @param properties 方块属性
     */
    public DyedreamDoublePlantBlock(Properties properties) {
        super(properties);
    }

    /**
     * 使用默认属性的快捷构造
     */
    public DyedreamDoublePlantBlock() {
        this(BlockBehaviour.Properties.of()
            .mapColor(MapColor.PLANT)
            .sound(SoundType.GRASS)
            .instabreak()
            .noCollission()
            .offsetType(BlockBehaviour.OffsetType.XZ)
            .pushReaction(PushReaction.DESTROY));
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(PDBlocks.DYEDREAM_GRASS.get())
            || state.is(PDBlocks.DYEDREAM_DIRT.get())
            || state.is(PDBlocks.DYEDREAM_SAND.get())
            || state.is(PDBlocks.DYEDREAM_BLOCK.get());
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        if (state.getValue(HALF) != DoubleBlockHalf.LOWER) {
            return Collections.emptyList();
        }
        return List.of(new ItemStack(this));
    }
}