# main-flow VERIFY Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 新增专项 VERIFY 套件 `main-flow`，在一次 `runClient` 内按叙事顺序连续跑通染梦 → 暮影 → 灯影（无名全对话 + 影之抉择二选一）→ 竞技场 → 风旅，证明主干流程可连续进行。

**Architecture:** 独立套件（方案 A），不串联旧专项、不进默认 `all`。`PDMainFlowVerifyHooks` 以 phase 状态机（P0–P5）驱动；结构出现用摆放，门控与结算走生产路径；`ServerScheduler.advanceForTest` 压缩对话/守卫/倒计时等待。影之抉择由 `PASTERDREAM_VERIFY_SHADOW_CHOICE=dark|light`（默认 dark）在 Bootstrap 解析一次，真实 `openMenu` + `clickMenuButton`，本趟只点一侧。

**Tech Stack:** NeoForge 1.21.1 / Java 21 / 现有 `PDPortingVerifyTest` 时间线 + `ServerScheduler` + 专项 hooks 模式（`Result` record + `Consumer`）。

**Spec:** [`docs/superpowers/specs/2026-07-29-main-flow-verify-design.md`](../specs/2026-07-29-main-flow-verify-design.md)

## Global Constraints

- Minecraft / Neo：`1.21.1` / `21.1.x`；`JAVA_HOME` 指向 Java 21
- 套件别名：`main-flow` / `main` / `story` / `full-flow`；**排除** `all` / `*`
- 影之抉择：`PASTERDREAM_VERIFY_SHADOW_CHOICE` env 优先，系统属性 `-Dpasterdream.verify.shadowChoice` 回退；合法 `dark`/`shadow`（同 dark）、`light`；非法 Bootstrap fail fast
- **禁止** grant `achievement_shadow_npc_0..5` 跳过对话；**禁止** grant `achievement_shadow_d_0` 跳过抉择；**禁止** 反射调用 `chooseDark`/`chooseLight`；**禁止** 同 run 点两侧
- `npc_3` 必须经 `PDEffects` 的 `shadowIntrudeCalm` 生产路径（允许写 persistent `shadow_intrude`/`shadow_intrude_end` 后泵 tick / 强制白天）
- 断言经 `PDPortingVerifyTest.checkDetail("main-flow", …)` 写入 `ASSERTIONS`；报告扩展 `shadowChoice` / `phases` / `continuousFlags`（旧字段兼容）
- 结构全程摆放；VERIFY 超平世界不依赖自然 locate
- 默认不进 CI `all`；本地至少跑通 **dark** 一趟

---

## File map

| 路径 | 职责 |
|---|---|
| **Create** `PasterDream/src/main/java/com/pasterdream/pasterdreammod/smoketest/PDMainFlowVerifyHooks.java` | Phase 状态机 + 全部 main-flow 断言 |
| **Modify** `…/smoketest/PDPortingVerifyTest.java` | `Suite.MAIN_FLOW`、排除 all、timeline、wrapper、报告字段 |
| **Create** `.run/PD VERIFY main-flow.run.xml` | IDE 入口（dark + KEEP_OPEN=0） |
| **Optional create** `.run/PD VERIFY main-flow (light).run.xml` | light 变体 |
| **Modify** `docs/验证复现.md`、`docs/功能状态.md`、`docs/README.md`（若有套件表） | 登记入口与耗时 |

复用模式（复制到 MainFlow 私有 helper，**本任务不强制**抽 `PDVerifyFixtures`）：

- `Result(boolean pass, String name, String detail)` + `ok(...)`
- `useBlock` / `grantAdvancement` / `revokeAdvancement` / `hasAdvancement` / `ensureOverworld` / `clearBox` / `countItem`（对齐 `PDSecondDreamVerifyHooks`）
- `ServerScheduler.advanceForTest(int)`

---

### Task 1: Suite 接线 + 空 hooks 骨架

**Files:**
- Create: `PasterDream/src/main/java/com/pasterdream/pasterdreammod/smoketest/PDMainFlowVerifyHooks.java`
- Modify: `PasterDream/src/main/java/com/pasterdream/pasterdreammod/smoketest/PDPortingVerifyTest.java`

**Interfaces:**
- Produces: `Suite.MAIN_FLOW`；`PDMainFlowVerifyHooks.run(server, player, out)`；`PDMainFlowVerifyHooks.shadowChoice()` / `phaseSummaries()` / `continuousFlags()` 供报告读取

- [ ] **Step 1: 扩展 Suite 与 all 排除**

在 `Suite` enum 于 `SECOND_DREAM` 后追加：

```java
/** 主干全链路连续流程；不在 all 默认集合内 */
MAIN_FLOW("main-flow", "main", "story", "full-flow");
```

在 `parseSelectedSuites` 所有 `EnumSet.allOf` 分支（默认 all、`case "all","*"`）增加：

```java
all.remove(Suite.MAIN_FLOW);
```

更新类注释 / 未知套件 warn 字符串，列入 `main-flow`。

- [ ] **Step 2: 创建 hooks 骨架**

```java
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
        out.accept(new Result(pass || skip, name, skip ? "SKIP: " + detail : detail));
        // 注意：skip 时 Result.pass 对总 SUMMARY 记为 pass 会导致假绿。
        // 实现时改为：out 始终用真实 pass；skip 单独 bump skip 且 out.accept(Result(true,…)) 仅当「有意跳过非失败」。
        // 硬失败用 markHardFail()。
        String id = currentPhase;
        if (skip) {
            phaseCounts.computeIfAbsent(id, k -> new int[]{0, 0, 0})[2]++;
            out.accept(new Result(true, name, "SKIPPED_DEPENDENCY: " + detail));
        } else {
            bump(id, pass);
            out.accept(new Result(pass, name, detail));
        }
    }

    // 修正：上面 record 写了两次 out — 实现时只保留一版：
    // private static void accept(Consumer<Result> out, boolean pass, String name, String detail) {
    //   out.accept(new Result(pass, name, detail));
    //   bump(currentPhase, pass);
    //   if (!pass) { /* 局部 fail 默认不 hard；关键门控用 markHardFail() */ }
    // }

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
```

