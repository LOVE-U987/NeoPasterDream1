package com.pasterdream.pasterdreammod.entity.projectile;

import com.pasterdream.pasterdreammod.api.entity.base.GeckoLibProjectileEntity;
import com.pasterdream.pasterdreammod.registry.PDParticles;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import com.pasterdream.pasterdreammod.api.entity.damage.DamageImmunityConfig;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;

/**
 * 暗影魔法球 (Shadow Magicball) —— 追踪玩家的爆炸弹射物
 * <p>
 * 行为要点：
 * <ul>
 *   <li>生成后 35 tick 内自动追踪最近玩家</li>
 *   <li>命中实体或方块时触发爆炸</li>
 *   <li>不伤害发射者（BOSS）与暗影系友军，不追踪创造模式玩家</li>
 *   <li>生成暗影石粒子与烟雾尾迹</li>
 * </ul>
 * <p>
 * 渲染：GeckoLib 动画实体，默认纹理 "shadow_magicball"，循环播放 "fly" 动画
 */
public class ShadowMagicballEntity extends GeckoLibProjectileEntity {

    /** 暗影系实体标签（排除友军，避免误炸 BOSS / 暗影召唤物） */
    private static final TagKey<EntityType<?>> SHADOW_MOB_TAG =
            TagKey.create(Registries.ENTITY_TYPE,
                    ResourceLocation.fromNamespaceAndPath("pasterdream", "shadow_mob"));

    private boolean hasExploded = false;
    private int lifespanTicks = 0;
    private static final int MAX_LIFESPAN = 35;

    public ShadowMagicballEntity(EntityType<? extends Projectile> type, Level level) {
        super(type, level);
        this.setNoGravity(true);
    }

    @Override
    protected String getDefaultTexture() {
        return "shadow_magicball";
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
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
            if (this.lifespanTicks >= MAX_LIFESPAN) {
                this.triggerExplosion();
                return;
            }
        }
        // 粒子尾迹
        if (!this.hasExploded && this.level() instanceof ServerLevel sl) {
            sl.sendParticles((net.minecraft.core.particles.SimpleParticleType) PDParticles.SHADOW_STONE_PARTICLE.particleType(), this.getX(), this.getY(), this.getZ(), 4, 0.2, 0.2, 0.2, 0.1);
            sl.sendParticles(ParticleTypes.SMOKE, this.getX(), this.getY(), this.getZ(), 4, 0.2, 0.2, 0.2, 0.1);
        }
        if (!this.hasExploded) {
            // 服务端：追踪 + 手动移动（Projectile 基类 tick 不移动实体，必须自行推进）
            if (!this.level().isClientSide()) {
                this.trackPlayer();
                this.moveProjectile();
            }
            // 实体近身检测爆炸
            this.detectAndTriggerExplosion();
        }
    }

    /**
     * 手动推进飞弹并检测方块碰撞。
     * <p>
     * 关键：{@code Projectile} 基类的 {@code tick()} 只调用 {@code baseTick()}，<b>不会</b>自动执行
     * {@code move(getDeltaMovement())}（该逻辑在 {@code AbstractArrow} / {@code AbstractHurtingProjectile}
     * 等子类中）。本类直接继承 {@code Projectile}，因此必须在此手动移动，否则飞弹原地不动。
     * <p>
     * 流程：方块射线检测（clip）→ 命中触发爆炸；未命中则推进位置并让飞弹朝向移动方向。
     */
    private void moveProjectile() {
        Vec3 delta = this.getDeltaMovement();
        if (delta.lengthSqr() < 1e-8) return;

        // 方块碰撞检测：从当前位置沿速度方向发射线，命中方块则爆炸
        Vec3 start = this.position();
        ClipContext ctx = new ClipContext(start, start.add(delta),
                ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this);
        HitResult hit = this.level().clip(ctx);
        if (hit.getType() != HitResult.Type.MISS) {
            this.onHit(hit);
            return;
        }

        // 推进位置
        this.setPos(start.add(delta));

        // 朝向移动方向（视觉，供 fly 动画对齐）
        this.setYRot((float) (Mth.atan2(delta.z, delta.x) * (180.0 / Math.PI)) - 90.0F);
        this.setXRot((float) (Mth.atan2(delta.y, Math.sqrt(delta.x * delta.x + delta.z * delta.z))
                * (180.0 / Math.PI)));
    }

    /**
     * 追踪最近的非创造玩家；若仅剩创造玩家则保持当前方向直线飞行。
     * <p>
     * 不使用 {@code getNearestPlayer}——它内部基于 {@code TargetingConditions.forCombat()} 做
     * 视线（line-of-sight）检查，竞技场封闭结构下飞弹到玩家的视线经常被阻挡，导致返回 null、
     * 飞弹完全不追踪。改为遍历场内玩家手动取最近存活非创造玩家，不依赖视线。
     */
    private void trackPlayer() {
        Player nearest = null;
        double bestDistSq = 64.0 * 64.0;
        for (Player p : this.level().players()) {
            if (!p.isAlive() || p.isCreative()) continue;
            double distSq = this.distanceToSqr(p);
            if (distSq < bestDistSq) {
                bestDistSq = distSq;
                nearest = p;
            }
        }
        if (nearest == null) return;

        Vec3 toTarget = nearest.getEyePosition(1).subtract(this.position()).normalize();
        Vec3 current = this.getDeltaMovement();
        Vec3 newMotion;
        if (current.lengthSqr() < 1e-4) {
            // 无初速（防御性）：直接朝目标
            newMotion = toTarget.scale(3.0);
        } else {
            // 平滑转向：7 成保留原速方向，3 成偏转目标，保持恒速 3.0
            newMotion = current.scale(0.7).add(toTarget.scale(0.3)).normalize().scale(3.0);
        }
        this.setDeltaMovement(newMotion);
    }

    /**
     * 检测近身目标并爆炸。
     * <p>
     * 排除发射者（BOSS）、暗影系友军与创造模式玩家，避免飞弹在 BOSS 身前
     * 刚生成时把 BOSS 本体误判为目标而原地爆炸。
     */
    private void detectAndTriggerExplosion() {
        Vec3 center = new Vec3(this.getX(), this.getY(), this.getZ());
        for (Entity target : this.level().getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(1.5))) {
            if (target != this && target.isAlive() && target instanceof LivingEntity
                    && target != this.getOwner()
                    && !target.getType().is(SHADOW_MOB_TAG)
                    && !(target instanceof Player p && p.isCreative())) {
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
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        super.registerControllers(controllers);
        controllers.add(new AnimationController<>(this, "movement", 4, state ->
                state.setAndContinue(RawAnimation.begin().thenLoop("fly"))));
    }
}