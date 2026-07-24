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
 * 盔甲装备创造模式标签页注册。
 *
 * @see PDCreativeTabs
 */
public class PDCreativeTabsArmor {


    // ==================== 6. 盔甲装备 ====================

    /**
     * 盔甲装备标签页
     * 包含所有盔甲套装及装备配件
     */
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> ARMOR_TAB = PDCreativeTabs.TABS.register("armor_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.pasterdream.armor_tab"))
                    .icon(() -> new ItemStack(PDItems.DYEDREAM_ARMOR_CHESTPLATE.get()))
                    .withTabsBefore(PDCreativeTabs.SOUVENIR_TAB.getKey())
                    .displayItems((parameters, output) -> {
                        // 铜盔甲套装
                        output.accept(PDItems.COPPER_ARMOR_HELMET.get());
                        output.accept(PDItems.COPPER_ARMOR_CHESTPLATE.get());
                        output.accept(PDItems.COPPER_ARMOR_LEGGINGS.get());
                        output.accept(PDItems.COPPER_ARMOR_BOOTS.get());
                        // 钛盔甲套装
                        output.accept(PDItems.TITANIUM_ARMOR_HELMET.get());
                        output.accept(PDItems.TITANIUM_ARMOR_CHESTPLATE.get());
                        output.accept(PDItems.TITANIUM_ARMOR_LEGGINGS.get());
                        output.accept(PDItems.TITANIUM_ARMOR_BOOTS.get());
                        // 潜声盔甲套装
                        output.accept(PDItems.SCULK_ARMOR_HELMET.get());
                        output.accept(PDItems.SCULK_ARMOR_CHESTPLATE.get());
                        output.accept(PDItems.SCULK_ARMOR_LEGGINGS.get());
                        output.accept(PDItems.SCULK_ARMOR_BOOTS.get());
                        // 染梦盔甲套装
                        output.accept(PDItems.DYEDREAM_ARMOR_HELMET.get());
                        output.accept(PDItems.DYEDREAM_ARMOR_CHESTPLATE.get());
                        output.accept(PDItems.DYEDREAM_ARMOR_LEGGINGS.get());
                        output.accept(PDItems.DYEDREAM_ARMOR_BOOTS.get());
                        // QYM盔甲套装
                        output.accept(PDItems.QIN_ARMOR_HELMET.get());
                        output.accept(PDItems.QIN_ARMOR_CHESTPLATE.get());
                        output.accept(PDItems.QIN_ARMOR_LEGGINGS.get());
                        output.accept(PDItems.QIN_ARMOR_BOOTS.get());
                    })
                    .build());
}
