package com.pasterdream.pasterdreammod.api.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * 维度 region 目录路径与 .mca/.mcc 文件操作。
 * <p>
 * 命令/消息文案、玩家传送等业务留主模；本类只做文件系统辅助。
 */
public final class DimensionRegionHelper {

    private DimensionRegionHelper() {
    }

    /**
     * {@code <world>/<namespace>/<path>/} 维度根目录。
     */
    public static Path dimensionRoot(MinecraftServer server, ResourceLocation dimensionId) {
        return server.getWorldPath(LevelResource.ROOT)
                .resolve(dimensionId.getNamespace())
                .resolve(dimensionId.getPath());
    }

    public static Path regionDirectory(MinecraftServer server, ResourceLocation dimensionId) {
        return dimensionRoot(server, dimensionId).resolve("region");
    }

    /**
     * 删除目录树下所有 {@code .mca}/{@code .mcc}（不删目录本身）。
     *
     * @return 成功删除的文件数
     */
    public static int deleteRegionChunkFiles(Path regionPath) throws IOException {
        return deleteRegionChunkFiles(regionPath, null);
    }

    /**
     * @param onDeleteFailure 单文件删除失败回调（可为 null）
     * @return 成功删除的文件数
     */
    public static int deleteRegionChunkFiles(Path regionPath, Consumer<Path> onDeleteFailure) throws IOException {
        if (regionPath == null || !Files.exists(regionPath)) {
            return 0;
        }
        AtomicInteger deleted = new AtomicInteger();
        Files.walkFileTree(regionPath, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                String name = file.getFileName().toString().toLowerCase();
                if (name.endsWith(".mca") || name.endsWith(".mcc")) {
                    try {
                        Files.delete(file);
                        deleted.incrementAndGet();
                    } catch (IOException e) {
                        if (onDeleteFailure != null) {
                            onDeleteFailure.accept(file);
                        }
                    }
                }
                return FileVisitResult.CONTINUE;
            }
        });
        return deleted.get();
    }

    /** 统计目录树下 .mca 文件数（不含 .mcc） */
    public static int countMcaFiles(Path regionPath) throws IOException {
        if (regionPath == null || !Files.exists(regionPath)) {
            return 0;
        }
        AtomicInteger count = new AtomicInteger();
        Files.walkFileTree(regionPath, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                if (file.getFileName().toString().toLowerCase().endsWith(".mca")) {
                    count.incrementAndGet();
                }
                return FileVisitResult.CONTINUE;
            }
        });
        return count.get();
    }
}
