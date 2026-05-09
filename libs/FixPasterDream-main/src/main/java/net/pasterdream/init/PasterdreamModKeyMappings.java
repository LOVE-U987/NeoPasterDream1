
/*
 *	MCreator note: This file will be REGENERATED on each build.
 */
package net.pasterdream.init;

import org.lwjgl.glfw.GLFW;

import net.pasterdream.network.TeleportationMessage;
import net.pasterdream.network.CloakActivateMessage;
import net.pasterdream.PasterdreamMod;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.client.Minecraft;
import net.minecraft.client.KeyMapping;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = {Dist.CLIENT})
public class PasterdreamModKeyMappings {
	public static final KeyMapping TELEPORTATION = new KeyMapping("key.pasterdream.teleportation", GLFW.GLFW_KEY_C, "key.categories.pasterdream") {
		private boolean isDownOld = false;

		@Override
		public void setDown(boolean isDown) {
			super.setDown(isDown);
			if (isDownOld != isDown && isDown) {
				PasterdreamMod.PACKET_HANDLER.sendToServer(new TeleportationMessage(0, 0));
				TeleportationMessage.pressAction(Minecraft.getInstance().player, 0, 0);
			}
			isDownOld = isDown;
		}
	};
	public static final KeyMapping CLOAK_ACTIVATE = new KeyMapping("key.pasterdream.cloak_activate", GLFW.GLFW_KEY_Z, "key.categories.misc") {
		private boolean isDownOld = false;

		@Override
		public void setDown(boolean isDown) {
			super.setDown(isDown);
			if (isDownOld != isDown && isDown) {
				PasterdreamMod.PACKET_HANDLER.sendToServer(new CloakActivateMessage(0, 0));
				CloakActivateMessage.pressAction(Minecraft.getInstance().player, 0, 0);
			}
			isDownOld = isDown;
		}
	};

	@SubscribeEvent
	public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
		event.register(TELEPORTATION);
		event.register(CLOAK_ACTIVATE);
	}

	@Mod.EventBusSubscriber({Dist.CLIENT})
	public static class KeyEventListener {
		@SubscribeEvent
		public static void onClientTick(TickEvent.ClientTickEvent event) {
			if (Minecraft.getInstance().screen == null) {
				TELEPORTATION.consumeClick();
				CLOAK_ACTIVATE.consumeClick();
			}
		}
	}
}
