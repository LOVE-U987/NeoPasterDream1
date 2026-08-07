package com.pasterdream.pasterdreammod.api.effect.cutscene;

/**
 * 过场动画缓动函数类型 —— 控制时间进度到路径进度的映射
 * <p>
 * 借鉴开源模组 FDLib（作者 FINDERFEED）的 {@code EasingType} 设计思路
 * （独立实现，非复制）。
 */
public enum EasingType {

    /** 线性（匀速） */
    LINEAR,

    /** 缓入（开始慢，逐渐加速） */
    EASE_IN,

    /** 缓出（开始快，逐渐减速） */
    EASE_OUT,

    /** 缓入缓出（两端慢，中间快） */
    EASE_IN_OUT,

    /** 平滑步进（smoothstep，常用默认） */
    SMOOTHSTEP;

    /**
     * 对进度应用缓动
     *
     * @param p 原始进度（0~1）
     * @return 缓动后进度（0~1）
     */
    public float apply(float p) {
        float x = Math.max(0.0f, Math.min(1.0f, p));
        return switch (this) {
            case LINEAR -> x;
            case EASE_IN -> x * x;
            case EASE_OUT -> 1.0f - (1.0f - x) * (1.0f - x);
            case EASE_IN_OUT -> x < 0.5f ? 2.0f * x * x : 1.0f - (float) Math.pow(-2.0f * x + 2.0f, 2.0f) / 2.0f;
            case SMOOTHSTEP -> x * x * (3.0f - 2.0f * x);
        };
    }
}
