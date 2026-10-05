package com.pasterdream.pasterdreammod.network;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * S2C：打开笔记阅读/编辑界面。
 * <p>
 * 固定叙事笔记仅传 {@link #noteId}（客户端从同步注册表解析正文/标题）；
 * 可编辑笔记 {@code noteId=-1}，直接携带双语正文与名称，客户端按语言取用。
 *
 * @param noteId 固定笔记序号（0..14）；-1 表示可编辑笔记
 * @param editable 是否可编辑
 * @param textZh 可编辑笔记正文（中文）；固定笔记为空
 * @param textEn 可编辑笔记正文（英文）；固定笔记为空
 * @param nameZh 可编辑笔记标题（中文）；固定笔记为空
 * @param nameEn 可编辑笔记标题（英文）；固定笔记为空
 */
public record OpenNotePayload(
        int noteId,
        boolean editable,
        String textZh,
        String textEn,
        String nameZh,
        String nameEn
) implements CustomPacketPayload {

    /** 包类型标识。 */
    public static final Type<OpenNotePayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "open_note"));

    /** 网络编解码器。 */
    public static final StreamCodec<ByteBuf, OpenNotePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, OpenNotePayload::noteId,
            ByteBufCodecs.BOOL, OpenNotePayload::editable,
            ByteBufCodecs.STRING_UTF8, OpenNotePayload::textZh,
            ByteBufCodecs.STRING_UTF8, OpenNotePayload::textEn,
            ByteBufCodecs.STRING_UTF8, OpenNotePayload::nameZh,
            ByteBufCodecs.STRING_UTF8, OpenNotePayload::nameEn,
            OpenNotePayload::new);

    /**
     * 构造固定叙事笔记打开包。
     *
     * @param noteId 0..14
     * @return 打开包
     */
    public static OpenNotePayload fixed(int noteId) {
        return new OpenNotePayload(noteId, false, "", "", "", "");
    }

    /**
     * 构造可编辑笔记打开包。
     *
     * @param textZh 中文正文
     * @param textEn 英文正文
     * @param nameZh 中文标题
     * @param nameEn 英文标题
     * @return 打开包
     */
    public static OpenNotePayload editable(String textZh, String textEn, String nameZh, String nameEn) {
        return new OpenNotePayload(-1, true, textZh, textEn, nameZh, nameEn);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
