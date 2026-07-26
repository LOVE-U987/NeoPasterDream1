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
     * <p>
     * 状态管理说明：MC 1.21.1 的 {@link ParticleRenderType} 只有 begin()、没有 end() 钩子，
     * 因此按 vanilla 各粒子 sheet 的惯例，begin() 自行设置本类型所需的全部渲染状态
     * （shader、纹理、blend、depthMask），不依赖上一个类型遗留的状态。本类设置的
     * depthMask(false) 与加法混合不会污染后续渲染，原因如下：
     * <ul>
     *     <li>{@code ParticleEngine.render()} 在全部粒子类型绘制完毕后会统一执行
     *     {@code RenderSystem.depthMask(true)} 与 {@code RenderSystem.disableBlend()}，
     *     即粒子阶段之后的渲染状态由 ParticleEngine 负责恢复；</li>
     *     <li>粒子阶段内部，NeoForge（ClientHooks.makeParticleRenderTypeComparator）
     *     保证 vanilla 类型先于自定义类型渲染，且各 vanilla sheet 的 begin() 均会
     *     重新设置自身的 blend/depthMask，故同样不受本类型影响。</li>
     * </ul>
     */
    public static final ParticleRenderType GLOWING_SHEET = new ParticleRenderType() {
        @Override
        public BufferBuilder begin(Tesselator tesselator, TextureManager textureManager) {
            // 发光粒子不写入深度缓冲，使加法混合的光斑可以正确相互叠加；
            // 该状态由 ParticleEngine.render() 在粒子阶段末尾统一恢复为 depthMask(true)
            RenderSystem.depthMask(false);
            // 加法混合 (SRC_ALPHA + ONE)；同样由 ParticleEngine.render() 末尾统一 disableBlend() 恢复
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
