package com.pasterdream.pasterdreammod.registry.items;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.api.curio.CurioAPI;
import com.pasterdream.pasterdreammod.api.curio.model.CurioSlot;
import com.pasterdream.pasterdreammod.api.effect.MobEffectAPI;
import com.pasterdream.pasterdreammod.api.entity.EntityAPI;
import com.pasterdream.pasterdreammod.api.item.ItemAPI;
import com.pasterdream.pasterdreammod.api.item.model.MigrationCategory;
import com.pasterdream.pasterdreammod.api.item.model.ToolSpec.ToolType;
import com.pasterdream.pasterdreammod.item.*;
import com.pasterdream.pasterdreammod.registry.PDBlocks;
import com.pasterdream.pasterdreammod.registry.PDEffects;
import com.pasterdream.pasterdreammod.registry.PDEntities;
import com.pasterdream.pasterdreammod.registry.PDItems;
import com.pasterdream.pasterdreammod.registry.PDParticles;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PlaceOnWaterBlockItem;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.food.FoodProperties;
import net.neoforged.neoforge.registries.DeferredItem;


/**
 * 功能性物品注册（含展示方块、箱子、法杖、调试水晶、BOSS 物品）。
 *
 * @see PDItems
 */
public class PDItemsFunctional {


    // ==================== 梦境炼药锅物品 ====================

    /**
     * 梦境炼药锅物品 (dream_cauldron)
     * 使用 DreamCauldronDisplayItem 实现手持 3D 渲染
     */
    public static final DeferredItem<DreamCauldronDisplayItem> DREAM_CAULDRON = PDItems.ITEMS.register("dream_cauldron",
            () -> new DreamCauldronDisplayItem(new Item.Properties()));


    // ==================== 融梦水晶箱物品 ====================

    /**
     * 融梦水晶箱物品（关闭状态）- 使用 MeltdreamChestDisplayItem 实现手持 3D 渲染
     */
    public static final DeferredItem<MeltdreamChestDisplayItem> MELTDREAM_CHEST = PDItems.ITEMS.register("meltdream_chest",
            () -> new MeltdreamChestDisplayItem(new Item.Properties()));

    /**
     * 融梦水晶箱（打开状态）物品 - 使用 MeltdreamChestOpenDisplayItem 实现手持 3D 渲染
     */
    public static final DeferredItem<MeltdreamChestOpenDisplayItem> MELTDREAM_CHEST_OPEN = PDItems.ITEMS.register("meltdream_chest_open",
            () -> new MeltdreamChestOpenDisplayItem(new Item.Properties()));


    // ==================== 需要自定义类的物品（tooltip/交互） ====================

    public static final DeferredItem<Item> AMBER_CANDY =
            ItemAPI.foodItem("amber_candy")
                    .nutrition(0).saturationModifier(0f)
                    .build();
    /**
     * 天使方块物品 (angel_block_item)：空中使用在脚下放置天使方块
     */
    public static final DeferredItem<Item> BONE_WING_FIRE_BALL = PDItems.ITEMS.register("bone_wing_fire_ball",
            BoneWingFireBallItem::new);
    public static final DeferredItem<Item> DREAMHARP_OF_WANDERER = PDItems.ITEMS.register("dreamharp_of_wanderer",
            DreamharpOfWandererItem::new);
    public static final DeferredItem<Item> PASTER_BLOCK_RESET_TOOL = PDItems.ITEMS.register("paster_block_reset_tool",
            PasterBlockResetToolItem::new);
    public static final DeferredItem<Item> SHADOW_HAND_LANTERN = PDItems.ITEMS.register("shadow_hand_lantern",
            ShadowHandLanternItem::new);

    public static final DeferredItem<Item> ANGEL_BLOCK_ITEM = PDItems.ITEMS.register("angel_block_item",
            () -> new AngelBlockItemItem(new Item.Properties()));
    /** 储物袋 9 格 (storage_bag) */
    public static final DeferredItem<Item> STORAGE_BAG = PDItems.ITEMS.register("storage_bag",
            () -> new StorageBagItem(false));
    /** 高级储物袋 25 格 (storage_bag_0) */
    public static final DeferredItem<Item> STORAGE_BAG_0 = PDItems.ITEMS.register("storage_bag_0",
            () -> new StorageBagItem(true));
    /** 风向标 (wind_vane) */
    public static final DeferredItem<Item> WIND_VANE = PDItems.ITEMS.register("wind_vane",
            () -> new WindVaneItem());
    public static final DeferredItem<Item> BLUE_DEW = PDItems.ITEMS.register("blue_dew",
            () -> new BlueDewItem(new Item.Properties()));
    public static final DeferredItem<Item> BREAD_SLICE =
            ItemAPI.foodItem("bread_slice")
                    .nutrition(0).saturationModifier(0f)
                    .build();
    public static final DeferredItem<Item> BUBBLE_TEA = PDItems.ITEMS.register("bubble_tea",
            () -> new BubbleTeaItem(new Item.Properties()));
    public static final DeferredItem<Item> CAKE_BASE =
            ItemAPI.foodItem("cake_base")
                    .nutrition(0).saturationModifier(0f)
                    .build();
    public static final DeferredItem<Item> CRADLE_IN_ONES_ARMS = PDItems.ITEMS.register("cradle_in_ones_arms",
            () -> new CradleInOnesArmsItem(new Item.Properties()));
    public static final DeferredItem<Item> DREAM_COIN_0 =
            ItemAPI.simpleItem("dream_coin_0").build();
    public static final DeferredItem<Item> DREAM_COIN_1 =
            ItemAPI.simpleItem("dream_coin_1").build();
    public static final DeferredItem<DreamFertilizerItem> DREAM_FERTILIZER = PDItems.ITEMS.register("dream_fertilizer",
            () -> new DreamFertilizerItem(new Item.Properties(), PDParticles.DREAMFERTILITER_PARTICLE.holder()));
    public static final DeferredItem<Item> DYEDREAM_FRUIT = PDItems.ITEMS.register("dyedream_fruit",
            () -> new DyedreamFruitItem(new Item.Properties()));
    public static final DeferredItem<Item> DYEDREAM_TELEPORT_CRYSTAL = PDItems.ITEMS.register("dyedream_teleport_crystal",
            () -> new DyedreamTeleportCrystal(new Item.Properties().stacksTo(16)));
    public static final DeferredItem<Item> DYEDREAM_PERFUME = PDItems.ITEMS.register("dyedream_perfume",
            () -> new DyedreamPerfumeItem(new Item.Properties().stacksTo(16)
                    .food(new FoodProperties.Builder().nutrition(0).saturationModifier(0f).alwaysEdible()
                            .effect(() -> new MobEffectInstance(PDEffects.DYEDREAM_PERFUME_BUFF.holder(), 1200, 0), 1.0f)
                            .build())));
    public static final DeferredItem<Item> ELIXIR_BOTTLE =
            ItemAPI.simpleItem("elixir_bottle").build();
    public static final DeferredItem<Item> FIG =
            ItemAPI.foodItem("fig")
                    .nutrition(0).saturationModifier(0f)
                    .build();
    public static final DeferredItem<Item> GLASSJAR =
            ItemAPI.simpleItem("glassjar").build();
    public static final DeferredItem<Item> GUIDING_DRUG = PDItems.ITEMS.register("guiding_drug",
            () -> new GuidingDrugItem(new Item.Properties()));

