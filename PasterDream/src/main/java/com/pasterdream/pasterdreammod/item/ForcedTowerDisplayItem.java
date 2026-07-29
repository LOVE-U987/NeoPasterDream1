package com.pasterdream.pasterdreammod.item;
import com.pasterdream.pasterdreammod.api.item.base.AbstractGeoDisplayItem;

import com.pasterdream.pasterdreammod.registry.PDBlocks;
import net.minecraft.world.item.Item;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;

/**
 * 强征传送塔显示物品 (Forced Tower Display Item)
 * 使用 GeoItem 实现 3D 物品手持渲染（原版 item model 走
 * displaysettings/forced_tower.item 的 builtin/entity 路线）。
 * <p>
 * 客户端渲染器通过 {@code PDClientItemExtensions} 中的
 * {@code RegisterClientExtensionsEvent} 单独注册，避免服务端类加载。
 */
public class ForcedTowerDisplayItem extends AbstractGeoDisplayItem {

    /**
     * 构造强征传送塔显示物品
     *
     * @param properties 物品属性
     */
    public ForcedTowerDisplayItem(Item.Properties properties) {
        super(PDBlocks.FORCED_TOWER.get(), properties);
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
