package com.pasterdream.pasterdreammod.worldgen;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.api.util.ServerScheduler;
import com.pasterdream.pasterdreammod.registry.PDBiomes;
import com.pasterdream.pasterdreammod.world.ArenaRuinInfection;
import com.pasterdream.pasterdreammod.world.PDAaroncosArenaSpawnData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.commands.FillBiomeCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * 亚伦柯斯竞技场主世界群系/感染协调器。
 * <p>
 * 竞技场遗迹本身由 {@code aaroncos_arena_portals} 结构集<b>正常随机生成</b>
 * （见 {@link com.pasterdream.pasterdreammod.worldgen.structure.AaroncosArenaPortalStructure}，
 * 每世界仅生成一次），本类不再负责搜索/放置遗迹，而是：
 * <ul>
 *   <li>结构生成后（服务端主线程回调 {@link #onArenaGenerated}）：记录中心坐标、
 *       分帧生成周围 chunk 并用 {@link FillBiomeCommand} 刷写竞技场群系
 *       {@code pasterdream:aaroncos_arena_biome}，同时启动遗迹感染。</li>
 *   <li>服务器启动时（{@link #onServerStarting}）：仅恢复已记录的遗迹感染，
 *       不再强制生成任何地形。</li>
 * </ul>
 * 中心坐标记录在 {@link PDAaroncosArenaSpawnData} 中。
 */
public class PDAaroncosArenaWorldgen {

    /** 群系覆盖半径（方块），决定竞技场群系范围 */
    private static final int BIOME_RADIUS = 48;
    /**
     * 分帧生成 chunk 的状态。
     * <p>
     * 使用 {@link ChunkStatus#FEATURES} 而非 FULL：特征（含树/高度图）已完成，
     * 但跳过最耗时的光照阶段；修改生物群系只需 biome 数据，无需完整光照。
     */
    private static final ChunkStatus PREGEN_CHUNK_STATUS = ChunkStatus.FEATURES;
    /** 每 tick 预生成的 chunk 数量，分摊压力避免卡顿 */
    private static final int CHUNKS_PER_TICK = 3;

    private PDAaroncosArenaWorldgen() {
    }

    /**
     * 服务器启动时：仅恢复遗迹持续感染，不强制生成任何地形。
     *
     * @param event 服务器启动中事件
     */
    public static void onServerStarting(ServerStartingEvent event) {
        ServerLevel overworld = event.getServer().overworld();
        if (overworld == null) {
            return;
        }

        PDAaroncosArenaSpawnData spawnData = PDAaroncosArenaSpawnData.get(overworld);
        if (spawnData.isPlaced() && spawnData.getCenter() != null) {
            ArenaRuinInfection.start(overworld, spawnData.getCenter());
            PasterDreamMod.LOGGER.info("[PDAaroncosArenaWorldgen] 当前世界已生成竞技场遗迹（{}），恢复遗迹感染",
                    spawnData.getCenter().toShortString());
        }
    }

    /**
     * 主世界竞技场遗迹生成后回调（服务端主线程调用）。
     * <p>
     * 记录遗迹中心坐标、分帧刷写竞技场群系、启动遗迹感染。
     *
     * @param overworld 主世界
     * @param center    遗迹起始位置（结构生成桩位置）
     */
    public static void onArenaGenerated(ServerLevel overworld, BlockPos center) {
        PDAaroncosArenaSpawnData spawnData = PDAaroncosArenaSpawnData.get(overworld);
        spawnData.setCenter(center);
        setArenaBiomeAsync(overworld, center);
        ArenaRuinInfection.start(overworld, center);
        PasterDreamMod.LOGGER.info("[PDAaroncosArenaWorldgen] 竞技场遗迹已生成于 {}，正在分帧刷写竞技场群系",
                center.toShortString());
    }

    /**
     * 分帧生成群系覆盖范围 chunk，全部完成后执行 {@link FillBiomeCommand}。
     *
     * @param level     主世界
     * @param centerPos 遗迹中心位置
     */
    private static void setArenaBiomeAsync(ServerLevel level, BlockPos centerPos) {
        Holder<Biome> arenaBiome = level.registryAccess().lookupOrThrow(Registries.BIOME)
                .getOrThrow(PDBiomes.BIOME_AARONCOS_ARENA);

        BlockPos from = centerPos.offset(-BIOME_RADIUS, -BIOME_RADIUS, -BIOME_RADIUS);
        BlockPos to = centerPos.offset(BIOME_RADIUS, BIOME_RADIUS, BIOME_RADIUS);

        List<ChunkPos> chunks = new ArrayList<>();
        for (int cx = from.getX() >> 4; cx <= (to.getX() >> 4); cx++) {
            for (int cz = from.getZ() >> 4; cz <= (to.getZ() >> 4); cz++) {
                chunks.add(new ChunkPos(cx, cz));
            }
        }

        generateChunksBatch(level, chunks, 0, () -> fillArenaBiome(level, from, to, arenaBiome));
    }

    /**
     * 每 tick 分帧生成一批 chunk，全部完成后执行回调。
     *
     * @param level  主世界
     * @param chunks 待生成 chunk 列表
     * @param index  当前处理下标
     * @param done   全部完成后的回调
     */
    private static void generateChunksBatch(ServerLevel level, List<ChunkPos> chunks, int index, Runnable done) {
        int end = Math.min(index + CHUNKS_PER_TICK, chunks.size());
        for (int i = index; i < end; i++) {
            ChunkPos chunkPos = chunks.get(i);
            level.getChunk(chunkPos.x, chunkPos.z, PREGEN_CHUNK_STATUS, true);
        }
        if (end < chunks.size()) {
            ServerScheduler.schedule(1, () -> generateChunksBatch(level, chunks, end, done));
        } else {
            done.run();
        }
    }

    /**
     * 将遗迹周围区域设置成亚伦柯斯竞技场群系。
     * <p>
     * 使用 {@link FillBiomeCommand#fill} 批量替换生物群系，并同步给客户端。
     *
     * @param level     主世界
     * @param from      区域一角
     * @param to        区域对角
     * @param arenaBiome 竞技场群系
     */
    private static void fillArenaBiome(ServerLevel level, BlockPos from, BlockPos to, Holder<Biome> arenaBiome) {
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
}
