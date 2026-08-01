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
        tooltipComponents.add(Component.translatable("tooltip.pasterdream.cradle_in_ones_arms.foxfire"));
        tooltipComponents.add(Component.translatable("tooltip.pasterdream.cradle_in_ones_arms.vulnerable"));
        tooltipComponents.add(Component.translatable("tooltip.pasterdream.cradle_in_ones_arms.regen"));
        tooltipComponents.add(Component.translatable("tooltip.pasterdream.cradle_in_ones_arms.duration"));
        tooltipComponents.add(Component.translatable("tooltip.pasterdream.cradle_in_ones_arms.energy_cost"));
        tooltipComponents.add(Component.translatable("tooltip.pasterdream.cradle_in_ones_arms.cooldown"));
        tooltipComponents.add(Component.translatable("tooltip.pasterdream.cradle_in_ones_arms.alirea"));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
