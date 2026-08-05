package com.pasterdream.pasterdreammod.client.sky.content;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.pasterdream.pasterdreammod.api.client.sky.SkyContent;
import com.pasterdream.pasterdreammod.api.client.sky.SkyboxRenderContext;
import com.pasterdream.pasterdreammod.client.sky.math.SkyColor;
import com.pasterdream.pasterdreammod.client.sky.math.SkyPoint;
import com.pasterdream.pasterdreammod.client.sky.render.SkyGeometry;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import org.joml.Matrix4f;

import java.util.List;

/**
 * 光带内容 —— 环形银河/光带，由三角网格构成
 * <p>
 * 沿环向多频正弦波叠加（大波/细节波/侧摆）产生丝带状流动，
 * 宽度方向按 blur 高斯式渐隐，颜色沿环向插值循环。
 * {@code arc} 达到 2π 时形成闭环（银河环），否则为弧段（如彩虹带）。
 */
public class SkyRibbonContent implements SkyContent {

    /** 光带内容标识 */
    private final ResourceLocation id;
    private final int priority;
    private final List<SkyColor> colors;
    private final Ribbon ribbon;
    private final int segments;
    private final int gradientSteps;
    private final float centerYaw;
    private final float basePitch;
    private final float spacing;
    private final float thickness;
    private final float arc;
    private final float tilt;
    private final float waveAmplitude;
    private final float waveFrequency;
    private final float wobbleAmplitude;
    private final float opacity;
    private final float speed;
    private final float edgeSoftness;
    private final float blur;

    /**
     * @param id              内容标识
     * @param priority        绘制优先级
     * @param colors          颜色列表（沿环向插值）
     * @param segments        环向分段
     * @param gradientSteps   宽度方向分段
     * @param centerYaw       中心偏航角（弧度）
     * @param basePitch       基线俯仰角
     * @param spacing         多带间距
     * @param thickness       带厚度
     * @param arc             弧长（2π = 闭环）
     * @param tilt            整体倾斜角
     * @param waveAmplitude   波浪振幅
     * @param waveFrequency   波浪频率
     * @param wobbleAmplitude 侧摆振幅
     * @param opacity         不透明度
     * @param speed           动画速度
     * @param edgeSoftness    弧两端柔化
     * @param blur            宽度渐变柔化
     * @param seed            随机种子
     */
    public SkyRibbonContent(
            ResourceLocation id, int priority, List<SkyColor> colors,
            int segments, int gradientSteps,
            float centerYaw, float basePitch, float spacing, float thickness,
            float arc, float tilt, float waveAmplitude, float waveFrequency,
            float wobbleAmplitude, float opacity, float speed, float edgeSoftness, float blur,
            long seed
    ) {
        this.id = id;
        this.priority = priority;
        this.colors = List.copyOf(colors);
        this.ribbon = makeRibbon(seed);
        this.segments = Math.max(8, segments);
        this.gradientSteps = Math.max(2, gradientSteps);
        this.centerYaw = centerYaw;
        this.basePitch = basePitch;
        this.spacing = spacing;
        this.thickness = thickness;
        this.arc = arc;
        this.tilt = tilt;
        this.waveAmplitude = waveAmplitude;
        this.waveFrequency = waveFrequency;
        this.wobbleAmplitude = wobbleAmplitude;
        this.opacity = opacity;
        this.speed = speed;
        this.edgeSoftness = edgeSoftness;
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
    public void render(SkyboxRenderContext context, float alpha) {
        // 与 Stellara 一致：不设置混合模式，依赖外层 SkyboxRenderer 统一的标准混合
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.disableCull();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        Matrix4f matrix = context.poseStack().last().pose();
        float time = context.renderTime() * this.speed;
        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_COLOR);
        renderRibbon(buffer, matrix, this.ribbon, time, alpha * this.opacity);
        BufferUploader.drawWithShader(buffer.buildOrThrow());
        RenderSystem.enableCull();
    }

