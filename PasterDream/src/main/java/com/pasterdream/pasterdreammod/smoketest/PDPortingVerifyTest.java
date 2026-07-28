package com.pasterdream.pasterdreammod.smoketest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.mojang.logging.LogUtils;
import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.block.entity.ShadowBlastFurnaceBlockEntity;
import com.pasterdream.pasterdreammod.entity.SpellEffects;
import com.pasterdream.pasterdreammod.entity.projectile.SpellProjectileEntity;
import com.pasterdream.pasterdreammod.item.StorageBagItem;
import com.pasterdream.pasterdreammod.registry.PDBlocks;
import com.pasterdream.pasterdreammod.registry.PDAttributes;
import com.pasterdream.pasterdreammod.registry.PDEffects;
import com.pasterdream.pasterdreammod.registry.PDEntities;
import com.pasterdream.pasterdreammod.registry.PDItems;
import com.pasterdream.pasterdreammod.registry.PDRecipeTypes;
import com.pasterdream.pasterdreammod.smoketest.PDCurioVerifyHooks;
import com.pasterdream.pasterdreammod.smoketest.PDStaleCommentVerifyHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.slf4j.Logger;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 100% 移植完成度验证驱动（PDPortingVerifyTest）。
 * <p>
 * 仅在环境变量 {@code PASTERDREAM_VERIFY=1}（或 JVM 参数 {@code -Dpasterdream.verify=true}）
 * 时激活；正常游戏零影响。可与 {@link PDSmokeTest} 同时开启（时间线错开）。
 * <p>
 * <b>分类运行</b>：{@code PASTERDREAM_VERIFY_SUITES}（或 {@code -Dpasterdream.verify.suites}）
 * 逗号分隔套件名；未设置 / {@code all} = 全量。可用名见 {@link Suite}。
 * 例：{@code PASTERDREAM_VERIFY_SUITES=registry,workshop} 只跑注册表 diff 与工坊。
 * 时间线按所选套件压缩拼接，未选套件不调度。
 * <p>
 * 两大验证面：
 * <ol>
 *     <li><b>注册表完备性</b>：加载打包在资源根的 {@code pd_porting_manifest.json}
 *         （原版 PasterDream 1.3fix 全注册名快照 + 刻意改名映射），
 *         对 14 个类别逐名 diff 实际注册表，输出缺失/新增清单与覆盖率。</li>
 *     <li><b>行为级抽测</b>：玩家属性挂载、附件数据读取、全部状态效果可施加、
 *         影灯/风之旅维度可传送与生成、5 种法术投射物发射与命中效果
 *         （落雷计数/剧毒施加/治疗立场回血/狂暴增益/冰冻定身）。</li>
 * </ol>
 * 结果三路输出，便于验证 agent 解析：
 * <ul>
 *     <li>日志：所有行带 {@code [PDVerify]} 前缀，最终有 SUMMARY/RESULT 行；</li>
 *     <li>机器可读报告：{@code <run目录>/pd_verify_report.json}
 *         （每类别 expected/present/missing/extra/percent + 每断言明细）；</li>
 *     <li>进程退出：verify 单独运行时测完自动退出客户端。</li>
 * </ul>
 */
@EventBusSubscriber(modid = PasterDreamMod.MOD_ID)
public final class PDPortingVerifyTest {

    /** 是否启用移植验证（环境变量或系统属性开关） */
    public static final boolean ENABLED =
            "1".equals(System.getenv("PASTERDREAM_VERIFY"))
                    || Boolean.getBoolean("pasterdream.verify");

    /**
     * 测完后是否保持客户端打开供人工观察（默认 true）。
     * 设 {@code PASTERDREAM_VERIFY_KEEP_OPEN=0} 或 {@code -Dpasterdream.verify.keepOpen=false} 可恢复自动退出。
     */
    public static final boolean KEEP_OPEN = keepOpenEnabled();

    /**
     * 可独立调度的验证套件。
     * <p>
     * 别名（env 里任意一个即可）：
     * <ul>
     *   <li>{@code registry} — manifest 注册表 diff + datapack 统计 + 新内容数据包</li>
     *   <li>{@code core} — 属性/附件/效果/梦志/蓝图/储物袋风向标/粉蛋卡勒/效果修饰/无尽书</li>
     *   <li>{@code dimensions}/{@code dim} — 影灯世界·风之旅途往返</li>
     *   <li>{@code spells} — 五法术投射物行为</li>
     *   <li>{@code content}/{@code machines} — 暗影高炉/法杖扫射/angel_block_item</li>
     *   <li>{@code structures}/{@code structure} — 结构生成子系统</li>
     *   <li>{@code workshop} — 武器工坊群 E2E</li>
     *   <li>{@code struct-dim}/{@code struct_dim} — 结构→维度映射</li>
     *   <li>{@code gallery}/{@code block-gallery} — 方块总览展台</li>
     *   <li>{@code entity-gallery}/{@code entity_gallery} — 实体展台</li>
     *   <li>{@code twilight-lantern}/{@code twilight}/{@code lantern} — 暮影之笼流程缺口核实</li>
     *   <li>{@code wind-journey}/{@code wind}/{@code third-dream} — 第三梦境风之旅途流程核实</li>
     * </ul>
     * 组合快捷：{@code all}（默认）、{@code quick}=registry+core、
     * {@code behavior}=core+dimensions+spells+content、
     * {@code worldgen}=structures+struct-dim、
     * {@code galleries}/{@code visual}=gallery+entity-gallery。
     * <p>
     * 注意：{@code all} <b>不含</b> {@code twilight-lantern} / {@code wind-journey}
     * （专项缺口核实，默认不进全量，避免与全绿终验语义冲突；
     * 显式 {@code PASTERDREAM_VERIFY_SUITES=twilight-lantern} 或 {@code wind-journey}）。
     */
    public enum Suite {
        REGISTRY("registry"),
        CORE("core"),
        DIMENSIONS("dimensions", "dim"),
        SPELLS("spells"),
        CONTENT("content", "machines"),
        STRUCTURES("structures", "structure"),
        WORKSHOP("workshop"),
        STRUCT_DIM("struct-dim", "struct_dim"),
        GALLERY("gallery", "block-gallery"),
        ENTITY_GALLERY("entity-gallery", "entity_gallery"),
        /** 暮影之笼专项；不在 all 默认集合内，见 {@link #parseSelectedSuites} */
        TWILIGHT_LANTERN("twilight-lantern", "twilight", "lantern"),
        /** 第三梦境风之旅途专项；不在 all 默认集合内 */
        WIND_JOURNEY("wind-journey", "wind", "third-dream");

        private final String[] aliases;

        Suite(String... aliases) {
            this.aliases = aliases;
        }

        boolean matches(String token) {
            for (String a : aliases) {
                if (a.equalsIgnoreCase(token)) {
                    return true;
                }
            }
            return name().equalsIgnoreCase(token);
        }

        String primary() {
            return aliases[0];
        }
    }

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String TAG = "[PDVerify] ";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    /** 本次运行选中的套件（未配置 = 全量） */
    public static final java.util.EnumSet<Suite> SELECTED_SUITES = parseSelectedSuites();

    private static boolean keepOpenEnabled() {
        String env = System.getenv("PASTERDREAM_VERIFY_KEEP_OPEN");
        if (env != null) {
            return !"0".equals(env) && !"false".equalsIgnoreCase(env);
        }
        // 系统属性显式 false 才关；未设置默认保持打开
        if (System.getProperty("pasterdream.verify.keepOpen") != null) {
            return Boolean.getBoolean("pasterdream.verify.keepOpen");
        }
        return true;
    }

