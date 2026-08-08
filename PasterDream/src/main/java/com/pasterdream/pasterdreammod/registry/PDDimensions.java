package com.pasterdream.pasterdreammod.registry;

import com.pasterdream.pasterdreammod.api.dimension.DimensionAPI;
import com.pasterdream.pasterdreammod.api.dimension.DimensionResult;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;

/**
 * 维度注册类 —— 定义已注册维度的 ResourceKey 引用
 * <p>
 * 维度类型和维度实例由 {@link DimensionAPI} 统一管理，
 * 此处提供便捷的静态引用和判断方法。
 * <p>
 * 维度特殊效果（天空/雾气渲染）的注册在
 * {@link com.pasterdream.pasterdreammod.client.ClientSetup} 中完成。
 */
public class PDDimensions {

    /** 染梦维度 API 结果（包含所有 ResourceKey 引用） */
    public static final DimensionResult DYEDREAM_WORLD;

    /** 染梦世界 Level Key（向后兼容） */
    public static final ResourceKey<Level> DYEDREAM_WORLD_LEVEL_KEY;

    /** 染梦世界 DimensionType Key（向后兼容） */
    public static final ResourceKey<DimensionType> DYEDREAM_WORLD_TYPE_KEY;

    /** 亚伦柯斯竞技场维度 API 结果（包含所有 ResourceKey 引用） */
    public static final DimensionResult AARONCOS_ARENA_WORLD;

    /** 亚伦柯斯竞技场 Level Key */
    public static final ResourceKey<Level> AARONCOS_ARENA_WORLD_LEVEL_KEY;

    /** 亚伦柯斯竞技场 DimensionType Key */
    public static final ResourceKey<DimensionType> AARONCOS_ARENA_WORLD_TYPE_KEY;

    /** 影灯世界（暗影维度）API 结果 */
    public static final DimensionResult LAMP_SHADOW_WORLD;

    /** 影灯世界 Level Key */
    public static final ResourceKey<Level> LAMP_SHADOW_WORLD_LEVEL_KEY;

    /** 影灯世界 DimensionType Key */
    public static final ResourceKey<DimensionType> LAMP_SHADOW_WORLD_TYPE_KEY;

    /** 风之旅途维度 API 结果 */
    public static final DimensionResult WIND_JOURNEY_WORLD;

    /** 风之旅途 Level Key */
    public static final ResourceKey<Level> WIND_JOURNEY_WORLD_LEVEL_KEY;

    /** 风之旅途 DimensionType Key */
    public static final ResourceKey<DimensionType> WIND_JOURNEY_WORLD_TYPE_KEY;

    /** 冷域维度 API 结果 */
    public static final DimensionResult COLD_DOMAIN_WORLD;

    /** 冷域 Level Key */
    public static final ResourceKey<Level> COLD_DOMAIN_WORLD_LEVEL_KEY;

    /** 冷域 DimensionType Key */
    public static final ResourceKey<DimensionType> COLD_DOMAIN_WORLD_TYPE_KEY;

