package com.pasterdream.pasterdreammod.pasterdreamsanity.registry;

import com.pasterdream.pasterdreammod.api.PasterDreamAPI;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * San 值系统物品注册类。
 * <p>
 * 白花胸针（white_flower_body）已合并注册至 PasterDream 主模组
 * （{@code PasterDream/.../registry/items/PDItemsCurios.java}，pasterdream 命名空间），
 * 以消除主模组对附属模组的反向依赖。本类不再注册任何物品。
 *
 * @author PasterDream
 */
public class PDSanityItems {

    /** 物品注册器（pasterdream 命名空间，与原版 ID 对齐）—— 已无待注册物品，保留以兼容旧存档时序 */
    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(PasterDreamAPI.DATA_NAMESPACE);

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
