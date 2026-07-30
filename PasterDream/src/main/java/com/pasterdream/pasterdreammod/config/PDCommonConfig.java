package com.pasterdream.pasterdreammod.config;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * 通用配置（PasterDream-Common.toml）
 * <p>
 * 移植自原版 {@code configuration/PasterdreamConfigCommonConfiguration.java}
 * （ForgeConfigSpec → NeoForge ModConfigSpec），配置键、注释、默认值与原版完全一致
 * （含原版个别键末尾的空格，保持配置文件兼容）。
 * <p>
 * 由主类构造器通过 {@code modContainer.registerConfig(ModConfig.Type.COMMON, SPEC,
 * "PasterDream-Common.toml")} 注册。
 */
public class PDCommonConfig {

    /** 配置规格 */
    public static final ModConfigSpec SPEC;

    // ==================== System ====================

    /** 启用 San 理智系统（默认 true） */
    public static final ModConfigSpec.ConfigValue<Boolean> ENABLE_SAN_SYSTEM;
    /** 启用融梦能量系统（默认 true） */
    public static final ModConfigSpec.ConfigValue<Boolean> ENABLE_MELTDREAM_ENERGY_SYSTEM;

    // ==================== Basic ====================

    /** 玩家在主世界的夜晚会降低精神值（默认 true） */
    public static final ModConfigSpec.ConfigValue<Boolean> OVERWORLD_NIGHT_LOWERS_SAN;
    /** 染梦裂隙自然生成（默认 true） */
    public static final ModConfigSpec.ConfigValue<Boolean> DYEDREAM_CRACK_GENERATE;
    /** 精神值低于一定数值时产生负面 buff（默认 true） */
    public static final ModConfigSpec.ConfigValue<Boolean> LOW_SAN_DEBUFF;
    /** 精神值大于等于该数值时给予振奋效果（默认 99） */
    public static final ModConfigSpec.ConfigValue<Double> CHEERUP_BUFF_THRESHOLD_VALUE;
    /** 融梦水晶箱触发传说宝藏的额外倍率（默认 1） */
    public static final ModConfigSpec.ConfigValue<Double> MELTDREAM_CHEST_LEGEND_MULTIPLIER;
    /** 融梦水晶箱触发稀有宝藏的额外倍率（默认 1） */
    public static final ModConfigSpec.ConfigValue<Double> MELTDREAM_CHEST_RARE_MULTIPLIER;
    /** 玩家睡眠完成时的精神值回复量（默认 10） */
    public static final ModConfigSpec.ConfigValue<Double> SLEEP_SAN_RECOVERY_AMOUNT;
    /** 过低精神值时产生画面抖动（默认 true） */
    public static final ModConfigSpec.ConfigValue<Boolean> LOW_SAN_PICTURE_JITTER;
    /** 初始生成世界时在 0,0 原点生成染梦裂隙（默认 false） */
    public static final ModConfigSpec.ConfigValue<Boolean> THE_ORIGIN_OF_THE_WORLD_INITIALLY_GENERATED_DYEDREAM_CRACK;
    /** 启用模组进入游戏时的聊天栏公告（默认 true） */
    public static final ModConfigSpec.ConfigValue<Boolean> MOD_ACCOUOCEMENT;
    /** 进入主题梦境《灯影之下》时是否给予苍白骨针（默认 false） */
    public static final ModConfigSpec.ConfigValue<Boolean> IN_LAMP_SHADOW_GIVE_PALE_BONENEEDLE;
    /** 禁止使用染梦世界的染梦裂隙向主世界返程传送（默认 false） */
    public static final ModConfigSpec.ConfigValue<Boolean> NO_RETURN_DYEDREAM_CRACK;
    /** 在染梦世界生成初始出生点岛屿（默认 true） */
    public static final ModConfigSpec.ConfigValue<Boolean> DYEDREAM_ORIGIN_SPAWNPOINT;
    /** 与无名第三次对话后强制传送回主世界（默认 true） */
    public static final ModConfigSpec.ConfigValue<Boolean> SHADOW_NPC_THIRD_DIALOGUE_AFTER_TP_PLAYER_BACK_TO_OVERWORLD;

    // ==================== property ====================

    /** 玩家刻功能程序更新频率，单位 tick（默认 5，推荐 2~20） */
    public static final ModConfigSpec.ConfigValue<Integer> PLAYER_TOTAL_TICK_UPDATE;

    // ==================== Ban ====================

