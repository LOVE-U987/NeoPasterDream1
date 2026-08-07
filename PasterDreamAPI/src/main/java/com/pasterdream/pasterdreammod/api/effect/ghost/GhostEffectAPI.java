package com.pasterdream.pasterdreammod.api.effect.ghost;

import com.pasterdream.pasterdreammod.api.network.GhostPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * 残影特效 API 门面 —— 服务端为指定实体开启残影拖尾的入口
 * <p>
 * 借鉴开源模组 FDBosses（作者 FINDERFEED）的 {@code EntityGhostParticle} 设计思路
 * （独立实现，非复制）。服务端调用后，客户端每 tick 采样目标实体位置生成
 * 半透明残影副本，渲染出"虚影拖尾"——适合 BOSS 冲刺/快速位移技能的视觉反馈。
 * <p>
 * 本类为纯静态门面，只引用通用类（服务端发送），不含客户端符号，专用服安全。
 */
public final class GhostEffectAPI {

    private GhostEffectAPI() {
        throw new UnsupportedOperationException("GhostEffectAPI 是纯静态门面类，不可实例化");
    }

    /**
     * 向指定位置范围内所有玩家广播开启残影
     *
     * @param level    服务端世界
     * @param center   广播中心
     * @param radius   广播半径（格）
     * @param entityId 目标实体网络 id
     * @param duration 残影持续 tick 数
     * @param alpha    残影初始透明度（0-255，越小越淡）
     */
    public static void startGhostTrail(ServerLevel level, Vec3 center, double radius,
                                       int entityId, int duration, int alpha) {
        PacketDistributor.sendToPlayersNear(level, null, center.x, center.y, center.z, radius,
                new GhostPayload(entityId, duration, alpha));
    }

    /**
     * 向单个玩家广播开启残影
     *
     * @param player   目标玩家（服务端）
     * @param entityId 目标实体网络 id
     * @param duration 残影持续 tick 数
     * @param alpha    残影初始透明度（0-255，越小越淡）
     */
    public static void startGhostTrail(ServerPlayer player, int entityId, int duration, int alpha) {
        PacketDistributor.sendToPlayer(player, new GhostPayload(entityId, duration, alpha));
    }
}
