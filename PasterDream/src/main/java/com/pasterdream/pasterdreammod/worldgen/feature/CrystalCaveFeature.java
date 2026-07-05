package com.pasterdream.pasterdreammod.worldgen.feature;

import com.pasterdream.pasterdreammod.registry.PDBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import java.util.HashSet;
import java.util.Set;

/**
 * 水晶洞穴特征 —— 在染梦世界地下 Y=-32~0 区域生成椭球体水晶洞穴
 * <p>
 * 生成算法：
 * <ol>
 *   <li>在起始位置周围构建椭球体噪声遮罩，确定洞穴主体形状</li>
 *   <li>使用随机游走算法生成拱形洞穴通道，增加自然感</li>
 *   <li>洞穴壁面有 20% 概率嵌入水晶类方块（life_crystal / windrunner_crystal_ore）</li>
 *   <li>洞穴地面替换为发光方块（meltdream_crystal_lamp）</li>
 * </ol>
 * <p>
 * 设计思路：椭球体提供基础空间，随机游走制造分叉和弯曲，
 * 最终形成既有大型空间又有狭长通道的天然水晶洞穴系统。
 *
 * @author PasterDream Team
 */
public class CrystalCaveFeature extends Feature<NoneFeatureConfiguration> {

    /** 洞穴椭球体水平半径基数 */
    private static final int CAVE_RADIUS_BASE = 6;

    /** 洞穴椭球体水平半径随机增量 */
    private static final int CAVE_RADIUS_VARY = 4;

    /** 洞穴椭球体垂直缩放（高度 = 水平半径 * 此值） */
    private static final double VERTICAL_SCALE = 0.6;

    /** 随机游走步数 */
    private static final int WALK_STEPS = 8;

    /** 随机游走单步最大偏移 */
    private static final int WALK_STEP_SIZE = 3;

    /** 水晶嵌入概率 (0~1) */
    private static final double CRYSTAL_CHANCE = 0.2;

    /** 地表替换发光方块检查半径 */
    private static final int FLOOR_CHECK_RADIUS = 2;

    /** Y 范围下限 */
    private static final int MIN_Y = -32;

    /** Y 范围上限 */
    private static final int MAX_Y = 0;

    /** 噪声频率 */
    private static final double NOISE_FREQUENCY = 0.15;

