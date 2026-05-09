package net.pasterdream.init;

import net.pasterdream.configuration.PasterdreamConfigCommonConfiguration;
import net.pasterdream.configuration.PasterdreamConfigClientConfiguration;
import net.pasterdream.PasterdreamMod;

import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Mod.EventBusSubscriber(modid = PasterdreamMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class PasterdreamModConfigs {
	@SubscribeEvent
	public static void register(FMLConstructModEvent event) {
		event.enqueueWork(() -> {
			ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, PasterdreamConfigClientConfiguration.SPEC, "PasterDream-Client.toml");
			ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, PasterdreamConfigCommonConfiguration.SPEC, "PasterDream-Common.toml");
		});
	}
}
