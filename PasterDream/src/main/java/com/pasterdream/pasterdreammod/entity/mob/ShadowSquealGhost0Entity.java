package com.pasterdream.pasterdreammod.entity.mob;

import com.pasterdream.pasterdreammod.api.entity.base.GeckoLibMonsterEntity;
import com.pasterdream.pasterdreammod.entity.mob.ShadowGhostEntity;
import com.pasterdream.pasterdreammod.entity.mob.ShadowSquealGhostEntity;
import com.pasterdream.pasterdreammod.registry.PDEntities;
import com.pasterdream.pasterdreammod.registry.PDSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
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
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;

import java.util.EnumSet;

/**
 * 暗影尖啸幽灵0 (Shadow Squeal Ghost 0) — 强化版飞行远程攻击幽灵生物
 * <p>
 * AI 行为：
 * <ul>
 *   <li>继承 {@link RangedAttackMob}，发射音波弹远程攻击（冷却更短）</li>
 *   <li>三维飞行移动（FlyingMoveControl + FlyingPathNavigation + 无重力）</li>
 *   <li>更高的血量（20）和攻击力（6）</li>
 *   <li>默认纹理 "shadow_squeal_wave_0"</li>
 *   <li>免疫火焰、摔落、仙人掌、溺水、药水伤害</li>
 * </ul>
 * <p>
 * 动画：movement(idle/walk) | attacking(attack) | procedure(触发式，由基类统一处理)
 */
public class ShadowSquealGhost0Entity extends GeckoLibMonsterEntity implements RangedAttackMob {

    /** 攻击挥动标记 */
    private boolean swinging;
    /** 上一次挥动的时间 */
    private long lastSwing;

    /** 召唤技能冷却计时器（0表示可释放技能） */
    private int summonCooldown = 0;
    /** 召唤阶段计时器（用于多阶段召唤） */
    private int summonPhase = 0;
    /** 是否正在施放技能 */
    private boolean isCastingSkill = false;

