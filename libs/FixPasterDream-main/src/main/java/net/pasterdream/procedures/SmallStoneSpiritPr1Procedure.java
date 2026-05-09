package net.pasterdream.procedures;

import net.minecraft.world.entity.Entity;

public class SmallStoneSpiritPr1Procedure {
	public static double execute(Entity entity) {
		if (entity == null)
			return 0;
		return entity.getPersistentData().getDouble("size") + 1;
	}
}
