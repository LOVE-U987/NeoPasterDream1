package com.pasterdream.pasterdreammod.block;

import com.pasterdream.pasterdreammod.attachment.PDAttachments;
import com.pasterdream.pasterdreammod.block.entity.W4DataBlockEntity;
import com.pasterdream.pasterdreammod.registry.PDBlockEntitiesFurniture;
import com.pasterdream.pasterdreammod.registry.PDDimensions;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.protocol.game.ClientboundLevelEventPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerAbilitiesPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
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
 * 影之床（shadow_bed）
 * <p>
 * 忠实还原原版 {@code ShadowBedBlock + ShadowBedPr0Procedure}：
 * 夜晚/雷暴且不在灯影世界时右键——已达成 achievement_shadow_start 的玩家
 * 理智 -10、补授 achievement_shadow_a_1，并传送至 lamp_shadow_world
 * （原 WorldSpawnPr1Procedure：0,100,0 为空气时落点 (0.5,104,0.5)，
 * 否则 (0.5,154,0.5)）；未达成则提示缺少进度；白天提示只能夜晚入眠。
 * 木质、可燃、FACING+WATERLOGGED，床形碰撞箱。
 */
public class ShadowBedBlock extends Block implements SimpleWaterloggedBlock, EntityBlock {

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    /**
     * 构造影之床方块
     *
     * @param properties 方块属性
     */
    public ShadowBedBlock(Properties properties) {
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
        return switch (state.getValue(FACING)) {
            case NORTH -> box(0, 0, -6, 16, 9, 24);
            case EAST -> box(-8, 0, 0, 22, 9, 16);
            case WEST -> box(-6, 0, 0, 24, 9, 16);
            default -> box(0, 0, -8, 16, 9, 22);
        };
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

    // ==================== 入眠交互（原 ShadowBedPr0Procedure） ====================

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hitResult) {
        boolean nightOrThunder = !level.isDay() || level.getLevelData().isThundering();
        boolean notInLampWorld = level.dimension() != PDDimensions.LAMP_SHADOW_WORLD_LEVEL_KEY;
        if (nightOrThunder || notInLampWorld) {
            if (player instanceof ServerPlayer serverPlayer && serverPlayer.level() instanceof ServerLevel
                    && hasAdvancement(serverPlayer, "achievement_shadow_start")) {
                PDAttachments.addPlayerSanWithCheck(serverPlayer, -10);
                if (!hasAdvancement(serverPlayer, "achievement_shadow_a_1")) {
                    awardAdvancement(serverPlayer, "achievement_shadow_a_1");
                }
                teleportToLampShadowWorld(level, player);
            } else if (!player.level().isClientSide()) {
                player.displayClientMessage(Component.literal("缺少灯影之下的进度"), true);
            }
        } else if (!player.level().isClientSide()) {
            player.displayClientMessage(Component.literal("你只能在夜晚或雷暴中入眠"), true);
        }
        player.swing(InteractionHand.MAIN_HAND, true);
        return InteractionResult.SUCCESS;
    }

    /**
     * 传送到灯影世界（原 WorldSpawnPr1Procedure）
     *
     * @param level  当前世界
     * @param entity 目标实体
     */
    public static void teleportToLampShadowWorld(Level level, Entity entity) {
        if (entity instanceof ServerPlayer player && !player.level().isClientSide()) {
            if (player.level().dimension() != PDDimensions.LAMP_SHADOW_WORLD_LEVEL_KEY) {
                ServerLevel target = player.server.getLevel(PDDimensions.LAMP_SHADOW_WORLD_LEVEL_KEY);
                if (target != null) {
                    player.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.WIN_GAME, 0));
                    player.teleportTo(target, player.getX(), player.getY(), player.getZ(),
                            player.getYRot(), player.getXRot());
                    player.connection.send(new ClientboundPlayerAbilitiesPacket(player.getAbilities()));
                    for (MobEffectInstance effect : player.getActiveEffects()) {
                        player.connection.send(new ClientboundUpdateMobEffectPacket(player.getId(), effect, false));
                    }
                    player.connection.send(new ClientboundLevelEventPacket(1032, BlockPos.ZERO, 0, false));
                }
            }
        }
        // 原版在“源世界”检查 (0,100,0) 是否为空气来决定落点高度（MCreator 生成的语义，按原样保留）
        double ty = level.getBlockState(new BlockPos(0, 100, 0)).getBlock() == Blocks.AIR ? 104 : 154;
        entity.teleportTo(0.5, ty, 0.5);
        if (entity instanceof ServerPlayer serverPlayer) {
            serverPlayer.connection.teleport(0.5, ty, 0.5, entity.getYRot(), entity.getXRot());
        }
    }

    /** 成就完成度查询（缺失时降级 false） */
    static boolean hasAdvancement(ServerPlayer player, String name) {
        AdvancementHolder holder = player.server.getAdvancements()
                .get(ResourceLocation.fromNamespaceAndPath("pasterdream", name));
        return holder != null && player.getAdvancements().getOrStartProgress(holder).isDone();
    }

    /** 授予成就（缺失时降级跳过） */
    static void awardAdvancement(ServerPlayer player, String name) {
        AdvancementHolder holder = player.server.getAdvancements()
                .get(ResourceLocation.fromNamespaceAndPath("pasterdream", name));
        if (holder == null) {
            return;
        }
        AdvancementProgress progress = player.getAdvancements().getOrStartProgress(holder);
        if (!progress.isDone()) {
            for (String criteria : progress.getRemainingCriteria()) {
                player.getAdvancements().award(holder, criteria);
            }
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new W4DataBlockEntity(PDBlockEntitiesFurniture.SHADOW_BED.get(), pos, state);
    }

    @Override
    public boolean triggerEvent(BlockState state, Level level, BlockPos pos, int eventId, int eventParam) {
        super.triggerEvent(state, level, pos, eventId, eventParam);
        BlockEntity blockEntity = level.getBlockEntity(pos);
        return blockEntity != null && blockEntity.triggerEvent(eventId, eventParam);
    }
}
