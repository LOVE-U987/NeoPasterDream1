package com.pasterdream.pasterdreammod.client.sky.skystatic;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.pasterdream.pasterdreammod.client.sky.SkyboxRenderer;
import com.pasterdream.pasterdreammod.config.PDCommonConfig;
import com.pasterdream.pasterdreammod.config.PDCommonConfig.SkyRenderMode;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import org.joml.Matrix4f;

/**
 * Static Sky 渲染模式编排器
 * <p>
 * 在 {@code LevelRenderer.renderSky()} 的 starBuffer 绘制之后、
 * {@code disableBlend()} 之前注入 NPD 天体渲染，使 Iris 光影包
 * 将 NPD 内容捕获到 colortex 中，参与光影包的 composite 合成。
 * <p>
 * 三种渲染模式：
 * <ul>
 *   <li>{@link SkyRenderMode#DYNAMIC} — 现有 AFTER_SKY 事件方式（默认）</li>
 *   <li>{@link SkyRenderMode#STATIC} — renderSky 注入（Iris 兼容）</li>
 *   <li>{@link SkyRenderMode#AUTO} — 检测到 Iris/Oculus 时用 STATIC，否则 DYNAMIC</li>
 * </ul>
 */
public final class StaticSkyRenderer {

    private StaticSkyRenderer() {
    }

    /**
     * 静态天空渲染入口（由 LevelRendererMixin @Inject 调用）
     *
     * @param modelViewMatrix  camera 旋转矩阵（renderSky 参数 $$0）
     * @param projectionMatrix 投影矩阵（renderSky 参数 $$1）
     * @param partialTick      帧间插值（renderSky 参数 $$2）
     * @param camera           当前相机（renderSky 参数 $$3）
     */
    public static void renderStaticSky(
            Matrix4f modelViewMatrix, Matrix4f projectionMatrix,
            float partialTick, Camera camera
    ) {
        if (!shouldRenderStatic()) return;

        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        if (level == null) return;

        // 构建 NPD 兼容 PoseStack（匹配 buildSkyPoseStack + NPD 旋转）
        PoseStack poseStack = new PoseStack();
        poseStack.mulPose(modelViewMatrix);
        poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
        float skyAngle = level.getTimeOfDay(partialTick) * 360.0F;
        poseStack.mulPose(Axis.YP.rotationDegrees(skyAngle));

        // 调用 NPD 内容渲染（全部 8 类型）
        SkyboxRenderer.renderStatic(poseStack, camera, partialTick);
        // 玩家连线星体（星空枕创建）——静态路径下 AFTER_SKY 被跳过，须在此补渲染
        SkyboxRenderer.renderPlayerSkyLinksStatic(poseStack, camera, partialTick);

        // 恢复星盘段的 additive blend（vanilla 后续会 disableBlend + defaultBlendFunc，
        // 此处恢复为防御性措施，确保 Iris 对 sky pass 状态的预期不被破坏）
        RenderSystem.blendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE,
                GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    /**
     * 判断是否需要跳过 AFTER_SKY 事件（静态模式接管时）
     *
     * @return true 时跳过 AFTER_SKY
     */
    public static boolean shouldSkipAfterSky() {
        return switch (getEffectiveMode()) {
            case STATIC -> true;
            case AUTO -> IrisDetector.isIrisLoaded();
            case DYNAMIC -> false;
        };
    }

    /**
     * 判断是否应使用静态渲染
     */
    private static boolean shouldRenderStatic() {
        return switch (getEffectiveMode()) {
            case STATIC -> true;
            case AUTO -> IrisDetector.isIrisLoaded();
            case DYNAMIC -> false;
        };
    }

    /**
     * 获取有效渲染模式（考虑默认值）
     */
    private static SkyRenderMode getEffectiveMode() {
        SkyRenderMode mode = PDCommonConfig.SKY_RENDER_MODE.get();
        return mode != null ? mode : SkyRenderMode.DYNAMIC;
    }
}