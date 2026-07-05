package com.pasterdream.pasterdreammod.entity.mob;

import com.pasterdream.pasterdreammod.registry.PDSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundSource;
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
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import com.pasterdream.pasterdreammod.api.entity.anim.ProcedureAnimationHandler;
import software.bernie.geckolib.util.GeckoLibUtil;

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
public class ShadowHandEntity extends Monster implements GeoEntity {

    /** 射击状态同步标记 */
    public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(ShadowHandEntity.class, EntityDataSerializers.BOOLEAN);
    /** 当前播放动画名称同步标记 */
    public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(ShadowHandEntity.class, EntityDataSerializers.STRING);
    /** 纹理名称同步标记（默认 "shadow_hand"） */
    public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(ShadowHandEntity.class, EntityDataSerializers.STRING);

    /** GeckoLib 动画实例缓存 */
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final ProcedureAnimationHandler procAnim = new ProcedureAnimationHandler();

    /** 攻击挥动标记 */
    private boolean swinging;
    /** 上一次挥动的时间 */
    private long lastSwing;
    /** 过程动画名称（"empty" 表示无过程动画） */
    public String animationprocedure = "empty";

    /** 冲锋冷却计时器 */
    private int chargeCooldown = 0;

    /** 是否正在冲锋 */
    private boolean isCharging = false;

    /** 冲锋计时 */
    private int chargeTicks = 0;

