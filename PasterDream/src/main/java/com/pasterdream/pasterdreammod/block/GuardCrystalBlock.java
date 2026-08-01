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

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hitResult) {
        double x = pos.getX();
        double y = pos.getY();
        double z = pos.getZ();
        if (!level.isClientSide()) {
            level.playSound(null, pos, SoundEvents.BEACON_ACTIVATE, SoundSource.NEUTRAL, 3, 1.2f);
        }
        ServerScheduler.schedule(3, () -> {
            setAnimation(level, pos, 0);
            setAnimation(level, pos, 1);
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
                if (!level.isClientSide()) {
                    level.explode(null, x + 0.5, y + 0.5, z + 0.5, 3, Level.ExplosionInteraction.TNT);
                }
                level.destroyBlock(pos, false);
                ServerScheduler.schedule(1, () -> level.destroyBlock(pos, false));
            });
        });
        return InteractionResult.SUCCESS;
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
