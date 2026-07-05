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

import java.util.ArrayList;
import java.util.List;

/**
 * 悬浮水晶核心特征 —— 在地下洞穴中生成中央主水晶柱 + 环绕小水晶的结构
 * <p>
 * 生成逻辑：
 * <ol>
 *   <li>检测周围 12 格半径内是否有足够大的空间（空气占比 > 50%）</li>
 *   <li>在中心生成一根 3~8 格高的主水晶柱（meltdream_crystal_lamp）</li>
 *   <li>在主柱周围 2~4 格半径处生成 3~5 个小型悬浮水晶</li>
 *   <li>小水晶高度 1~3 格，使用 life_crystal 和 windrunner_crystal_ore</li>
 * </ol>
 * <p>
 * 适合生成在水晶洞穴、大空腔中作为视觉焦点。
 *
 * @author PasterDream Team
 */
public class SuspendedCrystalFeature extends Feature<NoneFeatureConfiguration> {

    /** 空间检测半径 */
    private static final int SPACE_CHECK_RADIUS = 12;

    /** 空间判定阈值 —— 空气占比超过此值才生成 */
    private static final double SPACE_THRESHOLD = 0.5;

    /** 主水晶柱最小高度 */
    private static final int MAIN_MIN_HEIGHT = 3;

    /** 主水晶柱最大高度 */
    private static final int MAIN_MAX_HEIGHT = 8;

    /** 周围小水晶数量范围 */
    private static final int SATELLITE_MIN = 3;

    /** 周围小水晶数量范围 */
    private static final int SATELLITE_MAX = 5;

    /** 小水晶水平散布半径 */
    private static final int SATELLITE_RADIUS = 4;

    /** 小水晶最小高度 */
    private static final int SATELLITE_MIN_HEIGHT = 1;

    /** 小水晶最大高度 */
    private static final int SATELLITE_MAX_HEIGHT = 3;

    /** 小水晶距离主柱的偏移量 */
    private static final int SATELLITE_HORIZONTAL_OFFSET = 3;

