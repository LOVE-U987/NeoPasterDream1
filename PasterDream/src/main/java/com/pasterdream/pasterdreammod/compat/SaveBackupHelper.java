package com.pasterdream.pasterdreammod.compat;

import com.pasterdream.pasterdreammod.PasterDreamMod;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

/**
 * 存档备份工具。
 * <p>
 * 将当前世界目录整体复制到游戏目录下的 {@code backups/}（与 {@code saves/} 同级），
 * 避免备份被原版世界选择界面识别为可进入世界。
 * <p>
 * <b>原子性</b>：先复制到 {@code <目标>.tmp}，成功后再重命名为最终目录；
 * 中途失败会清理临时目录，{@link #findBackup} 不将 {@code .tmp} 视为已完成备份。
 * <p>
 * <b>一致性说明</b>：备份在服务器启动阶段（{@code ServerStartedEvent}）后台执行，
 * 此时尚无玩家连接、区块尚未被大量改写，属 best-effort；仍可能复制到正在写入的
 * region 文件。需要干净备份时可在存档关闭后手动复制，或用
 * {@code /pasterdream save backup} 重新触发。
 */
public final class SaveBackupHelper {

    /** 备份目录时间戳格式 */
    private static final DateTimeFormatter STAMP = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    /** 跳过被运行中进程锁定的文件 */
    private static final String SESSION_LOCK = "session.lock";

    /** 临时目录后缀（未完成备份） */
    private static final String TEMP_SUFFIX = ".tmp";

    /** 进度日志间隔（处理的条目数） */
    private static final int PROGRESS_INTERVAL = 500;

    /** 备份并发守卫：同一时刻只允许一个备份任务（自动/手动共用） */
    private static final AtomicBoolean RUNNING = new AtomicBoolean(false);

    private SaveBackupHelper() {
        throw new UnsupportedOperationException("SaveBackupHelper 是工具类，不可实例化");
    }

    /**
     * 尝试获取备份执行权。
     *
     * @return true 表示获取成功（调用方须在完成后 {@link #release()}）
     */
    public static boolean tryAcquire() {
        return RUNNING.compareAndSet(false, true);
    }

    /**
     * 释放备份执行权。
     */
    public static void release() {
        RUNNING.set(false);
    }

    /**
     * 当前是否有备份任务在执行。
     *
     * @return 是否执行中
     */
    public static boolean isRunning() {
        return RUNNING.get();
    }

    /**
     * 备份根目录（游戏目录下的 {@code backups/}）。
     *
     * @param serverDirectory 服务器运行目录
     * @return 备份根目录
     */
    public static Path backupRoot(Path serverDirectory) {
        return serverDirectory.resolve("backups");
    }

    /**
     * 查找指定标记的最新已完成备份（忽略未完成的 {@code .tmp} 目录）。
     * <p>
     * 目录名内含 {@code yyyyMMdd_HHmmss} 时间戳，按名称取最大即最新。
     *
     * @param serverDirectory 服务器运行目录
     * @param worldName       世界名
     * @param tag             备份标记（如 {@code v1}）
     * @return 最新的备份目录；不存在返回 {@link Optional#empty()}
     */
    public static Optional<Path> findBackup(Path serverDirectory, String worldName, String tag) {
        Path root = backupRoot(serverDirectory);
        if (!Files.isDirectory(root)) {
            return Optional.empty();
        }
        String prefix = worldName + "_" + tag + "_";
        try (Stream<Path> stream = Files.list(root)) {
            return stream
                    .filter(path -> {
                        if (!Files.isDirectory(path)) {
                            return false;
                        }
                        String name = path.getFileName().toString();
                        return name.startsWith(prefix) && !name.endsWith(TEMP_SUFFIX);
                    })
                    .max(Comparator.comparing(path -> path.getFileName().toString()));
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    /**
     * 执行备份（同步；调用方负责放到后台线程）。
     *
     * @param serverDirectory 服务器运行目录
     * @param worldDirectory  世界存档目录（{@code server.getWorldPath(LevelResource.ROOT)}）
     * @param worldName       世界名
     * @param tag             备份标记
     * @return 备份目标目录
     * @throws IOException 复制失败（临时目录已清理）
     */
    public static Path backup(Path serverDirectory, Path worldDirectory, String worldName, String tag)
            throws IOException {
        Path root = backupRoot(serverDirectory);
        Files.createDirectories(root);
        String base = worldName + "_" + tag + "_" + LocalDateTime.now().format(STAMP);
        Path temp = root.resolve(base + TEMP_SUFFIX);
        Path target = root.resolve(base);

        deleteRecursively(temp);
        Files.createDirectories(temp);
        PasterDreamMod.LOGGER.info("[PDSaveCompat] 开始备份存档 {} -> {}", worldDirectory, temp);

        long[] count = {0L};
        try (Stream<Path> stream = Files.walk(worldDirectory)) {
            stream.forEach(source -> {
                copyEntry(worldDirectory, temp, source);
                count[0]++;
                if (count[0] % PROGRESS_INTERVAL == 0) {
                    PasterDreamMod.LOGGER.info("[PDSaveCompat] 备份进行中：已处理 {} 个条目", count[0]);
                }
            });
        } catch (UncheckedIOException e) {
            deleteRecursively(temp);
            throw e.getCause();
        } catch (RuntimeException e) {
            deleteRecursively(temp);
            throw e;
        }

        Files.move(temp, target);
        PasterDreamMod.LOGGER.info("[PDSaveCompat] 备份完成：{}（{} 个条目）", target, count[0]);
        return target;
    }

    /**
     * 复制单个条目（目录或文件）。
     *
     * @param sourceRoot 源根目录
     * @param targetRoot 目标根目录
     * @param source     当前条目
     */
    private static void copyEntry(Path sourceRoot, Path targetRoot, Path source) {
        Path relative = sourceRoot.relativize(source);
        if (relative.toString().isEmpty()) {
            return;
        }
        if (SESSION_LOCK.equals(relative.getFileName().toString())) {
            return;
        }
        Path destination = targetRoot.resolve(relative.toString());
        try {
            if (Files.isDirectory(source)) {
                Files.createDirectories(destination);
            } else {
                Path parent = destination.getParent();
                if (parent != null) {
                    Files.createDirectories(parent);
                }
                Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * 递归删除目录（忽略失败）。
     *
     * @param path 目标路径
     */
    private static void deleteRecursively(Path path) {
        if (!Files.exists(path)) {
            return;
        }
        try (Stream<Path> stream = Files.walk(path)) {
            stream.sorted(Comparator.reverseOrder()).forEach(p -> {
                try {
                    Files.deleteIfExists(p);
                } catch (IOException ignored) {
                    // 清理失败不阻断主流程
                }
            });
        } catch (IOException ignored) {
            // 清理失败不阻断主流程
        }
    }

    /**
     * 生成带版本号的备份标记。
     *
     * @param schemaVersion ID schema 版本
     * @return 备份标记
     */
    public static String versionTag(int schemaVersion) {
        return "v" + schemaVersion;
    }

    /**
     * 当前时间戳字符串（用于记录升级时间）。
     *
     * @return 形如 {@code 2026-09-17 12:34:56}
     */
    public static String nowStamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
