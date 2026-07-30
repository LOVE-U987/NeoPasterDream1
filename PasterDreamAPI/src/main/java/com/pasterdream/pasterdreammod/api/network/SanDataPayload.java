package com.pasterdream.pasterdreammod.api.network;

import com.pasterdream.pasterdreammod.api.PasterDreamAPI;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * San 值同步网络包（S2C）。
 * <p>
 * 位于 PasterDreamAPI，供主模组及 San 附属模组共享，用于把服务器端 San 值同步到客户端。
 *
 * @param kind     同步类型：0 仅数值，1 仅开关，2 全量
 * @param sanValue 当前 San 值
 * @param sanCheck 是否启用 San 检查
 */
public record SanDataPayload(int kind, double sanValue, boolean sanCheck) implements CustomPacketPayload {

    /** 仅同步 San 数值 */
    public static final int KIND_VALUE_ONLY = 0;
    /** 仅同步 San 检查开关 */
    public static final int KIND_CHECK_ONLY = 1;
    /** 全量同步 */
    public static final int KIND_FULL = 2;

    /** 包类型 ID */
    public static final CustomPacketPayload.Type<SanDataPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(PasterDreamAPI.DATA_NAMESPACE, "san_data"));

    /** 网络编解码器 */
    public static final StreamCodec<ByteBuf, SanDataPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SanDataPayload::kind,
            ByteBufCodecs.DOUBLE, SanDataPayload::sanValue,
            ByteBufCodecs.BOOL, SanDataPayload::sanCheck,
            SanDataPayload::new
    );

    /**
     * 构造仅同步数值的包。
     *
     * @param sanValue San 值
     * @return 同步包
     */
    public static SanDataPayload valueOnly(double sanValue) {
        return new SanDataPayload(KIND_VALUE_ONLY, sanValue, false);
    }

    /**
     * 构造仅同步开关的包。
     *
     * @param sanCheck 开关值
     * @return 同步包
     */
    public static SanDataPayload checkOnly(boolean sanCheck) {
        return new SanDataPayload(KIND_CHECK_ONLY, 0.0D, sanCheck);
    }

    /**
     * 构造全量同步的包。
     *
     * @param sanValue San 值
     * @param sanCheck 开关值
     * @return 同步包
     */
    public static SanDataPayload full(double sanValue, boolean sanCheck) {
        return new SanDataPayload(KIND_FULL, sanValue, sanCheck);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
