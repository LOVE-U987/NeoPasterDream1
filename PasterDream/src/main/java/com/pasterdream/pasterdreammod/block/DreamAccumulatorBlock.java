package com.pasterdream.pasterdreammod.block;

import com.mojang.serialization.MapCodec;
import com.pasterdream.pasterdreammod.block.entity.DreamAccumulatorBlockEntity;
import com.pasterdream.pasterdreammod.registry.PDSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 蓄梦池方块 (Dream Accumulator)
 * 核心功能方块，用于收集梦境能量
 *
 * 特性：
 * - 继承 BaseEntityBlock 实现 EntityBlock 接口
 * - 使用 GeckoLib 渲染动画
 * - 方向性方块（根据玩家朝向放置）
 * - 自定义碰撞箱（扁平形状）
 * <p>
 * 本波次补全原版功能语义：右键打开 2 槽 GUI（产物/吸附剂），
 * 每 40 tick 执行蓄梦逻辑（原版 DreamAccumulatorPr0），
 * 放置/交互伴随 dream1 音效（原版 Pr1/GuiPr2），破坏时掉落库存并输出比较器信号。
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

    // 说明：GeckoLib 动画由渲染器自行驱动；蓄梦逻辑走方块计划刻
    // （原版即 scheduleTick(40) 周期，语义一致，无需 BE ticker）

    // ==================== 周期蓄梦（原版 DreamAccumulatorPr0，每 40 tick） ====================

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        level.scheduleTick(pos, this, 40);
        // 原版 DreamAccumulatorGuiPr2：放置伴随 dream1 音效
        if (!level.isClientSide()) {
            level.playSound(null, pos, PDSounds.DREAM1.get(), SoundSource.NEUTRAL, 0.8f, 1);
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.tick(state, level, pos, random);
        if (level.getBlockEntity(pos) instanceof DreamAccumulatorBlockEntity accumulator) {
            accumulator.accumulateTick();
        }
        level.scheduleTick(pos, this, 40);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
        super.setPlacedBy(level, pos, state, entity, stack);
        // 原版 DreamAccumulatorPr1：放置时蓄梦计时归零 + dream1 音效
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof DreamAccumulatorBlockEntity accumulator) {
            accumulator.resetTime();
        }
    }

    // ==================== 右键交互 ====================

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hitResult) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        if (level.getBlockEntity(pos) instanceof DreamAccumulatorBlockEntity accumulator
                && player instanceof ServerPlayer serverPlayer) {
            serverPlayer.openMenu(accumulator, pos);
            // 原版 DreamAccumulatorGuiPr0：初见馈赠寻梦者笔记（可能立即关闭界面）
            accumulator.giveIntroNotes(serverPlayer);
        }
        // 原版 DreamAccumulatorGuiPr2：交互伴随 dream1 音效
        level.playSound(null, pos, PDSounds.DREAM1.get(), SoundSource.NEUTRAL, 0.8f, 1);
        return InteractionResult.CONSUME;
    }

    @Override
    public boolean triggerEvent(BlockState state, Level level, BlockPos pos, int eventID, int eventParam) {
        super.triggerEvent(state, level, pos, eventID, eventParam);
        BlockEntity blockEntity = level.getBlockEntity(pos);
        return blockEntity != null && blockEntity.triggerEvent(eventID, eventParam);
    }

    // ==================== 移除掉落与比较器 ====================

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            if (level.getBlockEntity(pos) instanceof DreamAccumulatorBlockEntity accumulator) {
                ItemStackHandler handler = accumulator.getItemHandler();
                for (int i = 0; i < handler.getSlots(); i++) {
                    Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), handler.getStackInSlot(i));
                }
                level.updateNeighbourForOutputSignal(pos, this);
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof DreamAccumulatorBlockEntity accumulator) {
            return WeaponWorkshopBlock.calcRedstoneFromItemHandler(accumulator.getItemHandler());
        }
        return 0;
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

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        return List.of(new ItemStack(this));
    }
}