    /** 关闭并禁止所有翅膀的功能（默认 false） */
    public static final ModConfigSpec.ConfigValue<Boolean> BAN_ALL_THE_WINGS;
    /** 关闭并禁止大地之刃的功能（默认 false） */
    public static final ModConfigSpec.ConfigValue<Boolean> BAN_TERRA_SWORD;
    /** 关闭并禁止业火项链的功能（默认 false） */
    public static final ModConfigSpec.ConfigValue<Boolean> BAN_FIRE_NECKLACE;
    /** 关闭并禁止时之沙的功能（默认 false） */
    public static final ModConfigSpec.ConfigValue<Boolean> BAN_TIME_HOURGLASS;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.push("System");
        ENABLE_SAN_SYSTEM = builder
                .comment("启用 San 理智系统 默认：true（关闭后停止理智计算、同步与 HUD 显示）")
                .define("enable san system", true);
        ENABLE_MELTDREAM_ENERGY_SYSTEM = builder
                .comment("启用融梦能量系统 默认：true（关闭后停止能量计算、消耗与 HUD 显示）")
                .define("enable meltdream energy system", true);
        builder.pop();

        builder.push("Basic");
        OVERWORLD_NIGHT_LOWERS_SAN = builder
                .comment("玩家在主世界的夜晚会降低精神值 默认：true")
                .define("overworld night lowers san", true);
        DYEDREAM_CRACK_GENERATE = builder
                .comment("染梦裂隙自然生成（如非特别需要勿关，会影响游戏正常流程） 默认：true")
                .define("dyedream crack generate", true);
        LOW_SAN_DEBUFF = builder
                .comment("精神值低于一定数值时会产生负面buff效果 默认：true")
                .define("low san debuff", true);
        CHEERUP_BUFF_THRESHOLD_VALUE = builder
                .comment("在精神值大于等于这个数值时会给予玩家振奋效果 默认：99")
                .define("cheerup buff threshold value", (double) 99);
        MELTDREAM_CHEST_LEGEND_MULTIPLIER = builder
                .comment("融梦水晶箱触发传说宝藏的额外倍率 默认：1")
                .define("meltdream chest legend multiplier", (double) 1);
        MELTDREAM_CHEST_RARE_MULTIPLIER = builder
                .comment("融梦水晶箱触发稀有宝藏的额外倍率 默认：1")
                .define("meltdream chest rare multiplier", (double) 1);
        SLEEP_SAN_RECOVERY_AMOUNT = builder
                .comment("玩家在睡眠完成时的精神值回复量 默认：10")
                .define("sleep san recovery amount", (double) 10);
        LOW_SAN_PICTURE_JITTER = builder
                .comment("玩家在过低精神值时会产生画面抖动 默认：true")
                .define("low san picture jitter", true);
        // 注意：该键末尾的空格为原版原样保留（保持 toml 键兼容）
        THE_ORIGIN_OF_THE_WORLD_INITIALLY_GENERATED_DYEDREAM_CRACK = builder
                .comment("初始生成世界时在0,0原点生成染梦裂隙 默认：false")
                .define("the origin of the world initially generated dyedream crack ", false);
        MOD_ACCOUOCEMENT = builder
                .comment("启用模组进入游戏时的聊天栏公告 默认：true")
                .define("mod accouocement", true);
        IN_LAMP_SHADOW_GIVE_PALE_BONENEEDLE = builder
                .comment("进入主题梦境《灯影之下》时是否给予苍白骨针 默认：true（对齐图鉴；可关）")
                .define("in lamp shadow give pale boneneedle", true);
        NO_RETURN_DYEDREAM_CRACK = builder
                .comment("禁止在使用染梦世界的染梦裂隙向主世界的返程传送 默认：false")
                .define("no return dyedream crack", false);
        DYEDREAM_ORIGIN_SPAWNPOINT = builder
                .comment("在染梦世界生成初始出生点岛屿 默认：true")
                .define("dyedream origin spawnpoint", true);
        SHADOW_NPC_THIRD_DIALOGUE_AFTER_TP_PLAYER_BACK_TO_OVERWORLD = builder
                .comment("在与无名第三次对话后会被强制传送回主世界 默认：true")
                .define("shadow npc third dialogue after tp player back to overworld", true);
        builder.pop();

        builder.push("property");
        PLAYER_TOTAL_TICK_UPDATE = builder
                .comment("pasterdream玩家刻功能程序更新频率，算法：每-[此配置单位时间]-进行一次更新 单位/tick，过快的更新频率可能会影响性能，过慢的频率会让部分功能显得非常迟钝，推荐频率范围[2~20]整数  默认：5")
                .defineInRange("player total tick update", 5, 2, 20);
        builder.pop();

        builder.push("Ban");
        BAN_ALL_THE_WINGS = builder
                .comment("关闭并禁止所有翅膀的功能  默认：false")
                .define("ban all the wings", false);
        BAN_TERRA_SWORD = builder
                .comment("关闭并禁止大地之刃的功能  默认：false")
                .define("ban terra sword", false);
        BAN_FIRE_NECKLACE = builder
                .comment("关闭并禁止业火项链的功能  默认：false")
                .define("ban fire necklace", false);
        BAN_TIME_HOURGLASS = builder
                .comment("关闭并禁止时之沙的功能  默认：false")
                .define("ban time hourglass", false);
        builder.pop();

        SPEC = builder.build();
    }
}
