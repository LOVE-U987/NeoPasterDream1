package com.pasterdream.pasterdreammod.item;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * 粉蛋（占位版 - 待实体注册完成后添加投掷功能）
 */
public class PinkeggItem extends Item {
    public PinkeggItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            // 占位：后续替换为投掷实体逻辑
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.EGG_THROW, SoundSource.PLAYERS, 0.5F, 0.4F);
            if (!player.getAbilities().instabuild) {
                itemStack.shrink(1);
            }
        }
        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide());
    }
}