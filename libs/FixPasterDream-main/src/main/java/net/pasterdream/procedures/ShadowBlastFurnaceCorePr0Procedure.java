package net.pasterdream.procedures;

import net.pasterdream.init.PasterdreamModItems;
import net.pasterdream.init.PasterdreamModBlocks;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;

public class ShadowBlastFurnaceCorePr0Procedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		if ((entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == PasterdreamModItems.BLUEPRINT_0.get()) {
			if ((world.getBlockState(BlockPos.containing(x + 0, y + -1, z + 0))).getBlock() == PasterdreamModBlocks.CHISELED_SHADOW_STONE_BRICK.get()
					&& (world.getBlockState(BlockPos.containing(x + 1, y + -1, z + 1))).getBlock() == PasterdreamModBlocks.SHADOW_STONE_TILES.get()
					&& (world.getBlockState(BlockPos.containing(x + 1, y + -1, z + -1))).getBlock() == PasterdreamModBlocks.SHADOW_STONE_TILES.get()
					&& (world.getBlockState(BlockPos.containing(x + -1, y + -1, z + -1))).getBlock() == PasterdreamModBlocks.SHADOW_STONE_TILES.get()
					&& (world.getBlockState(BlockPos.containing(x + -1, y + -1, z + 1))).getBlock() == PasterdreamModBlocks.SHADOW_STONE_TILES.get()
					&& (world.getBlockState(BlockPos.containing(x + 1, y + -1, z + 0))).getBlock() == PasterdreamModBlocks.SHADOW_STONE_BRICK_SLAB.get()
					&& (world.getBlockState(BlockPos.containing(x + -1, y + -1, z + 0))).getBlock() == PasterdreamModBlocks.SHADOW_STONE_BRICK_SLAB.get()
					&& (world.getBlockState(BlockPos.containing(x + 0, y + -1, z + 1))).getBlock() == PasterdreamModBlocks.SHADOW_STONE_BRICK_SLAB.get()
					&& (world.getBlockState(BlockPos.containing(x + 0, y + -1, z + -1))).getBlock() == PasterdreamModBlocks.SHADOW_STONE_BRICK_SLAB.get()
					&& (world.getBlockState(BlockPos.containing(x + 1, y + 0, z + 1))).getBlock() == PasterdreamModBlocks.SHADOW_STONE_TILES_WALL.get()
					&& (world.getBlockState(BlockPos.containing(x + 1, y + 0, z + -1))).getBlock() == PasterdreamModBlocks.SHADOW_STONE_TILES_WALL.get()
					&& (world.getBlockState(BlockPos.containing(x + -1, y + 0, z + -1))).getBlock() == PasterdreamModBlocks.SHADOW_STONE_TILES_WALL.get()
					&& (world.getBlockState(BlockPos.containing(x + -1, y + 0, z + 1))).getBlock() == PasterdreamModBlocks.SHADOW_STONE_TILES_WALL.get()
					&& (world.getBlockState(BlockPos.containing(x + 1, y + 1, z + 1))).getBlock() == PasterdreamModBlocks.RUST_BLACK_METAL_BLOCK_WALL.get()
					&& (world.getBlockState(BlockPos.containing(x + 1, y + 1, z + -1))).getBlock() == PasterdreamModBlocks.RUST_BLACK_METAL_BLOCK_WALL.get()
					&& (world.getBlockState(BlockPos.containing(x + -1, y + 1, z + -1))).getBlock() == PasterdreamModBlocks.RUST_BLACK_METAL_BLOCK_WALL.get()
					&& (world.getBlockState(BlockPos.containing(x + -1, y + 1, z + 1))).getBlock() == PasterdreamModBlocks.RUST_BLACK_METAL_BLOCK_WALL.get()
					&& (world.getBlockState(BlockPos.containing(x + 0, y + 1, z + 0))).getBlock() == PasterdreamModBlocks.RUST_BLACK_METAL_BLOCK.get()) {
				world.destroyBlock(BlockPos.containing(x + 1, y + -1, z + 1), false);
				world.destroyBlock(BlockPos.containing(x + -1, y + -1, z + 1), false);
				world.destroyBlock(BlockPos.containing(x + -1, y + -1, z + -1), false);
				world.destroyBlock(BlockPos.containing(x + 1, y + -1, z + -1), false);
				world.destroyBlock(BlockPos.containing(x + 1, y + -1, z + 0), false);
				world.destroyBlock(BlockPos.containing(x + -1, y + -1, z + 0), false);
				world.destroyBlock(BlockPos.containing(x + 0, y + -1, z + 1), false);
				world.destroyBlock(BlockPos.containing(x + 0, y + -1, z + -1), false);
				world.destroyBlock(BlockPos.containing(x + 1, y + 0, z + -1), false);
				world.destroyBlock(BlockPos.containing(x + -1, y + 0, z + -1), false);
				world.destroyBlock(BlockPos.containing(x + -1, y + 0, z + 1), false);
				world.destroyBlock(BlockPos.containing(x + 1, y + 0, z + 1), false);
				world.destroyBlock(BlockPos.containing(x + 1, y + 1, z + 1), false);
				world.destroyBlock(BlockPos.containing(x + -1, y + 1, z + 1), false);
				world.destroyBlock(BlockPos.containing(x + -1, y + 1, z + -1), false);
				world.destroyBlock(BlockPos.containing(x + 1, y + 1, z + -1), false);
				world.destroyBlock(BlockPos.containing(x + 0, y + 1, z + 0), false);
				world.destroyBlock(BlockPos.containing(x + 0, y + 0, z + 0), false);
				world.setBlock(BlockPos.containing(x, y - 1, z), PasterdreamModBlocks.SHADOW_BLAST_FURNACE.get().defaultBlockState(), 3);
				ShadowBlastFurnacePr0Procedure.execute(world, x, (y - 1), z);
			} else {
				if (entity instanceof Player _player && !_player.level().isClientSide())
					_player.displayClientMessage(Component.literal("\u591A\u65B9\u5757\u7ED3\u6784\u4E0D\u5B8C\u6574"), true);
			}
		} else {
			if (entity instanceof Player _player && !_player.level().isClientSide())
				_player.displayClientMessage(Component.literal("\u7F3A\u5C11\u84DD\u56FE \u8BF7\u624B\u6301\u84DD\u56FE\u70B9\u51FB\u6838\u5FC3"), true);
		}
	}
}
