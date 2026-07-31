package com.pasterdream.pasterdreammod.worldgen;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.registry.PDBiomes;
import com.pasterdream.pasterdreammod.world.ArenaRuinInfection;
import com.pasterdream.pasterdreammod.world.PDAaroncosArenaSpawnData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.commands.FillBiomeCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

import java.util.Optional;

/**
 * 亚伦柯斯竞技场主世界生成器。
 * <p>
 * 不再把竞技场群系注入主世界的 MultiNoise 群系源（避免 /locate 在大范围内搜索导致卡死），
 * 而是在服务器启动时，在出生点附近寻找合适地表，直接放置遗迹结构，并用 {@link FillBiomeCommand}
 * 将周围区域设置为 {@code pasterdream:aaroncos_arena_biome}。
 * <p>
 * 每个世界仅放置一次，位置记录在 {@link PDAaroncosArenaSpawnData} 中。
 */
public class PDAaroncosArenaWorldgen {

    /** 在出生点附近搜索合适位置的最大半径（方块） */
    private static final int SPAWN_SEARCH_RADIUS = 128;
    /** 搜索时的步进（方块），越大越快但可能错过平坦点 */
    private static final int SEARCH_STEP = 4;
    /** 遗迹结构底座允许的最大高度差，超过则认为不够平坦 */
    private static final int MAX_TERRAIN_VARIATION = 6;
    /** 遗迹结构在 X/Z 方向上的占地尺寸（来自 NBT 的 size） */
    private static final int STRUCTURE_FOOTPRINT = 21;
    /** 遗迹底座埋入地下的方块数（18 格左右，使遗迹大部分沉于地下，仅露出顶部） */
    private static final int BASE_BURIAL_BLOCKS = 18;
    /** 群系覆盖半径（方块），决定竞技场群系范围 */
    private static final int BIOME_RADIUS = 48;

    /**
     * 服务器启动时，在主世界出生点附近生成唯一一座竞技场遗迹及其群系。
     *
     * @param event 服务器启动中事件
     */
    public static void onServerStarting(ServerStartingEvent event) {
        ServerLevel overworld = event.getServer().overworld();
        if (overworld == null) {
            return;
        }

        PDAaroncosArenaSpawnData spawnData = PDAaroncosArenaSpawnData.get(overworld);
        if (spawnData.isPlaced()) {
            // 重启后：恢复遗迹持续感染（若已记录中心坐标）
            ArenaRuinInfection.start(overworld, spawnData.getCenter());
            PasterDreamMod.LOGGER.info("[PDAaroncosArenaWorldgen] 当前世界已放置过竞技场遗迹，跳过放置");
            return;
        }

        BlockPos spawnPos = overworld.getSharedSpawnPos();
        BlockPos targetPos = findFlatSurfaceNearSpawn(overworld, spawnPos);
        if (targetPos == null) {
            PasterDreamMod.LOGGER.warn("[PDAaroncosArenaWorldgen] 未能在出生点附近找到合适的竞技场遗迹位置");
            return;
        }

        if (!placeArenaStructure(overworld, targetPos)) {
            PasterDreamMod.LOGGER.warn("[PDAaroncosArenaWorldgen] 竞技场遗迹结构放置失败");
            return;
        }

        setArenaBiome(overworld, targetPos);
        spawnData.markPlaced();
        spawnData.setCenter(targetPos);
        ArenaRuinInfection.start(overworld, targetPos);

        PasterDreamMod.LOGGER.info("[PDAaroncosArenaWorldgen] 已在出生点附近 {} 生成竞技场遗迹与群系", targetPos.toShortString());
    }

