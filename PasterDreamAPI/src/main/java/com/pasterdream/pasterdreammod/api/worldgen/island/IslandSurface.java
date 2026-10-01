package com.pasterdream.pasterdreammod.api.worldgen.island;

/**
 * 单列岛体求值结果 —— 某水平坐标处的岛体归属与上下缘高度
 * <p>
 * 由 {@link IslandLayoutManager#surfaceAt(int, int)} 返回，供地形生成器逐列填充、
 * 群系源判定位面归属、装饰 Feature 判断可站立面。
 *
 * @param kind         岛体类型
 * @param topY         岛体上缘高度（Y，含类型时有效）
 * @param bottomY      岛体下缘高度（Y，含类型时有效）
 * @param cluster      所属集群布局；小型岛或空中列时为 null
 * @param edgeDistance 到所属主岛边缘的距离（格，正值表示在内侧）；空中列为 {@link Double#MAX_VALUE}
 */
public record IslandSurface(
        Kind kind,
        int topY,
        int bottomY,
        IslandClusterLayout cluster,
        double edgeDistance
) {

    /** 空中列哨兵 —— 不属于任何岛体 */
    public static final IslandSurface AIR_COLUMN =
            new IslandSurface(Kind.NONE, Integer.MIN_VALUE, Integer.MIN_VALUE, null, Double.MAX_VALUE);

    /**
     * 岛体类型
     */
    public enum Kind {
        /** 风之主岛（中心主导岛） */
        MAIN,
        /** 风之岛之环上的破碎环岛 */
        RING,
        /** 风之岛（小型岛） */
        SMALL,
        /** 风蚀原型背风侧吹落的岛体碎块（拖尾） */
        DEBRIS,
        /** 空中，无岛体 */
        NONE
    }

    /**
     * 判断本列是否含岛体
     *
     * @return 含岛体返回 true
     */
    public boolean isIsland() {
        return kind != Kind.NONE;
    }

    /**
     * 判断某高度是否位于岛体岩体内
     *
     * @param y 世界 Y
     * @return 位于岩体内返回 true
     */
    public boolean containsY(int y) {
        return isIsland() && y >= bottomY && y <= topY;
    }

    /**
     * 云托范围 —— 岛体下方云团的垂直区间
     *
     * @param bottomY 云团下缘（Y）
     * @param topY    云团上缘（Y）
     */
    public record CloudSpan(int bottomY, int topY) {

        /**
         * 判断某高度是否位于云团内
         *
         * @param y 世界 Y
         * @return 位于云团内返回 true
         */
        public boolean containsY(int y) {
            return y >= bottomY && y <= topY;
        }
    }
}
