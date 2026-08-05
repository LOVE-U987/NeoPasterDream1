package com.pasterdream.pasterdreammod.pasterdreammeltdream.config;

import com.pasterdream.pasterdreammod.api.config.PDAddonConfigRegistry;
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
    private static final ModConfigSpec.ConfigValue<Integer> CHEST_COOLDOWN;

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
        CHEST_COOLDOWN = builder
                .comment("融梦水晶箱玩家开箱冷却时长（tick） 默认：12000（10 分钟）")
                .defineInRange("chest cooldown", 12000, 1, Integer.MAX_VALUE);
        builder.pop();

        // 向主模组配置界面注册本模组配置项
        registerConfigScreenEntries();

        SPEC = builder.build();
    }

    /**
     * 将本模组的配置项注册到 PasterDreamAPI 的附属配置注册表，
     * 供主模组配置界面统一展示与保存。
     */
    private static void registerConfigScreenEntries() {
        PDAddonConfigRegistry.registerBooleanEntry("pasterdreammeltdream", "meltdream_system", ENABLE_MELTDREAM_SYSTEM, "enable_meltdream_system");
        PDAddonConfigRegistry.registerIntegerEntry("pasterdreammeltdream", "meltdream_system", RECOVER_INTERVAL, 1, Integer.MAX_VALUE, "meltdream_recover_interval");
        PDAddonConfigRegistry.registerDoubleEntry("pasterdreammeltdream", "meltdream_system", RECOVER_AMOUNT, 0.0D, Double.MAX_VALUE, "meltdream_recover_amount");
        PDAddonConfigRegistry.registerDoubleEntry("pasterdreammeltdream", "meltdream_system", CHEST_GENERATION_MULTIPLIER, 0.0D, Double.MAX_VALUE, "meltdream_chest_generation_multiplier");
        PDAddonConfigRegistry.registerDoubleEntry("pasterdreammeltdream", "meltdream_system", CHEST_HURT_MULTIPLIER, 0.0D, Double.MAX_VALUE, "meltdream_chest_hurt_multiplier");
        PDAddonConfigRegistry.registerDoubleEntry("pasterdreammeltdream", "meltdream_system", CHEST_KILL_MULTIPLIER, 0.0D, Double.MAX_VALUE, "meltdream_chest_kill_multiplier");
        PDAddonConfigRegistry.registerDoubleEntry("pasterdreammeltdream", "meltdream_system", CHEST_MAX_ENERGY, 0.0D, Double.MAX_VALUE, "meltdream_chest_max_energy");
        PDAddonConfigRegistry.registerIntegerEntry("pasterdreammeltdream", "meltdream_system", CHEST_COOLDOWN, 1, Integer.MAX_VALUE, "meltdream_chest_cooldown");
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

    @Override
    public Supplier<Integer> chestCooldownTicks() {
        return CHEST_COOLDOWN;
    }
}
