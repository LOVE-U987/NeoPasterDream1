package com.pasterdream.pasterdreammod.smoketest;

import com.pasterdream.pasterdreammod.registry.PDDimensions;
import com.pasterdream.pasterdreammod.worldgen.PDWindLakeBiomeModifier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * 风之旅途水色湖专项 VERIFY 套件 {@code wind-lake}。
 * <p>
 * 在非超平坦 + 开建筑的测试世界上，临时启用 {@code ground_feature_wind_journey_1}
 *（via {@link PDWindLakeBiomeModifier}），进真实风维强制 gen 并断言：
 * 不崩 + 扫描到 water + cyan_stone 湖形貌。
 * <p>
 * <b>不</b>并入默认 {@code all}；须 {@code PASTERDREAM_VERIFY_SUITES=wind-lake}。
 */
public final class PDWindLakeVerifyHooks {

    public record Result(boolean pass, String name, String detail) {
    }

    private static final ResourceLocation BIOME_0 =
            ResourceLocation.fromNamespaceAndPath("pasterdream", "wind_journey_biome_0");
    private static final ResourceLocation LAKE_PLACED =
            ResourceLocation.fromNamespaceAndPath("pasterdream", "ground_feature_wind_journey_1");
    private static final ResourceLocation CYAN_STONE_ID =
            ResourceLocation.fromNamespaceAndPath("pasterdream", "cyan_stone");

    /** 强制加载半径（chunk，含中心 → 边长 2*r+1） */
    private static final int CHUNK_RADIUS = 2;
    /** 自生成表面向下/向上扫描的 Y 带半宽 */
    private static final int SCAN_Y_PAD = 12;
    /** 水平扫描半宽（块），覆盖强制 chunk 内核 */
    private static final int SCAN_XZ = 40;

    private PDWindLakeVerifyHooks() {
    }

    /**
     * 同步：挂接校验 → TP 风维 → 落到 biome_0 → 强制 gen → 扫湖。
     * 若 LakeFeature 再次 FATAL，进程无法写完报告 → CI 失败（「不崩」判据）。
     */
    public static void verify(MinecraftServer server, ServerPlayer player, Consumer<Result> out) {
        if (server == null) {
            out.accept(new Result(false, "wind_lake.server", "server == null"));
            return;
        }
        boolean gate = PDWindLakeBiomeModifier.isVerifyLakeEnabled();
        out.accept(new Result(gate, "wind_lake.gate",
                "ENABLED=" + PDPortingVerifyTest.ENABLED
                        + " suites=" + PDPortingVerifyTest.SELECTED_SUITES
                        + " gate=" + gate));
        if (!gate) {
            out.accept(new Result(false, "wind_lake.hook_enabled",
                    "VERIFY-only lake gate off；需要 PASTERDREAM_VERIFY=1 且 SUITES 含 wind-lake"));
            return;
        }

        ServerLevel overworld = server.overworld();
        boolean structuresOn = overworld.getServer().getWorldData().worldGenOptions().generateStructures();
        out.accept(new Result(structuresOn, "wind_lake.structures_on",
                "generateStructures=" + structuresOn + "（wind-lake 建档须 true）"));

        ServerLevel wind = server.getLevel(PDDimensions.WIND_JOURNEY_WORLD_LEVEL_KEY);
        if (wind == null) {
            out.accept(new Result(false, "wind_lake.dim", "wind_journey_world getLevel == null"));
            return;
        }

        boolean featureWired = isLakeFeatureOnBiome0(wind);
        out.accept(new Result(featureWired, "wind_lake.feature_wired",
                featureWired
                        ? "biome_0 surface_structures 含 " + LAKE_PLACED
                        : "biome_0 未注入 lake placed feature（modifier 未生效）"));
        if (!featureWired) {
            return;
        }

        if (player == null) {
            out.accept(new Result(false, "wind_lake.player", "player == null"));
            return;
        }

        float yRot = player.getYRot();
        float xRot = player.getXRot();
        // 先到风维原点高空，再螺旋找 biome_0 落点
        player.teleportTo(wind, 0.5D, 160.0D, 0.5D, yRot, xRot);
        boolean inWind = PDDimensions.isWindJourneyWorld(player.level());
        out.accept(new Result(inWind, "wind_lake.teleport",
                "dim=" + player.level().dimension().location()));
        if (!inWind) {
            return;
        }

        BlockPos anchor = findBiome0Anchor(wind, new BlockPos(0, 120, 0), 48);
        if (anchor == null) {
            out.accept(new Result(false, "wind_lake.biome0",
                    "未在搜索半径内找到 wind_journey_biome_0"));
            return;
        }
        int surfaceY = wind.getHeight(Heightmap.Types.WORLD_SURFACE_WG, anchor.getX(), anchor.getZ());
        if (surfaceY <= wind.getMinBuildHeight()) {
            surfaceY = Math.max(80, anchor.getY());
        }
        double px = anchor.getX() + 0.5D;
        double pz = anchor.getZ() + 0.5D;
        double py = surfaceY + 2.0D;
        player.teleportTo(wind, px, py, pz, yRot, xRot);
        out.accept(new Result(true, "wind_lake.anchor",
                "biome0@" + anchor.getX() + "," + surfaceY + "," + anchor.getZ()));

        int forced = forceChunks(wind, player.chunkPosition(), CHUNK_RADIUS);
        out.accept(new Result(forced > 0, "wind_lake.chunks",
                "forcedChunks=" + forced + " radius=" + CHUNK_RADIUS));

        LakeScan scan = scanLakeShape(wind, BlockPos.containing(px, surfaceY, pz));
        out.accept(new Result(scan.hits() > 0, "wind_lake.feature_shape",
                "hits=" + scan.hits()
                        + " water=" + scan.water()
                        + " cyanNearWater=" + scan.cyanNearWater()
                        + " sample=" + scan.sample()));
    }

