package net.pasterdream.procedures;

import net.pasterdream.entity.ShadowMagicballEntity;
import net.pasterdream.PasterdreamMod;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;

public class ShadowMagicballPr2Procedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		if (entity instanceof ShadowMagicballEntity) {
			((ShadowMagicballEntity) entity).setAnimation("idle");
		}
		PasterdreamMod.queueServerWork(30, () -> {
			if (entity.getPersistentData().getBoolean("switch") != true) {
				ShadowMagicballPr1Procedure.execute(world, x, y, z, entity);
			}
		});
	}
}
