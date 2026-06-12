package com.pasterdream.pasterdreammod.item;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * memento_item_01 物品类
 * 使用后恢复 San+10、融梦能量+10，并获得幸运效果
 */
public class MementoItem01Item extends Item {

    public MementoItem01Item(Item.Properties properties) {
        super(properties.stacksTo(64));
}

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
player.getCooldowns().addCooldown(this, 40);
            stack.shrink(1);
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
}

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("\u00A77\u25AA \u00A79\u4F7F\u7528\u540E\u5E78\u8FD0+10\u6301\u7EED3\u5206\u949F"));
        tooltipComponents.add(Component.literal("\u00A77Aerolite_Dust\u7684\u4E13\u5C5E\u9057\u7269"));
        tooltipComponents.add(Component.literal("\u00A76PasterDream\u521B\u4F5C\u8005 \u5F00\u53D1\u8005"));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}