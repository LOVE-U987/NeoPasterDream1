package com.pasterdream.pasterdreammod.api.client.effect.cutscene.motion;

import com.pasterdream.pasterdreammod.api.effect.cutscene.CutsceneData;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * 相机运动策略 —— 计算指定时刻的相机位置
 * <p>
 * 借鉴开源模组 FDLib（作者 FINDERFEED）的 {@code CameraMotion} 设计思路
 * （独立实现，非复制）。
 */
@OnlyIn(Dist.CLIENT)
public abstract class CameraMotion {

    /**
     * 计算相机位置
     *
     * @param data        过场数据（含路径关键帧）
     * @param currentTime 当前 tick
     * @param partialTick 部分 tick
     * @return 相机世界坐标
     */
    public abstract Vec3 calculateCameraPosition(CutsceneData data, int currentTime, float partialTick);
}
