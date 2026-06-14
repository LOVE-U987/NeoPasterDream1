package com.pasterdream.pasterdreammod.entity.mob;

import com.pasterdream.pasterdreammod.api.entity.anim.ProcedureAnimationHandler;
import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.registry.PDSounds;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
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
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

/**
 * 疯狂恐怖尖喙 (Crazy Terrorbeak) — 更强的大型地面敌对生物
 * <p>
 * 60 血、20 攻击力、免疫火焰/仙人掌/凋零伤害
 * 比普通恐怖尖喙更耐打、伤害更高
 */
public class CrazyTerrorbeakEntity extends Monster implements GeoEntity {

    private static final EntityDataAccessor<Boolean> SHOOT =
            SynchedEntityData.defineId(CrazyTerrorbeakEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<String> ANIMATION =
            SynchedEntityData.defineId(CrazyTerrorbeakEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> TEXTURE =
            SynchedEntityData.defineId(CrazyTerrorbeakEntity.class, EntityDataSerializers.STRING);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    /** 当前动画标识（用于 procedure 控制器） */
    public String animationprocedure = "empty";

    /** 咆哮技能冷却计时器（疯狂版冷却更短） */
    private int roarCooldown = 0;

    private boolean swinging;
    private long lastSwing;

    /** 客户端 procedure 动画处理器 */
    private final ProcedureAnimationHandler procAnim = new ProcedureAnimationHandler();

    /**
     * 构造疯狂恐怖尖喙实体
     *
     * @param type  实体类型
     * @param level 世界实例
     */
    public CrazyTerrorbeakEntity(EntityType<CrazyTerrorbeakEntity> type, Level level) {
        super(type, level);
        this.xpReward = 10;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SHOOT, false);
        builder.define(ANIMATION, "undefined");
        builder.define(TEXTURE, "crazy_terrorbeak");
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
     * 设置同步动画
     *
     * @param animation 动画名称
     */
    public void setAnimation(String animation) {
        this.entityData.set(ANIMATION, animation);
        this.animationprocedure = animation;
    }

    // ==================== 属性 ====================

    /**
     * 创建疯狂恐怖尖喙的属性
     * 60 血、20 攻击力、略高的移动速度
     *
     * @return 属性构造器
     */
    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 60)
                .add(Attributes.ARMOR, 0)
                .add(Attributes.ATTACK_DAMAGE, 20)
                .add(Attributes.MOVEMENT_SPEED, 0.31)
                .add(Attributes.FOLLOW_RANGE, 24)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1);
    }

    // ==================== AI 目标 ====================

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.8, false));
        this.goalSelector.addGoal(2, new RandomStrollGoal(this, 1));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this).setAlertOthers());
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(5, new FloatGoal(this));
        this.targetSelector.addGoal(6, new NearestAttackableTargetGoal<>(this, Player.class, false, false));
    }

    // ==================== 受伤/免疫 ====================

    @Override
    public boolean hurt(DamageSource source, float amount) {
        // 咆哮技能：受伤时触发（疯狂版冷却更短）
        if (!this.level().isClientSide() && this.isAlive()) {
            // 不处于沉默状态且冷却已过
            if (!this.hasEffect(MobEffects.CONFUSION) && roarCooldown <= 0) {
                // 触发怒吼动画
                this.setAnimation("roar");
                // 播放咆哮音效
                this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                        PDSounds.TERRORBEAK_ROAR.get(), this.getSoundSource(), 1.0F, 1.0F);
                // 对附近非特殊实体施加效果（半径5格）
                this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(5.0),
                                e -> e != this && !e.getType().is(TagKey.create(Registries.ENTITY_TYPE,
                                        ResourceLocation.fromNamespaceAndPath("pasterdream", "special_entity_tag"))))
                        .forEach(e -> {
                            if (e instanceof Player) {
                                e.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0));
                                e.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 0));
                                e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 0));
                            }
                        });
                // 设置冷却（疯狂版约5秒，更频繁触发）
                roarCooldown = 100;
            }

            // 递减冷却
            if (roarCooldown > 0) roarCooldown--;
        }
        if (source.is(DamageTypes.IN_FIRE)) return false;
        if (source.is(DamageTypes.CACTUS)) return false;
        if (source.is(DamageTypes.WITHER)) return false;
        if (source.is(DamageTypes.WITHER_SKULL)) return false;
        return super.hurt(source, amount);
    }

    // ==================== 音效 ====================

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.GENERIC_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.GENERIC_DEATH;
    }

    // ==================== NBT 持久化 ====================

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

    // ==================== 每 tick ====================

    @Override
    public void baseTick() {
        super.baseTick();
        this.refreshDimensions();
    }

    @Override
    protected void tickDeath() {
        ++this.deathTime;
        if (this.deathTime == 20) {
            this.remove(RemovalReason.KILLED);
            this.dropExperience(this.getLastHurtByMob());
        }
    }

    // ==================== GeckoLib 动画 ====================

    private PlayState movementPredicate(AnimationState<CrazyTerrorbeakEntity> state) {
        if (this.getSyncedAnimation().equals("empty")) {
            if ((state.isMoving() || !(state.getLimbSwingAmount() > -0.15F && state.getLimbSwingAmount() < 0.15F))) {
                return state.setAndContinue(RawAnimation.begin().thenLoop("walk"));
            }
            return state.setAndContinue(RawAnimation.begin().thenLoop("idle"));
        }
        return PlayState.STOP;
    }

    private PlayState attackingPredicate(AnimationState<CrazyTerrorbeakEntity> state) {
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

    private PlayState procedurePredicate(AnimationState<CrazyTerrorbeakEntity> state) {
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