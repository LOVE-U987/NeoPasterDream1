package com.pasterdream.pasterdreammod.block.entity;

import com.pasterdream.pasterdreammod.registry.PDBlockEntities;
import com.pasterdream.pasterdreammod.registry.PDBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

/**
 * 暗影漩涡方块实体
 * <p>
 * BOSS 右手涡流技能生成的临时方块实体，使用 GeckoLib 渲染旋转漩涡动画。
 * 放置后持续约 5 秒（100 tick）自动消失。
 * 对附近实体造成伤害并施加负面效果。
 * <p>
 * 伤害范围：9格（AABB膨胀4.5格）
 * 伤害值：3点魔法伤害
 * 负面效果：黑暗(20tick)、混乱(20tick)、减速(20tick)
 * <p>
 * 对应方块 {@link com.pasterdream.pasterdreammod.registry.PDBlocks#SHADOW_VORTEX}。
 */
public class ShadowVortexBlockEntity extends BlockEntity implements GeoBlockEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    /** 漩涡存活时间（tick），约 5 秒 */
    private static final int LIFETIME = 100;

    /** 伤害范围（格） */
    private static final double DAMAGE_RANGE = 9.0;

    /** 伤害值 */
    private static final float DAMAGE_AMOUNT = 3.0f;

    /** 已存在的 tick 数 */
    private int age = 0;

    /**
     * 构造暗影漩涡方块实体
     *
     * @param pos   方块位置
     * @param state 方块状态
     */
    public ShadowVortexBlockEntity(BlockPos pos, BlockState state) {
        super(PDBlockEntities.SHADOW_VORTEX.get(), pos, state);
    }

    /**
     * 客户端 tick —— 用于动画和计时
     * 服务端 tick —— 处理伤害逻辑和生命周期
     */
    public void tick() {
        if (level == null) return;

        age++;

        // 服务端逻辑：伤害和粒子
        if (!level.isClientSide && level instanceof ServerLevel serverLevel) {
            // 每 tick 对范围内实体造成伤害
            hurtNearbyEntities(serverLevel);

            // 每几 tick 生成粒子效果
            if (age % 2 == 0) {
                spawnParticles(serverLevel);
            }

            // 到达生命周期后移除方块
            if (age >= LIFETIME) {
                serverLevel.removeBlock(worldPosition, false);
            }
        }
    }

    /**
     * 对附近实体造成伤害
     * <p>
     * 伤害范围：9格（AABB膨胀4.5格）
     * 伤害类型：魔法伤害
     * 负面效果：黑暗(20tick)、混乱(20tick)、减速(20tick)
     * 排除：特殊实体标签和暗影生物标签
     */
    private void hurtNearbyEntities(ServerLevel serverLevel) {
        Vec3 center = new Vec3(
                worldPosition.getX() + 0.5,
                worldPosition.getY(),
                worldPosition.getZ() + 0.5
        );

        AABB aabb = new AABB(center, center).inflate(DAMAGE_RANGE / 2.0);

        List<Entity> entities = serverLevel.getEntitiesOfClass(Entity.class, aabb, e -> true);

        TagKey<net.minecraft.world.entity.EntityType<?>> specialEntityTag =
                TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath("pasterdream", "special_entity_tag"));
        TagKey<net.minecraft.world.entity.EntityType<?>> shadowMobTag =
                TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath("pasterdream", "shadow_mob"));

        DamageSource damageSource = new DamageSource(
                serverLevel.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
                        .getHolderOrThrow(DamageTypes.MAGIC)
        );

        for (Entity entity : entities) {
            // 排除特殊实体和暗影生物
            if (entity.getType().is(specialEntityTag) || entity.getType().is(shadowMobTag)) {
                continue;
            }

            // 对生物实体造成伤害和负面效果
            if (entity instanceof LivingEntity livingEntity) {
                // 施加负面效果
                livingEntity.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 20, 0));
                livingEntity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 20, 1));
                livingEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 0));

                // 造成伤害（跳过创造模式玩家）
                if (!(entity instanceof Player player) || !player.isCreative()) {
                    livingEntity.hurt(damageSource, DAMAGE_AMOUNT);
                }
            }
        }
    }

    /**
     * 生成粒子效果
     */
    private void spawnParticles(ServerLevel serverLevel) {
        double x = worldPosition.getX() + 0.5;
        double y = worldPosition.getY();
        double z = worldPosition.getZ() + 0.5;

        // 暗影石粒子
        serverLevel.sendParticles(
                com.pasterdream.pasterdreammod.registry.PDParticles.SHADOW_STONE_PARTICLE.holder().get(),
                x, y, z, 8, 1.5, 0.5, 1.5, 0.1
        );

        // 烟雾粒子
        serverLevel.sendParticles(ParticleTypes.SMOKE, x, y, z, 8, 1.5, 0.5, 1.5, 0.1);
    }

    /**
     * 动画谓词 —— 循环播放漩涡动画
     *
     * @param state 动画状态
     * @return 播放状态
     */
    private PlayState idlePredicate(AnimationState<ShadowVortexBlockEntity> state) {
        return state.setAndContinue(RawAnimation.begin().thenLoop("0"));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "vortex", 0, this::idlePredicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}