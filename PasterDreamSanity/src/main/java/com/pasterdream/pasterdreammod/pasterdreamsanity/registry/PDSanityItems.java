package com.pasterdream.pasterdreammod.pasterdreamsanity.registry;

import com.pasterdream.pasterdreammod.api.PasterDreamAPI;
import com.pasterdream.pasterdreammod.pasterdreamsanity.item.WhiteFlowerBodyItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * San 值系统物品注册类。
 * <p>
 * 白花胸针必须使用 {@code pasterdream} 命名空间（原版 ID、主模 lang、curios:body 数据包、
 * 剧情掉落均写死该 ID）。直接挂在本模事件总线、以 {@link PasterDreamAPI#DATA_NAMESPACE}
 * 注册，避免依赖 CurioAPI 共享 DeferredRegister 的跨模加载时序。
 * 槽位绑定由 {@code data/curios/tags/item/body.json} 完成。
 *
 * @author PasterDream
 */
public class PDSanityItems {

    /** 物品注册器（pasterdream 命名空间，与原版 ID 对齐） */
    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(PasterDreamAPI.DATA_NAMESPACE);

    /** 白花胸针 (white_flower_body) */
    public static final DeferredItem<Item> WHITE_FLOWER_BODY =
            ITEMS.register("white_flower_body", WhiteFlowerBodyItem::new);

    private PDSanityItems() {
    }

    /**
     * 将注册器挂到模组事件总线。
     *
     * @param modEventBus 模组事件总线
     */
    public static void init(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}
