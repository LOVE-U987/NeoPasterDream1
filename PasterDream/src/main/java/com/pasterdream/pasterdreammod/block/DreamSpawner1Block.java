package com.pasterdream.pasterdreammod.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jetbrains.annotations.Nullable;

/**
 * 梦境刷怪机 1 号 (dream_spawner_1)
 * <p>
 * 装饰性刷怪机外壳方块，水平朝向，完整方块体积。
 * 与 dream_spawner_0（带 BlockEntity 的真刷怪逻辑，移交 GUI/逻辑波次）不同，
 * 原版 1 号本身即为纯装饰方块，无实体逻辑。
 */
public class DreamSpawner1Block extends HorizontalDirectionalBlock {

    /** 方块 MapCodec（1.21 数据驱动序列化要求） */
    public static final MapCodec<DreamSpawner1Block> CODEC = simpleCodec(DreamSpawner1Block::new);

    /**
     * 构造梦境刷怪机 1 号
     *
     * @param properties 方块属性
     */
    public DreamSpawner1Block(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite());
    }
}
