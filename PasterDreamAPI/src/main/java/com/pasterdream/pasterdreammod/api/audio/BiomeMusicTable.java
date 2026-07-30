package com.pasterdream.pasterdreammod.api.audio;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.HashSet;
import java.util.LinkedHashMap;
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

    /** 群系 ID → 音乐名称映射 */
    private final Map<ResourceLocation, String> biomeMusicMap = new LinkedHashMap<>();

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
     *
     * @param biomeId   群系 path（相对于 defaultNamespace）
     * @param musicName 音乐注册名称（如 "dream_meadow"）
     */
    public void registerBiomeMusic(String biomeId, String musicName) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(defaultNamespace, biomeId);
        biomeMusicMap.put(id, musicName);
    }

    /**
     * 注册群系音乐映射（完整 ResourceLocation）。
     */
    public void registerBiomeMusic(ResourceLocation biomeId, String musicName) {
        biomeMusicMap.put(biomeId, musicName);
    }

    /**
     * 注册启用 BGM 框架的自定义维度。
     */
    public void registerCustomDimension(ResourceLocation dimensionId) {
        customDimensions.add(dimensionId);
    }

    /**
     * 获取群系对应的音乐名称；无映射时返回 null。
     */
    public String getMusicForBiome(ResourceLocation biomeId) {
        return biomeMusicMap.get(biomeId);
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
