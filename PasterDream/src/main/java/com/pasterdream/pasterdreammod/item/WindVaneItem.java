package com.pasterdream.pasterdreammod.item;

import com.pasterdream.pasterdreammod.registry.PDGameRules;
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
 * 风向标 (wind_vane)
 * <p>
 * 原版 {@code WindVaneItem} + {@code WindVanePr0Procedure}：
 * 显示玩家俯仰/偏航，并按游戏规则 {@code pasterdreamWindDirection} 报告当前风向。
 */
public class WindVaneItem extends Item {

    private static final String[] WIND_NAMES = {
            "§a北风", "§a东北风", "§a东风", "§a东南风",
            "§a南风", "§a西南风", "§a西风", "§a西北风"
    };

    public WindVaneItem() {
        super(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(Component.literal("§7检测当前的风向与玩家的角度"));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide()) {
            player.displayClientMessage(Component.literal(
                    "角度：" + String.format("%.2f", player.getXRot())
                            + " 方向：" + String.format("%.2f", player.getYRot())), true);
            level.playSound(null, player.blockPosition(), PDSounds.DING_0.get(),
                    SoundSource.PLAYERS, 1.0f, 1.0f);
            int dir = level.getGameRules().getInt(PDGameRules.WIND_DIRECTION);
            if (dir < 0 || dir >= WIND_NAMES.length) {
                dir = 0;
            }
            player.displayClientMessage(Component.literal("当前风向：" + WIND_NAMES[dir]), false);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
