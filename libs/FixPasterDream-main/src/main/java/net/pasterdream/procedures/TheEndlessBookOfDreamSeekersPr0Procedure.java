package net.pasterdream.procedures;

import net.pasterdream.world.inventory.TheEndlessBookOfDreamSeekersGuiMenu;
import net.pasterdream.init.PasterdreamModGameRules;
import net.pasterdream.PasterdreamMod;

import net.minecraftforge.network.NetworkHooks;

import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.MenuProvider;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.BlockPos;

import io.netty.buffer.Unpooled;

public class TheEndlessBookOfDreamSeekersPr0Procedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		{
			int _value = 1;
			BlockPos _pos = BlockPos.containing(x, y, z);
			BlockState _bs = world.getBlockState(_pos);
			if (_bs.getBlock().getStateDefinition().getProperty("animation") instanceof IntegerProperty _integerProp && _integerProp.getPossibleValues().contains(_value))
				world.setBlock(_pos, _bs.setValue(_integerProp, _value), 3);
		}
		if (world.getLevelData().getGameRules().getBoolean(PasterdreamModGameRules.PASTERDREAMDEBUGMODE) == true) {
			if (entity instanceof ServerPlayer _ent) {
				BlockPos _bpos = BlockPos.containing(x, y, z);
				NetworkHooks.openScreen((ServerPlayer) _ent, new MenuProvider() {
					@Override
					public Component getDisplayName() {
						return Component.literal("TheEndlessBookOfDreamSeekersGui");
					}

					@Override
					public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
						return new TheEndlessBookOfDreamSeekersGuiMenu(id, inventory, new FriendlyByteBuf(Unpooled.buffer()).writeBlockPos(_bpos));
					}
				}, _bpos);
			}
			PasterdreamMod.queueServerWork(1, () -> {
				TheEndlessBookOfDreamSeekersPr1Procedure.execute(world, x, y, z);
			});
		} else {
			TheEndlessBookOfDreamSeekersPr1Procedure.execute(world, x, y, z);
		}
	}
}
