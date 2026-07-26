package com.pasterdream.pasterdreammod.block;

import com.mojang.serialization.MapCodec;
import com.pasterdream.pasterdreammod.block.entity.W4DataBlockEntity;
import com.pasterdream.pasterdreammod.block.entity.W4GeoDataBlockEntity;
import com.pasterdream.pasterdreammod.registry.PDBlockEntitiesFurniture;
import com.pasterdream.pasterdreammod.registry.PDEntities;
import com.pasterdream.pasterdreammod.registry.PDSounds;
import com.pasterdream.pasterdreammod.registry.blocks.PDBlocksDungeon;
import com.pasterdream.pasterdreammod.registry.items.PDItemsMaterials;
import com.pasterdream.pasterdreammod.util.ServerScheduler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.item.ItemEntity;
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
 * 阴影火盆（shadow_brazier）
 * <p>
 * 忠实还原原版 {@code ShadowBrazierBlock + ShadowBrazierPr0/Pr1}：
 * <ul>
 *   <li>右键（Pr0）：主手影烛点燃火盆（switch=true + 邻居更新 + 两段叙事消息 +
 *       flintandsteel 音效，5t 后 shadow0、20t 后 shadow_music_0）；</li>
 *   <li>tick（Pr1）：点燃后每 20 tick time+1 并保持 animation=1，
 *       按时间轴召唤敌潮——1：25 格内玩家黑暗；6/65/105：噬影鸦×2；
 *       15/50/95：暗影之手×4；40/80：暗影傀儡；
 *       ≥120：叙事 + 5t 后火盆碎裂，掉落地牢钥匙与黑金属粒。</li>
 * </ul>
 * 不可破坏、石质音效、noOcclusion、GeckoLib 渲染，形状 (1,1,1,15,5,15)。
 */
public class ShadowBrazierBlock extends BaseEntityBlock {

    public static final MapCodec<ShadowBrazierBlock> CODEC = simpleCodec(ShadowBrazierBlock::new);

    public static final IntegerProperty ANIMATION = IntegerProperty.create("animation", 0, 2);

    /**
     * 构造阴影火盆方块
     *
     * @param properties 方块属性
     */
    public ShadowBrazierBlock(Properties properties) {
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
        return box(1, 1, 1, 15, 5, 15);
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

    // ==================== tick 敌潮（原 ShadowBrazierPr1Procedure） ====================

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean moving) {
        super.onPlace(state, level, pos, oldState, moving);
        level.scheduleTick(pos, this, 20);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.tick(state, level, pos, random);
        if (W4DataBlockEntity.getBooleanAt(level, pos, "switch")) {
            double time = W4DataBlockEntity.getDoubleAt(level, pos, "time") + 1;
            W4DataBlockEntity.putDoubleAt(level, pos, "time", time);
            setAnimation(level, pos, 1);

            int x = pos.getX();
            int y = pos.getY();
            int z = pos.getZ();
            if (time == 1) {
                applyDarknessNearby(level, pos);
            }
            if (time == 6 || time == 65 || time == 105) {
                // 105 时原版先 -x 后 +x，其余先 +x 后 -x —— 生成结果一致
                spawnAt(level, PDEntities.TERRORBEAK.get(), x + 7, y - 2, z);
                spawnAt(level, PDEntities.TERRORBEAK.get(), x - 7, y - 2, z);
            }
            if (time == 15 || time == 50 || time == 95) {
                spawnAt(level, PDEntities.SHADOW_HAND.get(), x, y, z + 7);
                spawnAt(level, PDEntities.SHADOW_HAND.get(), x, y, z - 7);
                spawnAt(level, PDEntities.SHADOW_HAND.get(), x + 7, y, z);
                spawnAt(level, PDEntities.SHADOW_HAND.get(), x - 7, y, z);
            }
            if (time == 40) {
                spawnAt(level, PDEntities.SHADOW_GOLEM.get(), x, y - 2, z + 7);
            }
            if (time == 80) {
                spawnAt(level, PDEntities.SHADOW_GOLEM.get(), x, y - 2, z - 7);
            }
            if (time >= 120) {
                Vec3 center = new Vec3(x, y, z);
                for (Entity entity : level.getEntitiesOfClass(Entity.class,
                        new AABB(center, center).inflate(25 / 2d), e -> true)) {
                    if (entity instanceof Player player && !player.level().isClientSide()) {
                        player.displayClientMessage(Component.literal("火盆燃尽熄灭，坠落在地上破碎"), false);
                        player.displayClientMessage(Component.literal("在火盆的夹层里掉落出一把钥匙"), false);
                    }
                }
                ServerScheduler.schedule(5, () -> {
                    level.destroyBlock(pos, false);
                    dropItem(level, pos, new ItemStack(PDItemsMaterials.SHADOW_DUNGEON_KEY.get()));
                    dropItem(level, pos, new ItemStack(PDItemsMaterials.BLACKMETAL_GRAIN.get()));
                });
            }
        }
        level.scheduleTick(pos, this, 20);
    }

    /** 25 格立方范围内玩家施加黑暗 60t */
    private static void applyDarknessNearby(ServerLevel level, BlockPos pos) {
        Vec3 center = new Vec3(pos.getX(), pos.getY(), pos.getZ());
        for (Entity entity : level.getEntitiesOfClass(Entity.class,
                new AABB(center, center).inflate(25 / 2d), e -> true)) {
            if (entity instanceof Player && entity instanceof LivingEntity living
                    && !living.level().isClientSide()) {
                living.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 60, 0));
            }
        }
    }

    private static void spawnAt(ServerLevel level, EntityType<?> type, double x, double y, double z) {
        Entity entity = type.spawn(level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
        if (entity != null) {
            entity.setYRot(level.getRandom().nextFloat() * 360F);
        }
    }

    private static void dropItem(ServerLevel level, BlockPos pos, ItemStack stack) {
        ItemEntity item = new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(), stack);
        item.setPickUpDelay(10);
        level.addFreshEntity(item);
    }

    // ==================== 右键点燃（原 ShadowBrazierPr0Procedure） ====================

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hitResult) {
        if (player.getMainHandItem().getItem() == PDBlocksDungeon.SHADOWCANDLE.get().asItem()) {
            W4DataBlockEntity.putBooleanAt(level, pos, "switch", true);
            level.updateNeighborsAt(pos, level.getBlockState(pos).getBlock());
            if (!player.level().isClientSide()) {
                player.displayClientMessage(Component.literal("火盆被点燃，但带来的并不是光明..."), false);
                player.displayClientMessage(Component.literal("阴影从四周蔓延开来"), false);
            }
            player.swing(InteractionHand.MAIN_HAND, true);
            if (!level.isClientSide()) {
                level.playSound(null, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.NEUTRAL, 1, 1);
            }
            ServerScheduler.schedule(5, () -> {
                if (!level.isClientSide()) {
                    level.playSound(null, pos, PDSounds.SHADOW_0.get(), SoundSource.NEUTRAL, 1, 1);
                }
            });
            ServerScheduler.schedule(20, () -> {
                if (!level.isClientSide()) {
                    level.playSound(null, pos, PDSounds.SHADOW_MUSIC_0.get(), SoundSource.NEUTRAL, 1, 1);
                }
            });
        } else if (!player.level().isClientSide()) {
            player.displayClientMessage(Component.literal("需要用阴影蜡烛点燃火盆"), true);
        }
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
        return new W4GeoDataBlockEntity(PDBlockEntitiesFurniture.SHADOW_BRAZIER.get(), pos, state);
    }
}
