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
        tooltipComponents.add(Component.literal("\u54C1\u8D28\uFF1A\u00A7b\u7CBE\u826F \u2605\u2605\u2605"));
        tooltipComponents.add(Component.literal("\u00A77\u25AA \u00A79\u5728\u7CBE\u795E\u503C\u5206\u522B\u4F4E\u4E8E60/40/20\u65F6"));
        tooltipComponents.add(Component.literal("\u00A77\u25AA \u00A79\u589E\u52A01/2/4\u70B9\u653B\u51FB\u529B"));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