    public static final DeferredItem<Item> HEART_CHOCOLATE_0 = PDItems.ITEMS.register("heart_chocolate_0",
            () -> new HeartChocolate0Item(new Item.Properties()));
    public static final DeferredItem<Item> HEART_CHOCOLATE_1 = PDItems.ITEMS.register("heart_chocolate_1",
            () -> new HeartChocolate1Item(new Item.Properties()));
    public static final DeferredItem<Item> HEART_CHOCOLATE_2 = PDItems.ITEMS.register("heart_chocolate_2",
            () -> new HeartChocolate2Item(new Item.Properties()));
    public static final DeferredItem<Item> LIGHT_MOSS_PHANTOM_MEMBRANE = PDItems.ITEMS.register("light_moss_phantom_membrane",
            () -> new LightMossPhantomMembraneItem(new Item.Properties()));
    public static final DeferredItem<Item> MELTDREAM_CRYSTAL_0 = PDItems.ITEMS.register("meltdream_crystal_0",
            () -> new MeltdreamCrystal0Item(new Item.Properties()));
    public static final DeferredItem<Item> MEMENTO_ITEM_01 = PDItems.ITEMS.register("memento_item_01",
            () -> new MementoItem01Item(new Item.Properties()));
    public static final DeferredItem<Item> MEMENTO_ITEM_02 = PDItems.ITEMS.registerSimpleItem("memento_item_02");
    public static final DeferredItem<Item> MEMENTO_ITEM_03 = PDItems.ITEMS.register("memento_item_03",
            () -> new DivinationItem(new Item.Properties()));
    public static final DeferredItem<Item> MEMENTO_ITEM_04 = PDItems.ITEMS.registerSimpleItem("memento_item_04");
    public static final DeferredItem<Item> MEMENTO_ITEM_05 = PDItems.ITEMS.registerSimpleItem("memento_item_05");
    public static final DeferredItem<Item> MEMENTO_ITEM_06 = PDItems.ITEMS.registerSimpleItem("memento_item_06");
    public static final DeferredItem<Item> MEMENTO_ITEM_07 = PDItems.ITEMS.registerSimpleItem("memento_item_07");
    public static final DeferredItem<Item> MEMENTO_ITEM_08 = PDItems.ITEMS.register("memento_item_08",
            () -> new SkyLinkItem(new Item.Properties()));
    public static final DeferredItem<Item> MEMENTO_ITEM_09 = PDItems.ITEMS.registerSimpleItem("memento_item_09");
    public static final DeferredItem<Item> MEMENTO_ITEM_10 = PDItems.ITEMS.registerSimpleItem("memento_item_10");
    public static final DeferredItem<Item> MEMORY_GEM_0 = PDItems.ITEMS.register("memory_gem_0",
            () -> new MemoryGem0Item(new Item.Properties()));
    public static final DeferredItem<Item> MOSS_PHANTOM_MEMBRANE = PDItems.ITEMS.register("moss_phantom_membrane",
            () -> new MossPhantomMembraneItem(new Item.Properties()));
    public static final DeferredItem<Item> POPPING_CANDY = PDItems.ITEMS.register("popping_candy",
            () -> new PoppingCandyItem(new Item.Properties()));
    public static final DeferredItem<Item> RED_DEW_0 = PDItems.ITEMS.register("red_dew_0",
            () -> new RedDew0Item(new Item.Properties()));
    public static final DeferredItem<Item> SHADOW_BREATH = PDItems.ITEMS.register("shadow_breath",
            () -> new ShadowBreathItem(new Item.Properties()));
    public static final DeferredItem<Item> SQUEAL_WAVE = PDItems.ITEMS.register("squeal_wave",
            () -> new SquealWaveItem(new Item.Properties()));
    public static final DeferredItem<Item> STRAWBERRY_HEART = PDItems.ITEMS.register("strawberry_heart",
            () -> new StrawberryHeartItem(new Item.Properties()));
    public static final DeferredItem<Item> WAFER_BISCUIT = PDItems.ITEMS.register("wafer_biscuit",
            () -> new WaferBiscuitItem(new Item.Properties()));


    // ==================== 调试结构法杖 ====================

    /**
     * 调试法杖 - 染梦列车
     */
    public static final DeferredItem<DebugStructureWandItem> DEBUG_WAND_DREAM_TRAIN =
            PDItems.ITEMS.register("debug_wand_dream_train",
                    () -> new DebugStructureWandItem(new Item.Properties().stacksTo(1), "dream_train"));

    /**
     * 调试法杖 - 巨型染梦树变体 0（NBT {@code dyedream_worldtree}，对应 worldgen dyedream_worldtree_0）
     */
    public static final DeferredItem<DebugStructureWandItem> DEBUG_WAND_WORLDTREE_0 =
            PDItems.ITEMS.register("debug_wand_worldtree_0",
                    () -> new DebugStructureWandItem(new Item.Properties().stacksTo(1), "dyedream_worldtree"));

    /**
     * 调试法杖 - 巨型染梦树变体 1 / 真树（NBT {@code dyedream_worldtree_true}，对应 worldgen dyedream_worldtree_1）
     * <p>注册名 {@code debug_wand_worldtree} 保留兼容旧档与创造栏引用。
     */
    public static final DeferredItem<DebugStructureWandItem> DEBUG_WAND_WORLDTREE =
            PDItems.ITEMS.register("debug_wand_worldtree",
                    () -> new DebugStructureWandItem(new Item.Properties().stacksTo(1), "dyedream_worldtree_true"));

    /** 与 {@link #DEBUG_WAND_WORLDTREE} 同实例别名，便于按 0/1 命名取用 */
    public static final DeferredItem<DebugStructureWandItem> DEBUG_WAND_WORLDTREE_1 = DEBUG_WAND_WORLDTREE;

    /**
     * 调试法杖 - 粉红菇屋 0
     */
    public static final DeferredItem<DebugStructureWandItem> DEBUG_WAND_PINKAGARIC_0 =
            PDItems.ITEMS.register("debug_wand_pinkagaric_0",
                    () -> new DebugStructureWandItem(new Item.Properties().stacksTo(1), "pinkagaric_house_0"));

    /**
     * 调试法杖 - 粉红菇屋 1
     */
    public static final DeferredItem<DebugStructureWandItem> DEBUG_WAND_PINKAGARIC_1 =
            PDItems.ITEMS.register("debug_wand_pinkagaric_1",
                    () -> new DebugStructureWandItem(new Item.Properties().stacksTo(1), "pinkagaric_house_1"));

    /**
     * 调试法杖 - 粉红菇屋 2
     */
    public static final DeferredItem<DebugStructureWandItem> DEBUG_WAND_PINKAGARIC_2 =
            PDItems.ITEMS.register("debug_wand_pinkagaric_2",
                    () -> new DebugStructureWandItem(new Item.Properties().stacksTo(1), "pinkagaric_house_2"));

    /**
     * 调试法杖 - 粉红菇屋 3
     */
    public static final DeferredItem<DebugStructureWandItem> DEBUG_WAND_PINKAGARIC_3 =
            PDItems.ITEMS.register("debug_wand_pinkagaric_3",
                    () -> new DebugStructureWandItem(new Item.Properties().stacksTo(1), "pinkagaric_house_3"));

    /**
     * 调试法杖 - 染梦裂隙
     */
    public static final DeferredItem<DebugStructureWandItem> DEBUG_WAND_DYEDREAM_CRACK =
            PDItems.ITEMS.register("debug_wand_dyedream_crack",
                    () -> new DebugStructureWandItem(new Item.Properties().stacksTo(1), "dyedreamcrack0"));

    /**
     * 调试法杖 - 沙漠小屋
     */
    public static final DeferredItem<DebugStructureWandItem> DEBUG_WAND_DESERT_COTTAGE =
            PDItems.ITEMS.register("debug_wand_desert_cottage",
                    () -> new DebugStructureWandItem(new Item.Properties().stacksTo(1), "desert_cottage_0"));

    /**
     * 调试法杖 - 云泡泡
     */
    public static final DeferredItem<DebugDecorWandItem> DEBUG_WAND_CLOUD_BUBBLE =
            PDItems.ITEMS.register("debug_wand_cloud_bubble",
                    () -> new DebugDecorWandItem(new Item.Properties().stacksTo(1), "cloud_bubble"));

    /**
     * 调试法杖 - 浮冰堆
     */
    public static final DeferredItem<DebugDecorWandItem> DEBUG_WAND_FLOATING_ICE_MOUND =
            PDItems.ITEMS.register("debug_wand_floating_ice_mound",
                    () -> new DebugDecorWandItem(new Item.Properties().stacksTo(1), "floating_ice_mound"));

    /**
     * 调试法杖 - 冰拱门
     */
    public static final DeferredItem<DebugDecorWandItem> DEBUG_WAND_ICE_ARCH =
            PDItems.ITEMS.register("debug_wand_ice_arch",
                    () -> new DebugDecorWandItem(new Item.Properties().stacksTo(1), "ice_arch"));

    /**
     * 调试法杖 - 冰拱门(毁)
     */
    public static final DeferredItem<DebugDecorWandItem> DEBUG_WAND_ICE_ARCH_RUINED =
            PDItems.ITEMS.register("debug_wand_ice_arch_ruined",
                    () -> new DebugDecorWandItem(new Item.Properties().stacksTo(1), "ice_arch_ruined"));

