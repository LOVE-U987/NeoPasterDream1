package com.pasterdream.pasterdreammod.registry.items;

import com.pasterdream.pasterdreammod.item.BlueprintItem;
import net.neoforged.neoforge.registries.DeferredItem;

/**
 * 蓝图物品聚合别名。实际注册沿用 {@link PDItemsFunctional}，避免同一注册名被
 * 两个分区重复写入；蓝图加载器可通过本类稳定触发物品分区初始化。
 */
public final class PDItemsBlueprint {

    public static final DeferredItem<BlueprintItem> BLUEPRINT_0 = PDItemsFunctional.BLUEPRINT_0;
    public static final DeferredItem<BlueprintItem> BLUEPRINT_1 = PDItemsFunctional.BLUEPRINT_1;

    private PDItemsBlueprint() {
    }

    public static void bootstrap() {
        Object unused = BLUEPRINT_0;
    }
}
