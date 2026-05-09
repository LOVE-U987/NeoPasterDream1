package net.pasterdream.procedures;

import net.pasterdream.capability.SanCapability;
import net.pasterdream.init.PasterdreamModAttributes;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;

public class TerrorbeakPr0Procedure {
	public static void execute(Entity sourceentity) {
		if (sourceentity == null)
			return;
		if (sourceentity instanceof Player pl) {
            if (pl.getCapability(SanCapability.Provider.PLAYER_SAN_CAPABILITY).map(cap -> cap.getSanValue() <= 20).orElse(false)) {
                SanCapability.addPlayerSanWithCheck(pl,7);
            }
		}

	}
}
