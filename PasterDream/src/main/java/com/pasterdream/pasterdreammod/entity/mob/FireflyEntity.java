package com.pasterdream.pasterdreammod.entity.mob;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.util.RandomSource;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;
import com.pasterdream.pasterdreammod.api.entity.anim.ProcedureAnimationHandler;

/**
 * 萤火虫 (Firefly) —— 染梦世界飞舞的发光小虫
 * <p>
 * 行为要点：
 * <ul>
 *   <li>被动小型生物，不会攻击</li>
 *   <li>三维飞行移动（FlyingMoveControl + FlyingPathNavigation + 无重力）</li>
 *   <li>可用玻璃罐捕捉（交互逻辑已预留，待物品移植后启用）</li>
 *   <li>免疫摔落伤害</li>
 * </ul>
 * <p>
 * 渲染：GeckoLib 动画实体，默认纹理 "firefly"
 */
public class FireflyEntity extends PathfinderMob implements GeoEntity {

    /** 射击状态同步标记（保留以兼容动画系统） */
    public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(FireflyEntity.class, EntityDataSerializers.BOOLEAN);
    /** 当前播放动画名称同步标记 */
    public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(FireflyEntity.class, EntityDataSerializers.STRING);
    /** 纹理名称同步标记（默认 "firefly"） */
    public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(FireflyEntity.class, EntityDataSerializers.STRING);

    /** GeckoLib 动画实例缓存 */
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    /** 攻击挥动标记（供动画系统使用） */
    private boolean swinging;
    /** 上一次挥动的时间 */
    private long lastSwing;
    /** 过程动画名称（"empty" 表示无过程动画） */
    public String animationprocedure = "empty";

    /** 客户端 procedure 动画处理器 */
    private final ProcedureAnimationHandler procAnim = new ProcedureAnimationHandler();

    /**
     * 构造萤火虫实体
     *
     * @param type  实体类型
     * @param level 世界实例
     */
    public FireflyEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
        this.moveControl = new FlyingMoveControl(this, 10, true);
        this.setNoGravity(true);
        this.xpReward = 1;
    }

    // ======================== 同步数据 ========================

    /**
     * 定义同步实体数据
     */
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SHOOT, false);
        builder.define(ANIMATION, "undefined");
        builder.define(TEXTURE, "firefly");
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

    /**
     * 创建飞行导航
     *
     * @param level 世界实例
     * @return 飞行路径导航
     */
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
     * 创建萤火虫的属性
     *
     * @return 属性构造器
     */
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.3)
                .add(Attributes.MAX_HEALTH, 2.0)
                .add(Attributes.ARMOR, 0.0)
                .add(Attributes.ATTACK_DAMAGE, 0.0)
                .add(Attributes.FOLLOW_RANGE, 16.0)
                .add(Attributes.FLYING_SPEED, 0.3);
    }

    // ======================== AI 目标 ========================

    @Override
    protected void registerGoals() {
        super.registerGoals();
        // 三维飞行随机游荡
        this.goalSelector.addGoal(1, new RandomStrollGoal(this, 0.8, 20) {
            @Override
            protected Vec3 getPosition() {
                RandomSource random = FireflyEntity.this.getRandom();
                double dirX = FireflyEntity.this.getX() + ((random.nextFloat() * 2 - 1) * 16);
                double dirY = FireflyEntity.this.getY() + ((random.nextFloat() * 2 - 1) * 16);
                double dirZ = FireflyEntity.this.getZ() + ((random.nextFloat() * 2 - 1) * 16);
                return new Vec3(dirX, dirY, dirZ);
            }
        });
        this.goalSelector.addGoal(2, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(3, new FloatGoal(this));
    }

    // ======================== 右键交互（玻璃罐捕捉）=======================

    /**
     * 玩家右键交互逻辑
     * <p>
     * TODO: ecology_glass_jar 和 light_firefly_glass_jar 物品尚未移植，
     * 当前交互逻辑保留框架，待物品注册后启用完整捕捉流程。
     *
     * @param player 交互的玩家
     * @param hand   交互的手
     * @return 交互结果
     */
    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        // TODO: 待 ecology_glass_jar 和 light_firefly_glass_jar 物品移植完成后，
        //  启用以下玻璃罐捕捉逻辑：
        //  if (itemStack.is(PDItems.ECOLOGY_GLASS_JAR.get())) {
        //      if (!player.level().isClientSide()) {
        //          itemStack.shrink(1);
        //          ItemStack result = new ItemStack(PDItems.LIGHT_FIREFLY_GLASS_JAR.get());
        //          if (!player.getInventory().add(result)) {
        //              player.drop(result, false);
        //          }
        //          this.discard();
        //      }
        //      return InteractionResult.sidedSuccess(player.level().isClientSide());
        //  }

        return super.mobInteract(player, hand);
    }

    // ======================== 受伤/免疫 ========================

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypes.FALL)) return false;
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
    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        // 萤火虫不发出脚步声
    }

    // ======================== 尺寸刷新 ========================

    @Override
    public void baseTick() {
        super.baseTick();
        this.refreshDimensions();
    }

    // ======================== 死亡处理 ========================

    @Override
    protected void tickDeath() {
        ++this.deathTime;
        if (this.deathTime == 20) {
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
     * 萤火虫始终播放 idle 循环动画
     *
     * @param state 动画状态
     * @return 播放状态
     */
    private PlayState movementPredicate(AnimationState<FireflyEntity> state) {
        if (this.getSyncedAnimation().equals("empty")) {
            if ((state.isMoving() || !(state.getLimbSwingAmount() > -0.15F && state.getLimbSwingAmount() < 0.15F)) && this.onGround()) {
                return state.setAndContinue(RawAnimation.begin().thenLoop("idle"));
            }
            if (!this.onGround()) {
                return state.setAndContinue(RawAnimation.begin().thenLoop("idle"));
            }
            return state.setAndContinue(RawAnimation.begin().thenLoop("idle"));
        }
        return PlayState.STOP;
    }

    /**
     * 过程动画控制器（用于触发一次性动画）
     *
     * @param state 动画状态
     * @return 播放状态
     */
    private PlayState procedurePredicate(AnimationState<FireflyEntity> state) {
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
