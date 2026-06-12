package com.pasterdream.pasterdreammod.block;

import com.mojang.serialization.MapCodec;
import com.pasterdream.pasterdreammod.block.entity.DyedreamDeskBlockEntity;
import com.pasterdream.pasterdreammod.registry.PDBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import java.util.List;

/**
 * 染梦书桌方块 (Dyedream Desk)
 * 支持方向放置、GUI 交互、物品展示（1 格库存）、水浸属性
 *
 * 交互逻辑：
 * - 右键打开 GUI，放置/取走展示物品
 * - 方块破坏时掉落槽内物品
 * - 支持红石比较器输出
 *
 * 碰撞箱由三个部分组成：
 * - 底座 (3,0,3)-(13,1,13)
 * - 桌身 (4,1,4)-(12,10,12)
 * - 桌面 (2,10,2)-(14,12,14)
 */
public class DyedreamDeskBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
    public static final MapCodec<DyedreamDeskBlock> CODEC = simpleCodec(DyedreamDeskBlock::new);
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    /**
     * 染梦书桌的碰撞箱
     * 由底座、桌身、桌面三个部分组成
     */
    private static final VoxelShape SHAPE = Shapes.or(
            Block.box(3, 0, 3, 13, 1, 13),
            Block.box(4, 1, 4, 12, 10, 12),
            Block.box(2, 10, 2, 14, 12, 14)
    );

    /**
     * 构造染梦书桌方块
     *
     * @param properties 方块属性
     */
    public DyedreamDeskBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(WATERLOGGED, false));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    /**
     * 获取方块的碰撞箱形状
     *
     * @param state   方块状态
     * @param level   世界实例
     * @param pos     方块位置
     * @param context 碰撞上下文
     * @return VoxelShape 碰撞箱
     */
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    /**
     * 设置渲染类型为模型渲染（而非 GeckoLib 动画渲染）
     *
     * @param state 方块状态
     * @return RenderShape.MODEL
     */
    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED);
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
        FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
    }

    // ==================== 水浸支持 ====================

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
        return state.getFluidState().isEmpty();
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
        return 0;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor world, BlockPos currentPos, BlockPos facingPos) {
        if (state.getValue(WATERLOGGED)) {
            world.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
        }
        return super.updateShape(state, facing, facingState, world, currentPos, facingPos);
    }

    // ==================== 方块实体 ====================

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DyedreamDeskBlockEntity(pos, state);
    }

    // ==================== 右键交互 ====================

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide()) return InteractionResult.SUCCESS;

        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof DyedreamDeskBlockEntity desk) {
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.openMenu(desk, pos);
            }
        }
        return InteractionResult.CONSUME;
    }

    // ==================== 方块破坏时掉落物品 ====================

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof DyedreamDeskBlockEntity desk) {
                for (int i = 0; i < desk.getItemHandler().getSlots(); i++) {
                    ItemStack stack = desk.getItemHandler().getStackInSlot(i);
                    if (!stack.isEmpty()) {
                        popResource(level, pos, stack);
                    }
                }
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        return List.of(new ItemStack(this));
    }

    // ==================== 红石比较器支持 ====================

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof DyedreamDeskBlockEntity desk) {
            return net.minecraft.world.inventory.AbstractContainerMenu.getRedstoneSignalFromContainer(
                    new net.minecraft.world.SimpleContainer(desk.getItemHandler().getSlots()) {
                        @Override
                        public net.minecraft.world.item.ItemStack getItem(int index) {
                            return desk.getItemHandler().getStackInSlot(index);
                        }

                        @Override
                        public boolean isEmpty() {
                            return desk.getItemHandler().getStackInSlot(0).isEmpty();
                        }
                    });
        }
        return 0;
    }
}
