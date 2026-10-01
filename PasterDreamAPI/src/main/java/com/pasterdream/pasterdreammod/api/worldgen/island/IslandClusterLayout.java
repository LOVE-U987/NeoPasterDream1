package com.pasterdream.pasterdreammod.api.worldgen.island;

/**
 * 岛系集群布局 —— 单个集群的形态参数（纯数据，不含位置相关计算）
 * <p>
 * 由 {@link IslandLayoutManager} 依据「世界种子 + 集群坐标」确定性推导后缓存。
 * 位置相关的几何求值（地表高度、岛体厚度、云托范围、蛀空判定）统一由管理器完成，
 * 以便生成器、群系源与装饰 Feature 复用同一套算法。
 *
 * @param clusterX     集群网格 X 坐标
 * @param clusterZ     集群网格 Z 坐标
 * @param centerX      主岛中心世界 X（浮点，避免整格对齐）
 * @param centerZ      主岛中心世界 Z
 * @param mainRadius   主岛半径（格）
 * @param extent       本集群自中心起算的最大延伸半径（含环岛与拖尾，格）
 * @param topY         主岛地表基准高度（Y）
 * @param archetype    岛系原型
 * @param hollow       是否被植物蛀空（仅普通原型的中型以上主岛）
 * @param hollowRadius 蛀空区水平半径（格），未蛀空时为 0
 * @param windDirection 本集群的固定风向（0~7），风蚀原型遵循此方向
 */
public record IslandClusterLayout(
        int clusterX,
        int clusterZ,
        double centerX,
        double centerZ,
        int mainRadius,
        int extent,
        int topY,
        IslandArchetype archetype,
        boolean hollow,
        double hollowRadius,
        int windDirection
) {

    /**
     * 计算某点到主岛中心的水平距离
     *
     * @param blockX 世界 X
     * @param blockZ 世界 Z
     * @return 水平距离（格）
     */
    public double distanceToCenter(double blockX, double blockZ) {
        double dx = blockX - centerX;
        double dz = blockZ - centerZ;
        return Math.sqrt(dx * dx + dz * dz);
    }

    /**
     * 计算某点相对主岛中心的方向角
     *
     * @param blockX 世界 X
     * @param blockZ 世界 Z
     * @return 方向角（弧度，-PI~PI，0 指向 +X）
     */
    public double angleTo(double blockX, double blockZ) {
        return Math.atan2(blockZ - centerZ, blockX - centerX);
    }

    /**
     * 判断点是否位于主岛水平范围内
     *
     * @param blockX 世界 X
     * @param blockZ 世界 Z
     * @return 位于主岛半径内返回 true
     */
    public boolean insideMain(double blockX, double blockZ) {
        return distanceToCenter(blockX, blockZ) <= mainRadius;
    }

    /**
     * 判断点是否位于本集群影响范围内（含环岛与拖尾）
     *
     * @param blockX 世界 X
     * @param blockZ 世界 Z
     * @return 位于延伸范围内返回 true
     */
    public boolean insideExtent(double blockX, double blockZ) {
        return distanceToCenter(blockX, blockZ) <= extent;
    }
}
