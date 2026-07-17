package com.pasterdream.pasterdreammod.api.entity.base;

import com.pasterdream.pasterdreammod.api.entity.anim.ProcedureAnimationHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public abstract class GeckoLibMonsterEntity extends Monster implements GeoEntity {

    protected static final EntityDataAccessor<Boolean> SHOOT =
            SynchedEntityData.defineId(GeckoLibMonsterEntity.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<String> ANIMATION =
            SynchedEntityData.defineId(GeckoLibMonsterEntity.class, EntityDataSerializers.STRING);
    protected static final EntityDataAccessor<String> TEXTURE =
            SynchedEntityData.defineId(GeckoLibMonsterEntity.class, EntityDataSerializers.STRING);

    protected final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    protected final ProcedureAnimationHandler procAnim = new ProcedureAnimationHandler();
    protected String animationprocedure = "empty";

    public GeckoLibMonsterEntity(EntityType<? extends Monster> entityType, Level level) {
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
                (AnimationState<GeckoLibMonsterEntity>) state,
                level().isClientSide(),
                this::getSyncedAnimation,
                () -> setAnimation("empty"));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "procedure", 4, this::procedurePredicate));
    }
}