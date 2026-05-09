package net.pasterdream.procedures;

import net.minecraft.server.level.ServerPlayer;
import net.pasterdream.capability.MeltDreamEnergyCapability;
import net.pasterdream.capability.SanCapability;
import net.pasterdream.init.PasterdreamModAttributes;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.pasterdream.init.PasterdreamModGameRules;
import net.pasterdream.init.PasterdreamModItems;
import top.theillusivec4.curios.api.CuriosApi;

public class SquealWaveWandPr2Procedure {
	public static boolean execute(ServerPlayer sp) {
        boolean flag_1 = sp.getCapability(MeltDreamEnergyCapability.Provider.PLAYER_MELTDREAMENERGY_CAPABILITY).map(cap -> cap.getMeltDreamEnergy() >=0.01 || cap.getNoNeedConsume()).orElse(false);
        boolean flag_2_1 = sp.getCapability(SanCapability.Provider.PLAYER_SAN_CAPABILITY).map(cap -> cap.getSanValue() >=0.02).orElse(false);
        boolean flag_2_2 = !SanCapability.IsSanCheckSystem();
        boolean flag = sp.getAbilities().instabuild || CuriosApi.getCuriosInventory(sp).map(inv->inv.findFirstCurio(PasterdreamModItems.GHOST_FACE_HEAD.get()).isPresent()).orElse(false);
		return flag || (flag_1 && (flag_2_1 || flag_2_2));
	}
}
