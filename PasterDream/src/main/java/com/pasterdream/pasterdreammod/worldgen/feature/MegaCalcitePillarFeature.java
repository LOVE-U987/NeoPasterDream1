package com.pasterdream.pasterdreammod.worldgen.feature;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.pasterdream.pasterdreammod.worldgen.WorldGenUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import java.util.Set;
import java.util.HashSet;

/**
 * 巨型方解石云端柱特征 —— 寒冷染梦的远古冰山地标
 * <p>
 * 60~80 格高的远古方解石巨柱，自成一片冰雪生态：
 * - 基部 7x7 逐渐收窄，宛如冰山底座
 * - 根部簇拥 8~12 根小方解石尖刺
 * - 柱身上每隔几格缠绕发光石英环带
 * - 柱身周围悬浮冰晶般的发光方块
 * - 顶部 11x11 宽阔云盘，盘面散布发光石英星辰
 * - 云盘边缘垂下 3~4 格发光冰晶珠帘
 * 生成前检查区域占用。
 */
public class MegaCalcitePillarFeature extends Feature<MegaCalcitePillarFeature.Config> {

    public MegaCalcitePillarFeature() {
        super(Config.CODEC.codec());
    }

    @Override
    public boolean place(FeaturePlaceContext<Config> context) {
        Config config = context.config();
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource random = context.random();

        int groundY = WorldGenUtils.findGroundY(level, origin.getX(), origin.getY(), origin.getZ(), 15);
        if (groundY == Integer.MIN_VALUE) {
            return false;
        }

        if (isAreaOccupied(level, origin.getX(), origin.getZ(), groundY, 12)) {
            return false;
        }

        int totalHeight = random.nextIntBetweenInclusive(60, 80);
        int undergroundPortion = Math.max(4, totalHeight / 3);
        int aboveGroundPortion = totalHeight - undergroundPortion;
        int bottomY = groundY - undergroundPortion;
        int topY = groundY + aboveGroundPortion;
        boolean placedAny = false;
        Set<BlockPos> allPlaced = new HashSet<>();

        placedAny |= placeMainPillar(level, random, config, origin.getX(), origin.getZ(), bottomY, topY, groundY, allPlaced);

        placedAny |= placeBabySpears(level, random, config, origin.getX(), origin.getZ(), groundY, allPlaced);

        placedAny |= placeGlowingRings(level, random, config, origin.getX(), origin.getZ(), groundY, topY, allPlaced);

        placedAny |= placeFloatingCrystals(level, random, config, origin.getX(), origin.getZ(), groundY, aboveGroundPortion, allPlaced);

        placedAny |= placeCloudPlatform(level, random, config, origin.getX(), origin.getZ(), topY, allPlaced);

        placedAny |= placeHangingIcicles(level, random, config, origin.getX(), origin.getZ(), topY, allPlaced);

        return placedAny;
    }

