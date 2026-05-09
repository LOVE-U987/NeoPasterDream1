package net.pasterdream.procedures;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.pasterdream.capability.SanCapability;
import net.pasterdream.init.PasterdreamModAttributes;
import net.pasterdream.init.PasterdreamModGameRules;
import net.pasterdream.init.PasterdreamModMobEffects;
import net.pasterdream.configuration.PasterdreamConfigCommonConfiguration;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.TickEvent;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber
public class PlayerTotalTickUpdateProcedure {
	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
			execute(event, event.player.level(), event.player);
		}
	}

	public static void execute(Level world, Player entity) {
		execute(null, world, entity);
	}

	private static void execute(@Nullable Event event, Level world, Player entity) {
		if (entity == null)
			return;
		if (entity.tickCount % PasterdreamConfigCommonConfiguration.PLAYER_TOTAL_TICK_UPDATE.get() == 0) {
			if (!entity.hasEffect(PasterdreamModMobEffects.WINDPROOF_BUFF.get())) {
				WindDirectionPr1Procedure.execute(world, entity);
				WindDirectionPr2Procedure.execute(world, entity);
			}
		}
	}
}
