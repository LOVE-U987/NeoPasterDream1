package com.pasterdream.pasterdreammod.item;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

/**
 * shadow_breath 物品类
 * 原版稀有度: COMMON
 */
public class ShadowBreathItem extends Item {

    /**
     * 构造方法
     *
     * @param properties 物品属性
     */
    public ShadowBreathItem(Item.Properties properties) {
        super(properties.stacksTo(64));
}

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("tooltip.pasterdream.shadow_breath.quality"));
        tooltipComponents.add(Component.translatable("tooltip.pasterdream.shadow_breath.effect_1"));
        tooltipComponents.add(Component.translatable("tooltip.pasterdream.shadow_breath.effect_2"));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