    private boolean placeMainPillar(WorldGenLevel level, RandomSource random, Config config,
                                     int originX, int originZ, int bottomY, int topY, int groundY,
                                     Set<BlockPos> allPlaced) {
        boolean placed = false;
        int totalHeight = topY - bottomY;
        int cloudPlatformRadius = 6;

        for (int y = bottomY; y <= topY; y++) {
            float progress = (float) (y - bottomY) / (float) totalHeight;
            int half;
            if (progress < 0.10f) {
                half = 3;
            } else if (progress < 0.25f) {
                half = 2;
            } else if (progress < 0.50f) {
                half = 1;
            } else if (progress < 0.80f) {
                half = 1;
            } else {
                half = 0;
            }
            if (y < groundY) {
                float undergroundBias = 1.0f + (float) (groundY - y) * 0.15f;
                half = Math.min(3, (int) Math.round((half + 1) * undergroundBias) - 1);
            }

            int layersFromTop = topY - y;
            boolean isCloudRingZone = layersFromTop >= 0 && layersFromTop <= 25;

            int cloudRingExtend = 0;
            if (isCloudRingZone) {
                if (layersFromTop <= 8) cloudRingExtend = 5;
                else if (layersFromTop <= 16) cloudRingExtend = 3;
                else cloudRingExtend = 2;
            }

            int searchRadius = Math.max(cloudPlatformRadius, half + cloudRingExtend + 1);

            for (int dx = -searchRadius; dx <= searchRadius; dx++) {
                for (int dz = -searchRadius; dz <= searchRadius; dz++) {
                    boolean isCalciteCore = dx >= -half && dx <= half
                        && dz >= -half && dz <= half;
                    boolean isCloudRing = isCloudRingZone && !isCalciteCore
                        && Math.abs(dx) <= half + cloudRingExtend
                        && Math.abs(dz) <= half + cloudRingExtend
                        && Math.abs(dx) > half && Math.abs(dz) > half;

                    if (!isCalciteCore && !isCloudRing) continue;

                    BlockPos placePos = new BlockPos(originX + dx, y, originZ + dz);
                    BlockState existing = level.getBlockState(placePos);
                    if (!existing.isAir() && !existing.canBeReplaced()) continue;
                    if (allPlaced.contains(placePos)) continue;

                    BlockState state;
                    if (isCalciteCore) {
                        if (y == topY && dx == 0 && dz == 0) {
                            state = config.glowBlock().defaultBlockState();
                        } else {
                            state = config.pillarBlock().defaultBlockState();
                        }
                    } else {
                        if (random.nextFloat() < 0.12f) {
                            state = config.thickCloudBlock().defaultBlockState();
                        } else {
                            state = config.cloudBlock().defaultBlockState();
                        }
                    }

                    level.setBlock(placePos, state, 3);
                    allPlaced.add(placePos);
                    placed = true;
                }
            }
        }
        return placed;
    }

    private boolean placeBabySpears(WorldGenLevel level, RandomSource random, Config config,
                                     int originX, int originZ, int groundY,
                                     Set<BlockPos> allPlaced) {
        boolean placed = false;
        int count = random.nextIntBetweenInclusive(8, 12);

        for (int i = 0; i < count; i++) {
            int angle = random.nextInt(360);
            int dist = random.nextIntBetweenInclusive(5, 14);
            int sx = originX + (int) (Math.cos(Math.toRadians(angle)) * dist);
            int sz = originZ + (int) (Math.sin(Math.toRadians(angle)) * dist);

            int spearGroundY = WorldGenUtils.findGroundY(level, sx, groundY + 4, sz, 8);
            if (spearGroundY == Integer.MIN_VALUE) continue;
            if (Math.abs(spearGroundY - groundY) > 3) continue;

            int spearHeight = random.nextIntBetweenInclusive(3, 8);
            int spearWidth = random.nextBoolean() ? 1 : 2;

            for (int y = spearGroundY; y < spearGroundY + spearHeight; y++) {
                float prog = (float) (y - spearGroundY) / (float) spearHeight;
                int hw = (int) Math.round(spearWidth * (1.0f - prog * 0.6f));
                if (hw < 1) break;

                for (int dx = -hw; dx <= hw; dx++) {
                    for (int dz = -hw; dz <= hw; dz++) {
                        BlockPos spearPos = new BlockPos(sx + dx, y, sz + dz);
                        if (canReplace(level, spearPos, allPlaced)) {
                            if (y == spearGroundY + spearHeight - 1
                                && dx == 0 && dz == 0
                                && random.nextFloat() < 0.15f) {
                                level.setBlock(spearPos, config.glowBlock().defaultBlockState(), 3);
                            } else {
                                level.setBlock(spearPos, config.pillarBlock().defaultBlockState(), 3);
                            }
                            allPlaced.add(spearPos);
                            placed = true;
                        }
                    }
                }
            }
        }
        return placed;
    }

