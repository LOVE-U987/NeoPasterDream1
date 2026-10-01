package com.pasterdream.pasterdreammod.smoketest;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.api.util.ServerScheduler;
import com.pasterdream.pasterdreammod.config.PDCommonConfig;
import com.pasterdream.pasterdreammod.registry.PDBiomes;
import com.pasterdream.pasterdreammod.registry.PDBlocks;
import com.pasterdream.pasterdreammod.world.ArenaInfectionUtils;
import com.pasterdream.pasterdreammod.world.ArenaRuinInfection;
import com.pasterdream.pasterdreammod.world.PDAaroncosArenaSpawnData;
import com.pasterdream.pasterdreammod.world.PortalInfectionData;
import com.pasterdream.pasterdreammod.worldgen.PDAaroncosArenaWorldgen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.commands.FillBiomeCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * 竞技场遗迹感染生命周期 VERIFY 套件 {@code arena-infection}。
 * <p>
 * 回归验证恶性 BUG 修复（详见 Changelog「竞技场遗迹感染系统恶性 BUG」）：
 * <ol>
 *   <li><b>Phase A 指南针模拟</b>：第三方结构查询（{@link Structure#generate}，
 *       即 Explorer's Compass 预览路径）对任意 chunk 调用 48 次后，
 *       {@code placed} 不得置位、感染不得启动——修复前任何一次 present
 *       即写入持久化数据并排队感染；</li>
 *   <li><b>Phase B 配置门控</b>：{@code arena ruin infection enabled} 默认关闭时，
 *       群系内感染调用零转化；</li>
 *   <li><b>Phase C 群系门控</b>：开启配置后，竞技场群系内感染生效并记录回滚数据、
 *       群系外不感染；</li>
 *   <li><b>Phase D 放置确认链</b>：{@code offerPendingPlacement} → 主线程确认器落库
 *       {@code placed}/{@code center}，二次 offer 不覆盖中心（只生成一次）；</li>
 *   <li><b>Phase E 感染退化</b>：{@code cureInfection} 停止感染、标记击败、
 *       回滚被感染方块、还原竞技场群系；击败后再次感染调用零转化。</li>
 * </ol>
 * 需要<b>非超平坦 + 开结构</b>测试世界（NoiseBasedChunkGenerator 才会走结构关门
 * 分支；地表需高于海平面才能通过候选点预检）——由
 * {@code PDPortingVerifyTest#needsNormalWorldWithStructures} 建档保证。
 * <p>
 * <b>不</b>并入默认 {@code all}；须 {@code PASTERDREAM_VERIFY_SUITES=arena-infection}。
 */
public final class PDArenaInfectionVerifyHooks {

    public record Result(boolean pass, String name, String detail) {
    }

    /** 指南针模拟的查询次数（对齐其单次搜索尝试的 48 chunk） */
    private static final int COMPASS_QUERIES = 48;
    /** 单次感染调用的最大转化数 */
    private static final int INFECT_CANDIDATES = 100;
    /** 小范围群系覆盖盒半径（保证感染源周围 6 格采样列全部落在 arena 群系内） */
    private static final int FILL_BOX_RADIUS = 8;
    /** 确认排水器的轮询间隔（与 PDAaroncosArenaWorldgen 一致）+ 余量 */
    private static final int DRAIN_TICKS = 30;
    /** 治愈后的回滚/还原等待 tick（群系还原 ~17t + 方块回滚分帧） */
    private static final int CURE_TICKS = 120;

    private PDArenaInfectionVerifyHooks() {
    }

    /**
     * 套件入口：五阶段顺序断言。
     *
     * @param server 集成服务器
     * @param player 测试玩家
     * @param out    结果回调
     */
    public static void verify(MinecraftServer server, ServerPlayer player, Consumer<Result> out) {
        if (server == null || player == null) {
            out.accept(new Result(false, "arena-infection-skip", "server/player null"));
            return;
        }
        ServerLevel ow = server.overworld();

        // ===== Phase A：第三方结构查询模拟（原恶性 BUG 触发路径回归） =====
        PDAaroncosArenaSpawnData spawnData = PDAaroncosArenaSpawnData.get(ow);
        if (spawnData.isPlaced()) {
            out.accept(new Result(false, "arena-infection-skip", "placed already true (dirty state)"));
            return;
        }
        simulateCompassQueries(server, ow);
        boolean placedAfterQuery = spawnData.isPlaced();
        out.accept(ok(!placedAfterQuery,
                "指南针式结构查询零副作用（placed 不置位）",
                "queries=" + COMPASS_QUERIES + " placed=" + placedAfterQuery));
        out.accept(ok(!ArenaRuinInfection.isActive(),
                "结构查询不启动遗迹感染",
                "active=" + ArenaRuinInfection.isActive()));

        // ===== 测试点准备：确定感染中心 A + 造竞技场群系 + 放置测试草方块 =====
        BlockPos centerA = surfaceAt(ow, player.blockPosition().offset(32, 0, 32));
        Optional<ResourceKey<Biome>> originalBiomeKey = ow.getBiome(centerA).unwrapKey();

        BlockPos grassA = prepareGrassColumn(ow, centerA);
        fillArenaBiomeBox(ow, centerA);

        boolean configWasEnabled = PDCommonConfig.ARENA_RUIN_INFECTION_ENABLED.get();
        try {
            // ===== Phase B：配置默认关闭 → 群系内感染调用零转化 =====
            PDCommonConfig.ARENA_RUIN_INFECTION_ENABLED.set(false);
            ArenaInfectionUtils.infectSurroundingBlocks(ow, centerA, 6, INFECT_CANDIDATES, ow.random);
            ArenaInfectionUtils.infectSurroundingBlocks(ow, centerA, 6, INFECT_CANDIDATES, ow.random);
            out.accept(ok(ow.getBlockState(grassA).is(Blocks.GRASS_BLOCK),
                    "配置关闭时群系内感染零转化",
                    "state=" + ow.getBlockState(grassA).getBlock()));

            // ===== Phase C：开启配置 → 群系内感染生效 + 群系外不感染 =====
            PDCommonConfig.ARENA_RUIN_INFECTION_ENABLED.set(true);
            ArenaInfectionUtils.infectSurroundingBlocks(ow, centerA, 6, INFECT_CANDIDATES, ow.random);
            ArenaInfectionUtils.infectSurroundingBlocks(ow, centerA, 6, INFECT_CANDIDATES, ow.random);
            boolean grassInfected = isInfectedGround(ow.getBlockState(grassA));
            boolean hasRecords = !PortalInfectionData.get(ow).getRecords(centerA).isEmpty();
            out.accept(ok(grassInfected && hasRecords,
                    "配置开启后竞技场群系内感染生效并记录回滚数据",
                    "grassInfected=" + grassInfected + " hasRecords=" + hasRecords));

            // 群系外对照点（centerA+64：感染半径/群系盒外，正常群系）
            BlockPos grassOutside = prepareGrassColumn(ow, surfaceAt(ow, centerA.offset(64, 0, 0)));
            ArenaInfectionUtils.infectSurroundingBlocks(ow, grassOutside, 6, INFECT_CANDIDATES, ow.random);
            out.accept(ok(ow.getBlockState(grassOutside).is(Blocks.GRASS_BLOCK),
                    "感染限制在竞技场群系内（群系外零转化）",
                    "outsideState=" + ow.getBlockState(grassOutside).getBlock()));

            // ===== Phase D：真实放置确认链（入队 → 主线程确认 → 只确认一次） =====
            PDAaroncosArenaWorldgen.offerPendingPlacement(centerA);
            ServerScheduler.advanceForTest(DRAIN_TICKS);
            boolean confirmed = spawnData.isPlaced() && centerA.equals(spawnData.getCenter());
            out.accept(ok(confirmed,
                    "放置确认链落库 placed/center",
                    "placed=" + spawnData.isPlaced()
                            + " center=" + (spawnData.getCenter() == null ? "null" : spawnData.getCenter().toShortString())));

            // 确认后配置开启时感染循环应已启动（confirm 分支 start）
            out.accept(ok(ArenaRuinInfection.isActive(),
                    "确认放置后遗迹感染循环启动",
                    "active=" + ArenaRuinInfection.isActive()));

            // 二次入队：placed 已置位 → 确认器短路，中心不得被覆盖（只生成一次）
            BlockPos secondOffer = centerA.offset(5, 0, 5);
            PDAaroncosArenaWorldgen.offerPendingPlacement(secondOffer);
            ServerScheduler.advanceForTest(DRAIN_TICKS);
            out.accept(ok(centerA.equals(spawnData.getCenter()),
                    "二次放置入队不覆盖中心（只生成一次）",
                    "center=" + spawnData.getCenter().toShortString()));

            // ===== Phase E：治愈流程（击败 BOSS / 配置关闭清理的统一路径） =====
            PDAaroncosArenaWorldgen.cureInfection(ow);
            out.accept(ok(!ArenaRuinInfection.isActive() && spawnData.isDefeated(),
                    "治愈后感染循环停止且标记击败",
                    "active=" + ArenaRuinInfection.isActive() + " defeated=" + spawnData.isDefeated()));

            // 等待分帧回滚方块 + 还原群系（advance 泵完所有排程任务）
            ServerScheduler.advanceForTest(CURE_TICKS);
            boolean grassRestored = ow.getBlockState(grassA).is(Blocks.GRASS_BLOCK);
            Optional<ResourceKey<Biome>> biomeAfterCure = ow.getBiome(centerA).unwrapKey();
            boolean biomeRestored = biomeAfterCure.isPresent()
                    && biomeAfterCure.equals(originalBiomeKey);
            out.accept(ok(grassRestored,
                    "治愈后感染方块回滚为原始状态",
                    "state=" + ow.getBlockState(grassA).getBlock()));
            out.accept(ok(biomeRestored,
                    "治愈后竞技场群系还原为原始群系（噪声重推导）",
                    "before=" + originalBiomeKey.map(k -> k.location().toString()).orElse("?")
                            + " after=" + biomeAfterCure.map(k -> k.location().toString()).orElse("?")));

            // 击败后再次感染调用零转化（defeated 门控）
            ArenaInfectionUtils.infectSurroundingBlocks(ow, centerA, 6, INFECT_CANDIDATES, ow.random);
            out.accept(ok(ow.getBlockState(grassA).is(Blocks.GRASS_BLOCK),
                    "击败后再次感染调用零转化（永久退化）",
                    "state=" + ow.getBlockState(grassA).getBlock()));
        } finally {
            // ===== Phase F：复位配置与测试现场 =====
            PDCommonConfig.ARENA_RUIN_INFECTION_ENABLED.set(configWasEnabled);
            ArenaRuinInfection.stop();
        }
    }

    // ==================== Phase A 辅助 ====================

    /**
     * 模拟第三方结构搜索工具（Explorer's Compass Enhance）对竞技场结构的查询：
     * 以真实服务器参数调用 {@link Structure#generate}（其第一行即
     * {@code findGenerationPoint}，与指南针 StructurePreviewBuilder 的反编译调用一致）。
     * <p>
     * 修复前：任意一次 present 候选即写入 {@code markPlaced()} 并排队感染；
     * 修复后：该方法为纯查询，断言由调用方检查 {@code placed} 与感染状态。
     *
     * @param server 集成服务器
     * @param ow     主世界
     */
    private static void simulateCompassQueries(MinecraftServer server, ServerLevel ow) {
        Structure structure = server.registryAccess()
                .registryOrThrow(Registries.STRUCTURE)
                .get(ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "aaroncos_arena_portals"));
        if (structure == null) {
            return;
        }
        StructureTemplateManager templateManager = ow.getStructureManager();
        Predicate<Holder<Biome>> validBiome = holder -> structure.biomes().contains(holder);
        long seed = server.getWorldData().worldGenOptions().seed();

        // 玩家附近 7x7 chunk 网格，共 48 次查询（对齐指南针单次搜索规模）
        ChunkPos baseChunk = new ChunkPos(ow.getSharedSpawnPos());
        int baseChunkX = baseChunk.x;
        int baseChunkZ = baseChunk.z;
        int called = 0;
        outer:
        for (int dx = -3; dx <= 3; dx++) {
            for (int dz = -3; dz <= 3; dz++) {
                if (called >= COMPASS_QUERIES) {
                    break outer;
                }
                ChunkPos chunkPos = new ChunkPos(baseChunkX + dx, baseChunkZ + dz);
                structure.generate(server.registryAccess(),
                        ow.getChunkSource().getGenerator(),
                        ow.getChunkSource().getGenerator().getBiomeSource(),
                        ow.getChunkSource().randomState(),
                        templateManager,
                        seed, chunkPos, 0, ow, validBiome);
                called++;
            }
        }
    }

    // ==================== 通用辅助 ====================

    /**
     * 取指定水平坐标的地表位置（最高非空方块）。
     * <p>
     * 先强制 FULL 状态加载目标 chunk：{@code Level.getHeight} 对不存在/未生成的
     * chunk 会直接返回世界底部（minY），导致选点越界（void_air）。
     *
     * @param ow 主世界
     * @param pos 任意参考位置（只取其 X/Z）
     * @return 该列地表方块位置
     */
    private static BlockPos surfaceAt(ServerLevel ow, BlockPos pos) {
        ow.getChunk(pos.getX() >> 4, pos.getZ() >> 4, ChunkStatus.FULL, true);
        int y = ow.getHeight(Heightmap.Types.WORLD_SURFACE, pos.getX(), pos.getZ());
        return new BlockPos(pos.getX(), y - 1, pos.getZ());
    }

    /**
     * 在地表准备一个孤立的草方块测试列：上方两格清空（满足 canInfect 的
     * {@code isEmptyBlock(above)} 前提），地表方块替换为草方块。
     *
     * @param ow  主世界
     * @param pos 地表位置
     * @return 草方块位置
     */
    private static BlockPos prepareGrassColumn(ServerLevel ow, BlockPos pos) {
        ow.setBlock(pos.above(), Blocks.AIR.defaultBlockState(), 3);
        ow.setBlock(pos.above(2), Blocks.AIR.defaultBlockState(), 3);
        ow.setBlock(pos, Blocks.GRASS_BLOCK.defaultBlockState(), 3);
        return pos;
    }

    /**
     * 以 centerA 为中心刷写小范围竞技场群系盒（保证感染半径 6 的采样列
     * 全部落在 arena 群系内）。临时放宽 fillbiome 体积限制，完成后还原。
     *
     * @param ow      主世界
     * @param centerA 感染中心
     */
    private static void fillArenaBiomeBox(ServerLevel ow, BlockPos centerA) {
        Holder<Biome> arenaBiome = ow.registryAccess().lookupOrThrow(Registries.BIOME)
                .getOrThrow(PDBiomes.AARONCOS_ARENA);
        BlockPos from = centerA.offset(-FILL_BOX_RADIUS, -FILL_BOX_RADIUS, -FILL_BOX_RADIUS);
        BlockPos to = centerA.offset(FILL_BOX_RADIUS, FILL_BOX_RADIUS, FILL_BOX_RADIUS);

        int originalLimit = ow.getGameRules().getInt(GameRules.RULE_COMMAND_MODIFICATION_BLOCK_LIMIT);
        ow.getGameRules().getRule(GameRules.RULE_COMMAND_MODIFICATION_BLOCK_LIMIT)
                .set(1_000_000, ow.getServer());
        FillBiomeCommand.fill(ow, from, to, arenaBiome);
        ow.getGameRules().getRule(GameRules.RULE_COMMAND_MODIFICATION_BLOCK_LIMIT)
                .set(originalLimit, ow.getServer());
    }

    /**
     * 判断方块是否为感染产物（草方块转化目标：阴影菌丝/阴影块/厚阴影块）。
     *
     * @param state 待检状态
     * @return true 若为感染转化后的地面方块
     */
    private static boolean isInfectedGround(BlockState state) {
        return state.is(PDBlocks.SHADOW_NYLIUM.get())
                || state.is(PDBlocks.SHADOW_BLOCK.get())
                || state.is(PDBlocks.THICK_SHADOW_BLOCK.get());
    }

    private static Result ok(boolean pass, String name, String detail) {
        return new Result(pass, name, detail);
    }
}
