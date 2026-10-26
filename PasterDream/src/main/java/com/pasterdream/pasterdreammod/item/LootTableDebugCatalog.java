package com.pasterdream.pasterdreammod.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 战利品库调试器的战利品表目录。
 * <p>
 * 每次打开界面时从服务器当前已加载的战利品表重新扫描，只保留 {@code pasterdream} 命名空间下
 * 路径以 {@code chests/} 或 {@code loots_} 开头的表，并按分类与路径排序。
 * 数据包增删表后重新打开界面即可刷新。
 */
public final class LootTableDebugCatalog {

    /** 只扫描本模组命名空间 */
    public static final String NAMESPACE = "pasterdream";

    /**
     * 战利品表分类，枚举顺序即界面中的分类顺序。
     */
    public enum Category {
        /** 遗迹通用表 */
        RELIC,
        /** 深海秘宝表 */
        DEEP_TREASURE,
        /** 融梦水晶箱主表 */
        MELTDREAM_MAIN,
        /** 融梦水晶箱附加/兜底/二级表 */
        MELTDREAM_EXTRA,
        /** 其余宝箱表 */
        OTHER
    }

    private LootTableDebugCatalog() {
        throw new UnsupportedOperationException("LootTableDebugCatalog 是纯静态工具类，不可实例化");
    }

    /**
     * 扫描当前服务器已加载的宝箱战利品表。
     *
     * @param server 服务器实例
     * @return 按分类、路径排序的表 ID 列表；无匹配时返回空列表
     */
    public static List<ResourceLocation> scan(MinecraftServer server) {
        List<ResourceLocation> tables = new ArrayList<>();
        for (ResourceLocation id : server.reloadableRegistries().getKeys(Registries.LOOT_TABLE)) {
            if (!NAMESPACE.equals(id.getNamespace())) {
                continue;
            }
            String path = id.getPath();
            if (!path.startsWith("chests/") && !path.startsWith("loots_")) {
                continue;
            }
            tables.add(id);
        }
        tables.sort(Comparator.comparingInt((ResourceLocation id) -> categoryOf(id).ordinal())
                .thenComparing(ResourceLocation::getPath));
        return tables;
    }

    /**
     * 判断战利品表所属分类。
     *
     * @param id 战利品表 ID
     * @return 分类；不匹配任何专用前缀时为 {@link Category#OTHER}
     */
    public static Category categoryOf(ResourceLocation id) {
        String path = id.getPath();
        if (path.startsWith("chests/loots_relic_") || path.startsWith("loots_relic_")) {
            return Category.RELIC;
        }
        if (path.startsWith("chests/loots_deep_treasure_")) {
            return Category.DEEP_TREASURE;
        }
        if (path.equals("chests/loots_meltdream_chest_0") || path.equals("chests/loots_meltdream_chest_1")) {
            return Category.MELTDREAM_MAIN;
        }
        if (path.startsWith("chests/loots_meltdream_chest_bonus_")
                || path.startsWith("chests/loots_meltdream_chest_fallback_")
                || path.startsWith("chests/loots_meltdream_chest_secondary_")) {
            return Category.MELTDREAM_EXTRA;
        }
        return Category.OTHER;
    }

    /**
     * 查找指定分类的第一项下标。
     *
     * @param tables   已排序的表列表
     * @param category 目标分类
     * @return 下标；分类为空时返回 -1
     */
    public static int firstOfCategory(List<ResourceLocation> tables, Category category) {
        for (int i = 0; i < tables.size(); i++) {
            if (categoryOf(tables.get(i)) == category) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 按分类顺序跳到上一个/下一个非空分类的首项，边界循环。
     *
     * @param tables       已排序的表列表
     * @param currentIndex 当前下标
     * @param direction    -1 上一个，1 下一个
     * @return 目标下标；空列表返回 0
     */
    public static int jumpCategory(List<ResourceLocation> tables, int currentIndex, int direction) {
        if (tables.isEmpty()) {
            return 0;
        }
        int safeIndex = Math.max(0, Math.min(currentIndex, tables.size() - 1));
        Category current = categoryOf(tables.get(safeIndex));
        List<Category> nonEmpty = new ArrayList<>();
        for (Category category : Category.values()) {
            if (firstOfCategory(tables, category) >= 0) {
                nonEmpty.add(category);
            }
        }
        int position = nonEmpty.indexOf(current);
        if (position < 0) {
            position = 0;
        }
        int target = Math.floorMod(position + direction, nonEmpty.size());
        return firstOfCategory(tables, nonEmpty.get(target));
    }

    /**
     * 获取分类的翻译键。
     *
     * @param category 分类
     * @return 语言键
     */
    public static String translationKey(Category category) {
        return switch (category) {
            case RELIC -> "gui.pasterdream.loot_table_debug.category.relic";
            case DEEP_TREASURE -> "gui.pasterdream.loot_table_debug.category.deep_treasure";
            case MELTDREAM_MAIN -> "gui.pasterdream.loot_table_debug.category.meltdream_main";
            case MELTDREAM_EXTRA -> "gui.pasterdream.loot_table_debug.category.meltdream_extra";
            case OTHER -> "gui.pasterdream.loot_table_debug.category.other";
        };
    }
}
