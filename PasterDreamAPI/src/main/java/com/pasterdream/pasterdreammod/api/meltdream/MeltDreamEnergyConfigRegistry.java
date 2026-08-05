package com.pasterdream.pasterdreammod.api.meltdream;

import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

/**
 * 融梦能量系统配置注册表。
 * <p>
 * 由 PasterDreamMeltDream 模组在启动时注册，主模组通过 {@link #get()} 获取配置。
 * 未注册时返回空实现（所有开关默认 false），确保主模组单独运行时不会崩溃。
 */
public final class MeltDreamEnergyConfigRegistry {

    /** 已注册配置实例 */
    private static final AtomicReference<IMeltDreamEnergySystemConfig> CONFIG = new AtomicReference<>();

    /** 空实现：所有开关默认关闭 */
    private static final IMeltDreamEnergySystemConfig EMPTY = new IMeltDreamEnergySystemConfig() {
        @Override
        public Supplier<Boolean> enabled() { return () -> false; }
        @Override
        public Supplier<Integer> recoverInterval() { return () -> Integer.MAX_VALUE; }
        @Override
        public Supplier<Double> recoverAmount() { return () -> 0.0D; }
        @Override
        public Supplier<Double> chestGenerationMultiplier() { return () -> 0.0D; }
        @Override
        public Supplier<Double> chestHurtMultiplier() { return () -> 0.0D; }
        @Override
        public Supplier<Double> chestKillMultiplier() { return () -> 0.0D; }
        @Override
        public Supplier<Double> chestMaxEnergy() { return () -> 0.0D; }
        @Override
        public Supplier<Integer> chestCooldownTicks() { return () -> 12000; }
    };

    private MeltDreamEnergyConfigRegistry() {
        throw new UnsupportedOperationException("MeltDreamEnergyConfigRegistry 是静态注册表，不可实例化");
    }

    /**
     * 注册融梦能量系统配置。
     *
     * @param config 配置实现
     */
    public static void register(@Nullable IMeltDreamEnergySystemConfig config) {
        CONFIG.set(config);
    }

    /**
     * 获取当前融梦能量系统配置。
     *
     * @return 配置实例；未注册时返回空实现
     */
    public static IMeltDreamEnergySystemConfig get() {
        IMeltDreamEnergySystemConfig config = CONFIG.get();
        return config != null ? config : EMPTY;
    }

    /**
     * 检查是否已有配置注册。
     *
     * @return true 表示已注册
     */
    public static boolean isRegistered() {
        return CONFIG.get() != null;
    }
}
