package com.pasterdream.pasterdreammod.block.entity;

import com.pasterdream.pasterdreammod.registry.PDBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 琴雨梦纪念玩偶方块实体 (Love U Doll Block Entity)
 * <p>
 * 继承纪念玩偶方块实体基类，仅绑定到 {@link PDBlockEntities#LOVE_U_DOLL}。
 */
public class LoveUDollBlockEntity extends MemorialDollBlockEntity {

    /**
     * 构造琴雨梦纪念玩偶方块实体
     *
     * @param pos   方块位置
     * @param state 方块状态
     */
    public LoveUDollBlockEntity(BlockPos pos, BlockState state) {
        super(PDBlockEntities.LOVE_U_DOLL.get(), pos, state);
    }
}
