package com.pasterdream.pasterdreammod.api.effect.particle;

import net.minecraft.resources.ResourceLocation;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 粒子发射器处理器类型注册表 —— 静态表，供网络包反查
 * <p>
 * 借鉴开源模组 FDLib（作者 FINDERFEED）的 {@code FDEmitterProcessorTypes} 设计思路
 * （独立实现，非复制）。处理器类型是代码定义的工厂，非 datapack 内容，
 * 因此使用普通静态 {@link ConcurrentHashMap} 而非 NeoForge 自定义 Registry。
 * <p>
 * 内置处理器在自身静态初始化时自注册；附属模组在类加载期直接
 * {@link #register(EmitterProcessorType)} 即可。
 */
public final class EmitterProcessorRegistry {

    /** 处理器类型表：id → 类型 */
    private static final Map<ResourceLocation, EmitterProcessorType<?>> TYPES = new ConcurrentHashMap<>();

    private EmitterProcessorRegistry() {
        throw new UnsupportedOperationException("EmitterProcessorRegistry 是纯静态注册表类，不可实例化");
    }

    /**
     * 注册一个处理器类型
     *
     * @param type 处理器类型
     */
    public static void register(EmitterProcessorType<?> type) {
        TYPES.put(type.id(), type);
    }

    /**
     * 按 id 查询处理器类型
     *
     * @param id 处理器类型 id
     * @return 处理器类型或 {@code null}（未注册）
     */
    public static EmitterProcessorType<?> get(ResourceLocation id) {
        return TYPES.get(id);
    }

    /**
     * 按 id 查询处理器类型（Optional 形式）
     *
     * @param id 处理器类型 id
     * @return 包含类型的 {@link Optional}
     */
    public static Optional<EmitterProcessorType<?>> find(ResourceLocation id) {
        return Optional.ofNullable(TYPES.get(id));
    }

    /**
     * 获取全部已注册类型的只读视图
     *
     * @return 类型表不可变视图
     */
    public static Map<ResourceLocation, EmitterProcessorType<?>> getAll() {
        return Collections.unmodifiableMap(TYPES);
    }

    /**
     * 测试辅助：清空注册表
     */
    public static void resetForTesting() {
        TYPES.clear();
    }
}