    private boolean placeGlowingRings(WorldGenLevel level, RandomSource random, Config config,
                                       int originX, int originZ, int groundY, int topY,
                                       Set<BlockPos> allPlaced) {
        boolean placed = false;
        int ringInterval = random.nextIntBetweenInclusive(6, 10);
        int stemHeight = topY - groundY;
        int baseRingLayer = groundY + random.nextIntBetweenInclusive(6, 12);

        for (int ringY = baseRingLayer; ringY < topY; ringY += ringInterval) {
            int layerFromGround = ringY - groundY;
            float progress = (float) layerFromGround / (float) stemHeight;
            int half;
            if (progress < 0.15f) half = 2;
            else if (progress < 0.4f) half = 1;
            else half = 0;

            int extend = half + 1;
            for (int dx = -extend; dx <= extend; dx++) {
                for (int dz = -extend; dz <= extend; dz++) {
                    boolean isSurface = (Math.abs(dx) == extend || Math.abs(dz) == extend);
                    if (!isSurface) continue;

                    BlockPos ringPos = new BlockPos(originX + dx, ringY, originZ + dz);
                    if (!canReplace(level, ringPos, allPlaced)) continue;

                    Block ringBlock = random.nextFloat() < 0.35f
                        ? config.glowBlock()
                        : (random.nextFloat() < 0.3f ? config.thickCloudBlock() : config.cloudBlock());
                    level.setBlock(ringPos, ringBlock.defaultBlockState(), 3);
                    allPlaced.add(ringPos);
                    placed = true;
                }
            }

            for (int dx = -extend - 1; dx <= extend + 1; dx++) {
                for (int dz = -extend - 1; dz <= extend + 1; dz++) {
                    if (Math.abs(dx) <= extend && Math.abs(dz) <= extend) continue;
                    if (Math.abs(dx) != extend + 1 && Math.abs(dz) != extend + 1) continue;
                    if (random.nextFloat() < 0.18f) {
                        BlockPos sparklePos = new BlockPos(originX + dx, ringY, originZ + dz);
                        if (!level.getBlockState(sparklePos).isAir()) continue;
                        if (allPlaced.contains(sparklePos)) continue;
                        level.setBlock(sparklePos, config.glowBlock().defaultBlockState(), 3);
                        allPlaced.add(sparklePos);
                        placed = true;
                    }
                }
            }
        }
        return placed;
    }

    private boolean placeFloatingCrystals(WorldGenLevel level, RandomSource random, Config config,
                                           int originX, int originZ, int groundY, int aboveGroundHeight,
                                           Set<BlockPos> allPlaced) {
        boolean placed = false;
        int crystalCount = random.nextIntBetweenInclusive(25, 45);

        for (int i = 0; i < crystalCount; i++) {
            int dist = random.nextIntBetweenInclusive(5, 12);
            int angle = random.nextInt(360);
            int cx = originX + (int) (Math.cos(Math.toRadians(angle)) * dist);
            int cz = originZ + (int) (Math.sin(Math.toRadians(angle)) * dist);
            int cy = groundY + random.nextInt(aboveGroundHeight);

            BlockPos crystalPos = new BlockPos(cx, cy, cz);
            if (!level.getBlockState(crystalPos).isAir()) continue;
            if (allPlaced.contains(crystalPos)) continue;

            Block crystalBlock = random.nextFloat() < 0.30f
                ? config.glowBlock()
                : (random.nextFloat() < 0.4f ? config.thickCloudBlock() : config.cloudBlock());
            level.setBlock(crystalPos, crystalBlock.defaultBlockState(), 3);
            allPlaced.add(crystalPos);
            placed = true;

            if (random.nextFloat() < 0.12f) {
                BlockPos crystalPos2 = new BlockPos(cx, cy + 1, cz);
                if (level.getBlockState(crystalPos2).isAir() && !allPlaced.contains(crystalPos2)) {
                    level.setBlock(crystalPos2, config.glowBlock().defaultBlockState(), 3);
                    allPlaced.add(crystalPos2);
                }
            }
        }
        return placed;
    }

