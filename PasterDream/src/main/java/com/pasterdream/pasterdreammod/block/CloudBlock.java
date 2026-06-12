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
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.LevelReader;

import java.util.List;

/**
 * 云朵方块
 * 轻质、透明、可被点燃，同种云朵相邻时会跳过面渲染，允许天光穿透
 */
public class CloudBlock extends Block {
    public CloudBlock() {
        super(BlockBehaviour.Properties.of()
                .ignitedByLava()
                .sound(SoundType.WOOL)
                .strength(0.2f, 0f)
                .noOcclusion()
                .isRedstoneConductor((bs, br, bp) -> false));
    }

    /**
     * 同种云朵相邻时跳过面渲染，实现透明云朵效果
     */
    @Override
    public boolean skipRendering(BlockState state, BlockState adjacentState, Direction side) {
        return adjacentState.is(this) || super.skipRendering(state, adjacentState, side);
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

    /**
     * 视觉形状为空，不影响视觉碰撞
     */
    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter reader, BlockPos pos, net.minecraft.world.phys.shapes.CollisionContext ctx) {
        return Shapes.empty();
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        return List.of(new ItemStack(this));
    }
}