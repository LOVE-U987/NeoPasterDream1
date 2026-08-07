package com.pasterdream.pasterdreammod.api.client.effect.impact;

import com.pasterdream.pasterdreammod.api.client.effect.post.PostShaderEvent;
import com.pasterdream.pasterdreammod.api.client.effect.post.PostShaderManager;
import com.pasterdream.pasterdreammod.api.effect.impact.ImpactFrame;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.PostChain;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * 客户端打击帧处理器 —— 维护打击帧队列并驱动后处理链
 * <p>
 * 借鉴开源模组 FDLib（作者 FINDERFEED）的 {@code ImpactFramesHandler} 设计思路
 * （独立实现，非复制）：
 * <ul>
 *   <li>活跃打击帧为单个 {@link ImpactFrame}，队列排多个帧依次播放；</li>
 *   <li>每个 tick 递减帧持续时间，期间在 {@link PostShaderEvent.Level} 阶段
 *       设置 impact_frame 后处理链的 uniform 并处理；</li>
 *   <li>由主模 {@code PDEffectClientEvents} 在客户端 tick 调 {@link #tick()}，
 *       在 {@link PostShaderEvent.Level} 调 {@link #renderLevel(PostShaderEvent.Level)}。</li>
 * </ul>
 * 本类为客户端专用，仅由 {@code api/client/**} 路径持有。
 */
@OnlyIn(Dist.CLIENT)
public final class ImpactFramesHandler {

    /** impact_frame 后处理链的注册 key（主模 PDShaderBootstrap 注册） */
    public static final String CHAIN_ID = "pasterdream:shaders/post/impact_frame.json";

    private static final Queue<ImpactFrame> QUEUE = new ArrayDeque<>();
    private static ImpactFrame current;
    private static int currentTick;

    private ImpactFramesHandler() {
        throw new UnsupportedOperationException("ImpactFramesHandler 是纯静态门面类，不可实例化");
    }

    /**
     * 加入打击帧队列（网络包落地时调用）
     *
     * @param frame 打击帧
     */
    public static void add(ImpactFrame frame) {
        QUEUE.offer(frame);
    }

    /**
     * 加入多个打击帧（网络包落地时调用）
     *
     * @param frames 打击帧序列
     */
    public static void addAll(Iterable<ImpactFrame> frames) {
        for (ImpactFrame frame : frames) {
            QUEUE.offer(frame);
        }
    }

    /**
     * 是否有活跃打击帧
     *
     * @return 有返回 {@code true}
     */
    public static boolean isActive() {
        return current != null;
    }

    /**
     * 客户端每 tick 推进打击帧队列（由 PDEffectClientEvents 调用）
     */
    public static void tick() {
        if (current != null) {
            currentTick++;
            if (currentTick >= current.duration()) {
                current = null;
                currentTick = 0;
                current = QUEUE.poll();
                if (current != null) {
                    currentTick = 1;
                }
            }
        } else {
            current = QUEUE.poll();
            if (current != null) {
                currentTick = 1;
            }
        }
    }

    /**
     * 世界帧级后处理：设置 impact_frame uniform 并处理
     *
     * @param event 世界帧级后处理事件
     */
    public static void renderLevel(PostShaderEvent.Level event) {
        if (current == null) {
            return;
        }
        PostChain chain = PostShaderManager.getChain(
                net.minecraft.resources.ResourceLocation.parse(CHAIN_ID));
        if (chain == null) {
            return;
        }
        DeltaTracker delta = event.getDeltaTracker();
        chain.setUniform("threshold", current.threshold());
        chain.setUniform("thresholdLerp", current.thresholdLerp());
        chain.setUniform("invert", current.invert() ? 1.0f : 0.0f);
        chain.process(delta.getGameTimeDeltaPartialTick(false));
    }

    /**
     * 玩家登出/世界卸载时清空
     */
    public static void clearAll() {
        QUEUE.clear();
        current = null;
        currentTick = 0;
    }

    /**
     * 测试辅助：清空队列
     */
    public static void resetForTesting() {
        clearAll();
    }
}
