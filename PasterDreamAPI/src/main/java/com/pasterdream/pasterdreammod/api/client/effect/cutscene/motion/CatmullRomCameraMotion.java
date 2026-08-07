package com.pasterdream.pasterdreammod.api.client.effect.cutscene.motion;

import com.pasterdream.pasterdreammod.api.effect.cutscene.CameraPos;
import com.pasterdream.pasterdreammod.api.effect.cutscene.CutsceneData;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;

/**
 * Catmull-Rom 样条相机运动 —— 关键帧之间平滑曲线插值
 * <p>
 * 借鉴开源模组 FDLib（作者 FINDERFEED）的 {@code CatmullRomCameraMotion} 设计思路
 * （独立实现，非复制）。利用前后各一个关键帧作为控制点做样条插值，
 * 路径平滑过弯。
 */
@OnlyIn(Dist.CLIENT)
public class CatmullRomCameraMotion extends CameraMotion {

    @Override
    public Vec3 calculateCameraPosition(CutsceneData data, int currentTime, float partialTick) {
        List<CameraPos> positions = data.cameraPositions();
        if (positions.isEmpty()) {
            throw new IllegalStateException("过场动画至少需要一个相机关键帧");
        }
        int total = data.cutsceneTime();
        float p = data.timeEasing().apply((currentTime + partialTick) / (float) total);
        float scaled = p * (positions.size() - 1);
        int index = Math.min((int) scaled, positions.size() - 1);
        float local = scaled - index;

        Vec3 p0 = pos(positions, index - 1);
        Vec3 p1 = pos(positions, index);
        Vec3 p2 = pos(positions, index + 1);
        Vec3 p3 = pos(positions, index + 2);

        return catmullRom(p0, p1, p2, p3, local);
    }

    /** 安全取关键帧位置（越界返回 null，样条按 null 处理） */
    private Vec3 pos(List<CameraPos> positions, int index) {
        if (index < 0 || index >= positions.size()) {
            return null;
        }
        return positions.get(index).pos();
    }

    /**
     * Catmull-Rom 样条插值（支持端点 null 降级为线性）
     */
    private Vec3 catmullRom(Vec3 p0, Vec3 p1, Vec3 p2, Vec3 p3, float t) {
        if (p0 == null || p3 == null) {
            // 端点缺失时退化为 p1→p2 线性插值
            return p1.lerp(p2, t);
        }
        double t2 = t * t;
        double t3 = t2 * t;
        return new Vec3(
                catmullRomAxis(p0.x, p1.x, p2.x, p3.x, t, t2, t3),
                catmullRomAxis(p0.y, p1.y, p2.y, p3.y, t, t2, t3),
                catmullRomAxis(p0.z, p1.z, p2.z, p3.z, t, t2, t3)
        );
    }

    private double catmullRomAxis(double a, double b, double c, double d, double t, double t2, double t3) {
        return 0.5 * ((2 * b) + (-a + c) * t + (2 * a - 5 * b + 4 * c - d) * t2
                + (-a + 3 * b - 3 * c + d) * t3);
    }
}
