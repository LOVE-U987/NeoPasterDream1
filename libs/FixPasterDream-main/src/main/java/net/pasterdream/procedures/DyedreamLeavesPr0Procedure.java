package net.pasterdream.procedures;

import net.minecraft.util.RandomSource;
import net.minecraft.util.Mth;

public class DyedreamLeavesPr0Procedure {
	public static boolean execute() {
		return Mth.nextDouble(RandomSource.create(), 1, 4) > 3;
	}
}
