package com.pasterdream.pasterdreammod.block.entity;

import com.pasterdream.pasterdreammod.registry.PDBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 幼幼紫纪念玩偶方块实体 (Eoul Doll Block Entity)
 * <p>
 * 继承纪念玩偶方块实体基类，仅绑定到 {@link PDBlockEntities#EOUL_DOLL}。
 */
public class EoulDollBlockEntity extends MemorialDollBlockEntity {

    /**
     * 构造幼幼紫纪念玩偶方块实体
     *
     * @param pos   方块位置
     * @param state 方块状态
     */
    public EoulDollBlockEntity(BlockPos pos, BlockState state) {
        super(PDBlockEntities.EOUL_DOLL.get(), pos, state);
    }
}
