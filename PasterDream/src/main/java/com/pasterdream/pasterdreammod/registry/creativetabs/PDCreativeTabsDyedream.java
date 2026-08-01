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
 * 染梦维度创造模式标签页注册。
 *
 * @see PDCreativeTabs
 */
public class PDCreativeTabsDyedream {


    // ==================== 2. 染梦维度 ====================

    /**
     * 染梦维度标签页
     * 包含染梦世界的所有原生方块、维度专属物品及功能方块
     */
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> DYEDREAM_TAB = PDCreativeTabs.TABS.register("dyedream_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.pasterdream.dyedream_tab"))
                    .icon(() -> new ItemStack(PDBlocks.DYEDREAM_BLOCK.get()))
                    .withTabsBefore(PDCreativeTabs.ENTITY_TAB.getKey())
                    .displayItems((parameters, output) -> {
                        // 功能方块
                        output.accept(PDItems.DREAM_ACCUMULATOR.get());
                        output.accept(PDItems.DYEDREAM_DESK.get());
                        output.accept(PDItems.LIFE_CRYSTAL.get());

                        // 天然方块
                        output.accept(PDBlocks.DYEDREAM_GRASS.get());
                        output.accept(PDBlocks.DYEDREAM_DIRT.get());
                        output.accept(PDBlocks.DYEDREAM_SAND.get());
                        output.accept(PDBlocks.DYEDREAM_BLOCK.get());
                        output.accept(PDBlocks.ICESTONE.get());
                        output.accept(PDBlocks.DYEDREAM_ICE.get());
                        output.accept(PDBlocks.DYEDREAM_PACKED_ICE.get());
                        output.accept(PDBlocks.BIG_BUBBLE.get());
                        output.accept(PDBlocks.PINKSLIME_BLOCK.get());
                        output.accept(PDBlocks.DYEDREAMQUARTZ_ORE.get());
                        output.accept(PDBlocks.DYEDREAMDUST_ORE.get());
                        output.accept(PDBlocks.AMBER_CANDY_ORE.get());
                        output.accept(PDBlocks.DYEDREAMQUARTZ_BLOCK.get());
                        output.accept(PDBlocks.SMOOTH_DYEDREAMQUARTZ_BLOCK.get());
                        output.accept(PDBlocks.BRICKS_DYEDREAMQUARTZ_BLOCK.get());
                        output.accept(PDBlocks.CHISELED_DYEDREAMQUARTZ_BLOCK.get());
                        output.accept(PDBlocks.PILLAR_DYEDREAMQUARTZ_BLOCK.get());
                        output.accept(PDBlocks.DYEDREAMQUARTZ_BLOCK_STAIRS.get());
                        output.accept(PDBlocks.DYEDREAMQUARTZ_BLOCK_SLAB.get());
                        output.accept(PDBlocks.DYEDREAMQUARTZ_BLOCK_WALL.get());

                        // 方解石系列
                        output.accept(PDBlocks.POLISHED_CALCITE.get());
                        output.accept(PDBlocks.POLISHED_CALCITE_STAIRS.get());
                        output.accept(PDBlocks.POLISHED_CALCITE_SLAB.get());
                        output.accept(PDBlocks.POLISHED_CALCITE_WALL.get());
                        output.accept(PDBlocks.CALCITE_TILES.get());
                        output.accept(PDBlocks.CALCITE_TILES_STAIRS.get());
                        output.accept(PDBlocks.CALCITE_TILES_SLAB.get());
                        output.accept(PDBlocks.CALCITE_TILES_WALL.get());

                        // 树木与木板
                        output.accept(PDBlocks.DYEDREAM_LOG.get());
                        output.accept(PDBlocks.DYEDREAM_WOOD.get());
                        output.accept(PDBlocks.DYEDREAM_LEAVES.get());
                        output.accept(PDBlocks.DYEDREAM_GLOWING_LEAVES.get());
                        output.accept(PDBlocks.DYEDREAM_FALLEN_LEAVES.get());
                        output.accept(PDBlocks.DYEDREAM_HANGING_VINE.get());
                        output.accept(PDBlocks.DYEDREAM_WORLDTREE_LEAVES.get());
                        output.accept(PDBlocks.DYEDREAM_PLANKS.get());
                        output.accept(PDBlocks.DYEDREAM_PLANKS_STAIRS.get());
                        output.accept(PDBlocks.DYEDREAM_PLANKS_SLAB.get());
                        output.accept(PDBlocks.DYEDREAM_PLANKS_FENCE.get());
                        output.accept(PDBlocks.DYEDREAM_PLANKS_FENCEGATE.get());
                        output.accept(PDBlocks.DYEDREAM_PLANKS_DOOR.get());
                        output.accept(PDBlocks.DYEDREAM_PLANKS_TRAPDOOR.get());
                        output.accept(PDBlocks.DYEDREAM_PLANKS_PRESSURE_PLATE.get());
                        output.accept(PDBlocks.DYEDREAM_PLANKS_BUTTON.get());
                        output.accept(PDBlocks.DYEDREAM_PLANKS_PANE.get());

                        // 花蕾系列
                        output.accept(PDBlocks.DYEDREAM_BUD_BLOCK.get());
                        output.accept(PDBlocks.DYEDREAM_BUD_STAIRS.get());
                        output.accept(PDBlocks.DYEDREAM_BUD_SLAB.get());
                        output.accept(PDBlocks.DYEDREAM_BUD_WALL.get());
                        output.accept(PDBlocks.DYEDREAM_BUD_0.get());
                        output.accept(PDBlocks.DYEDREAM_BUD_1.get());
                        output.accept(PDBlocks.DYEDREAM_BUD_2.get());
                        output.accept(PDBlocks.ICE_BUD_0.get());

                        // 粉丁菇
                        output.accept(PDBlocks.PINKAGARIC_0.get());
                        output.accept(PDBlocks.PINKAGARIC_1.get());
                        output.accept(PDBlocks.PINKAGARIC_2.get());
                        output.accept(PDBlocks.PINKAGARIC_3.get());

                        // 染梦花草
                        output.accept(PDBlocks.FLOWER_1.get());
                        output.accept(PDBlocks.FLOWER_2.get());
                        output.accept(PDBlocks.FLOWER_3.get());
                        output.accept(PDBlocks.FLOWER_5.get());
                        output.accept(PDBlocks.FLOWER_6.get());
                        output.accept(PDBlocks.FLOWER_7.get());
                        output.accept(PDBlocks.FLOWER_8.get());
                        output.accept(PDBlocks.FLOWER_9.get());
                        output.accept(PDBlocks.FLOWER_10.get());
                        output.accept(PDBlocks.FLOWER_11.get());
                        output.accept(PDBlocks.FLOWER_12.get());
                        output.accept(PDBlocks.FLOWER_13.get());
                        output.accept(PDBlocks.FLOWER_14.get());
                        output.accept(PDBlocks.FLOWER_15.get());
                        output.accept(PDBlocks.FLOWER_16.get());
                        output.accept(PDBlocks.FLOWER_17.get());
                        output.accept(PDBlocks.FLOWER_18.get());

                        // 染梦草
                        output.accept(PDBlocks.GRASS_1.get());
                        output.accept(PDBlocks.GRASS_2.get());
                        output.accept(PDBlocks.GRASS_3.get());
                        output.accept(PDBlocks.GRASS_4.get());
                        output.accept(PDBlocks.GRASS_5.get());
                        output.accept(PDBlocks.GRASS_6.get());
                        output.accept(PDBlocks.GRASS_7.get());
                        output.accept(PDBlocks.GRASS_8.get());
                        output.accept(PDBlocks.GRASS_9.get());
                        output.accept(PDBlocks.GRASS_10.get());
                        output.accept(PDBlocks.GRASS_11.get());
                        output.accept(PDBlocks.GRASS_12.get());
                        output.accept(PDBlocks.GRASS_13.get());
                        output.accept(PDBlocks.GRASS_14.get());
                        output.accept(PDBlocks.GRASS_15.get());

                        // 水面植物
                        output.accept(PDBlocks.DYEDREAM_LILY_PAD.get());
                        output.accept(PDBlocks.DYEDREAM_LOTUS.get());
                        output.accept(PDBlocks.DYEDREAM_SEAGRASS.get());

                        // 树苗与裂纹
                        output.accept(PDBlocks.DYEDREAM_SAPLING.get());
                        output.accept(PDBlocks.DYEDREAM_CRACK.get());

                        // 装饰方块
                        output.accept(PDBlocks.DYEDREAM_GLASS.get());
                        output.accept(PDBlocks.DYEDREAM_GLASSPANE.get());
                        output.accept(PDBlocks.CARVE_DYEDREAM_GLASS.get());
                        output.accept(PDBlocks.CARVE_DYEDREAM_GLASSPANE.get());
                        output.accept(PDBlocks.GOLD_CARVE_DYEDREAM_GLASS.get());
                        output.accept(PDBlocks.GOLD_CARVE_DYEDREAM_GLASSPANE.get());
                        output.accept(PDBlocks.DYEDREAM_LARTERN.get());

                        // 染梦维度专属物品
                        output.accept(PDItems.DYEDREAM_INGOT.get());
                        output.accept(PDItems.DYEDREAM_NUGGET.get());
                        output.accept(PDItems.DYEDREAM_DUST.get());
                        output.accept(PDItems.DYEDREAM_DUST_PIECE.get());
                        output.accept(PDItems.DYEDREAM_BASE.get());
                        output.accept(PDItems.DYEDREAM_DYE.get());
                        output.accept(PDItems.DYEDREAM_BUD_NUGGET.get());
                        output.accept(PDItems.DYEDREAMQUARTZ.get());
                        output.accept(PDItems.DYEDREAM_UPGRADE.get());
                        output.accept(PDItems.DYEDREAM_TELEPORT_CRYSTAL.get());
                        output.accept(PDItems.DYEDREAM_PERFUME.get());
                        output.accept(PDItems.DREAM_METER.get());
                        output.accept(PDItems.DREAMWISH.get());
                        output.accept(PDItems.DREAM_AURORIAN_STEEL.get());
                        output.accept(PDItems.DREAM_FERTILIZER.get());
                        output.accept(PDItems.DREAM_COTTON_CANDY.get());
                        output.accept(PDItems.MELTDREAM_CRYSTAL_0.get());
                        output.accept(PDItems.MELTDREAM_ELIXIR_BOTTLE.get());
                        output.accept(PDItems.DYEDREAM_FRUIT.get());
                        output.accept(PDItems.DYEDREAM_JUICE.get());
                        // 杂项补全：梦境果汁（原版装备栏 tab_3，饮用后获得梦愿效果）
                        output.accept(PDItems.DREAMJUICE.get());
                        output.accept(PDItems.DYEDREAM_FLOWER_TEA.get());
                        output.accept(PDItems.UNCOOKED_DYEDREAM_FLOWER_TEA.get());
                        output.accept(PDItems.DYEDREAM_POPSICLE.get());
                        output.accept(PDItems.DYEDREAM_FRUIT_BUNCAKE.get());
                        output.accept(PDItems.DYEDREAM_COROLLA.get());

                        // Phase 1: 移植方块
                        output.accept(PDBlocks.PEBBLE_0.get());
                        output.accept(PDBlocks.GOLDENROD.get());
                        output.accept(PDBlocks.CROP_0A.get());
                        output.accept(PDBlocks.CROP_1A.get());
                        output.accept(PDBlocks.CROP_2A.get());
                        output.accept(PDBlocks.CROP_3A.get());
                        output.accept(PDBlocks.CROP_4A.get());
                        output.accept(PDBlocks.VINE_0.get());

                        // 融梦水晶箱
                        output.accept(PDItems.MELTDREAM_CHEST.get());
                        output.accept(PDItems.MELTDREAM_CHEST_OPEN.get());

                        // 梦境炼药锅
                        output.accept(PDItems.DREAM_CAULDRON.get());

                        // 寻梦者的永恒书卷
                        output.accept(PDItems.THE_ENDLESS_BOOK_OF_DREAM_SEEKERS.get());

                        // 梦境列车结构方块
                        output.accept(PDItems.DREAM_TRAIN_STRUCTURE.get());

                        // 融梦涌泉桶
                        output.accept(PDItems.MELTDREAM_LIQUID_BUCKET.get());
                    })
                    .build());
}