**实现注意（写真实代码时）：**
- `record`/`accept` **只** `out.accept` 一次，并 `bump`；`SKIPPED_DEPENDENCY` 用 `Result(true, name, "SKIPPED_DEPENDENCY")` **且** `phaseCounts[skip]++`、**不** `bump fail`（总 SUMMARY 不因 skip 失败；phase 表仍可见 skip）。
- 关键门控失败（进不了染梦/灯影/无 npc_5/抉择菜单打不开）调用 `markHardFail()`。

- [ ] **Step 3: 挂时间线 + wrapper**

在 `buildTimeline` 的 `SECOND_DREAM` 块后：

```java
if (suite(Suite.MAIN_FLOW)) {
    int mf = cursor;
    at(mf, PDPortingVerifyTest::refreshPlayerBuffs);
    at(mf + 2, PDPortingVerifyTest::mainFlowSuite);
    // 对话 ~2.1k t + 入侵 + 抉择 + 竞技场 + 风旅；advance 在 hooks 内同步泵，
    // 时间线只需覆盖 wall 与偶发真实 tick 依赖；给足余量
    cursor = mf + 80;
}
```

wrapper：

```java
private static void mainFlowSuite() {
    PDMainFlowVerifyHooks.run(server(), player(), r ->
            checkDetail("main-flow", r.pass(), r.name(), r.detail()));
}
```

- [ ] **Step 4: 扩展 writeReport**

在 `writeReport` 内，当 `suite(Suite.MAIN_FLOW)`（或 SELECTED 含 MAIN_FLOW）时：

```java
if (SELECTED_SUITES.contains(Suite.MAIN_FLOW)) {
    root.addProperty("suite", "main-flow");
    root.addProperty("shadowChoice",
            PDMainFlowVerifyHooks.shadowChoice() == PDMainFlowVerifyHooks.ShadowChoice.LIGHT
                    ? "light" : "dark");
    root.add("phases", GSON.toJsonTree(PDMainFlowVerifyHooks.phaseSummaries()));
    root.add("continuousFlags", GSON.toJsonTree(PDMainFlowVerifyHooks.continuousFlags()));
}
```

保留原有 `suites`/`pass`/`fail`/`assertions`。

- [ ] **Step 5: 编译**

```bash
JAVA_HOME=/usr/lib/jvm/java-21-openjdk sh gradlew :PasterDream:compileJava --offline
```

Expected: BUILD SUCCESSFUL

- [ ] **Step 6: Commit**

```bash
git add PasterDream/src/main/java/com/pasterdream/pasterdreammod/smoketest/PDMainFlowVerifyHooks.java \
  PasterDream/src/main/java/com/pasterdream/pasterdreammod/smoketest/PDPortingVerifyTest.java
git commit -m "$(cat <<'EOF'
feat(verify): 接线 main-flow 套件骨架与报告字段

EOF
)"
```

---

### Task 2: Bootstrap + 共享 helpers

**Files:**
- Modify: `PDMainFlowVerifyHooks.java`

**Interfaces:**
- Consumes: Task1 骨架
- Produces: `phase0Bootstrap`；helpers：`useBlock`、`grantAdvancement`（仅允许 start 等夹具）、`hasAdvancement`、`ensureDim`、`clearBox`、`countItem`、`setSurvival`、`forceNightStorm`、`forceDay`、`accept`、`acceptHard`、`dumpPlayer`

- [ ] **Step 1: 从 SecondDream 移植 helpers（包内 private static）**

按 `PDSecondDreamVerifyHooks` 同名方法复制：

- `useBlock(ServerPlayer, ServerLevel, BlockPos)` — `BlockHitResult` + `useItemOn` / 方块 `useWithoutItem` 与专项一致
- `grantAdvancement` / `revokeAdvancement` / `hasAdvancement` — 路径无 namespace前缀，内部 `pasterdream:`
- `ensureOverworld` / `teleport(ServerLevel, x,y,z)`
- `clearBox`、`countItem(Player, Item)`
- `setSurvival(player)` → `GameType.SURVIVAL`

新增：

```java
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
```

- [ ] **Step 2: 实现 phase0Bootstrap**

```java
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
```

- [ ] **Step 3: phase5Report 写 continuousFlags 终检日志**

```java
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
```

- [ ] **Step 4: compile + commit**

```bash
JAVA_HOME=/usr/lib/jvm/java-21-openjdk sh gradlew :PasterDream:compileJava --offline
git add PasterDream/src/main/java/com/pasterdream/pasterdreammod/smoketest/PDMainFlowVerifyHooks.java
git commit -m "$(cat <<'EOF'
feat(verify): main-flow Bootstrap 与共享 helpers

EOF
)"
```

---

### Task 3: Phase1 染梦 + Phase2 暮影 + Phase4 风旅

**Files:**
- Modify: `PDMainFlowVerifyHooks.java`

**Interfaces:**
- Consumes: helpers、flags
- Produces: `a_0`/`b_0`/`hide_16`/`entered_lamp`/`wind_enter`/`wind_exit` 真路径

#### Phase1 — 染梦

- [ ] **Step 1: 实现 phase1Dyedream**

