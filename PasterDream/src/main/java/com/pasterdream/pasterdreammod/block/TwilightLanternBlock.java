package com.pasterdream.pasterdreammod.block;

import com.mojang.serialization.MapCodec;
import com.pasterdream.pasterdreammod.block.entity.W4DataBlockEntity;
import com.pasterdream.pasterdreammod.block.entity.W4GeoDataBlockEntity;
import com.pasterdream.pasterdreammod.registry.PDBlockEntitiesFurniture;
import com.pasterdream.pasterdreammod.registry.PDDimensions;
import com.pasterdream.pasterdreammod.registry.PDEntities;
import com.pasterdream.pasterdreammod.registry.PDSounds;
import com.pasterdream.pasterdreammod.registry.items.PDItemsFunctional;
import com.pasterdream.pasterdreammod.api.util.ServerScheduler;
import com.pasterdream.pasterdreammod.registry.PDAdvancements;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.protocol.game.ClientboundLevelEventPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerAbilitiesPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
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
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * 暮影之笼（twilight_lantern）
 * <p>
 * 忠实还原原版 {@code TwilightLanternBlock + TwilightLanternPr0/Pr1/Pr2}：
 * <ul>
 *   <li>在 lamp_shadow_world 中右键：20 tick 后 WIN_GAME 过场并传送至主世界重生点/出生点；</li>
 *   <li>主世界右键（achievement_hide_8 或 hide_10 已达成）：主手融梦水晶碎片
 *       点燃影灯（switch=true），触发暮影之笼事件——2600 tick 内 20 tick 周期
 *       计数（number），按节点召唤暗影幽魂波（Pr2）、暗影傀儡与噬影鸦，
 *       结束后若 46 格内仍有玩家则 key=true 并授予 achievement_hide_9；</li>
 *   <li>tick（Pr1）：switch 置位期间 number 递增并按 4/14/40/50/70/100/120
 *       召唤幽魂波，30/80 召唤傀儡+鸦。</li>
 * </ul>
 * 不可破坏、灯笼音效、光照 8（注册属性）、FACING+ANIMATION，
 * 形状 (3,4,3,13,17,13)。
 */
public class TwilightLanternBlock extends BaseEntityBlock {

    public static final MapCodec<TwilightLanternBlock> CODEC = simpleCodec(TwilightLanternBlock::new);

    public static final IntegerProperty ANIMATION = IntegerProperty.create("animation", 0, 1);
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    /**
     * 构造暮影之笼方块
     *
     * @param properties 方块属性
     */
    public TwilightLanternBlock(Properties properties) {
        super(properties);
        // ANIMATION 默认 0 → W4Geo  idle 循环 "0"；动画资源仅含 "0"，
        // 原版/本模均无 setValue(1) 触发位（procedure 控制器保留兼容）
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

    @Override
    public int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
        return 0;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return box(3, 4, 3, 13, 17, 13);
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
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
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
        return Collections.singletonList(new ItemStack(this));
    }

