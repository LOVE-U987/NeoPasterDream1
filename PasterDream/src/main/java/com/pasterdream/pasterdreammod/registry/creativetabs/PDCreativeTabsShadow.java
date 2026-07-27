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
 * 阴影维度创造模式标签页注册。
 *
 * @see PDCreativeTabs
 */
public class PDCreativeTabsShadow {


    // ==================== 3. 阴影维度 ====================

    /**
     * 阴影维度标签页
     * 包含阴影相关物品、暗影系列武器工具及饰品
     */
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> SHADOW_TAB = PDCreativeTabs.TABS.register("shadow_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.pasterdream.shadow_tab"))
                    .icon(() -> new ItemStack(PDItems.SHADOW_CHEST.get()))
                    .withTabsBefore(PDCreativeTabs.DYEDREAM_TAB.getKey())
                    .displayItems((parameters, output) -> {
                        // 阴影维度：容器/材料/方块（武器胚与成品在武器栏）
                        output.accept(PDItems.SHADOW_CHEST.get());
                        output.accept(PDItems.SHADOW_HILT.get());
                        output.accept(PDItems.SHADOW_DUNGEON_KEY.get());
                        output.accept(PDItems.ICESHADOW_CURIO.get());
                        output.accept(PDItems.DARK_ALLLEGORY_CURIO.get());
                        output.accept(PDItems.SCULK_HEART.get());
                        output.accept(PDItems.SCULK_UPGRADE.get());
                        output.accept(PDItems.NIGHTMARE_FUEL.get());
                        output.accept(PDItems.PURE_HORROR.get());
                        // 阴影系生物掉落（原版 paster_tab_3；曾误挂 entity_tab）
                        output.accept(PDItems.SHADOW_BREATH.get());
                        output.accept(PDItems.MOSS_PHANTOM_MEMBRANE.get());
                        output.accept(PDItems.LIGHT_MOSS_PHANTOM_MEMBRANE.get());
                        // 阴影维度基础方块
                        output.accept(PDItems.SHADOW_BLOCK.get());
                        output.accept(PDItems.THICK_SHADOW_BLOCK.get());
                        output.accept(PDItems.SHADOW_STONE.get());
                        output.accept(PDItems.SHADOW_STONE_BRICK.get());
                        output.accept(PDItems.SHADOW_STONE_BRICKS.get());
                        output.accept(PDItems.SHADOW_STONE_TILES.get());
                        output.accept(PDItems.CHISELED_SHADOW_STONE_BRICK.get());
                        output.accept(PDItems.CRACKED_SHADOW_STONE_BRICK.get());
                        output.accept(PDItems.SHADOW_NYLIUM.get());
                        output.accept(PDItems.SHADOW_SHROOMLIGHT.get());
                        output.accept(PDItems.SHADOW_WART_BLOCK.get());
                        output.accept(PDItems.SHADOW_STEM.get());
                        output.accept(PDItems.SHADOW_HYPHAE.get());
                        output.accept(PDItems.STRIPPED_SHADOW_STEM.get());
                        output.accept(PDItems.STRIPPED_SHADOW_HYPHAE.get());
                        output.accept(PDItems.SHADOW_PLANKS.get());
                        // 阴影石砖变体
                        output.accept(PDItems.SHADOW_STONE_BRICK_STAIRS.get());
                        output.accept(PDItems.SHADOW_STONE_BRICK_SLAB.get());
                        output.accept(PDItems.SHADOW_STONE_BRICK_WALL.get());
                        output.accept(PDItems.SHADOW_STONE_BRICKS_STAIRS.get());
                        output.accept(PDItems.SHADOW_STONE_BRICKS_SLAB.get());
                        output.accept(PDItems.SHADOW_STONE_BRICKS_WALL.get());
                        output.accept(PDItems.SHADOW_STONE_TILES_STAIRS.get());
                        output.accept(PDItems.SHADOW_STONE_TILES_SLAB.get());
                        output.accept(PDItems.SHADOW_STONE_TILES_WALL.get());
                        // 阴影木板变体
                        output.accept(PDItems.SHADOW_PLANKS_STAIRS.get());
                        output.accept(PDItems.SHADOW_PLANKS_SLAB.get());
                        output.accept(PDItems.SHADOW_PLANKS_FENCE.get());
                        output.accept(PDItems.SHADOW_PLANKS_FENCEGATE.get());
                        output.accept(PDItems.SHADOW_PLANKS_DOOR.get());
                        output.accept(PDItems.SHADOW_PLANKS_TRAPDOOR.get());
                        output.accept(PDItems.SHADOW_PLANKS_PRESSURE_PLATE.get());
                        output.accept(PDItems.SHADOW_PLANKS_BUTTON.get());
                        output.accept(PDItems.SHADOW_PLANKS_PANE.get());
                        // 暗影地牢 / 竞技场地砖
                        output.accept(PDItems.SHADOW_DUNGEON_BLOCK_0.get());
                        output.accept(PDItems.SHADOW_DUNGEON_BLOCK_1.get());
                        output.accept(PDItems.SHADOW_DUNGEON_BLOCK_2.get());
                        output.accept(PDItems.SHADOW_DUNGEON_BLOCK_3.get());
                        output.accept(PDItems.SHADOW_DUNGEON_BLOCK_4.get());
                        output.accept(PDItems.SHADOW_DUNGEON_BLOCK_5.get());
                        output.accept(PDItems.SHADOW_DUNGEON_BLOCK_6.get());
                        output.accept(PDItems.SHADOW_ARENA_BLOCK_0.get());
                        // 松动地牢砖
                        output.accept(PDItems.LOOSE_SHADOW_DUNGEON_BLOCK.get());
                        // 地牢门系列
                        output.accept(PDItems.SHADOW_DUNGEON_DOOR_0.get());
                        output.accept(PDItems.SHADOW_DUNGEON_DOOR_1.get());
                        output.accept(PDItems.SHADOWDUNGEONDOOR_2.get());
                        output.accept(PDItems.SHADOWDUNGEONDOOR_3.get());
                        // 地牢钥匙系列
                        output.accept(PDItems.SHADOW_DUNGEON_KEY_0.get());
                        output.accept(PDItems.SHADOW_DUNGEON_KEY_1.get());
                        // 暗影蜡烛
                        output.accept(PDItems.SHADOWCANDLE.get());
                        // 研究台组（原版 paster_tab_5 设备栏）
                        output.accept(PDItems.RESEARCH_TABLE.get());
                        output.accept(PDItems.FORCED_TOWER.get());
                        // 高炉核心与暗影高炉
                        output.accept(PDItems.SHADOW_BLAST_FURNACE_CORE.get());
                        output.accept(PDItems.SHADOW_BLAST_FURNACE.get());
                        // 暗影书架系列
                        output.accept(PDItems.SHADOWSHELF_0.get());
                        output.accept(PDItems.SHADOWSHELF_1.get());
                        output.accept(PDItems.SHADOWSHELF_2.get());
                        output.accept(PDItems.SHADOWSHELF_3.get());
                        // 暗影裂隙系列
                        output.accept(PDItems.SHADOW_FISSURE_0.get());
                        output.accept(PDItems.SHADOW_FISSURE_1.get());
                        output.accept(PDItems.SHADOW_FISSURE_2.get());
                        output.accept(PDItems.SHADOW_FISSURE_3.get());
                        output.accept(PDItems.SHADOW_FISSURE_4.get());
                        output.accept(PDItems.SHADOW_FISSURE_5.get());
                        // BOSS 相关方块
                        output.accept(PDItems.AARONCOS_ARENA_PORTALS.get());
                        output.accept(PDItems.AARONCOS_HAND_CHEST.get());
                        output.accept(PDItems.AARONCOSHANDSPAWNBLOCK.get());
                        // 构梦刷怪笼（原版位于调试栏 paster_tab_8；调试标签属并行分区，
                        // 与上方 BOSS 生成方块同为地牢/事件机关，暂归本页）
                        output.accept(PDItems.DREAM_SPAWNER_0.get());
                        output.accept(PDItems.DREAM_SPAWNER_1.get());
                        // 阴影植被与流体（波次C）
                        output.accept(PDItems.SHADOW_FUNGUS.get());
                        output.accept(PDItems.SHADOW_LIQUID_BUCKET.get());

                        // W4：影系家具、机关与容器
                        output.accept(PDItems.SHADOW_BED.get());
                        output.accept(PDItems.TRUE_SHADOW_BED.get());
                        output.accept(PDItems.SHADOW_DESK.get());
                        output.accept(PDItems.SHADOW_BRAZIER.get());
                        output.accept(PDItems.SHADOW_DUNGEON_PORTAL.get());
                        output.accept(PDItems.BROKEN_SHADOW_DUNGEON_PROTAL.get());
                        output.accept(PDItems.SHADOW_TRAP_0.get());
                        output.accept(PDItems.TWILIGHT_LANTERN.get());
                        output.accept(PDItems.GUARD_CRYSTAL.get());
                        output.accept(PDItems.DESERT_HERO_TOMB.get());
                        output.accept(PDItems.ECOLOGY_GLASS_JAR.get());
                        output.accept(PDItems.FIREFLY_GLASS_JAR.get());
                        output.accept(PDItems.LIGHT_FIREFLY_GLASS_JAR.get());
                        output.accept(PDItems.PICNIC_BASKET.get());
                        output.accept(PDItems.BIRDS_NEST.get());
                    })
                    .build());
}
