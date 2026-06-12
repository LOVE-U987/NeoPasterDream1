package com.pasterdream.pasterdreammod.api.dimension;

import com.pasterdream.pasterdreammod.api.dimension.builder.DimensionBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;

/**
 * 维度注册结果 —— 包含维度相关的所有 ResourceKey 引用
 * <p>
 * 由 {@link DimensionBuilder#build()} 返回，
 * 持有维度类型和维度实例的 ResourceKey，
 * 以及特殊效果 ID，方便在客户端注册自定义天空/雾气渲染。
 * <p>
 * 使用示例：
 * <pre>{@code
 * DimensionResult result = DimensionAPI.createDimension("dyedream_world")
 *     .natural().hasSkylight().bedWorks()
 *     .build();
 *
 * // 获取维度类型 Key
 * ResourceKey<DimensionType> typeKey = result.typeKey();
 *
 * // 获取维度实例 Key（可配合 TeleportCommand 使用）
 * ResourceKey<Level> levelKey = result.levelKey();
 * }</pre>
 *
 * @param dimensionName  维度注册名称
 * @param dimensionTypeId 维度类型完整 ID（如 "pasterdream:dyedream_world"）
 */
public record DimensionResult(
        String dimensionName,
        String dimensionTypeId
) {

    /**
     * 获取维度类型 ResourceKey
     * <p>
     * 用于引用 {@code data/{modId}/dimension_type/{name}.json} 中定义的维度类型。
     *
     * @return 维度类型 ResourceKey
     */
    public ResourceKey<DimensionType> typeKey() {
        return ResourceKey.create(
                Registries.DIMENSION_TYPE,
                ResourceLocation.parse(dimensionTypeId)
        );
    }

    /**
     * 获取维度实例 ResourceKey
     * <p>
     * 用于引用 {@code data/{modId}/dimension/{name}.json} 中定义的维度实例。
     * 可配合 {@link net.minecraft.server.commands.TeleportCommand} 或
     * {@link net.minecraft.world.entity.Entity#changeDimension} 使用。
     *
     * @return 维度实例 ResourceKey
     */
    public ResourceKey<Level> levelKey() {
        return ResourceKey.create(
                Registries.DIMENSION,
                ResourceLocation.parse(dimensionTypeId)
        );
    }

    /**
     * 获取特殊效果 ID
     * <p>
     * 对应 dimension_type JSON 中的 {@code "effects"} 字段，
     * 用于在客户端注册 {@link net.minecraft.client.renderer.DimensionSpecialEffects}。
     *
     * @return 特殊效果 ID（如 "pasterdream:dyedream_world"）
     */
    public String effectsId() {
        return dimensionTypeId;
    }

    /**
     * 判断目标维度是否与此结果代表的维度相同
     *
     * @param level 目标维度
     * @return 如果是同一维度返回 true
     */
    public boolean isDimension(Level level) {
        return level.dimension().equals(levelKey());
    }
}