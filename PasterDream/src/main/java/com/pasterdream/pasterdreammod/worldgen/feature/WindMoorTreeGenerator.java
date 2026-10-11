package com.pasterdream.pasterdreammod.worldgen.feature;

import com.pasterdream.pasterdreammod.api.worldgen.WorldGenUtils;
import com.pasterdream.pasterdreammod.api.worldgen.decor.DecorationConfig;
import com.pasterdream.pasterdreammod.api.worldgen.decor.ICustomDecorationGenerator;
import com.pasterdream.pasterdreammod.registry.PDBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

/**
 * 风之树生成器 —— 程序化、自适应、可参数化变种的风泊树
 * <p>
 * 以「染梦世界地物为基准」的程序化实现：不依赖结构 NBT，而是按参数在任意地形上
 * 即时生长，因此天然具备自适应（贴合地表、可替换判定）与自变种（高度/枝节/树冠半径
 * 均由构造参数控制）的能力。
 * <p>
 * 形态构成：
 * <ol>
 *   <li>主干：风泊原木竖直生长，高度由参数区间随机</li>
 *   <li>枝节：在主干 40%~85% 高度区间分层，每层向随机方向伸出上扬枝条，枝端结小叶簇</li>
 *   <li>树冠：顶端多层椭球叶簇，外层保留孔洞避免实心球观感</li>
 *   <li>垂吊植被：枝端与树冠下方按概率挂风泊悬挂藤（后续由随机刻向下生长图藤）</li>
 * </ol>
 * 由 {@code WindMoorTrees} 注册 18 种参数组合（大中小 × 低中高 × 多/少枝节）。
 *
 * @author PasterDream Team
 */
public class WindMoorTreeGenerator implements ICustomDecorationGenerator {

    /** 叶簇内层留孔概率（避免实心球） */
    private static final double LEAF_HOLLOW_CHANCE = 0.45;

    /** 叶簇外层随机缺失概率（形成蓬松边缘） */
    private static final double LEAF_SKIP_CHANCE = 0.18;

    /** 枝节起始高度占主干高度的比例 */
    private static final double BRANCH_START_RATIO = 0.40;

    /** 枝节终止高度占主干高度的比例 */
    private static final double BRANCH_END_RATIO = 0.85;

    /** 主干高度下限 */
    private final int minHeight;

    /** 主干高度上限 */
    private final int maxHeight;

    /** 枝节层数（多枝节 > 少枝节） */
    private final int branchLayers;

    /** 树冠基准半径 */
    private final int canopyRadius;

    /** 垂吊植被概率（0~1） */
    private final float hangingChance;

    /**
     * 构造风之树生成器
     *
     * @param minHeight     主干高度下限
     * @param maxHeight     主干高度上限
     * @param branchLayers  枝节层数
     * @param canopyRadius  树冠基准半径
     * @param hangingChance 垂吊植被概率（0~1）
     */
    public WindMoorTreeGenerator(int minHeight, int maxHeight, int branchLayers, int canopyRadius,
                                 float hangingChance) {
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
        this.branchLayers = branchLayers;
        this.canopyRadius = canopyRadius;
        this.hangingChance = hangingChance;
    }

    @Override
    public boolean generate(FeaturePlaceContext<DecorationConfig> context) {
        DecorationConfig config = context.config();
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        BlockPos origin = context.origin();

        // 贴合地形：取该列地表作为落脚点，并要求下方为实心、落脚点可替换
        int surfaceY = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, origin.getX(), origin.getZ());
        BlockPos base = new BlockPos(origin.getX(), surfaceY, origin.getZ());
        BlockPos ground = base.below();
        if (!level.getBlockState(ground).isSolidRender(level, ground)) {
            return false;
        }
        if (!WorldGenUtils.isReplaceable(level, config.replaceable(), base)) {
            return false;
        }

        int height = minHeight + random.nextInt(Math.max(1, maxHeight - minHeight + 1));
        if (!placeTrunk(level, config, origin, base, height)) {
            return false;
        }

