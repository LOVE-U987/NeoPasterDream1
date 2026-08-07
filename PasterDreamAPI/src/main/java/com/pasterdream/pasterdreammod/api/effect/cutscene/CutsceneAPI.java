package com.pasterdream.pasterdreammod.api.effect.cutscene;

import com.pasterdream.pasterdreammod.api.PasterDreamAPI;
import com.pasterdream.pasterdreammod.api.network.StartCutscenePayload;
import com.pasterdream.pasterdreammod.api.network.StopCutscenePayload;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 过场动画 API 门面 —— 服务端触发相机过场的入口
 * <p>
 * 借鉴开源模组 FDLib（作者 FINDERFEED）的 {@code FDLibCalls.startCutscene}
 * 设计思路（独立实现，非复制）。服务端调用后，过场数据经
 * {@link StartCutscenePayload} 下发到客户端，由 {@code CutsceneCameraHandler}
 * 接管相机。
 * <p>
 * 本类为纯静态门面，只引用通用类（服务端发送），不含客户端符号，专用服安全。
 * 客户端相机实体类型经 {@link #ENTITY_REGISTRY} 注册，专用服只注册不 spawn。
 */
public final class CutsceneAPI {

    /** 客户端相机实体类型注册器（客户端 {@code ClientCameraEntity}） */
    public static final DeferredRegister<EntityType<?>> ENTITY_REGISTRY =
            DeferredRegister.create(Registries.ENTITY_TYPE, PasterDreamAPI.DATA_NAMESPACE);

    /** 客户端相机实体类型的 Holder（客户端运行时经 {@link #CLIENT_CAMERA} 查询） */
    public static final net.minecraft.core.Holder<EntityType<?>> CLIENT_CAMERA =
            ENTITY_REGISTRY.register("client_camera", CutsceneAPI::createClientCameraType);

    static {
        // 兼容旧调用：确保注册发生在类加载（无额外逻辑）
    }

    /** 客户端相机实体类型的工厂（惰性方法引用，专用服只注册不 spawn） */
    private static EntityType<?> createClientCameraType() {
        return EntityType.Builder.<Entity>of(
                        (type, level) -> createClientCameraEntity(type, level),
                        MobCategory.MISC)
                .sized(0.1f, 0.1f)
                .noSummon()
                .noSave()
                .fireImmune()
                .build("client_camera");
    }

    /** 通过反射创建客户端相机实体，避免通用包静态链接客户端类 */
    private static Entity createClientCameraEntity(EntityType<?> type, net.minecraft.world.level.Level level) {
        try {
            Class<?> clazz = Class.forName("com.pasterdream.pasterdreammod.api.client.effect.cutscene.ClientCameraEntity");
            return (Entity) clazz.getConstructor(EntityType.class, net.minecraft.world.level.Level.class)
                    .newInstance(type, level);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("[PasterDreamAPI] 无法创建客户端相机实体（专用服不应调用）", e);
        }
    }

    private CutsceneAPI() {
        throw new UnsupportedOperationException("CutsceneAPI 是纯静态门面类，不可实例化");
    }

    /**
     * 向单个玩家发送过场动画
     *
     * @param player 目标玩家（服务端）
     * @param data   过场数据
     */
    public static void startCutscene(ServerPlayer player, CutsceneData data) {
        PacketDistributor.sendToPlayer(player, new StartCutscenePayload(data.toTag()));
    }

    /**
     * 向指定位置范围内所有玩家广播过场动画
     *
     * @param level   服务端世界
     * @param center  广播中心
     * @param radius  广播半径（格）
     * @param data    过场数据
     */
    public static void startCutsceneForPlayers(ServerLevel level, Vec3 center, double radius, CutsceneData data) {
        PacketDistributor.sendToPlayersNear(level, null, center.x, center.y, center.z, radius,
                new StartCutscenePayload(data.toTag()));
    }

    /**
     * 强制停止单个玩家的过场动画
     *
     * @param player 目标玩家（服务端）
     */
    public static void stopCutscene(ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, new StopCutscenePayload());
    }

    /**
     * 强制停止指定位置范围内所有玩家的过场动画
     *
     * @param level   服务端世界
     * @param center  广播中心
     * @param radius  广播半径（格）
     */
    public static void stopCutsceneForPlayers(ServerLevel level, Vec3 center, double radius) {
        PacketDistributor.sendToPlayersNear(level, null, center.x, center.y, center.z, radius,
                new StopCutscenePayload());
    }
}
