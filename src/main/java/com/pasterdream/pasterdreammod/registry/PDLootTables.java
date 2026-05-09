package com.pasterdream.pasterdreammod.registry;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import net.minecraft.resources.ResourceLocation;

/**
 * 战利品表（Loot Table）引用常量类
 * 定义所有自定义战利品表的 ResourceLocation，便于在代码中引用
 * <p>
 * 实际的战利品表数据定义在 data/pasterdream/loot_tables/ 下的 JSON 文件中
 * <p>
 * 战利品表类型概览（后续完善）：
 * - 方块破坏战利品（blocks/）
 * - 实体掉落战利品（entities/）
 * - 宝箱战利品（chests/）
 * - 游戏机制战利品（gameplay/）
 */
public class PDLootTables {

    // ==================== 方块战利品表 ID ====================

    /*
     * 示例：方块战利品表定义
     *
     * /** 蓄梦池 /
     * public static final ResourceLocation DREAM_ACCUMULATOR = ResourceLocation.fromNamespaceAndPath(
     *         PasterDreamMod.MOD_ID, "blocks/dream_accumulator");
     *
     * /** 染梦书桌 /
     * public static final ResourceLocation DYEDREAM_DESK = ResourceLocation.fromNamespaceAndPath(
     *         PasterDreamMod.MOD_ID, "blocks/dyedream_desk");
     */

    // ==================== 实体战利品表 ID ====================

    /*
     * 示例：实体战利品表定义
     *
     * /** 暗影魔像 /
     * public static final ResourceLocation SHADOW_GOLEM = ResourceLocation.fromNamespaceAndPath(
     *         PasterDreamMod.MOD_ID, "entities/shadow_golem");
     *
     * /** 粉色史莱姆 /
     * public static final ResourceLocation PINK_SLIME = ResourceLocation.fromNamespaceAndPath(
     *         PasterDreamMod.MOD_ID, "entities/pink_slime");
     */

    // ==================== 宝箱战利品表 ID ====================

    /*
     * 示例：宝箱战利品表定义
     *
     * /** 暗影地牢宝箱 /
     * public static final ResourceLocation SHADOW_DUNGEON_CHEST = ResourceLocation.fromNamespaceAndPath(
     *         PasterDreamMod.MOD_ID, "chests/shadow_dungeon_chest");
     *
     * /** 染梦遗迹宝箱 /
     * public static final ResourceLocation DYEDREAM_RUINS_CHEST = ResourceLocation.fromNamespaceAndPath(
     *         PasterDreamMod.MOD_ID, "chests/dyedream_ruins_chest");
     */

    // ==================== 游戏机制战利品表 ID ====================

    /*
     * public static final ResourceLocation DREAM_ACCUMULATOR_GAMEPLAY = ResourceLocation.fromNamespaceAndPath(
     *         PasterDreamMod.MOD_ID, "gameplay/dream_accumulator");
     */

    /**
     * 私有构造函数，防止实例化
     */
    private PDLootTables() {
    }
}