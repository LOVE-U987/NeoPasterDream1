package com.pasterdream.pasterdreammod.client;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 资源审计日志记录器
 * 监听客户端资源重载事件（F3+T 或进入世界时），扫描并记录 pasterdream 命名空间下的所有资源。
 * 协助排查模型、纹理、动画、粒子等资源文件加载失败的问题。
 */
@EventBusSubscriber(modid = PasterDreamMod.MOD_ID, value = Dist.CLIENT)
public class PDResourceLogger {

    private static final String NS = PasterDreamMod.MOD_ID;

    /**
     * 注册客户端重载监听器
     */
    @SubscribeEvent
    public static void onRegisterReloadListeners(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(new SimplePreparableReloadListener<Void>() {
            @Override
            protected Void prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
                return null;
            }

            @Override
            protected void apply(Void data, ResourceManager resourceManager, ProfilerFiller profiler) {
                logResourceSummary(resourceManager);
            }
        });
        PasterDreamMod.LOGGER.info("[PDResourceLogger] 资源审计日志记录器已注册，将在每次资源重载后输出报告");
    }

    /**
     * 输出资源审计报告
     */
    private static void logResourceSummary(ResourceManager manager) {
        List<ResourceCategory> categories = List.of(
                new ResourceCategory("blockstates",   "assets/{ns}/blockstates",        ".json"),
                new ResourceCategory("models/block",  "assets/{ns}/models/block",      ".json"),
                new ResourceCategory("models/item",   "assets/{ns}/models/item",       ".json"),
                new ResourceCategory("textures/block","assets/{ns}/textures/block",     ".png"),
                new ResourceCategory("textures/item", "assets/{ns}/textures/item",      ".png"),
                new ResourceCategory("textures/entity","assets/{ns}/textures/entity",   ".png"),
                new ResourceCategory("textures/particle","assets/{ns}/textures/particle",".png"),
                new ResourceCategory("geo",           "assets/{ns}/geo",               ".json"),
                new ResourceCategory("animations",    "assets/{ns}/animations",         ".json"),
                new ResourceCategory("particles",     "assets/{ns}/particles",          ".json"),
                new ResourceCategory("lang",          "assets/{ns}/lang",               ".json")
        );

        String assetPrefix = "assets/" + NS + "/";

        PasterDreamMod.LOGGER.info("========== [PDResourceLogger] {} 资源审计报告 ==========", NS);

        int totalCount = 0;
        int totalWarn = 0;
        for (ResourceCategory cat : categories) {
            String resolvedPrefix = cat.pathPrefix.replace("{ns}", NS);
            String path = resolvedPrefix.substring(assetPrefix.length());
            List<ResourceLocation> found = listResources(manager, path);
            if (found.isEmpty()) {
                PasterDreamMod.LOGGER.warn("  ⚠ [{}] 未找到任何资源（路径: {}）", cat.label, resolvedPrefix);
                totalWarn++;
                continue;
            }
            PasterDreamMod.LOGGER.info("  [{}] 共 {} 个文件:", cat.label, found.size());
            for (ResourceLocation loc : found) {
                PasterDreamMod.LOGGER.debug("    - {}", loc.getPath());
            }
            totalCount += found.size();
        }

        // 额外检测：检查 sounds.json 是否存在
        Map<ResourceLocation, ?> sounds = manager.listResources("sounds",
                loc -> loc.getNamespace().equals(NS) && loc.getPath().endsWith(".ogg"));
        if (!sounds.isEmpty()) {
            PasterDreamMod.LOGGER.info("  [sounds] 共 {} 个音效文件", sounds.size());
            totalCount += sounds.size();
        }

        PasterDreamMod.LOGGER.info("----------------------------------------------------");
        PasterDreamMod.LOGGER.info("  {} 命名空间资源总计: {} 个文件 | 空目录警告: {} 个", NS, totalCount, totalWarn);
        PasterDreamMod.LOGGER.info("====================================================");
    }

    /**
     * 获取指定路径下所有 pasterdream 命名空间的资源
     *
     * @param manager  资源管理器
     * @param path     资源路径（如 models/block）
     * @return 排序后的资源位置列表
     */
    private static List<ResourceLocation> listResources(ResourceManager manager, String path) {
        return manager.listResources(path, loc -> loc.getNamespace().equals(NS))
                .keySet().stream()
                .sorted(Comparator.comparing(ResourceLocation::getPath))
                .collect(Collectors.toList());
    }

    /**
     * 资源分类记录
     */
    private record ResourceCategory(String label, String pathPrefix, String extension) {}
}
