package com.pasterdream.pasterdreammod.api.block.builder;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.IntStream;

/**
 * 批量命名变种构建器（模式三）—— 用于批量注册编号命名的同类方块
 * <p>
 * 解决 PDBlocks 中 flower_1 ~ flower_17、grass_1 ~ grass_14 等
 * 大量反复注册同一类方块的问题。
 * 支持连续范围、显式索引列表、自定义工厂函数三种方式。
 * <p>
 * 使用示例：
 * <pre>{@code
 * // 方式一：显式索引列表
 * Map<String, DeferredBlock<Block>> flowers = BlockAPI.batchRegister("flower")
 *     .indexList(1, 2, 3, 5, 6, 8, 9, 13, 14, 15, 16, 17)
 *     .factory((index, props) -> new DyedreamFlowerBlock(MobEffects.HUNGER, 100, props))
 *     .withProperties(flowerProps())
 *     .build();
 *
 * // 方式二：连续范围
 * Map<String, DeferredBlock<Block>> grasses = BlockAPI.batchRegister("grass")
 *     .range(1, 14)
 *     .factory((index, props) -> new DyedreamFlowerBlock(MobEffects.MOVEMENT_SLOWDOWN, 100, props))
 *     .withProperties(flowerProps())
 *     .build();
 *
 * // 方式三：排除指定索引 + 双层植物
 * Map<String, DeferredBlock<Block>> doubleFlowers = BlockAPI.batchRegister("flower")
 *     .range(1, 18)
 *     .exclude(4)
 *     .factory((index, props) -> new DyedreamDoublePlantBlock())
 *     .withProperties(doublePlantProps())
 *     .build();
 * }</pre>
 */
public class BatchBlockBuilder {

    private final DeferredRegister.Blocks registry;
    private final String baseName;
    private final List<Integer> indices = new ArrayList<>();
    private final Set<Integer> exclusions = new HashSet<>();
    @Nullable
    private BiFunction<Integer, BlockBehaviour.Properties, Block> factory;
    @Nullable
    private BlockBehaviour.Properties properties;

    /**
     * 构造批量命名变种构建器
     *
     * @param registry 方块注册器
     * @param baseName 方块名称前缀
     */
    public BatchBlockBuilder(DeferredRegister.Blocks registry, String baseName) {
        this.registry = registry;
        this.baseName = baseName;
    }

    /**
     * 设置连续编号范围（包含两端）。
     * <p>
     * 例如 {@code range(1, 5)} 会注册 {baseName}_1 到 {baseName}_5。
     *
     * @param start 起始编号（包含）
     * @param end   结束编号（包含）
     * @return 当前构建器实例
     */
    public BatchBlockBuilder range(int start, int end) {
        this.indices.clear();
        IntStream.rangeClosed(start, end).forEach(this.indices::add);
        return this;
    }

    /**
     * 设置显式的索引列表。
     * <p>
     * 适用于编号不连续的场景（如 flower_1, flower_2, flower_5, flower_8）。
     *
     * @param indices 索引列表
     * @return 当前构建器实例
     */
    public BatchBlockBuilder indexList(Integer... indices) {
        this.indices.clear();
        this.indices.addAll(Arrays.asList(indices));
        return this;
    }

    /**
     * 排除指定编号。
     * <p>
     * 与 {@link #range(int, int)} 配合使用，跳过不存在的编号。
     *
     * @param excludeIndices 要排除的编号
     * @return 当前构建器实例
     */
    public BatchBlockBuilder exclude(Integer... excludeIndices) {
        this.exclusions.addAll(Arrays.asList(excludeIndices));
        return this;
    }

    /**
     * 设置方块工厂函数。
     * <p>
     * 工厂函数接收 {@code index} 和 {@code properties} 两个参数，
     * 返回对应的 {@link Block} 实例。
     * 该函数会在注册阶段才被调用，确保延迟创建。
     *
     * @param blockFactory 方块工厂 {@code (int index, BlockBehaviour.Properties props) -> Block}
     * @return 当前构建器实例
     */
    public BatchBlockBuilder factory(BiFunction<Integer, BlockBehaviour.Properties, Block> blockFactory) {
        this.factory = blockFactory;
        return this;
    }

    /**
     * 设置所有方块共享的属性配置。
     * <p>
     * 如果未设置，默认使用 {@code BlockBehaviour.Properties.of()}。
     *
     * @param props 方块属性配置
     * @return 当前构建器实例
     */
    public BatchBlockBuilder withProperties(BlockBehaviour.Properties props) {
        this.properties = props;
        return this;
    }

    /**
     * 执行注册，批量注册所有方块
     * <p>
     * 使用 {@link DeferredRegister.Blocks#registerBlock} 注册，
     * 确保方块和 BlockItem 都被正确创建。
     *
     * @return 注册名到 {@link DeferredBlock} 的不可变映射表
     * @throws IllegalStateException 如果未设置工厂函数或未指定任何索引
     */
    public Map<String, DeferredBlock<Block>> build() {
        if (factory == null) {
            throw new IllegalStateException("BatchBlockBuilder: 必须通过 .factory() 设置方块工厂函数");
        }
        BlockBehaviour.Properties props = properties != null
                ? properties : BlockBehaviour.Properties.of();

        if (indices.isEmpty()) {
            throw new IllegalStateException(
                    "BatchBlockBuilder: 请先调用 .range() 或 .indexList() 设置编号");
        }

        Map<String, DeferredBlock<Block>> results = new LinkedHashMap<>();
        for (int index : indices) {
            if (exclusions.contains(index)) {
                continue;
            }
            String fullName = baseName + "_" + index;
            int capturedIndex = index;
            results.put(fullName,
                    registry.registerBlock(fullName, p -> factory.apply(capturedIndex, p), props));
        }

        return Collections.unmodifiableMap(results);
    }
}