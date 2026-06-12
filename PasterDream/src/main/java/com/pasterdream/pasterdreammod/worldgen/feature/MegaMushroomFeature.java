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

import java.util.HashSet;
import java.util.Set;

/**
 * 巨型粉丁菇特征 —— 染梦草原的远古巨蘑地标
 * <p>
 * 60~80 格高的远古巨蘑，自成一个小生态：
 * - 基部 7x7 逐渐收窄，宛如巨树板根
 * - 根部簇拥 6~10 朵小粉丁菇
 * - 菌柄上每隔几格缠绕发光环带
 * - 菌柄周围悬浮孢子般的发光方块
 * - 下层 17x17 + 上层 13x13 双层宝塔菌伞
 * - 伞沿垂下 3~4 格长的发光珠帘
 * 生成前检查区域占用。
 */
public class MegaMushroomFeature extends Feature<MegaMushroomFeature.Config> {

    public MegaMushroomFeature() {
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
        int stemHeight = (int) (totalHeight * 0.68f);
        int stemTop = groundY + stemHeight;
        Set<BlockPos> allPlaced = new HashSet<>();
        boolean placedAny = false;

        placedAny |= placeStem(level, random, config, origin.getX(), origin.getZ(), groundY, stemTop, allPlaced);

        placedAny |= placeBabyMushrooms(level, random, config, origin.getX(), origin.getZ(), groundY, allPlaced);

        placedAny |= placeGlowingRings(level, random, config, origin.getX(), origin.getZ(), groundY, stemTop, allPlaced);

        placedAny |= placeSpores(level, random, config, origin.getX(), origin.getZ(), groundY, stemTop, allPlaced);

        int gapHeight = 4;
        int upperCapStemTop = stemTop + gapHeight;
        for (int y = stemTop + 1; y <= upperCapStemTop; y++) {
            int half = 1;
            for (int dx = -half; dx <= half; dx++) {
                for (int dz = -half; dz <= half; dz++) {
                    BlockPos gapPos = new BlockPos(origin.getX() + dx, y, origin.getZ() + dz);
                    if (canReplace(level, gapPos, allPlaced)) {
                        level.setBlock(gapPos, config.stem().defaultBlockState(), 3);
                        allPlaced.add(gapPos);
                        placedAny = true;
                    }
                }
            }
            if (y >= stemTop + 2 && y <= stemTop + 3) {
                for (int dx = -4; dx <= 4; dx++) {
                    for (int dz = -4; dz <= 4; dz++) {
                        if (Math.abs(dx) <= 1 && Math.abs(dz) <= 1) continue;
                        if (random.nextFloat() < 0.08f) {
                            BlockPos decoPos = new BlockPos(origin.getX() + dx, y, origin.getZ() + dz);
                            if (canReplace(level, decoPos, allPlaced)) {
                                level.setBlock(decoPos, random.nextFloat() < 0.3f
                                    ? config.glowCap().defaultBlockState()
                                    : config.capOuter().defaultBlockState(), 3);
                                allPlaced.add(decoPos);
                                placedAny = true;
                            }
                        }
                    }
                }
            }
        }

        placedAny |= placeCapLayer(level, random, config, origin.getX(), origin.getZ(),
            stemTop, 8, allPlaced, true);

        placedAny |= placeCapLayer(level, random, config, origin.getX(), origin.getZ(),
            upperCapStemTop, 6, allPlaced, false);

        placedAny |= placeHangingTendrils(level, random, config, origin.getX(), origin.getZ(), stemTop, allPlaced);

        return placedAny;
    }

