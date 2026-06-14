package com.pasterdream.pasterdreammod.entity.mob;

import com.pasterdream.pasterdreammod.registry.PDSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.SpectralArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import com.pasterdream.pasterdreammod.api.entity.anim.ProcedureAnimationHandler;

/**
 * 高压雷云 (Highvoltage Thundercloud) — 飞行敌对生物
 * <p>
 * 行为：
 * - 飞行移动，使用 FlyingMoveControl + FlyingPathNavigation
 * - 无重力，免疫火焰、摔落和闪电伤害
 * - 在三维空间中随机游荡
 * <p>
 * 渲染：GeckoLib 动画实体，始终播放 idle 动画
 */
public class HighvoltageThundercloudEntity extends Monster implements GeoEntity {

    private static final EntityDataAccessor<Boolean> SHOOT =
            SynchedEntityData.defineId(HighvoltageThundercloudEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<String> ANIMATION =
            SynchedEntityData.defineId(HighvoltageThundercloudEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> TEXTURE =
            SynchedEntityData.defineId(HighvoltageThundercloudEntity.class, EntityDataSerializers.STRING);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    /** 客户端 procedure 动画处理器 */
    private final ProcedureAnimationHandler procAnim = new ProcedureAnimationHandler();

    /** 过程动画名称（"empty" 表示无过程动画） */
    public String animationprocedure = "empty";

    /** 射击冷却计时器 */
    private int shootCooldown = 0;

    /**
     * 构造高压雷云实体
     *
     * @param type  实体类型
     * @param level 世界实例
     */
    public HighvoltageThundercloudEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.xpReward = 22;
        this.moveControl = new FlyingMoveControl(this, 10, true);
        this.setNoGravity(true);
    }

    // ======================== 同步数据 ========================

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SHOOT, false);
        builder.define(ANIMATION, "undefined");
        builder.define(TEXTURE, "highvoltage_thundercloud");
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

    // ======================== 导航 ========================

    @Override
    protected @NotNull PathNavigation createNavigation(Level level) {
        FlyingPathNavigation navigation = new FlyingPathNavigation(this, level);
        navigation.setCanOpenDoors(false);
        navigation.setCanFloat(true);
        navigation.setCanPassDoors(true);
        return navigation;
    }

    // ======================== 属性 ========================

    /**
     * 创建高压雷云实体的属性
     *
     * @return 属性构造器
     */
    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 50)
                .add(Attributes.ARMOR, 0)
                .add(Attributes.ATTACK_DAMAGE, 3)
                .add(Attributes.MOVEMENT_SPEED, 0.15)
                .add(Attributes.FLYING_SPEED, 0.15)
                .add(Attributes.FOLLOW_RANGE, 16)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.8);
    }

    // ======================== AI 目标 ========================

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new RandomStrollGoal(this, 0.4, 20) {
            @Override
            protected Vec3 getPosition() {
                RandomSource random = HighvoltageThundercloudEntity.this.getRandom();
                double dirX = HighvoltageThundercloudEntity.this.getX() + ((random.nextFloat() * 2 - 1) * 16);
                double dirY = HighvoltageThundercloudEntity.this.getY() + ((random.nextFloat() * 2 - 1) * 16);
                double dirZ = HighvoltageThundercloudEntity.this.getZ() + ((random.nextFloat() * 2 - 1) * 16);
                return new Vec3(dirX, dirY, dirZ);
            }
        });
    }

    // ======================== 音效 ========================

    @Override
    public void playStepSound(BlockPos pos, BlockState blockIn) {
        // 飞行实体无需步声音效
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.GENERIC_HURT;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvent.createVariableRangeEvent(
                net.minecraft.resources.ResourceLocation.withDefaultNamespace("block.candle.extinguish"));
    }

    // ======================== 受伤/免疫 ========================

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
        // 飞行生物，不处理摔落检测
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypes.IN_FIRE)) return false;
        if (source.is(DamageTypes.ON_FIRE)) return false;
        if (source.is(DamageTypes.LAVA)) return false;
        if (source.is(DamageTypes.LIGHTNING_BOLT)) return false;
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

    // ======================== 飞行行为 ========================

    @Override
    public void aiStep() {
        super.aiStep();
        this.setNoGravity(true);
    }

    @Override
    public void baseTick() {
        super.baseTick();
        this.refreshDimensions();
        // 服务端射击逻辑
        if (!this.level().isClientSide()) {
            serverShootTick();
        }
    }

    // ==================== 高压雷云射击逻辑 ====================

    /**
     * 服务端每 tick 射击检测逻辑
     * 高压雷云：更高概率触发（10%），伤害更强
     */
    private void serverShootTick() {
        if (shootCooldown > 0) {
            shootCooldown--;
            return;
        }
        // 约 10% 概率触发（每 tick），比普通雷云更频繁
        if (this.random.nextFloat() < 0.1f) {
            // 查找附近 24 格内的玩家（更远距离）
            Player target = this.level().getNearestPlayer(this, 24.0);
            if (target != null && target.isAlive() && this.hasLineOfSight(target)) {
                // 播放攻击音效
                this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                        PDSounds.THUNDERCLOUD_ATTACK.get(), this.getSoundSource(), 1.0F, 1.0F);
                // 发射光谱箭矢（雷电主题，速度更快）
                SpectralArrow arrow = new SpectralArrow(EntityType.SPECTRAL_ARROW, this.level());
                arrow.setPos(this.getX(), this.getY() + 1.5, this.getZ());
                arrow.setOwner(this);
                double dx = target.getX() - this.getX();
                double dy = target.getY(0.5) - (this.getY() + 1.5);
                double dz = target.getZ() - this.getZ();
                arrow.shoot(dx, dy, dz, 2.0F, 0.5F);
                // 高压雷云箭矢造成更高伤害
                arrow.setBaseDamage(6.0);
                this.level().addFreshEntity(arrow);
                // 设置冷却（60 tick = 3秒，更快）
                shootCooldown = 60;
            }
        }
    }

    @Override
    public void setNoGravity(boolean ignored) {
        super.setNoGravity(true);
    }

    // ======================== 死亡处理 ========================

    @Override
    protected void tickDeath() {
        ++this.deathTime;
        if (this.deathTime == 10) {
            this.remove(RemovalReason.KILLED);
        }
    }

    // ======================== GeckoLib 动画 ========================

    /**
     * 移动状态动画控制器
     * 高压雷云始终播放 idle 循环动画
     */
    private PlayState movementPredicate(AnimationState<HighvoltageThundercloudEntity> state) {
        if (this.getSyncedAnimation().equals("empty")) {
            return state.setAndContinue(RawAnimation.begin().thenLoop("idle"));
        }
        return PlayState.STOP;
    }

    /**
     * 过程动画控制器（用于触发一次性动画）
     */
    private PlayState procedurePredicate(AnimationState<HighvoltageThundercloudEntity> state) {
        return procAnim.predicate(state,
                level().isClientSide(),
                this::getSyncedAnimation,
                () -> setAnimation("empty"));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "movement", 4, this::movementPredicate));
        controllers.add(new AnimationController<>(this, "procedure", 4, this::procedurePredicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}