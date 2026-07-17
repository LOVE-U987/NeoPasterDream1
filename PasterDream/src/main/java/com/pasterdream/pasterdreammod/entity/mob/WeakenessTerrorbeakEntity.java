package com.pasterdream.pasterdreammod.entity.mob;

import com.pasterdream.pasterdreammod.api.entity.base.GeckoLibMonsterEntity;
import com.pasterdream.pasterdreammod.registry.PDSounds;
import net.minecraft.core.registries.Registries;
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
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;

/**
 * 虚弱恐怖尖喙 (Weakeness Terrorbeak) — 较弱的恐怖尖喙变种
 * <p>
 * 30 血、12 攻击力、体型较小（1.5f x 3.0f）、免疫火焰/仙人掌/凋零伤害
 */
public class WeakenessTerrorbeakEntity extends GeckoLibMonsterEntity {

    /** 咆哮技能冷却计时器 */
    private int roarCooldown = 0;
    private boolean swinging;
    private long lastSwing;

    /**
     * 构造虚弱恐怖尖喙实体
     *
     * @param type  实体类型
     * @param level 世界实例
     */
    public WeakenessTerrorbeakEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.xpReward = 4;
    }

    /**
     * 获取默认纹理名称
     *
     * @return 纹理名称
     */
    @Override
    protected String getDefaultTexture() {
        return "weakness_terrorbeak";
    }

    // ==================== 属性 ====================

    /**
     * 创建虚弱恐怖尖喙的属性
     * 30 血、12 攻击力、速度与普通版相同
     *
     * @return 属性构造器
     */
    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 30)
                .add(Attributes.ARMOR, 0)
                .add(Attributes.ATTACK_DAMAGE, 12)
                .add(Attributes.MOVEMENT_SPEED, 0.3)
                .add(Attributes.FOLLOW_RANGE, 24)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1);
    }

    // ==================== AI 目标 ====================

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.6, false));
        this.goalSelector.addGoal(2, new RandomStrollGoal(this, 1));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this).setAlertOthers());
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(5, new FloatGoal(this));
        this.targetSelector.addGoal(6, new NearestAttackableTargetGoal<>(this, Player.class, false, false));
    }

    // ==================== 受伤/免疫 ====================

    @Override
    public boolean hurt(DamageSource source, float amount) {
        // 咆哮技能：受伤时触发
        if (!this.level().isClientSide() && this.isAlive()) {
            // 不处于沉默状态且冷却已过
            if (!this.hasEffect(MobEffects.CONFUSION) && roarCooldown <= 0) {
                // 播放咆哮音效
                this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                        PDSounds.TERRORBEAK_ROAR.get(), this.getSoundSource(), 1.0F, 1.0F);
                // 触发"roar"咆哮动画
                this.setAnimation("roar");
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
                // 设置冷却（游戏内约10秒）
                roarCooldown = 200;
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

    // ==================== 每 tick ====================

    @Override
    public void baseTick() {
        super.baseTick();
        this.refreshDimensions();
    }

    @Override
    protected void tickDeath() {
        ++this.deathTime;
        if (this.deathTime == 10) {
            this.remove(RemovalReason.KILLED);
            this.dropExperience(this.getLastHurtByMob());
        }
    }

    // ==================== GeckoLib 动画 ====================

    /**
     * 移动状态动画控制器
     *
     * @param state 动画状态
     * @return 播放状态
     */
    private PlayState movementPredicate(AnimationState<WeakenessTerrorbeakEntity> state) {
        if (this.getSyncedAnimation().equals("empty")) {
            if ((state.isMoving() || !(state.getLimbSwingAmount() > -0.15F && state.getLimbSwingAmount() < 0.15F))) {
                return state.setAndContinue(RawAnimation.begin().thenLoop("walk"));
            }
            if (this.isDeadOrDying()) {
                return state.setAndContinue(RawAnimation.begin().thenPlay("death"));
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
    private PlayState attackingPredicate(AnimationState<WeakenessTerrorbeakEntity> state) {
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
