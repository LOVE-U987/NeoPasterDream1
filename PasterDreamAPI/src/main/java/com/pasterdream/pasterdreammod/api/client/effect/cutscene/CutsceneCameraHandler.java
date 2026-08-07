package com.pasterdream.pasterdreammod.api.client.effect.cutscene;

import com.pasterdream.pasterdreammod.api.client.effect.screen.ScreenEffect;
import com.pasterdream.pasterdreammod.api.client.effect.screen.ScreenEffectFactoryRegistry;
import com.pasterdream.pasterdreammod.api.client.effect.screen.ScreenEffectOverlay;
import com.pasterdream.pasterdreammod.api.effect.cutscene.CameraPos;
import com.pasterdream.pasterdreammod.api.effect.cutscene.CutsceneData;
import com.pasterdream.pasterdreammod.api.effect.cutscene.CutsceneScreenEffectData;
import com.pasterdream.pasterdreammod.api.effect.screen.ScreenEffectData;
import com.pasterdream.pasterdreammod.api.effect.screen.ScreenEffectFactory;
import com.pasterdream.pasterdreammod.api.effect.screen.ScreenEffectRegistry;
import com.pasterdream.pasterdreammod.api.effect.screen.ScreenEffectType;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;

/**
 * 过场相机处理器 —— 管理过场状态并接管相机/输入
 * <p>
 * 借鉴开源模组 FDLib（作者 FINDERFEED）的 {@code CutsceneCameraHandler} 设计思路
 * （独立实现，非复制）。本类为纯客户端静态状态机，<b>不自挂事件总线</b>，
 * 由主模 {@code PDEffectClientEvents} 在客户端 tick / 视角事件中调用。
 * <ul>
 *   <li>{@link #tick()}：驱动过场执行、接管输入与相机实体；</li>
 *   <li>{@link #computeCameraAngles(float, float[])}：计算当前朝向；</li>
 *   <li>内嵌屏幕特效时间轴在对应 tick 触发。</li>
 * </ul>
 */
@OnlyIn(Dist.CLIENT)
public final class CutsceneCameraHandler {

    private static ClientCameraEntity cameraEntity;
    private static CutsceneExecutor executor;

    private CutsceneCameraHandler() {
        throw new UnsupportedOperationException("CutsceneCameraHandler 是纯静态门面类，不可实例化");
    }