    /**
     * 调试法杖 - 染梦冰柱
     */
    public static final DeferredItem<DebugDecorWandItem> DEBUG_WAND_DYEDREAM_ICE_PILLAR =
            PDItems.ITEMS.register("debug_wand_dyedream_ice_pillar",
                    () -> new DebugDecorWandItem(new Item.Properties().stacksTo(1), "dyedream_ice_pillar"));

    /**
     * 调试法杖 - 冰晶丛
     */
    public static final DeferredItem<DebugDecorWandItem> DEBUG_WAND_ICE_CRYSTAL_CLUSTER =
            PDItems.ITEMS.register("debug_wand_ice_crystal_cluster",
                    () -> new DebugDecorWandItem(new Item.Properties().stacksTo(1), "ice_crystal_cluster"));

    /**
     * 调试法杖 - 冰霜尖刺
     */
    public static final DeferredItem<DebugDecorWandItem> DEBUG_WAND_FROST_SPIKE =
            PDItems.ITEMS.register("debug_wand_frost_spike",
                    () -> new DebugDecorWandItem(new Item.Properties().stacksTo(1), "frost_spike"));

    /**
     * 调试法杖 - 冰门
     */
    public static final DeferredItem<DebugDecorWandItem> DEBUG_WAND_ICE_GATE =
            PDItems.ITEMS.register("debug_wand_ice_gate",
                    () -> new DebugDecorWandItem(new Item.Properties().stacksTo(1), "ice_gate"));

    /**
     * 调试法杖 - 冰刺
     */
    public static final DeferredItem<DebugDecorWandItem> DEBUG_WAND_ICE_SPIKE =
            PDItems.ITEMS.register("debug_wand_ice_spike",
                    () -> new DebugDecorWandItem(new Item.Properties().stacksTo(1), "ice_spike"));

    /**
     * 调试法杖 - 冰晶花园
     */
    public static final DeferredItem<DebugDecorWandItem> DEBUG_WAND_ICE_CRYSTAL_GARDEN =
            PDItems.ITEMS.register("debug_wand_ice_crystal_garden",
                    () -> new DebugDecorWandItem(new Item.Properties().stacksTo(1), "ice_crystal_garden"));

    /**
     * 调试法杖 - 冰晶刺
     */
    public static final DeferredItem<DebugDecorWandItem> DEBUG_WAND_ICE_CRYSTAL_SPIKE =
            PDItems.ITEMS.register("debug_wand_ice_crystal_spike",
                    () -> new DebugDecorWandItem(new Item.Properties().stacksTo(1), "ice_crystal_spike"));

    /**
     * 调试法杖 - 冰柱
     */
    public static final DeferredItem<DebugDecorWandItem> DEBUG_WAND_ICE_PILLAR =
            PDItems.ITEMS.register("debug_wand_ice_pillar",
                    () -> new DebugDecorWandItem(new Item.Properties().stacksTo(1), "ice_pillar"));

    /**
     * 调试法杖 - 水下冰刺
     */
    public static final DeferredItem<DebugDecorWandItem> DEBUG_WAND_UNDERWATER_ICE_SPIKE =
            PDItems.ITEMS.register("debug_wand_underwater_ice_spike",
                    () -> new DebugDecorWandItem(new Item.Properties().stacksTo(1), "underwater_ice_spike"));

    /**
     * 调试法杖 - 海冰丘
     */
    public static final DeferredItem<DebugDecorWandItem> DEBUG_WAND_SEA_ICE_MOUND =
            PDItems.ITEMS.register("debug_wand_sea_ice_mound",
                    () -> new DebugDecorWandItem(new Item.Properties().stacksTo(1), "sea_ice_mound"));

    /**
     * 调试法杖 - 珊瑚礁
     */
    public static final DeferredItem<DebugDecorWandItem> DEBUG_WAND_CORAL_REEF =
            PDItems.ITEMS.register("debug_wand_coral_reef",
                    () -> new DebugDecorWandItem(new Item.Properties().stacksTo(1), "patch_coral_reef"));

    /**
     * 调试法杖 - 粉色珊瑚礁
     */
    public static final DeferredItem<DebugDecorWandItem> DEBUG_WAND_CORAL_REEF_PINK =
            PDItems.ITEMS.register("debug_wand_coral_reef_pink",
                    () -> new DebugDecorWandItem(new Item.Properties().stacksTo(1), "patch_coral_reef_pink"));

    /**
     * 调试法杖 - 巨型蘑菇
     */
    public static final DeferredItem<DebugDecorWandItem> DEBUG_WAND_MEGA_MUSHROOM =
            PDItems.ITEMS.register("debug_wand_mega_mushroom",
                    () -> new DebugDecorWandItem(new Item.Properties().stacksTo(1), "mega_mushroom"));

    /**
     * 调试法杖 - 巨型方解石柱
     */
    public static final DeferredItem<DebugDecorWandItem> DEBUG_WAND_MEGA_CALCITE_PILLAR =
            PDItems.ITEMS.register("debug_wand_mega_calcite_pillar",
                    () -> new DebugDecorWandItem(new Item.Properties().stacksTo(1), "mega_calcite_pillar"));

    /**
     * 调试法杖 - 粉红菇簇
     */
    public static final DeferredItem<DebugDecorWandItem> DEBUG_WAND_PINKAGARIC_CLUSTER =
            PDItems.ITEMS.register("debug_wand_pinkagaric_cluster",
                    () -> new DebugDecorWandItem(new Item.Properties().stacksTo(1), "pinkagaric_cluster"));

    /**
     * 调试法杖 - 方解石柱
     */
    public static final DeferredItem<DebugDecorWandItem> DEBUG_WAND_CALCITE_PILLAR =
            PDItems.ITEMS.register("debug_wand_calcite_pillar",
                    () -> new DebugDecorWandItem(new Item.Properties().stacksTo(1), "calcite_pillar"));

    /**
     * 调试法杖 - 染梦海草
     */
    public static final DeferredItem<DebugDecorWandItem> DEBUG_WAND_SEAGRASS =
            PDItems.ITEMS.register("debug_wand_seagrass",
                    () -> new DebugDecorWandItem(new Item.Properties().stacksTo(1), "patch_dyedream_seagrass"));

    /**
     * 调试法杖 - 亚伦柯斯竞技场
     * 右键瞄准方块放置 aaroncos_arena 结构，用于测试结构放置
     */
    public static final DeferredItem<DebugStructureWandItem> DEBUG_WAND_AARONCOS_ARENA =
            PDItems.ITEMS.register("debug_wand_aaroncos_arena",
                    () -> new DebugStructureWandItem(new Item.Properties().stacksTo(1), "aaroncos_arena"));


    // ======================== P0 移植遗迹调试水晶 ========================

    /**
     * 调试法杖 - 染梦悬浮寺庙
     * 右键瞄准方块放置 dyedream_floating_temple 结构
     */
    public static final DeferredItem<DebugStructureWandItem> DEBUG_WAND_DYEDREAM_FLOATING_TEMPLE =
            PDItems.ITEMS.register("debug_wand_dyedream_floating_temple",
                    () -> new DebugStructureWandItem(new Item.Properties().stacksTo(1), "dyedream_floating_temple"));

