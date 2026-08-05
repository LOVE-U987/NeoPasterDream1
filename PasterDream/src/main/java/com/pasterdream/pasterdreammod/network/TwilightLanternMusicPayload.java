package com.pasterdream.pasterdreammod.network;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * S2C：暮影之笼事件 BGM 状态同步包。
 * <p>
 * 暮影之笼激活时服务端播放 {@code shadow_music_0}（SoundSource.MUSIC），
 * 而 1.21.1 的 {@link net.minecraft.client.sounds.SoundEngine} 播放 MUSIC 源声音时
 * 不会停止原版 {@link net.minecraft.client.sounds.MusicManager} 正在播放的音乐，
 * 导致「暮影之笼 BGM + 原版 BGM」同时播放。
 * <p>
 * 本包在事件激活（+55t）时下发 {@code active=true}、事件结束（+2600t）时下发
 * {@code active=false}，客户端据此在 {@code SelectMusicEvent} 中返回 {@code null}，
 * 使原版 MusicManager 在事件期间保持静音，只保留暮影之笼自身的 BGM。
 *
 * @param active true=暮影之笼 BGM 激活中（静音原版音乐）；false=事件结束（恢复原版音乐）
 */
public record TwilightLanternMusicPayload(boolean active) implements CustomPacketPayload {

    /** 包类型标识 */
    public static final Type<TwilightLanternMusicPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "twilight_lantern_music"));

    /** 网络编解码器（单布尔字段） */
    public static final StreamCodec<ByteBuf, TwilightLanternMusicPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BOOL, TwilightLanternMusicPayload::active,
                    TwilightLanternMusicPayload::new);

    /**
     * 构造「事件激活」包。
     *
     * @return 激活包
     */
    public static TwilightLanternMusicPayload start() {
        return new TwilightLanternMusicPayload(true);
    }

    /**
     * 构造「事件结束」包。
     *
     * @return 结束包
     */
    public static TwilightLanternMusicPayload stop() {
        return new TwilightLanternMusicPayload(false);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
