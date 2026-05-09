package net.pasterdream.world;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.pasterdream.PasterdreamMod;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = PasterdreamMod.MODID)
public class VillageBuildingHelper {
    @SubscribeEvent
    public static void serverInitEvent(ServerAboutToStartEvent event) {
        Registry<StructureTemplatePool> templatePoolRegistry = event.getServer().registryAccess().registry(Registries.TEMPLATE_POOL).orElseThrow();

        registerBuild(templatePoolRegistry.get(new ResourceLocation("village/desert/houses")), String.format("%s:desert_cottage_0", PasterdreamMod.MODID), 200);
    }

    @SuppressWarnings("unchecked")
    private static void registerBuild(StructureTemplatePool templatePoolRegistry, String building, int weight) {
        SinglePoolElement element = StructurePoolElement.single(building).apply(StructureTemplatePool.Projection.TERRAIN_MATCHING);

        ObjectArrayList<StructurePoolElement> templates;
        List<Pair<StructurePoolElement, Integer>> rawTemplates;
        try {
            Field templatesField = StructureTemplatePool.class.getDeclaredField("templates");
            Field rawTemplatesField = StructureTemplatePool.class.getDeclaredField("rawTemplates");

            templatesField.setAccessible(true);
            rawTemplatesField.setAccessible(true);

            templates = (ObjectArrayList<StructurePoolElement>) templatesField.get(templatePoolRegistry);
            rawTemplates = (List<Pair<StructurePoolElement, Integer>>) rawTemplatesField.get(templatePoolRegistry);
        } catch (Exception e) {
            PasterdreamMod.LOGGER.error(e);
            return;
        }

        if (templates == null || rawTemplates == null) return;
        for (int i = 0; i < weight; i++) {
            templates.add(element);
        }

        List<Pair<StructurePoolElement, Integer>> listOfPieceEntries = new ArrayList<>(rawTemplates);
        listOfPieceEntries.add(new Pair<>(element, weight));

        try {
            rawTemplates.clear();
            rawTemplates.addAll(listOfPieceEntries);
        }catch (Exception ignore){

        }

    }
}
