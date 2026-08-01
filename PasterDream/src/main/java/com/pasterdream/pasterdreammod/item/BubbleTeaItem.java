package com.pasterdream.pasterdreammod.item;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;

/**
 * bubble_tea 物品类
 * 原版稀有度: COMMON
 */
public class BubbleTeaItem extends Item {

    /**
     * 构造方法
     *
     * @param properties 物品属性
     */
    public BubbleTeaItem(Item.Properties properties) {
        super(properties.stacksTo(64).food(new FoodProperties.Builder()
                    .nutrition(0).saturationModifier(0f).build()));
}

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("tooltip.pasterdream.bubble_tea.desc"));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }
}