    /**
     * 调试法杖 - 梦想教堂 0~10（11 个变体）
     * 右键瞄准方块放置对应 dream_church_X 结构
     */
    public static final DeferredItem<DebugStructureWandItem> DEBUG_WAND_DREAM_CHURCH_0 =
            PDItems.ITEMS.register("debug_wand_dream_church_0",
                    () -> new DebugStructureWandItem(new Item.Properties().stacksTo(1), "dream_church_0"));
    public static final DeferredItem<DebugStructureWandItem> DEBUG_WAND_DREAM_CHURCH_1 =
            PDItems.ITEMS.register("debug_wand_dream_church_1",
                    () -> new DebugStructureWandItem(new Item.Properties().stacksTo(1), "dream_church_1"));
    public static final DeferredItem<DebugStructureWandItem> DEBUG_WAND_DREAM_CHURCH_2 =
            PDItems.ITEMS.register("debug_wand_dream_church_2",
                    () -> new DebugStructureWandItem(new Item.Properties().stacksTo(1), "dream_church_2"));
    public static final DeferredItem<DebugStructureWandItem> DEBUG_WAND_DREAM_CHURCH_3 =
            PDItems.ITEMS.register("debug_wand_dream_church_3",
                    () -> new DebugStructureWandItem(new Item.Properties().stacksTo(1), "dream_church_3"));
    public static final DeferredItem<DebugStructureWandItem> DEBUG_WAND_DREAM_CHURCH_4 =
            PDItems.ITEMS.register("debug_wand_dream_church_4",
                    () -> new DebugStructureWandItem(new Item.Properties().stacksTo(1), "dream_church_4"));
    public static final DeferredItem<DebugStructureWandItem> DEBUG_WAND_DREAM_CHURCH_5 =
            PDItems.ITEMS.register("debug_wand_dream_church_5",
                    () -> new DebugStructureWandItem(new Item.Properties().stacksTo(1), "dream_church_5"));
    public static final DeferredItem<DebugStructureWandItem> DEBUG_WAND_DREAM_CHURCH_6 =
            PDItems.ITEMS.register("debug_wand_dream_church_6",
                    () -> new DebugStructureWandItem(new Item.Properties().stacksTo(1), "dream_church_6"));
    public static final DeferredItem<DebugStructureWandItem> DEBUG_WAND_DREAM_CHURCH_7 =
            PDItems.ITEMS.register("debug_wand_dream_church_7",
                    () -> new DebugStructureWandItem(new Item.Properties().stacksTo(1), "dream_church_7"));
    public static final DeferredItem<DebugStructureWandItem> DEBUG_WAND_DREAM_CHURCH_8 =
            PDItems.ITEMS.register("debug_wand_dream_church_8",
                    () -> new DebugStructureWandItem(new Item.Properties().stacksTo(1), "dream_church_8"));
    public static final DeferredItem<DebugStructureWandItem> DEBUG_WAND_DREAM_CHURCH_9 =
            PDItems.ITEMS.register("debug_wand_dream_church_9",
                    () -> new DebugStructureWandItem(new Item.Properties().stacksTo(1), "dream_church_9"));
    public static final DeferredItem<DebugStructureWandItem> DEBUG_WAND_DREAM_CHURCH_10 =
            PDItems.ITEMS.register("debug_wand_dream_church_10",
                    () -> new DebugStructureWandItem(new Item.Properties().stacksTo(1), "dream_church_10"));

    /**
     * 调试法杖 - 沙漠堡垒
     * 右键瞄准方块放置 desert_fortress 结构（注意 NBT 文件名为 desert_fortress，无 _0 后缀）
     */
    public static final DeferredItem<DebugStructureWandItem> DEBUG_WAND_DESERT_FORTRESS_0 =
            PDItems.ITEMS.register("debug_wand_desert_fortress_0",
                    () -> new DebugStructureWandItem(new Item.Properties().stacksTo(1), "desert_fortress"));


    // ======================== P1 移植遗迹调试水晶 ========================

    public static final DeferredItem<DebugStructureWandItem> DEBUG_WAND_DYEDREAM_TOWER_0 =
            PDItems.ITEMS.register("debug_wand_dyedream_tower_0",
                    () -> new DebugStructureWandItem(new Item.Properties().stacksTo(1), "dyedream_tower_0"));

    public static final DeferredItem<DebugStructureWandItem> DEBUG_WAND_DYEDREAM_TOWER_1 =
            PDItems.ITEMS.register("debug_wand_dyedream_tower_1",
                    () -> new DebugStructureWandItem(new Item.Properties().stacksTo(1), "dyedream_tower_1"));

    public static final DeferredItem<DebugStructureWandItem> DEBUG_WAND_DYEDREAM_LABORATORY_0 =
            PDItems.ITEMS.register("debug_wand_dyedream_laboratory_0",
                    () -> new DebugStructureWandItem(new Item.Properties().stacksTo(1), "dyedream_laboratory_0"));

    public static final DeferredItem<DebugStructureWandItem> DEBUG_WAND_DYEDREAM_TAVERN =
            PDItems.ITEMS.register("debug_wand_dyedream_tavern",
                    () -> new DebugStructureWandItem(new Item.Properties().stacksTo(1), "dyedream_tavern"));

    public static final DeferredItem<DebugStructureWandItem> DEBUG_WAND_DYEDREAM_PAVILION_0 =
            PDItems.ITEMS.register("debug_wand_dyedream_pavilion_0",
                    () -> new DebugStructureWandItem(new Item.Properties().stacksTo(1), "dyedream_pavilion_0"));

    public static final DeferredItem<DebugStructureWandItem> DEBUG_WAND_DYEDREAM_PAVILION_1 =
            PDItems.ITEMS.register("debug_wand_dyedream_pavilion_1",
                    () -> new DebugStructureWandItem(new Item.Properties().stacksTo(1), "dyedream_pavilion_1"));

    public static final DeferredItem<DebugStructureWandItem> DEBUG_WAND_DYEDREAM_PAVILION_2 =
            PDItems.ITEMS.register("debug_wand_dyedream_pavilion_2",
                    () -> new DebugStructureWandItem(new Item.Properties().stacksTo(1), "dyedream_pavilion_2"));

    public static final DeferredItem<DebugStructureWandItem> DEBUG_WAND_DYEDREAM_CAMPSITE_0 =
            PDItems.ITEMS.register("debug_wand_dyedream_campsite_0",
                    () -> new DebugStructureWandItem(new Item.Properties().stacksTo(1), "dyedream_campsite_0"));

    public static final DeferredItem<DebugStructureWandItem> DEBUG_WAND_DREAM_WISHINGTREE_0 =
            PDItems.ITEMS.register("debug_wand_dream_wishingtree_0",
                    () -> new DebugStructureWandItem(new Item.Properties().stacksTo(1), "dream_wishingtree_0"));

    public static final DeferredItem<DebugStructureWandItem> DEBUG_WAND_DREAM_WISHINGTREE_1 =
            PDItems.ITEMS.register("debug_wand_dream_wishingtree_1",
                    () -> new DebugStructureWandItem(new Item.Properties().stacksTo(1), "dream_wishingtree_1"));

    public static final DeferredItem<DebugStructureWandItem> DEBUG_WAND_TRAVELER_HOUSE_0 =
            PDItems.ITEMS.register("debug_wand_traveler_house_0",
                    () -> new DebugStructureWandItem(new Item.Properties().stacksTo(1), "traveler_house_0"));

    public static final DeferredItem<DebugStructureWandItem> DEBUG_WAND_TRAVELER_HOUSE_1 =
            PDItems.ITEMS.register("debug_wand_traveler_house_1",
                    () -> new DebugStructureWandItem(new Item.Properties().stacksTo(1), "traveler_house_1"));

    public static final DeferredItem<DebugStructureWandItem> DEBUG_WAND_TRAVELER_HOUSE_2 =
            PDItems.ITEMS.register("debug_wand_traveler_house_2",
                    () -> new DebugStructureWandItem(new Item.Properties().stacksTo(1), "traveler_house_2"));

    public static final DeferredItem<DebugStructureWandItem> DEBUG_WAND_GARDEN_DECRYPTION_0 =
            PDItems.ITEMS.register("debug_wand_garden_decryption_0",
                    () -> new DebugStructureWandItem(new Item.Properties().stacksTo(1), "garden_decryption_0"));

    public static final DeferredItem<DebugStructureWandItem> DEBUG_WAND_GARDEN_DECRYPTION_1 =
            PDItems.ITEMS.register("debug_wand_garden_decryption_1",
                    () -> new DebugStructureWandItem(new Item.Properties().stacksTo(1), "garden_decryption_1"));

    public static final DeferredItem<DebugStructureWandItem> DEBUG_WAND_GARDEN_DECRYPTION_2 =
            PDItems.ITEMS.register("debug_wand_garden_decryption_2",
                    () -> new DebugStructureWandItem(new Item.Properties().stacksTo(1), "garden_decryption_2"));

    public static final DeferredItem<DebugStructureWandItem> DEBUG_WAND_PICNIC_BASKET =
            PDItems.ITEMS.register("debug_wand_picnic_basket_structure",
                    () -> new DebugStructureWandItem(new Item.Properties().stacksTo(1), "picnic_basket_structure"));

    public static final DeferredItem<DebugStructureWandItem> DEBUG_WAND_MELTDREAM_LIQUID_WELL_0 =
            PDItems.ITEMS.register("debug_wand_meltdream_liquid_well_0",
                    () -> new DebugStructureWandItem(new Item.Properties().stacksTo(1), "meltdream_liquid_well_0"));

