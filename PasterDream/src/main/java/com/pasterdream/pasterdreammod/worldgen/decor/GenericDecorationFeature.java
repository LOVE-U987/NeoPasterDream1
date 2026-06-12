package com.pasterdream.pasterdreammod.worldgen.decor;

import com.pasterdream.pasterdreammod.worldgen.WorldGenUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

import java.util.*;

/**
 * 统一装饰物特征 —— 根据 {@link DecorationType} 通过策略模式分派到不同生成器
 * <p>
 * 使用 {@link DecorationPlacer} 策略接口实现类型分发，
 * 新增装饰物类型时无需修改 {@code place()} 方法，只需注册新的映射。
 * 比传统的 switch 语句更符合开放-封闭原则。
 */
public class GenericDecorationFeature extends Feature<DecorationConfig> {

    /**
     * 装饰物类型 → 放置策略的映射表
     * <p>
     * 每种 {@link DecorationType} 对应一个 {@link DecorationPlacer} 实现，
     * 通过构造函数初始化。新增类型时在此添加新映射即可。
     */
    private final Map<DecorationType, DecorationPlacer> placers = new HashMap<>();

    public GenericDecorationFeature() {
        super(DecorationConfig.CODEC.codec());
        // 初始化策略映射 —— 每种装饰物类型绑定到对应的放置方法
        placers.put(DecorationType.PILLAR, this::placePillar);
        placers.put(DecorationType.BLOB, this::placeBlob);
        placers.put(DecorationType.SPIKE, this::placeSpike);
        placers.put(DecorationType.GATE, this::placeGate);
        placers.put(DecorationType.SCATTER, this::placeScatter);
        placers.put(DecorationType.AQUATIC, this::placeAquatic);
        placers.put(DecorationType.CUSTOM, this::placeCustom);
    }

    /**
     * 入口方法 —— 通过策略模式分发到对应类型的放置器
     *
     * @param context 特征放置上下文
     * @return 是否放置了至少一个方块
     */
    @Override
    public boolean place(FeaturePlaceContext<DecorationConfig> context) {
        DecorationPlacer placer = placers.get(context.config().type());
        if (placer == null) {
            return false;
        }
        return placer.place(context);
    }

    // ======================== PILLAR (柱形) ========================

    /**
     * 柱形生成：从地下延伸到地上的锥形柱体，底部粗顶部细
     * <ul>
     *   <li>方形截面，宽度从 baseWidth 渐变为 topWidth</li>
     *   <li>地下部分宽度偏大（undergroundBias）</li>
     *   <li>地上部分支持悬空检测（checkHang）和悬空填充（fillHang）</li>
     *   <li>表面边缘方块以 crystalChance 概率替换为 oreBlock</li>
     *   <li>柱体周围散落 debrisCount 个碎片</li>
     * </ul>
     *
     * @param context 特征放置上下文
     * @return 是否放置了至少一个方块
     */
    private boolean placePillar(FeaturePlaceContext<DecorationConfig> context) {
        DecorationConfig config = context.config();
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource random = context.random();

        int groundY = WorldGenUtils.findGroundY(level, config.replaceable(),
            origin.getX(), origin.getY(), origin.getZ(), 10);
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

        if (config.regionCheck()
            && WorldGenUtils.isAreaOccupied(level, config.replaceable(),
            origin.getX(), origin.getZ(), bottomY, topY, (Math.max(effectiveBaseWidth, config.topWidth()) + 1) / 2, config.regionThreshold())) {
            return false;
        }

        Set<BlockPos> placedPositions = new HashSet<>();
        boolean placedAny = false;

        for (int y = bottomY; y <= topY; y++) {
            float progress = (float) (y - bottomY) / (float) totalHeight;
            int currentWidth = calcPillarWidth(effectiveBaseWidth, config.topWidth(), progress, y, groundY);
            int halfSize = currentWidth / 2;

            for (int dx = -halfSize; dx < currentWidth - halfSize; dx++) {
                for (int dz = -halfSize; dz < currentWidth - halfSize; dz++) {
                    BlockPos placePos = new BlockPos(origin.getX() + dx, y, origin.getZ() + dz);

                    if (!WorldGenUtils.isReplaceable(level, config.replaceable(), placePos)) {
                        continue;
                    }

                    if (y >= groundY) {
                        boolean supported = hasSupport(level, config.replaceable(), placePos, placedPositions);
                        if (!supported) {
                            if (config.fillHang()) {
                                fillHangingColumn(level, config, random, placePos, placedPositions);
                                continue;
                            }
                            if (config.checkHang()) {
                                continue;
                            }
                        }
                    }

                    boolean isSurfaceBlock = (dx == -halfSize || dx == currentWidth - halfSize - 1
                        || dz == -halfSize || dz == currentWidth - halfSize - 1);

                    BlockState state;
                    boolean canPlaceCrystal = config.oreBlock() != null && random.nextFloat() < config.crystalChance();
                    if (config.crystalOnlyOnTop()) {
                        boolean hasBlockAbove = false;
                        if (y < topY) {
                            float nextProgress = (float) (y + 1 - bottomY) / (float) totalHeight;
                            int nextWidth = calcPillarWidth(effectiveBaseWidth, config.topWidth(), nextProgress, y + 1, groundY);
                            int nextHalfSize = nextWidth / 2;
                            hasBlockAbove = dx >= -nextHalfSize && dx < nextWidth - nextHalfSize
                                && dz >= -nextHalfSize && dz < nextWidth - nextHalfSize;
                        }
                        if (hasBlockAbove) {
                            canPlaceCrystal = false;
                        }
                    }
                    if (y >= groundY && isSurfaceBlock && canPlaceCrystal) {
                        state = config.oreBlock().getState(random, placePos);
                    } else {
                        state = config.bodyBlock().getState(random, placePos);
                    }

                    level.setBlock(placePos, state, 3);
                    placedPositions.add(placePos);
                    placedAny = true;
                }
            }
        }

        if (config.debrisCount() > 0 && config.debrisBlock() != null) {
            placedAny |= scatterDebris(level, random, config, origin.getX(), origin.getZ(), groundY);
        }

        return placedAny;
    }

