
package net.pasterdream.block;

import net.pasterdream.init.PasterdreamModFluids;

import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.LiquidBlock;

public class ShadowLiquidBlock extends LiquidBlock {
	public ShadowLiquidBlock() {
		super(() -> PasterdreamModFluids.SHADOW_LIQUID.get(), BlockBehaviour.Properties.of().mapColor(MapColor.WATER).strength(100f).noCollission().noLootTable().liquid().pushReaction(PushReaction.DESTROY).sound(SoundType.EMPTY).replaceable());
	}
}
