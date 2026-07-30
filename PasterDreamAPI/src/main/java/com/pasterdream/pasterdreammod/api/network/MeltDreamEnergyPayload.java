package com.pasterdream.pasterdreammod.api.network;

import com.pasterdream.pasterdreammod.api.PasterDreamAPI;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * 融梦能量同步网络包（S2C）。
 * <p>
 * 位于 PasterDreamAPI，供主模组及融梦能量附属模组共享，用于把服务器端融梦能量同步到客户端。
 *
 * @param meltDreamEnergy 融梦能量值
 * @param noNeedConsume   免消耗计数
 */
public record MeltDreamEnergyPayload(double meltDreamEnergy, int noNeedConsume) implements CustomPacketPayload {

    /** 包类型 ID */
    public static final CustomPacketPayload.Type<MeltDreamEnergyPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(PasterDreamAPI.DATA_NAMESPACE, "meltdream_energy"));

    /** 网络编解码器 */
    public static final StreamCodec<ByteBuf, MeltDreamEnergyPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.DOUBLE, MeltDreamEnergyPayload::meltDreamEnergy,
            ByteBufCodecs.VAR_INT, MeltDreamEnergyPayload::noNeedConsume,
            MeltDreamEnergyPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
