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
 * strawberry_heart 物品类
 * 原版稀有度: COMMON
 */
public class StrawberryHeartItem extends Item {

    /**
     * 构造方法
     *
     * @param properties 物品属性
     */
    public StrawberryHeartItem(Item.Properties properties) {
        super(properties.stacksTo(64));
}

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("\u00A72\u6CD5\u672F\u4F24\u5BB3\uFF1A5"));
        tooltipComponents.add(Component.literal("\u00A72\u6CD5\u7403\u52A8\u80FD\uFF1A2"));
        tooltipComponents.add(Component.literal("\u00A72\u65BD\u6CD5\u51B7\u5374\uFF1A0.6\u79D2"));
        tooltipComponents.add(Component.literal("\u00A72\u65BD\u6CD5\u6D88\u8017\uFF1A\u00A7f\u9B54\u6CD5\u77F3"));
        tooltipComponents.add(Component.literal("\u00A77\u25AA \u00A79\u6F5C\u884C\u53F3\u51FB\u6F14\u594F \u00A74\u6D88\u80170.25 \u878D\u68A6\u80FD\u91CF"));
        tooltipComponents.add(Component.literal("\u00A77\u25AA \u00A79\u4E3A\u8303\u56F4\u5185\u73A9\u5BB6\u56DE\u590D4\u70B9\u751F\u547D \u5E76\u7ED9\u4E88\u77ED\u6682\u751F\u547D\u6062\u590D \u529B\u91CF \u548C\u901F\u5EA6\u6548\u679C"));
        tooltipComponents.add(Component.literal("\u00A7o\u00A77 -- Show by rock !"));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
