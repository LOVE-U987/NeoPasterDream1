package com.pasterdream.pasterdreammod.smoketest;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.api.util.ServerScheduler;
import com.pasterdream.pasterdreammod.entity.mob.ShadowHandEntity;
import com.pasterdream.pasterdreammod.entity.mob.ShadowNpc0Entity;
import com.pasterdream.pasterdreammod.entity.mob.WeakenessTerrorbeakEntity;
import com.pasterdream.pasterdreammod.registry.LampShadowEvents;
import com.pasterdream.pasterdreammod.registry.PDDimensions;
import com.pasterdream.pasterdreammod.registry.PDEffects;
import com.pasterdream.pasterdreammod.registry.PDEntities;
import com.pasterdream.pasterdreammod.registry.PDSounds;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.function.Consumer;

/**
 * 暗影入侵 VERIFY 套件 {@code shadow-intrude}。
 * <p>
 * 覆盖：注册面、离灯影挂 {@code shadow_spyon_buff} 门控、牛奶不可清、
 * 白天强制 calm → {@code npc_3}、清剿 calm（尖喙阻挡 → 清除后平息）、
 * {@code MOB_SUMMONED} 刷怪实体可用、calm 后无名 Stage4 门控。
 * <p>
 * <b>确定性</b>：不依赖 Phase1 {@code 0.0005} / Phase2 {@code 0.02} 随机；
 * 与 main-flow 一样写 persistent + 泵真实 {@code applyEffectTick}。
 * <p>
 * <b>不</b>并入默认 {@code all}；须
 * {@code PASTERDREAM_VERIFY_SUITES=shadow-intrude}。
 * <p>
 * 扫描结论见仓库 {@code docs/暗影入侵事件流程扫描.md}。
 */
public final class PDShadowIntrudeVerifyHooks {

    public record Result(boolean pass, String name, String detail) {
    }

    private static final String ADV_NPC_2 = "achievement_shadow_npc_2";
    private static final String ADV_NPC_3 = "achievement_shadow_npc_3";
    private static final String ADV_NPC_4 = "achievement_shadow_npc_4";
    private static final String ADV_NPC_5 = "achievement_shadow_npc_5";
    private static final String ADV_E_0 = "achievement_shadow_e_0";
    private static final String ADV_NPC_0 = "achievement_shadow_npc_0";
    private static final String ADV_NPC_1 = "achievement_shadow_npc_1";

    private PDShadowIntrudeVerifyHooks() {
    }

    public static void verify(MinecraftServer server, ServerPlayer player, Consumer<Result> out) {
        if (server == null) {
            out.accept(new Result(false, "shadow-intrude-skip", "server == null"));
            return;
        }
        verifyRegistrations(server, out);
        if (player == null) {
            out.accept(new Result(false, "shadow-intrude-player-skip", "player == null"));
            return;
        }

        player.setGameMode(GameType.SURVIVAL);
        ServerLevel overworld = server.overworld();
        ensureOverworld(player, overworld);
        prepareDarkPlatform(overworld, player.blockPosition());

        verifyBuffGrantGates(player, server, out);
        verifyMilkUncurable(player, out);
        verifyDaytimeCalmAwardsNpc3(player, server, out);
        verifyClearanceBlockedThenCalm(player, server, out);
        verifySummonedEntitiesSpawn(player, overworld, out);
        verifyStage4AfterNpc3(player, server, out);
        verifyRepeatCalmKeepsNpc3(player, server, out);

        // 收尾：避免污染后续同 JVM 套件
        cleanupInvasionState(player);
    }

    // ==================== 注册面 ====================

