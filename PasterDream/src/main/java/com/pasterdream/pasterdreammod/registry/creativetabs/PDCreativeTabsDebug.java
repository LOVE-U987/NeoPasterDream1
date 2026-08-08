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
 * 调试功能创造模式标签页注册。
 *
 * @see PDCreativeTabs
 */
public class PDCreativeTabsDebug {


    // ==================== 10. 调试功能 ====================

    /**
     * 调试功能标签页
     * 包含用于快速生成遗迹结构的调试法杖，仅在开发阶段使用
     */
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> DEBUG_TAB = PDCreativeTabs.TABS.register("debug_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.pasterdream.debug_tab"))
                    .icon(() -> new ItemStack(PDItems.DEBUG_WAND_DREAM_TRAIN.get()))
                    .withTabsBefore(PDCreativeTabs.DISC_TAB.getKey())
                    .displayItems((parameters, output) -> {
                        output.accept(PDItems.DEBUG_WAND_DREAM_TRAIN.get());
                        output.accept(PDItems.DEBUG_WAND_WORLDTREE_0.get());
                        output.accept(PDItems.DEBUG_WAND_WORLDTREE.get()); // worldtree_1 / true
                        output.accept(PDItems.DEBUG_WAND_PINKAGARIC_0.get());
                        output.accept(PDItems.DEBUG_WAND_PINKAGARIC_1.get());
                        output.accept(PDItems.DEBUG_WAND_PINKAGARIC_2.get());
                        output.accept(PDItems.DEBUG_WAND_PINKAGARIC_3.get());
                        output.accept(PDItems.DEBUG_WAND_DYEDREAM_CRACK.get());
                        output.accept(PDItems.DEBUG_WAND_DESERT_COTTAGE.get());
                        output.accept(PDItems.DEBUG_WAND_CLOUD_BUBBLE.get());
                        output.accept(PDItems.DEBUG_WAND_FLOATING_ICE_MOUND.get());
                        output.accept(PDItems.DEBUG_WAND_ICE_ARCH.get());
                        output.accept(PDItems.DEBUG_WAND_ICE_ARCH_RUINED.get());
                        output.accept(PDItems.DEBUG_WAND_DYEDREAM_ICE_PILLAR.get());
                        output.accept(PDItems.DEBUG_WAND_ICE_CRYSTAL_CLUSTER.get());
                        output.accept(PDItems.DEBUG_WAND_FROST_SPIKE.get());
                        output.accept(PDItems.DEBUG_WAND_ICE_GATE.get());
                        output.accept(PDItems.DEBUG_WAND_ICE_SPIKE.get());
                        output.accept(PDItems.DEBUG_WAND_ICE_CRYSTAL_GARDEN.get());
                        output.accept(PDItems.DEBUG_WAND_ICE_CRYSTAL_SPIKE.get());
                        output.accept(PDItems.DEBUG_WAND_ICE_PILLAR.get());
                        output.accept(PDItems.DEBUG_WAND_UNDERWATER_ICE_SPIKE.get());
                        output.accept(PDItems.DEBUG_WAND_SEA_ICE_MOUND.get());
                        output.accept(PDItems.DEBUG_WAND_CORAL_REEF.get());
                        output.accept(PDItems.DEBUG_WAND_CORAL_REEF_PINK.get());
                        output.accept(PDItems.DEBUG_WAND_MEGA_MUSHROOM.get());
                        output.accept(PDItems.DEBUG_WAND_MEGA_CALCITE_PILLAR.get());
                        output.accept(PDItems.DEBUG_WAND_PINKAGARIC_CLUSTER.get());
                        output.accept(PDItems.DEBUG_WAND_CALCITE_PILLAR.get());
                        output.accept(PDItems.DEBUG_WAND_SEAGRASS.get());
                        output.accept(PDItems.DEBUG_WAND_GRASS.get());
                        output.accept(PDItems.DEBUG_WAND_BUDS.get());
                        output.accept(PDItems.DEBUG_WAND_LOTUS.get());
                        output.accept(PDItems.DEBUG_WAND_LILY_PAD.get());
                        output.accept(PDItems.DEBUG_WAND_PINKAGARIC.get());
                        // 染梦世界装饰物调试水晶
                        output.accept(PDItems.DEBUG_WAND_DYEDREAM_CRYSTAL_CLUSTER.get());
                        output.accept(PDItems.DEBUG_WAND_MELTDREAM_CRYSTAL_PILLAR.get());
                        output.accept(PDItems.DEBUG_WAND_FLOATING_CLOUD_ISLAND.get());
                        output.accept(PDItems.DEBUG_WAND_CALCITE_CRYSTAL_GARDEN.get());
                        output.accept(PDItems.DEBUG_WAND_WARM_CRYSTAL_SPIKE.get());
                        output.accept(PDItems.DEBUG_WAND_PINKAGARIC_FOREST.get());
                        // 染梦世界树木调试水晶
                        output.accept(PDItems.DEBUG_WAND_DYEDREAM_TREE.get());
                        output.accept(PDItems.DEBUG_WAND_DYEDREAM_TREE_LARGE.get());
                        output.accept(PDItems.DEBUG_WAND_DYEDREAM_TREE_WEEPING.get());
                        output.accept(PDItems.DEBUG_WAND_DYEDREAM_TREE_BUSHY.get());
                        output.accept(PDItems.DEBUG_WAND_DYEDREAM_TREE_FANCY.get());
                        output.accept(PDItems.DEBUG_WAND_DYEDREAM_TREE_GLOWING.get());
                        output.accept(PDItems.DEBUG_WAND_DYEDREAM_TREE_ICY.get());
                        // Better Biomes 移植树调试水晶
                        output.accept(PDItems.DEBUG_WAND_DYEDREAM_TREE_TALLBIRCH.get());
                        output.accept(PDItems.DEBUG_WAND_DYEDREAM_TREE_BLOSSOM.get());
                        output.accept(PDItems.DEBUG_WAND_DYEDREAM_TREE_ASPEN.get());
                        output.accept(PDItems.DEBUG_WAND_DYEDREAM_TREE_ASPEN_MID.get());
                        output.accept(PDItems.DEBUG_WAND_DYEDREAM_TREE_ASPEN_SMALL.get());
                        output.accept(PDItems.DEBUG_WAND_DYEDREAM_TREE_POPLAR.get());
                        output.accept(PDItems.DEBUG_WAND_DYEDREAM_TREE_BUSH.get());
                        output.accept(PDItems.DEBUG_WAND_DYEDREAM_TREE_CHERRYBUSH.get());
                        output.accept(PDItems.DEBUG_WAND_DYEDREAM_TREE_PLAIN.get());
                        output.accept(PDItems.DEBUG_WAND_DYEDREAM_TREE_PALM.get());
                        output.accept(PDItems.DEBUG_WAND_DYEDREAM_TREE_SNOW.get());
                        output.accept(PDItems.DEBUG_WAND_DYEDREAM_TREE_CONIFER.get());
                        // 竞技场结构调试法杖
                        output.accept(PDItems.DEBUG_WAND_AARONCOS_ARENA.get());
                        // P0 移植遗迹调试水晶
                        output.accept(PDItems.DEBUG_WAND_DYEDREAM_FLOATING_TEMPLE.get());
                        output.accept(PDItems.DEBUG_WAND_DREAM_CHURCH_0.get());
                        output.accept(PDItems.DEBUG_WAND_DREAM_CHURCH_1.get());
                        output.accept(PDItems.DEBUG_WAND_DREAM_CHURCH_2.get());
                        output.accept(PDItems.DEBUG_WAND_DREAM_CHURCH_3.get());
                        output.accept(PDItems.DEBUG_WAND_DREAM_CHURCH_4.get());
                        output.accept(PDItems.DEBUG_WAND_DREAM_CHURCH_5.get());
                        output.accept(PDItems.DEBUG_WAND_DREAM_CHURCH_6.get());
                        output.accept(PDItems.DEBUG_WAND_DREAM_CHURCH_7.get());
                        output.accept(PDItems.DEBUG_WAND_DREAM_CHURCH_8.get());
                        output.accept(PDItems.DEBUG_WAND_DREAM_CHURCH_9.get());
                        output.accept(PDItems.DEBUG_WAND_DREAM_CHURCH_10.get());
                        output.accept(PDItems.DEBUG_WAND_DESERT_FORTRESS_0.get());
                        // P1 移植遗迹调试水晶
                        output.accept(PDItems.DEBUG_WAND_DYEDREAM_TOWER_0.get());
                        output.accept(PDItems.DEBUG_WAND_DYEDREAM_TOWER_1.get());
                        output.accept(PDItems.DEBUG_WAND_DYEDREAM_LABORATORY_0.get());
                        output.accept(PDItems.DEBUG_WAND_DYEDREAM_TAVERN.get());
                        output.accept(PDItems.DEBUG_WAND_DYEDREAM_PAVILION_0.get());
                        output.accept(PDItems.DEBUG_WAND_DYEDREAM_PAVILION_1.get());
                        output.accept(PDItems.DEBUG_WAND_DYEDREAM_PAVILION_2.get());
                        output.accept(PDItems.DEBUG_WAND_DYEDREAM_CAMPSITE_0.get());
                        output.accept(PDItems.DEBUG_WAND_DREAM_WISHINGTREE_0.get());
                        output.accept(PDItems.DEBUG_WAND_DREAM_WISHINGTREE_1.get());
                        output.accept(PDItems.DEBUG_WAND_TRAVELER_HOUSE_0.get());
                        output.accept(PDItems.DEBUG_WAND_TRAVELER_HOUSE_1.get());
                        output.accept(PDItems.DEBUG_WAND_TRAVELER_HOUSE_2.get());
                        output.accept(PDItems.DEBUG_WAND_GARDEN_DECRYPTION_0.get());
                        output.accept(PDItems.DEBUG_WAND_GARDEN_DECRYPTION_1.get());
                        output.accept(PDItems.DEBUG_WAND_GARDEN_DECRYPTION_2.get());
                        output.accept(PDItems.DEBUG_WAND_PICNIC_BASKET.get());
                        // output.accept(PDItems.DEBUG_WAND_LIFECRYSTAL_CAVE_0.get()); // 未注册
                        output.accept(PDItems.DEBUG_WAND_MELTDREAM_LIQUID_WELL_0.get());
                        output.accept(PDItems.DEBUG_WAND_MELTDREAM_LIQUID_WELL_1.get());
                        // 结构方块补全调试水晶（去重后保留为水晶）
                        output.accept(PDItems.DEBUG_WAND_CRYSTAL_BALL.get());
                        output.accept(PDItems.DEBUG_WAND_STONE_PILLAR_SKY.get());
                        output.accept(PDItems.DEBUG_WAND_SHADOW_WORLD_DOOR.get());
                        output.accept(PDItems.DEBUG_WAND_SHADOW_TOMB.get());
                        output.accept(PDItems.DEBUG_WAND_SHADOW_CHAIN.get());
                        output.accept(PDItems.DEBUG_WAND_SHADOW_SHELTER.get());
                        output.accept(PDItems.DEBUG_WAND_SHADOW_FUNGUS_NEST.get());
                        output.accept(PDItems.DEBUG_WAND_SHADOW_FOUNDRY.get());
                        output.accept(PDItems.DEBUG_WAND_SHADOW_DUNGEON.get());
                        output.accept(PDItems.DEBUG_WAND_SHADOW_FUNGUS_HOUSE.get());
                        output.accept(PDItems.DEBUG_WAND_SHADOW_UNDERGROUND_WORKROOM.get());
                        output.accept(PDItems.DEBUG_WAND_WINDMOOR_TREE.get());
                        output.accept(PDItems.DEBUG_WAND_HOT_AIR_BALLOON.get());
                        output.accept(PDItems.DEBUG_WAND_CHRISTMAS_TREE.get());
                        // W4：调试机关
                        output.accept(PDItems.GUARD_BLOCK.get());
                        output.accept(PDItems.RESTRAINMOVE_BLOCK.get());
                        output.accept(PDItems.LOST_SWORD_BLOCK.get());
                        output.accept(PDItems.CLAYPAN_1.get());
                        output.accept(PDItems.PASTER_BLOCK_RESET_TOOL.get());
                        // BOSS 调试物品
                        output.accept(PDItems.AARONCOS_ARENA_CREATE.get());
                        output.accept(PDItems.TEST_CURIO.get());
                    })
                    .build());
}
