package com.pasterdream.pasterdreammod.block;

import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.core.BlockPos;

import java.util.List;

/**
 * 厚重云朵方块
 * 实体方块，不透明，比普通云朵更坚固，允许天光穿透
 */
public class ThickCloudBlock extends Block {
    public ThickCloudBlock() {
        super(BlockBehaviour.Properties.of()
                .ignitedByLava()
                .sound(SoundType.WOOL)
                .strength(0.3f, 0.5f));
    }

    /**
     * 允许天光穿透
     */
    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
        return true;
    }

    /**
     * 不阻挡亮度
     */
    @Override
    public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
        return 0;
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        return List.of(new ItemStack(this));
    }
}