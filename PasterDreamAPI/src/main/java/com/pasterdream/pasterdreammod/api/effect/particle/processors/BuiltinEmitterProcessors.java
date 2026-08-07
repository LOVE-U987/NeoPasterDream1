package com.pasterdream.pasterdreammod.api.effect.particle.processors;

import com.pasterdream.pasterdreammod.api.effect.particle.EmitterProcessorRegistry;

/**
 * 内置粒子发射器处理器注册入口
 * <p>
 * 静态初始化时把 API 自带的所有处理器类型注册进
 * {@link EmitterProcessorRegistry}。主模/附属模组在类加载期显式引用本类
 * （或任一处理器常量）即可触发注册；网络包解码依赖该注册表反查类型。
 */
public final class BuiltinEmitterProcessors {

    private BuiltinEmitterProcessors() {
        throw new UnsupportedOperationException("BuiltinEmitterProcessors 是纯静态注册入口，不可实例化");
    }

    /** 注册全部内置处理器类型 */
    public static void registerAll() {
        EmitterProcessorRegistry.register(EmptyEmitterProcessor.TYPE);
        EmitterProcessorRegistry.register(CircleSpawnProcessor.TYPE);
        EmitterProcessorRegistry.register(BoundToEntityProcessor.TYPE);
    }
}
