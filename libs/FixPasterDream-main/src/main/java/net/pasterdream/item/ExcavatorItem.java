
package net.pasterdream.item;

import net.pasterdream.init.PasterdreamModTabs;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.network.chat.Component;

import java.util.List;

public class ExcavatorItem extends Item {
	public ExcavatorItem() {
		super(new Item.Properties().stacksTo(1));
	}



    @Override
	public void appendHoverText(ItemStack itemstack, Level world, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, world, list, flag);
		list.add(Component.literal("\u00a7f\u53f3\u952e\u4f7f\u7528\u4ee5\u4f7f\u524d\u65b95*5*20\u8303\u56f4\u5185\u7684\u77f3\u5934\u548c\u6df1\u677f\u5ca9\u88ab\u6316\u6398"));
        list.add(Component.literal("\u00a7f\u4ec5\u9650\u521b\u9020\u6a21\u5f0f\uff01\u751f\u5b58\u614e\u7528\uff01"));
	}

    @Override
    public InteractionResult useOn(UseOnContext context) {
        var world = context.getLevel();
        var player = context.getPlayer();
        var pos = context.getClickedPos();
        var item = context.getItemInHand();
        if (!world.isClientSide && (world.getBlockState(pos).is(Blocks.STONE) || world.getBlockState(pos).is(Blocks.DEEPSLATE))) {
            switch (player.getMotionDirection()) {
                case NORTH -> {
                    for (int y = 0; y <= 4; ++y) for (int x = -2; x <= 2; ++x) for (int z = -19; z < 1; ++z)
                        mineBlock(item, world, new BlockPos(pos.getX() + x, pos.getY() + y, pos.getZ() + z), player);
                }
                case WEST -> {
                    for (int y = 0; y <= 4; ++y) for (int x = -19; x < 1; ++x) for (int z = -2; z <= 2; ++z)
                        mineBlock(item, world, new BlockPos(pos.getX() + x, pos.getY() + y, pos.getZ() + z), player);
                }
                case EAST -> {
                    for (int y = 0; y <= 4; ++y) for (int x = 0; x <= 19; ++x) for (int z = -2; z <= 2; ++z)
                        mineBlock(item, world, new BlockPos(pos.getX() + x, pos.getY() + y, pos.getZ() + z), player);
                }
                case SOUTH -> {
                    for (int y = 0; y <= 4; ++y) for (int x = -2; x <= 2; ++x) for (int z = 0; z <= 19; ++z)
                        mineBlock(item, world, new BlockPos(pos.getX() + x, pos.getY() + y, pos.getZ() + z), player);
                }
            }
            if (!player.isCreative()) item.shrink(1);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }

    protected void mineBlock(ItemStack item, Level world, BlockPos pos, Player entity) {
        var state = world.getBlockState(pos);
        if (!state.is(Blocks.STONE) && !state.is(Blocks.DEEPSLATE)) return;

        state.spawnAfterBreak((ServerLevel) world, pos, item, true);
        List<ItemStack> drops = Block.getDrops(state, (ServerLevel) world, pos, world.getBlockEntity(pos), entity, item);
        drops.forEach(e -> Block.popResourceFromFace(world, pos, ((BlockHitResult) entity.pick(20D, 1F, false)).getDirection(), e));
        world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
    }
}