    private boolean placeCloudPlatform(WorldGenLevel level, RandomSource random, Config config,
                                        int originX, int originZ, int topY,
                                        Set<BlockPos> allPlaced) {
        boolean placed = false;
        int cloudPlatformRadius = 6;

        for (int y = topY - 2; y <= topY; y++) {
            int half = cloudPlatformRadius;
            for (int dx = -half; dx <= half; dx++) {
                for (int dz = -half; dz <= half; dz++) {
                    boolean isCore = Math.abs(dx) <= 1 && Math.abs(dz) <= 1 && y < topY;
                    if (isCore) continue;

                    BlockPos platPos = new BlockPos(originX + dx, y, originZ + dz);
                    BlockState existing = level.getBlockState(platPos);
                    if (!existing.isAir() && !existing.canBeReplaced()) continue;
                    if (allPlaced.contains(platPos)) continue;

                    BlockState state;
                    if (y == topY && random.nextFloat() < 0.06f) {
                        state = config.glowBlock().defaultBlockState();
                    } else if (random.nextFloat() < 0.08f) {
                        state = config.thickCloudBlock().defaultBlockState();
                    } else {
                        state = config.cloudBlock().defaultBlockState();
                    }
                    level.setBlock(platPos, state, 3);
                    allPlaced.add(platPos);
                    placed = true;
                }
            }

            for (int dx = -half - 1; dx <= half + 1; dx++) {
                for (int dz = -half - 1; dz <= half + 1; dz++) {
                    if (Math.abs(dx) <= half && Math.abs(dz) <= half) continue;
                    if (Math.abs(dx) != half + 1 && Math.abs(dz) != half + 1) continue;
                    if (random.nextFloat() < 0.15f) {
                        BlockPos particlePos = new BlockPos(originX + dx, y, originZ + dz);
                        if (!level.getBlockState(particlePos).isAir()) continue;
                        if (allPlaced.contains(particlePos)) continue;
                        Block particleBlock = random.nextFloat() < 0.3f
                            ? config.glowBlock() : config.cloudBlock();
                        level.setBlock(particlePos, particleBlock.defaultBlockState(), 3);
                        allPlaced.add(particlePos);
                        placed = true;
                    }
                }
            }
        }
        return placed;
    }

    private boolean placeHangingIcicles(WorldGenLevel level, RandomSource random, Config config,
                                         int originX, int originZ, int topY,
                                         Set<BlockPos> allPlaced) {
        boolean placed = false;

        for (int dx = -7; dx <= 7; dx++) {
            for (int dz = -7; dz <= 7; dz++) {
                if (Math.abs(dx) < 5 && Math.abs(dz) < 5) continue;
                if (random.nextFloat() < 0.15f) {
                    int dropLen = random.nextIntBetweenInclusive(1, 4);
                    for (int drop = 1; drop <= dropLen; drop++) {
                        BlockPos dripPos = new BlockPos(originX + dx, topY - drop, originZ + dz);
                        if (!level.getBlockState(dripPos).isAir()) break;
                        if (allPlaced.contains(dripPos)) break;
                        Block dripBlock = (random.nextFloat() < 0.35f)
                            ? config.glowBlock()
                            : (random.nextFloat() < 0.3f ? config.thickCloudBlock() : config.cloudBlock());
                        level.setBlock(dripPos, dripBlock.defaultBlockState(), 3);
                        allPlaced.add(dripPos);
                        placed = true;
                        if (random.nextFloat() < 0.2f) break;
                    }
                }
            }
        }
        return placed;
    }

    private boolean isAreaOccupied(WorldGenLevel level, int originX, int originZ, int groundY, int radius) {
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                BlockPos pos = new BlockPos(originX + dx, groundY, originZ + dz);
                BlockState state = level.getBlockState(pos);
                if (state.isAir() || state.canBeReplaced()) continue;
                if (state.is(Blocks.GRASS_BLOCK) || state.is(Blocks.DIRT)
                    || state.is(Blocks.STONE) || state.is(Blocks.CALCITE)) continue;
                return true;
            }
        }
        return false;
    }

    private boolean canReplace(WorldGenLevel level, BlockPos pos, Set<BlockPos> placedSet) {
        if (placedSet.contains(pos)) return false;
        BlockState state = level.getBlockState(pos);
        return state.isAir() || state.canBeReplaced();
    }

    public record Config(Block pillarBlock, Block cloudBlock, Block thickCloudBlock, Block glowBlock) implements FeatureConfiguration {
        public static final MapCodec<Config> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        net.minecraft.core.registries.BuiltInRegistries.BLOCK.byNameCodec()
                                .fieldOf("pillar_block").forGetter(Config::pillarBlock),
                        net.minecraft.core.registries.BuiltInRegistries.BLOCK.byNameCodec()
                                .fieldOf("cloud_block").forGetter(Config::cloudBlock),
                        net.minecraft.core.registries.BuiltInRegistries.BLOCK.byNameCodec()
                                .fieldOf("thick_cloud_block").forGetter(Config::thickCloudBlock),
                        net.minecraft.core.registries.BuiltInRegistries.BLOCK.byNameCodec()
                                .fieldOf("glow_block").forGetter(Config::glowBlock)
                ).apply(instance, Config::new)
        );
    }
}