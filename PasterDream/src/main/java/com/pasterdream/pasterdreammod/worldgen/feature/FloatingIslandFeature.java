package com.pasterdream.pasterdreammod.worldgen.feature;

import com.pasterdream.pasterdreammod.registry.PDBlocks;
import com.pasterdream.pasterdreammod.worldgen.WorldGenUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

/**
 * 浮空群岛特征 —— 在染梦世界 Y=160~220 高度区间生成浮空结构
 * <p>
 * 生成算法：
 * <ol>
 *   <li>使用位置哈希判定当前坐标是否应生成浮岛（模拟噪声分布）</li>
 *   <li>计算随机半径和高度，构建椭球体基底</li>
 *   <li>叠加随机游走扰动，使岛屿形状更有机自然</li>
 *   <li>使用配置中的加权随机列表选择方块类型</li>
 *   <li>大型岛屿（半径 > 8）底部生成倒垂水晶簇</li>
 *   <li>小型岛屿之间有概率生成云桥连接</li>
 * </ol>
 * <p>
 * 设计参考：原版浮冰、末地岛屿的生成模式，结合 GenericDecorationFeature 的
 * placeBlob 随机游走算法，但完全独立实现以满足浮岛特殊需求。
 *
 * @author PasterDream Team
 */
public class FloatingIslandFeature extends Feature<FloatingIslandConfiguration> {

    /** 浮岛分布密度 —— 位置哈希值超过此阈值才生成岛屿 */
    private static final double ISLAND_DENSITY = 0.20;

    /** 大型岛屿判定半径阈值 */
    private static final int LARGE_ISLAND_RADIUS = 8;

    /** 水晶簇最大长度 */
    private static final int MAX_CRYSTAL_LENGTH = 5;

    /** 云桥生成概率（0~1） */
    private static final float CLOUD_BRIDGE_CHANCE = 0.70f;

    /** 云桥最大距离 */
    private static final int CLOUD_BRIDGE_MAX_DIST = 15;

    /** 云桥最小距离 */
    private static final int CLOUD_BRIDGE_MIN_DIST = 5;

    /** 顶端装饰花概率 */
    private static final float TOP_FLOWER_CHANCE = 0.08f;

    public FloatingIslandFeature() {
        super(FloatingIslandConfiguration.CODEC.codec());
    }

    @Override
    public boolean place(FeaturePlaceContext<FloatingIslandConfiguration> context) {
        FloatingIslandConfiguration config = context.config();
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource random = context.random();

        // 1. 位置密度检查：使用位置哈希模拟噪声分布
        if (!shouldPlaceIsland(origin.getX(), origin.getZ(), level.getSeed())) {
            return false;
        }

        // 2. 计算岛屿尺寸
        int radius = random.nextIntBetweenInclusive(config.minRadius(), config.maxRadius());
        int height = random.nextIntBetweenInclusive(config.minHeight(), config.maxHeight());

        // 限制岛屿不超出世界建造高度
        int topY = origin.getY() + height / 2;
        int bottomY = origin.getY() - height / 2;
        if (topY >= level.getMaxBuildHeight() || bottomY <= level.getMinBuildHeight()) {
            return false;
        }

        Set<BlockPos> allPlaced = new HashSet<>();

        // 3. 构建岛屿主体（椭球体 + 随机游走扰动）
        boolean placedBody = placeIslandBody(level, random, config, origin, radius, height, allPlaced);
        if (!placedBody) {
            return false;
        }

        // 3.5 顶部表面铺染梦草方块 + 泥土
        applyTopGrass(level, origin, radius, height, allPlaced);

        // 4. 大型岛屿底部挂水晶簇
        if (config.decorateCrystal() && radius >= LARGE_ISLAND_RADIUS) {
            placeCrystalSpikes(level, random, origin, radius, bottomY, allPlaced);
        }

        // 5. 顶部点缀花朵
        placeTopDecoration(level, random, origin, radius, height, allPlaced);

        // 5.5 底部悬挂藤蔓
        if (config.decorateVines()) {
            placeBottomVines(level, random, origin, radius, bottomY, allPlaced);
        }

        // 6. 尝试生成云桥（连接附近岛屿）
        tryPlaceCloudBridge(level, random, config, origin, radius, height, allPlaced);

        return true;
    }

    // ==================== 岛屿主体生成 ====================

