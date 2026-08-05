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
 * 极光内容 —— 多层幕帘式程序化极光
 * <p>
 * 每层幕帘为横向 segments × 纵向 gradient_steps 的三角网格，
 * 顶点位置由多频正弦波叠加（波浪/深度/水平/基线/高度/密度/射线），
 * 颜色沿纵向渐变、横向淡出，整体缓慢脉动。
 */
public class AuroraSkyContent implements SkyContent {

    /** 极光内容标识 */
    private final ResourceLocation id;
    private final int priority;
    private final List<SkyColor> colors;
    private final int bands;
    private final int segments;
    private final int gradientSteps;
    private final float waveAmplitude;
    private final float waveFrequency;
    private final float speed;
    private final float opacity;
    private final float centerYaw;
    private final float width;
    private final float minPitch;
    private final float maxPitch;
    private final float rayStrength;
    private final float edgeSoftness;
    private final float depthAmplitude;
    private final float depthOffset;
    private final float sphereRadius;

    /**
     * @param id             内容标识
     * @param priority       绘制优先级
     * @param colors         极光颜色列表（纵向渐变）
     * @param bands          幕帘层数
     * @param segments       横向分段
     * @param gradientSteps  纵向分段
     * @param waveAmplitude  波浪振幅
     * @param waveFrequency  波浪频率
     * @param speed          动画速度
     * @param opacity        不透明度
     * @param centerYaw      中心偏航角（弧度）
     * @param width          幕帘宽度
     * @param minPitch       底部俯仰角
     * @param maxPitch       顶部俯仰角
     * @param rayStrength    射线感强度
     * @param edgeSoftness   横向边缘柔化
     * @param depthAmplitude 深度起伏振幅
     * @param depthOffset    深度偏移
     * @param sphereRadius   球半径
     */
    public AuroraSkyContent(
            ResourceLocation id, int priority, List<SkyColor> colors,
            int bands, int segments, int gradientSteps,
            float waveAmplitude, float waveFrequency, float speed, float opacity,
            float centerYaw, float width, float minPitch, float maxPitch,
            float rayStrength, float edgeSoftness, float depthAmplitude, float depthOffset,
            float sphereRadius
    ) {
        this.id = id;
        this.priority = priority;
        this.colors = List.copyOf(colors);
        this.bands = bands;
        this.segments = segments;
        this.gradientSteps = Math.max(1, gradientSteps);
        this.waveAmplitude = waveAmplitude;
        this.waveFrequency = waveFrequency;
        this.speed = speed;
        this.opacity = opacity;
        this.centerYaw = centerYaw;
        this.width = width;
        this.minPitch = minPitch;
        this.maxPitch = maxPitch;
        this.rayStrength = rayStrength;
        this.edgeSoftness = edgeSoftness;
        this.depthAmplitude = depthAmplitude;
        this.depthOffset = depthOffset;
        this.sphereRadius = Math.max(1.0F, sphereRadius);
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
    public void render(SkyboxRenderContext context, float alpha) {
        // 与 Stellara 一致：不设置混合模式，依赖外层 SkyboxRenderer 统一的标准混合
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.disableCull();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        context.poseStack().pushPose();
        // 反旋天空角，极光固定在世界方向
        context.poseStack().mulPose(Axis.YP.rotationDegrees(-context.skyAngle()));
        Matrix4f matrix = context.poseStack().last().pose();
        float time = context.renderTime() * this.speed;
        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_COLOR);
        renderCurtains(buffer, matrix, time, alpha * this.opacity);
        BufferUploader.drawWithShader(buffer.buildOrThrow());
        context.poseStack().popPose();
        RenderSystem.enableCull();
    }

