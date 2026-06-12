package com.pasterdream.pasterdreammod.worldgen.feature;

import com.pasterdream.pasterdreammod.worldgen.WorldGenUtils;
import com.pasterdream.pasterdreammod.worldgen.decor.DecorationConfig;
import com.pasterdream.pasterdreammod.worldgen.decor.ICustomDecorationGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import com.pasterdream.pasterdreammod.registry.PDBlocks;
import net.minecraft.util.random.SimpleWeightedRandomList;

/**
 * 冰拱门生成器 —— 弯曲管状半圆拱形冰结构
 * <p>
 * 沿拱门余弦曲线以细密步长放置 3D 球体，相邻球体重叠形成连续的弯曲管道。
 * 方块选择依据高度渐变：
 * - 水面以下：原版冰系方块（冰/浮冰/蓝冰）
 * - 水面附近：过渡混合
 * - 水面以上：染梦冰系方块 + 原版冰 + 雪块混合，越往上雪越多
 * 管道两端从地面伸出并逐渐加宽。
 */
public class IceArchGenerator implements ICustomDecorationGenerator {

    private static final float STEP = 0.25f;

    private static final SimpleWeightedRandomList<BlockState> VANILLA_ICE;

    static {
        VANILLA_ICE = SimpleWeightedRandomList.<BlockState>builder()
                .add(Blocks.ICE.defaultBlockState(), 50)
                .add(Blocks.PACKED_ICE.defaultBlockState(), 35)
                .add(Blocks.BLUE_ICE.defaultBlockState(), 15)
                .build();
    }

    @Override
    public boolean generate(FeaturePlaceContext<DecorationConfig> context) {
        DecorationConfig config = context.config();
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource random = context.random();

        int leftX = origin.getX() - config.gateMaxWidth();
        int rightX = origin.getX() + config.gateMaxWidth();
        int centerZ = origin.getZ();

        int leftGroundY = WorldGenUtils.findGroundY(level, config.replaceable(),
                leftX, origin.getY(), centerZ, 50);
        int rightGroundY = WorldGenUtils.findGroundY(level, config.replaceable(),
                rightX, origin.getY(), centerZ, 50);

        boolean leftValid = leftGroundY != Integer.MIN_VALUE
                && WorldGenUtils.isSolidSurface(level, new BlockPos(leftX, leftGroundY - 1, centerZ));
        boolean rightValid = rightGroundY != Integer.MIN_VALUE
                && WorldGenUtils.isSolidSurface(level, new BlockPos(rightX, rightGroundY - 1, centerZ));

        if (!leftValid && !rightValid) return false;

        int baseY = leftValid && rightValid
                ? Math.min(leftGroundY, rightGroundY)
                : (leftValid ? leftGroundY : rightGroundY);

        int seaLevel = level.getSeaLevel();
        int waterDepth = Math.max(0, seaLevel - baseY);
        int minArchHeight = Math.max(config.minHeight(), waterDepth + 10);
        int maxArchHeight = Math.max(config.maxHeight(), waterDepth + 30);
        int height = random.nextIntBetweenInclusive(minArchHeight, maxArchHeight);

        int aboveWaterHeight = height - waterDepth;
        int minSpan = Math.max(config.gateMinWidth(), aboveWaterHeight * 2);
        int maxSpan = Math.max(config.gateMaxWidth(), aboveWaterHeight * 3);
        maxSpan = Math.min(maxSpan, 48);
        minSpan = Math.min(minSpan, maxSpan);
        int halfWidth = random.nextIntBetweenInclusive(minSpan, maxSpan) / 2;
        if (halfWidth < 2) halfWidth = 2;

        int centerX = origin.getX();
        int tubeRadius = Math.max(3, config.beamThickness());
        int baseFlareRadius = tubeRadius + 1;
        boolean placedAny = false;

        int steps = (int) Math.ceil(halfWidth * 2.0f / STEP) + 1;

        for (int s = 0; s <= steps; s++) {
            float dx = -halfWidth + s * STEP;
            if (dx > halfWidth) dx = halfWidth;

            float absDx = Math.abs(dx);
            float xProgress = halfWidth > 0 ? absDx / halfWidth : 1.0f;
            float archY = baseY + height * (float) Math.cos(xProgress * Math.PI / 2.0);

            int cx = Math.round(centerX + dx);
            int cy = Math.round(archY);

            float localRadius = tubeRadius;
            if (xProgress > 0.7f) {
                float rp = (xProgress - 0.7f) / 0.3f;
                localRadius = tubeRadius + (baseFlareRadius - tubeRadius) * rp;
            }

            int sRad = (int) Math.ceil(localRadius);

            for (int bx = -sRad; bx <= sRad; bx++) {
                for (int by = -sRad; by <= sRad; by++) {
                    for (int bz = -sRad; bz <= sRad; bz++) {
                        float distSq = bx * bx + by * by + bz * bz;
                        if (distSq > localRadius * localRadius + 0.5f) continue;

                        BlockPos pos = new BlockPos(cx + bx, cy + by, centerZ + bz);
                        if (!WorldGenUtils.isWithinGenerationBounds(origin, pos)) continue;
                        if (!WorldGenUtils.isReplaceable(level, config.replaceable(), pos)) continue;

                        BlockState state = pickBlockForHeight(random, pos.getY(), seaLevel, config, pos);
                        level.setBlock(pos, state, 3);
                        placedAny = true;
                    }
                }
            }
        }

        addIcicles(level, random, config, origin, centerX, centerZ, baseY, height, halfWidth, tubeRadius);
        addDebrisScatter(level, random, config, centerX, centerZ, baseY, halfWidth, tubeRadius);
        addGlowDecorations(level, random, config, origin, centerZ, baseY, halfWidth, tubeRadius, height);

        return placedAny;
    }

