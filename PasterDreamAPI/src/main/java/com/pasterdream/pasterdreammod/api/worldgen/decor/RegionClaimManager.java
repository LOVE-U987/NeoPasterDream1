package com.pasterdream.pasterdreammod.api.worldgen.decor;

import net.minecraft.core.SectionPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 地物区域认领管理器 —— 防止地物生成时互相重叠（"叠罗汉"修复）
 * <p>
 * 核心思路：地物生成前必须认领其将要占据的空间（水平范围 + 垂直范围），
 * 认领成功后，其他地物在尝试认领重叠区域时会检测到冲突。
 * <ul>
 *   <li><b>垂直分层共存</b>：冲突判定要求「水平重叠 && 垂直重叠」同时成立。
 *       因此不同垂直层级的地物（如高空云岛/横梁 vs 地表植被）可以在同一水平位置共存，
 *       解决高大植物下方地表植被无法生成的问题。</li>
 *   <li><b>线程安全</b>：认领表基于 {@link ConcurrentHashMap}，认领/释放操作在锁内
 *       原子完成「检查 + 写入」，保证并行区块生成时不会出现同时认领同一区域。</li>
 *   <li><b>生命周期</b>：认领在地物成功生成后持续有效（防止后续串行生成的地物重叠），
 *       生成失败时由调用方通过 {@link #release(ClaimHandle)} 释放，避免残留无效占用。</li>
 *   <li><b>按维度隔离</b>：认领表以维度 ID 分层，不同维度互不干扰。</li>
 * </ul>
 * <p>
 * 认领记录写入其覆盖的<b>所有区块</b>（而非仅中心区块），查询时同样枚举目标区域覆盖的
 * 所有区块，保证跨区块的大型地物（门框/柱子等）与相邻地物之间的冲突能被正确检测。
 */
public final class RegionClaimManager {

    /**
     * 认领区域（方块坐标，含边界）
     *
     * @param minX 最小 X（含）
     * @param minZ 最小 Z（含）
     * @param maxX 最大 X（含）
     * @param maxZ 最大 Z（含）
     * @param minY 最小 Y（含）
     * @param maxY 最大 Y（含）
     */
    private record RegionClaim(int minX, int minZ, int maxX, int maxZ, int minY, int maxY) {

        /**
         * 判断两个认领区域是否冲突
         * <p>
         * 要求水平范围与垂直范围同时重叠才算冲突，
         * 垂直层级不重叠的地物可在同一水平位置共存。
         *
         * @param other 另一个认领区域
         * @return true 表示发生冲突
         */
        boolean overlaps(RegionClaim other) {
            return this.minX <= other.maxX && other.minX <= this.maxX
                && this.minZ <= other.maxZ && other.minZ <= this.maxZ
                && this.minY <= other.maxY && other.minY <= this.maxY;
        }
    }

    /**
     * 认领区域请求 —— 以中心点 + 水平半径 + 垂直范围描述一个待认领区域
     *
     * @param centerX 中心 X
     * @param centerZ 中心 Z
     * @param radius  水平半径（含，0=单格）
     * @param minY    最小 Y（含）
     * @param maxY    最大 Y（含）
     */
    public record ClaimArea(int centerX, int centerZ, int radius, int minY, int maxY) {

        /**
         * 将请求转换为绝对坐标的认领区域
         *
         * @return 认领区域
         */
        RegionClaim toClaim() {
            int r = Math.max(0, radius);
            return new RegionClaim(
                centerX - r, centerZ - r,
                centerX + r, centerZ + r,
                minY, maxY);
        }
    }

    /**
     * 认领句柄 —— 持有维度与已认领的区域列表，供生成失败时释放
     */
    public static final class ClaimHandle {

        /** 认领所属维度 */
        private final ResourceLocation dimension;

        /** 已认领的区域列表（不可变视图） */
        private final List<RegionClaim> claims;

        private ClaimHandle(ResourceLocation dimension, List<RegionClaim> claims) {
            this.dimension = dimension;
            this.claims = claims;
        }
    }

    /** 认领表：维度 -> 区块坐标 -> 该区块上的认领区域列表 */
    private static final ConcurrentHashMap<ResourceLocation, ConcurrentHashMap<Long, List<RegionClaim>>> CLAIMS =
        new ConcurrentHashMap<>();

    /** 认领/释放操作共享锁 —— 保证「检查 + 写入」原子性 */
    private static final Object LOCK = new Object();

    private RegionClaimManager() {
        throw new UnsupportedOperationException("RegionClaimManager 是纯静态门面类，不可实例化");
    }

    /**
     * 尝试认领单个区域（原子操作）
     *
     * @param dimension 维度 ID（通常为 {@code level.dimension().location()}）
     * @param centerX   认领中心 X
     * @param centerZ   认领中心 Z
     * @param radius    水平半径（含，0=单格）
     * @param minY      认领最低 Y（含）
     * @param maxY      认领最高 Y（含）
     * @return 认领句柄；目标区域已被认领（冲突）时返回 null
     */
    @Nullable
    public static ClaimHandle tryClaim(ResourceLocation dimension, int centerX, int centerZ,
                                       int radius, int minY, int maxY) {
        return tryClaim(dimension, List.of(new ClaimArea(centerX, centerZ, radius, minY, maxY)));
    }

    /**
     * 原子批量认领多个区域（如门框的左右柱 + 横梁）
     * <p>
     * 任一个区域与已有认领冲突则整体失败（不写入任何区域），
     * 避免出现「部分认领成功」的不一致状态。
     *
     * @param dimension 维度 ID
     * @param areas     待认领的区域请求列表（至少 1 个）
     * @return 认领句柄；任一区域冲突时返回 null
     */
    @Nullable
    public static ClaimHandle tryClaim(ResourceLocation dimension, List<ClaimArea> areas) {
        if (dimension == null || areas == null || areas.isEmpty()) {
            return null;
        }

        List<RegionClaim> claims = new ArrayList<>(areas.size());
        for (ClaimArea area : areas) {
            claims.add(area.toClaim());
        }

        synchronized (LOCK) {
            Map<Long, List<RegionClaim>> dimClaims =
                CLAIMS.computeIfAbsent(dimension, k -> new ConcurrentHashMap<>());

            // 1. 冲突检查：任一区域与已有认领冲突则整体失败
            for (RegionClaim claim : claims) {
                if (hasConflict(dimClaims, claim)) {
                    return null;
                }
            }

            // 2. 写入：认领写入其覆盖的所有区块，保证跨区块查询可见
            for (RegionClaim claim : claims) {
                forEachChunk(claim, (cx, cz) -> {
                    dimClaims.computeIfAbsent(ChunkPos.asLong(cx, cz), k -> new ArrayList<>()).add(claim);
                });
            }

            return new ClaimHandle(dimension, List.copyOf(claims));
        }
    }

    /**
     * 释放认领（仅在地物生成失败时调用）
     * <p>
     * 生成成功的认领应持续保留，用于阻止后续地物重叠。
     *
     * @param handle 认领句柄（可能为 null，null 时无操作）
     */
    public static void release(@Nullable ClaimHandle handle) {
        if (handle == null || handle.claims.isEmpty()) {
            return;
        }
        synchronized (LOCK) {
            Map<Long, List<RegionClaim>> dimClaims = CLAIMS.get(handle.dimension);
            if (dimClaims == null) {
                return;
            }
            for (RegionClaim claim : handle.claims) {
                forEachChunk(claim, (cx, cz) -> {
                    List<RegionClaim> list = dimClaims.get(ChunkPos.asLong(cx, cz));
                    if (list != null) {
                        list.remove(claim);
                    }
                });
            }
        }
    }

    /**
     * 清空指定维度的全部认领记录（可选维护接口）
     * <p>
     * 认领记录很小（每条约几十字节），正常情况下无需清理；
     * 若模组希望在世界卸载时释放内存，可调用此方法。
     *
     * @param dimension 维度 ID
     */
    public static void clearDimension(ResourceLocation dimension) {
        if (dimension != null) {
            synchronized (LOCK) {
                CLAIMS.remove(dimension);
            }
        }
    }

    /**
     * 检查认领区域是否与已有认领冲突
     *
     * @param dimClaims 维度认领表
     * @param claim     待检查的认领区域
     * @return true 表示存在冲突
     */
    private static boolean hasConflict(Map<Long, List<RegionClaim>> dimClaims, RegionClaim claim) {
        boolean[] conflict = {false};
        forEachChunk(claim, (cx, cz) -> {
            if (conflict[0]) {
                return;
            }
            List<RegionClaim> list = dimClaims.get(ChunkPos.asLong(cx, cz));
            if (list == null) {
                return;
            }
            for (RegionClaim existing : list) {
                if (existing.overlaps(claim)) {
                    conflict[0] = true;
                    return;
                }
            }
        });
        return conflict[0];
    }

    /**
     * 遍历认领区域覆盖的所有区块坐标
     *
     * @param claim    认领区域
     * @param consumer 区块坐标消费者
     */
    private static void forEachChunk(RegionClaim claim, ChunkConsumer consumer) {
        int minCx = SectionPos.blockToSectionCoord(claim.minX());
        int maxCx = SectionPos.blockToSectionCoord(claim.maxX());
        int minCz = SectionPos.blockToSectionCoord(claim.minZ());
        int maxCz = SectionPos.blockToSectionCoord(claim.maxZ());
        for (int cx = minCx; cx <= maxCx; cx++) {
            for (int cz = minCz; cz <= maxCz; cz++) {
                consumer.accept(cx, cz);
            }
        }
    }

    /** 区块坐标消费者回调 */
    @FunctionalInterface
    private interface ChunkConsumer {
        void accept(int chunkX, int chunkZ);
    }
}
