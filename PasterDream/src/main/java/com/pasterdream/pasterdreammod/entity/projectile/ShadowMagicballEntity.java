package com.pasterdream.pasterdreammod.entity.projectile;

import com.pasterdream.pasterdreammod.registry.PDParticles;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import com.pasterdream.pasterdreammod.entity.damage.DamageImmunityConfig;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.util.GeckoLibUtil;

public class ShadowMagicballEntity extends Projectile implements GeoEntity {

    public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(ShadowMagicballEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(ShadowMagicballEntity.class, EntityDataSerializers.STRING);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private boolean hasExploded = false;
    private int lifespanTicks = 0;
    private static final int MAX_LIFESPAN = 35;

    public ShadowMagicballEntity(EntityType<? extends ShadowMagicballEntity> type, Level level) {
        super(type, level);
        this.setNoGravity(true);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(ANIMATION, "empty");
        builder.define(TEXTURE, "shadow_magicball");
    }

    public String getTexture() { return this.entityData.get(TEXTURE); }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putString("Texture", this.getTexture());
        compound.putBoolean("HasExploded", this.hasExploded);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("HasExploded")) this.hasExploded = compound.getBoolean("HasExploded");
        if (compound.contains("LifespanTicks")) this.lifespanTicks = compound.getInt("LifespanTicks");
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.hasExploded) {
            this.lifespanTicks++;
            if (this.lifespanTicks >= MAX_LIFESPAN) this.triggerExplosion();
        }
        if (!this.hasExploded && this.level() instanceof ServerLevel sl) {
            sl.sendParticles((net.minecraft.core.particles.SimpleParticleType) PDParticles.SHADOW_STONE_PARTICLE.particleType(), this.getX(), this.getY(), this.getZ(), 4, 0.2, 0.2, 0.2, 0.1);
            sl.sendParticles(ParticleTypes.SMOKE, this.getX(), this.getY(), this.getZ(), 4, 0.2, 0.2, 0.2, 0.1);
        }
        if (!this.hasExploded) this.detectAndTriggerExplosion();
        if (!this.hasExploded && !this.level().isClientSide()) this.trackPlayer();
    }

    private void trackPlayer() {
        Player nearest = this.level().getNearestPlayer(this, 64.0);
        if (nearest != null) {
            Vec3 look = nearest.getEyePosition(1).subtract(this.position()).normalize();
            Vec3 current = this.getDeltaMovement();
            Vec3 newMotion = current.add(look.scale(0.05)).normalize().scale(3.0);
            this.setDeltaMovement(newMotion);
        }
    }

    private void detectAndTriggerExplosion() {
        Vec3 center = new Vec3(this.getX(), this.getY(), this.getZ());
        for (Entity target : this.level().getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(1.5))) {
            if (target != this && target.isAlive() && target instanceof LivingEntity) {
                this.triggerExplosion();
                break;
            }
        }
    }

    private void triggerExplosion() {
        if (this.hasExploded) return;
        this.hasExploded = true;
        this.level().explode(this, this.getX(), this.getY(), this.getZ(), 2.0f, Level.ExplosionInteraction.MOB);
        if (this.level() instanceof ServerLevel sl) {
            sl.sendParticles((net.minecraft.core.particles.SimpleParticleType) PDParticles.SHADOW_STONE_PARTICLE.particleType(), this.getX(), this.getY(), this.getZ(), 64, 3.0, 1.0, 3.0, 0.3);
            sl.sendParticles(ParticleTypes.SMOKE, this.getX(), this.getY(), this.getZ(), 64, 3.0, 1.0, 3.0, 0.3);
        }
        this.discard();
    }

    @Override
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
        if (!this.hasExploded) this.triggerExplosion();
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (DamageImmunityConfig.getInstance().isImmune(this, source)) {
            return false;
        }
        return super.hurt(source, amount);
    }

    @Override
    public void registerControllers(software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new software.bernie.geckolib.animation.AnimationController<>(this, "movement", 4, state ->
                state.setAndContinue(software.bernie.geckolib.animation.RawAnimation.begin().thenLoop("fly"))));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return this.cache; }
}