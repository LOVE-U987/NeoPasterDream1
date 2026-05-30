package com.pasterdream.pasterdreammod.api.itemmigration.model;

/**
 * 迁移类别枚举 —— 按物品功能分类
 * <p>
 * 用于 {@code MigrationManager} 按类别追踪物品的移植进度，
 * 帮助开发者清晰了解各类物品的迁移状态。
 * </p>
 */
public enum MigrationCategory {
    /** 基础材料（如锭、粉、碎片等合成材料） */
    MATERIAL,
    /** 食物类物品 */
    FOOD,
    /** 工具类物品（镐、斧、锹、锄等） */
    TOOL,
    /** 武器类物品（剑、锤等） */
    WEAPON,
    /** 盔甲类物品 */
    ARMOR,
    /** Curio 饰品（戒指、项链、护符等） */
    CURIO,
    /** 方块物品（BlockItem） */
    BLOCK_ITEM,
    /** 音乐唱片 */
    MUSIC_DISC,
    /** 刷怪蛋 */
    SPAWN_EGG,
    /** 记录/档案类物品 */
    RECORD,
    /** 其他杂项物品 */
    MISC
}
