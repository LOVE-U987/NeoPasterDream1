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
                        // 风之旅途材料
                        output.accept(PDItems.WIND_IRON_INGOT.get());
                        output.accept(PDItems.WIND_PLANT_EXTRACT.get());
                        output.accept(PDItems.WINDRUNNER_CRYSTAL.get());
                        output.accept(PDItems.PULSE_WINDRUNNER_CRYSTAL.get());
                        output.accept(PDItems.CONGEAL_WIND.get());

                        // 风系矿石
                        output.accept(PDBlocks.WINDRUNNER_CRYSTAL_ORE.get());
                        output.accept(PDBlocks.CONGEAL_WIND_ORE.get());

                        // 风骑士旗帜
                        output.accept(PDItems.WIND_KNIGHT_FLAG.get());

                        // 云朵方块
                        output.accept(PDBlocks.CLOUD.get());
                        output.accept(PDBlocks.DARK_CLOUD.get());
                        output.accept(PDBlocks.THICK_CLOUD.get());

                        // 风系特产方块
                        output.accept(PDBlocks.WINDRUNNER_CRYSTAL_BLOCK.get());
                        output.accept(PDBlocks.CONGEAL_WIND_BLOCK.get());
                        output.accept(PDBlocks.STARCALL_BLOCK.get());
                        output.accept(PDBlocks.STARCALL_CRACK.get());
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

                        // 风泊木族
                        output.accept(PDBlocks.WINDMOOR_LOG.get());
                        output.accept(PDBlocks.WINDMOOR_WOOD.get());
                        output.accept(PDBlocks.STRIPPED_WINDMOOR_LOG.get());
                        output.accept(PDBlocks.STRIPPED_WINDMOOR_WOOD.get());
                        output.accept(PDBlocks.WINDMOOR_PLANKS.get());
                        output.accept(PDBlocks.WINDMOOR_STAIRS.get());
                        output.accept(PDBlocks.WINDMOOR_SLAB.get());
                        output.accept(PDBlocks.WINDMOOR_FENCE.get());
                        output.accept(PDBlocks.WINDMOOR_FENCE_GATE.get());
                        output.accept(PDBlocks.WINDMOOR_DOOR.get());
                        output.accept(PDBlocks.WINDMOOR_TRAPDOOR.get());
                        output.accept(PDBlocks.WINDMOOR_PRESSURE_PLATE.get());
                        output.accept(PDBlocks.WINDMOOR_BUTTON.get());
                        output.accept(PDBlocks.WINDMOOR_LEAVES_0.get());
                        output.accept(PDBlocks.WINDMOOR_LEAVES_1.get());
                        output.accept(PDBlocks.WINDMOOR_LEAVES_2.get());

                        // 苍青岩系列
                        output.accept(PDBlocks.CYAN_STONE.get());
                        output.accept(PDBlocks.CYAN_MOSS_STONE.get());
                        output.accept(PDBlocks.CYAN_STONE_BRICKS.get());
                        output.accept(PDBlocks.CYAN_STONE_BRICK_STAIRS.get());
                        output.accept(PDBlocks.CYAN_STONE_BRICK_SLAB.get());
                        output.accept(PDBlocks.CYAN_STONE_BRICK_WALL.get());
                        output.accept(PDBlocks.MOSSY_CYAN_STONE_BRICKS.get());
                        output.accept(PDBlocks.MOSSY_CYAN_STONE_BRICK_STAIRS.get());
                        output.accept(PDBlocks.MOSSY_CYAN_STONE_BRICK_SLAB.get());
                        output.accept(PDBlocks.MOSSY_CYAN_STONE_BRICK_WALL.get());
                        output.accept(PDBlocks.CHISELED_CYAN_STONE_BRICKS.get());
                        output.accept(PDBlocks.CYAN_STONE_PILLAR.get());
                        output.accept(PDBlocks.CYAN_STONE_PRESSURE_PLATE.get());
                        output.accept(PDBlocks.CYAN_STONE_BUTTON.get());

                        // 锈黑金属族与甲胄残骸族
                        output.accept(PDBlocks.RUST_BLACK_METAL_BLOCK.get());
                        output.accept(PDBlocks.RUST_BLACK_METAL_BLOCK_WALL.get());
                        output.accept(PDBlocks.RUST_BLACK_METAL_BLOCK_BARS.get());
                        output.accept(PDBlocks.ARMOR_WRECK_BLOCK_0.get());
                        output.accept(PDBlocks.ARMOR_WRECK_BLOCK_1.get());
                        output.accept(PDBlocks.ARMOR_WRECK_BLOCK_2.get());
                        output.accept(PDBlocks.ARMOR_WRECK_BLOCK_3.get());
                        output.accept(PDBlocks.ARMOR_WRECK_BLOCK_4.get());

                        // 弹射装置
                        output.accept(PDBlocks.EJECTION_PRESSURE_BLOCK.get());
                        output.accept(PDBlocks.EJECTION_PRESSURE_PLATE.get());

                        // 风之旅途地表装饰
                        output.accept(PDBlocks.ANGEL_BLOCK.get());
                        output.accept(PDBlocks.FIREFLY_NEST.get());
                        output.accept(PDBlocks.SMALL_STONE_SPIRIT_BLOCK.get());

                        // 翅膀与风系饰品
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
