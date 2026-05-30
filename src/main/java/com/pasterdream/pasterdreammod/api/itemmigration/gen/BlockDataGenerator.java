package com.pasterdream.pasterdreammod.api.itemmigration.gen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 方块数据生成器 —— 帮助快速生成方块挖掘标签JSON、方块注册代码片段及BlockItem注册代码
 * <p>
 * 纯工具类，专为辅助AI或开发者批量生成 Minecraft 方块相关数据而设计。
 * 提供挖掘标签生成、方块注册代码生成、BlockItem 注册代码生成等功能。
 */
public class BlockDataGenerator {

    /** Gson 实例 —— 漂亮输出格式 */
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private BlockDataGenerator() {
    }

    // ==================== 挖掘标签 JSON 生成 ====================

    /**
     * 根据区块ID列表生成标签 JSON 的通用方法
     *
     * @param blockIds 方块注册名列表（格式："mod:block_name"）
     * @return 标签 JSON 对象
     */
    private static JsonObject buildTagJson(List<String> blockIds) {
        JsonObject root = new JsonObject();
        JsonArray values = new JsonArray();
        for (String id : blockIds) {
            values.add(id);
        }
        root.add("values", values);
        return root;
    }

    /**
     * 生成 mineable/pickaxe 挖掘标签 JSON
     * <p>
     * 适用于需用镐挖掘的方块（如矿石、石质方块等）
     *
     * @param blockIds 方块注册名列表（格式："mod:block_name"）
     * @return 格式化后的标签 JSON 字符串
     */
    public static String generateMineablePickaxeJson(List<String> blockIds) {
        return GSON.toJson(buildTagJson(blockIds));
    }

    /**
     * 生成 mineable/axe 挖掘标签 JSON
     * <p>
     * 适用于需用斧挖掘的方块（如木质方块等）
     *
     * @param blockIds 方块注册名列表（格式："mod:block_name"）
     * @return 格式化后的标签 JSON 字符串
     */
    public static String generateMineableAxeJson(List<String> blockIds) {
        return GSON.toJson(buildTagJson(blockIds));
    }

    /**
     * 生成 mineable/shovel 挖掘标签 JSON
     * <p>
     * 适用于需用锹挖掘的方块（如泥土、沙砾等）
     *
     * @param blockIds 方块注册名列表（格式："mod:block_name"）
     * @return 格式化后的标签 JSON 字符串
     */
    public static String generateMineableShovelJson(List<String> blockIds) {
        return GSON.toJson(buildTagJson(blockIds));
    }

    /**
     * 生成 mineable/hoe 挖掘标签 JSON
     * <p>
     * 适用于需用锄挖掘的方块（如树叶、干草块等）
     *
     * @param blockIds 方块注册名列表（格式："mod:block_name"）
     * @return 格式化后的标签 JSON 字符串
     */
    public static String generateMineableHoeJson(List<String> blockIds) {
        return GSON.toJson(buildTagJson(blockIds));
    }

    /**
     * 生成 needs_stone_tool 标签 JSON
     * <p>
     * 标记需要石镐及以上等级工具才能挖掘的方块
     *
     * @param blockIds 方块注册名列表（格式："mod:block_name"）
     * @return 格式化后的标签 JSON 字符串
     */
    public static String generateNeedsStoneToolJson(List<String> blockIds) {
        return GSON.toJson(buildTagJson(blockIds));
    }

    /**
     * 生成 needs_iron_tool 标签 JSON
     * <p>
     * 标记需要铁镐及以上等级工具才能挖掘的方块
     *
     * @param blockIds 方块注册名列表（格式："mod:block_name"）
     * @return 格式化后的标签 JSON 字符串
     */
    public static String generateNeedsIronToolJson(List<String> blockIds) {
        return GSON.toJson(buildTagJson(blockIds));
    }