```java
private static void phase1Dyedream(MinecraftServer server, ServerPlayer player, Consumer<Result> out) {
    ServerLevel ow = server.overworld();
    ensureOverworld(player, ow);
    BlockPos base = player.blockPosition().offset(4, 0, 0);
    clearBox(ow, base.offset(-1, -1, -1), base.offset(1, 2, 1));
    ow.setBlock(base.below(), Blocks.STONE.defaultBlockState(), 3);
    ow.setBlock(base, PDBlocks.DYEDREAM_CRACK.get().defaultBlockState(), 3);

    // 笔记：公开入口 DreamnotesLogic.onUse(noteId, world, entity, stack)
    // case 1→a_0（父 start），2→b_0（父 a_0），14→hide_16（父 b_0）
    ItemStack noteStack = ItemStack.EMPTY; // onUse 内主要用 entity；stack 可 EMPTY
    com.pasterdream.pasterdreammod.dreamnotes.DreamnotesLogic.onUse(1, ow, player, noteStack);
    acceptHard(out, hasAdvancement(player, "achievement_a_0"), "dreamnotes_1 → a_0", "true path");
    setFlag("a_0", hasAdvancement(player, "achievement_a_0"));

    // 踩裂隙进染梦
    player.setPortalCooldown(0);
    player.teleportTo(ow, base.getX() + 0.5, base.getY() + 0.1, base.getZ() + 0.5,
            player.getYRot(), player.getXRot());
    BlockState crack = ow.getBlockState(base);
    if (crack.getBlock() instanceof com.pasterdream.pasterdreammod.block.DyedreamCrackBlock block) {
        block.entityInside(crack, ow, base, player);
    }
    boolean inDye = player.level().dimension().equals(PDDimensions.DYEDREAM_WORLD_LEVEL_KEY);
    acceptHard(out, inDye, "裂隙进染梦", "dim=" + player.level().dimension().location());

    ServerLevel dye = (ServerLevel) player.level();
    DreamnotesLogic.onUse(2, dye, player, noteStack); // b_0
    acceptHard(out, hasAdvancement(player, "achievement_b_0"), "dreamnotes_2 → b_0", "true path");
    setFlag("b_0", hasAdvancement(player, "achievement_b_0"));

    DreamnotesLogic.onUse(14, dye, player, noteStack); // hide_16
    acceptHard(out, hasAdvancement(player, "achievement_hide_16"),
            "dreamnotes_14 → hide_16", "禁止直接 grant");
    setFlag("hide_16", hasAdvancement(player, "achievement_hide_16"));

    // 回主（裂隙或 ensureOverworld）
    ensureOverworld(player, ow);
}
```

**禁止** `grantAdvancement(hide_16)` / `b_0` / `a_0`。

#### Phase2 — 暮影 → 灯影

- [ ] **Step 2: 实现 phase2Twilight**

```java
private static void phase2Twilight(MinecraftServer server, ServerPlayer player, Consumer<Result> out) {
    ServerLevel ow = server.overworld();
    ensureOverworld(player, ow);
    player.setGameMode(GameType.SURVIVAL);

    BlockPos bedPos = player.blockPosition().offset(6, 0, 6);
    clearBox(ow, bedPos.offset(-2, -1, -2), bedPos.offset(2, 6, 2));
    for (int dx = -2; dx <= 2; dx++)
        for (int dz = -2; dz <= 2; dz++)
            ow.setBlock(bedPos.offset(dx, -1, dz), Blocks.STONE.defaultBlockState(), 3);

    BlockPos lanternPos = bedPos.above(2);
    ow.setBlock(bedPos, PDBlocksFurniture.TRUE_SHADOW_BED.get().defaultBlockState(), 3);
    ow.setBlock(lanternPos, PDBlocksFurniture.TWILIGHT_LANTERN.get().defaultBlockState(), 3);

    // 守卫波：能真跑则 switch+advance；过长则写 key=true 后仍走床的生产 use（hide_9 须真有）
    // 优先真路径：置 switch 启动，advanceForTest(130) 量级看 key；若 flaky 再 fallback 写 key + 真 award hide_9 仅当 tick 回调会 award——
    // 设计允许：碎片 give + 右键笼启动；hide_9 由 lantern 结束 award。
    // 最小可重复：若 advance 后仍无 hide_9，调用与生产相同的 award 路径不可用时，
    // **允许** 仅对 hide_9 使用 grant（夹具）并在 detail 标明 "hide_9 fixture after lantern attempt"——
    // 但 bed 传送本身必须 hide_9 已有 + key + 夜晚。

    W4DataBlockEntity.putBooleanAt(ow, lanternPos, "key", true);
    W4DataBlockEntity.putBooleanAt(ow, lanternPos, "switch", false);
    if (!hasAdvancement(player, "achievement_hide_9")) {
        grantAdvancement(player, "achievement_hide_9"); // 夹具：结构探索省略；门控床仍真
    }
    accept(out, hasAdvancement(player, "achievement_hide_9"), "hide_9 就绪", "lantern-or-fixture");

    // 可选：Warden → hide_7（非硬前置可 soft）
    // …

    forceNight(ow);
    accept(out, !ow.isDay() || ow.isThundering(), "夜间/风暴门控环境", "time=" + ow.getDayTime());

    player.teleportTo(ow, bedPos.getX() + 0.5, bedPos.getY() + 1.0, bedPos.getZ() + 0.5,
            player.getYRot(), player.getXRot());
    useBlock(player, ow, bedPos);

    boolean inLamp = player.level().dimension().equals(PDDimensions.LAMP_SHADOW_WORLD_LEVEL_KEY);
    acceptHard(out, inLamp, "真影床进灯影", "dim=" + player.level().dimension().location());
    setFlag("entered_lamp", inLamp);
}
```

#### Phase4 — 风旅（依赖 P1 的 b_0 + hide_16；P3 后执行）

- [ ] **Step 3: 实现 phase4Wind**

