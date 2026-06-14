package com.pasterdream.pasterdreammod.entity.mob;

import com.pasterdream.pasterdreammod.registry.PDSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import com.pasterdream.pasterdreammod.api.entity.anim.ProcedureAnimationHandler;

/**
 * 风之骑士 (Wind Knight) — 地面敌对生物
 * <p>
 * 行为：
 * - 主动攻击玩家，近战攻击
 * - 免疫火焰和摔落伤害
 * - 播放 iron_golem 风格的厚重步声和受伤音效
 * <p>
 * 动画：
 * - movement: idle / walk
 * - attacking: 触发式攻击动画
 * - procedure: 过程动画
 */
public class WindKnightEntity extends Monster implements GeoEntity {

    private static final EntityDataAccessor<Boolean> SHOOT =
            SynchedEntityData.defineId(WindKnightEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<String> ANIMATION =
            SynchedEntityData.defineId(WindKnightEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> TEXTURE =
            SynchedEntityData.defineId(WindKnightEntity.class, EntityDataSerializers.STRING);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    /** 攻击挥动标记（供动画系统使用） */
    private boolean swinging;
    /** 上一次挥动的时间 */
    private long lastSwing;
    /** 过程动画名称（"empty" 表示无过程动画） */
    public String animationprocedure = "empty";

    /** 客户端 procedure 动画处理器 */
    private final ProcedureAnimationHandler procAnim = new ProcedureAnimationHandler();

    /** AOE爆炸技能冷却计时器 */
    private int aoeCooldown = 0;

    /**
     * 构造风之骑士实体
     *
     * @param type  实体类型
     * @param level 世界实例
     */
    public WindKnightEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.xpReward = 32;
    }

    // ======================== 同步数据 ========================

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SHOOT, false);
        builder.define(ANIMATION, "undefined");
        builder.define(TEXTURE, "wind_knight");
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
     * 创建风之骑士实体的属性
     *
     * @return 属性构造器
     */
    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 150)
                .add(Attributes.ARMOR, 10)
                .add(Attributes.ATTACK_DAMAGE, 20)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.FOLLOW_RANGE, 16)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.4);
    }

    // ======================== AI 目标 ========================

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.25, false));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this).setAlertOthers());
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, false, false));
        this.goalSelector.addGoal(4, new RandomStrollGoal(this, 0.8));
    }

    // ======================== 音效 ========================

    @Override
    public void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(SoundEvents.IRON_GOLEM_STEP, 0.15f, 1.0f);
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.IRON_GOLEM_HURT;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.IRON_GOLEM_DEATH;
    }

    // ======================== 受伤/免疫 ========================

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypes.IN_FIRE)) return false;
        if (source.is(DamageTypes.ON_FIRE)) return false;
        if (source.is(DamageTypes.LAVA)) return false;
        if (source.is(DamageTypes.FALL)) return false;
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
        // 服务端AOE检测逻辑
        if (!this.level().isClientSide()) {
            serverAoeTick();
        }
    }

    // ==================== AOE爆炸技能实现 ====================

    /**
     * 服务端每 tick AOE检测逻辑
     * 原WindKnightPr0Procedure：检测玩家并对附近非特殊实体造成AOE伤害
     */
    private void serverAoeTick() {
        if (aoeCooldown > 0) {
            aoeCooldown--;
            return;
        }
        // 检测附近玩家
        Player target = this.level().getNearestPlayer(this, 10.0);
        if (target != null && target.isAlive() && this.hasLineOfSight(target)) {
            // 播放技能音效
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    PDSounds.WIND_KNIGHT_SKILL_0.get(), this.getSoundSource(), 1.0F, 1.0F);
            // 对半径4格内非特殊实体造成AOE伤害
            this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(4.0),
                            e -> e != this && !e.getType().is(TagKey.create(Registries.ENTITY_TYPE,
                                    ResourceLocation.fromNamespaceAndPath("pasterdream", "special_entity_tag"))))
                    .forEach(e -> e.hurt(this.damageSources().mobAttack(this), 12.0F));
            // 自身获得移动速度提升与缓慢减益（平衡性设计）
            this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 200, 0));
            this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 1));
            // 设置冷却（300 tick = 15秒）
            aoeCooldown = 300;
        }
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
        if (this.deathTime == 20) {
            this.remove(RemovalReason.KILLED);
            // xpReward 已在构造函数中设置
        }
    }

    // ======================== GeckoLib 动画 ========================

    /**
     * 移动状态动画控制器
     * 根据移动状态切换 idle / walk 动画
     */
    private PlayState movementPredicate(AnimationState<WindKnightEntity> state) {
        if (this.getSyncedAnimation().equals("empty")) {
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
    private PlayState attackingPredicate(AnimationState<WindKnightEntity> state) {
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
    private PlayState procedurePredicate(AnimationState<WindKnightEntity> state) {
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