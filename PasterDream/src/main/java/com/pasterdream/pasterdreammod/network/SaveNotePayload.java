package com.pasterdream.pasterdreammod.network;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * C2S：保存可编辑笔记的当前语言字段。
 * <p>
 * 服务端校验发送者主手为可编辑笔记载体、语言白名单与字节上限后写入物品
 * {@code CUSTOM_DATA}（{@code text}/{@code text_en} 与 {@code name}/{@code name_en}）。
 *
 * @param body       正文（当前语言）
 * @param name       标题（当前语言）
 * @param languageId 语言代码（{@code zh_cn}/{@code en_us}）
 */
public record SaveNotePayload(String body, String name, String languageId) implements CustomPacketPayload {

    /** 包类型标识。 */
    public static final Type<SaveNotePayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "save_note"));

    /** 网络编解码器。 */
    public static final StreamCodec<ByteBuf, SaveNotePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, SaveNotePayload::body,
            ByteBufCodecs.STRING_UTF8, SaveNotePayload::name,
            ByteBufCodecs.STRING_UTF8, SaveNotePayload::languageId,
            SaveNotePayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
