package com.pasterdream.pasterdreammod.registry;

import com.pasterdream.pasterdreammod.api.menu.MenuAPI;
import com.pasterdream.pasterdreammod.menu.DreamAccumulatorMenu;
import com.pasterdream.pasterdreammod.menu.DreamCauldronMenu;
import com.pasterdream.pasterdreammod.menu.DyedreamDeskMenu;
import com.pasterdream.pasterdreammod.menu.ResearchTableMenu;
import com.pasterdream.pasterdreammod.menu.ShadowBlastFurnaceMenu;
import com.pasterdream.pasterdreammod.menu.ShadowSelectEndMenu;
import com.pasterdream.pasterdreammod.menu.StorageBagMenu;
import com.pasterdream.pasterdreammod.menu.MeltdreamChestMenu;
import com.pasterdream.pasterdreammod.menu.ShadowChestMenu;
import com.pasterdream.pasterdreammod.menu.TheEndlessBookOfDreamSeekersMenu;
import com.pasterdream.pasterdreammod.menu.PlayerBookMenu;
import com.pasterdream.pasterdreammod.menu.WeaponWorkshopMenu;
import com.pasterdream.pasterdreammod.menu.WorkshopAnvilMenu;
import com.pasterdream.pasterdreammod.menu.WorkshopBlastMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredHolder;

/**
 * 菜单类型注册类
 * <p>
 * 使用 {@link MenuAPI} 统一注册所有 AbstractContainerMenu 类型，避免维护独立的 DeferredRegister。
 */
public class PDMenus {

    /**
     * 影之箱 GUI 菜单类型
     * 用于打开 15 格容器的箱子界面
     */
    public static final DeferredHolder<MenuType<?>, MenuType<ShadowChestMenu>> SHADOW_CHEST =
            MenuAPI.<ShadowChestMenu>createMenu("shadow_chest")
                    .factory(ShadowChestMenu::new)
                    .build();

    /**
     * 染梦书桌 GUI 菜单类型
     * 用于打开 1 格展示槽的界面（最大堆叠 1）
     */
    public static final DeferredHolder<MenuType<?>, MenuType<DyedreamDeskMenu>> DYEDREAM_DESK =
            MenuAPI.<DyedreamDeskMenu>createMenu("dyedream_desk")
                    .factory(DyedreamDeskMenu::new)
                    .build();

    /**
     * 融梦水晶箱 GUI 菜单类型
     * 用于打开 9 格容器的箱子界面
     */
    public static final DeferredHolder<MenuType<?>, MenuType<MeltdreamChestMenu>> MELTDREAM_CHEST =
            MenuAPI.<MeltdreamChestMenu>createMenu("meltdream_chest")
                    .factory(MeltdreamChestMenu::new)
                    .build();

    /**
     * 寻梦者的永恒书卷 GUI 菜单类型
     * 1 格展示槽 + 玩家背包
     */
    public static final DeferredHolder<MenuType<?>, MenuType<TheEndlessBookOfDreamSeekersMenu>> THE_ENDLESS_BOOK_OF_DREAM_SEEKERS =
            MenuAPI.<TheEndlessBookOfDreamSeekersMenu>createMenu("the_endless_book_of_dream_seekers")
                    .factory(TheEndlessBookOfDreamSeekersMenu::new)
                    .build();

    /**
     * 玩家书籍管理菜单（创造模式）
     * 与寻梦者的永恒书卷共用相同 GUI 纹理，数据存储于玩家 NBT。
     */
    public static final DeferredHolder<MenuType<?>, MenuType<PlayerBookMenu>> PLAYER_BOOK =
            MenuAPI.<PlayerBookMenu>createMenu("player_book")
                    .factory(PlayerBookMenu::new)
                    .build();

    /**
     * 梦境炼药锅 GUI 菜单类型
     * 还原原版 7 槽布局：引导药剂 + 3 材料 + 液体桶输入/空桶回收 + 成品槽 + 玩家背包
     */
    public static final DeferredHolder<MenuType<?>, MenuType<DreamCauldronMenu>> DREAM_CAULDRON =
            MenuAPI.<DreamCauldronMenu>createMenu("dream_cauldron")
                    .factory(DreamCauldronMenu::new)
                    .build();

