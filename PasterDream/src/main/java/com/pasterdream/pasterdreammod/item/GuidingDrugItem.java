package com.pasterdream.pasterdreammod.item;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

/**
 * guiding_drug 物品类
 * 原版稀有度: COMMON
 */
public class GuidingDrugItem extends Item {

    /**
     * 构造方法
     *
     * @param properties 物品属性
     */
    public GuidingDrugItem(Item.Properties properties) {
        super(properties.stacksTo(64));
}

}
