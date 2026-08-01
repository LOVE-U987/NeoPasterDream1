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
        tooltipComponents.add(Component.translatable("tooltip.pasterdream.strawberry_heart.spell_damage"));
        tooltipComponents.add(Component.translatable("tooltip.pasterdream.strawberry_heart.projectile_kinetic"));
        tooltipComponents.add(Component.translatable("tooltip.pasterdream.strawberry_heart.cooldown"));
        tooltipComponents.add(Component.translatable("tooltip.pasterdream.strawberry_heart.cost"));
        tooltipComponents.add(Component.translatable("tooltip.pasterdream.strawberry_heart.play"));
        tooltipComponents.add(Component.translatable("tooltip.pasterdream.strawberry_heart.heal"));
        tooltipComponents.add(Component.literal("\u00A7o\u00A77 -- Show by rock !"));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
