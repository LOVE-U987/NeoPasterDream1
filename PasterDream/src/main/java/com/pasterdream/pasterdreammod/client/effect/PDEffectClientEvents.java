package com.pasterdream.pasterdreammod.client.effect;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.api.client.effect.atmosphere.AtmosphereHandler;
import com.pasterdream.pasterdreammod.api.client.effect.cutscene.CutsceneCameraHandler;
import com.pasterdream.pasterdreammod.api.client.effect.ghost.GhostHandler;
import com.pasterdream.pasterdreammod.api.client.effect.impact.ImpactFramesHandler;
import com.pasterdream.pasterdreammod.api.client.effect.particle.ParticleEmitterHandler;
import com.pasterdream.pasterdreammod.api.client.effect.post.PostShaderEvent;
import com.pasterdream.pasterdreammod.api.client.effect.post.PostShaderManager;
import com.pasterdream.pasterdreammod.api.client.effect.screen.ScreenEffectOverlay;
import com.pasterdream.pasterdreammod.api.client.effect.shake.ScreenShakeHandler;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.event.RenderHandEvent;
import net.neoforged.neoforge.client.event.RenderHighlightEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

/**
 * 特效系统客户端事件挂载点 —— 唯一的客户端游戏总线订阅类
 * <p>
 * 职责（随实现逐步扩充）：
 * <ul>
 *   <li><b>后处理生命周期</b>：客户端 tick 时 {@link PostShaderManager#resizeAllIfNeeded()}，
 *       资源重载时 {@link PostShaderManager#reloadAll()}；</li>
 *   <li><b>后处理渲染</b>：订阅 {@link PostShaderEvent.Level}/{@link PostShaderEvent.Screen}
 *       （由 {@code GameRendererMixin} 发布），转发到各特效 handler；</li>
 *   <li><b>粒子发射器</b>：客户端 {@code LevelTickEvent.Pre} 转发到 ParticleEmitterHandler；</li>
 *   <li><b>过场动画</b>：{@code ClientTickEvent.Pre} 驱动 CutsceneCameraHandler，
 *       以及视角/HUD/交互接管事件。</li>
 * </ul>
 * 本类只在客户端加载（{@code value = Dist.CLIENT}），可安全引用
 * {@code api/client/**} 的客户端骨架类。
 */
@EventBusSubscriber(modid = PasterDreamMod.MOD_ID, value = Dist.CLIENT)
public class PDEffectClientEvents {

    private PDEffectClientEvents() {
    }

    // ==================== 后处理生命周期 ====================

