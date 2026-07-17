package com.pasterdream.pasterdreammod.entity.mob;

import com.pasterdream.pasterdreammod.api.entity.base.GeckoLibMonsterEntity;
import com.pasterdream.pasterdreammod.registry.PDSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.sounds.SoundSource;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;

import java.util.EnumSet;

/**
 * 暗影之手 (Shadow Hand) — 快速飞行近战攻击幽灵生物
 * <p>
 * AI 行为：
 * <ul>
 *   <li>高速度（1.4）飞行追踪玩家</li>
 *   <li>三维飞行移动（FlyingMoveControl + FlyingPathNavigation + 无重力）</li>
 *   <li>近战接触攻击，16 格内开始追踪</li>
 *   <li>免疫火焰、仙人掌伤害</li>
 *   <li>触碰玩家时造成额外效果（原模组通过 ShadowHandPr0Procedure 实现）</li>
 * </ul>
 * <p>
 * 动画：movement(idle/walk) | attacking(attack) | procedure(触发式)
 */
public class ShadowHandEntity extends GeckoLibMonsterEntity {

    private boolean swinging;
    private long lastSwing;

    private int chargeCooldown = 0;
    private boolean isCharging = false;
    private int chargeTicks = 0;

    public ShadowHandEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.moveControl = new FlyingMoveControl(this, 10, true);
        this.setNoGravity(true);
        this.xpReward = 1;
    }

    @Override
    protected String getDefaultTexture() {
        return "shadow_hand";
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        FlyingPathNavigation navigation = new FlyingPathNavigation(this, level);
        navigation.setCanOpenDoors(false);
        navigation.setCanFloat(true);
        navigation.setCanPassDoors(true);
        return navigation;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.4)
                .add(Attributes.MAX_HEALTH, 12.0)
                .add(Attributes.ARMOR, 0.0)
                .add(Attributes.ATTACK_DAMAGE, 8.0)
                .add(Attributes.FOLLOW_RANGE, 16.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 4.0)
                .add(Attributes.FLYING_SPEED, 0.4);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        this.goalSelector.addGoal(1, new Goal() {
            {
                this.setFlags(EnumSet.of(Goal.Flag.MOVE));
            }

            @Override
            public boolean canUse() {
                if (ShadowHandEntity.this.getTarget() != null && !ShadowHandEntity.this.getMoveControl().hasWanted()) {
                    return true;
                }
                return false;
            }

            @Override
            public boolean canContinueToUse() {
                return ShadowHandEntity.this.getMoveControl().hasWanted()
                        && ShadowHandEntity.this.getTarget() != null
                        && ShadowHandEntity.this.getTarget().isAlive();
            }

            @Override
            public void start() {
                LivingEntity target = ShadowHandEntity.this.getTarget();
                if (target != null) {
                    Vec3 vec3d = target.getEyePosition(1);
                    ShadowHandEntity.this.moveControl.setWantedPosition(vec3d.x, vec3d.y, vec3d.z, 1.4);
                }
            }

            @Override
            public void tick() {
                LivingEntity target = ShadowHandEntity.this.getTarget();
                if (target == null) return;
                if (ShadowHandEntity.this.getBoundingBox().intersects(target.getBoundingBox())) {
                    ShadowHandEntity.this.doHurtTarget(target);
                } else {
                    double distanceSqr = ShadowHandEntity.this.distanceToSqr(target);
                    if (distanceSqr < 16) {
                        Vec3 vec3d = target.getEyePosition(1);
                        ShadowHandEntity.this.moveControl.setWantedPosition(vec3d.x, vec3d.y, vec3d.z, 1.4);
                    }
                }
            }
        });

        this.goalSelector.addGoal(2, new RandomStrollGoal(this, 0.8, 20) {
            @Override
            protected Vec3 getPosition() {
                RandomSource random = ShadowHandEntity.this.getRandom();
                double dirX = ShadowHandEntity.this.getX() + ((random.nextFloat() * 2 - 1) * 16);
                double dirY = ShadowHandEntity.this.getY() + ((random.nextFloat() * 2 - 1) * 16);
                double dirZ = ShadowHandEntity.this.getZ() + ((random.nextFloat() * 2 - 1) * 16);
                return new Vec3(dirX, dirY, dirZ);
            }
        });

        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.5, false));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Player.class, false, false));
        this.targetSelector.addGoal(6, new HurtByTargetGoal(this).setAlertOthers());
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypes.IN_FIRE)) return false;
        if (source.is(DamageTypes.ON_FIRE)) return false;
        if (source.is(DamageTypes.LAVA)) return false;
        if (source.is(DamageTypes.CACTUS)) return false;
        return super.hurt(source, amount);
    }

    @Override
    public void playerTouch(Player sourceentity) {
        super.playerTouch(sourceentity);
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    @Override
    public void aiStep() {
        super.aiStep();
        this.updateSwingTime();
        this.setNoGravity(true);
    }

    @Override
    public void playStepSound(BlockPos pos, BlockState blockIn) {
    }

    @Override
    public void baseTick() {
        super.baseTick();
        this.refreshDimensions();
        if (!this.level().isClientSide()) {
            serverChargeTick();
        }
    }

    private void serverChargeTick() {
        if (chargeCooldown > 0) {
            chargeCooldown--;
        }

        if (isCharging) {
            chargeTicks++;
            if (chargeTicks >= 10) {
                isCharging = false;
                chargeTicks = 0;
                this.setAnimation("empty");
            }
            return;
        }

        if (chargeCooldown > 0 || this.getTarget() == null) return;

        if (!this.hasEffect(MobEffects.CONFUSION) && this.getTarget().isAlive()) {
            double dist = this.distanceToSqr(this.getTarget());
            if (dist < 64.0) {
                this.level().playSound(null, this.blockPosition(),
                        PDSounds.SHADOW_0.get(), SoundSource.HOSTILE, 1.0F, 1.0F);
                this.setAnimation("attack");
                isCharging = true;
                chargeTicks = 0;
                chargeCooldown = 60;
                Vec3 dir = this.getTarget().position().subtract(this.position()).normalize();
                this.setDeltaMovement(dir.x * 0.6, dir.y * 0.6, dir.z * 0.6);
                this.hasImpulse = true;
            }
        }
    }

    @Override
    protected void tickDeath() {
        ++this.deathTime;
        if (this.deathTime == 10) {
            this.remove(RemovalReason.KILLED);
            this.dropExperience(this.getLastHurtByMob());
        }
    }

    private PlayState movementPredicate(AnimationState<ShadowHandEntity> state) {
        if (this.getSyncedAnimation().equals("empty")) {
            if (state.isMoving() || !(state.getLimbSwingAmount() > -0.15F && state.getLimbSwingAmount() < 0.15F)) {
                return state.setAndContinue(RawAnimation.begin().thenLoop("walk"));
            }
            return state.setAndContinue(RawAnimation.begin().thenLoop("idle"));
        }
        return PlayState.STOP;
    }

    private PlayState attackingPredicate(AnimationState<ShadowHandEntity> state) {
        double d1 = this.getX() - this.xOld;
        double d0 = this.getZ() - this.zOld;
        float velocity = (float) Math.sqrt(d1 * d1 + d0 * d0);
        if (getAttackAnim(state.getPartialTick()) > 0f && !this.swinging) {
            this.swinging = true;
            this.lastSwing = level().getGameTime();
        }
        if (this.swinging && this.lastSwing + 7L <= level().getGameTime()) {
            this.swinging = false;
        }
        if (this.swinging && state.getController().getAnimationState() == AnimationController.State.STOPPED) {
            state.getController().forceAnimationReset();
            return state.setAndContinue(RawAnimation.begin().thenPlay("attack"));
        }
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        super.registerControllers(controllers);
        controllers.add(new AnimationController<>(this, "movement", 4, this::movementPredicate));
        controllers.add(new AnimationController<>(this, "attacking", 4, this::attackingPredicate));
    }
}