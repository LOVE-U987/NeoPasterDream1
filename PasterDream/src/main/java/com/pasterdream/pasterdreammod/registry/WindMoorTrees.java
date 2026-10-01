package com.pasterdream.pasterdreammod.registry;

import com.pasterdream.pasterdreammod.api.worldgen.decor.DecorationBuilder;
import com.pasterdream.pasterdreammod.api.worldgen.decor.DecorationRegistry;
import com.pasterdream.pasterdreammod.api.worldgen.decor.DecorationType;
import com.pasterdream.pasterdreammod.worldgen.feature.WindMoorTreeGenerator;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;

import java.util.ArrayList;
import java.util.List;

/**
 * 风之树装饰物注册 —— 18 种参数组合（大中小 × 低中高 × 多/少枝节）
 * <p>
 * 每种组合注册一个 {@link WindMoorTreeGenerator} 实例与对应的 CUSTOM 装饰物，
 * 命名规则 {@code windmoor_tree_<尺寸>_<高度>_<枝节>}，例如
 * {@code windmoor_tree_large_high_bushy}。
 * <p>
 * 由 {@link ModDecorations#register()} 统一调用；调试水晶按 {@link #variantNames()}
 * 的同一顺序暴露变体，两者顺序必须保持一致。
 *
 * @author PasterDream Team
 */
public final class WindMoorTrees {

    /** 目标群系标签 —— 风之旅途（见 data/pasterdream/tags/worldgen/biome/is_wind_journey.json） */
    private static final String BIOME_TAG = "#pasterdream:is_wind_journey";

    /** 垂吊植被概率 */
    private static final float HANGING_CHANCE = 0.45f;

    /** 尺寸档：{名称, 主干高度下限, 上限, 树冠半径} */
    private static final String[][] SIZES = {
            {"small", "5", "7", "2"},
            {"medium", "8", "11", "3"},
            {"large", "13", "17", "4"}
    };

    /** 高度档：{名称, 高度系数} */
    private static final String[][] HEIGHTS = {
            {"low", "0.75"},
            {"mid", "1.0"},
            {"high", "1.3"}
    };

    /** 枝节档：{名称, 枝节层数} */
    private static final String[][] BRANCHES = {
            {"sparse", "3"},
            {"bushy", "7"}
    };

    private WindMoorTrees() {
    }

    /**
     * 获取全部变体名（顺序与 {@link #register()} 的注册顺序一致）
     *
     * @return 变体名列表（18 项，只读语义）
     */
    public static List<String> variantNames() {
        List<String> names = new ArrayList<>();
        for (String[] size : SIZES) {
            for (String[] height : HEIGHTS) {
                for (String[] branch : BRANCHES) {
                    names.add(key(size[0], height[0], branch[0]));
                }
            }
        }
        return names;
    }

    /**
     * 注册全部 18 种风之树变体（生成器 + CUSTOM 装饰物）
     */
    public static void register() {
        for (String[] size : SIZES) {
            int baseMin = Integer.parseInt(size[1]);
            int baseMax = Integer.parseInt(size[2]);
            int canopyRadius = Integer.parseInt(size[3]);

            for (String[] height : HEIGHTS) {
                double factor = Double.parseDouble(height[1]);
                int minHeight = Math.max(3, (int) Math.round(baseMin * factor));
                int maxHeight = Math.max(minHeight + 1, (int) Math.round(baseMax * factor));

                for (String[] branch : BRANCHES) {
                    int layers = Integer.parseInt(branch[1]);
                    String name = key(size[0], height[0], branch[0]);

                    DecorationRegistry.registerCustomGenerator(name,
                            new WindMoorTreeGenerator(minHeight, maxHeight, layers, canopyRadius, HANGING_CHANCE));

                    DecorationBuilder.create()
                            .type(DecorationType.CUSTOM)
                            .body(PDBlocks.WINDMOOR_LOG.get())
                            .top(PDBlocks.WINDMOOR_LEAVES.get())
                            .customGenerator(name)
                            .checkHang(false)
                            .replaceable(BlockPredicate.anyOf(
                                    BlockPredicate.matchesBlocks(Blocks.AIR, Blocks.CAVE_AIR,
                                            PDBlocks.WINDMOOR_LEAVES.get()),
                                    BlockPredicate.matchesTag(BlockTags.REPLACEABLE)))
                            .biome(BIOME_TAG)
                            .rarity(1)
                            .step(GenerationStep.Decoration.VEGETAL_DECORATION)
                            .register(name);
                }
            }
        }
    }

    /**
     * 生成变体注册名
     *
     * @param size   尺寸档名（small/medium/large）
     * @param height 高度档名（low/mid/high）
     * @param branch 枝节档名（sparse/bushy）
     * @return 注册名
     */
    private static String key(String size, String height, String branch) {
        return "windmoor_tree_" + size + "_" + height + "_" + branch;
    }
}
