
package net.pasterdream.fluid;

import net.pasterdream.init.PasterdreamModItems;
import net.pasterdream.init.PasterdreamModFluids;
import net.pasterdream.init.PasterdreamModFluidTypes;
import net.pasterdream.init.PasterdreamModBlocks;

import net.minecraftforge.fluids.ForgeFlowingFluid;

import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.LiquidBlock;

public abstract class ShadowLiquidFluid extends ForgeFlowingFluid {
	public static final ForgeFlowingFluid.Properties PROPERTIES = new ForgeFlowingFluid.Properties(() -> PasterdreamModFluidTypes.SHADOW_LIQUID_TYPE.get(), () -> PasterdreamModFluids.SHADOW_LIQUID.get(),
			() -> PasterdreamModFluids.FLOWING_SHADOW_LIQUID.get()).explosionResistance(100f).bucket(() -> PasterdreamModItems.SHADOW_LIQUID_BUCKET.get()).block(() -> (LiquidBlock) PasterdreamModBlocks.SHADOW_LIQUID.get());

	private ShadowLiquidFluid() {
		super(PROPERTIES);
	}

	public static class Source extends ShadowLiquidFluid {
		public int getAmount(FluidState state) {
			return 8;
		}

		public boolean isSource(FluidState state) {
			return true;
		}
	}

	public static class Flowing extends ShadowLiquidFluid {
		protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
			super.createFluidStateDefinition(builder);
			builder.add(LEVEL);
		}

		public int getAmount(FluidState state) {
			return state.getValue(LEVEL);
		}

		public boolean isSource(FluidState state) {
			return false;
		}
	}
}
