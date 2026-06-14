package com.pasterdream.pasterdreammod.client.audio;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.LinkedHashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 群系音乐映射注册表 —— 管理群系与BGM的映射关系及自定义维度注册
 * <p>
 * 职责：
 * <ul>
 *   <li>维护群系 ID → 音乐名称的映射表</li>
 *   <li>维护 ModMusicManager 生效的自定义维度集合</li>
 *   <li>查询群系对应的音乐名称</li>
 *   <li>判断当前维度是否为已注册的自定义维度</li>
 * </ul>
 * <p>
 * 此类为纯数据容器，不包含任何播放逻辑。
 */
public class BiomeMusicRegistry {

    /** 群系 ID → 音乐名称映射 */
    private final Map<ResourceLocation, String> biomeMusicMap = new LinkedHashMap<>();

    /** 自定义维度 ID 集合（在此集合中的维度启用 ModMusicManager） */
    private final Set<ResourceLocation> customDimensions = new HashSet<>();

    /**
     * 注册群系音乐映射
     *
     * @param biomeId   群系 ID（相对于模组命名空间）
     * @param musicName 音乐注册名称（如 "dream_meadow"）
     */
    public void registerBiomeMusic(String biomeId, String musicName) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, biomeId);
        biomeMusicMap.put(id, musicName);
    }

    /**
     * 注册自定义维度（启用 ModMusicManager 的维度）
     *
     * @param dimensionId 维度 ID
     */
    public void registerCustomDimension(ResourceLocation dimensionId) {
        customDimensions.add(dimensionId);
    }

    /**
     * 获取群系对应的音乐名称
     *
     * @param biomeId 群系 ID
     * @return 音乐名称，无映射时返回 null
     */
    public String getMusicForBiome(ResourceLocation biomeId) {
        return biomeMusicMap.get(biomeId);
    }

    /**
     * 判断当前维度是否为已注册的自定义维度
     *
     * @param level 当前维度
     * @return 如果是自定义维度返回 true
     */
    public boolean isCustomDimension(Level level) {
        return customDimensions.contains(level.dimension().location());
    }

    /**
     * 判断指定生物群系是否有音乐映射
     *
     * @param biomeId 群系 ID
     * @return 如果有映射返回 true
     */
    public boolean hasMusicForBiome(ResourceLocation biomeId) {
        return biomeMusicMap.containsKey(biomeId);
    }

    /**
     * 获取所有已注册的自定义维度（只读视图）
     *
     * @return 自定义维度 ID 集合的只读视图
     */
    public Set<ResourceLocation> getCustomDimensions() {
        return Set.copyOf(customDimensions);
    }
}