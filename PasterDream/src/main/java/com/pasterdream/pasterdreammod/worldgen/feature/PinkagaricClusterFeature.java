package com.pasterdream.pasterdreammod.worldgen.feature;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.pasterdream.pasterdreammod.worldgen.WorldGenUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

import java.util.HashSet;
import java.util.Set;

/**
 * 粉丁菇巨簇特征 —— 在地面生成丛生的蘑菇群
 * <p>
 * 蘑菇结构（平顶草帽形）：
 * - 菌柄：1x1 pinkagaric_1 柱，2~4 格高
 * - 帽顶：在菌柄顶部 +1 处生成 3x3~4x4 的粉色平面 pinkagaric_0
 * - 伞沿：帽顶四边（北南东西）向下延伸 1 格 pinkagaric_0
 * - 帽底：帽沿之间的角落区域用 pinkagaric_2（菌褶）填充
 * <p>
 * 90% 概率生成 3~6 朵普通蘑菇的集群，
 * 10% 概率生成一朵巨型蘑菇（更大尺寸的帽顶 + 多圈伞沿）。
 * 每朵蘑菇独立检测地面，排除已放置的蘑菇方块，杜绝叠罗汉。
 */
public class PinkagaricClusterFeature extends Feature<PinkagaricClusterFeature.Config> {

    public PinkagaricClusterFeature() {
        super(Config.CODEC.codec());
    }

    @Override
    public boolean place(FeaturePlaceContext<Config> context) {
        Config config = context.config();
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource random = context.random();

        int groundY = WorldGenUtils.findGroundY(level, origin.getX(), origin.getY(), origin.getZ(), 10);
        if (groundY == Integer.MIN_VALUE) {
            return false;
        }

        boolean placedAny;

        if (random.nextFloat() < 0.1f) {
            placedAny = placeGiantMushroom(level, random, config, origin.getX(), origin.getZ(), groundY);
        } else {
            placedAny = placeMushroomCluster(level, random, config, origin.getX(), origin.getZ(), groundY);
        }

        return placedAny;
    }

    /**
     * 在指定中心周围生成一丛普通蘑菇（3~6 朵）
     *
     * @param level   世界生成级别访问
     * @param random  随机数源
     * @param config  特征配置
     * @param centerX 群系中心 X
     * @param centerZ 群系中心 Z
     * @param groundY 地面 Y
     * @return 是否放置了至少一个方块
     */
    private boolean placeMushroomCluster(WorldGenLevel level, RandomSource random, Config config,
                                          int centerX, int centerZ, int groundY) {
        int count = random.nextIntBetweenInclusive(3, 6);
        Set<BlockPos> allPlaced = new HashSet<>();
        boolean placedAny = false;

        for (int i = 0; i < count; i++) {
            int dx = random.nextInt(7) - 3;
            int dz = random.nextInt(7) - 3;
            if (dx == 0 && dz == 0) {
                dx = random.nextInt(3) - 1;
                dz = random.nextInt(3) - 1;
            }
            int mx = centerX + dx;
            int mz = centerZ + dz;

            int mushroomGroundY = findGroundExcludingPlaced(level, mx, groundY + 4, mz, 10, allPlaced);
            if (mushroomGroundY == Integer.MIN_VALUE) {
                continue;
            }

            int stemHeight = random.nextIntBetweenInclusive(2, 4);
            int capWidth = random.nextBoolean() ? 3 : 4;
            boolean placed = placeRegularMushroom(level, random, config, mx, mz, mushroomGroundY, stemHeight, capWidth, allPlaced);
            if (placed) {
                placedAny = true;
            }
        }

        return placedAny;
    }