    private static boolean isLakeFeatureOnBiome0(ServerLevel wind) {
        Optional<Holder.Reference<net.minecraft.world.level.biome.Biome>> biome =
                wind.registryAccess().registryOrThrow(Registries.BIOME).getHolder(BIOME_0);
        if (biome.isEmpty()) {
            return false;
        }
        Optional<Holder.Reference<PlacedFeature>> lake =
                wind.registryAccess().registryOrThrow(Registries.PLACED_FEATURE).getHolder(LAKE_PLACED);
        if (lake.isEmpty()) {
            return false;
        }
        List<HolderSet<PlacedFeature>> steps = biome.get().value().getGenerationSettings().features();
        int step = GenerationStep.Decoration.SURFACE_STRUCTURES.ordinal();
        if (step < 0 || step >= steps.size()) {
            // 兜底：任意 step 命中也算 wired
            for (HolderSet<PlacedFeature> set : steps) {
                if (set.contains(lake.get())) {
                    return true;
                }
            }
            return false;
        }
        return steps.get(step).contains(lake.get());
    }

    /** 螺旋搜索 biome_0；步长 16 格。 */
    private static BlockPos findBiome0Anchor(ServerLevel wind, BlockPos origin, int maxRing) {
        if (isBiome0(wind, origin)) {
            return origin;
        }
        for (int ring = 1; ring <= maxRing; ring++) {
            int step = 16;
            int r = ring * step;
            for (int dx = -r; dx <= r; dx += step) {
                BlockPos a = origin.offset(dx, 0, -r);
                if (isBiome0(wind, a)) {
                    return a;
                }
                BlockPos b = origin.offset(dx, 0, r);
                if (isBiome0(wind, b)) {
                    return b;
                }
            }
            for (int dz = -r + step; dz <= r - step; dz += step) {
                BlockPos a = origin.offset(-r, 0, dz);
                if (isBiome0(wind, a)) {
                    return a;
                }
                BlockPos b = origin.offset(r, 0, dz);
                if (isBiome0(wind, b)) {
                    return b;
                }
            }
        }
        return null;
    }

