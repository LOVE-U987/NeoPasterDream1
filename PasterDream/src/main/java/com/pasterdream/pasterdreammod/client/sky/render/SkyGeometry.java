package com.pasterdream.pasterdreammod.client.sky.render;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.pasterdream.pasterdreammod.client.sky.math.SkyColor;
import com.pasterdream.pasterdreammod.client.sky.math.SkyPoint;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

/**
 * 天空几何工具 —— 提供天空球面坐标换算与广告牌/线段绘制
 * <p>
 * 所有天体定位在半径 {@link #SKY_RADIUS} 的球面上：
 * {@code point(yaw, pitch) = (sin(yaw)*cos(pitch), sin(pitch), cos(yaw)*cos(pitch)) * SKY_RADIUS}
 */
public final class SkyGeometry {

    /** 天空球半径（与原版星空半径一致，可直接替换原版星盘） */
    public static final float SKY_RADIUS = 100.0F;

    private SkyGeometry() {
    }

    /**
     * 安全提交顶点缓冲 —— 空缓冲（没有任何顶点）时静默跳过
     * <p>
     * ⚠️ {@code buildOrThrow()} 在缓冲为空时抛
     * {@code IllegalStateException("BufferBuilder was empty")}，会直接崩溃游戏。
     * 天空内容的数据（如玩家创建的连线星体只有 1 颗、某纹理帧没有星星、
     * 两点重合导致线段被跳过等）可能让缓冲为空，统一用本方法防御。
     *
     * @param buffer 已写入顶点的缓冲
     */
    public static void drawIfNotEmpty(BufferBuilder buffer) {
        MeshData mesh = buffer.build();
        if (mesh != null) {
            BufferUploader.drawWithShader(mesh);
        }
    }

    /**
     * 将偏航角与俯仰角换算为球面坐标
     *
     * @param yaw   偏航角（弧度）
     * @param pitch 俯仰角（弧度，正值为上方）
     * @return 球面上的点
     */
    public static SkyPoint point(float yaw, float pitch) {
        float horizontal = Mth.cos(pitch);
        return new SkyPoint(
                Mth.sin(yaw) * horizontal * SKY_RADIUS,
                Mth.sin(pitch) * SKY_RADIUS,
                Mth.cos(yaw) * horizontal * SKY_RADIUS
        );
    }

    /**
     * 添加一个无纹理广告牌四边形（面向球心）
     *
     * @param buffer 顶点缓冲
     * @param matrix 变换矩阵
     * @param center 广告牌中心（球面上）
     * @param size   半边长
     * @param angle  自旋角（弧度）
     * @param color  颜色
     * @param alpha  透明度
     */
    public static void addBillboard(BufferBuilder buffer, Matrix4f matrix, SkyPoint center, float size, float angle, SkyColor color, float alpha) {
        addBillboard(buffer, matrix, center, size, angle, color, alpha, false, true);
    }

    /**
     * 添加一个带纹理广告牌四边形（面向球心）
     * <p>
     * 仅写入 UV，不写顶点颜色（用于 {@code POSITION_TEX} 格式，
     * 避免 {@code getParticleShader} 在 Iris 光影下把顶点色×光照变黑）。
     *
     * @param buffer 顶点缓冲（POSITION_TEX 格式）
     * @param matrix 变换矩阵
     * @param center 广告牌中心（球面上）
     * @param size   半边长
     * @param angle  自旋角（弧度）
     */
    public static void addTexturedBillboard(
            BufferBuilder buffer, Matrix4f matrix, SkyPoint center, float size, float angle
    ) {
        addBillboard(buffer, matrix, center, size, angle, null, 1.0F, true, false);
    }

    /**
     * 添加一个带纹理广告牌四边形（面向球心，含顶点色）
     *
     * @param buffer 顶点缓冲
     * @param matrix 变换矩阵
     * @param center 广告牌中心（球面上）
     * @param size   半边长
     * @param angle  自旋角（弧度）
     * @param color  颜色
     * @param alpha  透明度
     */
    public static void addTexturedBillboard(
            BufferBuilder buffer, Matrix4f matrix, SkyPoint center, float size, float angle, SkyColor color, float alpha
    ) {
        addBillboard(buffer, matrix, center, size, angle, color, alpha, true, true);
    }

