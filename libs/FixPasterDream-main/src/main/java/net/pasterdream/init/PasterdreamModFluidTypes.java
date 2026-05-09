
/*
 * MCreator note: This file will be REGENERATED on each build.
 */
package net.pasterdream.init;

import net.pasterdream.fluid.types.ShadowLiquidFluidType;
import net.pasterdream.fluid.types.MeltdreamLiquidFluidType;
import net.pasterdream.PasterdreamMod;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.fluids.FluidType;

public class PasterdreamModFluidTypes {
	public static final DeferredRegister<FluidType> REGISTRY = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, PasterdreamMod.MODID);
	public static final RegistryObject<FluidType> MELTDREAM_LIQUID_TYPE = REGISTRY.register("meltdream_liquid", () -> new MeltdreamLiquidFluidType());
	public static final RegistryObject<FluidType> SHADOW_LIQUID_TYPE = REGISTRY.register("shadow_liquid", () -> new ShadowLiquidFluidType());
}