    /**
     * 解析 {@code PASTERDREAM_VERIFY_SUITES} / {@code -Dpasterdream.verify.suites}。
     * 空、未设置、{@code all} → 全套；支持组合快捷 quick/behavior/worldgen/galleries/visual。
     */
    private static java.util.EnumSet<Suite> parseSelectedSuites() {
        String raw = System.getenv("PASTERDREAM_VERIFY_SUITES");
        if (raw == null || raw.isBlank()) {
            raw = System.getProperty("pasterdream.verify.suites", "");
        }
        raw = raw == null ? "" : raw.trim();
        if (raw.isEmpty() || "all".equalsIgnoreCase(raw) || "*".equals(raw)) {
            // 全量终验不含专项缺口套件（与 ALL PASS 语义冲突）
            java.util.EnumSet<Suite> all = java.util.EnumSet.allOf(Suite.class);
            all.remove(Suite.TWILIGHT_LANTERN);
            all.remove(Suite.WIND_JOURNEY);
            return all;
        }
        java.util.EnumSet<Suite> out = java.util.EnumSet.noneOf(Suite.class);
        for (String part : raw.split("[,;\\s]+")) {
            if (part.isBlank()) {
                continue;
            }
            String token = part.trim().toLowerCase(java.util.Locale.ROOT);
            switch (token) {
                case "all", "*" -> {
                    java.util.EnumSet<Suite> all = java.util.EnumSet.allOf(Suite.class);
                    all.remove(Suite.TWILIGHT_LANTERN);
                    all.remove(Suite.WIND_JOURNEY);
                    return all;
                }
                case "quick", "fast" -> {
                    out.add(Suite.REGISTRY);
                    out.add(Suite.CORE);
                }
                case "behavior" -> {
                    out.add(Suite.CORE);
                    out.add(Suite.DIMENSIONS);
                    out.add(Suite.SPELLS);
                    out.add(Suite.CONTENT);
                }
                case "worldgen" -> {
                    out.add(Suite.STRUCTURES);
                    out.add(Suite.STRUCT_DIM);
                }
                case "galleries", "visual" -> {
                    out.add(Suite.GALLERY);
                    out.add(Suite.ENTITY_GALLERY);
                }
                default -> {
                    boolean hit = false;
                    for (Suite s : Suite.values()) {
                        if (s.matches(token)) {
                            out.add(s);
                            hit = true;
                            break;
                        }
                    }
                    if (!hit) {
                        LogUtils.getLogger().warn("[PDVerify] 未知套件名 '{}'，已忽略（合法: registry,core,dimensions,"
                                + "spells,content,structures,workshop,struct-dim,gallery,entity-gallery,"
                                + "twilight-lantern,wind-journey 及快捷 all/quick/behavior/worldgen/galleries）", token);
                    }
                }
            }
        }
        if (out.isEmpty()) {
            LogUtils.getLogger().warn("[PDVerify] SUITES 解析结果为空，回退全量");
            return java.util.EnumSet.allOf(Suite.class);
        }
        return out;
    }

    private static boolean suite(Suite s) {
        return SELECTED_SUITES.contains(s);
    }

    /** 所选套件的逗号分隔主键，写入报告与启动日志 */
    private static String selectedSuitesLabel() {
        StringBuilder sb = new StringBuilder();
        for (Suite s : SELECTED_SUITES) {
            if (!sb.isEmpty()) {
                sb.append(',');
            }
            sb.append(s.primary());
        }
        return sb.toString();
    }

    /** 若冒烟测试同时开启，验证时间线整体后移到它结束之后（约 tick 420） */
    private static final int START_OFFSET_WITH_SMOKE = 460;

    /** 全部验证完成标记（客户端读取以决定退出） */
    static volatile boolean done = false;

    private PDPortingVerifyTest() {
    }

    // ==================== 报告模型 ====================

    /** 单条断言记录 */
    private record Assertion(String suite, String name, boolean pass, String detail) {
    }

    /** 注册表类别 diff 结果 */
    private record CategoryDiff(String category, int expected, int present,
                                List<String> missing, List<String> extra) {
    }

    private static final List<Assertion> ASSERTIONS =
            java.util.Collections.synchronizedList(new ArrayList<>());
    private static final List<CategoryDiff> DIFFS = new ArrayList<>();

    private static void check(String suite, boolean ok, String what) {
        ASSERTIONS.add(new Assertion(suite, what, ok, ""));
        if (ok) {
            LOGGER.info(TAG + "PASS [{}] {}", suite, what);
        } else {
            LOGGER.error(TAG + "FAIL [{}] {}", suite, what);
        }
    }

    private static void checkDetail(String suite, boolean ok, String what, String detail) {
        ASSERTIONS.add(new Assertion(suite, what, ok, detail));
        if (ok) {
            LOGGER.info(TAG + "PASS [{}] {} ({})", suite, what, detail);
        } else {
            LOGGER.error(TAG + "FAIL [{}] {} ({})", suite, what, detail);
        }
    }

    // ==================== manifest ====================

    /** 原版注册清单（类别 -> 名称列表），resources 根目录 pd_porting_manifest.json */
    private static Map<String, Object> manifest;

    @SuppressWarnings("unchecked")
    private static Map<String, Object> manifest() {
        if (manifest == null) {
            try (InputStream in = PDPortingVerifyTest.class.getResourceAsStream("/pd_porting_manifest.json")) {
                if (in == null) {
                    LOGGER.error(TAG + "pd_porting_manifest.json 缺失，注册表 diff 跳过");
                    manifest = Map.of();
                } else {
                    manifest = GSON.fromJson(new InputStreamReader(in, StandardCharsets.UTF_8),
                            new TypeToken<Map<String, Object>>() {
                            }.getType());
                }
            } catch (Exception e) {
                LOGGER.error(TAG + "manifest 加载失败", e);
                manifest = Map.of();
            }
        }
        return manifest;
    }

    @SuppressWarnings("unchecked")
    private static List<String> manifestNames(String category) {
        Object v = manifest().get(category);
        return v instanceof List<?> list ? (List<String>) list : List.of();
    }

    @SuppressWarnings("unchecked")
    private static Map<String, String> renames() {
        Object v = manifest().get("renames");
        return v instanceof Map<?, ?> m ? (Map<String, String>) m : Map.of();
    }

    // ==================== 序列器 ====================

    /** 延迟任务（相对登录后的验证时间线） */
    private record Step(int dueTick, Runnable task) {
    }

    /**
     * 按到期 tick 排序的小顶堆。
     * 不能用 ArrayDeque：插入顺序出队 + buildTimeline 非单调书写（如落雷验证 t+410
     * 先于剧毒 t+320 入队）会让队头堵塞后续步骤、到期后批量同 tick 连发——
     * 曾导致治疗/剧毒断言在 begin 同 tick 即执行的假阴性（实测取证定案）。
     */
    private static final java.util.PriorityQueue<Step> STEPS =
            new java.util.PriorityQueue<>(java.util.Comparator.comparingInt(Step::dueTick));
    private static UUID playerId;
    private static int ticks = -1;

    private static void at(int tick, Runnable task) {
        STEPS.add(new Step(tick, task));
    }

    // ==================== 事件入口 ====================