    /**
     * 渲染单条光带
     *
     * @param buffer 顶点缓冲
     * @param matrix 变换矩阵
     * @param ribbon 光带属性
     * @param time   动画时间
     * @param alpha  透明度
     */
    private void renderRibbon(BufferBuilder buffer, Matrix4f matrix, Ribbon ribbon, float time, float alpha) {
        float ribbonOffset = this.spacing + ribbon.offset();
        float ribbonAlpha = alpha * ribbon.alpha() * (0.78F + 0.22F * Mth.sin(time * 0.73F + ribbon.phaseA()));

        for (int segment = 0; segment < this.segments; segment++) {
            float left = (float) segment / this.segments;
            float right = this.isClosedArc() && segment == this.segments - 1 ? 0.0F : (segment + 1.0F) / this.segments;
            float leftFade = this.arcFade(left);
            float rightFade = this.arcFade(right);

            for (int step = 0; step < this.gradientSteps; step++) {
                float bottom = (float) step / this.gradientSteps;
                float top = (step + 1.0F) / this.gradientSteps;
                float bottomAlpha = this.gradientAlpha(bottom);
                float topAlpha = this.gradientAlpha(top);
                addQuad(
                        buffer, matrix,
                        this.point(ribbon, ribbonOffset, left, bottom, time),
                        this.point(ribbon, ribbonOffset, left, top, time),
                        this.point(ribbon, ribbonOffset, right, top, time),
                        this.point(ribbon, ribbonOffset, right, bottom, time),
                        this.ribbonColor(bottom + ribbon.colorShift()),
                        this.ribbonColor(top + ribbon.colorShift()),
                        ribbonAlpha * leftFade * bottomAlpha,
                        ribbonAlpha * leftFade * topAlpha,
                        ribbonAlpha * rightFade * topAlpha,
                        ribbonAlpha * rightFade * bottomAlpha
                );
            }
        }
    }

    /**
     * 计算光带网格顶点
     *
     * @param ribbon       光带属性
     * @param ribbonOffset 带偏移
     * @param progress     环向进度 0~1
     * @param widthProgress 宽度进度 0~1
     * @param time         动画时间
     * @return 顶点球面坐标
     */
    private SkyPoint point(Ribbon ribbon, float ribbonOffset, float progress, float widthProgress, float time) {
        float angle = (progress - 0.5F) * this.arc;
        float widthOffset = (widthProgress - 0.5F) * this.thickness * ribbon.thicknessScale();
        float primaryFrequency = this.periodicFrequency(this.waveFrequency);
        float detailFrequency = this.periodicFrequency(this.waveFrequency * 2.7F);
        float wobbleFrequency = this.periodicFrequency(this.waveFrequency * 1.35F);
        float largeWave = Mth.sin(progress * 6.2831855F * primaryFrequency + time + ribbon.phaseA()) * this.waveAmplitude;
        float smallWave = Mth.sin(progress * 6.2831855F * detailFrequency - time * 0.64F + ribbon.phaseB()) * this.waveAmplitude * 0.34F;
        float sideWobble = Mth.sin(progress * 6.2831855F * wobbleFrequency + time * 0.41F + ribbon.phaseC()) * this.wobbleAmplitude;
        float pitch = clampPitch(this.basePitch + ribbonOffset + widthOffset + largeWave + smallWave);
        float yaw = angle + sideWobble;
        SkyPoint point = SkyGeometry.point(yaw, pitch);
        // 整体倾斜（绕 X 轴旋转）
        float cosTilt = Mth.cos(this.tilt);
        float sinTilt = Mth.sin(this.tilt);
        SkyPoint tilted = Math.abs(this.tilt) < 0.001F
                ? point
                : new SkyPoint(point.x(), point.y() * cosTilt - point.z() * sinTilt, point.y() * sinTilt + point.z() * cosTilt);
        // 中心偏航旋转
        float cosYaw = Mth.cos(this.centerYaw);
        float sinYaw = Mth.sin(this.centerYaw);
        return new SkyPoint(
                tilted.x() * cosYaw + tilted.z() * sinYaw,
                tilted.y(),
                tilted.z() * cosYaw - tilted.x() * sinYaw
        );
    }

