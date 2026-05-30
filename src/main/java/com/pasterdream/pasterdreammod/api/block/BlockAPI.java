package com.pasterdream.pasterdreammod.api.block;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.api.block.builder.BatchBlockBuilder;
import com.pasterdream.pasterdreammod.api.block.builder.SimpleBlockBuilder;
import com.pasterdream.pasterdreammod.api.block.builder.VariantSetBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 方块注册 API —— 将繁琐的方块注册集中管理，提供高效简洁的注册方式
 * <p>
 * 采用与 {@link com.pasterdream.pasterdreammod.api.itemmigration.ItemMigrationAPI} 相似的
 * Facade 模式 + Builder 模式设计，覆盖三类常见方块注册场景：
 * <ul>
 *   <li><b>模式一（SimpleBlockBuilder）</b>：批量注册「换皮」基础方块，告别逐行 copy</li>
 *   <li><b>模式二（VariantSetBuilder）</b>：一键生成建筑变体全家桶（楼梯、台阶、墙……）</li>
 *   <li><b>模式三（BatchBlockBuilder）</b>：按编号批量注册同类型方块（花、草、矿石）</li>
 * </ul>
 * <p>
 * 使用示例：
 * <pre>{@code
 * // 模式一：基础换皮方块批量注册
 * BlockAPI.registerSimpleBlocks()
 *     .add("dyedream_block", Blocks.STONE)
 *     .add("dyedream_dirt", Blocks.DIRT)
 *     .build();
 *
 * // 模式二：建筑变体族
 * BlockAPI.createVariantSet("dyedream_planks", Blocks.OAK_PLANKS)
 *     .withStairs()
 *     .withSlab()
 *     .withFence()
 *     .build();
 *
 * // 模式三：批量花/草
 * BlockAPI.batchRegister("flower")
 *     .indexList(1, 2, 3, 5, 6, 8, 9, 13, 14, 15, 16, 17)
 *     .factory(index -> new DyedreamFlowerBlock(MobEffects.HUNGER, 100, flowerProps()))
 *     .build();
 * }</pre>
 */
public final class BlockAPI {

    /**
     * API 专属的方块注册器。
     * 注意：需要在 {@code PasterDreamMod} 构造函数中注册到事件总线：
     * <pre>{@code
     * BlockAPI.REGISTRY.register(modEventBus);
     * }</pre>
     */
    public static final DeferredRegister.Blocks REGISTRY =
            DeferredRegister.createBlocks(PasterDreamMod.MOD_ID);

    /** 方块配置存储表 —— 方块注册名 → 配置，供数据生成器读取 */
    static final Map<String, BlockConfig> BLOCK_CONFIGS = new HashMap<>();

    /**
     * 已注册方块的 Supplier 表 —— 方块注册名 → Block Supplier
     * <p>
     * 供数据生成器安全获取 Block 引用，避免在 gatherData 阶段依赖 BuiltInRegistries 查找。
     * 由各 Builder 的 build() 方法在注册时负责存入。
     */
    static final Map<String, Supplier<? extends Block>> BLOCK_SUPPLIERS = new HashMap<>();

    /**
     * 获取所有方块配置的不可变视图
     *
     * @return 方块注册名到配置的映射
     */
    public static Map<String, BlockConfig> getBlockConfigs() {
        return Collections.unmodifiableMap(BLOCK_CONFIGS);
    }

    /**
     * 存储方块配置
     *
     * @param name   方块注册名
     * @param config 方块配置
     */
    public static void putConfig(String name, BlockConfig config) {
        if (config != null) {
            BLOCK_CONFIGS.put(name, config);
        }
    }

    /**
     * 存储已注册方块的 Supplier，供数据生成器使用
     *
     * @param name   方块注册名
     * @param block  DeferredBlock 或任意 Block Supplier
     */
    public static void putBlock(String name, Supplier<? extends Block> block) {
        if (block != null) {
            BLOCK_SUPPLIERS.put(name, block);
        }
    }

    /**
     * 根据注册名获取 Block 实例，数据生成器中安全使用
     *
     * @param name 方块注册名
     * @return Block 实例，若未注册返回 null
     */
    public static Block getBlock(String name) {
        Supplier<? extends Block> supplier = BLOCK_SUPPLIERS.get(name);
        return supplier != null ? supplier.get() : null;
    }

    // ======================== Builder 工厂方法 ========================

    /**
     * 创建一个批量基础方块构建器（模式一）。
     * <p>
     * 适用于注册多个「换皮」方块，只需指定名称和参考的原版方块即可。
     *
     * @return {@link SimpleBlockBuilder} 实例
     */
    public static SimpleBlockBuilder registerSimpleBlocks() {
        return new SimpleBlockBuilder(REGISTRY);
    }

    /**
     * 创建一个建筑变体族构建器（模式二）。
     * <p>
     * 基于一个基础方块，一键生成楼梯、台阶、墙、栅栏等全套建筑变体。
     *
     * @param baseName  基础方块名称前缀（如 "dyedream_planks"）
     * @param baseBlock 基础方块引用（可为 {@code DeferredBlock}）
     * @return {@link VariantSetBuilder} 实例
     */
    public static VariantSetBuilder createVariantSet(
            String baseName, Supplier<? extends Block> baseBlock) {
        return new VariantSetBuilder(REGISTRY, baseName, baseBlock);
    }

    /**
     * 创建一个批量命名变种构建器（模式三）。
     * <p>
     * 适用于注册大量名称仅编号不同的同类方块（如 flower_1 ~ flower_17）。
     *
     * @param baseName 方块名称前缀（如 "flower"）
     * @return {@link BatchBlockBuilder} 实例
     */
    public static BatchBlockBuilder batchRegister(String baseName) {
        return new BatchBlockBuilder(REGISTRY, baseName);
    }

    private BlockAPI() {
        throw new UnsupportedOperationException("BlockAPI 是纯静态门面类，不可实例化");
    }
}