    /**
     * 在指定位置生成一朵平顶草帽形蘑菇
     * <p>
     * 结构：
     * - 菌柄（1x1 pinkagaric_1）从地面到 stemTop（含顶）
     * - 帽顶（capWidth x capWidth pinkagaric_0）在 stemTop + 1
     * - 伞沿（帽顶四边各向下延伸 1 格 pinkagaric_0）在 stemTop
     * - 10% 概率帽顶中心换成 pinkagaric_3（荧光）
     *
     * @param level      世界生成级别访问
     * @param random     随机数源
     * @param config     特征配置
     * @param mx         蘑菇 X
     * @param mz         蘑菇 Z
     * @param groundY    地面 Y
     * @param stemHeight 菌柄高度
     * @param capWidth   帽顶宽度（3=3x3, 4=4x4）
     * @param allPlaced  全局已放置方块集合
     * @return 是否放置了至少一个方块
     */
    private boolean placeRegularMushroom(WorldGenLevel level, RandomSource random, Config config,
                                          int mx, int mz, int groundY, int stemHeight, int capWidth,
                                          Set<BlockPos> allPlaced) {
        int stemTop = groundY + stemHeight;
        boolean placed = false;

        for (int y = groundY; y <= stemTop; y++) {
            BlockPos stemPos = new BlockPos(mx, y, mz);
            if (canReplace(level, stemPos, allPlaced)) {
                level.setBlock(stemPos, config.stem().defaultBlockState(), 3);
                allPlaced.add(stemPos);
                placed = true;
            }
        }

        int capCenterX = mx;
        int capCenterZ = mz;
        int half = capWidth / 2;

        for (int dx = -half; dx < capWidth - half; dx++) {
            for (int dz = -half; dz < capWidth - half; dz++) {
                BlockPos capPos = new BlockPos(capCenterX + dx, stemTop + 1, capCenterZ + dz);
                if (canReplace(level, capPos, allPlaced)) {
                    Block capBlock;
                    if (dx == 0 && dz == 0 && random.nextFloat() < 0.1f) {
                        capBlock = config.glowCap();
                    } else {
                        capBlock = config.capOuter();
                    }
                    level.setBlock(capPos, capBlock.defaultBlockState(), 3);
                    allPlaced.add(capPos);
                    placed = true;
                }
            }
        }

        for (int dx = -half; dx < capWidth - half; dx++) {
            for (int dz = -half; dz < capWidth - half; dz++) {
                boolean isEdgeX = (dx == -half || dx == capWidth - half - 1);
                boolean isEdgeZ = (dz == -half || dz == capWidth - half - 1);
                if (!isEdgeX && !isEdgeZ) {
                    continue;
                }
                BlockPos brimPos = new BlockPos(capCenterX + dx, stemTop, capCenterZ + dz);
                if (canReplace(level, brimPos, allPlaced)) {
                    level.setBlock(brimPos, config.capOuter().defaultBlockState(), 3);
                    allPlaced.add(brimPos);
                    placed = true;
                }
            }
        }

        return placed;
    }

