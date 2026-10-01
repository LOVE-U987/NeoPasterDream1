package com.pasterdream.pasterdreammod.block;

import com.mojang.serialization.MapCodec;
import com.pasterdream.pasterdreammod.block.entity.W4DataBlockEntity;
import com.pasterdream.pasterdreammod.block.entity.W4GeoDataBlockEntity;
import com.pasterdream.pasterdreammod.registry.PDBlockEntitiesFurniture;
import com.pasterdream.pasterdreammod.registry.PDEffects;
import com.pasterdream.pasterdreammod.registry.PDGameRules;
import com.pasterdream.pasterdreammod.api.util.ServerScheduler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
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

import java.util.Collections;
import java.util.List;

/**
 * 守护者水晶（guard_crystal）
 * <p>
 * 忠实还原原版 {@code GuardCrystalBlock + GuardBlockPr0/Pr1 + GuardCrystalPr0}：
 * <ul>
 *   <li>onPlace 初始化 range=16 / switch，10 tick 循环给范围内玩家施加禁止改造 buff；</li>
 *   <li>右键：beacon 激活音效 → 3 tick 后 animation=1 + 末地烛粒子，
 *       此后多段粒子脉冲，第 31/34 tick 提示范围内玩家并以 TNT 强度 3 爆炸、
 *       连续两次破坏自身（原 GuardCrystalPr0 的 5+26 嵌套延时）。</li>
 * </ul>
 * 强度 100、noOcclusion、GeckoLib 渲染，形状 (3,3,3,13,13,13)。
 */
public class GuardCrystalBlock extends BaseEntityBlock {

    public static final MapCodec<GuardCrystalBlock> CODEC = simpleCodec(GuardCrystalBlock::new);

    public static final IntegerProperty ANIMATION = IntegerProperty.create("animation", 0, 2);

