package com.pasterdream.pasterdreammod.api.client.effect.screen;

import com.pasterdream.pasterdreammod.api.effect.screen.ScreenEffectFactory;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 屏幕特效工厂注册表（客户端）—— 按特效类型 id 反查创建实例的工厂
 * <p>
 * 借鉴开源模组 FDLib（作者 FINDERFEED）的 {@code ScreenEffectType} 中工厂
 * 设计思路（独立实现，非复制）。工厂引用客户端特效实现类，因此独立于
 * 服务端安全的 {@code ScreenEffectRegistry}：服务端只发类型 id，客户端
 * 经本注册表拿工厂创建特效实例。
 * <p>
 * 本类为客户端专用（工厂返回客户端 {@link ScreenEffect}），仅由
 * {@code api/client/**} 路径持有。
 */
@OnlyIn(Dist.CLIENT)
public final class ScreenEffectFactoryRegistry {

    /** 工厂表：特效类型 id → 工厂 */
    private static final Map<ResourceLocation, ScreenEffectFactory<?>> FACTORIES = new ConcurrentHashMap<>();

    private ScreenEffectFactoryRegistry() {
        throw new UnsupportedOperationException("ScreenEffectFactoryRegistry 是纯静态注册表类，不可实例化");
    }

    /**
     * 注册一个特效工厂
     *
     * @param id      特效类型 id
     * @param factory 工厂
     */
    public static void register(ResourceLocation id, ScreenEffectFactory<?> factory) {
        FACTORIES.put(id, factory);
    }

    /**
     * 按 id 查询工厂
     *
     * @param id 特效类型 id
     * @return 工厂或 {@code null}（未注册）
     */
    public static ScreenEffectFactory<?> get(ResourceLocation id) {
        return FACTORIES.get(id);
    }

    /**
     * 测试辅助：清空注册表
     */
    public static void resetForTesting() {
        FACTORIES.clear();
    }
}
