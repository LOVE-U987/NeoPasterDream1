package com.pasterdream.pasterdreammod.config;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    // ==================== Advancement Lock ====================

    /** 进度锁总开关（默认 true） */
    public static final ModConfigSpec.ConfigValue<Boolean> ENABLE_ADVANCEMENT_LOCK;
    /** 创造模式玩家不受进度锁限制（默认 true） */
    public static final ModConfigSpec.ConfigValue<Boolean> CREATIVE_BYPASS_ADVANCEMENT_LOCK;
    /** 各进度独立锁：Key 为成就 ID，Value 为是否启用该成就锁 */
    public static final Map<ResourceLocation, ModConfigSpec.ConfigValue<Boolean>> ADVANCEMENT_LOCKS = new LinkedHashMap<>();

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

    // ==================== Custom Spawn ====================

    /** 自定义出生总开关（默认 false，关闭时玩家按原版逻辑在主世界出生） */
    public static final ModConfigSpec.ConfigValue<Boolean> CUSTOM_SPAWN_ENABLED;
    /** 自定义出生维度 ID（格式：命名空间:路径，如 minecraft:overworld / pasterdream:dyedream_world） */
    public static final ModConfigSpec.ConfigValue<String> CUSTOM_SPAWN_DIMENSION;
    /** 自定义出生群系 ID（格式：命名空间:路径，如 minecraft:plains / pasterdream:dyedream） */
    public static final ModConfigSpec.ConfigValue<String> CUSTOM_SPAWN_BIOME;
    /** 出生群系搜索半径，单位：格（以维度出生点为中心向外搜索指定群系） */
    public static final ModConfigSpec.ConfigValue<Integer> CUSTOM_SPAWN_SEARCH_RADIUS;

    // ==================== property ====================

    /** 玩家刻功能程序更新频率，单位 tick（默认 5，推荐 2~20） */
    public static final ModConfigSpec.ConfigValue<Integer> PLAYER_TOTAL_TICK_UPDATE;

    // ==================== Sky ====================

    /** 星空枕(memento_item_08)在天空上可创建的连线星体数量上限（默认 8） */
    public static final ModConfigSpec.ConfigValue<Integer> SKYLINK_MAX_STARS;

    // ==================== Meltdream Chest ====================

    /** 融梦水晶箱自定义战利品总开关（默认 false，关闭时使用内置默认物品池） */
    public static final ModConfigSpec.ConfigValue<Boolean> MELTDREAM_CHEST_CUSTOM_LOOT_ENABLED;
    /** 融梦水晶箱普通品质物品池（每行一个条目：物品ID [数量] [权重]） */
    public static final ModConfigSpec.ConfigValue<List<String>> MELTDREAM_CHEST_COMMON_LOOT;
    /** 融梦水晶箱稀有品质物品池 */
    public static final ModConfigSpec.ConfigValue<List<String>> MELTDREAM_CHEST_RARE_LOOT;
    /** 融梦水晶箱传说品质物品池 */
    public static final ModConfigSpec.ConfigValue<List<String>> MELTDREAM_CHEST_LEGENDARY_LOOT;

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

        builder.push("Advancement Lock");
        ENABLE_ADVANCEMENT_LOCK = builder
                .comment("进度锁总开关（关闭后，模组内依赖进度的交互/传送/功能限制将全部放行） 默认：true")
                .define("enable advancement lock", true);
        CREATIVE_BYPASS_ADVANCEMENT_LOCK = builder
                .comment("创造模式玩家不受进度锁限制（开启后，创造模式玩家可直接使用所有受进度限制的功能） 默认：true")
                .define("creative bypass advancement lock", true);

        // 各进度独立锁：关闭后该特定进度不再产生限制
        for (String advPath : getAllAdvancementPaths()) {
            ResourceLocation advId = ResourceLocation.fromNamespaceAndPath("pasterdream", advPath);
            String key = advPath + " lock";
            ModConfigSpec.ConfigValue<Boolean> value = builder
                    .comment("进度锁：" + advPath + "（关闭后该进度不再限制相关功能） 默认：true")
                    .define(key, true);
            ADVANCEMENT_LOCKS.put(advId, value);
        }
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

        builder.push("Custom Spawn");
        CUSTOM_SPAWN_ENABLED = builder
                .comment("自定义出生维度/群系总开关（开启后，新玩家首次进入世界时会在指定维度与群系出生；关闭后按原版逻辑在主世界出生） 默认：false")
                .define("custom spawn enabled", false);
        CUSTOM_SPAWN_DIMENSION = builder
                .comment("自定义出生维度 ID（格式：命名空间:路径，如 minecraft:overworld / pasterdream:dyedream_world） 默认：minecraft:overworld")
                .define("custom spawn dimension", "minecraft:overworld");
        CUSTOM_SPAWN_BIOME = builder
                .comment("自定义出生群系 ID（格式：命名空间:路径，如 minecraft:plains / pasterdream:dyedream） 默认：minecraft:plains")
                .define("custom spawn biome", "minecraft:plains");
        CUSTOM_SPAWN_SEARCH_RADIUS = builder
                .comment("出生群系搜索半径，单位：格（以目标维度出生点为中心向外搜索配置的群系；搜索不到时回退到维度出生点） 默认：10000 范围：100~100000")
                .defineInRange("custom spawn search radius", 10000, 100, 100000);
        builder.pop();

        builder.push("property");
        PLAYER_TOTAL_TICK_UPDATE = builder
                .comment("pasterdream玩家刻功能程序更新频率，算法：每-[此配置单位时间]-进行一次更新 单位/tick，过快的更新频率可能会影响性能，过慢的频率会让部分功能显得非常迟钝，推荐频率范围[2~20]整数  默认：5")
                .defineInRange("player total tick update", 5, 2, 20);
        builder.pop();

        builder.push("Sky");
        SKYLINK_MAX_STARS = builder
                .comment("星空枕(memento_item_08)在天空上可创建的连线星体数量上限 默认：8 范围：1~64")
                .defineInRange("skylink max stars", 8, 1, 64);
        builder.pop();

        builder.push("Meltdream Chest");
        MELTDREAM_CHEST_CUSTOM_LOOT_ENABLED = builder
                .comment("融梦水晶箱自定义战利品总开关（开启后按下方三个品质的物品池生成掉落；关闭使用内置默认池） 默认：false")
                .define("meltdream chest custom loot enabled", false);
        MELTDREAM_CHEST_COMMON_LOOT = builder
                .comment("融梦水晶箱普通品质物品池（每行一个条目，格式：物品ID 数量 权重，如 \"pasterdream:fried_egg 2 30\") 默认：内置普通物品池")
                .define("meltdream chest common loot", MeltdreamChestLootConfig.DEFAULT_COMMON_LOOT);
        MELTDREAM_CHEST_RARE_LOOT = builder
                .comment("融梦水晶箱稀有品质物品池（格式同上） 默认：内置稀有物品池")
                .define("meltdream chest rare loot", MeltdreamChestLootConfig.DEFAULT_RARE_LOOT);
        MELTDREAM_CHEST_LEGENDARY_LOOT = builder
                .comment("融梦水晶箱传说品质物品池（格式同上） 默认：内置传说物品池")
                .define("meltdream chest legendary loot", MeltdreamChestLootConfig.DEFAULT_LEGENDARY_LOOT);
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

    /**
     * 获取所有受独立进度锁控制的成就路径列表。
     * <p>
     * 顺序决定配置界面展示顺序，与 {@link PDAdvancements} 中的常量一一对应。
     *
     * @return 成就路径数组
     */
    private static String[] getAllAdvancementPaths() {
        return new String[]{
                // 主线 / 显示
                "achievement_start",
                "achievement_a_0", "achievement_a_1",
                "achievement_b_0", "achievement_b_1", "achievement_b_2", "achievement_b_3",
                "achievement_c_0", "achievement_c_1", "achievement_c_2", "achievement_c_3", "achievement_c_4",
                "achievement_d_0",
                "achievement_end_0",
                "achievement_nether_0",
                "achievement_adventure_0",
                "achievement_special_0",
                // 灯影 / 影之抉择
                "achievement_shadow_start",
                "achievement_shadow_a_0", "achievement_shadow_a_1",
                "achievement_shadow_b_0",
                "achievement_shadow_c_0",
                "achievement_shadow_d_0",
                "achievement_shadow_e_0",
                "achievement_talent_light",
                "achievement_talent_shadow",
                "achievement_shadow_npc_0", "achievement_shadow_npc_1", "achievement_shadow_npc_2",
                "achievement_shadow_npc_3", "achievement_shadow_npc_4", "achievement_shadow_npc_5",
                // 宝藏树
                "achievement_treasure_start",
                "achievement_treasure_dyedream",
                "achievement_treasure_wind_journey",
                // 隐藏追踪
                "achievement_hide_0", "achievement_hide_1", "achievement_hide_2", "achievement_hide_3",
                "achievement_hide_4", "achievement_hide_5", "achievement_hide_6", "achievement_hide_7",
                "achievement_hide_8", "achievement_hide_9", "achievement_hide_10", "achievement_hide_11",
                "achievement_hide_12", "achievement_hide_13", "achievement_hide_14", "achievement_hide_15",
                "achievement_hide_16"
        };
    }
}