    // ==================== [分区W] 武器工坊群 ====================

    /**
     * 精铸工坊 GUI 菜单类型
     * 7 槽锻造布局（5 材料 + 强化石 + 产物）+ 锻造按钮
     */
    public static final DeferredHolder<MenuType<?>, MenuType<WeaponWorkshopMenu>> WEAPON_WORKSHOP =
            MenuAPI.<WeaponWorkshopMenu>createMenu("weapon_workshop")
                    .factory(WeaponWorkshopMenu::new)
                    .build();

    /**
     * 工坊铁砧 GUI 菜单类型
     * 2 槽（原胚/产物）+ 开始按钮 + 5 数字按钮小游戏
     */
    public static final DeferredHolder<MenuType<?>, MenuType<WorkshopAnvilMenu>> WORKSHOP_ANVIL =
            MenuAPI.<WorkshopAnvilMenu>createMenu("workshop_anvil")
                    .factory(WorkshopAnvilMenu::new)
                    .build();

    /**
     * 工坊锻炉 GUI 菜单类型
     * 5 槽（原胚/镶嵌/岩浆桶/空桶/产物）+ 岩浆储罐显示
     */
    public static final DeferredHolder<MenuType<?>, MenuType<WorkshopBlastMenu>> WORKSHOP_BLAST =
            MenuAPI.<WorkshopBlastMenu>createMenu("workshop_blast")
                    .factory(WorkshopBlastMenu::new)
                    .build();

    // ==================== [分区R] 研究台组 ====================

    /**
     * 研究台 GUI 菜单类型（原版 research_table_gui，按项目惯例去 _gui 后缀）
     * 6 槽研究布局 + 复制/研究两个按钮
     */
    public static final DeferredHolder<MenuType<?>, MenuType<ResearchTableMenu>> RESEARCH_TABLE =
            MenuAPI.<ResearchTableMenu>createMenu("research_table")
                    .factory(ResearchTableMenu::new)
                    .build();

    /**
     * 暗影高炉 GUI 菜单类型（原版 shadow_blast_furnace_gui，按项目惯例去 _gui 后缀）
     * 6 槽冶炼布局 + 进度/液体储罐显示
     */
    public static final DeferredHolder<MenuType<?>, MenuType<ShadowBlastFurnaceMenu>> SHADOW_BLAST_FURNACE =
            MenuAPI.<ShadowBlastFurnaceMenu>createMenu("shadow_blast_furnace")
                    .factory(ShadowBlastFurnaceMenu::new)
                    .build();

    /**
     * 蓄梦池 GUI 菜单类型（原版 dream_accumulator_gui，按项目惯例去 _gui 后缀）
     * 2 槽（产物/吸附剂），取走产物时重置蓄梦计时
     */
    public static final DeferredHolder<MenuType<?>, MenuType<DreamAccumulatorMenu>> DREAM_ACCUMULATOR =
            MenuAPI.<DreamAccumulatorMenu>createMenu("dream_accumulator")
                    .factory(DreamAccumulatorMenu::new)
                    .build();

    /**
     * 影之抉择 GUI 菜单类型（原版 shadow_select_end，无槽位选择界面）
     * 黑暗/光明两个图片按钮，由真影之床（后续波次）打开
     */
    public static final DeferredHolder<MenuType<?>, MenuType<ShadowSelectEndMenu>> SHADOW_SELECT_END =
            MenuAPI.<ShadowSelectEndMenu>createMenu("shadow_select_end")
                    .factory(ShadowSelectEndMenu::new)
                    .build();

    /**
     * 储物袋 GUI（9 格，原版 storage_bag_gui）
     */
    public static final DeferredHolder<MenuType<?>, MenuType<StorageBagMenu>> STORAGE_BAG =
            MenuAPI.<StorageBagMenu>createMenu("storage_bag")
                    .factory(StorageBagMenu::new)
                    .build();

    /**
     * 高级储物袋 GUI（25 格，原版 storage_bag_0_gui）
     */
    public static final DeferredHolder<MenuType<?>, MenuType<StorageBagMenu>> STORAGE_BAG_0 =
            MenuAPI.<StorageBagMenu>createMenu("storage_bag_0")
                    .factory(StorageBagMenu::new)
                    .build();
}

