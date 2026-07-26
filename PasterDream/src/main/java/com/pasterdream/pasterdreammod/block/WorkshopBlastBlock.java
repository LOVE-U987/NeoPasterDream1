package com.pasterdream.pasterdreammod.block;

import com.mojang.serialization.MapCodec;
import com.pasterdream.pasterdreammod.block.entity.WorkshopBlastBlockEntity;
import com.pasterdream.pasterdreammod.registry.PDBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
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
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

/**
 * 工坊锻炉方块 (Workshop Blast)
 * <p>
 * 武器工坊群卫星工位之一：外观由精铸工坊核心统一渲染，本方块使用
 * 全透明占位模型。右键打开煅烧 GUI（5 槽 + 岩浆储罐显示）；
 * 原版通过 {@code scheduleTick(10)} 自续期驱动 WorkshopBlastPr1
 * （岩浆桶注入/入炉出炉/粒子音效），新版改为服务端 ticker 每 10 tick
 * 触发一次 {@link WorkshopBlastBlockEntity#tickBlast()}，节奏一致。
 */
public class WorkshopBlastBlock extends BaseEntityBlock {

    public static final MapCodec<WorkshopBlastBlock> CODEC = simpleCodec(WorkshopBlastBlock::new);
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    /**
     * 构造工坊锻炉方块
     *
     * @param properties 方块属性
     */
    public WorkshopBlastBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        // 占位 JSON 模型（air 纹理），实际外观由精铸工坊核心渲染
        return RenderShape.MODEL;
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
        // 与原版一致：完全阻挡光线
        return 15;
    }

    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        // 原版逐朝向碰撞箱（炉体 + 烟囱）
        return switch (state.getValue(FACING)) {
            case NORTH -> Shapes.or(box(8, 3, 8, 32, 14, 32), box(12, 15, 12, 30, 30, 28));
            case EAST -> Shapes.or(box(-16, 3, 8, 8, 14, 32), box(-12, 15, 12, 4, 30, 30));
            case WEST -> Shapes.or(box(8, 3, -16, 32, 14, 8), box(12, 15, -14, 28, 30, 4));
            default -> Shapes.or(box(-16, 3, -16, 8, 14, 8), box(-14, 15, -12, 4, 30, 4));
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
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

    // ==================== 方块实体与 ticker ====================

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new WorkshopBlastBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        // 煅烧流程只在服务端推进（原版 scheduleTick(10) 的等价节奏）
        if (level.isClientSide()) {
            return null;
        }
        return createTickerHelper(type, PDBlockEntities.WORKSHOP_BLAST.get(), (lvl, pos, st, blast) -> {
            if (lvl.getGameTime() % 10L == 0L) {
                blast.tickBlast();
            }
        });
    }

    // ==================== 右键交互 ====================

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hitResult) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        if (level.getBlockEntity(pos) instanceof WorkshopBlastBlockEntity blast
                && player instanceof ServerPlayer serverPlayer) {
            serverPlayer.openMenu(blast, pos);
        }
        return InteractionResult.CONSUME;
    }

    @Override
    public boolean triggerEvent(BlockState state, Level world, BlockPos pos, int eventID, int eventParam) {
        super.triggerEvent(state, world, pos, eventID, eventParam);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        return blockEntity != null && blockEntity.triggerEvent(eventID, eventParam);
    }

    // ==================== 移除掉落与比较器 ====================

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            if (world.getBlockEntity(pos) instanceof WorkshopBlastBlockEntity blast) {
                ItemStackHandler handler = blast.getItemHandler();
                for (int i = 0; i < handler.getSlots(); i++) {
                    Containers.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), handler.getStackInSlot(i));
                }
                world.updateNeighbourForOutputSignal(pos, this);
            }
            super.onRemove(state, world, pos, newState, isMoving);
        }
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos) {
        if (world.getBlockEntity(pos) instanceof WorkshopBlastBlockEntity blast) {
            return WeaponWorkshopBlock.calcRedstoneFromItemHandler(blast.getItemHandler());
        }
        return 0;
    }
}
