package net.pasterdream.procedures;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.pasterdream.init.PasterdreamModBlocks;
import net.pasterdream.MultiBlock;

import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.sounds.SoundSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;

import java.util.List;
import java.util.Map;

public class WeaponWorkshopPr0Procedure {
	static Map<Integer, List<Block>> map = Maps.newHashMap();

	private static void initMaps() {
		map.put(0, Lists.newArrayList(
				// 0 - 4
				null,
				null,
				null,
				null,
				null,

				// 5 - 9
				null,
				null,
				null,
				PasterdreamModBlocks.WEAPON_WORKSHOP.get(),
				PasterdreamModBlocks.WORKSHOP_CAULDEON.get(),

				// 10 - 14
				null,
				PasterdreamModBlocks.WORKSHOP_BLAST.get(),
				null,
				null,
				null,

				// 15 - 19
				PasterdreamModBlocks.WORKSHOP_GRIND.get(),
				null,
				null,
				null,
				PasterdreamModBlocks.WORKSHOP_ANVIL.get(),

				// 20 - 24
				null,
				null,
				null,
				null,
				null
		));
	}

	public static void execute(Level world, Player player, double x, double y, double z) {
		if (map.isEmpty()) initMaps();

		if (!world.isClientSide())
            world.playSound(null, new BlockPos(Mth.floor(x), Mth.floor(y), Mth.floor(z)), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("pasterdream:machine0")), SoundSource.NEUTRAL, 1, 1);
		else
            world.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("pasterdream:machine0")), SoundSource.NEUTRAL, 1, 1, false);

		MultiBlock.INITIALIZE.setMultiBlock(map, player, world, new BlockPos(Mth.floor(x), Mth.floor(y) - 2, Mth.floor(z)), 1, 3, player.getDirection(), 1);
	}
}
