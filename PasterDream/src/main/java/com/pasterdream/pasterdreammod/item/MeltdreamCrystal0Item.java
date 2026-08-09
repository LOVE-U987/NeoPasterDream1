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
        super(properties.stacksTo(16));
}

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("tooltip.pasterdream.meltdream_crystal_0.line_1"));
        tooltipComponents.add(Component.translatable("tooltip.pasterdream.meltdream_crystal_0.line_2"));
        tooltipComponents.add(Component.translatable("tooltip.pasterdream.meltdream_crystal_0.line_3"));
        tooltipComponents.add(Component.translatable("tooltip.pasterdream.meltdream_crystal_0.line_4"));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