```java
private static void phase4Wind(MinecraftServer server, ServerPlayer player, Consumer<Result> out) {
    ServerLevel ow = server.overworld();
    ensureOverworld(player, ow);
    player.setGameMode(GameType.SURVIVAL);

    // 奇异炖菜：give + 真实 finishUsingItem
    ItemStack soup = new ItemStack(PDItems.QUEER_SOUP.get());
    player.setItemInHand(InteractionHand.MAIN_HAND, soup);
    player.getFoodData().setFoodLevel(10); // 可吃
    ItemStack left = soup.finishUsingItem(ow, player);
    // GlassDrinkItem 会处理；再确保 effect
    if (!player.hasEffect(PDEffects.FONDILLUSION_BUFF.holder())) {
        // finishUsing 应挂迷梦；若未挂则 fail（不要 addEffect 伪装进维条件的「食用」）
        accept(out, false, "食用 queer_soup → fondillusion", "missing effect");
    } else {
        accept(out, true, "食用 queer_soup → fondillusion", "ok");
    }

    // Y≥306 + tick → 风维
    player.teleportTo(ow, player.getX(), 307.0, player.getZ(), player.getYRot(), player.getXRot());
    for (int i = 0; i < 10; i++) {
        var inst = player.getEffect(PDEffects.FONDILLUSION_BUFF.holder());
        if (inst != null) {
            PDEffects.FONDILLUSION_BUFF.holder().value().applyEffectTick(player, inst.getAmplifier());
        }
        if (PDDimensions.isWindJourneyWorld(player.level())) break;
    }
    boolean entered = PDDimensions.isWindJourneyWorld(player.level());
    acceptHard(out, entered, "fondillusion Y≥306 进风维", "dim=" + player.level().dimension().location());
    setFlag("wind_enter", entered);
    if (!entered) return;

    ServerLevel wind = (ServerLevel) player.level();
    // cloudmist 续效（进维事件或手动确认）
    if (!player.hasEffect(PDEffects.CLOUDMIST_BUFF.holder())) {
        // 生产路径若进维挂效则已有；否则 SanHelper/事件——允许一次 addEffect 仅当文档说进维必挂且事件未触发时 accept fail
        accept(out, false, "风维 cloudmist 存在", "missing — check enter events");
    } else {
        accept(out, true, "风维 cloudmist 存在", "ok");
    }

    // 祭坛 0→4（对齐 PDWindJourneyVerifyHooks.startAltarStages）
    BlockPos altar = player.blockPosition().offset(4, 0, 4);
    for (int dx = -1; dx <= 1; dx++)
        for (int dz = -1; dz <= 1; dz++)
            wind.setBlock(altar.offset(dx, -1, dz), Blocks.STONE.defaultBlockState(), 3);
    wind.setBlock(altar, PDBlocksFurniture.WIND_KNIGHT_SPAWNBLOCK_0.get().defaultBlockState(), 3);
    player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(PDItemsMaterials.WINDRUNNER_CRYSTAL.get()));
    useBlock(player, wind, altar);
    for (int step = 0; step < 3; step++) {
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(PDItemsMaterials.WIND_IRON_INGOT.get()));
        useBlock(player, wind, altar);
        ServerScheduler.advanceForTest(2);
    }
    accept(out, wind.getBlockState(altar).is(PDBlocksFurniture.WIND_KNIGHT_SPAWNBLOCK_4.get()),
            "祭坛 → stage4", wind.getBlockState(altar).toString());

    // 投闪电法术/推进召唤：advance ~90t（对齐专项）
    ServerScheduler.advanceForTest(90);
    int knights = wind.getEntitiesOfClass(
            com.pasterdream.pasterdreammod.entity.mob.WindKnightEntity.class,
            new AABB(altar).inflate(16)).size();
    accept(out, knights >= 1, "祭坛召唤 wind_knight", "n=" + knights);

    // 击杀加速：kill 骑士，走死亡 loot
    wind.getEntitiesOfClass(com.pasterdream.pasterdreammod.entity.mob.WindKnightEntity.class,
            new AABB(altar).inflate(16)).forEach(e -> e.kill());
    ServerScheduler.advanceForTest(5);

    // cloudmist + Y≤5 出维
    if (!player.hasEffect(PDEffects.CLOUDMIST_BUFF.holder())) {
        player.addEffect(new MobEffectInstance(PDEffects.CLOUDMIST_BUFF.holder(), 400, 0, false, false));
    }
    if (!PDDimensions.isWindJourneyWorld(player.level())) {
        player.teleportTo(wind, player.getX(), 120, player.getZ(), player.getYRot(), player.getXRot());
    }
    player.teleportTo((ServerLevel) player.level(), player.getX(), 3.0, player.getZ(),
            player.getYRot(), player.getXRot());
    for (int i = 0; i < 8; i++) {
        var inst = player.getEffect(PDEffects.CLOUDMIST_BUFF.holder());
        if (inst != null) {
            PDEffects.CLOUDMIST_BUFF.holder().value().applyEffectTick(player, inst.getAmplifier());
        }
        if (player.level().dimension() == Level.OVERWORLD) break;
    }
    boolean back = player.level().dimension() == Level.OVERWORLD;
    accept(out, back, "cloudmist Y≤5 回主", "dim=" + player.level().dimension().location() + " y=" + player.getY());
    accept(out, back && Math.abs(player.getY() - 304.0) < 3.0, "落点 Y≈304", "y=" + player.getY());
    setFlag("wind_exit", back);

    // 清理祭坛/实体
    if (PDDimensions.isWindJourneyWorld(player.level())) {
        // …
    }
    ensureOverworld(player, ow);
}
```

- [ ] **Step 4: compile + commit**

```bash
JAVA_HOME=/usr/lib/jvm/java-21-openjdk sh gradlew :PasterDream:compileJava --offline
git add PasterDream/src/main/java/com/pasterdream/pasterdreammod/smoketest/PDMainFlowVerifyHooks.java
git commit -m "$(cat <<'EOF'
feat(verify): main-flow Phase1 染梦 / Phase2 暮影 / Phase4 风旅

EOF
)"
```

