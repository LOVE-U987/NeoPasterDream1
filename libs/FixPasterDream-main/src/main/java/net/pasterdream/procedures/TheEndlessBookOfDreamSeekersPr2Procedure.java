package net.pasterdream.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.particles.ParticleTypes;

public class TheEndlessBookOfDreamSeekersPr2Procedure {
	public static void execute(LevelAccessor world, double x, double y, double z) {
		if (world instanceof ServerLevel _level)
			_level.sendParticles(ParticleTypes.ENCHANT, (x + 0.5), (y + 0.5), (z + 0.5), 6, 0.4, 0.4, 0.4, 1);
	}
}
