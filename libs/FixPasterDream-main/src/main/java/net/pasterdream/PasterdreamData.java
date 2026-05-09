package net.pasterdream;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.pasterdream.data.PDBlockTagProvider;
import net.pasterdream.data.PDItemTagProvider;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(modid = PasterdreamMod.MODID,bus = Mod.EventBusSubscriber.Bus.MOD)
public class PasterdreamData {
    @SubscribeEvent
    public static void gatherData(final GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        RegistrySetBuilder registrySetBuilder = new RegistrySetBuilder();

        boolean server = event.includeServer();
        DatapackBuiltinEntriesProvider datapackRegistryProvider = new DatapackBuiltinEntriesProvider(packOutput, lookupProvider, registrySetBuilder, Set.of(PasterdreamMod.MODID));
        generator.addProvider(server, datapackRegistryProvider);

        PDBlockTagProvider blockTags = new PDBlockTagProvider(packOutput, lookupProvider, existingFileHelper);
        generator.addProvider(server, blockTags);
        generator.addProvider(server, new PDItemTagProvider(packOutput, lookupProvider, blockTags.contentsGetter(), existingFileHelper));

    }
}
