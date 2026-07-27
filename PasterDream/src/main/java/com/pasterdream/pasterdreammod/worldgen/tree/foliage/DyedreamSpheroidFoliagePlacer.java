package com.pasterdream.pasterdreammod.worldgen.tree.foliage;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import com.pasterdream.pasterdreammod.worldgen.tree.DyedreamTreePlacers;

/**
 * 染梦球形/椭球树冠生成器
 * <p>
 * 生成规整的椭球树冠，适合繁茂树、巨型树与发光树。
 */
public class DyedreamSpheroidFoliagePlacer extends FoliagePlacer {

    public static final MapCodec<DyedreamSpheroidFoliagePlacer> CODEC = RecordCodecBuilder.mapCodec(instance ->
            foliagePlacerParts(instance).and(IntProvider.codec(0, 8).fieldOf("height").forGetter(fp -> fp.height))
                    .apply(instance, DyedreamSpheroidFoliagePlacer::new));

    private final IntProvider height;

    /**
     * 构造球形/椭球树冠生成器
     *
     * @param radius 树冠半径
     * @param offset 垂直偏移
     * @param height 树冠高度
     */
    public DyedreamSpheroidFoliagePlacer(IntProvider radius, IntProvider offset, IntProvider height) {
        super(radius, offset);
        this.height = height;
    }

    @Override
    protected FoliagePlacerType<?> type() {
        return DyedreamTreePlacers.SPHEROID_FOLIAGE_PLACER.get();
    }

    @Override
    protected void createFoliage(LevelSimulatedReader level, FoliageSetter foliageSetter,
                                 RandomSource random, TreeConfiguration config, int maxFreeTreeHeight,
                                 FoliageAttachment attachment, int foliageHeight, int radius, int offset) {
        BlockPos center = attachment.pos();
        int h = this.height.sample(random);

        for (int y = -h; y <= h; y++) {
            double yFactor = 1.0 - Math.abs(y) / (double) (h + 1);
            int layerRadius = (int) (radius * yFactor);
            for (int x = -layerRadius; x <= layerRadius; x++) {
                for (int z = -layerRadius; z <= layerRadius; z++) {
                    if (x * x + z * z <= layerRadius * layerRadius) {
                        BlockPos leafPos = center.offset(x, y + offset, z);
                        tryPlaceLeaf(level, foliageSetter, random, config, leafPos);
                    }
                }
            }
        }
    }

    @Override
    public int foliageHeight(RandomSource random, int height, TreeConfiguration config) {
        return this.height.sample(random);
    }

    @Override
    protected boolean shouldSkipLocation(RandomSource random, int localX, int localY, int localZ, int range, boolean large) {
        return false;
    }
}
