package com.pasterdream.pasterdreammod.registry;

import com.pasterdream.pasterdreammod.api.menu.MenuAPI;
import com.pasterdream.pasterdreammod.menu.BlueprintGui0Menu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredHolder;

/**
 * 蓝图阅览子系统菜单注册（独立于 {@link PDMenus}，避免与其他 agent 冲突）。
 * <p>
 * 注册名还原原版 {@code blueprint_gui_0}。类加载由 {@link com.pasterdream.pasterdreammod.data.BluePrintLoader}
 * 静态块触发，无需改 {@code PasterDreamMod}。
 */
public final class PDMenusBlueprint {

    /**
     * 蓝图阅览 GUI 菜单类型
     */
    public static final DeferredHolder<MenuType<?>, MenuType<BlueprintGui0Menu>> BLUEPRINT_GUI_0 =
            MenuAPI.<BlueprintGui0Menu>createMenu("blueprint_gui_0")
                    .factory(BlueprintGui0Menu::new)
                    .build();

    private PDMenusBlueprint() {
    }
}
