package net.pasterdream.configuration;

import net.minecraftforge.common.ForgeConfigSpec;

public class PasterdreamConfigCommonConfiguration {
	public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
	public static final ForgeConfigSpec SPEC;
	public static final ForgeConfigSpec.ConfigValue<Boolean> OVERWORLD_NIGHT_LOWERS_SAN;
	public static final ForgeConfigSpec.ConfigValue<Boolean> DYEDREAM_CRACK_GENERATE;
	public static final ForgeConfigSpec.ConfigValue<Boolean> LOW_SAN_DEBUFF;
	public static final ForgeConfigSpec.ConfigValue<Double> CHEERUP_BUFF_THRESHOLD_VALUE;
	public static final ForgeConfigSpec.ConfigValue<Double> MELTDREAM_CHEST_LEGEND_MULTIPLIER;
	public static final ForgeConfigSpec.ConfigValue<Double> MELTDREAM_CHEST_RARE_MULTIPLIER;
	public static final ForgeConfigSpec.ConfigValue<Double> SLEEP_SAN_RECOVERY_AMOUNT;
	public static final ForgeConfigSpec.ConfigValue<Boolean> LOW_SAN_PICTURE_JITTER;
	public static final ForgeConfigSpec.ConfigValue<Boolean> THE_ORIGIN_OF_THE_WORLD_INITIALLY_GENERATED_DYEDREAM_CRACK;
	public static final ForgeConfigSpec.ConfigValue<Boolean> MOD_ACCOUOCEMENT;
	public static final ForgeConfigSpec.ConfigValue<Boolean> IN_LAMP_SHADOW_GIVE_PALE_BONENEEDLE;
	public static final ForgeConfigSpec.ConfigValue<Boolean> NO_RETURN_DYEDREAM_CRACK;
	public static final ForgeConfigSpec.ConfigValue<Boolean> DYEDREAM_ORIGIN_SPAWNPOINT;
	public static final ForgeConfigSpec.ConfigValue<Boolean> SHADOW_NPC_THIRD_DIALOGUE_AFTER_TP_PLAYER_BACK_TO_OVERWORLD;
	public static final ForgeConfigSpec.ConfigValue<Integer> PLAYER_TOTAL_TICK_UPDATE;
	public static final ForgeConfigSpec.ConfigValue<Boolean> BAN_ALL_THE_WINGS;
	public static final ForgeConfigSpec.ConfigValue<Boolean> BAN_TERRA_SWORD;
	public static final ForgeConfigSpec.ConfigValue<Boolean> BAN_FIRE_NECKLACE;
	public static final ForgeConfigSpec.ConfigValue<Boolean> BAN_TIME_HOURGLASS;
	static {
		BUILDER.push("Basic");
		OVERWORLD_NIGHT_LOWERS_SAN = BUILDER.comment("玩家在主世界的夜晚会降低精神值 默认：true").define("overworld night lowers san", true);
		DYEDREAM_CRACK_GENERATE = BUILDER.comment("染梦裂隙自然生成（如非特别需要勿关，会影响游戏正常流程） 默认：true").define("dyedream crack generate", true);
		LOW_SAN_DEBUFF = BUILDER.comment("精神值低于一定数值时会产生负面buff效果 默认：true").define("low san debuff", true);
		CHEERUP_BUFF_THRESHOLD_VALUE = BUILDER.comment("在精神值大于等于这个数值时会给予玩家振奋效果 默认：99").define("cheerup buff threshold value", (double) 99);
		MELTDREAM_CHEST_LEGEND_MULTIPLIER = BUILDER.comment("融梦水晶箱触发传说宝藏的额外倍率 默认：1").define("meltdream chest legend multiplier", (double) 1);
		MELTDREAM_CHEST_RARE_MULTIPLIER = BUILDER.comment("融梦水晶箱触发稀有宝藏的额外倍率 默认：1").define("meltdream chest rare multiplier", (double) 1);
		SLEEP_SAN_RECOVERY_AMOUNT = BUILDER.comment("玩家在睡眠完成时的精神值回复量 默认：10").define("sleep san recovery amount", (double) 10);
		LOW_SAN_PICTURE_JITTER = BUILDER.comment("玩家在过低精神值时会产生画面抖动 默认：true").define("low san picture jitter", true);
		THE_ORIGIN_OF_THE_WORLD_INITIALLY_GENERATED_DYEDREAM_CRACK = BUILDER.comment("初始生成世界时在0,0原点生成染梦裂隙 默认：false").define("the origin of the world initially generated dyedream crack ", false);
		MOD_ACCOUOCEMENT = BUILDER.comment("启用模组进入游戏时的聊天栏公告 默认：true").define("mod accouocement", true);
		IN_LAMP_SHADOW_GIVE_PALE_BONENEEDLE = BUILDER.comment("进入主题梦境《灯影之下》时是否给予苍白骨针 默认：false").define("in lamp shadow give pale boneneedle", false);
		NO_RETURN_DYEDREAM_CRACK = BUILDER.comment("禁止在使用染梦世界的染梦裂隙向主世界的返程传送 默认：false").define("no return dyedream crack", false);
		DYEDREAM_ORIGIN_SPAWNPOINT = BUILDER.comment("在染梦世界生成初始出生点岛屿 默认：true").define("dyedream origin spawnpoint", true);
		SHADOW_NPC_THIRD_DIALOGUE_AFTER_TP_PLAYER_BACK_TO_OVERWORLD = BUILDER.comment("在与无名第三次对话后会被强制传送回主世界 默认：true").define("shadow npc third dialogue after tp player back to overworld", true);
		BUILDER.pop();
		BUILDER.push("property");
		PLAYER_TOTAL_TICK_UPDATE = BUILDER.comment("pasterdream玩家刻功能程序更新频率，算法：每-[此配置单位时间]-进行一次更新 单位/tick，过快的更新频率可能会影响性能，过慢的频率会让部分功能显得非常迟钝，推荐频率范围[2~20]整数  默认：5").define("player total tick update",  5);
		BUILDER.pop();
		BUILDER.push("Ban");
		BAN_ALL_THE_WINGS = BUILDER.comment("关闭并禁止所有翅膀的功能  默认：false").define("ban all the wings", false);
		BAN_TERRA_SWORD = BUILDER.comment("关闭并禁止大地之刃的功能  默认：false").define("ban terra sword", false);
		BAN_FIRE_NECKLACE = BUILDER.comment("关闭并禁止业火项链的功能  默认：false").define("ban fire necklace", false);
		BAN_TIME_HOURGLASS = BUILDER.comment("关闭并禁止时之沙的功能  默认：false").define("ban time hourglass", false);
		BUILDER.pop();

		SPEC = BUILDER.build();
	}

}
