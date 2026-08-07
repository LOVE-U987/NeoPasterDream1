package com.pasterdream.pasterdreammod.api.effect.particle;

import com.pasterdream.pasterdreammod.api.PasterDreamAPI;
import com.pasterdream.pasterdreammod.api.network.ParticleEmitterPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Optional;

/**
 * 粒子发射器 API 门面 —— 服务端触发粒子发射的入口
 * <p>
 * 借鉴开源模组 FDLib（作者 FINDERFEED）的 {@code FDLibCalls.addParticleEmitter}
 * 设计思路（独立实现，非复制）。服务端调用 {@link #spawn} 后，数据经
 * {@link ParticleEmitterPayload} 下发到范围内玩家客户端，由
 * {@code ParticleEmitterHandler} 执行发射。
 * <p>
 * 本类为纯静态门面，只引用通用类（服务端发送），不含客户端符号，专用服安全。
 *
 * @see ParticleEmitterData
 * @see EmitterProcessorRegistry
 */
public final class ParticleEmitterAPI {

    private ParticleEmitterAPI() {
        throw new UnsupportedOperationException("ParticleEmitterAPI 是纯静态门面类，不可实例化");
    }

    /**
     * 向指定位置范围内所有玩家广播一次粒子发射
     *
     * @param level  服务端世界
     * @param center 发射位置（同时也是广播中心）
     * @param radius 广播半径（格）
     * @param data   发射器数据
     */
    public static void spawn(ServerLevel level, Vec3 center, double radius, ParticleEmitterData data) {
        PacketDistributor.sendToPlayersNear(level, null, center.x, center.y, center.z, radius,
                new ParticleEmitterPayload(data));
    }

    /**
     * 向单个玩家发送一次粒子发射
     *
     * @param player 目标玩家（服务端）
     * @param data   发射器数据
     */
    public static void spawn(ServerPlayer player, ParticleEmitterData data) {
        PacketDistributor.sendToPlayer(player, new ParticleEmitterPayload(data));
    }

    /**
     * 注册一个自定义处理器类型（附属模组在类加载期调用）
     *
     * @param type 处理器类型
     */
    public static void registerProcessorType(EmitterProcessorType<?> type) {
        EmitterProcessorRegistry.register(type);
    }

    /**
     * 按 id 查询处理器类型
     *
     * @param id 处理器类型 id
     * @return 包含类型的 {@link Optional}
     */
    public static Optional<EmitterProcessorType<?>> getProcessorType(net.minecraft.resources.ResourceLocation id) {
        return EmitterProcessorRegistry.find(id);
    }

    /**
     * 测试辅助：清空处理器注册表
     */
    public static void resetForTesting() {
        EmitterProcessorRegistry.resetForTesting();
    }
}
