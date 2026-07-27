package com.pasterdream.pasterdreammod.worldgen.tree.trunk;

import com.google.common.collect.Lists;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import com.pasterdream.pasterdreammod.worldgen.tree.DyedreamTreePlacers;

import java.util.List;
import java.util.function.BiConsumer;

/**
 * 染梦巨型多柱主干生成器
 * <p>
 * 生成 2x2 主柱 + 随机外柱，适合巨型树变体。
 */
public class DyedreamMegaTrunkPlacer extends TrunkPlacer {

    public static final MapCodec<DyedreamMegaTrunkPlacer> CODEC = RecordCodecBuilder.mapCodec(instance ->
            trunkPlacerParts(instance).apply(instance, DyedreamMegaTrunkPlacer::new));

    /**
     * 构造巨型主干生成器
     *
     * @param baseHeight  基础高度
     * @param heightRandA 高度随机参数 A
     * @param heightRandB 高度随机参数 B
     */
    public DyedreamMegaTrunkPlacer(int baseHeight, int heightRandA, int heightRandB) {
        super(baseHeight, heightRandA, heightRandB);
    }

    @Override
    protected TrunkPlacerType<?> type() {
        return DyedreamTreePlacers.MEGA_TRUNK_PLACER.get();
    }

    @Override
    public List<FoliagePlacer.FoliageAttachment> placeTrunk(LevelSimulatedReader level, BiConsumer<BlockPos, BlockState> blockSetter,
                                                          RandomSource random, int freeTreeHeight, BlockPos pos,
                                                          TreeConfiguration config) {
        List<FoliagePlacer.FoliageAttachment> list = Lists.newArrayList();
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

        // 2x2 主柱
        for (int x = 0; x < 2; x++) {
            for (int z = 0; z < 2; z++) {
                for (int y = 0; y < freeTreeHeight; y++) {
                    mutable.set(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
                    this.placeLog(level, blockSetter, random, mutable, config);
                }
            }
        }

        list.add(new FoliagePlacer.FoliageAttachment(pos.above(freeTreeHeight).offset(1, 0, 1), 0, false));

        // 随机外柱
        int extraPillars = 2 + random.nextInt(3);
        for (int i = 0; i < extraPillars; i++) {
            Direction dir = Direction.Plane.HORIZONTAL.getRandomDirection(random);
            int height = freeTreeHeight / 2 + random.nextInt(freeTreeHeight / 2);
            BlockPos pillarBase = pos.relative(dir, 2);
            for (int y = 0; y < height; y++) {
                mutable.set(pillarBase.getX(), pillarBase.getY() + y, pillarBase.getZ());
                this.placeLog(level, blockSetter, random, mutable, config);
            }
            list.add(new FoliagePlacer.FoliageAttachment(pillarBase.above(height), 0, false));
        }

        return list;
    }
}
