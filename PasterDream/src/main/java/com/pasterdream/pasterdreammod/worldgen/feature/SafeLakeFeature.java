package com.pasterdream.pasterdreammod.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

/**
 * 世界生成安全水色湖：逻辑对齐原版 {@code LakeFeature}（椭圆腔 + fluid + barrier），
 * <b>刻意省略</b> 水面 {@code getBiome(...).shouldFreeze} 结冰检查。
 * <p>
 * 根因（1.21.1）：{@code LakeFeature.place} 在 16×16 水面格上调用 {@code LevelReader#getBiome}，
 * {@code BiomeManager} 会对采样点做 -2 偏移与 quart 邻域插值，可能请求
 * {@code WorldGenRegion} 3×3 缓存外的 chunk，触发
 * {@code IllegalStateException: Requested chunk unavailable during world generation}，
 * 导致 chunk 生成失败 → 风维空洞/坠落。
 * <p>
 * 风旅 biome temperature=1，结冰分支本就不会生效；去掉后形貌（水 + cyan_stone barrier）不变。
 * 配置字段与原版 lake 相同（fluid / barrier），便于 datapack 直接切换 type。
 */
public class SafeLakeFeature extends Feature<SafeLakeFeature.Configuration> {

    private static final BlockState AIR = Blocks.CAVE_AIR.defaultBlockState();

    public SafeLakeFeature() {
        super(Configuration.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<Configuration> context) {
        BlockPos origin = context.origin();
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        Configuration config = context.config();

        if (origin.getY() <= level.getMinBuildHeight() + 4) {
            return false;
        }

        // 与原版 LakeFeature 一致：整体下沉 4，上半为空气腔、下半为流体
        BlockPos base = origin.below(4);
        boolean[] mask = new boolean[2048];
        int blobs = random.nextInt(4) + 4;

        for (int b = 0; b < blobs; b++) {
            double rx = random.nextDouble() * 6.0 + 3.0;
            double ry = random.nextDouble() * 4.0 + 2.0;
            double rz = random.nextDouble() * 6.0 + 3.0;
            double cx = random.nextDouble() * (16.0 - rx - 2.0) + 1.0 + rx / 2.0;
            double cy = random.nextDouble() * (8.0 - ry - 4.0) + 2.0 + ry / 2.0;
            double cz = random.nextDouble() * (16.0 - rz - 2.0) + 1.0 + rz / 2.0;

            for (int x = 1; x < 15; x++) {
                for (int z = 1; z < 15; z++) {
                    for (int y = 1; y < 7; y++) {
                        double dx = (x - cx) / (rx / 2.0);
                        double dy = (y - cy) / (ry / 2.0);
                        double dz = (z - cz) / (rz / 2.0);
                        if (dx * dx + dy * dy + dz * dz < 1.0) {
                            mask[(x * 16 + z) * 8 + y] = true;
                        }
                    }
                }
            }
        }

        BlockState fluid = config.fluid().getState(random, base);

        // 预检：表面不得已是液体；腔体下半须为固体（或已是本 fluid）
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 0; y < 8; y++) {
                    boolean edge = !mask[(x * 16 + z) * 8 + y]
                            && (
                            x < 15 && mask[((x + 1) * 16 + z) * 8 + y]
                                    || x > 0 && mask[((x - 1) * 16 + z) * 8 + y]
                                    || z < 15 && mask[(x * 16 + z + 1) * 8 + y]
                                    || z > 0 && mask[(x * 16 + (z - 1)) * 8 + y]
                                    || y < 7 && mask[(x * 16 + z) * 8 + y + 1]
                                    || y > 0 && mask[(x * 16 + z) * 8 + (y - 1)]
                    );
                    if (!edge) {
                        continue;
                    }
                    BlockState st = level.getBlockState(base.offset(x, y, z));
                    if (y >= 4 && st.liquid()) {
                        return false;
                    }
                    if (y < 4 && !st.isSolid() && st != fluid) {
                        return false;
                    }
                }
            }
        }

        // 挖腔：y>=4 空气，y<4 流体
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 0; y < 8; y++) {
                    if (!mask[(x * 16 + z) * 8 + y]) {
                        continue;
                    }
                    BlockPos p = base.offset(x, y, z);
                    if (!canReplaceBlock(level.getBlockState(p))) {
                        continue;
                    }
                    boolean airHalf = y >= 4;
                    level.setBlock(p, airHalf ? AIR : fluid, 2);
                    if (airHalf) {
                        level.scheduleTick(p, AIR.getBlock(), 0);
                        this.markAboveForPostProcessing(level, p);
                    }
                }
            }
        }

        // barrier 外壳（cyan_stone 等）
        BlockState barrier = config.barrier().getState(random, base);
        if (!barrier.isAir()) {
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    for (int y = 0; y < 8; y++) {
                        boolean edge = !mask[(x * 16 + z) * 8 + y]
                                && (
                                x < 15 && mask[((x + 1) * 16 + z) * 8 + y]
                                        || x > 0 && mask[((x - 1) * 16 + z) * 8 + y]
                                        || z < 15 && mask[(x * 16 + z + 1) * 8 + y]
                                        || z > 0 && mask[(x * 16 + (z - 1)) * 8 + y]
                                        || y < 7 && mask[(x * 16 + z) * 8 + y + 1]
                                        || y > 0 && mask[(x * 16 + z) * 8 + (y - 1)]
                        );
                        // 与原版：edge && (y < 4 || nextInt(2) != 0)
                        if (!edge || (y >= 4 && random.nextInt(2) == 0)) {
                            continue;
                        }
                        BlockPos p = base.offset(x, y, z);
                        BlockState st = level.getBlockState(p);
                        if (st.isSolid() && !st.is(BlockTags.LAVA_POOL_STONE_CANNOT_REPLACE)) {
                            level.setBlock(p, barrier, 2);
                            this.markAboveForPostProcessing(level, p);
                        }
                    }
                }
            }
        }

        // 故意不调用 getBiome / shouldFreeze —— 见类注释
        return true;
    }

    private static boolean canReplaceBlock(BlockState state) {
        return !state.is(BlockTags.FEATURES_CANNOT_REPLACE);
    }

    public record Configuration(BlockStateProvider fluid, BlockStateProvider barrier) implements FeatureConfiguration {
        public static final Codec<Configuration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BlockStateProvider.CODEC.fieldOf("fluid").forGetter(Configuration::fluid),
                BlockStateProvider.CODEC.fieldOf("barrier").forGetter(Configuration::barrier)
        ).apply(instance, Configuration::new));
    }
}
