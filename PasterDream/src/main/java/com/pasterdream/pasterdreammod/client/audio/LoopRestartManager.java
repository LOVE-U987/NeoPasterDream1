package com.pasterdream.pasterdreammod.client.audio;

/**
 * 循环重播管理器 —— 委托 {@link com.pasterdream.pasterdreammod.api.audio.LoopRestartManager}。
 * <p>
 * 主模保留同名类型便于历史引用；新代码可直接使用 API 类。
 */
public class LoopRestartManager extends com.pasterdream.pasterdreammod.api.audio.LoopRestartManager {

    /**
     * @param sameBiomeMinInterval  同群系最小间隔
     * @param sameBiomeMaxInterval  同群系最大间隔
     * @param crossBiomeMinInterval 群系切换后最小间隔
     * @param crossBiomeMaxInterval 群系切换后最大间隔
     */
    public LoopRestartManager(int sameBiomeMinInterval, int sameBiomeMaxInterval,
                              int crossBiomeMinInterval, int crossBiomeMaxInterval) {
        super(sameBiomeMinInterval, sameBiomeMaxInterval, crossBiomeMinInterval, crossBiomeMaxInterval);
    }
}
