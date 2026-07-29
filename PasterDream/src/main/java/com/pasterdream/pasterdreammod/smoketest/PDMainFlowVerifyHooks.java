package com.pasterdream.pasterdreammod.smoketest;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.util.ServerScheduler;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

// … 后续 task 再补 import

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;

/**
 * 主干全链路 VERIFY 套件 {@code main-flow}。
 * 不并入默认 {@code all}；须 PASTERDREAM_VERIFY_SUITES=main-flow。
 * 影之抉择：PASTERDREAM_VERIFY_SHADOW_CHOICE=dark|light（默认 dark）。
 */
public final class PDMainFlowVerifyHooks {

    public record Result(boolean pass, String name, String detail) {}

    public enum ShadowChoice { DARK, LIGHT }

    public enum PhaseId {
        P0_BOOTSTRAP, P1_DYEDREAM, P2_TWILIGHT, P3_LAMP_ARENA, P4_WIND, P5_REPORT
    }

    private static ShadowChoice shadowChoice = ShadowChoice.DARK;
    private static boolean hardFailed;
    private static final Map<String, int[]> phaseCounts = new LinkedHashMap<>(); // id -> {pass,fail,skip}
    private static final Map<String, Boolean> flags = new LinkedHashMap<>();
    private static String currentPhase = "P0_bootstrap";

    private PDMainFlowVerifyHooks() {}

    public static ShadowChoice shadowChoice() { return shadowChoice; }