    /**
     * 根据方块高度选择材质，实现从水下到水上的渐变过渡
     * <p>
     * 高度分级策略：
     * - heightAboveSea < -3（深水区）：纯原版冰系方块
     * - -3 ~ 0（水面过渡）：原版冰为主，混入少量染梦冰
     * - 0 ~ 10（低出水）：染梦冰为主，混入原版冰和雪
     * - 10 ~ 20（中段）：雪和染梦冰对半
     * - > 20（顶部）：雪为主，点缀染梦冰
     */
    private BlockState pickBlockForHeight(RandomSource random, int worldY, int seaLevel,
                                           DecorationConfig config, BlockPos pos) {
        int heightAboveSea = worldY - seaLevel;

        if (heightAboveSea < -3) {
            return VANILLA_ICE.getRandomValue(random).orElse(Blocks.PACKED_ICE.defaultBlockState());
        } else if (heightAboveSea < 0) {
            if (random.nextFloat() < 0.2f) {
                return config.bodyBlock().getState(random, pos);
            }
            return VANILLA_ICE.getRandomValue(random).orElse(Blocks.PACKED_ICE.defaultBlockState());
        } else if (heightAboveSea < 10) {
            float r = random.nextFloat();
            if (r < 0.55f) {
                return config.bodyBlock().getState(random, pos);
            } else if (r < 0.75f) {
                return VANILLA_ICE.getRandomValue(random).orElse(Blocks.PACKED_ICE.defaultBlockState());
            } else {
                return Blocks.SNOW_BLOCK.defaultBlockState();
            }
        } else if (heightAboveSea < 20) {
            float r = random.nextFloat();
            if (r < 0.35f) {
                return config.bodyBlock().getState(random, pos);
            } else if (r < 0.50f) {
                return VANILLA_ICE.getRandomValue(random).orElse(Blocks.PACKED_ICE.defaultBlockState());
            } else {
                return Blocks.SNOW_BLOCK.defaultBlockState();
            }
        } else {
            if (config.topBlock() != null && random.nextFloat() < 0.3f) {
                return config.topBlock().getState(random, pos);
            }
            return Blocks.SNOW_BLOCK.defaultBlockState();
        }
    }

