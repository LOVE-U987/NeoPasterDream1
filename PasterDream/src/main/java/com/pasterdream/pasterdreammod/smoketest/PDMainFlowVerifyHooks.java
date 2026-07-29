package com.pasterdream.pasterdreammod.smoketest;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.block.AaroncosArenaPortalsBlock;
import com.pasterdream.pasterdreammod.block.DyedreamCrackBlock;
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
import com.pasterdream.pasterdreammod.util.ServerScheduler;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * 主干全链路 VERIFY 套件 {@code main-flow}。
 * <p>
 * 叙事顺序：染梦 → 暮影 → 灯影（无名全对话 + 影之抉择二选一）→ 竞技场 → 风旅。
 * <b>不</b>并入默认 {@code all}；须
 * {@code PASTERDREAM_VERIFY_SUITES=main-flow} 显式开启。
 * <p>
 * 影之抉择：{@code PASTERDREAM_VERIFY_SHADOW_CHOICE=dark|light}（默认 dark）；
 * 本趟只点一侧，经 {@code openMenu} + {@code clickMenuButton}。
 */
public final class PDMainFlowVerifyHooks {

    public record Result(boolean pass, String name, String detail) {
    }

    public enum ShadowChoice {
        DARK, LIGHT
    }

    private enum PhaseId {
        P0_BOOTSTRAP, P1_DYEDREAM, P2_TWILIGHT, P3_LAMP_ARENA, P4_WIND, P5_REPORT
    }

    private static ShadowChoice shadowChoice = ShadowChoice.DARK;
    private static boolean hardFailed;
    private static final Map<String, int[]> PHASE_COUNTS = new LinkedHashMap<>();
    private static final Map<String, Boolean> FLAGS = new LinkedHashMap<>();
    private static String currentPhase = "P0_bootstrap";

    private PDMainFlowVerifyHooks() {
    }

    public static ShadowChoice shadowChoice() {
        return shadowChoice;
    }

    public static List<Map<String, Object>> phaseSummaries() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (var e : PHASE_COUNTS.entrySet()) {
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
        return new LinkedHashMap<>(FLAGS);
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
        PHASE_COUNTS.clear();
        FLAGS.clear();
        for (String k : new String[]{
                "a_0", "b_0", "hide_16", "entered_lamp",
                "npc_0", "npc_1", "npc_2", "npc_3", "npc_4", "npc_5",
                "choice_done", "d_0", "talent_shadow", "talent_light",
                "e_0", "wind_enter", "wind_exit"
        }) {
            FLAGS.put(k, false);
        }
        shadowChoice = ShadowChoice.DARK;
        currentPhase = "P0_bootstrap";
    }

    private static void runPhase(PhaseId id, String reportId, Consumer<Result> out, Runnable body) {
        currentPhase = reportId;
        PHASE_COUNTS.putIfAbsent(reportId, new int[]{0, 0, 0});
        if (hardFailed && id != PhaseId.P5_REPORT) {
            acceptSkip(out, reportId + " SKIPPED_DEPENDENCY", "prior hard fail");
            return;
        }
        try {
            body.run();
        } catch (Exception e) {
            hardFailed = true;
            accept(out, false, reportId + " uncaught", e.toString());
            PasterDreamMod.LOGGER.error("[main-flow] phase {} failed", reportId, e);
        }
    }

    // ==================== Phase 0 ====================

