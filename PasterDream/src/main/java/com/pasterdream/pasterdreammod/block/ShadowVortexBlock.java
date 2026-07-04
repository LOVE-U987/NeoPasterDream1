package com.pasterdream.pasterdreammod.block;

import com.mojang.serialization.MapCodec;
import com.pasterdream.pasterdreammod.block.entity.ShadowVortexBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/**
 * 暗影漩涡方块
 * <p>
 * BOSS 右手涡流技能生成的临时方块，使用 GeckoLib 渲染旋转的漩涡动画。
 * 方块无碰撞、无掉落、持续一段时间后自动消失。
 * 对应方块实体 {@link ShadowVortexBlockEntity}。
 */
public class ShadowVortexBlock extends BaseEntityBlock {

    public static final MapCodec<ShadowVortexBlock> CODEC = simpleCodec(ShadowVortexBlock::new);

    /** 动画阶段属性（预留，当前仅用于状态定义） */
    public static final IntegerProperty ANIMATION = IntegerProperty.create("animation", 0, 1);

    /** 碰撞箱形状（中间较薄） */
    private static final VoxelShape SHAPE = Block.box(0, 4, 0, 16, 12, 16);

    /**
     * 构造暗影漩涡方块
     *
     * @param properties 方块属性
     */
    public ShadowVortexBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(ANIMATION, 0));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ShadowVortexBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(net.minecraft.world.level.Level level,
                                                                   BlockState state,
                                                                   BlockEntityType<T> blockEntityType) {
        return (lvl, pos, st, blockEntity) -> {
            if (blockEntity instanceof ShadowVortexBlockEntity vortexBlockEntity) {
                vortexBlockEntity.tick();
            }
        };
    }

    @Override
    public VoxelShape getShape(BlockState state, net.minecraft.world.level.BlockGetter level,
                               BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ANIMATION);
    }

    @Override
    public int getLightBlock(BlockState state, net.minecraft.world.level.BlockGetter level, BlockPos pos) {
        return 0;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, net.minecraft.world.level.BlockGetter level,
                                          BlockPos pos) {
        return true;
    }
}
