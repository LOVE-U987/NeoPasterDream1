package com.pasterdream.pasterdreammod.entity.projectile;

import com.pasterdream.pasterdreammod.registry.PDEntities;
import com.pasterdream.pasterdreammod.registry.PDSounds;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class SquealWaveProjectileEntity extends Projectile {

    public SquealWaveProjectileEntity(EntityType<? extends SquealWaveProjectileEntity> type, Level level) {
        super(type, level);
        this.setNoGravity(true);
        this.setSilent(true);
    }

    public SquealWaveProjectileEntity(EntityType<? extends SquealWaveProjectileEntity> type, LivingEntity owner, Level level) {
        super(type, level);
        this.setOwner(owner);
        this.setNoGravity(true);
        this.setSilent(true);
        this.setPos(owner.getX(), owner.getY() + owner.getEyeHeight(), owner.getZ());
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
    }

    @Override
    public void tick() {
        super.tick();
        // ⚡ 移动逻辑：客户端+服务端都执行，确保能看到飞行轨迹
        Vec3 motion = this.getDeltaMovement();
        this.setPos(this.getX() + motion.x, this.getY() + motion.y, this.getZ() + motion.z);

        if (!this.level().isClientSide) {
            // 超过存活时间自动消失
            if (this.tickCount > 60) {
                this.discard();
                return;
            }
            // 检测方块碰撞（位置被方块占据则销毁）
            if (!this.level().getBlockState(this.blockPosition()).isAir()) {
                this.discard();
                return;
            }
            // 检测实体碰撞
            AABB box = this.getBoundingBox();
            for (Entity target : this.level().getEntitiesOfClass(Entity.class, box)) {
                if (target != this && target != this.getOwner() && target.isAlive() && target instanceof LivingEntity) {
                    if (this.getOwner() instanceof LivingEntity owner) {
                        target.hurt(this.damageSources().mobAttack(owner), 0.5F);
                    }
                    this.discard();
                    return;
                }
            }
        }
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide) {
            this.discard();
        }
    }

    public static SquealWaveProjectileEntity shoot(LivingEntity entity, LivingEntity target) {
        var entityType = PDEntities.SQUEAL_WAVE_PROJECTILE.get();
        SquealWaveProjectileEntity projectile = new SquealWaveProjectileEntity(entityType, entity, entity.level());
        double dx = target.getX() - entity.getX();
        double dy = target.getY() + target.getEyeHeight() - 1.1;
        double dz = target.getZ() - entity.getZ();
        // 计算方向向量并乘以速度
        Vec3 dir = new Vec3(dx, dy - projectile.getY() + Math.hypot(dx, dz) * 0.2F, dz).normalize();
        projectile.setDeltaMovement(dir.scale(0.8F));
        projectile.setSilent(true);
        entity.level().addFreshEntity(projectile);
        entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                PDSounds.SQUEAL_WAVE.get(), SoundSource.HOSTILE, 1.0F, 1.0F);
        return projectile;
    }
}