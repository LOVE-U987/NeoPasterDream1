package com.pasterdream.pasterdreammod.api.client.effect.post;

import net.minecraft.client.DeltaTracker;
import net.neoforged.bus.api.Event;

/**
 * 后处理着色器事件 —— 供主模 {@code GameRenderer} Mixin 分发、附属模组订阅追加后处理链
 * <p>
 * 借鉴开源模组 FDLib（作者 FINDERFEED）的 {@code FDRenderPostShaderEvent} 设计思路
 * （已确认其许可证禁止复制源码，此处为独立实现的同构设计）：
 * <ul>
 *   <li><b>{@link Level}</b>：世界帧（不含 GUI）已渲染、写入主渲染目标前触发，
 *       适合 ImpactFrame 打击帧、世界级后处理</li>
 *   <li><b>{@link Screen}</b>：整帧（含 GUI）合成后触发，适合全屏屏幕特效后处理
 *       （如色差 Chromatic Aberration）</li>
 * </ul>
 * <p>
 * 本类属于客户端专用代码，仅由 {@code api/client/**} 路径持有；API 服务端侧
 * Facade 不得引用本类，以保证专用服可安全加载。
 *
 * @see PostShaderManager
 */
public abstract class PostShaderEvent extends Event {

    /** 当前帧的 DeltaTracker（用于后处理 partialTick） */
    private final DeltaTracker deltaTracker;

    /**
     * 构造后处理事件
     *
     * @param deltaTracker 帧时间跟踪器
     */
    public PostShaderEvent(DeltaTracker deltaTracker) {
        this.deltaTracker = deltaTracker;
    }

    /**
     * 获取帧时间跟踪器
     *
     * @return 当前帧 DeltaTracker
     */
    public DeltaTracker getDeltaTracker() {
        return deltaTracker;
    }

    /**
     * 世界帧级后处理事件（GUI 合成前）
     */
    public static class Level extends PostShaderEvent {
        /**
         * 构造世界帧级事件
         *
         * @param deltaTracker 帧时间跟踪器
         */
        public Level(DeltaTracker deltaTracker) {
            super(deltaTracker);
        }
    }

    /**
     * 整帧级后处理事件（GUI 合成后）
     */
    public static class Screen extends PostShaderEvent {
        /**
         * 构造整帧级事件
         *
         * @param deltaTracker 帧时间跟踪器
         */
        public Screen(DeltaTracker deltaTracker) {
            super(deltaTracker);
        }
    }
}
