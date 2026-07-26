package com.pasterdream.pasterdreammod.network;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * 瞬身术按键 C2S 消息
 * <p>
 * 移植自原版 {@code network/TeleportationMessage.java}：客户端按下瞬身术键
 * （默认 C，见 {@code client/PDKeyMappings}）时发送。字段语义与原版一致
 * （type=0 表示按下；pressedMs 原版恒为 0，保留以兼容原始协议格式）。
 * <p>
 * 服务端处理见 {@link PDNetwork#handleTeleportationOnServer}
 * 服务端逻辑由 {@link PDNetwork} 的 executeTeleportation 执行。
 *
 * @param action    按键动作类型（0=按下；命名避开 CustomPacketPayload#type() 接口方法）
 * @param pressedMs 按压时长毫秒（原版恒 0）
 */
public record TeleportationPayload(int action, int pressedMs) implements CustomPacketPayload {

    /** 包类型标识 */
    public static final Type<TeleportationPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "teleportation"));

    /** 网络编解码器 */
    public static final StreamCodec<ByteBuf, TeleportationPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, TeleportationPayload::action,
            ByteBufCodecs.VAR_INT, TeleportationPayload::pressedMs,
            TeleportationPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