    /**
     * 开始过场
     *
     * @param data 过场数据
     */
    public static void start(CutsceneData data) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            return;
        }
        List<CameraPos> positions = data.cameraPositions();
        if (positions.isEmpty()) {
            throw new IllegalStateException("过场动画至少需要一个相机关键帧");
        }

        try {
            // 创建或复用相机实体
            if (cameraEntity == null) {
                cameraEntity = new ClientCameraEntity(resolveCameraType(), mc.level);
            }

            Vec3 start = positions.get(0).pos();
            cameraEntity.setPos(start.x, start.y, start.z);
            cameraEntity.xo = start.x;
            cameraEntity.yo = start.y;
            cameraEntity.zo = start.z;

            executor = new CutsceneExecutor(data);
            mc.setCameraEntity(cameraEntity);
        } catch (Exception e) {
            // 过场启动失败：清理状态避免残留（保留 error 日志用于故障诊断）
            executor = null;
            cameraEntity = null;
            com.pasterdream.pasterdreammod.api.PasterDreamAPI.LOGGER.error(
                    "[CutsceneCameraHandler] 过场启动失败: {}", e.toString());
        }
    }

    /**
     * 解析相机实体类型。优先用已注册的 {@code client_camera} 类型；
     * 若未注册或取值异常（极端场景）则现场构建一个，保证过场可用。
     *
     * @return 相机实体类型
     */
    private static net.minecraft.world.entity.EntityType<?> resolveCameraType() {
        try {
            net.minecraft.world.entity.EntityType<?> registered =
                    com.pasterdream.pasterdreammod.api.effect.cutscene.CutsceneAPI.CLIENT_CAMERA.value();
            if (registered != null) {
                return registered;
            }
        } catch (Exception ignored) {
            // value() 抛异常（未注册等）：降级到现场构建
        }
        // 降级：现场构建（客户端专用，不依赖注册表）
        return net.minecraft.world.entity.EntityType.Builder
                .<net.minecraft.world.entity.Entity>of(
                        ClientCameraEntity::new,
                        net.minecraft.world.entity.MobCategory.MISC)
                .sized(0.1f, 0.1f)
                .noSummon()
                .noSave()
                .fireImmune()
                .build("client_camera");
    }

    /**
     * 是否过场激活
     *
     * @return 激活返回 {@code true}
     */
    public static boolean isActive() {
        return executor != null;
    }

    /**
     * 客户端每 tick 驱动过场（由 PDEffectClientEvents 调用）
     */
    public static void tick() {
        if (executor == null) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) {
            stop();
            return;
        }
        // 每 tick 确保相机实体接管（vanilla 可能重置 cameraEntity）
        ensureCameraEntity();
        // 接管输入
        nullifyInput(mc.player);
        if (mc.options.getCameraType() != CameraType.FIRST_PERSON) {
            mc.options.setCameraType(CameraType.FIRST_PERSON);
        }
        // 推进过场
        Vec3 pos = executor.tick();
        if (cameraEntity != null) {
            cameraEntity.setPos(pos);
            cameraEntity.xo = cameraEntity.getX();
            cameraEntity.yo = cameraEntity.getY();
            cameraEntity.zo = cameraEntity.getZ();
        }
        // 内嵌屏幕特效时间轴
        triggerScreenEffects(executor.getData(), executor.getCurrentTime());
        // 播完处理
        if (executor.hasEnded()) {
            CutsceneData next = executor.getData().nextCutscene();
            if (next != null) {
                start(next);
            } else if (executor.getData().stopMode() == CutsceneData.StopMode.AUTOMATIC) {
                stop();
            }
        }
    }

    /** 确保相机实体已被设为当前相机 */
    private static void ensureCameraEntity() {
        Minecraft mc = Minecraft.getInstance();
        if (cameraEntity != null
                && !(mc.cameraEntity instanceof ClientCameraEntity)) {
            mc.setCameraEntity(cameraEntity);
        }
    }

    /**
     * 计算当前相机朝向（由 PDEffectClientEvents 在 ViewportEvent.ComputeCameraAngles 调用）
     *
     * @param partialTick 部分 tick
     * @return 长度为 3 的数组 [yaw, pitch, roll]
     */
    public static float[] computeCameraAngles(float partialTick) {
        if (executor == null) {
            return null;
        }
        float[] look = executor.getLook(partialTick);
        return new float[]{look[0], look[1], 0.0f};
    }

    /**
     * 强制停止过场
     */
    public static void stop() {
        Minecraft mc = Minecraft.getInstance();
        if (executor != null) {
            executor = null;
            cameraEntity = null;
            resetMouseAccumulation(mc);
            if (mc.player != null) {
                mc.setCameraEntity(mc.player);
            }
        }
    }

    /**
     * 清空鼠标累积增量（过场结束防止视角跳变）。
     * <p>
     * {@code MouseHandler.accumulatedDX/accumulatedDY} 在部分映射下为
     * private 字段，故经反射置零，避免访问级别差异。
     *
     * @param mc Minecraft 实例
     */
    private static void resetMouseAccumulation(Minecraft mc) {
        try {
            java.lang.reflect.Field dx = net.minecraft.client.MouseHandler.class.getDeclaredField("accumulatedDX");
            java.lang.reflect.Field dy = net.minecraft.client.MouseHandler.class.getDeclaredField("accumulatedDY");
            dx.setAccessible(true);
            dy.setAccessible(true);
            dx.set(mc.mouseHandler, 0.0);
            dy.set(mc.mouseHandler, 0.0);
        } catch (ReflectiveOperationException ignored) {
            // 反射失败则跳过，不影响过场停止
        }
    }

    /**
     * 清空玩家输入
     *
     * @param player 本地玩家
     */
    public static void nullifyInput(LocalPlayer player) {
        net.minecraft.client.player.Input input = player.input;
        input.leftImpulse = 0;
        input.forwardImpulse = 0;
        input.up = false;
        input.down = false;
        input.left = false;
        input.right = false;
        input.jumping = false;
        input.shiftKeyDown = false;
    }

    /** 在指定 tick 触发内嵌屏幕特效 */
    private static void triggerScreenEffects(CutsceneData data, int tick) {
        List<CutsceneScreenEffectData.TimelineEffect> effects = data.screenEffects().get(tick);
        for (CutsceneScreenEffectData.TimelineEffect e : effects) {
            ScreenEffectType<?> type = ScreenEffectRegistry.get(
                    net.minecraft.resources.ResourceLocation.parse(e.typeId()));
            if (type == null) {
                continue;
            }
            ScreenEffectFactory<?> factory = ScreenEffectFactoryRegistry.get(type.id());
            if (factory == null) {
                continue;
            }
            ScreenEffectData dataObj = decodeEffectData(type, e.dataNbt());
            if (dataObj == null) {
                continue;
            }
            @SuppressWarnings("unchecked")
            ScreenEffect<?> effect =
                    (ScreenEffect<?>) factory.create(dataObj, e.inTime(), e.stayTime(), e.outTime());
            if (effect != null) {
                ScreenEffectOverlay.add(effect);
            }
        }
    }

    /** 从 NBT 字节解码特效数据（经类型 dataCodec） */
    private static ScreenEffectData decodeEffectData(ScreenEffectType<?> type, byte[] nbt) {
        io.netty.buffer.ByteBuf buf = io.netty.buffer.Unpooled.wrappedBuffer(nbt);
        try {
            @SuppressWarnings("unchecked")
            ScreenEffectData data =
                    (ScreenEffectData) type.dataCodec().decode(buf);
            return data;
        } catch (Exception e) {
            return null;
        } finally {
            buf.release();
        }
    }
}
