package com.pasterdream.pasterdreammod.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.Rarity;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

/**
 * 忆梦魔导透镜 (Dream Meter)
 * 使用 GeckoLib 实现完整 3D 手持模型渲染
 * 包含空闲旋转动画，在 GUI、第一人称、第三人称中均显示 3D 模型
 * <p>
 * 客户端渲染器通过 {@code PDClientItemExtensions} 中的
 * {@code RegisterClientExtensionsEvent} 单独注册，避免服务端类加载。
 */
public class DreamMeterItem extends Item implements GeoItem {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public String animationprocedure = "empty";
    public static ItemDisplayContext transformType;

    public DreamMeterItem() {
        super(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));
    }

    public void getTransformType(ItemDisplayContext type) {
        this.transformType = type;
    }

    private PlayState idlePredicate(software.bernie.geckolib.animation.AnimationState<DreamMeterItem> event) {
        if (this.transformType != null) {
            if (this.animationprocedure.equals("empty")) {
                event.getController().setAnimation(RawAnimation.begin().thenLoop("0"));
                return PlayState.CONTINUE;
            }
        }
        return PlayState.STOP;
    }

    private PlayState procedurePredicate(software.bernie.geckolib.animation.AnimationState<DreamMeterItem> event) {
        if (this.transformType != null) {
            if (!this.animationprocedure.equals("empty") && event.getController().getAnimationState() == AnimationController.State.STOPPED) {
                event.getController().setAnimation(RawAnimation.begin().thenPlay(this.animationprocedure));
                if (event.getController().getAnimationState() == AnimationController.State.STOPPED) {
                    this.animationprocedure = "empty";
                    event.getController().forceAnimationReset();
                }
            } else if (this.animationprocedure.equals("empty")) {
                return PlayState.STOP;
            }
        }
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        AnimationController<DreamMeterItem> procedureController = new AnimationController<>(this, "procedureController", 0, this::procedurePredicate);
        data.add(procedureController);
        AnimationController<DreamMeterItem> idleController = new AnimationController<>(this, "idleController", 0, this::idlePredicate);
        data.add(idleController);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}