package com.pasterdream.pasterdreammod.api.blockentity.base;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

/**
 * 自由 Compound + GeckoLib 双控制器方块实体骨架。
 * <ul>
 *   <li>{@code controller}：{@code animation==0} 时循环播放动画 {@code "0"}</li>
 *   <li>{@code procedurecontroller}：{@code animation!=0} 时单次播放编号动画，播完重置为 0</li>
 * </ul>
 * 动画属性名默认 {@code animation}，可覆写 {@link #animationPropertyName()}。
 */
public class GeoFreeDataBlockEntity extends FreeDataBlockEntity implements GeoBlockEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public GeoFreeDataBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    /** 方块状态上的 Integer 动画属性名 */
    protected String animationPropertyName() {
        return "animation";
    }

    protected int animationValue() {
        BlockState state = getBlockState();
        if (state.getBlock().getStateDefinition().getProperty(animationPropertyName()) instanceof IntegerProperty prop) {
            return state.getValue(prop);
        }
        return 0;
    }

    private PlayState idlePredicate(AnimationState<GeoFreeDataBlockEntity> event) {
        if (animationValue() == 0) {
            return event.setAndContinue(RawAnimation.begin().thenLoop("0"));
        }
        return PlayState.STOP;
    }

    private PlayState procedurePredicate(AnimationState<GeoFreeDataBlockEntity> event) {
        int anim = animationValue();
        String name = String.valueOf(anim);
        if (anim != 0 && event.getController().getAnimationState() == AnimationController.State.STOPPED) {
            event.getController().setAnimation(RawAnimation.begin().thenPlay(name));
            if (event.getController().getAnimationState() == AnimationController.State.STOPPED) {
                if (level != null
                        && getBlockState().getBlock().getStateDefinition()
                        .getProperty(animationPropertyName()) instanceof IntegerProperty prop) {
                    level.setBlock(getBlockPos(), getBlockState().setValue(prop, 0), 3);
                }
                event.getController().forceAnimationReset();
            }
        } else if (anim == 0) {
            return PlayState.STOP;
        }
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::idlePredicate));
        controllers.add(new AnimationController<>(this, "procedurecontroller", 0, this::procedurePredicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
