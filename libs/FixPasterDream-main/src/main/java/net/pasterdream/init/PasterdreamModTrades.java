
/*
*	MCreator note: This file will be REGENERATED on each build.
*/
package net.pasterdream.init;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.village.WandererTradesEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.common.BasicItemListing;

import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.npc.VillagerProfession;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PasterdreamModTrades {
	@SubscribeEvent
	public static void registerWanderingTrades(WandererTradesEvent event) {
		event.getGenericTrades().add(new BasicItemListing(new ItemStack(Items.EMERALD, 5),

				new ItemStack(PasterdreamModItems.JUNGLE_SPORE.get()), 10, 5, 0.05f));
		event.getGenericTrades().add(new BasicItemListing(new ItemStack(Items.EMERALD, 5),

				new ItemStack(PasterdreamModBlocks.CROP_1A.get(), 2), 10, 5, 0.05f));
		event.getGenericTrades().add(new BasicItemListing(new ItemStack(Items.EMERALD, 5),

				new ItemStack(PasterdreamModBlocks.CROP_4A.get(), 2), 10, 5, 0.05f));
		event.getGenericTrades().add(new BasicItemListing(new ItemStack(Items.EMERALD, 40),

				new ItemStack(PasterdreamModItems.FOURLEAF_CLOVER_CURIO.get()), 10, 5, 0.05f));
		event.getGenericTrades().add(new BasicItemListing(new ItemStack(Items.EMERALD, 2),

				new ItemStack(PasterdreamModItems.YEAST.get()), 10, 5, 0.05f));
	}

	@SubscribeEvent
	public static void registerTrades(VillagerTradesEvent event) {
		if (event.getType() == VillagerProfession.TOOLSMITH) {
			event.getTrades().get(5).add(new BasicItemListing(new ItemStack(PasterdreamModItems.PERGAMYN.get()), new ItemStack(Items.EMERALD, 28), new ItemStack(PasterdreamModItems.BLUEPRINT_1.get()), 10, 5, 0.05f));
		}
		if (event.getType() == VillagerProfession.FARMER) {
			event.getTrades().get(2).add(new BasicItemListing(new ItemStack(Items.EMERALD),

					new ItemStack(PasterdreamModItems.YEAST.get()), 10, 5, 0.05f));
		}
	}
}
