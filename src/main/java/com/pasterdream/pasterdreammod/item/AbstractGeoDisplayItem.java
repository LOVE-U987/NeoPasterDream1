package com.pasterdream.pasterdreammod.item;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

/**
 * 抽象地理显示物品基类 (Abstract Geo Display Item)
 * 封装所有使用 GeoItem + BlockItem 的显示物品的公共逻辑，
 * 统一动画注册和实例缓存管理。
 * 客户端渲染器扩展通过 RegisterClientExtensionsEvent 单独注册。
 * <p>
 * 注意：该类及其子类不能直接引用客户端专属类（如 BlockEntityWithoutLevelRenderer），
 * 否则专用服务端加载时会触发 ClassNotFoundException。
 * 客户端渲染由 {@code client.PDClientItemExtensions} 统一注册。
 */
public abstract class AbstractGeoDisplayItem extends BlockItem implements GeoItem {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    /**
     * 构造抽象地理显示物品
     *
     * @param block      对应的方块
     * @param properties 物品属性
     */
    public AbstractGeoDisplayItem(Block block, Item.Properties properties) {
        super(block, properties);
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    /**
     * 注册动画控制器
     * 子类通过 getControllerName、getTransitionTicks 和 predicate 自定义动画行为
     *
     * @param controllers 动画控制器注册器
     */
    @Override
    public final void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, getControllerName(), getTransitionTicks(), this::predicate));
    }

    /**
     * 获取动画实例缓存
     *
     * @return AnimatableInstanceCache 实例
     */
    @Override
    public final AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    /**
     * 获取动画控制器名称
     *
     * @return 控制器名称字符串
     */
    protected abstract String getControllerName();

    /**
     * 获取动画过渡刻数
     *
     * @return 过渡刻数
     */
    protected abstract int getTransitionTicks();

    /**
     * 动画状态谓词
     *
     * @param state 动画状态
     * @return 播放状态
     */
    protected abstract PlayState predicate(AnimationState<?> state);
}