    /**
     * 计算柱形在当前 Y 层的宽度（方块数），实现锥形渐变
     *
     * @param baseWidth  底部宽度
     * @param topWidth   顶部宽度
     * @param progress   当前进度（0~1）
     * @param y          当前 Y 坐标
     * @param groundY    地面 Y 坐标
     * @return 当前层的宽度
     */
    private int calcPillarWidth(int baseWidth, int topWidth, float progress, int y, int groundY) {
        float currentWidth = baseWidth + (float) (topWidth - baseWidth) * progress;
        if (y < groundY) {
            float undergroundBias = 1.0f + (float) (groundY - y) * 0.3f;
            currentWidth = Math.min(baseWidth + 0.5f, currentWidth * undergroundBias);
        }
        return Math.max(1, Math.round(currentWidth));
    }

    // ======================== BLOB (团块) ========================

    /**
     * 团块生成：使用随机游走算法生成不规则椭球状团块
     * <ul>
     *   <li>从原点开始向四周随机扩散</li>
     *   <li>椭球边界检测 (dist + noise <= 1.0)</li>
     *   <li>支持悬空填充（fillHang）和悬空检测（checkHang）</li>
     * </ul>
     *
     * @param context 特征放置上下文
     * @return 是否放置了至少一个方块
     */
    private boolean placeBlob(FeaturePlaceContext<DecorationConfig> context) {
        DecorationConfig config = context.config();
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource random = context.random();

        int groundY = WorldGenUtils.findGroundY(level, config.replaceable(),
            origin.getX(), origin.getY(), origin.getZ(), 10);
        if (groundY == Integer.MIN_VALUE) {
            return false;
        }

        BlockPos adjustedOrigin = new BlockPos(origin.getX(), groundY, origin.getZ());

        List<BlockPos> placedList = new ArrayList<>();
        Set<BlockPos> placedSet = new HashSet<>();
        placedList.add(adjustedOrigin);
        placedSet.add(adjustedOrigin);

        Direction[] directions = {
            Direction.UP, Direction.UP, Direction.UP,
            Direction.DOWN, Direction.DOWN,
            Direction.NORTH, Direction.NORTH,
            Direction.SOUTH, Direction.SOUTH,
            Direction.EAST, Direction.EAST,
            Direction.WEST, Direction.WEST
        };

        int clusterSize = config.clusterSize();
        int xRadius = Math.max(1, config.baseRadius());
        int yRadius = Math.max(1, config.yRadius());
        float irregularity = config.irregularity();
        BlockPredicate replaceable = config.replaceable();

        for (int i = 0; i < clusterSize; i++) {
            BlockPos current = placedList.get(random.nextInt(placedList.size()));
            Direction dir = directions[random.nextInt(directions.length)];
            BlockPos candidate = current.relative(dir);

            if (placedSet.contains(candidate)) {
                continue;
            }

            double dx = (candidate.getX() - adjustedOrigin.getX()) / (double) xRadius;
            double dy = (candidate.getY() - adjustedOrigin.getY()) / (double) yRadius;
            double dz = (candidate.getZ() - adjustedOrigin.getZ()) / (double) xRadius;

            double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
            double noise = (random.nextDouble() - 0.5) * irregularity;

            if (dist + noise <= 1.0 && WorldGenUtils.isReplaceable(level, config.replaceable(), candidate)) {
                placedList.add(candidate);
                placedSet.add(candidate);
            }
        }

        if (placedSet.isEmpty()) {
            return false;
        }

        Set<BlockPos> finalPositions;
        if (config.fillHang()) {
            finalPositions = stabilizeBlobPositions(level, config, placedSet);
        } else if (config.checkHang()) {
            finalPositions = filterHangingBlobPositions(level, config, placedSet);
        } else {
            finalPositions = placedSet;
        }

        if (finalPositions.isEmpty()) {
            return false;
        }

        for (BlockPos pos : finalPositions) {
            if (WorldGenUtils.isReplaceable(level, config.replaceable(), pos)) {
                BlockState state = config.bodyBlock().getState(random, pos);
                level.setBlock(pos, state, 3);
            }
        }

        return true;
    }

