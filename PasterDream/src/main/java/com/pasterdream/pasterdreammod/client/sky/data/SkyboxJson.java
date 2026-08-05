package com.pasterdream.pasterdreammod.client.sky.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.pasterdream.pasterdreammod.client.sky.math.SkyColor;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

/**
 * 天空盒 JSON 解析工具 —— 容错的字段读取与类型转换
 */
final class SkyboxJson {

    private SkyboxJson() {
    }

    /**
     * 读取浮点字段（缺省返回 fallback）
     *
     * @param json     JSON 对象
     * @param key      字段名
     * @param fallback 缺省值
     * @return 值
     */
    static float getFloat(JsonObject json, String key, float fallback) {
        return json.has(key) ? json.get(key).getAsFloat() : fallback;
    }

    /**
     * 读取整数字段（缺省返回 fallback）
     *
     * @param json     JSON 对象
     * @param key      字段名
     * @param fallback 缺省值
     * @return 值
     */
    static int getInt(JsonObject json, String key, int fallback) {
        return json.has(key) ? json.get(key).getAsInt() : fallback;
    }

    /**
     * 读取长整数字段（缺省返回 fallback）
     *
     * @param json     JSON 对象
     * @param key      字段名
     * @param fallback 缺省值
     * @return 值
     */
    static long getLong(JsonObject json, String key, long fallback) {
        return json.has(key) ? json.get(key).getAsLong() : fallback;
    }

    /**
     * 读取字符串字段（缺省返回 fallback）
     *
     * @param json     JSON 对象
     * @param key      字段名
     * @param fallback 缺省值
     * @return 值
     */
    static String getString(JsonObject json, String key, String fallback) {
        return json.has(key) ? json.get(key).getAsString() : fallback;
    }

    /**
     * 读取必填字符串字段（缺失抛异常）
     *
     * @param json JSON 对象
     * @param key  字段名
     * @return 值
     */
    static String requiredString(JsonObject json, String key) {
        if (!json.has(key)) {
            throw new JsonParseException("Missing required property: " + key);
        }
        return json.get(key).getAsString();
    }

    /**
     * 解析资源路径
     *
     * @param value 字符串
     * @return 资源路径
     */
    static ResourceLocation resourceLocation(String value) {
        ResourceLocation id = ResourceLocation.tryParse(value);
        if (id == null) {
            throw new JsonParseException("Invalid resource location: " + value);
        }
        return id;
    }

    /**
     * 读取资源路径列表（缺省返回空列表）
     *
     * @param json JSON 对象
     * @param key  字段名
     * @return 资源路径列表
     */
    static List<ResourceLocation> resourceLocationList(JsonObject json, String key) {
        if (!json.has(key)) {
            return List.of();
        }
        List<ResourceLocation> values = new ArrayList<>();
        for (JsonElement element : json.getAsJsonArray(key)) {
            values.add(resourceLocation(element.getAsString()));
        }
        return values;
    }

    /**
     * 读取必填资源路径列表（缺失抛异常）
     *
     * @param json JSON 对象
     * @param key  字段名
     * @return 资源路径列表
     */
    static List<ResourceLocation> requiredResourceLocationList(JsonObject json, String key) {
        if (!json.has(key)) {
            throw new JsonParseException("Missing required property: " + key);
        }
        return resourceLocationList(json, key);
    }

    /**
     * 读取 RGB 颜色（缺省返回 fallback）
     *
     * @param json     JSON 对象
     * @param key      字段名
     * @param fallback 缺省色
     * @return 颜色
     */
    static SkyColor color(JsonObject json, String key, SkyColor fallback) {
        if (!json.has(key)) {
            return fallback;
        }
        JsonArray array = json.getAsJsonArray(key);
        return new SkyColor(array.get(0).getAsFloat(), array.get(1).getAsFloat(), array.get(2).getAsFloat());
    }

    /**
     * 读取颜色列表（缺省返回 fallback）
     *
     * @param json     JSON 对象
     * @param fallback 缺省列表
     * @return 颜色列表
     */
    static List<SkyColor> colors(JsonObject json, List<SkyColor> fallback) {
        if (!json.has("colors")) {
            return fallback;
        }
        List<SkyColor> colors = new ArrayList<>();
        for (JsonElement element : json.getAsJsonArray("colors")) {
            JsonArray array = element.getAsJsonArray();
            colors.add(new SkyColor(array.get(0).getAsFloat(), array.get(1).getAsFloat(), array.get(2).getAsFloat()));
        }
        return colors.isEmpty() ? fallback : colors;
    }
}
