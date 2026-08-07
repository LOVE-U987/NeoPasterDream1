package com.pasterdream.pasterdreammod.api.network;

import com.pasterdream.pasterdreammod.api.PasterDreamAPI;
import com.pasterdream.pasterdreammod.api.effect.cutscene.CutsceneData;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * 过场动画开始网络包（S2C）—— 服务端下发一段过场动画数据到客户端
 * <p>
 * 数据经 {@link CutsceneData#toTag()} 序列化为 NBT 传输。
 *
 * @param dataNbt 过场动画数据（NBT）
 */
public record StartCutscenePayload(CompoundTag dataNbt) implements CustomPacketPayload {

    /** 包类型 ID */
    public static final CustomPacketPayload.Type<StartCutscenePayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(
                    PasterDreamAPI.DATA_NAMESPACE, "start_cutscene"));

    /** 网络编解码器 */
    public static final StreamCodec<ByteBuf, StartCutscenePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.COMPOUND_TAG, StartCutscenePayload::dataNbt,
            StartCutscenePayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
