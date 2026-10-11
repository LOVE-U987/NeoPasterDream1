package com.pasterdream.pasterdreammod.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.registry.PDMenusBlueprint;
import com.pasterdream.pasterdreammod.registry.items.PDItemsBlueprint;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackInputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import com.pasterdream.pasterdreammod.api.util.PDDebugLogger;
/**
 * 蓝图 JSON 数据加载器（还原原版 Fix {@code BluePrintLoader}）。
 * <p>
 * 从 {@code data/&lt;namespace&gt;/blueprints/*.json} 读取「逐层 5×5」结构页：
 * 每个 JSON 为数组，元素为页对象，键 {@code "0"}..{@code "24"} 为格子物品 ID。
 * 重载监听通过 {@link AddReloadListenerEvent} 挂到游戏总线（双端均加载）。
 */
@EventBusSubscriber(modid = PasterDreamMod.MOD_ID)
public final class BluePrintLoader {

    /** 每页固定 5×5 = 25 格（原版常量） */
    public static final int PAGE_SIZE = 25;

    private static final Map<ResourceLocation, BluePrint> BLUE_PRINT_MAP = new HashMap<>();

    static {
        // @EventBusSubscriber 类在模组构造期被扫描加载；借此强制初始化独立注册类，
        // 避免改动共享的 PasterDreamMod / PDMenus / PDItems。
        Object menus = PDMenusBlueprint.BLUEPRINT_GUI_0;
        Object items = PDItemsBlueprint.BLUEPRINT_0;
        if (menus == null || items == null) {
            PasterDreamMod.LOGGER.warn("[BluePrintLoader] blueprint menu/item holders unexpectedly null during class init");
        }
    }

    private BluePrintLoader() {
    }

    public static void bootstrap() {
        Object menus = PDMenusBlueprint.BLUEPRINT_GUI_0;
        PDItemsBlueprint.bootstrap();
    }

    /**
     * 按 ID 查询已加载蓝图
     *
     * @param id 蓝图 ID（如 {@code pasterdream:weapon_workshop}）
     * @return 蓝图；未加载时返回 {@code null}
     */
    public static BluePrint get(ResourceLocation id) {
        return BLUE_PRINT_MAP.get(id);
    }

    /**
     * 当前已加载蓝图的只读视图
     *
     * @return id → 蓝图
     */
    public static Map<ResourceLocation, BluePrint> getAll() {
        return Collections.unmodifiableMap(BLUE_PRINT_MAP);
    }

