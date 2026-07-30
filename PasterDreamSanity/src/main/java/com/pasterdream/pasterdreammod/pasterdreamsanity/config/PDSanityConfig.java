package com.pasterdream.pasterdreammod.pasterdreamsanity.config;

import com.pasterdream.pasterdreammod.api.san.ISanSystemConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.function.Supplier;

/**
 * PasterDreamSanity 通用配置（PasterDreamSanity-Common.toml）。
 * <p>
 * 实现 {@link ISanSystemConfig} 接口，向 PasterDreamAPI 注册后
 * 供主模组与其他附属模组以无硬依赖方式读取。
 *
 * @author PasterDream
 */
public class PDSanityConfig implements ISanSystemConfig {

    /** 配置规格 */
    public static final ModConfigSpec SPEC;

    private static final PDSanityConfig INSTANCE = new PDSanityConfig();

    private static final ModConfigSpec.ConfigValue<Boolean> ENABLE_SAN_SYSTEM;
    private static final ModConfigSpec.ConfigValue<Boolean> ENABLE_LOW_SAN_DEBUFF;
    private static final ModConfigSpec.ConfigValue<Boolean> OVERWORLD_NIGHT_LOWERS_SAN;
    private static final ModConfigSpec.ConfigValue<Boolean> NETHER_LOWERS_SAN;
    private static final ModConfigSpec.ConfigValue<Boolean> END_LOWERS_SAN;
    private static final ModConfigSpec.ConfigValue<Boolean> RAIN_LOWERS_SAN;
    private static final ModConfigSpec.ConfigValue<Boolean> THUNDER_LOWERS_SAN;
    private static final ModConfigSpec.ConfigValue<Integer> RECOVER_INTERVAL;
    private static final ModConfigSpec.ConfigValue<Double> RECOVER_AMOUNT;
    private static final ModConfigSpec.ConfigValue<Double> CHEERUP_THRESHOLD;
    private static final ModConfigSpec.ConfigValue<Integer> TICK_UPDATE_INTERVAL;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.push("San");
        ENABLE_SAN_SYSTEM = builder
                .comment("是否启用 San 值系统总开关 默认：true")
                .define("enable san system", true);
        ENABLE_LOW_SAN_DEBUFF = builder
                .comment("是否启用低 San 值 debuff 默认：true")
                .define("enable low san debuff", true);
        OVERWORLD_NIGHT_LOWERS_SAN = builder
                .comment("主世界夜晚是否降低 San 值 默认：true")
                .define("overworld night lowers san", true);
        NETHER_LOWERS_SAN = builder
                .comment("下界是否降低 San 值 默认：true")
                .define("nether lowers san", true);
        END_LOWERS_SAN = builder
                .comment("末地是否降低 San 值 默认：true")
                .define("end lowers san", true);
        RAIN_LOWERS_SAN = builder
                .comment("雨天是否降低 San 值 默认：true")
                .define("rain lowers san", true);
        THUNDER_LOWERS_SAN = builder
                .comment("雷暴是否降低 San 值 默认：true")
                .define("thunder lowers san", true);
        RECOVER_INTERVAL = builder
                .comment("San 值自然恢复间隔（tick） 默认：1200")
                .defineInRange("recover interval", 1200, 1, Integer.MAX_VALUE);
        RECOVER_AMOUNT = builder
                .comment("San 值自然恢复量 默认：0.1")
                .defineInRange("recover amount", 0.1D, 0.0D, Double.MAX_VALUE);
        CHEERUP_THRESHOLD = builder
                .comment("振奋效果阈值 默认：80.0")
                .defineInRange("cheerup threshold", 80.0D, 0.0D, Double.MAX_VALUE);
        TICK_UPDATE_INTERVAL = builder
                .comment("San 值系统总刻更新间隔（tick），过快的更新频率可能会影响性能，过慢的频率会让部分功能显得迟钝，推荐范围[2~20] 默认：5")
                .defineInRange("tick update interval", 5, 1, Integer.MAX_VALUE);
        builder.pop();

        SPEC = builder.build();
    }

    private PDSanityConfig() {
    }

    /**
     * 获取配置单例。
     *
     * @return 配置实例
     */
    public static PDSanityConfig getInstance() {
        return INSTANCE;
    }

    @Override
    public Supplier<Boolean> enabled() {
        return ENABLE_SAN_SYSTEM;
    }

    @Override
    public Supplier<Boolean> enableLowSanDebuff() {
        return ENABLE_LOW_SAN_DEBUFF;
    }

    @Override
    public Supplier<Boolean> overworldNightLowersSan() {
        return OVERWORLD_NIGHT_LOWERS_SAN;
    }

    @Override
    public Supplier<Boolean> netherLowersSan() {
        return NETHER_LOWERS_SAN;
    }

    @Override
    public Supplier<Boolean> endLowersSan() {
        return END_LOWERS_SAN;
    }

    @Override
    public Supplier<Boolean> rainLowersSan() {
        return RAIN_LOWERS_SAN;
    }

    @Override
    public Supplier<Boolean> thunderLowersSan() {
        return THUNDER_LOWERS_SAN;
    }

    @Override
    public Supplier<Integer> recoverInterval() {
        return RECOVER_INTERVAL;
    }

    @Override
    public Supplier<Double> recoverAmount() {
        return RECOVER_AMOUNT;
    }

    @Override
    public Supplier<Double> cheerupThreshold() {
        return CHEERUP_THRESHOLD;
    }

    @Override
    public Supplier<Integer> tickUpdateInterval() {
        return TICK_UPDATE_INTERVAL;
    }
}
