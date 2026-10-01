package com.pasterdream.pasterdreammod.api.worldgen.island;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 岛系布局管理器 —— 风之旅途重构的确定性布局核心
 * <p>
 * 设计目标：把「岛体几何」变成一处可复用的纯函数查询，
 * 让地形生成器、群系源（BiomeSource）与装饰 Feature 三方共用同一套判定，
 * 从根本上避免"群系与地形错位"。
 * <p>
 * <b>确定性</b>：全部结果仅由「世界种子 + 坐标」推导，不依赖任何生成期随机状态；
 * 因此群系源无需 RandomState 即可求值，且任何区块都能独立算出自己所处的岛系切片，
 * 规避 Feature 的 ±1 区块写半径限制。
 * <p>
 * <b>线程安全</b>：区块生成可能并行执行，集群参数缓存使用 {@link ConcurrentHashMap}，
 * 求值函数无副作用，可安全并发调用。
 *
 * @author PasterDream Team
 */
public final class IslandLayoutManager {

    // ==================== 哈希盐值（各用途相互独立） ====================

    private static final int SALT_CLUSTER = 1013;
    private static final int SALT_CENTER_X = 2027;
    private static final int SALT_CENTER_Z = 3041;
    private static final int SALT_RADIUS = 4057;
    private static final int SALT_ARCHETYPE = 5077;
    private static final int SALT_HOLLOW = 6091;
    private static final int SALT_TOP_Y = 7103;
    private static final int SALT_RELIEF = 8117;
    private static final int SALT_RING_SHAPE = 9133;
    private static final int SALT_RING_RELIEF = 10151;
    private static final int SALT_RING_BREAK = 11173;
    private static final int SALT_DEBRIS = 12197;
    private static final int SALT_SMALL = 13219;
    private static final int SALT_SMALL_X = 14243;
    private static final int SALT_SMALL_Z = 15269;
    private static final int SALT_SMALL_R = 16297;
    private static final int SALT_SMALL_Y = 17327;
    private static final int SALT_CLOUD_GAP = 18353;
    private static final int SALT_CLOUD_DEPTH = 19381;
    private static final int SALT_CLOUD_MASK = 20407;
    private static final int SALT_WIND = 21433;

    // ==================== 形态常量（结构性比例，非玩法平衡数值） ====================

    /** 蛀空区水平半径占主岛半径的比例 */
    private static final double HOLLOW_RADIUS_RATIO = 0.55;

    /** 主岛基准高度在集群之间的随机浮动幅度（±格） */
    private static final int TOP_Y_VARIANCE = 8;

    /** 小型岛基准高度在网格之间的随机浮动幅度（±格） */
    private static final int SMALL_TOP_Y_VARIANCE = 12;

    /** 地表起伏噪声的空间尺度（格） */
    private static final int RELIEF_NOISE_SCALE = 19;

    /** 云托噪声的空间尺度（格） */
    private static final int CLOUD_NOISE_SCALE = 23;

    /** 云托覆盖掩码的噪声尺度（格） */
    private static final int CLOUD_MASK_SCALE = 31;

    /** 环岛破碎化判定网格边长（格） */
    private static final int RING_FRAGMENT_SCALE = 9;

    /** 环带形态随角度变化的采样桶数 */
    private static final int RING_ANGLE_BUCKETS = 24;

    /** 环岛岩体基准厚度（格） */
    private static final int RING_THICKNESS = 3;

    /** 小型岛岩体基准厚度（格） */
    private static final int SMALL_THICKNESS = 3;

    /** 边缘下压上限（格）—— 制造"边缘凹凸不平"的收边 */
    private static final double EDGE_DIP_MAX = 3.0;

    /** 风蚀原型向风面起伏缩放（趋向圆滑） */
    private static final double WINDWARD_RELIEF_SCALE = 0.35;

    /** 风蚀原型背风面起伏缩放（趋向凹凸） */
    private static final double LEEWARD_RELIEF_SCALE = 1.6;

    /** 风蚀原型向风面半径收缩量（格）—— 被风削平 */
    private static final double WINDWARD_INSET = 3.0;

    /** 蛀空区距岛体下缘的保留厚度（格） */
    private static final int HOLLOW_FLOOR_MARGIN = 4;

    /** 蛀空区距岛体上缘的保留厚度（格） */
    private static final int HOLLOW_CEILING_MARGIN = 4;