    /**
     * 客户端 tick：窗口 resize 检查 + 打击帧队列推进 + 屏幕特效推进 + 过场推进 + 雾色阻尼推进
     *
     * @param event 客户端 tick 事件
     */
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Pre event) {
        PostShaderManager.resizeAllIfNeeded();
        ImpactFramesHandler.tick();
        ScreenEffectOverlay.tick();
        CutsceneCameraHandler.tick();
        AtmosphereHandler.tick();
        ScreenShakeHandler.tickAll();
    }

    /**
     * 客户端世界 tick：驱动粒子发射器 + 残影采样
     *
     * @param event 世界 tick 事件
     */
    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Pre event) {
        if (event.getLevel().isClientSide) {
            ParticleEmitterHandler.tickAll();
            GhostHandler.tickAll();
        }
    }

    /**
     * 资源重载（F3+T）：重实例化全部后处理链
     *
     * @param event 添加重载监听器事件
     */
    @SubscribeEvent
    public static void onAddReloadListeners(AddReloadListenerEvent event) {
        PostShaderManager.reloadAll();
    }

    // ==================== 后处理渲染 ====================

    /**
     * 世界帧级后处理（由 GameRendererMixin 发布）
     *
     * @param event 世界帧级后处理事件
     */
    @SubscribeEvent
    public static void onPostShaderLevel(PostShaderEvent.Level event) {
        // ImpactFrame 打击帧灰闪在此阶段处理
        ImpactFramesHandler.renderLevel(event);
    }

    /**
     * 整帧级后处理（由 GameRendererMixin 发布）
     *
     * @param event 整帧级后处理事件
     */
    @SubscribeEvent
    public static void onPostShaderScreen(PostShaderEvent.Screen event) {
        // ScreenEffect 屏幕特效后处理变体在此阶段处理
    }

    // ==================== 残影渲染 ====================

    /**
     * 世界帧渲染：实体渲染后绘制残影副本（AFTER_ENTITIES 阶段）
     *
     * @param event 渲染阶段事件
     */
    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_ENTITIES) {
            float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(false);
            GhostHandler.renderAll(event.getModelViewMatrix(), event.getCamera(), partialTick);
        }
    }

    // ==================== 过场动画：相机与交互接管 ====================

    /**
     * 过场时接管相机朝向
     *
     * @param event 相机角度计算事件
     */
    @SubscribeEvent
    public static void onComputeCameraAngles(ViewportEvent.ComputeCameraAngles event) {
        if (!CutsceneCameraHandler.isActive()) {
            return;
        }
        float[] angles = CutsceneCameraHandler.computeCameraAngles((float) event.getPartialTick());
        if (angles != null) {
            event.setYaw(angles[0]);
            event.setPitch(angles[1]);
            event.setRoll(angles[2]);
        }
    }

    /**
     * 氛围特效：修改雾色（暗化/血色雾）
     *
     * @param event 雾色计算事件
     */
    @SubscribeEvent
    public static void onComputeFogColor(ViewportEvent.ComputeFogColor event) {
        if (!AtmosphereHandler.isActive()) {
            return;
        }
        float[] fog = AtmosphereHandler.modifyFogColor(
                event.getRed(), event.getGreen(), event.getBlue());
        event.setRed(fog[0]);
        event.setGreen(fog[1]);
        event.setBlue(fog[2]);
    }

    /**
     * 过场时取消玩家交互（左/右键）
     *
     * @param event 交互按键映射事件
     */
    @SubscribeEvent
    public static void onInteractionKeyMapping(InputEvent.InteractionKeyMappingTriggered event) {
        if (CutsceneCameraHandler.isActive()) {
            event.setCanceled(true);
            event.setSwingHand(false);
        }
    }

    /**
     * 过场时取消手部渲染
     *
     * @param event 手部渲染事件
     */
    @SubscribeEvent
    public static void onRenderHand(RenderHandEvent event) {
        if (CutsceneCameraHandler.isActive()) {
            event.setCanceled(true);
        }
    }

    /**
     * 过场时取消方块高亮
     *
     * @param event 高亮渲染事件
     */
    @SubscribeEvent
    public static void onRenderHighlight(RenderHighlightEvent.Block event) {
        if (CutsceneCameraHandler.isActive()) {
            event.setCanceled(true);
        }
    }

    /**
     * 过场时隐藏除屏幕特效外的 HUD 层
     *
     * @param event GUI 层渲染前事件
     */
    @SubscribeEvent
    public static void onRenderGuiLayerPre(RenderGuiLayerEvent.Pre event) {
        if (CutsceneCameraHandler.isActive()) {
            // 放行屏幕特效叠加层（PDHudLayers 注册的 pasterdream:screen_effect）
            ResourceLocation layer = event.getName();
            if (layer == null
                    || !"pasterdream:screen_effect".equals(layer.toString())) {
                event.setCanceled(true);
            }
        }
    }

    /**
     * 登出时清理过场状态
     *
     * @param event 客户端登出事件
     */
    @SubscribeEvent
    public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        CutsceneCameraHandler.stop();
        ImpactFramesHandler.clearAll();
        ParticleEmitterHandler.clearAll();
        ScreenEffectOverlay.clearAll();
        ScreenShakeHandler.clearAll();
    }
}
