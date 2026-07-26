package com.pasterdream.pasterdreammod.item;

import com.pasterdream.pasterdreammod.registry.PDBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

import java.util.List;

/**
 * 天使方块（物品形态，angel_block_item）。
 * <p>
 * 对照原版 {@code AngelBlockItemItem} + {@code AngelBlockItemPr0Procedure}：
 * 空中使用时，若脚下一格为空气则放置一个天使方块并消耗本物品。
 */
public class AngelBlockItemItem extends Item {

    public AngelBlockItemItem(Properties properties) {
        super(properties.stacksTo(64).rarity(Rarity.COMMON));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        // 原版即为硬编码中文提示
        tooltip.add(Component.literal("在空中使用以在脚下生成一个天使方块"));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        InteractionResultHolder<ItemStack> result = super.use(level, player, hand);
        BlockPos below = BlockPos.containing(player.getX(), player.getY() - 1, player.getZ());
        if (level.getBlockState(below).getBlock() == Blocks.AIR) {
            level.setBlock(below, PDBlocks.ANGEL_BLOCK.get().defaultBlockState(), 3);
            result.getObject().shrink(1);
        }
        return result;
    }
}
