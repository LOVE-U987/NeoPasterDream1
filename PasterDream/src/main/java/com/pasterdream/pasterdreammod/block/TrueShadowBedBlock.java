package com.pasterdream.pasterdreammod.block;

import com.pasterdream.pasterdreammod.attachment.PDAttachments;
import com.pasterdream.pasterdreammod.block.entity.W4DataBlockEntity;
import com.pasterdream.pasterdreammod.registry.PDBlockEntitiesFurniture;
import com.pasterdream.pasterdreammod.registry.PDDimensions;
import com.pasterdream.pasterdreammod.registry.blocks.PDBlocksFurniture;
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
 * 忠实还原原版 {@code TrueShadowBedBlock + TrueShadowBedPr0Procedure}：
 * <ul>
 *   <li>夜晚/雷暴时右键：床上方 2 格若是激活（key=true）的暮影之笼，
 *       且玩家已达成 achievement_hide_9——理智 -10 并传送至 lamp_shadow_world；</li>
 *   <li>白天提示只能夜晚入眠；</li>
 *   <li>在 lamp_shadow_world 中：已达成 achievement_shadow_npc_5 且未达成
 *       achievement_shadow_d_0 的玩家，打开影之抉择菜单
 *       （ShadowSelectEndMenu，主线程已注册）。</li>
 * </ul>
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

    // ==================== 入眠交互（原 TrueShadowBedPr0Procedure） ====================

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hitResult) {
        player.swing(InteractionHand.MAIN_HAND, true);

        if (!level.isDay() || level.getLevelData().isThundering()) {
            BlockPos lanternPos = pos.above(2);
            if (level.getBlockState(lanternPos).getBlock() == PDBlocksFurniture.TWILIGHT_LANTERN.get()
                    && W4DataBlockEntity.getBooleanAt(level, lanternPos, "key")
                    && player instanceof ServerPlayer serverPlayer
                    && serverPlayer.level() instanceof ServerLevel
                    && ShadowBedBlock.hasAdvancement(serverPlayer, "achievement_hide_9")) {
                PDAttachments.addPlayerSanWithCheck(serverPlayer, -10);
                ShadowBedBlock.teleportToLampShadowWorld(level, serverPlayer);
            }
        } else if (!player.level().isClientSide()) {
            player.displayClientMessage(Component.literal("你只能在夜晚或雷暴中入眠"), true);
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
