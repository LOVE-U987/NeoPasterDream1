package com.pasterdream.pasterdreammod.worldgen.feature;

import com.pasterdream.pasterdreammod.worldgen.WorldGenUtils;
import com.pasterdream.pasterdreammod.worldgen.decor.DecorationConfig;
import com.pasterdream.pasterdreammod.worldgen.decor.ICustomDecorationGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * 冰门生成器 —— 整合完整冰门与倒塌冰门变种
 * <p>
 * 当两根支柱都能找到坚实地面时，生成完整的双柱+横梁冰门；
 * 当只有一根支柱能找到坚实地面时，生成倒塌变种（倾斜版或断柱版）。
 * 倒塌时横梁从柱顶坠落，在地面摔碎成冰带。
 */
public class IceGateGenerator implements ICustomDecorationGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(IceGateGenerator.class);

    @Override
    public boolean generate(FeaturePlaceContext<DecorationConfig> context) {
        DecorationConfig config = context.config();
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource random = context.random();

        int height = random.nextIntBetweenInclusive(config.minHeight(), config.maxHeight());
        int halfWidth = random.nextIntBetweenInclusive(config.gateMinWidth(), config.gateMaxWidth()) / 2;
        int radius = config.pillarRadius();

        int leftX = origin.getX() - halfWidth;
        int rightX = origin.getX() + halfWidth;

        int leftGroundY = WorldGenUtils.findGroundY(level, config.replaceable(),
                leftX, origin.getY(), origin.getZ(), 35);
        int rightGroundY = WorldGenUtils.findGroundY(level, config.replaceable(),
                rightX, origin.getY(), origin.getZ(), 35);

        boolean leftValid = leftGroundY != Integer.MIN_VALUE
                && WorldGenUtils.isSolidSurface(level, new BlockPos(leftX, leftGroundY - 1, origin.getZ()));
        boolean rightValid = rightGroundY != Integer.MIN_VALUE
                && WorldGenUtils.isSolidSurface(level, new BlockPos(rightX, rightGroundY - 1, origin.getZ()));

        if (!leftValid && !rightValid) {
            return false;
        }

        Set<BlockPos> placedPositions = new HashSet<>();

        if (leftValid && rightValid) {
            if (random.nextFloat() < 0.3f) {
                boolean tiltLeft = random.nextBoolean();
                int singleX = tiltLeft ? leftX : rightX;
                int singleGroundY = tiltLeft ? leftGroundY : rightGroundY;
                int tiltDir = tiltLeft ? -1 : 1;
                return placeFallenGate(level, random, config, singleX, origin.getZ(),
                        singleGroundY, height, halfWidth, radius, tiltDir, placedPositions);
            }
            return placeNormalGate(level, random, config, leftX, rightX, origin.getZ(),
                    leftGroundY, rightGroundY, height, halfWidth, radius, placedPositions);
        } else {
            int singleX = leftValid ? leftX : rightX;
            int singleGroundY = leftValid ? leftGroundY : rightGroundY;
            int tiltDir = leftValid ? -1 : 1;
            return placeFallenGate(level, random, config, singleX, origin.getZ(),
                    singleGroundY, height, halfWidth, radius, tiltDir, placedPositions);
        }
    }

    /**
     * 生成完整冰门（双柱+顶部横梁）
     * <p>
     * 两侧支柱以圆形截面从地面竖立到顶部，在顶部横梁范围内延伸连接两柱。
     * 柱子顶部 30% 位置使用配置中的 topBlock（若有），其余使用 bodyBlock。
     * 横梁跨段（两柱之间）直接放置，延伸段（柱外）检查下方支撑。
     */
    private boolean placeNormalGate(WorldGenLevel level, RandomSource random, DecorationConfig config,
                                     int leftX, int rightX, int centerZ,
                                     int leftGroundY, int rightGroundY,
                                     int height, int halfWidth, int radius,
                                     Set<BlockPos> placedPositions) {
        int baseY = Math.min(leftGroundY, rightGroundY);
        int topY = baseY + height;
        int beamThick = config.beamThickness();
        int beamStartY = topY - beamThick * 3;
        boolean placedAny = false;

        for (int y = baseY; y <= topY; y++) {
            int currentRadius = (y > topY - beamThick * 2)
                    ? radius + 1
                    : Math.max(1, radius - (int) (y - baseY) * (radius > 1 ? 1 : 0) / height);

            boolean placedLeft = placePillar(level, random, config, leftX, centerZ,
                    y, currentRadius, baseY, topY, placedPositions);
            boolean placedRight = placePillar(level, random, config, rightX, centerZ,
                    y, currentRadius, baseY, topY, placedPositions);
            if (placedLeft || placedRight) {
                placedAny = true;
            }

            if (y >= beamStartY && y <= topY) {
                if (!placedLeft || !placedRight) {
                    continue;
                }
                if (currentRadius < 2) {
                    continue;
                }

                int beamHalf = halfWidth + currentRadius;
                for (int bx = -beamHalf; bx <= beamHalf; bx++) {
                    for (int bz = -currentRadius; bz <= currentRadius; bz++) {
                        BlockPos beamPos = new BlockPos(leftX + halfWidth + bx, y, centerZ + bz);

                        if (!WorldGenUtils.isReplaceable(level, config.replaceable(), beamPos)) {
                            continue;
                        }

                        boolean isSpan = Math.abs(bx) <= halfWidth;
                        if (!isSpan) {
                            if (!hasSupport(level, beamPos, placedPositions)) {
                                continue;
                            }
                        }

                        BlockState beamState = (isSpan && config.topBlock() != null)
                                ? config.topBlock().getState(random, beamPos)
                                : config.bodyBlock().getState(random, beamPos);
                        level.setBlock(beamPos, beamState, 3);
                        placedPositions.add(beamPos);
                        placedAny = true;
                    }
                }
            }
        }

        for (int i = 0; i < height / 4; i++) {
            int dx = random.nextInt(halfWidth + radius + 3) - (halfWidth + radius + 3) / 2;
            int dz = random.nextInt(radius + 3) - (radius + 3) / 2;
            int dy = random.nextInt(height / 3);
            BlockPos decoPos = new BlockPos(leftX + halfWidth + dx, baseY + dy + 1, centerZ + dz);
            if (hasSupport(level, decoPos, placedPositions)
                    && WorldGenUtils.isReplaceable(level, config.replaceable(), decoPos)
                    && random.nextFloat() < config.decorationChance()) {
                BlockState state = config.bodyBlock().getState(random, decoPos);
                level.setBlock(decoPos, state, 3);
                placedPositions.add(decoPos);
                placedAny = true;
            }
        }

        addGlowDecorations(level, random, config, leftX + halfWidth, centerZ, baseY, halfWidth);

        return placedAny;
    }

    /**
     * 生成倒塌冰门
     * <p>
     * 当只有一根支柱有支撑时触发。
     * 随机选择倾斜版（整体均匀倾斜 15~45°）或断柱版（40~70% 处折断）。
     * 横梁从顶部坠落，在柱底到对面方向形成碎裂冰带。
     */
    private boolean placeFallenGate(WorldGenLevel level, RandomSource random, DecorationConfig config,
                                     int centerX, int centerZ, int groundY,
                                     int height, int halfWidth, int radius,
                                     int tiltDir, Set<BlockPos> placedPositions) {
        int topY = groundY + height;
        float angleDeg = 5 + random.nextFloat() * 5;
        float angleRad = (float) Math.toRadians(angleDeg);
        float totalShift = height * (float) Math.tan(angleRad);

        boolean isTilted = random.nextBoolean();
        boolean placedAny;

        if (isTilted) {
            placedAny = placeTilted(level, random, config, centerX, centerZ,
                    groundY, topY, radius, tiltDir, totalShift, halfWidth, placedPositions);
        } else {
            placedAny = placeBroken(level, random, config, centerX, centerZ,
                    groundY, topY, radius, tiltDir, halfWidth, placedPositions);
        }

        boolean beamStaked = random.nextFloat() < 0.4f;
        if (beamStaked) {
            placedAny |= placeStakedBeam(level, random, config,
                    centerX, centerZ, groundY, tiltDir, halfWidth, height);
        } else {
            placedAny |= placeFallenBeam(level, random, config,
                    centerX, centerZ, groundY, -tiltDir, halfWidth, height);
        }

        addGlowDecorations(level, random, config, centerX, centerZ, groundY, halfWidth);

        return placedAny;
    }

    /**
     * 放置圆形截面柱子（用于完整冰门）
     */
    private boolean placePillar(WorldGenLevel level, RandomSource random, DecorationConfig config,
                                 int centerX, int centerZ, int y, int radius,
                                 int baseY, int topY, Set<BlockPos> placedPositions) {
        boolean placed = false;
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                float distSq = dx * dx + dz * dz;
                if (distSq > (radius + 0.5f) * (radius + 0.5f)) {
                    continue;
                }
                BlockPos pos = new BlockPos(centerX + dx, y, centerZ + dz);
                if (!WorldGenUtils.isReplaceable(level, config.replaceable(), pos)) {
                    continue;
                }
                if (y > baseY) {
                    if (!hasSupport(level, pos, placedPositions)) {
                        continue;
                    }
                }
                float heightProgress = (float) (y - baseY) / (float) (topY - baseY);
                boolean isTopSection = heightProgress > 0.7f;
                BlockState state = (isTopSection && config.topBlock() != null)
                        ? config.topBlock().getState(random, pos)
                        : config.bodyBlock().getState(random, pos);
                level.setBlock(pos, state, 3);
                placedPositions.add(pos);
                placed = true;
            }
        }
        return placed;
    }

    /**
     * 倾斜版倒塌：整体均匀倾斜，顶端按 60% 斜率切割
     * <p>
     * 从柱尖 (centerX + totalShift*tiltDir, topY) 向柱底方向，
     * 顶面按 60% 斜率（每水平1格垂直下降0.6格）切削。
     * 高于该斜面的方块被切除，形成自然的碎裂斜面。
     */
    private boolean placeTilted(WorldGenLevel level, RandomSource random, DecorationConfig config,
                                 int centerX, int centerZ, int groundY, int topY,
                                 int radius, int tiltDir, float totalShift, int halfWidth,
                                 Set<BlockPos> placedPositions) {
        int height = topY - groundY;
        int tipX = centerX + Math.round(totalShift * tiltDir);
        float cutSlope = 0.6f;
        boolean placedAny = false;

        for (int y = groundY; y <= topY; y++) {
            int relativeY = y - groundY;
            float progress = (float) relativeY / height;
            int currentOffset = Math.round(totalShift * progress);
            int pillarX = centerX + currentOffset * tiltDir;

            int currentRadius = Math.max(1, radius - (int) (progress * (radius - 1)));

            for (int dx = -currentRadius; dx <= currentRadius; dx++) {
                for (int dz = -currentRadius; dz <= currentRadius; dz++) {
                    float distSq = dx * dx + dz * dz;
                    if (distSq > (currentRadius + 0.5f) * (currentRadius + 0.5f)) {
                        continue;
                    }

                    int bx = pillarX + dx;
                    int towardCenter = (tipX - bx) * tiltDir;
                    if (towardCenter < 0) continue;
                    float cutY = topY - cutSlope * towardCenter;
                    if (y > cutY) continue;

                    BlockPos pos = new BlockPos(bx, y, centerZ + dz);
                    BlockState state = config.bodyBlock().getState(random, pos);
                    level.setBlock(pos, state, 3);
                    placedPositions.add(pos);
                    placedAny = true;
                }
            }
        }
        return placedAny;
    }

    /**
     * 断柱版倒塌：柱子中部折断，约 50% 直立在地、50% 横躺在地面
     * <p>
     * 直立段从地面到折断点（约高度一半处），横躺段从柱脚向倾斜方向水平散落在地面。
     */
    private boolean placeBroken(WorldGenLevel level, RandomSource random, DecorationConfig config,
                                 int centerX, int centerZ, int groundY, int topY,
                                 int radius, int tiltDir, int halfWidth,
                                 Set<BlockPos> placedPositions) {
        int height = topY - groundY;
        int breakPoint = groundY + (int) (height * (0.4 + random.nextDouble() * 0.15));
        boolean placedAny = false;

        for (int y = groundY; y <= breakPoint; y++) {
            int localBreakPoint = breakPoint + (int)(random.nextGaussian() * 1.5);
            if (localBreakPoint < groundY + 1) localBreakPoint = groundY + 1;
            float progress = (float) (y - groundY) / (float) height;
            int currentRadius = Math.max(1, radius - (int) (progress * (radius - 1)));

            if (y >= localBreakPoint - 1) {
                currentRadius = Math.max(1, currentRadius - 1);
            }

            for (int dx = -currentRadius; dx <= currentRadius; dx++) {
                for (int dz = -currentRadius; dz <= currentRadius; dz++) {
                    float distSq = dx * dx + dz * dz;
                    if (distSq > (currentRadius + 0.5f) * (currentRadius + 0.5f)) {
                        continue;
                    }

                    BlockPos pos = new BlockPos(centerX + dx, y, centerZ + dz);
                    if (!WorldGenUtils.isReplaceable(level, config.replaceable(), pos)) {
                        continue;
                    }

                    if (y > groundY) {
                        if (!hasSupport(level, pos, placedPositions)) {
                            continue;
                        }
                    }

                    boolean nearBreakTop = y >= breakPoint - 1;
                    BlockState state = nearBreakTop
                        ? Blocks.SNOW_BLOCK.defaultBlockState()
                        : config.bodyBlock().getState(random, pos);
                    level.setBlock(pos, state, 3);
                    placedPositions.add(pos);
                    placedAny = true;
                }
            }
        }

        int fallenLength = (int) (height * 0.5);
        for (int layer = 0; layer < fallenLength; layer++) {
            int offsetX = (layer + 1) * tiltDir;
            int currentRadius = Math.max(1, radius - layer / 4);

            int placeY = groundY;
            while (placeY > level.getMinBuildHeight()
                    && !WorldGenUtils.isSolidSurface(level, new BlockPos(centerX + offsetX, placeY - 1, centerZ))) {
                placeY--;
            }
            if (placeY <= level.getMinBuildHeight()) continue;

            for (int dx = -currentRadius; dx <= currentRadius; dx++) {
                for (int dz = -currentRadius; dz <= currentRadius; dz++) {
                    float distSq = dx * dx + dz * dz;
                    if (distSq > (currentRadius + 0.5f) * (currentRadius + 0.5f)) {
                        continue;
                    }

                    BlockPos pos = new BlockPos(centerX + offsetX + dx, placeY, centerZ + dz);

                    if (random.nextFloat() < 0.85f) {
                        BlockState state = config.bodyBlock().getState(random, pos);
                        level.setBlock(pos, state, 3);
                        placedPositions.add(pos);
                        placedAny = true;
                    }
                }
            }

            if (height >= 25 && layer % 3 == 0 && random.nextFloat() < 0.3f) {
                int scatterX = centerX + offsetX + random.nextInt(3) - 1;
                int scatterZ = centerZ + random.nextInt(3) - 1;
                BlockPos scatterPos = new BlockPos(scatterX, placeY, scatterZ);
                if (random.nextBoolean()) {
                    level.setBlock(scatterPos, Blocks.ICE.defaultBlockState(), 3);
                } else {
                    level.setBlock(scatterPos, Blocks.PACKED_ICE.defaultBlockState(), 3);
                }
            }
        }

        addGlowDecorations(level, random, config, centerX, centerZ, groundY, halfWidth);

        return placedAny;
    }

    /**
     * 横梁坠落碎片：从柱顶高度向对面倾斜砸落地面，在落地处散落冰带
     * <p>
     * 横梁主体从柱顶高度（groundY + beamDrop）逐渐向下倾斜到地面，
     * 最远端的尖端磕碎成雪块和冰块向四周溅射。
     * 不检查可替换，总是向下找固体地面放置。
     */
    private boolean placeFallenBeam(WorldGenLevel level, RandomSource random, DecorationConfig config,
                                     int centerX, int centerZ, int groundY,
                                     int tiltDir, int halfWidth, int height) {
        boolean placedAny = false;
        int beamReach = halfWidth + random.nextIntBetweenInclusive(2, 4);
        int beamWidth = 2;
        float beamDrop = Math.min(height * 0.2f, 6);
        int intactCount = 0;
        int scatterCount = 0;

        for (int dist = 0; dist <= beamReach; dist++) {
            float progress = (float) dist / beamReach;
            int x = centerX + Math.round(dist * tiltDir);

            float angleSlope = 1 - progress * 0.7f;
            int currentWidth = Math.max(1, Math.round(beamWidth * angleSlope));

            for (int dw = -currentWidth; dw <= currentWidth; dw++) {
                double wobble = Math.sin(dist * 0.8 + random.nextDouble() * 0.3) * 0.6;
                int z = centerZ + (int) Math.round(dw + wobble);

                int idealY = groundY + Math.round(beamDrop * (1 - progress));
                while (idealY > level.getMinBuildHeight()
                        && !WorldGenUtils.isSolidSurface(level, new BlockPos(x, idealY - 1, z))) {
                    idealY--;
                }
                if (idealY <= level.getMinBuildHeight()) continue;

                BlockPos pos = new BlockPos(x, idealY, z);

                if (progress < 0.45f) {
                    BlockState state = config.bodyBlock().getState(random, pos);
                    level.setBlock(pos, state, 3);
                    intactCount++;
                    placedAny = true;
                } else if (progress < 0.7f) {
                    float placeChance = 0.7f - (progress - 0.45f) / 0.25f * 0.4f;
                    if (random.nextFloat() < placeChance) {
                        BlockState state = config.bodyBlock().getState(random, pos);
                        level.setBlock(pos, state, 3);
                        intactCount++;
                        placedAny = true;
                    }
                }
            }
        }

        int scatterRange = (beamReach + 3) * 2;
        for (int i = 0; i < scatterRange * 6; i++) {
            int sx = centerX + random.nextInt(scatterRange * 2 + 1) - scatterRange;
            int sz = centerZ + random.nextInt(scatterRange * 2 + 1) - scatterRange;
            int dx = sx - centerX;
            int dz = sz - centerZ;
            float distToCenter = (float) Math.sqrt(dx * dx + dz * dz);

            float placeChance = Math.max(0.03f, 0.6f - distToCenter / (scatterRange * 1.2f));
            if (random.nextFloat() >= placeChance) continue;

            int sy = groundY;
            while (sy > level.getMinBuildHeight()
                    && !WorldGenUtils.isSolidSurface(level, new BlockPos(sx, sy - 1, sz))) {
                sy--;
            }
            if (sy <= level.getMinBuildHeight()) continue;

            BlockPos sPos = new BlockPos(sx, sy, sz);
            BlockState scatterState;
            if (random.nextFloat() < 0.7f) {
                scatterState = Blocks.SNOW_BLOCK.defaultBlockState();
            } else if (random.nextFloat() < 0.5f) {
                scatterState = Blocks.ICE.defaultBlockState();
            } else {
                scatterState = Blocks.PACKED_ICE.defaultBlockState();
            }
            level.setBlock(sPos, scatterState, 3);
            scatterCount++;
            placedAny = true;
        }

        addGlowDecorations(level, random, config, centerX, centerZ, groundY, halfWidth);

        return placedAny;
    }

    /**
     * 插地版碎冰带：横梁垂直于柱子方向，顶端插入地面，尖端砸碎散落
     * <p>
     * 横梁沿 Z 轴方向延伸（垂直于柱子的倾斜方向 X 轴），
     * 顶部（近柱端）被直接插进地面——埋入地下，地面只有少量露出，
     * 中间段完整平躺地面，尖端被砸开以冰块/雪块向四周溅射。
     */
    private boolean placeStakedBeam(WorldGenLevel level, RandomSource random, DecorationConfig config,
                                     int centerX, int centerZ, int groundY,
                                     int tiltDir, int halfWidth, int height) {
        boolean placedAny = false;
        int beamReach = halfWidth + random.nextIntBetweenInclusive(1, 3);
        int beamWidth = 2;
        float beamDrop = Math.min(height * 0.15f, 4);
        int intactCount = 0;
        int scatterCount = 0;

        for (int dist = 0; dist <= beamReach; dist++) {
            float progress = (float) dist / beamReach;
            int z = centerZ + dist;

            int currentWidth = Math.max(1, beamWidth - (int) (progress * (beamWidth - 1)));

            for (int dx = -currentWidth; dx <= currentWidth; dx++) {
                double wobble = Math.sin(dist * 0.6 + random.nextDouble() * 0.3) * 0.4;
                int x = centerX + (int) Math.round(dx + wobble);

                int idealY = groundY + Math.round(beamDrop * (1 - progress) * 0.5f);
                while (idealY > level.getMinBuildHeight()
                        && !WorldGenUtils.isSolidSurface(level, new BlockPos(x, idealY - 1, z))) {
                    idealY--;
                }
                if (idealY <= level.getMinBuildHeight()) continue;

                BlockPos pos = new BlockPos(x, idealY, z);

                if (progress < 0.25f) {
                    if (random.nextFloat() < 0.4f) {
                        BlockState state = config.bodyBlock().getState(random, pos);
                        level.setBlock(pos, state, 3);
                        intactCount++;
                        placedAny = true;
                    }
                } else if (progress < 0.6f) {
                    float placeChance = 0.85f - (progress - 0.25f) / 0.35f * 0.15f;
                    if (random.nextFloat() < placeChance) {
                        BlockState state = config.bodyBlock().getState(random, pos);
                        level.setBlock(pos, state, 3);
                        intactCount++;
                        placedAny = true;
                    }
                } else if (progress < 0.8f) {
                    float placeChance = 0.5f - (progress - 0.6f) / 0.2f * 0.3f;
                    if (random.nextFloat() < placeChance) {
                        BlockState state = config.bodyBlock().getState(random, pos);
                        level.setBlock(pos, state, 3);
                        intactCount++;
                        placedAny = true;
                    }
                }
            }
        }

        int scatterRange = (beamReach + 2) * 2;
        for (int i = 0; i < scatterRange * 3; i++) {
            int sx = centerX + random.nextInt(scatterRange + 1) - scatterRange / 2;
            int sz = centerZ + random.nextInt(scatterRange * 2 + 1) - scatterRange;
            int dx = sx - centerX;
            int dz = sz - centerZ;
            float distToCenter = (float) Math.sqrt(dx * dx + dz * dz);

            float placeChance = Math.max(0.03f, 0.4f - distToCenter / (scatterRange * 1.5f));
            if (random.nextFloat() >= placeChance) continue;

            int sy = groundY;
            while (sy > level.getMinBuildHeight()
                    && !WorldGenUtils.isSolidSurface(level, new BlockPos(sx, sy - 1, sz))) {
                sy--;
            }
            if (sy <= level.getMinBuildHeight()) continue;

            BlockPos sPos = new BlockPos(sx, sy, sz);
            BlockState scatterState;
            if (random.nextFloat() < 0.7f) {
                scatterState = Blocks.SNOW_BLOCK.defaultBlockState();
            } else if (random.nextFloat() < 0.6f) {
                scatterState = Blocks.ICE.defaultBlockState();
            } else {
                scatterState = Blocks.PACKED_ICE.defaultBlockState();
            }
            level.setBlock(sPos, scatterState, 3);
            scatterCount++;
            placedAny = true;
        }


        addGlowDecorations(level, random, config, centerX, centerZ, groundY, halfWidth);

        return placedAny;
    }

    /**
     * 在冰门结构外围点缀发光冰芽
     * <p>
     * 在结构底部周围随机放置 ICE_BUD_0 发光方块作为装饰
     */
    private void addGlowDecorations(WorldGenLevel level, RandomSource random, DecorationConfig config, int centerX, int centerZ, int groundY, int halfWidth) {
        int glowCount = 1 + random.nextInt(3);
        for (int g = 0; g < glowCount; g++) {
            int gx = centerX + random.nextInt(halfWidth * 2 + 3) - halfWidth - 1;
            int gz = centerZ + random.nextInt(5) - 2;
            int gy = groundY;
            while (gy > level.getMinBuildHeight()
                    && !WorldGenUtils.isSolidSurface(level, new BlockPos(gx, gy - 1, gz))) {
                gy--;
            }
            if (gy <= level.getMinBuildHeight()) continue;
            BlockPos gPos = new BlockPos(gx, gy + 1, gz);
            if (!WorldGenUtils.isReplaceable(level, config.replaceable(), gPos)) continue;
            if (!WorldGenUtils.isWithinGenerationBounds(new BlockPos(centerX, groundY, centerZ), gPos)) continue;
            level.setBlock(gPos, com.pasterdream.pasterdreammod.registry.PDBlocks.ICE_BUD_0.get().defaultBlockState(), 3);
        }
    }

    /**
     * 检查指定位置下方是否有支撑
     */
    private boolean hasSupport(WorldGenLevel level, BlockPos pos, Set<BlockPos> placedPositions) {
        BlockPos below = pos.below();
        if (placedPositions.contains(below)) {
            return true;
        }
        return WorldGenUtils.isSolidSurface(level, below);
    }
}