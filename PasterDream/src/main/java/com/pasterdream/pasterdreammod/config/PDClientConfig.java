package com.pasterdream.pasterdreammod.config;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * 客户端配置（PasterDream-Client.toml）
 * <p>
 * 移植自原版 {@code configuration/PasterdreamConfigClientConfiguration.java}
 * （ForgeConfigSpec → NeoForge ModConfigSpec），配置键、注释、默认值与原版完全一致
 * （含原版个别键末尾的空格，保持配置文件兼容）。
 * <p>
 * 由主类构造器通过 {@code modContainer.registerConfig(ModConfig.Type.CLIENT, SPEC,
 * "PasterDream-Client.toml")} 注册。
 */
public class PDClientConfig {

    /** 配置规格 */
    public static final ModConfigSpec SPEC;

    /** 仅在潜行时显示融梦能量条和精神值的 HUD 图标（默认 false） */
    public static final ModConfigSpec.ConfigValue<Boolean> STEALTH_DISPLAY_ATTRIBUTE_HUD;
    /** 在加载界面时弹出帕斯特之梦的 tips（默认 true） */
    public static final ModConfigSpec.ConfigValue<Boolean> LOADING_GUI_TIPS;
    /** 启用帕斯特之梦的主题生命值条（默认 true） */
    public static final ModConfigSpec.ConfigValue<Boolean> PASTER_HEALTH_HUD;
    /** 融梦能量条在屏幕上的 xBase 位置（默认 1.0） */
    public static final ModConfigSpec.ConfigValue<Double> MELTDREAMENERGY_TANK_XBASE;
    /** 融梦能量条在屏幕上的 yBase 位置（默认 -19.0） */
    public static final ModConfigSpec.ConfigValue<Double> MELTDREAMENERGY_TANK_YBASE;
    /** 精神值量条在屏幕上的 xBase 位置（默认 -36.0） */
    public static final ModConfigSpec.ConfigValue<Double> SAN_TANK_XBASE;
    /** 精神值量条在屏幕上的 yBase 位置（默认 -34.0） */
    public static final ModConfigSpec.ConfigValue<Double> SAN_TANK_YBASE;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.push("HUD");
        STEALTH_DISPLAY_ATTRIBUTE_HUD = builder
                .comment("仅在潜行时显示融梦能量条和精神值的HUD图标 默认：false")
                .define("stealth display attribute hud", false);
        LOADING_GUI_TIPS = builder
                .comment("在加载界面时会弹出帕斯特之梦的tips 默认：true")
                .define("loading gui tips", true);
        PASTER_HEALTH_HUD = builder
                .comment("启动帕斯特之梦的主题生命值条 默认：true")
                .define("paster health hud", true);
        // 注意：以下两个键末尾的空格为原版原样保留（保持 toml 键兼容）
        MELTDREAMENERGY_TANK_XBASE = builder
                .comment("融梦能量条在屏幕上的xBase位置 默认：1.0")
                .define("meltdreamenergy tank xbase ", 1d);
        MELTDREAMENERGY_TANK_YBASE = builder
                .comment("融梦能量条在屏幕上的yBase位置 默认：-19.0")
                .define("meltdreamenergy tank ybase ", -19d);
        SAN_TANK_XBASE = builder
                .comment("精神值量条在屏幕上的xBase位置 默认：-36.0")
                .define("san tank xbase", -36d);
        SAN_TANK_YBASE = builder
                .comment("精神值量条在屏幕上的yBase位置 默认：-34.0")
                .define("san tank ybase", -34d);
        builder.pop();

        SPEC = builder.build();
    }
}