    /**
     * 稳定化处理团块方块：按 Y 排序，逐个检查支撑，无支撑则下移并填充路径
     *
     * @param level     世界生成级别访问
     * @param config    装饰物配置
     * @param positions 原始放置位置集合
     * @return 稳定化处理后的位置集合
     */
    private Set<BlockPos> stabilizeBlobPositions(WorldGenLevel level, DecorationConfig config,
                                                  Set<BlockPos> positions) {
        List<BlockPos> sortedPositions = new ArrayList<>(positions);
        sortedPositions.sort(Comparator.comparingInt(BlockPos::getY));

        Set<BlockPos> finalPositions = new HashSet<>();
        for (BlockPos pos : sortedPositions) {
            BlockPos stabilized = pos;
            while (stabilized.getY() > level.getMinBuildHeight()) {
                BlockPos below = stabilized.below();
                boolean hasGroundSupport = WorldGenUtils.isSolidSurface(level, below);
                boolean hasStructureSupport = finalPositions.contains(below);
                if (hasGroundSupport || hasStructureSupport) {
                    break;
                }
                stabilized = below;
            }
            for (int y = stabilized.getY(); y <= pos.getY(); y++) {
                BlockPos fillPos = new BlockPos(pos.getX(), y, pos.getZ());
                if (WorldGenUtils.isReplaceable(level, config.replaceable(), fillPos)) {
                    finalPositions.add(fillPos);
                }
            }
        }
        return finalPositions;
    }

    /**
     * 过滤掉悬空的团块方块（不做填充，仅跳过无支撑方块）
     *
     * @param level     世界生成级别访问
     * @param config    装饰物配置
     * @param positions 原始放置位置集合
     * @return 过滤后保留的位置集合
     */
    private Set<BlockPos> filterHangingBlobPositions(WorldGenLevel level, DecorationConfig config,
                                                      Set<BlockPos> positions) {
        Set<BlockPos> filtered = new HashSet<>();
        for (BlockPos pos : positions) {
            if (hasSupport(level, config.replaceable(), pos, filtered)) {
                filtered.add(pos);
            }
        }
        return filtered;
    }

    // ======================== SPIKE (尖刺) ========================