    /**
     * 渲染全部幕帘层
     *
     * @param buffer 顶点缓冲
     * @param matrix 变换矩阵
     * @param time   动画时间
     * @param alpha  整体透明度
     */
    private void renderCurtains(BufferBuilder buffer, Matrix4f matrix, float time, float alpha) {
        for (int band = 0; band < this.bands; band++) {
            float bandProgress = this.bands <= 1 ? 0.5F : (float) band / (this.bands - 1);
            float bandWidth = this.width;
            float bandDepthOffset = (bandProgress - 0.5F) * this.width * 0.035F;
            float bandVerticalOffset = (bandProgress - 0.5F) * 0.08F;
            float bandMinPitch = this.minPitch + bandVerticalOffset;
            float bandMaxPitch = this.maxPitch + bandVerticalOffset;
            float bandAlpha = alpha * this.opacity * Mth.lerp(0.5F + 0.5F * Mth.sin(time * 0.7F + band * 1.37F), 0.58F, 1.0F);
            float xStart = -bandWidth * 0.5F;
            float xEnd = bandWidth * 0.5F;

            for (int segment = 0; segment < this.segments; segment++) {
                float left = (float) segment / this.segments;
                float right = (segment + 1.0F) / this.segments;
                float xLeft = Mth.lerp(left, xStart, xEnd);
                float xRight = Mth.lerp(right, xStart, xEnd);
                float leftRay = ray(left, band, time);
                float rightRay = ray(right, band, time);
                float leftDensity = densityWave(left, band, time);
                float rightDensity = densityWave(right, band, time);
                float leftHeight = heightWave(left, band, time);
                float rightHeight = heightWave(right, band, time);
                float leftBaseline = baselineWave(left, band, time);
                float rightBaseline = baselineWave(right, band, time);
                float leftWave = depthWave(left, band, time) * this.depthAmplitude;
                float rightWave = depthWave(right, band, time) * this.depthAmplitude;
                float leftDepth = leftWave * 0.55F + bandDepthOffset;
                float rightDepth = rightWave * 0.55F + bandDepthOffset;
                float leftHorizontal = xLeft + leftWave * 0.22F + leftBaseline * this.width * 0.34F + horizontalWave(left, band, time) * this.width * 0.42F;
                float rightHorizontal = xRight + rightWave * 0.22F + rightBaseline * this.width * 0.34F + horizontalWave(right, band, time) * this.width * 0.42F;
                float leftBottom = clampPitch(bandMinPitch + wave(left, band, time + 2.1F) * 1.25F + leftBaseline * 0.12F - leftDensity * 0.025F);
                float rightBottom = clampPitch(bandMinPitch + wave(right, band, time + 2.1F) * 1.25F + rightBaseline * 0.12F - rightDensity * 0.025F);
                float leftTop = clampPitch(bandMaxPitch + wave(left, band, time) * 1.25F + leftRay * (0.08F + leftHeight * 0.22F) + leftHeight * 0.16F);
                float rightTop = clampPitch(bandMaxPitch + wave(right, band, time) * 1.25F + rightRay * (0.08F + rightHeight * 0.22F) + rightHeight * 0.16F);
                float horizontalFade = horizontalFade(left) * horizontalFade(right);

                for (int step = 0; step < this.gradientSteps; step++) {
                    float bottom = (float) step / this.gradientSteps;
                    float top = (step + 1.0F) / this.gradientSteps;
                    float lowerAlpha = bandAlpha * horizontalFade * gradientAlpha(bottom)
                            * Mth.lerp(leftRay, 1.0F - this.rayStrength, 1.0F) * leftDensity;
                    float upperAlpha = bandAlpha * horizontalFade * gradientAlpha(top)
                            * Mth.lerp(rightRay, 1.0F - this.rayStrength, 1.0F) * rightDensity;
                    addCurtain(
                            buffer, matrix,
                            curtainPoint(leftHorizontal, Mth.lerp(bottom, leftBottom, leftTop), leftDepth, bottom),
                            curtainPoint(leftHorizontal, Mth.lerp(top, leftBottom, leftTop), leftDepth, top),
                            curtainPoint(rightHorizontal, Mth.lerp(top, rightBottom, rightTop), rightDepth, top),
                            curtainPoint(rightHorizontal, Mth.lerp(bottom, rightBottom, rightTop), rightDepth, bottom),
                            color(bottom), color(top), lowerAlpha, upperAlpha
                    );
                }
            }
        }
    }

    /**
     * 幕帘顶点坐标（含深度起伏）
     *
     * @param horizontal      横向坐标
     * @param pitch           俯仰角
     * @param depth           深度起伏
     * @param verticalProgress 纵向进度（未直接使用，保留接口）
     * @return 球面坐标
     */
    private SkyPoint curtainPoint(float horizontal, float pitch, float depth, float verticalProgress) {
        float layerRadius = 100.0F * (1.0F + this.depthOffset);
        float radius = layerRadius * Math.max(0.05F, 1.0F + depth * 0.18F);
        float yaw = this.centerYaw + horizontal / this.sphereRadius;
        float horizontalRadius = Mth.cos(clampPitch(pitch)) * radius;
        return new SkyPoint(
                Mth.sin(yaw) * horizontalRadius,
                Mth.sin(clampPitch(pitch)) * radius,
                Mth.cos(yaw) * horizontalRadius
        );
    }

    /**
     * 主波浪（双频正弦叠加）
     *
     * @param progress 横向进度
     * @param band     层索引
     * @param time     时间
     * @return 波浪值
     */
    private float wave(float progress, int band, float time) {
        return Mth.sin(progress * 6.2831855F * this.waveFrequency + time + band * 1.9F) * this.waveAmplitude
                + Mth.sin(progress * 6.2831855F * (this.waveFrequency * 0.47F) - time * 0.63F + band) * this.waveAmplitude * 0.45F;
    }

