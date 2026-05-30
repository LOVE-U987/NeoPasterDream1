package com.pasterdream.pasterdreammod.api.itemmigration.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.api.itemmigration.ItemMigrationAPI;
import com.pasterdream.pasterdreammod.api.itemmigration.builder.FoodItemBuilder;
import com.pasterdream.pasterdreammod.api.itemmigration.builder.SimpleItemBuilder;
import com.pasterdream.pasterdreammod.api.itemmigration.model.FoodSpec;
import com.pasterdream.pasterdreammod.api.itemmigration.model.ItemSpec;
import com.pasterdream.pasterdreammod.api.itemmigration.model.MigrationCategory;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 物品移植管理器 —— 追踪物品移植进度、批量注册、生成迁移报告
 * <p>
 * 负责管理从旧版模组向新版模组移植物品的整个生命周期，
 * 包括标记移植状态、批量注册基础物品和食物物品，
 * 以及生成可视化的迁移进度报告。
 * <p>
 * 所有注册操作基于 {@link PDItems#ITEMS}，通过 {@link SimpleItemBuilder} 和 {@link FoodItemBuilder} 执行。
 */
public class MigrationManager {

    /** Gson 实例 —— 用于生成语言 JSON */
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    /** 按类别追踪已移植物品的注册名集合 */
    private final Map<MigrationCategory, Set<String>> migratedItems;

    /** 按类别追踪待移植物品的注册名集合 */
    private final Map<MigrationCategory, Set<String>> pendingItems;

    /** 所有已注册物品的注册名集合（用于快速查找） */
    private final Set<String> allMigratedNames;

    /** 迁移过程中的警告信息列表 */
    private final List<String> warnings;

    /**
     * 构造迁移管理器 —— 初始化所有追踪容器
     */
    public MigrationManager() {
        this.migratedItems = new EnumMap<>(MigrationCategory.class);
        this.pendingItems = new EnumMap<>(MigrationCategory.class);
        this.allMigratedNames = ConcurrentHashMap.newKeySet();
        this.warnings = Collections.synchronizedList(new ArrayList<>());

        for (MigrationCategory category : MigrationCategory.values()) {
            migratedItems.put(category, ConcurrentHashMap.newKeySet());
            pendingItems.put(category, ConcurrentHashMap.newKeySet());
        }
    }

    /**
     * 标记指定物品为已移植状态
     *
     * @param category      物品所属类别
     * @param registryNames 物品的注册名列表
     */
    public void markMigrated(MigrationCategory category, String... registryNames) {
        Set<String> target = migratedItems.get(category);
        for (String name : registryNames) {
            target.add(name);
            allMigratedNames.add(name);
            pendingItems.get(category).remove(name);
        }
    }

    /**
     * 标记指定物品为待移植状态
     *
     * @param category      物品所属类别
     * @param registryNames 物品的注册名列表
     */
    public void markPending(MigrationCategory category, String... registryNames) {
        Set<String> target = pendingItems.get(category);
        for (String name : registryNames) {
            target.add(name);
        }
    }

    /**
     * 检查指定注册名的物品是否已被移植
     *
     * @param registryName 物品的注册名
     * @return 如果已移植返回 true，否则返回 false
     */
    public boolean isMigrated(String registryName) {
        return allMigratedNames.contains(registryName);
    }

    /**
     * 获取指定类别的已移植物品集合
     *
     * @param category 物品类别
     * @return 已移植物品注册名的不可变集合
     */
    public Set<String> getMigratedItems(MigrationCategory category) {
        return Collections.unmodifiableSet(migratedItems.get(category));
    }

    /**
     * 获取指定类别的待移植物品集合
     *
     * @param category 物品类别
     * @return 待移植物品注册名的不可变集合
     */
    public Set<String> getPendingItems(MigrationCategory category) {
        return Collections.unmodifiableSet(pendingItems.get(category));
    }

    /**
     * 获取所有已移植物品的注册名集合
     *
     * @return 已移植物品注册名的不可变集合
     */
    public Set<String> getAllMigratedNames() {
        return Collections.unmodifiableSet(allMigratedNames);
    }

    /**
     * 添加迁移警告信息
     *
     * @param warning 警告文本
     */
    public void addWarning(String warning) {
        warnings.add(warning);
    }

    /**
     * 获取所有迁移警告
     *
     * @return 警告列表的不可变视图
     */
    public List<String> getWarnings() {
        return Collections.unmodifiableList(warnings);
    }

    /**
     * 批量注册简易材料物品
     * <p>
     * 遍历传入的 {@link ItemSpec} 列表，使用 {@link SimpleItemBuilder} 逐一构建并注册到 {@link ItemMigrationAPI#REGISTRY}，
     * 根据 ItemSpec 配置堆叠数、稀有度、防火属性及描述文本，
     * 注册成功后自动将物品标记为 {@link MigrationCategory#MATERIAL} 类别已移植。
     *
     * @param specs 物品规格列表
     * @return 已注册的 DeferredItem 列表
     */
    public List<DeferredItem<Item>> batchRegisterSimple(List<ItemSpec> specs) {
        List<DeferredItem<Item>> registered = new ArrayList<>();

        for (ItemSpec spec : specs) {
            try {
                SimpleItemBuilder builder = new SimpleItemBuilder(ItemMigrationAPI.REGISTRY, spec.registryName())
                        .stacksTo(spec.stackSize())
                        .rarity(spec.rarity());
                if (spec.fireResistant()) {
                    builder.fireResistant();
                }
                if (!spec.tooltipLines().isEmpty()) {
                    builder.tooltip(spec.tooltipLines().toArray(new String[0]));
                }
                DeferredItem<Item> item = builder.build();
                registered.add(item);
                markMigrated(MigrationCategory.MATERIAL, spec.registryName());
                PasterDreamMod.LOGGER.debug("[MigrationManager] 已注册简单物品: {}", spec.registryName());
            } catch (Exception e) {
                warnings.add("注册物品失败: " + spec.registryName() + " - " + e.getMessage());
                PasterDreamMod.LOGGER.error("[MigrationManager] 注册物品失败: {}", spec.registryName(), e);
            }
        }

        return registered;
    }

    /**
     * 批量注册食物物品
     * <p>
     * 遍历传入的名称到食物规格映射，使用 {@link FoodItemBuilder} 逐一构建并注册到 {@link ItemMigrationAPI#REGISTRY}，
     * 根据 FoodSpec 配置营养值、饱和度、食用方式等属性，
     * 注册成功后自动将物品标记为 {@link MigrationCategory#FOOD} 类别已移植。
     *
     * @param specs 名称到食物规格的映射（name -> FoodSpec）
     * @return 已注册的 DeferredItem 列表
     */
    public List<DeferredItem<Item>> batchRegisterFood(Map<String, FoodSpec> specs) {
        List<DeferredItem<Item>> registered = new ArrayList<>();

        for (Map.Entry<String, FoodSpec> entry : specs.entrySet()) {
            String name = entry.getKey();
            FoodSpec spec = entry.getValue();
            try {
                FoodItemBuilder builder = new FoodItemBuilder(ItemMigrationAPI.REGISTRY, name)
                        .nutrition(spec.nutrition())
                        .saturationModifier(spec.saturationModifier());
                if (spec.alwaysEdible()) {
                    builder.alwaysEdible();
                }
                if (spec.fastFood()) {
                    builder.fastFood();
                }
                DeferredItem<Item> item = builder.build();
                registered.add(item);
                markMigrated(MigrationCategory.FOOD, name);
                PasterDreamMod.LOGGER.debug("[MigrationManager] 已注册食物物品: {}", name);
            } catch (Exception e) {
                warnings.add("注册食物物品失败: " + name + " - " + e.getMessage());
                PasterDreamMod.LOGGER.error("[MigrationManager] 注册食物物品失败: {}", name, e);
            }
        }

        return registered;
    }

    /**
     * 生成语言文件（.lang.json）的 JSON 字符串内容
     * <p>
     * 将传入的翻译键-翻译文本映射直接序列化为标准 JSON 格式。
     * 条目按键排序以保证输出一致性。
     *
     * @param modId   模组 ID（用于日志记录，不直接影响 JSON 生成）
     * @param entries 翻译键到翻译文本的映射表
     * @return 格式化后的 JSON 字符串，可直接写入语言文件
     */
    public String generateLangJson(String modId, Map<String, String> entries) {
        TreeMap<String, String> sorted = new TreeMap<>(entries);
        JsonObject root = new JsonObject();
        for (Map.Entry<String, String> entry : sorted.entrySet()) {
            root.addProperty(entry.getKey(), entry.getValue());
        }
        return GSON.toJson(root);
    }

    /**
     * 生成移植进度报告
     * <p>
     * 统计每个类别的已移植/待移植数量，计算总完成百分比，
     * 并包含所有迁移过程中的警告信息。
     *
     * @return 格式化的移植报告字符串
     */
    public String generateReport() {
        MigrationReport report = new MigrationReport(PasterDreamMod.MOD_ID);

        for (MigrationCategory category : MigrationCategory.values()) {
            int migrated = migratedItems.get(category).size();
            int pending = pendingItems.get(category).size();
            report.setCategoryCounts(category, migrated + pending, migrated);
        }

        warnings.forEach(report::addWarning);

        return report.toConsoleSummary();
    }

    /**
     * 重置所有追踪状态
     * <p>
     * 清空已移植、待移植、已注册名称集合以及警告列表。
     * 注意：此操作不会取消物品注册，仅清除追踪记录。
     */
    public void reset() {
        for (MigrationCategory category : MigrationCategory.values()) {
            migratedItems.get(category).clear();
            pendingItems.get(category).clear();
        }
        allMigratedNames.clear();
        warnings.clear();
        PasterDreamMod.LOGGER.info("[MigrationManager] 所有追踪状态已重置");
    }
}
