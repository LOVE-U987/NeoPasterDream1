
package net.pasterdream.world.features.treedecorators;

import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import net.minecraft.world.level.levelgen.feature.treedecorators.TrunkVineDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;

import com.mojang.serialization.Codec;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class BiomeDyedream0TrunkDecorator extends TrunkVineDecorator {
	public static Codec<BiomeDyedream0TrunkDecorator> CODEC = Codec.unit(BiomeDyedream0TrunkDecorator::new);
	public static TreeDecoratorType<?> DECORATOR_TYPE = new TreeDecoratorType<>(CODEC);

	@SubscribeEvent
	public static void registerPointOfInterest(RegisterEvent event) {
		event.register(ForgeRegistries.Keys.TREE_DECORATOR_TYPES, registerHelper -> registerHelper.register("biome_dyedream_0_tree_trunk_decorator", DECORATOR_TYPE));
	}

	@Override
	protected TreeDecoratorType<?> type() {
		return DECORATOR_TYPE;
	}

	@Override
	public void place(TreeDecorator.Context context) {
		context.logs().forEach(blockpos -> {
			if (context.random().nextInt(3) > 0) {
				BlockPos pos = blockpos.west();
				if (context.isAir(pos)) {
					context.setBlock(pos, Blocks.AIR.defaultBlockState());
				}
			}
			if (context.random().nextInt(3) > 0) {
				BlockPos pos = blockpos.east();
				if (context.isAir(pos)) {
					context.setBlock(pos, Blocks.AIR.defaultBlockState());
				}
			}
			if (context.random().nextInt(3) > 0) {
				BlockPos pos = blockpos.north();
				if (context.isAir(pos)) {
					context.setBlock(pos, Blocks.AIR.defaultBlockState());
				}
			}
			if (context.random().nextInt(3) > 0) {
				BlockPos pos = blockpos.south();
				if (context.isAir(pos)) {
					context.setBlock(pos, Blocks.AIR.defaultBlockState());
				}
			}
		});
	}
}
