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
        ItemStack stack = player.getItemInHand(hand);
        BlockPos below = BlockPos.containing(player.getX(), player.getY() - 1, player.getZ());
        if (level.getBlockState(below).getBlock() != Blocks.AIR) {
            return super.use(level, player, hand);
        }
        // 服务端权威：仅服务端放置方块并消耗物品，避免幽灵方块与双端不同步；
        // 客户端命中条件时返回 consume，以触发服务端再次执行 use。
        // 创造模式也扣 1：VERIFY 与原版空中放置手感一致；创造背包会再补满。
        if (!level.isClientSide()) {
            level.setBlock(below, PDBlocks.ANGEL_BLOCK.get().defaultBlockState(), 3);
            stack.shrink(1);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
