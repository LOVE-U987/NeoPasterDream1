package com.pasterdream.pasterdreammod.block.entity;

import com.pasterdream.pasterdreammod.api.blockentity.base.GeoFreeDataBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * W4 波次通用 GeckoLib 数据方块实体（双控制器 + {@code PDData}）。
 * <p>
 * 实现见 API {@link GeoFreeDataBlockEntity}。用于 birds_nest、twilight_lantern、
 * wind_knight_spawnblock 等 ENTITYBLOCK_ANIMATED 方块。
 */
public class W4GeoDataBlockEntity extends GeoFreeDataBlockEntity {

    public W4GeoDataBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    protected String persistentDataKey() {
        return "PDData";
    }
}
