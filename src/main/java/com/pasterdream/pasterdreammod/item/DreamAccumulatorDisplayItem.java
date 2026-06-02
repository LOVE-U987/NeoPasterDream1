package com.pasterdream.pasterdreammod.item;

import net.minecraft.world.level.block.Block;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;

/**
 * 蓄梦池显示物品 (Dream Accumulator Display Item)
 * 用于手持时渲染 GeckoLab 动画模型，渲染器通过 RegisterClientExtensionsEvent 注册。
 */
public class DreamAccumulatorDisplayItem extends AbstractGeoDisplayItem {

    /**
     * 构造显示物品
     *
     * @param block      对应的方块
     * @param properties 物品属性
     */
    public DreamAccumulatorDisplayItem(Block block, Properties properties) {
        super(block, properties);
    }

    /**
     * 获取动画控制器名称
     *
     * @return 控制器名称字符串
     */
    @Override
    protected String getControllerName() {
        return "controller";
    }

    /**
     * 获取动画过渡刻数
     *
     * @return 过渡刻数
     */
    @Override
    protected int getTransitionTicks() {
        return 0;
    }

    /**
     * 动画状态谓词
     *
     * @param state 动画状态
     * @return 播放状态
     */
    @Override
    protected PlayState predicate(AnimationState<?> state) {
        return PlayState.CONTINUE;
    }
}