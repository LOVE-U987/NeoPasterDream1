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
        tooltipComponents.add(Component.translatable("tooltip.pasterdream.memento_item_01.effect"));
        tooltipComponents.add(Component.translatable("tooltip.pasterdream.memento_item_01.relic"));
        tooltipComponents.add(Component.translatable("tooltip.pasterdream.memento_item_01.author"));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}