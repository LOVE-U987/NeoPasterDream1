package com.pasterdream.pasterdreammod.api.block.loot;

import com.pasterdream.pasterdreammod.api.PasterDreamAPI;
import com.pasterdream.pasterdreammod.api.itemmigration.gen.LootTableGenerator;
import com.pasterdream.pasterdreammod.api.itemmigration.gen.LootTableGenerator.DropEntry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 方块战利品表生成 API —— 为 BlockAPI 的三类注册模式提供配套战利品表生成
 * <p>
 * 与 {@link com.pasterdream.pasterdreammod.api.block.BlockAPI} 的三种模式一一对应：
 * <ul>
 *   <li><b>模式一</b>：{@link #selfDrop(String)} / {@link #selfDropAll(String...)} 对应 SimpleBlockBuilder</li>
 *   <li><b>模式二</b>：{@link #variantSetDropAll(String)} 对应 VariantSetBuilder</li>
 *   <li><b>模式三</b>：{@link #batchDropSelf(String, int...)} / {@link #batchDropSelfRange(String, int, int, int...)} 对应 BatchBlockBuilder</li>
 * </ul>
 * <p>
 * 所有方法生成的 JSON 文件默认写入 {@code src/main/resources/data/{modId}/loot_table/blocks/} 目录。
 * <p>
 * 使用示例：
 * <pre>{@code
 * // 模式一：批量生成基础方块的战利品表
 * BlockLootAPI.selfDrop("dyedream_block");
 * BlockLootAPI.selfDropAll("dyedream_block", "dyedream_dirt", "dyedream_sand");
 *
 * // 矿石掉落（时运加成）
 * BlockLootAPI.oreDrop("dyedreamdust_ore", "dyedream_dust");
 *
 * // 模式二：生成建筑变体族战利品表
 * BlockLootAPI.variantSetDropAll("dyedream_planks");
 *
 * // 模式三：批量生成花/草的战利品表
 * BlockLootAPI.batchDropSelf("flower", 1, 2, 3, 5, 6, 8, 9, 13, 14, 15, 16, 17);
 * }</pre>
 */
public final class BlockLootAPI {

    /** 模组 ID —— 用于构建命名空间和资源目录路径 */
    public static final String MOD_ID = PasterDreamAPI.MOD_ID;

    /** 资源文件基础路径 */
    public static final String BASE_PATH = "src/main/resources";

    // ======================== 基础方法 ========================

    /**
     * 生成方块自掉落的战利品表
     * <p>
     * 方块被破坏时掉落自身，附带爆炸生存条件。
     *
     * @param blockName 方块注册名（如 "dyedream_block"）
     * @throws RuntimeException 如果文件写入失败
     */
    public static void selfDrop(String blockName) {
        String blockId = MOD_ID + ":" + blockName;
        PasterDreamAPI.LOGGER.debug("[BlockLootAPI] selfDrop() 方块: {}", blockName);
        String json = LootTableGenerator.generateSelfDrop(blockId);
        saveToFile(blockName, json);
    }

    /**
     * 生成矿石掉落的战利品表（时运加成 + 爆炸衰减）
     * <p>
     * 方块被破坏时掉落指定物品（不是方块自身），
     * 应用 {@code apply_bonus + fortune + ore_drops} 公式。
     *
     * @param blockName   方块注册名（如 "dyedreamdust_ore"）
     * @param dropItemId  掉落物品 ID，自动补全命名空间（如 "dyedream_dust" 或 "pasterdream:dyedream_dust"）
     * @throws RuntimeException 如果文件写入失败
     */
    public static void oreDrop(String blockName, String dropItemId) {
        String fullId = ensureNamespace(dropItemId);
        PasterDreamAPI.LOGGER.debug("[BlockLootAPI] oreDrop() 方块: {}, 掉落: {}", blockName, fullId);
        String json = LootTableGenerator.generateOreDrop(fullId, blockName);
        saveToFile(blockName, json);
    }

    /**
     * 生成精准采集掉落的战利品表
     * <p>
     * 仅在使用精准采集附魔工具时掉落方块自身，
     * 普通破坏不掉落任何物品。
     *
     * @param blockName 方块注册名（如 "carve_dyedream_glass"）
     * @throws RuntimeException 如果文件写入失败
     */
    public static void silkTouchDrop(String blockName) {
        String blockId = MOD_ID + ":" + blockName;
        PasterDreamAPI.LOGGER.debug("[BlockLootAPI] silkTouchDrop() 方块: {}", blockName);
        String json = LootTableGenerator.generateSilkTouchDrop(blockId);
        saveToFile(blockName, json);
    }

    /**
     * 生成自定义掉落的战利品表
     * <p>
     * 精细控制掉落的三种特性：精准采集要求、时运加成、爆炸衰减。
     *
     * @param blockName      方块注册名
     * @param dropItemId     掉落物品 ID，自动补全命名空间
     * @param silkTouchOnly  是否仅精准采集掉落
     * @param fortuneEnabled 是否启用时运加成
     * @param explosionDecay 是否启用爆炸衰减
     * @throws RuntimeException 如果文件写入失败
     */
    public static void customDrop(String blockName, String dropItemId,
                                  boolean silkTouchOnly, boolean fortuneEnabled,
                                  boolean explosionDecay) {
        String fullId = ensureNamespace(dropItemId);
        PasterDreamAPI.LOGGER.debug("[BlockLootAPI] customDrop() 方块: {}, 掉落: {}", blockName, fullId);
        String json = LootTableGenerator.generateCustomDrop(fullId, blockName,
                silkTouchOnly, fortuneEnabled, explosionDecay);
        saveToFile(blockName, json);
    }

    /**
     * 生成多物品掉落的战利品表
     * <p>
     * 一个方块的战利品池中包含多个独立物品条目，
     * 每个条目可独立配置数量、概率、精准采集和时运。
     *
     * @param blockName 方块注册名
     * @param entries   掉落条目列表
     * @throws RuntimeException 如果文件写入失败
     */
    public static void multiDrop(String blockName, List<DropEntry> entries) {
        PasterDreamAPI.LOGGER.debug("[BlockLootAPI] multiDrop() 方块: {}, 条目数: {}", blockName, entries.size());
        String json = LootTableGenerator.generateMultiDrop(entries, blockName);
        saveToFile(blockName, json);
    }

    // ======================== 模式一：SimpleBlockBuilder 配套 ========================

    /**
     * 批量生成多个方块的自掉落战利品表
     * <p>
     * 适用于 {@link com.pasterdream.pasterdreammod.api.block.builder.SimpleBlockBuilder#build()}
     * 注册的一系列基础换皮方块。
     *
     * @param blockNames 方块注册名列表（如 "dyedream_block", "dyedream_dirt", ...）
     * @throws RuntimeException 如果有任何文件写入失败
     */
    public static void selfDropAll(String... blockNames) {
        PasterDreamAPI.LOGGER.info("[BlockLootAPI] selfDropAll() 生成 {} 个方块战利品表", blockNames.length);
        for (String name : blockNames) {
            selfDrop(name);
        }
    }

    /**
     * 批量生成多个矿石方块掉落的战利品表
     * <p>
     * 每个矿石方块需指定对应的掉落物品 ID。
     *
     * @param drops 矿石掉落对数组，每对为 (blockName, dropItemId)
     * @throws RuntimeException 如果有任何文件写入失败
     */
    public static void oreDropAll(OreDropPair... drops) {
        PasterDreamAPI.LOGGER.info("[BlockLootAPI] oreDropAll() 生成 {} 个矿石战利品表", drops.length);
        for (OreDropPair pair : drops) {
            oreDrop(pair.blockName(), pair.dropItemId());
        }
    }

    /**
     * 矿石掉落对 —— 描述一个矿石方块及其掉落物品的对应关系
     *
     * @param blockName  方块注册名
     * @param dropItemId 掉落物品 ID
     */
    public record OreDropPair(String blockName, String dropItemId) {
    }

    // ======================== 模式二：VariantSetBuilder 配套 ========================

    /**
     * 生成建筑变体族全套方块的自掉落战利品表
     * <p>
     * 与 {@link com.pasterdream.pasterdreammod.api.block.builder.VariantSetBuilder}
     * 的常用变体一一对应，自动生成所有建筑变体的 JSON 文件。
     * <p>
     * 默认生成的变体集合：
     * <ul>
     *   <li>{baseName}_stairs</li>
     *   <li>{baseName}_slab</li>
     *   <li>{baseName}_wall</li>
     *   <li>{baseName}_fence</li>
     *   <li>{baseName}_fencegate</li>
     *   <li>{baseName}_door</li>
     *   <li>{baseName}_trapdoor</li>
     *   <li>{baseName}_pressure_plate</li>
     *   <li>{baseName}_button</li>
     * </ul>
     *
     * @param baseName 基础方块名称前缀（如 "dyedream_planks"）
     * @throws RuntimeException 如果有任何文件写入失败
     */
    public static void variantSetDropAll(String baseName) {
        PasterDreamAPI.LOGGER.info("[BlockLootAPI] variantSetDropAll() 生成 {} 变体族战利品表", baseName);
        variantSetDropAll(baseName,
                "_stairs", "_slab", "_wall",
                "_fence", "_fencegate",
                "_door", "_trapdoor",
                "_pressure_plate", "_button");
    }

    /**
     * 生成建筑变体族指定变体的自掉落战利品表
     * <p>
     * 通过变体后缀列表精确控制生成哪些变体的战利品表。
     *
     * @param baseName  基础方块名称前缀
     * @param suffixes 变体后缀列表（如 "_stairs", "_slab", "_fence" 等）
     * @throws RuntimeException 如果有任何文件写入失败
     */
    public static void variantSetDropAll(String baseName, String... suffixes) {
        PasterDreamAPI.LOGGER.debug("[BlockLootAPI] variantSetDropAll() 方块: {}, 后缀: {}", baseName, String.join(", ", suffixes));
        for (String suffix : suffixes) {
            selfDrop(baseName + suffix);
        }
    }

    // ======================== 模式三：BatchBlockBuilder 配套 ========================

    /**
     * 批量生成指定编号方块的自动掉战利品表
     * <p>
     * 适用于 {@link com.pasterdream.pasterdreammod.api.block.builder.BatchBlockBuilder#build()}
     * 中通过 {@code indexList()} 设置的非连续编号。
     *
     * @param baseName 方块名称前缀（如 "flower"）
     * @param indices  编号列表（如 1, 2, 3, 5, 6, 8, 9 ...）
     * @throws RuntimeException 如果有任何文件写入失败
     */
    public static void batchDropSelf(String baseName, int... indices) {
        PasterDreamAPI.LOGGER.info("[BlockLootAPI] batchDropSelf() {} 生成 {} 个方块战利品表", baseName, indices.length);
        for (int index : indices) {
            selfDrop(baseName + "_" + index);
        }
    }

    /**
     * 批量生成连续编号方块的自掉落战利品表
     * <p>
     * 适用于 {@link com.pasterdream.pasterdreammod.api.block.builder.BatchBlockBuilder}
     * 中通过 {@code range()} 设置的连续编号范围，并可排除指定索引。
     *
     * @param baseName       方块名称前缀
     * @param start          起始编号（包含）
     * @param end            结束编号（包含）
     * @param excludeIndices 需要排除的编号（可选）
     * @throws RuntimeException 如果有任何文件写入失败
     */
    public static void batchDropSelfRange(String baseName, int start, int end, int... excludeIndices) {
        PasterDreamAPI.LOGGER.debug("[BlockLootAPI] batchDropSelfRange() 方块: {}, 范围: [{}, {}]", baseName, start, end);
        List<Integer> excludes = new ArrayList<>();
        for (int ex : excludeIndices) {
            excludes.add(ex);
        }
        int count = 0;
        for (int i = start; i <= end; i++) {
            if (!excludes.contains(i)) {
                selfDrop(baseName + "_" + i);
                count++;
            }
        }
        PasterDreamAPI.LOGGER.info("[BlockLootAPI] batchDropSelfRange() 完成: {}, 生成 {} 个", baseName, count);
    }

    // ======================== 私有辅助 ========================

    /**
     * 确保物品 ID 包含命名空间前缀
     *
     * @param itemId 物品 ID（可能带或不带命名空间）
     * @return 确保带命名空间的完整 ID
     */
    private static String ensureNamespace(String itemId) {
        if (itemId.contains(":")) {
            return itemId;
        }
        return MOD_ID + ":" + itemId;
    }

    /**
     * 将战利品表 JSON 保存到文件
     *
     * @param blockName 方块注册名
     * @param json      战利品表 JSON 字符串
     * @throws RuntimeException 如果文件写入失败
     */
    private static void saveToFile(String blockName, String json) {
        try {
            LootTableGenerator.saveLootTableToFile(json, MOD_ID, blockName, BASE_PATH);
        } catch (IOException e) {
            PasterDreamAPI.LOGGER.error("[BlockLootAPI] 无法保存战利品表文件 [{}]: {}", blockName, e.getMessage(), e);
            throw new RuntimeException("BlockLootAPI: 无法保存战利品表文件 [" + blockName + "]", e);
        }
    }

    private BlockLootAPI() {
        throw new UnsupportedOperationException("BlockLootAPI 是纯工具类，不可实例化");
    }
}