package com.pasterdream.pasterdreammod.item;
import com.pasterdream.pasterdreammod.api.item.base.AbstractGeoDisplayItem;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;

/**
 * W4 波次通用 GeckoLib 显示方块物品
 * <p>
 * 复用 {@link AbstractGeoDisplayItem} 的动画注册与缓存逻辑，
 * 固定 display 控制器（20 tick 过渡、持续播放），服务
 * 波次内所有 GeckoLib 方块的 3D 手持/展示渲染；
 * 客户端渲染器在 {@code PDClientFurniture} 中按注册名逐一绑定。
 */
public class W4GeoDisplayItem extends AbstractGeoDisplayItem {

    /**
     * 构造通用显示方块物品
     *
     * @param block      对应方块
     * @param properties 物品属性
     */
    public W4GeoDisplayItem(Block block, Item.Properties properties) {
        super(block, properties);
    }

    @Override
    protected String getControllerName() {
        return "display";
    }

    @Override
    protected int getTransitionTicks() {
        return 20;
    }

    @Override
    protected PlayState predicate(AnimationState<?> state) {
        return PlayState.CONTINUE;
    }
}
