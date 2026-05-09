package net.pasterdream.procedures;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.BlockPos;

public class JellyfishPr0Procedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		if (Math.random() > 0.1) {
			if (!world.isEmptyBlock(BlockPos.containing(x, y - 2, z))) {
				entity.setDeltaMovement(new Vec3(0, 0.1, 0));
			}
			if (world instanceof ServerLevel _level)
				_level.sendParticles(ParticleTypes.GLOW, x, y, z, 1, 0.15, 0.5, 0.15, 0.01);
		}
	}
}
