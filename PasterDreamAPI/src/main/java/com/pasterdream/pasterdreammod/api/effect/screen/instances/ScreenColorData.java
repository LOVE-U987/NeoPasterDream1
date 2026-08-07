package com.pasterdream.pasterdreammod.api.effect.screen.instances;

import com.pasterdream.pasterdreammod.api.effect.screen.ScreenEffectData;
import com.pasterdream.pasterdreammod.api.effect.screen.ScreenEffectType;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

/**
 * 纯色屏幕特效数据 —— 全屏填充单一颜色的特效参数
 * <p>
 * 借鉴开源模组 FDLib（作者 FINDERFEED）的 {@code ScreenColorData} 设计思路
 * （独立实现，非复制）。
 *
 * @param argb 颜色（ARGB 32 位，如 {@code 0x55000000} 为半透明黑）
 */
public record ScreenColorData(int argb) implements ScreenEffectData {

    /** 网络编解码器 */
    public static final StreamCodec<ByteBuf, ScreenColorData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ScreenColorData::argb,
            ScreenColorData::new
    );

    /** 特效类型 id（服务端发送与客户端反查共用） */
    public static final ResourceLocation TYPE_ID = ResourceLocation.parse("pasterdream:screen_color");

    /** 特效类型（id + 数据编解码，服务端安全，不含客户端工厂） */
    public static final ScreenEffectType<ScreenColorData> TYPE =
            new ScreenEffectType<>(TYPE_ID, STREAM_CODEC);

    /**
     * 便捷构造：由 RGBA 分量生成
     *
     * @param r 红 0-255
     * @param g 绿 0-255
     * @param b 蓝 0-255
     * @param a 透明 0-255
     * @return 颜色数据
     */
    public static ScreenColorData of(int r, int g, int b, int a) {
        return new ScreenColorData((a << 24) | (r << 16) | (g << 8) | b);
    }
}
