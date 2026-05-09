package net.pasterdream.procedures;

import net.pasterdream.entity.AshBoneWingEntity;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.BlockPos;

public class BoneWingPr0Procedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		if (Math.random() <= 0.2) {
			if (world.isEmptyBlock(BlockPos.containing(entity.getX(), entity.getY() - 3, entity.getZ())) == false) {
				entity.setDeltaMovement(new Vec3(0, 0.1, 0));
			}
			if (entity instanceof AshBoneWingEntity) {
				if (world instanceof ServerLevel _level)
					_level.sendParticles(ParticleTypes.SMOKE, x, y, z, 16, 1, 1, 1, 0.05);
				if (world instanceof ServerLevel _level)
					_level.sendParticles(ParticleTypes.FLAME, x, y, z, 8, 0.6, 0.6, 0.6, 0.05);
			}
		}
	}
}
