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
import com.pasterdream.pasterdreammod.api.worldgen.WorldGenUtils;
import com.pasterdream.pasterdreammod.worldgen.tree.DyedreamTreePlacers;

import java.util.List;
import java.util.function.BiConsumer;

/**
 * 染梦分叉主干生成器
 * <p>
 * 生成一根主干并在中上部分出 2~3 个侧枝，侧枝末端产生 foliage attachment。
 */
public class DyedreamBranchingTrunkPlacer extends TrunkPlacer {

    public static final MapCodec<DyedreamBranchingTrunkPlacer> CODEC = RecordCodecBuilder.mapCodec(instance ->
            trunkPlacerParts(instance).apply(instance, DyedreamBranchingTrunkPlacer::new));

    /**
     * 构造分叉主干生成器
     *
     * @param baseHeight  基础高度
     * @param heightRandA 高度随机参数 A
     * @param heightRandB 高度随机参数 B
     */
    public DyedreamBranchingTrunkPlacer(int baseHeight, int heightRandA, int heightRandB) {
        super(baseHeight, heightRandA, heightRandB);
    }

    @Override
    protected TrunkPlacerType<?> type() {
        return DyedreamTreePlacers.BRANCHING_TRUNK_PLACER.get();
    }

    /**
     * 扩展有效位置判定 —— 越出当前世界生成可写范围的方块位置直接判定为无效
     * <p>
     * 侧枝可伸出数格，在区块边缘生成时会跨越到尚未就绪的相邻区块，触发
     * "Detected setBlock in a far chunk" 错误刷屏；此处提前过滤，越界位置不放置。
     *
     * @param level 模拟世界读取器
     * @param pos   目标位置
     * @return true 表示该位置有效且可安全写入
     */
    @Override
    protected boolean validTreePos(LevelSimulatedReader level, BlockPos pos) {
        return super.validTreePos(level, pos) && WorldGenUtils.canPlaceInRegion(level, pos);
    }

    @Override
    public List<FoliagePlacer.FoliageAttachment> placeTrunk(LevelSimulatedReader level, BiConsumer<BlockPos, BlockState> blockSetter,
                                                          RandomSource random, int freeTreeHeight, BlockPos pos,
                                                          TreeConfiguration config) {
        List<FoliagePlacer.FoliageAttachment> list = Lists.newArrayList();
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

        // 主杆
        for (int y = 0; y < freeTreeHeight; y++) {
            mutable.set(pos.getX(), pos.getY() + y, pos.getZ());
            this.placeLog(level, blockSetter, random, mutable, config);
        }

        list.add(new FoliagePlacer.FoliageAttachment(pos.above(freeTreeHeight), 0, false));

        // 侧枝
        int branchCount = 2 + random.nextInt(2);
        for (int i = 0; i < branchCount; i++) {
            Direction dir = Direction.Plane.HORIZONTAL.getRandomDirection(random);
            int branchStart = freeTreeHeight / 2 + random.nextInt(freeTreeHeight / 3);
            int branchLength = 2 + random.nextInt(3);
            BlockPos.MutableBlockPos branchPos = new BlockPos.MutableBlockPos(pos.getX(), pos.getY() + branchStart, pos.getZ());

            for (int j = 0; j < branchLength; j++) {
                branchPos.move(dir);
                branchPos.move(Direction.UP);
                this.placeLog(level, blockSetter, random, branchPos, config);
            }

            list.add(new FoliagePlacer.FoliageAttachment(branchPos.above(), 0, false));
        }

        return list;
    }
}