    public CrystalCaveFeature() {
        super(NoneFeatureConfiguration.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource random = context.random();

        // 确保在目标 Y 范围内
        if (origin.getY() < MIN_Y || origin.getY() > MAX_Y) {
            return false;
        }

        // 初始化椭球体参数
        int radiusH = CAVE_RADIUS_BASE + random.nextInt(CAVE_RADIUS_VARY);
        int radiusV = (int) (radiusH * VERTICAL_SCALE);
        if (radiusV < 3) radiusV = 3;

        // 使用位置哈希确定洞穴种子偏移，使不同位置的洞穴形状不同
        long posSeed = (origin.getX() * 3129871L) ^ (origin.getZ() * 116129781L) ^ (origin.getY() * 73471L);
        RandomSource caveRandom = RandomSource.create(posSeed);

        // 生成随机游走路径点（用于拱形扰动）
        int[][] walkPoints = new int[WALK_STEPS][3];
        int walkX = 0, walkY = 0, walkZ = 0;
        for (int i = 0; i < WALK_STEPS; i++) {
            walkX += caveRandom.nextInt(WALK_STEP_SIZE * 2 + 1) - WALK_STEP_SIZE;
            walkY += caveRandom.nextInt((int)(WALK_STEP_SIZE * VERTICAL_SCALE * 2) + 1) - (int)(WALK_STEP_SIZE * VERTICAL_SCALE);
            walkZ += caveRandom.nextInt(WALK_STEP_SIZE * 2 + 1) - WALK_STEP_SIZE;
            walkPoints[i][0] = walkX;
            walkPoints[i][1] = walkY;
            walkPoints[i][2] = walkZ;
        }

        Set<BlockPos> placedCrystals = new HashSet<>();
        boolean placedAny = false;

        // 遍历椭球体范围 + 游走偏移
        int checkRadius = radiusH + WALK_STEPS * WALK_STEP_SIZE / 2 + 2;
        for (int dx = -checkRadius; dx <= checkRadius; dx++) {
            for (int dz = -checkRadius; dz <= checkRadius; dz++) {
                // 水平距离平方
                double distH = dx * dx + dz * dz;
                int maxWalkOffset = WALK_STEPS * WALK_STEP_SIZE / 2;
                if (distH > (checkRadius + maxWalkOffset) * (checkRadius + maxWalkOffset)) {
                    continue;
                }

                // 计算该水平位置最近的椭球体距离（叠加随机游走扰动）
                double minDist = Double.MAX_VALUE;

                // 检查主椭球体
                double mainDist = (dx * dx + dz * dz) / (double) (radiusH * radiusH);
                minDist = Math.min(minDist, mainDist);

                // 检查每个游走点的椭球体
                for (int w = 0; w < WALK_STEPS; w++) {
                    int wdx = dx - walkPoints[w][0];
                    int wdz = dz - walkPoints[w][2];
                    double wDist = (wdx * wdx + wdz * wdz) / (double) (radiusH * radiusH * 0.36);
                    if (wDist < minDist) {
                        minDist = wDist;
                    }
                }

                if (minDist > 1.0) continue;

                // 在该水平位置遍历垂直范围
                int vRadius = (int) (radiusV * (1.0 - minDist * 0.3));
                if (vRadius < 2) vRadius = 2;

                for (int dy = -vRadius; dy <= vRadius; dy++) {
                    // 叠加随机游走的垂直偏移
                    double verticalOffset = 0;
                    for (int w = 0; w < WALK_STEPS; w++) {
                        int wdx = dx - walkPoints[w][0];
                        int wdz = dz - walkPoints[w][2];
                        double wDist = (wdx * wdx + wdz * wdz) / (double) (radiusH * radiusH * 0.25);
                        if (wDist < 1.0) {
                            verticalOffset += walkPoints[w][1] * (1.0 - wDist);
                        }
                    }

                    int actualDy = dy + (int) (verticalOffset * 0.3);
                    // 椭球体垂直判定
                    double vDist = (double) (actualDy * actualDy) / (vRadius * vRadius);
                    if (vDist > 1.0) continue;
                    double totalDist = minDist + vDist * 0.5;
                    if (totalDist > 1.0) continue;

                    // 使用洞穴噪声做二次过滤，使洞穴边缘更自然
                    double caveNoise = sampleCaveNoise(dx, dy, dz, caveRandom);
                    if (caveNoise < 0.3 && totalDist > 0.7) continue;

                    BlockPos cavePos = origin.offset(dx, dy, dz);
                    if (cavePos.getY() < level.getMinBuildHeight() || cavePos.getY() >= level.getMaxBuildHeight()) continue;

                    BlockState existing = level.getBlockState(cavePos);
                    if (existing.isAir()) continue;

                    // 替换为空气（挖出洞穴空间）
                    level.setBlock(cavePos, Blocks.AIR.defaultBlockState(), 3);
                    placedAny = true;

                    // 洞穴壁面随机嵌入水晶
                    if (caveRandom.nextFloat() < CRYSTAL_CHANCE && isCaveWall(level, cavePos)) {
                        Block crystalBlock = caveRandom.nextBoolean()
                                ? PDBlocks.LIFE_CRYSTAL.get()
                                : PDBlocks.WINDRUNNER_CRYSTAL_ORE.get();
                        level.setBlock(cavePos, crystalBlock.defaultBlockState(), 3);
                        placedCrystals.add(cavePos);
                    }
                }
            }
        }

        // 洞穴地面铺发光方块
        placeGlowFloor(level, origin, radiusH, random, placedCrystals);

        return placedAny;
    }

    /**
     * 在洞穴底部替换为发光方块
     * <p>
     * 扫描洞穴区域的底部非空气方块，替换为 {@link PDBlocks#MELTDREAM_CRYSTAL_LAMP}
     * 或 {@link PDBlocks#MOLTENGOLD_BLOCK}，形成发光地面效果。
     *
     * @param level         世界生成级别访问
     * @param origin        原点
     * @param radius        洞穴半径
     * @param random        随机数源
     * @param placedCrystals 已放置水晶的集合（避免重复替换）
     */
    private void placeGlowFloor(WorldGenLevel level, BlockPos origin, int radius,
                                RandomSource random, Set<BlockPos> placedCrystals) {
        BlockPos.MutableBlockPos floorPos = new BlockPos.MutableBlockPos();
        BlockPos.MutableBlockPos checkPos = new BlockPos.MutableBlockPos();

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                if (dx * dx + dz * dz > radius * radius) continue;

                int startY = origin.getY() - radius;
                int endY = origin.getY() + radius / 2;

                // 从下往上找洞穴地面（第一个非空气方块）
                boolean foundFloor = false;
                for (int dy = startY; dy <= endY; dy++) {
                    floorPos.set(origin.getX() + dx, dy, origin.getZ() + dz);
                    if (placedCrystals.contains(floorPos)) continue;
                    BlockState state = level.getBlockState(floorPos);
                    if (!state.isAir()) {
                        // 检查上方是否为空气（确认是地面而非墙壁）
                        boolean hasAirAbove = false;
                        for (int ab = 1; ab <= 2; ab++) {
                            checkPos.set(floorPos.getX(), floorPos.getY() + ab, floorPos.getZ());
                            if (level.getBlockState(checkPos).isAir()) {
                                hasAirAbove = true;
                                break;
                            }
                        }
                        if (hasAirAbove) {
                            Block glowBlock = random.nextFloat() < 0.3f
                                    ? PDBlocks.MOLTENGOLD_BLOCK.get()
                                    : PDBlocks.MELTDREAM_CRYSTAL_LAMP.get();
                            level.setBlock(floorPos, glowBlock.defaultBlockState(), 3);
                            foundFloor = true;
                        }
                        break;
                    }
                }
                if (!foundFloor) {
                    // 从顶往下找洞穴天花板（防止洞穴太深找不到地面）
                    for (int dy = endY; dy >= startY; dy--) {
                        floorPos.set(origin.getX() + dx, dy, origin.getZ() + dz);
                        if (placedCrystals.contains(floorPos)) continue;
                        BlockState state = level.getBlockState(floorPos);
                        if (!state.isAir()) {
                            // 检查上方是否有至少 2 格空气
                            boolean hasSpaceAbove = true;
                            for (int ab = 1; ab <= 2; ab++) {
                                checkPos.set(floorPos.getX(), floorPos.getY() + ab, floorPos.getZ());
                                if (!level.getBlockState(checkPos).isAir()) {
                                    hasSpaceAbove = false;
                                    break;
                                }
                            }
                            if (hasSpaceAbove) {
                                Block glowBlock = random.nextFloat() < 0.3f
                                        ? PDBlocks.MOLTENGOLD_BLOCK.get()
                                        : PDBlocks.MELTDREAM_CRYSTAL_LAMP.get();
                                level.setBlock(floorPos, glowBlock.defaultBlockState(), 3);
                            }
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * 判断方块位置是否为洞穴墙壁（周围有空气和非空气的交界处）
     *
     * @param level 世界生成级别访问
     * @param pos   要检查的位置
     * @return true 表示该位置是洞穴墙壁
     */
    private boolean isCaveWall(WorldGenLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (state.isAir()) return false;

        // 检查六面是否有至少一面是空气
        return level.getBlockState(pos.above()).isAir()
                || level.getBlockState(pos.below()).isAir()
                || level.getBlockState(pos.north()).isAir()
                || level.getBlockState(pos.south()).isAir()
                || level.getBlockState(pos.east()).isAir()
                || level.getBlockState(pos.west()).isAir();
    }

    /**
     * 使用简易噪声采样，用于洞穴边缘的二次过滤
     *
     * @param dx     局部偏移 X
     * @param dy     局部偏移 Y
     * @param dz     局部偏移 Z
     * @param random 随机数源
     * @return 噪声值 (0~1)
     */
    private double sampleCaveNoise(int dx, int dy, int dz, RandomSource random) {
        // 使用正弦/余弦组合产生平滑的 3D 噪声
        double fx = dx * NOISE_FREQUENCY;
        double fy = dy * NOISE_FREQUENCY;
        double fz = dz * NOISE_FREQUENCY;
        double baseSeed = random.nextDouble() * 100.0;

        double noise = Math.sin(fx * 2.3 + fy * 1.7 + fz * 3.1 + baseSeed) * 0.5
                + Math.cos(fx * 4.1 - fy * 0.9 + fz * 2.3 + baseSeed * 1.3) * 0.3
                + Math.sin(fx * 0.7 + fy * 5.3 - fz * 1.1 + baseSeed * 0.7) * 0.2;
        return (noise + 1.0) / 2.0; // 归一化到 0~1
    }
}
