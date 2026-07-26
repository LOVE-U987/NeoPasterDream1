package com.pasterdream.pasterdreammod.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 蓝图 JSON 纯解析工具（无 Minecraft 依赖，可供单元/CI 断言）。
 * <p>
 * 格式与原版一致：根为数组，元素为页对象，键 {@code "0"}..{@code "24"} → 物品 ID 字符串。
 */
public final class BlueprintJsonParser {

    /** 每页格子数 */
    public static final int PAGE_SIZE = 25;

    private BlueprintJsonParser() {
    }

    /**
     * 解析原始页数据
     *
     * @param reader JSON reader
     * @return 每页 slot→itemId（缺省格不写入）
     */
    public static List<Map<Integer, String>> parseRawPages(Reader reader) {
        JsonArray array = JsonParser.parseReader(reader).getAsJsonArray();
        List<Map<Integer, String>> pages = new ArrayList<>(array.size());
        for (JsonElement element : array) {
            JsonObject object = element.getAsJsonObject();
            Map<Integer, String> page = new HashMap<>();
            for (int i = 0; i < PAGE_SIZE; i++) {
                JsonElement jsonItem = object.get(String.valueOf(i));
                if (jsonItem != null && jsonItem.isJsonPrimitive()) {
                    page.put(i, jsonItem.getAsString());
                }
            }
            pages.add(page);
        }
        return pages;
    }

    /**
     * 资源路径 → 蓝图 path（不含 namespace）
     * <p>
     * {@code blueprints/foo/bar.json} → {@code foo/bar}
     *
     * @param resourcePath listResources 的 path 部分
     * @return 蓝图 path
     */
    public static String blueprintPathFromResourcePath(String resourcePath) {
        Objects.requireNonNull(resourcePath, "resourcePath");
        String relative = resourcePath;
        int idx = relative.indexOf("blueprints/");
        if (idx >= 0) {
            relative = relative.substring(idx + "blueprints/".length());
        }
        if (relative.endsWith(".json")) {
            relative = relative.substring(0, relative.length() - 5);
        }
        return relative;
    }
}
