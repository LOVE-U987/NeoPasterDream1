package com.pasterdream.pasterdreammod.block;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * 染梦荷叶方块——仿原生 LilyPadBlock 实现。
 * 继承 BushBlock，只能放置在水面上，提供 1.5 像素高的碰撞箱。
 * 与原生 LilyPadBlock 的区别：额外实现了 entityInside 防沉没逻辑，
 * 防止实体穿过薄碰撞箱掉入水中。
 */
public class DyedreamLilyPadBlock extends BushBlock {
    public static final MapCodec<DyedreamLilyPadBlock> CODEC = simpleCodec(properties -> new DyedreamLilyPadBlock());

    protected static final VoxelShape SHAPE = box(0.0, 0.0, 0.0, 16.0, 1.5, 16.0);

    public DyedreamLilyPadBlock() {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.PLANT)
                .strength(0.5f)
                .noOcclusion()
                .offsetType(BlockBehaviour.OffsetType.XZ)
                .dynamicShape()
                .sound(SoundType.LILY_PAD)
                .pushReaction(PushReaction.DESTROY));
    }

    @Override
    protected MapCodec<? extends BushBlock> codec() {
        return CODEC;
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        // 原版 LilyPadBlock 逻辑：下方有水即可放置
        return state.is(Blocks.WATER) || state.getFluidState().is(Fluids.WATER);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos below = pos.below();
        // 绕过 Forge/NeoForge 的 canSustainPlant 补丁，直接使用 mayPlaceOn 检查
        return this.mayPlaceOn(level.getBlockState(below), level, below);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        // 防沉没逻辑：将实体轻轻托在荷叶表面
        double topY = pos.getY() + 1.5 / 16.0;
        if (entity.getY() >= topY - 0.1) {
            Vec3 motion = entity.getDeltaMovement();
            if (motion.y < -0.08) {
                entity.setDeltaMovement(motion.x, Math.min(motion.y * 0.5, -0.04), motion.z);
            }
            if (entity.getY() < topY) {
                double lift = topY - entity.getY();
                entity.move(MoverType.SELF, new Vec3(0, lift, 0));
                entity.setDeltaMovement(
                        entity.getDeltaMovement().x,
                        Math.max(entity.getDeltaMovement().y, 0),
                        entity.getDeltaMovement().z
                );
            }
        }
    }
}