    public static List<Map<String, Object>> phaseSummaries() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (var e : phaseCounts.entrySet()) {
            int[] c = e.getValue();
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", e.getKey());
            m.put("pass", c[0]);
            m.put("fail", c[1]);
            m.put("skip", c[2]);
            list.add(m);
        }
        return list;
    }

    public static Map<String, Boolean> continuousFlags() {
        return new LinkedHashMap<>(flags);
    }

    /** 时间线单入口：顺序跑 P0–P5；硬失败后后续 phase SKIPPED_DEPENDENCY。 */
    public static void run(MinecraftServer server, ServerPlayer player, Consumer<Result> out) {
        resetState();
        if (server == null || player == null) {
            out.accept(new Result(false, "main-flow-skip", "server/player null"));
            return;
        }
        runPhase(PhaseId.P0_BOOTSTRAP, "P0_bootstrap", out, () -> phase0Bootstrap(server, player, out));
        runPhase(PhaseId.P1_DYEDREAM, "P1_dyedream", out, () -> phase1Dyedream(server, player, out));
        runPhase(PhaseId.P2_TWILIGHT, "P2_twilight", out, () -> phase2Twilight(server, player, out));
        runPhase(PhaseId.P3_LAMP_ARENA, "P3_lamp_arena", out, () -> phase3LampArena(server, player, out));
        runPhase(PhaseId.P4_WIND, "P4_wind", out, () -> phase4Wind(server, player, out));
        runPhase(PhaseId.P5_REPORT, "P5_report", out, () -> phase5Report(out));
    }

    private static void resetState() {
        hardFailed = false;
        phaseCounts.clear();
        flags.clear();
        for (String k : new String[]{
                "a_0", "b_0", "hide_16", "entered_lamp",
                "npc_0", "npc_1", "npc_2", "npc_3", "npc_4", "npc_5",
                "choice_done", "d_0", "talent_shadow", "talent_light",
                "e_0", "wind_enter", "wind_exit"
        }) {
            flags.put(k, false);
        }
        shadowChoice = ShadowChoice.DARK;
        currentPhase = "P0_bootstrap";
    }

    private static void runPhase(PhaseId id, String reportId, Consumer<Result> out, Runnable body) {
        currentPhase = reportId;
        phaseCounts.putIfAbsent(reportId, new int[]{0, 0, 0});
        if (hardFailed && id != PhaseId.P5_REPORT) {
            record(out, false, reportId + " SKIPPED_DEPENDENCY", "prior hard fail", true);
            return;
        }
        try {
            body.run();
        } catch (Exception e) {
            hardFailed = true;
            out.accept(new Result(false, reportId + " uncaught", e.toString()));
            bump(reportId, false);
            PasterDreamMod.LOGGER.error("[main-flow] phase {} failed", reportId, e);
        }
    }

    /** pass=false 且 skip=false → fail；skip=true 计 skip 且不标 hard（除非调用方 setHard） */
    static void record(Consumer<Result> out, boolean pass, String name, String detail, boolean skip) {
        String id = currentPhase;
        if (skip) {
            phaseCounts.computeIfAbsent(id, k -> new int[]{0, 0, 0})[2]++;
            out.accept(new Result(true, name, "SKIPPED_DEPENDENCY: " + detail));
        } else {
            bump(id, pass);
            out.accept(new Result(pass, name, detail));
        }
    }

    private static void bump(String id, boolean pass) {
        int[] c = phaseCounts.computeIfAbsent(id, k -> new int[]{0, 0, 0});
        if (pass) c[0]++; else c[1]++;
    }

    private static void markHardFail() { hardFailed = true; }

    private static void setFlag(String key, boolean v) { flags.put(key, v); }

    private static Result ok(boolean pass, String name, String detail) {
        return new Result(pass, name, detail);
    }

    // ==================== shared helpers (from SecondDream + 新增 for main-flow) ====================

    private static void useBlock(ServerPlayer player, ServerLevel level, BlockPos pos) {
        BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(pos), Direction.UP, pos, false);
        level.getBlockState(pos).useWithoutItem(level, player, hit);
    }

    private static boolean hasAdvancement(ServerPlayer player, String path) {
        AdvancementHolder h = player.server.getAdvancements()
                .get(ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, path));
        return h != null && player.getAdvancements().getOrStartProgress(h).isDone();
    }

    private static void grantAdvancement(ServerPlayer player, String path) {
        AdvancementHolder h = player.server.getAdvancements()
                .get(ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, path));
        if (h == null) {
            return;
        }
        AdvancementProgress p = player.getAdvancements().getOrStartProgress(h);
        if (!p.isDone()) {
            for (String c : p.getRemainingCriteria()) {
                player.getAdvancements().award(h, c);
            }
        }
    }

    private static void revokeAdvancement(ServerPlayer player, String path) {
        AdvancementHolder h = player.server.getAdvancements()
                .get(ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, path));
        if (h == null) {
            return;
        }
        AdvancementProgress p = player.getAdvancements().getOrStartProgress(h);
        for (String c : p.getCompletedCriteria()) {
            player.getAdvancements().revoke(h, c);
        }
    }

    private static void ensureOverworld(ServerPlayer player, ServerLevel overworld) {
        if (player.level().dimension() != Level.OVERWORLD) {
            BlockPos spawn = overworld.getSharedSpawnPos();
            player.teleportTo(overworld,
                    spawn.getX() + 0.5, spawn.getY() + 1.0, spawn.getZ() + 0.5,
                    player.getYRot(), player.getXRot());
        }
        player.setGameMode(GameType.SURVIVAL);
    }

    /** minimal teleport helper per brief */
    private static void teleport(ServerLevel level, ServerPlayer player, double x, double y, double z) {
        player.teleportTo(level, x, y, z, player.getYRot(), player.getXRot());
    }

    private static void clearBox(ServerLevel level, BlockPos a, BlockPos b) {
        BlockPos.betweenClosed(a, b).forEach(p -> {
            if (!level.getBlockState(p).isAir()) {
                level.removeBlock(p.immutable(), false);
            }
        });
    }

    private static int countItem(Player player, net.minecraft.world.item.Item item) {  // note: Player for generality, but callers use ServerPlayer
        int n = 0;
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack s = player.getInventory().getItem(i);
            if (s.is(item)) {
                n += s.getCount();
            }
        }
        return n;
    }

    private static void setSurvival(ServerPlayer player) {
        player.setGameMode(GameType.SURVIVAL);
    }

    private static void forceNight(ServerLevel level) {
        level.setDayTime(18000); // 午夜
        level.setWeatherParameters(0, 6000, true, false); // 可选雨；床门控要 !isDay || thundering
    }

    private static void forceDay(ServerLevel level) {
        level.setDayTime(1000);
        level.setWeatherParameters(6000, 0, false, false);
    }

    private static void accept(Consumer<Result> out, boolean pass, String name, String detail) {
        out.accept(new Result(pass, name, detail));
        bump(currentPhase, pass);
    }

    private static void acceptHard(Consumer<Result> out, boolean pass, String name, String detail) {
        accept(out, pass, name, detail);
        if (!pass) markHardFail();
    }

    private static void dumpPlayer(ServerPlayer player, String label) {
        if (player == null) {
            PasterDreamMod.LOGGER.info("[PDVerify] DUMP {}: player=null", label);
            return;
        }
        PasterDreamMod.LOGGER.info("[PDVerify] DUMP {}: dim={} pos={} mode={} health={} xp={}",
                label,
                player.level().dimension().location(),
                player.blockPosition(),
                player.gameMode.getGameModeForPlayer(),
                player.getHealth(),
                player.experienceLevel);
    }

    // ---- phase stubs（Task 2+ 填充；仅 P0/P5 本 task 实现）----
    private static void phase0Bootstrap(MinecraftServer server, ServerPlayer player, Consumer<Result> out) {
        var choice = parseShadowChoice();
        if (choice.isEmpty()) {
            acceptHard(out, false, "SHADOW_CHOICE 非法",
                    String.valueOf(System.getenv("PASTERDREAM_VERIFY_SHADOW_CHOICE")));
            return;
        }
        shadowChoice = choice.get();
        accept(out, true, "shadowChoice=" + shadowChoice.name().toLowerCase(Locale.ROOT), "env/prop");

        player.setGameMode(GameType.SURVIVAL);
        accept(out, player.gameMode.getGameModeForPlayer() == GameType.SURVIVAL, "切生存", "ok");

        // 最小自举：无 start 时 grant achievement_start（夹具）
        if (!hasAdvancement(player, "achievement_start")) {
            grantAdvancement(player, "achievement_start");
        }
        accept(out, hasAdvancement(player, "achievement_start"), "achievement_start", "fixture-or-prior");

        ServerLevel ow = server.overworld();
        ensureOverworld(player, ow);
        BlockPos anchor = player.blockPosition();
        // 小平台
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                ow.setBlock(anchor.offset(dx, -1, dz), Blocks.STONE.defaultBlockState(), 3);
                ow.setBlock(anchor.offset(dx, 0, dz), Blocks.AIR.defaultBlockState(), 3);
            }
        }
        accept(out, true, "主世界锚点平台", anchor.toShortString());
    }

    private static void phase1Dyedream(MinecraftServer server, ServerPlayer player, Consumer<Result> out) {
        out.accept(new Result(true, "P1 stub", "TODO Task3"));
    }
    private static void phase2Twilight(MinecraftServer server, ServerPlayer player, Consumer<Result> out) {
        out.accept(new Result(true, "P2 stub", "TODO Task3"));
    }
    private static void phase3LampArena(MinecraftServer server, ServerPlayer player, Consumer<Result> out) {
        out.accept(new Result(true, "P3 stub", "TODO Task4-5"));
    }
    private static void phase4Wind(MinecraftServer server, ServerPlayer player, Consumer<Result> out) {
        out.accept(new Result(true, "P4 stub", "TODO Task3"));
    }
    private static void phase5Report(Consumer<Result> out) {
        // 按 shadowChoice 校验 talent 互斥
        boolean shadow = Boolean.TRUE.equals(flags.get("talent_shadow"));
        boolean light = Boolean.TRUE.equals(flags.get("talent_light"));
        boolean exclusive = shadowChoice == ShadowChoice.DARK
                ? (shadow && !light) : (light && !shadow);
        accept(out, exclusive || !Boolean.TRUE.equals(flags.get("choice_done")),
                "talent 与 shadowChoice 互斥一致",
                "choice=" + shadowChoice + " shadow=" + shadow + " light=" + light);
        accept(out, true, "continuousFlags", flags.toString());
        PasterDreamMod.LOGGER.info("[PDVerify] MAIN-FLOW phases={} choice={}",
                phaseSummaries(), shadowChoice);
    }


    /** 解析影之抉择；非法返回 empty，由 P0 fail fast */
    static java.util.Optional<ShadowChoice> parseShadowChoice() {
        String raw = System.getenv("PASTERDREAM_VERIFY_SHADOW_CHOICE");
        if (raw == null || raw.isBlank()) {
            raw = System.getProperty("pasterdream.verify.shadowChoice", "");
        }
        raw = raw == null ? "" : raw.trim().toLowerCase(Locale.ROOT);
        if (raw.isEmpty() || "dark".equals(raw) || "shadow".equals(raw)) {
            return java.util.Optional.of(ShadowChoice.DARK);
        }
        if ("light".equals(raw)) {
            return java.util.Optional.of(ShadowChoice.LIGHT);
        }
        return java.util.Optional.empty();
    }
}
