package com.pasterdream.pasterdreammod.api.dimension;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;

/**
 * PasterDream 共享维度定义与判断工具。
 * <p>
 * 位于 PasterDreamAPI，供主模组及 San/融梦/法术等附属模组共享。
 * 维度实例仍由 {@link DimensionAPI} 管理，此处仅暴露判断方法与 ResourceKey，
 * 避免附属模组反向依赖主模组的 PDDimensions。
 *
 * @author PasterDream
 */
public final class APIDimensions {

    /** 染梦维度 API 结果 */
    public static final DimensionResult DYEDREAM_WORLD;

    /** 染梦世界 Level Key */
    public static final ResourceKey<Level> DYEDREAM_WORLD_LEVEL_KEY;

    /** 染梦世界 DimensionType Key */
    public static final ResourceKey<DimensionType> DYEDREAM_WORLD_TYPE_KEY;

    /** 亚伦柯斯竞技场维度 API 结果 */
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
                .generateJson(false)
                .build();

        DYEDREAM_WORLD_LEVEL_KEY = DYEDREAM_WORLD.levelKey();
        DYEDREAM_WORLD_TYPE_KEY = DYEDREAM_WORLD.typeKey();

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
    }

    private APIDimensions() {
        throw new UnsupportedOperationException("APIDimensions 是常量类，不可实例化");
    }

    /**
     * 判断当前维度是否为染梦维度。
     *
     * @param level 目标维度
     * @return 如果是染梦维度返回 true
     */
    public static boolean isDyedreamWorld(Level level) {
        return DYEDREAM_WORLD.isDimension(level);
    }

    /**
     * 判断当前维度是否为亚伦柯斯竞技场维度。
     *
     * @param level 目标维度
     * @return 如果是竞技场维度返回 true
     */
    public static boolean isAaroncosArenaWorld(Level level) {
        return AARONCOS_ARENA_WORLD.isDimension(level);
    }

    /**
     * 判断当前维度是否为主世界。
     *
     * @param level 目标维度
     * @return 如果是主世界返回 true
     */
    public static boolean isOverworld(Level level) {
        return level.dimension().equals(Level.OVERWORLD);
    }

    /**
     * 判断当前维度是否为影灯世界（暗影维度）。
     *
     * @param level 目标维度
     * @return 如果是影灯世界返回 true
     */
    public static boolean isLampShadowWorld(Level level) {
        return LAMP_SHADOW_WORLD.isDimension(level);
    }

    /**
     * 判断当前维度是否为风之旅途维度。
     *
     * @param level 目标维度
     * @return 如果是风之旅途维度返回 true
     */
    public static boolean isWindJourneyWorld(Level level) {
        return WIND_JOURNEY_WORLD.isDimension(level);
    }
}
