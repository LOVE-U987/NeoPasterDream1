package com.pasterdream.pasterdreammod.block;

import com.pasterdream.pasterdreammod.attachment.PDAttachments;
import com.pasterdream.pasterdreammod.block.entity.W4DataBlockEntity;
import com.pasterdream.pasterdreammod.registry.PDBlockEntitiesFurniture;
import com.pasterdream.pasterdreammod.registry.PDDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * 真·影之床（true_shadow_bed）
 * <p>
 * 入眠交互与 {@link ShadowBedBlock} 完全一致：
 * 夜晚/雷暴且不在灯影世界时右键——已达成 achievement_shadow_start 的玩家
 * 理智 -10、补授 achievement_shadow_a_1，并传送至 lamp_shadow_world
 * （原 WorldSpawnPr1Procedure：0,100,0 为空气时落点 (0.5,104,0.5)，
 * 否则 (0.5,154,0.5)）；未达成则提示缺少进度；白天提示只能夜晚入眠。
 * <p>
 * 保留灯影世界内的独有特性：已达成 achievement_shadow_npc_5 且未达成
 * achievement_shadow_d_0 的玩家，打开影之抉择菜单
 * （ShadowSelectEndMenu，主线程已注册）。
 * <p>
 * 不可破坏木床，FACING+WATERLOGGED，床形碰撞箱。
 */
public class TrueShadowBedBlock extends Block implements SimpleWaterloggedBlock, EntityBlock {

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    /**
     * 构造真·影之床方块
     *
     * @param properties 方块属性
     */
    public TrueShadowBedBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(WATERLOGGED, false));
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
        return 0;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction facing = state.getValue(FACING);
        if (facing == Direction.NORTH) return box(0, 0, -6, 16, 9, 24);
        if (facing == Direction.EAST) return box(-8, 0, 0, 22, 9, 16);
        if (facing == Direction.WEST) return box(-6, 0, 0, 24, 9, 16);
        return box(0, 0, -8, 16, 9, 22);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        boolean water = context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER;
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(WATERLOGGED, water);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                  LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        List<ItemStack> drops = super.getDrops(state, builder);
        if (!drops.isEmpty()) {
            return drops;
        }
        return Collections.singletonList(new ItemStack(this));
    }

    // ==================== 入眠交互（与 ShadowBedBlock 完全一致） ====================

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hitResult) {
        boolean nightOrThunder = !level.isDay() || level.getLevelData().isThundering();
        boolean notInLampWorld = level.dimension() != PDDimensions.LAMP_SHADOW_WORLD_LEVEL_KEY;
        if (nightOrThunder || notInLampWorld) {
            if (player instanceof ServerPlayer serverPlayer && serverPlayer.level() instanceof ServerLevel
                    && ShadowBedBlock.hasAdvancement(serverPlayer, "achievement_shadow_start")) {
                PDAttachments.addPlayerSanWithCheck(serverPlayer, -10);
                if (!ShadowBedBlock.hasAdvancement(serverPlayer, "achievement_shadow_a_1")) {
                    ShadowBedBlock.awardAdvancement(serverPlayer, "achievement_shadow_a_1");
                }
                ShadowBedBlock.teleportToLampShadowWorld(level, player);
            } else if (!player.level().isClientSide()) {
                player.displayClientMessage(Component.translatable("message.pasterdream.shadow_bed.lack_progress"), true);
            }
        } else if (!player.level().isClientSide()) {
            player.displayClientMessage(Component.translatable("message.pasterdream.shadow_bed.night_only"), true);
        }

        // 灯影世界中：影之抉择（ShadowSelectEnd GUI）
        if (level.dimension() == PDDimensions.LAMP_SHADOW_WORLD_LEVEL_KEY
                && player instanceof ServerPlayer serverPlayer
                && serverPlayer.level() instanceof ServerLevel
                && ShadowBedBlock.hasAdvancement(serverPlayer, "achievement_shadow_npc_5")
                && !ShadowBedBlock.hasAdvancement(serverPlayer, "achievement_shadow_d_0")) {
            BlockPos bedPos = pos.immutable();
            // 打开主线程已注册的 PDMenus.SHADOW_SELECT_END（菜单 ctor 内 super(PDMenus.SHADOW_SELECT_END)）
            serverPlayer.openMenu(new MenuProvider() {
                @Override
                public Component getDisplayName() {
                    return Component.literal("ShadowSelectEnd");
                }

                @Override
                public AbstractContainerMenu createMenu(int id, Inventory inventory, Player p) {
                    return new com.pasterdream.pasterdreammod.menu.ShadowSelectEndMenu(id, inventory);
                }
            }, bedPos);
        }
        player.swing(InteractionHand.MAIN_HAND, true);
        return InteractionResult.SUCCESS;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new W4DataBlockEntity(PDBlockEntitiesFurniture.TRUE_SHADOW_BED.get(), pos, state);
    }

    @Override
    public boolean triggerEvent(BlockState state, Level level, BlockPos pos, int eventId, int eventParam) {
        super.triggerEvent(state, level, pos, eventId, eventParam);
        BlockEntity blockEntity = level.getBlockEntity(pos);
        return blockEntity != null && blockEntity.triggerEvent(eventId, eventParam);
    }
}