    /**
     * 在出生点附近寻找相对平坦的地表位置。
     * <p>
     * 以出生点为中心向外螺旋搜索，检测 21x21 范围内的高度差，
     * 找到第一个高度差不超过 {@link #MAX_TERRAIN_VARIATION} 的位置。
     *
     * @param level    主世界
     * @param spawnPos 世界出生点
     * @return 适合放置遗迹的方块位置；找不到则返回 null
     */
    private static BlockPos findFlatSurfaceNearSpawn(ServerLevel level, BlockPos spawnPos) {
        RandomSource random = level.getRandom();

        for (int radius = 0; radius <= SPAWN_SEARCH_RADIUS; radius += SEARCH_STEP) {
            int attempts = Math.max(1, radius / SEARCH_STEP * 4);
            for (int i = 0; i < attempts; i++) {
                double angle = random.nextDouble() * Math.PI * 2;
                int dx = (int) Math.round(Math.cos(angle) * radius);
                int dz = (int) Math.round(Math.sin(angle) * radius);
                int x = spawnPos.getX() + dx;
                int z = spawnPos.getZ() + dz;

                // 预生成该位置的 chunk（若尚未生成）
                level.getChunk(x >> 4, z >> 4, ChunkStatus.FULL, true);

                if (isFlatSurface(level, x, z)) {
                    int surfaceY = level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z);
                    // 底座埋入 BASE_BURIAL_BLOCKS 格：结构原点 Y = 地表 Y - 埋入格数 - 1
                    return new BlockPos(x, surfaceY - BASE_BURIAL_BLOCKS - 1, z);
                }
            }
        }
        return null;
    }

    /**
     * 判断指定位置的地表是否足够平坦。
     * <p>
     * 采样以 (x, z) 为中心的 {@link #STRUCTURE_FOOTPRINT} x {@link #STRUCTURE_FOOTPRINT} 区域，
     * 计算地表高度的最小值与最大值之差。
     *
     * @param level 主世界
     * @param x     中心 X
     * @param z     中心 Z
     * @return true 若高度差不超过 {@link #MAX_TERRAIN_VARIATION}
     */
    private static boolean isFlatSurface(ServerLevel level, int x, int z) {
        int half = STRUCTURE_FOOTPRINT / 2;
        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;

        for (int dx = -half; dx <= half; dx += 4) {
            for (int dz = -half; dz <= half; dz += 4) {
                int sampleX = x + dx;
                int sampleZ = z + dz;

                // 确保 chunk 已生成
                level.getChunk(sampleX >> 4, sampleZ >> 4, ChunkStatus.FULL, true);

                int y = level.getHeight(Heightmap.Types.WORLD_SURFACE, sampleX, sampleZ);
                minY = Math.min(minY, y);
                maxY = Math.max(maxY, y);

                if (maxY - minY > MAX_TERRAIN_VARIATION) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 在目标位置放置竞技场遗迹结构 NBT。
     *
     * @param level     主世界
     * @param targetPos 结构原点位置
     * @return true 若放置成功
     */
    private static boolean placeArenaStructure(ServerLevel level, BlockPos targetPos) {
        ResourceLocation structureId = ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "aaroncos_arena_portals");
        Optional<StructureTemplate> templateOpt = level.getStructureManager().get(structureId);
        if (templateOpt.isEmpty() || templateOpt.get().getSize().getX() <= 0) {
            return false;
        }

        StructureTemplate template = templateOpt.get();
        StructurePlaceSettings settings = new StructurePlaceSettings()
                .setRotation(net.minecraft.world.level.block.Rotation.NONE)
                .setMirror(net.minecraft.world.level.block.Mirror.NONE)
                .setIgnoreEntities(false);

        return template.placeInWorld(level, targetPos, targetPos, settings, level.random, 3);
    }

    /**
     * 将遗迹周围区域设置成亚伦柯斯竞技场群系。
     * <p>
     * 使用 {@link FillBiomeCommand#fill} 批量替换生物群系，并同步给客户端。
     *
     * @param level     主世界
     * @param centerPos 遗迹中心位置
     */
    private static void setArenaBiome(ServerLevel level, BlockPos centerPos) {
        Holder<Biome> arenaBiome = level.registryAccess().lookupOrThrow(Registries.BIOME)
                .getOrThrow(PDBiomes.BIOME_AARONCOS_ARENA);

        BlockPos from = centerPos.offset(-BIOME_RADIUS, -BIOME_RADIUS, -BIOME_RADIUS);
        BlockPos to = centerPos.offset(BIOME_RADIUS, BIOME_RADIUS, BIOME_RADIUS);

        // 预先生成群系覆盖范围内的所有 chunk，避免 fill 因 unloaded chunk 失败
        ensureChunksGenerated(level, from, to);

        // 临时放宽 /fillbiome 的体积限制，确保大半径群系能一次设置成功
        int originalLimit = level.getGameRules().getInt(GameRules.RULE_COMMAND_MODIFICATION_BLOCK_LIMIT);
        level.getGameRules().getRule(GameRules.RULE_COMMAND_MODIFICATION_BLOCK_LIMIT).set(1_000_000, level.getServer());

        var result = FillBiomeCommand.fill(level, from, to, arenaBiome);

        // 恢复原限制
        level.getGameRules().getRule(GameRules.RULE_COMMAND_MODIFICATION_BLOCK_LIMIT).set(originalLimit, level.getServer());

        if (result.right().isPresent()) {
            PasterDreamMod.LOGGER.warn("[PDAaroncosArenaWorldgen] 设置竞技场群系失败: {}", result.right().get().getMessage());
        } else if (result.left().isPresent()) {
            PasterDreamMod.LOGGER.info("[PDAaroncosArenaWorldgen] 成功设置竞技场群系，共修改 {} 个 biome 位置", result.left().get());
        }
    }

    /**
     * 强制生成指定矩形区域内的所有 chunk。
     *
     * @param level 主世界
     * @param from  区域一角
     * @param to    区域对角
     */
    private static void ensureChunksGenerated(ServerLevel level, BlockPos from, BlockPos to) {
        int minChunkX = from.getX() >> 4;
        int minChunkZ = from.getZ() >> 4;
        int maxChunkX = to.getX() >> 4;
        int maxChunkZ = to.getZ() >> 4;

        for (int cx = minChunkX; cx <= maxChunkX; cx++) {
            for (int cz = minChunkZ; cz <= maxChunkZ; cz++) {
                level.getChunk(cx, cz, ChunkStatus.FULL, true);
            }
        }
    }
}
