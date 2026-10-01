package com.pasterdream.pasterdreammod.api.worldgen.island;

/**
 * 岛系布局规格 —— 由调用方（主模）注入的全部形态参数
 * <p>
 * 本记录不包含任何具体玩法数值：所有数值均由主模构造布局管理器时注入，
 * 以保证 API 层满足「零 PD 注册表 / 零玩法数值 / 零具体资产路径」的边界判定。
 * <p>
 * 参数含义中的"上限/下限"均为区间采样：同一岛系集群内的取值由集群坐标确定性推导，
 * 不同集群之间自然产生形貌差异。
 *
 * @param clusterSpacing   岛系集群网格边长（格）；集群中心被约束在网格内缩进区域内
 * @param clusterChance    集群出现概率 0~1，未命中则网格留空（保证岛系之间有空隙）
 * @param mainRadiusMin    风之主岛半径下限（格）
 * @param mainRadiusMax    风之主岛半径上限（格）
 * @param ringGapMin       环岛与主岛边缘的间距下限（格）
 * @param ringGapMax       环岛与主岛边缘的间距上限（格）
 * @param ringWidthMin     环带径向宽度下限（格）
 * @param ringWidthMax     环带径向宽度上限（格）
 * @param ringChance       环带内某点成为碎片岛的概率 0~1（形成破碎化观感）
 * @param erodedChance     风蚀原型占比 0~1
 * @param hollowChance     中型岛被植物蛀空的概率 0~1
 * @param hollowMinRadius  蛀空生效的最小主岛半径（小于此半径的岛不蛀空）
 * @param baseTopY         主岛地表基准高度（Y）
 * @param surfaceReliefMax 主岛地表起伏上限（格），设计给定 2~5
 * @param funnelDepthMax   主岛漏斗状下体的最大深度（格，位于岛心）
 * @param minBodyThickness 主岛边缘的最小岩体厚度（格）
 * @param cloudGapMax      云托与岛体下缘之间允许的最大间隙（格）
 * @param cloudDepthMax    云托云团的最大厚度（格）
 * @param cloudChance      某列形成云托云团的概率 0~1
 * @param smallSpacing     风之岛（小型岛）网格边长（格）
 * @param smallChance      小型岛在网格中的出现概率 0~1
 * @param smallRadiusMin   小型岛半径下限（格）
 * @param smallRadiusMax   小型岛半径上限（格）
 * @param smallClearance   小型岛与岛系集群外缘的最小距离（格），避免连成大片地面
 * @param windRegionSpacing 区域固定风向的网格边长（格），相邻区域风向一致
 * @param debrisMax        风蚀原型背风侧拖尾碎块的最大延伸距离（格）
 */
public record IslandLayoutSpec(
        int clusterSpacing,
        double clusterChance,
        int mainRadiusMin,
        int mainRadiusMax,
        int ringGapMin,
        int ringGapMax,
        int ringWidthMin,
        int ringWidthMax,
        double ringChance,
        double erodedChance,
        double hollowChance,
        int hollowMinRadius,
        int baseTopY,
        int surfaceReliefMax,
        int funnelDepthMax,
        int minBodyThickness,
        int cloudGapMax,
        int cloudDepthMax,
        double cloudChance,
        int smallSpacing,
        double smallChance,
        int smallRadiusMin,
        int smallRadiusMax,
        int smallClearance,
        int windRegionSpacing,
        int debrisMax
) {

    /**
     * 校验参数合法性 —— 区间颠倒或概率越界属于调用方配置错误，直接快速失败
     *
     * @throws IllegalArgumentException 当区间下限大于上限，或概率不在 0~1 内时抛出
     */
    public IslandLayoutSpec {
        requirePositive(clusterSpacing, "clusterSpacing");
        requirePositive(mainRadiusMin, "mainRadiusMin");
        requirePositive(smallSpacing, "smallSpacing");
        requirePositive(windRegionSpacing, "windRegionSpacing");
        requireRange(mainRadiusMin, mainRadiusMax, "mainRadius");
        requireRange(ringGapMin, ringGapMax, "ringGap");
        requireRange(ringWidthMin, ringWidthMax, "ringWidth");
        requireRange(smallRadiusMin, smallRadiusMax, "smallRadius");
        requireChance(clusterChance, "clusterChance");
        requireChance(ringChance, "ringChance");
        requireChance(erodedChance, "erodedChance");
        requireChance(hollowChance, "hollowChance");
        requireChance(cloudChance, "cloudChance");
        requireChance(smallChance, "smallChance");
        if (mainRadiusMin <= ringGapMax + ringWidthMax + smallClearance) {
            throw new IllegalArgumentException(
                    "集群网格过小：clusterSpacing 必须大于主岛半径与环岛外缘、小岛净距之和，"
                            + "否则相邻集群会重叠");
        }
    }

    /**
     * 主岛与环岛之外的岛系总延伸半径（含拖尾）
     *
     * @return 自集群中心起算的最大延伸距离（格）
     */
    public int clusterExtentMax() {
        return mainRadiusMax + ringGapMax + ringWidthMax + debrisMax;
    }

    /**
     * 集群中心在网格内的最小缩进量 —— 保证相邻集群的岛体不互相侵入
     *
     * @return 缩进量（格）
     */
    public int centerInset() {
        return clusterExtentMax();
    }

    /**
     * 校验正整数参数
     *
     * @param value 待校验值
     * @param name  参数名（用于异常信息）
     */
    private static void requirePositive(int value, String name) {
        if (value <= 0) {
            throw new IllegalArgumentException(name + " 必须为正整数，实际为 " + value);
        }
    }

    /**
     * 校验区间参数
     *
     * @param min  下限
     * @param max  上限
     * @param name 区间名（用于异常信息）
     */
    private static void requireRange(int min, int max, String name) {
        if (min > max) {
            throw new IllegalArgumentException(name + " 区间颠倒：" + min + " > " + max);
        }
    }

    /**
     * 校验概率参数
     *
     * @param value 概率值
     * @param name  参数名（用于异常信息）
     */
    private static void requireChance(double value, String name) {
        if (value < 0.0 || value > 1.0) {
            throw new IllegalArgumentException(name + " 必须位于 0~1，实际为 " + value);
        }
    }
}
