package net.pasterdream.procedures;

import net.minecraft.world.entity.player.Player;
import net.pasterdream.capability.SanCapability;
import net.pasterdream.init.PasterdreamModAttributes;
import net.pasterdream.configuration.PasterdreamConfigCommonConfiguration;
import net.pasterdream.PasterdreamMod;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber
public class PlayerEndSleepProcedure {
	@SubscribeEvent
	public static void onEntityEndSleep(PlayerWakeUpEvent event) {
		execute(event, event.getEntity().level(), event.getEntity());
	}

	public static void execute(LevelAccessor world, Entity entity) {
		execute(null, world, entity);
	}

	private static void execute(@Nullable Event event, LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		PasterdreamMod.queueServerWork(5, () -> {
			if (world instanceof Level _lvl0 && _lvl0.isDay()) {
                if(entity instanceof Player pl)
                {
                    SanCapability.addPlayerSanWithCheck(pl,PasterdreamConfigCommonConfiguration.SLEEP_SAN_RECOVERY_AMOUNT.get());
                }
			}
		});
	}
}
