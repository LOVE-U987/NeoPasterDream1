package com.pasterdream.pasterdreammod.smoketest;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.util.ServerScheduler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
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

    // ---- phase stubs（Task 2+ 填充）----
    private static void phase0Bootstrap(MinecraftServer server, ServerPlayer player, Consumer<Result> out) {
        out.accept(new Result(true, "P0 stub", "TODO Task2"));
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
        out.accept(new Result(true, "P5 continuousFlags snapshot", flags.toString()));
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