    // ==================== 状态 ====================

    /** 世界种子 */
    private final long seed;

    /** 形态规格（由主模注入） */
    private final IslandLayoutSpec spec;

    /** 集群参数缓存（键为集群坐标打包值） */
    private final ConcurrentHashMap<Long, IslandClusterLayout> clusterCache = new ConcurrentHashMap<>();

    /** 空集群哨兵 —— 该网格不生成岛系 */
    private static final IslandClusterLayout EMPTY_CLUSTER = new IslandClusterLayout(
            0, 0, Double.NaN, Double.NaN, 0, 0, 0, IslandArchetype.NORMAL, false, 0.0, 0);

    /**
     * 构造布局管理器
     *
     * @param seed 世界种子
     * @param spec 形态规格（由主模注入全部数值）
     */
    public IslandLayoutManager(long seed, IslandLayoutSpec spec) {
        this.seed = seed;
        this.spec = spec;
    }

    /**
     * 获取形态规格
     *
     * @return 规格实例
     */
    public IslandLayoutSpec spec() {
        return spec;
    }

    /**
     * 获取指定集群的布局参数
     *
     * @param clusterX 集群网格 X 坐标
     * @param clusterZ 集群网格 Z 坐标
     * @return 布局参数；该网格不生成岛系时返回 {@code null}
     */
    public IslandClusterLayout clusterAt(int clusterX, int clusterZ) {
        IslandClusterLayout layout = clusterCache.computeIfAbsent(
                clusterKey(clusterX, clusterZ), key -> buildCluster(clusterX, clusterZ));
        return layout == EMPTY_CLUSTER ? null : layout;
    }

