package com.pasterdream.pasterdreammod.api.block.builder;

import com.pasterdream.pasterdreammod.api.block.BlockAPI;
import com.pasterdream.pasterdreammod.api.block.BlockConfig;
import com.pasterdream.pasterdreammod.block.SelfDropBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 基础方块批量构建器（模式一）—— 用于批量注册「换皮」基础方块
 * <p>
 * 解决 {@code PDBlocks.java} 中大量重复的 {@code registerSimpleBlock} + {@code ofFullCopy} 调用。
 * 只需指定方块名称和参考的原版方块，即可完成注册。
 * <p>
 * 使用示例：
 * <pre>{@code
 * Map<String, DeferredBlock<Block>> blocks = BlockAPI.registerSimpleBlocks()
 *     .add("dyedream_block", Blocks.STONE)
 *     .add("dyedream_dirt", Blocks.DIRT)
 *     .add("dyedream_sand", Blocks.SAND)
 *     .addCustom("chiseled_dyedreamquartz_block",
 *         BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).lightLevel(s -> 10))
 *     .build();
 * }</pre>
 */
public class SimpleBlockBuilder {

    private final DeferredRegister.Blocks registry;

    private final Map<String, BlockBehaviour.Properties> entries = new LinkedHashMap<>();

    private final Map<String, BlockConfig> configs = new LinkedHashMap<>();

    /**
     * 构造基础方块构建器
     *
     * @param registry 方块注册器
     */
    public SimpleBlockBuilder(DeferredRegister.Blocks registry) {
        this.registry = registry;
    }

    /**
     * 添加一个基础换皮方块，属性完全复制自参考方块
     *
     * @param name      方块注册名（snake_case 格式）
     * @param reference 参考的原版方块，用于复制属性
     * @return 当前构建器实例
     */
    public SimpleBlockBuilder add(String name, Block reference) {
        entries.put(name, BlockBehaviour.Properties.ofFullCopy(reference));
        return this;
    }

    /**
     * 添加一个带自定义属性的方块
     *
     * @param name       方块注册名
     * @param properties 方块的完整属性配置
     * @return 当前构建器实例
     */
    public SimpleBlockBuilder addCustom(String name, BlockBehaviour.Properties properties) {
        entries.put(name, properties);
        return this;
    }

    /**
     * 添加一个基础换皮方块，附带方块配置（纹理/模型/标签/交互等）
     *
     * @param name      方块注册名
     * @param reference 参考的原版方块
     * @param config    方块配置
     * @return 当前构建器实例
     */
    public SimpleBlockBuilder add(String name, Block reference, @Nullable BlockConfig config) {
        entries.put(name, BlockBehaviour.Properties.ofFullCopy(reference));
        if (config != null) {
            configs.put(name, config);
        }
        return this;
    }

    /**
     * 添加一个带自定义属性的方块，附带方块配置
     *
     * @param name       方块注册名
     * @param properties 方块的完整属性配置
     * @param config     方块配置
     * @return 当前构建器实例
     */
    public SimpleBlockBuilder addCustom(String name, BlockBehaviour.Properties properties, @Nullable BlockConfig config) {
        entries.put(name, properties);
        if (config != null) {
            configs.put(name, config);
        }
        return this;
    }

    /**
     * 执行注册，将所有已添加的方块批量注册到注册表
     * <p>
     * 所有方块使用 {@code registerSimpleBlock} 注册，自动生成对应的 BlockItem。
     *
     * @return 注册名到 {@link DeferredBlock} 的映射表，可用于后续引用
     * @throws IllegalStateException 如果未添加任何方块
     */
    public Map<String, DeferredBlock<Block>> build() {
        if (entries.isEmpty()) {
            throw new IllegalStateException("SimpleBlockBuilder: 至少需要添加一个方块");
        }
        Map<String, DeferredBlock<Block>> results = new LinkedHashMap<>();
        for (Map.Entry<String, BlockBehaviour.Properties> entry : entries.entrySet()) {
            DeferredBlock<Block> deferred = registry.registerBlock(
                    entry.getKey(), SelfDropBlock::new, entry.getValue());
            results.put(entry.getKey(), deferred);
            BlockAPI.putBlock(entry.getKey(), deferred);
            BlockConfig cfg = configs.get(entry.getKey());
            if (cfg != null) {
                BlockAPI.putConfig(entry.getKey(), cfg);
            }
        }
        return results;
    }
}