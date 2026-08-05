package com.pasterdream.pasterdreammod.api.client.block;

import com.pasterdream.pasterdreammod.api.block.BlockAPI;
import com.pasterdream.pasterdreammod.api.block.BlockConfig;
import com.pasterdream.pasterdreammod.api.block.BlockConfig.TintType;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

import java.util.Map;

/**
 * 方块染色客户端门面 —— 根据 {@link BlockAPI} 配置的 {@link BlockConfig#getTint()} 自动注册颜色提供者。
 * <p>
 * 供主模在客户端事件（{@code RegisterColorHandlersEvent.Block/Item}）中调用，将
 * BlockConfig 声明式配置的染色能力落地为实际渲染：
 * <ul>
 *   <li>{@link TintType#FOLIAGE}：走 {@link BiomeColors#getAverageFoliageColor}（原版树叶机制，兼容 Sodium/Iris）</li>
 *   <li>{@link TintType#GRASS}：走 {@link BiomeColors#getAverageGrassColor}</li>
 *   <li>{@link TintType#FIXED}：固定颜色（须含 alpha 位，否则渲染透明）</li>
 * </ul>
 * <p>
 * 使用示例（主模 ClientSetup）：
 * <pre>{@code
 * // PDBlocks 中声明
 * BlockAPI.putConfig("dyedream_leaves", BlockConfig.of().tintFoliage());
 *
 * // ClientSetup 中注册
 * BlockTintClient.registerBlockTints(event);   // RegisterColorHandlersEvent.Block
 * BlockTintClient.registerItemTints(event);    // RegisterColorHandlersEvent.Item
 * }</pre>
 */
public final class BlockTintClient {

    private BlockTintClient() {
        throw new UnsupportedOperationException("BlockTintClient 是纯静态门面类，不可实例化");
    }

    /**
     * 注册所有配置了染色的方块的 BlockColor 提供者。
     *
     * @param event 方块颜色处理器注册事件
     */
    public static void registerBlockTints(RegisterColorHandlersEvent.Block event) {
        for (Map.Entry<String, BlockConfig> entry : BlockAPI.getBlockConfigs().entrySet()) {
            BlockConfig config = entry.getValue();
            Block block = BlockAPI.getBlock(entry.getKey()).orElse(null);
            if (block == null || config.getTint() == TintType.NONE) {
                continue;
            }
            event.register(blockColorProvider(config), block);
        }
    }

    /**
     * 注册所有配置了染色的方块的 ItemColor 提供者（物品/手持/掉落物显示）。
     * <p>仅对 {@link TintType#FIXED} 配置固定色；FOLIAGE/GRASS 类物品无世界位置，
     * 固定使用配置的固定色（缺省基础粉紫）保证可见。
     *
     * @param event 物品颜色处理器注册事件
     */
    public static void registerItemTints(RegisterColorHandlersEvent.Item event) {
        for (Map.Entry<String, BlockConfig> entry : BlockAPI.getBlockConfigs().entrySet()) {
            BlockConfig config = entry.getValue();
            Block block = BlockAPI.getBlock(entry.getKey()).orElse(null);
            if (block == null || config.getTint() == TintType.NONE) {
                continue;
            }
            // BlockItem 是染色方块的物品形态
            if (block.asItem() instanceof BlockItem blockItem) {
                event.register(itemColorProvider(config), blockItem);
            }
        }
    }

    /**
     * 根据配置构建 BlockColor 提供者。
     *
     * @param config 方块配置
     * @return BlockColor 实例
     */
    private static BlockColor blockColorProvider(BlockConfig config) {
        return (state, level, pos, tintIndex) -> {
            if (level == null || pos == null) {
                return config.getFixedTint();
            }
            return switch (config.getTint()) {
                case FOLIAGE -> BiomeColors.getAverageFoliageColor(level, pos);
                case GRASS -> BiomeColors.getAverageGrassColor(level, pos);
                case FIXED -> config.getFixedTint();
                default -> config.getFixedTint();
            };
        };
    }

    /**
     * 根据配置构建 ItemColor 提供者（物品无世界位置，固定色）。
     *
     * @param config 方块配置
     * @return ItemColor 实例
     */
    private static ItemColor itemColorProvider(BlockConfig config) {
        return (stack, tintIndex) -> tintIndex == 0 ? config.getFixedTint() : -1;
    }
}