    /**
     * 注册数据重载监听器
     *
     * @param event 重载监听器事件
     */
    @SubscribeEvent
    public static void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(new SimplePreparableReloadListener<Map<ResourceLocation, BluePrint>>() {
            @Override
            protected Map<ResourceLocation, BluePrint> prepare(ResourceManager manager, ProfilerFiller profiler) {
                return loadConfig(manager);
            }

            @Override
            protected void apply(Map<ResourceLocation, BluePrint> config, ResourceManager manager, ProfilerFiller profiler) {
                applyConfig(config);
            }
        });
        PDDebugLogger.mainDebug("[BluePrintLoader] reload listener registered");
    }

    /**
     * 从资源管理器加载全部蓝图（prepare 阶段，后台线程）
     *
     * @param manager 资源管理器
     * @return 解析结果
     */
    public static Map<ResourceLocation, BluePrint> loadConfig(ResourceManager manager) {
        Map<ResourceLocation, BluePrint> res = new HashMap<>();
        Map<ResourceLocation, Resource> resources = manager.listResources(
                "blueprints", location -> location.getPath().endsWith(".json"));
        for (Map.Entry<ResourceLocation, Resource> entry : resources.entrySet()) {
            try (Reader reader = openReaderStrippingBom(entry.getValue())) {
                BluePrint blueprint = parseBluePrint(reader, BluePrintLoader::resolveItem);
                ResourceLocation id = toBlueprintId(entry.getKey());
                res.put(id, blueprint);
            } catch (Exception e) {
                PasterDreamMod.LOGGER.warn("Failed to parse blueprint {}", entry.getKey(), e);
            }
        }
        return res;
    }

    /**
     * 将加载结果应用到运行时表（主线程）
     * <p>
     * 相对原版 {@code putAll} 的改进：先清空再写入，避免资源包移除后陈旧条目残留。
     *
     * @param config 新配置
     */
    public static void applyConfig(Map<ResourceLocation, BluePrint> config) {
        BLUE_PRINT_MAP.clear();
        BLUE_PRINT_MAP.putAll(config);
        PDDebugLogger.mainInfo("[BluePrintLoader] loaded {} blueprint(s)", BLUE_PRINT_MAP.size());
    }

    /**
     * 资源路径 → 蓝图 ID：{@code namespace:blueprints/foo.json} → {@code namespace:foo}
     *
     * @param resourcePath listResources 返回的资源位置
     * @return 蓝图 ID
     */
    public static ResourceLocation toBlueprintId(ResourceLocation resourcePath) {
        String withoutExt = BlueprintJsonParser.blueprintPathFromResourcePath(resourcePath.getPath());
        return ResourceLocation.fromNamespaceAndPath(resourcePath.getNamespace(), withoutExt);
    }

    /**
     * 解析蓝图 JSON 数组（可注入物品解析器，便于单元测试）
     *
     * @param reader       JSON 读入
     * @param itemResolver 物品 ID → Item
     * @return 蓝图数据
     */
    public static BluePrint parseBluePrint(Reader reader, Function<String, Item> itemResolver) {
        JsonArray array = JsonParser.parseReader(reader).getAsJsonArray();
        BluePrint blueprint = new BluePrint();
        for (JsonElement element : array) {
            List<Item> itemList = new ArrayList<>(PAGE_SIZE);
            JsonObject object = element.getAsJsonObject();
            for (int i = 0; i < PAGE_SIZE; i++) {
                JsonElement jsonItem = object.get(String.valueOf(i));
                if (jsonItem != null && jsonItem.isJsonPrimitive()) {
                    itemList.add(itemResolver.apply(jsonItem.getAsString()));
                } else {
                    itemList.add(Items.AIR);
                }
            }
            blueprint.pages.add(itemList);
        }
        return blueprint;
    }

    /**
     * 仅解析结构（不解析物品注册表）：返回每页 slot→itemId
     * <p>
     * 供自动测试断言 JSON 形状与页数，无需 Minecraft 注册表。
     *
     * @param reader JSON 读入
     * @return 每页的格子映射（缺省格不出现）
     */
    public static List<Map<Integer, String>> parseRawPages(Reader reader) {
        return BlueprintJsonParser.parseRawPages(reader);
    }

    private static Item resolveItem(String itemId) {
        try {
            ResourceLocation loc = ResourceLocation.parse(itemId);
            Optional<Item> item = BuiltInRegistries.ITEM.getOptional(loc);
            return item.orElse(Items.AIR);
        } catch (Exception e) {
            return Items.AIR;
        }
    }

    /**
     * 打开资源流并剥离 UTF-8 BOM（若存在）
     *
     * @param resource 数据包资源
     * @return Reader
     * @throws IOException 打开失败
     */
    static Reader openReaderStrippingBom(Resource resource) throws IOException {
        InputStream raw = resource.open();
        PushbackInputStream pushback = new PushbackInputStream(raw, 3);
        byte[] bom = new byte[3];
        int n = pushback.read(bom);
        boolean hasBom = n == 3
                && (bom[0] & 0xFF) == 0xEF
                && (bom[1] & 0xFF) == 0xBB
                && (bom[2] & 0xFF) == 0xBF;
        if (!hasBom && n > 0) {
            pushback.unread(bom, 0, n);
        }
        return new BufferedReader(new InputStreamReader(pushback, StandardCharsets.UTF_8));
    }

    /**
     * 单份蓝图：多层（多页）5×5 物品网格
     */
    public static final class BluePrint {
        private final List<List<Item>> pages = new ArrayList<>();

        /**
         * 取某一页的 25 格物品只读副本
         *
         * @param index 0-based 页码
         * @return 长度 25 的物品列表
         */
        public List<Item> get(int index) {
            return List.copyOf(pages.get(index));
        }

        /**
         * 总页数
         *
         * @return 页数
         */
        public int getMaxPage() {
            return pages.size();
        }

        /**
         * 是否无页
         *
         * @return true 表示空蓝图
         */
        public boolean isEmpty() {
            return pages.isEmpty();
        }
    }
}