    static {
        DYEDREAM_WORLD = DimensionAPI.createDimension("dyedream_world")
                .natural()
                .hasSkylight()
                .bedWorks()
                .hasRaids(true)
                .withAmbientLight(0.5)
                .minY(-64).height(384)
                .monsterSpawnLight(0, 7)
                .withDefaultBlock("pasterdream:dyedream_block")
                .withDefaultFluid("minecraft:water")
                .withNoiseSettings("pasterdream:dyedream_world")
                // 注意：此处不重新生成 JSON 文件（已存在手动编写的 JSON）
                .generateJson(false)
                .build();

        DYEDREAM_WORLD_LEVEL_KEY = DYEDREAM_WORLD.levelKey();
        DYEDREAM_WORLD_TYPE_KEY = DYEDREAM_WORLD.typeKey();

        // 亚伦柯斯竞技场：无天空光照、非自然、无昼夜、高度 0-128
        // 使用 .generateJson(false) 因为 JSON 文件需要手动配置复杂噪声路由和表面规则
        AARONCOS_ARENA_WORLD = DimensionAPI.createDimension("aaroncos_arena_world")
                .natural(false)
                .hasSkylight(false)
                .bedWorks(false)
                .hasRaids(false)
                .piglinSafe(true)
                .withAmbientLight(0.5)
                .minY(0).height(128)
                .monsterSpawnLight(0, 7)
                .withDefaultBlock("minecraft:air")
                .withDefaultFluid("minecraft:air")
                .generateJson(false)
                .build();

        AARONCOS_ARENA_WORLD_LEVEL_KEY = AARONCOS_ARENA_WORLD.levelKey();
        AARONCOS_ARENA_WORLD_TYPE_KEY = AARONCOS_ARENA_WORLD.typeKey();

        // 影灯世界（暗影维度）：无天空光照、固定时间的暗世界，参数与原版 dimension_type 一致
        // JSON（dimension/dimension_type/noise 内联设置）已从原版迁移，不重新生成
        LAMP_SHADOW_WORLD = DimensionAPI.createDimension("lamp_shadow_world")
                .natural(false)
                .hasSkylight(false)
                .bedWorks(false)
                .hasRaids(false)
                .piglinSafe(true)
                .withAmbientLight(0.125)
                .minY(-64).height(384)
                .monsterSpawnLight(0, 7)
                .withDefaultBlock("pasterdream:shadow_stone")
                .withDefaultFluid("minecraft:water")
                .generateJson(false)
                .build();

        LAMP_SHADOW_WORLD_LEVEL_KEY = LAMP_SHADOW_WORLD.levelKey();
        LAMP_SHADOW_WORLD_TYPE_KEY = LAMP_SHADOW_WORLD.typeKey();

        // 风之旅途维度：高空云海世界（0~256），参数与原版 dimension_type 一致
        WIND_JOURNEY_WORLD = DimensionAPI.createDimension("wind_journey_world")
                .natural(false)
                .hasSkylight()
                .bedWorks()
                .hasRaids(false)
                .piglinSafe(true)
                .withAmbientLight(0)
                .minY(0).height(256)
                .monsterSpawnLight(0, 7)
                .withDefaultBlock("pasterdream:thick_cloud")
                .withDefaultFluid("minecraft:water")
                .generateJson(false)
                .build();

        WIND_JOURNEY_WORLD_LEVEL_KEY = WIND_JOURNEY_WORLD.levelKey();
        WIND_JOURNEY_WORLD_TYPE_KEY = WIND_JOURNEY_WORLD.typeKey();

        // 冷域维度：寒冷冰雪世界（固定冷域群系，地表由雪地草坪/冷域泥土构成）
        COLD_DOMAIN_WORLD = DimensionAPI.createDimension("cold_domain_world")
                .natural()
                .hasSkylight()
                .bedWorks()
                .hasRaids(false)
                .withAmbientLight(0.35)
                .minY(-64).height(384)
                .monsterSpawnLight(0, 7)
                .withDefaultBlock("minecraft:stone")
                .withDefaultFluid("minecraft:water")
                .withNoiseSettings("pasterdream:cold_domain_world")
                // JSON 已手动编写（固定群系 + 冷域地表 surface_rule）
                .generateJson(false)
                .build();

        COLD_DOMAIN_WORLD_LEVEL_KEY = COLD_DOMAIN_WORLD.levelKey();
        COLD_DOMAIN_WORLD_TYPE_KEY = COLD_DOMAIN_WORLD.typeKey();
    }

    /**
     * 判断当前维度是否为冷域维度
     *
     * @param level 目标维度
     * @return 如果是冷域维度返回 true
     */
    public static boolean isColdDomainWorld(Level level) {
        return COLD_DOMAIN_WORLD.isDimension(level);
    }

    /**
     * 判断当前维度是否为染梦维度
     *
     * @param level 目标维度
     * @return 如果是染梦维度返回 true
     */
    public static boolean isDyedreamWorld(Level level) {
        return DYEDREAM_WORLD.isDimension(level);
    }

    /**
     * 判断当前维度是否为亚伦柯斯竞技场维度
     *
     * @param level 目标维度
     * @return 如果是竞技场维度返回 true
     */
    public static boolean isAaroncosArenaWorld(Level level) {
        return AARONCOS_ARENA_WORLD.isDimension(level);
    }

    /**
     * 判断当前维度是否为主世界
     *
     * @param level 目标维度
     * @return 如果是主世界返回 true
     */
    public static boolean isOverworld(Level level) {
        return level.dimension().equals(Level.OVERWORLD);
    }

    /**
     * 判断当前维度是否为影灯世界（暗影维度）
     *
     * @param level 目标维度
     * @return 如果是影灯世界返回 true
     */
    public static boolean isLampShadowWorld(Level level) {
        return LAMP_SHADOW_WORLD.isDimension(level);
    }

    /**
     * 判断当前维度是否为风之旅途维度
     *
     * @param level 目标维度
     * @return 如果是风之旅途维度返回 true
     */
    public static boolean isWindJourneyWorld(Level level) {
        return WIND_JOURNEY_WORLD.isDimension(level);
    }

    // ==================== 未来维度预留 ====================
    // - 暗影地牢（shadow_dungeon）：灯影深处的随机地牢
}