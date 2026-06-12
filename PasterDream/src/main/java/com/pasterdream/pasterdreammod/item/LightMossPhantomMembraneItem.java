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
        super(properties.stacksTo(64));
}

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("\u54C1\u8D28\uFF1A\u00A7a\u4F18\u79C0 \u2605\u2605"));
        tooltipComponents.add(Component.literal("\u00A77\u25AA \u00A79\u4E3A\u6B63\u5728\u88C5\u5907\u7684\u9798\u7FC5\u6062\u590D\u8010\u4E45"));
        tooltipComponents.add(Component.literal("\u00A77\u25AA \u00A79\u5728\u9ED1\u6697\u7684\u73AF\u5883\u4E0B\u8010\u4E45\u6062\u590D\u901F\u5EA6\u51CF\u6162"));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
