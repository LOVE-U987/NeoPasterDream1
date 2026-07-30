package com.pasterdream.pasterdreammod.api.audio;

import java.util.concurrent.ThreadLocalRandom;

/**
 * 循环重播管理器 —— 控制 BGM 播放完毕后的重播间隔。
 * <p>
 * 职责：
 * <ul>
 *   <li>检测 BGM 是否已播放完毕</li>
 *   <li>根据群系是否发生过切换选择不同的间隔范围</li>
 *   <li>在间隔到期后通知外部执行重播</li>
 * </ul>
 * <p>
 * 间隔策略：
 * <ul>
 *   <li>同群系内：较长间隔（如 1200~1800 tick）</li>
 *   <li>群系切换后：较短间隔（如 600~1200 tick）</li>
 * </ul>
 * <p>
 * 纯 Java 逻辑，无客户端依赖。
 */
public class LoopRestartManager {

    private final int sameBiomeMinInterval;
    private final int sameBiomeMaxInterval;
    private final int crossBiomeMinInterval;
    private final int crossBiomeMaxInterval;

    /** 是否在等待循环间隔 */
    private boolean isWaiting = false;

    /** 循环间隔开始的游戏 tick 数 */
    private long startTick = 0;

    /** 当前循环等待的间隔 tick 数 */
    private int delayTicks;

    /** 标记当前BGM播放期间是否发生过群系切换（用于决定下次播放的间隔） */
    private boolean biomeChangedDuringPlay = false;

    /**
     * @param sameBiomeMinInterval  同群系最小间隔
     * @param sameBiomeMaxInterval  同群系最大间隔
     * @param crossBiomeMinInterval 群系切换后最小间隔
     * @param crossBiomeMaxInterval 群系切换后最大间隔
     */
    public LoopRestartManager(int sameBiomeMinInterval, int sameBiomeMaxInterval,
                              int crossBiomeMinInterval, int crossBiomeMaxInterval) {
        this.sameBiomeMinInterval = sameBiomeMinInterval;
        this.sameBiomeMaxInterval = sameBiomeMaxInterval;
        this.crossBiomeMinInterval = crossBiomeMinInterval;
        this.crossBiomeMaxInterval = crossBiomeMaxInterval;
    }

    /**
     * 标记群系已变化（BGM 播放期间发生了群系切换）
     */
    public void markBiomeChanged() {
        this.biomeChangedDuringPlay = true;
    }

    /**
     * 每 tick 更新。
     *
     * @param isMusicActive 当前 BGM 是否仍在 SoundManager 中活跃
     * @param gameTick      当前游戏 tick
     * @return true 表示间隔已到期，调用方应执行 restart
     */
    public boolean update(boolean isMusicActive, long gameTick) {
        if (isMusicActive) {
            // 仍在播放 → 取消等待
            isWaiting = false;
            return false;
        }

        if (!isWaiting) {
            startWaiting(gameTick);
            return false;
        }

        if (gameTick - startTick >= delayTicks) {
            isWaiting = false;
            biomeChangedDuringPlay = false;
            return true;
        }
        return false;
    }

    /**
     * 开始等待重播间隔。
     */
    public void startWaiting(long gameTick) {
        this.isWaiting = true;
        this.startTick = gameTick;
        if (biomeChangedDuringPlay) {
            this.delayTicks = randomBetween(crossBiomeMinInterval, crossBiomeMaxInterval);
        } else {
            this.delayTicks = randomBetween(sameBiomeMinInterval, sameBiomeMaxInterval);
        }
    }

    /**
     * 重置全部等待状态。
     */
    public void reset() {
        isWaiting = false;
        startTick = 0;
        delayTicks = 0;
        biomeChangedDuringPlay = false;
    }

    public boolean isWaiting() {
        return isWaiting;
    }

    private static int randomBetween(int min, int max) {
        if (max <= min) {
            return min;
        }
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }
}
