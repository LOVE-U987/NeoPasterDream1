package net.pasterdream.procedures;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffects;

public class RestrainmoveBlockBuffPr0Procedure {
	public static void execute(Entity entity) {
		if (entity == null)
			return;
		if (entity instanceof LivingEntity _entity)
			_entity.removeEffect(MobEffects.JUMP);
		if (entity instanceof Player _player) {
			_player.getAbilities().flying = false;
			_player.onUpdateAbilities();
		}
	}
}
