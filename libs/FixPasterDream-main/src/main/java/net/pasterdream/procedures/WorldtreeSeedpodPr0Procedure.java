package net.pasterdream.procedures;

import net.pasterdream.capability.MeltDreamEnergyCapability;
import net.pasterdream.init.PasterdreamModBlocks;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.BlockPos;

public class WorldtreeSeedpodPr0Procedure {
	public static void execute(LevelAccessor world, double x, double y, double z, LivingEntity entity) {
		if (entity == null)
			return;
		if ((entity.level().dimension()) == (ResourceKey.create(Registries.DIMENSION, new ResourceLocation("pasterdream:dyedream_world")))
                && world.canSeeSkyFromBelowWater(BlockPos.containing(x, y, z))
                && world instanceof Level _lvl4 && _lvl4.isDay()
				&& entity.getY() > 160
                && ((world.getBlockState(BlockPos.containing(x, y - 1, z))).getBlock() == PasterdreamModBlocks.DYEDREAM_WORLDTREE_LEAVES.get()
						|| (world.getBlockState(BlockPos.containing(x, y - 2, z))).getBlock() == PasterdreamModBlocks.DYEDREAM_WORLDTREE_LEAVES.get())
                && entity.tickCount % 20 == 0) {
			if (entity instanceof Player pl)
            {
                pl.causeFoodExhaustion(0.4f);
                MeltDreamEnergyCapability.addPlayerMeltDreamEnergy(pl,0.1);
            }
		}
	}
}
