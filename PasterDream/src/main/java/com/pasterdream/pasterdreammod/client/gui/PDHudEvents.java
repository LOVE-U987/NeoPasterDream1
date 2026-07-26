package com.pasterdream.pasterdreammod.client.gui;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.config.PDClientConfig;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

/**
 * HUD 渲染事件处理类（仅客户端，游戏总线）
 * <p>
 * 移植自原版 {@code event/PDHUDEvent.java}（RenderGuiOverlayEvent →
 * 1.21.1 {@link RenderGuiLayerEvent.Pre}）：客户端配置 {@code paster health hud}
 * 开启时取消 vanilla 血条层（player_health）的渲染，由
 * {@link PlayerHealthHudOverlay}（pd_health 主题血条）替代显示。
 * 配置关闭时不干预，vanilla 血条照常绘制。
 */
@EventBusSubscriber(modid = PasterDreamMod.MOD_ID, value = Dist.CLIENT)
public class PDHudEvents {

    /**
     * GUI 层渲染前置事件：按配置隐藏 vanilla 血条
     *
     * @param event GUI 层渲染前置事件（可取消）
     */
    @SubscribeEvent
    public static void onRenderGuiLayerPre(RenderGuiLayerEvent.Pre event) {
        if (!PDClientConfig.PASTER_HEALTH_HUD.get()) {
            return;
        }
        if (VanillaGuiLayers.PLAYER_HEALTH.equals(event.getName())) {
            event.setCanceled(true);
        }
    }
}
