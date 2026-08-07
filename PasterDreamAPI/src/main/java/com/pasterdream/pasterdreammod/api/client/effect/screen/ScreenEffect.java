package com.pasterdream.pasterdreammod.api.client.effect.screen;

import com.pasterdream.pasterdreammod.api.effect.screen.ScreenEffectData;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * 屏幕特效抽象基类 —— 管理 in/stay/out 三阶段生命周期并提供渲染钩子
 * <p>
 * 借鉴开源模组 FDLib（作者 FINDERFEED）的 {@code ScreenEffect} 设计思路
 * （独立实现，非复制）：
 * <ul>
 *   <li><b>inTime</b>：渐入阶段（alpha 0→1）；</li>
 *   <li><b>stayTime</b>：持续阶段（alpha 1）；</li>
 *   <li><b>outTime</b>：渐出阶段（alpha 1→0）。</li>
 * </ul>
 * 子类实现 {@link #render} 绘制全屏效果（GUI 空间），由
 * {@link ScreenEffectOverlay} 统一调度。
 * <p>
 * 本类为客户端专用（引用 {@link GuiGraphics}），仅由 {@code api/client/**} 持有。
 *
 * @param <T> 特效数据实现
 */
@OnlyIn(Dist.CLIENT)
public abstract class ScreenEffect<T extends ScreenEffectData> {

    private final int inTime;
    private final int stayTime;
    private final int outTime;
    private final T data;

    /**
     * 构造屏幕特效
     *
     * @param data     特效数据
     * @param inTime   渐入 tick 数
     * @param stayTime 持续 tick 数
     * @param outTime  渐出 tick 数
     */
    public ScreenEffect(T data, int inTime, int stayTime, int outTime) {
        this.data = data;
        this.inTime = inTime;
        this.stayTime = stayTime;
        this.outTime = outTime;
    }

    /**
     * 渲染特效（GUI 空间，全屏）
     *
     * @param graphics     GUI 绘制器
     * @param deltaTracker 帧时间跟踪器
     * @param currentTick  特效已运行 tick 数
     * @param screenWidth  屏幕宽（缩放后）
     * @param screenHeight 屏幕高（缩放后）
     */
    public abstract void render(GuiGraphics graphics, DeltaTracker deltaTracker,
                                int currentTick, float screenWidth, float screenHeight);

    /**
     * 是否处于渐入阶段
     *
     * @param currentTick 当前 tick
     * @return 渐入返回 {@code true}
     */
    public boolean isInTime(int currentTick) {
        return inTime > 0 && currentTick <= inTime;
    }

    /**
     * 是否处于持续阶段
     *
     * @param currentTick 当前 tick
     * @return 持续返回 {@code true}
     */
    public boolean isStayTime(int currentTick) {
        if (stayTime == 0) {
            return false;
        }
        int time = currentTick - inTime;
        return time > 0 && time <= stayTime;
    }

    /**
     * 是否处于渐出阶段
     *
     * @param currentTick 当前 tick
     * @return 渐出返回 {@code true}
     */
    public boolean isOutTime(int currentTick) {
        if (outTime == 0) {
            return false;
        }
        int time = currentTick - inTime - stayTime;
        return time > 0 && time <= outTime;
    }

    /**
     * 渐入进度百分比（0~1）
     *
     * @param currentTick 当前 tick
     * @param pticks      部分 tick
     * @return 进度
     */
    public float getInTimePercent(int currentTick, float pticks) {
        if (inTime == 0) {
            return 0;
        }
        return Mth.clamp((currentTick + pticks) / inTime, 0, 1);
    }

    /**
     * 渐出进度百分比（0~1）
     *
     * @param currentTick 当前 tick
     * @param pticks      部分 tick
     * @return 进度
     */
    public float getOutTimePercent(int currentTick, float pticks) {
        if (outTime == 0) {
            return 0;
        }
        float time = currentTick + pticks - inTime - stayTime;
        return Mth.clamp(time / outTime, 0, 1);
    }

    /**
     * 特效总生命周期（tick 数）
     *
     * @return 总时长
     */
    public int getLifetime() {
        return inTime + stayTime + outTime;
    }

    /**
     * 获取特效数据
     *
     * @return 数据
     */
    public T getData() {
        return data;
    }
}
