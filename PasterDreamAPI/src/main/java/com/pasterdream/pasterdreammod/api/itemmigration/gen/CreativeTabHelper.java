package com.pasterdream.pasterdreammod.api.itemmigration.gen;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 创造模式标签页代码生成助手 —— 帮助快速生成标签页注册代码片段和语言文件条目
 * <p>
 * 纯工具类，所有方法均返回字符串形式的 Java 代码片段或 JSON 内容。
 * 输出格式与 {@code PDCreativeTabs.java} 中的模式保持一致。
 */
public class CreativeTabHelper {

    private CreativeTabHelper() {
    }

    /**
     * 将蛇形命名（snake_case）转换为大写蛇形命名（UPPER_SNAKE_CASE）
     * <p>
     * 例如: {@code "paster_tab_0"} -> {@code "PASTER_TAB_0"}
     *
     * @param snakeCase 输入字符串
     * @return 转换后的大写蛇形字符串
     */
    private static String toUpperSnakeCase(String snakeCase) {
        if (snakeCase == null || snakeCase.isEmpty()) {
            return "";
        }
        return snakeCase.toUpperCase();
    }

    /**
     * 将蛇形命名转换为注释友好的可读英文
     * <p>
     * 例如: {@code "paster_tab_0"} -> {@code "Paster Tab 0"}
     *
     * @param snakeCase 输入字符串
     * @return 可读英文描述
     */
    private static String snakeToEnglishComment(String snakeCase) {
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
     * 生成完整的创造模式标签页注册代码
     * <p>
     * 输出格式与 {@code PDCreativeTabs.java} 保持一致，包含：
     * <ul>
     *   <li>Javadoc 注释</li>
     *   <li>DeferredHolder 声明</li>
     *   <li>标题、图标、排序、物品显示占位</li>
     * </ul>
     *
     * @param tabId       标签页注册 ID（如 {@code "paster_tab_0"}）
     * @param tabName     标签页注释描述（如 {@code "基础材料与功能方块标签页"}）
     * @param iconItemId  图标物品在 PDItems 中的字段名（如 {@code "DREAM_ACCUMULATOR"}）
     * @param beforeTabId 排序参考标签页在 PDCreativeTabs 中的字段名（如 {@code "PASTER_TAB_1"}），
     *                    若为 null 或空则不生成 {@code withTabsBefore} 行
     * @return 完整的标签页注册代码字符串
     */
    public static String generateTabRegistrationCode(String tabId, String tabName,
                                                     String iconItemId, String beforeTabId) {
        String fieldName = toUpperSnakeCase(tabId);
        String comment = (tabName != null && !tabName.isEmpty())
                ? tabName
                : snakeToEnglishComment(tabId);

        StringBuilder sb = new StringBuilder();
        sb.append("    /**\n");
        sb.append("     * ").append(comment).append("\n");
        sb.append("     */\n");
        sb.append("    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> ")
                .append(fieldName).append(" = TABS.register(\"").append(tabId).append("\",\n");
        sb.append("            () -> CreativeModeTab.builder()\n");
        sb.append("                    .title(Component.translatable(\"itemGroup.pasterdream.")
                .append(tabId).append("\"))\n");
        sb.append("                    .icon(() -> new ItemStack(PDItems.")
                .append(iconItemId).append(".get()))\n");

        if (beforeTabId != null && !beforeTabId.trim().isEmpty()) {
            sb.append("                    .withTabsBefore(PDCreativeTabs.")
                    .append(beforeTabId).append(".getKey())\n");
        }

        sb.append("                    .displayItems((parameters, output) -> {\n");
        sb.append("                        // TODO: 添加物品到此标签页\n");
        sb.append("                        // output.accept(PDItems.YOUR_ITEM.get());\n");
        sb.append("                    })\n");
        sb.append("                    .build());\n");

        return sb.toString();
    }

    /**
     * 生成单行 {@code output.accept} 代码
     * <p>
     * 示例输出: {@code output.accept(PDItems.TITANIUM_INGOT.get());}
     *
     * @param fieldName     物品/方块在注册类中的字段名（如 {@code "TITANIUM_INGOT"}）
     * @param registryClass 注册类名，可选 {@code "PDItems"} 或 {@code "PDBlocks"}
     * @return 单行 {@code output.accept} 代码字符串
     */
    public static String generateDisplayItemLine(String fieldName, String registryClass) {
        String clazz = (registryClass != null && !registryClass.isEmpty())
                ? registryClass
                : "PDItems";
        return "                        output.accept(" + clazz + "." + fieldName + ".get());";
    }

    /**
     * 批量生成 {@code output.accept} 代码行
     * <p>
     * 每行缩进 24 个空格以匹配 {@code displayItems} 内部的缩进层级。
     *
     * @param fieldNames    物品/方块字段名列表
     * @param registryClass 注册类名，可选 {@code "PDItems"} 或 {@code "PDBlocks"}
     * @return 多行 {@code output.accept} 代码字符串（末尾不带换行）
     */
    public static String generateDisplayItemLines(List<String> fieldNames, String registryClass) {
        if (fieldNames == null || fieldNames.isEmpty()) {
            return "";
        }
        String clazz = (registryClass != null && !registryClass.isEmpty())
                ? registryClass
                : "PDItems";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fieldNames.size(); i++) {
            if (i > 0) {
                sb.append("\n");
            }
            sb.append("                        output.accept(")
                    .append(clazz).append(".").append(fieldNames.get(i)).append(".get());");
        }
        return sb.toString();
    }

