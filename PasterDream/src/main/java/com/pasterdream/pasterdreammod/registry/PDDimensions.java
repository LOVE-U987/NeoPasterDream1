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
                .withMusic("dyedream_world")
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

    // ==================== 未来维度预留 ====================
    // - 灯影世界（lamp_shadow_world）：更深层梦境
    // - 暗影地牢（shadow_dungeon）：灯影深处的随机地牢
    // - 风之旅维度（wind_journey_world）：天空浮岛世界
}