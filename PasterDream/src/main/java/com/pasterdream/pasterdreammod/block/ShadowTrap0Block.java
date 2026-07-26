package com.pasterdream.pasterdreammod.block;

import com.mojang.serialization.MapCodec;
import com.pasterdream.pasterdreammod.attachment.PDAttachments;
import com.pasterdream.pasterdreammod.block.entity.W4DataBlockEntity;
import com.pasterdream.pasterdreammod.block.entity.W4GeoDataBlockEntity;
import com.pasterdream.pasterdreammod.registry.PDBlockEntitiesFurniture;
import com.pasterdream.pasterdreammod.registry.PDEntities;
import com.pasterdream.pasterdreammod.registry.PDSounds;
import com.pasterdream.pasterdreammod.util.ServerScheduler;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
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
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * 阴影陷阱（shadow_trap_0）
 * <p>
 * 忠实还原原版 {@code ShadowTrap0Block + ShadowTrap0Pr0/Pr1Procedure}：
 * <ul>
 *   <li>entityInside：玩家首次踩入（switch 未置位）→ 置位 + animation=1 +
 *       陷阱音效 + 5 点魔法伤害 + 理智 -5 + 黑暗 80t/缓慢 II 20t +
 *       makeStuckInBlock 束缚，25 tick 后自身消失；</li>
 *   <li>被玩家破坏时（onDestroyedByPlayer）刷出一只暗影之手并 -1 理智。</li>
 * </ul>
 * 无碰撞、砂砾音效、GeckoLib 渲染，形状 (0,0,0,16,1,16)。
 */
public class ShadowTrap0Block extends BaseEntityBlock {

    public static final MapCodec<ShadowTrap0Block> CODEC = simpleCodec(ShadowTrap0Block::new);

    public static final IntegerProperty ANIMATION = IntegerProperty.create("animation", 0, 2);

    /**
     * 构造阴影陷阱方块
     *
     * @param properties 方块属性
     */
    public ShadowTrap0Block(Properties properties) {
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
        return box(0, 0, 0, 16, 1, 16);
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

    // ==================== 破坏触发（原 ShadowTrap0Pr1Procedure） ====================

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos,
                                       Player player, boolean willHarvest, FluidState fluid) {
        boolean result = super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
        if (level instanceof ServerLevel serverLevel) {
            Entity hand = PDEntities.SHADOW_HAND.get().spawn(serverLevel,
                    BlockPos.containing(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5),
                    MobSpawnType.MOB_SUMMONED);
            if (hand != null) {
                hand.setYRot(serverLevel.getRandom().nextFloat() * 360F);
            }
        }
        PDAttachments.addPlayerSanWithCheck(player, -1);
        return result;
    }

    // ==================== 踩入触发（原 ShadowTrap0Pr0Procedure） ====================

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        super.entityInside(state, level, pos, entity);
        if (W4DataBlockEntity.getBooleanAt(level, pos, "switch") || !(entity instanceof Player)) {
            return;
        }
        W4DataBlockEntity.putBooleanAt(level, pos, "switch", true);
        setAnimation(level, pos, 0);
        setAnimation(level, pos, 1);
        if (!level.isClientSide()) {
            level.playSound(null, pos, PDSounds.SHADOW_TRAP_0.get(), SoundSource.NEUTRAL, 1, 1);
        }
        entity.hurt(level.damageSources().magic(), 5);
        if (entity instanceof Player player) {
            PDAttachments.addPlayerSanWithCheck(player, -5);
        }
        if (entity instanceof LivingEntity living && !living.level().isClientSide()) {
            living.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 80, 0));
            living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 1));
        }
        entity.makeStuckInBlock(Blocks.AIR.defaultBlockState(), new Vec3(0.25, 0.05, 0.25));
        ServerScheduler.schedule(25, () -> level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3));
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
        return new W4GeoDataBlockEntity(PDBlockEntitiesFurniture.SHADOW_TRAP_0.get(), pos, state);
    }
}
