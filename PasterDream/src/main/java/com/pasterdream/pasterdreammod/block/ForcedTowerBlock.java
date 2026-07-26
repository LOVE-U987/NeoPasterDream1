package com.pasterdream.pasterdreammod.block;

import com.mojang.serialization.MapCodec;
import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.attachment.PDAttachments;
import com.pasterdream.pasterdreammod.block.entity.ForcedTowerBlockEntity;
import com.pasterdream.pasterdreammod.registry.PDBlocks;
import com.pasterdream.pasterdreammod.registry.PDSounds;
import com.pasterdream.pasterdreammod.util.PasterItemData;
import com.pasterdream.pasterdreammod.util.ServerScheduler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 强征传送塔方块 (Forced Tower)
 * <p>
 * GeckoLib 3D 模型（ENTITYBLOCK_ANIMATED），ANIMATION(0-3) 状态。
 * 交互还原原版三条 procedure：
 * <ul>
 *   <li>周期 tick（ForcedTowerPr0，每 5 tick）：7×7×7 内有玩家时动画置 1
 *       并播放信标环境音；</li>
 *   <li>空手右键（ForcedTowerPr1）：塔间链接有效且落点畅通时消耗 0.5 融梦能量，
 *       信标激活音 + 35 tick 后传送玩家至链接塔顶（末地门音效 + 末影烛粒子），
 *       动画置 2；链接丢失/能量不足时给出提示；</li>
 *   <li>手持聚梦法杖右键（ForcedTowerPr2）：第一次记录本塔坐标到法杖，第二次
 *       在两塔间建立双向链接（写入两端 BE 的目标坐标），并播放 dream1 音效。</li>
 * </ul>
 */
public class ForcedTowerBlock extends BaseEntityBlock {

    public static final MapCodec<ForcedTowerBlock> CODEC = simpleCodec(ForcedTowerBlock::new);
    /** 动画状态（原版 0-3；"0" 为空闲循环） */
    public static final IntegerProperty ANIMATION = IntegerProperty.create("animation", 0, 3);

    /** 每次传送消耗的融梦能量 */
    private static final double TELEPORT_ENERGY_COST = 0.5;

