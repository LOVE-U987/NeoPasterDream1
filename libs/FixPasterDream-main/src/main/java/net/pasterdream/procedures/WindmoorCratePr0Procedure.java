package net.pasterdream.procedures;

import net.pasterdream.init.PasterdreamModParticleTypes;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.BlockPos;

public class WindmoorCratePr0Procedure {
	public static void execute(LevelAccessor world, double x, double y, double z) {
		if ((new Object() {
			public boolean getValue(LevelAccessor world, BlockPos pos, String tag) {
				BlockEntity blockEntity = world.getBlockEntity(pos);
				if (blockEntity != null)
					return blockEntity.getPersistentData().getBoolean(tag);
				return false;
			}
		}.getValue(world, BlockPos.containing(x, y, z), "new_loots")) == true) {
			if (world instanceof ServerLevel _level)
				_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.MELTDREAM_CRYSTAL_PARTICLE.get()), (x + 0.5), (y + 0.5), (z + 0.5), 4, 0.17, 0.2, 0.17, 0.01);
			if (world instanceof ServerLevel _level)
				_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.DUST_0_PARTICLE.get()), (x + 0.5), (y + 0.5), (z + 0.5), 4, 0.17, 0.2, 0.17, 0.01);
		}
	}
}
