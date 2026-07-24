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
import net.minecraft.world.item.Rarity;
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
            () -> new DyedreamPerfumeItem(new Item.Properties().stacksTo(64)
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
    public static final DeferredItem<Item> MEMENTO_ITEM_03 = PDItems.ITEMS.registerSimpleItem("memento_item_03");
    public static final DeferredItem<Item> MEMENTO_ITEM_04 = PDItems.ITEMS.registerSimpleItem("memento_item_04");
    public static final DeferredItem<Item> MEMENTO_ITEM_05 = PDItems.ITEMS.registerSimpleItem("memento_item_05");
    public static final DeferredItem<Item> MEMENTO_ITEM_06 = PDItems.ITEMS.registerSimpleItem("memento_item_06");
    public static final DeferredItem<Item> MEMENTO_ITEM_07 = PDItems.ITEMS.registerSimpleItem("memento_item_07");
    public static final DeferredItem<Item> MEMENTO_ITEM_08 = PDItems.ITEMS.registerSimpleItem("memento_item_08");
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
     * 调试法杖 - 巨型染梦树
     */
    public static final DeferredItem<DebugStructureWandItem> DEBUG_WAND_WORLDTREE =
            PDItems.ITEMS.register("debug_wand_worldtree",
                    () -> new DebugStructureWandItem(new Item.Properties().stacksTo(1), "dyedream_worldtree_true"));

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

}
