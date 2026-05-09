package com.pasterdream.pasterdreammod.registry;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.world.inventory.ShadowChestMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 菜单类型注册类
 * 使用 DeferredRegister 模式注册所有 AbstractContainerMenu 类型
 */
public class PDMenus {

    /**
     * 菜单类型注册器
     */
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, PasterDreamMod.MOD_ID);

    /**
     * 影之箱 GUI 菜单类型
     * 用于打开 15 格容器的箱子界面
     */
    public static final DeferredHolder<MenuType<?>, MenuType<ShadowChestMenu>> SHADOW_CHEST =
            MENUS.register("shadow_chest",
                    () -> IMenuTypeExtension.create(ShadowChestMenu::new));
}