    /**
     * 构建浮岛主体 —— 椭球体 + 随机游走扰动
     * <p>
     * 在椭球体内逐层生成方块，每层叠加随机游走的水平偏移，
     * 使岛屿从完美的椭球变成有机形状。
     *
     * @param level     世界生成级别访问
     * @param random    随机数源
     * @param config    浮岛配置
     * @param origin    生成原点
     * @param radius    岛屿水平半径
     * @param height    岛屿垂直厚度
     * @param allPlaced 已放置方块集合
     * @return 是否放置了至少一个方块
     */
    private boolean placeIslandBody(WorldGenLevel level, RandomSource random,
                                    FloatingIslandConfiguration config, BlockPos origin,
                                    int radius, int height, Set<BlockPos> allPlaced) {
        boolean placed = false;
        int halfHeight = height / 2;
        float radiusSq = radius * radius;
        float heightRatio = height / 2f;

        // 随机游走偏移累计器
        float walkOffsetX = 0;
        float walkOffsetZ = 0;

        for (int dy = -halfHeight; dy <= halfHeight; dy++) {
            float yProgress = (float) dy / halfHeight;
            // 椭球 Y 轴衰减：边缘层水平半径缩小
            float yFactor = (float) Math.sqrt(1.0f - yProgress * yProgress);
            int layerRadius = Math.max(1, Math.round(radius * yFactor));

            // 更新随机游走偏移（每层微调，形成有机波动）
            walkOffsetX += (random.nextFloat() - 0.5f) * 0.8f;
            walkOffsetZ += (random.nextFloat() - 0.5f) * 0.8f;
            // 限制偏移范围，防止过度扭曲
            walkOffsetX = Math.max(-radius * 0.3f, Math.min(radius * 0.3f, walkOffsetX));
            walkOffsetZ = Math.max(-radius * 0.3f, Math.min(radius * 0.3f, walkOffsetZ));

            int centerX = origin.getX() + Math.round(walkOffsetX);
            int centerZ = origin.getZ() + Math.round(walkOffsetZ);

            for (int dx = -layerRadius; dx <= layerRadius; dx++) {
                for (int dz = -layerRadius; dz <= layerRadius; dz++) {
                    // 椭球内部判定：水平椭圆 + 垂直椭球衰减
                    double distHorizontal = Math.sqrt(dx * dx + dz * dz);
                    double distVertical = Math.abs(dy) / (double) heightRatio;
                    double normalizedDist = (distHorizontal * distHorizontal) / (radiusSq)
                            + (distVertical * distVertical);

                    // 密度阈值扰动：使岛屿表面产生不规则凹凸
                    double noisePerturb = (random.nextDouble() - 0.5) * (1.0 - config.densityThreshold());
                    if (normalizedDist + noisePerturb > 1.0) {
                        continue;
                    }

                    // 表面层 20% 概率跳过（制造空洞/凹陷，增加自然感）
                    boolean isSurface = normalizedDist + noisePerturb > 0.75;
                    if (isSurface && random.nextFloat() < 0.08f) {
                        continue;
                    }

                    BlockPos placePos = new BlockPos(centerX + dx, origin.getY() + dy, centerZ + dz);
                    if (!canReplace(level, placePos, allPlaced)) {
                        continue;
                    }

                    // 按位置选择方块：云朵包裹下底盘和侧盘，方解石做骨架内核
                    BlockState state;
                    double normalizedHorizDist = layerRadius > 0
                            ? distHorizontal / layerRadius : 1.0;

                    if (dy < 0) {
                        // 下半部：全部云朵（坚实的云端底座）
                        state = PDBlocks.CLOUD.get().defaultBlockState();
                    } else if (normalizedHorizDist > 0.65) {
                        // 上半部侧边：云朵包裹（外圈 35% 区域）
                        state = PDBlocks.CLOUD.get().defaultBlockState();
                    } else {
                        // 上半部内核：方解石（骨架）
                        state = Blocks.CALCITE.defaultBlockState();
                    }
                    level.setBlock(placePos, state, 3);
                    allPlaced.add(placePos);
                    placed = true;
                }
            }
        }

        return placed;
    }

    // ==================== 底部水晶簇 ====================

