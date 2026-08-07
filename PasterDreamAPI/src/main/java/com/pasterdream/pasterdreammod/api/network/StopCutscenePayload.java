package com.pasterdream.pasterdreammod.api.network;

import com.pasterdream.pasterdreammod.api.PasterDreamAPI;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * 过场动画停止网络包（S2C）—— 服务端通知客户端强制结束当前过场
 * <p>
 * 空载荷，用 {@link StreamCodec#unit(Object)} 表示无数据。
 */
public record StopCutscenePayload() implements CustomPacketPayload {

    /** 包类型 ID */
    public static final CustomPacketPayload.Type<StopCutscenePayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(
                    PasterDreamAPI.DATA_NAMESPACE, "stop_cutscene"));

    /** 网络编解码器 */
    public static final StreamCodec<ByteBuf, StopCutscenePayload> STREAM_CODEC =
            StreamCodec.unit(new StopCutscenePayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
