
/*
 * MCreator note: This file will be REGENERATED on each build.
 */
package net.pasterdream.init;

import net.pasterdream.fluid.ShadowLiquidFluid;
import net.pasterdream.fluid.MeltdreamLiquidFluid;
import net.pasterdream.PasterdreamMod;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ItemBlockRenderTypes;

public class PasterdreamModFluids {
	public static final DeferredRegister<Fluid> REGISTRY = DeferredRegister.create(ForgeRegistries.FLUIDS, PasterdreamMod.MODID);
	public static final RegistryObject<FlowingFluid> MELTDREAM_LIQUID = REGISTRY.register("meltdream_liquid", () -> new MeltdreamLiquidFluid.Source());
	public static final RegistryObject<FlowingFluid> FLOWING_MELTDREAM_LIQUID = REGISTRY.register("flowing_meltdream_liquid", () -> new MeltdreamLiquidFluid.Flowing());
	public static final RegistryObject<FlowingFluid> SHADOW_LIQUID = REGISTRY.register("shadow_liquid", () -> new ShadowLiquidFluid.Source());
	public static final RegistryObject<FlowingFluid> FLOWING_SHADOW_LIQUID = REGISTRY.register("flowing_shadow_liquid", () -> new ShadowLiquidFluid.Flowing());

	@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
	public static class ClientSideHandler {
		@SubscribeEvent
		public static void clientSetup(FMLClientSetupEvent event) {
			ItemBlockRenderTypes.setRenderLayer(MELTDREAM_LIQUID.get(), RenderType.translucent());
			ItemBlockRenderTypes.setRenderLayer(FLOWING_MELTDREAM_LIQUID.get(), RenderType.translucent());
			ItemBlockRenderTypes.setRenderLayer(SHADOW_LIQUID.get(), RenderType.translucent());
			ItemBlockRenderTypes.setRenderLayer(FLOWING_SHADOW_LIQUID.get(), RenderType.translucent());
		}
	}
}
