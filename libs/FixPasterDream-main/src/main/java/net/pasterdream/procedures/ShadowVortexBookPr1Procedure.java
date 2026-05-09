package net.pasterdream.procedures;

import net.minecraft.server.level.ServerPlayer;
import net.pasterdream.capability.MeltDreamEnergyCapability;
import net.pasterdream.capability.SanCapability;

public class ShadowVortexBookPr1Procedure {
	public static boolean execute(ServerPlayer sp) {
        boolean flag_1 = sp.getCapability(MeltDreamEnergyCapability.Provider.PLAYER_MELTDREAMENERGY_CAPABILITY).map(cap -> cap.getMeltDreamEnergy() >=0.01 || cap.getNoNeedConsume()).orElse(false);
        boolean flag_2_1 = sp.getCapability(SanCapability.Provider.PLAYER_SAN_CAPABILITY).map(cap -> cap.getSanValue() >= 0.05).orElse(false);
        boolean flag_2_2 = !SanCapability.IsSanCheckSystem();
        boolean flag = sp.getAbilities().instabuild;
		return flag || (flag_1 && (flag_2_1 || flag_2_2));
	}
}