    private boolean placeStem(WorldGenLevel level, RandomSource random, Config config,
                               int originX, int originZ, int groundY, int stemTop,
                               Set<BlockPos> allPlaced) {
        boolean placed = false;
        int minHalf = 0;
        int totalStemLayers = stemTop - groundY;

        for (int y = groundY; y <= stemTop; y++) {
            int layerFromGround = y - groundY;
            float progress = (float) layerFromGround / (float) totalStemLayers;
            int half;
            if (progress < 0.15f) {
                half = 3;
            } else if (progress < 0.4f) {
                half = 2;
            } else if (progress < 0.7f) {
                half = 1;
            } else {
                half = Math.max(minHalf, (int) Math.round(1.0f - (progress - 0.7f) / 0.3f));
            }

            for (int dx = -half; dx <= half; dx++) {
                for (int dz = -half; dz <= half; dz++) {
                    BlockPos stemPos = new BlockPos(originX + dx, y, originZ + dz);
                    if (canReplace(level, stemPos, allPlaced)) {
                        level.setBlock(stemPos, config.stem().defaultBlockState(), 3);
                        allPlaced.add(stemPos);
                        placed = true;
                    }
                }
            }
        }
        return placed;
    }

    private boolean placeBabyMushrooms(WorldGenLevel level, RandomSource random, Config config,
                                        int originX, int originZ, int groundY,
                                        Set<BlockPos> allPlaced) {
        boolean placed = false;
        int count = random.nextIntBetweenInclusive(6, 10);

        for (int i = 0; i < count; i++) {
            int angle = random.nextInt(360);
            int dist = random.nextIntBetweenInclusive(6, 12);
            int mx = originX + (int) (Math.cos(Math.toRadians(angle)) * dist);
            int mz = originZ + (int) (Math.sin(Math.toRadians(angle)) * dist);

            int babyGroundY = WorldGenUtils.findGroundY(level, mx, groundY + 4, mz, 8);
            if (babyGroundY == Integer.MIN_VALUE) continue;
            if (Math.abs(babyGroundY - groundY) > 3) continue;

            int stemHeight = random.nextIntBetweenInclusive(2, 5);
            int capWidth = random.nextBoolean() ? 3 : 4;
            placed |= placeSmallMushroom(level, random, config, mx, mz, babyGroundY, stemHeight, capWidth, allPlaced);
        }
        return placed;
    }

    private boolean placeSmallMushroom(WorldGenLevel level, RandomSource random, Config config,
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

        int half = capWidth / 2;
        for (int dx = -half; dx < capWidth - half; dx++) {
            for (int dz = -half; dz < capWidth - half; dz++) {
                BlockPos capPos = new BlockPos(mx + dx, stemTop + 1, mz + dz);
                if (canReplace(level, capPos, allPlaced)) {
                    level.setBlock(capPos, (dx == 0 && dz == 0 && random.nextFloat() < 0.1f)
                        ? config.glowCap().defaultBlockState()
                        : config.capOuter().defaultBlockState(), 3);
                    allPlaced.add(capPos);
                    placed = true;
                }
            }
        }

        for (int dx = -half; dx < capWidth - half; dx++) {
            for (int dz = -half; dz < capWidth - half; dz++) {
                boolean isEdgeX = (dx == -half || dx == capWidth - half - 1);
                boolean isEdgeZ = (dz == -half || dz == capWidth - half - 1);
                if (!isEdgeX && !isEdgeZ) continue;
                BlockPos brimPos = new BlockPos(mx + dx, stemTop, mz + dz);
                if (canReplace(level, brimPos, allPlaced)) {
                    level.setBlock(brimPos, config.capOuter().defaultBlockState(), 3);
                    allPlaced.add(brimPos);
                    placed = true;
                }
            }
        }
        return placed;
    }

