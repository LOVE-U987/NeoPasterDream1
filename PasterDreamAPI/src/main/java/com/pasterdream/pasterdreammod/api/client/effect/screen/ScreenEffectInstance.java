package com.pasterdream.pasterdreammod.api.client.effect.screen;

import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * 屏幕特效实例 —— 包装特效与当前运行时间
 * <p>
 * 借鉴开源模组 FDLib（作者 FINDERFEED）的 {@code ScreenEffectInstance} 设计思路
 * （独立实现，非复制）。由 {@link ScreenEffectOverlay} 持有，每 tick 推进
 * {@code currentTime}，生命周期结束时移除。
 */
@OnlyIn(Dist.CLIENT)
public class ScreenEffectInstance {

    private final ScreenEffect<?> effect;
    private int currentTime;

    /**
     * 构造实例
     *
     * @param effect 特效
     */
    public ScreenEffectInstance(ScreenEffect<?> effect) {
        this.effect = effect;
        this.currentTime = 0;
    }

    /**
     * 每 tick 推进时间
     */
    public void tick() {
        this.currentTime = Mth.clamp(currentTime + 1, 0, effect.getLifetime());
    }

    /**
     * 生命周期是否已结束
     *
     * @return 结束返回 {@code true}
     */
    public boolean isFinished() {
        return currentTime >= effect.getLifetime();
    }

    /**
     * 获取当前运行时间
     *
     * @return tick 数
     */
    public int getCurrentTime() {
        return currentTime;
    }

    /**
     * 获取特效
     *
     * @return 特效
     */
    public ScreenEffect<?> getEffect() {
        return effect;
    }
}
