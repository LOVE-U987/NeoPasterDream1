package com.pasterdream.pasterdreammod.block;

import com.mojang.serialization.MapCodec;
import com.pasterdream.pasterdreammod.registry.PDEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/**
 * 小石灵方块 (small_stone_spirit_block)
 * <p>
 * 风之旅途维度地表装饰方块。玩家破坏时召唤小石灵实体并播放灰烬/云粒子
 * （原版 {@code SmallStoneSpiritBlockPr0Procedure}）。
 */
public class SmallStoneSpiritBlock extends HorizontalDirectionalBlock {

    public static final MapCodec<SmallStoneSpiritBlock> CODEC = simpleCodec(SmallStoneSpiritBlock::new);
    private static final VoxelShape SHAPE = box(2, 0, 2, 14, 4, 14);

    public SmallStoneSpiritBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, net.minecraft.core.Direction.NORTH));
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

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    /**
     * 玩家破坏时召唤小石灵（原 SmallStoneSpiritBlockPr0Procedure）。
     */
    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos,
                                       Player player, boolean willHarvest, FluidState fluid) {
        boolean result = super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
        if (level instanceof ServerLevel serverLevel) {
            spawnSpirit(serverLevel, pos);
        }
        return result;
    }

    private static void spawnSpirit(ServerLevel level, BlockPos pos) {
        double x = pos.getX();
        double y = pos.getY();
        double z = pos.getZ();
        Entity entity = PDEntities.SMALL_STONE_SPIRIT.get().spawn(level,
                BlockPos.containing(x + 0.5, y + 0.5, z + 0.5),
                MobSpawnType.MOB_SUMMONED);
        // 原版 spawn 后未设置朝向；保持一致
        if (entity != null) {
            // no-op：占位以便未来补朝向/属性
        }
        level.sendParticles(ParticleTypes.ASH, x + 0.5, y + 0.5, z + 0.5, 12, 0.5, 0.5, 0.5, 0.05);
        level.sendParticles(ParticleTypes.CLOUD, x + 0.5, y + 0.5, z + 0.5, 12, 0.5, 0.5, 0.5, 0.05);
    }
}
