package com.pasterdream.pasterdreammod.item;
import com.pasterdream.pasterdreammod.api.item.base.AbstractGeoDisplayItem;

import com.pasterdream.pasterdreammod.registry.PDBlocks;
import net.minecraft.world.item.Item;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;

/**
 * 精铸工作台显示物品 (Weapon Table Display Item)
 * 使用 GeoItem 实现 3D 物品手持渲染（原版 item model 走
 * displaysettings/weapon_table.item 的 builtin/entity 路线）。
 * <p>
 * 客户端渲染器通过 {@code PDClientItemExtensions} 中的
 * {@code RegisterClientExtensionsEvent} 单独注册，避免服务端类加载。
 */
public class WeaponTableDisplayItem extends AbstractGeoDisplayItem {

    /**
     * 构造精铸工作台显示物品
     *
     * @param properties 物品属性
     */
    public WeaponTableDisplayItem(Item.Properties properties) {
        super(PDBlocks.WEAPON_TABLE.get(), properties);
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
