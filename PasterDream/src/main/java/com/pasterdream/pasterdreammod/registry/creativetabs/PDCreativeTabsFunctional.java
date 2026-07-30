package com.pasterdream.pasterdreammod.registry.creativetabs;

import com.pasterdream.pasterdreammod.registry.PDCreativeTabs;
import com.pasterdream.pasterdreammod.registry.PDItems;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;


/**
 * 功能与装饰创造模式标签页注册。
 * <p>
 * 存放跨维度或中性的功能性方块、容器与装饰物，避免挤占维度专属页。
 *
 * @see PDCreativeTabs
 */
public class PDCreativeTabsFunctional {


    // ==================== 5. 功能与装饰 ====================

    /**
     * 功能与装饰标签页
     * 包含研究台、构梦刷怪笼、玻璃罐、野餐篮等跨维度/中立装饰容器
     */
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> FUNCTIONAL_TAB = PDCreativeTabs.TABS.register("functional_decor_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.pasterdream.functional_decor_tab"))
                    .icon(() -> new ItemStack(PDItems.RESEARCH_TABLE.get()))
                    .withTabsBefore(PDCreativeTabs.WIND_TAB.getKey())
                    .displayItems((parameters, output) -> {
                        output.accept(PDItems.RESEARCH_TABLE.get());
                        output.accept(PDItems.FORCED_TOWER.get());
                        output.accept(PDItems.DREAM_SPAWNER_0.get());
                        output.accept(PDItems.DREAM_SPAWNER_1.get());
                        output.accept(PDItems.ECOLOGY_GLASS_JAR.get());
                        output.accept(PDItems.FIREFLY_GLASS_JAR.get());
                        output.accept(PDItems.LIGHT_FIREFLY_GLASS_JAR.get());
                        output.accept(PDItems.PICNIC_BASKET.get());
                        output.accept(PDItems.BIRDS_NEST.get());
                        output.accept(PDItems.GOLDEN_FOX_SCULPTURE.get());
                        output.accept(PDItems.DESERT_HERO_TOMB.get());
                    })
                    .build());
}
