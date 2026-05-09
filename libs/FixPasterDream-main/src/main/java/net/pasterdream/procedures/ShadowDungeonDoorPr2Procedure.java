package net.pasterdream.procedures;

import net.pasterdream.init.PasterdreamModItems;
import net.pasterdream.init.PasterdreamModBlocks;

import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.InteractionHand;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;

public class ShadowDungeonDoorPr2Procedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		if (world instanceof Level _level) {
			if (!_level.isClientSide()) {
				_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("pasterdream:shadow_door")), SoundSource.BLOCKS, 1, 1);
			} else {
				_level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("pasterdream:shadow_door")), SoundSource.BLOCKS, 1, 1, false);
			}
		}
		if ((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == PasterdreamModBlocks.SHADOW_DUNGEON_DOOR_0.get()) {
			if ((entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == PasterdreamModItems.SHADOW_DUNGEON_KEY.get()) {
				if (entity instanceof LivingEntity _entity) {
					ItemStack _setstack = new ItemStack(Blocks.AIR);
					_setstack.setCount(1);
					_entity.setItemInHand(InteractionHand.MAIN_HAND, _setstack);
					if (_entity instanceof Player _player)
						_player.getInventory().setChanged();
				}
				world.destroyBlock(BlockPos.containing(x, y, z), false);
				world.destroyBlock(BlockPos.containing(x + 1, y, z + 1), false);
				world.destroyBlock(BlockPos.containing(x - 1, y, z + 1), false);
				world.destroyBlock(BlockPos.containing(x - 1, y, z - 1), false);
				world.destroyBlock(BlockPos.containing(x + 1, y, z - 1), false);
				world.destroyBlock(BlockPos.containing(x + 1, y, z), false);
				world.destroyBlock(BlockPos.containing(x - 1, y, z), false);
				world.destroyBlock(BlockPos.containing(x, y, z + 1), false);
				world.destroyBlock(BlockPos.containing(x, y, z - 1), false);
			} else {
				if (entity instanceof Player _player && !_player.level().isClientSide())
					_player.displayClientMessage(Component.literal("\u9700\u8981\u5728\u672C\u5C42\u5BFB\u627E\u6697\u5F71\u5730\u7262\u94A5\u5319\u4EE5\u6253\u5F00\u5927\u95E8"), true);
			}
		} else {
			if (entity instanceof ServerPlayer _plr16 && _plr16.level() instanceof ServerLevel
					&& _plr16.getAdvancements().getOrStartProgress(_plr16.server.getAdvancements().getAdvancement(new ResourceLocation("pasterdream:achievement_shadow_npc_5"))).isDone()) {
				world.destroyBlock(BlockPos.containing(x, y, z), false);
				world.destroyBlock(BlockPos.containing(x + 1, y + 1, z), false);
				world.destroyBlock(BlockPos.containing(x - 1, y + 1, z), false);
				world.destroyBlock(BlockPos.containing(x - 1, y - 1, z), false);
				world.destroyBlock(BlockPos.containing(x + 1, y - 1, z), false);
				world.destroyBlock(BlockPos.containing(x + 1, y, z), false);
				world.destroyBlock(BlockPos.containing(x - 1, y, z), false);
				world.destroyBlock(BlockPos.containing(x, y + 1, z), false);
				world.destroyBlock(BlockPos.containing(x, y - 1, z), false);
			} else {
				if (entity instanceof Player _player && !_player.level().isClientSide())
					_player.displayClientMessage(Component.literal("\u5927\u95E8\u7D27\u95ED\u4E0D\u5F00"), true);
			}
		}
	}
}
