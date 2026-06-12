package com.pasterdream.pasterdreammod.registry;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.menu.DreamCauldronMenu;
import com.pasterdream.pasterdreammod.menu.DyedreamDeskMenu;
import com.pasterdream.pasterdreammod.menu.MeltdreamChestMenu;
import com.pasterdream.pasterdreammod.menu.ShadowChestMenu;
import com.pasterdream.pasterdreammod.menu.TheEndlessBookOfDreamSeekersMenu;
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

    /**
     * 染梦书桌 GUI 菜单类型
     * 用于打开 1 格展示槽的界面（最大堆叠 1）
     */
    public static final DeferredHolder<MenuType<?>, MenuType<DyedreamDeskMenu>> DYEDREAM_DESK =
            MENUS.register("dyedream_desk",
                    () -> IMenuTypeExtension.create(DyedreamDeskMenu::new));

    /**
     * 融梦水晶箱 GUI 菜单类型
     * 用于打开 9 格容器的箱子界面
     */
    public static final DeferredHolder<MenuType<?>, MenuType<MeltdreamChestMenu>> MELTDREAM_CHEST =
            MENUS.register("meltdream_chest",
                    () -> IMenuTypeExtension.create(MeltdreamChestMenu::new));

    /**
     * 寻梦者的永恒书卷 GUI 菜单类型
     * 1 格展示槽 + 玩家背包
     */
    public static final DeferredHolder<MenuType<?>, MenuType<TheEndlessBookOfDreamSeekersMenu>> THE_ENDLESS_BOOK_OF_DREAM_SEEKERS =
            MENUS.register("the_endless_book_of_dream_seekers",
                    () -> IMenuTypeExtension.create(TheEndlessBookOfDreamSeekersMenu::new));

    /**
     * 梦境炼药锅 GUI 菜单类型
     * 3 输入槽 + 1 输出槽 + 玩家背包
     */
    public static final DeferredHolder<MenuType<?>, MenuType<DreamCauldronMenu>> DREAM_CAULDRON =
            MENUS.register("dream_cauldron",
                    () -> IMenuTypeExtension.create(DreamCauldronMenu::new));
}