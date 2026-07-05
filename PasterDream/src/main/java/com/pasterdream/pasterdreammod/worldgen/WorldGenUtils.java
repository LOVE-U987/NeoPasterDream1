package com.pasterdream.pasterdreammod.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
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
}