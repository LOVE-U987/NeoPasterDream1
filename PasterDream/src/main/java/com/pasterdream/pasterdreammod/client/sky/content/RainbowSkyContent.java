package com.pasterdream.pasterdreammod.client.sky.content;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import com.pasterdream.pasterdreammod.api.client.sky.SkyContent;
import com.pasterdream.pasterdreammod.api.client.sky.SkyboxRenderContext;
import com.pasterdream.pasterdreammod.client.sky.math.SkyColor;
import com.pasterdream.pasterdreammod.client.sky.math.SkyPoint;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

import java.util.List;

/**
 * 彩虹内容 —— 天空中的彩色半圆弧
 * <p>
 * 按径向（颜色渐变）与环向（弧段）双轴细分三角网格，
 * 两端 edgeFade 淡出、径向 blur 柔化。覆写 {@link #targetAlpha}
 * 实现"白天才显示"（夜晚消失）。
 */
public class RainbowSkyContent implements SkyContent {

    private final ResourceLocation id;
    private final int priority;
    private final List<SkyColor> colors;
    private final float yaw;
    private final float basePitch;
    private final float radius;
    private final float thickness;
    private final int segments;
    private final float arc;
    private final float opacity;
    private final float blur;

    /**
     * @param id        内容标识
     * @param priority  绘制优先级
     * @param colors    颜色列表（径向渐变）
     * @param yaw       中心偏航角（弧度）
     * @param basePitch 基线俯仰角
     * @param radius    外半径
     * @param thickness 厚度（外半径 - 内半径）
     * @param segments  环向分段
     * @param arc       弧长（弧度）
     * @param opacity   不透明度
     * @param blur      径向柔化
     */
    public RainbowSkyContent(
            ResourceLocation id, int priority, List<SkyColor> colors,
            float yaw, float basePitch, float radius, float thickness,
            int segments, float arc, float opacity, float blur
    ) {
        this.id = id;
        this.priority = priority;
        this.colors = List.copyOf(colors);
        this.yaw = yaw;
        this.basePitch = basePitch;
        this.radius = radius;
        this.thickness = thickness;
        this.segments = Math.max(8, segments);
        this.arc = arc;
        this.opacity = opacity;
        this.blur = Math.max(0.0F, blur);
    }

    @Override
    public ResourceLocation id() {
        return this.id;
    }

    @Override
    public int priority() {
        return this.priority;
    }

    @Override
    public float targetAlpha(SkyboxRenderContext context) {
        // 白天显示、夜晚消失
        return Mth.clamp((1.0F - context.nightFactor()) * context.weatherFactor(), 0.0F, 1.0F);
    }

    @Override
    public void render(SkyboxRenderContext context, float alpha) {
        if (this.colors.isEmpty()) {
            return;
        }
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.disableCull();
        context.poseStack().pushPose();
        // 反旋天空角，彩虹固定在世界方向
        context.poseStack().mulPose(Axis.YP.rotationDegrees(-context.skyAngle()));
        Matrix4f matrix = context.poseStack().last().pose();
        float rainbowAlpha = alpha * this.opacity;
        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        renderGradient(buffer, matrix, this.radius, this.radius - this.thickness, rainbowAlpha);
        BufferUploader.drawWithShader(buffer.buildOrThrow());
        context.poseStack().popPose();
        RenderSystem.enableCull();
    }

    /**
     * 渲染彩虹渐变带（径向 × 环向双轴网格）
     *
     * @param buffer      顶点缓冲
     * @param matrix      变换矩阵
     * @param outerRadius 外半径
     * @param innerRadius 内半径
     * @param alpha       透明度
     */
    private void renderGradient(BufferBuilder buffer, Matrix4f matrix, float outerRadius, float innerRadius, float alpha) {
        int radialSteps = Math.max(24, this.colors.size() * 12);

        for (int radial = 0; radial < radialSteps; radial++) {
            float outer = (float) radial / radialSteps;
            float inner = (radial + 1.0F) / radialSteps;
            float outerArcRadius = Mth.lerp(outer, outerRadius, innerRadius);
            float innerArcRadius = Mth.lerp(inner, outerRadius, innerRadius);
            SkyColor outerColor = this.radialColor(outer);
            SkyColor innerColor = this.radialColor(inner);
            float outerRadialAlpha = this.radialAlpha(outer);
            float innerRadialAlpha = this.radialAlpha(inner);

            for (int segment = 0; segment < this.segments; segment++) {
                float left = (float) segment / this.segments;
                float right = (segment + 1.0F) / this.segments;
                float leftArcAlpha = alpha * edgeFade(left);
                float rightArcAlpha = alpha * edgeFade(right);
                addBand(
                        buffer, matrix,
                        this.point(left, outerArcRadius),
                        this.point(left, innerArcRadius),
                        this.point(right, innerArcRadius),
                        this.point(right, outerArcRadius),
                        outerColor, innerColor,
                        leftArcAlpha * outerRadialAlpha,
                        leftArcAlpha * innerRadialAlpha,
                        rightArcAlpha * innerRadialAlpha,
                        rightArcAlpha * outerRadialAlpha
                );
            }
        }
    }

