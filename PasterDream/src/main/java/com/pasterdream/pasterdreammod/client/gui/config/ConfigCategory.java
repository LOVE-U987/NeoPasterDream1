package com.pasterdream.pasterdreammod.client.gui.config;

import net.minecraft.network.chat.Component;

/**
 * 配置界面左侧导航分类
 * <p>
 * 与 TOML 顶层分类一一对应：Client 配置归入 HUD，Common 配置归入 Basic/Property/Ban。
 *
 * @author PasterDream
 */
public enum ConfigCategory {

    /** 客户端 HUD 显示配置 */
    HUD("hud"),
    /** 系统级总开关配置 */
    SYSTEM("system"),
    /** 进度锁总控配置 */
    ADVANCEMENT_LOCK("advancement_lock"),
    /** 通用基础游戏机制配置 */
    BASIC("basic"),
    /** 性能与更新频率配置 */
    PROPERTY("property"),
    /** 功能禁用配置 */
    BAN("ban"),
    /** 背景音乐配置 */
    BGM("bgm"),
    /** San 精神值 HUD 显示设置 */
    SAN("san"),
    /** 融梦能量 HUD 显示设置 */
    MELTDREAM("meltdream"),
    /** 融梦水晶箱战利品配置 */
    MELTDREAM_CHEST("meltdream_chest"),
    /** San 精神值系统设置（PasterDreamSanity 附属模组） */
    SANITY_SYSTEM("sanity_system"),
    /** 融梦能量系统设置（PasterDreamMeltDream 附属模组） */
    MELTDREAM_SYSTEM("meltdream_system"),
    /** 法术系统设置（PasterDreamSpells 附属模组） */
    SPELLS_SYSTEM("spells_system"),
    /** 调试日志设置 */
    DEBUG("debug");

    private final String key;
    private final Component title;

    /**
     * @param key 语言键短名，完整语言键为 {@code gui.pasterdream.config.category.<key>}
     */
    ConfigCategory(String key) {
        this.key = key;
        this.title = Component.translatable("gui.pasterdream.config.category." + key);
    }

    /**
     * @return 分类语言键短名
     */
    public String getKey() {
        return key;
    }

    /**
     * @return 分类显示标题（已翻译）
     */
    public Component getTitle() {
        return title;
    }
}
