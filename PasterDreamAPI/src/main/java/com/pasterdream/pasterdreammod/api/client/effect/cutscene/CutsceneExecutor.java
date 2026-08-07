package com.pasterdream.pasterdreammod.api.client.effect.cutscene;

import com.pasterdream.pasterdreammod.api.client.effect.cutscene.motion.CameraMotion;
import com.pasterdream.pasterdreammod.api.client.effect.cutscene.motion.CatmullRomCameraMotion;
import com.pasterdream.pasterdreammod.api.client.effect.cutscene.motion.LinearCameraMotion;
import com.pasterdream.pasterdreammod.api.effect.cutscene.CameraPos;
import com.pasterdream.pasterdreammod.api.effect.cutscene.CutsceneData;
import com.pasterdream.pasterdreammod.api.effect.cutscene.CurveType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;

/**
 * 过场执行器 —— 计算相机位置与朝向
 * <p>
 * 借鉴开源模组 FDLib（作者 FINDERFEED）的 {@code CutsceneExecutor} 设计思路
 * （独立实现，非复制）。由 {@link CutsceneCameraHandler} 每 tick 驱动，
 * 按时间进度插值出相机位置与视角。
 */
@OnlyIn(Dist.CLIENT)
public class CutsceneExecutor {

    private final CutsceneData data;
    private final CameraMotion motion;
    private int currentTime;

    /**
     * 构造过场执行器
     *
     * @param data 过场数据
     */
    public CutsceneExecutor(CutsceneData data) {
        this.data = data;
        this.motion = data.moveCurveType() == CurveType.LINEAR
                ? new LinearCameraMotion() : new CatmullRomCameraMotion();
        this.currentTime = 0;
    }

    /**
     * 每 tick 推进并返回当前相机位置
     *
     * @return 当前世界坐标
     */
    public Vec3 tick() {
        Vec3 pos = motion.calculateCameraPosition(data, currentTime, 0);
        currentTime = Mth.clamp(currentTime + 1, 0, data.cutsceneTime());
        return pos;
    }

    /**
     * 获取当前相机位置（含部分 tick 插值）
     *
     * @param partialTick 部分 tick
     * @return 世界坐标
     */
    public Vec3 getCameraPos(float partialTick) {
        return motion.calculateCameraPosition(data, currentTime, partialTick);
    }

    /**
     * 计算当前朝向（插值关键帧 yaw/pitch）
     *
     * @param partialTick 部分 tick
     * @return [yaw, pitch]（度）
     */
    public float[] getLook(float partialTick) {
        List<CameraPos> positions = data.cameraPositions();
        if (positions.isEmpty()) {
            return new float[]{0, 0};
        }
        int total = data.cutsceneTime();
        float p = data.lookEasing().apply((currentTime + partialTick) / (float) total);
        float scaled = p * (positions.size() - 1);
        int index = Math.min((int) scaled, positions.size() - 1);
        float local = scaled - index;
        CameraPos from = positions.get(index);
        CameraPos to = positions.get(Math.min(index + 1, positions.size() - 1));
        float yaw = lerpAngle(from.yaw(), to.yaw(), local);
        float pitch = Mth.lerp(local, from.pitch(), to.pitch());
        return new float[]{yaw, pitch};
    }

    /**
     * 获取当前运行 tick
     *
     * @return 当前 tick
     */
    public int getCurrentTime() {
        return currentTime;
    }

    /**
     * 是否已播完
     *
     * @return 播完返回 {@code true}
     */
    public boolean hasEnded() {
        return currentTime >= data.cutsceneTime();
    }

    /**
     * 获取过场数据
     *
     * @return 数据
     */
    public CutsceneData getData() {
        return data;
    }

    /** 角度线性插值（处理 360 环绕） */
    private float lerpAngle(float from, float to, float p) {
        float delta = Mth.wrapDegrees(to - from);
        return from + delta * p;
    }
}
