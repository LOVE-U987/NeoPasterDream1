package com.pasterdream.pasterdreammod.api.itemmigration.manager;

import com.pasterdream.pasterdreammod.api.itemmigration.model.MigrationCategory;

import java.util.*;

/**
 * 迁移报告记录类 —— 用于生成格式化的物品移植进度报告
 * <p>
 * 统计每个迁移类别的总数和已移植数，计算完成百分比，
 * 支持生成 Markdown 格式和控制台格式的摘要报告。
 */
public class MigrationReport {

    /** 模组 ID */
    private final String modId;

    /** 各类别物品总数（类别 -> 总数） */
    private final Map<MigrationCategory, Integer> totalCounts;

    /** 各类别已移植物品数（类别 -> 已移植数） */
    private final Map<MigrationCategory, Integer> migratedCounts;

    /** 迁移过程中的警告信息 */
    private final List<String> warnings;

    /** 迁移过程中的错误信息 */
    private final List<String> errors;

    /** 报告分隔线 */
    private static final String SEPARATOR = "========================================";

    /**
     * 构造迁移报告
     *
     * @param modId 模组 ID
     */
    public MigrationReport(String modId) {
        this.modId = modId;
        this.totalCounts = new EnumMap<>(MigrationCategory.class);
        this.migratedCounts = new EnumMap<>(MigrationCategory.class);
        this.warnings = new ArrayList<>();
        this.errors = new ArrayList<>();

        for (MigrationCategory category : MigrationCategory.values()) {
            totalCounts.put(category, 0);
            migratedCounts.put(category, 0);
        }
    }

    /**
     * 设置指定类别的物品数量和已移植数量
     *
     * @param category 物品类别
     * @param total    该类物品总数
     * @param migrated 该类已移植物品数
     */
    public void setCategoryCounts(MigrationCategory category, int total, int migrated) {
        totalCounts.put(category, Math.max(0, total));
        migratedCounts.put(category, Math.max(0, Math.min(migrated, total)));
    }

    /**
     * 添加警告信息
     *
     * @param warning 警告文本
     */
    public void addWarning(String warning) {
        warnings.add(warning);
    }

    /**
     * 添加错误信息
     *
     * @param error 错误文本
     */
    public void addError(String error) {
        errors.add(error);
    }

    /**
     * 获取指定类别的总数
     *
     * @param category 物品类别
     * @return 该类物品总数
     */
    public int getTotalCount(MigrationCategory category) {
        return totalCounts.getOrDefault(category, 0);
    }

    /**
     * 获取指定类别的已移植数
     *
     * @param category 物品类别
     * @return 该类已移植物品数
     */
    public int getMigratedCount(MigrationCategory category) {
        return migratedCounts.getOrDefault(category, 0);
    }

    /**
     * 计算总完成百分比
     * <p>
     * 取所有类别已移植数之和与总数之和的比值。
     * 如果总数为 0，返回 100%。
     *
     * @return 完成百分比（0.0 ~ 100.0）
     */
    public double getCompletionPercentage() {
        int total = totalCounts.values().stream().mapToInt(Integer::intValue).sum();
        if (total == 0) {
            return 100.0;
        }
        int migrated = migratedCounts.values().stream().mapToInt(Integer::intValue).sum();
        return (double) migrated / total * 100.0;
    }

    /**
     * 获取指定类别的完成百分比
     *
     * @param category 物品类别
     * @return 该类完成百分比（0.0 ~ 100.0）
     */
    public double getCategoryPercentage(MigrationCategory category) {
        int total = totalCounts.getOrDefault(category, 0);
        if (total == 0) {
            return 100.0;
        }
        int migrated = migratedCounts.getOrDefault(category, 0);
        return (double) migrated / total * 100.0;
    }

