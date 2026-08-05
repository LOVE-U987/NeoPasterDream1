package com.pasterdream.pasterdreammod.client.sky;

import com.mojang.blaze3d.vertex.PoseStack;
import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.api.client.sky.SkyboxAPI;
import com.pasterdream.pasterdreammod.client.sky.data.SkyboxDataReloadListener;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.event.AddReloadListenerEvent;

/**
 * 天空盒客户端事件 —— 注册数据重载监听器 + 挂载天空渲染
 * <p>
 * 自定义天空通过 {@link RenderLevelStageEvent.Stage#AFTER_SKY} 渲染
 * （用户在实测中验证过该方式下光带正常）。此阶段在原版天空渲染完成后
 * 触发，用 {@code modelViewMatrix}（相机旋转）构建 PoseStack，使天体
 * 定位在世界空间天空盒上。
 * <p>
 * ⚠️ 注意：{" AFTER_SKY"} 事件传入的 PoseStack 为 null，必须用
 * {@code new PoseStack() + mulPose(modelViewMatrix)} 构建。
 */
@EventBusSubscriber(modid = PasterDreamMod.MOD_ID, value = Dist.CLIENT)
public class SkyboxClientEvents {

    private SkyboxClientEvents() {
    }

    /**
     * 注册天空盒数据重载监听器（游戏总线事件）
     *
     * @param event 重载监听器事件
     */
    @SubscribeEvent
    public static void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(new SkyboxDataReloadListener());
    }

    /**
     * 天空渲染挂载点（AFTER_SKY 阶段，原版天空渲染完成后、云之前）
     *
     * @param event 渲染阶段事件
     */
    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (!SkyboxAPI.isAfterSky(event)) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) {
            return;
        }
        // AFTER_SKY 事件 PoseStack 为 null，用 API 辅助构建（含相机旋转）
        PoseStack poseStack = SkyboxAPI.buildSkyPoseStack(event);
        SkyboxRenderer.render(poseStack, event.getCamera(), event.getPartialTick().getGameTimeDeltaPartialTick(false));
    }
}