    /**
     * 生成 needs_diamond_tool 标签 JSON
     * <p>
     * 标记需要钻石镐及以上等级工具才能挖掘的方块
     *
     * @param blockIds 方块注册名列表（格式："mod:block_name"）
     * @return 格式化后的标签 JSON 字符串
     */
    public static String generateNeedsDiamondToolJson(List<String> blockIds) {
        return GSON.toJson(buildTagJson(blockIds));
    }

    /**
     * 一键生成所有挖掘标签 JSON
     * <p>
     * 根据传入的各分类方块列表，批量生成对应的挖掘标签 JSON，
     * 返回 Map 结构，key 为标签文件名（如 "mineable/pickaxe.json"），
     * value 为格式化后的 JSON 内容字符串。
     *
     * @param modId         模组 ID
     * @param pickaxeBlocks 镭类挖掘方块列表（格式："mod:block_name"）
     * @param axeBlocks     斧类挖掘方块列表（格式："mod:block_name"）
     * @param shovelBlocks  锹类挖掘方块列表（格式："mod:block_name"）
     * @param hoeBlocks     锄类挖掘方块列表（格式："mod:block_name"）
     * @param stoneBlocks   需要石工具等级方块列表（格式："mod:block_name"）
     * @param ironBlocks    需要铁工具等级方块列表（格式："mod:block_name"）
     * @param diamondBlocks 需要钻石工具等级方块列表（格式："mod:block_name"）
     * @return 标签文件名到 JSON 内容的映射
     */
    public static Map<String, String> generateDefaultMiningTags(
            String modId,
            List<String> pickaxeBlocks,
            List<String> axeBlocks,
            List<String> shovelBlocks,
            List<String> hoeBlocks,
            List<String> stoneBlocks,
            List<String> ironBlocks,
            List<String> diamondBlocks) {

        Map<String, String> result = new LinkedHashMap<>();

        if (pickaxeBlocks != null && !pickaxeBlocks.isEmpty()) {
            result.put("mineable/pickaxe.json", generateMineablePickaxeJson(pickaxeBlocks));
        }
        if (axeBlocks != null && !axeBlocks.isEmpty()) {
            result.put("mineable/axe.json", generateMineableAxeJson(axeBlocks));
        }
        if (shovelBlocks != null && !shovelBlocks.isEmpty()) {
            result.put("mineable/shovel.json", generateMineableShovelJson(shovelBlocks));
        }
        if (hoeBlocks != null && !hoeBlocks.isEmpty()) {
            result.put("mineable/hoe.json", generateMineableHoeJson(hoeBlocks));
        }
        if (stoneBlocks != null && !stoneBlocks.isEmpty()) {
            result.put("needs_stone_tool.json", generateNeedsStoneToolJson(stoneBlocks));
        }
        if (ironBlocks != null && !ironBlocks.isEmpty()) {
            result.put("needs_iron_tool.json", generateNeedsIronToolJson(ironBlocks));
        }
        if (diamondBlocks != null && !diamondBlocks.isEmpty()) {
            result.put("needs_diamond_tool.json", generateNeedsDiamondToolJson(diamondBlocks));
        }

        return result;
    }

    // ==================== 注册代码片段生成 ====================

    /**
     * 将 snake_case 格式的注册名转换为 UPPER_SNAKE_CASE 格式的字段名
     * <p>
     * 例如: "example_block" -> "EXAMPLE_BLOCK"
     *
     * @param snakeCase snake_case 格式的字符串
     * @return 转换后的 UPPER_SNAKE_CASE 格式
     */
    private static String snakeToUpperSnakeCase(String snakeCase) {
        if (snakeCase == null || snakeCase.isEmpty()) {
            return "";
        }
        return snakeCase.toUpperCase();
    }

