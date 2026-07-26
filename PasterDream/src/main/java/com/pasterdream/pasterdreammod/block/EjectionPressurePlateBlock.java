package com.pasterdream.pasterdreammod.block;

import com.pasterdream.pasterdreammod.util.ServerScheduler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/**
 * 弹射压力板 (ejection_pressure_plate)
 * <p>
 * 原版 {@code EjectionPressurePlateBlock} + {@code EjectionPressurePlatePr0Procedure}：
 * 实体踩上后延迟 2 tick 赋予向上 0.8 的速度，并设置 fallDistance=3。
 */
public class EjectionPressurePlateBlock extends PressurePlateBlock {

    public EjectionPressurePlateBlock(BlockSetType type, Properties properties) {
        super(type, properties);
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        super.entityInside(state, level, pos, entity);
        if (level.isClientSide()) {
            return;
        }
        entity.fallDistance = 3;
        ServerScheduler.schedule(2, () -> {
            if (entity.isAlive()) {
                entity.setDeltaMovement(new Vec3(0, 0.8, 0));
                entity.hurtMarked = true;
            }
        });
    }
}
