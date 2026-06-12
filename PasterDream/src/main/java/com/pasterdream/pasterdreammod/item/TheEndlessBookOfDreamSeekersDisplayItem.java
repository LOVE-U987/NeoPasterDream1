package com.pasterdream.pasterdreammod.item;

import com.pasterdream.pasterdreammod.registry.PDBlocks;
import net.minecraft.world.item.Item;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;

/**
 * 寻梦者的永恒书卷显示物品
 * 使用 GeoItem 实现 3D 物品手持渲染（无 GeckoLib 动画的简化版本）
 */
public class TheEndlessBookOfDreamSeekersDisplayItem extends AbstractGeoDisplayItem {

    /**
     * 构造寻梦者的永恒书卷显示物品
     *
     * @param properties 物品属性
     */
    public TheEndlessBookOfDreamSeekersDisplayItem(Item.Properties properties) {
        super(PDBlocks.THE_ENDLESS_BOOK_OF_DREAM_SEEKERS.get(), properties);
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
