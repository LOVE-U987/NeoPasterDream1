package com.pasterdream.pasterdreammod.api.audio;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 群系 → 音乐名 映射表 + 自定义维度白名单（BGM 框架数据层）。
 * <p>
 * 纯数据容器，无播放逻辑、无客户端依赖。
 * 主模填写具体 biome/维度 ID 与音乐名；框架 tick 只读本表。
 */
public class BiomeMusicTable {

    /** 群系 ID → 音乐名称列表映射（支持多首曲目随机播放） */
    private final Map<ResourceLocation, List<String>> biomeMusicMap = new LinkedHashMap<>();

    /** 启用 BGM 框架的自定义维度 ID 集合 */
    private final Set<ResourceLocation> customDimensions = new HashSet<>();

    /** 短 biome id 默认命名空间（如 {@code pasterdream}） */
    private final String defaultNamespace;

    /**
     * @param defaultNamespace 短 biomeId 补全时使用的命名空间
     */
    public BiomeMusicTable(String defaultNamespace) {
        this.defaultNamespace = Objects.requireNonNull(defaultNamespace, "defaultNamespace");
    }

    /**
     * 注册群系音乐映射（短 id，自动补全 defaultNamespace）。
     * <p>
     * 同一群系可多次调用，每首曲目会被追加到曲目列表中，
     * 播放时从中随机选择一首。
     *
     * @param biomeId   群系 path（相对于 defaultNamespace）
     * @param musicName 音乐注册名称（如 "dream_meadow"）
     */
    public void registerBiomeMusic(String biomeId, String musicName) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(defaultNamespace, biomeId);
        biomeMusicMap.computeIfAbsent(id, k -> new ArrayList<>()).add(musicName);
    }

    /**
     * 注册群系音乐映射（完整 ResourceLocation）。
     * <p>
     * 同一群系可多次调用，每首曲目会被追加到曲目列表中。
     */
    public void registerBiomeMusic(ResourceLocation biomeId, String musicName) {
        biomeMusicMap.computeIfAbsent(biomeId, k -> new ArrayList<>()).add(musicName);
    }

    /**
     * 注册启用 BGM 框架的自定义维度。
     */
    public void registerCustomDimension(ResourceLocation dimensionId) {
        customDimensions.add(dimensionId);
    }

    /**
     * 获取群系对应的音乐名称列表（可能有多首曲目，播放时随机选一首）；
     * 无映射时返回空列表。
     */
    public List<String> getMusicForBiome(ResourceLocation biomeId) {
        return biomeMusicMap.getOrDefault(biomeId, List.of());
    }

    /**
     * 判断当前维度是否在白名单中。
     */
    public boolean isCustomDimension(Level level) {
        return customDimensions.contains(level.dimension().location());
    }

    /**
     * 判断指定生物群系是否有音乐映射。
     */
    public boolean hasMusicForBiome(ResourceLocation biomeId) {
        return biomeMusicMap.containsKey(biomeId);
    }

    /**
     * 已注册自定义维度的只读视图。
     */
    public Set<ResourceLocation> getCustomDimensions() {
        return Set.copyOf(customDimensions);
    }
}
