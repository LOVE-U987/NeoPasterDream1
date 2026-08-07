package com.pasterdream.pasterdreammod.api.network;

import com.pasterdream.pasterdreammod.api.PasterDreamAPI;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * 氛围特效网络包（S2C）—— 服务端触发雾色/暗化氛围
 * <p>
 * 借鉴开源模组 FDBosses（作者 FINDERFEED）的 {@code BossClientEvents} 雾色氛围
 * 设计思路（独立实现，非复制）。
 *
 * @param kind     氛围类型：0=暗化（灰雾变暗），1=血色雾
 * @param strength 强度（0-1，越大越浓）
 * @param duration 持续 tick 数（到达后自动衰减退出）
 */
public record AtmospherePayload(int kind, float strength, int duration) implements CustomPacketPayload {

    /** 氛围类型常量：暗化（灰雾） */
    public static final int KIND_DARKEN = 0;
    /** 氛围类型常量：血色雾 */
    public static final int KIND_BLOOD_FOG = 1;

    /** 包类型 ID */
    public static final CustomPacketPayload.Type<AtmospherePayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(
                    PasterDreamAPI.DATA_NAMESPACE, "atmosphere"));

    /** 网络编解码器 */
    public static final StreamCodec<ByteBuf, AtmospherePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, AtmospherePayload::kind,
            ByteBufCodecs.FLOAT, AtmospherePayload::strength,
            ByteBufCodecs.VAR_INT, AtmospherePayload::duration,
            AtmospherePayload::new
    );

    /**
     * 构造暗化氛围
     *
     * @param strength 强度（0-1）
     * @param duration 持续 tick 数
     * @return 包实例
     */
    public static AtmospherePayload darken(float strength, int duration) {
        return new AtmospherePayload(KIND_DARKEN, strength, duration);
    }

    /**
     * 构造血色雾氛围
     *
     * @param strength 强度（0-1）
     * @param duration 持续 tick 数
     * @return 包实例
     */
    public static AtmospherePayload bloodFog(float strength, int duration) {
        return new AtmospherePayload(KIND_BLOOD_FOG, strength, duration);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
