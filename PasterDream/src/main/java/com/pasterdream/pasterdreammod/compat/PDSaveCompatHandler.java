package com.pasterdream.pasterdreammod.compat;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.config.PDCommonConfig;
import com.pasterdream.pasterdreammod.network.SaveUpgradePromptPayload;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.ServerLevelData;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * 旧存档检测 / 自动备份 / 提示 处理器。
 * <p>
 * 事件时序：
 * <ol>
 *   <li>{@code LevelEvent.CreateSpawnPosition}（仅新世界）：写入当前 schema 标记。</li>
 *   <li>{@code ServerStartedEvent}：读取主世界标记判定旧存档；<b>后台</b>执行自动备份并缓存结果。
 *       此时无玩家在线，不做投递。</li>
 *   <li>{@code PlayerLoggedInEvent}：备份完成后向玩家投递提示
 *       （单机 S2C 界面 / 专用服务器 OP 系统消息）。</li>
 * </ol>
 * 玩家确认（S2C 界面按钮或 {@code /pasterdream save upgrade}）后仅写入 schema 标记。
 */
public final class PDSaveCompatHandler {

    private static final String TAG = "[PDSaveCompat]";

    /** 本会话是否存在待处理的旧存档 */
    private static volatile boolean pending = false;

    /** 本会话服务器引用（后台备份完成后回主线程投递用） */
    private static volatile MinecraftServer serverRef;

    /** 自动备份目录路径（未备份为空串） */
    private static volatile String backupPath = "";

    /** 自动备份是否失败 */
    private static volatile boolean backupFailed = false;

    /** 自动备份是否仍在进行（进行中不投递，避免提示缺少备份路径） */
    private static volatile boolean backupRunning = false;

    /** 等待投递提示的玩家（备份完成后投递） */
    private static final Set<UUID> awaiting = new HashSet<>();

    private PDSaveCompatHandler() {
        throw new UnsupportedOperationException("PDSaveCompatHandler 是事件处理器，不可实例化");
    }