        placeBranches(level, config, random, origin, base, height);
        placeCanopy(level, config, random, origin, base, height);
        return true;
    }

    /**
     * 生长主干（竖直风泊原木）
     *
     * @param level  世界生成级别
     * @param config 装饰配置（含可替换判定）
     * @param origin 生成原点
     * @param base   落脚点
     * @param height 主干高度
     * @return 至少放置了一格返回 true
     */
    private boolean placeTrunk(WorldGenLevel level, DecorationConfig config, BlockPos origin,
                               BlockPos base, int height) {
        boolean placed = false;
        for (int i = 0; i < height; i++) {
            BlockPos pos = base.above(i);
            if (!WorldGenUtils.isWithinGenerationBounds(origin, pos)) {
                break;
            }
            if (!WorldGenUtils.isReplaceable(level, config.replaceable(), pos)) {
                break;
            }
            level.setBlock(pos, PDBlocks.WINDMOOR_LOG.get().defaultBlockState(), 3);
            placed = true;
        }
        return placed;
    }

    /**
     * 生长枝节 —— 分层向随机方向伸出上扬枝条，枝端结叶簇并可能挂垂吊植被
     *
     * @param level   世界生成级别
     * @param config  装饰配置
     * @param random  随机源
     * @param origin  生成原点
     * @param base    落脚点
     * @param height  主干高度
     */
    private void placeBranches(WorldGenLevel level, DecorationConfig config, RandomSource random,
                               BlockPos origin, BlockPos base, int height) {
        int startIndex = Math.max(1, (int) (height * BRANCH_START_RATIO));
        int endIndex = Math.max(startIndex, (int) (height * BRANCH_END_RATIO));
        int span = Math.max(1, endIndex - startIndex);

        for (int layer = 0; layer < branchLayers; layer++) {
            int trunkIndex = startIndex + (int) Math.round((double) span * layer / Math.max(1, branchLayers - 1));
            trunkIndex = Math.min(trunkIndex, height - 2);
            if (trunkIndex < 1) {
                continue;
            }
            BlockPos attach = base.above(trunkIndex);

            // 每层两条朝相反方向的枝条，形成对称但长度随机的枝架
            double baseAngle = random.nextDouble() * Math.PI * 2;
            for (int side = 0; side < 2; side++) {
                double angle = baseAngle + side * Math.PI + (random.nextDouble() - 0.5) * 0.6;
                int length = 2 + random.nextInt(3);
                BlockPos tip = attach;
                for (int step = 1; step <= length; step++) {
                    int bx = attach.getX() + (int) Math.round(Math.cos(angle) * step);
                    int bz = attach.getZ() + (int) Math.round(Math.sin(angle) * step);
                    int by = attach.getY() + (step >= 2 ? 1 : 0);
                    BlockPos pos = new BlockPos(bx, by, bz);
                    if (!WorldGenUtils.isWithinGenerationBounds(origin, pos)) {
                        break;
                    }
                    if (!WorldGenUtils.isReplaceable(level, config.replaceable(), pos)) {
                        break;
                    }
                    level.setBlock(pos, PDBlocks.WINDMOOR_LOG.get().defaultBlockState(), 3);
                    tip = pos;
                }
                // 枝端叶簇 + 垂吊植被
                placeLeafBlob(level, config, random, origin, tip, 1 + random.nextInt(2));
                placeHangingVine(level, random, tip);
            }
        }
    }

    /**
     * 生长树冠 —— 顶端多层椭球叶簇，半径逐层收缩
     *
     * @param level  世界生成级别
     * @param config 装饰配置
     * @param random 随机源
     * @param origin 生成原点
     * @param base   落脚点
     * @param height 主干高度
     */
    private void placeCanopy(WorldGenLevel level, DecorationConfig config, RandomSource random,
                             BlockPos origin, BlockPos base, int height) {
        for (int layer = 0; layer < 3; layer++) {
            int radius = Math.max(1, canopyRadius - layer);
            BlockPos center = base.above(height + layer - 1);
            placeLeafBlob(level, config, random, origin, center, radius);
        }
        // 树冠下方也可能挂垂吊植被
        placeHangingVine(level, random, base.above(height - 1));
    }

    /**
     * 生成球形叶簇（椭球 + 内层留孔 + 外层随机缺失）
     *
     * @param level  世界生成级别
     * @param config 装饰配置
     * @param random 随机源
     * @param origin 生成原点
     * @param center 球心
     * @param radius 半径
     */
    private void placeLeafBlob(WorldGenLevel level, DecorationConfig config, RandomSource random,
                               BlockPos origin, BlockPos center, int radius) {
        int r = Math.max(1, radius);
        double outerSq = (r + 0.5) * (r + 0.5);
        double innerSq = (r - 1) * (r - 1);
        for (int dx = -r; dx <= r; dx++) {
            for (int dy = -r; dy <= r; dy++) {
                for (int dz = -r; dz <= r; dz++) {
                    double distSq = dx * dx + dy * dy + dz * dz;
                    if (distSq > outerSq) {
                        continue;
                    }
                    if (distSq < innerSq && random.nextDouble() < LEAF_HOLLOW_CHANCE) {
                        continue;
                    }
                    if (random.nextDouble() < LEAF_SKIP_CHANCE) {
                        continue;
                    }
                    BlockPos pos = center.offset(dx, dy, dz);
                    if (!WorldGenUtils.isWithinGenerationBounds(origin, pos)) {
                        continue;
                    }
                    if (!WorldGenUtils.isReplaceable(level, config.replaceable(), pos)) {
                        continue;
                    }
                    level.setBlock(pos, PDBlocks.WINDMOOR_LEAVES.get().defaultBlockState(), 3);
                }
            }
        }
    }

    /**
     * 挂垂吊植被 —— 风泊悬挂藤要求上方为原木/树叶等支撑面
     * <p>
     * 该方块不可自身上下堆叠（生存判定看上方的支撑面），故只挂一格；
     * 后续由随机刻自行向下生长图藤，形成垂吊层次。
     *
     * @param level   世界生成级别
     * @param random  随机源
     * @param support 支撑方块位置（原木枝端或树叶）
     */
    private void placeHangingVine(WorldGenLevel level, RandomSource random, BlockPos support) {
        if (random.nextFloat() >= hangingChance) {
            return;
        }
        BlockPos pos = support.below();
        if (!level.getBlockState(pos).isAir()) {
            return;
        }
        level.setBlock(pos, PDBlocks.WINDMOOR_HANGING_VINE.get().defaultBlockState(), 3);
    }
}
