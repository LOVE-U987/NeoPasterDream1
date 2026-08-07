package com.pasterdream.pasterdreammod.api.effect.cutscene;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

/**
 * 相机位置点 —— 过场动画路径上的一个关键帧
 * <p>
 * 借鉴开源模组 FDLib（作者 FINDERFEED）的 {@code CameraPos} 设计思路
 * （独立实现，非复制）。包含位置与朝向（yaw/pitch/roll），可经
 * {@link #of(Vec3, Vec3)} 由位置 + 视线方向便捷构造。
 *
 * @param pos   世界坐标
 * @param yaw   偏航角（度，Minecraft 约定）
 * @param pitch 俯仰角（度，限制 ±90）
 * @param roll  翻滚角（度）
 */
public record CameraPos(Vec3 pos, float yaw, float pitch, float roll) {

    /**
     * 由位置与视线方向构造相机点
     *
     * @param pos         相机位置
     * @param lookDirection 视线方向（指向目标）
     * @return 相机点
     */
    public static CameraPos of(Vec3 pos, Vec3 lookDirection) {
        Vec3 dir = lookDirection.normalize();
        float yaw = (float) (Math.toDegrees(Math.atan2(-dir.x, dir.z)));
        float pitch = (float) (Math.toDegrees(Math.asin(dir.y)));
        return new CameraPos(pos, yaw, Mth.clamp(pitch, -90, 90), 0.0f);
    }

    /**
     * 直接构造相机点
     *
     * @param pos   位置
     * @param yaw   偏航角
     * @param pitch 俯仰角
     * @return 相机点
     */
    public static CameraPos of(Vec3 pos, float yaw, float pitch) {
        return new CameraPos(pos, yaw, Mth.clamp(pitch, -90, 90), 0.0f);
    }
}
