package com.pasterdream.pasterdreammod.api.entity.base;

import com.pasterdream.pasterdreammod.api.entity.anim.ProcedureAnimationHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

/**
 * GeckoLib 动画弹射物基类
 * <p>
 * 为自定义弹射物实体提供统一的动画基础设施：
 * <ul>
 *   <li>纹理同步数据（TEXTURE）</li>
 *   <li>动画同步数据（ANIMATION）</li>
 *   <li>射击状态同步数据（SHOOT）</li>
 *   <li>procedure 动画控制器</li>
 *   <li>纹理 NBT 持久化</li>
 * </ul>
 * 子类只需实现 {@link #getDefaultTexture()} 并注册自己的动画控制器。
 */
public abstract class GeckoLibProjectileEntity extends Projectile implements GeoEntity {

    protected static final EntityDataAccessor<Boolean> SHOOT =
            SynchedEntityData.defineId(GeckoLibProjectileEntity.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<String> ANIMATION =
            SynchedEntityData.defineId(GeckoLibProjectileEntity.class, EntityDataSerializers.STRING);
    protected static final EntityDataAccessor<String> TEXTURE =
            SynchedEntityData.defineId(GeckoLibProjectileEntity.class, EntityDataSerializers.STRING);

    protected final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    protected final ProcedureAnimationHandler procAnim = new ProcedureAnimationHandler();
    protected String animationprocedure = "empty";

    /**
     * 构造动画弹射物实体
     *
     * @param entityType 实体类型
     * @param level      世界实例
     */
    public GeckoLibProjectileEntity(EntityType<? extends Projectile> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(SHOOT, false);
        builder.define(ANIMATION, "undefined");
        builder.define(TEXTURE, getDefaultTexture());
    }

    /**
     * 获取默认纹理名称
     *
     * @return 纹理名称
     */
    protected String getDefaultTexture() {
        return "unknown";
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
     * 设置纹理名称
     *
     * @param texture 纹理名称
     */
    public void setTexture(String texture) {
        this.entityData.set(TEXTURE, texture);
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

    /**
     * 获取射击状态
     *
     * @return 是否处于射击状态
     */
    public boolean isShooting() {
        return this.entityData.get(SHOOT);
    }

    /**
     * 设置射击状态
     *
     * @param shooting 射击状态
     */
    public void setShooting(boolean shooting) {
        this.entityData.set(SHOOT, shooting);
    }

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

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @SuppressWarnings("unchecked")
    private PlayState procedurePredicate(AnimationState<?> state) {
        return procAnim.predicate(
                (AnimationState<GeckoLibProjectileEntity>) state,
                level().isClientSide(),
                this::getSyncedAnimation,
                () -> setAnimation("empty"));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "procedure", 4, this::procedurePredicate));
    }
}
