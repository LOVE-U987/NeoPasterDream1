package com.pasterdream.pasterdreammod.api.network;

import com.pasterdream.pasterdreammod.api.PasterDreamAPI;
import com.pasterdream.pasterdreammod.api.effect.impact.ImpactFrame;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

/**
 * 打击帧网络包（S2C）—— 服务端下发一次打击帧序列到客户端
 * <p>
 * 可携带多个 {@link ImpactFrame} 排队依次播放（如多段重击连打）。
 */
public record ImpactFramesPayload(List<ImpactFrame> frames) implements CustomPacketPayload {

    /** 包类型 ID */
    public static final CustomPacketPayload.Type<ImpactFramesPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(
                    PasterDreamAPI.DATA_NAMESPACE, "impact_frames"));

    /** 网络编解码器 */
    public static final StreamCodec<FriendlyByteBuf, ImpactFramesPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.collection(ArrayList::new, ImpactFrame.STREAM_CODEC),
                    ImpactFramesPayload::frames,
                    ImpactFramesPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
