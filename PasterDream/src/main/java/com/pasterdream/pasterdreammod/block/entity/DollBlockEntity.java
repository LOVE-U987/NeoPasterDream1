package com.pasterdream.pasterdreammod.block.entity;

import com.pasterdream.pasterdreammod.api.doll.DollAPI;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 通用玩偶方块实体
 * <p>
 * 继承 {@link MemorialDollBlockEntity}，根据当前方块状态自动解析对应的方块实体类型。
 */
public class DollBlockEntity extends MemorialDollBlockEntity {

    /**
     * 构造通用玩偶方块实体
     * <p>
     * 该构造函数供 {@link BlockEntityType.Builder#of} 使用，自动从 {@link DollAPI} 查询类型。
     *
     * @param pos   方块位置
     * @param state 方块状态
     */
    public DollBlockEntity(BlockPos pos, BlockState state) {
        super(resolveType(state), pos, state);
    }

    private static BlockEntityType<?> resolveType(BlockState state) {
        return DollAPI.getBlockEntityType(state.getBlock()).orElseThrow(
                () -> new IllegalStateException("[DollBlockEntity] 无法为方块 " + state.getBlock() + " 找到对应的方块实体类型"));
    }
}
