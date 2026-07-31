package com.pasterdream.pasterdreammod.smoketest;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.registry.PDArenaBossManager;
import com.pasterdream.pasterdreammod.registry.PDAdvancements;
import com.pasterdream.pasterdreammod.registry.PDBlocks;
import com.pasterdream.pasterdreammod.registry.PDDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

import java.util.function.Consumer;

/**
 * BOSS 胜利离场冷却 VERIFY 套件 {@code arena-exit}。
 * <p>
 * 回归验证「击败 BOSS 后被传送回返回传送门，却立即再次进入竞技场」bug 的修复：
 * <ul>
 *   <li>胜利/离场传送（{@link PDArenaBossManager#teleportPlayersToOverworld}）会写入玩家离场冷却；</li>
 *   <li>冷却期内返回传送门（{@code aaroncos_arena_portals}）对离场玩家不响应（不再重进竞技场）；</li>
 *   <li>冷却过期后传送门恢复正常可进入。</li>
 * </ul>
 * 使用真实 {@code entityInside} 触发链（零位移 {@code move} → {@code tryCheckInsideBlocks}）。
 * <p>
 * <b>不</b>并入默认 {@code all}；须 {@code PASTERDREAM_VERIFY_SUITES=arena-exit}。
 */
public final class PDArenaExitVerifyHooks {

    public record Result(boolean pass, String name, String detail) {
    }

    private PDArenaExitVerifyHooks() {
    }

    public static void verify(MinecraftServer server, ServerPlayer player, Consumer<Result> out) {
        if (server == null || player == null) {
            out.accept(new Result(false, "arena-exit-skip", "server/player null"));
            return;
        }
        ServerLevel arena = server.getLevel(PDDimensions.AARONCOS_ARENA_WORLD_LEVEL_KEY);
        if (arena == null) {
            out.accept(new Result(false, "arena-exit-skip", "arena dimension not loaded"));
            return;
        }

        // 注册面
        out.accept(ok(BuiltInRegistries.BLOCK.containsKey(pdrl("aaroncos_arena_portals")),
                "传送门方块 aaroncos_arena_portals 已注册",
                "key=" + pdrl("aaroncos_arena_portals")));

        // 清理残留：清除可能由先前套件写入的离场冷却，保证基线干净
        player.getPersistentData().remove(PDArenaBossManager.ARENA_EXIT_COOLDOWN_KEY);

        // 准备：主世界放置测试传送门
        ServerLevel ow = server.overworld();
        ensureOverworld(player, ow);
        BlockPos portalPos = player.blockPosition().offset(5, 0, 0);
        clearAndPlacePortal(ow, portalPos);

        // 玩家站上传送门（脚部在方块内），并授予 d_0（传送门进入门槛）
        grantD0(player);
        player.setPortalCooldown(0);
        player.teleportTo(ow, portalPos.getX() + 0.5, portalPos.getY() + 0.1, portalPos.getZ() + 0.5,
                player.getYRot(), player.getXRot());

        // ===== Phase 1 基线：无冷却 → 踩上传送门即进入竞技场 =====
        player.move(MoverType.SELF, Vec3.ZERO);
        boolean entered = player.level().dimension().equals(PDDimensions.AARONCOS_ARENA_WORLD_LEVEL_KEY);
        out.accept(ok(entered, "无冷却踩传送门进入竞技场",
                "dim=" + player.level().dimension().location()));
        if (!entered) {
            // 无法进入则后续断言无意义；清理后结束
            cleanup(ow, portalPos);
            return;
        }

        // 回到主世界
        ensureOverworld(player, ow);

        // ===== Phase 2 离场冷却：真实胜利离场传送写入冷却，冷却期内传送门不响应 =====
        player.setGameMode(GameType.SURVIVAL);
        PDArenaBossManager.teleportPlayersToOverworld(arena, player);
        long cooldownUntil = player.getPersistentData().getLong(PDArenaBossManager.ARENA_EXIT_COOLDOWN_KEY);
        boolean cooldownSet = cooldownUntil > ow.getGameTime();
        out.accept(ok(cooldownSet, "胜利离场传送写入离场冷却",
                "until=" + cooldownUntil + " now=" + ow.getGameTime()));
        if (!cooldownSet) {
            cleanup(ow, portalPos);
            return;
        }

        // 重新放回传送门（离场传送落在出生点/返回点，需手动复位）
        player.setPortalCooldown(0);
        player.teleportTo(ow, portalPos.getX() + 0.5, portalPos.getY() + 0.1, portalPos.getZ() + 0.5,
                player.getYRot(), player.getXRot());
        player.move(MoverType.SELF, Vec3.ZERO);
        boolean stillOw = player.level().dimension() == Level.OVERWORLD;
        out.accept(ok(stillOw, "冷却期内传送门不响应（不重进竞技场）",
                "dim=" + player.level().dimension().location()));

        // ===== Phase 3 冷却过期 → 传送门恢复 =====
        player.getPersistentData().remove(PDArenaBossManager.ARENA_EXIT_COOLDOWN_KEY);
        player.setPortalCooldown(0);
        player.teleportTo(ow, portalPos.getX() + 0.5, portalPos.getY() + 0.1, portalPos.getZ() + 0.5,
                player.getYRot(), player.getXRot());
        player.move(MoverType.SELF, Vec3.ZERO);
        boolean reEntered = player.level().dimension().equals(PDDimensions.AARONCOS_ARENA_WORLD_LEVEL_KEY);
        out.accept(ok(reEntered, "冷却过期后传送门恢复可进入",
                "dim=" + player.level().dimension().location()));

        // 收尾
        ensureOverworld(player, ow);
        cleanup(ow, portalPos);
    }

    /** 清空传送门周边并放置测试传送门（平台 + 传送门） */
    private static void clearAndPlacePortal(ServerLevel ow, BlockPos pos) {
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                ow.setBlock(pos.offset(dx, -1, dz), Blocks.STONE.defaultBlockState(), 3);
                ow.setBlock(pos.offset(dx, 0, dz), Blocks.AIR.defaultBlockState(), 3);
                ow.setBlock(pos.offset(dx, 1, dz), Blocks.AIR.defaultBlockState(), 3);
            }
        }
        ow.setBlock(pos, PDBlocks.AARONCOS_ARENA_PORTALS.get().defaultBlockState(), 3);
    }

    private static void cleanup(ServerLevel ow, BlockPos portalPos) {
        ow.removeBlock(portalPos, false);
    }

    /** 授予竞技场进入门槛成就（若总开关锁定则必须真实持有） */
    private static void grantD0(ServerPlayer player) {
        PDAdvancements.award(player, PDAdvancements.SHADOW_D_0);
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

    private static ResourceLocation pdrl(String path) {
        return ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, path);
    }

    private static Result ok(boolean pass, String name, String detail) {
        return new Result(pass, name, detail);
    }
}