    public SuspendedCrystalFeature() {
        super(NoneFeatureConfiguration.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource random = context.random();

        // 1. 检测周围空间是否足够大
        if (!hasEnoughSpace(level, origin)) {
            return false;
        }

        // 找到悬挂点（第一个非空气方块的上方）
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos(origin.getX(), origin.getY(), origin.getZ());
        int ceilingY = origin.getY();
        for (int dy = 0; dy < 5; dy++) {
            mutablePos.setY(origin.getY() - dy);
            if (!level.getBlockState(mutablePos).isAir()) {
                ceilingY = origin.getY() - dy;
                break;
            }
        }

        boolean placedAny = false;

        // 2. 生成主水晶柱（从天花板向下生长）
        int mainHeight = MAIN_MIN_HEIGHT + random.nextInt(MAIN_MAX_HEIGHT - MAIN_MIN_HEIGHT + 1);
        for (int i = 0; i < mainHeight; i++) {
            BlockPos crystalPos = new BlockPos(origin.getX(), ceilingY - i, origin.getZ());
            if (crystalPos.getY() < level.getMinBuildHeight()) break;
            if (!level.getBlockState(crystalPos).isAir()) break;
            level.setBlock(crystalPos, PDBlocks.MELTDREAM_CRYSTAL_LAMP.get().defaultBlockState(), 3);
            placedAny = true;
        }

        // 3. 生成周围小水晶
        int satelliteCount = SATELLITE_MIN + random.nextInt(SATELLITE_MAX - SATELLITE_MIN + 1);
        // 收集已放置的小水晶位置，防重叠
        List<BlockPos> placedSatellites = new ArrayList<>();

        for (int i = 0; i < satelliteCount; i++) {
            // 在环形上随机取角度
            double angle = random.nextDouble() * Math.PI * 2;
            int sx = origin.getX() + (int) (Math.cos(angle) * (SATELLITE_HORIZONTAL_OFFSET + random.nextInt(SATELLITE_RADIUS - SATELLITE_HORIZONTAL_OFFSET + 1)));
            int sz = origin.getZ() + (int) (Math.sin(angle) * (SATELLITE_HORIZONTAL_OFFSET + random.nextInt(SATELLITE_RADIUS - SATELLITE_HORIZONTAL_OFFSET + 1)));

            // 垂直偏移：在主柱高度范围内随机
            int sy = ceilingY - random.nextInt(mainHeight);

            // 检测重叠
            boolean overlaps = false;
            for (BlockPos placed : placedSatellites) {
                if (Math.abs(placed.getX() - sx) <= 1 && Math.abs(placed.getZ() - sz) <= 1
                        && Math.abs(placed.getY() - sy) <= 2) {
                    overlaps = true;
                    break;
                }
            }
            if (overlaps) continue;

            // 检测位置是否在空气中
            BlockPos satPos = new BlockPos(sx, sy, sz);
            if (!level.getBlockState(satPos).isAir()) continue;

            // 随机选择小水晶方块类型
            Block crystalBlock = switch (random.nextInt(3)) {
                case 0 -> PDBlocks.LIFE_CRYSTAL.get();
                case 1 -> PDBlocks.WINDRUNNER_CRYSTAL_ORE.get();
                default -> PDBlocks.MELTDREAM_CRYSTAL_LAMP.get();
            };

            // 小水晶高度
            int satHeight = SATELLITE_MIN_HEIGHT + random.nextInt(SATELLITE_MAX_HEIGHT - SATELLITE_MIN_HEIGHT + 1);
            for (int h = 0; h < satHeight; h++) {
                BlockPos hPos = new BlockPos(sx, sy - h, sz);
                if (hPos.getY() < level.getMinBuildHeight()) break;
                if (!level.getBlockState(hPos).isAir()) break;
                level.setBlock(hPos, crystalBlock.defaultBlockState(), 3);
                placedAny = true;
            }
            placedSatellites.add(satPos);

            // 小水晶底部偶尔加一个小的发光末端
            BlockPos tipPos = new BlockPos(sx, sy - satHeight, sz);
            if (tipPos.getY() >= level.getMinBuildHeight() && level.getBlockState(tipPos).isAir()
                    && random.nextFloat() < 0.4f) {
                level.setBlock(tipPos, PDBlocks.MELTDREAM_CRYSTAL_LAMP.get().defaultBlockState(), 3);
            }
        }

        return placedAny;
    }

    /**
     * 检测周围 12 格半径内空间是否足够大
     * <p>
     * 采样柱状空间，计算空气方块占比。只有在开阔空间
     * （洞穴/空腔）中才生成悬浮水晶，避免嵌入密实岩层。
     *
     * @param level  世界生成级别访问
     * @param center 检测中心位置
     * @return true 表示空间足够大
     */
    private boolean hasEnoughSpace(WorldGenLevel level, BlockPos center) {
        int airCount = 0;
        int totalCount = 0;
        BlockPos.MutableBlockPos checkPos = new BlockPos.MutableBlockPos();

        // 分三层采样（Y 方向间隔 4 格），减少计算量
        for (int dy = -SPACE_CHECK_RADIUS / 2; dy <= SPACE_CHECK_RADIUS / 2; dy += 4) {
            for (int dx = -SPACE_CHECK_RADIUS; dx <= SPACE_CHECK_RADIUS; dx += 2) {
                for (int dz = -SPACE_CHECK_RADIUS; dz <= SPACE_CHECK_RADIUS; dz += 2) {
                    int distSq = dx * dx + dz * dz + dy * dy * 4;
                    if (distSq > SPACE_CHECK_RADIUS * SPACE_CHECK_RADIUS) continue;

                    checkPos.set(center.getX() + dx, center.getY() + dy, center.getZ() + dz);
                    if (checkPos.getY() < level.getMinBuildHeight() || checkPos.getY() >= level.getMaxBuildHeight()) continue;

                    if (level.getBlockState(checkPos).isAir()) {
                        airCount++;
                    }
                    totalCount++;
                }
            }
        }

        return totalCount > 0 && (double) airCount / totalCount > SPACE_THRESHOLD;
    }
}