    /**
     * 生成 Markdown 格式的迁移报告
     * <p>
     * 包含表头、各类别统计表格、总进度条以及警告/错误列表。
     *
     * @return Markdown 格式的完整报告字符串
     */
    public String toMarkdown() {
        StringBuilder sb = new StringBuilder();

        sb.append("# 物品移植报告\n\n");
        sb.append("**模组**: `").append(modId).append("`\n\n");
        sb.append("**生成时间**: ").append(new Date()).append("\n\n");

        double overall = getCompletionPercentage();
        sb.append("## 总进度\n\n");
        sb.append(String.format("**%.2f%%** 完成\n\n", overall));

        int barWidth = 30;
        int filledBars = (int) Math.round(overall / 100.0 * barWidth);
        sb.append("[");
        sb.append("█".repeat(Math.max(0, filledBars)));
        sb.append("░".repeat(Math.max(0, barWidth - filledBars)));
        sb.append("]\n\n");

        sb.append("## 各类别统计\n\n");
        sb.append("| 类别 | 总数 | 已移植 | 待移植 | 完成率 |\n");
        sb.append("|------|------|--------|--------|--------|\n");

        for (MigrationCategory category : MigrationCategory.values()) {
            int total = totalCounts.getOrDefault(category, 0);
            int migrated = migratedCounts.getOrDefault(category, 0);
            int pending = total - migrated;
            double pct = getCategoryPercentage(category);
            sb.append(String.format("| %s | %d | %d | %d | %.2f%% |\n",
                    categoryName(category), total, migrated, pending, pct));
        }

        int grandTotal = totalCounts.values().stream().mapToInt(Integer::intValue).sum();
        int grandMigrated = migratedCounts.values().stream().mapToInt(Integer::intValue).sum();
        sb.append(String.format("| **合计** | **%d** | **%d** | **%d** | **%.2f%%** |\n",
                grandTotal, grandMigrated, grandTotal - grandMigrated, overall));

        if (!warnings.isEmpty()) {
            sb.append("\n## 警告\n\n");
            for (int i = 0; i < warnings.size(); i++) {
                sb.append(i + 1).append(". ").append(warnings.get(i)).append("\n");
            }
        }

        if (!errors.isEmpty()) {
            sb.append("\n## 错误\n\n");
            for (int i = 0; i < errors.size(); i++) {
                sb.append(i + 1).append(". ❌ ").append(errors.get(i)).append("\n");
            }
        }

        return sb.toString();
    }

    /**
     * 生成控制台格式的摘要报告
     * <p>
     * 以纯文本表格形式输出到控制台，适合在日志或终端中查看。
     *
     * @return 控制台格式的摘要字符串
     */
    public String toConsoleSummary() {
        StringBuilder sb = new StringBuilder();

        sb.append("\n").append(SEPARATOR).append("\n");
        sb.append("  物品移植报告 [").append(modId).append("]\n");
        sb.append(SEPARATOR).append("\n\n");

        double overall = getCompletionPercentage();
        sb.append(String.format("  总进度: %.2f%%\n\n", overall));

        sb.append(String.format("  %-15s %8s %8s %8s %8s\n", "类别", "总数", "已移植", "待移植", "完成率"));
        sb.append("  ").append("-".repeat(55)).append("\n");

        for (MigrationCategory category : MigrationCategory.values()) {
            int total = totalCounts.getOrDefault(category, 0);
            int migrated = migratedCounts.getOrDefault(category, 0);
            int pending = total - migrated;
            double pct = getCategoryPercentage(category);
            sb.append(String.format("  %-15s %8d %8d %8d %7.2f%%\n",
                    categoryName(category), total, migrated, pending, pct));
        }

        int grandTotal = totalCounts.values().stream().mapToInt(Integer::intValue).sum();
        int grandMigrated = migratedCounts.values().stream().mapToInt(Integer::intValue).sum();
        sb.append("  ").append("-".repeat(55)).append("\n");
        sb.append(String.format("  %-15s %8d %8d %8d %7.2f%%\n",
                "合计", grandTotal, grandMigrated, grandTotal - grandMigrated, overall));

        if (!warnings.isEmpty()) {
            sb.append("\n  ⚠ 警告:\n");
            for (String warning : warnings) {
                sb.append("    - ").append(warning).append("\n");
            }
        }

        if (!errors.isEmpty()) {
            sb.append("\n  ✖ 错误:\n");
            for (String error : errors) {
                sb.append("    - ").append(error).append("\n");
            }
        }

        sb.append("\n").append(SEPARATOR).append("\n");

        return sb.toString();
    }

    /**
     * 将枚举类别转换为可读的类别名称
     *
     * @param category 迁移类别枚举
     * @return 中文类别名称
     */
    private static String categoryName(MigrationCategory category) {
        return switch (category) {
            case MATERIAL -> "材料";
            case FOOD -> "食物";
            case TOOL -> "工具";
            case WEAPON -> "武器";
            case ARMOR -> "护甲";
            case CURIO -> "饰品";
            case BLOCK_ITEM -> "方块物品";
            case MUSIC_DISC -> "音乐唱片";
            case SPAWN_EGG -> "刷怪蛋";
            case RECORD -> "记录";
            case MISC -> "杂项";
        };
    }
}
