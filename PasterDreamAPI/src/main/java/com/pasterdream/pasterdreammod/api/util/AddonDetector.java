package com.pasterdream.pasterdreammod.api.util;

import net.neoforged.fml.ModList;

/**
 * 附属模组加载检测工具。
 * <p>
 * 供 PasterDream 主模组与 API 下游模组在不产生硬依赖的前提下，
 * 判断某个功能扩展模组是否已经加载。
 *
 * @author PasterDream
 */
public final class AddonDetector {

    /** PasterDream San 值系统附属模组 ID */
    public static final String SANITY_MOD_ID = "pasterdreamsanity";
    /** PasterDream 融梦能量系统附属模组 ID */
    public static final String MELTDREAM_MOD_ID = "pasterdreammeltdream";
    /** PasterDream 法术系统附属模组 ID */
    public static final String SPELLS_MOD_ID = "pasterdreamspells";

    private AddonDetector() {
        throw new UnsupportedOperationException("AddonDetector 是门面类，不可实例化");
    }

    /**
     * 判断指定 modId 是否已加载。
     *
     * @param modId 目标模组 ID
     * @return 已加载返回 true
     */
    public static boolean isLoaded(String modId) {
        ModList modList = ModList.get();
        return modList != null && modList.isLoaded(modId);
    }

    /**
     * San 值系统附属模组是否已加载。
     *
     * @return 已加载返回 true
     */
    public static boolean isSanityLoaded() {
        return isLoaded(SANITY_MOD_ID);
    }

    /**
     * 融梦能量系统附属模组是否已加载。
     *
     * @return 已加载返回 true
     */
    public static boolean isMeltDreamLoaded() {
        return isLoaded(MELTDREAM_MOD_ID);
    }

    /**
     * 法术系统附属模组是否已加载。
     *
     * @return 已加载返回 true
     */
    public static boolean isSpellsLoaded() {
        return isLoaded(SPELLS_MOD_ID);
    }
}