    // ==================== tick 计数（原 TwilightLanternPr1Procedure） ====================

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean moving) {
        super.onPlace(state, level, pos, oldState, moving);
        level.scheduleTick(pos, this, 20);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.tick(state, level, pos, random);
        if (W4DataBlockEntity.getBooleanAt(level, pos, "switch")) {
            double number = W4DataBlockEntity.getDoubleAt(level, pos, "number") + 1;
            W4DataBlockEntity.putDoubleAt(level, pos, "number", number);
            if (number == 4 || number == 14 || number == 40 || number == 50
                    || number == 70 || number == 100 || number == 120) {
                spawnGhostWave(level, pos);
            }
            if (number == 30) {
                spawnAt(level, PDEntities.SHADOW_GOLEM.get(), pos.getX(), pos.getY() - 1, pos.getZ() + 13);
                spawnAt(level, PDEntities.TERRORBEAK.get(), pos.getX() - 13, pos.getY() - 1, pos.getZ());
            }
            if (number == 80) {
                spawnAt(level, PDEntities.SHADOW_GOLEM.get(), pos.getX(), pos.getY() - 1, pos.getZ() - 13);
                spawnAt(level, PDEntities.TERRORBEAK.get(), pos.getX() - 13, pos.getY() - 1, pos.getZ());
            }
        }
        level.scheduleTick(pos, this, 20);
    }

    /** 幽魂波（原 TwilightLanternPr2Procedure：4 幽魂 + 4 尖啸幽魂 + 4 凋灵骷髅） */
    private static void spawnGhostWave(ServerLevel level, BlockPos pos) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        spawnAt(level, PDEntities.SHADOW_GHOST.get(), x + 6, y + 2, z);
        spawnAt(level, PDEntities.SHADOW_GHOST.get(), x - 6, y + 2, z);
        spawnAt(level, PDEntities.SHADOW_GHOST.get(), x, y + 2, z - 6);
        spawnAt(level, PDEntities.SHADOW_GHOST.get(), x, y + 2, z - 6);
        spawnAt(level, PDEntities.SHADOW_SQUEAL_GHOST.get(), x + 8, y + 1, z + 8);
        spawnAt(level, PDEntities.SHADOW_SQUEAL_GHOST.get(), x - 8, y + 1, z + 8);
        spawnAt(level, PDEntities.SHADOW_SQUEAL_GHOST.get(), x + 8, y + 1, z - 8);
        spawnAt(level, PDEntities.SHADOW_SQUEAL_GHOST.get(), x - 8, y + 1, z - 8);
        spawnAt(level, EntityType.WITHER_SKELETON, x + 9, y - 1, z + 1);
        spawnAt(level, EntityType.WITHER_SKELETON, x - 9, y - 1, z + 1);
        spawnAt(level, EntityType.WITHER_SKELETON, x, y - 1, z + 9);
        spawnAt(level, EntityType.WITHER_SKELETON, x, y - 1, z - 9);
    }

    private static void spawnAt(ServerLevel level, EntityType<?> type, double x, double y, double z) {
        type.spawn(level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
    }

    // ==================== 右键交互（原 TwilightLanternPr0Procedure） ====================

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hitResult) {
        double x = pos.getX();
        double y = pos.getY();
        double z = pos.getZ();
        if (level.dimension() == PDDimensions.LAMP_SHADOW_WORLD_LEVEL_KEY) {
            if (!level.isClientSide()) {
                level.playSound(null, pos, SoundEvents.LANTERN_PLACE, SoundSource.NEUTRAL, 1, 1);
            }
            ServerScheduler.schedule(20, () -> {
                if (!(player instanceof ServerPlayer serverPlayer) || serverPlayer.level().isClientSide()) {
                    return;
                }
                // 返程：WIN_GAME 过场 + 主世界重生点/世界出生点（不再依赖未注册的裸 /spawn）
                teleportToOverworldSpawn(serverPlayer);
            });
            return InteractionResult.SUCCESS;
        }

        // 主世界侧：需要 achievement_hide_8 或 achievement_hide_10
        if (hasAdvancement(player, "achievement_hide_8") || hasAdvancement(player, "achievement_hide_10")) {
            if (player.getMainHandItem().getItem() == PDItemsFunctional.MELTDREAM_CRYSTAL_0.get()
                    && !W4DataBlockEntity.getBooleanAt(level, pos, "switch")) {
                if (player instanceof LivingEntity living && !living.level().isClientSide()) {
                    living.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 140, 0, false, false));
                }
                // Neo 增强：再点燃时清零 number，避免上次残留导致刷怪节点无法再次命中
                W4DataBlockEntity.putDoubleAt(level, pos, "number", 0);
                W4DataBlockEntity.putBooleanAt(level, pos, "switch", true);
                ItemStack toRemove = new ItemStack(PDItemsFunctional.MELTDREAM_CRYSTAL_0.get());
                player.getInventory().clearOrCountMatchingItems(
                        s -> toRemove.getItem() == s.getItem(), 1, player.inventoryMenu.getCraftSlots());
                if (!level.isClientSide()) {
                    level.playSound(null, pos, SoundEvents.LANTERN_PLACE, SoundSource.NEUTRAL, 1, 1);
                }
                if (level instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ParticleTypes.END_ROD,
                            x + 0.5, y - 0.5, z + 0.5, 32, 1, 1, 1, 0.05);
                }
                ServerScheduler.schedule(18, () -> {
                    if (!level.isClientSide()) {
                        level.playSound(null, pos, PDSounds.SHADOW_0.get(), SoundSource.NEUTRAL, 1, 1);
                    }
                    if (!player.level().isClientSide()) {
                        player.displayClientMessage(
                                Component.literal("§8诡异的灯笼突然剧烈抖动，黑色的阴影从灯芯里流出。"), false);
                    }
                });
                ServerScheduler.schedule(55, () -> {
                    if (!player.level().isClientSide()) {
                        player.displayClientMessage(
                                Component.literal("§8阴影化为实物，四周传来空洞的回响。"), false);
                    }
                    if (!level.isClientSide()) {
                        level.playSound(null, pos, PDSounds.SHADOW_MUSIC_0.get(), SoundSource.MUSIC, 1, 1);
                    }
                });
                ServerScheduler.schedule(2600, () -> {
                    if (!level.getEntitiesOfClass(Player.class,
                            AABB.ofSize(new Vec3(x, y, z), 46, 46, 46), e -> true).isEmpty()) {
                        W4DataBlockEntity.putBooleanAt(level, pos, "key", true);
                        Vec3 center = new Vec3(x, y, z);
                        for (Entity nearby : level.getEntitiesOfClass(Entity.class,
                                new AABB(center, center).inflate(54 / 2d), e -> true)) {
                            if (nearby instanceof Player p && !p.level().isClientSide()) {
                                p.displayClientMessage(
                                        Component.literal("§8阴影不再从四周向外涌出，暮影之笼也逐渐归为平静。"), false);
                            }
                            ServerScheduler.schedule(60, () -> {
                                if (nearby instanceof Player p && !p.level().isClientSide()) {
                                    p.displayClientMessage(
                                            Component.literal("§5“躺上去吧，在寂静的夜晚，在这亭笼之下”"), false);
                                    p.displayClientMessage(
                                            Component.literal("§5已可以与暮影之笼发生共鸣"), true);
                                }
                            });
                            ServerScheduler.schedule(100, () -> {
                                if (nearby instanceof ServerPlayer sp) {
                                    awardAdvancement(sp, "achievement_hide_9");
                                }
                            });
                        }
                    }
                    // Neo 增强：事件结束同时清零 number（原版只关 switch）
                    W4DataBlockEntity.putBooleanAt(level, pos, "switch", false);
                    W4DataBlockEntity.putDoubleAt(level, pos, "number", 0);
                });
            } else if (!player.level().isClientSide()) {
                player.displayClientMessage(Component.literal("需要用融梦水晶碎片点燃影灯"), true);
            }
        } else if (!player.level().isClientSide()) {
            player.displayClientMessage(Component.literal("你尚未知晓如何激活影灯"), true);
        }
        return InteractionResult.SUCCESS;
    }

    /** 成就完成度查询（缺失成就时降级为 false） */
    private static boolean hasAdvancement(Player player, String name) {
        if (!(player instanceof ServerPlayer serverPlayer) || !(serverPlayer.level() instanceof ServerLevel)) {
            return false;
        }
        if (!PDAdvancements.isAdvancementLocked(serverPlayer, ResourceLocation.fromNamespaceAndPath("pasterdream", name))) {
            return true;
        }
        AdvancementHolder holder = serverPlayer.server.getAdvancements()
                .get(ResourceLocation.fromNamespaceAndPath("pasterdream", name));
        return holder != null && serverPlayer.getAdvancements().getOrStartProgress(holder).isDone();
    }

    /**
     * 灯影返主世界：过场包 + 落在玩家主世界重生点，否则世界共享出生点。
     * 不再执行未注册的裸命令 {@code spawn}。
     */
    private static void teleportToOverworldSpawn(ServerPlayer player) {
        ServerLevel overworld = player.server.getLevel(Level.OVERWORLD);
        if (overworld == null) {
            return;
        }
        Vec3 dest = overworldSpawnTarget(overworld, player);
        if (player.level().dimension() != Level.OVERWORLD) {
            player.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.WIN_GAME, 0));
        }
        player.teleportTo(overworld, dest.x, dest.y, dest.z, player.getYRot(), player.getXRot());
        player.connection.send(new ClientboundPlayerAbilitiesPacket(player.getAbilities()));
        for (MobEffectInstance effect : player.getActiveEffects()) {
            player.connection.send(new ClientboundUpdateMobEffectPacket(player.getId(), effect, false));
        }
        player.connection.send(new ClientboundLevelEventPacket(1032, BlockPos.ZERO, 0, false));
    }

    private static Vec3 overworldSpawnTarget(ServerLevel overworld, ServerPlayer player) {
        if (player.getRespawnPosition() != null
                && Level.OVERWORLD.equals(player.getRespawnDimension())) {
            BlockPos rp = player.getRespawnPosition();
            return new Vec3(rp.getX() + 0.5, rp.getY(), rp.getZ() + 0.5);
        }
        BlockPos spawn = overworld.getSharedSpawnPos();
        int y = overworld.getHeight(
                net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING,
                spawn.getX(), spawn.getZ());
        return new Vec3(spawn.getX() + 0.5, Math.max(y, spawn.getY()), spawn.getZ() + 0.5);
    }

    /** 授予成就（缺失成就时降级跳过） */
    private static void awardAdvancement(ServerPlayer player, String name) {
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
        return new W4GeoDataBlockEntity(PDBlockEntitiesFurniture.TWILIGHT_LANTERN.get(), pos, state);
    }
}
