package com.pasterdream.pasterdreammod.worldgen.tree.trunk;

import com.google.common.collect.Lists;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.pasterdream.pasterdreammod.worldgen.tree.DyedreamTreePlacers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * 染梦世界树主干生成器（宏大化版本）
 * <p>
 * 生成 5x5 主柱 + 角柱 + 多层大型侧枝，用于“世界树”级别的地标巨木。
 * 参考 Eternal Starlight 中 BranchingTrunkPlacer 的 Bresenham 3D 分支算法，
 * 但规模进一步放大：高度可达 55~75 格，主柱占地 5x5，树冠半径可达 10~12 格。
 */
public class DyedreamWorldTreeTrunkPlacer extends TrunkPlacer {

    public static final MapCodec<DyedreamWorldTreeTrunkPlacer> CODEC = RecordCodecBuilder.mapCodec(instance ->
            trunkPlacerParts(instance).apply(instance, DyedreamWorldTreeTrunkPlacer::new));

    /**
     * 构造世界树主干生成器
     *
     * @param baseHeight  基础高度
     * @param heightRandA 高度随机参数 A
     * @param heightRandB 高度随机参数 B
     */
    public DyedreamWorldTreeTrunkPlacer(int baseHeight, int heightRandA, int heightRandB) {
        super(baseHeight, heightRandA, heightRandB);
    }

    @Override
    protected TrunkPlacerType<?> type() {
        return DyedreamTreePlacers.WORLD_TREE_TRUNK_PLACER.get();
    }

    @Override
    public List<FoliagePlacer.FoliageAttachment> placeTrunk(LevelSimulatedReader level, BiConsumer<BlockPos, BlockState> blockSetter,
                                                          RandomSource random, int freeTreeHeight, BlockPos pos,
                                                          TreeConfiguration config) {
        List<FoliagePlacer.FoliageAttachment> list = Lists.newArrayList();
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

        // 5x5 主柱，让树干更加雄伟
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                // 削去四个极端角，形成八角形粗柱
                if (Math.abs(x) == 2 && Math.abs(z) == 2) {
                    continue;
                }
                for (int y = 0; y < freeTreeHeight; y++) {
                    mutable.set(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
                    this.placeLog(level, blockSetter, random, mutable, config);
                }
            }
        }

        // 顶部主树冠锚点（位于主柱几何中心上方）
        list.add(new FoliagePlacer.FoliageAttachment(pos.above(freeTreeHeight), 0, false));

        // 四角副柱，高度为主柱的 1/2 ~ 4/5，围绕主柱形成塔楼式结构
        int cornerCount = 5 + random.nextInt(3);
        for (int i = 0; i < cornerCount; i++) {
            Direction dir = Direction.Plane.HORIZONTAL.getRandomDirection(random);
            int cornerHeight = freeTreeHeight / 2 + random.nextInt(freeTreeHeight / 3);
            int cwOffset = random.nextInt(3) - 1; // -1, 0, 1
            BlockPos cornerBase = pos.relative(dir, 3).relative(dir.getClockWise(), cwOffset);

            // 副柱自身为 2x2 小柱
            for (int dx = 0; dx <= 1; dx++) {
                for (int dz = 0; dz <= 1; dz++) {
                    for (int y = 0; y < cornerHeight; y++) {
                        mutable.set(cornerBase.getX() + dx, cornerBase.getY() + y, cornerBase.getZ() + dz);
                        this.placeLog(level, blockSetter, random, mutable, config);
                    }
                }
            }
            list.add(new FoliagePlacer.FoliageAttachment(cornerBase.above(cornerHeight).offset(1, 0, 1), 0, false));
        }

