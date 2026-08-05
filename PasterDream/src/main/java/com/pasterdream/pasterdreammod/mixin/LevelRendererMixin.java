package com.pasterdream.pasterdreammod.mixin;

import com.mojang.blaze3d.vertex.VertexBuffer;
import com.pasterdream.pasterdreammod.client.sky.SkyboxRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * LevelRenderer 混合注入 —— 抑制原版星星
 * <p>
 * 在 {@code LevelRenderer#renderSky} 中，{@link VertexBuffer#drawWithShader}
 * 共调用 3 次（ordinal）：skyBuffer(0) 天穹渐变 → starBuffer(1) 星盘 →
 * darkBuffer(2) 地平线下暗面。ordinal=1 即原版星星。
 * <p>
 * 当 {@link SkyboxRenderer} 有活跃天空盒内容时跳过星盘绘制，
 * 由自定义星空（star_field）接管，避免双重星空。
 * <p>
 * 自定义天空的实际渲染通过 {@code RenderLevelStageEvent.AFTER_SKY} 事件
 * （{@code SkyboxClientEvents}）完成——这是用户实测验证在 Iris 光影下
 * 正常的方式；不再使用 {@code renderSky} 的 {@code @Inject(RETURN)}。
 */
@Mixin(LevelRenderer.class)
public class LevelRendererMixin {

    /**
     * 重定向第 2 次 VertexBuffer.drawWithShader（原版星星星盘）
     * <p>
     * 有活跃天空盒时跳过原方法调用（星盘不绘制）。
     *
     * @param instance          VertexBuffer 实例（星盘）
     * @param modelViewMatrix   模型视图矩阵
     * @param projectionMatrix  投影矩阵
     * @param shader            着色器
     */
    @Redirect(
            method = "renderSky",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/VertexBuffer;drawWithShader(Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;Lnet/minecraft/client/renderer/ShaderInstance;)V",
                    ordinal = 1
            )
    )
    private void pasterdream$suppressVanillaStars(
            VertexBuffer instance, Matrix4f modelViewMatrix, Matrix4f projectionMatrix, ShaderInstance shader
    ) {
        if (!SkyboxRenderer.shouldSuppressVanillaStars()) {
            instance.drawWithShader(modelViewMatrix, projectionMatrix, shader);
        }
    }
}
