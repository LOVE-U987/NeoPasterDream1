package com.pasterdream.pasterdreammod.api.client.effect.cutscene.motion;

import com.pasterdream.pasterdreammod.api.effect.cutscene.CameraPos;
import com.pasterdream.pasterdreammod.api.effect.cutscene.CutsceneData;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * 线性相机运动 —— 关键帧之间直线插值
 * <p>
 * 借鉴开源模组 FDLib（作者 FINDERFEED）的 {@code LinearCameraMotion} 设计思路
 * （独立实现，非复制）。
 */
@OnlyIn(Dist.CLIENT)
public class LinearCameraMotion extends CameraMotion {

    @Override
    public Vec3 calculateCameraPosition(CutsceneData data, int currentTime, float partialTick) {
        java.util.List<CameraPos> positions = data.cameraPositions();
        if (positions.isEmpty()) {
            throw new IllegalStateException("过场动画至少需要一个相机关键帧");
        }
        int total = data.cutsceneTime();
        float p = data.timeEasing().apply((currentTime + partialTick) / (float) total);
        float scaled = p * (positions.size() - 1);
        int index = Math.min((int) scaled, positions.size() - 1);
        float local = scaled - index;
        CameraPos from = positions.get(index);
        CameraPos to = positions.get(Math.min(index + 1, positions.size() - 1));
        return from.pos().lerp(to.pos(), local);
    }
}
