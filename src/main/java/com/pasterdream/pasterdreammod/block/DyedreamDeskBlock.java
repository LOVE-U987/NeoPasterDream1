package com.pasterdream.pasterdreammod.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.storage.loot.LootParams;
import java.util.List;

/**
 * 染梦书桌方块 (Dyedream Desk)
 * 方向性方块，玩家放置时根据朝向旋转
 *
 * 原模组特性（已简化）：
 * - Block + EntityBlock + SimpleWaterloggedBlock → 简化为 HorizontalDirectionalBlock
 * - TileEntity (DyedreamDeskBlockEntity) → 移除（暂不实现）
 * - GUI (DyedreamDeskGuiMenu) → 移除（暂不实现）
 * - WATERLOGGED 属性 → 移除（暂不支持水中放置）
 * - 复杂碰撞箱 → 保留简化版本
 *
 * 当前实现：
 * - 方向性方块（根据玩家朝向放置）
 * - 自定义碰撞箱（桌子形状：底座+桌身+桌面）
 */
public class DyedreamDeskBlock extends HorizontalDirectionalBlock {

    /**
     * MapCodec 用于序列化/反序列化方块状态
     */
    public static final MapCodec<DyedreamDeskBlock> CODEC = simpleCodec(DyedreamDeskBlock::new);

    /**
     * 染梦书桌的碰撞箱定义
     * 由三个部分组成：底座、桌身、桌面
     */
    private static final VoxelShape SHAPE = Shapes.or(
            Block.box(3, 0, 3, 13, 1, 13),   // 底座
            Block.box(4, 1, 4, 12, 10, 12),   // 桌身
            Block.box(2, 10, 2, 14, 12, 14)    // 桌面
    );

    /**
     * 构造染梦书桌方块
     *
     * @param properties 方块属性
     */
    public DyedreamDeskBlock(Properties properties) {
        super(properties);
        // 注册默认状态：朝向北方
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    /**
     * 获取方块的 MapCodec
     *
     * @return MapCodec 实例
     */
    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    /**
     * 获取方块的碰撞箱形状
     *
     * @param state 方块状态
     * @param level 世界实例
     * @param pos 方块位置
     * @param context 碰撞上下文
     * @return VoxelShape 碰撞箱
     */
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    /**
     * 创建方块状态定义
     * 注册 FACING 属性
     *
     * @param builder 状态构建器
     */
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
    }

    /**
     * 获取放置时的方块状态
     * 根据玩家的水平朝向设置方块方向（相反方向）
     *
     * @param context 放置上下文
     * @return 放置后的方块状态
     */
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        return List.of(new ItemStack(this));
    }
}
