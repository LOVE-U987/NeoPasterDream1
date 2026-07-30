package com.pasterdream.pasterdreammod.network;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * S2C：通知本地客户端开始播放 playerAnimator 闪避姿势（evasion）。
 * <p>
 * 原版依赖双端 PlayerTick + 实体持久化 NBT {@code evasion} 在客户端侧驱动
 * {@code EvasionAnimationProcedure}；新版瞬身/回避均在服务端写 NBT，客户端拿不到该标志，
 * 故在设置 {@code evasion=true} 时向目标玩家下发本包，由客户端启动本地 tick 播放。
 */
public record EvasionPosePayload() implements CustomPacketPayload {

    /** 包类型标识 */
    public static final Type<EvasionPosePayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "evasion_pose"));

    /** 单例（无字段） */
    public static final EvasionPosePayload INSTANCE = new EvasionPosePayload();

    /** 网络编解码器（空载荷） */
    public static final StreamCodec<ByteBuf, EvasionPosePayload> STREAM_CODEC =
            StreamCodec.unit(INSTANCE);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
