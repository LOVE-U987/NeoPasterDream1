package com.pasterdream.pasterdreammod.item;

import com.pasterdream.pasterdreammod.registry.PDBlocks;
import net.minecraft.world.item.Item;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;

/**
 * 娇小琴雨梦玩偶显示物品
 * 使用 GeoItem 实现 3D 物品渲染
 * <p>
 * 客户端渲染器通过 {@code PDClientItemExtensions} 中的
 * {@code RegisterClientExtensionsEvent} 单独注册，避免服务端类加载。
 */
public class QymDoll0DisplayItem extends AbstractGeoDisplayItem {

    /**
     * 构造娇小琴雨梦玩偶显示物品
     *
     * @param properties 物品属性
     */
    public QymDoll0DisplayItem(Item.Properties properties) {
        super(PDBlocks.QIN_DOLL_0.get(), properties);
    }

    /**
     * 获取动画控制器名称
     *
     * @return 控制器名称字符串
     */
    @Override
    protected String getControllerName() {
        return "display";
    }

    /**
     * 获取动画过渡刻数
     *
     * @return 过渡刻数
     */
    @Override
    protected int getTransitionTicks() {
        return 20;
    }

    /**
     * 动画状态谓词 - 静态模型，无动画
     */
    @Override
    protected PlayState predicate(AnimationState<?> state) {
        return PlayState.CONTINUE;
    }
}