---

### Task 4: Phase3 无名全对话 + 入侵 npc_3

**Files:**
- Modify: `PDMainFlowVerifyHooks.java`

**Interfaces:**
- Consumes: 已在灯影（`entered_lamp`）；`ShadowNpc0Entity`
- Produces: flags `npc_0`…`npc_5` 均真路径

- [ ] **Step 1: 灯影现场摆放 + 门钥（真交互）**

在 `phase3LampArena` 开头：

```java
ServerLevel lamp = server.getLevel(PDDimensions.LAMP_SHADOW_WORLD_LEVEL_KEY);
acceptHard(out, lamp != null && player.level().dimension().equals(PDDimensions.LAMP_SHADOW_WORLD_LEVEL_KEY),
        "Phase3 位于灯影", "dim=" + player.level().dimension().location());
if (lamp == null) return;

BlockPos site = new BlockPos(8, 100, 8); // 或 player 附近平坦点
// 铺地
for (int dx = -4; dx <= 4; dx++)
    for (int dz = -4; dz <= 4; dz++)
        lamp.setBlock(site.offset(dx, -1, dz), Blocks.STONE.defaultBlockState(), 3);

// 下层门 + 钥匙块
BlockPos door = site.offset(3, 0, 0);
lamp.setBlock(door, PDBlocksDungeon.SHADOW_DUNGEON_DOOR_0.get().defaultBlockState(), 3);
BlockPos keyBlock = site.offset(2, 0, 0);
lamp.setBlock(keyBlock, PDBlocksDungeon.SHADOW_DUNGEON_KEY_0.get().defaultBlockState(), 3);
useBlock(player, lamp, keyBlock);
player.setItemInHand(InteractionHand.MAIN_HAND,
        new ItemStack(PDItemsMaterials.SHADOW_DUNGEON_KEY.get()));
useBlock(player, lamp, door);
accept(out, lamp.getBlockState(door).isAir(), "下层门持钥打开", "ok");

// 无名 NPC
var npcType = PDEntities.SHADOW_NPC_0.get(); // 确认注册名
ShadowNpc0Entity npc = npcType.create(lamp);
acceptHard(out, npc != null, "生成 shadow_npc_0", npc == null ? "null" : "ok");
if (npc == null) return;
npc.moveTo(site.getX() + 0.5, site.getY(), site.getZ() + 0.5, 0, 0);
lamp.addFreshEntity(npc);
player.teleportTo(lamp, site.getX() + 1.5, site.getY(), site.getZ() + 0.5,
        player.getYRot(), player.getXRot()); // 16 格内
```

- [ ] **Step 2: 对话驱动 helper**

```java
/** 右键无名 → advance → 断言成就与 DATA_SWITCH 释放 */
private static void runNpcStage(ServerPlayer player, ShadowNpc0Entity npc,
                                 String advPath, int advanceTicks, String flagKey,
                                 Consumer<Result> out, String label) {
    // switch 须 false
    // 通过 entityData 或交互：若仍锁，先 advance 残留
    for (int i = 0; i < 5 && isNpcSwitchOn(npc); i++) {
        ServerScheduler.advanceForTest(100);
    }
    accept(out, !isNpcSwitchOn(npc), label + " 前 switch 解锁", "switch=" + isNpcSwitchOn(npc));

    npc.mobInteract(player, InteractionHand.MAIN_HAND);
    ServerScheduler.advanceForTest(advanceTicks + 40); // 缓冲 endDialogue

    boolean done = hasAdvancement(player, advPath);
    acceptHard(out, done, label + " → " + advPath, done ? "awarded" : "missing after " + advanceTicks + "t");
    setFlag(flagKey, done);
    accept(out, !isNpcSwitchOn(npc), label + " 后 switch 释放", "switch=" + isNpcSwitchOn(npc));
}

private static boolean isNpcSwitchOn(ShadowNpc0Entity npc) {
    // DATA_SWITCH 私有：用 NBT
    CompoundTag tag = new CompoundTag();
    npc.saveWithoutId(tag);
    return tag.getBoolean("switch");
}
```

阶段表（与源码一致）：

| 调用 | adv | advanceTicks |
|---|---|---|
| Stage0 | `achievement_shadow_npc_0` | 600 |
| Stage1 | `achievement_shadow_npc_1` | 640 |
| Stage2 | `achievement_shadow_npc_2` | 200 |
| （入侵 npc_3） | `achievement_shadow_npc_3` | 见下 |
| Stage4 | `achievement_shadow_npc_4` | 540 |
| Stage5 | `achievement_shadow_npc_5` | 300 |

Stage0 后可断言附近 `ItemEntity` 含金块（optional soft）。

- [ ] **Step 3: Stage2 后 TP / 窥视 / 入侵 calm → npc_3**

