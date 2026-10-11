package com.pasterdream.pasterdreammod.registry.items;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.item.DreamseekerNotesItem;
import com.pasterdream.pasterdreammod.registry.PDItems;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.DeferredItem;

import com.pasterdream.pasterdreammod.api.util.PDDebugLogger;
/**
 * 可编辑寻梦者笔记物品分区注册 (dreamseeker_notes)。
 * <p>
 * 条目写入共享 {@link PDItems#ITEMS}；本类由 {@link EventBusSubscriber} 在 MOD 总线扫描时加载，
 * 确保 RegisterEvent 前完成 {@code DeferredItem} 填充。
 */
@EventBusSubscriber(modid = PasterDreamMod.MOD_ID)
public final class PDItemsDreamseeker {

    /** 可编辑寻梦者笔记（玩家可写）。 */
    public static final DeferredItem<DreamseekerNotesItem> DREAMSEEKER_NOTES =
            PDItems.ITEMS.register("dreamseeker_notes", DreamseekerNotesItem::new);

    private PDItemsDreamseeker() {
    }

    /**
     * 空监听：保证类在 common setup 前完成加载。
     *
     * @param event common setup
     */
    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        PDDebugLogger.mainInfo("[PDItemsDreamseeker] 可编辑笔记分区已加载 (dreamseeker_notes)");
    }

    /** 显式触发类加载。 */
    public static void bootstrap() {
        Object unused = DREAMSEEKER_NOTES;
    }
}
