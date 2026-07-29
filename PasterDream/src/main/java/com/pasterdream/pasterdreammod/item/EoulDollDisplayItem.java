package com.pasterdream.pasterdreammod.item;
import com.pasterdream.pasterdreammod.api.item.base.AbstractGeoDisplayItem;

import com.pasterdream.pasterdreammod.registry.PDBlocks;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;

/**
 * 幼幼紫纪念玩偶显示物品 (Eoul Doll Display Item)
 * <p>
 * 使用 {@link AbstractGeoDisplayItem} 实现 3D 手持渲染，对应方块 {@link PDBlocks#EOUL_DOLL}。
 * 客户端渲染器通过 {@code PDClientItemExtensions} 单独注册。
 */
public class EoulDollDisplayItem extends AbstractGeoDisplayItem {

    /**
     * 构造幼幼紫纪念玩偶显示物品
     * <p>
     * 默认稀有度为史诗且不可被火焰烧毁。
     */
    public EoulDollDisplayItem() {
        this(new Item.Properties().rarity(Rarity.EPIC).fireResistant());
    }

    /**
     * 构造幼幼紫纪念玩偶显示物品
     *
     * @param properties 物品属性
     */
    public EoulDollDisplayItem(Item.Properties properties) {
        super(PDBlocks.EOUL_DOLL.get(), properties);
    }

    @Override
    protected String getControllerName() {
        return "display";
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
