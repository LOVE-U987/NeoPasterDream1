package com.pasterdream.pasterdreammod.pasterdreamspells.config;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * PasterDreamSpells 通用配置（PasterDreamSpells-Common.toml）。
 * <p>
 * 负责法术系统相关参数，如法术冷却、消耗倍率等。
 *
 * @author PasterDream
 */
public class PDSpellsConfig {

    /** 配置规格 */
    public static final ModConfigSpec SPEC;

    private static final ModConfigSpec.ConfigValue<Boolean> ENABLE_SPELL_SYSTEM;
    private static final ModConfigSpec.ConfigValue<Double> SPELL_COST_MULTIPLIER;
    private static final ModConfigSpec.ConfigValue<Double> SPELL_COOLDOWN_MULTIPLIER;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.push("Spells");
        ENABLE_SPELL_SYSTEM = builder
                .comment("是否启用法术系统总开关 默认：true")
                .define("enable spell system", true);
        SPELL_COST_MULTIPLIER = builder
                .comment("法术消耗倍率 默认：1.0")
                .defineInRange("spell cost multiplier", 1.0D, 0.0D, Double.MAX_VALUE);
        SPELL_COOLDOWN_MULTIPLIER = builder
                .comment("法术冷却倍率 默认：1.0")
                .defineInRange("spell cooldown multiplier", 1.0D, 0.0D, Double.MAX_VALUE);
        builder.pop();

        SPEC = builder.build();
    }

    private PDSpellsConfig() {
    }

    /**
     * 是否启用法术系统。
     *
     * @return 开关 Supplier
     */
    public static ModConfigSpec.ConfigValue<Boolean> enableSpellSystem() {
        return ENABLE_SPELL_SYSTEM;
    }

    /**
     * 获取法术消耗倍率。
     *
     * @return 倍率 Supplier
     */
    public static ModConfigSpec.ConfigValue<Double> spellCostMultiplier() {
        return SPELL_COST_MULTIPLIER;
    }

    /**
     * 获取法术冷却倍率。
     *
     * @return 倍率 Supplier
     */
    public static ModConfigSpec.ConfigValue<Double> spellCooldownMultiplier() {
        return SPELL_COOLDOWN_MULTIPLIER;
    }
}