    /**
     * 在大型岛屿底部生成倒垂水晶簇
     * <p>
     * 水晶从岛屿底面（最低层）向下延伸，形成钟乳石状结构。
     * 使用融梦水晶灯作为晶体主体，生命水晶块作为尖端装饰。
     *
     * @param level     世界生成级别访问
     * @param random    随机数源
     * @param origin    生成原点
     * @param radius    岛屿水平半径
     * @param bottomY   岛屿底部 Y
     * @param allPlaced 已放置方块集合
     */
    private void placeCrystalSpikes(WorldGenLevel level, RandomSource random,
                                     BlockPos origin, int radius, int bottomY,
                                     Set<BlockPos> allPlaced) {
        int crystalCount = random.nextIntBetweenInclusive(3, 6 + radius / 3);

        for (int i = 0; i < crystalCount; i++) {
            // 在岛屿底面范围内随机选择生成位置
            int angle = random.nextInt(360);
            int dist = random.nextInt(radius - 1);
            int spikeX;
            int spikeZ;

            // 边缘水晶（距离中心较远）
            if (random.nextBoolean()) {
                spikeX = origin.getX() + (int) (Math.cos(Math.toRadians(angle)) * (radius * 0.6 + dist * 0.4));
                spikeZ = origin.getZ() + (int) (Math.sin(Math.toRadians(angle)) * (radius * 0.6 + dist * 0.4));
            } else {
                spikeX = origin.getX() + random.nextInt(radius * 2) - radius;
                spikeZ = origin.getZ() + random.nextInt(radius * 2) - radius;
            }

            // 检查岛屿底部是否有方块（水晶的附着点）
            BlockPos attachPos = new BlockPos(spikeX, bottomY, spikeZ);
            if (!allPlaced.contains(attachPos) || level.getBlockState(attachPos).isAir()) {
                continue;
            }

            // 生成向下延伸的水晶
            int crystalLength = random.nextIntBetweenInclusive(1, MAX_CRYSTAL_LENGTH);
            Block crystalBlock = random.nextFloat() < 0.3f
                    ? PDBlocks.LIFE_CRYSTAL.get()
                    : PDBlocks.MELTDREAM_CRYSTAL_LAMP.get();

            for (int drop = 1; drop <= crystalLength; drop++) {
                BlockPos crystalPos = new BlockPos(spikeX, bottomY - drop, spikeZ);
                if (!level.getBlockState(crystalPos).isAir()) {
                    break;
                }
                if (allPlaced.contains(crystalPos)) {
                    break;
                }

                if (drop == crystalLength && random.nextFloat() < 0.25f) {
                    // 尖端使用生命水晶或融梦水晶灯
                    level.setBlock(crystalPos, PDBlocks.LIFE_CRYSTAL.get().defaultBlockState(), 3);
                } else {
                    level.setBlock(crystalPos, crystalBlock.defaultBlockState(), 3);
                }
                allPlaced.add(crystalPos);
            }

            // 10% 概率在主水晶旁边生成小副水晶
            if (random.nextFloat() < 0.10f) {
                int sideOffset = random.nextBoolean() ? 1 : -1;
                int sideAxis = random.nextBoolean() ? 0 : 1; // 0=X偏移, 1=Z偏移
                BlockPos sidePos;
                if (sideAxis == 0) {
                    sidePos = new BlockPos(spikeX + sideOffset, bottomY - 1, spikeZ);
                } else {
                    sidePos = new BlockPos(spikeX, bottomY - 1, spikeZ + sideOffset);
                }
                if (level.getBlockState(sidePos).isAir() && !allPlaced.contains(sidePos)) {
                    level.setBlock(sidePos, PDBlocks.MELTDREAM_CRYSTAL_LAMP.get().defaultBlockState(), 3);
                    allPlaced.add(sidePos);
                }
            }
        }
    }

    // ==================== 顶部装饰 ====================