    public static final DeferredItem<DebugStructureWandItem> DEBUG_WAND_MELTDREAM_LIQUID_WELL_1 =
            PDItems.ITEMS.register("debug_wand_meltdream_liquid_well_1",
                    () -> new DebugStructureWandItem(new Item.Properties().stacksTo(1), "meltdream_liquid_well"));

    /**
     * 调试法杖 - 染梦草
     */
    public static final DeferredItem<DebugDecorWandItem> DEBUG_WAND_GRASS =
            PDItems.ITEMS.register("debug_wand_grass",
                    () -> new DebugDecorWandItem(new Item.Properties().stacksTo(1), "patch_dyedream_grass"));

    /**
     * 调试法杖 - 染梦芽
     */
    public static final DeferredItem<DebugDecorWandItem> DEBUG_WAND_BUDS =
            PDItems.ITEMS.register("debug_wand_buds",
                    () -> new DebugDecorWandItem(new Item.Properties().stacksTo(1), "patch_dyedream_buds"));

    /**
     * 调试法杖 - 染梦莲
     */
    public static final DeferredItem<DebugDecorWandItem> DEBUG_WAND_LOTUS =
            PDItems.ITEMS.register("debug_wand_lotus",
                    () -> new DebugDecorWandItem(new Item.Properties().stacksTo(1), "patch_dyedream_lotus"));

    /**
     * 调试法杖 - 荷叶
     */
    public static final DeferredItem<DebugDecorWandItem> DEBUG_WAND_LILY_PAD =
            PDItems.ITEMS.register("debug_wand_lily_pad",
                    () -> new DebugDecorWandItem(new Item.Properties().stacksTo(1), "patch_dyedream_lily_pad"));

    /**
     * 调试法杖 - 粉红菇
     */
    public static final DeferredItem<DebugDecorWandItem> DEBUG_WAND_PINKAGARIC =
            PDItems.ITEMS.register("debug_wand_pinkagaric",
                    () -> new DebugDecorWandItem(new Item.Properties().stacksTo(1), "patch_pinkagaric_0"));


    // ==================== 染梦世界装饰物调试水晶 ====================

    /**
     * 调试水晶 - 染梦水晶簇
     */
    public static final DeferredItem<DebugDecorWandItem> DEBUG_WAND_DYEDREAM_CRYSTAL_CLUSTER =
            PDItems.ITEMS.register("debug_wand_dyedream_crystal_cluster",
                    () -> new DebugDecorWandItem(new Item.Properties().stacksTo(1), "dyedream_crystal_cluster"));

    /**
     * 调试水晶 - 融梦水晶灯柱
     */
    public static final DeferredItem<DebugDecorWandItem> DEBUG_WAND_MELTDREAM_CRYSTAL_PILLAR =
            PDItems.ITEMS.register("debug_wand_meltdream_crystal_pillar",
                    () -> new DebugDecorWandItem(new Item.Properties().stacksTo(1), "meltdream_crystal_pillar"));

    /**
     * 调试水晶 - 浮空云岛
     */
    public static final DeferredItem<DebugDecorWandItem> DEBUG_WAND_FLOATING_CLOUD_ISLAND =
            PDItems.ITEMS.register("debug_wand_floating_cloud_island",
                    () -> new DebugDecorWandItem(new Item.Properties().stacksTo(1), "floating_cloud_island"));

    /**
     * 调试水晶 - 方解石水晶花园
     */
    public static final DeferredItem<DebugDecorWandItem> DEBUG_WAND_CALCITE_CRYSTAL_GARDEN =
            PDItems.ITEMS.register("debug_wand_calcite_crystal_garden",
                    () -> new DebugDecorWandItem(new Item.Properties().stacksTo(1), "calcite_crystal_garden"));

    /**
     * 调试水晶 - 暖水晶山脉
     */
    public static final DeferredItem<DebugDecorWandItem> DEBUG_WAND_WARM_CRYSTAL_SPIKE =
            PDItems.ITEMS.register("debug_wand_warm_crystal_spike",
                    () -> new DebugDecorWandItem(new Item.Properties().stacksTo(1), "warm_crystal_spike"));

    /**
     * 调试水晶 - 粉丁菇森林
     */
    public static final DeferredItem<DebugDecorWandItem> DEBUG_WAND_PINKAGARIC_FOREST =
            PDItems.ITEMS.register("debug_wand_pinkagaric_forest",
                    () -> new DebugDecorWandItem(new Item.Properties().stacksTo(1), "pinkagaric_forest"));


    // ==================== 染梦世界树木调试水晶 ====================

    /**
     * 调试水晶 - 染梦树
     */
    public static final DeferredItem<DebugDecorWandItem> DEBUG_WAND_DYEDREAM_TREE =
            PDItems.ITEMS.register("debug_wand_dyedream_tree",
                    () -> new DebugDecorWandItem(new Item.Properties().stacksTo(1), "dyedream_tree"));

    /**
     * 调试水晶 - 大型染梦树
     */
    public static final DeferredItem<DebugDecorWandItem> DEBUG_WAND_DYEDREAM_TREE_LARGE =
            PDItems.ITEMS.register("debug_wand_dyedream_tree_large",
                    () -> new DebugDecorWandItem(new Item.Properties().stacksTo(1), "dyedream_tree_large"));

    /**
     * 调试水晶 - 垂枝染梦树
     */
    public static final DeferredItem<DebugDecorWandItem> DEBUG_WAND_DYEDREAM_TREE_WEEPING =
            PDItems.ITEMS.register("debug_wand_dyedream_tree_weeping",
                    () -> new DebugDecorWandItem(new Item.Properties().stacksTo(1), "dyedream_tree_weeping"));

    /**
     * 调试水晶 - 丛生染梦树
     */
    public static final DeferredItem<DebugDecorWandItem> DEBUG_WAND_DYEDREAM_TREE_BUSHY =
            PDItems.ITEMS.register("debug_wand_dyedream_tree_bushy",
                    () -> new DebugDecorWandItem(new Item.Properties().stacksTo(1), "dyedream_tree_bushy"));

    /**
     * 调试水晶 - 华丽染梦树
     */
    public static final DeferredItem<DebugDecorWandItem> DEBUG_WAND_DYEDREAM_TREE_FANCY =
            PDItems.ITEMS.register("debug_wand_dyedream_tree_fancy",
                    () -> new DebugDecorWandItem(new Item.Properties().stacksTo(1), "dyedream_tree_fancy"));

    /**
     * 调试水晶 - 发光染梦树
     */
    public static final DeferredItem<DebugDecorWandItem> DEBUG_WAND_DYEDREAM_TREE_GLOWING =
            PDItems.ITEMS.register("debug_wand_dyedream_tree_glowing",
                    () -> new DebugDecorWandItem(new Item.Properties().stacksTo(1), "dyedream_tree_glowing"));

    /**
     * 调试水晶 - 冰雪染梦树
     */
    public static final DeferredItem<DebugDecorWandItem> DEBUG_WAND_DYEDREAM_TREE_ICY =
            PDItems.ITEMS.register("debug_wand_dyedream_tree_icy",
                    () -> new DebugDecorWandItem(new Item.Properties().stacksTo(1), "dyedream_tree_icy"));


    // ==================== Better Biomes 移植树调试水晶 ====================
    // 原封不动提取 Better Biomes 数据包树结构 NBT（方块替换为染梦 log/leaves），
    // 用 DebugStructureWandItem 直接放置结构，右键生成对应树。
    // centered=true：结构中心对齐点击点，树冠以点击位置为中心展开。

    /** 调试水晶 - 染梦高桦（提取 Better Biomes tallbirch） */
    public static final DeferredItem<DebugStructureWandItem> DEBUG_WAND_DYEDREAM_TREE_TALLBIRCH =
            PDItems.ITEMS.register("debug_wand_dyedream_tree_tallbirch",
                    () -> new DebugStructureWandItem(new Item.Properties().stacksTo(1), "bb_tallbirch", true));

    /** 调试水晶 - 染梦樱花大冠树（提取 Better Biomes blossom） */
    public static final DeferredItem<DebugStructureWandItem> DEBUG_WAND_DYEDREAM_TREE_BLOSSOM =
            PDItems.ITEMS.register("debug_wand_dyedream_tree_blossom",
                    () -> new DebugStructureWandItem(new Item.Properties().stacksTo(1), "bb_blossom", true));

