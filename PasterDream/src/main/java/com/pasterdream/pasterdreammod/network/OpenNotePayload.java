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
 * 固定叙事笔记仅传 {@link #noteId}（客户端从同步注册表解析正文/作者）；玩家笔记 {@code noteId=-1}，
 * 携带双语正文、作者与每语言定稿标记，客户端按当前语言取用并据此路由到阅读/编辑界面。
 *
 * @param noteId    固定笔记序号（0..14）；-1 表示玩家笔记
 * @param playerNote 是否玩家笔记（与 {@code noteId==-1} 等价；仅作语义标注）
 * @param textZh    玩家笔记正文（中文）；固定笔记为空
 * @param textEn    玩家笔记正文（英文）；固定笔记为空
 * @param authorZh  玩家笔记作者（中文）；固定笔记为空
 * @param authorEn  玩家笔记作者（英文）；固定笔记为空
 * @param signedZh  中文是否已署名（只读）
 * @param signedEn  英文是否已署名（只读）
 */
public record OpenNotePayload(
        int noteId,
        boolean playerNote,
        String textZh,
        String textEn,
        String authorZh,
        String authorEn,
        boolean signedZh,
        boolean signedEn
) implements CustomPacketPayload {

    /** 包类型标识。 */
    public static final Type<OpenNotePayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "open_note"));

    /**
     * 网络编解码器（手写：8 字段超出 vanilla {@code StreamCodec.composite} 的 6 段上限）。
     * <p>
     * wire 顺序：{@code noteId, playerNote, textZh, textEn, authorZh, authorEn, signedZh, signedEn}。
     */
    public static final StreamCodec<ByteBuf, OpenNotePayload> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public OpenNotePayload decode(ByteBuf buffer) {
            int noteId = ByteBufCodecs.VAR_INT.decode(buffer);
            boolean playerNote = ByteBufCodecs.BOOL.decode(buffer);
            String textZh = ByteBufCodecs.STRING_UTF8.decode(buffer);
            String textEn = ByteBufCodecs.STRING_UTF8.decode(buffer);
            String authorZh = ByteBufCodecs.STRING_UTF8.decode(buffer);
            String authorEn = ByteBufCodecs.STRING_UTF8.decode(buffer);
            boolean signedZh = ByteBufCodecs.BOOL.decode(buffer);
            boolean signedEn = ByteBufCodecs.BOOL.decode(buffer);
            return new OpenNotePayload(noteId, playerNote, textZh, textEn, authorZh, authorEn, signedZh, signedEn);
        }

        @Override
        public void encode(ByteBuf buffer, OpenNotePayload value) {
            ByteBufCodecs.VAR_INT.encode(buffer, value.noteId());
            ByteBufCodecs.BOOL.encode(buffer, value.playerNote());
            ByteBufCodecs.STRING_UTF8.encode(buffer, value.textZh());
            ByteBufCodecs.STRING_UTF8.encode(buffer, value.textEn());
            ByteBufCodecs.STRING_UTF8.encode(buffer, value.authorZh());
            ByteBufCodecs.STRING_UTF8.encode(buffer, value.authorEn());
            ByteBufCodecs.BOOL.encode(buffer, value.signedZh());
            ByteBufCodecs.BOOL.encode(buffer, value.signedEn());
        }
    };

    /**
     * 构造固定叙事笔记打开包。
     *
     * @param noteId 0..14
     * @return 打开包
     */
    public static OpenNotePayload fixed(int noteId) {
        return new OpenNotePayload(noteId, false, "", "", "", "", false, false);
    }

    /**
     * 构造玩家笔记打开包。
     *
     * @param textZh   中文正文
     * @param textEn   英文正文
     * @param authorZh 中文作者
     * @param authorEn 英文作者
     * @param signedZh 中文是否已署名
     * @param signedEn 英文是否已署名
     * @return 打开包
     */
    public static OpenNotePayload player(String textZh, String textEn, String authorZh, String authorEn,
                                         boolean signedZh, boolean signedEn) {
        return new OpenNotePayload(-1, true, textZh, textEn, authorZh, authorEn, signedZh, signedEn);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