    /**
     * 在浮岛顶部铺染梦草方块 + 泥土
     * <p>
     * 扫描岛屿的水平范围，从最高点向下找到每列最顶部的方块，
     * 如果是方解石则替换为染梦草方块，并在其下方铺设一层染梦泥土。
     *
     * @param level     世界生成级别访问
     * @param origin    生成原点
     * @param radius    岛屿水平半径
     * @param height    岛屿垂直厚度
     * @param allPlaced 已放置方块集合
     */
    private void applyTopGrass(WorldGenLevel level, BlockPos origin, int radius,
                                int height, Set<BlockPos> allPlaced) {
        int halfHeight = height / 2;
        BlockState grassBlock = PDBlocks.DYEDREAM_GRASS.get().defaultBlockState();
        BlockState dirtBlock = PDBlocks.DYEDREAM_DIRT.get().defaultBlockState();

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                // 从岛屿最高点向下扫，找到该列最顶部的已放置方块
                boolean foundTop = false;
                for (int dy = halfHeight; dy >= -halfHeight && !foundTop; dy--) {
                    BlockPos checkPos = new BlockPos(origin.getX() + dx, origin.getY() + dy, origin.getZ() + dz);
                    if (!allPlaced.contains(checkPos)) {
                        continue;
                    }
                    BlockState current = level.getBlockState(checkPos);
                    if (current.is(Blocks.CALCITE)) {
                        // 顶部方解石 → 草方块
                        level.setBlock(checkPos, grassBlock, 3);
                        // 下方再铺一层泥土
                        BlockPos belowPos = checkPos.below();
                        if (allPlaced.contains(belowPos)
                                && level.getBlockState(belowPos).is(Blocks.CALCITE)) {
                            level.setBlock(belowPos, dirtBlock, 3);
                        }
                        foundTop = true;
                    } else {
                        // 不是方解石（可能是云朵等），保留原样
                        foundTop = true;
                    }
                }
            }
        }
    }

    /**
     * 在浮岛顶部点缀花朵
     * <p>
     * 在岛屿最高层的草方块上随机种植染梦花品种。
     *
     * @param level     世界生成级别访问
     * @param random    随机数源
     * @param origin    生成原点
     * @param radius    岛屿水平半径
     * @param height    岛屿垂直厚度
     * @param allPlaced 已放置方块集合
     */
    private void placeTopDecoration(WorldGenLevel level, RandomSource random,
                                     BlockPos origin, int radius, int height,
                                     Set<BlockPos> allPlaced) {
        int topLayer = origin.getY() + height / 2 - 1; // 在岛屿最高层下面一层放花

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                float distFromCenter = (float) Math.sqrt(dx * dx + dz * dz);
                if (distFromCenter > radius * 0.8f) {
                    continue; // 太边缘的不种花
                }

                BlockPos decoratePos = new BlockPos(origin.getX() + dx, topLayer, origin.getZ() + dz);
                if (!allPlaced.contains(decoratePos)) {
                    continue;
                }

                // 检查上方是否为空气（确保是表面）
                BlockPos abovePos = decoratePos.above();
                if (!level.getBlockState(abovePos).isAir()) {
                    continue;
                }

                // 只在染梦草方块上种花
                if (random.nextFloat() < TOP_FLOWER_CHANCE
                        && level.getBlockState(decoratePos).is(PDBlocks.DYEDREAM_GRASS.get())) {
                    Block flowerBlock = selectRandomFlower(random);
                    level.setBlock(abovePos, flowerBlock.defaultBlockState(), 3);
                    allPlaced.add(abovePos);
                }
            }
        }
    }

    /**
     * 随机选择一个染梦花品种
     *
     * @param random 随机数源
     * @return 花方块
     */
    private Block selectRandomFlower(RandomSource random) {
        int choice = random.nextInt(5);
        return switch (choice) {
            case 0 -> PDBlocks.FLOWER_1.get();
            case 1 -> PDBlocks.FLOWER_2.get();
            case 2 -> PDBlocks.FLOWER_3.get();
            case 3 -> PDBlocks.FLOWER_5.get();
            default -> PDBlocks.FLOWER_6.get();
        };
    }

    // ==================== 云桥连接 ====================

    /**
     * 尝试在当前岛屿和附近岛屿之间生成云桥（最多尝试 2 个方向）
     * <p>
     * 云桥由云朵方块构成，连接两个浮岛的边缘。
     * 只在短距离（5~15 格）内且概率判定通过后生成。
     * 如果第一次尝试成功（或概率判定通过），还会尝试第二个方向以提高连接密度。
     *
     * @param level     世界生成级别访问
     * @param random    随机数源
     * @param config    浮岛配置
     * @param origin    当前岛屿中心
     * @param radius    当前岛屿半径
     * @param height    当前岛屿高度
     * @param allPlaced 已放置方块集合
     */
    private void tryPlaceCloudBridge(WorldGenLevel level, RandomSource random,
                                      FloatingIslandConfiguration config, BlockPos origin,
                                      int radius, int height, Set<BlockPos> allPlaced) {
        // 尝试 1~2 个方向，提高连接密度
        int attempts = random.nextFloat() < CLOUD_BRIDGE_CHANCE ? 2 : 1;

        for (int attempt = 0; attempt < attempts; attempt++) {
            // 在岛屿周围随机选一个方向尝试连接
            int angle = random.nextInt(360);
            int bridgeDist = random.nextIntBetweenInclusive(CLOUD_BRIDGE_MIN_DIST, CLOUD_BRIDGE_MAX_DIST);
            int targetX = origin.getX() + (int) (Math.cos(Math.toRadians(angle)) * (radius + bridgeDist));
            int targetZ = origin.getZ() + (int) (Math.sin(Math.toRadians(angle)) * (radius + bridgeDist));

            // 目标位置必须在生成范围内
            if (!WorldGenUtils.isWithinExpandedGenerationBounds(origin,
                    new BlockPos(targetX, origin.getY(), targetZ), 1)) {
                continue;
            }

            int bridgeY = origin.getY() - height / 4; // 桥在岛屿中间偏下位置
            CloudBridgeDecorator.buildBridge(level, random, origin.getX(), origin.getZ(),
                    targetX, targetZ, bridgeY, radius, allPlaced);
        }
    }

    // ==================== 底部藤蔓 ====================

    /**
     * 在浮岛底部悬挂藤蔓（vine_0）
     * <p>
     * 在岛屿底部的边缘和中心位置随机悬挂藤蔓，使浮岛看起来更自然。
     * 藤蔓向下延伸 2~5 格，只在空气位置放置。
     *
     * @param level     世界生成级别访问
     * @param random    随机数源
     * @param origin    生成原点
     * @param radius    岛屿水平半径
     * @param bottomY   岛屿底部 Y 坐标
     * @param allPlaced 已放置方块集合
     */
    private void placeBottomVines(WorldGenLevel level, RandomSource random,
                                   BlockPos origin, int radius, int bottomY,
                                   Set<BlockPos> allPlaced) {
        int vineCount = random.nextIntBetweenInclusive(6, 14 + radius / 2);

        for (int i = 0; i < vineCount; i++) {
            // 在岛屿底部范围内随机选位置
            int vineX = origin.getX() + random.nextInt(radius * 2) - radius;
            int vineZ = origin.getZ() + random.nextInt(radius * 2) - radius;
            BlockPos attachPos = new BlockPos(vineX, bottomY, vineZ);

            // 确保附着点有方块
            if (!allPlaced.contains(attachPos) || level.getBlockState(attachPos).isAir()) {
                continue;
            }

            // 检查附着点下面的位置是否是空气
            BlockPos belowPos = attachPos.below();
            if (!level.getBlockState(belowPos).isAir()) {
                continue;
            }

            // 藤蔓长度 3~6 格
            int vineLength = random.nextIntBetweenInclusive(3, 6);
            for (int drop = 1; drop <= vineLength; drop++) {
                BlockPos vinePos = new BlockPos(vineX, bottomY - drop, vineZ);
                if (!level.getBlockState(vinePos).isAir()) {
                    break;
                }
                if (allPlaced.contains(vinePos)) {
                    break;
                }

                level.setBlock(vinePos, PDBlocks.VINE_0.get().defaultBlockState(), 3);
                allPlaced.add(vinePos);
            }
        }
    }

    // ==================== 工具方法 ====================

    /**
     * 使用位置哈希判定当前位置是否应生成浮岛
     * <p>
     * 此方法模拟了 {@link com.pasterdream.pasterdreammod.worldgen.chunkgen.DyedreamNoises#sampleIslandNoise}
     * 的行为，但不需要访问 {@code RandomState}（Feature 层级无法直接获取）。
     * 使用世界种子 + 方块坐标的哈希值产生确定性分布。
     *
     * @param x       方块 X 坐标
     * @param z       方块 Z 坐标
     * @param seed    世界种子
     * @return true 表示该位置应生成浮岛
     */
    private static boolean shouldPlaceIsland(int x, int z, long seed) {
        long hash = seed + x * 341873128712L + z * 132897987541L;
        hash = hash * hash * 0x9E3779B97F4A7C15L + hash * 0xBF58476D1CE4E5B9L;
        double normalized = ((hash >> 32) & 0x7FFFFFFF) / (double) Integer.MAX_VALUE;
        return normalized < ISLAND_DENSITY;
    }

    /**
     * 从配置的加权列表中获取一个随机方块状态
     *
     * @param random 随机数源
     * @param config 浮岛配置（内含加权方块条目列表）
     * @return 随机选中的方块状态（若列表为空则返回默认的染梦石英块）
     */
    private static BlockState getRandomBlockState(RandomSource random,
                                                    FloatingIslandConfiguration config) {
        return config.toWeightedList().getRandomValue(random)
                .orElse(PDBlocks.DYEDREAMQUARTZ_BLOCK.get())
                .defaultBlockState();
    }

    /**
     * 检查位置是否可以被替换
     *
     * @param level     世界生成级别访问
     * @param pos       要检查的位置
     * @param placedSet 已放置方块集合
     * @return true 表示可以替换
     */
    private static boolean canReplace(WorldGenLevel level, BlockPos pos, @Nullable Set<BlockPos> placedSet) {
        if (placedSet != null && placedSet.contains(pos)) {
            return false;
        }
        BlockState state = level.getBlockState(pos);
        return state.isAir() || state.canBeReplaced();
    }
}
