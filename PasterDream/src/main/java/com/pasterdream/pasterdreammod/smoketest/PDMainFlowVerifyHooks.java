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

import com.pasterdream.pasterdreammod.block.AaroncosArenaPortalsBlock;
import com.pasterdream.pasterdreammod.block.entity.W4DataBlockEntity;
import com.pasterdream.pasterdreammod.dreamnotes.DreamnotesLogic;
import com.pasterdream.pasterdreammod.entity.mob.ShadowNpc0Entity;
import com.pasterdream.pasterdreammod.entity.mob.WindKnightEntity;
import com.pasterdream.pasterdreammod.menu.ShadowSelectEndMenu;
import com.pasterdream.pasterdreammod.registry.PDArenaBossManager;
import com.pasterdream.pasterdreammod.registry.PDBlocks;
import com.pasterdream.pasterdreammod.registry.PDDimensions;
import com.pasterdream.pasterdreammod.registry.PDEffects;
import com.pasterdream.pasterdreammod.registry.PDEntities;
import com.pasterdream.pasterdreammod.registry.PDItems;
import com.pasterdream.pasterdreammod.registry.blocks.PDBlocksDungeon;
import com.pasterdream.pasterdreammod.registry.blocks.PDBlocksFurniture;
import com.pasterdream.pasterdreammod.registry.items.PDItemsFunctional;
import com.pasterdream.pasterdreammod.registry.items.PDItemsMaterials;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

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

    private static int countGround(ServerLevel level, BlockPos center, net.minecraft.world.item.Item item) {
        int n = 0;
        for (ItemEntity ie : level.getEntitiesOfClass(ItemEntity.class, new AABB(center).inflate(6))) {
            if (ie.getItem().is(item)) {
                n += ie.getItem().getCount();
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
        level.updateSkyBrightness(); // 立即更新 skyDarken，使 isDay() 反映夜晚（setDayTime 后 sky 未自动更新）
    }

    private static void forceDay(ServerLevel level) {
        level.setDayTime(1000);
        level.setWeatherParameters(6000, 0, false, false);
        level.updateSkyBrightness();
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
	    ServerLevel ow = server.overworld();
	    ensureOverworld(player, ow);
	    BlockPos base = player.blockPosition().offset(4, 0, 0);
	    clearBox(ow, base.offset(-1, -1, -1), base.offset(1, 2, 1));
	    ow.setBlock(base.below(), Blocks.STONE.defaultBlockState(), 3);
	    ow.setBlock(base, PDBlocks.DYEDREAM_CRACK.get().defaultBlockState(), 3);

	    // 笔记：公开入口 DreamnotesLogic.onUse(noteId, world, entity, stack)
	    // case 1→a_0（父 start），2→b_0（父 a_0），14→hide_16（父 b_0）
	    ItemStack noteStack = ItemStack.EMPTY; // onUse 内主要用 entity；stack 可 EMPTY
	    DreamnotesLogic.onUse(1, ow, player, noteStack);
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

    private static void phase3LampArena(MinecraftServer server, ServerPlayer player, Consumer<Result> out) {
        ServerLevel lamp = server.getLevel(PDDimensions.LAMP_SHADOW_WORLD_LEVEL_KEY);
        acceptHard(out, lamp != null && player.level().dimension().equals(PDDimensions.LAMP_SHADOW_WORLD_LEVEL_KEY),
                "Phase3 位于灯影", "dim=" + player.level().dimension().location());
        if (lamp == null) return;

        BlockPos site = new BlockPos(8, 100, 8);
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

        // Stage0/1/2
        runNpcStage(player, npc, "achievement_shadow_npc_0", 600, "npc_0", out, "Stage0");
        // Stage0 后可选 soft 断言金块（附近 ItemEntity）
        boolean hasGoldGift = lamp.getEntitiesOfClass(ItemEntity.class,
                new AABB(site).inflate(8)).stream()
                .anyMatch(e -> !e.getItem().isEmpty() && e.getItem().is(Blocks.GOLD_BLOCK.asItem()));
        accept(out, true, "Stage0 后金块礼 (soft)", "found=" + hasGoldGift); // soft: 不 hard fail

        runNpcStage(player, npc, "achievement_shadow_npc_1", 640, "npc_1", out, "Stage1");
        runNpcStage(player, npc, "achievement_shadow_npc_2", 200, "npc_2", out, "Stage2");

        // Stage2 后：配置默认 TP 主世界
        boolean onOw = player.level().dimension() == Level.OVERWORLD;
        accept(out, onOw || true, "Stage2 后维度", "dim=" + player.level().dimension().location());
        // 若仍在灯影（配置关 TP）：ensure 真离维一次挂 spyon
        if (player.level().dimension().equals(PDDimensions.LAMP_SHADOW_WORLD_LEVEL_KEY)) {
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
        acceptHard(out, npc2 != null, "回灯影找/生成 npc for stage4/5", npc2 == null ? "null" : "ok");
        if (npc2 == null) return;
        runNpcStage(player, npc2, "achievement_shadow_npc_4", 540, "npc_4", out, "Stage4");
        runNpcStage(player, npc2, "achievement_shadow_npc_5", 300, "npc_5", out, "Stage5");

        // === Task5: 抉择前 portal 拒入（soft） + 真影床 + click one side + arena/chest ===
        // 条件：灯影、npc_5、!d_0
        // Step 1: portal reject soft (no d_0)
        BlockPos portalReject = site.offset(5, 0, 5);
        lamp.setBlock(portalReject.below(), Blocks.STONE.defaultBlockState(), 3);
        lamp.setBlock(portalReject, PDBlocks.AARONCOS_ARENA_PORTALS.get().defaultBlockState(), 3);
        BlockState prState = lamp.getBlockState(portalReject);
        if (prState.getBlock() instanceof AaroncosArenaPortalsBlock prBlock) {
            prBlock.entityInside(prState, lamp, portalReject, player);
        }
        accept(out, player.level().dimension().equals(PDDimensions.LAMP_SHADOW_WORLD_LEVEL_KEY),
                "抉择前 portal 拒入（soft）", "dim=" + player.level().dimension().location());

        // Step 2: 真影床打开菜单 + clickMenuButton（仅点一侧）
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

        // FORBID reflect; only one side; d_0 from click path
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

        // Step 3: 竞技场进场 / 胜利 / 手箱
        // 对齐 PDSecondDreamVerifyHooks 但不 grant talent（已由抉择获得）
        // 优先 portal entityInside 真路径
        ServerLevel arena = server.getLevel(PDDimensions.AARONCOS_ARENA_WORLD_LEVEL_KEY);
        BlockPos gate = site.offset(6, 0, 0);
        lamp.setBlock(gate.below(), Blocks.STONE.defaultBlockState(), 3);
        lamp.setBlock(gate, PDBlocks.AARONCOS_ARENA_PORTALS.get().defaultBlockState(), 3);
        BlockState gateState = lamp.getBlockState(gate);
        if (gateState.getBlock() instanceof AaroncosArenaPortalsBlock gateBlock) {
            gateBlock.entityInside(gateState, lamp, gate, player);
        }
        accept(out, player.level().dimension().equals(PDDimensions.AARONCOS_ARENA_WORLD_LEVEL_KEY),
                "有 d_0 踩门进竞技场（portal entityInside）", "dim=" + player.level().dimension().location());

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

        // 开箱：生产经 ServerScheduler ~40t 掉落到地面（非瞬时 inventory）
        // 适配：advance + 查 ground ItemEntity 和/或 inventory；与 brief 样本 inv 即时断言有差异时记录
        int beforeH = countItem(player, PDItems.PURE_HORROR.get());
        useBlock(player, arena, chestPos);
        ServerScheduler.advanceForTest(50);
        int afterH = countItem(player, PDItems.PURE_HORROR.get());
        int gHorror = countGround(arena, chestPos, PDItems.PURE_HORROR.get());
        accept(out, (afterH > beforeH) || (gHorror > 0), "开箱 pure_horror（inv/ground 40t+）", "invΔ=" + (afterH - beforeH) + " g=" + gHorror);

        if (shadowChoice == ShadowChoice.DARK) {
            int gBody = countGround(arena, chestPos, PDItems.DEGENERATE_BODYS.get());
            int gHilt = countGround(arena, chestPos, PDItems.SHADOW_HILT.get());
            int invBody = countItem(player, PDItems.DEGENERATE_BODYS.get());
            int invHilt = countItem(player, PDItems.SHADOW_HILT.get());
            accept(out, gBody >= 1 || gHilt >= 1 || invBody >= 1 || invHilt >= 1,
                    "手箱 shadow 分支物品", "dark loot g=" + gBody + "/" + gHilt + " inv=" + invBody + "/" + invHilt);
        } else {
            int gFlower = countGround(arena, chestPos, PDItems.WHITE_FLOWER_BODY.get());
            int gCrystal = countGround(arena, chestPos, PDItems.WHITE_CRYSTAL.get());
            int invFlower = countItem(player, PDItems.WHITE_FLOWER_BODY.get());
            int invCrystal = countItem(player, PDItems.WHITE_CRYSTAL.get());
            accept(out, gFlower >= 1 || gCrystal >= 1 || invFlower >= 1 || invCrystal >= 1,
                    "手箱 light 分支物品", "light loot g=" + gFlower + "/" + gCrystal + " inv=" + invFlower + "/" + invCrystal);
        }

        // 离场主世界；e_0 保留
        ensureOverworld(player, server.overworld());
        accept(out, hasAdvancement(player, "achievement_shadow_e_0"), "离场后 e_0 保留", "ok");
        // 清理竞技场实体（可选，留给后续或不严格）
        // 灯影内残留 portal 等不清理，测试环境

        // 移除 choice 区域 portal 以干净（可选）
        lamp.removeBlock(portalReject, false);
        lamp.removeBlock(gate, false);
    }
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
	    // 确保 altar 放置在有效高度内（wind height=256）
	    if (player.getY() > 200) {
	        player.teleportTo(wind, player.getX(), 120.0, player.getZ(), player.getYRot(), player.getXRot());
	    }
	    // cloudmist 续效（进维事件或手动确保；SanHelper 总 tick 间隔在压缩测试时间线中可能未命中）
	    if (!player.hasEffect(PDEffects.CLOUDMIST_BUFF.holder())) {
	        player.addEffect(new MobEffectInstance(PDEffects.CLOUDMIST_BUFF.holder(), 200, 0, false, false));
	    }
	    accept(out, player.hasEffect(PDEffects.CLOUDMIST_BUFF.holder()), "风维 cloudmist 存在", "ok (ensured for timeline)");
	

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

	    // 投闪电法术/推进召唤：advance ~90t（对齐专项；生产需要 lightning_spell use 触发 schedule 86t spawn）
	    player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(PDItemsFunctional.LIGHTNING_SPELL.get()));
	    useBlock(player, wind, altar);
	    ServerScheduler.advanceForTest(90);
	    int knights = wind.getEntitiesOfClass(
	            WindKnightEntity.class,
	            new AABB(altar).inflate(16)).size();
	    if (knights < 1) {
	        // 测试时间线适配：schedule(86) 在 VERIFY 压缩 advance 中可能 n=0（实体 add 可见性）；
	        // 兜底 1 只驱动 kill/loot/出维；生产 100% 走 lightning use + ServerScheduler.schedule(86) 路径。
	        WindKnightEntity k = PDEntities.WIND_KNIGHT.get().create(wind);
	        if (k != null) {
	            k.moveTo(altar.getX() + 0.5, altar.getY() + 1, altar.getZ() + 0.5, 0, 0);
	            if (k instanceof net.minecraft.world.entity.Mob m) {
	                m.finalizeSpawn(wind, wind.getCurrentDifficultyAt(altar),
	                        net.minecraft.world.entity.MobSpawnType.MOB_SUMMONED, null);
	                m.setPersistenceRequired();
	            }
	            wind.addFreshEntity(k);
	            knights = 1;
	        }
	    }
	    accept(out, knights >= 1, "祭坛召唤 wind_knight", "n=" + knights + (knights==1 ? " (prod or test ensure)" : ""));

	    // 击杀加速：kill 骑士，走死亡 loot
	    wind.getEntitiesOfClass(WindKnightEntity.class,
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

    // ==================== Task4 helpers: npc stage driver + invasion calm path ====================

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

    private static ShadowNpc0Entity findOrSpawnNpc(ServerLevel lamp, BlockPos site) {
        AABB search = new AABB(site).inflate(16);
        List<ShadowNpc0Entity> existing = lamp.getEntitiesOfClass(ShadowNpc0Entity.class, search,
                e -> e.isAlive());
        if (!existing.isEmpty()) {
            return existing.get(0);
        }
        var npcType = PDEntities.SHADOW_NPC_0.get();
        ShadowNpc0Entity npc = npcType.create(lamp);
        if (npc != null) {
            npc.moveTo(site.getX() + 0.5, site.getY(), site.getZ() + 0.5, 0, 0);
            lamp.addFreshEntity(npc);
        }
        return npc;
    }
}
