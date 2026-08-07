package com.pasterdream.pasterdreammod.api.effect.atmosphere;

import com.pasterdream.pasterdreammod.api.network.AtmospherePayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * 氛围特效 API 门面 —— 服务端触发雾色/暗化氛围的入口
 * <p>
 * 借鉴开源模组 FDBosses（作者 FINDERFEED）的 {@code BossClientEvents} 雾色氛围
 * 设计思路（独立实现，非复制）。服务端调用后，客户端经
 * {@code ViewportEvent.ComputeFogColor} 修改雾色（暗化/血色），持续
 * {@code duration} tick 后自动衰减退出。适合 BOSS 狂暴/阶段切换的氛围铺垫。
 * <p>
 * 本类为纯静态门面，只引用通用类（服务端发送），不含客户端符号，专用服安全。
 */
public final class AtmosphereEffectAPI {

    private AtmosphereEffectAPI() {
        throw new UnsupportedOperationException("AtmosphereEffectAPI 是纯静态门面类，不可实例化");
    }

    /**
     * 向指定位置范围内所有玩家广播暗化氛围（灰雾变暗）
     *
     * @param level    服务端世界
     * @param center   广播中心
     * @param radius   广播半径（格）
     * @param strength 强度（0-1）
     * @param duration 持续 tick 数
     */
    public static void darken(ServerLevel level, Vec3 center, double radius, float strength, int duration) {
        PacketDistributor.sendToPlayersNear(level, null, center.x, center.y, center.z, radius,
                AtmospherePayload.darken(strength, duration));
    }

    /**
     * 向指定位置范围内所有玩家广播血色雾氛围
     *
     * @param level    服务端世界
     * @param center   广播中心
     * @param radius   广播半径（格）
     * @param strength 强度（0-1）
     * @param duration 持续 tick 数
     */
    public static void bloodFog(ServerLevel level, Vec3 center, double radius, float strength, int duration) {
        PacketDistributor.sendToPlayersNear(level, null, center.x, center.y, center.z, radius,
                AtmospherePayload.bloodFog(strength, duration));
    }

    /**
     * 向单个玩家发送暗化氛围
     *
     * @param player   目标玩家（服务端）
     * @param strength 强度（0-1）
     * @param duration 持续 tick 数
     */
    public static void darken(ServerPlayer player, float strength, int duration) {
        PacketDistributor.sendToPlayer(player, AtmospherePayload.darken(strength, duration));
    }

    /**
     * 向单个玩家发送血色雾氛围
     *
     * @param player   目标玩家（服务端）
     * @param strength 强度（0-1）
     * @param duration 持续 tick 数
     */
    public static void bloodFog(ServerPlayer player, float strength, int duration) {
        PacketDistributor.sendToPlayer(player, AtmospherePayload.bloodFog(strength, duration));
    }
}
