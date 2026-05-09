package net.pasterdream.configuration;

import net.minecraftforge.common.ForgeConfigSpec;

public class PasterdreamConfigClientConfiguration {
	public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
	public static final ForgeConfigSpec SPEC;
	public static final ForgeConfigSpec.ConfigValue<Boolean> STEALTH_DISPLAY_ATTRIBUTE_HUD;
	public static final ForgeConfigSpec.ConfigValue<Boolean> LOADING_GUI_TIPS;
	public static final ForgeConfigSpec.ConfigValue<Boolean> PASTER_HEALTH_HUD;
	public static final ForgeConfigSpec.ConfigValue<Double> MELTDREAMENERGY_TANK_XBASE;
	public static final ForgeConfigSpec.ConfigValue<Double> MELTDREAMENERGY_TANK_YBASE;
	public static final ForgeConfigSpec.ConfigValue<Double> SAN_TANK_XBASE;
	public static final ForgeConfigSpec.ConfigValue<Double> SAN_TANK_YBASE;
	static {
		BUILDER.push("HUD");
		STEALTH_DISPLAY_ATTRIBUTE_HUD = BUILDER.comment("仅在潜行时显示融梦能量条和精神值的HUD图标 默认：false").define("stealth display attribute hud", false);
		LOADING_GUI_TIPS = BUILDER.comment("在加载界面时会弹出帕斯特之梦的tips 默认：true").define("loading gui tips", true);
		PASTER_HEALTH_HUD = BUILDER.comment("启动帕斯特之梦的主题生命值条 默认：true").define("paster health hud", true);
		MELTDREAMENERGY_TANK_XBASE = BUILDER.comment("融梦能量条在屏幕上的xBase位置 默认：1.0").define("meltdreamenergy tank xbase ", 1d);
		MELTDREAMENERGY_TANK_YBASE = BUILDER.comment("融梦能量条在屏幕上的yBase位置 默认：-19.0").define("meltdreamenergy tank ybase ", -19d);
		SAN_TANK_XBASE = BUILDER.comment("精神值量条在屏幕上的xBase位置 默认：-36.0").define("san tank xbase", -36d);
		SAN_TANK_YBASE = BUILDER.comment("精神值量条在屏幕上的yBase位置 默认：-34.0").define("san tank ybase", -34d);
		BUILDER.pop();

		SPEC = BUILDER.build();
	}

}
