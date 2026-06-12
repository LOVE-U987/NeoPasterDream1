package com.pasterdream.pasterdreammod.item;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;

/**
 * blue_dew 物品类
 * 原版稀有度: COMMON
 */
public class BlueDewItem extends Item {

    /**
     * 构造方法
     *
     * @param properties 物品属性
     */
    public BlueDewItem(Item.Properties properties) {
        super(properties.stacksTo(64).food(new FoodProperties.Builder()
                    .nutrition(0).saturationModifier(0f).build()));
}

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("\u00A77\u996E\u7528\u540E\u83B7\u5F97\u6548\u679C:"));
        tooltipComponents.add(Component.literal("\u00A77\u25AA \u00A79\u6C34\u4E0B\u547C\u5438I \uFF081:00\uFF09"));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }
}
