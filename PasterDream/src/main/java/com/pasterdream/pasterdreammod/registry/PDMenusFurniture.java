package com.pasterdream.pasterdreammod.registry;

import com.pasterdream.pasterdreammod.api.menu.MenuAPI;
import com.pasterdream.pasterdreammod.menu.PicnicBasketMenu;
import com.pasterdream.pasterdreammod.menu.ShadowDeskMenu;
import com.pasterdream.pasterdreammod.menu.WindmoorCrateMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredHolder;

/**
 * 容器/家具方块组菜单注册（[分区F]，波次 W4）。
 * <p>
 * 原版 menus 清单中的 picnic_basket_gui / shadow_desk_gui / windmoor_crate_gui，
 * 注册名沿用现有波次「去 _gui 后缀」惯例（对照 dream_cauldron_gui → dream_cauldron）。
 */
public class PDMenusFurniture {

    /**
     * 野餐篮 GUI 菜单类型
     * 15 格（5×3）+ 玩家背包
     */
    public static final DeferredHolder<MenuType<?>, MenuType<PicnicBasketMenu>> PICNIC_BASKET =
            MenuAPI.<PicnicBasketMenu>createMenu("picnic_basket")
                    .factory(PicnicBasketMenu::new)
                    .build();

    /**
     * 影之桌 GUI 菜单类型
     * 1 格展示槽 + 玩家背包
     */
    public static final DeferredHolder<MenuType<?>, MenuType<ShadowDeskMenu>> SHADOW_DESK =
            MenuAPI.<ShadowDeskMenu>createMenu("shadow_desk")
                    .factory(ShadowDeskMenu::new)
                    .build();

    /**
     * 风泊木箱 GUI 菜单类型
     * 15 格（5×3）+ 玩家背包
     */
    public static final DeferredHolder<MenuType<?>, MenuType<WindmoorCrateMenu>> WINDMOOR_CRATE =
            MenuAPI.<WindmoorCrateMenu>createMenu("windmoor_crate")
                    .factory(WindmoorCrateMenu::new)
                    .build();
}