    /**
     * 在拱门底部周围散落碎冰渣（损坏变种效果）
     */
    private void addDebrisScatter(WorldGenLevel level, RandomSource random, DecorationConfig config,
                                   int centerX, int centerZ, int baseY,
                                   int halfWidth, int tubeRadius) {
        float scatterBaseChance = config.decorationChance() * 1.5f;
        if (random.nextFloat() >= scatterBaseChance) return;

        int debrisCount = 5 + random.nextInt(12);
        for (int i = 0; i < debrisCount; i++) {
            int sx = centerX + random.nextInt(halfWidth * 2 + 1) - halfWidth;
            int sz = centerZ + random.nextInt(tubeRadius * 4 + 1) - tubeRadius * 2;

            int sy = level.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, sx, sz);
            if (sy < baseY - 2) sy = baseY - 2;

            BlockPos sPos = new BlockPos(sx, sy + 1, sz);
            if (!WorldGenUtils.isReplaceable(level, config.replaceable(), sPos)) continue;

            BlockState debrisState = config.bodyBlock().getState(random, sPos);
            level.setBlock(sPos, debrisState, 3);
        }
    }

    /**
     * 在拱门底部内侧悬挂冰凌（钟乳石状冰柱）
     * <p>
     * 沿拱门底部轮廓采样，在拱门内沿下方生成随机长度的下垂冰凌。
     * 冰凌长度 2~5 格，生成概率约 35%，仅出现在拱门内壁底部附近。
     */
    private void addIcicles(WorldGenLevel level, RandomSource random, DecorationConfig config,
                             BlockPos origin, int centerX, int centerZ, int baseY, int height,
                             int halfWidth, int tubeRadius) {
        int steps = (int) Math.ceil(halfWidth * 2.0f / STEP) + 1;
        for (int s = 0; s <= steps; s++) {
            float dx = -halfWidth + s * STEP;
            if (dx > halfWidth) dx = halfWidth;
            float absDx = Math.abs(dx);
            float xProgress = halfWidth > 0 ? absDx / halfWidth : 1.0f;
            if (xProgress > 0.6f) continue;
            float archY = baseY + height * (float) Math.cos(xProgress * Math.PI / 2.0);
            int cx = Math.round(centerX + dx);
            int cy = Math.round(archY);
            float localRadius = tubeRadius;
            if (xProgress > 0.7f) {
                float rp = (xProgress - 0.7f) / 0.3f;
                localRadius = tubeRadius + 1 * rp;
            }
            int sRad = (int) Math.ceil(localRadius);
            for (int bz = -sRad; bz <= sRad; bz += 2) {
                for (int by = -sRad + 1; by <= 0; by++) {
                    float distSq = by * by + bz * bz;
                    if (distSq > localRadius * localRadius + 0.5f) continue;
                    if (random.nextFloat() > 0.08f) continue;
                    int icicleLen = 3 + random.nextInt(5);
                    for (int i = 1; i <= icicleLen; i++) {
                        BlockPos pos = new BlockPos(cx, cy + by - i, centerZ + bz);
                        if (!WorldGenUtils.isWithinGenerationBounds(origin, pos)) break;
                        if (!WorldGenUtils.isReplaceable(level, config.replaceable(), pos)) break;
                        level.setBlock(pos, config.bodyBlock().getState(random, pos), 3);
                    }
                    break;
                }
            }
        }
    }

    /**
     * 在拱门底部地面附近随机添加发光冰晶方块作为装饰点缀
     * <p>
     * 在拱门底部范围内随机选取 1~2 个位置，检测地面高度后放置 ICE_BUD_0 发光方块。
     * 仅放置在与拱门底部高度差不超过 5 格且可替换的位置上。
     */
    private void addGlowDecorations(WorldGenLevel level, RandomSource random, DecorationConfig config,
                                     BlockPos origin, int centerZ, int baseY,
                                     int halfWidth, int tubeRadius, int height) {
        int glowCount = 1 + random.nextInt(2);
        for (int g = 0; g < glowCount; g++) {
            int gx = origin.getX() + random.nextInt(halfWidth * 2 + 1) - halfWidth;
            int gz = centerZ + random.nextInt(tubeRadius * 4 + 1) - tubeRadius * 2;
            int gy = level.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, gx, gz);
            if (gy < baseY - 2) gy = baseY - 2;
            if (Math.abs(gy - baseY) > 5) continue;
            BlockPos gPos = new BlockPos(gx, gy + 1, gz);
            if (!WorldGenUtils.isReplaceable(level, config.replaceable(), gPos)) continue;
            if (!WorldGenUtils.isWithinGenerationBounds(origin, gPos)) continue;
            level.setBlock(gPos, PDBlocks.ICE_BUD_0.get().defaultBlockState(), 3);
        }
    }
}