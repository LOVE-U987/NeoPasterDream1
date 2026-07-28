package com.pasterdream.pasterdreammod.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * 破风幕帐 — 未着地时沿视线水平弹射（原版 {@code BreakwindCurtainPr0}）。
 */
public class BreakwindCurtainBlock extends Block {

    public BreakwindCurtainBlock() {
        super(BlockBehaviour.Properties.of()
                .sound(SoundType.WOOL)
                .strength(0.1F)
                .noOcclusion()
                .noCollission());
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.block();
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (!entity.onGround()) {
            Vec3 look = entity.getLookAngle();
            entity.setDeltaMovement(new Vec3(look.x * 5.0D, look.y, look.z * 5.0D));
            entity.hurtMarked = true;
        }
    }
}