    /**
     * 弧两端淡出
     *
     * @param progress 环向进度
     * @return 0~1
     */
    private float arcFade(float progress) {
        if (this.isClosedArc()) {
            return 1.0F;
        }
        float left = Mth.clamp(progress / Math.max(0.001F, this.edgeSoftness), 0.0F, 1.0F);
        float right = Mth.clamp((1.0F - progress) / Math.max(0.001F, this.edgeSoftness), 0.0F, 1.0F);
        return Mth.sin(Math.min(left, right) * 1.5707964F);
    }

    /**
     * 闭环时周期频率取整（保证波形首尾衔接）
     *
     * @param frequency 原始频率
     * @return 调整后频率
     */
    private float periodicFrequency(float frequency) {
        return !this.isClosedArc() ? frequency : Math.max(1.0F, (float) Math.round(frequency));
    }

    /**
     * 是否为闭环（arc ≥ 2π - 0.01）
     *
     * @return 是否闭环
     */
    private boolean isClosedArc() {
        return this.arc >= 6.2631855F;
    }

    /**
     * 宽度方向渐变（blur 高斯式）
     *
     * @param progress 宽度进度
     * @return 0~1
     */
    private float gradientAlpha(float progress) {
        if (this.blur <= 0.0F) {
            return 1.0F;
        }
        float fadeWidth = Mth.clamp(this.blur * 0.5F, 0.001F, 0.5F);
        float edgeProgress = Mth.clamp(Math.min(progress, 1.0F - progress) / fadeWidth, 0.0F, 1.0F);
        return Mth.sin(edgeProgress * 1.5707964F);
    }

    /**
     * 环向颜色插值（循环取色）
     *
     * @param progress 进度
     * @return 颜色
     */
    private SkyColor color(float progress) {
        if (this.colors.size() == 1) {
            return this.colors.get(0);
        }
        float wrapped = progress - Mth.floor(progress);
        float scaled = wrapped * this.colors.size();
        int index = Mth.floor(scaled);
        int next = (index + 1) % this.colors.size();
        float blend = scaled - index;
        SkyColor from = this.colors.get(index);
        SkyColor to = this.colors.get(next);
        return from.lerp(to, blend);
    }

    /**
     * 光带颜色（含 blur 模式）
     *
     * @param progress 进度
     * @return 颜色
     */
    private SkyColor ribbonColor(float progress) {
        if (this.colors.size() == 1) {
            return this.colors.get(0);
        }
        if (this.blur <= 0.0F) {
            float wrapped = progress - Mth.floor(progress);
            return this.colors.get(Mth.clamp((int) Math.floor(wrapped * this.colors.size()), 0, this.colors.size() - 1));
        }
        return this.color(progress);
    }

    /**
     * 添加一个四边形面片（拆 4 三角形，中心增亮）
     *
     * @param buffer          顶点缓冲
     * @param matrix          变换矩阵
     * @param lowerLeft       左下
     * @param upperLeft       左上
     * @param upperRight      右上
     * @param lowerRight      右下
     * @param lowerColor      下缘颜色
     * @param upperColor      上缘颜色
     * @param lowerLeftAlpha  左下透明度
     * @param upperLeftAlpha  左上透明度
     * @param upperRightAlpha 右上透明度
     * @param lowerRightAlpha 右下透明度
     */
    private static void addQuad(
            BufferBuilder buffer, Matrix4f matrix,
            SkyPoint lowerLeft, SkyPoint upperLeft, SkyPoint upperRight, SkyPoint lowerRight,
            SkyColor lowerColor, SkyColor upperColor,
            float lowerLeftAlpha, float upperLeftAlpha, float upperRightAlpha, float lowerRightAlpha
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
        float centerAlpha = (lowerLeftAlpha + upperLeftAlpha + upperRightAlpha + lowerRightAlpha) * 0.25F;
        addTriangle(buffer, matrix, lowerLeft, lowerColor, lowerLeftAlpha, upperLeft, upperColor, upperLeftAlpha, center, centerColor, centerAlpha);
        addTriangle(buffer, matrix, upperLeft, upperColor, upperLeftAlpha, upperRight, upperColor, upperRightAlpha, center, centerColor, centerAlpha);
        addTriangle(buffer, matrix, upperRight, upperColor, upperRightAlpha, lowerRight, lowerColor, lowerRightAlpha, center, centerColor, centerAlpha);
        addTriangle(buffer, matrix, lowerRight, lowerColor, lowerRightAlpha, lowerLeft, lowerColor, lowerLeftAlpha, center, centerColor, centerAlpha);
    }

