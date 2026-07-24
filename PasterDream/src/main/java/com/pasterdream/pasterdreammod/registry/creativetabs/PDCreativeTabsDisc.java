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
 * 音乐唱片创造模式标签页注册。
 *
 * @see PDCreativeTabs
 */
public class PDCreativeTabsDisc {


    // ==================== 9. 音乐唱片 ====================

    /**
     * 音乐唱片标签页
     * 包含所有音乐唱片
     */
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> DISC_TAB = PDCreativeTabs.TABS.register("disc_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.pasterdream.disc_tab"))
                    .icon(() -> new ItemStack(PDItems.SWEETDREAM_DISC.get()))
                    .withTabsBefore(PDCreativeTabs.CURIO_TAB.getKey())
                    .displayItems((parameters, output) -> {
                        output.accept(PDItems.SWEETDREAM_DISC.get());
                        output.accept(PDItems.SNOWFALLDREAM_DISC.get());
                        output.accept(PDItems.AARONCOS_DISC.get());
                        output.accept(PDItems.DYEDREAM_WORLD_DISC.get());
                        output.accept(PDItems.WIND_JOURNEY_DISC.get());
                        output.accept(PDItems.WIND_JOURNEY_1_DISC.get());
                        output.accept(PDItems.DREAM_MEADOW_DISC.get());
                        output.accept(PDItems.DREAM_HEATH_DISC.get());
                        output.accept(PDItems.DREAM_TAIGA_DISC.get());
                        output.accept(PDItems.DREAM_DELTA_DISC.get());
                    })
                    .build());
}
