package com.pasterdream.pasterdreammod.entity.mob;

import com.pasterdream.pasterdreammod.entity.damage.ConfigurableImmunityEntity;
import com.pasterdream.pasterdreammod.registry.PDParticles;
import com.pasterdream.pasterdreammod.registry.PDSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;

import java.util.List;

/**
 * 狐火 (FoxFire) —— 染梦世界中飘浮的神秘火焰精灵
 * <p>
 * 行为要点：
 * <ul>
 *   <li>中立环境实体，继承 ConfigurableImmunityEntity（间接获得动画能力）</li>
 *   <li>setNoAi(true) — 完全静止，无任何 AI 行为</li>
 *   <li>不可推动，免疫几乎所有伤害类型</li>
 *   <li>生成后 400 tick（约 20 秒）自动消散</li>
 *   <li>每 tick 散发狐火粒子，对范围内实体施加效果</li>
 * </ul>
 * <p>
 * 渲染：GeckoLib 动画实体，默认纹理 "fox_fire"
 */
public class FoxFireEntity extends ConfigurableImmunityEntity {

    public FoxFireEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
        this.xpReward = 0;
        setNoAi(true);
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    protected String getDefaultTexture() {
        return "fox_fire";
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 2.0)
                .add(Attributes.ARMOR, 0.0)
                .add(Attributes.MOVEMENT_SPEED, 0.0)
                .add(Attributes.ATTACK_DAMAGE, 3.0)
                .add(Attributes.FOLLOW_RANGE, 16.0);
    }

    @Override
    protected void registerGoals() {
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected void doPush(Entity entityIn) {
    }

    @Override
    protected void pushEntities() {
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty,
                                        MobSpawnType reason, @Nullable SpawnGroupData livingdata) {
        SpawnGroupData retval = super.finalizeSpawn(world, difficulty, reason, livingdata);

        this.setYRot(0);
        this.setXRot(0);
        this.yBodyRot = this.getYRot();
        this.yHeadRot = this.getYRot();
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();
        this.yBodyRotO = this.getYRot();
        this.yHeadRotO = this.getYRot();

        if (!this.level().isClientSide()) {
            this.level().playSound(null, BlockPos.containing(this.getX(), this.getY(), this.getZ()),
                    PDSounds.FOX_FIRE.get(), SoundSource.NEUTRAL, 1.0f, 1.0f);
        }

        return retval;
    }

    @Override
    public void baseTick() {
        super.baseTick();
        double time = this.getPersistentData().getDouble("time");
        if (time >= 400) {
            if (!this.level().isClientSide()) {
                if (this.level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(
                            (SimpleParticleType) PDParticles.FOX_FIRE_0_PARTICLE.particleType(),
                            this.getX(), this.getY(), this.getZ(),
                            30, 3.0, 1.0, 3.0, 0.5
                    );
                    serverLevel.sendParticles(
                            (SimpleParticleType) PDParticles.FOX_FIRE_1_PARTICLE.particleType(),
                            this.getX(), this.getY(), this.getZ(),
                            30, 3.0, 1.0, 3.0, 0.5
                    );
                }
                this.level().playSound(null, this.blockPosition(),
                        SoundEvents.FIRE_EXTINGUISH, SoundSource.NEUTRAL, 0.8f, 1.2f);
                this.discard();
            }
        } else {
            this.getPersistentData().putDouble("time", time + 1);

            if (this.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(
                        (SimpleParticleType) PDParticles.FOX_FIRE_0_PARTICLE.particleType(),
                        this.getX(), this.getY(), this.getZ(),
                        5, 5.0, 0.15, 5.0, 1.0
                );
                serverLevel.sendParticles(
                        (SimpleParticleType) PDParticles.FOX_FIRE_1_PARTICLE.particleType(),
                        this.getX(), this.getY(), this.getZ(),
                        5, 5.0, 0.15, 5.0, 1.0
                );
            }

            Vec3 center = new Vec3(this.getX(), this.getY(), this.getZ());
            AABB aabb = new AABB(center, center).inflate(6.0 / 2.0);
            List<Entity> entities = this.level().getEntitiesOfClass(Entity.class, aabb, e -> true);

            for (Entity entity : entities) {
                if (entity instanceof Mob mob) {
                    if (mob instanceof LivingEntity living) {
                        living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 0));
                    }
                } else if (entity instanceof Player player) {
                    player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 0));
                }
            }
        }

        this.refreshDimensions();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState blockIn) {
    }

    @Override
    protected void tickDeath() {
        ++this.deathTime;
        if (this.deathTime == 20) {
            this.remove(RemovalReason.KILLED);
        }
    }

    private PlayState movementPredicate(AnimationState<FoxFireEntity> state) {
        if (this.getSyncedAnimation().equals("empty")) {
            return state.setAndContinue(RawAnimation.begin().thenLoop("idle"));
        }
        return PlayState.STOP;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        super.registerControllers(controllers);
        controllers.add(new AnimationController<>(this, "movement", 4, this::movementPredicate));
    }
}