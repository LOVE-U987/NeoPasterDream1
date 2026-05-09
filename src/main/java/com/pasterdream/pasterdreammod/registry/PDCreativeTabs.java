package com.pasterdream.pasterdreammod.registry;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 创造模式物品栏注册类
 * 配置所有创造模式标签页和物品显示
 */
public class PDCreativeTabs {

    /**
     * 创造模式物品栏注册器
     */
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(
            BuiltInRegistries.CREATIVE_MODE_TAB, PasterDreamMod.MOD_ID);

    /**
     * 基础材料与功能方块标签页
     * 包含核心功能方块如蓄梦池、染梦书桌等
     */
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> PASTER_TAB_0 = TABS.register("paster_tab_0",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.pasterdream.paster_tab_0"))
                    .icon(() -> new ItemStack(PDItems.DREAM_ACCUMULATOR.get()))
                    .displayItems((parameters, output) -> {
                        // 添加试点方块到物品栏
                        output.accept(PDItems.DREAM_ACCUMULATOR.get());
                        output.accept(PDItems.DYEDREAM_DESK.get());
                        output.accept(PDItems.LIFE_CRYSTAL.get());
                        output.accept(PDItems.SHADOW_CHEST.get());
                    })
                    .build());

    /**
     * 刷怪蛋标签页
     * 包含所有生物刷怪蛋
     */
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> SPAWN_EGGS_TAB = TABS.register("spawn_eggs_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.pasterdream.spawn_eggs_tab"))
                    .icon(() -> new ItemStack(PDItems.SHADOW_GOLEM_SPAWN_EGG.get()))
                    .withTabsBefore(PASTER_TAB_0.getKey())
                    .displayItems((parameters, output) -> {
                        // 添加刷怪蛋到物品栏
                        output.accept(PDItems.SHADOW_GOLEM_SPAWN_EGG.get());
                        output.accept(PDItems.PINK_SLIME_SPAWN_EGG.get());
                    })
                    .build());
}
