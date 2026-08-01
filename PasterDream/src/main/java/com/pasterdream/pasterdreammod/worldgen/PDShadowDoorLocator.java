package com.pasterdream.pasterdreammod.worldgen;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.api.util.StructureLocator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.Optional;

/**
 * 暮影据点（{@code pasterdream:shadow_world_door}）唯一读坐标入口。
 * <p>
 * 实现委托 API {@link StructureLocator}；tag 为本内容。据点生成在主世界，
 * 支持跨维度定位：玩家在主世界时以自身位置为锚点，在其他维度（如染梦）
 * 以主世界出生点为锚点，返回真实主世界坐标。
 */
public final class PDShadowDoorLocator {

    public static final TagKey<Structure> TWILIGHT_LANTERN_LOCATED = TagKey.create(
            Registries.STRUCTURE,
            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "twilight_lantern_located"));

    public static final int DEFAULT_RADIUS = StructureLocator.DEFAULT_RADIUS;

    private PDShadowDoorLocator() {
    }

    public static Optional<BlockPos> locate(ServerLevel level, BlockPos origin) {
        return locate(level, origin, DEFAULT_RADIUS);
    }

    /**
     * 跨维度定位最近的主世界暮影据点。
     *
     * @param level  玩家当前所在服务端维度（任意）
     * @param origin 玩家当前位置（仅在主世界时作为搜索锚点）
     * @param radius 搜索半径（chunk）
     * @return 主世界坐标系中的据点位置；无则 empty
     */
    public static Optional<BlockPos> locate(ServerLevel level, BlockPos origin, int radius) {
        if (level == null || origin == null) {
            return Optional.empty();
        }
        MinecraftServer server = level.getServer();
        ServerLevel overworld = server.overworld();
        if (overworld == null) {
            return Optional.empty();
        }
        // 玩家已在主世界 → 以自身位置为锚点；跨维度（染梦等）→ 以主世界出生点为锚点
        BlockPos searchOrigin = level.dimension() == Level.OVERWORLD
                ? origin
                : overworld.getSharedSpawnPos();
        return StructureLocator.findNearest(overworld, TWILIGHT_LANTERN_LOCATED, searchOrigin, radius);
    }
}
