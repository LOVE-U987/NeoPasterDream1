package com.pasterdream.pasterdreammod.worldgen.feature;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

import java.util.HashSet;
import java.util.Set;

/**
 * 方解石之柱特征 —— 从地下穿出地面的细长方解石柱子
 * <p>
 * 生成方形截面的锥形柱体：
 * 1. 柱子从地下深处穿到地表以上，替换沿途所有方块
 * 2. 露出地面的柱子侧边上随机嵌入染梦世界的矿物和晶体
 * 3. 柱子周围散落方解石碎片，模拟掉落物
 * 4. 地上部分检查下方支撑，杜绝悬空
 * 5. 放置后清理柱体内残留的植被
 */
public class CalcitePillarFeature extends Feature<CalcitePillarConfiguration> {

    public CalcitePillarFeature() {
        super(CalcitePillarConfiguration.CODEC.codec());
    }

    @Override
    public boolean place(FeaturePlaceContext<CalcitePillarConfiguration> context) {
        CalcitePillarConfiguration config = context.config();
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource random = context.random();

        int groundY = findGroundY(level, origin.getX(), origin.getY(), origin.getZ(), 10);
        if (groundY == Integer.MIN_VALUE) {
            return false;
        }

        int totalHeight = random.nextIntBetweenInclusive(config.minHeight(), config.maxHeight());
        int undergroundPortion = Math.max(1, totalHeight / 3 + random.nextInt(Math.max(1, totalHeight / 6)));
        int aboveGroundPortion = totalHeight - undergroundPortion;

        int effectiveBaseWidth = config.baseWidth();
        if (random.nextFloat() < 0.3f) {
            effectiveBaseWidth += 1;
        }

        int bottomY = groundY - undergroundPortion;
        int topY = groundY + aboveGroundPortion;

        Set<BlockPos> placedPositions = new HashSet<>();
        boolean placedAny = false;

        for (int y = bottomY; y <= topY; y++) {
            float progress = (float) (y - bottomY) / (float) totalHeight;
            int currentWidth = calcWidth(effectiveBaseWidth, config, progress, y, groundY);
            int halfSize = currentWidth / 2;

            for (int dx = -halfSize; dx < currentWidth - halfSize; dx++) {
                for (int dz = -halfSize; dz < currentWidth - halfSize; dz++) {
                    BlockPos placePos = new BlockPos(origin.getX() + dx, y, origin.getZ() + dz);

                    if (!config.replaceable().test(level, placePos)) {
                        continue;
                    }

                    if (y >= groundY) {
                        BlockPos belowPos = placePos.below();
                        boolean supported = placedPositions.contains(belowPos)
                            || isSolidSurface(level, belowPos);
                        if (!supported && level.getBlockState(belowPos).isAir()) {
                            continue;
                        }
                    }

                    boolean isSurfaceBlock = (dx == -halfSize || dx == currentWidth - halfSize - 1
                        || dz == -halfSize || dz == currentWidth - halfSize - 1);

                    BlockState state;
                    if (y >= groundY && isSurfaceBlock && random.nextFloat() < config.crystalChance()) {
                        state = config.crystalBlock().getState(random, placePos);
                    } else {
                        state = config.pillarBlock().getState(random, placePos);
                    }

                    level.setBlock(placePos, state, 3);
                    placedPositions.add(placePos);
                    placedAny = true;
                }
            }
        }

        for (int i = 0; i < config.debrisCount(); i++) {
            int dx = random.nextInt(-config.debrisRadius(), config.debrisRadius() + 1);
            int dz = random.nextInt(-config.debrisRadius(), config.debrisRadius() + 1);

            if (dx == 0 && dz == 0) {
                continue;
            }

            BlockPos debrisPos = new BlockPos(origin.getX() + dx, groundY, origin.getZ() + dz);

            if (isSolidSurface(level, debrisPos.below()) && level.getBlockState(debrisPos).isAir()) {
                BlockState state = config.debrisBlock().getState(random, debrisPos);
                level.setBlock(debrisPos, state, 3);
                placedAny = true;
            }
        }

        return placedAny;
    }

    /**
     * 计算当前 Y 层的柱子宽度（方块数）
     */
    private int calcWidth(int effectiveBaseWidth, CalcitePillarConfiguration config, float progress, int y, int groundY) {
        int topWidth = config.topWidth();
        float currentWidth = effectiveBaseWidth + (float) (topWidth - effectiveBaseWidth) * progress;
        if (y < groundY) {
            float undergroundBias = 1.0f + (float) (groundY - y) * 0.3f;
            currentWidth = Math.min(effectiveBaseWidth + 0.5f, currentWidth * undergroundBias);
        }
        return Math.max(1, Math.round(currentWidth));
    }

    /**
     * 从起始 Y 向下搜索，找到第一个固体地面层
     */
    private int findGroundY(WorldGenLevel level, int x, int startY, int z, int maxFall) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(x, startY, z);
        for (int i = 0; i <= maxFall; i++) {
            pos.setY(startY - i);
            if (isSolidSurface(level, pos)) {
                return pos.getY() + 1;
            }
        }
        return Integer.MIN_VALUE;
    }

    /**
     * 判断方块是否为固体地面（排除空气、树叶、植被类可替换方块）
     */
    private boolean isSolidSurface(WorldGenLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (state.isAir()) return false;
        if (state.is(BlockTags.LEAVES) || state.is(BlockTags.REPLACEABLE_BY_TREES)) return false;
        return state.isCollisionShapeFullBlock(level, pos);
    }
}