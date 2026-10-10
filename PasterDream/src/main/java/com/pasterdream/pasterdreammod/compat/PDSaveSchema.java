package com.pasterdream.pasterdreammod.compat;

import com.pasterdream.pasterdreammod.PasterDreamMod;

/**
 * 存档 ID schema 版本常量。
 * <p>
 * 每次对<b>已发布内容</b>做注册名改名/合并时递增
 * {@link #CURRENT_ID_SCHEMA_VERSION}，并在 {@link PDIdAliases} 登记旧→新映射。
 * <p>
 * 旧存档判定：存档内记录的主模 schema 版本小于当前值（或缺失）即为旧存档，
 * 触发备份与提示；玩家确认后写入当前值（仅写标记，不重写世界数据）。
 */
public final class PDSaveSchema {

    /** 主模 ID（复用主类常量，避免重复定义） */
    public static final String MOD_ID = PasterDreamMod.MOD_ID;

    /**
     * 当前 ID schema 版本。
     * <p>
     * 版本 1 为首个引入本机制的版本：所有旧存档首次加载都会被判定为旧存档。
     * <p>
     * 版本 2（0.11.x）：ID 迁移第一批——5 个音效注册名去 {@code _0} 后缀
     * （ding_0→ding、shadow_biome_0→shadow_biome、shadow_music_0→shadow_music、
     * shadow_trap_0→shadow_trap、wind_knight_skill_0→wind_knight_skill），别名见 {@link PDIdAliases}。
     */
    public static final int CURRENT_ID_SCHEMA_VERSION = 2;

    /** SavedData 标识 */
    public static final String SAVE_COMPAT_DATA_ID = "pasterdream_save_compat";

    private PDSaveSchema() {
        throw new UnsupportedOperationException("PDSaveSchema 是常量类，不可实例化");
    }
}
