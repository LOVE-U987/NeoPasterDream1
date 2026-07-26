package com.pasterdream.pasterdreammod.registry;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.api.menu.MenuAPI;
import com.pasterdream.pasterdreammod.menu.DreamnotesGui0Menu;
import com.pasterdream.pasterdreammod.registry.items.PDItemsDreamnotes;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.DeferredHolder;

/**
 * 寻梦者笔记菜单分区注册 (dreamnotes_gui_0)。
 * <p>
 * 经 {@link MenuAPI} 写入 API 的 DeferredRegister；本类由
 * {@link EventBusSubscriber} 扫描加载，确保条目在 RegisterEvent 前填充。
 * 勿改共享 {@code PDMenus.java}，合并 re-export 见 staging。
 */
@EventBusSubscriber(modid = PasterDreamMod.MOD_ID)
public final class PDMenusDreamnotes {

    /** 笔记阅读 GUI（原版注册名 dreamnotes_gui_0） */
    public static final DeferredHolder<MenuType<?>, MenuType<DreamnotesGui0Menu>> DREAMNOTES_GUI_0 =
            MenuAPI.<DreamnotesGui0Menu>createMenu("dreamnotes_gui_0")
                    .factory(DreamnotesGui0Menu::new)
                    .build();

    private PDMenusDreamnotes() {
    }

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        PasterDreamMod.LOGGER.debug("[PDMenusDreamnotes] dreamnotes_gui_0 菜单分区已加载");
    }

    /** 显式触发类加载（同时拉起物品分区） */
    public static void bootstrap() {
        Object unused = DREAMNOTES_GUI_0;
        PDItemsDreamnotes.bootstrap();
    }
}