    /**
     * 构造暗影尖啸幽灵0实体
     *
     * @param type  实体类型
     * @param level 世界实例
     */
    public ShadowSquealGhost0Entity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.moveControl = new FlyingMoveControl(this, 10, true);
        this.setNoGravity(true);
        this.xpReward = 2;
    }

    /**
     * 返回默认纹理名称
     *
     * @return 默认纹理名
     */
    @Override
    protected String getDefaultTexture() {
        return "shadow_squeal_wave_0";
    }

    // ======================== 同步数据 ========================

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
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
     * 创建暗影尖啸幽灵0的属性
     *
     * @return 属性构造器
     */
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.8)
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.ARMOR, 0.0)
                .add(Attributes.ATTACK_DAMAGE, 6.0)
                .add(Attributes.FOLLOW_RANGE, 20.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.4)
                .add(Attributes.FLYING_SPEED, 0.8);
    }

    // ======================== AI 目标 ========================

    @Override
    protected void registerGoals() {
        super.registerGoals();

        // 近战攻击
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2, false));

        // 远程攻击 Goal（冷却更短，5 tick 间隔）
        this.goalSelector.addGoal(1, new ShadowSquealGhost0Entity.RangedAttackGoal(this, 1.25, 5, 16f) {
            @Override
            public boolean canContinueToUse() {
                return this.canUse();
            }
        });

        // 飞行追踪目标 Goal
        this.goalSelector.addGoal(2, new Goal() {
            {
                this.setFlags(EnumSet.of(Goal.Flag.MOVE));
            }

            @Override
            public boolean canUse() {
                if (ShadowSquealGhost0Entity.this.getTarget() != null && !ShadowSquealGhost0Entity.this.getMoveControl().hasWanted()) {
                    return true;
                }
                return false;
            }

            @Override
            public boolean canContinueToUse() {
                return ShadowSquealGhost0Entity.this.getMoveControl().hasWanted()
                        && ShadowSquealGhost0Entity.this.getTarget() != null
                        && ShadowSquealGhost0Entity.this.getTarget().isAlive();
            }

            @Override
            public void start() {
                LivingEntity target = ShadowSquealGhost0Entity.this.getTarget();
                if (target != null) {
                    Vec3 vec3d = target.getEyePosition(1);
                    ShadowSquealGhost0Entity.this.moveControl.setWantedPosition(vec3d.x, vec3d.y, vec3d.z, 0.6);
                }
            }

            @Override
            public void tick() {
                LivingEntity target = ShadowSquealGhost0Entity.this.getTarget();
                if (target == null) return;
                if (ShadowSquealGhost0Entity.this.getBoundingBox().intersects(target.getBoundingBox())) {
                    ShadowSquealGhost0Entity.this.doHurtTarget(target);
                } else {
                    double distanceSqr = ShadowSquealGhost0Entity.this.distanceToSqr(target);
                    if (distanceSqr < 5) {
                        Vec3 vec3d = target.getEyePosition(1);
                        ShadowSquealGhost0Entity.this.moveControl.setWantedPosition(vec3d.x, vec3d.y, vec3d.z, 0.6);
                    }
                }
            }
        });

        // 三维随机飞行
        this.goalSelector.addGoal(3, new RandomStrollGoal(this, 0.8, 20) {
            @Override
            protected Vec3 getPosition() {
                RandomSource random = ShadowSquealGhost0Entity.this.getRandom();
                double dirX = ShadowSquealGhost0Entity.this.getX() + ((random.nextFloat() * 2 - 1) * 16);
                double dirY = ShadowSquealGhost0Entity.this.getY() + ((random.nextFloat() * 2 - 1) * 16);
                double dirZ = ShadowSquealGhost0Entity.this.getZ() + ((random.nextFloat() * 2 - 1) * 16);
                return new Vec3(dirX, dirY, dirZ);
            }
        });

        // 随机张望
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));

        // 目标选择器：反击 + 主动攻击玩家
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Player.class, false, false));
        this.targetSelector.addGoal(6, new HurtByTargetGoal(this));
    }

    /**
     * 自定义远程攻击 Goal（与 RangedAttackGoal 标准实现兼容）
     */
    public class RangedAttackGoal extends Goal {
        private final Mob mob;
        private final RangedAttackMob rangedAttackMob;
        @Nullable
        private LivingEntity target;
        private int attackTime = -1;
        private final double speedModifier;
        private int seeTime;
        private final int attackIntervalMin;
        private final int attackIntervalMax;
        private final float attackRadius;
        private final float attackRadiusSqr;

        public RangedAttackGoal(RangedAttackMob mob, double speedModifier, int attackInterval, float attackRadius) {
            this(mob, speedModifier, attackInterval, attackInterval, attackRadius);
        }

        public RangedAttackGoal(RangedAttackMob mob, double speedModifier, int attackIntervalMin, int attackIntervalMax, float attackRadius) {
            if (!(mob instanceof LivingEntity)) {
                throw new IllegalArgumentException("RangedAttackGoal requires Mob implements RangedAttackMob");
            }
            this.rangedAttackMob = mob;
            this.mob = (Mob) mob;
            this.speedModifier = speedModifier;
            this.attackIntervalMin = attackIntervalMin;
            this.attackIntervalMax = attackIntervalMax;
            this.attackRadius = attackRadius;
            this.attackRadiusSqr = attackRadius * attackRadius;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity livingentity = this.mob.getTarget();
            if (livingentity != null && livingentity.isAlive()) {
                this.target = livingentity;
                return true;
            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            return this.canUse() || (this.target != null && this.target.isAlive() && !this.mob.getNavigation().isDone());
        }

        @Override
        public void stop() {
            this.target = null;
            this.seeTime = 0;
            this.attackTime = -1;
            ((ShadowSquealGhost0Entity) rangedAttackMob).setShooting(false);
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            if (target == null) return;
            double distanceSq = this.mob.distanceToSqr(this.target.getX(), this.target.getY(), this.target.getZ());
            boolean hasLineOfSight = this.mob.getSensing().hasLineOfSight(this.target);
            if (hasLineOfSight) {
                ++this.seeTime;
            } else {
                this.seeTime = 0;
            }
            ShadowSquealGhost0Entity ghost = (ShadowSquealGhost0Entity) this.rangedAttackMob;
            if (!(distanceSq > (double) this.attackRadiusSqr) && this.seeTime >= 5) {
                this.mob.getNavigation().stop();
            } else {
                this.mob.getNavigation().moveTo(this.target, this.speedModifier);
            }
            this.mob.getLookControl().setLookAt(this.target, 30.0F, 30.0F);
            if (--this.attackTime == 0) {
                if (!hasLineOfSight) {
                    ghost.setShooting(false);
                    return;
                }
                ghost.setShooting(true);
                float f = (float) Math.sqrt(distanceSq) / this.attackRadius;
                float f1 = Mth.clamp(f, 0.1F, 1.0F);
                this.rangedAttackMob.performRangedAttack(this.target, f1);
                this.attackTime = Mth.floor(f * (float) (this.attackIntervalMax - this.attackIntervalMin) + (float) this.attackIntervalMin);
            } else if (this.attackTime < 0) {
                this.attackTime = Mth.floor(Mth.lerp(Math.sqrt(distanceSq) / (double) this.attackRadius, (double) this.attackIntervalMin, (double) this.attackIntervalMax));
            } else {
                ghost.setShooting(false);
            }
        }
    }

    // ======================== 远程攻击实现 ========================

    /**
     * 执行远程攻击（发射音波弹）
     * <p>
     * 注意：此方法需要 {@code SquealWaveProjectileEntity} 配合使用，
     * 当前使用占位实现。实际 projectile 注册完成后请替换为：
     * {@code SquealWaveProjectileEntity.shoot(this, target);}
     *
     * @param target 攻击目标
     * @param flval  速度修正系数
     */
    @Override
    public void performRangedAttack(LivingEntity target, float flval) {
        // TODO: 替换为 SquealWaveProjectileEntity.shoot(this, target)
        // 原模组引用 net.pasterdream.entity.SquealWaveProjectileEntity
    }

    // ======================== 受伤/免疫 ========================

    /**
     * 暗影尖啸幽灵0免疫以下伤害类型：
     * <ul>
     *   <li>火焰</li>
     *   <li>药水云</li>
     *   <li>摔落</li>
     *   <li>仙人掌</li>
     *   <li>溺水</li>
     * </ul>
     * <p>
     * 注意：原模组在 hurt() 中还会执行 ShadowSquealGhost0Pr0Procedure，
     * 该过程效果需在未来的 projectile 实现中另行处理。
     *
     * @param source 伤害来源
     * @param amount 伤害值
     * @return 是否受到伤害
     */
    @Override
    public boolean hurt(DamageSource source, float amount) {
        // 注意：原模组此处在免疫检测前执行 ShadowSquealGhost0Pr0Procedure
        // 该 procedure 涉及以下逻辑，需在 projectile 系统中实现：
        //   - 被攻击时有一定概率召唤 SquealWaveProjectileEntity
        if (source.is(DamageTypes.IN_FIRE)) return false;
        if (source.is(DamageTypes.ON_FIRE)) return false;
        if (source.is(DamageTypes.LAVA)) return false;
        if (source.getDirectEntity() instanceof net.minecraft.world.entity.projectile.ThrownPotion
                || source.getDirectEntity() instanceof net.minecraft.world.entity.AreaEffectCloud) {
            return false;
        }
        if (source.is(DamageTypes.FALL)) return false;
        if (source.is(DamageTypes.CACTUS)) return false;
        if (source.is(DamageTypes.DROWN)) return false;
        return super.hurt(source, amount);
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
        this.serverTick();
    }

    // ==================== 技能系统实现 ====================

    /**
     * 服务端 tick 逻辑：处理召唤技能冷却和阶段
     */
    private void serverTick() {
        if (this.level().isClientSide()) return;

        // 正在施放技能，推进阶段
        if (isCastingSkill) {
            summonPhase++;

            // 8 tick: 召唤第1只暗影幽灵
            if (summonPhase == 8) {
                ShadowGhostEntity ghost1 = new ShadowGhostEntity(PDEntities.SHADOW_GHOST.get(), this.level());
                ghost1.setPos(this.getX() + 2, this.getY(), this.getZ());
                ghost1.setTarget(this.getTarget());
                this.level().addFreshEntity(ghost1);
            }
            // 16 tick: 召唤第2只暗影幽灵
            if (summonPhase == 16) {
                ShadowGhostEntity ghost2 = new ShadowGhostEntity(PDEntities.SHADOW_GHOST.get(), this.level());
                ghost2.setPos(this.getX() - 2, this.getY() + 1, this.getZ());
                ghost2.setTarget(this.getTarget());
                this.level().addFreshEntity(ghost2);
            }
            // 24 tick: 召唤暗影尖啸幽灵
            if (summonPhase == 24) {
                ShadowSquealGhostEntity squeal = new ShadowSquealGhostEntity(PDEntities.SHADOW_SQUEAL_GHOST.get(), this.level());
                squeal.setPos(this.getX(), this.getY() + 1, this.getZ() + 2);
                squeal.setTarget(this.getTarget());
                this.level().addFreshEntity(squeal);
            }
            // 30 tick: 结束技能
            if (summonPhase >= 30) {
                isCastingSkill = false;
                summonPhase = 0;
            }
            return;
        }

        // 递减冷却
        if (summonCooldown > 0) {
            summonCooldown--;
            return;
        }

        // 检测目标并释放技能
        if (this.getTarget() != null && this.getTarget().isAlive()) {
            // 播放音效
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    PDSounds.GHOST_0.get(), this.getSoundSource(), 1.0F, 1.0F);
            // 设置技能动画
            this.setAnimation("skill");
            // 开始施法
            isCastingSkill = true;
            summonPhase = 0;
            // 设置冷却（200 tick = 10秒）
            summonCooldown = 200;
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

    // ======================== GeckoLib 动画 ========================

    /**
     * 移动状态动画控制器
     *
     * @param state 动画状态
     * @return 播放状态
     */
    private PlayState movementPredicate(AnimationState<ShadowSquealGhost0Entity> state) {
        if (this.getSyncedAnimation().equals("empty")) {
            if (state.isMoving() || !(state.getLimbSwingAmount() > -0.15F && state.getLimbSwingAmount() < 0.15F)) {
                return state.setAndContinue(RawAnimation.begin().thenLoop("walk"));
            }
            return state.setAndContinue(RawAnimation.begin().thenLoop("idle"));
        }
        return PlayState.STOP;
    }

    /**
     * 攻击动画控制器（远程射击 + 近战挥动）
     *
     * @param state 动画状态
     * @return 播放状态
     */
    private PlayState attackingPredicate(AnimationState<ShadowSquealGhost0Entity> state) {
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
        if ((this.swinging || this.isShooting()) && state.getController().getAnimationState() == AnimationController.State.STOPPED) {
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
