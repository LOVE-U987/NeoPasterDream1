package com.pasterdream.pasterdreammod.network;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.attachment.SanData;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * San 理智数据 S2C 同步包
 * <p>
 * 移植自原版 {@code communication/SanDataMessage.java}（SimpleChannel），
 * kind 语义与原版一致：
 * <ul>
 *   <li>{@link #KIND_VALUE_ONLY}（0）— 仅更新理智值</li>
 *   <li>{@link #KIND_CHECK_ONLY}（1）— 仅更新理智系统开关</li>
 *   <li>{@link #KIND_FULL}（2）— 全量同步（原版为 NBT 全量，此处直接携带两个字段）</li>
 * </ul>
 * 客户端处理逻辑见 {@link PDNetwork#handleSanDataOnClient}。
 *
 * @param kind     同步类型（0/1/2）
 * @param sanValue 理智值（kind=0/2 时有效）
 * @param sanCheck 理智系统开关（kind=1/2 时有效）
 */
public record SanDataPayload(int kind, double sanValue, boolean sanCheck) implements CustomPacketPayload {

    /** kind=0：仅修改数值 */
    public static final int KIND_VALUE_ONLY = 0;
    /** kind=1：仅修改开关 */
    public static final int KIND_CHECK_ONLY = 1;
    /** kind=2：全量同步 */
    public static final int KIND_FULL = 2;

    /** 包类型标识 */
    public static final Type<SanDataPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "san_data"));

    /** 网络编解码器 */
    public static final StreamCodec<ByteBuf, SanDataPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SanDataPayload::kind,
            ByteBufCodecs.DOUBLE, SanDataPayload::sanValue,
            ByteBufCodecs.BOOL, SanDataPayload::sanCheck,
            SanDataPayload::new);

    /**
     * 构建"仅数值"包（对应原版 SanDataMessage(double)）
     *
     * @param value 理智值
     * @return 包实例
     */
    public static SanDataPayload valueOnly(double value) {
        return new SanDataPayload(KIND_VALUE_ONLY, value, false);
    }

    /**
     * 构建"仅开关"包（对应原版 SanDataMessage(boolean)）
     *
     * @param check 开关值
     * @return 包实例
     */
    public static SanDataPayload checkOnly(boolean check) {
        return new SanDataPayload(KIND_CHECK_ONLY, 0.0D, check);
    }

    /**
     * 构建全量同步包（对应原版 SanDataMessage(SanCapability)）
     *
     * @param data San 数据
     * @return 包实例
     */
    public static SanDataPayload full(SanData data) {
        return new SanDataPayload(KIND_FULL, data.sanValue(), data.sanCheck());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
