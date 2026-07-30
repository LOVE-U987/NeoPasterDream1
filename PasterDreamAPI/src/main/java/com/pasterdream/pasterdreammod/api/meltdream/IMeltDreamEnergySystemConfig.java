package com.pasterdream.pasterdreammod.api.meltdream;

import java.util.function.Supplier;

/**
 * 融梦能量系统配置接口。
 * <p>
 * 位于 PasterDreamAPI，由 PasterDreamMeltDream 模组实现并注册。
 * 主模组通过此接口以无硬依赖方式判断融梦能量系统是否启用，
 * 未注册时默认关闭，避免附属模组缺失时崩溃。
 */
public interface IMeltDreamEnergySystemConfig {

    /**
     * 是否启用融梦能量系统总开关。
     *
     * @return 开关 Supplier
     */
    Supplier<Boolean> enabled();

    /**
     * 融梦能量自然恢复间隔（tick）。
     *
     * @return 间隔 Supplier
     */
    Supplier<Integer> recoverInterval();

    /**
     * 融梦能量自然恢复量。
     *
     * @return 恢复量 Supplier
     */
    Supplier<Double> recoverAmount();

    /**
     * 融梦水晶箱自然产生能量的倍率。
     *
     * @return 倍率 Supplier
     */
    Supplier<Double> chestGenerationMultiplier();

    /**
     * 融梦水晶箱被攻击时能量产生倍率。
     *
     * @return 倍率 Supplier
     */
    Supplier<Double> chestHurtMultiplier();

    /**
     * 融梦水晶箱被杀死时能量产生倍率。
     *
     * @return 倍率 Supplier
     */
    Supplier<Double> chestKillMultiplier();

    /**
     * 融梦水晶箱能量转化上限。
     *
     * @return 上限 Supplier
     */
    Supplier<Double> chestMaxEnergy();
}
