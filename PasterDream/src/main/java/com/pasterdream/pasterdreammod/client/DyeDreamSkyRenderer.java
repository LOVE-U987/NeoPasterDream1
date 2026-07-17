package com.pasterdream.pasterdreammod.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.registry.PDDimensions;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

/**
 * 染梦世界极光天幕渲染器
 * <p>
 * 参考 ES 模组做法，在 {@link RenderLevelStageEvent.Stage#AFTER_WEATHER} 阶段
 * 绘制一张覆盖天空的极光彩带。通过多层半透明色带的波浪动画模拟极光效果，
 * 使用 {@link DefaultVertexFormat#POSITION_COLOR} 格式实现 GPU 端颜色插值。
 * <p>
 * 极光高度固定在相机上方 128 格处，覆盖半径 128 格，随夜晚深度调整透明度。
 */
@EventBusSubscriber(modid = PasterDreamMod.MOD_ID, value = Dist.CLIENT)
public class DyeDreamSkyRenderer {

    /** 极光带数量 */
    private static final int BAND_COUNT = 7;
    /** 极光弧分段数（越高越平滑） */
    private static final int SEGMENTS = 64;
    /** 极光带的 y 偏移（相对于相机上方） */
    private static final float AURORA_Y_OFFSET = 128.0f;
    /** 极光带覆盖半径 */
    private static final float AURORA_RADIUS = 128.0f;
    /** 极光带厚度（上下摆动幅度） */
    private static final float BAND_THICKNESS = 6.0f;

    /** 极光颜色调色板（紫/粉/青/绿渐变） */
    private static final float[][] AURORA_COLORS = {
            {1.0f, 0.4f, 0.8f},   // 紫粉
            {0.7f, 0.3f, 1.0f},   // 浅紫
            {0.4f, 0.5f, 1.0f},   // 浅蓝
            {0.3f, 0.8f, 1.0f},   // 天蓝
            {0.5f, 1.0f, 0.8f},   // 青绿
            {0.8f, 1.0f, 0.5f},   // 黄绿
            {1.0f, 0.7f, 0.4f}    // 橙粉
    };

    /**
     * 渲染事件入口，仅在染梦维度且为 AFTER_WEATHER 阶段时触发
     *
     * @param event 渲染阶段事件
     */
    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_WEATHER) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;
        if (!PDDimensions.isDyedreamWorld(mc.level)) return;

        // 使用 level.getDayTime() 直接判断昼夜，避免 partialTick 来源问题
        long dayTime = mc.level.getDayTime() % 24000;
        float sunAngle = mc.level.getSunAngle(0);
        float sunHeight = (float) Math.sin(sunAngle);
        float alpha = calculateAuroraAlpha(sunHeight);

        // 当 alpha 太低时跳过渲染
        if (alpha < 0.01f) return;

        Camera camera = mc.gameRenderer.getMainCamera();
        Vec3 cameraPos = camera.getPosition();
        // 使用游戏刻计算时间（不依赖 partialTick）
        float gameTime = mc.level.getGameTime() / 20.0f;

        // 设置渲染状态：关闭深度写入 + 开启混合
        RenderSystem.depthMask(false);
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        // 渲染极光带
        renderAuroraBands(gameTime, alpha, cameraPos);

        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
    }

    /**
     * 根据太阳高度计算极光透明度
     * <p>
     * 白天完全消失（sunHeight > 0.3），
     * 黄昏/黎明淡入淡出，
     * 夜晚透明度最高。
     *
     * @param sunHeight 太阳高度（-1 ~ 1）
     * @return 透明度（0 ~ 1）
     */
    private static float calculateAuroraAlpha(float sunHeight) {
        if (sunHeight > 0.3f) return 0.0f;
        if (sunHeight > 0.0f) {
            return Math.max(0.0f, 1.0f - sunHeight * 3.33f) * 0.5f;
        }
        if (sunHeight > -0.3f) {
            return 0.5f + (1.0f + sunHeight * 3.33f) * 0.5f;
        }
        return 1.0f;
    }

    /**
     * 渲染多层极光彩带
     * <p>
     * 每层光带为一张水平弧形的三角带，以相机为中心环绕。
     * 使用正弦波组合驱动波浪运动，颜色和透明度逐层渐变。
     *
     * @param gameTime  游戏已运行秒数
     * @param alpha     整体透明度
     * @param cameraPos 相机世界坐标
     */
    private static void renderAuroraBands(float gameTime, float alpha, Vec3 cameraPos) {
        Tesselator tesselator = Tesselator.getInstance();
        float auroraY = (float) cameraPos.y + AURORA_Y_OFFSET;

        for (int band = 0; band < BAND_COUNT; band++) {
            BufferBuilder buffer = tesselator.begin(
                    VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);

            float bandPhase = band * 0.35f;
            float heightOffset = band * 6.0f;
            float radiusOffset = band * (-3.0f);
            float bandHeight = auroraY + heightOffset;
            float bandRadius = AURORA_RADIUS + radiusOffset;
            float bandAlpha = alpha * 0.45f * (1.0f - band * 0.08f);

            float[] baseColor = AURORA_COLORS[band % AURORA_COLORS.length];

            for (int i = 0; i <= SEGMENTS; i++) {
                float t = (float) i / SEGMENTS;
                // 角度范围：-70° ~ 70°
                float angle = -1.22f + t * 2.44f;

                // 多层波浪组合
                float wave1 = (float) Math.sin(angle * 2.0 + gameTime * 0.18 + bandPhase) * 5.0f;
                float wave2 = (float) Math.sin(angle * 3.5 + gameTime * 0.22 + bandPhase * 0.7) * 3.0f;
                float wave3 = (float) Math.sin(gameTime * 0.12 + bandPhase * 0.4) * 2.0f;
                float wave = wave1 + wave2 + wave3;

                // 计算顶点位置（以相机为中心）
                float x = (float) (Math.sin(angle) * bandRadius);
                float z = (float) (Math.cos(angle) * bandRadius * 0.4f - bandRadius * 0.2f);
                float y = bandHeight + wave;

                // 颜色沿弧渐变
                float colorShift = (float) ((Math.sin(angle * 2.0 + gameTime * 0.12 + bandPhase) + 1.0) * 0.5);

                float r = baseColor[0] * (0.7f + 0.3f * colorShift);
                float g = baseColor[1] * (0.7f + 0.3f * colorShift);
                float b = baseColor[2] * (0.7f + 0.3f * colorShift);

                float flicker = (float) (0.85 + Math.sin(gameTime * 1.5 + angle + bandPhase) * 0.15);

                // 上顶点（亮色）
                buffer.addVertex(x, y + BAND_THICKNESS, z)
                        .setColor(r * flicker, g * flicker, b * flicker, bandAlpha);
                // 下顶点（暗色，产生渐变光晕感）
                buffer.addVertex(x, y - BAND_THICKNESS, z)
                        .setColor(r * 0.3f * flicker, g * 0.3f * flicker, b * 0.3f * flicker, bandAlpha * 0.3f);
            }

            BufferUploader.drawWithShader(buffer.buildOrThrow());
        }
    }
}