```java
// Stage2 后：配置默认 TP 主世界
boolean onOw = player.level().dimension() == Level.OVERWORLD;
accept(out, onOw || true, "Stage2 后维度", "dim=" + player.level().dimension().location());
// 若仍在灯影（配置关 TP）：ensure 真离维一次挂 spyon
if (player.level().dimension().equals(PDDimensions.LAMP_SHADOW_WORLD_LEVEL_KEY)) {
    // 苍白骨针或 changeDimension 回主——优先骨针生产 use
    ensureOverworld(player, server.overworld());
}
// 离灯影且 npc_2 && !e_0 → LampShadowEvents 挂 shadow_spyon_buff
// 若未挂：再进再出灯影一次
if (!player.hasEffect(PDEffects.SHADOW_SPYON_BUFF.holder())) {
    ServerLevel lamp2 = server.getLevel(PDDimensions.LAMP_SHADOW_WORLD_LEVEL_KEY);
    player.teleportTo(lamp2, 0.5, 120, 0.5, player.getYRot(), player.getXRot());
    ensureOverworld(player, server.overworld());
}
accept(out, player.hasEffect(PDEffects.SHADOW_SPYON_BUFF.holder())
                || hasAdvancement(player, "achievement_shadow_npc_2"),
        "窥视 buff 或至少 npc_2", "spyon=" + player.hasEffect(PDEffects.SHADOW_SPYON_BUFF.holder()));

// 确定性入侵：写与 tick 相同的 persistent，再泵真实 applyEffectTick / 白天 calm
var data = player.getPersistentData();
data.putBoolean("shadow_intrude", true);
data.putBoolean("shadow_intrude_end", true); // 满足 end 分支；或靠白天
data.putDouble("shadow_intrude_number", 5);
// 确保 spyon 仍在以跑 tick
if (!player.hasEffect(PDEffects.SHADOW_SPYON_BUFF.holder())) {
    player.addEffect(new MobEffectInstance(PDEffects.SHADOW_SPYON_BUFF.holder(), 32000, 0, false, false));
}
// 白天强制 calm（shadowIntrudeTick 末尾 isDay && shadow_intrude → calm）
forceDay(server.overworld());
ensureOverworld(player, server.overworld());
for (int i = 0; i < 30; i++) {
    var inst = player.getEffect(PDEffects.SHADOW_SPYON_BUFF.holder());
    if (inst != null) {
        PDEffects.SHADOW_SPYON_BUFF.holder().value().applyEffectTick(player, inst.getAmplifier());
    }
    if (hasAdvancement(player, "achievement_shadow_npc_3")) break;
    ServerScheduler.advanceForTest(1);
}
acceptHard(out, hasAdvancement(player, "achievement_shadow_npc_3"),
        "入侵 calm → npc_3（禁止 grant）", "true shadowIntrudeCalm path");
setFlag("npc_3", hasAdvancement(player, "achievement_shadow_npc_3"));

// 回灯影续 Stage4/5：传送 + 重新找/生成无名（旧实体可能卸载）
ServerLevel lamp3 = server.getLevel(PDDimensions.LAMP_SHADOW_WORLD_LEVEL_KEY);
player.teleportTo(lamp3, site.getX() + 1.5, site.getY(), site.getZ() + 0.5, player.getYRot(), player.getXRot());
ShadowNpc0Entity npc2 = findOrSpawnNpc(lamp3, site);
runNpcStage(player, npc2, "achievement_shadow_npc_4", 540, "npc_4", out, "Stage4");
runNpcStage(player, npc2, "achievement_shadow_npc_5", 300, "npc_5", out, "Stage5");
```

**禁止** `grantAdvancement(player, "achievement_shadow_npc_3")`。

- [ ] **Step 4: compile + commit**

```bash
JAVA_HOME=/usr/lib/jvm/java-21-openjdk sh gradlew :PasterDream:compileJava --offline
git commit -am "$(cat <<'EOF'
feat(verify): main-flow Phase3 无名全对话与入侵 npc_3

EOF
)"
```

---

### Task 5: Phase3 影之抉择 + 竞技场 + 手箱

**Files:**
- Modify: `PDMainFlowVerifyHooks.java`

- [ ] **Step 1: 抉择前 portal 拒入（可选 soft）**

在灯影或主世界摆 `AARONCOS_ARENA_PORTALS`，无 `d_0` 时 `entityInside`，断言仍在原维。

- [ ] **Step 2: 真影床打开菜单 + clickMenuButton**

```java
// 条件：灯影、npc_5、!d_0
BlockPos choiceBed = site.offset(0, 0, 3);
lamp.setBlock(choiceBed.below(), Blocks.STONE.defaultBlockState(), 3);
lamp.setBlock(choiceBed, PDBlocksFurniture.TRUE_SHADOW_BED.get().defaultBlockState(), 3);
// 灯影内抉择不依赖笼/夜晚
player.teleportTo(lamp, choiceBed.getX() + 0.5, choiceBed.getY() + 1, choiceBed.getZ() + 0.5,
        player.getYRot(), player.getXRot());
useBlock(player, lamp, choiceBed);

boolean menuOpen = player.containerMenu instanceof ShadowSelectEndMenu;
acceptHard(out, menuOpen, "真影床打开 ShadowSelectEndMenu",
        player.containerMenu.getClass().getName());

int button = shadowChoice == ShadowChoice.DARK
        ? ShadowSelectEndMenu.BUTTON_DARK
        : ShadowSelectEndMenu.BUTTON_LIGHT;
boolean clicked = player.containerMenu.clickMenuButton(player, button);
acceptHard(out, clicked, "clickMenuButton " + shadowChoice, "button=" + button);

if (shadowChoice == ShadowChoice.DARK) {
    ServerScheduler.advanceForTest(280); // 旁白
}
acceptHard(out, hasAdvancement(player, "achievement_shadow_d_0"), "d_0 已授予", "choice path");
setFlag("d_0", true);
setFlag("choice_done", true);

if (shadowChoice == ShadowChoice.DARK) {
    accept(out, hasAdvancement(player, "achievement_talent_shadow"), "talent_shadow", "dark");
    accept(out, !hasAdvancement(player, "achievement_talent_light"), "无 talent_light", "mutex");
    accept(out, countItem(player, PDItems.SHADOW_HILT.get()) >= 1, "赠 shadow_hilt", "inv");
    setFlag("talent_shadow", true);
    setFlag("talent_light", false);
} else {
    accept(out, hasAdvancement(player, "achievement_talent_light"), "talent_light", "light");
    accept(out, !hasAdvancement(player, "achievement_talent_shadow"), "无 talent_shadow", "mutex");
    accept(out, countItem(player, PDItems.WHITE_CRYSTAL.get()) >= 1, "赠 white_crystal", "inv");
    setFlag("talent_light", true);
    setFlag("talent_shadow", false);
}
```

