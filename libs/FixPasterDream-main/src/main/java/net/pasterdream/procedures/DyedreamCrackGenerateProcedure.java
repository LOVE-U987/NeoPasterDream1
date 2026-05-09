package net.pasterdream.procedures;

import net.pasterdream.configuration.PasterdreamConfigCommonConfiguration;

public class DyedreamCrackGenerateProcedure {
	public static boolean execute() {
		return PasterdreamConfigCommonConfiguration.DYEDREAM_CRACK_GENERATE.get() == true;
	}
}
