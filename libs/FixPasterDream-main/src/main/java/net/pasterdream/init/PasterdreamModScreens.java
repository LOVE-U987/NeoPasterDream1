
/*
 *	MCreator note: This file will be REGENERATED on each build.
 */
package net.pasterdream.init;

import net.pasterdream.client.gui.WorkshopBlastGuiScreen;
import net.pasterdream.client.gui.WorkshopAnvilGuiScreen;
import net.pasterdream.client.gui.WindmoorCrateGuiScreen;
import net.pasterdream.client.gui.WeaponWorkshopGuiScreen;
import net.pasterdream.client.gui.TheEndlessBookOfDreamSeekersGuiScreen;
import net.pasterdream.client.gui.StorageBagGuiScreen;
import net.pasterdream.client.gui.StorageBag0GuiScreen;
import net.pasterdream.client.gui.ShadowSelectEndScreen;
import net.pasterdream.client.gui.ShadowDeskGuiScreen;
import net.pasterdream.client.gui.ShadowChestGuiScreen;
import net.pasterdream.client.gui.ShadowBlastFurnaceGuiScreen;
import net.pasterdream.client.gui.ResearchTableGuiScreen;
import net.pasterdream.client.gui.PicnicBasketGuiScreen;
import net.pasterdream.client.gui.MeltdreamChestGui0Screen;
import net.pasterdream.client.gui.DyedreamDeskGuiScreen;
import net.pasterdream.client.gui.DreamnotesGui0Screen;
import net.pasterdream.client.gui.DreamCauldronGuiScreen;
import net.pasterdream.client.gui.DreamAccumulatorGuiScreen;
import net.pasterdream.client.gui.BlueprintGui0Screen;

import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.client.gui.screens.MenuScreens;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class PasterdreamModScreens {
	@SubscribeEvent
	public static void clientLoad(FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
			MenuScreens.register(PasterdreamModMenus.DREAM_ACCUMULATOR_GUI.get(), DreamAccumulatorGuiScreen::new);
			MenuScreens.register(PasterdreamModMenus.MELTDREAM_CHEST_GUI_0.get(), MeltdreamChestGui0Screen::new);
			MenuScreens.register(PasterdreamModMenus.DYEDREAM_DESK_GUI.get(), DyedreamDeskGuiScreen::new);
			MenuScreens.register(PasterdreamModMenus.STORAGE_BAG_GUI.get(), StorageBagGuiScreen::new);
			MenuScreens.register(PasterdreamModMenus.STORAGE_BAG_0_GUI.get(), StorageBag0GuiScreen::new);
			MenuScreens.register(PasterdreamModMenus.SHADOW_DESK_GUI.get(), ShadowDeskGuiScreen::new);
			MenuScreens.register(PasterdreamModMenus.BLUEPRINT_GUI_0.get(), BlueprintGui0Screen::new);
			MenuScreens.register(PasterdreamModMenus.SHADOW_BLAST_FURNACE_GUI.get(), ShadowBlastFurnaceGuiScreen::new);
			MenuScreens.register(PasterdreamModMenus.RESEARCH_TABLE_GUI.get(), ResearchTableGuiScreen::new);
			MenuScreens.register(PasterdreamModMenus.SHADOW_CHEST_GUI.get(), ShadowChestGuiScreen::new);
			MenuScreens.register(PasterdreamModMenus.WEAPON_WORKSHOP_GUI.get(), WeaponWorkshopGuiScreen::new);
			MenuScreens.register(PasterdreamModMenus.WORKSHOP_BLAST_GUI.get(), WorkshopBlastGuiScreen::new);
			MenuScreens.register(PasterdreamModMenus.WORKSHOP_ANVIL_GUI.get(), WorkshopAnvilGuiScreen::new);
			MenuScreens.register(PasterdreamModMenus.DREAMNOTES_GUI_0.get(), DreamnotesGui0Screen::new);
			MenuScreens.register(PasterdreamModMenus.DREAM_CAULDRON_GUI.get(), DreamCauldronGuiScreen::new);
			MenuScreens.register(PasterdreamModMenus.SHADOW_SELECT_END.get(), ShadowSelectEndScreen::new);
			MenuScreens.register(PasterdreamModMenus.WINDMOOR_CRATE_GUI.get(), WindmoorCrateGuiScreen::new);
			MenuScreens.register(PasterdreamModMenus.THE_ENDLESS_BOOK_OF_DREAM_SEEKERS_GUI.get(), TheEndlessBookOfDreamSeekersGuiScreen::new);
			MenuScreens.register(PasterdreamModMenus.PICNIC_BASKET_GUI.get(), PicnicBasketGuiScreen::new);
		});
	}
}
