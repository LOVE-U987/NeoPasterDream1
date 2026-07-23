package com.pasterdream.pasterdreammod.entity.mob;

import com.pasterdream.pasterdreammod.entity.damage.ConfigurableImmunityEntity;
import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.registry.PDParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;

/**
 * 暗影魔像 (Shadow Golem) — 150 血的精英怪物
 * <p>
 * AI 行为：
 * - 主动攻击玩家（20 攻击力）
 * - 免疫箭矢、药水、摔落、仙人掌
 * - 高击退抗性（0.7）
 * <p>
 * 技能系统：
 * - 每 tick 积累技能值，200 tick（~10秒）满时触发
 * - 被攻击额外 +10 技能值（加速技能释放）
 * - 技能动画序列：storage → 咆哮 → skill → 大范围爆炸
 * - 爆炸对 10 格内非自身实体造成 15 点伤害 + 击飞
 * <p>
 * 动画：
 * - movement: idle → walk → attack（基于状态切换）
 * - attacking: 手部挥击动画（触发式播放）
 * - procedure: 由技能系统触发的动画（storage / skill）
 */
public class ShadowGolemEntity extends ConfigurableImmunityEntity {

    private boolean swinging;
    private boolean lastloop;
    private long lastSwing;

    private double skillTime = 0;
    private int skillTimer = 0;

    private static final ResourceLocation ROAR_SOUND = ResourceLocation.fromNamespaceAndPath("pasterdream", "roar0");

    public ShadowGolemEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
        this.xpReward = 7;
    }

    @Override
    protected String getDefaultTexture() {
        return "shadow_golem";
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 150)
                .add(Attributes.ARMOR, 8)
                .add(Attributes.ATTACK_DAMAGE, 20)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.FOLLOW_RANGE, 20)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.7)
                .add(Attributes.ATTACK_KNOCKBACK, 0.5);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2, false));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
        this.goalSelector.addGoal(3, new RandomStrollGoal(this, 0.9));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Player.class, false, false));
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean result = super.hurt(source, amount);
        if (result && !level().isClientSide()) {
            if (skillTimer == 0 && skillTime < 189) {
                skillTime += 10;
            }
        }
        return result;
    }

    @Override
    public void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(SoundEvents.DEEPSLATE_TILES_FALL, 0.15f, 1.0f);
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.DEEPSLATE_TILES_BREAK;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.IRON_GOLEM_DEATH;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putDouble("SkillTime", this.skillTime);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("SkillTime")) {
            this.skillTime = compound.getDouble("SkillTime");
        }
    }

    @Override
    public void baseTick() {
        super.baseTick();
        if (!level().isClientSide()) {
            tickSkill();
        }
    }

    @Override
    protected void tickDeath() {
        ++this.deathTime;
        if (this.deathTime == 20) {
            this.remove(RemovalReason.KILLED);
        }
    }

    private void tickSkill() {
        if (skillTimer > 0) {
            skillTimer++;
            if (skillTimer == 2) {
                playRoarSound();
            }
            if (skillTimer == 9) {
                PasterDreamMod.LOGGER.info("ShadowGolem skill phase 2: playing skill animation");
                setAnimation("skill");
            }
            if (skillTimer == 45) {
                PasterDreamMod.LOGGER.info("ShadowGolem skill phase 3: explosion!");
                doSkillExplosion();
            }
            if (skillTimer >= 46) {
                skillTimer = 0;
                setAnimation("empty");
            }
        } else {
            skillTime++;
            if (skillTime >= 200) {
                Player nearest = level().getNearestPlayer(this, 10);
                if (nearest != null) {
                    skillTimer = 1;
                    setAnimation("storage");
                    skillTime = 0;
                    PasterDreamMod.LOGGER.info("ShadowGolem skill triggered! storage animation set, player distance: {}",
                            this.distanceTo(nearest));
                }
            }
        }
    }

    private void playRoarSound() {
        SoundEvent roar = SoundEvent.createVariableRangeEvent(ROAR_SOUND);
        this.playSound(roar, 1.2f, 1.0f);
        if (level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE,
                    this.getX(), this.getY() + 1, this.getZ(),
                    30, 1.5, 0.5, 1.5, 0.05);
        }
    }

    private void doSkillExplosion() {
        if (!(level() instanceof ServerLevel serverLevel)) return;

        serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE,
                this.getX(), this.getY(), this.getZ(),
                200, 3, 0.1, 3, 0.2);
        serverLevel.sendParticles((SimpleParticleType) PDParticles.SHADOW_STONE_PARTICLE.particleType(),
                this.getX(), this.getY(), this.getZ(),
                200, 3, 0.4, 3, 0.1);
        serverLevel.playSound(null, this.blockPosition(),
                SoundEvents.GENERIC_EXPLODE.value(), net.minecraft.sounds.SoundSource.HOSTILE, 1, 1);

        AABB area = new AABB(this.blockPosition()).inflate(10);
        for (Entity target : level().getEntities(this, area)) {
            if (target != this && target instanceof LivingEntity) {
                target.hurt(this.damageSources().mobAttack(this), 15);
                target.setDeltaMovement(new Vec3(0, 1.5, 0));
                target.hurtMarked = true;
            }
        }
    }

    private PlayState movementPredicate(AnimationState<ShadowGolemEntity> state) {
        if (this.getSyncedAnimation().equals("empty")) {
            if ((state.isMoving() || !(state.getLimbSwingAmount() > -0.15F && state.getLimbSwingAmount() < 0.15F))
                    && !this.isAggressive()) {
                return state.setAndContinue(RawAnimation.begin().thenLoop("walk"));
            }
            if (this.isSprinting()) {
                return state.setAndContinue(RawAnimation.begin().thenLoop("walk"));
            }
            if (this.isAggressive() && state.isMoving()) {
                return state.setAndContinue(RawAnimation.begin().thenLoop("attack"));
            }
            return state.setAndContinue(RawAnimation.begin().thenLoop("idle"));
        }
        return PlayState.STOP;
    }

    private PlayState attackingPredicate(AnimationState<ShadowGolemEntity> state) {
        Vec3 delta = this.getDeltaMovement();
        float velocity = (float) Math.sqrt(delta.x * delta.x + delta.z * delta.z);
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