    /**
     * 构造守护者水晶方块
     *
     * @param properties 方块属性
     */
    public GuardCrystalBlock(Properties properties) {
        super(properties);
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
        return box(3, 3, 3, 13, 13, 13);
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
        return Collections.singletonList(new ItemStack(this));
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean moving) {
        super.onPlace(state, level, pos, oldState, moving);
        level.scheduleTick(pos, this, 10);
        // 原 GuardBlockPr1
        if (!level.isClientSide() && !W4DataBlockEntity.getBooleanAt(level, pos, "switch")) {
            W4DataBlockEntity.putDoubleAt(level, pos, "range", 16);
            W4DataBlockEntity.putBooleanAt(level, pos, "switch", true);
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.tick(state, level, pos, random);
        // 原 GuardBlockPr0
        if (!level.getGameRules().getBoolean(PDGameRules.PASTERDREAM_DEBUG_MODE)) {
            double range = W4DataBlockEntity.getDoubleAt(level, pos, "range");
            Vec3 center = new Vec3(pos.getX(), pos.getY(), pos.getZ());
            for (Entity entity : level.getEntitiesOfClass(Entity.class,
                    new AABB(center, center).inflate(range / 2d), e -> true)) {
                if (entity instanceof Player && entity instanceof LivingEntity living
                        && !living.level().isClientSide()) {
                    living.addEffect(new MobEffectInstance(PDEffects.GUARD_BLOCK_BUFF.holder(), 60, 0, false, false));
                }
            }
        }
        level.scheduleTick(pos, this, 10);
    }

    // ==================== 右键自毁流程（原 GuardCrystalPr0Procedure） ====================

    /**
     * 右键触发自毁序列。
     * <p>
     * <b>server-only</b>：交互入口统一在服务端执行。客户端预测路径若同样执行，
     * 会把延迟任务调度进共享的 {@link ServerScheduler} 队列并捕获 {@code ClientLevel}，
     * 导致"客户端本地移除方块、服务端不同步"的残影现象（残影需下一次方块更新才消失）。
     * <p>
     * <b>防重复触发</b>：以方块实体数据 {@code triggered} 作为触发锁。
     * 不用 {@code ANIMATION} 属性——GeckoLib 动画控制器在动画播放完毕后会自动把
     * {@code ANIMATION} 复位为 0，无法覆盖整段 +31 tick 的自毁延迟窗口；
     * BE 数据随方块存档持久化，可靠锁定。
     */
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hitResult) {
        // 服务端独占：避免客户端预测调度 ClientLevel 任务
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        // 已进入自毁流程的方块不再响应右键（BE 触发锁）
        if (W4DataBlockEntity.getBooleanAt(level, pos, "triggered")) {
            return InteractionResult.CONSUME;
        }
        W4DataBlockEntity.putBooleanAt(level, pos, "triggered", true);
        // 0→1 状态转变驱动 GeckoLib 播放开启动画
        level.setBlock(pos, state.setValue(ANIMATION, 1), 3);

        double x = pos.getX();
        double y = pos.getY();
        double z = pos.getZ();
        level.playSound(null, pos, SoundEvents.BEACON_ACTIVATE, SoundSource.NEUTRAL, 3, 1.2f);
        ServerScheduler.schedule(3, () -> {
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.END_ROD, x + 0.5, y + 0.5, z + 0.5, 64, 1, 1, 1, 0.3);
            }
        });
        ServerScheduler.schedule(5, () -> {
            ServerScheduler.schedule(7, () -> {
                if (level instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ParticleTypes.END_ROD, x + 0.5, y + 0.5, z + 0.5, 64, 1, 1, 1, 0.4);
                }
            });
            ServerScheduler.schedule(18, () -> {
                if (level instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ParticleTypes.END_ROD, x + 0.5, y + 0.5, z + 0.5, 64, 0.5, 0.5, 0.5, 0.2);
                }
            });
            ServerScheduler.schedule(26, () -> {
                double range = W4DataBlockEntity.getDoubleAt(level, pos, "range");
                Vec3 center = new Vec3(x, y, z);
                for (Entity entity : level.getEntitiesOfClass(Entity.class,
                        new AABB(center, center).inflate(range / 2d), e -> true)) {
                    if (entity instanceof Player p && !p.level().isClientSide()) {
                        p.displayClientMessage(Component.translatable("message.pasterdream.guard_crystal.guardian_destroyed"), false);
                    }
                }
                if (level instanceof ServerLevel serverLevel) {
                    serverLevel.explode(null, x + 0.5, y + 0.5, z + 0.5, 3, Level.ExplosionInteraction.TNT);
                    removeCrystal(serverLevel, pos);
                } else {
                    level.removeBlock(pos, false);
                }
                ServerScheduler.schedule(1, () -> level.removeBlock(pos, false));
            });
        });
        return InteractionResult.SUCCESS;
    }

    /**
     * 彻底移除守护者水晶：先移除方块实体再移除方块，并强制向客户端同步空状态。
     * <p>
     * 守护者水晶为 {@code ENTITYBLOCK_ANIMATED} + GeckoLib BER 方块，
     * 常规 {@code destroyBlock} 在存在动画方块实体时可能残留在客户端的
     * 方块实体渲染列表中，形成"残影"。显式移除 BE + {@code sendBlockUpdated}
     * 可确保客户端立即清除该位置的渲染内容。
     *
     * @param level 服务端世界
     * @param pos   方块位置
     */
    private static void removeCrystal(ServerLevel level, BlockPos pos) {
        BlockState oldState = level.getBlockState(pos);
        level.removeBlockEntity(pos);
        level.removeBlock(pos, false);
        level.sendBlockUpdated(pos, oldState, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
    }

    /** 设置 animation 属性 */
    private static void setAnimation(Level level, BlockPos pos, int value) {
        BlockState state = level.getBlockState(pos);
        if (state.getBlock().getStateDefinition().getProperty("animation") instanceof IntegerProperty prop
                && prop.getPossibleValues().contains(value)) {
            level.setBlock(pos, state.setValue(prop, value), 3);
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new W4GeoDataBlockEntity(PDBlockEntitiesFurniture.GUARD_CRYSTAL.get(), pos, state);
    }
}