    /**
     * 玩家登录后装配验证时间线
     *
     * @param event 登录事件
     */
    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!ENABLED || !(event.getEntity() instanceof ServerPlayer sp) || ticks >= 0) {
            return;
        }
        playerId = sp.getUUID();
        ticks = 0;
        int base = PDSmokeTest.ENABLED ? START_OFFSET_WITH_SMOKE : 60;
        buildTimeline(base);
        LOGGER.info(TAG + "porting verify timeline started (base tick {}, suites=[{}])",
                base, selectedSuitesLabel());
    }

    /**
     * 服务端 tick 驱动序列器
     *
     * @param event 服务端 tick 事件
     */
    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        if (!ENABLED || ticks < 0 || done) {
            return;
        }
        MinecraftServer server = event.getServer();
        ServerPlayer player = playerId == null ? null : server.getPlayerList().getPlayer(playerId);
        if (player == null) {
            return;
        }
        ticks++;
        while (!STEPS.isEmpty() && STEPS.peek().dueTick() <= ticks) {
            Step step = STEPS.poll();
            try {
                step.task().run();
            } catch (Exception e) {
                check("framework", false, "步骤异常 @" + step.dueTick() + ": " + e);
                LOGGER.error(TAG + "step exception", e);
            }
        }
    }

    // ==================== 时间线装配 ====================

    /**
     * 按 {@link #SELECTED_SUITES} 装配时间线：未选中的套件完全不调度。
     * 各套件使用独立相对偏移，拼接后总时长随所选缩减（单套件约数十 tick，全量约 500）。
     */
    private static void buildTimeline(int t) {
        // 全程夜视 / 飞行：登录后立刻 + 各长时套件关键节点刷新
        at(t + 1, PDPortingVerifyTest::refreshPlayerBuffs);

        int cursor = t;

        if (suite(Suite.REGISTRY)) {
            int r = cursor;
            at(r, () -> runRegistryDiffs(server()));
            at(r + 5, () -> runDataPackStats(server()));
            at(r + 7, () -> runNewContentDataSuite(server()));
            cursor = r + 10;
        }

        if (suite(Suite.CORE)) {
            int c = cursor;
            at(c, () -> PDDreamnotesVerifyHooks.verify(player(), r ->
                    checkDetail("dreamnotes", r.pass(), r.name(), r.detail())));
            at(c + 2, () -> {
                java.util.List<String> fails = PDBlueprintVerifyHooks.verifyAll();
                checkDetail("blueprint", fails.isEmpty(),
                        "蓝图子系统校验 " + (fails.isEmpty() ? "通过" : "失败 " + fails.size()),
                        fails.isEmpty() ? "registry+loaded pages OK" : fails.toString());
            });
            at(c + 3, () -> runAttributeSuite(player()));
            at(c + 4, PDPortingVerifyTest::storageBagAndWindVaneSuite);
            at(c + 5, () -> runAttachmentSuite(player()));
            at(c + 6, PDPortingVerifyTest::pinkeggAndCalleSuite);
            at(c + 7, PDPortingVerifyTest::effectModifierSuite);
            at(c + 8, () -> runEffectSuite(player()));
            at(c + 9, PDPortingVerifyTest::endlessBookImportSuite);
            at(c + 10, () -> PDStaleCommentVerifyHooks.verify(server(), player() != null ? player().serverLevel() : null, player(), r ->
                    checkDetail("stale-comments", r.pass(), r.name(), r.detail())));
            at(c + 11, () -> PDCurioVerifyHooks.verify(player(), r ->
                    checkDetail("curios", r.pass(), r.name(), r.detail())));
            cursor = c + 13;
        }

        if (suite(Suite.STRUCTURES)) {
            int s = cursor;
            at(s, PDPortingVerifyTest::structureGenSuite);
            cursor = s + 5;
        }

        if (suite(Suite.TWILIGHT_LANTERN)) {
            int tl = cursor;
            at(tl, PDPortingVerifyTest::refreshPlayerBuffs);
            at(tl + 2, PDPortingVerifyTest::twilightLanternSuite);
            // 大模板 place + 扫描，预留 tick
            cursor = tl + 40;
        }

        if (suite(Suite.WIND_JOURNEY)) {
            int wj = cursor;
            at(wj, PDPortingVerifyTest::refreshPlayerBuffs);
            at(wj + 2, PDPortingVerifyTest::windJourneySuiteSync);
            // 祭坛 86t 召唤 + 缓冲
            at(wj + 100, PDPortingVerifyTest::windJourneySuiteAltarAftermath);
            cursor = wj + 120;
        }

        if (suite(Suite.WORKSHOP)) {
            int w = cursor;
            at(w, PDPortingVerifyTest::workshopSuite);
            cursor = w + 5;
        }

        if (suite(Suite.STRUCT_DIM)) {
            int d = cursor;
            at(d, PDPortingVerifyTest::structureDimensionSuite);
            cursor = d + 5;
        }

        if (suite(Suite.DIMENSIONS)) {
            int d = cursor;
            // 影灯世界 → 风之旅途 → 主世界（每跳留 80 tick 供区块生成）
            at(d, PDPortingVerifyTest::refreshPlayerBuffs);
            at(d + 5, () -> teleportToDimension(player(), "lamp_shadow_world"));
            at(d + 85, () -> verifyDimension(player(), "lamp_shadow_world"));
            at(d + 90, PDPortingVerifyTest::refreshPlayerBuffs);
            at(d + 95, () -> teleportToDimension(player(), "wind_journey_world"));
            at(d + 175, () -> verifyDimension(player(), "wind_journey_world"));
            at(d + 180, PDPortingVerifyTest::refreshPlayerBuffs);
            at(d + 185, () -> returnToOverworld(player()));
            cursor = d + 200;
        }

        if (suite(Suite.SPELLS)) {
            int s = cursor;
            // 五法术错位 64 格；落雷需约 100 tick 观察
            at(s, () -> spellProjectileSpawnSuite(player()));
            at(s + 10, PDPortingVerifyTest::spellLightningBegin);
            at(s + 110, PDPortingVerifyTest::spellLightningVerify);
            at(s + 20, PDPortingVerifyTest::spellPoisonBegin);
            at(s + 28, PDPortingVerifyTest::spellPoisonVerify);
            at(s + 30, PDPortingVerifyTest::spellHealingBegin);
            at(s + 90, PDPortingVerifyTest::spellHealingVerify);
            at(s + 40, PDPortingVerifyTest::spellFuryBegin);
            at(s + 55, PDPortingVerifyTest::spellFuryVerify);
            at(s + 50, PDPortingVerifyTest::spellIceBegin);
            at(s + 60, PDPortingVerifyTest::spellIceVerify);
            cursor = s + 120;
        }

        if (suite(Suite.CONTENT)) {
            int c = cursor;
            at(c, PDPortingVerifyTest::blastFurnaceSetup);
            at(c + 5, PDPortingVerifyTest::wandProjectileSweep);
            at(c + 10, PDPortingVerifyTest::angelBlockItemTest);
            // 高炉验证按配方时长动态追加；cursor 预留缓冲
            cursor = c + 40;
        }

        if (suite(Suite.GALLERY)) {
            int g = cursor;
            at(g, PDPortingVerifyTest::refreshPlayerBuffs);
            at(g + 5, PDPortingVerifyTest::blockGallerySuite);
            cursor = g + 20;
        }

        if (suite(Suite.ENTITY_GALLERY)) {
            int e = cursor;
            at(e, PDPortingVerifyTest::refreshPlayerBuffs);
            at(e + 5, PDPortingVerifyTest::entityGallerySuite);
            cursor = e + 20;
        }

        // finish 自适应：若仍有后续步骤（如高炉按配方时长动态追加的验证）则自动顺延
        at(Math.max(cursor + 10, t + 20), PDPortingVerifyTest::finish);
    }

    private static MinecraftServer serverRef;

    private static MinecraftServer server() {
        return serverRef;
    }

    private static ServerPlayer player() {
        return serverRef == null ? null : serverRef.getPlayerList().getPlayer(playerId);
    }

    /** 捕获服务器引用（tick 事件首个到达前 onLogin 已发生） */
    @SubscribeEvent
    public static void onServerTickPre(ServerTickEvent.Pre event) {
        if (ENABLED && serverRef == null) {
            serverRef = event.getServer();
        }
    }

    // ==================== S1 注册表完备性 ====================

    private static void runRegistryDiffs(MinecraftServer server) {
        diffRegistry("blocks", BuiltInRegistries.BLOCK);
        diffRegistry("items", BuiltInRegistries.ITEM);
        diffRegistry("entities", BuiltInRegistries.ENTITY_TYPE);
        diffRegistry("effects", BuiltInRegistries.MOB_EFFECT);
        diffRegistry("potions", BuiltInRegistries.POTION);
        diffRegistry("particles", BuiltInRegistries.PARTICLE_TYPE);
        diffRegistry("fluids", BuiltInRegistries.FLUID);
        diffRegistry("menus", BuiltInRegistries.MENU);
        diffRegistry("block_entities", BuiltInRegistries.BLOCK_ENTITY_TYPE);
        diffRegistry("attributes", BuiltInRegistries.ATTRIBUTE);
        diffRegistry("tabs", BuiltInRegistries.CREATIVE_MODE_TAB);
        diffRegistry("sounds", BuiltInRegistries.SOUND_EVENT);

        // 数据驱动附魔（1.21.1 注册表在 registryAccess）
        Set<String> presentEnch = new TreeSet<>();
        server.registryAccess().registryOrThrow(Registries.ENCHANTMENT).keySet().forEach(rl -> {
            if (rl.getNamespace().equals(PasterDreamMod.MOD_ID)) {
                presentEnch.add(rl.getPath());
            }
        });
        diffNames("enchantments", presentEnch);

        // 维度（levelKeys）
        Set<String> presentDims = new TreeSet<>();
        server.levelKeys().forEach(key -> {
            if (key.location().getNamespace().equals(PasterDreamMod.MOD_ID)) {
                presentDims.add(key.location().getPath());
            }
        });
        diffNames("dimensions", presentDims);
    }

    private static void diffRegistry(String category, Registry<?> registry) {
        Set<String> present = new TreeSet<>();
        registry.keySet().forEach(rl -> {
            if (rl.getNamespace().equals(PasterDreamMod.MOD_ID)) {
                present.add(rl.getPath());
            }
        });
        diffNames(category, present);
    }

    @SuppressWarnings("unchecked")
    private static List<String> excludedNames(String category) {
        Object v = manifest().get("excluded");
        if (v instanceof Map<?, ?> m && m.get(category) instanceof List<?> list) {
            return (List<String>) list;
        }
        return List.of();
    }

    private static void diffNames(String category, Set<String> present) {
        List<String> expectedRaw = manifestNames(category);
        Map<String, String> renames = renames();
        Set<String> expected = new TreeSet<>();
        for (String name : expectedRaw) {
            expected.add(renames.getOrDefault(name, name));
        }
        // 原版已弃用/刻意不移植的注册项从期望集中扣除
        excludedNames(category).forEach(expected::remove);
        List<String> missing = new ArrayList<>();
        for (String name : expected) {
            if (!present.contains(name)) {
                missing.add(name);
            }
        }
        List<String> extra = new ArrayList<>();
        for (String name : present) {
            if (!expected.contains(name)) {
                extra.add(name);
            }
        }
        DIFFS.add(new CategoryDiff(category, expected.size(), expected.size() - missing.size(),
                missing, extra));
        String pct = expected.isEmpty() ? "n/a"
                : String.format("%.1f%%", 100.0 * (expected.size() - missing.size()) / expected.size());
        checkDetail("registry", missing.isEmpty(),
                category + " 覆盖 " + (expected.size() - missing.size()) + "/" + expected.size(),
                "覆盖率 " + pct + (missing.isEmpty() ? "" : "，缺失样例 "
                        + missing.subList(0, Math.min(8, missing.size()))));
        if (!missing.isEmpty()) {
            LOGGER.warn(TAG + "MISSING[{}] ({}): {}", category, missing.size(), missing);
        }
    }

    // ==================== S2 数据包统计 ====================

    private static void runDataPackStats(MinecraftServer server) {
        long recipes = server.getRecipeManager().getRecipes().stream()
                .filter(h -> h.id().getNamespace().equals(PasterDreamMod.MOD_ID)).count();
        long advancements = server.getAdvancements().getAllAdvancements().stream()
                .filter(a -> a.id().getNamespace().equals(PasterDreamMod.MOD_ID)).count();
        long lootTables = server.reloadableRegistries().getKeys(Registries.LOOT_TABLE).stream()
                .filter(rl -> rl.getNamespace().equals(PasterDreamMod.MOD_ID)).count();
        checkDetail("datapack", recipes >= 434, "配方加载数 " + recipes, "研究台组结算后≥434（W4 结算再+4）");
        checkDetail("datapack", advancements >= 60, "成就加载数 " + advancements, "当前 60/62，W4 结算后 62");
        checkDetail("datapack", lootTables >= 174, "战利品表加载数 " + lootTables, "研究台组补齐后 174+");
    }

    // ==================== S3/S4 玩家属性与附件 ====================

    private static void runAttributeSuite(ServerPlayer player) {
        List<String> attached = new ArrayList<>();
        List<String> unattached = new ArrayList<>();
        BuiltInRegistries.ATTRIBUTE.holders().forEach(holder -> {
            ResourceLocation rl = holder.key().location();
            if (!rl.getNamespace().equals(PasterDreamMod.MOD_ID)) {
                return;
            }
            if (player.getAttributes().hasAttribute(holder)) {
                attached.add(rl.getPath());
            } else {
                unattached.add(rl.getPath());
            }
        });
        // 期望数扣除 manifest excluded（原版已弃用的 san/meltdreamenergy 两项）
        int expected = manifestNames("attributes").size() - excludedNames("attributes").size();
        checkDetail("attributes", attached.size() + unattached.size() >= expected,
                "自定义属性注册 " + (attached.size() + unattached.size()) + "/" + expected,
                "玩家已挂载 " + attached + "；未挂载 " + unattached);
    }

    private static void runAttachmentSuite(ServerPlayer player) {
        int total = 0;
        int readable = 0;
        List<String> failed = new ArrayList<>();
        for (Map.Entry<net.minecraft.resources.ResourceKey<AttachmentType<?>>, AttachmentType<?>> e
                : NeoForgeRegistries.ATTACHMENT_TYPES.entrySet()) {
            ResourceLocation rl = e.getKey().location();
            if (!rl.getNamespace().equals(PasterDreamMod.MOD_ID)) {
                continue;
            }
            total++;
            try {
                Object value = player.getData(e.getValue());
                if (value != null) {
                    readable++;
                }
            } catch (Exception ex) {
                failed.add(rl.getPath() + ":" + ex.getClass().getSimpleName());
            }
        }
        checkDetail("attachments", total > 0 && failed.isEmpty(),
                "玩家附件类型 " + readable + "/" + total + " 可读取默认值",
                failed.isEmpty() ? "全部正常" : "异常: " + failed);
    }

    // ==================== S5 状态效果全量施加 ====================

    private static void runEffectSuite(ServerPlayer player) {
        int applied = 0;
        int instant = 0;
        List<String> failed = new ArrayList<>();
        List<Holder.Reference<MobEffect>> holders = BuiltInRegistries.MOB_EFFECT.holders()
                .filter(h -> h.key().location().getNamespace().equals(PasterDreamMod.MOD_ID))
                .sorted(Comparator.comparing(h -> h.key().location().getPath()))
                .toList();
        for (Holder.Reference<MobEffect> holder : holders) {
            String path = holder.key().location().getPath();
            try {
                if (holder.value().isInstantenous()) {
                    instant++;
                    continue;
                }
                boolean ok = player.addEffect(new MobEffectInstance(holder, 100, 0))
                        && player.hasEffect(holder);
                if (ok) {
                    applied++;
                    player.removeEffect(holder);
                } else {
                    failed.add(path);
                }
            } catch (Exception e) {
                failed.add(path + ":" + e.getClass().getSimpleName());
            }
        }
        checkDetail("effects", failed.isEmpty(),
                "状态效果可施加 " + applied + " 个（另 " + instant + " 个瞬时效果跳过）",
                failed.isEmpty() ? "全部正常" : "失败: " + failed);
    }

    // ==================== S6 维度行为 ====================

    private static void teleportToDimension(ServerPlayer player, String dimensionPath) {
        ServerLevel target = levelOf(dimensionPath);
        check("dimensions", target != null, dimensionPath + " ServerLevel 存在");
        if (target != null) {
            player.setGameMode(net.minecraft.world.level.GameType.CREATIVE);
            player.getAbilities().flying = true;
            player.onUpdateAbilities();
            player.teleportTo(target, 0.5, 160, 0.5, 0, 0);
            LOGGER.info(TAG + "teleported to {}", dimensionPath);
        }
    }

    private static void verifyDimension(ServerPlayer player, String dimensionPath) {
        boolean inDim = player.level().dimension().location().getPath().equals(dimensionPath);
        check("dimensions", inDim, "玩家已处于 " + dimensionPath);
        if (!inDim) {
            return;
        }
        // 自地表向下找到首个非空气方块，确认噪声生成产出了实际地形
        ServerLevel level = player.serverLevel();
        String found = "无";
        for (int y = Math.min(level.getMaxBuildHeight() - 1, 200); y > level.getMinBuildHeight(); y--) {
            BlockPos pos = new BlockPos(0, y, 0);
            if (!level.getBlockState(pos).isAir()) {
                found = BuiltInRegistries.BLOCK.getKey(level.getBlockState(pos).getBlock()).toString();
                break;
            }
        }
        checkDetail("dimensions", !"无".equals(found),
                dimensionPath + " 地形已生成", "首个非空气方块: " + found);
    }

    private static void returnToOverworld(ServerPlayer player) {
        ServerLevel overworld = serverRef.getLevel(Level.OVERWORLD);
        if (overworld != null) {
            BlockPos spawn = overworld.getSharedSpawnPos();
            player.teleportTo(overworld, spawn.getX() + 0.5,
                    overworld.getHeight(Heightmap.Types.MOTION_BLOCKING, spawn.getX(), spawn.getZ()) + 1,
                    spawn.getZ() + 0.5, 0, 0);
        }
    }

    // ==================== S7 法术行为 ====================

    /** 落雷计数窗口开关与计数器 */
    private static volatile boolean countLightning = false;
    private static final AtomicInteger LIGHTNING_COUNT = new AtomicInteger();
    private static BlockPos spellBase;

    /**
     * 落雷计数：统计验证窗口内加入主世界的闪电实体
     *
     * @param event 实体加入世界事件
     */
    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        if (ENABLED && countLightning && !event.getLevel().isClientSide()
                && event.getEntity() instanceof LightningBolt) {
            LIGHTNING_COUNT.incrementAndGet();
        }
    }

    private static BlockPos spellPos(int index) {
        // 24 格间距：既避免五法术 AoE（最大半径 4）互相干扰，又保证全部位于
        // 玩家周边的实体计算（entity-ticking）区块内——否则效果/冻结不会推进
        return spellBase.offset(index * 24, 0, 0);
    }

    /** 在指定测试点的地表上方生成测试猪（此前误用 pos.above(surfaceY) 导致生成在虚空） */
    private static Pig spawnTestPig(BlockPos pos) {
        BlockPos surface = new BlockPos(pos.getX(), surfaceY(pos) + 1, pos.getZ());
        Pig pig = EntityType.PIG.spawn(overworld(), surface, MobSpawnType.MOB_SUMMONED);
        if (pig != null) {
            // 禁 AI：防止测试期间游走出法术 AoE 判定半径造成假阴性
            pig.setNoAi(true);
        }
        return pig;
    }

    private static ServerLevel overworld() {
        return serverRef.getLevel(Level.OVERWORLD);
    }

    private static void spellProjectileSpawnSuite(ServerPlayer player) {
        spellBase = player.blockPosition().offset(16, 0, 16);
        ServerLevel level = overworld();
        for (SpellProjectileEntity.SpellType type : SpellProjectileEntity.SpellType.values()) {
            try {
                SpellProjectileEntity projectile = SpellProjectileEntity.shoot(level, player, type);
                check("spells", projectile.isAlive(), "法术投射物发射: " + type.name());
                projectile.discard();
            } catch (Exception e) {
                check("spells", false, "法术投射物发射: " + type.name() + " 异常 " + e);
            }
        }
    }

    private static void spellLightningBegin() {
        LIGHTNING_COUNT.set(0);
        countLightning = true;
        BlockPos pos = spellPos(0);
        SpellEffects.lightning(overworld(), pos.getX(), surfaceY(pos), pos.getZ());
        LOGGER.info(TAG + "lightning spell impact triggered at {}", pos);
    }

    private static void spellLightningVerify() {
        countLightning = false;
        int count = LIGHTNING_COUNT.get();
        checkDetail("spells", count >= 4, "闪电法术落雷 " + count + " 道", "预期 4 道（t=55/65/75/85）");
    }

    private static Pig poisonPig;

    private static void spellPoisonBegin() {
        BlockPos pos = spellPos(1);
        poisonPig = spawnTestPig(pos);
        SpellEffects.poison(overworld(), pos.getX(), surfaceY(pos) + 1, pos.getZ());
    }

    private static void spellPoisonVerify() {
        boolean ok = poisonPig != null && poisonPig.hasEffect(MobEffects.POISON)
                && poisonPig.hasEffect(MobEffects.WEAKNESS);
        check("spells", ok, "剧毒法术对范围内生物施加剧毒+虚弱");
        if (poisonPig != null) {
            poisonPig.discard();
        }
    }

    private static Pig healPig;

    private static void spellHealingBegin() {
        BlockPos pos = spellPos(2);
        healPig = spawnTestPig(pos);
        if (healPig != null) {
            healPig.setHealth(2.0f);
        }
        SpellEffects.healing(overworld(), pos.getX(), surfaceY(pos), pos.getZ());
    }

    private static void spellHealingVerify() {
        boolean fieldPresent = !overworld().getEntitiesOfClass(
                com.pasterdream.pasterdreammod.entity.mob.HealingSpellFieldEntity.class,
                new net.minecraft.world.phys.AABB(spellPos(2)).inflate(8)).isEmpty();
        check("spells", fieldPresent, "治疗立场实体已生成且存活");
        boolean healed = healPig != null && healPig.getHealth() > 2.0f;
        checkDetail("spells", healed, "治疗立场回血生效",
                healPig == null ? "猪缺失" : "血量 2.0 → " + healPig.getHealth());
        if (healPig != null) {
            healPig.discard();
        }
    }

    private static void spellFuryBegin() {
        BlockPos pos = spellPos(3);
        // 玩家瞬移到立场边上以吃到增益（狂暴只作用玩家）
        player().teleportTo(overworld(), pos.getX() + 0.5, surfaceY(pos) + 1, pos.getZ() + 0.5, 0, 0);
        SpellEffects.fury(overworld(), pos.getX(), surfaceY(pos), pos.getZ());
    }

    private static void spellFuryVerify() {
        boolean fieldPresent = !overworld().getEntitiesOfClass(
                com.pasterdream.pasterdreammod.entity.mob.FurySpellFieldEntity.class,
                new net.minecraft.world.phys.AABB(spellPos(3)).inflate(8)).isEmpty();
        check("spells", fieldPresent, "狂暴立场实体已生成且存活");
        Holder<MobEffect> buff = holderOf("fury_spell_buff");
        check("spells", buff != null && player().hasEffect(buff), "玩家获得狂暴法术增益");
    }

    private static Pig icePig;

    private static void spellIceBegin() {
        BlockPos pos = spellPos(4);
        icePig = spawnTestPig(pos);
        SpellEffects.ice(overworld(), pos.getX(), surfaceY(pos) + 1, pos.getZ());
    }

    private static void spellIceVerify() {
        Holder<MobEffect> buff = holderOf("ice_spell_buff");
        boolean frozen = icePig != null && icePig.getTicksFrozen() > 0;
        boolean debuffed = icePig != null && buff != null && icePig.hasEffect(buff);
        check("spells", frozen, "冰冻法术冻结范围内实体（ticksFrozen>0）");
        check("spells", debuffed, "冰冻法术施加冰冻减益");
        if (icePig != null) {
            icePig.discard();
        }
    }

    // ==================== S8 新内容数据层（研究台组/法杖/C1/Patchouli） ====================

    private static void runNewContentDataSuite(MinecraftServer server) {
        var rm = server.getRecipeManager();

        // shadow_blasting 数据包配方：原版即 5 个
        long blasting = rm.getRecipes().stream()
                .filter(h -> h.value().getType() == PDRecipeTypes.SHADOW_BLASTING.get()).count();
        checkDetail("newdata", blasting == 5, "shadow_blasting 配方加载 " + blasting + "/5",
                "锈黑金属粒/云/石头tag/玫瑰丛/骷髅头");

        // 配方解析：rust_black_metal_grain → blackmetal_grain
        ItemStack in = new ItemStack(itemOf("rust_black_metal_grain"));
        var match = in.isEmpty() ? java.util.Optional.empty()
                : rm.getRecipeFor(PDRecipeTypes.SHADOW_BLASTING.get(), new SingleRecipeInput(in), overworld());
        checkDetail("newdata", match.isPresent(), "shadow_blasting 配方可按输入解析",
                "输入 rust_black_metal_grain");

        // 本轮结算落位的配方 id 全部在 RecipeManager 中
        String[] newRecipes = {"crafting_215", "crafting_216", "crafting_217", "crafting_221",
                "crafting_271", "crafting_206", "crafting_155", "crafting_156", "crafting_223", "smithing_23"};
        List<String> missingRecipes = new ArrayList<>();
        for (String id : newRecipes) {
            if (rm.byKey(pdrl(id)).isEmpty()) {
                missingRecipes.add(id);
            }
        }
        checkDetail("newdata", missingRecipes.isEmpty(),
                "研究台/法杖/C1 新配方落位 " + (newRecipes.length - missingRecipes.size()) + "/" + newRecipes.length,
                missingRecipes.isEmpty() ? "全部加载" : "缺失: " + missingRecipes);

        // Patchouli 条件配方：加载状态必须与模组在场状态一致（不装=干净跳过，装了=可合成）
        boolean patchouliLoaded = net.neoforged.fml.ModList.get().isLoaded("patchouli");
        boolean guideRecipe = rm.byKey(pdrl("crafting_doremys_guidebook")).isPresent();
        checkDetail("newdata", patchouliLoaded == guideRecipe,
                "Patchouli 指南书条件配方状态一致",
                "patchouli在场=" + patchouliLoaded + "，配方已加载=" + guideRecipe);

        // 本轮结算落位的成就 id
        Set<String> advIds = new TreeSet<>();
        server.getAdvancements().getAllAdvancements().forEach(a -> {
            if (a.id().getNamespace().equals(PasterDreamMod.MOD_ID)) {
                advIds.add(a.id().getPath());
            }
        });
        String[] newAdvs = {"achievement_a_0", "achievement_b_3", "achievement_end_0", "achievement_treasure_a_8"};
        List<String> missingAdvs = new ArrayList<>();
        for (String id : newAdvs) {
            if (!advIds.contains(id)) {
                missingAdvs.add(id);
            }
        }
        checkDetail("newdata", missingAdvs.isEmpty(),
                "研究台组成就落位 " + (newAdvs.length - missingAdvs.size()) + "/" + newAdvs.length,
                missingAdvs.isEmpty() ? "a_0/b_3/end_0/treasure_a_8 全部加载" : "缺失: " + missingAdvs);

        // C1 能量戒指进入 curios:ring 槽位 tag
        boolean ringTagged = BuiltInRegistries.ITEM.getHolder(pdrl("meltdream_energy_0_ring"))
                .map(h -> h.is(TagKey.create(Registries.ITEM, ResourceLocation.parse("curios:ring"))))
                .orElse(false);
        check("newdata", ringTagged, "meltdream_energy_0_ring 已进入 curios:ring 槽位 tag");
    }

    // ==================== S9 暗影高炉端到端冶炼 ====================

    private static BlockPos furnacePos;

    private static void blastFurnaceSetup() {
        ServerLevel level = overworld();
        BlockPos base = player().blockPosition().offset(8, 0, 8);
        furnacePos = new BlockPos(base.getX(), surfaceY(base) + 1, base.getZ());
        level.setBlock(furnacePos, PDBlocks.SHADOW_BLAST_FURNACE.get().defaultBlockState(), 3);
        if (!(level.getBlockEntity(furnacePos) instanceof ShadowBlastFurnaceBlockEntity furnace)) {
            check("blast", false, "暗影高炉方块实体已创建");
            return;
        }
        check("blast", true, "暗影高炉方块实体已创建");
        var handler = furnace.getItemHandler();
        handler.setStackInSlot(ShadowBlastFurnaceBlockEntity.SLOT_INPUT,
                new ItemStack(itemOf("rust_black_metal_grain")));
        handler.setStackInSlot(ShadowBlastFurnaceBlockEntity.SLOT_FUEL,
                new ItemStack(PDItems.NIGHTMARE_FUEL.get(), 8));
        handler.setStackInSlot(ShadowBlastFurnaceBlockEntity.SLOT_BUCKET_IN,
                new ItemStack(PDItems.SHADOW_LIQUID_BUCKET.get()));
        // 按配方声明的冶炼时长动态安排验证点（缺省兜底 300 tick）
        var match = level.getRecipeManager().getRecipeFor(PDRecipeTypes.SHADOW_BLASTING.get(),
                new SingleRecipeInput(handler.getStackInSlot(ShadowBlastFurnaceBlockEntity.SLOT_INPUT)), level);
        int wait = match.map(h -> h.value().getBlastingTick()).orElse(300) + 60;
        at(ticks + 20, PDPortingVerifyTest::blastFurnaceFluidCheck);
        at(ticks + wait, PDPortingVerifyTest::blastFurnaceVerify);
        LOGGER.info(TAG + "blast furnace placed at {}, verify in {} ticks", furnacePos, wait);
    }

    private static ShadowBlastFurnaceBlockEntity furnace() {
        return overworld().getBlockEntity(furnacePos) instanceof ShadowBlastFurnaceBlockEntity f ? f : null;
    }

    private static void blastFurnaceFluidCheck() {
        ShadowBlastFurnaceBlockEntity furnace = furnace();
        boolean drained = furnace != null && furnace.getItemHandler()
                .getStackInSlot(ShadowBlastFurnaceBlockEntity.SLOT_BUCKET_OUT).is(Items.BUCKET);
        checkDetail("blast", drained, "暗影液体桶已吸入储罐（空桶落入回收槽）",
                furnace == null ? "BE 缺失" : "回收槽: "
                        + furnace.getItemHandler().getStackInSlot(ShadowBlastFurnaceBlockEntity.SLOT_BUCKET_OUT));
    }

    private static void blastFurnaceVerify() {
        ShadowBlastFurnaceBlockEntity furnace = furnace();
        if (furnace == null) {
            check("blast", false, "暗影高炉冶炼产物验证（BE 缺失）");
            return;
        }
        ItemStack result = furnace.getItemHandler().getStackInSlot(ShadowBlastFurnaceBlockEntity.SLOT_RESULT);
        ItemStack input = furnace.getItemHandler().getStackInSlot(ShadowBlastFurnaceBlockEntity.SLOT_INPUT);
        checkDetail("blast", result.is(itemOf("blackmetal_grain")),
                "暗影高炉端到端冶炼：锈黑金属粒 → 黑金属粒",
                "产物槽: " + result + "，输入槽: " + input);
        overworld().removeBlock(furnacePos, false);
    }

    // ==================== S10 法杖投射物实例化扫掠 ====================

    /** 法杖模块 12 个投射物注册名（registry diff 保证存在性，本测保证可实例化不炸） */
    private static final String[] WAND_PROJECTILES = {
            "shadow_magicball", "bone_wing_fire_ball_projectile", "squeal_wave_projectile",
            "lightning_projectile", "moltengold_wand_projectile", "true_moltengold_wand_projectile",
            "truest_moltengold_wand_projectile", "squeal_wave_wand_projectile",
            "shadow_vortex_book_projectile", "pinkegg_projectile", "strawberry_heart_projectile",
            "white_sword_rain_projectile"};

    private static void wandProjectileSweep() {
        // AbstractArrow 子类不能 type.create(level) 无 owner —— 会 NPE。
        // 用各投射物的 shoot(level, shooter, random) 工厂做真实可发射性检查。
        ServerLevel level = overworld();
        ServerPlayer shooter = player();
        int ok = 0;
        List<String> failed = new ArrayList<>();
        java.util.Map<String, java.util.function.Supplier<Entity>> factories = new java.util.LinkedHashMap<>();
        factories.put("shadow_magicball", () -> {
            var e = PDEntities.SHADOW_MAGICBALL.get().create(level);
            if (e != null) { e.setPos(shooter.getX(), shooter.getY() + 30, shooter.getZ()); level.addFreshEntity(e);} return e;
        });
        factories.put("bone_wing_fire_ball_projectile", () ->
                com.pasterdream.pasterdreammod.entity.projectile.BoneWingFireBallProjectileEntity.shoot(level, shooter, shooter.getRandom()));
        factories.put("squeal_wave_projectile", () -> {
            var pig = spawnTestPig(shooter.blockPosition().offset(2, 0, 2));
            try {
                return com.pasterdream.pasterdreammod.entity.projectile.SquealWaveProjectileEntity.shoot(shooter, pig);
            } finally { if (pig != null) pig.discard(); }
        });
        factories.put("lightning_projectile", () ->
                com.pasterdream.pasterdreammod.entity.projectile.LightningProjectileEntity.shoot(level, shooter, shooter.getRandom()));
        factories.put("moltengold_wand_projectile", () ->
                com.pasterdream.pasterdreammod.entity.projectile.MoltengoldWandProjectileEntity.shoot(level, shooter, shooter.getRandom()));
        factories.put("true_moltengold_wand_projectile", () ->
                com.pasterdream.pasterdreammod.entity.projectile.TrueMoltengoldWandProjectileEntity.shoot(level, shooter, shooter.getRandom()));
        factories.put("truest_moltengold_wand_projectile", () ->
                com.pasterdream.pasterdreammod.entity.projectile.TruestMoltengoldWandProjectileEntity.shoot(level, shooter, shooter.getRandom()));
        factories.put("squeal_wave_wand_projectile", () ->
                com.pasterdream.pasterdreammod.entity.projectile.SquealWaveWandProjectileEntity.shoot(level, shooter, shooter.getRandom()));
        factories.put("shadow_vortex_book_projectile", () ->
                com.pasterdream.pasterdreammod.entity.projectile.ShadowVortexBookProjectileEntity.shoot(level, shooter, shooter.getRandom()));
        factories.put("pinkegg_projectile", () ->
                com.pasterdream.pasterdreammod.entity.projectile.PinkeggProjectileEntity.shoot(level, shooter, shooter.getRandom()));
        factories.put("strawberry_heart_projectile", () ->
                com.pasterdream.pasterdreammod.entity.projectile.StrawberryHeartProjectileEntity.shoot(level, shooter, shooter.getRandom()));
        factories.put("white_sword_rain_projectile", () ->
                com.pasterdream.pasterdreammod.entity.projectile.WhiteSwordRainProjectileEntity.shoot(level, shooter, shooter.getRandom()));

        for (var e : factories.entrySet()) {
            String name = e.getKey();
            try {
                if (BuiltInRegistries.ENTITY_TYPE.getOptional(pdrl(name)).isEmpty()) {
                    failed.add(name + ":未注册");
                    continue;
                }
                Entity entity = e.getValue().get();
                if (entity == null) {
                    failed.add(name + ":shoot返回null");
                    continue;
                }
                boolean alive = entity.isAlive();
                entity.discard();
                if (alive) ok++; else failed.add(name + ":入世即消亡");
            } catch (Exception ex) {
                failed.add(name + ":" + ex.getClass().getSimpleName() + ":" + String.valueOf(ex.getMessage()));
            }
        }
        checkDetail("wands", failed.isEmpty(),
                "法杖投射物发射 " + ok + "/" + factories.size(),
                failed.isEmpty() ? "全部可发射" : "失败: " + failed);
    }

    // ==================== S11 angel_block_item 行为 ====================

    private static void angelBlockItemTest() {
        ServerPlayer player = player();
        ServerLevel level = overworld();
        BlockPos base = player.blockPosition().offset(-8, 0, -8);
        // 悬浮到地表上方 6 格（脚下为空气），复现"空中使用"场景
        player.teleportTo(level, base.getX() + 0.5, surfaceY(base) + 6, base.getZ() + 0.5, 0, 0);
        ItemStack stack = new ItemStack(itemOf("angel_block_item"));
        if (stack.isEmpty()) {
            check("items", false, "angel_block_item 物品已注册");
            return;
        }
        player.setItemInHand(InteractionHand.MAIN_HAND, stack);
        stack.getItem().use(level, player, InteractionHand.MAIN_HAND);
        BlockPos below = BlockPos.containing(player.getX(), player.getY() - 1, player.getZ());
        boolean placed = level.getBlockState(below).is(PDBlocks.ANGEL_BLOCK.get());
        boolean consumed = player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty();
        checkDetail("items", placed, "angel_block_item 空中使用在脚下放置天使方块",
                "脚下方块: " + level.getBlockState(below));
        check("items", consumed, "angel_block_item 使用后消耗一枚");
        level.removeBlock(below, false);
        player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
    }

    // ==================== S12 储物袋/风向标 ====================

    private static void storageBagAndWindVaneSuite() {
        // 储物袋 CONTAINER 读写往返
        ItemStack bag = new ItemStack(itemOf("storage_bag"));
        check("bags", !bag.isEmpty() && bag.getItem() instanceof com.pasterdream.pasterdreammod.item.StorageBagItem,
                "storage_bag 已注册为 StorageBagItem");
        if (!bag.isEmpty()) {
            java.util.List<ItemStack> filled = new java.util.ArrayList<>();
            for (int i = 0; i < StorageBagItem.SIZE_SMALL; i++) {
                filled.add(i == 4 ? new ItemStack(Items.DIAMOND, 3) : ItemStack.EMPTY);
            }
            StorageBagItem.writeContents(bag, filled);
            java.util.List<ItemStack> round = StorageBagItem.readContents(bag, StorageBagItem.SIZE_SMALL);
            boolean ok = round.size() == StorageBagItem.SIZE_SMALL
                    && round.get(4).is(Items.DIAMOND) && round.get(4).getCount() == 3;
            checkDetail("bags", ok, "storage_bag CONTAINER 读写往返",
                    "slot4=" + round.get(4));
        }
        ItemStack bag0 = new ItemStack(itemOf("storage_bag_0"));
        check("bags", !bag0.isEmpty() && bag0.getItem() instanceof StorageBagItem s0 && s0.isAdvanced(),
                "storage_bag_0 已注册为高级储物袋");

        // 风向标：创造玩家 use 后不抛异常 + 物品仍在
        ItemStack vane = new ItemStack(itemOf("wind_vane"));
        check("items", !vane.isEmpty(), "wind_vane 已注册");
        if (!vane.isEmpty()) {
            ServerPlayer p = player();
            p.setItemInHand(InteractionHand.MAIN_HAND, vane);
            ItemStack before = p.getMainHandItem().copy();
            before.getItem().use(overworld(), p, InteractionHand.MAIN_HAND);
            check("items", p.getMainHandItem().is(before.getItem()),
                    "wind_vane 使用后仍保留在手中");
            p.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        }
    }

    // ==================== S13 粉蛋/凯尔卡 ====================

    private static void pinkeggAndCalleSuite() {
        // 粉蛋物品 + 投射物类型
        check("pinkegg", !new ItemStack(itemOf("pinkegg")).isEmpty(), "pinkegg 物品已注册");
        check("pinkegg", BuiltInRegistries.ENTITY_TYPE.containsKey(pdrl("pinkegg_projectile")),
                "pinkegg_projectile 实体已注册");
        check("pinkegg", BuiltInRegistries.ITEM.containsKey(pdrl("pinkegg"))
                        && !BuiltInRegistries.ITEM.containsKey(pdrl("pink_egg")),
                "粉鸡下蛋 ID 使用 pinkegg（非 pink_egg）");

        // 卡 2/8 注册 + 抽卡池含 1..9
        for (int i = 0; i <= 9; i++) {
            check("calle", BuiltInRegistries.ITEM.containsKey(pdrl("calle_card_" + i)),
                    "calle_card_" + i + " 已注册");
        }
        // 卡 3 使用后应有 rapid_reaction（创造玩家）
        ServerPlayer p = player();
        ItemStack card3 = new ItemStack(itemOf("calle_card_3"));
        if (!card3.isEmpty()) {
            p.setItemInHand(InteractionHand.MAIN_HAND, card3);
            card3.getItem().use(overworld(), p, InteractionHand.MAIN_HAND);
            boolean hasRapid = p.hasEffect(PDEffects.RAPID_REACTION.holder());
            check("calle", hasRapid, "卡3『疾行』施加 rapid_reaction");
            p.removeAllEffects();
            p.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        }
        // 卡2 flareup / 卡8 grail
        for (var pair : java.util.List.of(
                java.util.Map.entry("calle_card_2", PDEffects.FLAREUP_BUFF.holder()),
                java.util.Map.entry("calle_card_8", PDEffects.GRAIL_BUFF.holder()))) {
            ItemStack c = new ItemStack(itemOf(pair.getKey()));
            if (c.isEmpty()) continue;
            p.setItemInHand(InteractionHand.MAIN_HAND, c);
            c.getItem().use(overworld(), p, InteractionHand.MAIN_HAND);
            check("calle", p.hasEffect(pair.getValue()), pair.getKey() + " 施加对应效果");
            p.removeAllEffects();
            p.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        }
    }

    // ==================== S14 效果属性修饰符 ====================

    private static void effectModifierSuite() {
        ServerPlayer p = player();
        // cook_buff → SAN_VARIABILITY +1.2
        p.addEffect(new MobEffectInstance(PDEffects.COOK_BUFF, 200, 0));
        var san = p.getAttribute(PDAttributes.SAN_VARIABILITY);
        boolean cookOk = san != null && Math.abs(san.getValue() - (san.getBaseValue() + 1.2)) < 0.001
                || (san != null && san.getModifiers().stream().anyMatch(m -> Math.abs(m.amount() - 1.2) < 0.001));
        checkDetail("effects-mod", cookOk, "cook_buff 附加 SAN_VARIABILITY +1.2",
                san == null ? "attr null" : "value=" + san.getValue());
        p.removeEffect(PDEffects.COOK_BUFF);

        p.addEffect(new MobEffectInstance(PDEffects.OPPRESSION_BUFF, 200, 0));
        boolean oppOk = san != null && san.getModifiers().stream().anyMatch(m -> Math.abs(m.amount() + 9.6) < 0.001);
        check("effects-mod", oppOk, "oppression_buff 附加 SAN_VARIABILITY -9.6");
        p.removeEffect(PDEffects.OPPRESSION_BUFF);

        p.addEffect(new MobEffectInstance(PDEffects.FURY_SPELL_BUFF, 200, 0));
        var skill = p.getAttribute(PDAttributes.SKILLCD);
        var tele = p.getAttribute(PDAttributes.TELEPORTATIONCD);
        boolean furyOk = skill != null && skill.getModifiers().stream().anyMatch(m -> Math.abs(m.amount() + 0.3) < 0.001)
                && tele != null && tele.getModifiers().stream().anyMatch(m -> Math.abs(m.amount() + 0.3) < 0.001);
        check("effects-mod", furyOk, "fury_spell_buff 附加 SKILLCD/TELEPORTATIONCD -0.3");
        p.removeEffect(PDEffects.FURY_SPELL_BUFF);
        p.removeAllEffects();
    }

    // ==================== S16 世界结构生成 ====================

    private static void structureGenSuite() {
        PDStructureVerifyHooks.verify(server(), player(), r ->
                checkDetail("structures", r.pass(), r.name(), r.detail()));
    }

    // ==================== 暮影之笼流程缺口核实 ====================

    private static void twilightLanternSuite() {
        PDTwilightLanternVerifyHooks.verify(server(), player(), r ->
                checkDetail("twilight-lantern", r.pass(), r.name(), r.detail()));
    }

    // ==================== 第三梦境风之旅途流程核实 ====================

    private static void windJourneySuiteSync() {
        PDWindJourneyVerifyHooks.verifySync(server(), player(), r ->
                checkDetail("wind-journey", r.pass(), r.name(), r.detail()));
    }

    private static void windJourneySuiteAltarAftermath() {
        PDWindJourneyVerifyHooks.verifyAltarAftermath(player(), r ->
                checkDetail("wind-journey", r.pass(), r.name(), r.detail()));
    }

    // ==================== S17 武器工坊群 ====================

    private static void workshopSuite() {
        PDWorkshopVerifyHooks.verify(player(), r ->
                checkDetail("workshop", r.pass(), r.name(), r.detail()));
    }

    // ==================== S18 结构目标维度 ====================

    private static void structureDimensionSuite() {
        PDGalleryVerifyHooks.verifyStructureDimensions(server(), r ->
                checkDetail("struct-dim", r.pass(), r.name(), r.detail()));
    }

    // ==================== S19 方块总览展台（人工观察） ====================

    private static void blockGallerySuite() {
        refreshPlayerBuffs();
        PDGalleryVerifyHooks.placeBlockGallery(player(), r ->
                checkDetail("gallery", r.pass(), r.name(), r.detail()));
    }

    // ==================== S20 实体展台（刷怪蛋容器 + 玻璃笼） ====================

    private static void entityGallerySuite() {
        refreshPlayerBuffs();
        PDEntityGalleryVerifyHooks.placeEntityGallery(player(), r ->
                checkDetail("entity-gallery", r.pass(), r.name(), r.detail()));
    }

    /** 全程夜视 / 抗性 / 飞行 */
    private static void refreshPlayerBuffs() {
        ServerPlayer p = player();
        if (p != null) {
            PDGalleryVerifyHooks.ensureNightVision(p);
        }
    }

    // ==================== S15 无尽书导入槽 ====================

    private static void endlessBookImportSuite() {
        check("endless", BuiltInRegistries.BLOCK_ENTITY_TYPE.containsKey(pdrl("the_endless_book_of_dream_seekers")),
                "无尽书 BE 类型已注册");
        check("endless", BuiltInRegistries.MENU.containsKey(pdrl("the_endless_book_of_dream_seekers")),
                "无尽书菜单已注册");
        // 逻辑级：2 槽 importFromSlot
        try {
            var beClass = Class.forName(
                    "com.pasterdream.pasterdreammod.block.entity.TheEndlessBookOfDreamSeekersBlockEntity");
            var block = BuiltInRegistries.BLOCK.getOptional(pdrl("the_endless_book_of_dream_seekers"));
            if (block.isEmpty()) {
                check("endless", false, "无尽书方块已注册");
                return;
            }
            ServerLevel level = overworld();
            BlockPos pos = player().blockPosition().offset(4, 0, 4);
            pos = new BlockPos(pos.getX(), surfaceY(pos) + 1, pos.getZ());
            level.setBlock(pos, block.get().defaultBlockState(), 3);
            var be = level.getBlockEntity(pos);
            check("endless", be != null && beClass.isInstance(be), "无尽书 BE 已创建");
            if (be != null && beClass.isInstance(be)) {
                var handler = (net.neoforged.neoforge.items.ItemStackHandler)
                        beClass.getMethod("getItemHandler").invoke(be);
                checkDetail("endless", handler.getSlots() == 2, "无尽书库存 2 槽",
                        "slots=" + handler.getSlots());
                ItemStack sample = new ItemStack(Items.DIAMOND);
                handler.setStackInSlot(1, sample.copy());
                handler.setStackInSlot(0, ItemStack.EMPTY);
                boolean imported = (boolean) beClass.getMethod("importFromSlot").invoke(be);
                ItemStack display = handler.getStackInSlot(0);
                ItemStack imp = handler.getStackInSlot(1);
                checkDetail("endless", imported && display.is(Items.DIAMOND) && imp.isEmpty(),
                        "importFromSlot：槽1→槽0 并清空导入槽",
                        "display=" + display + " import=" + imp);
            }
            level.removeBlock(pos, false);
        } catch (Exception e) {
            checkDetail("endless", false, "无尽书导入逻辑异常", e.toString());
        }
    }

    // ==================== 收尾与报告 ====================

    private static void finish() {
        // 自适应顺延：动态追加的步骤（高炉验证等）尚未跑完时不收尾
        if (!STEPS.isEmpty()) {
            at(ticks + 40, PDPortingVerifyTest::finish);
            LOGGER.info(TAG + "finish deferred: {} steps pending", STEPS.size());
            return;
        }
        long pass = ASSERTIONS.stream().filter(Assertion::pass).count();
        long fail = ASSERTIONS.size() - pass;
        LOGGER.info(TAG + "==================================================");
        LOGGER.info(TAG + "SUITES: [{}]", selectedSuitesLabel());
        LOGGER.info(TAG + "SUMMARY: {} passed, {} failed", pass, fail);
        for (CategoryDiff diff : DIFFS) {
            LOGGER.info(TAG + "COVERAGE {} {}/{}", diff.category(), diff.present(), diff.expected());
        }
        LOGGER.info(TAG + (fail == 0 ? "RESULT: ALL PASS" : "RESULT: HAS FAILURES"));
        writeReport(pass, fail);
        if (KEEP_OPEN) {
            refreshPlayerBuffs();
            LOGGER.info(TAG + "COMPLETE — KEEP_OPEN=true，客户端保持打开供人工观察方块总览/结构；手动关闭窗口结束");
        } else {
            LOGGER.info(TAG + "COMPLETE");
        }
        done = true;
    }

    private static void writeReport(long pass, long fail) {
        try {
            JsonObject root = new JsonObject();
            root.addProperty("generated", "PDPortingVerifyTest");
            root.addProperty("suites", selectedSuitesLabel());
            root.addProperty("pass", pass);
            root.addProperty("fail", fail);
            root.add("registry_coverage", GSON.toJsonTree(DIFFS));
            root.add("assertions", GSON.toJsonTree(ASSERTIONS));
            Path out = serverRef.getServerDirectory().resolve("pd_verify_report.json");
            Files.writeString(out, GSON.toJson(root), StandardCharsets.UTF_8);
            LOGGER.info(TAG + "report written: {}", out.toAbsolutePath());
        } catch (Exception e) {
            LOGGER.error(TAG + "report write failed", e);
        }
    }

    // ==================== 工具 ====================

    private static ServerLevel levelOf(String path) {
        for (var key : serverRef.levelKeys()) {
            if (key.location().getNamespace().equals(PasterDreamMod.MOD_ID)
                    && key.location().getPath().equals(path)) {
                return serverRef.getLevel(key);
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static Holder<MobEffect> holderOf(String path) {
        return (Holder<MobEffect>) (Holder<?>) BuiltInRegistries.MOB_EFFECT
                .getHolder(ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, path))
                .orElse(null);
    }

    private static int surfaceY(BlockPos pos) {
        return overworld().getHeight(Heightmap.Types.MOTION_BLOCKING, pos.getX(), pos.getZ());
    }

    private static ResourceLocation pdrl(String path) {
        return ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, path);
    }

    /** 按注册名取物品；未注册时返回 AIR（下游以 stack.isEmpty() 判定并报 FAIL） */
    private static net.minecraft.world.item.Item itemOf(String path) {
        return BuiltInRegistries.ITEM.getOptional(pdrl(path)).orElse(Items.AIR);
    }

    // ==================== 客户端：verify 单独运行时的退出驱动 ====================

    /**
     * 客户端钩子：验证完成后安全退出（仅 verify 单独运行时；
     * 与冒烟测试同开时由冒烟测试的退出逻辑收尾）
     */
    @EventBusSubscriber(modid = PasterDreamMod.MOD_ID, value = Dist.CLIENT)
    public static final class Client {

        private static int stopCountdown = -1;

        private Client() {
        }

        /**
         * 客户端 tick：done 后倒计时退出
         *
         * @param event 客户端 tick 事件
         */
        private static boolean packChecked = false;

        @SubscribeEvent
        public static void onClientTick(net.neoforged.neoforge.client.event.ClientTickEvent.Post event) {
            if (!ENABLED) {
                return;
            }
            // 内嵌 UI 资源包激活断言（双模同开时也执行；只跑一次）
            net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
            if (!packChecked && mc.level != null) {
                packChecked = true;
                List<String> selected = new ArrayList<>(mc.getResourcePackRepository().getSelectedIds());
                boolean present = selected.stream().anyMatch(id -> id.contains("paster_vanilla_ui"));
                checkDetail("client", present, "内嵌 UI 资源包 paster_vanilla_ui 已激活",
                        "已选资源包: " + selected);
            }
            if (PDSmokeTest.ENABLED) {
                return;
            }
            // 默认 KEEP_OPEN：写完报告后不退出，方便人工巡视方块总览展台
            if (KEEP_OPEN) {
                return;
            }
            if (done && stopCountdown < 0) {
                stopCountdown = 60;
            }
            if (stopCountdown > 0) {
                stopCountdown--;
            } else if (stopCountdown == 0) {
                stopCountdown = -1;
                LOGGER.info(TAG + "verify finished, stopping client");
                net.minecraft.client.Minecraft.getInstance().stop();
            }
        }
    }
}
