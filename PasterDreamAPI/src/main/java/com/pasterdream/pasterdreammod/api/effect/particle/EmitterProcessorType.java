package com.pasterdream.pasterdreammod.api.effect.particle;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

/**
 * 粒子发射器处理器类型 —— 提供处理器的网络编解码与唯一标识
 * <p>
 * 借鉴开源模组 FDLib（作者 FINDERFEED）的 {@code EmitterProcessorType} 设计思路
 * （独立实现，非复制）。每个处理器实现（如圆形生成、绑定实体）内部持有一个
 * {@code Type}，负责：
 * <ul>
 *   <li>自身的 {@link StreamCodec}（把处理器参数序列化进网络包）；</li>
 *   <li>唯一 {@code id()}（注册进 {@link EmitterProcessorRegistry} 供反查）。</li>
 * </ul>
 *
 * @param <T> 处理器类型
 * @see EmitterProcessor
 * @see EmitterProcessorRegistry
 */
public interface EmitterProcessorType<T extends EmitterProcessor<T>> {

    /**
     * 获取处理器的网络编解码器
     *
     * @return 处理器参数的 StreamCodec
     */
    StreamCodec<FriendlyByteBuf, T> codec();

    /**
     * 获取处理器类型的唯一标识（注册表 key）
     *
     * @return 资源位置
     */
    ResourceLocation id();
}
