package com.pasterdream.pasterdreammod.config;

import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.function.Supplier;

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

    /** 位置锚点：左上角 */
    public static final int ANCHOR_TOP_LEFT = 0;
    /** 位置锚点：右上角 */
    public static final int ANCHOR_TOP_RIGHT = 1;
    /** 位置锚点：左下角 */
    public static final int ANCHOR_BOTTOM_LEFT = 2;
    /** 位置锚点：右下角 */
    public static final int ANCHOR_BOTTOM_RIGHT = 3;

    // ==================== HUD 显示配置 ====================

    /** 仅在潜行时显示融梦能量条和精神值的 HUD 图标（默认 false） */
    public static final ModConfigSpec.ConfigValue<Boolean> STEALTH_DISPLAY_ATTRIBUTE_HUD;
    /** 在加载界面时弹出帕斯特之梦的 tips（默认 true） */
    public static final ModConfigSpec.ConfigValue<Boolean> LOADING_GUI_TIPS;
    /** 启用帕斯特之梦的主题生命值条（默认 true） */
    public static final ModConfigSpec.ConfigValue<Boolean> PASTER_HEALTH_HUD;
    /**
     * 启用模组对原版 UI 的修改（默认 true）
     * <p>
     * 仅控制帕斯特之梦对原版界面的替换/覆盖部分（主题生命值条、自定义 BOSS 血条）。
     * 关闭后恢复原版血条与 BOSS 血条显示；模组独立 HUD（融梦能量条、San 条、
     * 云雾/失智全屏效果等）不受此开关影响。
     */
    public static final ModConfigSpec.ConfigValue<Boolean> ENABLE_MOD_UI;

    // ==================== San 精神值配置 ====================

    /** 显示 San 精神值条（默认 true） */
    public static final ModConfigSpec.ConfigValue<Boolean> SHOW_SAN_HUD;
    /** 精神值量条在屏幕上的 xBase 位置（默认 -36.0） */
    public static final ModConfigSpec.ConfigValue<Double> SAN_TANK_XBASE;
    /** 精神值量条在屏幕上的 yBase 位置（默认 -34.0） */
    public static final ModConfigSpec.ConfigValue<Double> SAN_TANK_YBASE;
    /** San 精神值条缩放倍率（0.5 ~ 2.0，默认 1.0） */
    public static final ModConfigSpec.ConfigValue<Double> SAN_HUD_SCALE;
    /** San 精神值条位置锚点（0=左上 1=右上 2=左下 3=右下，默认 3） */
    public static final ModConfigSpec.ConfigValue<Integer> SAN_HUD_ANCHOR;
    /** 始终显示 San 数值文本（默认 false，仍可在潜行时查看） */
    public static final ModConfigSpec.ConfigValue<Boolean> SAN_SHOW_VALUE_ALWAYS;

    // ==================== 融梦能量配置 ====================

    /** 显示融梦能量条（默认 true） */
    public static final ModConfigSpec.ConfigValue<Boolean> SHOW_MELTDREAM_ENERGY_HUD;
    /** 融梦能量条在屏幕上的 xBase 位置（默认 1.0） */
    public static final ModConfigSpec.ConfigValue<Double> MELTDREAMENERGY_TANK_XBASE;
    /** 融梦能量条在屏幕上的 yBase 位置（默认 -19.0） */
    public static final ModConfigSpec.ConfigValue<Double> MELTDREAMENERGY_TANK_YBASE;
    /** 融梦能量条缩放倍率（0.5 ~ 2.0，默认 1.0） */
    public static final ModConfigSpec.ConfigValue<Double> MELTDREAM_ENERGY_HUD_SCALE;
    /** 融梦能量条位置锚点（0=左上 1=右上 2=左下 3=右下，默认 2） */
    public static final ModConfigSpec.ConfigValue<Integer> MELTDREAM_ENERGY_HUD_ANCHOR;
    /** 始终显示融梦能量数值文本（默认 false，仍可在潜行时查看） */
    public static final ModConfigSpec.ConfigValue<Boolean> MELTDREAM_ENERGY_SHOW_VALUE_ALWAYS;

    // ==================== BGM 背景音乐配置 ====================

    /** 启用模组自定义 BGM（默认 true） */
    public static final ModConfigSpec.ConfigValue<Boolean> BGM_MASTER_ENABLED;
    /** BGM 主音量倍率（0.0 ~ 1.0，默认 1.0） */
    public static final ModConfigSpec.ConfigValue<Double> BGM_MASTER_VOLUME;
    /** 启用「完整播放+间隔」BGM 切换模式（true=把当前曲目完整放完，间隔后再播下一首；false=现有交叉淡化切换，默认 false） */
    public static final ModConfigSpec.ConfigValue<Boolean> BGM_USE_SONG_COMPLETE_MODE;
    /** 完整播放后间隔秒数（30 ~ 60，默认 45） */
    public static final ModConfigSpec.ConfigValue<Integer> BGM_SONG_INTERVAL_SECONDS;

    // --- 染梦世界 ---
    /** 染梦世界主 BGM 开关（默认 true） */
    public static final ModConfigSpec.ConfigValue<Boolean> BGM_DYEDREAM_WORLD;
    /** 染梦世界主 BGM 音量倍率（默认 1.0） */
    public static final ModConfigSpec.ConfigValue<Double> BGM_DYEDREAM_WORLD_VOLUME;

    // --- 梦幻荒原 ---
    /** 梦幻荒原群系 BGM 开关（默认 true） */
    public static final ModConfigSpec.ConfigValue<Boolean> BGM_DREAM_HEATH;
    /** 梦幻荒原群系 BGM 音量倍率（默认 1.0） */
    public static final ModConfigSpec.ConfigValue<Double> BGM_DREAM_HEATH_VOLUME;

    // --- 梦幻三角洲 ---
    /** 梦幻三角洲群系 BGM 开关（默认 true） */
    public static final ModConfigSpec.ConfigValue<Boolean> BGM_DREAM_DELTA;
    /** 梦幻三角洲群系 BGM 音量倍率（默认 1.0） */
    public static final ModConfigSpec.ConfigValue<Double> BGM_DREAM_DELTA_VOLUME;

    // --- 梦幻雪林 ---
    /** 梦幻雪林群系 BGM 开关（默认 true） */
    public static final ModConfigSpec.ConfigValue<Boolean> BGM_DREAM_TAIGA;
    /** 梦幻雪林群系 BGM 音量倍率（默认 1.0） */
    public static final ModConfigSpec.ConfigValue<Double> BGM_DREAM_TAIGA_VOLUME;

    // --- 甜梦深海 ---
    /** 甜梦深海群系 BGM 开关（默认 true） */
    public static final ModConfigSpec.ConfigValue<Boolean> BGM_SWEETDREAM;
    /** 甜梦深海群系 BGM 音量倍率（默认 1.0） */
    public static final ModConfigSpec.ConfigValue<Double> BGM_SWEETDREAM_VOLUME;

    // --- 落雪蘑菇原 ---
    /** 落雪蘑菇原群系 BGM 开关（默认 true） */
    public static final ModConfigSpec.ConfigValue<Boolean> BGM_SNOWFALL_DREAM;
    /** 落雪蘑菇原群系 BGM 音量倍率（默认 1.0） */
    public static final ModConfigSpec.ConfigValue<Double> BGM_SNOWFALL_DREAM_VOLUME;

    // --- 风之旅途·启程 ---
    /** 风之旅途·启程 BGM 开关（默认 true） */
    public static final ModConfigSpec.ConfigValue<Boolean> BGM_WIND_JOURNEY_DEPARTURE;
    /** 风之旅途·启程 BGM 音量倍率（默认 1.0） */
    public static final ModConfigSpec.ConfigValue<Double> BGM_WIND_JOURNEY_DEPARTURE_VOLUME;

    // --- 风之旅途·盛夏光年 ---
    /** 风之旅途·盛夏光年 BGM 开关（默认 true） */
    public static final ModConfigSpec.ConfigValue<Boolean> BGM_WIND_JOURNEY_MIDSUMMER;
    /** 风之旅途·盛夏光年 BGM 音量倍率（默认 1.0） */
    public static final ModConfigSpec.ConfigValue<Double> BGM_WIND_JOURNEY_MIDSUMMER_VOLUME;

    // --- 梦幻草原·Daisy ---
    /** 梦幻草原·Daisy BGM 开关（默认 true） */
    public static final ModConfigSpec.ConfigValue<Boolean> BGM_DREAM_MEADOW_DAISY;
    /** 梦幻草原·Daisy BGM 音量倍率（默认 1.0） */
    public static final ModConfigSpec.ConfigValue<Double> BGM_DREAM_MEADOW_DAISY_VOLUME;

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
        ENABLE_MOD_UI = builder
                .comment("启用帕斯特之梦模组自定义原版UI纹理（按钮/生命值条/BOSS血条/快捷栏等，关闭后将使用原版UI） 默认：true")
                .define("enable mod ui", true);
        builder.pop();

        builder.push("SAN");
        SHOW_SAN_HUD = builder
                .comment("显示 San 精神值条 默认：true")
                .define("show san hud", true);
        SAN_TANK_XBASE = builder
                .comment("精神值量条在屏幕上的xBase位置 默认：-36.0")
                .define("san tank xbase", -36d);
        SAN_TANK_YBASE = builder
                .comment("精神值量条在屏幕上的yBase位置 默认：-34.0")
                .define("san tank ybase", -34d);
        SAN_HUD_SCALE = builder
                .comment("San 精神值条缩放倍率（0.5 ~ 2.0）默认：1.0")
                .defineInRange("san hud scale", 1.0d, 0.5d, 2.0d);
        SAN_HUD_ANCHOR = builder
                .comment("San 精神值条位置锚点（0=左上 1=右上 2=左下 3=右下）默认：3")
                .defineInRange("san hud anchor", 3, 0, 3);
        SAN_SHOW_VALUE_ALWAYS = builder
                .comment("始终显示 San 数值文本 默认：false（潜行时仍可查看）")
                .define("san show value always", false);
        builder.pop();

        builder.push("MELTDREAM");
        SHOW_MELTDREAM_ENERGY_HUD = builder
                .comment("显示融梦能量条 默认：true")
                .define("show meltdream energy hud", true);
        // 注意：以下两个键末尾的空格为原版原样保留（保持 toml 键兼容）
        MELTDREAMENERGY_TANK_XBASE = builder
                .comment("融梦能量条在屏幕上的xBase位置 默认：1.0")
                .define("meltdreamenergy tank xbase ", 1d);
        MELTDREAMENERGY_TANK_YBASE = builder
                .comment("融梦能量条在屏幕上的yBase位置 默认：-19.0")
                .define("meltdreamenergy tank ybase ", -19d);
        MELTDREAM_ENERGY_HUD_SCALE = builder
                .comment("融梦能量条缩放倍率（0.5 ~ 2.0）默认：1.0")
                .defineInRange("meltdream energy hud scale", 1.0d, 0.5d, 2.0d);
        MELTDREAM_ENERGY_HUD_ANCHOR = builder
                .comment("融梦能量条位置锚点（0=左上 1=右上 2=左下 3=右下）默认：2")
                .defineInRange("meltdream energy hud anchor", 2, 0, 3);
        MELTDREAM_ENERGY_SHOW_VALUE_ALWAYS = builder
                .comment("始终显示融梦能量数值文本 默认：false（潜行时仍可查看）")
                .define("meltdream energy show value always", false);
        builder.pop();

        builder.push("BGM");
        BGM_MASTER_ENABLED = builder
                .comment("启用帕斯特之梦模组自定义背景音乐 默认：true（关闭后不影响唱片播放）")
                .define("bgm master enabled", true);
        BGM_MASTER_VOLUME = builder
                .comment("BGM 主音量倍率（0.0 ~ 1.0）默认：1.0")
                .defineInRange("bgm master volume", 1.0d, 0.0d, 1.0d);
        BGM_USE_SONG_COMPLETE_MODE = builder
                .comment("启用「完整播放+间隔」BGM 切换模式（true=把当前曲目完整放完，间隔后再播下一首；false=现有交叉淡化切换）默认：false")
                .define("bgm use song complete mode", false);
        BGM_SONG_INTERVAL_SECONDS = builder
                .comment("完整播放后间隔秒数（30 ~ 60）默认：45")
                .defineInRange("bgm song interval seconds", 45, 30, 60);

        // 染梦世界
        BGM_DYEDREAM_WORLD = builder
                .comment("启用染梦世界主 BGM 默认：true")
                .define("bgm dyedream world", true);
        BGM_DYEDREAM_WORLD_VOLUME = builder
                .comment("染梦世界主 BGM 音量倍率（0.0 ~ 2.0）默认：1.0")
                .defineInRange("bgm dyedream world volume", 1.0d, 0.0d, 2.0d);

        // 梦幻荒原
        BGM_DREAM_HEATH = builder
                .comment("启用梦幻荒原群系 BGM 默认：true")
                .define("bgm dream heath", true);
        BGM_DREAM_HEATH_VOLUME = builder
                .comment("梦幻荒原群系 BGM 音量倍率（0.0 ~ 2.0）默认：1.0")
                .defineInRange("bgm dream heath volume", 1.0d, 0.0d, 2.0d);

        // 梦幻三角洲
        BGM_DREAM_DELTA = builder
                .comment("启用梦幻三角洲群系 BGM 默认：true")
                .define("bgm dream delta", true);
        BGM_DREAM_DELTA_VOLUME = builder
                .comment("梦幻三角洲群系 BGM 音量倍率（0.0 ~ 2.0）默认：1.0")
                .defineInRange("bgm dream delta volume", 1.0d, 0.0d, 2.0d);

        // 梦幻雪林
        BGM_DREAM_TAIGA = builder
                .comment("启用梦幻雪林群系 BGM 默认：true")
                .define("bgm dream taiga", true);
        BGM_DREAM_TAIGA_VOLUME = builder
                .comment("梦幻雪林群系 BGM 音量倍率（0.0 ~ 2.0）默认：1.0")
                .defineInRange("bgm dream taiga volume", 1.0d, 0.0d, 2.0d);

        // 甜梦深海
        BGM_SWEETDREAM = builder
                .comment("启用甜梦深海群系 BGM 默认：true")
                .define("bgm sweetdream", true);
        BGM_SWEETDREAM_VOLUME = builder
                .comment("甜梦深海群系 BGM 音量倍率（0.0 ~ 2.0）默认：1.0")
                .defineInRange("bgm sweetdream volume", 1.0d, 0.0d, 2.0d);

        // 落雪蘑菇原
        BGM_SNOWFALL_DREAM = builder
                .comment("启用落雪蘑菇原群系 BGM 默认：true")
                .define("bgm snowfall dream", true);
        BGM_SNOWFALL_DREAM_VOLUME = builder
                .comment("落雪蘑菇原群系 BGM 音量倍率（0.0 ~ 2.0）默认：1.0")
                .defineInRange("bgm snowfall dream volume", 1.0d, 0.0d, 2.0d);

        // 风之旅途·启程
        BGM_WIND_JOURNEY_DEPARTURE = builder
                .comment("启用风之旅途·启程 BGM 默认：true")
                .define("bgm wind journey departure", true);
        BGM_WIND_JOURNEY_DEPARTURE_VOLUME = builder
                .comment("风之旅途·启程 BGM 音量倍率（0.0 ~ 2.0）默认：1.0")
                .defineInRange("bgm wind journey departure volume", 1.0d, 0.0d, 2.0d);

        // 风之旅途·盛夏光年
        BGM_WIND_JOURNEY_MIDSUMMER = builder
                .comment("启用风之旅途·盛夏光年 BGM 默认：true")
                .define("bgm wind journey midsummer", true);
        BGM_WIND_JOURNEY_MIDSUMMER_VOLUME = builder
                .comment("风之旅途·盛夏光年 BGM 音量倍率（0.0 ~ 2.0）默认：1.0")
                .defineInRange("bgm wind journey midsummer volume", 1.0d, 0.0d, 2.0d);

        // 梦幻草原·Daisy
        BGM_DREAM_MEADOW_DAISY = builder
                .comment("启用梦幻草原·Daisy BGM 默认：true")
                .define("bgm dream meadow daisy", true);
        BGM_DREAM_MEADOW_DAISY_VOLUME = builder
                .comment("梦幻草原·Daisy BGM 音量倍率（0.0 ~ 2.0）默认：1.0")
                .defineInRange("bgm dream meadow daisy volume", 1.0d, 0.0d, 2.0d);
        builder.pop();

        SPEC = builder.build();
    }

    /**
     * 根据音乐名称查询对应的独立开关配置。
     * <p>
     * 仅对 ModMusicManager 管理的维度群系 BGM 生效，唱片音乐不受影响。
     *
     * @param musicName 音乐名称
     * @return 该音乐的启用开关配置 Supplier；若未定义则返回 null
     */
    public static Supplier<Boolean> getBgmSwitch(String musicName) {
        return switch (musicName) {
            case "dyedream_world" -> BGM_DYEDREAM_WORLD::get;
            case "dream_heath" -> BGM_DREAM_HEATH::get;
            case "dream_delta" -> BGM_DREAM_DELTA::get;
            case "dream_taiga" -> BGM_DREAM_TAIGA::get;
            case "sweetdream_music" -> BGM_SWEETDREAM::get;
            case "snowfall_dream_music" -> BGM_SNOWFALL_DREAM::get;
            case "wind_journey_departure" -> BGM_WIND_JOURNEY_DEPARTURE::get;
            case "wind_journey_midsummer" -> BGM_WIND_JOURNEY_MIDSUMMER::get;
            case "dream_meadow_daisy" -> BGM_DREAM_MEADOW_DAISY::get;
            default -> null;
        };
    }

    /**
     * 根据音乐名称查询对应的独立音量倍率配置。
     * <p>
     * 用于 ModMusicManager 计算各曲目的实际播放音量，唱片音乐不受影响。
     *
     * @param musicName 音乐名称
     * @return 该音乐的音量倍率配置 Supplier；若未定义则返回 null
     */
    public static Supplier<Double> getBgmVolume(String musicName) {
        return switch (musicName) {
            case "dyedream_world" -> BGM_DYEDREAM_WORLD_VOLUME::get;
            case "dream_heath" -> BGM_DREAM_HEATH_VOLUME::get;
            case "dream_delta" -> BGM_DREAM_DELTA_VOLUME::get;
            case "dream_taiga" -> BGM_DREAM_TAIGA_VOLUME::get;
            case "sweetdream_music" -> BGM_SWEETDREAM_VOLUME::get;
            case "snowfall_dream_music" -> BGM_SNOWFALL_DREAM_VOLUME::get;
            case "wind_journey_departure" -> BGM_WIND_JOURNEY_DEPARTURE_VOLUME::get;
            case "wind_journey_midsummer" -> BGM_WIND_JOURNEY_MIDSUMMER_VOLUME::get;
            case "dream_meadow_daisy" -> BGM_DREAM_MEADOW_DAISY_VOLUME::get;
            default -> null;
        };
    }
}
