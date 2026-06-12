package com.pasterdream.pasterdreammod.client.particle;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;

/**
 * PasterDream 粒子渲染类型常量池
 * <p>
 * 定义模组自定义的粒子渲染模式，如发光（加法混合）粒子渲染类型等，
 * 供各粒子类在 {@link net.minecraft.client.particle.Particle#getRenderType()} 中返回。
 */
public final class PDParticleRenderTypes {

    /**
     * 发光粒子渲染类型（Sprite Sheet 版）
     * <p>
     * 使用加法混合 (SRC_ALPHA + ONE)，使得粒子颜色叠加到背景上，
     * 呈现发光效果。适用于星光、冰晶、魔法粒子等需要发光的粒子。
     */
    public static final ParticleRenderType GLOWING_SHEET = new ParticleRenderType() {
        @Override
        public BufferBuilder begin(Tesselator tesselator, TextureManager textureManager) {
            RenderSystem.depthMask(false);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(
                    GlStateManager.SourceFactor.SRC_ALPHA,
                    GlStateManager.DestFactor.ONE
            );
            RenderSystem.setShader(GameRenderer::getParticleShader);
            RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
            return tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }

        @Override
        public String toString() {
            return "PasterDream_GLOWING_SHEET";
        }
    };

    private PDParticleRenderTypes() {
        throw new UnsupportedOperationException("工具类不可实例化");
    }
}
