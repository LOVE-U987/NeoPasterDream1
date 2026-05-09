package net.pasterdream.event;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.pasterdream.PasterdreamMod;
import org.apache.commons.lang3.StringUtils;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(modid = PasterdreamMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BluePrintLoader {
    private static final Map<ResourceLocation,BluePrint> BLUE_PRINT_MAP = new HashMap<>();
    public static BluePrint get(ResourceLocation resourceLocation)
    {
        return BLUE_PRINT_MAP.get(resourceLocation);
    }
    @SubscribeEvent
    public static void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(new SimplePreparableReloadListener<Map<ResourceLocation,BluePrint>>() {
            @Override
            protected Map<ResourceLocation,BluePrint> prepare(ResourceManager manager, ProfilerFiller profiler) {
                // 异步加载 JSON 文件（在后台线程执行）
                return loadConfig(manager);
            }

            @Override
            protected void apply(Map<ResourceLocation,BluePrint> config, ResourceManager manager, ProfilerFiller profiler) {
                // 在主线程应用配置
                applyConfig(config);
            }
        });
    }
    private static Map<ResourceLocation,BluePrint> loadConfig(ResourceManager manager) {
        // 从 data/pasterdream/blueprints/ 读取 JSON
        Map<ResourceLocation,BluePrint> res = new HashMap<>();
        Map<ResourceLocation, Resource> resources = manager.listResources("blueprints", location -> location.getPath().endsWith(".json"));
        for (Map.Entry<ResourceLocation, Resource> entry : resources.entrySet()) {
            try(InputStreamReader reader = new InputStreamReader(entry.getValue().open())) {
                List<JsonElement> jsonlist = JsonParser.parseReader(reader).getAsJsonArray().asList();
                BluePrint newbluePrint = new BluePrint();
                for(JsonElement element : jsonlist)
                {
                    List<Item> ItemList = new ArrayList<>();
                    JsonObject object = element.getAsJsonObject();
                    for(int i = 0; i < 25; ++i)
                    {
                        JsonElement jsonitem = object.get(String.valueOf(i));
                        if(jsonitem != null)
                        {
                            String itemResourceLocation = jsonitem.getAsString();
                            Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemResourceLocation));
                            ItemList.add(item!=null?item: Items.AIR);
                        }
                        else {
                            ItemList.add(Items.AIR);
                        }
                    }
                    newbluePrint.blue_print_page.add(ItemList);
                }
                res.put(new ResourceLocation(entry.getKey().getNamespace(),StringUtils.substringBeforeLast(StringUtils.substringAfter(entry.getKey().getPath(),"blueprints/"),".json"))
                        ,newbluePrint);
            } catch (Exception e) {
                PasterdreamMod.LOGGER.warn("Failed to parse {}", entry.getKey(), e);
            }
        }
        return res; // 默认空配置
    }

    private static void applyConfig(Map<ResourceLocation,BluePrint> config) {
        // 将配置应用到模组逻辑
        BLUE_PRINT_MAP.putAll(config);
    }
    public static class BluePrint{
        List<List<Item>>blue_print_page = new ArrayList<>();
        public List<Item>get(int index)
        {
            return List.copyOf(blue_print_page.get(index));
        }
        public int getMaxPage()
        {
            return blue_print_page.size();
        }
    }
}
