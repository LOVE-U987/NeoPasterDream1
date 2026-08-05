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
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

/**
 * 天空色调内容 —— 为整片天空染上一层渐变主题色（光滑球面穹顶）
 * <p>
 * 以半径 100 的球面三角网格绘制，颜色按纬度渐变（天底暗、天顶亮），
 * 顶点法线连续、无棱角，光影（Iris）下也不会出现"侧面平面发光"的伪影。
 * 通常以负优先级打底。
 */
public class SkyTintContent implements SkyContent {

    /** 纬度圈分段数（越高越平滑） */
    private static final int RINGS = 20;
    /** 经线分段数 */
    private static final int SLICES = 36;
    /** 天空球半径 */
    private static final float RADIUS = 100.0F;

    private final ResourceLocation id;
    private final int priority;
    private final SkyColor color;
    private final float opacity;

    /**
     * @param id       内容标识
     * @param priority 绘制优先级
     * @param color    着色颜色（RGB 0~1）
     * @param opacity  最大着色强度（0~1）
     */
    public SkyTintContent(ResourceLocation id, int priority, SkyColor color, float opacity) {
        this.id = id;
        this.priority = priority;
        this.color = color;
        this.opacity = opacity;
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
        // 天空色彩全天可见：白天淡淡的群系氛围色，夜晚（半夜）达到最强
        // 0.15 基础 + 0.85 夜晚权重，保证 time 18000 时 sky_tint 最明显
        return Mth.clamp(context.weatherFactor() * (0.15F + 0.85F * context.nightFactor()), 0.0F, 1.0F);
    }

    @Override
    public void render(SkyboxRenderContext context, float alpha) {
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        Matrix4f matrix = context.poseStack().last().pose();
        float tintAlpha = alpha * this.opacity;
        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_COLOR);

        // 球面渐变穹顶：纬度圈 × 经线三角网格
        for (int ring = 0; ring < RINGS; ring++) {
            float pitch0 = (float) ring / RINGS * (float) Math.PI - (float) Math.PI / 2.0F;
            float pitch1 = (float) (ring + 1) / RINGS * (float) Math.PI - (float) Math.PI / 2.0F;
            float alpha0 = tintAlpha * gradientAlpha((float) ring / RINGS);
            float alpha1 = tintAlpha * gradientAlpha((float) (ring + 1) / RINGS);
            for (int slice = 0; slice < SLICES; slice++) {
                float yaw0 = (float) slice / SLICES * 6.2831855F;
                float yaw1 = (float) (slice + 1) / SLICES * 6.2831855F;
                // 两个三角形组成一个面片
                addVertex(buffer, matrix, yaw0, pitch0, alpha0);
                addVertex(buffer, matrix, yaw1, pitch0, alpha0);
                addVertex(buffer, matrix, yaw1, pitch1, alpha1);
                addVertex(buffer, matrix, yaw0, pitch0, alpha0);
                addVertex(buffer, matrix, yaw1, pitch1, alpha1);
                addVertex(buffer, matrix, yaw0, pitch1, alpha1);
            }
        }
        BufferUploader.drawWithShader(buffer.buildOrThrow());
    }

    /**
     * 添加一个球面顶点
     *
     * @param buffer 顶点缓冲
     * @param matrix 变换矩阵
     * @param yaw    偏航角（弧度）
     * @param pitch  俯仰角（弧度）
     * @param alpha  透明度
     */
    private void addVertex(BufferBuilder buffer, Matrix4f matrix, float yaw, float pitch, float alpha) {
        float horizontal = Mth.cos(pitch);
        float x = Mth.sin(yaw) * horizontal * RADIUS;
        float y = Mth.sin(pitch) * RADIUS;
        float z = Mth.cos(yaw) * horizontal * RADIUS;
        buffer.addVertex(matrix, x, y, z)
                .setColor(this.color.red(), this.color.green(), this.color.blue(), alpha);
    }

    /**
     * 纵向渐隐系数：天底暗、天顶亮（正弦过渡，无接缝）
     *
     * @param progress 纬度进度（0=天底，1=天顶）
     * @return 0~1
     */
    private static float gradientAlpha(float progress) {
        return 0.15F + 0.85F * Mth.sin(progress * 3.1415927F);
    }
}
