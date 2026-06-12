package com.pasterdream.pasterdreammod.api.itemmigration.gen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.pasterdream.pasterdreammod.api.itemmigration.model.ItemSpec;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 语言文件生成器 —— 自动生成物品的本地化翻译条目
 * <p>
 * 提供静态工具方法，将物品的注册名自动转换为标准翻译键，
 * 并生成符合 Minecraft 语言文件格式的 JSON 片段/完整内容。
 * 支持 snake_case 到可读展示名的转换。
 */
public class LanguageGenerator {

    /** Gson 实例 —— 漂亮输出格式 */
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private LanguageGenerator() {
    }

    /**
     * 根据模组 ID 和注册名生成标准物品翻译键
     * <p>
     * 格式: {@code item.<modId>.<registryName>}
     *
     * @param modId        模组 ID
     * @param registryName 物品注册名
     * @return 完整的翻译键，如 "item.pasterdream.titanium_ingot"
     */
    public static String itemKey(String modId, String registryName) {
        return "item." + modId + "." + registryName;
    }

    /**
     * 将 snake_case 格式的字符串转换为英文可读名称
     * <p>
     * 转换规则：
     * <ul>
     *   <li>下划线替换为空格</li>
     *   <li>每个单词首字母大写</li>
     * </ul>
     *
     * @param snakeCase snake_case 格式的字符串
     * @return 转换后的可读英文名称，如 "titanium_ingot" -> "Titanium Ingot"
     */
    public static String snakeToEnglishDisplay(String snakeCase) {
        if (snakeCase == null || snakeCase.isEmpty()) {
            return "";
        }

        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = true;

        for (int i = 0; i < snakeCase.length(); i++) {
            char c = snakeCase.charAt(i);
            if (c == '_') {
                result.append(' ');
                capitalizeNext = true;
            } else if (capitalizeNext) {
                result.append(Character.toUpperCase(c));
                capitalizeNext = false;
            } else {
                result.append(c);
            }
        }

        return result.toString();
    }

    /**
     * 生成单个物品的语言文件条目 JSON 片段
     * <p>
     * 格式: {@code "item.<modId>.<registryName>": "<displayName>"}
     *
     * @param modId        模组 ID
     * @param registryName 物品注册名
     * @param displayName  物品显示名称
     * @return JSON 键值对字符串
     */
    public static String generateItemLangEntry(String modId, String registryName, String displayName) {
        String key = itemKey(modId, registryName);
        return "  \"" + key + "\": \"" + escapeJson(displayName) + "\"";
    }

    /**
     * 批量生成语言文件 JSON 内容
     * <p>
     * 遍历条目映射，生成完整的语言 JSON 文件内容。
     * 条目按键排序，输出格式美观。
     *
     * @param modId   模组 ID
     * @param entries 注册名到显示名称的映射
     * @return 完整的语言 JSON 字符串
     */
    public static String generateLangJson(String modId, Map<String, String> entries) {
        TreeMap<String, String> sorted = new TreeMap<>();
        for (Map.Entry<String, String> entry : entries.entrySet()) {
            sorted.put(itemKey(modId, entry.getKey()), entry.getValue());
        }

        JsonObject root = new JsonObject();
        for (Map.Entry<String, String> entry : sorted.entrySet()) {
            root.addProperty(entry.getKey(), entry.getValue());
        }

        return GSON.toJson(root);
    }

    /**
     * 从 ItemSpec 列表生成语言条目
     * <p>
     * 遍历 ItemSpec 列表，使用注册名自动生成可读英文显示名。
     * 如需自定义显示名，请使用 {@link #generateLangJson(String, Map)}。
     *
     * @param modId 模组 ID
     * @param specs 物品规格列表
     * @return 完整的语言 JSON 字符串
     */
    public static String generateFromSpecs(String modId, List<ItemSpec> specs) {
        Map<String, String> entries = new LinkedHashMap<>();
        for (ItemSpec spec : specs) {
            String displayName = snakeToEnglishDisplay(spec.registryName());
            entries.put(spec.registryName(), displayName);
        }
        return generateLangJson(modId, entries);
    }

    /**
     * 自动生成创造模式标签页翻译键
     * <p>
     * 格式: {@code itemGroup.<modId>.<tabName>}
     *
     * @param modId   模组 ID
     * @param tabName 标签页名称
     * @return 完整的翻译键
     */
    public static String creativeTabKey(String modId, String tabName) {
        return "itemGroup." + modId + "." + tabName;
    }

    /**
     * 转义 JSON 字符串中的特殊字符
     *
     * @param input 原始字符串
     * @return 转义后的字符串
     */
    private static String escapeJson(String input) {
        if (input == null) {
            return "";
        }
        return input
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
