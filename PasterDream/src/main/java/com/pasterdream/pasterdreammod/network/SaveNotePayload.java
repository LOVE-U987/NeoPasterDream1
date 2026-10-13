package com.pasterdream.pasterdreammod.network;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * C2S：保存玩家笔记的当前语言字段。
 * <p>
 * 服务端校验发送者主手为可编辑笔记载体、语言白名单与字节上限后写入物品
 * {@code CUSTOM_DATA} 的**当前语言键**（{@code text}/{@code name}/{@code author}/{@code signed}
 * 或对应 {@code _en}）。
 * <ul>
 *   <li>{@code signed=false}（完成）：仅写当前语言正文，不动标题/作者/署名标记；</li>
 *   <li>{@code signed=true}（署名）：写正文 + 当前语言标题/作者/署名标记。</li>
 * </ul>
 *
 * @param body       正文（当前语言）
 * @param name       标题（当前语言）；{@code signed=false} 时忽略
 * @param author     作者（当前语言）；{@code signed=false} 时忽略
 * @param languageId 语言代码（{@code zh_cn}/{@code en_us}）
 * @param signed     本次是否署名定稿
 */
public record SaveNotePayload(String body, String name, String author, String languageId, boolean signed)
        implements CustomPacketPayload {

    /** 包类型标识。 */
    public static final Type<SaveNotePayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "save_note"));

    /** 网络编解码器。 */
    public static final StreamCodec<ByteBuf, SaveNotePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, SaveNotePayload::body,
            ByteBufCodecs.STRING_UTF8, SaveNotePayload::name,
            ByteBufCodecs.STRING_UTF8, SaveNotePayload::author,
            ByteBufCodecs.STRING_UTF8, SaveNotePayload::languageId,
            ByteBufCodecs.BOOL, SaveNotePayload::signed,
            SaveNotePayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