    /** 调试水晶 - 染梦大白杨（提取 Better Biomes aspen/big） */
    public static final DeferredItem<DebugStructureWandItem> DEBUG_WAND_DYEDREAM_TREE_ASPEN =
            PDItems.ITEMS.register("debug_wand_dyedream_tree_aspen",
                    () -> new DebugStructureWandItem(new Item.Properties().stacksTo(1), "bb_aspen_big", true));

    /** 调试水晶 - 染梦杨树（提取 Better Biomes poplar） */
    public static final DeferredItem<DebugStructureWandItem> DEBUG_WAND_DYEDREAM_TREE_POPLAR =
            PDItems.ITEMS.register("debug_wand_dyedream_tree_poplar",
                    () -> new DebugStructureWandItem(new Item.Properties().stacksTo(1), "bb_poplar", true));

    /** 调试水晶 - 染梦中白杨（提取 Better Biomes aspen/mid） */
    public static final DeferredItem<DebugStructureWandItem> DEBUG_WAND_DYEDREAM_TREE_ASPEN_MID =
            PDItems.ITEMS.register("debug_wand_dyedream_tree_aspen_mid",
                    () -> new DebugStructureWandItem(new Item.Properties().stacksTo(1), "bb_aspen_mid", true));

    /** 调试水晶 - 染梦小白杨（提取 Better Biomes aspen/small） */
    public static final DeferredItem<DebugStructureWandItem> DEBUG_WAND_DYEDREAM_TREE_ASPEN_SMALL =
            PDItems.ITEMS.register("debug_wand_dyedream_tree_aspen_small",
                    () -> new DebugStructureWandItem(new Item.Properties().stacksTo(1), "bb_aspen_small", true));

    /** 调试水晶 - 染梦灌木（提取 Better Biomes bush） */
    public static final DeferredItem<DebugStructureWandItem> DEBUG_WAND_DYEDREAM_TREE_BUSH =
            PDItems.ITEMS.register("debug_wand_dyedream_tree_bush",
                    () -> new DebugStructureWandItem(new Item.Properties().stacksTo(1), "bb_bush", true));

    /** 调试水晶 - 染梦樱桃灌木（提取 Better Biomes cherrybush） */
    public static final DeferredItem<DebugStructureWandItem> DEBUG_WAND_DYEDREAM_TREE_CHERRYBUSH =
            PDItems.ITEMS.register("debug_wand_dyedream_tree_cherrybush",
                    () -> new DebugStructureWandItem(new Item.Properties().stacksTo(1), "bb_cherrybush", true));

    /** 调试水晶 - 染梦平原树（提取 Better Biomes plaintree） */
    public static final DeferredItem<DebugStructureWandItem> DEBUG_WAND_DYEDREAM_TREE_PLAIN =
            PDItems.ITEMS.register("debug_wand_dyedream_tree_plain",
                    () -> new DebugStructureWandItem(new Item.Properties().stacksTo(1), "bb_plaintree", true));

    /** 调试水晶 - 染梦棕榈树（提取 Better Biomes smallpalm1） */
    public static final DeferredItem<DebugStructureWandItem> DEBUG_WAND_DYEDREAM_TREE_PALM =
            PDItems.ITEMS.register("debug_wand_dyedream_tree_palm",
                    () -> new DebugStructureWandItem(new Item.Properties().stacksTo(1), "bb_smallpalm1", true));

    /** 调试水晶 - 染梦雪树（提取 Better Biomes snowtree，保留雪方块） */
    public static final DeferredItem<DebugStructureWandItem> DEBUG_WAND_DYEDREAM_TREE_SNOW =
            PDItems.ITEMS.register("debug_wand_dyedream_tree_snow",
                    () -> new DebugStructureWandItem(new Item.Properties().stacksTo(1), "bb_snowtree", true));

    /** 调试水晶 - 染梦巨型针叶树（提取 Better Biomes conifers/big1-0） */
    public static final DeferredItem<DebugStructureWandItem> DEBUG_WAND_DYEDREAM_TREE_CONIFER =
            PDItems.ITEMS.register("debug_wand_dyedream_tree_conifer",
                    () -> new DebugStructureWandItem(new Item.Properties().stacksTo(1), "bb_conifer_big", true));


    // ==================== 结构方块补全调试水晶 ====================
    // 以下对应 PDStructureBlock 中尚未有独立调试水晶的结构方块，
    // 右键复现原结构方块的随机抽号、偏移与旋转逻辑。

    /** 调试水晶 - 染梦水晶球（对应 structure_block_6） */
    public static final DeferredItem<DebugStructureBlockWandItem> DEBUG_WAND_CRYSTAL_BALL =
            PDItems.ITEMS.register("debug_wand_crystal_ball",
                    () -> new DebugStructureBlockWandItem(new Item.Properties().stacksTo(1), 6));

    /** 调试水晶 - 染梦浮空石柱（对应 structure_block_8） */
    public static final DeferredItem<DebugStructureBlockWandItem> DEBUG_WAND_STONE_PILLAR_SKY =
            PDItems.ITEMS.register("debug_wand_stone_pillar_sky",
                    () -> new DebugStructureBlockWandItem(new Item.Properties().stacksTo(1), 8));

    /** 调试水晶 - 暮影之笼（对应 structure_block_9） */
    public static final DeferredItem<DebugStructureBlockWandItem> DEBUG_WAND_SHADOW_WORLD_DOOR =
            PDItems.ITEMS.register("debug_wand_shadow_world_door",
                    () -> new DebugStructureBlockWandItem(new Item.Properties().stacksTo(1), 9));

    /** 调试水晶 - 阴影坟墓（对应 structure_block_10） */
    public static final DeferredItem<DebugStructureBlockWandItem> DEBUG_WAND_SHADOW_TOMB =
            PDItems.ITEMS.register("debug_wand_shadow_tomb",
                    () -> new DebugStructureBlockWandItem(new Item.Properties().stacksTo(1), 10));

    /** 调试水晶 - 阴影灯笼链（对应 structure_block_11） */
    public static final DeferredItem<DebugStructureBlockWandItem> DEBUG_WAND_SHADOW_CHAIN =
            PDItems.ITEMS.register("debug_wand_shadow_chain",
                    () -> new DebugStructureBlockWandItem(new Item.Properties().stacksTo(1), 11));

    /** 调试水晶 - 坟墓庇护所（对应 structure_block_13） */
    public static final DeferredItem<DebugStructureBlockWandItem> DEBUG_WAND_SHADOW_SHELTER =
            PDItems.ITEMS.register("debug_wand_shadow_shelter",
                    () -> new DebugStructureBlockWandItem(new Item.Properties().stacksTo(1), 13));

    /** 调试水晶 - 阴影虫巢（对应 structure_block_14） */
    public static final DeferredItem<DebugStructureBlockWandItem> DEBUG_WAND_SHADOW_FUNGUS_NEST =
            PDItems.ITEMS.register("debug_wand_shadow_fungus_nest",
                    () -> new DebugStructureBlockWandItem(new Item.Properties().stacksTo(1), 14));

    /** 调试水晶 - 暗影高炉（对应 structure_block_15） */
    public static final DeferredItem<DebugStructureBlockWandItem> DEBUG_WAND_SHADOW_FOUNDRY =
            PDItems.ITEMS.register("debug_wand_shadow_foundry",
                    () -> new DebugStructureBlockWandItem(new Item.Properties().stacksTo(1), 15));

    /** 调试水晶 - 暗影地牢（对应 structure_block_17） */
    public static final DeferredItem<DebugStructureBlockWandItem> DEBUG_WAND_SHADOW_DUNGEON =
            PDItems.ITEMS.register("debug_wand_shadow_dungeon",
                    () -> new DebugStructureBlockWandItem(new Item.Properties().stacksTo(1), 17));

    /** 调试水晶 - 阴影蘑菇小屋（对应 structure_block_18） */
    public static final DeferredItem<DebugStructureBlockWandItem> DEBUG_WAND_SHADOW_FUNGUS_HOUSE =
            PDItems.ITEMS.register("debug_wand_shadow_fungus_house",
                    () -> new DebugStructureBlockWandItem(new Item.Properties().stacksTo(1), 18));

    /** 调试水晶 - 阴影地下工作室（对应 structure_block_19） */
    public static final DeferredItem<DebugStructureBlockWandItem> DEBUG_WAND_SHADOW_UNDERGROUND_WORKROOM =
            PDItems.ITEMS.register("debug_wand_shadow_underground_workroom",
                    () -> new DebugStructureBlockWandItem(new Item.Properties().stacksTo(1), 19));