    /**
     * 新世界创建出生点时写入当前 schema 标记（避免新档被误判为旧档）。
     *
     * @param event 出生点创建事件
     */
    public static void onCreateSpawnPosition(LevelEvent.CreateSpawnPosition event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }
        if (!Level.OVERWORLD.equals(level.dimension())) {
            return;
        }
        PDSaveCompatData data = PDSaveCompatData.get(level);
        data.setVersion(PDSaveSchema.MOD_ID, PDSaveSchema.CURRENT_ID_SCHEMA_VERSION, currentModVersion());
        data.markUpgradedAt(SaveBackupHelper.nowStamp());
        PasterDreamMod.LOGGER.debug("{} 新世界已写入 ID schema 标记 v{}", TAG, PDSaveSchema.CURRENT_ID_SCHEMA_VERSION);
    }

    /**
     * 服务器启动：检测旧存档并在后台执行自动备份（不投递）。
     *
     * @param event 服务器启动完成事件
     */
    public static void onServerStarted(ServerStartedEvent event) {
        MinecraftServer server = event.getServer();
        serverRef = server;
        pending = false;
        backupPath = "";
        backupFailed = false;
        backupRunning = false;
        awaiting.clear();

        if (!isEnabled()) {
            return;
        }

        ServerLevel overworld = server.overworld();
        // 兜底：新世界尚未完成出生点初始化时不可能为旧档
        if (overworld.getLevelData() instanceof ServerLevelData levelData && !levelData.isInitialized()) {
            return;
        }

        PDSaveCompatData data = PDSaveCompatData.get(overworld);
        int stored = data.getVersion(PDSaveSchema.MOD_ID);
        if (stored >= PDSaveSchema.CURRENT_ID_SCHEMA_VERSION) {
            return;
        }

        pending = true;
        PasterDreamMod.LOGGER.info("{} 检测到旧版本存档（记录版本 {}，当前 {}），准备备份与提示",
                TAG, stored, PDSaveSchema.CURRENT_ID_SCHEMA_VERSION);

        if (!isAutoBackupEnabled()) {
            PasterDreamMod.LOGGER.info("{} 自动备份已关闭，仅提示（可用 /pasterdream save backup 手动备份）", TAG);
            return;
        }

        String worldName = server.getWorldData().getLevelName();
        String tag = SaveBackupHelper.versionTag(PDSaveSchema.CURRENT_ID_SCHEMA_VERSION);
        Path serverDirectory = server.getServerDirectory();
        Optional<Path> existing = SaveBackupHelper.findBackup(serverDirectory, worldName, tag);
        if (existing.isPresent()) {
            backupPath = existing.get().toString();
            PasterDreamMod.LOGGER.info("{} 已存在同版本备份，跳过自动备份：{}", TAG, backupPath);
            return;
        }

        if (!SaveBackupHelper.tryAcquire()) {
            PasterDreamMod.LOGGER.info("{} 已有备份任务在执行，跳过自动备份", TAG);
            return;
        }

        backupRunning = true;
        Path worldDirectory = server.getWorldPath(LevelResource.ROOT);
        CompletableFuture.runAsync(() -> {
            Path target = null;
            Throwable error = null;
            try {
                target = SaveBackupHelper.backup(serverDirectory, worldDirectory, worldName, tag);
            } catch (Throwable t) {
                error = t;
            } finally {
                // 守卫只保护文件复制，后台线程立即释放，避免服务器关闭时 execute 回调被丢弃导致泄漏
                SaveBackupHelper.release();
            }
            Path result = target;
            Throwable failure = error;
            server.execute(() -> {
                if (failure != null) {
                    PasterDreamMod.LOGGER.warn("{} 旧存档自动备份失败：{}", TAG, failure.toString());
                    backupFailed = true;
                } else {
                    backupPath = result.toString();
                }
                backupRunning = false;
                deliverAwaiting();
            });
        }, Util.backgroundExecutor());
    }

    /**
     * 玩家登录：若有待处理旧存档，登记并在备份完成后投递提示。
     *
     * @param event 玩家登录事件
     */
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (!pending) {
            return;
        }
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        MinecraftServer server = player.getServer();
        if (server == null) {
            return;
        }
        // 专用服务器仅对 OP 投递；无 OP 在线时仅日志（备份完成后由 OP 登录触发）
        if (server.isDedicatedServer() && !player.hasPermissions(2)) {
            PasterDreamMod.LOGGER.info("{} 专用服务器旧存档待处理，等待 OP 登录投递提示（备份：{}）", TAG, backupPath);
            return;
        }
        awaiting.add(player.getUUID());
        deliverAwaiting();
    }

    /**
     * 玩家确认更新：写入当前 schema 标记并停止提示。
     * <p>
     * 专用服务器仅 OP 可确认（与命令权限一致）。
     *
     * @param player 玩家
     */
    public static void confirm(ServerPlayer player) {
        MinecraftServer server = player.getServer();
        if (server == null) {
            return;
        }
        if (server.isDedicatedServer() && !player.hasPermissions(2)) {
            PasterDreamMod.LOGGER.warn("{} 非 OP 玩家 {} 尝试确认旧存档更新，已拒绝",
                    TAG, player.getName().getString());
            return;
        }
        ServerLevel overworld = server.overworld();
        PDSaveCompatData data = PDSaveCompatData.get(overworld);
        data.setVersion(PDSaveSchema.MOD_ID, PDSaveSchema.CURRENT_ID_SCHEMA_VERSION, currentModVersion());
        data.markUpgradedAt(SaveBackupHelper.nowStamp());
        pending = false;
        awaiting.clear();
        PasterDreamMod.LOGGER.info("{} 玩家 {} 已确认更新旧存档 ID schema 标记", TAG, player.getName().getString());
    }

    /**
     * 备份完成后向等待中的玩家投递提示（须在主线程调用）。
     */
    private static void deliverAwaiting() {
        if (!pending || backupRunning) {
            return;
        }
        MinecraftServer server = serverRef;
        if (server == null) {
            return;
        }
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (awaiting.remove(player.getUUID())) {
                deliver(player);
            }
        }
    }

    /**
     * 投递提示。
     *
     * @param player 玩家
     */
    private static void deliver(ServerPlayer player) {
        MinecraftServer server = player.getServer();
        if (server == null) {
            return;
        }
        if (server.isDedicatedServer()) {
            if (player.hasPermissions(2)) {
                player.sendSystemMessage(Component.translatable("pasterdream.save_compat.dedicated_notice", backupPath));
            }
            return;
        }
        PacketDistributor.sendToPlayer(player, new SaveUpgradePromptPayload(
                server.getWorldData().getLevelName(),
                backupPath,
                backupFailed,
                PDSaveSchema.CURRENT_ID_SCHEMA_VERSION));
    }

    /**
     * 是否启用旧存档提示/备份（运行时读取配置）。
     *
     * @return 是否启用
     */
    public static boolean isEnabled() {
        try {
            return PDCommonConfig.SAVE_COMPAT_ENABLED.get();
        } catch (IllegalStateException e) {
            return true;
        }
    }

    /**
     * 是否启用自动备份（运行时读取配置）。
     *
     * @return 是否启用
     */
    public static boolean isAutoBackupEnabled() {
        try {
            return PDCommonConfig.SAVE_COMPAT_AUTO_BACKUP.get();
        } catch (IllegalStateException e) {
            return true;
        }
    }

    /**
     * @return 本会话是否有待处理的旧存档
     */
    public static boolean isPending() {
        return pending;
    }

    /**
     * 清除待处理状态（命令升级后调用）。
     */
    public static void clearPending() {
        pending = false;
        awaiting.clear();
    }

    /**
     * @return 自动备份目录路径（未备份为空串）
     */
    public static String getBackupPath() {
        return backupPath;
    }

    /**
     * @return 自动备份是否失败
     */
    public static boolean isBackupFailed() {
        return backupFailed;
    }

    /**
     * 读取当前主模版本字符串。
     *
     * @return 模组版本
     */
    public static String currentModVersion() {
        return ModList.get().getModContainerById(PDSaveSchema.MOD_ID)
                .map(container -> container.getModInfo().getVersion().toString())
                .orElse("unknown");
    }
}