    /**
     * 尖刺生成：底部粗尖端细的锥形尖刺，支持水下生成和随机倾斜
     * <ul>
     *   <li>圆形截面，半径从 baseRadius 线性递减到 topRadius</li>
     *   <li>支持区域占用检测（regionCheck）</li>
     *   <li>地下部分宽度偏大</li>
     *   <li>以 embedChance 概率嵌入 oreBlock</li>
     *   <li>顶部放置 topBlock</li>
     *   <li>支持倾斜（tiltIntensity > 0 时每层随机偏移）</li>
     *   <li>支持水下生成（waterRequired=true 时替换水方块）</li>
     * </ul>
     *
     * @param context 特征放置上下文
     * @return 是否放置了至少一个方块
     */
    private boolean placeSpike(FeaturePlaceContext<DecorationConfig> context) {
        DecorationConfig config = context.config();
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource random = context.random();

        if (config.waterRequired() && !isInWater(level, origin)) {
            return false;
        }

        int groundY = WorldGenUtils.findGroundY(level, config.replaceable(),
            origin.getX(), origin.getY(), origin.getZ(), 8);
        if (groundY == Integer.MIN_VALUE) {
            return false;
        }

        int totalHeight = random.nextIntBetweenInclusive(config.minHeight(), config.maxHeight());
        int undergroundPortion = Math.max(1, totalHeight / 3 + random.nextInt(Math.max(1, totalHeight / 6)));
        int aboveGroundPortion = totalHeight - undergroundPortion;

        int bottomY = groundY - undergroundPortion;
        int topY = groundY + aboveGroundPortion;

        if (config.regionCheck()
            && WorldGenUtils.isAreaOccupied(level, config.replaceable(),
            origin.getX(), origin.getZ(), bottomY, topY, config.baseRadius(), config.regionThreshold())) {
            return false;
        }

        boolean placedAny = false;
        BlockPos topCenterPos = null;

        float accumulatedDx = 0;
        float accumulatedDz = 0;
        float tiltDirX = 0;
        float tiltDirZ = 0;
        if (config.tiltIntensity() > 0) {
            tiltDirX = (random.nextFloat() - 0.5f) * 2;
            tiltDirZ = (random.nextFloat() - 0.5f) * 2;
        }

        for (int y = bottomY; y <= topY; y++) {
            float progress = (float) (y - bottomY) / (float) totalHeight;
            float currentRadius = config.baseRadius() + (config.topRadius() - config.baseRadius()) * progress;

            if (y < groundY) {
                float undergroundBias = 1.0f + (groundY - y) * 0.3f;
                currentRadius = Math.min(config.baseRadius() + 0.5f, currentRadius * undergroundBias);
            }

            int radius = Math.max(0, Math.round(currentRadius));

            if (config.tiltIntensity() > 0) {
                float layerTilt = config.tiltIntensity() * progress;
                accumulatedDx += (random.nextFloat() - 0.5f) * layerTilt * 0.6f + tiltDirX * layerTilt * 0.15f;
                accumulatedDz += (random.nextFloat() - 0.5f) * layerTilt * 0.6f + tiltDirZ * layerTilt * 0.15f;
            }

            int centerX = origin.getX() + Math.round(accumulatedDx);
            int centerZ = origin.getZ() + Math.round(accumulatedDz);

            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    float distSq = dx * dx + dz * dz;
                    if (distSq > (radius + 0.5f) * (radius + 0.5f)) {
                        continue;
                    }

                    BlockPos placePos = new BlockPos(centerX + dx, y, centerZ + dz);

                    BlockState existingState = level.getBlockState(placePos);
                    boolean isReplaceable = WorldGenUtils.isReplaceable(level, config.replaceable(), placePos);
                    if (config.waterRequired()) {
                        isReplaceable = isReplaceable || existingState.getFluidState().is(FluidTags.WATER);
                    }
                    if (!isReplaceable) {
                        continue;
                    }

                    if (y > groundY) {
                        boolean supported = hasSupport(level, config.replaceable(), placePos, null);
                        if (!supported) {
                            if (config.fillHang()) {
                                fillHangingColumn(level, config, random, placePos, null);
                                continue;
                            }
                            if (config.checkHang()) {
                                continue;
                            }
                        }
                    }

                    BlockState state;
                    boolean canPlaceCrystal = config.oreBlock() != null && random.nextFloat() < config.crystalChance();
                    if (config.crystalOnlyOnTop()) {
                        boolean hasBlockAbove = false;
                        if (y < topY) {
                            float nextProgress = (float) (y + 1 - bottomY) / (float) totalHeight;
                            float nextRadius = config.baseRadius() + (config.topRadius() - config.baseRadius()) * nextProgress;
                            if (y + 1 < groundY) {
                                float undergroundBias = 1.0f + (groundY - (y + 1)) * 0.3f;
                                nextRadius = Math.min(config.baseRadius() + 0.5f, nextRadius * undergroundBias);
                            }
                            int nextR = Math.max(0, Math.round(nextRadius));
                            float nextDistSq = dx * dx + dz * dz;
                            hasBlockAbove = nextDistSq <= (nextR + 0.5f) * (nextR + 0.5f);
                        }
                        if (hasBlockAbove) {
                            canPlaceCrystal = false;
                        }
                    }
                    if (canPlaceCrystal) {
                        state = config.oreBlock().getState(random, placePos);
                    } else {
                        state = config.bodyBlock().getState(random, placePos);
                    }

                    level.setBlock(placePos, state, 3);
                    placedAny = true;

                    if (y == topY && dx == 0 && dz == 0) {
                        topCenterPos = placePos;
                    }
                }
            }
        }

        if (topCenterPos != null && config.topBlock() != null) {
            BlockState snowState = config.topBlock().getState(random, topCenterPos);
            level.setBlock(topCenterPos, snowState, 3);
        }

        return placedAny;
    }

    // ======================== GATE (门框) ========================

    /**
     * 门框生成：左右两柱 + 顶部横梁组成的门框形结构
     * <ul>
     *   <li>每根柱子独立查找地面高度</li>
     *   <li>柱子部分做悬空检测</li>
     *   <li>横梁跨段（两柱之间）直接放置（桥梁概念）</li>
     *   <li>横梁延伸段需要下方支撑</li>
     *   <li>额外装饰方块随机散落</li>
     * </ul>
     *
     * @param context 特征放置上下文
     * @return 是否放置了至少一个方块
     */
    private boolean placeGate(FeaturePlaceContext<DecorationConfig> context) {
        DecorationConfig config = context.config();
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource random = context.random();

        int height = random.nextIntBetweenInclusive(config.minHeight(), config.maxHeight());
        int halfWidth = random.nextIntBetweenInclusive(config.gateMinWidth(), config.gateMaxWidth()) / 2;
        int radius = config.pillarRadius();
        int beamThick = config.beamThickness();

        int leftX = origin.getX() - halfWidth;
        int rightX = origin.getX() + halfWidth;

        int leftGroundY = WorldGenUtils.findGroundY(level, config.replaceable(),
            leftX, origin.getY(), origin.getZ(), 8);
        int rightGroundY = WorldGenUtils.findGroundY(level, config.replaceable(),
            rightX, origin.getY(), origin.getZ(), 8);
        int centerGroundY = WorldGenUtils.findGroundY(level, config.replaceable(),
            origin.getX(), origin.getY(), origin.getZ(), 8);

        if (leftGroundY == Integer.MIN_VALUE || rightGroundY == Integer.MIN_VALUE
            || centerGroundY == Integer.MIN_VALUE) {
            return false;
        }

        int baseY = Math.min(leftGroundY, rightGroundY);
        int topY = baseY + height;
        int beamStartY = topY - beamThick * 3;

        if (config.regionCheck()
            && WorldGenUtils.isAreaOccupied(level, config.replaceable(),
            origin.getX(), origin.getZ(), baseY, topY, halfWidth + radius, config.regionThreshold())) {
            return false;
        }

        Set<BlockPos> placedPositions = new HashSet<>();
        boolean placedAny = false;

        for (int y = baseY; y <= topY; y++) {
            float progress = (float) (y - baseY) / (float) height;
            int currentRadius = (y > topY - beamThick * 2)
                ? radius + 1
                : Math.max(1, radius - (int) (progress * (radius > 1 ? 1 : 0)));

            boolean placedLeft = placeGatePillar(level, random, config, leftX, origin.getZ(), origin,
                y, currentRadius, baseY, topY, placedPositions);
            boolean placedRight = placeGatePillar(level, random, config, rightX, origin.getZ(), origin,
                y, currentRadius, baseY, topY, placedPositions);
            if (placedLeft || placedRight) {
                placedAny = true;
            }

            if (y >= beamStartY && y <= topY) {
                int beamHalf = halfWidth + radius;
                for (int bx = -beamHalf; bx <= beamHalf; bx++) {
                    for (int bz = -beamThick / 2; bz <= beamThick / 2; bz++) {
                        BlockPos beamPos = new BlockPos(origin.getX() + bx, y, origin.getZ() + bz);

                        if (!WorldGenUtils.isReplaceable(level, config.replaceable(), beamPos)) {
                            continue;
                        }

                        boolean isSpan = Math.abs(bx) <= halfWidth;
                        if (!isSpan) {
                            boolean hasSupport = hasSupport(level, config.replaceable(), beamPos, placedPositions);
                            if (!hasSupport) {
                                continue;
                            }
                        }

                        BlockState beamState = isSpan
                            ? (config.topBlock() != null ? config.topBlock() : config.bodyBlock()).getState(random, beamPos)
                            : config.bodyBlock().getState(random, beamPos);
                        if (safeSetBlock(level, origin, beamPos, beamState, 3)) {
                            placedPositions.add(beamPos);
                            placedAny = true;
                        }
                    }
                }
            }
        }

        for (int i = 0; i < height / 4; i++) {
            int dx = random.nextInt(halfWidth + radius + 3) - (halfWidth + radius + 3) / 2;
            int dz = random.nextInt(radius + 3) - (radius + 3) / 2;
            int dy = random.nextInt(height / 3);
            BlockPos decoPos = new BlockPos(origin.getX() + dx, baseY + dy + 1, origin.getZ() + dz);
            if (hasSupport(level, config.replaceable(), decoPos, placedPositions)
                && WorldGenUtils.isReplaceable(level, config.replaceable(), decoPos)
                && random.nextFloat() < config.decorationChance()) {
                BlockState state = config.bodyBlock().getState(random, decoPos);
                if (safeSetBlock(level, origin, decoPos, state, 3)) {
                    placedPositions.add(decoPos);
                    placedAny = true;
                }
            }
        }

        return placedAny;
    }

    /**
     * 放置门框的一根柱子（圆形截面，带悬空检测）
     *
     * @param level          世界生成级别访问
     * @param random         随机数源
     * @param config         装饰物配置
     * @param centerX        柱子中心 X
     * @param centerZ        柱子中心 Z
     * @param y              当前 Y 层
     * @param radius         当前层半径
     * @param baseY          底部 Y
     * @param topY           顶部 Y
     * @param placedPositions 已放置方块集合
     * @return 是否放置了至少一个方块
     */
    private boolean placeGatePillar(WorldGenLevel level, RandomSource random, DecorationConfig config,
                                    int centerX, int centerZ, BlockPos origin, int y, int radius,
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
                    boolean supported = hasSupport(level, config.replaceable(), pos, placedPositions);
                    if (!supported) {
                        continue;
                    }
                }
                float heightProgress = (float) (y - baseY) / (float) (topY - baseY);
                boolean isTopSection = heightProgress > 0.7f;
                BlockState state = (isTopSection && config.topBlock() != null)
                    ? config.topBlock().getState(random, pos)
                    : config.bodyBlock().getState(random, pos);
                if (safeSetBlock(level, origin, pos, state, 3)) {
                    placedPositions.add(pos);
                    placed = true;
                }
            }
        }
        return placed;
    }

    // ======================== SCATTER (散布) ========================

    /**
     * 散布生成：在地表随机散布 bodyBlock
     * <ul>
     *   <li>检查下方是否为固体地面</li>
     *   <li>如果 checkHang=true，悬空跳过</li>
     *   <li>固定尝试 20 次，每次成功概率 50%</li>
     * </ul>
     *
     * @param context 特征放置上下文
     * @return 是否放置了至少一个方块
     */
    private boolean placeScatter(FeaturePlaceContext<DecorationConfig> context) {
        DecorationConfig config = context.config();
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource random = context.random();

        int groundY = WorldGenUtils.findGroundY(level, config.replaceable(),
            origin.getX(), origin.getY(), origin.getZ(), 8);
        if (groundY == Integer.MIN_VALUE) {
            return false;
        }

        boolean placedAny = false;
        int count = config.clusterSize();
        int maxAttempts = 20;

        for (int i = 0; i < maxAttempts; i++) {
            if (random.nextFloat() >= 0.5f) {
                continue;
            }

            int dx = random.nextInt(9) - 4;
            int dz = random.nextInt(9) - 4;
            int scatterX = origin.getX() + dx;
            int scatterZ = origin.getZ() + dz;

            int scatterGroundY = WorldGenUtils.findGroundY(level, config.replaceable(),
                scatterX, groundY + 4, scatterZ, 8);
            if (scatterGroundY == Integer.MIN_VALUE) {
                continue;
            }

            BlockPos placePos = new BlockPos(scatterX, scatterGroundY, scatterZ);

            if (!WorldGenUtils.isReplaceable(level, config.replaceable(), placePos)) {
                continue;
            }

            if (config.checkHang()) {
                boolean supported = hasSupport(level, config.replaceable(), placePos, null);
                if (!supported) {
                    continue;
                }
            }

            BlockState state = config.bodyBlock().getState(random, placePos);
            level.setBlock(placePos, state, 3);
            placedAny = true;

            count--;
            if (count <= 0) {
                break;
            }
        }

        return placedAny;
    }

    // ======================== AQUATIC (水下) ========================

    /**
     * 水下生成：与 PILLAR 类似，但必须在水环境中
     * <ul>
     *   <li>如果 waterRequired=true，检查位置是否在水中</li>
     *   <li>替换逻辑可替换水方块</li>
     * </ul>
     *
     * @param context 特征放置上下文
     * @return 是否放置了至少一个方块
     */
    private boolean placeAquatic(FeaturePlaceContext<DecorationConfig> context) {
        DecorationConfig config = context.config();
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource random = context.random();

        if (config.waterRequired() && !isInWater(level, origin)) {
            return false;
        }

        int groundY = WorldGenUtils.findGroundY(level, config.replaceable(),
            origin.getX(), origin.getY(), origin.getZ(), 10);
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

        if (config.regionCheck()
            && WorldGenUtils.isAreaOccupied(level, config.replaceable(),
            origin.getX(), origin.getZ(), bottomY, topY, (Math.max(effectiveBaseWidth, config.topWidth()) + 1) / 2, config.regionThreshold())) {
            return false;
        }

        Set<BlockPos> placedPositions = new HashSet<>();
        boolean placedAny = false;

        for (int y = bottomY; y <= topY; y++) {
            float progress = (float) (y - bottomY) / (float) totalHeight;
            int currentWidth = calcPillarWidth(effectiveBaseWidth, config.topWidth(), progress, y, groundY);
            int halfSize = currentWidth / 2;

            for (int dx = -halfSize; dx < currentWidth - halfSize; dx++) {
                for (int dz = -halfSize; dz < currentWidth - halfSize; dz++) {
                    BlockPos placePos = new BlockPos(origin.getX() + dx, y, origin.getZ() + dz);

                    BlockState existingState = level.getBlockState(placePos);
                    boolean isAirOrWater = existingState.isAir()
                        || existingState.getFluidState().is(FluidTags.WATER);

                    if (!isAirOrWater && !WorldGenUtils.isReplaceable(level, config.replaceable(), placePos)) {
                        continue;
                    }

                    if (y >= groundY) {
                        boolean supported = hasSupport(level, config.replaceable(), placePos, placedPositions);
                        if (!supported) {
                            if (config.fillHang()) {
                                fillHangingColumnAquatic(level, config, random, placePos, placedPositions);
                                continue;
                            }
                            if (config.checkHang()) {
                                continue;
                            }
                        }
                    }

                    boolean isSurfaceBlock = (dx == -halfSize || dx == currentWidth - halfSize - 1
                        || dz == -halfSize || dz == currentWidth - halfSize - 1);

                    BlockState state;
                    boolean canPlaceCrystal = config.oreBlock() != null && random.nextFloat() < config.crystalChance();
                    if (config.crystalOnlyOnTop()) {
                        boolean hasBlockAbove = false;
                        if (y < topY) {
                            float nextProgress = (float) (y + 1 - bottomY) / (float) totalHeight;
                            int nextWidth = calcPillarWidth(effectiveBaseWidth, config.topWidth(), nextProgress, y + 1, groundY);
                            int nextHalfSize = nextWidth / 2;
                            hasBlockAbove = dx >= -nextHalfSize && dx < nextWidth - nextHalfSize
                                && dz >= -nextHalfSize && dz < nextWidth - nextHalfSize;
                        }
                        if (hasBlockAbove) {
                            canPlaceCrystal = false;
                        }
                    }
                    if (y >= groundY && isSurfaceBlock && canPlaceCrystal) {
                        state = config.oreBlock().getState(random, placePos);
                    } else {
                        state = config.bodyBlock().getState(random, placePos);
                    }

                    level.setBlock(placePos, state, 3);
                    placedPositions.add(placePos);
                    placedAny = true;
                }
            }
        }

        if (config.debrisCount() > 0 && config.debrisBlock() != null) {
            placedAny |= scatterDebris(level, random, config, origin.getX(), origin.getZ(), groundY);
        }

        return placedAny;
    }

    /**
     * 检查指定位置是否在水中
     *
     * @param level 世界生成级别访问
     * @param pos   要检查的位置
     * @return true 表示该位置在水中
     */
    private boolean isInWater(WorldGenLevel level, BlockPos pos) {
        return level.getBlockState(pos).getFluidState().is(FluidTags.WATER);
    }

    /**
     * 水下模式下的悬空填充：从悬空位置向下填充，沿途可替换水
     *
     * @param level          世界生成级别访问
     * @param config         装饰物配置
     * @param random         随机数源
     * @param pos            悬空位置
     * @param placedPositions 已放置方块集合
     */
    private void fillHangingColumnAquatic(WorldGenLevel level, DecorationConfig config, RandomSource random,
                                          BlockPos pos, Set<BlockPos> placedPositions) {
        BlockPos.MutableBlockPos cursor = pos.mutable();
        while (cursor.getY() > level.getMinBuildHeight()) {
            cursor.move(Direction.DOWN);
            BlockState belowState = level.getBlockState(cursor);
            boolean isAirOrWater = belowState.isAir() || belowState.getFluidState().is(FluidTags.WATER);
            if (!isAirOrWater && !WorldGenUtils.isReplaceable(level, config.replaceable(), cursor)) {
                break;
            }
            boolean supported = placedPositions != null && placedPositions.contains(cursor.immutable())
                || WorldGenUtils.isSolidSurface(level, cursor.below());
            if (supported) {
                break;
            }
        }
        while (cursor.getY() < pos.getY()) {
            if (WorldGenUtils.isReplaceable(level, config.replaceable(), cursor)
                || level.getBlockState(cursor).getFluidState().is(FluidTags.WATER)) {
                BlockState state = config.bodyBlock().getState(random, cursor);
                level.setBlock(cursor, state, 3);
                if (placedPositions != null) {
                    placedPositions.add(cursor.immutable());
                }
            }
            cursor.move(Direction.UP);
        }
    }

    // ======================== CUSTOM (自定义) ========================

    /**
     * 自定义生成：从配置中获取 customGeneratorKey，查找已注册的生成器并调度
     * <p>
     * 通过 {@link DecorationRegistry#getCustomGenerator(String)} 查找生成器，
     * 如果未设置 key 或未找到对应生成器则返回 false。
     *
     * @param context 特征放置上下文
     * @return 是否生成成功
     */
    private boolean placeCustom(FeaturePlaceContext<DecorationConfig> context) {
        DecorationConfig config = context.config();
        String key = config.customGeneratorKey();
        if (key == null || key.isEmpty()) {
            return false;
        }
        ICustomDecorationGenerator generator = DecorationRegistry.getCustomGenerator(key);
        if (generator == null) {
            return false;
        }
        return generator.generate(context);
    }

    // ======================== 共享工具方法 ========================

    /**
     * 安全放置方块 —— 检查目标位置是否在区块生成安全范围内，防止 far chunk 错误
     * <p>
     * Minecraft 世界生成时区块生成范围有限，大跨度结构（如冰之门）如果
     * 将方块放置到生成范围之外的区块会触发 "Detected setBlock in a far chunk" 错误。
     * 此方法通过比较目标位置与原点的区块坐标来防止越界放置。
     *
     * @param level  世界生成级别访问
     * @param origin 特征生成原点（FeaturePlaceContext.origin）
     * @param pos    要放置方块的位置
     * @param state  要放置的方块状态
     * @param flags  放置标志
     * @return true 表示方块已安全放置，false 表示越界跳过
     */
    private boolean safeSetBlock(WorldGenLevel level, BlockPos origin, BlockPos pos, BlockState state, int flags) {
        if (!WorldGenUtils.isWithinGenerationBounds(origin, pos)) {
            return false;
        }
        level.setBlock(pos, state, flags);
        return true;
    }

    /**
     * 检查指定位置是否有支撑（下方为固体地面或已放置方块）
     *
     * @param level          世界生成级别访问
     * @param replaceable    可替换方块判定
     * @param pos            要检查的位置
     * @param placedPositions 已放置方块集合（可为 null）
     * @return true 表示下方有支撑
     */
    private boolean hasSupport(WorldGenLevel level, BlockPredicate replaceable,
                               BlockPos pos, Set<BlockPos> placedPositions) {
        BlockPos below = pos.below();
        if (placedPositions != null && placedPositions.contains(below)) {
            return true;
        }
        return WorldGenUtils.isSolidSurface(level, below);
    }

    /**
     * 悬空填充：将悬空方块向下填补到有支撑位置
     *
     * @param level          世界生成级别访问
     * @param config         装饰物配置
     * @param random         随机数源
     * @param pos            悬空位置
     * @param placedPositions 已放置方块集合
     */
    private void fillHangingColumn(WorldGenLevel level, DecorationConfig config, RandomSource random,
                                   BlockPos pos, Set<BlockPos> placedPositions) {
        BlockPos.MutableBlockPos cursor = pos.mutable();
        while (cursor.getY() > level.getMinBuildHeight()) {
            cursor.move(Direction.DOWN);
            if (!WorldGenUtils.isReplaceable(level, config.replaceable(), cursor)) {
                break;
            }
            boolean supported = (placedPositions != null && placedPositions.contains(cursor.below()))
                || WorldGenUtils.isSolidSurface(level, cursor.below());
            if (supported) {
                break;
            }
        }
        while (cursor.getY() < pos.getY()) {
            if (WorldGenUtils.isReplaceable(level, config.replaceable(), cursor)) {
                BlockState state = config.bodyBlock().getState(random, cursor);
                level.setBlock(cursor, state, 3);
                if (placedPositions != null) {
                    placedPositions.add(cursor.immutable());
                }
            }
            cursor.move(Direction.UP);
        }
    }

    /**
     * 在指定位置周围散落碎片方块
     *
     * @param level    世界生成级别访问
     * @param random   随机数源
     * @param config   装饰物配置
     * @param centerX  中心 X
     * @param centerZ  中心 Z
     * @param groundY  地面 Y
     * @return 是否放置了至少一个碎片
     */
    private boolean scatterDebris(WorldGenLevel level, RandomSource random, DecorationConfig config,
                                  int centerX, int centerZ, int groundY) {
        boolean placedAny = false;
        for (int i = 0; i < config.debrisCount(); i++) {
            int dx = random.nextInt(-config.debrisRadius(), config.debrisRadius() + 1);
            int dz = random.nextInt(-config.debrisRadius(), config.debrisRadius() + 1);
            if (dx == 0 && dz == 0) {
                continue;
            }
            BlockPos debrisPos = new BlockPos(centerX + dx, groundY, centerZ + dz);
            if (WorldGenUtils.isSolidSurface(level, debrisPos.below())
                && level.getBlockState(debrisPos).isAir()) {
                BlockState state = config.debrisBlock().getState(random, debrisPos);
                level.setBlock(debrisPos, state, 3);
                placedAny = true;
            }
        }
        return placedAny;
    }
}