    /** 调试水晶 - 大风泊树（对应 structure_block_21） */
    public static final DeferredItem<DebugStructureBlockWandItem> DEBUG_WAND_WINDMOOR_TREE =
            PDItems.ITEMS.register("debug_wand_windmoor_tree",
                    () -> new DebugStructureBlockWandItem(new Item.Properties().stacksTo(1), 21));

    /** 调试水晶 - 热气球（对应 structure_block_22） */
    public static final DeferredItem<DebugStructureBlockWandItem> DEBUG_WAND_HOT_AIR_BALLOON =
            PDItems.ITEMS.register("debug_wand_hot_air_balloon",
                    () -> new DebugStructureBlockWandItem(new Item.Properties().stacksTo(1), 22));

    /** 调试水晶 - 圣诞树（对应 structure_block_23） */
    public static final DeferredItem<DebugStructureBlockWandItem> DEBUG_WAND_CHRISTMAS_TREE =
            PDItems.ITEMS.register("debug_wand_christmas_tree",
                    () -> new DebugStructureBlockWandItem(new Item.Properties().stacksTo(1), 23));


    // ==================== BOSS 相关物品 ====================

    /**
     * 亚伦柯斯竞技场创建器 (aaroncos_arena_create)
     * 创造模式调试用物品，右键点击生成/传送至亚伦柯斯 BOSS 竞技场
     */
    public static final DeferredItem<Item> AARONCOS_ARENA_CREATE = PDItems.ITEMS.register("aaroncos_arena_create",
            () -> new com.pasterdream.pasterdreammod.item.AaroncosArenaCreateItem());

    /**
     * 亚伦柯斯竞技场传送门 BlockItem (aaroncos_arena_portals)
     */
    public static final DeferredItem<BlockItem> AARONCOS_ARENA_PORTALS = PDItems.ITEMS.registerSimpleBlockItem("aaroncos_arena_portals",
            PDBlocks.AARONCOS_ARENA_PORTALS);

    /**
     * 亚伦柯斯之触战利品箱物品 (aaroncos_hand_chest)
     * 对应 PDBlocks.AARONCOS_HAND_CHEST 方块
     * 使用 AaroncosHandChestDisplayItem 实现手持 3D 渲染
     */
    public static final DeferredItem<AaroncosHandChestDisplayItem> AARONCOS_HAND_CHEST = PDItems.ITEMS.register("aaroncos_hand_chest",
            () -> new AaroncosHandChestDisplayItem(new Item.Properties()));

    /**
     * 亚伦柯斯之手生成激活方块物品 (aaroncoshandspawnblock)
     * 对应 PDBlocks.AARONCOSHANDSPAWNBLOCK 方块
     * 使用 AaroncosHandSpawnBlockDisplayItem 实现手持 3D 渲染
     */
    public static final DeferredItem<AaroncosHandSpawnBlockDisplayItem> AARONCOSHANDSPAWNBLOCK = PDItems.ITEMS.register("aaroncoshandspawnblock",
            () -> new AaroncosHandSpawnBlockDisplayItem(new Item.Properties()));


    // ==================== 杂项补全：功能物品（批量移植自原版） ====================

    /**
     * 时之沙 (time_hourglass)
     * 对空气使用跳跃世界时间；右击计时梦境方块瞬间完成一个阶段
     */
    public static final DeferredItem<TimeHourglassItem> TIME_HOURGLASS = PDItems.ITEMS.register("time_hourglass",
            () -> new TimeHourglassItem(new Item.Properties()));

    /**
     * 卡莱的黄金预言（卡勒占卜卡牌）
     * 0 号为占卜牌，使用后随机抽取一张预言卡牌；其余为具体预言卡牌
     */
    public static final DeferredItem<CalleCardItem> CALLE_CARD_0 = PDItems.ITEMS.register("calle_card_0",
            () -> new CalleCardItem(0));
    public static final DeferredItem<CalleCardItem> CALLE_CARD_1 = PDItems.ITEMS.register("calle_card_1",
            () -> new CalleCardItem(1,
                    "tooltip.pasterdream.calle_card.name.1",
                    "tooltip.pasterdream.calle_card.usage",
                    "tooltip.pasterdream.calle_card.desc.1.0",
                    "tooltip.pasterdream.calle_card.desc.1.1"));
    public static final DeferredItem<CalleCardItem> CALLE_CARD_2 = PDItems.ITEMS.register("calle_card_2",
            () -> new CalleCardItem(2,
                    "tooltip.pasterdream.calle_card.name.2",
                    "tooltip.pasterdream.calle_card.usage",
                    "tooltip.pasterdream.calle_card.desc.2.0"));
    public static final DeferredItem<CalleCardItem> CALLE_CARD_3 = PDItems.ITEMS.register("calle_card_3",
            () -> new CalleCardItem(3,
                    "tooltip.pasterdream.calle_card.name.3",
                    "tooltip.pasterdream.calle_card.usage",
                    "tooltip.pasterdream.calle_card.desc.3.0",
                    "tooltip.pasterdream.calle_card.desc.3.1"));
    public static final DeferredItem<CalleCardItem> CALLE_CARD_4 = PDItems.ITEMS.register("calle_card_4",
            () -> new CalleCardItem(4,
                    "tooltip.pasterdream.calle_card.name.4",
                    "tooltip.pasterdream.calle_card.usage",
                    "tooltip.pasterdream.calle_card.desc.4.0"));
    public static final DeferredItem<CalleCardItem> CALLE_CARD_5 = PDItems.ITEMS.register("calle_card_5",
            () -> new CalleCardItem(5,
                    "tooltip.pasterdream.calle_card.name.5",
                    "tooltip.pasterdream.calle_card.usage",
                    "tooltip.pasterdream.calle_card.desc.5.0"));
    public static final DeferredItem<CalleCardItem> CALLE_CARD_6 = PDItems.ITEMS.register("calle_card_6",
            () -> new CalleCardItem(6,
                    "tooltip.pasterdream.calle_card.name.6",
                    "tooltip.pasterdream.calle_card.usage",
                    "tooltip.pasterdream.calle_card.desc.6.0",
                    "tooltip.pasterdream.calle_card.desc.6.1"));
    public static final DeferredItem<CalleCardItem> CALLE_CARD_7 = PDItems.ITEMS.register("calle_card_7",
            () -> new CalleCardItem(7,
                    "tooltip.pasterdream.calle_card.name.7",
                    "tooltip.pasterdream.calle_card.usage",
                    "tooltip.pasterdream.calle_card.desc.7.0",
                    "tooltip.pasterdream.calle_card.desc.7.1"));
    public static final DeferredItem<CalleCardItem> CALLE_CARD_8 = PDItems.ITEMS.register("calle_card_8",
            () -> new CalleCardItem(8,
                    "tooltip.pasterdream.calle_card.name.8",
                    "tooltip.pasterdream.calle_card.usage",
                    "tooltip.pasterdream.calle_card.desc.8.0"));
    public static final DeferredItem<CalleCardItem> CALLE_CARD_9 = PDItems.ITEMS.register("calle_card_9",
            () -> new CalleCardItem(9,
                    "tooltip.pasterdream.calle_card.name.9",
                    "tooltip.pasterdream.calle_card.usage",
                    "tooltip.pasterdream.calle_card.desc.9.0",
                    "tooltip.pasterdream.calle_card.desc.9.1"));

    /**
     * 深海秘宝 / 染梦深海秘宝 (deep_treasure_0 / deep_treasure_1)
     * 右键开启随机战利品；带 deep_treasure_super 自定义数据时呈附魔光效并使用超级战利品表
     */
    public static final DeferredItem<DeepTreasureItem> DEEP_TREASURE_0 = PDItems.ITEMS.register("deep_treasure_0",
            () -> new DeepTreasureItem("chests/loots_deep_treasure_0"));
    public static final DeferredItem<DeepTreasureItem> DEEP_TREASURE_1 = PDItems.ITEMS.register("deep_treasure_1",
            () -> new DeepTreasureItem("chests/loots_deep_treasure_1"));

    /**
     * 梦境果汁 (dreamjuice)
     * 饮用后（需完成成就 achievement_b_0）获得梦愿效果 90 秒
     */
    public static final DeferredItem<DreamjuiceItem> DREAMJUICE = PDItems.ITEMS.register("dreamjuice",
            () -> new DreamjuiceItem(new Item.Properties()));

