package com.pasterdream.pasterdreammod.worldgen.chunkgen;

import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.RandomState;
import org.jetbrains.annotations.NotNull;

/**
 * 染梦世界噪声工具类 —— 提供岛屿/河流/洞穴等噪声采样方法
 * <p>
 * 基于原版 Minecraft 的 {@link RandomState#router()} 提供的密度函数，
 * 将大陆性、侵蚀度等噪声映射为染梦世界特有的地形控制信号。
 * <p>
 * 所有方法均使用原版 API，不引入外部噪声库（如 FastNoise）。
 * 噪声配置由维度的 noise_settings JSON 中的 noise_router 定义，
 * 此处仅做具名封装，方便 ChunkGenerator 和 BiomeSource 调用。
 *
 * @author PasterDream Team
 */
public final class DyedreamNoises {

    /** 岛屿判定阈值 —— 大陆性噪声高于此值时为陆地 */
    public static final double ISLAND_THRESHOLD = -0.3;

    /** 河流判定阈值 —— 侵蚀度噪声高于此值时为河流区域 */
    public static final double RIVER_THRESHOLD = 0.7;

    /** 洞穴噪声频率缩放 */
    private static final double CAVE_FREQUENCY = 0.05;

    /** 岛屿高度噪声频率缩放 */
    private static final double HEIGHT_FREQUENCY = 0.005;

    private DyedreamNoises() {}

    // ==================== 岛屿噪声 ====================

    /**
     * 采样大陆性噪声（用于岛屿/海洋判定）
     * <p>
     * 值越高表示该位置越可能是陆地，低于 {@link #ISLAND_THRESHOLD} 为海洋。
     *
     * @param randomState 随机状态（包含噪声路由器和噪声数据）
     * @param x           世界坐标 X
     * @param z           世界坐标 Z
     * @return 大陆性噪声值（范围大致 -1.0 ~ 1.0）
     */
    public static double sampleIslandNoise(@NotNull RandomState randomState, int x, int z) {
        return randomState.router().continents()
                .compute(new DensityFunction.SinglePointContext(x, 0, z));
    }

    /**
     * 采样岛屿高度噪声（用于岛屿表面海拔）
     * <p>
     * 在大陆性噪声基础上叠加高频扰动，使地面有自然起伏。
     *
     * @param randomState 随机状态
     * @param x           世界坐标 X
     * @param z           世界坐标 Z
     * @return 高度偏移值（范围大致 -8 ~ 8，需叠加基础海拔）
     */
    public static double sampleHeightNoise(@NotNull RandomState randomState, int x, int z) {
        DensityFunction heightNoise = randomState.router().ridges();
        double ridges = heightNoise.compute(new DensityFunction.SinglePointContext(x, 0, z));
        double erosion = randomState.router().erosion()
                .compute(new DensityFunction.SinglePointContext(x, 0, z));
        // 山脊+侵蚀度的组合形成地形起伏
        return (ridges * 0.5 + erosion * 0.5) * 8.0;
    }

    // ==================== 河流噪声 ====================

    /**
     * 采样河流噪声（用于河流/谷地判定）
     *
     * @param randomState 随机状态
     * @param x           世界坐标 X
     * @param z           世界坐标 Z
     * @return 侵蚀度噪声值（越高越可能是河流/谷地）
     */
    public static double sampleRiverNoise(@NotNull RandomState randomState, int x, int z) {
        return randomState.router().erosion()
                .compute(new DensityFunction.SinglePointContext(x, 0, z));
    }

    // ==================== 洞穴噪声 ====================

    /**
     * 采样三维洞穴噪声（用于在地下挖出空洞）
     * <p>
     * 使用深度密度函数结合坐标生成洞穴状空洞。
     *
     * @param randomState 随机状态
     * @param x           世界坐标 X
     * @param y           世界坐标 Y
     * @param z           世界坐标 Z
     * @return 洞穴噪声值（低于阈值时为空洞）
     */
    public static double sampleCaveNoise(@NotNull RandomState randomState, int x, int y, int z) {
        DensityFunction depth = randomState.router().depth();
        double depthVal = depth.compute(new DensityFunction.SinglePointContext(x, y, z));
        // 在深度噪声上叠加高频扰动形成洞穴
        double noiseX = x * CAVE_FREQUENCY;
        double noiseZ = z * CAVE_FREQUENCY;
        double caveSeed = (Math.sin(noiseX * 3.7 + noiseZ * 7.3 + y * 1.1) * 0.3
                + Math.cos(noiseX * 5.1 - noiseZ * 2.9 + y * 0.8) * 0.2);
        return depthVal + caveSeed;
    }

    // ==================== 实用工具 ====================

    /**
     * 判断某位置是否为岛屿（基于大陆性噪声）
     *
     * @param randomState 随机状态
     * @param x           世界坐标 X
     * @param z           世界坐标 Z
     * @return true 表示该位置为陆地/岛屿
     */
    public static boolean isIsland(@NotNull RandomState randomState, int x, int z) {
        return sampleIslandNoise(randomState, x, z) > ISLAND_THRESHOLD;
    }

    /**
     * 计算某位置的地形表面高度
     * <p>
     * 如果该位置为岛屿，返回海平面 + 噪声偏移；
     * 否则返回低于海平面的深度值。
     *
     * @param randomState 随机状态
     * @param x           世界坐标 X
     * @param z           世界坐标 Z
     * @param seaLevel    海平面高度
     * @return 表面方块 Y 坐标
     */
    public static int computeSurfaceHeight(@NotNull RandomState randomState, int x, int z, int seaLevel) {
        if (!isIsland(randomState, x, z)) {
            // 海洋区域：低于海平面的深度
            return seaLevel - 5;
        }
        double heightOffset = sampleHeightNoise(randomState, x, z);
        return (int) Math.round(seaLevel + heightOffset);
    }
}
