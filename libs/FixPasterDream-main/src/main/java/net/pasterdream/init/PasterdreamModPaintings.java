
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.pasterdream.init;

import net.pasterdream.PasterdreamMod;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.entity.decoration.PaintingVariant;

public class PasterdreamModPaintings {
	public static final DeferredRegister<PaintingVariant> REGISTRY = DeferredRegister.create(ForgeRegistries.PAINTING_VARIANTS, PasterdreamMod.MODID);
	public static final RegistryObject<PaintingVariant> PASTERDREAM_DRAW = REGISTRY.register("pasterdream_draw", () -> new PaintingVariant(64, 64));
	public static final RegistryObject<PaintingVariant> PASTERDREAM_DRAW_0 = REGISTRY.register("pasterdream_draw_0", () -> new PaintingVariant(64, 64));
}
