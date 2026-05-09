package com.pasterdream.pasterdreammod.item;

import com.pasterdream.pasterdreammod.client.renderer.item.DreamAccumulatorDisplayItemRenderer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

/**
 * 蓄梦池显示物品 (Dream Accumulator Display Item)
 * 用于手持时渲染 GeckoLib 动画模型
 *
 * 原模组使用 MCreator 生成的 DisplayItem 系统，
 * 这里重新实现为 NeoForge 1.21.1 兼容版本
 */
public class DreamAccumulatorDisplayItem extends BlockItem implements GeoItem {

    /**
     * GeckoLib 动画实例缓存
     */
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    /**
     * 构造显示物品
     *
     * @param block 对应的方块
     * @param properties 物品属性
     */
    public DreamAccumulatorDisplayItem(Block block, Properties properties) {
        super(block, properties);
    }

    /**
     * 初始化客户端扩展
     * 注册自定义渲染器
     *
     * @param consumer 客户端扩展消费者
     */
    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private final BlockEntityWithoutLevelRenderer renderer = new DreamAccumulatorDisplayItemRenderer();

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return renderer;
            }
        });
    }

    /**
     * 注册动画控制器
     *
     * @param controllers 动画控制器注册器
     */
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, state -> {
            // 手持时持续播放动画
            return PlayState.CONTINUE;
        }));
    }

    /**
     * 获取动画实例缓存
     *
     * @return AnimatableInstanceCache 实例
     */
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
