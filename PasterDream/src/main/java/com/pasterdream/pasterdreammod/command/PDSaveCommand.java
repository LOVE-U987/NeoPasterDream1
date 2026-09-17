package com.pasterdream.pasterdreammod.command;

import com.mojang.brigadier.context.CommandContext;
import com.pasterdream.pasterdreammod.compat.PDSaveCompatData;
import com.pasterdream.pasterdreammod.compat.PDSaveCompatHandler;
import com.pasterdream.pasterdreammod.compat.PDSaveSchema;
import com.pasterdream.pasterdreammod.compat.SaveBackupHelper;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

/**
 * 存档兼容命令：{@code /pasterdream save status|backup|upgrade}（OP level 2）。
 * <p>
 * 复用既有 {@code pasterdream} 根节点（Brigadier 同名子节点自动合并）。
 */
public final class PDSaveCommand {

    private PDSaveCommand() {
        throw new UnsupportedOperationException("PDSaveCommand 是命令注册类，不可实例化");
    }

    /**
     * 注册命令。
     *
     * @param event 命令注册事件
     */
    public static void register(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("pasterdream")
                .then(Commands.literal("save")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.literal("status").executes(PDSaveCommand::status))
                        .then(Commands.literal("backup").executes(PDSaveCommand::backup))
                        .then(Commands.literal("upgrade").executes(PDSaveCommand::upgrade))));
    }

    /**
     * 显示当前与存档记录的 schema 版本。
     *
     * @param context 命令上下文
     * @return 结果码
     */
    private static int status(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        MinecraftServer server = source.getServer();
        PDSaveCompatData data = PDSaveCompatData.get(server.overworld());
        int stored = data.getVersion(PDSaveSchema.MOD_ID);
        source.sendSuccess(() -> Component.translatable("pasterdream.save_compat.cmd.status",
                stored, PDSaveSchema.CURRENT_ID_SCHEMA_VERSION, data.getLastUpgradedAt()), false);
        if (PDSaveCompatHandler.isPending()) {
            source.sendSuccess(() -> Component.translatable("pasterdream.save_compat.cmd.pending",
                    PDSaveCompatHandler.getBackupPath()), false);
        }
        return 1;
    }

    /**
     * 手动触发一次存档备份（后台执行，不阻塞服务器主线程）。
     *
     * @param context 命令上下文
     * @return 结果码
     */
    private static int backup(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        MinecraftServer server = source.getServer();
        if (!SaveBackupHelper.tryAcquire()) {
            source.sendFailure(Component.translatable("pasterdream.save_compat.cmd.backup_busy"));
            return 0;
        }

        String worldName = server.getWorldData().getLevelName();
        String tag = SaveBackupHelper.versionTag(PDSaveSchema.CURRENT_ID_SCHEMA_VERSION);
        Path serverDirectory = server.getServerDirectory();
        Path worldDirectory = server.getWorldPath(LevelResource.ROOT);
        source.sendSuccess(() -> Component.translatable("pasterdream.save_compat.cmd.backup_started"), false);

        CompletableFuture.runAsync(() -> {
            Path target = null;
            Throwable error = null;
            try {
                target = SaveBackupHelper.backup(serverDirectory, worldDirectory, worldName, tag);
            } catch (Throwable t) {
                error = t;
            } finally {
                // 守卫只保护文件复制，在后台线程立即释放，避免服务器关闭时 execute 回调被丢弃导致泄漏
                SaveBackupHelper.release();
            }
            Path result = target;
            Throwable failure = error;
            server.execute(() -> {
                if (failure != null) {
                    source.sendFailure(Component.translatable("pasterdream.save_compat.cmd.backup_failed",
                            failure.toString()));
                } else {
                    source.sendSuccess(() -> Component.translatable("pasterdream.save_compat.cmd.backup_ok",
                            result.toString()), true);
                }
            });
        }, Util.backgroundExecutor());
        return 1;
    }

    /**
     * 写入当前 schema 标记（仅写标记，不重写世界数据）。
     *
     * @param context 命令上下文
     * @return 结果码
     */
    private static int upgrade(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        MinecraftServer server = source.getServer();
        PDSaveCompatData data = PDSaveCompatData.get(server.overworld());
        data.setVersion(PDSaveSchema.MOD_ID, PDSaveSchema.CURRENT_ID_SCHEMA_VERSION,
                PDSaveCompatHandler.currentModVersion());
        data.markUpgradedAt(SaveBackupHelper.nowStamp());
        PDSaveCompatHandler.clearPending();
        source.sendSuccess(() -> Component.translatable("pasterdream.save_compat.cmd.upgrade_ok",
                PDSaveSchema.CURRENT_ID_SCHEMA_VERSION), true);
        return 1;
    }
}
