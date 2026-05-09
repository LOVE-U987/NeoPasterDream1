package com.pasterdream.pasterdreammod.block;

import com.mojang.serialization.MapCodec;
import com.pasterdream.pasterdreammod.block.entity.DreamAccumulatorBlockEntity;
import com.pasterdream.pasterdreammod.registry.PDBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/**
 * 蓄梦池方块 (Dream Accumulator)
 * 核心功能方块，用于收集梦境能量
 *
 * 特性：
 * - 继承 BaseEntityBlock 实现 EntityBlock 接口
 * - 使用 GeckoLib 渲染动画
 * - 方向性方块（根据玩家朝向放置）
 * - 自定义碰撞箱（扁平形状）
 */
public class DreamAccumulatorBlock extends BaseEntityBlock {

    /**
     * MapCodec 用于序列化/反序列化方块状态
     */
    public static final MapCodec<DreamAccumulatorBlock> CODEC = simpleCodec(DreamAccumulatorBlock::new);

    /**
     * 蓄梦池的碰撞箱定义
     * 扁平的台面形状，高度为 4 像素
     */
    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 4, 16);

    /**
     * 构造蓄梦池方块
     *
     * @param properties 方块属性
     */
    public DreamAccumulatorBlock(Properties properties) {
        super(properties);
        // 注册默认状态：朝向北方
        this.registerDefaultState(this.stateDefinition.any().setValue(HorizontalDirectionalBlock.FACING, Direction.NORTH));
    }

    /**
     * 获取方块的 MapCodec
     *
     * @return MapCodec 实例
     */
    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    /**
     * 获取方块的渲染形状
     * 返回 ENTITYBLOCK_ANIMATED 以使用 BlockEntityRenderer 进行渲染
     *
     * @param state 方块状态
     * @return RenderShape 渲染形状
     */
    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    /**
     * 创建方块实体
     * 当方块被放置时调用
     *
     * @param pos 方块位置
     * @param state 方块状态
     * @return BlockEntity 方块实体实例
     */
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DreamAccumulatorBlockEntity(pos, state);
    }

    /**
     * 获取方块实体Ticker
     * 用于客户端动画更新
     *
     * @param level 世界实例
     * @param state 方块状态
     * @param blockEntityType 方块实体类型
     * @return BlockEntityTicker 实例
     */
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return createTickerHelper(blockEntityType, PDBlockEntities.DREAM_ACCUMULATOR.get(),
                (lvl, pos, blockState, blockEntity) -> {
                    // 客户端动画更新
                    if (lvl.isClientSide) {
                        // GeckoLib 动画自动处理
                    }
                });
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
        builder.add(HorizontalDirectionalBlock.FACING);
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
                .setValue(HorizontalDirectionalBlock.FACING, context.getHorizontalDirection().getOpposite());
    }
}
