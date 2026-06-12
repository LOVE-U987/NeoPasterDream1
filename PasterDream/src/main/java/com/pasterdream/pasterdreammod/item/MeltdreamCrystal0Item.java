package com.pasterdream.pasterdreammod.item;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

/**
 * meltdream_crystal_0 物品类
 * 原版稀有度: COMMON
 */
public class MeltdreamCrystal0Item extends Item {

    /**
     * 构造方法
     *
     * @param properties 物品属性
     */
    public MeltdreamCrystal0Item(Item.Properties properties) {
        super(properties.stacksTo(64));
}

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("\u00A77\u6B64\u4E16\u95F4\u4E00\u5207\u62E5\u6709\u7075\u9B42\u7684\u751F\u7269\u7686\u6709\u68A6"));
        tooltipComponents.add(Component.literal("\u00A77\u90A3\u4E9B\u9192\u6765\u65F6\u65E0\u6CD5\u60F3\u8D77\u7684\u7EC6\u7F15\u56DE\u5FC6"));
        tooltipComponents.add(Component.literal("\u00A77\u7834\u788E\u5E76\u878D\u5316 \u88AB\u57CB\u85CF\u5728\u4E16\u754C\u5404\u5904\u51DD\u7ED3\u6210\u6676"));
        tooltipComponents.add(Component.literal("\u00A77\u7B49\u5F85\u7740\u88AB\u65B0\u7684\u7075\u9B42\u53D1\u73B0"));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
