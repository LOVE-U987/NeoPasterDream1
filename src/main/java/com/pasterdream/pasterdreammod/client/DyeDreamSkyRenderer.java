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
@EventBusSubscriber(modid = PasterDreamMod.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
public class DyeDreamSkyRenderer {

    private static final int BAND_COUNT = 5;
    private static final int SEGMENTS = 36;
    private static final float BASE_HEIGHT = 70.0f;
    private static final float BASE_RADIUS = 85.0f;
    private static final float RIBBON_THICKNESS = 3.0f;

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
        float alpha = Math.max(0.0f, Math.min(1.0f, (-sunHeight) * 2.0f));

        if (alpha < 0.01f) return;

        float gameTime = (mc.level.getGameTime() + partialTick) / 20.0f;

        renderAurora(gameTime, alpha);
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

            float bandPhase = band * 0.3f;
            float heightOffset = band * 10.0f;
            float radiusOffset = band * (-4.0f);
            float bandHeight = BASE_HEIGHT + heightOffset;
            float bandRadius = BASE_RADIUS + radiusOffset;
            float bandAlpha = alpha * 0.28f * (1.0f - band * 0.12f);

            for (int i = 0; i <= SEGMENTS; i++) {
                float t = (float) i / SEGMENTS;
                float angle = -1.2f + t * 2.4f;

                float wave = (float) (Math.sin(angle * 3.0 + gameTime * 0.3 + bandPhase) * 4.0
                        + Math.sin(angle * 5.0 + gameTime * 0.2 + bandPhase * 0.7) * 2.0);

                float x = (float) (Math.sin(angle) * bandRadius);
                float z = (float) (Math.cos(angle) * bandRadius * 0.5);
                float y = bandHeight + wave;

                float colorT = (float) ((Math.sin(angle * 1.5 + gameTime * 0.1 + bandPhase) + 1.0) * 0.5);

                float r = 0.7f + 0.3f * (1.0f - colorT);
                float g = 0.2f + 0.3f * colorT;
                float b = 0.7f + 0.3f * colorT;

                buffer.addVertex(x, y + RIBBON_THICKNESS, z).setColor(r, g, b, bandAlpha);
                buffer.addVertex(x, y - RIBBON_THICKNESS, z)
                        .setColor(r * 0.3f, g * 0.3f, b * 0.3f, bandAlpha * 0.3f);
            }

            BufferUploader.drawWithShader(buffer.buildOrThrow());
        }

        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
    }
}
