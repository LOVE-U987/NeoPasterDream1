package com.pasterdream.pasterdreammod.api.effect.impact;

import com.pasterdream.pasterdreammod.api.network.ImpactFramesPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Arrays;
import java.util.List;

/**
 * 打击帧 API 门面 —— 服务端触发全屏打击帧的入口
 * <p>
 * 借鉴开源模组 FDLib（作者 FINDERFEED）的 {@code FDLibCalls.sendImpactFrames}
 * 设计思路（独立实现，非复制）。服务端调用后，数据经 {@link ImpactFramesPayload}
 * 下发到范围内玩家客户端，由 {@code ImpactFramesHandler} 排队播放。
 * <p>
 * 本类为纯静态门面，只引用通用类（服务端发送），不含客户端符号，专用服安全。
 */
public final class ImpactFrameAPI {

    private ImpactFrameAPI() {
        throw new UnsupportedOperationException("ImpactFrameAPI 是纯静态门面类，不可实例化");
    }

    /**
     * 向指定位置范围内所有玩家广播打击帧
     *
     * @param level   服务端世界
     * @param center  广播中心
     * @param radius  广播半径（格）
     * @param frames  打击帧序列（按顺序排队播放）
     */
    public static void sendImpactFrames(ServerLevel level, Vec3 center, double radius, ImpactFrame... frames) {
        if (frames.length == 0) {
            return;
        }
        List<ImpactFrame> list = Arrays.asList(frames);
        PacketDistributor.sendToPlayersNear(level, null, center.x, center.y, center.z, radius,
                new ImpactFramesPayload(list));
    }

    /**
     * 向单个玩家发送打击帧
     *
     * @param player 目标玩家（服务端）
     * @param frames 打击帧序列
     */
    public static void sendImpactFrame(ServerPlayer player, ImpactFrame... frames) {
        if (frames.length == 0) {
            return;
        }
        PacketDistributor.sendToPlayer(player, new ImpactFramesPayload(Arrays.asList(frames)));
    }
}
