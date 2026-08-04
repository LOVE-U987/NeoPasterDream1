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
import com.pasterdream.pasterdreammod.api.worldgen.WorldGenUtils;
import com.pasterdream.pasterdreammod.worldgen.tree.DyedreamTreePlacers;

/**
 * 染梦垂枝树冠生成器
 * <p>
 * 在 attachment 周围生成下垂的叶片条带，适合垂泪树变体。
 */
public class DyedreamWeepingFoliagePlacer extends FoliagePlacer {

    public static final MapCodec<DyedreamWeepingFoliagePlacer> CODEC = RecordCodecBuilder.mapCodec(instance ->
            foliagePlacerParts(instance).and(IntProvider.codec(0, 8).fieldOf("height").forGetter(fp -> fp.height))
                    .apply(instance, DyedreamWeepingFoliagePlacer::new));

    private final IntProvider height;

    /**
     * 构造垂枝树冠生成器
     *
     * @param radius 树冠半径
     * @param offset 垂直偏移
     * @param height 下垂高度
     */
    public DyedreamWeepingFoliagePlacer(IntProvider radius, IntProvider offset, IntProvider height) {
        super(radius, offset);
        this.height = height;
    }

    @Override
    protected FoliagePlacerType<?> type() {
        return DyedreamTreePlacers.WEEPING_FOLIAGE_PLACER.get();
    }

    @Override
    protected void createFoliage(LevelSimulatedReader level, FoliageSetter foliageSetter,
                                 RandomSource random, TreeConfiguration config, int maxFreeTreeHeight,
                                 FoliageAttachment attachment, int foliageHeight, int radius, int offset) {
        BlockPos center = attachment.pos();
        int h = this.height.sample(random);

        // 包装 FoliageSetter：越出当前世界生成可写范围的方块位置直接跳过
        //（垂枝树冠在区块边缘生成时会跨越到尚未就绪的相邻区块，触发
        //  "Detected setBlock in a far chunk" 错误刷屏，此处提前过滤）
        FoliageSetter safeSetter = new FoliageSetter() {
            @Override
            public void set(BlockPos pos, BlockState state) {
                if (WorldGenUtils.canPlaceInRegion(level, pos)) {
                    foliageSetter.set(pos, state);
                }
            }

            @Override
            public boolean isSet(BlockPos pos) {
                return foliageSetter.isSet(pos);
            }
        };

        for (int y = 0; y >= -h; y--) {
            int layerRadius = radius - (int) (Math.abs(y) * 0.5);
            for (int x = -layerRadius; x <= layerRadius; x++) {
                for (int z = -layerRadius; z <= layerRadius; z++) {
                    if (x * x + z * z <= layerRadius * layerRadius) {
                        BlockPos leafPos = center.offset(x, y + offset, z);
                        tryPlaceLeaf(level, safeSetter, random, config, leafPos);
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
        return random.nextFloat() < 0.1f;
    }
}
