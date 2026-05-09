
package net.pasterdream.potion;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.pasterdream.PasterdreamMod;
import net.pasterdream.procedures.EvasionBuffPr0Procedure;

@Mod.EventBusSubscriber(modid = PasterdreamMod.MODID)
public class EvasionBuffMobEffect extends MobEffect {
	public EvasionBuffMobEffect() {
		super(MobEffectCategory.BENEFICIAL, -2752516);
	}

	@Override
	public String getDescriptionId() {
		return "effect.pasterdream.evasion_buff";
	}

	@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		return true;
	}

	public static class PlayerEvasionDamageEvent extends Event {
		private final Player player;
		public PlayerEvasionDamageEvent(Player player) {
			this.player = player;
		}

		public Player getPlayer() {
			return this.player;
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onPlayerDamage(PlayerEvasionDamageEvent event) {
		var player = event.getPlayer();
		EvasionBuffPr0Procedure.execute(player.level(), player.getX(), player.getY(), player.getZ(), player);
	}
}
