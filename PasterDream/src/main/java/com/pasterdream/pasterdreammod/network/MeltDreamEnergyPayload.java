package com.pasterdream.pasterdreammod.network;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * 融梦能量数据 S2C 同步包
 * <p>
 * 移植自原版 {@code communication/MeltDreamEnergyDataMessage.java}（SimpleChannel + NBT 全量），
 * 此处直接携带两个字段做全量同步。客户端处理逻辑见
 * {@link PDNetwork#handleMeltDreamEnergyOnClient}。
 *
 * @param meltDreamEnergy 融梦能量值
 * @param noNeedConsume   免消耗计数器
 */
public record MeltDreamEnergyPayload(double meltDreamEnergy, int noNeedConsume) implements CustomPacketPayload {

    /** 包类型标识 */
    public static final Type<MeltDreamEnergyPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "melt_dream_energy_data"));

    /** 网络编解码器 */
    public static final StreamCodec<ByteBuf, MeltDreamEnergyPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.DOUBLE, MeltDreamEnergyPayload::meltDreamEnergy,
            ByteBufCodecs.VAR_INT, MeltDreamEnergyPayload::noNeedConsume,
            MeltDreamEnergyPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
