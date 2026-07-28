package com.pasterdream.pasterdreammod.worldgen;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.Optional;

/**
 * 暮影据点（{@code pasterdream:shadow_world_door}）唯一读坐标入口。
 * 对齐原版末影之眼：{@code ServerLevel.findNearestMapStructure} + structure tag。
 * <p>
 * 无 place、无写 gamerule、无加载笔记副作用。
 */
public final class PDShadowDoorLocator {

    public static final TagKey<Structure> TWILIGHT_LANTERN_LOCATED = TagKey.create(
            Registries.STRUCTURE,
            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "twilight_lantern_located"));

    /** 与 {@code EnderEyeItem} 调用 findNearestMapStructure 的 radius 一致。 */
    public static final int DEFAULT_RADIUS = 100;

    private PDShadowDoorLocator() {
    }

    public static Optional<BlockPos> locate(ServerLevel level, BlockPos origin) {
        return locate(level, origin, DEFAULT_RADIUS);
    }

    /**
     * @param radius findNearestMapStructure 搜索半径（chunk 语义与原版 API 相同）
     * @return 最近据点锚点；非主世界或未找到 → empty
     */
    public static Optional<BlockPos> locate(ServerLevel level, BlockPos origin, int radius) {
        if (level == null || origin == null) {
            return Optional.empty();
        }
        if (level.dimension() != Level.OVERWORLD) {
            return Optional.empty();
        }
        BlockPos found = level.findNearestMapStructure(TWILIGHT_LANTERN_LOCATED, origin, radius, false);
        return Optional.ofNullable(found);
    }
}