    /**
     * 求值某水平坐标处的岛体信息
     * <p>
     * 依次检索本格与周围八格的集群（因集群中心在网格内缩进，岛体不会越出邻格范围），
     * 未命中集群时再检索风之岛（小型岛）网格。
     *
     * @param blockX 世界 X
     * @param blockZ 世界 Z
     * @return 岛体信息；空中列返回 {@link IslandSurface#AIR_COLUMN}
     */
    public IslandSurface surfaceAt(int blockX, int blockZ) {
        int spacing = spec.clusterSpacing();
        int cellX = Math.floorDiv(blockX, spacing);
        int cellZ = Math.floorDiv(blockZ, spacing);

        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                IslandClusterLayout cluster = clusterAt(cellX + dx, cellZ + dz);
                if (cluster == null) {
                    continue;
                }
                IslandSurface surface = evaluateCluster(cluster, blockX, blockZ);
                if (surface.isIsland()) {
                    return surface;
                }
            }
        }
        return evaluateSmallIsland(blockX, blockZ);
    }

    /**
     * 判断某列是否含岛体
     *
     * @param blockX 世界 X
     * @param blockZ 世界 Z
     * @return 含岛体返回 true
     */
    public boolean isIslandAt(int blockX, int blockZ) {
        return surfaceAt(blockX, blockZ).isIsland();
    }

    /**
     * 获取某列的岛体上缘高度
     *
     * @param blockX 世界 X
     * @param blockZ 世界 Z
     * @return 上缘 Y；空中列返回 {@link Integer#MIN_VALUE}
     */
    public int topYAt(int blockX, int blockZ) {
        return surfaceAt(blockX, blockZ).topY();
    }

    /**
     * 获取某列的岛体下缘高度
     *
     * @param blockX 世界 X
     * @param blockZ 世界 Z
     * @return 下缘 Y；空中列返回 {@link Integer#MIN_VALUE}
     */
    public int bottomYAt(int blockX, int blockZ) {
        return surfaceAt(blockX, blockZ).bottomY();
    }

    /**
     * 获取某列的云托范围（岛体下方的云团）
     * <p>
     * P1 阶段仅岛体所在列生成云团（横向拉伸与悬挑在 P2 完善），
     * 云团厚度与贴合间隙均由噪声决定，避免出现"平贴岛底的云板"。
     *
     * @param blockX 世界 X
     * @param blockZ 世界 Z
     * @return 云团垂直区间；该列无云团时返回 {@code null}
     */
    public IslandSurface.CloudSpan cloudSpanAt(int blockX, int blockZ) {
        IslandSurface surface = surfaceAt(blockX, blockZ);
        if (!surface.isIsland()) {
            return null;
        }
        if (valueNoise(blockX, blockZ, CLOUD_MASK_SCALE, SALT_CLOUD_MASK) >= spec.cloudChance()) {
            return null;
        }
        double gap = valueNoise(blockX, blockZ, CLOUD_NOISE_SCALE, SALT_CLOUD_GAP) * spec.cloudGapMax();
        double depth = valueNoise(blockX, blockZ, CLOUD_NOISE_SCALE, SALT_CLOUD_DEPTH) * spec.cloudDepthMax();
        int top = surface.bottomY() - (int) Math.round(gap);
        int bottom = top - (int) Math.round(1.0 + depth);
        return new IslandSurface.CloudSpan(bottom, top);
    }

    /**
     * 判断某位置是否位于蛀空生态圈的内部空腔中
     *
     * @param blockX 世界 X
     * @param blockY 世界 Y
     * @param blockZ 世界 Z
     * @return 位于蛀空区内部返回 true
     */
    public boolean isInsideHollow(int blockX, int blockY, int blockZ) {
        IslandSurface surface = surfaceAt(blockX, blockZ);
        if (surface.kind() != IslandSurface.Kind.MAIN || surface.cluster() == null) {
            return false;
        }
        IslandClusterLayout cluster = surface.cluster();
        if (!cluster.hollow()) {
            return false;
        }
        if (cluster.distanceToCenter(blockX, blockZ) > cluster.hollowRadius()) {
            return false;
        }
        return blockY >= surface.bottomY() + HOLLOW_FLOOR_MARGIN
                && blockY <= surface.topY() - HOLLOW_CEILING_MARGIN;
    }

    /**
     * 获取某位置的区域固定风向
     * <p>
     * 风向由粗网格哈希决定：同一区域内（含相邻岛系集群）风向一致，
     * 与现有"日更全局风向"相互独立，供风蚀群柏岛与定向植被使用。
     *
     * @param blockX 世界 X
     * @param blockZ 世界 Z
     * @return 风向编号 0~7（每 45 度一个方向）
     */
    public int windDirectionAt(int blockX, int blockZ) {
        int grid = spec.windRegionSpacing();
        int gridX = Math.floorDiv(blockX, grid);
        int gridZ = Math.floorDiv(blockZ, grid);
        return (int) (hash01(gridX, gridZ, SALT_WIND) * 8.0) & 7;
    }

    /**
     * 判断某列是否属于风蚀原型岛系
     *
     * @param blockX 世界 X
     * @param blockZ 世界 Z
     * @return 属于风蚀原型返回 true
     */
    public boolean isErodedAt(int blockX, int blockZ) {
        IslandSurface surface = surfaceAt(blockX, blockZ);
        return surface.cluster() != null
                && surface.cluster().archetype() == IslandArchetype.WIND_ERODED;
    }

    // ==================== 内部：集群构建 ====================

    /**
     * 构建集群布局参数（纯函数，可并发调用）
     *
     * @param clusterX 集群网格 X 坐标
     * @param clusterZ 集群网格 Z 坐标
     * @return 布局参数；该网格为空时返回 {@link #EMPTY_CLUSTER}
     */
    private IslandClusterLayout buildCluster(int clusterX, int clusterZ) {
        int spacing = spec.clusterSpacing();
        if (hash01(clusterX, clusterZ, SALT_CLUSTER) >= spec.clusterChance()) {
            return EMPTY_CLUSTER;
        }

        // 集群中心约束在网格内缩进区域：保证相邻集群的岛体互不侵入
        int inset = spec.centerInset();
        int usable = Math.max(1, spacing - 2 * inset);
        double centerX = clusterX * (double) spacing + inset + hash01(clusterX, clusterZ, SALT_CENTER_X) * usable;
        double centerZ = clusterZ * (double) spacing + inset + hash01(clusterX, clusterZ, SALT_CENTER_Z) * usable;

        int mainRadius = lerpInt(spec.mainRadiusMin(), spec.mainRadiusMax(), hash01(clusterX, clusterZ, SALT_RADIUS));

        IslandArchetype archetype = hash01(clusterX, clusterZ, SALT_ARCHETYPE) < spec.erodedChance()
                ? IslandArchetype.WIND_ERODED
                : IslandArchetype.NORMAL;

        boolean hollow = archetype == IslandArchetype.NORMAL
                && mainRadius >= spec.hollowMinRadius()
                && hash01(clusterX, clusterZ, SALT_HOLLOW) < spec.hollowChance();

        int topY = spec.baseTopY() + (int) Math.round((hash01(clusterX, clusterZ, SALT_TOP_Y) - 0.5) * 2 * TOP_Y_VARIANCE);

        // 风蚀原型环岛已丢失，延伸范围只保留主岛与背风拖尾
        int ringExtent = archetype == IslandArchetype.NORMAL
                ? spec.ringGapMax() + spec.ringWidthMax()
                : 0;
        int extent = mainRadius + ringExtent + spec.debrisMax();

        int windDirection = windDirectionAt((int) centerX, (int) centerZ);

        return new IslandClusterLayout(
                clusterX, clusterZ, centerX, centerZ, mainRadius, extent, topY,
                archetype, hollow, hollow ? mainRadius * HOLLOW_RADIUS_RATIO : 0.0, windDirection);
    }

    /**
     * 求值主岛与环岛构成的岛体（单集群视角）
     *
     * @param cluster 集群布局
     * @param blockX  世界 X
     * @param blockZ  世界 Z
     * @return 岛体信息；该点不在本集群内时返回 {@link IslandSurface#AIR_COLUMN}
     */
    private IslandSurface evaluateCluster(IslandClusterLayout cluster, int blockX, int blockZ) {
        double distance = cluster.distanceToCenter(blockX, blockZ);
        if (distance > cluster.extent()) {
            return IslandSurface.AIR_COLUMN;
        }

        double angle = cluster.angleTo(blockX, blockZ);
        double windBearing = cluster.windDirection() * (Math.PI / 4.0);
        boolean windward = normalizeAngle(angle - windBearing) < Math.PI / 2.0;

        // 风蚀原型：向风面被削平圆滑，背风面起伏加剧
        double reliefScale = 1.0;
        double radius = cluster.mainRadius();
        if (cluster.archetype() == IslandArchetype.WIND_ERODED) {
            if (windward) {
                reliefScale = WINDWARD_RELIEF_SCALE;
                radius -= WINDWARD_INSET;
            } else {
                reliefScale = LEEWARD_RELIEF_SCALE;
            }
        }

        // ---------- 主岛 ----------
        if (distance <= radius) {
            double edge = distance / Math.max(1.0, radius);
            double relief = valueNoise(blockX, blockZ, RELIEF_NOISE_SCALE, SALT_RELIEF)
                    * spec.surfaceReliefMax() * reliefScale;
            double edgeDip = edge * edge * EDGE_DIP_MAX;          // 收边下压，形成凹凸不平的边缘
            int topY = cluster.topY() + (int) Math.round(relief - edgeDip);

            double funnel = 1.0 - edge * edge;                    // 中心最厚，边缘最薄
            int thickness = spec.minBodyThickness() + (int) Math.round(funnel * spec.funnelDepthMax());
            int bottomY = topY - thickness;
            return new IslandSurface(IslandSurface.Kind.MAIN, topY, bottomY, cluster, radius - distance);
        }

        // ---------- 环岛（风蚀原型已丢失） ----------
        if (cluster.archetype() == IslandArchetype.NORMAL) {
            double gap = lerp(spec.ringGapMin(), spec.ringGapMax(), ringShapeNoise(cluster, angle, SALT_RING_SHAPE));
            double width = lerp(spec.ringWidthMin(), spec.ringWidthMax(),
                    ringShapeNoise(cluster, angle, SALT_RING_SHAPE + RING_ANGLE_BUCKETS));
            double inner = cluster.mainRadius() + gap;
            double outer = inner + width;
            if (distance >= inner && distance <= outer
                    && ringFragmentAt(blockX, blockZ) < spec.ringChance()) {
                double relief = valueNoise(blockX, blockZ, RELIEF_NOISE_SCALE, SALT_RING_RELIEF)
                        * spec.surfaceReliefMax() * 0.5;
                int topY = cluster.topY() + (int) Math.round(relief);
                int bottomY = topY - RING_THICKNESS;
                return new IslandSurface(IslandSurface.Kind.RING, topY, bottomY, cluster,
                        -(distance - cluster.mainRadius()));
            }
            return IslandSurface.AIR_COLUMN;
        }

        // ---------- 风蚀拖尾：背风侧散落的岛体碎块 ----------
        if (!windward && distance <= radius + spec.debrisMax()
                && ringFragmentAt(blockX, blockZ) < spec.ringChance()) {
            double relief = valueNoise(blockX, blockZ, RELIEF_NOISE_SCALE, SALT_DEBRIS) * spec.surfaceReliefMax() * 0.4;
            int topY = cluster.topY() + (int) Math.round(relief) - 1;
            int bottomY = topY - RING_THICKNESS;
            return new IslandSurface(IslandSurface.Kind.DEBRIS, topY, bottomY, cluster,
                    -(distance - radius));
        }
        return IslandSurface.AIR_COLUMN;
    }

    /**
     * 求值风之岛（小型岛）
     *
     * @param blockX 世界 X
     * @param blockZ 世界 Z
     * @return 岛体信息；该点不在小型岛内时返回 {@link IslandSurface#AIR_COLUMN}
     */
    private IslandSurface evaluateSmallIsland(int blockX, int blockZ) {
        int spacing = spec.smallSpacing();
        int cellX = Math.floorDiv(blockX, spacing);
        int cellZ = Math.floorDiv(blockZ, spacing);
        if (hash01(cellX, cellZ, SALT_SMALL) >= spec.smallChance()) {
            return IslandSurface.AIR_COLUMN;
        }

        int inset = spec.smallRadiusMax();
        int usable = Math.max(1, spacing - 2 * inset);
        double centerX = cellX * (double) spacing + inset + hash01(cellX, cellZ, SALT_SMALL_X) * usable;
        double centerZ = cellZ * (double) spacing + inset + hash01(cellX, cellZ, SALT_SMALL_Z) * usable;

        double dx = blockX - centerX;
        double dz = blockZ - centerZ;
        double distance = Math.sqrt(dx * dx + dz * dz);
        int radius = lerpInt(spec.smallRadiusMin(), spec.smallRadiusMax(), hash01(cellX, cellZ, SALT_SMALL_R));
        if (distance > radius) {
            return IslandSurface.AIR_COLUMN;
        }

        // 净距判定用"小型岛自身中心"而非查询点：保证同一座小岛整体保留或整体剔除，
        // 避免出现被切掉一半的残缺小岛
        if (tooCloseToCluster(centerX, centerZ)) {
            return IslandSurface.AIR_COLUMN;
        }

        double edge = distance / Math.max(1.0, radius);
        double relief = valueNoise(blockX, blockZ, RELIEF_NOISE_SCALE, SALT_RELIEF) * spec.surfaceReliefMax() * 0.6;
        int topY = spec.baseTopY()
                + (int) Math.round((hash01(cellX, cellZ, SALT_SMALL_Y) - 0.5) * 2 * SMALL_TOP_Y_VARIANCE)
                + (int) Math.round(relief - edge * EDGE_DIP_MAX);
        int bottomY = topY - SMALL_THICKNESS - (int) Math.round((1.0 - edge * edge) * 2.0);
        return new IslandSurface(IslandSurface.Kind.SMALL, topY, bottomY, null, radius - distance);
    }

    /**
     * 判断某点是否离任一岛系集群过近（用于小型岛净距）
     *
     * @param blockX 世界 X
     * @param blockZ 世界 Z
     * @return 过近返回 true
     */
    private boolean tooCloseToCluster(double blockX, double blockZ) {
        int spacing = spec.clusterSpacing();
        int cellX = Math.floorDiv((int) Math.floor(blockX), spacing);
        int cellZ = Math.floorDiv((int) Math.floor(blockZ), spacing);
        double clearance = spec.smallClearance();
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                IslandClusterLayout cluster = clusterAt(cellX + dx, cellZ + dz);
                if (cluster != null && cluster.distanceToCenter(blockX, blockZ) <= cluster.extent() + clearance) {
                    return true;
                }
            }
        }
        return false;
    }

    // ==================== 内部：噪声与数学工具 ====================

    /**
     * 环带形态随角度变化的采样（同一集群内平滑过渡，不同集群各自独立）
     *
     * @param cluster 集群布局
     * @param angle   方向角（弧度）
     * @param salt    盐值
     * @return [0,1) 采样值
     */
    private double ringShapeNoise(IslandClusterLayout cluster, double angle, int salt) {
        double normalized = (normalizeAngle(angle) + Math.PI) / (2 * Math.PI);
        int bucket = (int) Math.floor(normalized * RING_ANGLE_BUCKETS) % RING_ANGLE_BUCKETS;
        return hash01(cluster.clusterX(), cluster.clusterZ(), salt + bucket);
    }

    /**
     * 环岛破碎化判定（细网格哈希，形成碎片化环带）
     *
     * @param blockX 世界 X
     * @param blockZ 世界 Z
     * @return [0,1) 判定值
     */
    private double ringFragmentAt(int blockX, int blockZ) {
        return hash01(Math.floorDiv(blockX, RING_FRAGMENT_SCALE), Math.floorDiv(blockZ, RING_FRAGMENT_SCALE),
                SALT_RING_BREAK);
    }

    /**
     * 二维值噪声 —— 格点哈希 + 平滑插值，返回 [0,1)
     * <p>
     * 不依赖 Minecraft 的噪声路由器，因此群系源（无 RandomState）也能求值，
     * 且与地形生成器使用完全相同的数值。
     *
     * @param blockX 世界 X
     * @param blockZ 世界 Z
     * @param scale  空间尺度（格）
     * @param salt   盐值
     * @return [0,1) 噪声值
     */
    private double valueNoise(int blockX, int blockZ, int scale, int salt) {
        double fx = (double) blockX / scale;
        double fz = (double) blockZ / scale;
        int x0 = (int) Math.floor(fx);
        int z0 = (int) Math.floor(fz);
        double tx = smoothstep(fx - x0);
        double tz = smoothstep(fz - z0);

        double v00 = hash01(x0, z0, salt);
        double v10 = hash01(x0 + 1, z0, salt);
        double v01 = hash01(x0, z0 + 1, salt);
        double v11 = hash01(x0 + 1, z0 + 1, salt);

        double lower = v00 + (v10 - v00) * tx;
        double upper = v01 + (v11 - v01) * tx;
        return lower + (upper - lower) * tz;
    }

    /**
     * 三次平滑插值权重
     *
     * @param t 归一化偏移 0~1
     * @return 平滑权重
     */
    private static double smoothstep(double t) {
        return t * t * (3.0 - 2.0 * t);
    }

    /**
     * 位置哈希 —— 由「世界种子 + 坐标 + 盐值」得到确定性随机值
     *
     * @param x    格点 X
     * @param z    格点 Z
     * @param salt 盐值
     * @return [0,1) 随机值
     */
    private double hash01(int x, int z, int salt) {
        long h = seed;
        h = mix(h, x);
        h = mix(h, z);
        h = mix(h, salt);
        return (h >>> 11) * 0x1.0p-53;
    }

    /**
     * 混合函数（SplitMix 风格）
     *
     * @param hash 当前哈希值
     * @param value 待混入值
     * @return 混合后的哈希值
     */
    private static long mix(long hash, long value) {
        long h = hash ^ (value + 0x9E3779B97F4A7C15L + (hash << 6) + (hash >>> 2));
        h *= 0xff51afd7ed558ccdL;
        h ^= h >>> 33;
        return h;
    }

    /**
     * 打包集群坐标为缓存键
     *
     * @param clusterX 集群网格 X 坐标
     * @param clusterZ 集群网格 Z 坐标
     * @return 打包键
     */
    private static long clusterKey(int clusterX, int clusterZ) {
        return ((long) clusterX << 32) ^ (clusterZ & 0xFFFFFFFFL);
    }

    /**
     * 线性插值（双精度）
     *
     * @param min  下限
     * @param max  上限
     * @param t    权重 0~1
     * @return 插值结果
     */
    private static double lerp(double min, double max, double t) {
        return min + (max - min) * t;
    }

    /**
     * 线性插值（整数，四舍五入）
     *
     * @param min  下限
     * @param max  上限
     * @param t    权重 0~1
     * @return 插值结果
     */
    private static int lerpInt(int min, int max, double t) {
        return min + (int) Math.round((max - min) * t);
    }

    /**
     * 将角度归一化到 -PI~PI
     *
     * @param angle 角度（弧度）
     * @return 归一化角度
     */
    private static double normalizeAngle(double angle) {
        double a = angle;
        while (a > Math.PI) {
            a -= 2 * Math.PI;
        }
        while (a < -Math.PI) {
            a += 2 * Math.PI;
        }
        return a;
    }
}
