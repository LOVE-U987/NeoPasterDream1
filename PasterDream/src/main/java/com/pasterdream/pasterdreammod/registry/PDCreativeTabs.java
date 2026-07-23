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
 * 按维度/功能划分为9个标签页，便于玩家分类查找物品
 */
public class PDCreativeTabs {

    /**
     * 创造模式物品栏注册器
     */
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(
            BuiltInRegistries.CREATIVE_MODE_TAB, PasterDreamMod.MOD_ID);

    // ==================== 1. 生物实体 ====================

    /**
     * 生物实体标签页
     * 包含所有刷怪蛋和生物专属掉落物
     */
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> ENTITY_TAB = TABS.register("entity_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.pasterdream.entity_tab"))
                    .icon(() -> new ItemStack(PDItems.SHADOW_GOLEM_SPAWN_EGG.get()))
                    .displayItems((parameters, output) -> {
                        output.accept(PDItems.SHADOW_GOLEM_SPAWN_EGG.get());
                        output.accept(PDItems.PINK_SLIME_SPAWN_EGG.get());
                        output.accept(PDItems.PINK_CHICKEN_SPAWN_EGG.get());
                        output.accept(PDItems.JELLYFISH_SPAWN_EGG.get());
                        output.accept(PDItems.FRIENDLY_GHOST_SPAWN_EGG.get());
                        output.accept(PDItems.FIREFLY_SPAWN_EGG.get());
                        output.accept(PDItems.GOLDEN_FOX_SPAWN_EGG.get());
                        output.accept(PDItems.MELTDREAM_CRYSTAL_SPAWN_EGG.get());
                        output.accept(PDItems.SHADOW_GHOST_SPAWN_EGG.get());
                        output.accept(PDItems.SHADOW_SQUEAL_GHOST_SPAWN_EGG.get());
                        output.accept(PDItems.SHADOW_SQUEAL_GHOST_0_SPAWN_EGG.get());
                        output.accept(PDItems.SHADOW_HAND_SPAWN_EGG.get());
                        output.accept(PDItems.THUNDERCLOUD_SPAWN_EGG.get());
                        output.accept(PDItems.HIGHVOLTAGE_SPAWN_EGG.get());
                        output.accept(PDItems.WIND_KNIGHT_SPAWN_EGG.get());
                        output.accept(PDItems.SHAKING_CRYSTAL_SPAWN_EGG.get());
                        output.accept(PDItems.SHADOW_TUNE_TOTEM_SPAWN_EGG.get());
                        output.accept(PDItems.SMALL_STONE_SPIRIT_SPAWN_EGG.get());
                        output.accept(PDItems.BLACK_BEETLE_SPAWN_EGG.get());
                        output.accept(PDItems.BLACK_BEETLE_MOTHER_SPAWN_EGG.get());
                        output.accept(PDItems.TERRORBEAK_SPAWN_EGG.get());
                        output.accept(PDItems.CRAZY_TERRORBEAK_SPAWN_EGG.get());
                        output.accept(PDItems.WEAKENESS_TERRORBEAK_SPAWN_EGG.get());
                        output.accept(PDItems.BONE_WING_SPAWN_EGG.get());
                        output.accept(PDItems.ASH_BONE_WING_SPAWN_EGG.get());
                        output.accept(PDItems.BASALT_SNAIL_SPAWN_EGG.get());
                        output.accept(PDItems.FOX_FIRE_SPAWN_EGG.get());
                        output.accept(PDItems.SHADOW_NPC_0_SPAWN_EGG.get());
                        output.accept(PDItems.SPORE_ENTITY_SPAWN_EGG.get());
                        output.accept(PDItems.AARONCOS_LEFTHAND_0_SPAWN_EGG.get());
                        output.accept(PDItems.AARONCOS_RIGHTHAND_0_SPAWN_EGG.get());
                        output.accept(PDItems.PINK_SLIMEBALL.get());
                        output.accept(PDItems.SHADOW_BREATH.get());
                        output.accept(PDItems.BLACK_BEETLE_CARAPACE.get());
                        output.accept(PDItems.BLACK_BEETLE_VOCALCORD.get());
                        output.accept(PDItems.BASALT_SNAIL_SHELL.get());
                        output.accept(PDItems.ELDER_GUARDIAN_SCALE.get());
                        output.accept(PDItems.MOSS_PHANTOM_MEMBRANE.get());
                        output.accept(PDItems.LIGHT_MOSS_PHANTOM_MEMBRANE.get());
                        output.accept(PDItems.SOUL_DUST.get());
                        output.accept(PDItems.SOUL_ESSENCE.get());
                        output.accept(PDItems.STRAWBERRY_HEART.get());
                        output.accept(PDItems.JELLYFISH_MUD.get());
                        output.accept(PDItems.JELLYFISH_JELLO.get());
                    })
                    .build());

    // ==================== 2. 染梦维度 ====================

    /**
     * 染梦维度标签页
     * 包含染梦世界的所有原生方块、维度专属物品及功能方块
     */
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> DYEDREAM_TAB = TABS.register("dyedream_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.pasterdream.dyedream_tab"))
                    .icon(() -> new ItemStack(PDBlocks.DYEDREAM_BLOCK.get()))
                    .withTabsBefore(ENTITY_TAB.getKey())
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
                        output.accept(PDItems.MELTDREAM_PICKAXE.get());
                        output.accept(PDItems.DYEDREAM_FRUIT.get());
                        output.accept(PDItems.DYEDREAM_JUICE.get());
                        output.accept(PDItems.DYEDREAM_FLOWER_TEA.get());
                        output.accept(PDItems.UNCOOKED_DYEDREAM_FLOWER_TEA.get());
                        output.accept(PDItems.DYEDREAM_POPSICLE.get());
                        output.accept(PDItems.DYEDREAM_FRUIT_BUNCAKE.get());
                        output.accept(PDItems.DYEDREAM_SWORD_0.get());
                        output.accept(PDItems.DYEDREAM_SWORD.get());
                        output.accept(PDItems.DYEDREAM_PICKAXE.get());
                        output.accept(PDItems.DYEDREAM_HAMMER.get());
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
                        output.accept(PDBlocks.SHADOW_LIGHT_0.get());

                        // 融梦水晶箱
                        output.accept(PDItems.MELTDREAM_CHEST.get());
                        output.accept(PDItems.MELTDREAM_CHEST_OPEN.get());

                        // 融梦水晶灯
                        output.accept(PDItems.MELTDREAM_CRYSTAL_LAMP.get());

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

    // ==================== 3. 阴影维度 ====================

    /**
     * 阴影维度标签页
     * 包含阴影相关物品、暗影系列武器工具及饰品
     */
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> SHADOW_TAB = TABS.register("shadow_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.pasterdream.shadow_tab"))
                    .icon(() -> new ItemStack(PDItems.SHADOW_CHEST.get()))
                    .withTabsBefore(DYEDREAM_TAB.getKey())
                    .displayItems((parameters, output) -> {
                        output.accept(PDItems.SHADOW_CHEST.get());
                        output.accept(PDItems.SHADOW_HILT.get());
                        output.accept(PDItems.SHADOW_DUNGEON_KEY.get());
                        output.accept(PDItems.SHADOW_SWORD.get());
                        output.accept(PDItems.SHADOW_SWORD_EMBRYO.get());
                        output.accept(PDItems.SHADOW_EROSION_SWORD.get());
                        output.accept(PDItems.SHADOW_EROSION_SWORD_EMBRYO.get());
                        output.accept(PDItems.SHADOW_EROSION_PICKAXE.get());
                        output.accept(PDItems.SHADOW_EROSION_PICKAXE_EMBRYO.get());
                        output.accept(PDItems.SHADOW_EROSION_AXE_EMBRYO.get());
                        output.accept(PDItems.SHADOW_EROSION_HOE_EMBRYO.get());
                        output.accept(PDItems.SHADOW_EROSION_SHOVEL_EMBRYO.get());
                        output.accept(PDItems.ICESHADOW_HAMMER.get());
                        output.accept(PDItems.ICESHADOW_HAMMER_EMBRYO.get());
                        output.accept(PDItems.ICESHADOW_CURIO.get());
                        output.accept(PDItems.DARK_ALLLEGORY_CURIO.get());
                        output.accept(PDItems.SCULK_HEART.get());
                        output.accept(PDItems.SCULK_UPGRADE.get());
                        output.accept(PDItems.NIGHTMARE_FUEL.get());
                        output.accept(PDItems.PURE_HORROR.get());
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
                        // 高炉核心
                        output.accept(PDItems.SHADOW_BLAST_FURNACE_CORE.get());
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
                    })
                    .build());

    // ==================== 4. 风之旅途维度 ====================

    /**
     * 风之旅途维度标签页
     * 包含风系物品、云朵方块及翅膀系列
     */
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> WIND_TAB = TABS.register("wind_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.pasterdream.wind_tab"))
                    .icon(() -> new ItemStack(PDItems.WIND_KNIGHT_FLAG.get()))
                    .withTabsBefore(SHADOW_TAB.getKey())
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
                        output.accept(PDItems.ANGEL_WING.get());
                        output.accept(PDItems.FORSAKENS_WING.get());
                        output.accept(PDItems.GROUND_WING.get());
                        output.accept(PDItems.MACHINE_WING.get());
                        output.accept(PDItems.WINGS_OF_FANG.get());
                    })
                    .build());

    // ==================== 5. 纪念品 ====================

    /**
     * 纪念品标签页
     * 包含特殊功能道具、剧情物品与收藏品
     */
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> SOUVENIR_TAB = TABS.register("souvenir_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.pasterdream.souvenir_tab"))
                    .icon(() -> new ItemStack(PDItems.MEMENTO_ITEM_01.get()))
                    .withTabsBefore(WIND_TAB.getKey())
                    .displayItems((parameters, output) -> {
                        output.accept(PDItems.DREAM_COIN_0.get());
                        output.accept(PDItems.DREAM_COIN_1.get());
                        output.accept(PDItems.BLUE_DEW.get());
                        output.accept(PDItems.RED_DEW_0.get());
                        output.accept(PDItems.MEMENTO_ITEM_01.get());
                        output.accept(PDItems.MEMENTO_ITEM_02.get());
                        output.accept(PDItems.MEMENTO_ITEM_03.get());
                        output.accept(PDItems.MEMENTO_ITEM_04.get());
                        output.accept(PDItems.MEMENTO_ITEM_05.get());
                        output.accept(PDItems.MEMENTO_ITEM_06.get());
                        output.accept(PDItems.MEMENTO_ITEM_07.get());
                        output.accept(PDItems.MEMENTO_ITEM_08.get());
                        output.accept(PDItems.MEMENTO_ITEM_09.get());
                        output.accept(PDItems.MEMENTO_ITEM_10.get());
                        output.accept(PDItems.MEMORY_GEM_0.get());
                        output.accept(PDItems.BROKENNOTES_0.get());
                        output.accept(PDItems.UNKNOWNNOTES_0.get());
                        output.accept(PDItems.CRADLE_IN_ONES_ARMS.get());
                        output.accept(PDItems.SQUEAL_WAVE.get());
                        output.accept(PDItems.GUIDING_DRUG.get());
                        output.accept(PDItems.WHITE_COROLLA.get());
                        output.accept(PDItems.PALE_BONENEEDLE.get());
                        // 玩偶/雕像
                        output.accept(PDItems.QIN_DOLL_0.get());
                        output.accept(PDItems.LITTLE_PURPLE_DOLL_0.get());
                        output.accept(PDItems.GOLDEN_FOX_SCULPTURE.get());
                    })
                    .build());

    // ==================== 6. 盔甲装备 ====================

    /**
     * 盔甲装备标签页
     * 包含所有盔甲套装及装备配件
     */
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> ARMOR_TAB = TABS.register("armor_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.pasterdream.armor_tab"))
                    .icon(() -> new ItemStack(PDItems.DYEDREAM_ARMOR_CHESTPLATE.get()))
                    .withTabsBefore(SOUVENIR_TAB.getKey())
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

    // ==================== 7. 武器工具 ====================

    /**
     * 武器工具标签页
     * 包含所有武器、工具、升级套件及基础材料
     */
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> WEAPON_TAB = TABS.register("weapon_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.pasterdream.weapon_tab"))
                    .icon(() -> new ItemStack(PDItems.MOLTENGOLD_SWORD.get()))
                    .withTabsBefore(ARMOR_TAB.getKey())
                    .displayItems((parameters, output) -> {
                        // 基础材料
                        output.accept(PDItems.TITANIUM_INGOT.get());
                        output.accept(PDItems.TITANIUM_NUGGET.get());
                        output.accept(PDItems.RAW_TITANIUM.get());
                        output.accept(PDItems.MOLTENGOLD_INGOT.get());
                        output.accept(PDItems.MOLTENGOLD_NUGGET.get());
                        output.accept(PDItems.MOLTENGOLD_DUST.get());
                        output.accept(PDItems.RAW_MOLTENGOLD.get());
                        output.accept(PDItems.BLACKMETAL_INGOT.get());
                        output.accept(PDItems.BLACKMETAL_GRAIN.get());
                        output.accept(PDItems.RUST_BLACK_METAL_GRAIN.get());
                        output.accept(PDItems.BLACKSTICK.get());
                        output.accept(PDItems.ENHANCE_STONE_0.get());
                        output.accept(PDItems.ENHANCE_STONE_1.get());
                        output.accept(PDItems.PROTECT_DECK.get());
                        output.accept(PDItems.TITANIUM_UPGRADE.get());
                        output.accept(PDItems.WHITE_CRYSTAL.get());
                        output.accept(PDItems.CHARGED_AMETHYST.get());
                        output.accept(PDItems.MAGIC_STONE.get());
                        output.accept(PDItems.MANADUST.get());
                        output.accept(PDItems.BLUE_HEART_OF_THE_SEA.get());
                        output.accept(PDItems.MORTAR.get());
                        output.accept(PDItems.SILVER_BELL.get());
                        output.accept(PDItems.SORBENT.get());
                        output.accept(PDItems.COTTON.get());
                        output.accept(PDItems.SPOOL.get());
                        output.accept(PDItems.FABRIC.get());
                        output.accept(PDItems.PERGAMYN.get());
                        output.accept(PDItems.PEN_AND_INK.get());
                        output.accept(PDItems.REEDROD.get());

                        // 武器原胚
                        output.accept(PDItems.SWORD_EMBRYO_0.get());
                        output.accept(PDItems.SHADOW_SWORD_EMBRYO.get());
                        output.accept(PDItems.SHADOW_EROSION_SWORD_EMBRYO.get());
                        output.accept(PDItems.SHADOW_EROSION_AXE_EMBRYO.get());
                        output.accept(PDItems.SHADOW_EROSION_PICKAXE_EMBRYO.get());
                        output.accept(PDItems.SHADOW_EROSION_HOE_EMBRYO.get());
                        output.accept(PDItems.SHADOW_EROSION_SHOVEL_EMBRYO.get());
                        output.accept(PDItems.ICESHADOW_HAMMER_EMBRYO.get());
                        output.accept(PDItems.WHITE_SWORD_EMBRYO.get());
                        output.accept(PDItems.TERRASWORD_EMBRYO.get());
                        output.accept(PDItems.STAR_WISH_ROD_EMBRYO.get());

                        // 剑类武器
                        output.accept(PDItems.BROKEN_HERO_SWORD.get());
                        output.accept(PDItems.COPPER_SWORD.get());
                        output.accept(PDItems.CREATIVE_SWORD.get());
                        output.accept(PDItems.DESERT_SWORD.get());
                        output.accept(PDItems.GRASS_SWORD.get());
                        output.accept(PDItems.ICESHADOW_HAMMER.get());
                        output.accept(PDItems.MOLTENGOLD_SWORD.get());
                        output.accept(PDItems.SHADOW_EROSION_SWORD.get());
                        output.accept(PDItems.SHADOW_SWORD.get());
                        output.accept(PDItems.TERRA_SWORD.get());
                        output.accept(PDItems.THERMAL_DAGGER.get());
                        output.accept(PDItems.TIDE_SWORD.get());
                        output.accept(PDItems.TITANIUM_SWORD.get());
                        output.accept(PDItems.TRUE_DESERT_SWORD.get());
                        output.accept(PDItems.TRUE_GRASS_SWORD.get());
                        output.accept(PDItems.TRUE_MOLTENGOLD_SWORD.get());
                        output.accept(PDItems.TRUE_TIDE_SWORD.get());
                        output.accept(PDItems.TRUEST_MOLTENGOLD_SWORD.get());
                        output.accept(PDItems.WHITE_SWORD.get());

                        // 镐类/锤类工具
                        output.accept(PDItems.COPPER_PICKAXE.get());
                        output.accept(PDItems.MOLTENGOLD_PICKAXE.get());
                        output.accept(PDItems.SHADOW_EROSION_PICKAXE.get());
                        output.accept(PDItems.TITANIUM_PICKAXE.get());
                        output.accept(PDItems.TRUE_MOLTENGOLD_PICKAXE.get());

                        // 其他工具
                        output.accept(PDItems.COPPER_AXE.get());
                        output.accept(PDItems.COPPER_SHOVEL.get());
                        output.accept(PDItems.COPPER_HOE.get());
                        output.accept(PDItems.TITANIUM_AXE.get());
                        output.accept(PDItems.TITANIUM_SHOVEL.get());
                        output.accept(PDItems.TITANIUM_HOE.get());
                        // 染梦工具
                        output.accept(PDItems.DYEDREAM_AXE.get());
                        output.accept(PDItems.DYEDREAM_SHOVEL.get());
                        output.accept(PDItems.DYEDREAM_HOE.get());
                        // 熔金工具
                        output.accept(PDItems.MOLTENGOLD_AXE.get());
                        output.accept(PDItems.MOLTENGOLD_SHOVEL.get());
                        output.accept(PDItems.MOLTENGOLD_HOE.get());
                        // 融梦工具
                        output.accept(PDItems.MELTDREAM_AXE.get());
                        output.accept(PDItems.MELTDREAM_SHOVEL.get());
                        output.accept(PDItems.MELTDREAM_HOE.get());
                        // 蚀影工具
                        output.accept(PDItems.SHADOW_EROSION_AXE.get());
                        output.accept(PDItems.SHADOW_EROSION_SHOVEL.get());
                        output.accept(PDItems.SHADOW_EROSION_HOE.get());
                        output.accept(PDItems.GLASS_CUP.get());
                        output.accept(PDItems.DOUGH.get());
                        output.accept(PDItems.RYESEED.get());

                        // Phase 1: 移植方块
                        output.accept(PDBlocks.TITANIUM_BLOCK.get());
                        output.accept(PDBlocks.RAW_TITANIUM_BLOCK.get());
                        output.accept(PDBlocks.MOLTENGOLD_BLOCK.get());
                        output.accept(PDBlocks.BLACKMETAL_BLOCK.get());
                        output.accept(PDBlocks.CHARGED_AMETHYST_BLOCK.get());
                        output.accept(PDBlocks.WIND_IRON_BLOCK.get());
                        output.accept(PDBlocks.DEEPSLATE_TITANIUM_ORE.get());
                        output.accept(PDBlocks.TITANIUM_ORE.get());
                        output.accept(PDBlocks.MOLTENGOLD_ORE.get());
                        output.accept(PDBlocks.SOUL_ORE.get());

                        // Phase 1: 移植特殊物品
                        output.accept(PDItems.JUNGLE_SPORE.get());
                        output.accept(PDItems.MELTDREAM_LIQUID_BUCKET.get());
                        output.accept(PDItems.PINKEGG.get());
                        output.accept(PDItems.PLIERS.get());
                    })
                    .build());

    // ==================== 8. 食物饮品 ====================

    /**
     * 食物饮品标签页
     * 包含所有食物、饮料及烹饪原料
     */
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> FOOD_TAB = TABS.register("food_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.pasterdream.food_tab"))
                    .icon(() -> new ItemStack(PDItems.DYEDREAM_FRUIT.get()))
                    .withTabsBefore(WEAPON_TAB.getKey())
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

    // ==================== 9. 饰品装备 ====================

    /**
     * 饰品装备标签页
     * 包含所有 Curio 饰品、戒指、项链、护符、腰带及翅膀
     */
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> CURIO_TAB = TABS.register("curio_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.pasterdream.curio_tab"))
                    .icon(() -> new ItemStack(PDItems.FOURLEAF_CLOVER_CURIO.get()))
                    .withTabsBefore(FOOD_TAB.getKey())
                    .displayItems((parameters, output) -> {
                        output.accept(PDItems.EMBRYO_CHARM.get());
                        output.accept(PDItems.EMBRYO_RING.get());
                        output.accept(PDItems.EMBRYO_NECKLACE.get());
                        output.accept(PDItems.EMBRYO_BELT.get());
                        output.accept(PDItems.HITHARD_0_RING.get());
                        output.accept(PDItems.HITHARD_1_RING.get());
                        output.accept(PDItems.RED_DEW_0_RING.get());
                        output.accept(PDItems.RED_DEW_1_RING.get());
                        output.accept(PDItems.RED_DEW_2_RING.get());
                        output.accept(PDItems.RED_DEW_3_RING.get());
                        output.accept(PDItems.COUNTER_RING.get());
                        output.accept(PDItems.DARK_ALLLEGORY_CURIO.get());
                        output.accept(PDItems.CECILIACARE_CHARM.get());
                        output.accept(PDItems.CARAPAX_CHARM.get());
                        output.accept(PDItems.SEA_CHARM.get());
                        output.accept(PDItems.CALAIS_SPICE_BOTTLE_CURIO.get());
                        output.accept(PDItems.CROSS_NECKLACE.get());
                        output.accept(PDItems.FEATHER_NECKLACE.get());
                        output.accept(PDItems.FIRE_0_NECKLACE.get());
                        output.accept(PDItems.HEALTH_0_NECKLACE.get());
                        output.accept(PDItems.RABBIT_0_NECKLACE.get());
                        output.accept(PDItems.GOLD_CHARM.get());
                        output.accept(PDItems.ENDEYE_CHARM.get());
                        output.accept(PDItems.TERRA_CHARM.get());
                        output.accept(PDItems.DREAM_TRAVELER_BELT.get());
                        output.accept(PDItems.NATURE_BELT.get());
                        output.accept(PDItems.TRAVELER_BELT.get());
                        output.accept(PDItems.EVASION_CLOAK.get());
                        output.accept(PDItems.TURNBACK_CLOAK.get());
                        output.accept(PDItems.GARLAND.get());
                        output.accept(PDItems.PAPER_PLANE.get());
                        output.accept(PDItems.DUKE_COIN_CURIO.get());
                        output.accept(PDItems.BOBO_PLUME.get());
                        output.accept(PDItems.BRIGHT_BUTTERFLY_CURIO.get());
                        output.accept(PDItems.LIGHT_BUTTERFLY_CURIO.get());
                        output.accept(PDItems.ICESHADOW_CURIO.get());
                        output.accept(PDItems.DEGENERATE_BODYS.get());
                        output.accept(PDItems.WHITE_FLOWER_BODY.get());
                        output.accept(PDItems.WORLDTREE_SEEDPOD.get());
                        output.accept(PDItems.HIYORI_HEAD.get());
                        output.accept(PDItems.QYM_HEAD.get());
                        output.accept(PDItems.SNOW_VOW_HEAD.get());
                        output.accept(PDItems.GHOST_FACE_HEAD.get());
                        output.accept(PDItems.ALLKINDS_RING.get());
                        output.accept(PDItems.TEST_CURIO.get());
                        output.accept(PDItems.FOURLEAF_CLOVER_CURIO.get());
                    })
                    .build());

    // ==================== 9. 音乐唱片 ====================

    /**
     * 音乐唱片标签页
     * 包含所有音乐唱片
     */
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> DISC_TAB = TABS.register("disc_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.pasterdream.disc_tab"))
                    .icon(() -> new ItemStack(PDItems.SWEETDREAM_DISC.get()))
                    .withTabsBefore(CURIO_TAB.getKey())
                    .displayItems((parameters, output) -> {
                        output.accept(PDItems.SWEETDREAM_DISC.get());
                        output.accept(PDItems.SNOWFALLDREAM_DISC.get());
                        output.accept(PDItems.AARONCOS_DISC.get());
                        output.accept(PDItems.DYEDREAM_WORLD_DISC.get());
                        output.accept(PDItems.WIND_JOURNEY_DISC.get());
                        output.accept(PDItems.WIND_JOURNEY_1_DISC.get());
                        output.accept(PDItems.DREAM_MEADOW_DISC.get());
                        output.accept(PDItems.DREAM_HEATH_DISC.get());
                        output.accept(PDItems.DREAM_TAIGA_DISC.get());
                        output.accept(PDItems.DREAM_DELTA_DISC.get());
                    })
                    .build());

    // ==================== 10. 调试功能 ====================

    /**
     * 调试功能标签页
     * 包含用于快速生成遗迹结构的调试法杖，仅在开发阶段使用
     */
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> DEBUG_TAB = TABS.register("debug_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.pasterdream.debug_tab"))
                    .icon(() -> new ItemStack(PDItems.DEBUG_WAND_DREAM_TRAIN.get()))
                    .withTabsBefore(DISC_TAB.getKey())
                    .displayItems((parameters, output) -> {
                        output.accept(PDItems.DEBUG_WAND_DREAM_TRAIN.get());
                        output.accept(PDItems.DEBUG_WAND_WORLDTREE.get());
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
                        // BOSS 调试物品
                        output.accept(PDItems.AARONCOS_ARENA_CREATE.get());
                    })
                    .build());

}
