package com.pasterdream.pasterdreammod.api.effect.shake;

import com.pasterdream.pasterdreammod.api.network.ScreenShakePayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * 屏幕晃动 API 门面 —— 服务端触发屏幕晃动的入口
 * <p>
 * 借鉴开源模组 FDLib（作者 FINDERFEED）的 {@code FDScreenShake} 设计思路
 * （独立实现，非复制）。服务端调用后，晃动数据经 {@link ScreenShakePayload}
 * 下发到客户端，由 {@code ScreenShakeHandler} 在渲染时对投影矩阵施加
 * 随机偏移（三阶段衰减）。适合 BOSS 终结技/爆炸等大冲击时刻。
 * <p>
 * 本类为纯静态门面，只引用通用类（服务端发送），不含客户端符号，专用服安全。
 */
public final class ScreenShakeAPI {

    private ScreenShakeAPI() {
        throw new UnsupportedOperationException("ScreenShakeAPI 是纯静态门面类，不可实例化");
    }

    /**
     * 向指定位置范围内所有玩家广播屏幕晃动
     *
     * @param level   服务端世界
     * @param center  广播中心
     * @param radius  广播半径（格）
     * @param data    晃动数据
     */
    public static void sendShake(ServerLevel level, Vec3 center, double radius, ScreenShakeData data) {
        PacketDistributor.sendToPlayersNear(level, null, center.x, center.y, center.z, radius,
                new ScreenShakePayload(data));
    }

    /**
     * 向单个玩家发送屏幕晃动
     *
     * @param player 目标玩家（服务端）
     * @param data   晃动数据
     */
    public static void sendShake(ServerPlayer player, ScreenShakeData data) {
        PacketDistributor.sendToPlayer(player, new ScreenShakePayload(data));
    }
}
