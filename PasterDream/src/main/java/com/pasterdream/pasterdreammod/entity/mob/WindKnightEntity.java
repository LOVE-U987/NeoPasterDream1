package com.pasterdream.pasterdreammod.entity.mob;

import com.pasterdream.pasterdreammod.api.entity.base.GeckoLibMobEntity;
import com.pasterdream.pasterdreammod.api.util.ServerScheduler;
import com.pasterdream.pasterdreammod.registry.PDSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;

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
 */
public class WindKnightEntity extends GeckoLibMobEntity {

    /** 攻击挥动标记（供动画系统使用） */
    private boolean swinging;
    /** 上一次挥动的时间 */
    private long lastSwing;

    /**
     * AOE 充能计数（原版 persistentData {@code time}）：
     * 每 tick +1，≥180 且 11 格内有玩家时释放技能并归零。
     */
    private int aoeCharge = 0;

    /** 技能前摇进行中，避免重复触发 */
    private boolean aoeWindingUp = false;

    /**
     * 构造风之骑士实体
     *
     * @param type  实体类型
     * @param level 世界实例
     */
    public WindKnightEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
        this.xpReward = 32;
    }

    /**
     * 返回默认纹理名称
     *
     * @return 默认纹理 "wind_knight"
     */
    @Override
    protected String getDefaultTexture() {
        return "wind_knight";
    }

    // ======================== 同步数据 ========================

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
    }

    // ======================== 属性 ========================

    /**
     * 创建风之骑士实体的属性
     *
     * @return 属性构造器
     */
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
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
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
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
     * 服务端每 tick AOE（对齐原版 {@code WindKnightPr0}）。
     * <p>
     * 充能 180t → 11 格内有玩家 → skill_0 动画 + 缓慢 IV 20t → 5t 铁傀儡受伤音
     * → 25t 后半径约 6 格 30 伤 + 粒子/爆炸音 + 自身速度 I 20t。
     */
    private void serverAoeTick() {
        this.clearFire();
        if (aoeWindingUp) {
            return;
        }
        if (aoeCharge < 180) {
            aoeCharge++;
            return;
        }
        Player nearest = this.level().getNearestPlayer(this, 11.0);
        if (nearest == null || !nearest.isAlive()) {
            return;
        }

        aoeWindingUp = true;
        aoeCharge = 0;
        this.setAnimation("skill_0");
        this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 4, false, false));

        final double cx = this.getX();
        final double cy = this.getY();
        final double cz = this.getZ();
        final Level level = this.level();

        // 5t：铁傀儡受伤音
        ServerScheduler.schedule(5, () -> {
            if (!this.isAlive() || level.isClientSide()) {
                return;
            }
            level.playSound(null, this.blockPosition(), SoundEvents.IRON_GOLEM_HURT,
                    SoundSource.MASTER, 1.0F, 1.1F);
        });

        // 25t：爆发伤害与特效
        ServerScheduler.schedule(25, () -> {
            aoeWindingUp = false;
            if (!this.isAlive() || level.isClientSide()) {
                return;
            }
            TagKey<net.minecraft.world.entity.EntityType<?>> special = TagKey.create(
                    Registries.ENTITY_TYPE,
                    ResourceLocation.fromNamespaceAndPath("pasterdream", "special_entity_tag"));
            level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(6.0),
                            e -> e != this && !e.getType().is(special))
                    .forEach(e -> {
                        e.hurt(this.damageSources().mobAttack(this), 30.0F);
                        if (level instanceof net.minecraft.server.level.ServerLevel sl) {
                            sl.sendParticles(ParticleTypes.EXPLOSION,
                                    e.getX(), e.getY(), e.getZ(), 3, 0.1, 0.1, 0.1, 0.1);
                        }
                    });
            if (level instanceof net.minecraft.server.level.ServerLevel sl) {
                sl.sendParticles(ParticleTypes.CLOUD, cx, cy + 1.5, cz, 80, 3, 0.5, 3, 0.1);
                sl.sendParticles(ParticleTypes.CRIT, cx, cy + 1.5, cz, 80, 3, 0.5, 3, 0.1);
            }
            level.playSound(null, this.blockPosition(), PDSounds.WIND_KNIGHT_SKILL_0.get(),
                    SoundSource.MASTER, 1.1F, 0.9F);
            level.playSound(null, this.blockPosition(), SoundEvents.GENERIC_EXPLODE.value(),
                    SoundSource.MASTER, 0.7F, 1.0F);
            this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20, 0, false, false));
        });
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

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        super.registerControllers(controllers);
        controllers.add(new AnimationController<>(this, "movement", 4, this::movementPredicate));
        controllers.add(new AnimationController<>(this, "attacking", 4, this::attackingPredicate));
    }
}
