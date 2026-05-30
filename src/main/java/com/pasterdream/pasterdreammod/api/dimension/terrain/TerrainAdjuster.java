package com.pasterdream.pasterdreammod.api.dimension.terrain;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;

/**
 * 地形调整引擎 —— 采用隆起平台策略，在不产生明显断层的前提下调整地形。
 * <p>
 * 核心算法通过余弦插值实现平滑过渡，将起伏地形抬升/降低为平地，
 * 边缘与原地形无缝衔接。
 */
public class TerrainAdjuster {

    private TerrainAdjuster() {
        throw new UnsupportedOperationException("TerrainAdjuster 是纯静态工具类，不可实例化");
    }

    /**
     * 在指定区块区域创建平滑平台。
     * <p>
     * 将区块内指定中心的区域地形抬升/降低为平地，
     * 边缘使用余弦插值与原地形无缝衔接。
     *
     * @param chunk        目标区块
     * @param centerX      平台中心 X（世界坐标）
     * @param centerZ      平台中心 Z（世界坐标）
     * @param flatRadius   平地半径
     * @param blendRadius  渐变过渡半径
     */
    public static void createSmoothPlatform(ChunkAccess chunk, int centerX, int centerZ,
                                            int flatRadius, int blendRadius) {
        int totalRadius = flatRadius + blendRadius;
        int minBX = centerX - totalRadius;
        int maxBX = centerX + totalRadius;
        int minBZ = centerZ - totalRadius;
        int maxBZ = centerZ + totalRadius;

        int chunkMinX = chunk.getPos().getMinBlockX();
        int chunkMaxX = chunk.getPos().getMaxBlockX();
        int chunkMinZ = chunk.getPos().getMinBlockZ();
        int chunkMaxZ = chunk.getPos().getMaxBlockZ();

        int startX = Math.max(minBX, chunkMinX);
        int endX = Math.min(maxBX, chunkMaxX);
        int startZ = Math.max(minBZ, chunkMinZ);
        int endZ = Math.min(maxBZ, chunkMaxZ);

        PasterDreamMod.LOGGER.debug("[TerrainAdjuster] 🏗️ 创建平滑平台: center=({},{}), flat={}, blend={}, total={}",
                centerX, centerZ, flatRadius, blendRadius, totalRadius);

        int count = 0;
        for (int x = startX; x <= endX; x++) {
            for (int z = startZ; z <= endZ; z++) {
                double distance = Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(z - centerZ, 2));

                double blendFactor;
                if (distance <= flatRadius) {
                    blendFactor = 1.0;
                } else if (distance <= totalRadius) {
                    double t = (distance - flatRadius) / blendRadius;
                    blendFactor = cosineBlend(1.0 - t);
                } else {
                    continue;
                }

                int originalHeight = getSurfaceHeight(chunk, x, z);
                int targetHeight = calculateTargetHeight(chunk, centerX, centerZ, flatRadius);
                int adjustedHeight = (int) Math.round(originalHeight + (targetHeight - originalHeight) * blendFactor);

                adjustColumn(chunk, x, z, originalHeight, adjustedHeight);
                count++;
            }
        }

