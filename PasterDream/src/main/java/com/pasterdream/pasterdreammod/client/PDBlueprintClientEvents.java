package com.pasterdream.pasterdreammod.client;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.client.screen.BlueprintGui0Screen;
import com.pasterdream.pasterdreammod.registry.PDMenusBlueprint;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

import com.pasterdream.pasterdreammod.api.util.PDDebugLogger;
/**
 * 蓝图阅览子系统客户端事件（独立于 {@link ClientSetup}，避免共享文件冲突）。
 */
@EventBusSubscriber(modid = PasterDreamMod.MOD_ID, value = Dist.CLIENT)
public final class PDBlueprintClientEvents {

    private PDBlueprintClientEvents() {
    }

    /**
     * 注册蓝图阅览屏幕
     *
     * @param event 菜单屏幕注册事件
     */
    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(PDMenusBlueprint.BLUEPRINT_GUI_0.get(), BlueprintGui0Screen::new);
        PDDebugLogger.mainDebug("[PDBlueprintClientEvents] registered blueprint_gui_0 → BlueprintGui0Screen");
    }
}