    private static void verifyRegistrations(MinecraftServer server, Consumer<Result> out) {
        out.accept(ok(PDEffects.SHADOW_SPYON_BUFF != null && PDEffects.SHADOW_SPYON_BUFF.holder() != null,
                "效果 shadow_spyon_buff 注册",
                PDEffects.SHADOW_SPYON_BUFF == null ? "null"
                        : String.valueOf(BuiltInRegistries.MOB_EFFECT.getKey(
                        PDEffects.SHADOW_SPYON_BUFF.holder().value()))));

        out.accept(ok(PDEntities.WEAKENESS_TERRORBEAK.get() != null,
                "实体 weakeness_terrorbeak 注册",
                String.valueOf(BuiltInRegistries.ENTITY_TYPE.getKey(PDEntities.WEAKENESS_TERRORBEAK.get()))));
        out.accept(ok(PDEntities.SHADOW_HAND.get() != null,
                "实体 shadow_hand 注册",
                String.valueOf(BuiltInRegistries.ENTITY_TYPE.getKey(PDEntities.SHADOW_HAND.get()))));
        out.accept(ok(PDEntities.SHADOW_NPC_0.get() != null,
                "实体 shadow_npc_0 注册",
                String.valueOf(BuiltInRegistries.ENTITY_TYPE.getKey(PDEntities.SHADOW_NPC_0.get()))));

        out.accept(ok(PDSounds.SHADOW_DOOR.get() != null,
                "音效 shadow_door 注册",
                String.valueOf(BuiltInRegistries.SOUND_EVENT.getKey(PDSounds.SHADOW_DOOR.get()))));

        ServerLevel lamp = server.getLevel(PDDimensions.LAMP_SHADOW_WORLD_LEVEL_KEY);
        out.accept(ok(lamp != null, "维度 lamp_shadow_world 可解析",
                lamp == null ? "null" : lamp.dimension().location().toString()));

        for (String adv : new String[]{ADV_NPC_2, ADV_NPC_3, ADV_NPC_4, ADV_E_0}) {
            AdvancementHolder h = server.getAdvancements()
                    .get(ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, adv));
            out.accept(ok(h != null, "成就可加载 " + adv, h == null ? "missing" : "ok"));
        }
    }

    // ==================== buff 门控（LampShadowEvents） ====================

    /**
     * 直接派发 {@link PlayerEvent.PlayerChangedDimensionEvent}（from 灯影 → 主世界），
     * 对齐生产离维路径，不依赖真实区块卸载时序。
     */
    private static void verifyBuffGrantGates(ServerPlayer player, MinecraftServer server, Consumer<Result> out) {
        // 1) 无 npc_2 → 不挂
        revokeAdvancement(player, ADV_NPC_2);
        revokeAdvancement(player, ADV_E_0);
        player.removeEffect(PDEffects.SHADOW_SPYON_BUFF.holder());
        fireLeaveLampShadow(player);
        out.accept(ok(!player.hasEffect(PDEffects.SHADOW_SPYON_BUFF.holder()),
                "离灯影无 npc_2 不挂 spyon",
                "spyon=" + player.hasEffect(PDEffects.SHADOW_SPYON_BUFF.holder())));

        // 2) npc_2 && !e_0 → 挂 32000t
        grantAdvancement(player, ADV_NPC_2);
        revokeAdvancement(player, ADV_E_0);
        player.removeEffect(PDEffects.SHADOW_SPYON_BUFF.holder());
        fireLeaveLampShadow(player);
        MobEffectInstance inst = player.getEffect(PDEffects.SHADOW_SPYON_BUFF.holder());
        boolean granted = inst != null;
        boolean durationOk = inst != null && inst.getDuration() >= 31000; // 允许 1 tick 损耗
        out.accept(ok(granted, "离灯影 npc_2&&!e_0 挂 spyon",
                granted ? "duration=" + inst.getDuration() : "missing"));
        out.accept(ok(durationOk, "spyon 时长约 32000t",
                inst == null ? "n/a" : "duration=" + inst.getDuration()));

        // 3) 已 e_0 → 不挂
        grantAdvancement(player, ADV_E_0);
        player.removeEffect(PDEffects.SHADOW_SPYON_BUFF.holder());
        fireLeaveLampShadow(player);
        out.accept(ok(!player.hasEffect(PDEffects.SHADOW_SPYON_BUFF.holder()),
                "离灯影已 e_0 不挂 spyon",
                "spyon=" + player.hasEffect(PDEffects.SHADOW_SPYON_BUFF.holder())));

        // 后续用例需要可入侵：清 e_0，保留 npc_2
        revokeAdvancement(player, ADV_E_0);
        player.removeEffect(PDEffects.SHADOW_SPYON_BUFF.holder());
    }

    private static void fireLeaveLampShadow(ServerPlayer player) {
        LampShadowEvents.onPlayerChangedDimension(
                new PlayerEvent.PlayerChangedDimensionEvent(
                        player,
                        PDDimensions.LAMP_SHADOW_WORLD_LEVEL_KEY,
                        Level.OVERWORLD));
    }

    // ==================== 牛奶不可清 ====================

    private static void verifyMilkUncurable(ServerPlayer player, Consumer<Result> out) {
        player.removeEffect(PDEffects.SHADOW_SPYON_BUFF.holder());
        player.addEffect(new MobEffectInstance(
                PDEffects.SHADOW_SPYON_BUFF.holder(), 32000, 0, false, false));
        MobEffectInstance inst = player.getEffect(PDEffects.SHADOW_SPYON_BUFF.holder());
        boolean emptyCures = inst != null && inst.getCures().isEmpty();
        out.accept(ok(emptyCures, "spyon cures 为空（牛奶不可清）",
                inst == null ? "no inst" : "cures=" + inst.getCures().size()));
        player.removeEffect(PDEffects.SHADOW_SPYON_BUFF.holder());
    }

    // ==================== 白天强制 calm → npc_3 ====================

    private static void verifyDaytimeCalmAwardsNpc3(ServerPlayer player, MinecraftServer server,
                                                     Consumer<Result> out) {
        ensureOverworld(player, server.overworld());
        revokeAdvancement(player, ADV_NPC_3);
        cleanupFlags(player);
        ensureSpyon(player);

        CompoundTag data = player.getPersistentData();
        data.putBoolean("shadow_intrude", true);
        data.putBoolean("shadow_intrude_end", true);
        data.putDouble("shadow_intrude_number", 5);

        forceDay(server.overworld());
        ensureOverworld(player, server.overworld());
        int pumps = pumpSpyonTicks(player, 40);

        boolean npc3 = hasAdvancement(player, ADV_NPC_3);
        boolean flagsClear = !data.getBoolean("shadow_intrude") && !data.getBoolean("shadow_intrude_end");
        boolean buffGone = !player.hasEffect(PDEffects.SHADOW_SPYON_BUFF.holder());

        out.accept(ok(npc3, "白天 calm → npc_3（真 shadowIntrudeCalm，禁止 grant）",
                "pumps=" + pumps + " npc3=" + npc3));
        out.accept(ok(flagsClear, "白天 calm 后 intrude/end 已清",
                "intrude=" + data.getBoolean("shadow_intrude")
                        + " end=" + data.getBoolean("shadow_intrude_end")));
        out.accept(ok(buffGone, "首次 calm 移除 spyon",
                "spyon=" + player.hasEffect(PDEffects.SHADOW_SPYON_BUFF.holder())));
    }

    // ==================== 清剿：尖喙阻挡 → 清除后平息 ====================

    private static void verifyClearanceBlockedThenCalm(ServerPlayer player, MinecraftServer server,
                                                      Consumer<Result> out) {
        ServerLevel ow = server.overworld();
        ensureOverworld(player, ow);
        revokeAdvancement(player, ADV_NPC_3);
        cleanupFlags(player);
        ensureSpyon(player);

        BlockPos base = player.blockPosition();
        prepareDarkPlatform(ow, base);
        // 清附近残留尖喙
        discardNearbyTerrorbeaks(ow, base, 40);

        CompoundTag data = player.getPersistentData();
        data.putBoolean("shadow_intrude", true);
        data.putBoolean("shadow_intrude_end", true);
        data.putDouble("shadow_intrude_number", 5);

        forceNight(ow);
        ensureOverworld(player, ow);

        WeakenessTerrorbeakEntity blocker = PDEntities.WEAKENESS_TERRORBEAK.get()
                .spawn(ow, base.offset(2, 0, 0), MobSpawnType.MOB_SUMMONED);
        out.accept(ok(blocker != null && blocker.isAlive(), "清剿阻挡：刷出 weakeness_terrorbeak",
                blocker == null ? "null" : "id=" + blocker.getId()));

        pumpSpyonTicks(player, 15);
        boolean stillIntrude = data.getBoolean("shadow_intrude");
        boolean noNpc3Yet = !hasAdvancement(player, ADV_NPC_3);
        out.accept(ok(stillIntrude && noNpc3Yet,
                "32 格内有尖喙时不清剿 calm",
                "intrude=" + stillIntrude + " npc3=" + !noNpc3Yet));

        if (blocker != null) {
            blocker.discard();
        }
        discardNearbyTerrorbeaks(ow, base, 40);

        // 夜间 end + 无尖喙 → Phase3 calm（或若 isDay 误判则仍可走白天分支）
        int pumps = pumpSpyonTicks(player, 40);
        boolean npc3 = hasAdvancement(player, ADV_NPC_3);
        boolean flagsClear = !data.getBoolean("shadow_intrude");
        out.accept(ok(npc3, "清除尖喙后清剿/平息 → npc_3",
                "pumps=" + pumps + " npc3=" + npc3
                        + " isDay=" + ow.isDay()));
        out.accept(ok(flagsClear, "清剿 calm 后 intrude 已清",
                "intrude=" + data.getBoolean("shadow_intrude")));
    }

    // ==================== MOB_SUMMONED 实体可生成 ====================

    private static void verifySummonedEntitiesSpawn(ServerPlayer player, ServerLevel level,
                                                   Consumer<Result> out) {
        BlockPos pos = player.blockPosition().offset(3, 0, 3);
        prepareDarkPlatform(level, pos.offset(-1, 0, -1));

        var terror = PDEntities.WEAKENESS_TERRORBEAK.get()
                .spawn(level, pos, MobSpawnType.MOB_SUMMONED);
        var hand = PDEntities.SHADOW_HAND.get()
                .spawn(level, pos.offset(1, 0, 0), MobSpawnType.MOB_SUMMONED);

        out.accept(ok(terror != null && terror.isAlive(),
                "MOB_SUMMONED 生成 weakeness_terrorbeak",
                terror == null ? "null" : terror.getClass().getSimpleName()));
        out.accept(ok(hand != null && hand.isAlive(),
                "MOB_SUMMONED 生成 shadow_hand",
                hand == null ? "null" : hand.getClass().getSimpleName()));

        if (terror != null) {
            terror.discard();
        }
        if (hand != null) {
            hand.discard();
        }
    }

    // ==================== npc_3 → Stage4 ====================

    private static void verifyStage4AfterNpc3(ServerPlayer player, MinecraftServer server,
                                               Consumer<Result> out) {
        ServerLevel lamp = server.getLevel(PDDimensions.LAMP_SHADOW_WORLD_LEVEL_KEY);
        if (lamp == null) {
            out.accept(new Result(false, "Stage4 跳过：无灯影维", "null"));
            return;
        }

        // 门控：done3 && !done4；并需 done0..2 以免落回更早 stage
        grantAdvancement(player, ADV_NPC_0);
        grantAdvancement(player, ADV_NPC_1);
        grantAdvancement(player, ADV_NPC_2);
        if (!hasAdvancement(player, ADV_NPC_3)) {
            // 前序用例应已授；兜底不直接 grant npc_3（保持「生产 calm 授予」原则）
            // 若缺失则用白天 calm 再走一次
            ensureSpyon(player);
            CompoundTag data = player.getPersistentData();
            data.putBoolean("shadow_intrude", true);
            data.putBoolean("shadow_intrude_end", true);
            forceDay(server.overworld());
            ensureOverworld(player, server.overworld());
            pumpSpyonTicks(player, 30);
        }
        out.accept(ok(hasAdvancement(player, ADV_NPC_3), "Stage4 前已有 npc_3",
                hasAdvancement(player, ADV_NPC_3) ? "ok" : "missing — 前序 calm 失败"));
        if (!hasAdvancement(player, ADV_NPC_3)) {
            return;
        }

        revokeAdvancement(player, ADV_NPC_4);
        revokeAdvancement(player, ADV_NPC_5);

        BlockPos site = new BlockPos(8, 100, 8);
        clearBox(lamp, site.offset(-2, -1, -2), site.offset(2, 3, 2));
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                lamp.setBlock(site.offset(dx, -1, dz), Blocks.STONE.defaultBlockState(), 3);
            }
        }

        ShadowNpc0Entity npc = PDEntities.SHADOW_NPC_0.get().create(lamp);
        out.accept(ok(npc != null, "生成 shadow_npc_0 for Stage4", npc == null ? "null" : "ok"));
        if (npc == null) {
            return;
        }
        npc.moveTo(site.getX() + 0.5, site.getY(), site.getZ() + 0.5, 0, 0);
        lamp.addFreshEntity(npc);
        player.teleportTo(lamp, site.getX() + 1.5, site.getY(), site.getZ() + 0.5,
                player.getYRot(), player.getXRot());

        npc.mobInteract(player, InteractionHand.MAIN_HAND);
        // Stage4 约 500t 授 npc_4
        ServerScheduler.advanceForTest(560);

        boolean npc4 = hasAdvancement(player, ADV_NPC_4);
        out.accept(ok(npc4, "npc_3 后门控 Stage4 → npc_4（真 mobInteract）",
                npc4 ? "awarded" : "missing after advance 560"));

        npc.discard();
        ensureOverworld(player, server.overworld());
    }

    // ==================== 已 npc_3 后重复 calm 不重授 / 仍清 flag ====================

    private static void verifyRepeatCalmKeepsNpc3(ServerPlayer player, MinecraftServer server,
                                                   Consumer<Result> out) {
        if (!hasAdvancement(player, ADV_NPC_3)) {
            out.accept(new Result(true, "重复 calm 跳过（无 npc_3）", "prior fail"));
            return;
        }
        ensureOverworld(player, server.overworld());
        // 已 npc_3：再挂 buff + 设 flag + 白天 calm → 应清 flag，不 remove buff（原版语义）
        ensureSpyon(player);
        CompoundTag data = player.getPersistentData();
        data.putBoolean("shadow_intrude", true);
        data.putBoolean("shadow_intrude_end", true);
        forceDay(server.overworld());
        pumpSpyonTicks(player, 20);

        boolean stillNpc3 = hasAdvancement(player, ADV_NPC_3);
        boolean flagsClear = !data.getBoolean("shadow_intrude");
        boolean buffRemains = player.hasEffect(PDEffects.SHADOW_SPYON_BUFF.holder());
        out.accept(ok(stillNpc3 && flagsClear, "已 npc_3 重复 calm：成就保留且 flag 清",
                "npc3=" + stillNpc3 + " intrude=" + data.getBoolean("shadow_intrude")));
        out.accept(ok(buffRemains, "已 npc_3 重复 calm：不移除 spyon（原版语义）",
                "spyon=" + buffRemains));
    }

    // ==================== helpers ====================

    private static Result ok(boolean pass, String name, String detail) {
        return new Result(pass, name, detail);
    }

    private static void ensureSpyon(ServerPlayer player) {
        if (!player.hasEffect(PDEffects.SHADOW_SPYON_BUFF.holder())) {
            player.addEffect(new MobEffectInstance(
                    PDEffects.SHADOW_SPYON_BUFF.holder(), 32000, 0, false, false));
        }
    }

    /** @return 实际泵送次数 */
    private static int pumpSpyonTicks(ServerPlayer player, int max) {
        int i = 0;
        for (; i < max; i++) {
            var inst = player.getEffect(PDEffects.SHADOW_SPYON_BUFF.holder());
            if (inst != null) {
                PDEffects.SHADOW_SPYON_BUFF.holder().value()
                        .applyEffectTick(player, inst.getAmplifier());
            }
            ServerScheduler.advanceForTest(1);
        }
        return i;
    }

    private static void forceDay(ServerLevel level) {
        level.setDayTime(1000);
        level.setWeatherParameters(6000, 0, false, false);
        level.updateSkyBrightness();
    }

    private static void forceNight(ServerLevel level) {
        level.setDayTime(18000);
        level.setWeatherParameters(0, 6000, true, false);
        level.updateSkyBrightness();
    }

    private static void prepareDarkPlatform(ServerLevel level, BlockPos center) {
        // 石台 + 头顶清空，避免刷怪/站立失败；无光源
        for (int dx = -4; dx <= 4; dx++) {
            for (int dz = -4; dz <= 4; dz++) {
                level.setBlock(center.offset(dx, -1, dz), Blocks.STONE.defaultBlockState(), 3);
                for (int dy = 0; dy <= 3; dy++) {
                    level.setBlock(center.offset(dx, dy, dz), Blocks.AIR.defaultBlockState(), 3);
                }
            }
        }
    }

    private static void clearBox(ServerLevel level, BlockPos a, BlockPos b) {
        BlockPos.betweenClosed(a, b).forEach(p -> {
            if (!level.getBlockState(p).isAir()) {
                level.removeBlock(p.immutable(), false);
            }
        });
    }

    private static void discardNearbyTerrorbeaks(ServerLevel level, BlockPos center, double range) {
        for (WeakenessTerrorbeakEntity e : level.getEntitiesOfClass(
                WeakenessTerrorbeakEntity.class, new AABB(center).inflate(range))) {
            e.discard();
        }
        for (ShadowHandEntity e : level.getEntitiesOfClass(
                ShadowHandEntity.class, new AABB(center).inflate(range))) {
            e.discard();
        }
    }

    private static void cleanupFlags(ServerPlayer player) {
        CompoundTag data = player.getPersistentData();
        data.putBoolean("shadow_intrude", false);
        data.putBoolean("shadow_intrude_end", false);
        data.putDouble("shadow_intrude_number", 0);
    }

    private static void cleanupInvasionState(ServerPlayer player) {
        cleanupFlags(player);
        player.removeEffect(PDEffects.SHADOW_SPYON_BUFF.holder());
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
}
