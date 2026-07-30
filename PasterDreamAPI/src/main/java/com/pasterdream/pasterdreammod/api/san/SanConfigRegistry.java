package com.pasterdream.pasterdreammod.api.san;

import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

/**
 * San 值系统配置注册表。
 * <p>
 * 由 PasterDreamSanity 模组在启动时注册，主模组通过 {@link #get()} 获取配置。
 * 未注册时返回空实现（所有开关默认 false），确保主模组单独运行时不会崩溃。
 */
public final class SanConfigRegistry {

    /** 已注册配置实例 */
    private static final AtomicReference<ISanSystemConfig> CONFIG = new AtomicReference<>();

    /** 空实现：所有开关默认关闭 */
    private static final ISanSystemConfig EMPTY = new ISanSystemConfig() {
        @Override
        public Supplier<Boolean> enabled() { return () -> false; }
        @Override
        public Supplier<Boolean> enableLowSanDebuff() { return () -> false; }
        @Override
        public Supplier<Boolean> overworldNightLowersSan() { return () -> false; }
        @Override
        public Supplier<Boolean> netherLowersSan() { return () -> false; }
        @Override
        public Supplier<Boolean> endLowersSan() { return () -> false; }
        @Override
        public Supplier<Boolean> rainLowersSan() { return () -> false; }
        @Override
        public Supplier<Boolean> thunderLowersSan() { return () -> false; }
        @Override
        public Supplier<Integer> recoverInterval() { return () -> Integer.MAX_VALUE; }
        @Override
        public Supplier<Double> recoverAmount() { return () -> 0.0D; }
        @Override
        public Supplier<Double> cheerupThreshold() { return () -> 99.0D; }
        @Override
        public Supplier<Integer> tickUpdateInterval() { return () -> Integer.MAX_VALUE; }
    };

    private SanConfigRegistry() {
        throw new UnsupportedOperationException("SanConfigRegistry 是静态注册表，不可实例化");
    }

    /**
     * 注册 San 系统配置。
     *
     * @param config 配置实现
     */
    public static void register(@Nullable ISanSystemConfig config) {
        CONFIG.set(config);
    }

    /**
     * 获取当前 San 系统配置。
     *
     * @return 配置实例；未注册时返回空实现
     */
    public static ISanSystemConfig get() {
        ISanSystemConfig config = CONFIG.get();
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
