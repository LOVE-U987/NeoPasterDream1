package com.pasterdream.pasterdreammod.api.effect.screen;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

/**
 * 屏幕特效类型 —— 描述一种屏幕特效的编解码（不含客户端工厂）
 * <p>
 * 借鉴开源模组 FDLib（作者 FINDERFEED）的 {@code ScreenEffectType} 设计思路
 * （独立实现，非复制）。本类只持有<b>服务端安全</b>的元数据：
 * <ul>
 *   <li>唯一 {@code id()}（注册进 {@link ScreenEffectRegistry}）；</li>
 *   <li>数据编解码器 {@code dataCodec()}（把特效数据序列化进网络包）。</li>
 * </ul>
 * <b>不持有客户端工厂</b>——创建特效实例的工厂经客户端
 * {@code ScreenEffectFactoryRegistry} 按 id 反查，避免服务端加载客户端类。
 *
 * @param <T> 特效数据实现
 * @see ScreenEffectRegistry
 */
public final class ScreenEffectType<T extends ScreenEffectData> {

    private final ResourceLocation id;
    private final StreamCodec<ByteBuf, T> dataCodec;

    /**
     * 构造屏幕特效类型
     *
     * @param id        特效类型唯一标识
     * @param dataCodec 数据网络编解码器
     */
    public ScreenEffectType(ResourceLocation id, StreamCodec<ByteBuf, T> dataCodec) {
        this.id = id;
        this.dataCodec = dataCodec;
    }

    /**
     * 获取类型唯一标识
     *
     * @return 资源位置
     */
    public ResourceLocation id() {
        return id;
    }

    /**
     * 获取数据网络编解码器
     *
     * @return StreamCodec
     */
    public StreamCodec<ByteBuf, T> dataCodec() {
        return dataCodec;
    }
}
