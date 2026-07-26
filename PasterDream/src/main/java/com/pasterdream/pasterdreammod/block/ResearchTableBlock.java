package com.pasterdream.pasterdreammod.block;

import com.mojang.serialization.MapCodec;
import com.pasterdream.pasterdreammod.block.entity.ResearchTableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 研究台方块 (Research Table)
 * <p>
 * GeckoLib 3D 模型（ENTITYBLOCK_ANIMATED），水平朝向 + ANIMATION(0-1) 状态；
 * 右键打开研究 GUI（复制/研究两个按钮，语义见
 * {@link ResearchTableBlockEntity}）；破坏时掉落库存并输出比较器信号。
 * 逐朝向碰撞箱与原版 ResearchTableBlock 完全一致（模型宽于 1 格）。
 */
public class ResearchTableBlock extends BaseEntityBlock {

    public static final MapCodec<ResearchTableBlock> CODEC = simpleCodec(ResearchTableBlock::new);
    /** 动画状态（原版 0-1；"0" 为空闲循环） */
    public static final IntegerProperty ANIMATION = IntegerProperty.create("animation", 0, 1);
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    /**
     * 构造研究台方块
     *
     * @param properties 方块属性
     */
    public ResearchTableBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(ANIMATION, 0)
                .setValue(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ResearchTableBlockEntity(pos, state);
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
        return true;
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
        return 0;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        // 原版逐朝向碰撞箱（桌面比 1 格更宽）
        return switch (state.getValue(FACING)) {
            case NORTH -> box(-4, 0, 3, 21, 15, 16);
            case EAST -> box(0, 0, -4, 13, 15, 21);
            case WEST -> box(3, 0, -5, 16, 15, 20);
            default -> box(-5, 0, 0, 20, 15, 13);
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ANIMATION, FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        List<ItemStack> drops = super.getDrops(state, builder);
        if (!drops.isEmpty()) {
            return drops;
        }
        return List.of(new ItemStack(this));
    }

    // ==================== 右键交互 ====================

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hitResult) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        if (level.getBlockEntity(pos) instanceof ResearchTableBlockEntity table
                && player instanceof ServerPlayer serverPlayer) {
            serverPlayer.openMenu(table, pos);
        }
        return InteractionResult.CONSUME;
    }

    @Override
    public boolean triggerEvent(BlockState state, Level level, BlockPos pos, int eventID, int eventParam) {
        super.triggerEvent(state, level, pos, eventID, eventParam);
        BlockEntity blockEntity = level.getBlockEntity(pos);
        return blockEntity != null && blockEntity.triggerEvent(eventID, eventParam);
    }

    // ==================== 移除掉落 ====================

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            if (level.getBlockEntity(pos) instanceof ResearchTableBlockEntity table) {
                ItemStackHandler handler = table.getItemHandler();
                for (int i = 0; i < handler.getSlots(); i++) {
                    Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), handler.getStackInSlot(i));
                }
                level.updateNeighbourForOutputSignal(pos, this);
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    // ==================== 红石比较器 ====================

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof ResearchTableBlockEntity table) {
            // 按 vanilla 容器公式计算比较器信号（复用工坊的通用实现）
            return WeaponWorkshopBlock.calcRedstoneFromItemHandler(table.getItemHandler());
        }
        return 0;
    }
}
