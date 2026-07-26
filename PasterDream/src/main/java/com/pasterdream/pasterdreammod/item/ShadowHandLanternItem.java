package com.pasterdream.pasterdreammod.item;

import com.pasterdream.pasterdreammod.registry.PDSounds;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * 暗影之手灯笼 (shadow_hand_lantern)
 * 完整 Gecko 动画/范围效果可后续加深；先保证注册、冷却与音效闭环。
 */
public class ShadowHandLanternItem extends Item {
    public ShadowHandLanternItem() {
        super(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));
    }
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext ctx, List<Component> tip, TooltipFlag flag) {
        super.appendHoverText(stack, ctx, tip, flag);
        tip.add(Component.literal("§7召唤暗影之手的灯笼"));
    }
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide()) {
            player.getCooldowns().addCooldown(this, 160);
            level.playSound(null, player.blockPosition(), PDSounds.SHADOW_HAND_LANTERN.get(), SoundSource.PLAYERS, 1f, 1f);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
