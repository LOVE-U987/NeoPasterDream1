package com.pasterdream.pasterdreammod.api.san;

import java.util.function.Supplier;

/**
 * San 值系统配置接口。
 * <p>
 * 位于 PasterDreamAPI，由 PasterDreamSanity 模组实现并注册。
 * 主模组通过此接口以无硬依赖方式判断 San 系统是否启用，
 * 未注册时默认关闭，避免附属模组缺失时崩溃。
 */
public interface ISanSystemConfig {

    /**
     * 是否启用 San 值系统总开关。
     *
     * @return 开关 Supplier
     */
    Supplier<Boolean> enabled();

    /**
     * 是否启用低 San 值 debuff。
     *
     * @return 开关 Supplier
     */
    Supplier<Boolean> enableLowSanDebuff();

    /**
     * 是否启用黑夜降低 San 值。
     *
     * @return 开关 Supplier
     */
    Supplier<Boolean> overworldNightLowersSan();

    /**
     * 是否启用下界降低 San 值。
     *
     * @return 开关 Supplier
     */
    Supplier<Boolean> netherLowersSan();

    /**
     * 是否启用末地降低 San 值。
     *
     * @return 开关 Supplier
     */
    Supplier<Boolean> endLowersSan();

    /**
     * 是否启用雨天降低 San 值。
     *
     * @return 开关 Supplier
     */
    Supplier<Boolean> rainLowersSan();

    /**
     * 是否启用雷暴降低 San 值。
     *
     * @return 开关 Supplier
     */
    Supplier<Boolean> thunderLowersSan();

    /**
     * 获取 San 值自然恢复间隔（tick）。
     *
     * @return 间隔 Supplier
     */
    Supplier<Integer> recoverInterval();

    /**
     * 获取 San 值自然恢复量。
     *
     * @return 恢复量 Supplier
     */
    Supplier<Double> recoverAmount();

    /**
     * 获取振奋效果阈值。
     *
     * @return 阈值 Supplier
     */
    Supplier<Double> cheerupThreshold();

    /**
     * 获取 San 值系统总刻更新间隔（tick）。
     * <p>
     * 控制低 San debuff、环境修饰等逻辑的触发频率。
     *
     * @return 间隔 Supplier
     */
    Supplier<Integer> tickUpdateInterval();
}
