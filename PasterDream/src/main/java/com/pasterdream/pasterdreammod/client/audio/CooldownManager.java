package com.pasterdream.pasterdreammod.client.audio;

/**
 * 切换冷却管理器 —— 委托 {@link com.pasterdream.pasterdreammod.api.audio.CooldownManager}。
 * <p>
 * 主模保留同名类型便于历史引用；新代码可直接使用 API 类。
 */
public class CooldownManager extends com.pasterdream.pasterdreammod.api.audio.CooldownManager {

    /**
     * @param switchCooldownTicks 冷却 tick 数（20 tick ≈ 1 秒），至少 1 tick
     */
    public CooldownManager(int switchCooldownTicks) {
        super(switchCooldownTicks);
    }
}
