package com.pasterdream.pasterdreammod.config;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * PasterDream 主模组通用配置（PasterDream-Common.toml）。
 * <p>
 * 自 1.21.1 重构后，San 值与融梦能量系统的配置已拆分至
 * PasterDreamSanity / PasterDreamMeltDream 两个附属模组。
 * 本配置仅保留维度、世界生成、封禁、调试等主模组专属选项。
 */
public class PDCommonConfig {

    /** 配置规格 */
    public static final ModConfigSpec SPEC;

    // ==================== System ====================

    /** 染梦裂隙自然生成（默认 true） */
    public static final ModConfigSpec.ConfigValue<Boolean> DYEDREAM_CRACK_GENERATE;

    // ==================== Basic ====================

    /** 进入主题梦境《灯影之下》时是否给予苍白骨针（默认 false） */
    public static final ModConfigSpec.ConfigValue<Boolean> IN_LAMP_SHADOW_GIVE_PALE_BONENEEDLE;
    /** 初始生成世界时在 0,0 原点生成染梦裂隙（默认 false） */
    public static final ModConfigSpec.ConfigValue<Boolean> THE_ORIGIN_OF_THE_WORLD_INITIALLY_GENERATED_DYEDREAM_CRACK;
    /** 启用模组进入游戏时的聊天栏公告（默认 true） */
    public static final ModConfigSpec.ConfigValue<Boolean> MOD_ACCOUOCEMENT;
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

    // ==================== Debug ====================

    /** 调试日志总开关（默认 false） */
    public static final ModConfigSpec.ConfigValue<Boolean> ENABLE_DEBUG_LOG;
    /** 启用 API 模块调试日志（默认 false） */
    public static final ModConfigSpec.ConfigValue<Boolean> ENABLE_API_DEBUG_LOG;
    /** 启用主模块调试日志（默认 false） */
    public static final ModConfigSpec.ConfigValue<Boolean> ENABLE_MAIN_DEBUG_LOG;
    /** 启用 Smoketest / 移植验证调试日志（默认 false） */
    public static final ModConfigSpec.ConfigValue<Boolean> ENABLE_SMOKETEST_DEBUG_LOG;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.push("System");
        DYEDREAM_CRACK_GENERATE = builder
                .comment("染梦裂隙自然生成（如非特别需要勿关，会影响游戏正常流程） 默认：true")
                .define("dyedream crack generate", true);
        builder.pop();

        builder.push("Basic");
        IN_LAMP_SHADOW_GIVE_PALE_BONENEEDLE = builder
                .comment("进入主题梦境《灯影之下》时是否给予苍白骨针 默认：false")
                .define("in lamp shadow give pale boneneedle", false);
        // 注意：该键末尾的空格为原版原样保留（保持 toml 键兼容）
        THE_ORIGIN_OF_THE_WORLD_INITIALLY_GENERATED_DYEDREAM_CRACK = builder
                .comment("初始生成世界时在0,0原点生成染梦裂隙 默认：false")
                .define("the origin of the world initially generated dyedream crack ", false);
        MOD_ACCOUOCEMENT = builder
                .comment("启用模组进入游戏时的聊天栏公告 默认：true")
                .define("mod accouocement", true);
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

        builder.push("Debug");
        ENABLE_DEBUG_LOG = builder
                .comment("调试日志总开关 默认：false（关闭后不输出任何调试/诊断日志）")
                .define("enable debug log", false);
        ENABLE_API_DEBUG_LOG = builder
                .comment("启用 PasterDreamAPI 模块的调试日志 默认：false")
                .define("enable api debug log", false);
        ENABLE_MAIN_DEBUG_LOG = builder
                .comment("启用 PasterDream 主模块的调试日志 默认：false")
                .define("enable main debug log", false);
        ENABLE_SMOKETEST_DEBUG_LOG = builder
                .comment("启用 Smoketest / 移植验证的调试日志 默认：false")
                .define("enable smoketest debug log", false);
        builder.pop();

        SPEC = builder.build();
    }
}
