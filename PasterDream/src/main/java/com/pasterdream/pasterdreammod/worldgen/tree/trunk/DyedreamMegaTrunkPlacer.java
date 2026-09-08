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

    /**
     * 扩展有效位置判定 —— 越出当前世界生成可写范围的方块位置直接判定为无效
     * <p>
     * 巨型多柱主干横向跨度大，在区块边缘生成时会跨越到尚未就绪的相邻区块，触发
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

        // 地面支撑检测：扫描 2x2 主柱 + 外柱（偏移2）覆盖范围内的最低地面 Y，
        // 若最低地面低于树根点，向下延伸主柱填补悬空
        int footprintRadius = 2; // 主柱范围 + 外柱偏移2
        int minGroundY = findMinGroundY(level, pos, footprintRadius);
        if (minGroundY < pos.getY()) {
            fillTrunkGap(level, blockSetter, random, pos, minGroundY, config);
        }

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

    /**
     * 扫描主柱足迹范围内所有列的最低地面 Y（地面上方可放置位置）
     * <p>
     * 扫描范围覆盖2x2主柱与外柱（偏移2），即中心 ± {@code radius}。
     * 逐列向下搜索，返回所有列中最低的地面 Y+1。
     *
     * @param level  模拟世界读取器
     * @param pos    树根中心点
     * @param radius 扫描半径
     * @return 足迹范围内最低的地面放置 Y，或 Integer.MIN_VALUE 表示无地面
     */
    private static int findMinGroundY(LevelSimulatedReader level, BlockPos pos, int radius) {
        int minGroundY = Integer.MAX_VALUE;
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                for (int dy = 0; dy <= 40; dy++) {
                    mutable.set(pos.getX() + dx, pos.getY() - dy, pos.getZ() + dz);
                    if (isSolidGround(level, mutable)) {
                        int groundY = mutable.getY() + 1;
                        if (groundY < minGroundY) {
                            minGroundY = groundY;
                        }
                        break;
                    }
                }
            }
        }
        return minGroundY == Integer.MAX_VALUE ? Integer.MIN_VALUE : minGroundY;
    }

    /**
     * 判断指定位置是否为可承托树干的固体地面
     * <p>
     * 排除空气、树叶、植被类可替换方块，要求方块遮挡光线（实心不透明方块）。
     * 使用 {@link net.minecraft.world.level.block.state.BlockState#canOcclude()} 判断，
     * 无需 {@link net.minecraft.world.level.BlockGetter} 参数，兼容 {@link LevelSimulatedReader}。
     *
     * @param level 模拟世界读取器
     * @param pos   检测位置
     * @return true 表示该位置是固体地面
     */
    private static boolean isSolidGround(LevelSimulatedReader level, BlockPos pos) {
        return level.isStateAtPosition(pos, state -> {
            if (state.isAir()) return false;
            if (state.is(net.minecraft.tags.BlockTags.LEAVES)
                    || state.is(net.minecraft.tags.BlockTags.REPLACEABLE_BY_TREES)) return false;
            return state.canOcclude();
        });
    }

    /**
     * 填补主柱底部悬空：从最低地面向上放置原木至 pos.Y()，消除树干与地面之间的空隙
     * <p>
     * 仅填充2x2主柱范围，外柱由各自的放置逻辑独立处理。
     * 仅替换空气方块，不破坏已有地形方块。
     *
     * @param level      模拟世界读取器
     * @param blockSetter 方块放置回调
     * @param random     随机源
     * @param pos        树根中心点
     * @param groundY    最低地面 Y（地面上方可放置位置）
     * @param config     树配置
     */
    private static void fillTrunkGap(LevelSimulatedReader level, BiConsumer<BlockPos, BlockState> blockSetter,
                                     RandomSource random, BlockPos pos, int groundY, TreeConfiguration config) {
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        for (int x = 0; x < 2; x++) {
            for (int z = 0; z < 2; z++) {
                for (int y = groundY; y < pos.getY(); y++) {
                    mutable.set(pos.getX() + x, y, pos.getZ() + z);
                    if (level.isStateAtPosition(mutable, BlockState::isAir)) {
                        blockSetter.accept(mutable.immutable(), config.trunkProvider.getState(random, mutable));
                    }
                }
            }
        }
    }
}