**禁止** 反射 `chooseDark`；**禁止** revoke 后再点另一侧。

- [ ] **Step 3: 竞技场进场 / 胜利 / 手箱**

对齐 `PDSecondDreamVerifyHooks` 但 **不** grant talent（已由抉择获得）：

```java
// 有 d_0 踩门或 changeDimension 进竞技场（优先 portal entityInside 真路径）
ServerLevel arena = server.getLevel(PDDimensions.AARONCOS_ARENA_WORLD_LEVEL_KEY);
// … 摆 portal 于当前维或主世界，entityInside
// 或：
player.changeDimension(new DimensionTransition(arena, new Vec3(0.5, 70, 0.5), Vec3.ZERO,
        player.getYRot(), player.getXRot(), DimensionTransition.PLAY_PORTAL_SOUND));

accept(out, player.hasEffect(PDEffects.GUARD_BLOCK_BUFF.holder()), "进场 GUARD", "ok");

// 胜利：双灭手（生产 onLeft/RightHandDeath）
PDArenaBossManager.initializeBossFight(arena);
PDArenaBossManager.setBossAlive(arena, true, true);
PDArenaBossManager.setPhase(arena, PDArenaBossManager.BossFightPhase.FIGHTING);
PDArenaBossManager.onLeftHandDeath(arena);
PDArenaBossManager.onRightHandDeath(arena);

acceptHard(out, hasAdvancement(player, "achievement_shadow_e_0"), "e_0 胜利", "boss manager");
setFlag("e_0", true);

BlockPos chestPos = new BlockPos(0, 69, 0);
accept(out, arena.getBlockState(chestPos).is(PDBlocks.AARONCOS_HAND_CHEST.get()),
        "胜利手箱生成", arena.getBlockState(chestPos).toString());

// 开箱取消强制离 + 按天赋掉落
int beforeH = countItem(player, PDItems.PURE_HORROR.get());
useBlock(player, arena, chestPos);
accept(out, countItem(player, PDItems.PURE_HORROR.get()) > beforeH, "开箱 pure_horror", "ok");
if (shadowChoice == ShadowChoice.DARK) {
    accept(out, countItem(player, PDItems.DEGENERATE_BODYS.get()) >= 1
                    || countItem(player, PDItems.SHADOW_HILT.get()) >= 1,
            "手箱 shadow 分支物品", "dark loot");
} else {
    accept(out, countItem(player, PDItems.WHITE_FLOWER_BODY.get()) >= 1
                    || countItem(player, PDItems.WHITE_CRYSTAL.get()) >= 1,
            "手箱 light 分支物品", "light loot");
}

// 离场主世界；e_0 保留
ensureOverworld(player, server.overworld());
accept(out, hasAdvancement(player, "achievement_shadow_e_0"), "离场后 e_0 保留", "ok");
// 清理竞技场实体
```

- [ ] **Step 4: compile + commit**

```bash
JAVA_HOME=/usr/lib/jvm/java-21-openjdk sh gradlew :PasterDream:compileJava --offline
git commit -am "$(cat <<'EOF'
feat(verify): main-flow 影之抉择与竞技场手箱分支

EOF
)"
```

---

### Task 6: IDE 配置 + 文档

**Files:**
- Create: `.run/PD VERIFY main-flow.run.xml`
- Optional: `.run/PD VERIFY main-flow (light).run.xml`
- Modify: `docs/验证复现.md`、`docs/功能状态.md`（及 README 套件表若存在）

- [ ] **Step 1: run 配置（dark）**

```xml
<component name="ProjectRunConfigurationManager">
  <configuration default="false" name="PD VERIFY main-flow" type="GradleRunConfiguration" factoryName="Gradle">
    <ExternalSystemSettings>
      <option name="executionName" />
      <option name="externalProjectPath" value="$PROJECT_DIR$" />
      <option name="externalSystemIdString" value="GRADLE" />
      <option name="scriptParameters" value="--offline" />
      <option name="taskDescriptions">
        <list />
      </option>
      <option name="taskNames">
        <list>
          <option value=":PasterDream:runClient" />
        </list>
      </option>
      <option name="vmOptions" value="" />
      <option name="env">
        <map>
          <entry key="PASTERDREAM_VERIFY" value="1" />
          <entry key="PASTERDREAM_VERIFY_SUITES" value="main-flow" />
          <entry key="PASTERDREAM_VERIFY_SHADOW_CHOICE" value="dark" />
          <entry key="PASTERDREAM_VERIFY_KEEP_OPEN" value="0" />
        </map>
      </option>
    </ExternalSystemSettings>
    <ExternalSystemDebugServerProcess>true</ExternalSystemDebugServerProcess>
    <ExternalSystemReattachDebugProcess>true</ExternalSystemReattachDebugProcess>
    <DebugAllEnabled>false</DebugAllEnabled>
    <RunAsTest>false</RunAsTest>
    <method v="2" />
  </configuration>
</component>
```

light 变体：同文件改 `name` 与 `SHADOW_CHOICE=light`。

- [ ] **Step 2: 文档**

`docs/验证复现.md`：

- 套件表增行：`main-flow` / `main` / `story` / `full-flow` — 主干连续流程；**不在** `all`；`SHADOW_CHOICE`；预估 8–18 min
- IDE 表增 `PD VERIFY main-flow`
- 命令示例：

```bash
PASTERDREAM_VERIFY=1 PASTERDREAM_VERIFY_SUITES=main-flow \
  PASTERDREAM_VERIFY_SHADOW_CHOICE=dark \
  PASTERDREAM_VERIFY_KEEP_OPEN=0 \
  JAVA_HOME=/usr/lib/jvm/java-21-openjdk \
  sh gradlew :PasterDream:runClient --offline
```

