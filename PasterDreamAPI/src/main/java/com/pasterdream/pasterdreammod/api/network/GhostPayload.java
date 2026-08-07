package com.pasterdream.pasterdreammod.api.network;

import com.pasterdream.pasterdreammod.api.PasterDreamAPI;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * 残影特效网络包（S2C）—— 服务端通知客户端对指定实体开启残影拖尾
 * <p>
 * 客户端收到后每 tick 采样实体位置生成半透明残影副本，渲染出"虚影拖尾"。
 *
 * @param entityId 目标实体网络 id（服务端 {@code entity.getId()}）
 * @param duration 残影持续 tick 数
 * @param alpha    残影初始透明度（0-255，越小越淡）
 */
public record GhostPayload(int entityId, int duration, int alpha) implements CustomPacketPayload {

    /** 包类型 ID */
    public static final CustomPacketPayload.Type<GhostPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(
                    PasterDreamAPI.DATA_NAMESPACE, "ghost"));

    /** 网络编解码器 */
    public static final StreamCodec<ByteBuf, GhostPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, GhostPayload::entityId,
            ByteBufCodecs.VAR_INT, GhostPayload::duration,
            ByteBufCodecs.VAR_INT, GhostPayload::alpha,
            GhostPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
