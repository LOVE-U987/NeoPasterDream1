package com.pasterdream.pasterdreammod.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 染梦发光树叶方块
 * <p>
 * 继承 LeavesBlock，自带亮度 8 的微光效果，用于树冠边缘与发光树变体。
 */
public class DyedreamGlowingLeavesBlock extends LeavesBlock {

    /**
     * 使用默认属性构造
     */
    public DyedreamGlowingLeavesBlock() {
        super(BlockBehaviour.Properties.of()
                .ignitedByLava()
                .sound(SoundType.GRASS)
                .strength(0.01f, 0.1f)
                .noOcclusion()
                .lightLevel(state -> 8)
                .isRedstoneConductor((bs, br, bp) -> false)
                .dynamicShape());
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
        return true;
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
        return 0;
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
        return 20;
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return true;
    }
}
