package com.pasterdream.pasterdreammod.api.effect.screen;

import com.pasterdream.pasterdreammod.api.network.ScreenEffectPayload;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Optional;

/**
 * 屏幕特效 API 门面 —— 服务端触发全屏屏幕特效的入口
 * <p>
 * 借鉴开源模组 FDLib（作者 FINDERFEED）的 {@code FDLibCalls.sendScreenEffect}
 * 设计思路（独立实现，非复制）。服务端调用后，数据经 {@link ScreenEffectPayload}
 * 下发到客户端，由 {@code ScreenEffectOverlay} 渲染。
 * <p>
 * <b>专用服安全</b>：本类为纯静态门面，只引用通用类（{@link ScreenEffectType}
 * 仅含 id + 数据编解码，不含客户端工厂），服务端可安全触发特效，无需加载
 * 客户端实现类。
 *
 * @see ScreenEffectType
 * @see ScreenEffectRegistry
 */
public final class ScreenEffectAPI {

    private ScreenEffectAPI() {
        throw new UnsupportedOperationException("ScreenEffectAPI 是纯静态门面类，不可实例化");
    }

    /**
     * 向单个玩家发送屏幕特效
     *
     * @param player   目标玩家（服务端）
     * @param type     特效类型（通用元数据，服务端安全）
     * @param data     特效数据
     * @param inTime   渐入 tick 数
     * @param stayTime 持续 tick 数
     * @param outTime  渐出 tick 数
     * @param <D>      特效数据实现
     */
    public static <D extends ScreenEffectData> void sendScreenEffect(
            ServerPlayer player, ScreenEffectType<D> type, D data, int inTime, int stayTime, int outTime) {
        PacketDistributor.sendToPlayer(player,
                new ScreenEffectPayload(type.id(), data, inTime, stayTime, outTime));
    }

    /**
     * 向指定位置范围内所有玩家广播屏幕特效
     *
     * @param level    服务端世界
     * @param center   广播中心
     * @param radius   广播半径（格）
     * @param type     特效类型（通用元数据，服务端安全）
     * @param data     特效数据
     * @param inTime   渐入 tick 数
     * @param stayTime 持续 tick 数
     * @param outTime  渐出 tick 数
     * @param <D>      特效数据实现
     */
    public static <D extends ScreenEffectData> void sendScreenEffectToPlayers(
            ServerLevel level, Vec3 center, double radius,
            ScreenEffectType<D> type, D data, int inTime, int stayTime, int outTime) {
        PacketDistributor.sendToPlayersNear(level, null, center.x, center.y, center.z, radius,
                new ScreenEffectPayload(type.id(), data, inTime, stayTime, outTime));
    }

    /**
     * 注册一个屏幕特效类型（附属模组在类加载期调用，服务端安全）
     *
     * @param id    特效类型唯一标识
     * @param codec 数据网络编解码器
     * @param <D>   特效数据实现
     */
    public static <D extends ScreenEffectData> void registerType(
            ResourceLocation id, StreamCodec<ByteBuf, D> codec) {
        ScreenEffectRegistry.register(new ScreenEffectType<>(id, codec));
    }

    /**
     * 按 id 查询特效类型
     *
     * @param id 特效类型 id
     * @return 包含类型的 {@link Optional}
     */
    public static Optional<ScreenEffectType<?>> getType(ResourceLocation id) {
        return ScreenEffectRegistry.find(id);
    }

    /**
     * 测试辅助：清空特效类型注册表
     */
    public static void resetForTesting() {
        ScreenEffectRegistry.resetForTesting();
    }
}