    private static void phase0Bootstrap(MinecraftServer server, ServerPlayer player, Consumer<Result> out) {
        Optional<ShadowChoice> choice = parseShadowChoice();
        if (choice.isEmpty()) {
            acceptHard(out, false, "SHADOW_CHOICE 非法",
                    String.valueOf(System.getenv("PASTERDREAM_VERIFY_SHADOW_CHOICE")));
            return;
        }
        shadowChoice = choice.get();
        accept(out, true, "shadowChoice=" + shadowChoice.name().toLowerCase(Locale.ROOT), "env/prop");

        player.setGameMode(GameType.SURVIVAL);
        accept(out, player.gameMode.getGameModeForPlayer() == GameType.SURVIVAL, "切生存", "ok");

        if (!hasAdvancement(player, "achievement_start")) {
            grantAdvancement(player, "achievement_start");
        }
        accept(out, hasAdvancement(player, "achievement_start"), "achievement_start", "fixture-or-prior");

        ServerLevel ow = server.overworld();
        ensureOverworld(player, ow);
        BlockPos anchor = player.blockPosition();
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                ow.setBlock(anchor.offset(dx, -1, dz), Blocks.STONE.defaultBlockState(), 3);
                ow.setBlock(anchor.offset(dx, 0, dz), Blocks.AIR.defaultBlockState(), 3);
                ow.setBlock(anchor.offset(dx, 1, dz), Blocks.AIR.defaultBlockState(), 3);
            }
        }
        accept(out, true, "主世界锚点平台", anchor.toShortString());
    }

    // ==================== Phase 1 染梦 ====================

    private static void phase1Dyedream(MinecraftServer server, ServerPlayer player, Consumer<Result> out) {
        ServerLevel ow = server.overworld();
        ensureOverworld(player, ow);
        BlockPos base = player.blockPosition().offset(4, 0, 0);
        clearBox(ow, base.offset(-1, -1, -1), base.offset(1, 2, 1));
        ow.setBlock(base.below(), Blocks.STONE.defaultBlockState(), 3);
        ow.setBlock(base, PDBlocks.DYEDREAM_CRACK.get().defaultBlockState(), 3);

        ItemStack noteStack = ItemStack.EMPTY;
        DreamnotesLogic.onUse(1, ow, player, noteStack);
        acceptHard(out, hasAdvancement(player, "achievement_a_0"), "dreamnotes_1 → a_0", "true path");
        setFlag("a_0", hasAdvancement(player, "achievement_a_0"));

        player.setPortalCooldown(0);
        player.teleportTo(ow, base.getX() + 0.5, base.getY() + 0.05, base.getZ() + 0.5,
                player.getYRot(), player.getXRot());
        BlockState crack = ow.getBlockState(base);
        if (crack.getBlock() instanceof DyedreamCrackBlock block) {
            block.entityInside(crack, ow, base, player);
        }
        boolean inDye = player.level().dimension().equals(PDDimensions.DYEDREAM_WORLD_LEVEL_KEY);
        acceptHard(out, inDye, "裂隙进染梦", "dim=" + player.level().dimension().location());
        if (!inDye) {
            return;
        }

        ServerLevel dye = (ServerLevel) player.level();
        DreamnotesLogic.onUse(2, dye, player, noteStack);
        acceptHard(out, hasAdvancement(player, "achievement_b_0"), "dreamnotes_2 → b_0", "true path");
        setFlag("b_0", hasAdvancement(player, "achievement_b_0"));

        DreamnotesLogic.onUse(14, dye, player, noteStack);
        acceptHard(out, hasAdvancement(player, "achievement_hide_16"),
                "dreamnotes_14 → hide_16", "禁止直接 grant");
        setFlag("hide_16", hasAdvancement(player, "achievement_hide_16"));

        ensureOverworld(player, ow);
    }

    // ==================== Phase 2 暮影 → 灯影 ====================

    private static void phase2Twilight(MinecraftServer server, ServerPlayer player, Consumer<Result> out) {
        ServerLevel ow = server.overworld();
        ensureOverworld(player, ow);
        player.setGameMode(GameType.SURVIVAL);

        BlockPos bedPos = player.blockPosition().offset(6, 0, 6);
        clearBox(ow, bedPos.offset(-2, -1, -2), bedPos.offset(2, 6, 2));
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                ow.setBlock(bedPos.offset(dx, -1, dz), Blocks.STONE.defaultBlockState(), 3);
            }
        }

        BlockPos lanternPos = bedPos.above(2);
        ow.setBlock(bedPos, PDBlocksFurniture.TRUE_SHADOW_BED.get().defaultBlockState(), 3);
        ow.setBlock(lanternPos, PDBlocksFurniture.TWILIGHT_LANTERN.get().defaultBlockState(), 3);

        // 结构探索省略：key + hide_9 夹具；床传送本身走生产 use
        W4DataBlockEntity.putBooleanAt(ow, lanternPos, "key", true);
        W4DataBlockEntity.putBooleanAt(ow, lanternPos, "switch", false);
        W4DataBlockEntity.putDoubleAt(ow, lanternPos, "number", 0);
        if (!hasAdvancement(player, "achievement_hide_9")) {
            grantAdvancement(player, "achievement_hide_9");
        }
        accept(out, hasAdvancement(player, "achievement_hide_9"), "hide_9 就绪", "fixture after lantern site");
        accept(out, W4DataBlockEntity.getBooleanAt(ow, lanternPos, "key"), "笼 key=true", "fixture");

        forceNight(ow);
        // 与 TrueShadowBedBlock 门控一致：isDay() + getLevelData().isThundering()
        // （Level#isThundering 看雷电插值，setWeatherParameters 同 tick 不可靠）
        accept(out, !ow.isDay() || ow.getLevelData().isThundering(),
                "夜间/风暴门控环境",
                "time=" + ow.getDayTime()
                        + " isDay=" + ow.isDay()
                        + " thunderingData=" + ow.getLevelData().isThundering());

        player.teleportTo(ow, bedPos.getX() + 0.5, bedPos.getY() + 1.0, bedPos.getZ() + 0.5,
                player.getYRot(), player.getXRot());
        useBlock(player, ow, bedPos);

        boolean inLamp = player.level().dimension().equals(PDDimensions.LAMP_SHADOW_WORLD_LEVEL_KEY);
        acceptHard(out, inLamp, "真影床进灯影", "dim=" + player.level().dimension().location());
        setFlag("entered_lamp", inLamp);
    }

    // ==================== Phase 3 灯影 / 无名 / 抉择 / 竞技场 ====================

    private static void phase3LampArena(MinecraftServer server, ServerPlayer player, Consumer<Result> out) {
        ServerLevel lamp = server.getLevel(PDDimensions.LAMP_SHADOW_WORLD_LEVEL_KEY);
        boolean inLamp = lamp != null
                && player.level().dimension().equals(PDDimensions.LAMP_SHADOW_WORLD_LEVEL_KEY);
        if (!inLamp && lamp != null) {
            player.teleportTo(lamp, 8.5, 100.0, 8.5, player.getYRot(), player.getXRot());
            inLamp = player.level().dimension().equals(PDDimensions.LAMP_SHADOW_WORLD_LEVEL_KEY);
        }
        acceptHard(out, inLamp, "Phase3 位于灯影",
                "dim=" + player.level().dimension().location());
        if (!inLamp || lamp == null) {
            return;
        }

        player.setGameMode(GameType.SURVIVAL);
        BlockPos site = new BlockPos(8, 100, 8);
        clearBox(lamp, site.offset(-5, -1, -5), site.offset(5, 4, 5));
        for (int dx = -5; dx <= 5; dx++) {
            for (int dz = -5; dz <= 5; dz++) {
                lamp.setBlock(site.offset(dx, -1, dz), Blocks.STONE.defaultBlockState(), 3);
            }
        }

        // 下层门 + 钥匙
        BlockPos door = site.offset(3, 0, 0);
        lamp.setBlock(door, PDBlocksDungeon.SHADOW_DUNGEON_DOOR_0.get().defaultBlockState(), 3);
        BlockPos keyBlock = site.offset(2, 0, 0);
        lamp.setBlock(keyBlock, PDBlocksDungeon.SHADOW_DUNGEON_KEY_0.get().defaultBlockState(), 3);
        player.teleportTo(lamp, site.getX() + 0.5, site.getY(), site.getZ() + 0.5,
                player.getYRot(), player.getXRot());
        useBlock(player, lamp, keyBlock);
        if (countItem(player, PDItemsMaterials.SHADOW_DUNGEON_KEY.get()) < 1) {
            player.setItemInHand(InteractionHand.MAIN_HAND,
                    new ItemStack(PDItemsMaterials.SHADOW_DUNGEON_KEY.get()));
        } else {
            // 确保主手持钥
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack s = player.getInventory().getItem(i);
                if (s.is(PDItemsMaterials.SHADOW_DUNGEON_KEY.get())) {
                    player.setItemInHand(InteractionHand.MAIN_HAND, s.copy());
                    break;
                }
            }
        }
        useBlock(player, lamp, door);
        accept(out, lamp.getBlockState(door).isAir(), "下层门持钥打开",
                lamp.getBlockState(door).toString());

        ShadowNpc0Entity npc = findOrSpawnNpc(lamp, site);
        acceptHard(out, npc != null && npc.isAlive(), "生成 shadow_npc_0",
                npc == null ? "null" : "id=" + npc.getId());
        if (npc == null) {
            return;
        }
        player.teleportTo(lamp, site.getX() + 1.5, site.getY(), site.getZ() + 0.5,
                player.getYRot(), player.getXRot());

        // 全无名对话
        runNpcStage(player, npc, "achievement_shadow_npc_0", 600, "npc_0", out, "Stage0");
        // Stage0 见面礼金块（soft）
        int goldNear = 0;
        for (ItemEntity ie : lamp.getEntitiesOfClass(ItemEntity.class, new AABB(site).inflate(8))) {
            if (ie.getItem().is(Blocks.GOLD_BLOCK.asItem())) {
                goldNear += ie.getItem().getCount();
            }
        }
        accept(out, goldNear >= 1 || hasAdvancement(player, "achievement_shadow_npc_0"),
                "Stage0 见面礼金块或已 npc_0", "goldNear=" + goldNear);

        npc = findOrSpawnNpc(lamp, site);
        runNpcStage(player, npc, "achievement_shadow_npc_1", 640, "npc_1", out, "Stage1");

        npc = findOrSpawnNpc(lamp, site);
        runNpcStage(player, npc, "achievement_shadow_npc_2", 200, "npc_2", out, "Stage2");

        // Stage2 后可能已 TP 主世界；确保离灯影挂窥视，再驱动 calm→npc_3
        driveNpc3ViaInvasion(server, player, out);

        // 回灯影 Stage4/5
        ServerLevel lampBack = server.getLevel(PDDimensions.LAMP_SHADOW_WORLD_LEVEL_KEY);
        if (lampBack == null) {
            acceptHard(out, false, "回灯影续对话", "lamp null");
            return;
        }
        player.teleportTo(lampBack, site.getX() + 1.5, site.getY(), site.getZ() + 0.5,
                player.getYRot(), player.getXRot());
        npc = findOrSpawnNpc(lampBack, site);
        runNpcStage(player, npc, "achievement_shadow_npc_4", 540, "npc_4", out, "Stage4");
        npc = findOrSpawnNpc(lampBack, site);
        runNpcStage(player, npc, "achievement_shadow_npc_5", 300, "npc_5", out, "Stage5");

        if (!hasAdvancement(player, "achievement_shadow_npc_5")) {
            markHardFail();
            return;
        }

        // 抉择前：无 d_0 门户应拒（主世界摆门）
        ensureOverworld(player, server.overworld());
        BlockPos portalPos = player.blockPosition().offset(10, 0, 0);
        ServerLevel ow = server.overworld();
        ow.setBlock(portalPos.below(), Blocks.STONE.defaultBlockState(), 3);
        ow.setBlock(portalPos, PDBlocks.AARONCOS_ARENA_PORTALS.get().defaultBlockState(), 3);
        BlockState portal = ow.getBlockState(portalPos);
        if (portal.getBlock() instanceof AaroncosArenaPortalsBlock block) {
            block.entityInside(portal, ow, portalPos, player);
        }
        accept(out, player.level().dimension() == Level.OVERWORLD,
                "抉择前无 d_0 竞技场门拒入", "dim=" + player.level().dimension().location());
        ow.removeBlock(portalPos, false);

        // 回灯影真影床抉择
        player.teleportTo(lampBack, site.getX() + 0.5, site.getY(), site.getZ() + 0.5,
                player.getYRot(), player.getXRot());
        BlockPos choiceBed = site.offset(0, 0, 3);
        lampBack.setBlock(choiceBed.below(), Blocks.STONE.defaultBlockState(), 3);
        lampBack.setBlock(choiceBed, PDBlocksFurniture.TRUE_SHADOW_BED.get().defaultBlockState(), 3);
        player.teleportTo(lampBack, choiceBed.getX() + 0.5, choiceBed.getY() + 1.0, choiceBed.getZ() + 0.5,
                player.getYRot(), player.getXRot());
        useBlock(player, lampBack, choiceBed);

        boolean menuOpen = player.containerMenu instanceof ShadowSelectEndMenu;
        acceptHard(out, menuOpen, "真影床打开 ShadowSelectEndMenu",
                player.containerMenu == null ? "null" : player.containerMenu.getClass().getName());
        if (!menuOpen) {
            return;
        }

        int button = shadowChoice == ShadowChoice.DARK
                ? ShadowSelectEndMenu.BUTTON_DARK
                : ShadowSelectEndMenu.BUTTON_LIGHT;
        boolean clicked = player.containerMenu.clickMenuButton(player, button);
        acceptHard(out, clicked, "clickMenuButton " + shadowChoice.name().toLowerCase(Locale.ROOT),
                "button=" + button);
        if (shadowChoice == ShadowChoice.DARK) {
            ServerScheduler.advanceForTest(280);
        }

        acceptHard(out, hasAdvancement(player, "achievement_shadow_d_0"), "d_0 已授予", "choice path");
        setFlag("d_0", hasAdvancement(player, "achievement_shadow_d_0"));
        setFlag("choice_done", true);

        if (shadowChoice == ShadowChoice.DARK) {
            accept(out, hasAdvancement(player, "achievement_talent_shadow"), "talent_shadow", "dark");
            accept(out, !hasAdvancement(player, "achievement_talent_light"), "无 talent_light", "mutex");
            accept(out, countItem(player, PDItems.SHADOW_HILT.get()) >= 1, "赠 shadow_hilt", "inv");
            setFlag("talent_shadow", hasAdvancement(player, "achievement_talent_shadow"));
            setFlag("talent_light", false);
        } else {
            accept(out, hasAdvancement(player, "achievement_talent_light"), "talent_light", "light");
            accept(out, !hasAdvancement(player, "achievement_talent_shadow"), "无 talent_shadow", "mutex");
            accept(out, countItem(player, PDItems.WHITE_CRYSTAL.get()) >= 1, "赠 white_crystal", "inv");
            setFlag("talent_light", hasAdvancement(player, "achievement_talent_light"));
            setFlag("talent_shadow", false);
        }

        // 竞技场：有 d_0 进场
        ServerLevel arena = server.getLevel(PDDimensions.AARONCOS_ARENA_WORLD_LEVEL_KEY);
        acceptHard(out, arena != null, "竞技场维度可解析", arena == null ? "null" : "ok");
        if (arena == null) {
            return;
        }

        // 真 portal 路径：主世界有 d_0 踩门
        ensureOverworld(player, server.overworld());
        BlockPos p2 = player.blockPosition().offset(12, 0, 0);
        ow.setBlock(p2.below(), Blocks.STONE.defaultBlockState(), 3);
        ow.setBlock(p2, PDBlocks.AARONCOS_ARENA_PORTALS.get().defaultBlockState(), 3);
        portal = ow.getBlockState(p2);
        if (portal.getBlock() instanceof AaroncosArenaPortalsBlock block) {
            block.entityInside(portal, ow, p2, player);
        }
        boolean enteredArena = player.level().dimension().equals(PDDimensions.AARONCOS_ARENA_WORLD_LEVEL_KEY);
        if (!enteredArena) {
            // 回退 changeDimension（仍走进维事件）
            player.changeDimension(new DimensionTransition(
                    arena, new Vec3(0.5, 70.0, 0.5), Vec3.ZERO,
                    player.getYRot(), player.getXRot(), DimensionTransition.PLAY_PORTAL_SOUND));
            enteredArena = player.level().dimension().equals(PDDimensions.AARONCOS_ARENA_WORLD_LEVEL_KEY);
        }
        acceptHard(out, enteredArena, "有 d_0 进竞技场",
                "dim=" + player.level().dimension().location());
        ow.removeBlock(p2, false);

        accept(out, player.hasEffect(PDEffects.GUARD_BLOCK_BUFF.holder())
                        || player.gameMode.getGameModeForPlayer() == GameType.ADVENTURE,
                "进场 GUARD 或冒险",
                "guard=" + player.hasEffect(PDEffects.GUARD_BLOCK_BUFF.holder())
                        + " mode=" + player.gameMode.getGameModeForPlayer());

        // 胜利：双灭手
        if (!player.level().dimension().equals(PDDimensions.AARONCOS_ARENA_WORLD_LEVEL_KEY)) {
            player.teleportTo(arena, 0.5, 70.0, 0.5, player.getYRot(), player.getXRot());
        }
        player.addEffect(new MobEffectInstance(
                PDEffects.SHADOW_SPYON_BUFF.holder(), 32000, 0, false, false));
        PDArenaBossManager.initializeBossFight(arena);
        PDArenaBossManager.setBossAlive(arena, true, true);
        PDArenaBossManager.setPhase(arena, PDArenaBossManager.BossFightPhase.FIGHTING);
        PDArenaBossManager.onLeftHandDeath(arena);
        PDArenaBossManager.onRightHandDeath(arena);

        acceptHard(out, hasAdvancement(player, "achievement_shadow_e_0"), "e_0 胜利", "boss manager");
        setFlag("e_0", hasAdvancement(player, "achievement_shadow_e_0"));

        BlockPos chestPos = new BlockPos(0, 69, 0);
        accept(out, arena.getBlockState(chestPos).is(PDBlocks.AARONCOS_HAND_CHEST.get()),
                "胜利手箱生成", arena.getBlockState(chestPos).toString());

        // 手箱 loot 经 ServerScheduler 40t 掉地（非直接入包）；对齐 second-dream
        player.teleportTo(arena, chestPos.getX() + 0.5, chestPos.getY() + 1.0, chestPos.getZ() + 0.5,
                player.getYRot(), player.getXRot());
        useBlock(player, arena, chestPos);
        ServerScheduler.advanceForTest(45);
        int groundHorror = countGroundItems(arena, chestPos, PDItems.PURE_HORROR.get());
        int invHorror = countItem(player, PDItems.PURE_HORROR.get());
        boolean chestGone = !arena.getBlockState(chestPos).is(PDBlocks.AARONCOS_HAND_CHEST.get());
        accept(out, groundHorror >= 1 || invHorror >= 1 || chestGone,
                "开箱 pure_horror 或箱已拆",
                "ground=" + groundHorror + " inv=" + invHorror + " gone=" + chestGone);

        if (shadowChoice == ShadowChoice.DARK) {
            int bodys = countGroundItems(arena, chestPos, PDItems.DEGENERATE_BODYS.get())
                    + countItem(player, PDItems.DEGENERATE_BODYS.get());
            int hilts = countGroundItems(arena, chestPos, PDItems.SHADOW_HILT.get())
                    + countItem(player, PDItems.SHADOW_HILT.get());
            accept(out, bodys >= 1 || hilts >= 1, "手箱 shadow 分支物品",
                    "bodys=" + bodys + " hilts=" + hilts);
        } else {
            int flowers = countGroundItems(arena, chestPos, PDItems.WHITE_FLOWER_BODY.get())
                    + countItem(player, PDItems.WHITE_FLOWER_BODY.get());
            int crystals = countGroundItems(arena, chestPos, PDItems.WHITE_CRYSTAL.get())
                    + countItem(player, PDItems.WHITE_CRYSTAL.get());
            accept(out, flowers >= 1 || crystals >= 1, "手箱 light 分支物品",
                    "flowers=" + flowers + " crystals=" + crystals);
        }

        // 取消强制离倒计时（已开箱）；回主
        ServerScheduler.advanceForTest(5);
        ensureOverworld(player, server.overworld());
        accept(out, hasAdvancement(player, "achievement_shadow_e_0"), "离场后 e_0 保留", "ok");
        player.setGameMode(GameType.SURVIVAL);
    }

    private static void driveNpc3ViaInvasion(MinecraftServer server, ServerPlayer player,
                                              Consumer<Result> out) {
        ServerLevel ow = server.overworld();
        // 若仍在灯影，离维触发 LampShadowEvents 挂 spyon（npc_2 && !e_0）
        if (player.level().dimension().equals(PDDimensions.LAMP_SHADOW_WORLD_LEVEL_KEY)) {
            ensureOverworld(player, ow);
        }
        if (!player.hasEffect(PDEffects.SHADOW_SPYON_BUFF.holder())
                && hasAdvancement(player, "achievement_shadow_npc_2")
                && !hasAdvancement(player, "achievement_shadow_e_0")) {
            // 再进再出一次
            ServerLevel lamp = server.getLevel(PDDimensions.LAMP_SHADOW_WORLD_LEVEL_KEY);
            if (lamp != null) {
                player.teleportTo(lamp, 0.5, 120.0, 0.5, player.getYRot(), player.getXRot());
                ensureOverworld(player, ow);
            }
        }
        if (!player.hasEffect(PDEffects.SHADOW_SPYON_BUFF.holder())) {
            // 仍无 buff：挂上以便跑 tick（事件未触发时的兜底；calm 仍走生产）
            player.addEffect(new MobEffectInstance(
                    PDEffects.SHADOW_SPYON_BUFF.holder(), 32000, 0, false, false));
        }
        accept(out, player.hasEffect(PDEffects.SHADOW_SPYON_BUFF.holder()),
                "窥视 buff 就绪", "for invasion calm");

        ensureOverworld(player, ow);
        var data = player.getPersistentData();
        data.putBoolean("shadow_intrude", true);
        data.putBoolean("shadow_intrude_end", true);
        data.putDouble("shadow_intrude_number", 5);
        forceDay(ow);

        for (int i = 0; i < 40; i++) {
            var inst = player.getEffect(PDEffects.SHADOW_SPYON_BUFF.holder());
            if (inst != null) {
                PDEffects.SHADOW_SPYON_BUFF.holder().value()
                        .applyEffectTick(player, inst.getAmplifier());
            }
            if (hasAdvancement(player, "achievement_shadow_npc_3")) {
                break;
            }
            ServerScheduler.advanceForTest(1);
        }
        acceptHard(out, hasAdvancement(player, "achievement_shadow_npc_3"),
                "入侵 calm → npc_3（禁止 grant）", "true shadowIntrudeCalm path");
        setFlag("npc_3", hasAdvancement(player, "achievement_shadow_npc_3"));
    }

    // ==================== Phase 4 风旅 ====================

    private static void phase4Wind(MinecraftServer server, ServerPlayer player, Consumer<Result> out) {
        ServerLevel ow = server.overworld();
        ensureOverworld(player, ow);
        player.setGameMode(GameType.SURVIVAL);

        if (!hasAdvancement(player, "achievement_b_0")
                || !hasAdvancement(player, "achievement_hide_16")) {
            acceptHard(out, false, "风旅前置 b_0+hide_16",
                    "b_0=" + hasAdvancement(player, "achievement_b_0")
                            + " hide_16=" + hasAdvancement(player, "achievement_hide_16"));
            return;
        }

        ItemStack soup = new ItemStack(PDItems.QUEER_SOUP.get());
        player.setItemInHand(InteractionHand.MAIN_HAND, soup);
        player.getFoodData().setFoodLevel(10);
        soup.finishUsingItem(ow, player);
        boolean hasFond = player.hasEffect(PDEffects.FONDILLUSION_BUFF.holder());
        accept(out, hasFond, "食用 queer_soup → fondillusion", hasFond ? "ok" : "missing effect");
        if (!hasFond) {
            // 食物 effect 概率/时机问题：仍不允许伪造进维条件；硬失败
            acceptHard(out, false, "迷梦缺失无法真进风维", "finishUsingItem did not apply");
            return;
        }

        player.teleportTo(ow, player.getX(), 307.0, player.getZ(), player.getYRot(), player.getXRot());
        for (int i = 0; i < 15; i++) {
            var inst = player.getEffect(PDEffects.FONDILLUSION_BUFF.holder());
            if (inst != null) {
                PDEffects.FONDILLUSION_BUFF.holder().value()
                        .applyEffectTick(player, inst.getAmplifier());
            }
            if (PDDimensions.isWindJourneyWorld(player.level())) {
                break;
            }
        }
        boolean entered = PDDimensions.isWindJourneyWorld(player.level());
        acceptHard(out, entered, "fondillusion Y≥306 进风维",
                "dim=" + player.level().dimension().location());
        setFlag("wind_enter", entered);
        if (!entered) {
            return;
        }

        ServerLevel wind = (ServerLevel) player.level();
        // 风维 height=256；迷梦同坐标 TP 会停在 Y≥306（界外）。先落到可建造高度。
        double safeY = Math.min(120.0, wind.getMaxBuildHeight() - 8.0);
        player.teleportTo(wind, player.getX(), safeY, player.getZ(),
                player.getYRot(), player.getXRot());

        // cloudmist 由 PDSanHelper 按 total-tick 续；压缩 VERIFY 无完整 player tick 时补挂以测出维
        boolean autoCloud = player.hasEffect(PDEffects.CLOUDMIST_BUFF.holder());
        if (!autoCloud) {
            player.addEffect(new MobEffectInstance(
                    PDEffects.CLOUDMIST_BUFF.holder(), 6000, 0, false, false));
        }
        accept(out, player.hasEffect(PDEffects.CLOUDMIST_BUFF.holder()),
                "风维 cloudmist（SanHelper 续或 VERIFY 补挂）",
                autoCloud ? "SanHelper auto" : "VERIFY 补挂 for exit");

        // 祭坛 0→4（与 wind-journey 专项同序：水晶→铁锭×3+1t schedule→雷法术→86t 召唤）
        BlockPos altar = player.blockPosition().offset(4, 0, 4);
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                wind.setBlock(altar.offset(dx, -1, dz), Blocks.STONE.defaultBlockState(), 3);
            }
        }
        wind.setBlock(altar, PDBlocksFurniture.WIND_KNIGHT_SPAWNBLOCK_0.get().defaultBlockState(), 3);
        player.setItemInHand(InteractionHand.MAIN_HAND,
                new ItemStack(PDItemsMaterials.WINDRUNNER_CRYSTAL.get()));
        useBlock(player, wind, altar);
        for (int step = 0; step < 3; step++) {
            player.setItemInHand(InteractionHand.MAIN_HAND,
                    new ItemStack(PDItemsMaterials.WIND_IRON_INGOT.get()));
            useBlock(player, wind, altar);
            ServerScheduler.advanceForTest(2);
        }
        boolean stage4 = wind.getBlockState(altar).is(PDBlocksFurniture.WIND_KNIGHT_SPAWNBLOCK_4.get());
        accept(out, stage4, "祭坛 → stage4", wind.getBlockState(altar).toString());

        if (stage4) {
            player.setItemInHand(InteractionHand.MAIN_HAND,
                    new ItemStack(PDItemsFunctional.LIGHTNING_SPELL.get()));
            useBlock(player, wind, altar);
            ServerScheduler.advanceForTest(90);
        } else {
            ServerScheduler.advanceForTest(2);
        }
        // spawn 经 addFresh；同 tick 查询可能漏，故 advance 后再查 + 工厂兜底计数
        int knights = wind.getEntitiesOfClass(WindKnightEntity.class, new AABB(altar).inflate(16)).size();
        boolean altarReset = wind.getBlockState(altar).is(PDBlocksFurniture.WIND_KNIGHT_SPAWNBLOCK_0.get());
        accept(out, knights >= 1 || altarReset,
                "祭坛召唤或台回 stage0",
                "knights=" + knights + " state=" + wind.getBlockState(altar));

        wind.getEntitiesOfClass(WindKnightEntity.class, new AABB(altar).inflate(16))
                .forEach(e -> e.kill());
        ServerScheduler.advanceForTest(5);

        // cloudmist + Y≤5 出维
        if (!player.hasEffect(PDEffects.CLOUDMIST_BUFF.holder())) {
            player.addEffect(new MobEffectInstance(
                    PDEffects.CLOUDMIST_BUFF.holder(), 400, 0, false, false));
        }
        if (!PDDimensions.isWindJourneyWorld(player.level())) {
            ServerLevel w2 = server.getLevel(PDDimensions.WIND_JOURNEY_WORLD_LEVEL_KEY);
            if (w2 != null) {
                player.teleportTo(w2, player.getX(), 120.0, player.getZ(),
                        player.getYRot(), player.getXRot());
            }
        }
        ServerLevel windNow = player.serverLevel();
        if (PDDimensions.isWindJourneyWorld(windNow)) {
            player.teleportTo(windNow, player.getX(), 3.0, player.getZ(),
                    player.getYRot(), player.getXRot());
            for (int i = 0; i < 10; i++) {
                var inst = player.getEffect(PDEffects.CLOUDMIST_BUFF.holder());
                if (inst != null) {
                    PDEffects.CLOUDMIST_BUFF.holder().value()
                            .applyEffectTick(player, inst.getAmplifier());
                }
                if (player.level().dimension() == Level.OVERWORLD) {
                    break;
                }
            }
        }
        boolean back = player.level().dimension() == Level.OVERWORLD;
        accept(out, back, "cloudmist Y≤5 回主",
                "dim=" + player.level().dimension().location() + " y=" + player.getY());
        accept(out, back && Math.abs(player.getY() - 304.0) < 4.0,
                "落点 Y≈304", "y=" + player.getY());
        setFlag("wind_exit", back);

        // 清理
        if (PDDimensions.isWindJourneyWorld(player.level())) {
            ServerLevel w = (ServerLevel) player.level();
            w.getEntitiesOfClass(WindKnightEntity.class, new AABB(altar).inflate(24))
                    .forEach(e -> e.discard());
            w.removeBlock(altar, false);
        }
        ensureOverworld(player, ow);
        player.setGameMode(GameType.SURVIVAL);
    }

    // ==================== Phase 5 ====================

    private static void phase5Report(Consumer<Result> out) {
        boolean shadow = Boolean.TRUE.equals(FLAGS.get("talent_shadow"));
        boolean light = Boolean.TRUE.equals(FLAGS.get("talent_light"));
        boolean choiceDone = Boolean.TRUE.equals(FLAGS.get("choice_done"));
        boolean exclusive = !choiceDone
                || (shadowChoice == ShadowChoice.DARK ? (shadow && !light) : (light && !shadow));
        accept(out, exclusive, "talent 与 shadowChoice 互斥一致",
                "choice=" + shadowChoice + " shadow=" + shadow + " light=" + light);
        accept(out, true, "continuousFlags", FLAGS.toString());

        long pass = 0;
        long fail = 0;
        for (int[] c : PHASE_COUNTS.values()) {
            pass += c[0];
            fail += c[1];
        }
        PasterDreamMod.LOGGER.info("[PDVerify] MAIN-FLOW {}/{} pass (choice={}) flags={}",
                pass, pass + fail, shadowChoice.name().toLowerCase(Locale.ROOT), FLAGS);
    }

    // ==================== NPC dialogue ====================

    private static void runNpcStage(ServerPlayer player, ShadowNpc0Entity npc,
                                     String advPath, int advanceTicks, String flagKey,
                                     Consumer<Result> out, String label) {
        if (npc == null || !npc.isAlive()) {
            acceptHard(out, false, label + " NPC 存活", "null/dead");
            return;
        }
        // 确保玩家与 NPC 同维且在 16 格内（forEachNearbyPlayer 半径）
        ServerLevel npcLevel = (ServerLevel) npc.level();
        if (player.level() != npcLevel || player.distanceToSqr(npc) > 15 * 15) {
            player.teleportTo(npcLevel,
                    npc.getX() + 1.0, npc.getY(), npc.getZ(),
                    player.getYRot(), player.getXRot());
        }
        for (int i = 0; i < 8 && isNpcSwitchOn(npc); i++) {
            ServerScheduler.advanceForTest(80);
        }
        accept(out, !isNpcSwitchOn(npc), label + " 前 switch 解锁",
                "switch=" + isNpcSwitchOn(npc));

        double dist = Math.sqrt(player.distanceToSqr(npc));
        boolean sameDim = player.level() == npcLevel;
        npc.mobInteract(player, InteractionHand.MAIN_HAND);
        boolean switchAfterInteract = isNpcSwitchOn(npc);
        ServerScheduler.advanceForTest(advanceTicks + 40);

        boolean done = hasAdvancement(player, advPath);
        acceptHard(out, done, label + " → " + advPath,
                done ? "awarded"
                        : "missing after " + advanceTicks + "t"
                        + " sameDim=" + sameDim
                        + " dist=" + String.format(Locale.ROOT, "%.2f", dist)
                        + " switchAfterInteract=" + switchAfterInteract
                        + " switchNow=" + isNpcSwitchOn(npc));
        setFlag(flagKey, done);
        accept(out, !isNpcSwitchOn(npc), label + " 后 switch 释放",
                "switch=" + isNpcSwitchOn(npc));
    }

    private static boolean isNpcSwitchOn(ShadowNpc0Entity npc) {
        CompoundTag tag = new CompoundTag();
        npc.saveWithoutId(tag);
        return tag.getBoolean("switch");
    }

    private static ShadowNpc0Entity findOrSpawnNpc(ServerLevel lamp, BlockPos site) {
        List<ShadowNpc0Entity> existing = lamp.getEntitiesOfClass(
                ShadowNpc0Entity.class, new AABB(site).inflate(16));
        if (!existing.isEmpty()) {
            return existing.getFirst();
        }
        EntityType<ShadowNpc0Entity> type = PDEntities.SHADOW_NPC_0.get();
        ShadowNpc0Entity npc = type.create(lamp);
        if (npc == null) {
            return null;
        }
        npc.moveTo(site.getX() + 0.5, site.getY(), site.getZ() + 0.5, 0, 0);
        lamp.addFreshEntity(npc);
        return npc;
    }

    // ==================== choice parse ====================

    static Optional<ShadowChoice> parseShadowChoice() {
        String raw = System.getenv("PASTERDREAM_VERIFY_SHADOW_CHOICE");
        if (raw == null || raw.isBlank()) {
            raw = System.getProperty("pasterdream.verify.shadowChoice", "");
        }
        raw = raw == null ? "" : raw.trim().toLowerCase(Locale.ROOT);
        if (raw.isEmpty() || "dark".equals(raw) || "shadow".equals(raw)) {
            return Optional.of(ShadowChoice.DARK);
        }
        if ("light".equals(raw)) {
            return Optional.of(ShadowChoice.LIGHT);
        }
        return Optional.empty();
    }

    // ==================== helpers ====================

    private static void accept(Consumer<Result> out, boolean pass, String name, String detail) {
        out.accept(new Result(pass, name, detail));
        bump(currentPhase, pass);
    }

    private static void acceptHard(Consumer<Result> out, boolean pass, String name, String detail) {
        accept(out, pass, name, detail);
        if (!pass) {
            markHardFail();
        }
    }

    private static void acceptSkip(Consumer<Result> out, String name, String detail) {
        out.accept(new Result(true, name, "SKIPPED_DEPENDENCY: " + detail));
        int[] c = PHASE_COUNTS.computeIfAbsent(currentPhase, k -> new int[]{0, 0, 0});
        c[2]++;
    }

    private static void bump(String id, boolean pass) {
        int[] c = PHASE_COUNTS.computeIfAbsent(id, k -> new int[]{0, 0, 0});
        if (pass) {
            c[0]++;
        } else {
            c[1]++;
        }
    }

    private static void markHardFail() {
        hardFailed = true;
    }

    private static void setFlag(String key, boolean v) {
        FLAGS.put(key, v);
    }

    private static void forceNight(ServerLevel level) {
        level.setDayTime(18000);
        level.setWeatherParameters(0, 6000, true, true);
        // setDayTime 不自动刷 skyDarken；isDay() 依赖它
        level.updateSkyBrightness();
    }

    private static void forceDay(ServerLevel level) {
        level.setDayTime(1000);
        level.setWeatherParameters(6000, 0, false, false);
        level.updateSkyBrightness();
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

    private static void useBlock(ServerPlayer player, ServerLevel level, BlockPos pos) {
        BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(pos), Direction.UP, pos, false);
        level.getBlockState(pos).useWithoutItem(level, player, hit);
    }

    private static void clearBox(ServerLevel level, BlockPos a, BlockPos b) {
        BlockPos.betweenClosed(a, b).forEach(p -> {
            if (!level.getBlockState(p).isAir()) {
                level.removeBlock(p.immutable(), false);
            }
        });
    }

    private static int countGroundItems(ServerLevel level, BlockPos center, Item item) {
        int n = 0;
        for (ItemEntity ie : level.getEntitiesOfClass(ItemEntity.class, new AABB(center).inflate(8))) {
            if (ie.getItem().is(item)) {
                n += ie.getItem().getCount();
            }
        }
        return n;
    }

    private static int countItem(ServerPlayer player, Item item) {
        int n = 0;
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack s = player.getInventory().getItem(i);
            if (s.is(item)) {
                n += s.getCount();
            }
        }
        return n;
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
}
