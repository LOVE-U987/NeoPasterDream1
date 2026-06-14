package com.pasterdream.pasterdreammod.entity.mob;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import com.pasterdream.pasterdreammod.api.entity.anim.ProcedureAnimationHandler;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.Comparator;

/**
 * 小石灵 (Small Stone Spirit) — 地面敌对生物
 * <p>
 * 行为：
 * - 主动近战攻击
 * - 免疫箭矢伤害
 * - 播放深板岩风格的音效
 * <p>
 * 动画：
 * - movement: idle / walk / death
 * - attacking: 触发式攻击动画
 * - procedure: 过程动画
 */
public class SmallStoneSpiritEntity extends Monster implements GeoEntity {

    private static final EntityDataAccessor<Boolean> SHOOT =
            SynchedEntityData.defineId(SmallStoneSpiritEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<String> ANIMATION =
            SynchedEntityData.defineId(SmallStoneSpiritEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> TEXTURE =
            SynchedEntityData.defineId(SmallStoneSpiritEntity.class, EntityDataSerializers.STRING);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    /** 攻击挥动标记（供动画系统使用） */
    private boolean swinging;
    /** 上一次挥动的时间 */
    private long lastSwing;
    /** 过程动画名称（"empty" 表示无过程动画） */
    public String animationprocedure = "empty";

    /** 客户端 procedure 动画处理器 */
    private final ProcedureAnimationHandler procAnim = new ProcedureAnimationHandler();

    // ==================== 群体增益技能 ====================
    /** 累积的尺寸数值（受附近小石灵数量影响） */
    private double size = 0;
    /** 群体增益检测间隔 counter（每 20 tick 检测一次） */
    private int groupBuffCooldown = 0;

    /**
     * 构造小石灵实体
     *
     * @param type  实体类型
     * @param level 世界实例
     */
    public SmallStoneSpiritEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.xpReward = 0;
    }

    // ======================== 同步数据 ========================

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SHOOT, false);
        builder.define(ANIMATION, "undefined");
        builder.define(TEXTURE, "small_stone_spirit");
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

    // ======================== 属性 ========================

    /**
     * 创建小石灵实体的属性
     *
     * @return 属性构造器
     */
    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 10)
                .add(Attributes.ARMOR, 4)
                .add(Attributes.ATTACK_DAMAGE, 4)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.FOLLOW_RANGE, 16)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1);
    }

    // ======================== AI 目标 ========================

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.1, false));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this).setAlertOthers());
        this.goalSelector.addGoal(3, new RandomStrollGoal(this, 0.5));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
    }


    // ======================== 音效 ========================

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.DEEPSLATE_STEP;
    }

    @Override
    public void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(SoundEvents.DEEPSLATE_STEP, 0.15f, 1.0f);
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.DEEPSLATE_HIT;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.DEEPSLATE_BREAK;
    }

    // ======================== 受伤/免疫 ========================

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypes.ARROW)) return false;
        if (source.is(DamageTypes.THROWN)) return false;
        return super.hurt(source, amount);
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

    // ======================== 每 tick 更新 ========================

    @Override
    public void baseTick() {
        super.baseTick();
        this.refreshDimensions();
        // 服务端群体增益逻辑（每 20 tick 检测一次）
        if (!this.level().isClientSide() && this.isAlive()) {
            groupBuffCooldown--;
            if (groupBuffCooldown <= 0) {
                groupBuffCooldown = 20; // 每秒检测一次
                executeGroupBuff();
            }
        }
    }

    // ==================== 群体增益技能实现 ====================

    /**
     * 群体增益：附近有小石灵时相互 buff
     * 原 SmallStoneSpiritPr0Procedure 逻辑
     * - 附近每只小石灵使 size +0.1
     * - 自身获得 再生VI、抗性I、生命提升（等级 = size * 10）
     */
    private void executeGroupBuff() {
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();

        // 检测 10 格内的小石灵
        AABB aabb = new AABB(new Vec3(x, y, z), new Vec3(x, y, z)).inflate(10 / 2d);
        List<SmallStoneSpiritEntity> nearbySpirits = this.level().getEntitiesOfClass(
                SmallStoneSpiritEntity.class, aabb, e -> e != this && e.isAlive());

        for (SmallStoneSpiritEntity spirit : nearbySpirits) {
            // 增加对方 size
            spirit.size += 0.1;
        }

        // 如果有附近小石灵，自身获得增益效果
        if (!nearbySpirits.isEmpty()) {
            // 播放石灵增益音效（原模组无 skill 动画，仅保留音效）
            this.level().playSound(null, BlockPos.containing(x, y, z),
                    SoundEvents.STONE_PLACE, SoundSource.HOSTILE, 1.0F, 1.2F);

            this.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 80, 6, false, false));
            this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 400, 0, false, false));
            int healthBoostLevel = (int) (size * 10 - 1);
            if (healthBoostLevel >= 0) {
                this.addEffect(new MobEffectInstance(MobEffects.HEALTH_BOOST, 400,
                        Math.min(healthBoostLevel, 255), false, false));
            }
        }

        // 如果当前位置是空气且下方不是空气（悬浮状态），50% 概率保持不动
        // 注意：SmallStoneSpiritBlock 尚未移植，跳过放置方块逻辑
    }

    @Override
    public void aiStep() {
        super.aiStep();
        this.updateSwingTime();
    }

    // ======================== 死亡处理 ========================

    @Override
    protected void tickDeath() {
        ++this.deathTime;
        if (this.deathTime == 24) {
            this.remove(RemovalReason.KILLED);
        }
    }

    // ======================== GeckoLib 动画 ========================

    /**
     * 移动状态动画控制器
     * 根据移动和死亡状态切换 idle / walk / death 动画
     */
    private PlayState movementPredicate(AnimationState<SmallStoneSpiritEntity> state) {
        if (this.getSyncedAnimation().equals("empty")) {
            if (this.isDeadOrDying()) {
                return state.setAndContinue(RawAnimation.begin().thenPlay("death"));
            }
            if (state.isMoving() || this.isSprinting()) {
                return state.setAndContinue(RawAnimation.begin().thenLoop("walk"));
            }
            return state.setAndContinue(RawAnimation.begin().thenLoop("idle"));
        }
        return PlayState.STOP;
    }

    /**
     * 攻击动画控制器
     * 在实体挥动时触发 attack 动画
     */
    private PlayState attackingPredicate(AnimationState<SmallStoneSpiritEntity> state) {
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
     */
    private PlayState procedurePredicate(AnimationState<SmallStoneSpiritEntity> state) {
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