    /**
     * 深度起伏波浪
     *
     * @param progress 横向进度
     * @param band     层索引
     * @param time     时间
     * @return 深度波浪值
     */
    private float depthWave(float progress, int band, float time) {
        float first = Mth.sin(progress * 6.2831855F * (this.waveFrequency * 0.22F) + time * 0.42F + band * 0.7F);
        float second = Mth.sin(progress * 6.2831855F * (this.waveFrequency * 0.11F) - time * 0.25F + band * 1.3F);
        return (first * 0.72F + second * 0.28F) * this.waveAmplitude;
    }

    /**
     * 水平晃动波浪
     *
     * @param progress 横向进度
     * @param band     层索引
     * @param time     时间
     * @return 水平波浪值
     */
    private float horizontalWave(float progress, int band, float time) {
        float first = Mth.sin(progress * 3.1415927F * 4.0F + time * 0.9F + band * 2.1F);
        float second = Mth.sin(progress * 3.1415927F * 2.0F - time * 0.58F + band * 1.3F);
        return (first * 0.65F + second * 0.35F) * this.waveAmplitude;
    }

    /**
     * 基线波浪
     *
     * @param progress 横向进度
     * @param band     层索引
     * @param time     时间
     * @return 基线波浪值
     */
    private float baselineWave(float progress, int band, float time) {
        float first = Mth.sin(progress * 6.2831855F * 1.15F + time * 0.32F + band * 1.9F);
        float second = Mth.sin(progress * 6.2831855F * 2.1F - time * 0.21F + band * 0.6F);
        return (first * 0.68F + second * 0.32F) * this.waveAmplitude;
    }

    /**
     * 高度波浪（0~1）
     *
     * @param progress 横向进度
     * @param band     层索引
     * @param time     时间
     * @return 高度值 0~1
     */
    private float heightWave(float progress, int band, float time) {
        float first = Mth.sin(progress * 6.2831855F * 4.3F - time * 0.46F + band * 2.4F);
        float second = Mth.sin(progress * 6.2831855F * 9.0F + time * 0.18F + band * 1.1F);
        return Mth.clamp((first * 0.62F + second * 0.38F + 1.0F) * 0.5F, 0.0F, 1.0F);
    }

    /**
     * 密度波浪（0.45~1）
     *
     * @param progress 横向进度
     * @param band     层索引
     * @param time     时间
     * @return 密度值
     */
    private float densityWave(float progress, int band, float time) {
        float first = Mth.sin(progress * 6.2831855F * 3.2F + time * 0.27F + band * 1.7F);
        float second = Mth.sin(progress * 6.2831855F * 6.8F - time * 0.19F + band * 0.8F);
        float density = (first * 0.58F + second * 0.42F + 1.0F) * 0.5F;
        return Mth.lerp(Mth.clamp(density, 0.0F, 1.0F), 0.45F, 1.0F);
    }

    /**
     * 横向边缘淡出（正弦，宽柔化避免轮廓分明）
     *
     * @param progress 横向进度
     * @return 0~1
     */
    private float horizontalFade(float progress) {
        // 加大柔化范围（edgeSoftness 放大 3 倍），消除"轮廓分明"的层状边缘
        float soft = Math.max(0.3F, this.edgeSoftness * 3.0F);
        float left = Mth.clamp(progress / Math.max(0.001F, soft), 0.0F, 1.0F);
        float right = Mth.clamp((1.0F - progress) / Math.max(0.001F, soft), 0.0F, 1.0F);
        return Mth.sin(Math.min(left, right) * 1.5707964F);
    }

    /**
     * 射线强度（快速波动，产生丝状感）
     *
     * @param progress 横向进度
     * @param band     层索引
     * @param time     时间
     * @return 0~1
     */
    private float ray(float progress, int band, float time) {
        float fast = Mth.sin(progress * 6.2831855F * 17.0F + band * 2.7F + time * 0.55F);
        float slow = Mth.sin(progress * 6.2831855F * 7.0F - band * 1.4F - time * 0.31F);
        return Mth.clamp((fast * 0.5F + slow * 0.5F + 1.0F) * 0.5F, 0.0F, 1.0F);
    }

    /**
     * 纵向渐隐（底部/顶部柔和淡出，避免层状边缘）
     *
     * @param progress 纵向进度
     * @return 0~1
     */
    private static float gradientAlpha(float progress) {
        // 用平方正弦使上下边缘更柔和，消除"轮廓分明"
        float s = Mth.sin(progress * 3.1415927F);
        return Mth.clamp(s * s * 1.3F, 0.02F, 1.0F);
    }

    /**
     * 俯仰角钳制
     *
     * @param pitch 俯仰角
     * @return 钳制结果
     */
    private static float clampPitch(float pitch) {
        return Mth.clamp(pitch, -1.45F, 1.45F);
    }

