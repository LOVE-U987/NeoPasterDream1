package com.pasterdream.pasterdreammod.client.sky;

import com.google.gson.JsonObject;
import com.pasterdream.pasterdreammod.api.client.sky.SkyboxEntry;
import com.pasterdream.pasterdreammod.api.client.sky.SkyboxRegistry;
import com.pasterdream.pasterdreammod.client.sky.data.SkyboxDataReloadListener;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

/**
 * 天空盒预设加载器 —— 将 {@code SkyboxPresets} 生成的预设 JSON 注册为天空内容
 * <p>
 * 预设与数据包 JSON 共用同一解析器（{@link SkyboxDataReloadListener#parseEntries}），
 * 因此预设条目同样支持条件、淡入淡出、候选切换等全部机制。
 * <p>
 * 供需要程序化注册天空内容的场景使用（如附属模组为自身维度配天空）。
 */
public final class SkyboxPresetLoader {

    private SkyboxPresetLoader() {
    }

    /**
     * 注册一个预设天空盒（组装为完整 skybox，含 biomes/dimensions）
     *
     * @param id     条目基资源路径（如 pasterdream:presets/galaxy_warm）
     * @param skybox 由 {@code SkyboxPresets#asSkybox} 生成的完整天空盒 JSON
     */
    public static void register(ResourceLocation id, JsonObject skybox) {
        List<SkyboxEntry> entries = SkyboxDataReloadListener.parseEntries(id, skybox);
        for (SkyboxEntry entry : entries) {
            SkyboxRegistry.register(entry.content(), entry.condition(), entry.fadeSpeed());
        }
    }

    /**
     * 仅注册内容层（不含 biomes/dimensions 条件的纯层数组）
     * <p>
     * 用于代码内直接通过 {@code SkyboxAPI.register} 配合 {@code SkyCondition} 的进阶场景。
     *
     * @param id     条目基资源路径
     * @param layers 由组合预设返回的 layers JsonArray
     */
    public static void registerLayers(ResourceLocation id, com.google.gson.JsonArray layers) {
        JsonObject skybox = new JsonObject();
        skybox.add("layers", layers);
        List<SkyboxEntry> entries = SkyboxDataReloadListener.parseEntries(id, skybox);
        for (SkyboxEntry entry : entries) {
            SkyboxRegistry.register(entry.content(), entry.condition(), entry.fadeSpeed());
        }
    }
}
