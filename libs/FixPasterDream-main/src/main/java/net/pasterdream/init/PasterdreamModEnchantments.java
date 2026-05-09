
/*
 *	MCreator note: This file will be REGENERATED on each build.
 */
package net.pasterdream.init;

import net.pasterdream.enchantment.EnchantmentSheltererEnchantment;
import net.pasterdream.enchantment.EnchantmentAtkspdEnchantment;
import net.pasterdream.PasterdreamMod;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.item.enchantment.Enchantment;

public class PasterdreamModEnchantments {
	public static final DeferredRegister<Enchantment> REGISTRY = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, PasterdreamMod.MODID);
	public static final RegistryObject<Enchantment> ENCHANTMENT_ATKSPD = REGISTRY.register("enchantment_atkspd", () -> new EnchantmentAtkspdEnchantment());
	public static final RegistryObject<Enchantment> ENCHANTMENT_SHELTERER = REGISTRY.register("enchantment_shelterer", () -> new EnchantmentSheltererEnchantment());
}