    /**
     * 在指定位置生成一朵巨型蘑菇（10% 概率触发）
     * <p>
     * 结构：
     * - 菌柄：1x1 pinkagaric_1，4~6 格高
     * - 帽顶：5x5 pinkagaric_0（在 stemTop + 1 处）
     * - 帽沿中层：5x5 边缘 pinkagaric_0（在 stemTop 处）
     * - 帽檐下层：外扩到 7x7 边缘 pinkagaric_0（在 stemTop - 1 处，双层伞沿）
     *
     * @param level   世界生成级别访问
     * @param random  随机数源
     * @param config  特征配置
     * @param mx      蘑菇 X
     * @param mz      蘑菇 Z
     * @param groundY 地面 Y
     * @return 是否放置了至少一个方块
     */
    private boolean placeGiantMushroom(WorldGenLevel level, RandomSource random, Config config,
                                        int mx, int mz, int groundY) {
        Set<BlockPos> allPlaced = new HashSet<>();

        int mushroomGroundY = findGroundExcludingPlaced(level, mx, groundY + 6, mz, 10, allPlaced);
        if (mushroomGroundY == Integer.MIN_VALUE) {
            return false;
        }

        int stemHeight = random.nextIntBetweenInclusive(4, 6);
        int stemTop = mushroomGroundY + stemHeight;
        boolean placed = false;

        for (int y = mushroomGroundY; y <= stemTop; y++) {
            BlockPos stemPos = new BlockPos(mx, y, mz);
            if (canReplace(level, stemPos, allPlaced)) {
                level.setBlock(stemPos, config.stem().defaultBlockState(), 3);
                allPlaced.add(stemPos);
                placed = true;
            }
        }

        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                BlockPos capPos = new BlockPos(mx + dx, stemTop + 1, mz + dz);
                if (canReplace(level, capPos, allPlaced)) {
                    Block capBlock;
                    if (dx == 0 && dz == 0 && random.nextFloat() < 0.15f) {
                        capBlock = config.glowCap();
                    } else {
                        capBlock = config.capOuter();
                    }
                    level.setBlock(capPos, capBlock.defaultBlockState(), 3);
                    allPlaced.add(capPos);
                    placed = true;
                }
            }
        }

        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                boolean isEdgeX = (dx == -2 || dx == 2);
                boolean isEdgeZ = (dz == -2 || dz == 2);
                if (!isEdgeX && !isEdgeZ) {
                    continue;
                }
                BlockPos brimPos = new BlockPos(mx + dx, stemTop, mz + dz);
                if (canReplace(level, brimPos, allPlaced)) {
                    level.setBlock(brimPos, config.capOuter().defaultBlockState(), 3);
                    allPlaced.add(brimPos);
                    placed = true;
                }
            }
        }

        for (int dx = -3; dx <= 3; dx++) {
            for (int dz = -3; dz <= 3; dz++) {
                if (Math.abs(dx) <= 2 && Math.abs(dz) <= 2) {
                    continue;
                }
                boolean isEdgeX = (dx == -3 || dx == 3);
                boolean isEdgeZ = (dz == -3 || dz == 3);
                if (!isEdgeX && !isEdgeZ) {
                    continue;
                }
                BlockPos brimPos = new BlockPos(mx + dx, stemTop - 1, mz + dz);
                if (canReplace(level, brimPos, allPlaced)) {
                    level.setBlock(brimPos, config.capOuter().defaultBlockState(), 3);
                    allPlaced.add(brimPos);
                    placed = true;
                }
            }
        }

        return placed;
    }

    /**
     * 从起始 Y 向下搜索地面，排除已放置方块（防叠罗汉）
     * <p>
     * 和 {@link WorldGenUtils#findGroundY} 一样向下找固体表面，
     * 但额外排除已在 {@code placedSet} 中的位置——蘑菇的帽顶/伞沿
     * 不会被当作"地面"，彻底杜绝蘑菇长在蘑菇上的叠罗汉问题。
     *
     * @param level     世界生成级别访问
     * @param x         搜索位置的 X
     * @param startY    起始 Y
     * @param z         搜索位置的 Z
     * @param maxFall   最大向下搜索距离
     * @param placedSet 已放置方块集合
     * @return 地面层的 Y+1，找不到返回 Integer.MIN_VALUE
     */
    private int findGroundExcludingPlaced(WorldGenLevel level, int x, int startY, int z, int maxFall,
                                           Set<BlockPos> placedSet) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(x, startY, z);
        for (int i = 0; i <= maxFall; i++) {
            pos.setY(startY - i);
            if (placedSet.contains(pos)) {
                continue;
            }
            if (isSolidSurface(level, pos)) {
                return pos.getY() + 1;
            }
        }
        return Integer.MIN_VALUE;
    }

    /**
     * 判断方块是否为固体地面（排除空气、树叶、植被、已放置的蘑菇块）
     *
     * @param level 世界生成级别访问
     * @param pos   要检查的位置
     * @return true 表示该位置是固体地面
     */
    private boolean isSolidSurface(WorldGenLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (state.isAir()) return false;
        if (state.is(BlockTags.LEAVES) || state.is(BlockTags.REPLACEABLE_BY_TREES)) return false;
        return state.isCollisionShapeFullBlock(level, pos);
    }

    /**
     * 检查指定位置的方块是否可被替换（空气、可替换方块或不在已放置集合中）
     *
     * @param level     世界生成级别访问
     * @param pos       要检查的位置
     * @param placedSet 已放置方块集合
     * @return true 表示可以替换
     */
    private boolean canReplace(WorldGenLevel level, BlockPos pos, Set<BlockPos> placedSet) {
        if (placedSet.contains(pos)) {
            return false;
        }
        BlockState state = level.getBlockState(pos);
        return state.isAir() || state.canBeReplaced();
    }

    /**
     * 粉丁菇巨簇配置记录
     * <p>
     * 定义了蘑菇各部件使用的方块类型。
     *
     * @param stem      菌柄方块（pinkagaric_1）
     * @param capOuter  菌伞外层方块（pinkagaric_0）
     * @param gillInner 菌褶内层方块（pinkagaric_2）
     * @param glowCap   荧光菌伞方块（pinkagaric_3）
     */
    public record Config(
            Block stem,
            Block capOuter,
            Block gillInner,
            Block glowCap
    ) implements FeatureConfiguration {

        public static final MapCodec<Config> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        net.minecraft.core.registries.BuiltInRegistries.BLOCK.byNameCodec()
                                .fieldOf("stem_block").forGetter(Config::stem),
                        net.minecraft.core.registries.BuiltInRegistries.BLOCK.byNameCodec()
                                .fieldOf("cap_outer_block").forGetter(Config::capOuter),
                        net.minecraft.core.registries.BuiltInRegistries.BLOCK.byNameCodec()
                                .fieldOf("gill_inner_block").forGetter(Config::gillInner),
                        net.minecraft.core.registries.BuiltInRegistries.BLOCK.byNameCodec()
                                .fieldOf("glow_cap_block").orElse(net.minecraft.world.level.block.Blocks.AIR)
                                .forGetter(Config::glowCap)
                ).apply(instance, Config::new)
        );
    }
}