    private static boolean isBiome0(ServerLevel wind, BlockPos pos) {
        int y = wind.getHeight(Heightmap.Types.WORLD_SURFACE_WG, pos.getX(), pos.getZ());
        if (y <= wind.getMinBuildHeight()) {
            y = 80;
        }
        return wind.getBiome(new BlockPos(pos.getX(), y, pos.getZ())).is(BIOME_0);
    }

    private static int forceChunks(ServerLevel wind, ChunkPos center, int radius) {
        int n = 0;
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                int cx = center.x + dx;
                int cz = center.z + dz;
                LevelChunk chunk = wind.getChunk(cx, cz);
                if (chunk != null) {
                    wind.setChunkForced(cx, cz, true);
                    n++;
                }
            }
        }
        return n;
    }

    private record LakeScan(int hits, int water, int cyanNearWater, String sample) {
    }

    private static LakeScan scanLakeShape(ServerLevel wind, BlockPos center) {
        Block cyan = BuiltInRegistries.BLOCK.get(CYAN_STONE_ID);
        boolean cyanKnown = cyan != null && cyan != Blocks.AIR;

        int minY = wind.getMinBuildHeight();
        int maxY = wind.getMaxBuildHeight() - 1;
        int water = 0;
        int cyanNear = 0;
        int hits = 0;
        String sample = "-";

        int y0 = Math.max(minY, center.getY() - SCAN_Y_PAD);
        int y1 = Math.min(maxY, center.getY() + SCAN_Y_PAD);

        // 扩大：对中心附近每个列取 surface 再扫，避免 heightmap 与湖面偏差
        for (int dx = -SCAN_XZ; dx <= SCAN_XZ; dx += 2) {
            for (int dz = -SCAN_XZ; dz <= SCAN_XZ; dz += 2) {
                int x = center.getX() + dx;
                int z = center.getZ() + dz;
                int surface = wind.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z);
                int colY0 = Math.max(minY, surface - SCAN_Y_PAD);
                int colY1 = Math.min(maxY, Math.max(surface + 4, y1));
                for (int y = colY0; y <= colY1; y++) {
                    BlockPos p = new BlockPos(x, y, z);
                    BlockState st = wind.getBlockState(p);
                    if (!st.is(Blocks.WATER)) {
                        continue;
                    }
                    water++;
                    if (!cyanKnown) {
                        continue;
                    }
                    if (hasAdjacentOrBelow(wind, p, cyan)) {
                        cyanNear++;
                        hits++;
                        if ("-".equals(sample)) {
                            sample = p.getX() + "," + p.getY() + "," + p.getZ();
                        }
                    }
                }
            }
        }
        // 若 barrier 登记失败，至少要求有水（弱断言，仍记 fail 于 cyan 路径）
        if (!cyanKnown && water > 0) {
            hits = 0;
            sample = "cyan_stone missing in registry; water=" + water;
        }
        return new LakeScan(hits, water, cyanNear, sample);
    }

    private static boolean hasAdjacentOrBelow(ServerLevel wind, BlockPos water, Block cyan) {
        BlockPos.MutableBlockPos m = new BlockPos.MutableBlockPos();
        int[][] offs = {
                {0, -1, 0}, {0, -2, 0},
                {1, 0, 0}, {-1, 0, 0}, {0, 0, 1}, {0, 0, -1},
                {1, -1, 0}, {-1, -1, 0}, {0, -1, 1}, {0, -1, -1}
        };
        for (int[] o : offs) {
            m.set(water.getX() + o[0], water.getY() + o[1], water.getZ() + o[2]);
            if (wind.getBlockState(m).is(cyan)) {
                return true;
            }
        }
        return false;
    }
}