    /**
     * 彩虹弧上的点（球面坐标）
     *
     * @param progress  环向进度 0~1
     * @param arcRadius 弧半径
     * @return 球面坐标
     */
    private SkyPoint point(float progress, float arcRadius) {
        float angle = (progress - 0.5F) * this.arc;
        float horizontal = Mth.sin(angle) * arcRadius;
        float vertical = this.basePitch + Mth.cos(angle) * arcRadius;
        float normalX = Mth.sin(this.yaw);
        float normalZ = Mth.cos(this.yaw);
        float sideX = Mth.cos(this.yaw);
        float sideZ = -Mth.sin(this.yaw);
        float x = normalX + sideX * horizontal;
        float z = normalZ + sideZ * horizontal;
        float scale = 100.0F / Mth.sqrt(x * x + vertical * vertical + z * z);
        return new SkyPoint(x * scale, vertical * scale, z * scale);
    }

    /**
     * 环向两端淡出
     *
     * @param progress 进度
     * @return 0~1
     */
    private static float edgeFade(float progress) {
        return Mth.clamp(Mth.sin(progress * 3.1415927F) * 1.35F, 0.0F, 1.0F);
    }

    /**
     * 径向柔化（高斯式）
     *
     * @param progress 径向进度
     * @return 0~1
     */
    private float radialAlpha(float progress) {
        if (this.blur <= 0.0F) {
            return 1.0F;
        }
        float fadeWidth = Mth.clamp(this.blur * 0.5F, 0.001F, 0.5F);
        float edgeProgress = Mth.clamp(Math.min(progress, 1.0F - progress) / fadeWidth, 0.0F, 1.0F);
        return Mth.sin(edgeProgress * 1.5707964F);
    }

    /**
     * 径向颜色插值
     *
     * @param progress 径向进度 0~1
     * @return 颜色
     */
    private SkyColor color(float progress) {
        if (this.colors.size() == 1) {
            return this.colors.get(0);
        }
        float scaled = Mth.clamp(progress, 0.0F, 1.0F) * (this.colors.size() - 1);
        int index = Mth.clamp((int) Math.floor(scaled), 0, this.colors.size() - 2);
        float blend = scaled - index;
        return this.colors.get(index).lerp(this.colors.get(index + 1), blend);
    }

    /**
     * 径向取色（含 blur 模式）
     *
     * @param progress 径向进度
     * @return 颜色
     */
    private SkyColor radialColor(float progress) {
        if (this.blur <= 0.0F) {
            return this.colors.get(Mth.clamp(
                    (int) Math.floor(Mth.clamp(progress, 0.0F, 0.999999F) * this.colors.size()), 0, this.colors.size() - 1
            ));
        }
        return this.color(progress);
    }

    /**
     * 添加一个色带面片（QUAD）
     *
     * @param buffer          顶点缓冲
     * @param matrix          变换矩阵
     * @param outerLeft       外左
     * @param innerLeft       内左
     * @param innerRight      内右
     * @param outerRight      外右
     * @param outerColor      外缘颜色
     * @param innerColor      内缘颜色
     * @param outerLeftAlpha  外左透明度
     * @param innerLeftAlpha  内左透明度
     * @param innerRightAlpha 内右透明度
     * @param outerRightAlpha 外右透明度
     */
    private static void addBand(
            BufferBuilder buffer, Matrix4f matrix,
            SkyPoint outerLeft, SkyPoint innerLeft, SkyPoint innerRight, SkyPoint outerRight,
            SkyColor outerColor, SkyColor innerColor,
            float outerLeftAlpha, float innerLeftAlpha, float innerRightAlpha, float outerRightAlpha
    ) {
        buffer.addVertex(matrix, outerLeft.x(), outerLeft.y(), outerLeft.z())
                .setColor(outerColor.red(), outerColor.green(), outerColor.blue(), outerLeftAlpha);
        buffer.addVertex(matrix, innerLeft.x(), innerLeft.y(), innerLeft.z())
                .setColor(innerColor.red(), innerColor.green(), innerColor.blue(), innerLeftAlpha);
        buffer.addVertex(matrix, innerRight.x(), innerRight.y(), innerRight.z())
                .setColor(innerColor.red(), innerColor.green(), innerColor.blue(), innerRightAlpha);
        buffer.addVertex(matrix, outerRight.x(), outerRight.y(), outerRight.z())
                .setColor(outerColor.red(), outerColor.green(), outerColor.blue(), outerRightAlpha);
    }
}
