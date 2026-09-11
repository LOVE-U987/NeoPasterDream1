package com.pasterdream.pasterdreammod.worldgen.feature;

import com.pasterdream.pasterdreammod.api.worldgen.WorldGenUtils;
import com.pasterdream.pasterdreammod.registry.PDBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * 发光菌体特征 —— 在地下 Y=-32~0 区域生成发光的粉色菌群
 * <p>
 * 算法复用了 {@link PinkagaricClusterFeature} 的设计思路，
 * 但专门为地下洞穴环境做了适配：
 * <ol>
 *   <li>在洞穴天花板或墙壁上生成（利用洞穴已有空间）</li>
 *   <li>群落半径 3~6 格，密度 40~70%</li>
 *   <li>使用 pinkagaric 方块变体（0=伞面, 1=菌柄, 2=菌褶, 3=荧光）</li>
 *   <li>发光菌伞（pinkagaric_3, light=15）提供光源</li>
 *   <li>空间检测：整朵蘑菇（菌柄+菌伞+伞沿）预检全部通过才放置，空间不足时自适应缩短菌柄重试，最矮仍放不下则放弃</li>
 *   <li>遗迹避让：附近区块引用已注册遗迹时跳过生成（{@link WorldGenUtils#isNearRegisteredRuin}）</li>
 * </ol>
 * <p>
 * 设计参考 PinkagaricClusterFeature 的平顶草帽形蘑菇生成算法，
 * 但做了洞穴专用的简化：更紧凑、更矮小、发光比例更高。
 *
 * @author PasterDream Team
 */
public class CaveGlowMushroomFeature extends Feature<NoneFeatureConfiguration> {

    /** Y 范围下限 */
    private static final int MIN_Y = -32;

    /** Y 范围上限 */
    private static final int MAX_Y = 0;

    /** 群落半径范围 */
    private static final int CLUSTER_RADIUS_MIN = 3;

    /** 群落半径范围 */
    private static final int CLUSTER_RADIUS_MAX = 6;

    /** 群落密度范围 (0~1) */
    private static final double DENSITY_MIN = 0.4;

    /** 群落密度范围 (0~1) */
    private static final double DENSITY_MAX = 0.7;

    /** 菌柄高度范围 */
    private static final int STEM_HEIGHT_MIN = 1;

    /** 菌柄高度范围 */
    private static final int STEM_HEIGHT_MAX = 3;

    /** 伞面宽度 */
    private static final int CAP_WIDTH = 3;

    /** 发光菌伞概率替换 */
    private static final float GLOW_CAP_CHANCE = 0.3f;

    public CaveGlowMushroomFeature() {
        super(NoneFeatureConfiguration.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource random = context.random();

        // 遗迹避让：附近区块引用了已注册遗迹时跳过生成，避免蘑菇长进地下遗迹
        if (WorldGenUtils.isNearRegisteredRuin(level, origin)) {
            return false;
        }

        // 确保在目标 Y 范围内
        if (origin.getY() < MIN_Y || origin.getY() > MAX_Y) {
            return false;
        }

        // 找附着面（优先找天花板，其次找墙壁）
        int attachY = findAttachY(level, origin, random);
        if (attachY == Integer.MIN_VALUE) {
            return false;
        }

        // 确定群落参数
        int clusterRadius = CLUSTER_RADIUS_MIN + random.nextInt(CLUSTER_RADIUS_MAX - CLUSTER_RADIUS_MIN + 1);
        double density = DENSITY_MIN + random.nextDouble() * (DENSITY_MAX - DENSITY_MIN);

        Set<BlockPos> allPlaced = new HashSet<>();
        boolean placedAny = false;

        // 在群落半径内散布蘑菇
        for (int dx = -clusterRadius; dx <= clusterRadius; dx++) {
            for (int dz = -clusterRadius; dz <= clusterRadius; dz++) {
                double distSq = dx * dx + dz * dz;
                if (distSq > clusterRadius * clusterRadius) continue;

                // 密度控制：越靠近中心密度越高
                double localDensity = density * (1.0 - distSq / (clusterRadius * clusterRadius) * 0.5);
                if (random.nextDouble() > localDensity) continue;

                // 找该位置的实际附着面
                BlockPos mushroomPos = new BlockPos(origin.getX() + dx, attachY, origin.getZ() + dz);
                int localAttachY = findLocalAttachY(level, mushroomPos, random);
                if (localAttachY == Integer.MIN_VALUE) continue;

                // 生成一朵小蘑菇（悬挂式：菌柄向下生长）
                boolean placed = placeHangingMushroom(level, random, mushroomPos.getX(), localAttachY,
                        mushroomPos.getZ(), allPlaced);
                if (placed) placedAny = true;
            }
        }

        return placedAny;
    }

    /**
     * 生成一朵悬挂式小蘑菇（从天花板向下生长）
     * <p>
     * 采用"计划 → 提交"两阶段放置 + 自适应降级：
     * <ol>
     *   <li>从随机柄高开始，逐级降级到最低柄高，找到第一个能完整放下的高度</li>
     *   <li>预检整朵蘑菇（菌柄路径 + 菌伞 3x3 + 伞沿）的全部目标格均可替换</li>
     *   <li>全部通过才一次性放置；任一格不满足则放弃当前高度，最矮仍放不下则整朵放弃</li>
     * </ol>
     * 结构（反转的平顶草帽形）：
     * - 菌柄：1x1 pinkagaric_1，从附着面下方 1 格向下 stemHeight 格
     * - 菌伞底面：3x3 pinkagaric_0，在菌柄末端处
     * - 菌伞边缘：底面边缘向下延伸 1 格 pinkagaric_0
     * - 30% 概率伞中心替换为 pinkagaric_3（荧光）
     *
     * @param level      世界生成级别访问
     * @param random     随机数源
     * @param mx         蘑菇 X
     * @param attachY    附着固体 Y（蘑菇挂在其下方）
     * @param mz         蘑菇 Z
     * @param allPlaced  已放置方块集合
     * @return 是否成功放置了完整的蘑菇
     */
    private boolean placeHangingMushroom(WorldGenLevel level, RandomSource random,
                                          int mx, int attachY, int mz,
                                          Set<BlockPos> allPlaced) {
        int randomHeight = STEM_HEIGHT_MIN + random.nextInt(STEM_HEIGHT_MAX - STEM_HEIGHT_MIN + 1);
        // 自适应降级：空间不足时缩短菌柄重试，最矮也放不下才放弃
        for (int stemHeight = randomHeight; stemHeight >= STEM_HEIGHT_MIN; stemHeight--) {
            Map<BlockPos, BlockState> plan = planMushroomBlocks(level, random, mx, attachY, mz, stemHeight, allPlaced);
            if (plan == null) continue;
            commitMushroomPlan(level, plan, allPlaced);
            return true;
        }
        return false;
    }

    /**
     * 按指定菌柄高度收集蘑菇的完整放置计划（阶段一：预检，不写入世界）
     *
     * @param level      世界生成级别访问
     * @param random     随机数源
     * @param mx         蘑菇 X
     * @param attachY    附着固体 Y
     * @param mz         蘑菇 Z
     * @param stemHeight 菌柄高度
     * @param allPlaced  已放置方块集合
     * @return 位置 → 方块状态的放置计划；任一目标格不可替换时返回 null
     */
    @Nullable
    private Map<BlockPos, BlockState> planMushroomBlocks(WorldGenLevel level, RandomSource random,
                                                         int mx, int attachY, int mz,
                                                         int stemHeight, Set<BlockPos> allPlaced) {
        Map<BlockPos, BlockState> plan = new LinkedHashMap<>();
        int stemEnd = attachY - stemHeight;

        // 菌柄路径（附着面下方 1 格 → 柄底）
        for (int y = attachY - 1; y >= stemEnd; y--) {
            plan.put(new BlockPos(mx, y, mz), PDBlocks.PINKAGARIC_1.get().defaultBlockState());
        }

        // 菌伞底面 3x3（伞中心与柄底重合），中心按概率替换为荧光菌伞
        int half = CAP_WIDTH / 2;
        for (int dx = -half; dx < CAP_WIDTH - half; dx++) {
            for (int dz = -half; dz < CAP_WIDTH - half; dz++) {
                Block capBlock;
                if (dx == 0 && dz == 0 && random.nextFloat() < GLOW_CAP_CHANCE) {
                    capBlock = PDBlocks.PINKAGARIC_3.get();
                } else {
                    capBlock = PDBlocks.PINKAGARIC_0.get();
                }
                plan.put(new BlockPos(mx + dx, stemEnd, mz + dz), capBlock.defaultBlockState());
            }
        }

        // 菌伞边缘向下延伸 1 格
        for (int dx = -half; dx < CAP_WIDTH - half; dx++) {
            for (int dz = -half; dz < CAP_WIDTH - half; dz++) {
                boolean isEdgeX = (dx == -half || dx == CAP_WIDTH - half - 1);
                boolean isEdgeZ = (dz == -half || dz == CAP_WIDTH - half - 1);
                if (!isEdgeX && !isEdgeZ) continue;

                plan.put(new BlockPos(mx + dx, stemEnd - 1, mz + dz),
                        PDBlocks.PINKAGARIC_0.get().defaultBlockState());
            }
        }

        // 预检：任一目标格不可替换则整朵放弃
        for (BlockPos pos : plan.keySet()) {
            if (!canReplace(level, pos, allPlaced)) {
                return null;
            }
        }
        return plan;
    }

    /**
     * 执行放置计划（阶段二：一次性写入世界）
     *
     * @param level     世界生成级别访问
     * @param plan      放置计划
     * @param allPlaced 已放置方块集合
     */
    private void commitMushroomPlan(WorldGenLevel level, Map<BlockPos, BlockState> plan, Set<BlockPos> allPlaced) {
        for (Map.Entry<BlockPos, BlockState> entry : plan.entrySet()) {
            level.setBlock(entry.getKey(), entry.getValue(), 3);
            allPlaced.add(entry.getKey());
        }
    }

    /**
     * 寻找附着面（天花板或墙壁）
     * <p>
     * 优先在起点位置向上找天花板（空气 → 固体），
     * 其次在水平方向找固体墙壁。
     * <p>
     * 返回值语义统一为"附着固体所在的 Y"，蘑菇悬挂在该固体下方。
     *
     * @param level  世界生成级别访问
     * @param pos    起始位置
     * @param random 随机数源
     * @return 附着固体所在的 Y 坐标，找不到则返回 Integer.MIN_VALUE
     */
    private int findAttachY(WorldGenLevel level, BlockPos pos, RandomSource random) {
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos(pos.getX(), pos.getY(), pos.getZ());

        // 优先向上找天花板（先找空气再找固体）
        for (int dy = 0; dy <= 6; dy++) {
            mutablePos.setY(pos.getY() + dy);
            BlockState state = level.getBlockState(mutablePos);
            if (state.isAir()) continue;

            // 找到固体方块（天花板）
            if (state.isCollisionShapeFullBlock(level, mutablePos)) {
                return mutablePos.getY(); // 返回天花板本身的 Y
            }
        }

        // 天花板找不到时，尝试在水平方向找墙壁（蘑菇贴墙悬挂）
        int[][] dirs = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};
        for (int[] dir : dirs) {
            int checkX = pos.getX() + dir[0] * 2;
            int checkZ = pos.getZ() + dir[1] * 2;
            mutablePos.set(checkX, pos.getY(), checkZ);
            BlockState wallState = level.getBlockState(mutablePos);
            if (!wallState.isAir() && wallState.isCollisionShapeFullBlock(level, mutablePos)) {
                // 墙壁语义与天花板一致：返回固体所在 Y，且需确认其下方存在柄顶挂点
                BlockPos hangPos = mutablePos.below();
                if (hangPos.getY() >= level.getMinBuildHeight() && level.getBlockState(hangPos).isAir()) {
                    return pos.getY();
                }
            }
        }

        return Integer.MIN_VALUE;
    }

    /**
     * 在指定位置附近寻找局部附着面
     * <p>
     * 仅接受天花板型附着（固体下方是空气）——悬挂式蘑菇不能以地板为附着面，
     * 否则菌柄/菌伞会向下生长进固体内部（埋地）。
     *
     * @param level  世界生成级别访问
     * @param pos    起始位置
     * @param random 随机数源
     * @return 附着固体所在的 Y，找不到返回 Integer.MIN_VALUE
     */
    private int findLocalAttachY(WorldGenLevel level, BlockPos pos, RandomSource random) {
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos(pos.getX(), pos.getY(), pos.getZ());

        // 上下搜索 ±3 格，只接受天花板型附着
        for (int dy = -3; dy <= 3; dy++) {
            mutablePos.setY(pos.getY() + dy);
            BlockState state = level.getBlockState(mutablePos);
            if (state.isAir()) continue;
            if (state.isCollisionShapeFullBlock(level, mutablePos)) {
                // 仅当固体下方是空气（天花板）才可作为悬挂附着面
                BlockPos below = mutablePos.below();
                if (below.getY() >= level.getMinBuildHeight() && level.getBlockState(below).isAir()) {
                    return mutablePos.getY();
                }
            }
        }
        return Integer.MIN_VALUE;
    }

    /**
     * 检查指定位置的方块是否可被替换
     *
     * @param level     世界生成级别访问
     * @param pos       要检查的位置
     * @param placedSet 已放置方块集合
     * @return true 表示可以替换
     */
    private boolean canReplace(WorldGenLevel level, BlockPos pos, Set<BlockPos> placedSet) {
        if (placedSet.contains(pos)) return false;
        BlockState state = level.getBlockState(pos);
        return state.isAir() || state.canBeReplaced();
    }
}
