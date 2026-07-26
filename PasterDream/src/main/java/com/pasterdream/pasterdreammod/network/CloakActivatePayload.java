package com.pasterdream.pasterdreammod.network;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * 斗篷激活按键 C2S 消息
 * <p>
 * 移植自原版 {@code network/CloakActivateMessage.java}：客户端按下斗篷激活键
 * （默认 Z，见 {@code client/PDKeyMappings}）时发送。字段语义与原版一致。
 * <p>
 * 服务端处理见 {@link PDNetwork#handleCloakActivateOnServer}
 * 服务端逻辑由 {@link PDNetwork} 的 executeCloakActivate 执行。
 *
 * @param action    按键动作类型（0=按下；命名避开 CustomPacketPayload#type() 接口方法）
 * @param pressedMs 按压时长毫秒（原版恒 0）
 */
public record CloakActivatePayload(int action, int pressedMs) implements CustomPacketPayload {

    /** 包类型标识 */
    public static final Type<CloakActivatePayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "cloak_activate"));

    /** 网络编解码器 */
    public static final StreamCodec<ByteBuf, CloakActivatePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, CloakActivatePayload::action,
            ByteBufCodecs.VAR_INT, CloakActivatePayload::pressedMs,
            CloakActivatePayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