    /**
     * 失色塞西莉娅的加护 (turn_pale_cecilia)
     * 瞄准融梦液体源使用，转化为塞西莉娅的关怀饰品
     */
    public static final DeferredItem<TurnPaleCeciliaItem> TURN_PALE_CECILIA = PDItems.ITEMS.register("turn_pale_cecilia",
            () -> new TurnPaleCeciliaItem(new Item.Properties()));

    /**
     * 挖掘机3000! (excavator)
     * 调试用挖掘工具：右键石头/深板岩挖掘前方 5*5*20 区域
     */
    public static final DeferredItem<ExcavatorItem> EXCAVATOR = PDItems.ITEMS.register("excavator",
            () -> new ExcavatorItem(new Item.Properties()));

    /**
     * 战利品生成工具 (lootstable_create_0~9)
     * 潜行右击容器方块写入对应主题的战利品表（结构搭建/调试用）
     */
    public static final DeferredItem<LootstableCreateItem> LOOTSTABLE_CREATE_0 = PDItems.ITEMS.register("lootstable_create_0",
            () -> new LootstableCreateItem("chests/loots_relic_0", "§e染梦世界遗迹通用"));
    public static final DeferredItem<LootstableCreateItem> LOOTSTABLE_CREATE_1 = PDItems.ITEMS.register("lootstable_create_1",
            () -> new LootstableCreateItem("loots_relic_1", "§e染梦世界遗迹通用 (少量)"));
    public static final DeferredItem<LootstableCreateItem> LOOTSTABLE_CREATE_2 = PDItems.ITEMS.register("lootstable_create_2",
            () -> new LootstableCreateItem("chests/loots_relic_2", "§e沙漠通用"));
    public static final DeferredItem<LootstableCreateItem> LOOTSTABLE_CREATE_3 = PDItems.ITEMS.register("lootstable_create_3",
            () -> new LootstableCreateItem("chests/loots_relic_3", "§e灯影通用"));
    public static final DeferredItem<LootstableCreateItem> LOOTSTABLE_CREATE_4 = PDItems.ITEMS.register("lootstable_create_4",
            () -> new LootstableCreateItem("chests/loots_relic_4", "§e诡异森林遗迹通用"));
    public static final DeferredItem<LootstableCreateItem> LOOTSTABLE_CREATE_5 = PDItems.ITEMS.register("lootstable_create_5",
            () -> new LootstableCreateItem("chests/loots_relic_5", "§e风泊板条筐"));
    public static final DeferredItem<LootstableCreateItem> LOOTSTABLE_CREATE_6 = PDItems.ITEMS.register("lootstable_create_6",
            () -> new LootstableCreateItem("chests/loots_relic_6", "§e海岸通用"));
    public static final DeferredItem<LootstableCreateItem> LOOTSTABLE_CREATE_7 = PDItems.ITEMS.register("lootstable_create_7",
            () -> new LootstableCreateItem("chests/loots_relic_7", "§e圣诞小玩意"));
    public static final DeferredItem<LootstableCreateItem> LOOTSTABLE_CREATE_8 = PDItems.ITEMS.register("lootstable_create_8",
            () -> new LootstableCreateItem("chests/loots_relic_8", "§e风旅通用"));
    public static final DeferredItem<LootstableCreateItem> LOOTSTABLE_CREATE_9 = PDItems.ITEMS.register("lootstable_create_9",
            () -> new LootstableCreateItem("chests/loots_relic_9", "§e野餐篮食物"));

    /**
     * 溯源苍白骨针 (roots_pale_boneneedle)
     * 潜行右击设置标记点；使用后从帕斯特之梦维度返回主世界标记点/重生点（可重复使用）
     */
    public static final DeferredItem<RootsPaleBoneneedleItem> ROOTS_PALE_BONENEEDLE = PDItems.ITEMS.register("roots_pale_boneneedle",
            () -> new RootsPaleBoneneedleItem(new Item.Properties()));

    // ==================== [分区W] 武器工坊：蓝图物品（工坊模块专属分区） ====================

    /**
     * 蓝图·暗影高炉 (blueprint_0)
     * 暗影高炉多方块结构图纸（高炉 BE/菜单/配方/JEI 已落地；手持用于结构校验与铺设）
     */
    public static final DeferredItem<BlueprintItem> BLUEPRINT_0 = PDItems.ITEMS.register("blueprint_0",
            () -> new BlueprintItem("pasterdream:shadow_blast_furnace"));

    /**
     * 蓝图·精铸工坊 (blueprint_1)
     * 精铸工坊多方块结构图纸：手持点击精铸工作台激活结构校验与铺设
     */
    public static final DeferredItem<BlueprintItem> BLUEPRINT_1 = PDItems.ITEMS.register("blueprint_1",
            () -> new BlueprintItem("pasterdream:weapon_workshop", 1));

    // ==================== 法杖武器（W2-D，还原自原版法杖战斗模块） ====================
    // 物品属性（stacksTo/耐久/防火等）均在各物品类构造器内按原版补齐，注册处仅传空 Properties

    /**
     * 聚梦法杖 (dream_wand)
     * TieredItem 万用工具型法杖：五类工具行为 + 站在染梦书桌上右键清空法杖数据
     */
    public static final DeferredItem<DreamWandItem> DREAM_WAND = PDItems.ITEMS.register("dream_wand",
            () -> new DreamWandItem(new Item.Properties()));

    /**
     * 聚魔法杖 (mana_wand)
     * 普通物品型法杖：攻击 +1 / 攻速 -2、不可损耗、防火
     */
    public static final DeferredItem<ManaWandItem> MANA_WAND = PDItems.ITEMS.register("mana_wand",
            () -> new ManaWandItem(new Item.Properties()));

    /**
     * 炙焰金杖 (moltengold_wand)
     * 右键蓄力发射炙焰法球（消耗魔法石），命中方块点火
     */
    public static final DeferredItem<MoltengoldWandItem> MOLTENGOLD_WAND = PDItems.ITEMS.register("moltengold_wand",
            () -> new MoltengoldWandItem(new Item.Properties()));

    /**
     * 唤星者法杖 (true_moltengold_wand)
     * 右键蓄力发射唤星法球，命中释放唤星照明并概率召唤唤星裂隙
     */
    public static final DeferredItem<TrueMoltengoldWandItem> TRUE_MOLTENGOLD_WAND = PDItems.ITEMS.register("true_moltengold_wand",
            () -> new TrueMoltengoldWandItem(new Item.Properties()));

    /**
     * 『亚勒兹』唤星 (truest_moltengold_wand)
     * 右键蓄力发射唤星雨法球，命中 50% 概率召唤唤星裂隙并散射炙焰法球
     */
    public static final DeferredItem<TruestMoltengoldWandItem> TRUEST_MOLTENGOLD_WAND = PDItems.ITEMS.register("truest_moltengold_wand",
            () -> new TruestMoltengoldWandItem(new Item.Properties()));

    /**
     * 魂啸法杖 (squeal_wave_wand)
     * 右键蓄力发射魂啸音波（消耗魔法石 + 融梦能量/San），法术强度触发范围魔法伤害
     */
    public static final DeferredItem<SquealWaveWandItem> SQUEAL_WAVE_WAND = PDItems.ITEMS.register("squeal_wave_wand",
            () -> new SquealWaveWandItem(new Item.Properties()));

    /**
     * 占星者的祈愿 (star_wish_rod)
     * 特殊钓鱼竿：不可损耗、防火，收/抛竿贴图由 cast 自定义数据驱动
     */
    public static final DeferredItem<StarWishRodItem> STAR_WISH_ROD = PDItems.ITEMS.register("star_wish_rod",
            () -> new StarWishRodItem(new Item.Properties()));

    /**
     * 暗影旋涡 (shadow_vortex_book)
     * 蓄力松开发射暗影旋涡法球的法术书；未达成暗影天赋成就者施法遭反噬
     */
    public static final DeferredItem<ShadowVortexBookItem> SHADOW_VORTEX_BOOK = PDItems.ITEMS.register("shadow_vortex_book",
            () -> new ShadowVortexBookItem(new Item.Properties()));

    /**
     * 白厄剑雨 (white_sword_rain)
     * 可独立投掷的光剑物品（耐久 100），亦为白色灾厄剑技的剑雨投射物贴图来源
     */
    public static final DeferredItem<WhiteSwordRainItem> WHITE_SWORD_RAIN = PDItems.ITEMS.register("white_sword_rain",
            () -> new WhiteSwordRainItem(new Item.Properties()));

}