    /**
     * 生成创造模式标签页的翻译键
     * <p>
     * 格式: {@code itemGroup.<modId>.<tabId>}
     *
     * @param modId 模组 ID（如 {@code "pasterdream"}）
     * @param tabId 标签页注册 ID（如 {@code "paster_tab_0"}）
     * @return 完整的翻译键字符串
     */
    public static String generateLangKey(String modId, String tabId) {
        return "itemGroup." + modId + "." + tabId;
    }

    /**
     * 生成单个创造模式标签页的语言文件 JSON 片段
     * <p>
     * 输出格式: {@code {"itemGroup.<modId>.<tabId>": "<displayName>"}}
     *
     * @param modId       模组 ID
     * @param tabId       标签页注册 ID
     * @param displayName 标签页显示名称
     * @return 单条目 JSON 字符串
     */
    public static String generateTabLangJson(String modId, String tabId, String displayName) {
        String key = generateLangKey(modId, tabId);
        String escapedDisplayName = displayName
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");
        return "  \"" + key + "\": \"" + escapedDisplayName + "\"";
    }

    /**
     * 批量生成多个标签页的语言文件 JSON 片段
     * <p>
     * 条目按键排序，适用于直接粘贴到语言文件中。
     *
     * @param tabEntries 标签页 ID 到显示名称的映射
     * @param modId      模组 ID
     * @return 多行 JSON 条目字符串，每行格式为 {@code "itemGroup.<modId>.<tabId>": "<displayName>"}
     */
    public static String generateTabLangJson(Map<String, String> tabEntries, String modId) {
        if (tabEntries == null || tabEntries.isEmpty()) {
            return "";
        }
        TreeMap<String, String> sorted = new TreeMap<>(tabEntries);
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : sorted.entrySet()) {
            if (!first) {
                sb.append(",\n");
            }
            sb.append(generateTabLangJson(modId, entry.getKey(), entry.getValue()));
            first = false;
        }
        return sb.toString();
    }

    /**
     * 一键生成包含物品和方块列表的完整标签页注册代码
     * <p>
     * 自动组合标签页注册模板和所有 {@code output.accept} 行。
     * 注意：生成的代码不含导入语句，需要手动添加到目标文件。
     *
     * @param tabId              标签页注册 ID
     * @param tabName            标签页注释描述
     * @param iconItemId         图标物品字段名（使用 PDItems）
     * @param beforeTabId        排序参考标签页字段名，若不需要则传 null
     * @param modId              模组 ID（仅用于翻译键）
     * @param itemFields         物品字段名列表，无物品传空列表
     * @param itemRegistryClass  物品注册类名（如 {@code "PDItems"}）
     * @param blockFields        方块字段名列表，无方块传空列表
     * @param blockRegistryClass 方块注册类名（如 {@code "PDBlocks"}）
     * @return 完整的标签页注册代码字符串
     */
    public static String generateCompleteTabWithItems(
            String tabId, String tabName, String iconItemId, String beforeTabId,
            String modId,
            List<String> itemFields, String itemRegistryClass,
            List<String> blockFields, String blockRegistryClass) {

        String fieldName = toUpperSnakeCase(tabId);
        String comment = (tabName != null && !tabName.isEmpty())
                ? tabName
                : snakeToEnglishComment(tabId);
        String langKey = generateLangKey(modId, tabId);

        StringBuilder sb = new StringBuilder();
        // 类注释
        sb.append("    /**\n");
        sb.append("     * ").append(comment).append("\n");
        sb.append("     */\n");

        // 注册声明
        sb.append("    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> ")
                .append(fieldName).append(" = TABS.register(\"").append(tabId).append("\",\n");
        sb.append("            () -> CreativeModeTab.builder()\n");
        sb.append("                    .title(Component.translatable(\"").append(langKey).append("\"))\n");
        sb.append("                    .icon(() -> new ItemStack(PDItems.")
                .append(iconItemId).append(".get()))\n");

        // 排序
        if (beforeTabId != null && !beforeTabId.trim().isEmpty()) {
            sb.append("                    .withTabsBefore(PDCreativeTabs.")
                    .append(beforeTabId).append(".getKey())\n");
        }

        // displayItems
        sb.append("                    .displayItems((parameters, output) -> {\n");

        // 物品
        if (itemFields != null && !itemFields.isEmpty()) {
            String itemClass = (itemRegistryClass != null && !itemRegistryClass.isEmpty())
                    ? itemRegistryClass : "PDItems";
            for (String field : itemFields) {
                sb.append("                        output.accept(")
                        .append(itemClass).append(".").append(field).append(".get());\n");
            }
        }

        // 方块
        if (blockFields != null && !blockFields.isEmpty()) {
            String blockClass = (blockRegistryClass != null && !blockRegistryClass.isEmpty())
                    ? blockRegistryClass : "PDBlocks";
            for (String field : blockFields) {
                sb.append("                        output.accept(")
                        .append(blockClass).append(".").append(field).append(".get());\n");
            }
        }

        // 兜底占位注释
        if ((itemFields == null || itemFields.isEmpty())
                && (blockFields == null || blockFields.isEmpty())) {
            sb.append("                        // TODO: 添加物品到此标签页\n");
        }

        sb.append("                    })\n");
        sb.append("                    .build());\n");

        return sb.toString();
    }
}
