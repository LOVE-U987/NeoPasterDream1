
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.pasterdream.init;

import net.pasterdream.PasterdreamMod;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;

public class PasterdreamModPotions {
	public static final DeferredRegister<Potion> REGISTRY = DeferredRegister.create(ForgeRegistries.POTIONS, PasterdreamMod.MODID);
	public static final RegistryObject<Potion> DREAMWISH_POTION = REGISTRY.register("dreamwish_potion", () -> new Potion(new MobEffectInstance(PasterdreamModMobEffects.DREAMWISH_BUFF.get(), 3600, 0, false, true)));
	public static final RegistryObject<Potion> EXPUP_POTION = REGISTRY.register("expup_potion", () -> new Potion(new MobEffectInstance(PasterdreamModMobEffects.EXPUP_BUFF.get(), 3600, 0, false, true)));
	public static final RegistryObject<Potion> BASALT = REGISTRY.register("basalt", () -> new Potion(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 3600, 0, false, true), new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 1200, 0, false, true),
			new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 2400, 0, false, true)));
	public static final RegistryObject<Potion> FONDILLUSION_POTION = REGISTRY.register("fondillusion_potion", () -> new Potion(new MobEffectInstance(PasterdreamModMobEffects.FONDILLUSION_BUFF.get(), 3600, 0, false, true)));
}
