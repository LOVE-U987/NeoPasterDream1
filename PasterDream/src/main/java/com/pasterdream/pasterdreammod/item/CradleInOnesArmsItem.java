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
 * cradle_in_ones_arms 物品类
 * 原版稀有度: COMMON
 */
public class CradleInOnesArmsItem extends Item {

    /**
     * 构造方法
     *
     * @param properties 物品属性
     */
    public CradleInOnesArmsItem(Item.Properties properties) {
        super(properties.stacksTo(64));
}

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("\u00A77\u25AA \u00A79\u5BF912*12\u533A\u57DF\u8303\u56F4\u5185\u751F\u6210\u72D0\u706B\u7ACB\u573A"));
        tooltipComponents.add(Component.literal("\u00A77\u25AA \u00A79\u4F7F\u9664\u73A9\u5BB6\u5916\u7684\u751F\u7269\u53D7\u523020%\u7684\u6613\u4F24\u548C\u7F13\u6162\u6548\u679C"));
        tooltipComponents.add(Component.literal("\u00A77\u25AA \u00A79\u7ED9\u4E88\u73A9\u5BB6\u751F\u547D\u6062\u590D"));
        tooltipComponents.add(Component.literal("\u00A77\u25AA \u00A79\u6301\u7EED\u65F6\u95F4 20\u79D2"));
        tooltipComponents.add(Component.literal("\u00A7f\u25AA \u00A74\u878D\u68A6\u80FD\u91CF -5"));
        tooltipComponents.add(Component.literal("\u00A77\u25AA \u00A79\u51B7\u5374\u65F6\u95F4 8 \u79D2"));
        tooltipComponents.add(Component.literal("\u00A7o\u00A77 -- Alirea\u5B9A\u5236\u7269\u54C1"));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
