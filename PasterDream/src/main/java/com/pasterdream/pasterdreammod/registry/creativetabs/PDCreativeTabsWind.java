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
 * 风之旅途维度创造模式标签页注册。
 *
 * @see PDCreativeTabs
 */
public class PDCreativeTabsWind {


    // ==================== 4. 风之旅途维度 ====================

    /**
     * 风之旅途维度标签页
     * 包含风系物品、云朵方块及翅膀系列
     */
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> WIND_TAB = PDCreativeTabs.TABS.register("wind_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.pasterdream.wind_tab"))
                    .icon(() -> new ItemStack(PDItems.WIND_KNIGHT_FLAG.get()))
                    .withTabsBefore(PDCreativeTabs.SHADOW_TAB.getKey())
                    .displayItems((parameters, output) -> {
                        output.accept(PDItems.WIND_IRON_INGOT.get());
                        output.accept(PDItems.WIND_PLANT_EXTRACT.get());
                        output.accept(PDItems.WINDRUNNER_CRYSTAL.get());
                        output.accept(PDItems.PULSE_WINDRUNNER_CRYSTAL.get());
                        output.accept(PDItems.CONGEAL_WIND.get());
                        output.accept(PDBlocks.WINDRUNNER_CRYSTAL_ORE.get());
                        output.accept(PDBlocks.CONGEAL_WIND_ORE.get());
                        output.accept(PDItems.WIND_KNIGHT_FLAG.get());
                        output.accept(PDBlocks.CLOUD.get());
                        output.accept(PDBlocks.DARK_CLOUD.get());
                        output.accept(PDBlocks.THICK_CLOUD.get());

                        // 染梦维度 Phase2 风主题方块
                        output.accept(PDBlocks.WINDRUNNER_CRYSTAL_BLOCK.get());
                        output.accept(PDBlocks.CONGEAL_WIND_BLOCK.get());
                        output.accept(PDBlocks.STARCALL_BLOCK.get());
                        output.accept(PDBlocks.STARCALL_CRACK.get());
                        output.accept(PDBlocks.BIG_BUBBLE.get());
                        output.accept(PDBlocks.WHITE_SAND.get());
                        output.accept(PDBlocks.SALT_BLOCK.get());
                        output.accept(PDBlocks.CLARITY_GLASS.get());
                        output.accept(PDBlocks.CARVE_CLARITY_GLASS.get());
                        output.accept(PDBlocks.FRAME_CLARITY_GLASS.get());
                        output.accept(PDBlocks.CLARITY_GLASSPANE.get());
                        output.accept(PDBlocks.CARVE_CLARITY_GLASSPANE.get());
                        output.accept(PDBlocks.FRAME_CLARITY_GLASSPANE.get());
                        output.accept(PDBlocks.BREAKWIND_CURTAIN.get());
                        output.accept(PDBlocks.WINDIRON_BARS.get());

                        output.accept(PDItems.ANGEL_WING.get());
                        output.accept(PDItems.FORSAKENS_WING.get());
                        output.accept(PDItems.GROUND_WING.get());
                        output.accept(PDItems.MACHINE_WING.get());
                        output.accept(PDItems.WINGS_OF_FANG.get());
                        output.accept(PDItems.WIND_VANE.get());

                        // W4：风骑士唤醒台与风泊木箱
                        output.accept(PDItems.WIND_KNIGHT_SPAWNBLOCK_0.get());
                        output.accept(PDItems.WIND_KNIGHT_SPAWNBLOCK_1.get());
                        output.accept(PDItems.WIND_KNIGHT_SPAWNBLOCK_2.get());
                        output.accept(PDItems.WIND_KNIGHT_SPAWNBLOCK_3.get());
                        output.accept(PDItems.WIND_KNIGHT_SPAWNBLOCK_4.get());
                        output.accept(PDItems.WINDMOOR_CRATE.get());
                    })
                    .build());
}
