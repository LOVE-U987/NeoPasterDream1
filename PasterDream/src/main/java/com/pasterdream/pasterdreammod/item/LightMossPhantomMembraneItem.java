package com.pasterdream.pasterdreammod.item;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

/**
 * light_moss_phantom_membrane 物品类
 * 原版稀有度: COMMON
 */
public class LightMossPhantomMembraneItem extends Item {

    /**
     * 构造方法
     *
     * @param properties 物品属性
     */
    public LightMossPhantomMembraneItem(Item.Properties properties) {
        super(properties.stacksTo(1));
}

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("tooltip.pasterdream.light_moss_phantom_membrane.quality"));
        tooltipComponents.add(Component.translatable("tooltip.pasterdream.light_moss_phantom_membrane.effect_1"));
        tooltipComponents.add(Component.translatable("tooltip.pasterdream.light_moss_phantom_membrane.effect_2"));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
