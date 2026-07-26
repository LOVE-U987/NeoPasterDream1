package com.pasterdream.pasterdreammod.block;

import com.mojang.serialization.MapCodec;
import com.pasterdream.pasterdreammod.block.entity.ShadowBlastFurnaceBlockEntity;
import com.pasterdream.pasterdreammod.registry.PDBlockEntities;
import com.pasterdream.pasterdreammod.registry.PDSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
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
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 暗影高炉方块 (Shadow Blast Furnace)
 * <p>
 * GeckoLib 3D 模型（ENTITYBLOCK_ANIMATED），ANIMATION(0-4) + WORKING 状态；
 * 由暗影高炉核心的多方块结构搭建产生（见 {@link ShadowBlastFurnaceCoreBlock}）。
 * 右键播放 machine3 音效并把动画置为 2（原版 ShadowBlastFurnacePr1），随后打开
 * 冶炼 GUI；放置时播放 machine0 音效并把动画置为 3（原版 ShadowBlastFurnacePr0）。
 * 双端 ticker：客户端工作特效 / 服务端冶炼状态机（见
 * {@link ShadowBlastFurnaceBlockEntity}）。
 */
public class ShadowBlastFurnaceBlock extends BaseEntityBlock {

    public static final MapCodec<ShadowBlastFurnaceBlock> CODEC = simpleCodec(ShadowBlastFurnaceBlock::new);
    /** 动画状态（原版 0-4；"0" 为空闲循环） */
    public static final IntegerProperty ANIMATION = IntegerProperty.create("animation", 0, 4);
    /** 是否正在冶炼（驱动客户端工作特效与音效） */
    public static final BooleanProperty WORKING = BooleanProperty.create("working");

    /**
     * 构造暗影高炉方块
     *
     * @param properties 方块属性
     */
    public ShadowBlastFurnaceBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(WORKING, false)
                .setValue(ANIMATION, 0));
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
        return new ShadowBlastFurnaceBlockEntity(pos, state);
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
        // 原版说明：碰撞箱抬高到 16 以匹配工作面，后续版本再考虑多方块化
        return box(0, 0, 0, 16, 16, 16);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ANIMATION);
        builder.add(WORKING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState();
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        List<ItemStack> drops = super.getDrops(state, builder);
        if (!drops.isEmpty()) {
            return drops;
        }
        return List.of(new ItemStack(this));
    }

    // ==================== 放置联动（原版 ShadowBlastFurnacePr0） ====================

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
        super.setPlacedBy(level, pos, state, entity, stack);
        playAssembleEffect(level, pos);
    }

    /**
     * 搭建/放置完成特效（原版 ShadowBlastFurnacePr0Procedure）：
     * machine0 音效 + 动画状态 0 → 3
     *
     * @param level 世界
     * @param pos   方块位置
     */
    public static void playAssembleEffect(Level level, BlockPos pos) {
        if (!level.isClientSide()) {
            level.playSound(null, pos, PDSounds.MACHINE0.get(), SoundSource.NEUTRAL, 1, 1);
        }
        BlockState state = level.getBlockState(pos);
        if (state.hasProperty(ANIMATION)) {
            level.setBlock(pos, state.setValue(ANIMATION, 0), 3);
            level.setBlock(pos, level.getBlockState(pos).setValue(ANIMATION, 3), 3);
        }
    }

    // ==================== 右键交互 ====================

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hitResult) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        if (level.getBlockEntity(pos) instanceof ShadowBlastFurnaceBlockEntity furnace
                && player instanceof ServerPlayer serverPlayer) {
            serverPlayer.openMenu(furnace, pos);
        }
        // 原版 ShadowBlastFurnacePr1：machine3 音效 + 动画置 2
        level.playSound(null, pos, PDSounds.MACHINE3.get(), SoundSource.NEUTRAL, 1, 1);
        BlockState current = level.getBlockState(pos);
        if (current.hasProperty(ANIMATION)) {
            level.setBlock(pos, current.setValue(ANIMATION, 2), 3);
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
            if (level.getBlockEntity(pos) instanceof ShadowBlastFurnaceBlockEntity furnace) {
                ItemStackHandler handler = furnace.getItemHandler();
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
        if (level.getBlockEntity(pos) instanceof ShadowBlastFurnaceBlockEntity furnace) {
            return WeaponWorkshopBlock.calcRedstoneFromItemHandler(furnace.getItemHandler());
        }
        return 0;
    }

    // ==================== 环境音效与 ticker ====================

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        // 工作中偶发高炉噼啪声（原版 animateTick，1/10 概率）
        if (state.getValue(WORKING) && random.nextInt(10) == 0) {
            level.playLocalSound(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5,
                    SoundEvents.BLASTFURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 2, 1, false);
        }
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) {
            return createTickerHelper(type, PDBlockEntities.SHADOW_BLAST_FURNACE.get(),
                    ShadowBlastFurnaceBlockEntity::animationTick);
        }
        return createTickerHelper(type, PDBlockEntities.SHADOW_BLAST_FURNACE.get(),
                ShadowBlastFurnaceBlockEntity::blastingTick);
    }
}
