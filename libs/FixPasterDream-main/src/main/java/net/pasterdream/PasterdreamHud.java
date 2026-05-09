
package net.pasterdream;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = PasterdreamMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class PasterdreamHud {
	@SubscribeEvent
    public static void onRegisterOverlays(RegisterGuiOverlaysEvent event) {
        event.registerAbove(VanillaGuiOverlay.FOOD_LEVEL.id(), "meltdreamenergy_bar", MeltdreamenergyTank.guiOverlay);
        event.registerAbove(VanillaGuiOverlay.FOOD_LEVEL.id(), "san_bar", SanTank.guiOverlay);
        event.registerAbove(VanillaGuiOverlay.FOOD_LEVEL.id(), "lose_mind_gui", LoseMind.guiOverlay);
        event.registerAbove(VanillaGuiOverlay.FOOD_LEVEL.id(), "aaroncos_lefthand_boss_bar", AaroncosLefthandBossBar.guiOverlay);
        event.registerAbove(VanillaGuiOverlay.FOOD_LEVEL.id(), "couldmist_hud", CloudmistHud.guiOverlay);
        event.registerAbove(VanillaGuiOverlay.PLAYER_HEALTH.id(), "pd_health", new PlayerHealthHUD());
    }
}