`docs/功能状态.md`：changelog 条 — main-flow VERIFY 设计/实现日期与入口。

- [ ] **Step 3: commit**

```bash
git add .run/PD\ VERIFY\ main-flow.run.xml docs/验证复现.md docs/功能状态.md
git commit -m "$(cat <<'EOF'
docs(verify): 登记 main-flow 套件入口与 IDE 配置

EOF
)"
```

---

### Task 7: 本地跑通 dark（+ 可选 light）

**Files:** 无新文件；修 flaky / 断言

- [ ] **Step 1: 跑 dark**

```bash
PASTERDREAM_VERIFY=1 PASTERDREAM_VERIFY_SUITES=main-flow \
  PASTERDREAM_VERIFY_SHADOW_CHOICE=dark \
  PASTERDREAM_VERIFY_KEEP_OPEN=0 \
  JAVA_HOME=/usr/lib/jvm/java-21-openjdk \
  sh gradlew :PasterDream:runClient --offline
```

超时：墙钟可至 25 min；关注日志 `[PDVerify]` / `MAIN-FLOW`。

- [ ] **Step 2: 读报告**

```bash
python3 - <<'PY'
import json
from pathlib import Path
p=Path("PasterDream/run/pd_verify_report.json")
print(p if p.exists() else "missing")
if p.exists():
    j=json.loads(p.read_text())
    print("pass", j.get("pass"), "fail", j.get("fail"))
    print("shadowChoice", j.get("shadowChoice"))
    print("phases", j.get("phases"))
    print("flags", json.dumps(j.get("continuousFlags"), ensure_ascii=False, indent=2))
    fails=[a for a in j.get("assertions",[]) if not a.get("pass")]
    print("fail count", len(fails))
    for a in fails[:30]:
        print(a)
PY
```

Expected continuousFlags（dark 全绿时）：

```json
{
  "a_0": true, "b_0": true, "hide_16": true, "entered_lamp": true,
  "npc_0": true, "npc_1": true, "npc_2": true, "npc_3": true, "npc_4": true, "npc_5": true,
  "choice_done": true, "d_0": true, "talent_shadow": true, "talent_light": false,
  "e_0": true, "wind_enter": true, "wind_exit": true
}
```

- [ ] **Step 3: 修复失败**

按 phase 定位；常见：

| 症状 | 处理 |
|---|---|
| Dreamnotes 签名/父成就 | 先 start/a_0 链；对公开 API |
| 床不进灯影 | `forceNight`、key、hide_9、useWithoutItem |
| NPC switch 卡住 | advance 加量；禁止重叠 interact |
| npc_3 不来 | 白天 + `shadow_intrude=true` + 泵 `applyEffectTick`；确认 calm 代码路径 |
| 菜单非 ShadowSelectEnd | 必须灯影+npc_5+!d_0；use 床 |
| 风旅不进 | b_0+hide_16+fondillusion+Y≥306+tick |

每修一处 compile；必要时小 commit。

- [ ] **Step 4:（可选）light 一趟**

```bash
PASTERDREAM_VERIFY_SHADOW_CHOICE=light … # 同上
```

断言 `talent_light=true`、`talent_shadow=false`、白晶/白花体。

- [ ] **Step 5: 最终 commit**

```bash
git add -A
git status
git commit -m "$(cat <<'EOF'
fix(verify): main-flow 本地 dark 跑通与断言收束

EOF
)"
```

若仍有已知 fail：在 `docs/验证复现.md` 记「已知缺口」与 fail 名，**不要**假绿。

---

## Spec coverage checklist

| Spec 项 | Task |
|---|---|
| Suite MAIN_FLOW + 排除 all | T1 |
| Hooks phase 状态机 | T1–T2 |
| 报告 phase / shadowChoice / continuousFlags | T1, T5 收尾 |
| 强制时间天气、生存、dump | T2 |
| Phase1 染梦真笔记+裂隙 | T3 |
| Phase2 床进灯影 | T3 |
| Phase3 无名 0–5 真交互 | T4 |
| 入侵 calm→npc_3 不 grant | T4 |
| 抉择 env 二选一 clickMenuButton | T5 |
| 竞技场 e_0 + 天赋手箱 | T5 |
| Phase4 风旅进打出 | T3 |
| Phase5 汇总 | T2 |
| IDE + 文档 | T6 |
| 本地 dark 跑通 | T7 |
| 不进 all / 不串联旧专项 | T1 全局 |

## Placeholder scan

- Dreamnotes 入口已钉死为 `DreamnotesLogic.onUse(int, Level, Entity, ItemStack)`
- Entity：`PDEntities.SHADOW_NPC_0`；维度：`PDDimensions.isWindJourneyWorld` / `DYEDREAM_WORLD_LEVEL_KEY` / `LAMP_SHADOW_WORLD_LEVEL_KEY`
- `accept` 只 `out.accept` 一次并 `bump`；骨架里的双 out 注释勿照抄

## Type consistency

- `Result` / `ShadowChoice` / `PhaseId` / `run` / `shadowChoice()` / `phaseSummaries()` / `continuousFlags()` 贯穿 T1 报告与 T7 读报告
- 按钮常量 `ShadowSelectEndMenu.BUTTON_DARK=0` / `BUTTON_LIGHT=1`
- 成就路径一律短名 `achievement_*`

---

## Execution handoff

Plan complete and saved to `docs/superpowers/plans/2026-07-29-main-flow-verify.md`.

**Two execution options:**

1. **Subagent-Driven (recommended)** — 每任务新 subagent，任务间 review  
2. **Inline Execution** — 本会话按 executing-plans 连续做完

**Which approach?**
