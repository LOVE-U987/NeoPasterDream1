package com.pasterdream.pasterdreammod.worldgen.tree.trunk;

import com.google.common.collect.Lists;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.pasterdream.pasterdreammod.api.worldgen.WorldGenUtils;
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
 * 染梦超巨型主干生成器（强化版）
 * <p>
 * 生成 3x3 主柱 + 角柱 + 多层 Bresenham 3D 自然侧枝，用于“世界树”级别的地标巨木。
 * 参考 Eternal Starlight 中 BranchingTrunkPlacer 的分支生成思路，
 * 规模放大：高度可达 36~52 格，主柱占地 3x3，分支更长更自然。
 */
public class DyedreamColossalTrunkPlacer extends TrunkPlacer {

    public static final MapCodec<DyedreamColossalTrunkPlacer> CODEC = RecordCodecBuilder.mapCodec(instance ->
            trunkPlacerParts(instance).apply(instance, DyedreamColossalTrunkPlacer::new));

    /**
     * 构造超巨型主干生成器
     *
     * @param baseHeight  基础高度
     * @param heightRandA 高度随机参数 A
     * @param heightRandB 高度随机参数 B
     */
    public DyedreamColossalTrunkPlacer(int baseHeight, int heightRandA, int heightRandB) {
        super(baseHeight, heightRandA, heightRandB);
    }

    @Override
    protected TrunkPlacerType<?> type() {
        return DyedreamTreePlacers.COLOSSAL_TRUNK_PLACER.get();
    }

    /**
     * 扩展有效位置判定 —— 越出当前世界生成可写范围的方块位置直接判定为无效
     * <p>
     * 超巨型主干含 3x3 主柱、角柱与多层长侧枝，横向跨度可达 10+ 格，在区块边缘
     * 生成时会跨越到尚未就绪的相邻区块，触发 "Detected setBlock in a far chunk"
     * 错误刷屏；此处提前过滤，越界位置不放置。
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

        // 3x3 主柱
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                for (int y = 0; y < freeTreeHeight; y++) {
                    mutable.set(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
                    this.placeLog(level, blockSetter, random, mutable, config);
                }
            }
        }

        // 顶部主树冠锚点
        list.add(new FoliagePlacer.FoliageAttachment(pos.above(freeTreeHeight), 0, false));

        // 四角副柱，高度为主柱的 1/2 ~ 3/4
        int cornerCount = 3 + random.nextInt(2);
        for (int i = 0; i < cornerCount; i++) {
            Direction dir = Direction.Plane.HORIZONTAL.getRandomDirection(random);
            int cornerHeight = freeTreeHeight / 2 + random.nextInt(freeTreeHeight / 3);
            int cwOffset = random.nextInt(3) - 1;
            BlockPos cornerBase = pos.relative(dir, 2).relative(dir.getClockWise(), cwOffset);

            for (int y = 0; y < cornerHeight; y++) {
                mutable.set(cornerBase.getX(), cornerBase.getY() + y, cornerBase.getZ());
                this.placeLog(level, blockSetter, random, mutable, config);
            }
            list.add(new FoliagePlacer.FoliageAttachment(cornerBase.above(cornerHeight), 0, false));
        }

        // 中层大型侧枝，从 1/3 高度开始向四周伸出，使用 Bresenham 3D 算法
        int branchLayers = 3 + random.nextInt(2);
        for (int layer = 0; layer < branchLayers; layer++) {
            int branchStart = freeTreeHeight / 3 + layer * (freeTreeHeight / 8) + random.nextInt(4);
            if (branchStart >= freeTreeHeight - 3) continue;

            Direction dir = Direction.Plane.HORIZONTAL.getRandomDirection(random);
            int branchLength = 5 + random.nextInt(6);
            BlockPos startPos = pos.offset(random.nextInt(2), branchStart, random.nextInt(2));
            BlockPos endPos = startPos.relative(dir, branchLength)
                    .above(2 + random.nextInt(3))
                    .relative(dir.getClockWise(), random.nextInt(4) - 2);

            List<BlockPos> branchPoints = getBresenham3D(startPos, endPos);
            for (BlockPos branchPos : branchPoints) {
                this.placeLog(level, blockSetter, random, branchPos, config);
            }

            if (!branchPoints.isEmpty()) {
                BlockPos tip = branchPoints.get(branchPoints.size() - 1);
                list.add(new FoliagePlacer.FoliageAttachment(tip.above(), 0, false));

                // 随机在分支末端再分一个小叉
                if (random.nextBoolean()) {
                    Direction subDir = Direction.Plane.HORIZONTAL.getRandomDirection(random);
                    BlockPos subEnd = tip.relative(subDir, 2 + random.nextInt(3)).above(random.nextInt(2));
                    for (BlockPos subPos : getBresenham3D(tip, subEnd)) {
                        this.placeLog(level, blockSetter, random, subPos, config);
                    }
                    list.add(new FoliagePlacer.FoliageAttachment(subEnd.above(), 0, false));
                }
            }
        }

        // 顶层短枝，丰富树冠下方层次
        int topBranchCount = 4 + random.nextInt(3);
        int topStart = freeTreeHeight - 5;
        for (int i = 0; i < topBranchCount; i++) {
            Direction dir = Direction.Plane.HORIZONTAL.getRandomDirection(random);
            int topLength = 3 + random.nextInt(4);
            BlockPos startPos = pos.above(topStart).offset(random.nextInt(2), 0, random.nextInt(2));
            BlockPos endPos = startPos.relative(dir, topLength).above(1 + random.nextInt(2));

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
     * 使用 Bresenham 3D 算法计算两个方块坐标之间的离散点集，生成自然弯曲树枝。
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
