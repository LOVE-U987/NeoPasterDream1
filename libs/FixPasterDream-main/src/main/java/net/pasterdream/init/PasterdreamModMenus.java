
/*
 *	MCreator note: This file will be REGENERATED on each build.
 */
package net.pasterdream.init;

import net.pasterdream.world.inventory.WorkshopBlastGuiMenu;
import net.pasterdream.world.inventory.WorkshopAnvilGuiMenu;
import net.pasterdream.world.inventory.WindmoorCrateGuiMenu;
import net.pasterdream.world.inventory.WeaponWorkshopGuiMenu;
import net.pasterdream.world.inventory.TheEndlessBookOfDreamSeekersGuiMenu;
import net.pasterdream.world.inventory.StorageBagGuiMenu;
import net.pasterdream.world.inventory.StorageBag0GuiMenu;
import net.pasterdream.world.inventory.ShadowSelectEndMenu;
import net.pasterdream.world.inventory.ShadowDeskGuiMenu;
import net.pasterdream.world.inventory.ShadowChestGuiMenu;
import net.pasterdream.world.inventory.ShadowBlastFurnaceGuiMenu;
import net.pasterdream.world.inventory.ResearchTableGuiMenu;
import net.pasterdream.world.inventory.PicnicBasketGuiMenu;
import net.pasterdream.world.inventory.MeltdreamChestGui0Menu;
import net.pasterdream.world.inventory.DyedreamDeskGuiMenu;
import net.pasterdream.world.inventory.DreamnotesGui0Menu;
import net.pasterdream.world.inventory.DreamCauldronGuiMenu;
import net.pasterdream.world.inventory.DreamAccumulatorGuiMenu;
import net.pasterdream.world.inventory.BlueprintGui0Menu;
import net.pasterdream.PasterdreamMod;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.common.extensions.IForgeMenuType;

import net.minecraft.world.inventory.MenuType;

public class PasterdreamModMenus {
	public static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.MENU_TYPES, PasterdreamMod.MODID);
	public static final RegistryObject<MenuType<DreamAccumulatorGuiMenu>> DREAM_ACCUMULATOR_GUI = REGISTRY.register("dream_accumulator_gui", () -> IForgeMenuType.create(DreamAccumulatorGuiMenu::new));
	public static final RegistryObject<MenuType<MeltdreamChestGui0Menu>> MELTDREAM_CHEST_GUI_0 = REGISTRY.register("meltdream_chest_gui_0", () -> IForgeMenuType.create(MeltdreamChestGui0Menu::new));
	public static final RegistryObject<MenuType<DyedreamDeskGuiMenu>> DYEDREAM_DESK_GUI = REGISTRY.register("dyedream_desk_gui", () -> IForgeMenuType.create(DyedreamDeskGuiMenu::new));
	public static final RegistryObject<MenuType<StorageBagGuiMenu>> STORAGE_BAG_GUI = REGISTRY.register("storage_bag_gui", () -> IForgeMenuType.create(StorageBagGuiMenu::new));
	public static final RegistryObject<MenuType<StorageBag0GuiMenu>> STORAGE_BAG_0_GUI = REGISTRY.register("storage_bag_0_gui", () -> IForgeMenuType.create(StorageBag0GuiMenu::new));
	public static final RegistryObject<MenuType<ShadowDeskGuiMenu>> SHADOW_DESK_GUI = REGISTRY.register("shadow_desk_gui", () -> IForgeMenuType.create(ShadowDeskGuiMenu::new));
	public static final RegistryObject<MenuType<BlueprintGui0Menu>> BLUEPRINT_GUI_0 = REGISTRY.register("blueprint_gui_0", () -> IForgeMenuType.create(BlueprintGui0Menu::new));
	public static final RegistryObject<MenuType<ShadowBlastFurnaceGuiMenu>> SHADOW_BLAST_FURNACE_GUI = REGISTRY.register("shadow_blast_furnace_gui", () -> IForgeMenuType.create(ShadowBlastFurnaceGuiMenu::new));
	public static final RegistryObject<MenuType<ResearchTableGuiMenu>> RESEARCH_TABLE_GUI = REGISTRY.register("research_table_gui", () -> IForgeMenuType.create(ResearchTableGuiMenu::new));
	public static final RegistryObject<MenuType<ShadowChestGuiMenu>> SHADOW_CHEST_GUI = REGISTRY.register("shadow_chest_gui", () -> IForgeMenuType.create(ShadowChestGuiMenu::new));
	public static final RegistryObject<MenuType<WeaponWorkshopGuiMenu>> WEAPON_WORKSHOP_GUI = REGISTRY.register("weapon_workshop_gui", () -> IForgeMenuType.create(WeaponWorkshopGuiMenu::new));
	public static final RegistryObject<MenuType<WorkshopBlastGuiMenu>> WORKSHOP_BLAST_GUI = REGISTRY.register("workshop_blast_gui", () -> IForgeMenuType.create(WorkshopBlastGuiMenu::new));
	public static final RegistryObject<MenuType<WorkshopAnvilGuiMenu>> WORKSHOP_ANVIL_GUI = REGISTRY.register("workshop_anvil_gui", () -> IForgeMenuType.create(WorkshopAnvilGuiMenu::new));
	public static final RegistryObject<MenuType<DreamnotesGui0Menu>> DREAMNOTES_GUI_0 = REGISTRY.register("dreamnotes_gui_0", () -> IForgeMenuType.create(DreamnotesGui0Menu::new));
	public static final RegistryObject<MenuType<DreamCauldronGuiMenu>> DREAM_CAULDRON_GUI = REGISTRY.register("dream_cauldron_gui", () -> IForgeMenuType.create(DreamCauldronGuiMenu::new));
	public static final RegistryObject<MenuType<ShadowSelectEndMenu>> SHADOW_SELECT_END = REGISTRY.register("shadow_select_end", () -> IForgeMenuType.create(ShadowSelectEndMenu::new));
	public static final RegistryObject<MenuType<WindmoorCrateGuiMenu>> WINDMOOR_CRATE_GUI = REGISTRY.register("windmoor_crate_gui", () -> IForgeMenuType.create(WindmoorCrateGuiMenu::new));
	public static final RegistryObject<MenuType<TheEndlessBookOfDreamSeekersGuiMenu>> THE_ENDLESS_BOOK_OF_DREAM_SEEKERS_GUI = REGISTRY.register("the_endless_book_of_dream_seekers_gui",
			() -> IForgeMenuType.create(TheEndlessBookOfDreamSeekersGuiMenu::new));
	public static final RegistryObject<MenuType<PicnicBasketGuiMenu>> PICNIC_BASKET_GUI = REGISTRY.register("picnic_basket_gui", () -> IForgeMenuType.create(PicnicBasketGuiMenu::new));
}
