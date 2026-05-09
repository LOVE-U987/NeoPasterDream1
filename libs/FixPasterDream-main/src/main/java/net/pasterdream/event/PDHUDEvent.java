package net.pasterdream.event;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLLoader;
import net.pasterdream.PasterdreamMod;
import net.pasterdream.configuration.PasterdreamConfigClientConfiguration;

@Mod.EventBusSubscriber(modid = PasterdreamMod.MODID, value = Dist.CLIENT)
public class PDHUDEvent {
    @SubscribeEvent
    public static void onRenderHUD(RenderGuiOverlayEvent event) {
        if (!PasterdreamConfigClientConfiguration.PASTER_HEALTH_HUD.get()) return;
        if (event.getOverlay() == VanillaGuiOverlay.PLAYER_HEALTH.type()) event.setCanceled(true);
    }
}