    /**
     * 构造暗影之手实体
     *
     * @param type  实体类型
     * @param level 世界实例
     */
    public ShadowHandEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.moveControl = new FlyingMoveControl(this, 10, true);
        this.setNoGravity(true);
        this.xpReward = 1;
    }

    // ======================== 同步数据 ========================

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SHOOT, false);
        builder.define(ANIMATION, "undefined");
        builder.define(TEXTURE, "shadow_hand");
    }

    /**
     * 设置纹理名称
     *
     * @param texture 纹理名称
     */
    public void setTexture(String texture) {
        this.entityData.set(TEXTURE, texture);
    }

    /**
     * 获取当前纹理名称
     *
     * @return 纹理名称
     */
    public String getTexture() {
        return this.entityData.get(TEXTURE);
    }

    // ======================== NBT 持久化 ========================

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putString("Texture", this.getTexture());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Texture")) {
            this.setTexture(compound.getString("Texture"));
        }
    }

    // ======================== 导航 ========================

    @Override
    protected PathNavigation createNavigation(Level level) {
        FlyingPathNavigation navigation = new FlyingPathNavigation(this, level);
        navigation.setCanOpenDoors(false);
        navigation.setCanFloat(true);
        navigation.setCanPassDoors(true);
        return navigation;
    }

    // ======================== 属性 ========================

    /**
     * 创建暗影之手的属性
     *
     * @return 属性构造器
     */
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

    // ======================== AI 目标 ========================

    @Override
    protected void registerGoals() {
        super.registerGoals();

        // 飞行追踪目标 Goal（速度 1.4）
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

        // 三维随机飞行
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

        // 近战攻击
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.5, false));

        // 随机张望
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));

        // 目标选择器：反击（警报其他暗影之手）+ 主动攻击玩家
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Player.class, false, false));
        this.targetSelector.addGoal(6, new HurtByTargetGoal(this).setAlertOthers());
    }

    // ======================== 受伤/免疫 ========================

    /**
     * 暗影之手免疫以下伤害类型：
     * <ul>
     *   <li>火焰</li>
     *   <li>仙人掌</li>
     * </ul>
     * <p>
     * 注意：原模组在 hurt() 中还会执行 ShadowHandPr0Procedure，
     * 该过程在原模组中会在受伤时对攻击者造成反伤等效果，
     * 需在后续功能实现中另行处理。
     *
     * @param source 伤害来源
     * @param amount 伤害值
     * @return 是否受到伤害
     */
    @Override
    public boolean hurt(DamageSource source, float amount) {
        // 注意：原模组此处在免疫检测前执行 ShadowHandPr0Procedure
        // 该 procedure 涉及以下逻辑，需在后续实现：
        //   - 被攻击时有一定概率对攻击者造成额外伤害或效果
        if (source.is(DamageTypes.IN_FIRE)) return false;
        if (source.is(DamageTypes.ON_FIRE)) return false;
        if (source.is(DamageTypes.LAVA)) return false;
        if (source.is(DamageTypes.CACTUS)) return false;
        return super.hurt(source, amount);
    }

    // ======================== 触碰玩家 ========================

    /**
     * 当暗影之手触碰玩家时，原模组会执行 TerrorbeakPr1Procedure
     * <p>
     * 该过程在原模组中会对玩家造成额外伤害或效果，
     * 需在后续功能实现中另行处理。
     *
     * @param sourceentity 触碰的玩家
     */
    @Override
    public void playerTouch(Player sourceentity) {
        super.playerTouch(sourceentity);
        // 注意：原模组此处在 playerTouch 中执行 TerrorbeakPr1Procedure
        // 该 procedure 在原模组中会对玩家造成伤害、减速或击退效果，
        // 需在后续实现。
    }

    // ======================== 飞行行为 ========================

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
        // 飞行生物，不处理摔落检测
    }

    @Override
    public void aiStep() {
        super.aiStep();
        this.updateSwingTime();
        this.setNoGravity(true);
    }

    @Override
    public void playStepSound(BlockPos pos, BlockState blockIn) {
        // 幽灵飞行无脚步声
    }

    // ======================== 尺寸刷新 ========================

    @Override
    public void baseTick() {
        super.baseTick();
        this.refreshDimensions();
        // 服务端冲锋逻辑
        if (!this.level().isClientSide()) {
            serverChargeTick();
        }
    }

    // ==================== 冲锋技能实现 ====================

    /**
     * 服务端每 tick 冲锋检测逻辑
     * 原ShadowHandPr0Procedure：检测目标并冲刺攻击
     */
    private void serverChargeTick() {
        if (chargeCooldown > 0) {
            chargeCooldown--;
        }

        // 正在冲锋，推进冲锋阶段
        if (isCharging) {
            chargeTicks++;
            // 冲锋持续 10 tick 后结束，移除指数加速
            if (chargeTicks >= 10) {
                isCharging = false;
                chargeTicks = 0;
                this.setAnimation("empty");
            }
            return;
        }

        // 冷却未到或无目标，跳过
        if (chargeCooldown > 0 || this.getTarget() == null) return;

        // 没有沉默效果且目标在视线范围内时触发冲锋
        if (!this.hasEffect(MobEffects.CONFUSION) && this.getTarget().isAlive()) {
            double dist = this.distanceToSqr(this.getTarget());
            if (dist < 64.0) { // 8格内触发冲锋
                // 播放暗影冲锋音效
                this.level().playSound(null, this.blockPosition(),
                        PDSounds.SHADOW_0.get(), SoundSource.HOSTILE, 1.0F, 1.0F);
                // 设置冲锋动画
                this.setAnimation("attack");
                isCharging = true;
                chargeTicks = 0;
                // 设置冷却（60 tick = 3秒）
                chargeCooldown = 60;
                // 朝目标方向冲刺，降低冲锋速度
                Vec3 dir = this.getTarget().position().subtract(this.position()).normalize();
                this.setDeltaMovement(dir.x * 0.6, dir.y * 0.6, dir.z * 0.6);
                this.hasImpulse = true;
            }
        }
    }

    // ======================== 死亡处理 ========================

    @Override
    protected void tickDeath() {
        ++this.deathTime;
        if (this.deathTime == 10) {
            this.remove(RemovalReason.KILLED);
            this.dropExperience(this.getLastHurtByMob());
        }
    }

    // ======================== 动画 getter/setter ========================

    /**
     * 获取同步的动画名称
     *
     * @return 动画名称
     */
    public String getSyncedAnimation() {
        return this.entityData.get(ANIMATION);
    }

    /**
     * 设置同步的动画名称
     *
     * @param animation 动画名称
     */
    public void setAnimation(String animation) {
        this.entityData.set(ANIMATION, animation);
    }

    // ======================== GeckoLib 动画 ========================

    /**
     * 移动状态动画控制器
     *
     * @param state 动画状态
     * @return 播放状态
     */
    private PlayState movementPredicate(software.bernie.geckolib.animation.AnimationState<ShadowHandEntity> state) {
        if (this.getSyncedAnimation().equals("empty")) {
            if (state.isMoving() || !(state.getLimbSwingAmount() > -0.15F && state.getLimbSwingAmount() < 0.15F)) {
                return state.setAndContinue(RawAnimation.begin().thenLoop("walk"));
            }
            return state.setAndContinue(RawAnimation.begin().thenLoop("idle"));
        }
        return PlayState.STOP;
    }

    /**
     * 攻击动画控制器
     *
     * @param state 动画状态
     * @return 播放状态
     */
    private PlayState attackingPredicate(software.bernie.geckolib.animation.AnimationState<ShadowHandEntity> state) {
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

    /**
     * 过程动画控制器（用于触发一次性动画）
     *
     * @param state 动画状态
     * @return 播放状态
     */
    private PlayState procedurePredicate(software.bernie.geckolib.animation.AnimationState<ShadowHandEntity> state) {
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