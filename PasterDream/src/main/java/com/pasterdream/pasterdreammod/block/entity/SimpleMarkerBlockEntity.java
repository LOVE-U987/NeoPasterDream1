package com.pasterdream.pasterdreammod.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 原版 MCreator 空壳 BE（0 槽/无逻辑）的 1.21 等价物，仅满足注册闭包与 NBT 同步位。
 */
public class SimpleMarkerBlockEntity extends BlockEntity {
    public SimpleMarkerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
}