        PasterDreamMod.LOGGER.debug("[TerrainAdjuster] ✅ 平台创建完成: 调整了 {} 个方块列", count);
    }

    /**
     * 计算指定区域的平均地形高度。
     *
     * @param level   世界实例
     * @param centerX 中心 X
     * @param centerZ 中心 Z
     * @param radius  采样半径
     * @return 平均高度
     */
    public static double calculateAverageHeight(Level level, int centerX, int centerZ, int radius) {
        long totalHeight = 0;
        int sampleCount = 0;

        for (int x = centerX - radius; x <= centerX + radius; x++) {
            for (int z = centerZ - radius; z <= centerZ + radius; z++) {
                BlockPos pos = new BlockPos(x, 0, z);
                int height = level.getHeight();
                sampleCount++;
                totalHeight += height;
            }
        }

        return sampleCount > 0 ? (double) totalHeight / sampleCount : 64.0;
    }

    /**
     * 计算指定区域的最大地形起伏。
     *
     * @param level   世界实例
     * @param centerX 中心 X
     * @param centerZ 中心 Z
     * @param radius  采样半径
     * @return 最大高度差
     */
    public static double calculateMaxVariation(Level level, int centerX, int centerZ, int radius) {
        double min = Double.MAX_VALUE;
        double max = -Double.MAX_VALUE;

        for (int x = centerX - radius; x <= centerX + radius; x++) {
            for (int z = centerZ - radius; z <= centerZ + radius; z++) {
                BlockPos pos = new BlockPos(x, 0, z);
                int height = level.getHeight();
                if (height < min) min = height;
                if (height > max) max = height;
            }
        }

        return (min == Double.MAX_VALUE) ? 0.0 : max - min;
    }

    /**
     * 估算区域坡度。
     *
     * @param level   世界实例
     * @param centerX 中心 X
     * @param centerZ 中心 Z
     * @param radius  采样半径
     * @return 估算坡度值（0~1）
     */
    public static double estimateSlope(Level level, int centerX, int centerZ, int radius) {
        double variation = calculateMaxVariation(level, centerX, centerZ, radius);
        double maxPossibleVariation = level.getMaxBuildHeight() - level.getMinBuildHeight();
        return maxPossibleVariation > 0 ? variation / maxPossibleVariation : 0.0;
    }

    /**
     * 获取地表高度。
     */
    private static int getSurfaceHeight(ChunkAccess chunk, int x, int z) {
        int maxY = chunk.getMaxBuildHeight();
        int minY = chunk.getMinBuildHeight();

        for (int y = maxY - 1; y >= minY; y--) {
            BlockState state = chunk.getBlockState(new BlockPos(x, y, z));
            if (!state.isAir() && !state.liquid()) {
                return y;
            }
        }
        return minY;
    }

    /**
     * 计算平台目标高度（取中心区域的平均高度）。
     */
    private static int calculateTargetHeight(ChunkAccess chunk, int centerX, int centerZ, int flatRadius) {
        long total = 0;
        int count = 0;
        int sampleStep = Math.max(1, flatRadius / 4);

        for (int x = centerX - flatRadius; x <= centerX + flatRadius; x += sampleStep) {
            for (int z = centerZ - flatRadius; z <= centerZ + flatRadius; z += sampleStep) {
                total += getSurfaceHeight(chunk, x, z);
                count++;
            }
        }

        return count > 0 ? (int) Math.round((double) total / count) : 64;
    }

    /**
     * 调整单个方块列的地形。
     */
    private static void adjustColumn(ChunkAccess chunk, int x, int z,
                                     int originalHeight, int targetHeight) {
        int heightDiff = targetHeight - originalHeight;

        if (heightDiff > 0) {
            fillColumn(chunk, x, z, originalHeight + 1, targetHeight, Blocks.STONE.defaultBlockState());
        } else if (heightDiff < 0) {
            removeColumn(chunk, x, z, targetHeight + 1, originalHeight);
        }
    }

    /**
     * 填充方块列。
     */
    private static void fillColumn(ChunkAccess chunk, int x, int z,
                                   int startY, int endY, BlockState fillState) {
        BlockState topState = Blocks.GRASS_BLOCK.defaultBlockState();
        for (int y = startY; y <= endY; y++) {
            BlockState state = (y == endY) ? topState : fillState;
            chunk.setBlockState(new BlockPos(x, y, z), state, false);
        }
    }

    /**
     * 移除方块列。
     */
    private static void removeColumn(ChunkAccess chunk, int x, int z,
                                     int startY, int endY) {
        for (int y = startY; y <= endY; y++) {
            chunk.setBlockState(new BlockPos(x, y, z), Blocks.AIR.defaultBlockState(), false);
        }
    }

    /**
     * 余弦插值函数。
     * <p>
     * 使用 {@code (1 - cos(π * t)) / 2} 实现平滑过渡。
     * t=0 时返回 0（完全使用原始值），t=1 时返回 1（完全使用目标值）。
     *
     * @param t 插值参数（0~1）
     * @return 插值后的混合因子
     */
    public static double cosineBlend(double t) {
        t = Math.max(0.0, Math.min(1.0, t));
        return (1.0 - Math.cos(Math.PI * t)) / 2.0;
    }
}