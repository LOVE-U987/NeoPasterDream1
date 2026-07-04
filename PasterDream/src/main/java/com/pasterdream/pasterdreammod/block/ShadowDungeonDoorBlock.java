package com.pasterdream.pasterdreammod.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

/**
 * 暗影地牢门方块 (Shadow Dungeon Door Block)
 * 不可破坏的竞技场门方块，具有特殊碰撞箱
 * door_0/1 为薄板状（水平中间），door_2/3 为整高方块（Z轴中间薄）
 */
public class ShadowDungeonDoorBlock extends Block {

    /** 门的碰撞箱（通过构造函数传入） */
    private final VoxelShape shape;

    /**
     * 构造暗影地牢门方块
     * @param properties 方块属性
     * @param shape 碰撞箱形状
     */
    public ShadowDungeonDoorBlock(BlockBehaviour.Properties properties, VoxelShape shape) {
        super(properties);
        this.shape = shape;
    }

    /**
     * 获取碰撞箱形状
     * @param state 方块状态
     * @param level 世界读取器
     * @param pos 方块位置
     * @param context 碰撞上下文
     * @return 碰撞箱形状
     */
    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level,
                                         @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return shape;
    }
}
