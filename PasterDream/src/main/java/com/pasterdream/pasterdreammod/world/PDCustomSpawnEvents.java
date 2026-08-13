package com.pasterdream.pasterdreammod.world;

import com.mojang.datafixers.util.Pair;
import com.pasterdream.pasterdreammod.api.util.PDDebugLogger;
import com.pasterdream.pasterdreammod.config.PDCommonConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

/**
 * 自定义出生维度/群系事件处理器（游戏总线）。
 * <p>
 * 通过 {@code PasterDream-Common.toml} 的 {@code Custom Spawn} 配置段控制（默认关闭）：
 * <ul>
 *   <li>{@code custom spawn enabled} —— 总开关</li>
 *   <li>{@code custom spawn dimension} —— 出生维度 ID（如 {@code pasterdream:dyedream_world}）</li>
 *   <li>{@code custom spawn biome} —— 出生群系 ID（如 {@code pasterdream:dyedream}）</li>
 *   <li>{@code custom spawn search radius} —— 群系搜索半径（格）</li>
 * </ul>
 * 开启后，<b>尚未执行过自定义出生</b>的玩家在登录（首次进入世界）时，
 * 会被传送到指定维度中最近的指定群系位置，并设置该位置为重生点；
 * 执行完成后在玩家持久 NBT 写入标记，同一存档内只生效一次。
 * <p>
 * 标记存放在 {@link Player#PERSISTED_NBT_TAG}（{@code PlayerPersisted}）子标签下，
 * 与《帕斯特指南》发放标记同一模式：跨死亡/跨维度克隆自动保留，避免重复触发。
 *
 * @author PasterDream
 */
public class PDCustomSpawnEvents {

    /** 玩家持久 NBT 中「自定义出生已执行」标记键（命名空间化，避免与其他模组键冲突） */
    private static final String SPAWN_DONE_KEY = "pasterdream.custom_spawn_done";

    /**
     * 玩家登录：若启用自定义出生且该玩家尚未执行过，则传送到配置指定的维度与群系并设置重生点。
     *
     * @param event 登录事件
     */
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        // 仅在服务端逻辑侧处理
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        // 总开关默认关闭，关闭时完全不影响原版出生流程
        if (!PDCommonConfig.CUSTOM_SPAWN_ENABLED.get()) {
            return;
        }
        // 仅首次生效：标记存放在 PlayerPersisted 子标签，跨死亡/重登保留
        CompoundTag persistent = player.getPersistentData();
        CompoundTag persisted = persistent.contains(Player.PERSISTED_NBT_TAG, Tag.TAG_COMPOUND)
                ? persistent.getCompound(Player.PERSISTED_NBT_TAG)
                : new CompoundTag();
        if (persisted.getBoolean(SPAWN_DONE_KEY)) {
            return;
        }

        MinecraftServer server = player.server;

        // 1. 解析目标维度（格式非法时跳过并保留原版出生）
        ResourceKey<Level> dimKey = parseDimension(PDCommonConfig.CUSTOM_SPAWN_DIMENSION.get());
        if (dimKey == null) {
            PDDebugLogger.mainDebug("[PDCustomSpawnEvents] ⚠️ 配置的出生维度 {} 格式非法，跳过自定义出生",
                    PDCommonConfig.CUSTOM_SPAWN_DIMENSION.get());
            return;
        }
        ServerLevel targetLevel = server.getLevel(dimKey);
        if (targetLevel == null) {
            PDDebugLogger.mainDebug("[PDCustomSpawnEvents] ⚠️ 目标维度 {} 不存在或未加载，跳过自定义出生", dimKey.location());
            return;
        }

        // 2. 解析并校验目标群系（未注册时跳过，避免搜索永远落空）
        ResourceKey<Biome> biomeKey = parseBiome(PDCommonConfig.CUSTOM_SPAWN_BIOME.get());
        if (biomeKey == null
                || !server.registryAccess().registryOrThrow(Registries.BIOME).containsKey(biomeKey)) {
            PDDebugLogger.mainDebug("[PDCustomSpawnEvents] ⚠️ 配置的出生群系 {} 格式非法或未注册，跳过自定义出生",
                    PDCommonConfig.CUSTOM_SPAWN_BIOME.get());
            return;
        }

        // 3. 以目标维度出生点为中心搜索指定群系（水平半径取配置值，垂直覆盖 ±128）
        BlockPos center = targetLevel.getSharedSpawnPos();
        int radius = PDCommonConfig.CUSTOM_SPAWN_SEARCH_RADIUS.get();
        Pair<BlockPos, Holder<Biome>> found = targetLevel.findClosestBiome3d(
                holder -> holder.is(biomeKey), center, radius, 32, 128);
        BlockPos biomePos = found != null ? found.getFirst() : center;

        // 4. 计算安全地表高度（搜索到的 y 仅为群系采样高度，需取地表高度兜底）
        int groundY = targetLevel.getHeight(Heightmap.Types.MOTION_BLOCKING, biomePos.getX(), biomePos.getZ());
        int y = Math.max(groundY + 1, targetLevel.getMinBuildHeight() + 1);
        BlockPos finalPos = new BlockPos(biomePos.getX(), y, biomePos.getZ());

        // 5. 跨维度传送到目标位置（已在目标维度则原地传送）
        if (!player.level().dimension().equals(dimKey)) {
            player.teleportTo(targetLevel, finalPos.getX() + 0.5, finalPos.getY(), finalPos.getZ() + 0.5,
                    player.getYRot(), player.getXRot());
        } else {
            player.teleportTo(finalPos.getX() + 0.5, finalPos.getY(), finalPos.getZ() + 0.5);
        }

        // 6. 设置重生点（强制在该位置重生，死亡后仍回到目标维度）
        player.setRespawnPosition(dimKey, finalPos, 0.0F, true, false);

        // 7. 写入已完成标记，同一存档内不再重复执行
        persisted.putBoolean(SPAWN_DONE_KEY, true);
        persistent.put(Player.PERSISTED_NBT_TAG, persisted);

        PDDebugLogger.mainDebug("[PDCustomSpawnEvents] ✅ 玩家 {} 已在维度 {} 群系 {} 出生（位置 {}）",
                player.getName().getString(), dimKey.location(), biomeKey.location(), finalPos);
    }

    /**
     * 解析维度 ID 字符串为维度 ResourceKey。
     *
     * @param id 配置的维度 ID（如 {@code minecraft:overworld} / {@code pasterdream:dyedream_world}）
     * @return 维度 ResourceKey；格式非法时返回 {@code null}
     */
    private static ResourceKey<Level> parseDimension(String id) {
        try {
            return ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(id));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 解析群系 ID 字符串为群系 ResourceKey。
     *
     * @param id 配置的群系 ID（如 {@code minecraft:plains} / {@code pasterdream:dyedream}）
     * @return 群系 ResourceKey；格式非法时返回 {@code null}
     */
    private static ResourceKey<Biome> parseBiome(String id) {
        try {
            return ResourceKey.create(Registries.BIOME, ResourceLocation.parse(id));
        } catch (Exception e) {
            return null;
        }
    }
}