    /**
     * 按纵向进度取颜色（相邻色插值）
     *
     * @param progress 纵向进度 0~1
     * @return 颜色
     */
    private SkyColor color(float progress) {
        if (this.colors.size() == 1) {
            return this.colors.get(0);
        }
        float scaled = Mth.clamp(progress, 0.0F, 1.0F) * (this.colors.size() - 1);
        int index = (int) Math.floor(scaled);
        index = Mth.clamp(index, 0, this.colors.size() - 2);
        float blend = scaled - index;
        SkyColor from = this.colors.get(index);
        SkyColor to = this.colors.get(index + 1);
        return from.lerp(to, blend);
    }

    /**
     * 添加一块幕帘面片（四边形拆为 4 个三角形，中心增亮）
     *
     * @param buffer     顶点缓冲
     * @param matrix     变换矩阵
     * @param lowerLeft  左下顶点
     * @param upperLeft  左上顶点
     * @param upperRight 右上顶点
     * @param lowerRight 右下顶点
     * @param lowerColor 下缘颜色
     * @param upperColor 上缘颜色
     * @param lowerAlpha 下缘透明度
     * @param upperAlpha 上缘透明度
     */
    private void addCurtain(
            BufferBuilder buffer, Matrix4f matrix,
            SkyPoint lowerLeft, SkyPoint upperLeft, SkyPoint upperRight, SkyPoint lowerRight,
            SkyColor lowerColor, SkyColor upperColor, float lowerAlpha, float upperAlpha
    ) {
        SkyPoint center = normalize(
                lowerLeft.x() + upperLeft.x() + upperRight.x() + lowerRight.x(),
                lowerLeft.y() + upperLeft.y() + upperRight.y() + lowerRight.y(),
                lowerLeft.z() + upperLeft.z() + upperRight.z() + lowerRight.z()
        );
        SkyColor centerColor = new SkyColor(
                (lowerColor.red() + upperColor.red()) * 0.5F,
                (lowerColor.green() + upperColor.green()) * 0.5F,
                (lowerColor.blue() + upperColor.blue()) * 0.5F
        );
        float centerAlpha = (lowerAlpha + upperAlpha) * 0.5F;
        addTriangle(buffer, matrix, lowerLeft, lowerColor, lowerAlpha, upperLeft, upperColor, upperAlpha, center, centerColor, centerAlpha);
        addTriangle(buffer, matrix, upperLeft, upperColor, upperAlpha, upperRight, upperColor, upperAlpha, center, centerColor, centerAlpha);
        addTriangle(buffer, matrix, upperRight, upperColor, upperAlpha, lowerRight, lowerColor, lowerAlpha, center, centerColor, centerAlpha);
        addTriangle(buffer, matrix, lowerRight, lowerColor, lowerAlpha, lowerLeft, lowerColor, lowerAlpha, center, centerColor, centerAlpha);
    }

    /**
     * 向量归一化到半径 100 球面
     *
     * @param x X
     * @param y Y
     * @param z Z
     * @return 归一化点
     */
    private static SkyPoint normalize(float x, float y, float z) {
        float length = 100.0F / Mth.sqrt(x * x + y * y + z * z);
        return new SkyPoint(x * length, y * length, z * length);
    }

    /**
     * 添加一个三角形
     * <p>
     * 颜色做<b>色相归一化</b>（最亮分量提升到 1）：加法混合下保持颜色鲜艳，
     * 避免暗色被叠加后发灰发白；透明度由 alpha 单独控制。
     *
     * @param buffer      顶点缓冲
     * @param matrix      变换矩阵
     * @param first       顶点 1
     * @param firstColor  顶点 1 颜色
     * @param firstAlpha  顶点 1 透明度
     * @param second      顶点 2
     * @param secondColor 顶点 2 颜色
     * @param secondAlpha 顶点 2 透明度
     * @param third       顶点 3
     * @param thirdColor  顶点 3 颜色
     * @param thirdAlpha  顶点 3 透明度
     */
    private static void addTriangle(
            BufferBuilder buffer, Matrix4f matrix,
            SkyPoint first, SkyColor firstColor, float firstAlpha,
            SkyPoint second, SkyColor secondColor, float secondAlpha,
            SkyPoint third, SkyColor thirdColor, float thirdAlpha
    ) {
        // 注意：不做色相归一化（归一化会让多 band 加法叠加后 RGB 全钳到 1 变白色）
        buffer.addVertex(matrix, first.x(), first.y(), first.z())
                .setColor(firstColor.red(), firstColor.green(), firstColor.blue(), firstAlpha);
        buffer.addVertex(matrix, second.x(), second.y(), second.z())
                .setColor(secondColor.red(), secondColor.green(), secondColor.blue(), secondAlpha);
        buffer.addVertex(matrix, third.x(), third.y(), third.z())
                .setColor(thirdColor.red(), thirdColor.green(), thirdColor.blue(), thirdAlpha);
    }
}
