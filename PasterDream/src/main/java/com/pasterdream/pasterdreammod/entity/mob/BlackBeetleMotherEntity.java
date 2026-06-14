package com.pasterdream.pasterdreammod.entity.mob;

import com.pasterdream.pasterdreammod.registry.PDEntities;
import com.pasterdream.pasterdreammod.registry.PDSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import com.pasterdream.pasterdreammod.api.entity.anim.ProcedureAnimationHandler;

/**
 * 黑甲虫母体 (Black Beetle Mother) — 地面敌对 Boss 级生物
 * <p>
 * 行为：
 * - 主动近战攻击
 * - 免疫摔落伤害
 * - 追踪范围大
 * - 带有 Boss 血条（粉色）
 * <p>
 * 动画：
 * - movement: idle / walk
 * - attacking: 触发式攻击动画
 * - procedure: 过程动画
 */
public class BlackBeetleMotherEntity extends Monster implements GeoEntity {

    private static final EntityDataAccessor<Boolean> SHOOT =
            SynchedEntityData.defineId(BlackBeetleMotherEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<String> ANIMATION =
            SynchedEntityData.defineId(BlackBeetleMotherEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> TEXTURE =
            SynchedEntityData.defineId(BlackBeetleMotherEntity.class, EntityDataSerializers.STRING);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    /** 客户端 procedure 动画处理器 */
    private final ProcedureAnimationHandler procAnim = new ProcedureAnimationHandler();

    /** 攻击挥动标记（供动画系统使用） */
    private boolean swinging;
    /** 上一次挥动的时间 */
    private long lastSwing;
    /** 过程动画名称（"empty" 表示无过程动画） */
    public String animationprocedure = "empty";

    /** 召唤技能冷却计时器 */
    private int summonCooldown = 0;

    /** Boss 血条（粉色进度条） */
    private final ServerBossEvent bossInfo = new ServerBossEvent(this.getDisplayName(),
            BossEvent.BossBarColor.PINK, BossEvent.BossBarOverlay.PROGRESS);

    /**
     * 构造黑甲虫母体实体
     *
     * @param type  实体类型
     * @param level 世界实例
     */
    public BlackBeetleMotherEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.xpReward = 10;
    }

    // ======================== 同步数据 ========================

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SHOOT, false);
        builder.define(ANIMATION, "undefined");
        builder.define(TEXTURE, "black_beetle_mother");
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
     * 创建黑甲虫母体实体的属性
     *
     * @return 属性构造器
     */
    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 100)
                .add(Attributes.ARMOR, 10)
                .add(Attributes.ATTACK_DAMAGE, 10)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.FOLLOW_RANGE, 48)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1);
    }

    // ======================== AI 目标 ========================

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2, false));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this).setAlertOthers());
        this.goalSelector.addGoal(3, new RandomStrollGoal(this, 1));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(5, new FloatGoal(this));
    }


    // ======================== 音效 ========================

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvent.createVariableRangeEvent(
                net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("pasterdream", "beetle_attack"));
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvent.createVariableRangeEvent(
                net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("pasterdream", "beetle_attack"));
    }

    // ======================== 受伤/免疫 ========================

    @Override
    public boolean hurt(DamageSource source, float amount) {
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
        // 服务端召唤技能逻辑
        if (!this.level().isClientSide()) {
            serverSummonTick();
        }
    }

    // ==================== 召唤技能实现 ====================

    /**
     * 服务端每 tick 召唤检测逻辑
     * 原 BlackBeetleMotherPr0Procedure 逻辑：
     * - 检测目标，播放 skill 动画 → 自身获得漂浮/抗性/吸收/缓慢 → 播放音效
     * - 在周围 4 个方向各召唤 1 只黑甲虫
     * - 给附近黑甲虫施加力量/速度/抗性，并设置目标为最近玩家
     * - 冷却 600 tick（30秒），且血量 > 1 时才可触发
     */
    private void serverSummonTick() {
        if (summonCooldown > 0) {
            summonCooldown--;
            return;
        }
        // 血量不足时不触发技能
        if (this.getHealth() <= 1) return;

        // 检测附近玩家
        Player target = this.level().getNearestPlayer(this, 10.0);
        if (target != null && target.isAlive()) {
            double x = this.getX();
            double y = this.getY();
            double z = this.getZ();

            // 播放 skill 动画
            this.setAnimation("empty");
            this.setAnimation("skill");

            // 自身获得增益和减益（原版：漂浮4秒、抗性II 4秒、吸收V 4秒、缓慢II 2秒）
            this.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 80, 0, false, false));
            this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 80, 1, false, false));
            this.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 80, 4, false, false));
            this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 1, false, false));

            // 播放召唤音效
            this.level().playSound(null, BlockPos.containing(x, y, z),
                    PDSounds.BEETLE_SKILL.get(), SoundSource.HOSTILE, 2.0F, 1.0F);

            // 在周围四个方向各召唤1只黑甲虫
            double[][] offsets = {{2, 0, 0}, {-2, 0, 0}, {0, 0, 2}, {0, 0, -2}};
            for (double[] offset : offsets) {
                BlackBeetleEntity beetle = new BlackBeetleEntity(PDEntities.BLACK_BEETLE.get(), this.level());
                beetle.setPos(this.getX() + offset[0], this.getY() + offset[1], this.getZ() + offset[2]);
                beetle.setTarget(target);
                this.level().addFreshEntity(beetle);
            }

            // 给附近 24 格内的所有黑甲虫施加增益（力量II 12秒、速度II 12秒、抗性I 12秒）
            AABB beetleAABB = new AABB(new Vec3(x, y, z), new Vec3(x, y, z)).inflate(24 / 2d);
            for (BlackBeetleEntity beetle : this.level().getEntitiesOfClass(
                    BlackBeetleEntity.class, beetleAABB, e -> e.isAlive())) {
                beetle.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 240, 1, false, false));
                beetle.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 240, 1, false, false));
                beetle.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 240, 0, false, false));
                // 设置目标为最近的玩家
                Player nearestPlayer = this.level().getNearestPlayer(beetle, 16.0);
                if (nearestPlayer != null) {
                    beetle.setTarget(nearestPlayer);
                }
            }

            // 自身回复 1 HP
            this.heal(1.0F);

            // 设置冷却（600 tick = 30秒）
            summonCooldown = 600;
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();
        this.updateSwingTime();
    }

    // ======================== Boss 血条 ========================

    public boolean canChangeDimensions() {
        return false;
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossInfo.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossInfo.removePlayer(player);
    }

    @Override
    public void customServerAiStep() {
        super.customServerAiStep();
        this.bossInfo.setProgress(this.getHealth() / this.getMaxHealth());
    }

    // ======================== 死亡处理 ========================

    @Override
    protected void tickDeath() {
        ++this.deathTime;
        if (this.deathTime == 20) {
            this.remove(RemovalReason.KILLED);
        }
    }

    // ======================== GeckoLib 动画 ========================

    /**
     * 移动状态动画控制器
     * 根据移动状态切换 idle / walk 动画
     */
    private PlayState movementPredicate(AnimationState<BlackBeetleMotherEntity> state) {
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
    private PlayState attackingPredicate(AnimationState<BlackBeetleMotherEntity> state) {
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
    private PlayState procedurePredicate(AnimationState<BlackBeetleMotherEntity> state) {
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