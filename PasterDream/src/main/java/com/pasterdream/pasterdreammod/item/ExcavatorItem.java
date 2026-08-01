package com.pasterdream.pasterdreammod.item;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.List;

/**
 * 挖掘机3000!（excavator）。
 * <p>
 * 直接移植自原版 ExcavatorItem（原版即为手写类，非 MCreator 模板）：
 * 右键石头或深板岩后，按玩家运动朝向挖掘前方 5*5*20 范围内的所有石头与深板岩，
 * 掉落物照常产出；非创造模式使用后消耗 1 个。
 */
public class ExcavatorItem extends Item {

    public ExcavatorItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.pasterdream.excavator.desc"));
        tooltip.add(Component.translatable("tooltip.pasterdream.excavator.warning"));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        BlockPos pos = context.getClickedPos();
        ItemStack stack = context.getItemInHand();
        if (player == null || level.isClientSide) {
            return InteractionResult.PASS;
        }
        if (!level.getBlockState(pos).is(Blocks.STONE) && !level.getBlockState(pos).is(Blocks.DEEPSLATE)) {
            return InteractionResult.FAIL;
        }
        // 按玩家运动朝向确定 5*5*20 挖掘区域（与原版逐轴循环一致）
        switch (player.getMotionDirection()) {
            case NORTH -> {
                for (int y = 0; y <= 4; ++y)
                    for (int x = -2; x <= 2; ++x)
                        for (int z = -19; z < 1; ++z)
                            mineBlock(stack, level, new BlockPos(pos.getX() + x, pos.getY() + y, pos.getZ() + z), player);
            }
            case WEST -> {
                for (int y = 0; y <= 4; ++y)
                    for (int x = -19; x < 1; ++x)
                        for (int z = -2; z <= 2; ++z)
                            mineBlock(stack, level, new BlockPos(pos.getX() + x, pos.getY() + y, pos.getZ() + z), player);
            }
            case EAST -> {
                for (int y = 0; y <= 4; ++y)
                    for (int x = 0; x <= 19; ++x)
                        for (int z = -2; z <= 2; ++z)
                            mineBlock(stack, level, new BlockPos(pos.getX() + x, pos.getY() + y, pos.getZ() + z), player);
            }
            case SOUTH -> {
                for (int y = 0; y <= 4; ++y)
                    for (int x = -2; x <= 2; ++x)
                        for (int z = 0; z <= 19; ++z)
                            mineBlock(stack, level, new BlockPos(pos.getX() + x, pos.getY() + y, pos.getZ() + z), player);
            }
            default -> { /* UP/DOWN 朝向不挖掘（与原版 switch 缺省分支一致） */ }
        }
        if (!player.isCreative()) {
            stack.shrink(1);
        }
        return InteractionResult.SUCCESS;
    }

    /** 挖掘单个方块：仅处理石头/深板岩，掉落物朝玩家视线命中面弹出 */
    private static void mineBlock(ItemStack tool, Level level, BlockPos pos, Player player) {
        BlockState state = level.getBlockState(pos);
        if (!state.is(Blocks.STONE) && !state.is(Blocks.DEEPSLATE)) {
            return;
        }
        ServerLevel serverLevel = (ServerLevel) level;
        state.spawnAfterBreak(serverLevel, pos, tool, true);
        List<ItemStack> drops = Block.getDrops(state, serverLevel, pos, level.getBlockEntity(pos), player, tool);
        drops.forEach(drop -> Block.popResourceFromFace(level, pos,
                ((BlockHitResult) player.pick(20.0d, 1.0f, false)).getDirection(), drop));
        level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
    }
}