    /**
     * 添加一个广告牌四边形（纹理可选）
     * <p>
     * 广告牌朝向：以球面法线为轴构建局部坐标系（yaw/pitch），
     * 四边形在其切线平面内按 {@code angle} 自旋。
     *
     * @param buffer       顶点缓冲
     * @param matrix       变换矩阵
     * @param center       广告牌中心
     * @param size         半边长
     * @param angle        自旋角
     * @param color        颜色（null 则不写颜色）
     * @param alpha        透明度
     * @param textured     是否写入 UV
     * @param writeColor   是否写入顶点颜色
     */
    private static void addBillboard(
            BufferBuilder buffer, Matrix4f matrix, SkyPoint center, float size, float angle, SkyColor color, float alpha, boolean textured, boolean writeColor
    ) {
        float unitX = center.x() / SKY_RADIUS;
        float unitY = center.y() / SKY_RADIUS;
        float unitZ = center.z() / SKY_RADIUS;
        float yaw = (float) Math.atan2(unitX, unitZ);
        float sinYaw = Mth.sin(yaw);
        float cosYaw = Mth.cos(yaw);
        float pitch = (float) Math.atan2(Mth.sqrt(unitX * unitX + unitZ * unitZ), unitY);
        float sinPitch = Mth.sin(pitch);
        float cosPitch = Mth.cos(pitch);
        float sinAngle = Mth.sin(angle);
        float cosAngle = Mth.cos(angle);

        for (int index = 0; index < 4; index++) {
            float x = ((index & 2) - 1.0F) * size;
            float y = ((index + 1 & 2) - 1.0F) * size;
            float rotatedX = x * cosAngle - y * sinAngle;
            float rotatedY = y * cosAngle + x * sinAngle;
            float dy = rotatedX * sinPitch;
            float ae = -rotatedX * cosPitch;
            float dx = ae * sinYaw - rotatedY * cosYaw;
            float dz = rotatedY * sinYaw + ae * cosYaw;
            VertexConsumer vertex = buffer.addVertex(matrix, center.x() + dx, center.y() + dy, center.z() + dz);
            if (textured) {
                vertex.setUv(index >> 1 & 1, index + 1 >> 1 & 1);
            }
            if (writeColor) {
                vertex.setColor(color.red(), color.green(), color.blue(), alpha);
            }
        }
    }

    /**
     * 添加一条宽线段（星座连线等用）
     * <p>
     * 线段宽度方向取中点叉积法线，绘制为矩形 QUAD。
     *
     * @param buffer 顶点缓冲
     * @param matrix 变换矩阵
     * @param from   起点
     * @param to     终点
     * @param width  线段宽度
     * @param color  颜色
     * @param alpha  透明度
     */
    public static void addLine(BufferBuilder buffer, Matrix4f matrix, SkyPoint from, SkyPoint to, float width, SkyColor color, float alpha) {
        float dx = to.x() - from.x();
        float dy = to.y() - from.y();
        float dz = to.z() - from.z();
        float midX = from.x() + to.x();
        float midY = from.y() + to.y();
        float midZ = from.z() + to.z();
        float sideX = dy * midZ - dz * midY;
        float sideY = dz * midX - dx * midZ;
        float sideZ = dx * midY - dy * midX;
        float sideLength = Mth.sqrt(sideX * sideX + sideY * sideY + sideZ * sideZ);
        if (sideLength < 0.001F) {
            return;
        }
        sideX = sideX / sideLength * width;
        sideY = sideY / sideLength * width;
        sideZ = sideZ / sideLength * width;
        buffer.addVertex(matrix, from.x() + sideX, from.y() + sideY, from.z() + sideZ)
                .setColor(color.red(), color.green(), color.blue(), alpha);
        buffer.addVertex(matrix, to.x() + sideX, to.y() + sideY, to.z() + sideZ)
                .setColor(color.red(), color.green(), color.blue(), alpha);
        buffer.addVertex(matrix, to.x() - sideX, to.y() - sideY, to.z() - sideZ)
                .setColor(color.red(), color.green(), color.blue(), alpha);
        buffer.addVertex(matrix, from.x() - sideX, from.y() - sideY, from.z() - sideZ)
                .setColor(color.red(), color.green(), color.blue(), alpha);
    }
}