    private boolean placeGlowingRings(WorldGenLevel level, RandomSource random, Config config,
                                       int originX, int originZ, int groundY, int stemTop,
                                       Set<BlockPos> allPlaced) {
        boolean placed = false;
        int ringInterval = random.nextIntBetweenInclusive(6, 10);
        int baseRingLayer = groundY + random.nextIntBetweenInclusive(8, 15);

        for (int ringY = baseRingLayer; ringY < stemTop; ringY += ringInterval) {
            int layerFromGround = ringY - groundY;
            int maxLayers = stemTop - groundY;
            float progress = (float) layerFromGround / (float) maxLayers;
            int half;
            if (progress < 0.15f) half = 3;
            else if (progress < 0.4f) half = 2;
            else if (progress < 0.7f) half = 1;
            else half = 0;

            int extend = half + 1;
            for (int dx = -extend; dx <= extend; dx++) {
                for (int dz = -extend; dz <= extend; dz++) {
                    boolean isStemSurface = Math.abs(dx) > half || Math.abs(dz) > half;
                    if (!isStemSurface) continue;
                    if (Math.abs(dx) != extend && Math.abs(dz) != extend) continue;

                    BlockPos ringPos = new BlockPos(originX + dx, ringY, originZ + dz);
                    if (!canReplace(level, ringPos, allPlaced)) continue;

                    Block ringBlock = random.nextFloat() < 0.4f
                        ? config.glowCap()
                        : (random.nextFloat() < 0.3f ? config.capOuter() : config.stem());
                    level.setBlock(ringPos, ringBlock.defaultBlockState(), 3);
                    allPlaced.add(ringPos);
                    placed = true;
                }
            }

            for (int dx = -extend - 1; dx <= extend + 1; dx++) {
                for (int dz = -extend - 1; dz <= extend + 1; dz++) {
                    if (Math.abs(dx) <= extend && Math.abs(dz) <= extend) continue;
                    if (Math.abs(dx) != extend + 1 && Math.abs(dz) != extend + 1) continue;
                    if (random.nextFloat() < 0.20f) {
                        BlockPos sparklePos = new BlockPos(originX + dx, ringY, originZ + dz);
                        if (level.getBlockState(sparklePos).isAir()) {
                            level.setBlock(sparklePos, config.glowCap().defaultBlockState(), 3);
                            allPlaced.add(sparklePos);
                            placed = true;
                        }
                    }
                }
            }
        }
        return placed;
    }

    private boolean placeSpores(WorldGenLevel level, RandomSource random, Config config,
                                 int originX, int originZ, int groundY, int stemTop,
                                 Set<BlockPos> allPlaced) {
        boolean placed = false;
        int sporeCount = random.nextIntBetweenInclusive(20, 40);
        int maxLayers = stemTop - groundY;

        for (int i = 0; i < sporeCount; i++) {
            int dist = random.nextIntBetweenInclusive(4, 10);
            int angle = random.nextInt(360);
            int sx = originX + (int) (Math.cos(Math.toRadians(angle)) * dist);
            int sz = originZ + (int) (Math.sin(Math.toRadians(angle)) * dist);

            int layerOffset = random.nextInt(maxLayers);
            int sy = groundY + layerOffset;

            BlockPos sporePos = new BlockPos(sx, sy, sz);
            if (!level.getBlockState(sporePos).isAir()) continue;
            if (allPlaced.contains(sporePos)) continue;

            Block sporeBlock = random.nextFloat() < 0.35f
                ? config.glowCap()
                : (random.nextFloat() < 0.3f ? config.capOuter() : config.stem());
            level.setBlock(sporePos, sporeBlock.defaultBlockState(), 3);
            allPlaced.add(sporePos);
            placed = true;

            if (random.nextFloat() < 0.15f) {
                int sy2 = sy + 1;
                BlockPos sporePos2 = new BlockPos(sx, sy2, sz);
                if (level.getBlockState(sporePos2).isAir() && !allPlaced.contains(sporePos2)) {
                    level.setBlock(sporePos2, config.glowCap().defaultBlockState(), 3);
                    allPlaced.add(sporePos2);
                }
            }
        }
        return placed;
    }

