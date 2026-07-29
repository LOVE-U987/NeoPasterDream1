package com.pasterdream.pasterdreammod.block;

import com.mojang.serialization.MapCodec;
import com.pasterdream.pasterdreammod.api.block.HorizontalWaterloggedBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

/**
 * 圣诞彩灯 (christmas_lights)
 * <p>
 * 贴墙装饰灯串，水平朝向 + 可含水，发光等级 12，无碰撞体积。
 * 形状为贴在方块面上的 1 像素厚灯串层（还原自原版 ChristmasLightsBlock）。
 */
public class ChristmasLightsBlock extends HorizontalWaterloggedBlock {

    /** 方块 MapCodec（1.21 数据驱动序列化要求） */
    public static final MapCodec<ChristmasLightsBlock> CODEC = simpleCodec(ChristmasLightsBlock::new);

    /**
     * 构造圣诞彩灯
     *
     * @param properties 方块属性
     */
    public ChristmasLightsBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    /**
     * 按朝向返回贴墙形状（与原版 switch 分支一一对应，default 覆盖 SOUTH）
     */
    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level,
                                        @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case NORTH -> box(0, 0, 15, 16, 16, 16);
            case EAST -> box(0, 0, 0, 1, 16, 16);
            case WEST -> box(15, 0, 0, 16, 16, 16);
            default -> box(0, 0, 0, 16, 16, 1);
        };
    }
}
