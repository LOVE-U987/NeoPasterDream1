package com.pasterdream.pasterdreammod.network;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * C2S：玩家确认「旧存档已备份，执行更新标记」。
 * <p>
 * 服务端收到后仅写入 ID schema 标记（不重写世界数据），并停止本会话的提示。
 */
public record SaveUpgradeConfirmPayload() implements CustomPacketPayload {

    /** 包类型标识 */
    public static final Type<SaveUpgradeConfirmPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "save_upgrade_confirm"));

    /** 网络编解码器（无字段） */
    public static final StreamCodec<ByteBuf, SaveUpgradeConfirmPayload> STREAM_CODEC =
            StreamCodec.unit(new SaveUpgradeConfirmPayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
