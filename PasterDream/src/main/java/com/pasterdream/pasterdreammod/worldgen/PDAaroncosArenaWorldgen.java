package com.pasterdream.pasterdreammod.worldgen;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.api.util.ServerScheduler;
import com.pasterdream.pasterdreammod.config.PDCommonConfig;
import com.pasterdream.pasterdreammod.registry.PDBiomes;
import com.pasterdream.pasterdreammod.world.ArenaRuinInfection;
import com.pasterdream.pasterdreammod.world.PDAaroncosArenaSpawnData;
import com.pasterdream.pasterdreammod.world.PortalInfectionData;
import com.pasterdream.pasterdreammod.world.PortalRestorationHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.commands.FillBiomeCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 亚伦柯斯竞技场主世界群系/感染协调器。
 * <p>
 * 竞技场遗迹本身由 {@code aaroncos_arena_portals} 结构集<b>正常随机生成</b>
 * （见 {@link com.pasterdream.pasterdreammod.worldgen.structure.AaroncosArenaPortalStructure}，
 * 每世界仅生成一次）。本类负责放置确认与感染生命周期：
 * <ul>
 *   <li><b>放置确认</b>：结构查询方法（{@code findGenerationPoint}）不再承担任何副作用
 *       （历史恶性 BUG：第三方结构搜索工具一次查询即触发不可逆感染并永久锁死结构生成）。
 *       改由传送门方块在世界生成阶段真实落入世界时（工作线程）调用
 *       {@link #offerPendingPlacement} 入队，主线程确认器
 *       {@link #confirmArenaPlacement} 落库：标记 placed/记录中心，并按配置启动
 *       群系刷写与遗迹感染。结构查询/预览路径零写入。</li>
 *   <li><b>服务器启动</b>（{@link #onServerStarting}）：配置关闭（默认）时强制停止
 *       已有感染并自动清理（回滚被感染方块、还原竞技场群系）；配置开启且 BOSS
 *       未击败时恢复遗迹感染（群系刷写中断会补完）。</li>
 *   <li><b>治愈</b>（{@link #cureInfection}）：BOSS 击败或配置关闭清理时调用——
 *       停止感染、标记击败、回滚全部被感染方块、按噪声源重推导还原竞技场群系。</li>
 * </ul>
 * 中心坐标与状态记录在 {@link PDAaroncosArenaSpawnData} 中。
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
    /** 放置确认排水器的轮询间隔（tick） */
    private static final int CONFIRM_INTERVAL_TICKS = 20;

    /**
     * 世界生成放置的传送门待确认位置队列。
     * <p>
     * 世界生成在区块生成工作线程上放置方块，{@code onPlace} 上下文无
     * ServerLevel/主线程调度器可用，故经此线程安全队列转交主线程确认器。
     */
    private static final ConcurrentLinkedQueue<BlockPos> PENDING_CONFIRM = new ConcurrentLinkedQueue<>();

    private PDAaroncosArenaWorldgen() {
    }

    // ==================== 放置确认 ====================

    /**
     * 传送门方块在世界生成阶段真实落入主世界时入队待确认位置。
     * <p>
     * 仅可由方块 {@code onPlace} 的世界生成上下文（非客户端、非 ServerLevel、
     * 主世界维度）调用；区块生成工作线程安全。
     *
     * @param pos 传送门方块位置
     */
    public static void offerPendingPlacement(BlockPos pos) {
        if (pos != null) {
            PENDING_CONFIRM.add(pos.immutable());
        }
    }

    /**
     * 启动主线程放置确认排水器（自续轮询）。
     * <p>
     * 每隔 {@link #CONFIRM_INTERVAL_TICKS} tick 排空待确认队列一次；
     * 服务器停止时 {@link ServerScheduler} 清空任务，自续循环自然终止。
     *
     * @param overworld 主世界
     */
    private static void startConfirmDrainer(ServerLevel overworld) {
        ServerScheduler.schedule(CONFIRM_INTERVAL_TICKS, () -> {
            BlockPos pos;
            while ((pos = PENDING_CONFIRM.poll()) != null) {
                confirmArenaPlacement(overworld, pos);
            }
            startConfirmDrainer(overworld);
        });
    }

    /**
     * 确认一次竞技场遗迹真实放置（仅主线程调用）。
     * <p>
     * 传送门方块实际落入世界即证明结构真实生成（区别于结构查询/预览），
     * 此时才标记 placed、记录中心——保证主世界竞技场只生成一次，
     * 且第三方结构查询永远不会误触发。首次确认后按配置启动群系刷写与感染；
     * 同结构的其余传送门方块（或同 tick 竞态的其余候选）会被 placed 短路跳过。
     *
     * @param overworld 主世界
     * @param pos       被放置的传送门方块位置
     */
    private static void confirmArenaPlacement(ServerLevel overworld, BlockPos pos) {
        PDAaroncosArenaSpawnData spawnData = PDAaroncosArenaSpawnData.get(overworld);
        if (spawnData.isPlaced()) {
            return; // 已确认过（同结构多块传送门/竞态败者），保证只生成一次
        }
        spawnData.markPlaced();
        spawnData.setCenter(pos);

        // 配置关闭（默认）：仅记录放置与中心用于"只生成一次"，不刷群系、不启动感染
        if (!PDCommonConfig.ARENA_RUIN_INFECTION_ENABLED.get()) {
            PasterDreamMod.LOGGER.info(
                    "[PDAaroncosArenaWorldgen] 竞技场遗迹已确认生成于 {}（感染功能已在配置中关闭，不刷群系、不启动感染）",
                    pos.toShortString());
            return;
        }

        PasterDreamMod.LOGGER.info(
                "[PDAaroncosArenaWorldgen] 竞技场遗迹已确认生成于 {}，正在分帧刷写竞技场群系并启动遗迹感染",
                pos.toShortString());
        if (!spawnData.isBiomePainted()) {
            setArenaBiomeAsync(overworld, pos);
        }
        ArenaRuinInfection.start(overworld, pos);
    }

    // ==================== 服务器生命周期 ====================

    /**
     * 服务器启动时：启动放置确认排水器，并按配置处理已有存档的感染。
     * <ul>
     *   <li>配置关闭（默认）：已有感染的存档被<b>强制停止并自动清理</b>
     *       （停止感染循环、回滚被感染方块、还原竞技场群系、标记击败状态，
     *       之后重启不再恢复感染）；</li>
     *   <li>配置开启且 BOSS 未击败：恢复遗迹感染（群系刷写中断的补完）；</li>
     *   <li>配置开启且 BOSS 已击败：保持退化状态，不再恢复。</li>
     * </ul>
     *
     * @param event 服务器启动中事件
     */
    public static void onServerStarting(ServerStartingEvent event) {
        ServerLevel overworld = event.getServer().overworld();
        if (overworld == null) {
            return;
        }

        // 无论配置开关，排水器必须运行：真实放置的确认（placed 落库）保证"只生成一次"
        startConfirmDrainer(overworld);

        PDAaroncosArenaSpawnData spawnData = PDAaroncosArenaSpawnData.get(overworld);
        BlockPos center = spawnData.getCenter();

        // 配置关闭（默认）：强制停止已有感染并自动清理，之后不再恢复
        if (!PDCommonConfig.ARENA_RUIN_INFECTION_ENABLED.get()) {
            if (spawnData.isPlaced() && center != null && !spawnData.isDefeated()) {
                PasterDreamMod.LOGGER.info(
                        "[PDAaroncosArenaWorldgen] 感染功能已在配置中关闭，强制停止并自动清理存档中的遗迹感染（中心 {}）",
                        center.toShortString());
                cureInfection(overworld);
            }
            return;
        }

        // 配置开启：恢复未击败存档的感染
        if (spawnData.isPlaced() && center != null && !spawnData.isDefeated()) {
            if (!spawnData.isBiomePainted()) {
                // 群系刷写中断（服务器上次在分帧刷写期间关闭）的补完
                setArenaBiomeAsync(overworld, center);
            }
            ArenaRuinInfection.start(overworld, center);
            PasterDreamMod.LOGGER.info("[PDAaroncosArenaWorldgen] 当前世界已生成竞技场遗迹（{}），恢复遗迹感染",
                    center.toShortString());
        } else if (spawnData.isPlaced() && spawnData.isDefeated()) {
            PasterDreamMod.LOGGER.info("[PDAaroncosArenaWorldgen] 当前世界竞技场BOSS已被击败，遗迹感染保持退化状态");
        }
    }

    /**
     * 服务器停止时清空放置确认队列，避免跨存档残留。
     *
     * @param event 服务器停止事件
     */
    public static void onServerStopped(ServerStoppedEvent event) {
        PENDING_CONFIRM.clear();
    }

    // ==================== 治愈（感染退化） ====================

    /**
     * 治愈遗迹感染：停止感染 + 标记击败 + 回滚被感染方块 + 还原竞技场群系。
     * <p>
     * 两个调用方：竞技场 BOSS 胜利（{@code PDArenaBossManager} 触发，即"击败 BOSS
     * 后感染退化"）；服务器启动时配置关闭的强制清理（旧存档自动清理）。
     * <p>
     * 群系还原采用噪声源重推导：竞技场群系是覆盖在世界生成群系之上的，
     * {@link BiomeSource#getNoiseBiome} 按种子确定性给出刷写前的原始群系，
     * 因此无需记录原始群系数据即可精确还原（对旧版存档同样有效）。
     *
     * @param overworld 主世界
     */
    public static void cureInfection(ServerLevel overworld) {
        // 先停感染、标记击败，再回滚，防止回滚过程中被重新感染
        ArenaRuinInfection.stop();

        PDAaroncosArenaSpawnData spawnData = PDAaroncosArenaSpawnData.get(overworld);
        spawnData.markDefeated();

        PortalInfectionData infectionData = PortalInfectionData.get(overworld);
        PortalRestorationHandler.startRestoration(overworld, infectionData.getPortalPositions());

        // 群系还原：新版有 biomePainted 记录；旧版存档没有该记录，
        // 但存在感染记录即说明旧版曾无条件刷写过群系，一并还原
        if (spawnData.getCenter() != null
                && (spawnData.isBiomePainted() || !infectionData.isEmpty())) {
            restoreArenaBiomeAsync(overworld, spawnData.getCenter());
        }
    }

    // ==================== 群系刷写（竞技场氛围） ====================

    /**
     * 分帧生成群系覆盖范围 chunk，全部完成后执行 {@link FillBiomeCommand}。
     *
     * @param level     主世界
     * @param centerPos 遗迹中心位置
     */
    private static void setArenaBiomeAsync(ServerLevel level, BlockPos centerPos) {
        Holder<Biome> arenaBiome = level.registryAccess().lookupOrThrow(Registries.BIOME)
                .getOrThrow(PDBiomes.AARONCOS_ARENA);

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
     * 将遗迹周围区域设置成亚伦柯斯竞技场群系，完成后标记 biomePainted。
     * <p>
     * 使用 {@link FillBiomeCommand#fill} 批量替换生物群系，并同步给客户端。
     *
     * @param level     主世界
     * @param from      区域一角
     * @param to        区域对角
     * @param arenaBiome 竞技场群系
     */
    private static void fillArenaBiome(ServerLevel level, BlockPos from, BlockPos to, Holder<Biome> arenaBiome) {
        // 配置中途关闭则放弃本次刷写（分帧加载期间的运行时开关）
        if (!PDCommonConfig.ARENA_RUIN_INFECTION_ENABLED.get()) {
            return;
        }

        // 临时放宽 /fillbiome 的体积限制，确保大半径群系能一次设置成功
        int originalLimit = level.getGameRules().getInt(GameRules.RULE_COMMAND_MODIFICATION_BLOCK_LIMIT);
        level.getGameRules().getRule(GameRules.RULE_COMMAND_MODIFICATION_BLOCK_LIMIT).set(1_000_000, level.getServer());

        var result = FillBiomeCommand.fill(level, from, to, arenaBiome);

        // 恢复原限制
        level.getGameRules().getRule(GameRules.RULE_COMMAND_MODIFICATION_BLOCK_LIMIT).set(originalLimit, level.getServer());

        if (result.right().isPresent()) {
            PasterDreamMod.LOGGER.warn("[PDAaroncosArenaWorldgen] 设置竞技场群系失败: {}", result.right().get().getMessage());
            return;
        }

        if (result.left().isPresent()) {
            PasterDreamMod.LOGGER.info("[PDAaroncosArenaWorldgen] 成功设置竞技场群系，共修改 {} 个 biome 位置", result.left().get());
        }
        // 刷写完成才标记，服务器在分帧期间停止时下次启动会补刷
        PDAaroncosArenaSpawnData.get(level).markBiomePainted();
    }

    // ==================== 群系还原（感染退化） ====================

    /**
     * 分帧加载群系覆盖范围的完整 chunk（FULL 状态），全部完成后执行群系还原。
     *
     * @param level     主世界
     * @param centerPos 遗迹中心位置
     */
    private static void restoreArenaBiomeAsync(ServerLevel level, BlockPos centerPos) {
        BlockPos from = centerPos.offset(-BIOME_RADIUS, -BIOME_RADIUS, -BIOME_RADIUS);
        BlockPos to = centerPos.offset(BIOME_RADIUS, BIOME_RADIUS, BIOME_RADIUS);

        List<ChunkPos> chunks = new ArrayList<>();
        for (int cx = from.getX() >> 4; cx <= (to.getX() >> 4); cx++) {
            for (int cz = from.getZ() >> 4; cz <= (to.getZ() >> 4); cz++) {
                chunks.add(new ChunkPos(cx, cz));
            }
        }

        // 群系还原要求 chunk 达到 FULL 状态（resendBiomeForChunks 需要真实 LevelChunk）
        generateFullChunksBatch(level, chunks, 0, () -> restoreArenaBiome(level, from, to));
    }

    /**
     * 每 tick 分帧强制加载一批 FULL 状态 chunk，全部完成后执行回调。
     *
     * @param level  主世界
     * @param chunks 待加载 chunk 列表
     * @param index  当前处理下标
     * @param done   全部完成后的回调
     */
    private static void generateFullChunksBatch(ServerLevel level, List<ChunkPos> chunks, int index, Runnable done) {
        int end = Math.min(index + CHUNKS_PER_TICK, chunks.size());
        for (int i = index; i < end; i++) {
            ChunkPos chunkPos = chunks.get(i);
            level.getChunk(chunkPos.x, chunkPos.z, ChunkStatus.FULL, true);
        }
        if (end < chunks.size()) {
            ServerScheduler.schedule(1, () -> generateFullChunksBatch(level, chunks, end, done));
        } else {
            done.run();
        }
    }

    /**
     * 将竞技场群系覆盖区域还原为原始群系（按噪声源重推导）。
     * <p>
     * 与 {@link FillBiomeCommand#fill} 完全对称的逆操作：fill 将区域统一覆盖为
     * 竞技场群系；本方法对区域内每个四分位单元调用
     * {@link BiomeSource#getNoiseBiome}（世界生成群系的本源，按种子确定），
     * 还原为未被覆盖前的原始群系，区域外保持现状。
     *
     * @param level 主世界
     * @param from  区域一角（方块坐标）
     * @param to    区域对角（方块坐标）
     */
    private static void restoreArenaBiome(ServerLevel level, BlockPos from, BlockPos to) {
        BlockPos qFrom = quantize(from);
        BlockPos qTo = quantize(to);
        BoundingBox region = BoundingBox.fromCorners(qFrom, qTo);

        BiomeSource biomeSource = level.getChunkSource().getGenerator().getBiomeSource();
        Climate.Sampler sampler = level.getChunkSource().randomState().sampler();

        List<ChunkAccess> chunks = new ArrayList<>();
        for (int cz = SectionPos.blockToSectionCoord(region.minZ());
                cz <= SectionPos.blockToSectionCoord(region.maxZ()); cz++) {
            for (int cx = SectionPos.blockToSectionCoord(region.minX());
                    cx <= SectionPos.blockToSectionCoord(region.maxX()); cx++) {
                ChunkAccess chunk = level.getChunk(cx, cz, ChunkStatus.FULL, false);
                if (chunk != null) {
                    chunks.add(chunk);
                }
            }
        }

        for (ChunkAccess chunk : chunks) {
            ChunkAccess sectionHolder = chunk;
            // 区域内还原为噪声推导的原始群系，区域外保持现状（与原版 fill 的 resolver 模式一致）
            chunk.fillBiomesFromNoise((quartX, quartY, quartZ, quartSampler) -> {
                if (region.isInside(QuartPos.toBlock(quartX), QuartPos.toBlock(quartY), QuartPos.toBlock(quartZ))) {
                    return biomeSource.getNoiseBiome(quartX, quartY, quartZ, quartSampler);
                }
                return sectionHolder.getNoiseBiome(quartX, quartY, quartZ);
            }, sampler);
            chunk.setUnsaved(true);
        }

        // 将修改后的群系数据重新同步给在线客户端
        level.getChunkSource().chunkMap.resendBiomesForChunks(chunks);
        PasterDreamMod.LOGGER.info("[PDAaroncosArenaWorldgen] 竞技场群系已还原为原始群系（{} 个 chunk）", chunks.size());
    }

    /**
     * 四分位量化（与 {@link FillBiomeCommand} 保持一致，保证还原区域与刷写区域完全重合）。
     *
     * @param value 方块坐标分量
     * @return 量化后的方块坐标分量
     */
    private static int quantize(int value) {
        return QuartPos.toBlock(QuartPos.fromBlock(value));
    }

    /**
     * 对位置做四分位量化。
     *
     * @param pos 方块坐标
     * @return 量化后的方块坐标
     */
    private static BlockPos quantize(BlockPos pos) {
        return new BlockPos(quantize(pos.getX()), quantize(pos.getY()), quantize(pos.getZ()));
    }
}
