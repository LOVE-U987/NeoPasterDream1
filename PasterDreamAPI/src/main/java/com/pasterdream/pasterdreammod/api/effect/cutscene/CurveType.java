package com.pasterdream.pasterdreammod.api.effect.cutscene;

/**
 * 过场相机路径曲线类型
 * <p>
 * 借鉴开源模组 FDLib（作者 FINDERFEED）的 {@code CurveType} 设计思路
 * （独立实现，非复制）。
 */
public enum CurveType {

    /** Catmull-Rom 样条曲线（平滑过弯，默认） */
    CATMULLROM,

    /** 线性插值（直线段） */
    LINEAR
}
