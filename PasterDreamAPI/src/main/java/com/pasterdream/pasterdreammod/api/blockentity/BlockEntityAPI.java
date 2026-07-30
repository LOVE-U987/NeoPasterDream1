package com.pasterdream.pasterdreammod.api.blockentity;

import com.pasterdream.pasterdreammod.api.PasterDreamAPI;
import com.pasterdream.pasterdreammod.api.blockentity.builder.BlockEntityBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import com.pasterdream.pasterdreammod.api.util.PDDebugLogger;
/**
 * 方块实体注册 API —— 集中管理 BlockEntityType 的注册与查询
 * <p>
 * 采用 Facade 模式 + Builder 模式设计，与 {@link com.pasterdream.pasterdreammod.api.block.BlockAPI} 风格一致。
 * <p>
 * 使用示例：
 * <pre>{@code
 * BlockEntityAPI.register("dream_accumulator",
 *     () -> BlockEntityType.Builder.of(DreamAccumulatorBlockEntity::new, PDBlocks.DREAM_ACCUMULATOR.get()).build(null));
 *
 * // 或使用 Builder
 * BlockEntityAPI.createBlockEntity("dream_accumulator")
 *     .factory(DreamAccumulatorBlockEntity::new)
 *     .validBlock(PDBlocks.DREAM_ACCUMULATOR)
 *     .build();
 * }</pre>
 */
public final class BlockEntityAPI {

    /**
     * API 专属的方块实体类型注册器。
     * 需要在 {@code PasterDreamMod} 构造函数中通过 {@link #registerAll(IEventBus)} 注册到事件总线。
     */
    public static final DeferredRegister<BlockEntityType<?>> REGISTRY =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, PasterDreamAPI.DATA_NAMESPACE);

    /** 已注册方块实体的 DeferredHolder 缓存 */
    private static final Map<String, DeferredHolder<BlockEntityType<?>, ? extends BlockEntityType<?>>> HOLDERS =
            new HashMap<>();

    private BlockEntityAPI() {
        throw new UnsupportedOperationException("BlockEntityAPI 是纯静态门面类，不可实例化");
    }

    /**
     * 注册一个方块实体类型。
     *
     * @param name     方块实体注册名（snake_case 格式）
     * @param supplier 方块实体类型 Supplier
     * @param <T>      方块实体类型参数
     * @return 注册后的 DeferredHolder
     */
    public static <T extends BlockEntity> DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> register(
            String name, Supplier<BlockEntityType<T>> supplier) {
        DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> holder = REGISTRY.register(name, supplier);
        HOLDERS.put(name, holder);
        PDDebugLogger.apiDebug("[BlockEntityAPI] 已注册方块实体: {}", name);
        return holder;
    }

    /**
     * 创建一个方块实体构建器。
     *
     * @param name 方块实体注册名
     * @param <T>  方块实体类型参数
     * @return {@link BlockEntityBuilder} 实例
     */
    public static <T extends BlockEntity> BlockEntityBuilder<T> createBlockEntity(String name) {
        return new BlockEntityBuilder<>(name);
    }

    /**
     * 根据注册名查询方块实体类型。
     *
     * @param name 方块实体注册名
     * @return {@link BlockEntityType} 的 Optional，未找到返回 {@link Optional#empty()}
     */
    public static Optional<BlockEntityType<?>> getBlockEntityType(String name) {
        return Optional.ofNullable(HOLDERS.get(name)).map(DeferredHolder::get);
    }

    /**
     * 根据注册名查询方块实体的 DeferredHolder。
     *
     * @param name 方块实体注册名
     * @return DeferredHolder 的 Optional，未找到返回 {@link Optional#empty()}
     */
    public static Optional<DeferredHolder<BlockEntityType<?>, ? extends BlockEntityType<?>>> getHolder(String name) {
        return Optional.ofNullable(HOLDERS.get(name));
    }

    /**
     * 获取所有已注册方块实体的不可变视图。
     *
     * @return 注册名到 DeferredHolder 的映射
     */
    public static Map<String, DeferredHolder<BlockEntityType<?>, ? extends BlockEntityType<?>>> getRegisteredBlockEntities() {
        return Collections.unmodifiableMap(HOLDERS);
    }

    /**
     * 将方块实体注册器注册到模组事件总线。
     *
     * @param modEventBus NeoForge 模组事件总线
     */
    public static void registerAll(IEventBus modEventBus) {
        REGISTRY.register(modEventBus);
        PDDebugLogger.apiDebug("[BlockEntityAPI] 已注册方块实体 DeferredRegister 到事件总线");
    }

    /**
     * 重置所有静态缓存，供测试使用。
     * <p>
     * 清空已缓存的方块实体 DeferredHolder，使每次测试都在干净的缓存状态下运行。
     * 注意：此方法不会取消 DeferredRegister 中的已注册方块实体类型，仅清除 API 层面的缓存数据。
     */
    public static void resetForTesting() {
        HOLDERS.clear();
    }
}
