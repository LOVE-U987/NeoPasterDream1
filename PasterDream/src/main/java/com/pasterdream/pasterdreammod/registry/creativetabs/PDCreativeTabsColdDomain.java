package com.pasterdream.pasterdreammod.registry.creativetabs;

import com.pasterdream.pasterdreammod.registry.PDCreativeTabs;
import com.pasterdream.pasterdreammod.registry.items.PDItemsColdDomain;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;

/**
 * 冷域维度创造模式标签页注册。
 * <p>
 * 收纳冷域维度（cold_domain_world）的专属方块：雪地草坪、冷域泥土、冷域木头、
 * 去皮冷域木头、冷域树叶。
 *
 * @see PDCreativeTabs
 */
public class PDCreativeTabsColdDomain {

    // ==================== 冷域维度 ====================

    /**
     * 冷域维度标签页
     */
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> COLD_DOMAIN_TAB = PDCreativeTabs.TABS.register("cold_domain_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.pasterdream.cold_domain_tab"))
                    .icon(() -> new ItemStack(PDItemsColdDomain.SNOWY_COLD_DOMAIN_GRASS.get()))
                    .withTabsBefore(PDCreativeTabs.DYEDREAM_TAB.getKey())
                    .displayItems((parameters, output) -> {
                        // 地表方块
                        output.accept(PDItemsColdDomain.SNOWY_COLD_DOMAIN_GRASS.get());
                        output.accept(PDItemsColdDomain.COLD_DOMAIN_DIRT.get());
                        // 树木方块
                        output.accept(PDItemsColdDomain.COLD_DOMAIN_LOG.get());
                        output.accept(PDItemsColdDomain.STRIPPED_COLD_DOMAIN_LOG.get());
                        output.accept(PDItemsColdDomain.COLD_DOMAIN_LEAVES.get());
                    })
                    .build());
}
