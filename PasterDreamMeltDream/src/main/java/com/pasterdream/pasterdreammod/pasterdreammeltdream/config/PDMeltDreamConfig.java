package com.pasterdream.pasterdreammod.pasterdreammeltdream.config;

import com.pasterdream.pasterdreammod.api.meltdream.IMeltDreamEnergySystemConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.function.Supplier;

/**
 * PasterDreamMeltDream 通用配置（PasterDreamMeltDream-Common.toml）。
 * <p>
 * 实现 {@link IMeltDreamEnergySystemConfig} 接口，向 PasterDreamAPI 注册后
 * 供主模组与其他附属模组以无硬依赖方式读取。
 *
 * @author PasterDream
 */
public class PDMeltDreamConfig implements IMeltDreamEnergySystemConfig {

    /** 配置规格 */
    public static final ModConfigSpec SPEC;

    private static final PDMeltDreamConfig INSTANCE = new PDMeltDreamConfig();

    private static final ModConfigSpec.ConfigValue<Boolean> ENABLE_MELTDREAM_SYSTEM;
    private static final ModConfigSpec.ConfigValue<Integer> RECOVER_INTERVAL;
    private static final ModConfigSpec.ConfigValue<Double> RECOVER_AMOUNT;
    private static final ModConfigSpec.ConfigValue<Double> CHEST_GENERATION_MULTIPLIER;
    private static final ModConfigSpec.ConfigValue<Double> CHEST_HURT_MULTIPLIER;
    private static final ModConfigSpec.ConfigValue<Double> CHEST_KILL_MULTIPLIER;
    private static final ModConfigSpec.ConfigValue<Double> CHEST_MAX_ENERGY;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.push("MeltDream");
        ENABLE_MELTDREAM_SYSTEM = builder
                .comment("是否启用融梦能量系统总开关 默认：true")
                .define("enable meltdream system", true);
        RECOVER_INTERVAL = builder
                .comment("融梦能量自然恢复间隔（tick） 默认：1200")
                .defineInRange("recover interval", 1200, 1, Integer.MAX_VALUE);
        RECOVER_AMOUNT = builder
                .comment("融梦能量自然恢复量 默认：0.1")
                .defineInRange("recover amount", 0.1D, 0.0D, Double.MAX_VALUE);
        CHEST_GENERATION_MULTIPLIER = builder
                .comment("融梦水晶箱自然产生能量的倍率 默认：1.0")
                .defineInRange("chest generation multiplier", 1.0D, 0.0D, Double.MAX_VALUE);
        CHEST_HURT_MULTIPLIER = builder
                .comment("融梦水晶箱被攻击时能量产生倍率 默认：1.0")
                .defineInRange("chest hurt multiplier", 1.0D, 0.0D, Double.MAX_VALUE);
        CHEST_KILL_MULTIPLIER = builder
                .comment("融梦水晶箱被杀死时能量产生倍率 默认：1.0")
                .defineInRange("chest kill multiplier", 1.0D, 0.0D, Double.MAX_VALUE);
        CHEST_MAX_ENERGY = builder
                .comment("融梦水晶箱能量转化上限 默认：1000.0")
                .defineInRange("chest max energy", 1000.0D, 0.0D, Double.MAX_VALUE);
        builder.pop();

        SPEC = builder.build();
    }

    private PDMeltDreamConfig() {
    }

    /**
     * 获取配置单例。
     *
     * @return 配置实例
     */
    public static PDMeltDreamConfig getInstance() {
        return INSTANCE;
    }

    @Override
    public Supplier<Boolean> enabled() {
        return ENABLE_MELTDREAM_SYSTEM;
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
    public Supplier<Double> chestGenerationMultiplier() {
        return CHEST_GENERATION_MULTIPLIER;
    }

    @Override
    public Supplier<Double> chestHurtMultiplier() {
        return CHEST_HURT_MULTIPLIER;
    }

    @Override
    public Supplier<Double> chestKillMultiplier() {
        return CHEST_KILL_MULTIPLIER;
    }

    @Override
    public Supplier<Double> chestMaxEnergy() {
        return CHEST_MAX_ENERGY;
    }
}
