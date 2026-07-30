package com.pasterdream.pasterdreammod.api.util;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.Optional;

/**
 * 结构 nearest 定位工具（对齐末影之眼 {@code findNearestMapStructure} 语义）。
 * <p>
 * 不写 gamerule、不放置、无笔记副作用。维度/tag/radius 均由调用方参数化。
 */
public final class StructureLocator {

    /** 与 {@code EnderEyeItem} 默认 radius 一致 */
    public static final int DEFAULT_RADIUS = 100;

    private StructureLocator() {
    }

    /**
     * 在当前维度按 structure tag 找最近锚点。
     *
     * @param skipKnownStructures 与原版 API 同名参数（末影之眼为 false）
     */
    public static Optional<BlockPos> findNearest(
            ServerLevel level,
            TagKey<Structure> structureTag,
            BlockPos origin,
            int radius,
            boolean skipKnownStructures) {
        if (level == null || structureTag == null || origin == null) {
            return Optional.empty();
        }
        BlockPos found = level.findNearestMapStructure(structureTag, origin, radius, skipKnownStructures);
        return Optional.ofNullable(found);
    }

    public static Optional<BlockPos> findNearest(
            ServerLevel level,
            TagKey<Structure> structureTag,
            BlockPos origin,
            int radius) {
        return findNearest(level, structureTag, origin, radius, false);
    }

    public static Optional<BlockPos> findNearest(
            ServerLevel level,
            TagKey<Structure> structureTag,
            BlockPos origin) {
        return findNearest(level, structureTag, origin, DEFAULT_RADIUS, false);
    }

    /**
     * 仅当 {@code level.dimension()} 等于 {@code requiredDimension} 时搜索。
     */
    public static Optional<BlockPos> findNearestInDimension(
            ServerLevel level,
            ResourceKey<Level> requiredDimension,
            TagKey<Structure> structureTag,
            BlockPos origin,
            int radius,
            boolean skipKnownStructures) {
        if (level == null || requiredDimension == null || level.dimension() != requiredDimension) {
            return Optional.empty();
        }
        return findNearest(level, structureTag, origin, radius, skipKnownStructures);
    }

    public static Optional<BlockPos> findNearestInDimension(
            ServerLevel level,
            ResourceKey<Level> requiredDimension,
            TagKey<Structure> structureTag,
            BlockPos origin,
            int radius) {
        return findNearestInDimension(level, requiredDimension, structureTag, origin, radius, false);
    }
}
