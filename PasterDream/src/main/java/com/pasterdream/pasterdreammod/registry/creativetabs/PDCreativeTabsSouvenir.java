package com.pasterdream.pasterdreammod.registry.creativetabs;

import com.pasterdream.pasterdreammod.registry.PDCreativeTabs;
import com.pasterdream.pasterdreammod.registry.PDItems;
import com.pasterdream.pasterdreammod.registry.items.PDItemsDreamnotes;
import com.pasterdream.pasterdreammod.registry.PDBlocks;
import com.pasterdream.pasterdreammod.registry.PDEntities;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;


/**
 * 纪念品创造模式标签页注册。
 *
 * @see PDCreativeTabs
 */
public class PDCreativeTabsSouvenir {


    // ==================== 5. 纪念品 ====================

    /**
     * 纪念品标签页
     * 包含特殊功能道具、剧情物品与收藏品
     */
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> SOUVENIR_TAB = PDCreativeTabs.TABS.register("souvenir_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.pasterdream.souvenir_tab"))
                    .icon(() -> new ItemStack(PDItems.MEMENTO_ITEM_01.get()))
                    .withTabsBefore(PDCreativeTabs.WIND_TAB.getKey())
                    .displayItems((parameters, output) -> {
                        output.accept(PDItems.DREAM_COIN_0.get());
                        output.accept(PDItems.DREAM_COIN_1.get());
                        output.accept(PDItems.BLUE_DEW.get());
                        output.accept(PDItems.RED_DEW_0.get());
                        output.accept(PDItems.MEMENTO_ITEM_01.get());
                        output.accept(PDItems.MEMENTO_ITEM_02.get());
                        output.accept(PDItems.MEMENTO_ITEM_03.get());
                        output.accept(PDItems.MEMENTO_ITEM_04.get());
                        output.accept(PDItems.MEMENTO_ITEM_05.get());
                        output.accept(PDItems.MEMENTO_ITEM_06.get());
                        output.accept(PDItems.MEMENTO_ITEM_07.get());
                        output.accept(PDItems.MEMENTO_ITEM_08.get());
                        output.accept(PDItems.MEMENTO_ITEM_09.get());
                        output.accept(PDItems.MEMENTO_ITEM_10.get());
                        output.accept(PDItems.MEMORY_GEM_0.get());
                        output.accept(PDItems.BROKENNOTES_0.get());
                        output.accept(PDItems.UNKNOWNNOTES_0.get());
                        // 寻梦者笔记完整卷册（原版 paster_tab_6）
                        output.accept(PDItemsDreamnotes.DREAMNOTES_0.get());
                        output.accept(PDItemsDreamnotes.DREAMNOTES_1.get());
                        output.accept(PDItemsDreamnotes.DREAMNOTES_2.get());
                        output.accept(PDItemsDreamnotes.DREAMNOTES_3.get());
                        output.accept(PDItemsDreamnotes.DREAMNOTES_4.get());
                        output.accept(PDItemsDreamnotes.DREAMNOTES_5.get());
                        output.accept(PDItemsDreamnotes.DREAMNOTES_6.get());
                        output.accept(PDItemsDreamnotes.DREAMNOTES_7.get());
                        output.accept(PDItemsDreamnotes.DREAMNOTES_8.get());
                        output.accept(PDItemsDreamnotes.DREAMNOTES_9.get());
                        output.accept(PDItemsDreamnotes.DREAMNOTES_10.get());
                        output.accept(PDItemsDreamnotes.DREAMNOTES_11.get());
                        output.accept(PDItemsDreamnotes.DREAMNOTES_12.get());
                        output.accept(PDItemsDreamnotes.DREAMNOTES_13.get());
                        output.accept(PDItemsDreamnotes.DREAMNOTES_14.get());
                        output.accept(PDItems.BLUEPRINT_0.get());
                        output.accept(PDItems.BLUEPRINT_1.get());
                        output.accept(PDItems.DREAMHARP_OF_WANDERER.get());
                        output.accept(PDItems.SHADOW_HAND_LANTERN.get());
                        output.accept(PDItems.BONE_WING_FIRE_BALL.get());
                        output.accept(PDItems.CRADLE_IN_ONES_ARMS.get());
                        output.accept(PDItems.SQUEAL_WAVE.get());
                        output.accept(PDItems.GUIDING_DRUG.get());
                        // 法术物品（梦境炼药锅炼制产出）
                        output.accept(PDItems.LIGHTNING_SPELL.get());
                        output.accept(PDItems.POISON_SPELL.get());
                        output.accept(PDItems.HEALING_SPELL.get());
                        output.accept(PDItems.FURY_SPELL.get());
                        output.accept(PDItems.ICE_SPELL.get());
                        output.accept(PDItems.WHITE_COROLLA.get());
                        output.accept(PDItems.PALE_BONENEEDLE.get());
                        // 玩偶/雕像
                        output.accept(PDItems.QIN_DOLL_0.get());
                        output.accept(PDItems.LITTLE_PURPLE_DOLL_0.get());
                        output.accept(PDItems.GOLDEN_FOX_SCULPTURE.get());

                        // 杂项补全：特殊功能道具与收藏品（原版物品栏 tab_0）
                        output.accept(PDItems.TIME_HOURGLASS.get());
                        output.accept(PDItems.STORAGE_BAG.get());
                        output.accept(PDItems.STORAGE_BAG_0.get());
                        output.accept(PDItems.ROOTS_PALE_BONENEEDLE.get());
                        output.accept(PDItems.TURN_PALE_CECILIA.get());
                        output.accept(PDItems.DEEP_TREASURE_0.get());
                        output.accept(PDItems.DEEP_TREASURE_1.get());
                        // 卡勒占卜卡牌 0..9
                        output.accept(PDItems.CALLE_CARD_0.get());
                        output.accept(PDItems.CALLE_CARD_1.get());
                        output.accept(PDItems.CALLE_CARD_2.get());
                        output.accept(PDItems.CALLE_CARD_3.get());
                        output.accept(PDItems.CALLE_CARD_4.get());
                        output.accept(PDItems.CALLE_CARD_5.get());
                        output.accept(PDItems.CALLE_CARD_6.get());
                        output.accept(PDItems.CALLE_CARD_7.get());
                        output.accept(PDItems.CALLE_CARD_8.get());
                        output.accept(PDItems.CALLE_CARD_9.get());
                        // 标签图标物品（原版调试栏/拓展栏 tab_8/tab_9 图标）
                        output.accept(PDItems.TABITEM_1.get());
                        output.accept(PDItems.TABITEM_2.get());
                        // 战利品生成工具（原版调试栏 tab_8）
                        output.accept(PDItems.LOOTSTABLE_CREATE_0.get());
                        output.accept(PDItems.LOOTSTABLE_CREATE_1.get());
                        output.accept(PDItems.LOOTSTABLE_CREATE_2.get());
                        output.accept(PDItems.LOOTSTABLE_CREATE_3.get());
                        output.accept(PDItems.LOOTSTABLE_CREATE_4.get());
                        output.accept(PDItems.LOOTSTABLE_CREATE_5.get());
                        output.accept(PDItems.LOOTSTABLE_CREATE_6.get());
                        output.accept(PDItems.LOOTSTABLE_CREATE_7.get());
                        output.accept(PDItems.LOOTSTABLE_CREATE_8.get());
                        output.accept(PDItems.LOOTSTABLE_CREATE_9.get());
                    })
                    .build());
}
