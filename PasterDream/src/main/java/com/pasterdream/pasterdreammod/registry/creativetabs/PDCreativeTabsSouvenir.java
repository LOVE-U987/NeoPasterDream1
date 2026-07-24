package com.pasterdream.pasterdreammod.registry.creativetabs;

import com.pasterdream.pasterdreammod.registry.PDCreativeTabs;
import com.pasterdream.pasterdreammod.registry.PDItems;
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
                        output.accept(PDItems.CRADLE_IN_ONES_ARMS.get());
                        output.accept(PDItems.SQUEAL_WAVE.get());
                        output.accept(PDItems.GUIDING_DRUG.get());
                        output.accept(PDItems.WHITE_COROLLA.get());
                        output.accept(PDItems.PALE_BONENEEDLE.get());
                        // 玩偶/雕像
                        output.accept(PDItems.QIN_DOLL_0.get());
                        output.accept(PDItems.LITTLE_PURPLE_DOLL_0.get());
                        output.accept(PDItems.GOLDEN_FOX_SCULPTURE.get());
                    })
                    .build());
}
