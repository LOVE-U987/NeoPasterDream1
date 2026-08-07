package com.pasterdream.pasterdreammod.api.effect.screen;

import net.minecraft.resources.ResourceLocation;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 屏幕特效类型注册表 —— 静态表，供网络包反查
 * <p>
 * 借鉴开源模组 FDLib（作者 FINDERFEED）的 {@code FDScreenEffects} 设计思路
 * （独立实现，非复制）。特效类型是代码定义的工厂，非 datapack 内容，
 * 使用普通静态 {@link ConcurrentHashMap} 而非 NeoForge 自定义 Registry。
 * <p>
 * 本表只存<b>服务端安全</b>的类型元数据（id + 数据编解码），不含客户端工厂；
 * 客户端工厂经 {@code ScreenEffectFactoryRegistry} 按 id 反查。内置类型在
 * 静态初始化时自注册，附属模组在类加载期直接 {@link #register(ScreenEffectType)}。
 */
public final class ScreenEffectRegistry {

    /** 特效类型表：id → 类型 */
    private static final Map<ResourceLocation, ScreenEffectType<?>> TYPES = new ConcurrentHashMap<>();

    static {
        // 内置特效类型注册（服务端安全，仅元数据）
        register(com.pasterdream.pasterdreammod.api.effect.screen.instances.ScreenColorData.TYPE);
    }

    private ScreenEffectRegistry() {
        throw new UnsupportedOperationException("ScreenEffectRegistry 是纯静态注册表类，不可实例化");
    }

    /**
     * 注册一个屏幕特效类型
     *
     * @param type 特效类型
     */
    public static void register(ScreenEffectType<?> type) {
        TYPES.put(type.id(), type);
    }

    /**
     * 按 id 查询特效类型
     *
     * @param id 特效类型 id
     * @return 特效类型或 {@code null}（未注册）
     */
    public static ScreenEffectType<?> get(ResourceLocation id) {
        return TYPES.get(id);
    }

    /**
     * 按 id 查询特效类型（Optional 形式）
     *
     * @param id 特效类型 id
     * @return 包含类型的 {@link Optional}
     */
    public static Optional<ScreenEffectType<?>> find(ResourceLocation id) {
        return Optional.ofNullable(TYPES.get(id));
    }

    /**
     * 获取全部已注册类型的只读视图
     *
     * @return 类型表不可变视图
     */
    public static Map<ResourceLocation, ScreenEffectType<?>> getAll() {
        return Collections.unmodifiableMap(TYPES);
    }

    /**
     * 测试辅助：清空注册表
     */
    public static void resetForTesting() {
        TYPES.clear();
    }
}