    private boolean placeCapLayer(WorldGenLevel level, RandomSource random, Config config,
                                   int capX, int capZ, int baseY, int capRadius,
                                   Set<BlockPos> allPlaced, boolean isLowerCap) {
        int[] layerRadii = {capRadius, capRadius, capRadius - 1,
            capRadius - 2, capRadius - 3, capRadius - 4, capRadius - 5, capRadius - 6};
        int layers = isLowerCap ? layerRadii.length : 5;
        boolean placed = false;

        for (int layer = 0; layer < layers; layer++) {
            int ly = baseY + layer;
            int half = layerRadii[layer];
            if (half < 0) break;

            for (int dx = -half; dx <= half; dx++) {
                for (int dz = -half; dz <= half; dz++) {
                    boolean isEdge = (dx == -half || dx == half || dz == -half || dz == half);
                    boolean isInner = !isEdge;
                    boolean isCenter = (dx == 0 && dz == 0);
                    boolean isBottomLayer = (layer == 0);

                    BlockPos capPos = new BlockPos(capX + dx, ly, capZ + dz);
                    if (!canReplace(level, capPos, allPlaced)) continue;

                    Block capBlock;
                    if (isBottomLayer && isInner && isCenter) {
                        capBlock = config.stem();
                    } else if (isBottomLayer && isInner) {
                        capBlock = config.gillInner();
                    } else if (isInner) {
                        capBlock = (random.nextFloat() < 0.08f) ? config.glowCap() : config.capOuter();
                    } else {
                        capBlock = (random.nextFloat() < 0.04f) ? config.glowCap() : config.capOuter();
                    }

                    level.setBlock(capPos, capBlock.defaultBlockState(), 3);
                    allPlaced.add(capPos);
                    placed = true;
                }
            }
        }
        return placed;
    }

    private boolean placeHangingTendrils(WorldGenLevel level, RandomSource random, Config config,
                                          int originX, int originZ, int stemTop,
                                          Set<BlockPos> allPlaced) {
        boolean placed = false;
        int[] radii = {8, 8, 7, 6, 5, 4, 3, 2};

        for (int layer = 0; layer < 3; layer++) {
            int ly = stemTop + layer;
            int half = radii[Math.min(layer, radii.length - 1)];

            for (int dx = -half; dx <= half; dx++) {
                for (int dz = -half; dz <= half; dz++) {
                    if (Math.abs(dx) < half - 1 && Math.abs(dz) < half - 1) continue;
                    if (random.nextFloat() < 0.18f) {
                        int dropLen = random.nextIntBetweenInclusive(1, 4);
                        for (int drop = 1; drop <= dropLen; drop++) {
                            BlockPos dripPos = new BlockPos(originX + dx, ly - drop, originZ + dz);
                            if (!level.getBlockState(dripPos).isAir()) break;
                            if (allPlaced.contains(dripPos)) break;
                            Block dripBlock = (random.nextFloat() < 0.3f)
                                ? config.glowCap()
                                : (random.nextFloat() < 0.4f ? config.capOuter() : config.stem());
                            level.setBlock(dripPos, dripBlock.defaultBlockState(), 3);
                            allPlaced.add(dripPos);
                            placed = true;
                            if (random.nextFloat() < 0.2f) break;
                        }
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

    public record Config(Block stem, Block capOuter, Block gillInner, Block glowCap) implements FeatureConfiguration {
        public static final MapCodec<Config> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        net.minecraft.core.registries.BuiltInRegistries.BLOCK.byNameCodec()
                                .fieldOf("stem_block").forGetter(Config::stem),
                        net.minecraft.core.registries.BuiltInRegistries.BLOCK.byNameCodec()
                                .fieldOf("cap_outer_block").forGetter(Config::capOuter),
                        net.minecraft.core.registries.BuiltInRegistries.BLOCK.byNameCodec()
                                .fieldOf("gill_inner_block").forGetter(Config::gillInner),
                        net.minecraft.core.registries.BuiltInRegistries.BLOCK.byNameCodec()
                                .fieldOf("glow_cap_block").forGetter(Config::glowCap)
                ).apply(instance, Config::new)
        );
    }
}