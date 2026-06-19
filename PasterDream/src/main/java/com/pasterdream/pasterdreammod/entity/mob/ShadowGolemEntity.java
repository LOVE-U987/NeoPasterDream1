package com.pasterdream.pasterdreammod.entity.mob;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.registry.PDParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
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
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import com.pasterdream.pasterdreammod.api.entity.anim.ProcedureAnimationHandler;

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
public class ShadowGolemEntity extends Monster implements GeoEntity {

    private static final EntityDataAccessor<Boolean> SHOOT =
            SynchedEntityData.defineId(ShadowGolemEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<String> ANIMATION =
            SynchedEntityData.defineId(ShadowGolemEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> TEXTURE =
            SynchedEntityData.defineId(ShadowGolemEntity.class, EntityDataSerializers.STRING);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    /** Procedure 动画处理器 */
    private final ProcedureAnimationHandler procAnim = new ProcedureAnimationHandler();
    private boolean swinging;
    private boolean lastloop;
    private long lastSwing;

    /** 技能充能计数器（达到 200 触发技能） */
    private double skillTime = 0;
    /** 技能阶段计时器（>0 表示技能正在执行） */
    private int skillTimer = 0;

    private static final ResourceLocation ROAR_SOUND = ResourceLocation.fromNamespaceAndPath("pasterdream", "roar0");

    /**
     * 构造暗影魔像实体
     *
     * @param type  实体类型
     * @param level 世界实例
     */
    public ShadowGolemEntity(EntityType<ShadowGolemEntity> type, Level level) {
        super(type, level);
        this.xpReward = 7;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SHOOT, false);
        builder.define(ANIMATION, "undefined");
        builder.define(TEXTURE, "shadow_golem");
    }

    /**
     * 设置纹理
     *
     * @param texture 纹理名称
     */
    public void setTexture(String texture) {
        this.entityData.set(TEXTURE, texture);
    }

    /**
     * 获取纹理名称
     *
     * @return 纹理名称
     */
    public String getTexture() {
        return this.entityData.get(TEXTURE);
    }

    /**
     * 获取同步的动画名称
     *
     * @return 动画名称
     */
    public String getSyncedAnimation() {
        return this.entityData.get(ANIMATION);
    }

    /**
     * 设置同步动画，更新 ANIMATION 同步数据以触发 procedure 控制器
     *
     * @param animation 动画名称
     */
    public void setAnimation(String animation) {
        this.entityData.set(ANIMATION, animation);
        PasterDreamMod.LOGGER.info("setAnimation('{}') called on {} side", 
                animation, level().isClientSide() ? "CLIENT" : "SERVER");
    }

    // ==================== 属性 ====================

    /**
     * 创建暗影魔像的属性
     * 150 血量、20 攻击力、8 护甲、高击退抗性
     *
     * @return 属性构造器
     */
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

    // ==================== AI 目标 ====================

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2, false));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
        this.goalSelector.addGoal(3, new RandomStrollGoal(this, 0.9));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Player.class, false, false));
    }

    // ==================== 受伤/免疫 ====================

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypes.ARROW)) return false;
        if (source.is(DamageTypes.THROWN)) return false;
        if (source.is(DamageTypes.INDIRECT_MAGIC)) return false;
        if (source.is(DamageTypes.FALL)) return false;
        if (source.is(DamageTypes.CACTUS)) return false;

        boolean result = super.hurt(source, amount);
        if (result && !level().isClientSide()) {
            // 被攻击时加速技能充能（不超过 189，保证最低 11 tick 触发）
            if (skillTimer == 0 && skillTime < 189) {
                skillTime += 10;
            }
        }
        return result;
    }

    // ==================== 音效 ====================

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

    // ==================== NBT 持久化 ====================

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putString("Texture", this.getTexture());
        compound.putDouble("SkillTime", this.skillTime);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Texture")) {
            this.setTexture(compound.getString("Texture"));
        }
        if (compound.contains("SkillTime")) {
            this.skillTime = compound.getDouble("SkillTime");
        }
    }

    // ==================== 每 tick 更新 ====================

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

    // ==================== 技能系统 ====================

    /**
     * 每 tick 处理技能充能及阶段执行
     * <p>
     * 技能触发条件：
     * - skillTimer == 0（未在技能执行中）
     * - skillTime >= 200（充能完成）
     * - 10 格内有玩家
     * <p>
     * 技能执行序列（skillTimer 计数）：
     * - T=0: 播放 storage 动画
     * - T=1: 播放 roar0 咆哮音效
     * - T=8: 切换到 skill 动画
     * - T=44: 爆炸粒子 + AOE 伤害 + 击飞
     * - T=45: 技能结束，重置所有计时器
     */
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

    /**
     * 播放咆哮音效（roar0）
     */
    private void playRoarSound() {
        SoundEvent roar = SoundEvent.createVariableRangeEvent(ROAR_SOUND);
        this.playSound(roar, 1.2f, 1.0f);
        if (level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE,
                    this.getX(), this.getY() + 1, this.getZ(),
                    30, 1.5, 0.5, 1.5, 0.05);
        }
    }

    /**
     * 执行技能爆炸效果
     * - 生成大量烟雾 + 暗影石粒子
     * - 播放爆炸音效
     * - 对 10 格内非自身的实体造成 15 点伤害并击飞
     */
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

    // ==================== GeckoLib 动画 ====================

    private PlayState movementPredicate(software.bernie.geckolib.animation.AnimationState<ShadowGolemEntity> state) {
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

    private PlayState attackingPredicate(software.bernie.geckolib.animation.AnimationState<ShadowGolemEntity> state) {
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

    private PlayState procedurePredicate(software.bernie.geckolib.animation.AnimationState<ShadowGolemEntity> state) {
        return procAnim.predicate(state,
                level().isClientSide(),
                this::getSyncedAnimation,
                () -> setAnimation("empty"));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "movement", 4, this::movementPredicate));
        controllers.add(new AnimationController<>(this, "attacking", 4, this::attackingPredicate));
        controllers.add(new AnimationController<>(this, "procedure", 4, this::procedurePredicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}