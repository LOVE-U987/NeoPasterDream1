package com.pasterdream.pasterdreammod.api.network;

import com.pasterdream.pasterdreammod.api.PasterDreamAPI;
import com.pasterdream.pasterdreammod.api.effect.shake.ScreenShakeData;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * 屏幕晃动网络包（S2C）—— 服务端触发一次屏幕晃动到客户端
 * <p>
 * 客户端收到后由 {@code ScreenShakeHandler} 维护晃动实例，渲染时对投影
 * 矩阵施加随机偏移。
 *
 * @param data 晃动数据
 */
public record ScreenShakePayload(ScreenShakeData data) implements CustomPacketPayload {

    /** 包类型 ID */
    public static final CustomPacketPayload.Type<ScreenShakePayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(
                    PasterDreamAPI.DATA_NAMESPACE, "screen_shake"));

    /** 网络编解码器 */
    public static final StreamCodec<ByteBuf, ScreenShakePayload> STREAM_CODEC = StreamCodec.composite(
            ScreenShakeData.STREAM_CODEC, ScreenShakePayload::data,
            ScreenShakePayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
