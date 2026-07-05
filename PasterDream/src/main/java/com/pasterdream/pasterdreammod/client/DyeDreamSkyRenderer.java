package com.pasterdream.pasterdreammod.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.registry.PDDimensions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

/**
 * 染梦维度极光天幕渲染器
 * <p>
 * 监听 RenderLevelStageEvent.Stage.AFTER_SKY 事件，在天空渲染完成后绘制
 * 梦幻极光带。极光由多层半透明彩色光带组成，随时间正弦波动，
 * 使用粉/紫/青渐变色调，夜晚可见度最高。
 * 通过 {@link EventBusSubscriber} 自动注册到游戏事件总线，仅在客户端生效。
 */
@EventBusSubscriber(modid = PasterDreamMod.MOD_ID, value = Dist.CLIENT)
public class DyeDreamSkyRenderer {

    private static final int BAND_COUNT = 7;
    private static final int SEGMENTS = 48;
    private static final float BASE_HEIGHT = 65.0f;
    private static final float BASE_RADIUS = 90.0f;
    private static final float RIBBON_THICKNESS = 4.0f;

    private static final float[][] AURORA_COLORS = {
            {1.0f, 0.4f, 0.8f},
            {0.7f, 0.3f, 1.0f},
            {0.4f, 0.5f, 1.0f},
            {0.3f, 0.8f, 1.0f},
            {0.5f, 1.0f, 0.8f},
            {0.8f, 1.0f, 0.5f},
            {1.0f, 0.7f, 0.4f}
    };

    /**
     * 渲染事件入口，仅在染梦维度且为 AFTER_SKY 阶段时触发
     *
     * @param event 渲染阶段事件
     */
    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_SKY) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;
        if (!PDDimensions.isDyedreamWorld(mc.level)) return;

        float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(true);
        float sunAngle = mc.level.getSunAngle(partialTick);
        float sunHeight = (float) Math.sin(sunAngle);
        float alpha = calculateAuroraAlpha(sunHeight);

        if (alpha < 0.01f) return;

        float gameTime = (mc.level.getGameTime() + partialTick) / 20.0f;

        renderAurora(gameTime, alpha);
        renderAuroraStars(gameTime, alpha);
    }

    /**
     * 根据太阳高度计算极光透明度
     * 白天完全消失，黄昏和黎明有淡入淡出效果，夜晚最亮
     *
     * @param sunHeight 太阳高度（-1 ~ 1）
     * @return 透明度（0 ~ 1）
     */
    private static float calculateAuroraAlpha(float sunHeight) {
        if (sunHeight > 0.3f) return 0.0f;
        if (sunHeight > 0.0f) {
            return Math.max(0.0f, 1.0f - sunHeight * 3.33f) * 0.3f;
        }
        if (sunHeight > -0.3f) {
            return (1.0f + sunHeight * 3.33f);
        }
        return 1.0f;
    }

    /**
     * 渲染多层极光带
     * <p>
     * 每层光带为由三角带组成的弧形飘带，宽度/高度/颜色逐层渐变。
     * 使用 {@link DefaultVertexFormat#POSITION_COLOR} 格式实现半透明叠加。
     *
     * @param gameTime 游戏已运行秒数（含部分Tick），用于驱动波动动画
     * @param alpha    整体透明度（基于夜晚深度 0~1）
     */
    private static void renderAurora(float gameTime, float alpha) {
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        Tesselator tesselator = Tesselator.getInstance();

        for (int band = 0; band < BAND_COUNT; band++) {
            BufferBuilder buffer = tesselator.begin(
                    VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);

            float bandPhase = band * 0.25f;
            float heightOffset = band * 8.0f;
            float radiusOffset = band * (-3.5f);
            float bandHeight = BASE_HEIGHT + heightOffset;
            float bandRadius = BASE_RADIUS + radiusOffset;
            float bandAlpha = alpha * 0.25f * (1.0f - band * 0.08f);

            float[] baseColor = AURORA_COLORS[band % AURORA_COLORS.length];

            for (int i = 0; i <= SEGMENTS; i++) {
                float t = (float) i / SEGMENTS;
                float angle = -1.5f + t * 3.0f;

                float wave1 = (float) Math.sin(angle * 2.5 + gameTime * 0.25 + bandPhase) * 5.0f;
                float wave2 = (float) Math.sin(angle * 4.0 + gameTime * 0.18 + bandPhase * 0.8) * 3.0f;
                float wave3 = (float) Math.sin(angle * 6.5 + gameTime * 0.35 + bandPhase * 0.5) * 2.0f;
                float wave = wave1 + wave2 + wave3;

                float x = (float) (Math.sin(angle) * bandRadius);
                float z = (float) (Math.cos(angle) * bandRadius * 0.5);
                float y = bandHeight + wave;

                float colorShift = (float) ((Math.sin(angle * 2.0 + gameTime * 0.15 + bandPhase) + 1.0) * 0.5);

                float r = baseColor[0] * (0.7f + 0.3f * colorShift);
                float g = baseColor[1] * (0.7f + 0.3f * colorShift);
                float b = baseColor[2] * (0.7f + 0.3f * colorShift);

                float flicker = (float) (0.85 + Math.sin(gameTime * 2.0 + angle + bandPhase) * 0.15);

                buffer.addVertex(x, y + RIBBON_THICKNESS, z)
                        .setColor(r * flicker, g * flicker, b * flicker, bandAlpha);
                buffer.addVertex(x, y - RIBBON_THICKNESS, z)
                        .setColor(r * 0.25f * flicker, g * 0.25f * flicker, b * 0.25f * flicker, bandAlpha * 0.3f);
            }

            BufferUploader.drawWithShader(buffer.buildOrThrow());
        }

        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
    }

    /**
     * 渲染极光中的闪烁星光
     * 在极光带周围随机生成闪烁的光点，增强梦幻感
     *
     * @param gameTime 游戏已运行秒数（含部分Tick）
     * @param alpha    整体透明度
     */
    private static void renderAuroraStars(float gameTime, float alpha) {
        if (alpha < 0.1f) return;

        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.begin(
                VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        int starCount = 30;
        float starAlpha = alpha * 0.4f;

        for (int i = 0; i < starCount; i++) {
            float starPhase = i * 0.7f;
            float angle = (starPhase + gameTime * 0.05f) % (float) (Math.PI * 2);
            float radius = BASE_RADIUS * 0.6f + (float) Math.sin(starPhase) * BASE_RADIUS * 0.3f;
            float height = BASE_HEIGHT + (float) Math.cos(starPhase * 1.5f) * 40.0f;

            float x = (float) (Math.sin(angle) * radius);
            float z = (float) (Math.cos(angle) * radius * 0.5);
            float y = height;

            float flicker = (float) (0.3 + Math.sin(gameTime * 3.0 + starPhase * 2.0) * 0.7);
            float size = 0.5f + flicker * 0.5f;

            float[] color = AURORA_COLORS[i % AURORA_COLORS.length];

            buffer.addVertex(x - size, y, z - size).setColor(color[0], color[1], color[2], starAlpha * flicker);
            buffer.addVertex(x + size, y, z - size).setColor(color[0], color[1], color[2], starAlpha * flicker);
            buffer.addVertex(x + size, y, z + size).setColor(color[0], color[1], color[2], starAlpha * flicker);
            buffer.addVertex(x - size, y, z + size).setColor(color[0], color[1], color[2], starAlpha * flicker);
        }

        BufferUploader.drawWithShader(buffer.buildOrThrow());

        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
    }
}
