/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.pasterdream.init;

import net.pasterdream.PasterdreamMod;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.EntityType;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class PasterdreamModAttributes {
	public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, PasterdreamMod.MODID);
    //@Deprecated(forRemoval = true, since = "1.3")
    //public static final RegistryObject<Attribute> MELTDREAMENERGY = ATTRIBUTES.register("meltdreamenergy", () -> (new RangedAttribute("attribute." + PasterdreamMod.MODID + ".meltdreamenergy", 0, 0, 100)).setSyncable(true));
	public static final RegistryObject<Attribute> SKILLMULTIPLIER = ATTRIBUTES.register("skillmultiplier", () -> (new RangedAttribute("attribute." + PasterdreamMod.MODID + ".skillmultiplier", 1, 0, 10)).setSyncable(true));
	public static final RegistryObject<Attribute> SKILLCD = ATTRIBUTES.register("skillcd", () -> (new RangedAttribute("attribute." + PasterdreamMod.MODID + ".skillcd", 1, 0, 2)).setSyncable(true));
	public static final RegistryObject<Attribute> TELEPORTATIONCD = ATTRIBUTES.register("teleportationcd", () -> (new RangedAttribute("attribute." + PasterdreamMod.MODID + ".teleportationcd", 1, 0, 10)).setSyncable(true));
	public static final RegistryObject<Attribute> TELEPORTATIONCONSUME = ATTRIBUTES.register("teleportationconsume", () -> (new RangedAttribute("attribute." + PasterdreamMod.MODID + ".teleportationconsume", 1, 0, 10)).setSyncable(true));
	public static final RegistryObject<Attribute> TELEPORTATIONRANGE = ATTRIBUTES.register("teleportationrange", () -> (new RangedAttribute("attribute." + PasterdreamMod.MODID + ".teleportationrange", 1, 0, 10)).setSyncable(true));
    //@Deprecated(forRemoval = true, since = "1.3")
    //public static final RegistryObject<Attribute> SAN = ATTRIBUTES.register("san", () -> (new RangedAttribute("attribute." + PasterdreamMod.MODID + ".san", 90, 0, 100)).setSyncable(true));
    public static final RegistryObject<Attribute> SAN_VARIABILITY = ATTRIBUTES.register("san_variability", () -> (new RangedAttribute("attribute." + PasterdreamMod.MODID + ".san_variability", 0, -120000, 120000)).setSyncable(true));
    public static final RegistryObject<Attribute> LUCK = ATTRIBUTES.register("luck", () -> (new RangedAttribute("attribute." + PasterdreamMod.MODID + ".luck", 0, -100, 1000)).setSyncable(true));
	public static final RegistryObject<Attribute> MAGICCD = ATTRIBUTES.register("magiccd", () -> (new RangedAttribute("attribute." + PasterdreamMod.MODID + ".magiccd", 1, 0, 10)).setSyncable(true));
	public static final RegistryObject<Attribute> MAGICPOWER = ATTRIBUTES.register("magicpower", () -> (new RangedAttribute("attribute." + PasterdreamMod.MODID + ".magicpower", 0, -10, 10)).setSyncable(true));
	public static final RegistryObject<Attribute> TOTALHEALING = ATTRIBUTES.register("total_healing", () -> (new RangedAttribute("attribute." + PasterdreamMod.MODID + ".total_healing", 1, 0, 10)).setSyncable(true));

	@SubscribeEvent
	public static void register(FMLConstructModEvent event) {
		event.enqueueWork(() -> {
			ATTRIBUTES.register(FMLJavaModLoadingContext.get().getModEventBus());
		});
	}

	@SubscribeEvent
	public static void addAttributes(EntityAttributeModificationEvent event) {
		//event.add(EntityType.PLAYER, MELTDREAMENERGY.get());
		event.add(EntityType.PLAYER, SKILLMULTIPLIER.get());
		event.add(EntityType.PLAYER, SKILLCD.get());
		event.add(EntityType.PLAYER, TELEPORTATIONCD.get());
		event.add(EntityType.PLAYER, TELEPORTATIONCONSUME.get());
		event.add(EntityType.PLAYER, TELEPORTATIONRANGE.get());
		//event.add(EntityType.PLAYER, SAN.get());
        event.add(EntityType.PLAYER, SAN_VARIABILITY.get());//每分钟的san变化率
        event.add(EntityType.PLAYER, LUCK.get());
		event.add(EntityType.PLAYER, MAGICCD.get());
		event.add(EntityType.PLAYER, MAGICPOWER.get());
		event.add(EntityType.PLAYER, TOTALHEALING.get());
	}

	/*@Mod.EventBusSubscriber
	private class Utils {
		@SubscribeEvent
		public static void persistAttributes(PlayerEvent.Clone event) {
			Player oldP = event.getOriginal();
			Player newP = event.getEntity();
			newP.getAttribute(MELTDREAMENERGY.get()).setBaseValue(oldP.getAttribute(MELTDREAMENERGY.get()).getBaseValue());
		}
	}*/
}
