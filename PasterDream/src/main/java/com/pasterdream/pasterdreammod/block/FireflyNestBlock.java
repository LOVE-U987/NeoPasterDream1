package com.pasterdream.pasterdreammod.block;

import com.pasterdream.pasterdreammod.block.entity.W4DataBlockEntity;
import com.pasterdream.pasterdreammod.registry.PDBlockEntities;
import com.pasterdream.pasterdreammod.registry.PDEntities;
import com.pasterdream.pasterdreammod.registry.PDParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/**
 * 萤火虫巢 (firefly_nest)
 * <p>
 * 风之旅途维度地表方块。日间 randomTick 将 BE 标记 {@code firefly_nest=true}，
 * 夜间若标记为真则召唤一只萤火虫并清除标记，同时播放射线粒子。
 * 还原自原版 {@code FireflyNestBlock} + {@code FireflyNestPr0Procedure}。
 */
public class FireflyNestBlock extends Block implements SimpleWaterloggedBlock, EntityBlock {

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private static final VoxelShape SHAPE = box(4, 0, 4, 12, 6, 12);
    private static final String TAG_READY = "firefly_nest";

    public FireflyNestBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        boolean water = context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER;
        return this.defaultBlockState().setValue(WATERLOGGED, water);
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState,
                                     LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new W4DataBlockEntity(PDBlockEntities.FIREFLY_NEST.get(), pos, state);
    }

    /**
     * 原版 MCreator 把逻辑写在 {@code tick} 且挂了 {@code randomTicks}；
     * 1.21 侧用 {@link #randomTick} 承接等价的随机刻触发。
     */
    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        tickNest(level, pos);
    }

    /** 日间蓄能 / 夜间放飞（原 FireflyNestPr0Procedure） */
    private static void tickNest(ServerLevel level, BlockPos pos) {
        double x = pos.getX();
        double y = pos.getY();
        double z = pos.getZ();

        if (level.isDay()) {
            W4DataBlockEntity.putBooleanAt(level, pos, TAG_READY, true);
            return;
        }

        if (W4DataBlockEntity.getBooleanAt(level, pos, TAG_READY)) {
            Entity firefly = PDEntities.FIREFLY.get().spawn(level,
                    BlockPos.containing(x + 0.5, y + 0.6, z + 0.5),
                    MobSpawnType.MOB_SUMMONED);
            if (firefly != null) {
                firefly.setYRot(level.getRandom().nextFloat() * 360F);
            }
            W4DataBlockEntity.putBooleanAt(level, pos, TAG_READY, false);
        }

        level.sendParticles(
                (SimpleParticleType) PDParticles.FIREFLY_PARTICLE.particleType(),
                x + 0.5, y + 0.4, z + 0.5,
                5, 0.5, 0.5, 0.5, 0.02);
    }

    @Override
    public boolean triggerEvent(BlockState state, Level level, BlockPos pos, int id, int param) {
        super.triggerEvent(state, level, pos, id, param);
        BlockEntity be = level.getBlockEntity(pos);
        return be != null && be.triggerEvent(id, param);
    }
}
