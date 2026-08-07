package com.pasterdream.pasterdreammod.api.network;

import com.pasterdream.pasterdreammod.api.PasterDreamAPI;
import com.pasterdream.pasterdreammod.api.effect.particle.ParticleEmitterData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * 粒子发射器网络包（S2C）—— 服务端下发一次粒子发射事件到客户端
 * <p>
 * 数据为 {@link ParticleEmitterData}（含处理器与粒子类型池），客户端收到后
 * 由 {@code PDClientVfx} 落地到 {@code ParticleEmitterHandler}。
 * 粒子类型需经注册表解析，故使用 {@link RegistryFriendlyByteBuf} 编解码。
 */
public record ParticleEmitterPayload(ParticleEmitterData data) implements CustomPacketPayload {

    /** 包类型 ID */
    public static final CustomPacketPayload.Type<ParticleEmitterPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(
                    PasterDreamAPI.DATA_NAMESPACE, "particle_emitter"));

    /** 网络编解码器 */
    public static final StreamCodec<RegistryFriendlyByteBuf, ParticleEmitterPayload> STREAM_CODEC =
            StreamCodec.of(
                    (buf, payload) -> ParticleEmitterData.STREAM_CODEC.encode(buf, payload.data()),
                    (buf) -> new ParticleEmitterPayload(ParticleEmitterData.STREAM_CODEC.decode(buf))
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