    /**
     * 构造强征传送塔方块
     *
     * @param properties 方块属性
     */
    public ForcedTowerBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(ANIMATION, 0));
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
        return new ForcedTowerBlockEntity(pos, state);
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
        return box(2, 0, 2, 14, 6, 14);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ANIMATION);
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

    // ==================== 周期 tick（原版 ForcedTowerPr0，每 5 tick） ====================

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        level.scheduleTick(pos, this, 5);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.tick(state, level, pos, random);
        // 7×7×7 内有玩家时：动画置 1 + 信标环境音
        if (!level.getEntitiesOfClass(Player.class,
                AABB.ofSize(new Vec3(pos.getX(), pos.getY(), pos.getZ()), 7, 7, 7), e -> true).isEmpty()) {
            BlockState current = level.getBlockState(pos);
            if (current.hasProperty(ANIMATION)) {
                level.setBlock(pos, current.setValue(ANIMATION, 1), 3);
            }
            level.playSound(null, pos, SoundEvents.BEACON_AMBIENT, SoundSource.NEUTRAL, 1.5f, 1);
        }
        level.scheduleTick(pos, this, 5);
    }

    // ==================== 右键交互 ====================

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hitResult) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        tryTeleport(level, pos, player);
        return InteractionResult.CONSUME;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.isClientSide()) {
            return ItemInteractionResult.SUCCESS;
        }
        // 手持聚梦法杖：建立/记录链接（原版 ForcedTowerPr2）
        tryLinkWithWand(level, pos, player);
        return ItemInteractionResult.CONSUME;
    }

    /**
     * 空手传送逻辑（原版 ForcedTowerPr1Procedure）：
     * 校验链接塔存在且落点两格畅通 → 消耗融梦能量 → 信标音效 + 动画置 2 →
     * 35 tick 后执行传送并播放末地门音效、双端末影烛粒子
     *
     * @param level  世界（服务端）
     * @param pos    本塔位置
     * @param player 交互玩家
     */
    private void tryTeleport(Level level, BlockPos pos, Player player) {
        if (!(level instanceof ServerLevel serverLevel)
                || !(level.getBlockEntity(pos) instanceof ForcedTowerBlockEntity tower)) {
            return;
        }
        if (!player.getMainHandItem().isEmpty()) {
            // 与原版一致：仅空手触发传送分支（持杖分支由 useItemOn 处理）
            return;
        }
        BlockPos target = BlockPos.containing(tower.getCoordX(), tower.getCoordY(), tower.getCoordZ());
        boolean targetValid = serverLevel.getBlockState(target).getBlock() == PDBlocks.FORCED_TOWER.get()
                && serverLevel.getBlockState(target.above(1)).isAir()
                && serverLevel.getBlockState(target.above(2)).isAir();
        if (!targetValid) {
            player.displayClientMessage(Component.literal("强征传送塔已被遗失或阻挡"), true);
            return;
        }
        if (!PDAttachments.consumePlayerMeltDreamEnergy(player, TELEPORT_ENERGY_COST)) {
            player.displayClientMessage(Component.literal("融梦能量不足"), true);
            return;
        }
        serverLevel.playSound(null, pos, SoundEvents.BEACON_ACTIVATE, SoundSource.NEUTRAL, 1.5f, 1);
        BlockState current = serverLevel.getBlockState(pos);
        if (current.hasProperty(ANIMATION)) {
            serverLevel.setBlock(pos, current.setValue(ANIMATION, 2), 3);
        }
        double sx = pos.getX();
        double sy = pos.getY();
        double sz = pos.getZ();
        ServerScheduler.schedule(35, () -> {
            if (player instanceof ServerPlayer serverPlayer && serverPlayer.serverLevel() == serverLevel) {
                serverPlayer.teleportTo(target.getX() + 0.5, target.getY() + 0.8, target.getZ() + 0.5);
                serverPlayer.connection.teleport(target.getX() + 0.5, target.getY() + 0.8, target.getZ() + 0.5,
                        serverPlayer.getYRot(), serverPlayer.getXRot());
            }
            serverLevel.playSound(null, target, SoundEvents.END_PORTAL_SPAWN, SoundSource.BLOCKS, 1, 1);
            serverLevel.sendParticles(ParticleTypes.END_ROD, sx, sy, sz, 64, 1, 1, 1, 0.5);
            serverLevel.sendParticles(ParticleTypes.END_ROD,
                    target.getX(), target.getY(), target.getZ(), 64, 1, 1, 1, 0.5);
        });
    }

    /**
     * 聚梦法杖链接逻辑（原版 ForcedTowerPr2Procedure）：
     * 法杖未记录坐标时记录本塔；已记录时与记录塔互写目标坐标建立双向链接
     *
     * @param level  世界（服务端）
     * @param pos    本塔位置
     * @param player 交互玩家
     */
    private void tryLinkWithWand(Level level, BlockPos pos, Player player) {
        Item wand = BuiltInRegistries.ITEM
                .getOptional(ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "dream_wand"))
                .orElse(Items.AIR);
        if (wand == Items.AIR) {
            PasterDreamMod.LOGGER.debug("[ForcedTower] dream_wand 未注册，跳过链接交互");
            return;
        }
        ItemStack held = player.getMainHandItem();
        if (!held.is(wand)) {
            return;
        }
        if (!(level.getBlockEntity(pos) instanceof ForcedTowerBlockEntity tower)) {
            return;
        }
        if (!PasterItemData.getBoolean(held, "switch")) {
            // 第一次点击：把本塔坐标记录进法杖
            PasterItemData.putDouble(held, "coord_x", pos.getX());
            PasterItemData.putDouble(held, "coord_y", pos.getY());
            PasterItemData.putDouble(held, "coord_z", pos.getZ());
            PasterItemData.putBoolean(held, "switch", true);
            tower.setLinked(false);
            player.swing(InteractionHand.MAIN_HAND, true);
            player.displayClientMessage(Component.literal("已存储记录点"), true);
        } else {
            BlockPos recorded = BlockPos.containing(
                    PasterItemData.getDouble(held, "coord_x"),
                    PasterItemData.getDouble(held, "coord_y"),
                    PasterItemData.getDouble(held, "coord_z"));
            if (level.getBlockState(recorded).getBlock() == PDBlocks.FORCED_TOWER.get()) {
                // 第二次点击：两塔互写坐标建立双向链接
                tower.setCoords(recorded.getX(), recorded.getY(), recorded.getZ());
                tower.setLinked(true);
                if (level.getBlockEntity(recorded) instanceof ForcedTowerBlockEntity other) {
                    other.setCoords(pos.getX(), pos.getY(), pos.getZ());
                    other.setLinked(true);
                }
                PasterItemData.putBoolean(held, "switch", false);
                player.swing(InteractionHand.MAIN_HAND, true);
                player.displayClientMessage(Component.literal("强征传送塔链接建立成功"), true);
            } else {
                player.displayClientMessage(Component.literal("对应坐标没有找到强征传送塔，建立链接失败"), true);
            }
        }
        level.playSound(null, pos, PDSounds.DREAM1.get(), SoundSource.NEUTRAL, 1.1f, 1);
    }

    @Override
    public boolean triggerEvent(BlockState state, Level level, BlockPos pos, int eventID, int eventParam) {
        super.triggerEvent(state, level, pos, eventID, eventParam);
        BlockEntity blockEntity = level.getBlockEntity(pos);
        return blockEntity != null && blockEntity.triggerEvent(eventID, eventParam);
    }
}
