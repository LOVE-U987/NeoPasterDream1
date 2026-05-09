package net.pasterdream.procedures;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Mth;

public class ExpupBuffPr0Procedure {
	public static void execute(Entity entity) {
		if (entity == null)
			return;
		if (Mth.nextInt(RandomSource.create(), 1, 1000) <= 10) {
			if (entity instanceof Player _player)
				_player.giveExperiencePoints(1);
		}
	}
}