        // 中层大型侧枝，从 1/4 高度开始向四周伸出，使用 Bresenham 3D 算法生成自然弯曲分支
        int branchLayers = 4 + random.nextInt(3);
        for (int layer = 0; layer < branchLayers; layer++) {
            int branchStart = freeTreeHeight / 4 + layer * (freeTreeHeight / 10) + random.nextInt(5);
            if (branchStart >= freeTreeHeight - 5) continue;

            Direction dir = Direction.Plane.HORIZONTAL.getRandomDirection(random);
            int branchLength = 6 + random.nextInt(7);
            BlockPos startPos = pos.offset(random.nextInt(3) - 1, branchStart, random.nextInt(3) - 1);
            BlockPos endPos = startPos.relative(dir, branchLength)
                    .above(2 + random.nextInt(4))
                    .relative(dir.getClockWise(), random.nextInt(5) - 2);

            List<BlockPos> branchPoints = getBresenham3D(startPos, endPos);
            for (BlockPos branchPos : branchPoints) {
                this.placeLog(level, blockSetter, random, branchPos, config);
            }

            if (!branchPoints.isEmpty()) {
                BlockPos tip = branchPoints.get(branchPoints.size() - 1);
                list.add(new FoliagePlacer.FoliageAttachment(tip.above(), 0, false));

                // 在分支末端再分出 1~2 个小叉
                int subBranches = 1 + random.nextInt(2);
                for (int s = 0; s < subBranches; s++) {
                    Direction subDir = Direction.Plane.HORIZONTAL.getRandomDirection(random);
                    BlockPos subEnd = tip.relative(subDir, 2 + random.nextInt(3)).above(random.nextInt(2));
                    for (BlockPos subPos : getBresenham3D(tip, subEnd)) {
                        this.placeLog(level, blockSetter, random, subPos, config);
                    }
                    list.add(new FoliagePlacer.FoliageAttachment(subEnd.above(), 0, false));
                }
            }
        }

        // 顶层辐射状分支，从树冠下方呈伞骨状展开
        int topBranchCount = 6 + random.nextInt(4);
        int topStart = freeTreeHeight - 6;
        for (int i = 0; i < topBranchCount; i++) {
            Direction dir = Direction.Plane.HORIZONTAL.getRandomDirection(random);
            int topLength = 4 + random.nextInt(5);
            BlockPos startPos = pos.above(topStart).offset(random.nextInt(3) - 1, 0, random.nextInt(3) - 1);
            BlockPos endPos = startPos.relative(dir, topLength).above(1 + random.nextInt(3));

            List<BlockPos> topPoints = getBresenham3D(startPos, endPos);
            for (BlockPos branchPos : topPoints) {
                this.placeLog(level, blockSetter, random, branchPos, config);
            }
            if (!topPoints.isEmpty()) {
                list.add(new FoliagePlacer.FoliageAttachment(topPoints.get(topPoints.size() - 1).above(), 0, false));
            }
        }

        return list;
    }

    /**
     * 使用 Bresenham 3D 算法计算两个方块坐标之间的离散点集，
     * 用于生成自然弯曲的粗壮树枝。
     *
     * @param start 起始坐标
     * @param end   结束坐标
     * @return 按顺序排列的坐标点列表（包含两端点）
     */
    private static List<BlockPos> getBresenham3D(BlockPos start, BlockPos end) {
        List<BlockPos> points = new ArrayList<>();
        int x1 = start.getX();
        int y1 = start.getY();
        int z1 = start.getZ();
        int x2 = end.getX();
        int y2 = end.getY();
        int z2 = end.getZ();

        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int dz = Math.abs(z2 - z1);
        int xs = x2 > x1 ? 1 : -1;
        int ys = y2 > y1 ? 1 : -1;
        int zs = z2 > z1 ? 1 : -1;

        if (dx >= dy && dx >= dz) {
            int p1 = 2 * dy - dx;
            int p2 = 2 * dz - dx;
            while (x1 != x2) {
                points.add(new BlockPos(x1, y1, z1));
                x1 += xs;
                if (p1 >= 0) {
                    y1 += ys;
                    p1 -= 2 * dx;
                }
                if (p2 >= 0) {
                    z1 += zs;
                    p2 -= 2 * dx;
                }
                p1 += 2 * dy;
                p2 += 2 * dz;
            }
        } else if (dy >= dx && dy >= dz) {
            int p1 = 2 * dx - dy;
            int p2 = 2 * dz - dy;
            while (y1 != y2) {
                points.add(new BlockPos(x1, y1, z1));
                y1 += ys;
                if (p1 >= 0) {
                    x1 += xs;
                    p1 -= 2 * dy;
                }
                if (p2 >= 0) {
                    z1 += zs;
                    p2 -= 2 * dy;
                }
                p1 += 2 * dx;
                p2 += 2 * dz;
            }
        } else {
            int p1 = 2 * dy - dz;
            int p2 = 2 * dx - dz;
            while (z1 != z2) {
                points.add(new BlockPos(x1, y1, z1));
                z1 += zs;
                if (p1 >= 0) {
                    y1 += ys;
                    p1 -= 2 * dz;
                }
                if (p2 >= 0) {
                    x1 += xs;
                    p2 -= 2 * dz;
                }
                p1 += 2 * dy;
                p2 += 2 * dx;
            }
        }
        points.add(new BlockPos(x1, y1, z1));
        return points;
    }
}
