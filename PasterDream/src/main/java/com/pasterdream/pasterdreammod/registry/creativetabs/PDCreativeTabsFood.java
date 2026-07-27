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
 * 食物饮品创造模式标签页注册。
 *
 * @see PDCreativeTabs
 */
public class PDCreativeTabsFood {


    // ==================== 8. 食物饮品 ====================

    /**
     * 食物饮品标签页
     * 包含所有食物、饮料及烹饪原料
     */
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> FOOD_TAB = PDCreativeTabs.TABS.register("food_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.pasterdream.food_tab"))
                    .icon(() -> new ItemStack(PDItems.DYEDREAM_FRUIT.get()))
                    .withTabsBefore(PDCreativeTabs.WEAPON_TAB.getKey())
                    .displayItems((parameters, output) -> {
                        output.accept(PDItems.DYEDREAM_FRUIT.get());
                        output.accept(PDItems.DYEDREAM_JUICE.get());
                        output.accept(PDItems.DYEDREAM_FLOWER_TEA.get());
                        output.accept(PDItems.UNCOOKED_DYEDREAM_FLOWER_TEA.get());
                        output.accept(PDItems.DYEDREAM_POPSICLE.get());
                        output.accept(PDItems.DYEDREAM_FRUIT_BUNCAKE.get());
                        output.accept(PDItems.BUBBLE_TEA.get());
                        output.accept(PDItems.APPLE_JUICE.get());
                        output.accept(PDItems.HONEY_JUICE.get());
                        output.accept(PDItems.WATERMELON_JUICE.get());
                        output.accept(PDItems.BREAD_SLICE.get());
                        output.accept(PDItems.CAKE_BASE.get());
                        output.accept(PDItems.WAFER_BISCUIT.get());
                        output.accept(PDItems.CHOCOLATE.get());
                        output.accept(PDItems.CHOCOLATE_MATCHA_CAKE.get());
                        output.accept(PDItems.SWISS_ROLL.get());
                        output.accept(PDItems.STUFFED_WAFER_COOKIES.get());
                        output.accept(PDItems.CREAM_BUNCAKE.get());
                        output.accept(PDItems.BERRY_BUNCAKE.get());
                        output.accept(PDItems.POTATO_BUNCAKE.get());
                        output.accept(PDItems.MELON_BUNCAKE.get());
                        output.accept(PDItems.PUMPKIN_BUNCAKE.get());
                        output.accept(PDItems.GLOW_BERRY_BUNCAKE.get());
                        output.accept(PDItems.FIG.get());
                        output.accept(PDItems.BACONE_EGG.get());
                        output.accept(PDItems.ODD_BACONE_EGG.get());
                        output.accept(PDItems.FRIED_EGG.get());
                        output.accept(PDItems.SANDWICH.get());
                        output.accept(PDItems.RICECAKE.get());
                        output.accept(PDItems.GINGERBREAD_MAN.get());
                        output.accept(PDItems.CANDY_CANE.get());
                        output.accept(PDItems.AMBER_CANDY.get());
                        output.accept(PDItems.POPPING_CANDY.get());
                        output.accept(PDItems.BUBBLE_GUM.get());
                        output.accept(PDItems.SILVER_FOX_COTTON_CANDY.get());
                        output.accept(PDItems.HEART_CHOCOLATE_0.get());
                        output.accept(PDItems.HEART_CHOCOLATE_1.get());
                        output.accept(PDItems.HEART_CHOCOLATE_2.get());
                        output.accept(PDItems.PINEAPPLE_LOVE_SEA.get());
                        output.accept(PDItems.GOLDENROD_TEA.get());
                        output.accept(PDItems.LEGEND_DRAGON_HORN_ICE_CREAM.get());
                        // 水母相关食材（原版 paster_tab_4 食物栏；曾误挂 entity_tab）
                        output.accept(PDItems.JELLYFISH_MUD.get());
                        output.accept(PDItems.JELLYFISH_JELLO.get());
                        output.accept(PDItems.LIGHT_ORGAN.get());
                        output.accept(PDItems.QUEER_SOUP.get());
                        output.accept(PDItems.RAGE_ELIXIR_0.get());
                        output.accept(PDItems.ELIXIR_BOTTLE.get());
                        output.accept(PDItems.DYEDREAM_PERFUME.get());
                        output.accept(PDItems.WATER_GLASSJAR.get());
                        output.accept(PDItems.MILK_GLASSJAR.get());
                        output.accept(PDItems.GLASSJAR.get());
                        output.accept(PDItems.FLOUR.get());
                        output.accept(PDItems.YEAST.get());
                        output.accept(PDItems.EGGDOUGH.get());
                        output.accept(PDItems.COARSE_SALT.get());
                        output.accept(PDItems.SALT.get());
                    })
                    .build());
}
