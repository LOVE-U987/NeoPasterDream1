package com.pasterdream.pasterdreammod.worldgen;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.api.util.StructureLocator;
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
 * <p>
 * 实现委托 API {@link StructureLocator}；tag / 主世界约束为本内容。
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

    public static Optional<BlockPos> locate(ServerLevel level, BlockPos origin, int radius) {
        return StructureLocator.findNearestInDimension(
                level, Level.OVERWORLD, TWILIGHT_LANTERN_LOCATED, origin, radius, false);
    }
}
