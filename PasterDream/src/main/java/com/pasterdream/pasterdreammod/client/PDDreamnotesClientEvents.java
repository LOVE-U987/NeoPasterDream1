package com.pasterdream.pasterdreammod.client;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.client.screen.DreamnotesGui0Screen;
import com.pasterdream.pasterdreammod.registry.PDMenusDreamnotes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

/**
 * 寻梦者笔记客户端接线：菜单屏幕绑定。
 * <p>
 * 独立 {@link EventBusSubscriber}，不修改共享 {@code ClientSetup.java}。
 */
@EventBusSubscriber(modid = PasterDreamMod.MOD_ID, value = Dist.CLIENT)
public final class PDDreamnotesClientEvents {

    private PDDreamnotesClientEvents() {
    }

    /**
     * 绑定 dreamnotes_gui_0 → DreamnotesGui0Screen
     *
     * @param event 菜单屏幕注册事件
     */
    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(PDMenusDreamnotes.DREAMNOTES_GUI_0.get(), DreamnotesGui0Screen::new);
        PasterDreamMod.LOGGER.debug("[PDDreamnotesClientEvents] 注册 GUI 屏幕: dreamnotes_gui_0 → DreamnotesGui0Screen");
    }
}
