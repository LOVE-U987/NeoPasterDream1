
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

public abstract class MeltdreamLiquidFluid extends ForgeFlowingFluid {
	public static final ForgeFlowingFluid.Properties PROPERTIES = new ForgeFlowingFluid.Properties(() -> PasterdreamModFluidTypes.MELTDREAM_LIQUID_TYPE.get(), () -> PasterdreamModFluids.MELTDREAM_LIQUID.get(),
			() -> PasterdreamModFluids.FLOWING_MELTDREAM_LIQUID.get()).explosionResistance(100f).tickRate(3).bucket(() -> PasterdreamModItems.MELTDREAM_LIQUID_BUCKET.get()).block(() -> (LiquidBlock) PasterdreamModBlocks.MELTDREAM_LIQUID.get());

	private MeltdreamLiquidFluid() {
		super(PROPERTIES);
	}

	public static class Source extends MeltdreamLiquidFluid {
		public int getAmount(FluidState state) {
			return 8;
		}

		public boolean isSource(FluidState state) {
			return true;
		}
	}

	public static class Flowing extends MeltdreamLiquidFluid {
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
