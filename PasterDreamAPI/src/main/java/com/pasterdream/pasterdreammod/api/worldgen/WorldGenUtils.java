package com.pasterdream.pasterdreammod.api.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;

import javax.annotation.Nullable;
import java.util.Set;

/**
 * 世界生成工具类 —— 提供多方块结构生成常用的共享方法
 * <p>
 * 所有装饰物特征在生成时共用此工具类的逻辑，
 * 避免在每个 Feature 实现中重复编写 findGroundY、isSolidSurface 等方法。
 */
public final class WorldGenUtils {

    private WorldGenUtils() {}

    /**
     * 从起始 Y 向下搜索，找到第一个固体地面层的 Y 坐标
     *
     * @param level    世界生成级别访问
     * @param x        搜索位置的 X
     * @param startY   起始 Y（搜索向下进行）
     * @param z        搜索位置的 Z
     * @param maxFall  最大向下搜索距离
     * @return 地面层的 Y+1（地面上第一个可放置位置），如果找不到返回 Integer.MIN_VALUE
     */
    public static int findGroundY(WorldGenLevel level, int x, int startY, int z, int maxFall) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(x, startY, z);
        for (int i = 0; i <= maxFall; i++) {
            pos.setY(startY - i);
            if (isSolidSurface(level, pos)) {
                int resultY = pos.getY() + 1;
                    return resultY;
            }
        }
        return Integer.MIN_VALUE;
    }

    /**
     * 从起始 Y 向下搜索，使用自定义可替换判定找到第一个固体地面层
     *
     * @param level      世界生成级别访问
     * @param replaceable 可替换方块判定
     * @param x          搜索位置的 X
     * @param startY     起始 Y
     * @param z          搜索位置的 Z
     * @param maxFall    最大向下搜索距离
     * @return 地面层的 Y+1，找不到返回 Integer.MIN_VALUE
     */
    public static int findGroundY(WorldGenLevel level, BlockPredicate replaceable, int x, int startY, int z, int maxFall) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(x, startY, z);
        for (int i = 0; i <= maxFall; i++) {
            pos.setY(startY - i);
            if (isSolidSurface(level, pos)) {
                int resultY = pos.getY() + 1;
                    return resultY;
            }
        }
        return Integer.MIN_VALUE;
    }

    /**
     * 判断方块位置是否为固体地面（排除空气、树叶、植被类可替换方块）
     *
     * @param level 世界生成级别访问
     * @param pos   要检查的位置
     * @return true 表示该位置是固体地面
     */
    public static boolean isSolidSurface(WorldGenLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (state.isAir()) return false;
        if (state.is(BlockTags.LEAVES) || state.is(BlockTags.REPLACEABLE_BY_TREES)) return false;
        return state.isCollisionShapeFullBlock(level, pos);
    }

    /**
     * 判断方块是否可以被替换
     * <p>
     * 若 {@code replaceable} 为 null，仅检测是否为空气（即只允许替换空气）。
     *
     * @param level      世界生成级别访问
     * @param replaceable 自定义可替换判定（可为 null，null=仅空气可替换）
     * @param pos        要检查的位置
     * @return true 表示该位置可以被替换
     */
    public static boolean isReplaceable(WorldGenLevel level, @Nullable BlockPredicate replaceable, BlockPos pos) {
        if (replaceable != null) {
            return replaceable.test(level, pos) || level.getBlockState(pos).isAir();
        }
        return level.getBlockState(pos).isAir();
    }

    /**
     * 检测区域内非可替换方块的占比是否超过阈值（用于检测区域是否被占用）
     *
     * @param level      世界生成级别访问
     * @param replaceable 可替换方块判定（可为 null）
     * @param centerX    区域中心 X
     * @param centerZ    区域中心 Z
     * @param bottomY    区域底部 Y
     * @param topY       区域顶部 Y
     * @param radius     水平检测半径
     * @param threshold  占用比例阈值（0~1）
     * @return true 表示区域已被过度占用
     */
    public static boolean isAreaOccupied(WorldGenLevel level, @Nullable BlockPredicate replaceable,
                                          int centerX, int centerZ, int bottomY, int topY,
                                          int radius, float threshold) {
        if (replaceable != null) {
            // 有自定义可替换判定：精确布尔检测，任一非可替换方块即视为被占用
            // 只检测靠近地面层的范围（groundY 附近 ±2 层），避免高层空气稀释比率
            int centerY = (bottomY + topY) / 2;
            int checkBottom = Math.max(bottomY, centerY - 2);
            int checkTop = Math.min(topY, centerY + 2);
            for (int y = checkBottom; y <= checkTop; y++) {
                for (int dx = -radius; dx <= radius; dx++) {
                    for (int dz = -radius; dz <= radius; dz++) {
                        if (!isReplaceable(level, replaceable, new BlockPos(centerX + dx, y, centerZ + dz))) {
                                            return true;
                        }
                    }
                }
            }
            return false;
        }

        // 无可自定义可替换判定：退化为采样+阈值法
        int occupied = 0;
        int total = 0;
        int yStep = Math.max(1, (topY - bottomY) / 8);
        for (int y = bottomY; y <= topY; y += yStep) {
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    BlockPos checkPos = new BlockPos(centerX + dx, y, centerZ + dz);
                    if (!level.getBlockState(checkPos).isAir()) {
                        occupied++;
                    }
                    total++;
                }
            }
        }
        boolean occupiedResult = total > 0 && (float) occupied / (float) total > threshold;
        if (occupiedResult) {
        }
        return occupiedResult;
    }

    /**
     * 检查特定位置下方是否有支撑（用于悬空检测）
     *
     * @param level      世界生成级别访问
     * @param replaceable 可替换方块判定
     * @param pos        要检查的位置
     * @param existingSet 已放置的方块集合（用于检测已有结构的支撑）
     * @return true 表示下方有支撑
     */
    public static boolean hasSupportBelow(WorldGenLevel level, BlockPredicate replaceable,
                                           BlockPos pos, java.util.Set<BlockPos> existingSet) {
        BlockPos below = pos.below();
        if (existingSet != null && existingSet.contains(below)) {
            return true;
        }
        return isSolidSurface(level, below);
    }

    /**
     * 检查目标位置是否在区块生成安全范围内（防 far chunk 错误）
     * <p>
     * Minecraft 世界生成时区块生成范围有限，大跨度结构（如冰之门）如果
     * 将方块放置到生成范围之外的区块会触发 "Detected setBlock in a far chunk" 错误。
     * 此方法通过比较目标位置与原点的区块坐标来防止越界放置。
     *
     * @param origin 特征生成原点（FeaturePlaceContext.origin）
     * @param target 要放置方块的位置
     * @return true 表示目标位置在安全范围内，false 表示越界应跳过
     */
    public static boolean isWithinGenerationBounds(BlockPos origin, BlockPos target) {
        int originChunkX = origin.getX() >> 4;
        int originChunkZ = origin.getZ() >> 4;
        int targetChunkX = target.getX() >> 4;
        int targetChunkZ = target.getZ() >> 4;
        return targetChunkX == originChunkX
            && targetChunkZ == originChunkZ;
    }

    /**
     * 扩展版区块边界检查 —— 允许在相邻区块内放置方块
     * <p>
     * 对于大跨度结构（如冰拱门可达 48 格宽），单个区块（16格）限制会导致结构被截断。
     * 此方法允许指定相邻区块数量，Minecraft 特征生成时通常保证 ±1 区块已加载可用。
     *
     * @param origin      特征生成原点
     * @param target      要放置方块的位置
     * @param chunkRadius 允许的相邻区块半径（0=仅同区块，1=±1区块共9区块，覆盖48格范围）
     * @return true 表示目标位置在扩展安全范围内
     */
    public static boolean isWithinExpandedGenerationBounds(BlockPos origin, BlockPos target, int chunkRadius) {
        int originChunkX = origin.getX() >> 4;
        int originChunkZ = origin.getZ() >> 4;
        int targetChunkX = target.getX() >> 4;
        int targetChunkZ = target.getZ() >> 4;
        return Math.abs(targetChunkX - originChunkX) <= chunkRadius
            && Math.abs(targetChunkZ - originChunkZ) <= chunkRadius;
    }

    /**
     * 检查目标位置是否处于世界生成区域的可写范围内（防 far chunk 报错）
     * <p>
     * Minecraft 1.21.1 的 {@link net.minecraft.server.level.WorldGenRegion} 在生成期间
     * 只允许向「生成步骤写方块半径」内的区块写入方块；一旦越界，内部
     * {@code ensureCanWrite} 会记录 "Detected setBlock in a far chunk" 错误并拒绝放置。
     * 大跨度结构（浮空岛、巨型染梦树等）的方块很容易落在未就绪的相邻区块上，从而刷屏报错。
     * <p>
     * <b>静默几何预检</b>：直接调用 {@code WorldGenRegion.ensureCanWrite} 有一个副作用——
     * 它在判定越界时<b>本身就会打印</b> "Detected setBlock in a far chunk" 的 ERROR 日志。
     * 因此此处先用中心区块坐标做纯几何判断（features 阶段写半径恒为 1，即中心 ±1 区块），
     * 越界位置直接返回 false（不调 ensureCanWrite、不打日志、不写入）；
     * 几何范围内再放行，由 {@code setBlock} 内部的 {@code ensureCanWrite} 兜底。
     * 非世界生成环境（如 {@code /place} 命令）下非 {@code WorldGenRegion} 实例，恒返回 true。
     *
     * @param level 模拟世界读取器（世界生成期间为 WorldGenRegion）
     * @param pos   要放置方块的位置
     * @return true 表示目标位置可安全写入，false 表示应跳过该位置
     */
    public static boolean canPlaceInRegion(LevelSimulatedReader level, BlockPos pos) {
        if (level instanceof WorldGenRegion region) {
            // features 阶段 blockStateWriteRadius = 1（vanilla ChunkPyramid 常量），
            // 允许范围为中心区块 ±1（共 3×3 区块）；纯几何判断，不触发 ensureCanWrite 的日志
            ChunkPos center = region.getCenter();
            int targetChunkX = SectionPos.blockToSectionCoord(pos.getX());
            int targetChunkZ = SectionPos.blockToSectionCoord(pos.getZ());
            if (Math.abs(targetChunkX - center.x) > 1 || Math.abs(targetChunkZ - center.z) > 1) {
                return false;
            }
            return true;
        }
        return true;
    }

    /**
     * 将生成原点对齐到所在区块中心（X/Z），Y 保持不变
     * <p>
     * features 阶段的 {@code WorldGenRegion} 写半径仅为 1 区块（含中心区块 ±1，共 3×3 区块）。
     * 大跨度结构（浮空岛、巨型染梦树）若以随机 origin 为基准生成，横向跨度很容易超出写半径，
     * 触发 "Detected setBlock in a far chunk" 刷屏并导致连锁的光照 DataLayer NPE。
     * 将 origin 对齐到区块中心后，结构横向最大只需不超过「区块半宽 + 1 区块 = 24 格」即可全程安全写入。
     *
     * @param origin 原始生成原点
     * @return 对齐到所在区块中心的新原点（X/Z = chunkX*16+8, chunkZ*16+8，Y 不变）
     */
    public static BlockPos alignToChunkCenter(BlockPos origin) {
        return new BlockPos(
                (origin.getX() >> 4) * 16 + 8,
                origin.getY(),
                (origin.getZ() >> 4) * 16 + 8
        );
    }
}