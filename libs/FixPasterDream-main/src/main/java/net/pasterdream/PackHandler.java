
package net.pasterdream;

import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.FilePackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forgespi.locating.IModFile;

import java.io.File;
import java.nio.file.Path;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class PackHandler {
    @SubscribeEvent
    public static void onAddPackFindersEvent(AddPackFindersEvent event) {
        if (!event.getPackType().equals(PackType.CLIENT_RESOURCES)) return;
        final IModFile modFile = ModList.get().getModFileById("pasterdream").getFile();
        final Path resourcePath = modFile.findResource("packs/paster_vanilla_ui");
        final PathPackResources pack = new PathPackResources(
                modFile.getFileName() + ":" + resourcePath,
                resourcePath,
                false
        );
        final Pack.ResourcesSupplier sup = v -> pack;
        final String name = "builtin/paster_vanilla_ui";
        event.addRepositorySource(c -> {
            c.accept(Pack.create(
                    name,
                    Component.translatable("pack.pasterdream.vanilla.title"),
                    true,
                    sup,
                    Pack.readPackInfo(name, sup),
                    PackType.CLIENT_RESOURCES,
                    Pack.Position.TOP,
                    false,
                    PackSource.BUILT_IN
                    ));
        });
    }
}
