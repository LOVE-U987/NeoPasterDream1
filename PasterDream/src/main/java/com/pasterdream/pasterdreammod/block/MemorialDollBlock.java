package com.pasterdream.pasterdreammod.block;

import com.mojang.serialization.MapCodec;
import com.pasterdream.pasterdreammod.block.entity.MemorialDollBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/**
 * 纪念玩偶方块基类 (Memorial Doll Block)
 * <p>
 * 与 qin_doll_0 / golden_fox_sculpture 等方块保持一致：可水平旋转、支持水浸、
 * 使用方块实体渲染 GeckoLib 3D 模型。额外提供“手持物品右击”交互，将物品存入方块实体。
 */
public abstract class MemorialDollBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {

    /**
     * 水平朝向属性
     */
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    /**
     * 水浸属性
     */
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    /**
     * 抱物状态属性（用于客户端同步模型切换）
     */
    public static final BooleanProperty HOLDING = BooleanProperty.create("holding");

    /**
     * 玩偶碰撞箱：中等尺寸，居中对齐
     */
    private static final VoxelShape SHAPE = Block.box(4, 0, 4, 12, 16, 12);

    /**
     * 构造纪念玩偶方块
     *
     * @param properties 方块属性
     */
    protected MemorialDollBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(WATERLOGGED, false)
                .setValue(HOLDING, false));
    }

    /**
     * 获取当前方块的 MapCodec
     *
     * @return 子类定义的 MapCodec
     */
    protected abstract MapCodec<? extends MemorialDollBlock> getCodec();

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return getCodec();
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED, HOLDING);
    }

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

    // ==================== 右键交互：抱住物品 ====================

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.isClientSide) {
            return ItemInteractionResult.SUCCESS;
        }

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof MemorialDollBlockEntity doll)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        // 空手右键：若玩偶正抱着物品，则取下并交还给玩家
        if (stack.isEmpty()) {
            if (!doll.isHolding()) {
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }

            ItemStack heldItem = doll.getHeldItem();
            doll.clearHeldItem();

            if (!player.getInventory().add(heldItem)) {
                player.drop(heldItem, false);
            }

            // 同步 block state，让客户端模型切回空状态
            level.setBlock(pos, state.setValue(HOLDING, false), Block.UPDATE_ALL);

            level.playSound(null, pos, SoundEvents.ARMOR_EQUIP_GENERIC.value(), SoundSource.BLOCKS, 0.6f, 0.8f);
            return ItemInteractionResult.SUCCESS;
        }

        // 已抱物或手持自身方块时禁止放置
        if (doll.isHolding() || stack.getItem() == this.asItem()) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        doll.setHeldItem(stack);
        stack.shrink(1);

        // 同步 block state，让客户端模型切换到抱物状态
        level.setBlock(pos, state.setValue(HOLDING, true), Block.UPDATE_ALL);

        level.playSound(null, pos, SoundEvents.ARMOR_EQUIP_GENERIC.value(), SoundSource.BLOCKS, 0.6f, 1.2f);
        return ItemInteractionResult.SUCCESS;
    }

    // ==================== 破坏时释放被抱物品 ====================

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!level.isClientSide && state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof MemorialDollBlockEntity doll && doll.isHolding()) {
                popResource(level, pos, doll.getHeldItem());
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    // ==================== 方块实体 ====================

    @Nullable
    @Override
    public abstract BlockEntity newBlockEntity(BlockPos pos, BlockState state);
}
