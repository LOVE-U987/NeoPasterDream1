package com.pasterdream.pasterdreammod.item;

import com.pasterdream.pasterdreammod.block.entity.W4DataBlockEntity;
import com.pasterdream.pasterdreammod.registry.PDBlocks;
import com.pasterdream.pasterdreammod.registry.PDSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import java.util.List;

/** 方块数据重置工具：潜行右键暮影之笼重置 switch（原版 PasterBlockResetToolPr0） */
public class PasterBlockResetToolItem extends Item {
    public PasterBlockResetToolItem() {
        super(new Item.Properties().stacksTo(1).fireResistant());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext ctx, List<Component> tip, TooltipFlag flag) {
        super.appendHoverText(stack, ctx, tip, flag);
        tip.add(Component.literal("未完工"));
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        Level level = ctx.getLevel();
        Player player = ctx.getPlayer();
        BlockPos pos = ctx.getClickedPos();
        if (player == null || !player.isShiftKeyDown()) {
            return InteractionResult.PASS;
        }
        if (!level.getBlockState(pos).is(PDBlocks.TWILIGHT_LANTERN.get())) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide()) {
            W4DataBlockEntity.putBooleanAt(level, pos, "switch", false);
            level.sendBlockUpdated(pos, level.getBlockState(pos), level.getBlockState(pos), 3);
            level.playSound(null, pos, PDSounds.DING_0.get(), SoundSource.NEUTRAL, 1f, 1f);
            player.displayClientMessage(Component.literal("§e方块数据已重置"), false);
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }
}
