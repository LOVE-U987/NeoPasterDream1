package com.pasterdream.pasterdreammod.block.entity;

import com.pasterdream.pasterdreammod.api.blockentity.base.FreeDataBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * W4 波次通用数据方块实体 —— 存档键 {@code PDData}（兼容既有世界）。
 * <p>
 * 实现见 API {@link FreeDataBlockEntity}。服务 structure_block、shadow_bed、
 * claypan、guard/restrainmove、lost_sword 等注册名，由 {@link BlockEntityType} 区分。
 */
public class W4DataBlockEntity extends FreeDataBlockEntity {

    public W4DataBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    protected String persistentDataKey() {
        return "PDData";
    }
}