    /**
     * 将 snake_case 格式的注册名转换为 PascalCase 格式的类名
     * <p>
     * 例如: "example_block" -> "ExampleBlock"
     *
     * @param snakeCase snake_case 格式的字符串
     * @return 转换后的 PascalCase 格式
     */
    private static String snakeToPascalCase(String snakeCase) {
        if (snakeCase == null || snakeCase.isEmpty()) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = true;
        for (int i = 0; i < snakeCase.length(); i++) {
            char c = snakeCase.charAt(i);
            if (c == '_') {
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
     * 生成 PDBlocks.java 风格的方块注册代码片段
     * <p>
     * 根据参数自动选择注册方式：
     * <ul>
     *   <li>无自定义类：使用 {@code registerSimpleBlock} 注册，返回 {@code DeferredBlock<Block>}</li>
     *   <li>有自定义类：使用 {@code registerBlock} 注册，返回 {@code DeferredBlock<自定义类>}</li>
     * </ul>
     *
     * @param blockName      方块注册名（snake_case 格式，如 "example_block"）
     * @param blockClass     方块字段名（UPPER_SNAKE_CASE 格式，如 "EXAMPLE_BLOCK"）
     * @param templateBlock  模板方块引用（如 "Blocks.STONE"）或自定义方块类全限定名
     * @param hasCustomClass 是否有自定义方块类，{@code true} 则使用 registerBlock 并生成对应类名
     * @return 方块注册代码片段字符串
     */
    public static String generateBlockRegistrationCode(String blockName, String blockClass, String templateBlock, boolean hasCustomClass) {
        if (hasCustomClass) {
            // 有自定义方块类 —— 使用 BLOCKS.registerBlock() 模式
            String customClassName = snakeToPascalCase(blockName);
            return String.format(
                    "    public static final DeferredBlock<%s> %s = BLOCKS.registerBlock(\"%s\",%n" +
                    "            %s::new, BlockBehaviour.Properties.ofFullCopy(%s));",
                    customClassName, blockClass, blockName, customClassName, templateBlock
            );
        } else {
            // 无自定义方块类 —— 使用 BLOCKS.registerSimpleBlock() 模式
            return String.format(
                    "    public static final DeferredBlock<Block> %s = BLOCKS.registerSimpleBlock(\"%s\",%n" +
                    "            BlockBehaviour.Properties.ofFullCopy(%s));",
                    blockClass, blockName, templateBlock
            );
        }
    }

    /**
     * 生成 PDItems.java 风格的 BlockItem 注册代码片段
     * <p>
     * 使用 {@code ITEMS.registerSimpleBlockItem()} 快速注册方块的对应物品形式。
     *
     * @param blockName 方块/物品注册名（snake_case 格式，如 "example_block"）
     * @return BlockItem 注册代码片段字符串
     */
    public static String generateBlockItemRegistrationCode(String blockName) {
        String fieldName = snakeToUpperSnakeCase(blockName);
        return String.format(
                "    public static final DeferredItem<BlockItem> %1$s = ITEMS.registerSimpleBlockItem(\"%2$s\", PDBlocks.%1$s);",
                fieldName, blockName
        );
    }

    // ==================== 文件保存 ====================

    /**
     * 将标签 JSON 保存到文件
     * <p>
     * 保存路径格式: {@code basePath/data/modId/tags/block/tagPath.json}
     * 例如: tagPath 为 "mineable/pickaxe" 时，保存到 {@code basePath/data/modId/tags/block/mineable/pickaxe.json}
     * <p>
     * 如果目标目录不存在，会自动创建.
     *
     * @param tagJson 标签 JSON 内容字符串
     * @param modId   模组 ID
     * @param tagPath 标签路径（如 "mineable/pickaxe"、"needs_iron_tool"）
     * @param basePath 基础路径（通常为 "src/generated/resources" 或 "src/main/resources"）
     * @throws IOException 如果文件写入失败
     */
    public static void saveTagToFile(String tagJson, String modId, String tagPath, String basePath) throws IOException {
        // 构建完整路径: basePath/data/modId/tags/block/tagPath.json
        String fullPath = basePath + "/data/" + modId + "/tags/block/" + tagPath + ".json";
        Path outputPath = Paths.get(fullPath);

        // 确保父目录存在
        Path parentDir = outputPath.getParent();
        if (parentDir != null && !Files.exists(parentDir)) {
            Files.createDirectories(parentDir);
        }

        // 写入文件
        Files.writeString(outputPath, tagJson);
    }
}
