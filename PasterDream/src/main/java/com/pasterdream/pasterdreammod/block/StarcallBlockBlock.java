package com.pasterdream.pasterdreammod.block;

import com.pasterdream.pasterdreammod.registry.PDParticles;
import com.pasterdream.pasterdreammod.util.ServerScheduler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * 唤星照明（starcall_block）
 * <p>
 * 原版限时光点方块：法球命中后置于落点 y+2。对齐 {@code StarcallBlockBlock} + Pr0/Pr1：
 * <ul>
 *   <li>放置后每 40 tick 在中心喷唤星粒子</li>
 *   <li>存活 1200 tick（60s）后自毁</li>
 *   <li>一触即破、可含水、无碰撞、发光 15、可被其它方块替换</li>
 * </ul>
 */
public class StarcallBlockBlock extends Block implements SimpleWaterloggedBlock {

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    /** 中心 2×2×2 像素点 */
    private static final VoxelShape SHAPE = Block.box(7.0D, 7.0D, 7.0D, 9.0D, 9.0D, 9.0D);

    private static final int TICK_INTERVAL = 40;
    private static final int LIFETIME_TICKS = 1200;

    /**
     * 构造唤星照明：紫水晶芽音效、瞬破、全亮、无碰撞。
     */
    public StarcallBlockBlock() {
        super(BlockBehaviour.Properties.of()
                .instrument(NoteBlockInstrument.HAT)
                .sound(SoundType.MEDIUM_AMETHYST_BUD)
                .instabreak()
                .lightLevel(s -> 15)
                .noCollission()
                .noOcclusion()
                .hasPostProcess((bs, br, bp) -> true)
                .emissiveRendering((bs, br, bp) -> true)
                .isRedstoneConductor((bs, br, bp) -> false));
        this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, false));
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
        return state.getFluidState().isEmpty();
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
        return 0;
    }

    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        boolean water = context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER;
        return this.defaultBlockState().setValue(WATERLOGGED, water);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState,
                                   LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
    }

    @Override
    public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        return context.getItemInHand().getItem() != this.asItem();
    }

    /**
     * 放置：40tick 粒子节奏 + 1200tick 自毁 + 初始粒子爆发。
     */
    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean moving) {
        super.onPlace(state, level, pos, oldState, moving);
        if (level.isClientSide()) {
            return;
        }
        level.scheduleTick(pos, this, TICK_INTERVAL);
        final BlockPos locked = pos.immutable();
        ServerScheduler.schedule(LIFETIME_TICKS, () -> {
            if (level instanceof ServerLevel server
                    && server.getBlockState(locked).is(this)) {
                server.removeBlock(locked, false);
            }
        });
        if (level instanceof ServerLevel server) {
            server.sendParticles(
                    (SimpleParticleType) PDParticles.STARCALL_PARTICLE.particleType(),
                    locked.getX(), locked.getY(), locked.getZ(),
                    8, 3.0D, 3.0D, 3.0D, 0.1D);
        }
    }

    /**
     * 周期 tick：唤星粒子（原版 Pr0），并重排。
     */
    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.tick(state, level, pos, random);
        level.sendParticles(
                (SimpleParticleType) PDParticles.STARCALL_PARTICLE.particleType(),
                pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D,
                7, 2.0D, 2.0D, 2.0D, 0.01D);
        if (level.getBlockState(pos).is(this)) {
            level.scheduleTick(pos, this, TICK_INTERVAL);
        }
    }
}
