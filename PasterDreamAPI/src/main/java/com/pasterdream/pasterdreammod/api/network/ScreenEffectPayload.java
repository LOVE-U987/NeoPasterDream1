package com.pasterdream.pasterdreammod.api.network;

import com.pasterdream.pasterdreammod.api.PasterDreamAPI;
import com.pasterdream.pasterdreammod.api.effect.screen.ScreenEffectData;
import com.pasterdream.pasterdreammod.api.effect.screen.ScreenEffectRegistry;
import com.pasterdream.pasterdreammod.api.effect.screen.ScreenEffectType;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * 屏幕特效网络包（S2C）—— 服务端下发一次屏幕特效到客户端
 * <p>
 * 网络格式：{@code typeId(ResourceLocation) + dataBytes(VarInt 长度 + 数据字节)
 * + inTime/stayTime/outTime(VarInt)}。数据字节由类型的数据编解码器
 * 序列化，客户端按 typeId 反查类型解码。
 * <p>
 * <b>健壮性</b>：若客户端未注册该特效类型（如附属模组场景），仍能正常解码
 * （{@code data} 为 {@code null}），由客户端 handler 跳过而非抛异常断连。
 *
 * @param typeId  特效类型注册 id
 * @param data    特效数据（解码失败时可能为 {@code null}）
 * @param inTime  渐入 tick 数
 * @param stayTime 持续 tick 数
 * @param outTime 渐出 tick 数
 */
public record ScreenEffectPayload(
        ResourceLocation typeId,
        ScreenEffectData data,
        int inTime,
        int stayTime,
        int outTime
) implements CustomPacketPayload {

    /** 包类型 ID */
    public static final CustomPacketPayload.Type<ScreenEffectPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(
                    PasterDreamAPI.DATA_NAMESPACE, "screen_effect"));

    /** 网络编解码器 */
    public static final StreamCodec<FriendlyByteBuf, ScreenEffectPayload> STREAM_CODEC =
            new StreamCodec<>() {
                @Override
                public ScreenEffectPayload decode(FriendlyByteBuf buf) {
                    ResourceLocation id = ResourceLocation.STREAM_CODEC.decode(buf);
                    byte[] dataBytes = buf.readByteArray();
                    int in = buf.readVarInt();
                    int stay = buf.readVarInt();
                    int out = buf.readVarInt();

                    ScreenEffectType<?> type = ScreenEffectRegistry.get(id);
                    ScreenEffectData data = type != null ? decodeData(type, dataBytes) : null;
                    return new ScreenEffectPayload(id, data, in, stay, out);
                }

                @Override
                public void encode(FriendlyByteBuf buf, ScreenEffectPayload payload) {
                    ResourceLocation.STREAM_CODEC.encode(buf, payload.typeId());
                    ScreenEffectType<?> type = ScreenEffectRegistry.get(payload.typeId());
                    byte[] dataBytes = type != null && payload.data() != null
                            ? encodeData(type, payload.data())
                            : new byte[0];
                    buf.writeByteArray(dataBytes);
                    buf.writeVarInt(payload.inTime());
                    buf.writeVarInt(payload.stayTime());
                    buf.writeVarInt(payload.outTime());
                }

                @SuppressWarnings("unchecked")
                private <T extends ScreenEffectData> ScreenEffectData decodeData(ScreenEffectType<?> type, byte[] bytes) {
                    ByteBuf buf = Unpooled.wrappedBuffer(bytes);
                    try {
                        return ((StreamCodec<ByteBuf, T>) type.dataCodec()).decode(buf);
                    } catch (Exception e) {
                        return null;
                    } finally {
                        buf.release();
                    }
                }

                @SuppressWarnings("unchecked")
                private <T extends ScreenEffectData> byte[] encodeData(ScreenEffectType<?> type, ScreenEffectData data) {
                    ByteBuf buf = Unpooled.buffer();
                    try {
                        ((StreamCodec<ByteBuf, T>) type.dataCodec()).encode(buf, (T) data);
                        byte[] bytes = new byte[buf.readableBytes()];
                        buf.readBytes(bytes);
                        return bytes;
                    } catch (Exception e) {
                        return new byte[0];
                    } finally {
                        buf.release();
                    }
                }
            };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
