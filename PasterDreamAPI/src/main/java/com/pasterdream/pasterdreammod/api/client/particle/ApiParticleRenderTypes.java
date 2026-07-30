package com.pasterdream.pasterdreammod.api.client.particle;

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
 * 常见自定义粒子 RenderType 常量（发光/加法混合等）。
 * <p>
 * MC 1.21.1 的 {@link ParticleRenderType} 仅有 begin、无 end；begin 须自备全部状态。
 * {@code ParticleEngine.render()} 在粒子阶段末尾会统一 depthMask(true)/disableBlend()。
 */
public final class ApiParticleRenderTypes {

    /**
     * 发光粒子 sheet：加法混合 (SRC_ALPHA + ONE)，不写深度。
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
            return "PasterDreamAPI_GLOWING_SHEET";
        }
    };

    private ApiParticleRenderTypes() {
        throw new UnsupportedOperationException("工具类不可实例化");
    }
}