    /**
     * 归一化到半径 100 球面
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
     * 添加三角形
     * <p>
     * 颜色做<b>色相归一化</b>（最亮分量提升到 1）：加法混合下保持颜色鲜艳，
     * 避免暗色被叠加后发灰发白；透明度由 alpha 单独控制。
     *
     * @param buffer 顶点缓冲
     * @param matrix 变换矩阵
     * @param first  顶点 1
     * @param firstColor 顶点 1 颜色
     * @param firstAlpha 顶点 1 透明度
     * @param second 顶点 2
     * @param secondColor 顶点 2 颜色
     * @param secondAlpha 顶点 2 透明度
     * @param third  顶点 3
     * @param thirdColor 顶点 3 颜色
     * @param thirdAlpha 顶点 3 透明度
     */
    private static void addTriangle(
            BufferBuilder buffer, Matrix4f matrix,
            SkyPoint first, SkyColor firstColor, float firstAlpha,
            SkyPoint second, SkyColor secondColor, float secondAlpha,
            SkyPoint third, SkyColor thirdColor, float thirdAlpha
    ) {
        // 注意：不做色相归一化（归一化会让多彩光带加法叠加后 RGB 全钳到 1 变白）
        buffer.addVertex(matrix, first.x(), first.y(), first.z())
                .setColor(firstColor.red(), firstColor.green(), firstColor.blue(), firstAlpha);
        buffer.addVertex(matrix, second.x(), second.y(), second.z())
                .setColor(secondColor.red(), secondColor.green(), secondColor.blue(), secondAlpha);
        buffer.addVertex(matrix, third.x(), third.y(), third.z())
                .setColor(thirdColor.red(), thirdColor.green(), thirdColor.blue(), thirdAlpha);
    }

    /**
     * 俯仰角钳制
     *
     * @param pitch 俯仰角
     * @return 钳制结果
     */
    private static float clampPitch(float pitch) {
        return Mth.clamp(pitch, -1.42F, 1.42F);
    }

    /**
     * 生成随机光带属性
     *
     * @param seed 种子
     * @return 光带属性
     */
    private static Ribbon makeRibbon(long seed) {
        LegacyRandomSource random = new LegacyRandomSource(seed);
        return new Ribbon(
                randomRange(-0.035F, 0.035F, random),
                randomRange(0.72F, 1.18F, random),
                randomRange(0.65F, 1.35F, random),
                random.nextFloat() * 6.2831855F,
                random.nextFloat() * 6.2831855F,
                random.nextFloat() * 6.2831855F,
                random.nextFloat()
        );
    }

    /**
     * 区间随机
     *
     * @param min    最小值
     * @param max    最大值
     * @param random 随机源
     * @return 随机值
     */
    private static float randomRange(float min, float max, LegacyRandomSource random) {
        return min + random.nextFloat() * (max - min);
    }

    /**
     * 光带静态属性
     *
     * @param offset         带偏移
     * @param alpha          基础透明度
     * @param thicknessScale 厚度缩放
     * @param phaseA         相位 A
     * @param phaseB         相位 B
     * @param phaseC         相位 C
     * @param colorShift     颜色偏移
     */
    private record Ribbon(float offset, float alpha, float thicknessScale, float phaseA, float phaseB, float phaseC, float colorShift) {
    }
}
