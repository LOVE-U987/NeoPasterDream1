package com.pasterdream.pasterdreammod.item;

import com.pasterdream.pasterdreammod.api.item.base.AbstractGeoDisplayItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;

/**
 * 通用玩偶显示物品
 * <p>
 * 所有 API 玩偶的物品共用此类，客户端通过 {@link DollItemRenderer} 渲染。
 */
public class DollDisplayItem extends AbstractGeoDisplayItem {

    /**
     * 构造通用玩偶显示物品
     *
     * @param block      对应的方块
     * @param properties 物品属性
     */
    public DollDisplayItem(Block block, Item.Properties properties) {
        super(block, properties);
    }

    @Override
    protected String getControllerName() {
        return "doll_controller";
    }

    @Override
    protected int getTransitionTicks() {
        return 0;
    }

    @Override
    protected PlayState predicate(AnimationState<?> state) {
        return PlayState.STOP;
    }
}
