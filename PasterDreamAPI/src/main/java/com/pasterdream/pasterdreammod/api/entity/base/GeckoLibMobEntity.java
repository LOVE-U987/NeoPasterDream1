package com.pasterdream.pasterdreammod.api.entity.base;

import com.pasterdream.pasterdreammod.api.entity.anim.ProcedureAnimationHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public abstract class GeckoLibMobEntity extends PathfinderMob implements GeoEntity {

    protected static final EntityDataAccessor<Boolean> SHOOT =
            SynchedEntityData.defineId(GeckoLibMobEntity.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<String> ANIMATION =
            SynchedEntityData.defineId(GeckoLibMobEntity.class, EntityDataSerializers.STRING);
    protected static final EntityDataAccessor<String> TEXTURE =
            SynchedEntityData.defineId(GeckoLibMobEntity.class, EntityDataSerializers.STRING);

    protected final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    protected final ProcedureAnimationHandler procAnim = new ProcedureAnimationHandler();
    protected String animationprocedure = "empty";

    public GeckoLibMobEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SHOOT, false);
        builder.define(ANIMATION, "undefined");
        builder.define(TEXTURE, getDefaultTexture());
    }

    protected String getDefaultTexture() {
        return "unknown";
    }

    public String getTexture() {
        return this.entityData.get(TEXTURE);
    }

    public void setTexture(String texture) {
        this.entityData.set(TEXTURE, texture);
    }

    public String getSyncedAnimation() {
        return this.entityData.get(ANIMATION);
    }

    public void setAnimation(String animation) {
        // 同名动画重复触发时（如 BOSS 连续两次 skill_magicball / skill_sprint），
        // 若当前同步值已经是该名称，entityData.set 不会标记脏数据、也不会重新同步，
        // 客户端就收不到新状态、无法重新播放。因此同名时先写 "empty" 强制脏标记，
        // 再写入目标动画，确保每次触发都重新同步到客户端。
        if (animation.equals(this.entityData.get(ANIMATION))) {
            this.entityData.set(ANIMATION, "empty");
        }
        this.entityData.set(ANIMATION, animation);
        this.animationprocedure = animation;
    }

    public boolean isShooting() {
        return this.entityData.get(SHOOT);
    }

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
                (AnimationState<GeckoLibMobEntity>) state,
                level().isClientSide(),
                this::getSyncedAnimation,
                () -> setAnimation("empty"));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "procedure", 4, this::procedurePredicate));
    }
}