package com.pasterdream.pasterdreammod.client.sky.content;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.pasterdream.pasterdreammod.api.client.sky.SkyContent;
import com.pasterdream.pasterdreammod.api.client.sky.SkyboxRenderContext;
import com.pasterdream.pasterdreammod.client.sky.PlayerSkyLinkData;
import com.pasterdream.pasterdreammod.client.sky.math.SkyColor;
import com.pasterdream.pasterdreammod.client.sky.math.SkyPoint;
import com.pasterdream.pasterdreammod.client.sky.render.SkyGeometry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

import java.util.List;

/**
 * 玩家连线星体内容 —— 渲染玩家用「星空枕」在天空上创建的星体节点与连线
 * <p>
 * 节点按创建顺序依次连线，节点渲染为明亮的星点（带呼吸闪烁）。
 * 数据来自 {@link PlayerSkyLinkData}（纯客户端）。
 */
public class SkyLinkContent implements SkyContent {

    /** 星体大小 */
    private static final float STAR_SIZE = 1.6F;
    /** 连线宽度 */
    private static final float LINE_WIDTH = 0.12F;
    /** 星体颜色（暖金） */
    private static final SkyColor STAR_COLOR = new SkyColor(1.0F, 0.9F, 0.6F);
    /** 连线颜色（淡金） */
    private static final SkyColor LINE_COLOR = new SkyColor(1.0F, 0.85F, 0.5F);

    private final ResourceLocation id;

    /**
     * @param id 内容标识
     */
    public SkyLinkContent(ResourceLocation id) {
        this.id = id;
    }

    @Override
    public ResourceLocation id() {
        return this.id;
    }

    @Override
    public int priority() {
        return 18;   // 星座(15)之上、极光(20)之下
    }

    @Override
    public float targetAlpha(SkyboxRenderContext context) {
        // 与星空一致：夜晚才可见
        return context.visibility();
    }

    @Override
    public void render(SkyboxRenderContext context, float alpha) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }
        List<SkyPoint> stars = PlayerSkyLinkData.getStars(player.getUUID());
        if (stars.isEmpty()) {
            return;
        }
        float time = context.renderTime();
        Matrix4f matrix = context.poseStack().last().pose();
        RenderSystem.disableCull();
        // 不设置混合模式，依赖外层标准混合

        // 连线（按创建顺序依次连接，无首尾闭环——星体序列是开放链而非集合体；
        // 仅当至少 2 颗星时才绘制，否则缓冲为空会导致 buildOrThrow 崩溃）
        if (stars.size() > 1) {
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            BufferBuilder lineBuffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
            for (int i = 0; i < stars.size() - 1; i++) {
                SkyGeometry.addLine(lineBuffer, matrix, stars.get(i), stars.get(i + 1),
                        LINE_WIDTH, LINE_COLOR, alpha * 0.6F);
            }
            SkyGeometry.drawIfNotEmpty(lineBuffer);
        }

        // 星体（呼吸闪烁）
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        BufferBuilder starBuffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        for (int i = 0; i < stars.size(); i++) {
            float twinkle = 0.8F + 0.2F * Mth.sin(time * 0.05F + i * 1.3F);
            SkyGeometry.addBillboard(starBuffer, matrix, stars.get(i),
                    STAR_SIZE * twinkle, 0.0F, STAR_COLOR, alpha * twinkle);
        }
        SkyGeometry.drawIfNotEmpty(starBuffer);

        RenderSystem.enableCull();
    }
}
