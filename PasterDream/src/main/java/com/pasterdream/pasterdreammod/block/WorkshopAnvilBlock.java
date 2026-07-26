package com.pasterdream.pasterdreammod.block;

import com.mojang.serialization.MapCodec;
import com.pasterdream.pasterdreammod.block.entity.WorkshopAnvilBlockEntity;
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
 * 工坊铁砧方块 (Workshop Anvil)
 * <p>
 * 武器工坊群卫星工位之一：外观由精铸工坊核心的 GeckoLib 模型统一渲染，
 * 本方块使用全透明占位模型，仅保留碰撞箱与交互。
 * 右键打开锤炼小游戏 GUI（原版经 NetworkHooks.openScreen，
 * 新版由 BE 作为 MenuProvider 直接 openMenu）；
 * 原版通过 {@code scheduleTick(10)} 自续期驱动
 * WorkshopAnvilPr1（结算计数），新版改为服务端 ticker 每 10 tick 触发一次
 * {@link WorkshopAnvilBlockEntity#tickGame()}，节奏一致。
 */
public class WorkshopAnvilBlock extends BaseEntityBlock {

    public static final MapCodec<WorkshopAnvilBlock> CODEC = simpleCodec(WorkshopAnvilBlock::new);
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    /**
     * 构造工坊铁砧方块
     *
     * @param properties 方块属性
     */
    public WorkshopAnvilBlock(Properties properties) {
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
        // 原版逐朝向碰撞箱
        return switch (state.getValue(FACING)) {
            case NORTH -> box(1, 3, 1, 9, 17, 17);
            case EAST -> box(-1, 3, 1, 15, 17, 9);
            case WEST -> box(1, 3, 7, 17, 17, 15);
            default -> box(7, 3, -1, 15, 17, 15);
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
        return new WorkshopAnvilBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        // 小游戏结算只在服务端推进（原版 scheduleTick(10) 的等价节奏）
        if (level.isClientSide()) {
            return null;
        }
        return createTickerHelper(type, PDBlockEntities.WORKSHOP_ANVIL.get(), (lvl, pos, st, anvil) -> {
            if (lvl.getGameTime() % 10L == 0L) {
                anvil.tickGame();
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
        if (level.getBlockEntity(pos) instanceof WorkshopAnvilBlockEntity anvil
                && player instanceof ServerPlayer serverPlayer) {
            serverPlayer.openMenu(anvil, pos);
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
            if (world.getBlockEntity(pos) instanceof WorkshopAnvilBlockEntity anvil) {
                ItemStackHandler handler = anvil.getItemHandler();
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
        if (world.getBlockEntity(pos) instanceof WorkshopAnvilBlockEntity anvil) {
            return WeaponWorkshopBlock.calcRedstoneFromItemHandler(anvil.getItemHandler());
        }
        return 0;
    }
}
