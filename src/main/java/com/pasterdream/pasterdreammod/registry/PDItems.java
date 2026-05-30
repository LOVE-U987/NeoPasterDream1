package com.pasterdream.pasterdreammod.registry;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.api.itemmigration.ItemMigrationAPI;
import com.pasterdream.pasterdreammod.api.itemmigration.model.MigrationCategory;
import com.pasterdream.pasterdreammod.api.itemmigration.model.ToolSpec.ToolType;
import com.pasterdream.pasterdreammod.item.*;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.food.FoodProperties;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 物品注册类
 * 使用 DeferredRegister 模式注册所有物品
 */
public class PDItems {

    /**
     * 物品注册器
     */
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(PasterDreamMod.MOD_ID);

    /**
     * 蓄梦池物品 (dream_accumulator)
     * 使用 DreamAccumulatorDisplayItem 实现手持 GeckoLib 动画
     */
    public static final DeferredItem<BlockItem> DREAM_ACCUMULATOR = ITEMS.register("dream_accumulator",
            () -> new DreamAccumulatorDisplayItem(PDBlocks.DREAM_ACCUMULATOR.get(), new Item.Properties()));

    /**
     * 染梦书桌物品 (dyedream_desk)
     * 对应 PDBlocks.DYEDREAM_DESK 方块
     */
    public static final DeferredItem<BlockItem> DYEDREAM_DESK = ITEMS.registerSimpleBlockItem("dyedream_desk",
            PDBlocks.DYEDREAM_DESK);

    /**
     * 生命水晶物品 (life_crystal)
     * 对应 PDBlocks.LIFE_CRYSTAL 方块
     * 使用 LifeCrystalDisplayItem 实现手持 3D 渲染
     * 站在附近可以缓慢恢复生命值
     */
    public static final DeferredItem<LifeCrystalDisplayItem> LIFE_CRYSTAL = ITEMS.register("life_crystal",
            () -> new LifeCrystalDisplayItem(new Item.Properties()));

    /**
     * 影之箱子物品 (shadow_chest)
     * 对应 PDBlocks.SHADOW_CHEST 方块
     * 使用 ShadowChestDisplayItem 实现手持 3D 渲染
     * 装饰性方块，无存储功能
     */
    public static final DeferredItem<ShadowChestDisplayItem> SHADOW_CHEST = ITEMS.register("shadow_chest",
            () -> new ShadowChestDisplayItem(new Item.Properties()));

    // ==================== 染梦世界方块物品 ====================

    public static final DeferredItem<BlockItem> DYEDREAM_BLOCK = ITEMS.registerSimpleBlockItem("dyedream_block", PDBlocks.DYEDREAM_BLOCK);
    public static final DeferredItem<BlockItem> DYEDREAM_DIRT = ITEMS.registerSimpleBlockItem("dyedream_dirt", PDBlocks.DYEDREAM_DIRT);
    public static final DeferredItem<BlockItem> DYEDREAM_SAND = ITEMS.registerSimpleBlockItem("dyedream_sand", PDBlocks.DYEDREAM_SAND);
    public static final DeferredItem<BlockItem> DYEDREAM_PLANKS = ITEMS.registerSimpleBlockItem("dyedream_planks", PDBlocks.DYEDREAM_PLANKS);
    public static final DeferredItem<BlockItem> DYEDREAM_GLASS = ITEMS.registerSimpleBlockItem("dyedream_glass", PDBlocks.DYEDREAM_GLASS);
    public static final DeferredItem<BlockItem> DYEDREAM_ICE = ITEMS.registerSimpleBlockItem("dyedream_ice", PDBlocks.DYEDREAM_ICE);
    public static final DeferredItem<BlockItem> DYEDREAM_PACKED_ICE = ITEMS.registerSimpleBlockItem("dyedream_packed_ice", PDBlocks.DYEDREAM_PACKED_ICE);
    public static final DeferredItem<BlockItem> DYEDREAMQUARTZ_BLOCK = ITEMS.registerSimpleBlockItem("dyedreamquartz_block", PDBlocks.DYEDREAMQUARTZ_BLOCK);
    public static final DeferredItem<BlockItem> SMOOTH_DYEDREAMQUARTZ_BLOCK = ITEMS.registerSimpleBlockItem("smooth_dyedreamquartz_block", PDBlocks.SMOOTH_DYEDREAMQUARTZ_BLOCK);
    public static final DeferredItem<BlockItem> BRICKS_DYEDREAMQUARTZ_BLOCK = ITEMS.registerSimpleBlockItem("bricks_dyedreamquartz_block", PDBlocks.BRICKS_DYEDREAMQUARTZ_BLOCK);
    public static final DeferredItem<BlockItem> CHISELED_DYEDREAMQUARTZ_BLOCK = ITEMS.registerSimpleBlockItem("chiseled_dyedreamquartz_block", PDBlocks.CHISELED_DYEDREAMQUARTZ_BLOCK);
    public static final DeferredItem<BlockItem> DYEDREAM_BUD_BLOCK = ITEMS.registerSimpleBlockItem("dyedream_bud_block", PDBlocks.DYEDREAM_BUD_BLOCK);
    public static final DeferredItem<BlockItem> PINKSLIME_BLOCK = ITEMS.registerSimpleBlockItem("pinkslime_block", PDBlocks.PINKSLIME_BLOCK);
    public static final DeferredItem<BlockItem> ICESTONE = ITEMS.registerSimpleBlockItem("icestone", PDBlocks.ICESTONE);
    public static final DeferredItem<BlockItem> DYEDREAM_LEAVES = ITEMS.registerSimpleBlockItem("dyedream_leaves", PDBlocks.DYEDREAM_LEAVES);
    public static final DeferredItem<BlockItem> DYEDREAM_WORLDTREE_LEAVES = ITEMS.registerSimpleBlockItem("dyedream_worldtree_leaves", PDBlocks.DYEDREAM_WORLDTREE_LEAVES);
    public static final DeferredItem<BlockItem> DYEDREAMQUARTZ_ORE = ITEMS.registerSimpleBlockItem("dyedreamquartz_ore", PDBlocks.DYEDREAMQUARTZ_ORE);
    public static final DeferredItem<BlockItem> DYEDREAMDUST_ORE = ITEMS.registerSimpleBlockItem("dyedreamdust_ore", PDBlocks.DYEDREAMDUST_ORE);
    public static final DeferredItem<BlockItem> AMBER_CANDY_ORE = ITEMS.registerSimpleBlockItem("amber_candy_ore", PDBlocks.AMBER_CANDY_ORE);
    public static final DeferredItem<BlockItem> CARVE_DYEDREAM_GLASS = ITEMS.registerSimpleBlockItem("carve_dyedream_glass", PDBlocks.CARVE_DYEDREAM_GLASS);
    public static final DeferredItem<BlockItem> GOLD_CARVE_DYEDREAM_GLASS = ITEMS.registerSimpleBlockItem("gold_carve_dyedream_glass", PDBlocks.GOLD_CARVE_DYEDREAM_GLASS);
    public static final DeferredItem<BlockItem> DYEDREAM_GRASS = ITEMS.registerSimpleBlockItem("dyedream_grass", PDBlocks.DYEDREAM_GRASS);
    public static final DeferredItem<BlockItem> DYEDREAM_LOG = ITEMS.registerSimpleBlockItem("dyedream_log", PDBlocks.DYEDREAM_LOG);
    public static final DeferredItem<BlockItem> DYEDREAM_WOOD = ITEMS.registerSimpleBlockItem("dyedream_wood", PDBlocks.DYEDREAM_WOOD);
    public static final DeferredItem<BlockItem> STRIPPED_DYEDREAM_LOG = ITEMS.registerSimpleBlockItem("stripped_dyedream_log", PDBlocks.STRIPPED_DYEDREAM_LOG);
    public static final DeferredItem<BlockItem> STRIPPED_DYEDREAM_WOOD = ITEMS.registerSimpleBlockItem("stripped_dyedream_wood", PDBlocks.STRIPPED_DYEDREAM_WOOD);
    public static final DeferredItem<BlockItem> PILLAR_DYEDREAMQUARTZ_BLOCK = ITEMS.registerSimpleBlockItem("pillar_dyedreamquartz_block", PDBlocks.PILLAR_DYEDREAMQUARTZ_BLOCK);
    public static final DeferredItem<BlockItem> DYEDREAM_PLANKS_STAIRS = ITEMS.registerSimpleBlockItem("dyedream_planks_stairs", PDBlocks.DYEDREAM_PLANKS_STAIRS);
    public static final DeferredItem<BlockItem> DYEDREAM_BUD_STAIRS = ITEMS.registerSimpleBlockItem("dyedream_bud_stairs", PDBlocks.DYEDREAM_BUD_STAIRS);
    public static final DeferredItem<BlockItem> DYEDREAMQUARTZ_BLOCK_STAIRS = ITEMS.registerSimpleBlockItem("dyedreamquartz_block_stairs", PDBlocks.DYEDREAMQUARTZ_BLOCK_STAIRS);
    public static final DeferredItem<BlockItem> DYEDREAM_PLANKS_SLAB = ITEMS.registerSimpleBlockItem("dyedream_planks_slab", PDBlocks.DYEDREAM_PLANKS_SLAB);
    public static final DeferredItem<BlockItem> DYEDREAM_BUD_SLAB = ITEMS.registerSimpleBlockItem("dyedream_bud_slab", PDBlocks.DYEDREAM_BUD_SLAB);
    public static final DeferredItem<BlockItem> DYEDREAMQUARTZ_BLOCK_SLAB = ITEMS.registerSimpleBlockItem("dyedreamquartz_block_slab", PDBlocks.DYEDREAMQUARTZ_BLOCK_SLAB);
    public static final DeferredItem<BlockItem> DYEDREAM_BUD_WALL = ITEMS.registerSimpleBlockItem("dyedream_bud_wall", PDBlocks.DYEDREAM_BUD_WALL);
    public static final DeferredItem<BlockItem> DYEDREAMQUARTZ_BLOCK_WALL = ITEMS.registerSimpleBlockItem("dyedreamquartz_block_wall", PDBlocks.DYEDREAMQUARTZ_BLOCK_WALL);
    public static final DeferredItem<BlockItem> DYEDREAM_PLANKS_FENCE = ITEMS.registerSimpleBlockItem("dyedream_planks_fence", PDBlocks.DYEDREAM_PLANKS_FENCE);
    public static final DeferredItem<BlockItem> DYEDREAM_PLANKS_FENCEGATE = ITEMS.registerSimpleBlockItem("dyedream_planks_fencegate", PDBlocks.DYEDREAM_PLANKS_FENCEGATE);
    public static final DeferredItem<BlockItem> DYEDREAM_PLANKS_DOOR = ITEMS.registerSimpleBlockItem("dyedream_planks_door", PDBlocks.DYEDREAM_PLANKS_DOOR);
    public static final DeferredItem<BlockItem> DYEDREAM_PLANKS_TRAPDOOR = ITEMS.registerSimpleBlockItem("dyedream_planks_trapdoor", PDBlocks.DYEDREAM_PLANKS_TRAPDOOR);
    public static final DeferredItem<BlockItem> DYEDREAM_PLANKS_PRESSURE_PLATE = ITEMS.registerSimpleBlockItem("dyedream_planks_pressure_plate", PDBlocks.DYEDREAM_PLANKS_PRESSURE_PLATE);
    public static final DeferredItem<BlockItem> DYEDREAM_PLANKS_BUTTON = ITEMS.registerSimpleBlockItem("dyedream_planks_button", PDBlocks.DYEDREAM_PLANKS_BUTTON);
    public static final DeferredItem<BlockItem> DYEDREAM_GLASSPANE = ITEMS.registerSimpleBlockItem("dyedream_glasspane", PDBlocks.DYEDREAM_GLASSPANE);
    public static final DeferredItem<BlockItem> CARVE_DYEDREAM_GLASSPANE = ITEMS.registerSimpleBlockItem("carve_dyedream_glasspane", PDBlocks.CARVE_DYEDREAM_GLASSPANE);
    public static final DeferredItem<BlockItem> GOLD_CARVE_DYEDREAM_GLASSPANE = ITEMS.registerSimpleBlockItem("gold_carve_dyedream_glasspane", PDBlocks.GOLD_CARVE_DYEDREAM_GLASSPANE);
    public static final DeferredItem<BlockItem> DYEDREAM_LARTERN = ITEMS.registerSimpleBlockItem("dyedream_lartern", PDBlocks.DYEDREAM_LARTERN);

    // ==================== 刷怪蛋 ====================

    /**
     * 暗影魔像刷怪蛋 (shadow_golem_spawn_egg)
     * 主色: 深灰色 (0x171717), 副色: 暗紫色 (0x7A7A7A)
     */
    public static final DeferredItem<SpawnEggItem> SHADOW_GOLEM_SPAWN_EGG = ITEMS.register("shadow_golem_spawn_egg",
            () -> new SpawnEggItem(PDEntities.SHADOW_GOLEM.get(), 0x171717, 0x7A7A7A, new Item.Properties()));

    /**
     * 粉色史莱姆刷怪蛋 (pink_slime_spawn_egg)
     * 主色: 粉色 (0xFFB6C1), 副色: 深粉色 (0xFF69B4)
     */
    public static final DeferredItem<SpawnEggItem> PINK_SLIME_SPAWN_EGG = ITEMS.register("pink_slime_spawn_egg",
            () -> new SpawnEggItem(PDEntities.PINK_SLIME.get(), 0xFFB6C1, 0xFF69B4, new Item.Properties()));

    // ==================== 测试材料物品 ====================

    /**
     * 钛锭 (titanium_ingot)
     * 基础材料，稀有度为 UNCOMMON
     */
    public static final DeferredItem<Item> TITANIUM_INGOT =
            ItemMigrationAPI.simpleItem("titanium_ingot")
                    .rarity(Rarity.UNCOMMON)
                    .build();

    /**
     * 染梦粉 (dyedream_dust)
     * 基础材料
     */
    public static final DeferredItem<Item> DYEDREAM_DUST = ITEMS.registerSimpleItem("dyedream_dust");

    /**
     * 魔法石 (magic_stone)
     * 基础材料，带有特殊描述文本
     */
    public static final DeferredItem<Item> MAGIC_STONE =
            ItemMigrationAPI.simpleItem("magic_stone")
                    .tooltip("§7§o哪个法师的兜里不会踹几块魔法石呢？")
                    .build();

    /**
     * 粉色粘液球 (pink_slimeball)
     */
    public static final DeferredItem<Item> PINK_SLIMEBALL = ITEMS.registerSimpleItem("pink_slimeball");

    /**
     * 染梦石英 (dyedreamquartz)
     */
    public static final DeferredItem<Item> DYEDREAMQUARTZ = ITEMS.registerSimpleItem("dyedreamquartz");

    // ==================== 批量移植的材料物品 ====================

    /**
     * 基础材料物品 - COMMON 稀有度
     */
    public static final DeferredItem<Item> BASALT_SNAIL_SHELL = ITEMS.registerSimpleItem("basalt_snail_shell");
    public static final DeferredItem<Item> BLACK_BEETLE_CARAPACE = ITEMS.registerSimpleItem("black_beetle_carapace");
    public static final DeferredItem<Item> BLACK_BEETLE_VOCALCORD = ITEMS.registerSimpleItem("black_beetle_vocalcord");
    public static final DeferredItem<Item> BLACKMETAL_GRAIN = ITEMS.registerSimpleItem("blackmetal_grain");
    public static final DeferredItem<Item> BLACKMETAL_INGOT = ITEMS.registerSimpleItem("blackmetal_ingot");
    public static final DeferredItem<Item> BLACKSTICK = ITEMS.registerSimpleItem("blackstick");
    public static final DeferredItem<Item> BLUE_HEART_OF_THE_SEA = ITEMS.registerSimpleItem("blue_heart_of_the_sea");
    public static final DeferredItem<Item> BROKENNOTES_0 = ITEMS.registerSimpleItem("brokennotes_0");
    public static final DeferredItem<Item> CHARGED_AMETHYST = ITEMS.registerSimpleItem("charged_amethyst");
    public static final DeferredItem<Item> COARSE_SALT = ITEMS.registerSimpleItem("coarse_salt");
    public static final DeferredItem<Item> CONGEAL_WIND = ITEMS.registerSimpleItem("congeal_wind");
    public static final DeferredItem<Item> COTTON = ITEMS.registerSimpleItem("cotton");
    public static final DeferredItem<Item> DREAM_AURORIAN_STEEL = ITEMS.registerSimpleItem("dream_aurorian_steel");
    /**
     * 忆梦魔导透镜 (dream_meter)
     * 使用 GeckoLib 实现完整 3D 手持模型渲染
     * 替换了原简单材料版本
     */
    public static final DeferredItem<DreamMeterItem> DREAM_METER = ITEMS.register("dream_meter",
            () -> new DreamMeterItem());
    public static final DeferredItem<Item> DREAMWISH = ITEMS.registerSimpleItem("dreamwish");
    public static final DeferredItem<Item> DYEDREAM_BASE = ITEMS.registerSimpleItem("dyedream_base");
    public static final DeferredItem<Item> DYEDREAM_BUD_NUGGET = ITEMS.registerSimpleItem("dyedream_bud_nugget");
    public static final DeferredItem<Item> DYEDREAM_COROLLA = ITEMS.registerSimpleItem("dyedream_corolla");
    public static final DeferredItem<Item> DYEDREAM_DUST_PIECE = ITEMS.registerSimpleItem("dyedream_dust_piece");
    public static final DeferredItem<Item> DYEDREAM_DYE = ITEMS.registerSimpleItem("dyedream_dye");
    public static final DeferredItem<Item> DYEDREAM_NUGGET = ITEMS.registerSimpleItem("dyedream_nugget");
    public static final DeferredItem<Item> DYEDREAM_UPGRADE = ITEMS.registerSimpleItem("dyedream_upgrade", new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> EGGDOUGH = ITEMS.registerSimpleItem("eggdough");
    public static final DeferredItem<Item> ELDER_GUARDIAN_SCALE = ITEMS.registerSimpleItem("elder_guardian_scale");
    public static final DeferredItem<Item> ENHANCE_STONE_0 = ITEMS.registerSimpleItem("enhance_stone_0");
    public static final DeferredItem<Item> ENHANCE_STONE_1 = ITEMS.registerSimpleItem("enhance_stone_1");
    public static final DeferredItem<Item> FABRIC = ITEMS.registerSimpleItem("fabric");
    public static final DeferredItem<Item> FLOUR = ITEMS.registerSimpleItem("flour");
    public static final DeferredItem<Item> ICESHADOW_HAMMER_EMBRYO = ITEMS.registerSimpleItem("iceshadow_hammer_embryo");
    public static final DeferredItem<Item> MANADUST = ITEMS.registerSimpleItem("manadust");
    public static final DeferredItem<Item> MOLTENGOLD_DUST = ITEMS.registerSimpleItem("moltengold_dust");
    public static final DeferredItem<Item> MOLTENGOLD_INGOT = ITEMS.registerSimpleItem("moltengold_ingot");
    public static final DeferredItem<Item> MOLTENGOLD_NUGGET = ITEMS.registerSimpleItem("moltengold_nugget");
    public static final DeferredItem<Item> MORTAR = ITEMS.registerSimpleItem("mortar");
    public static final DeferredItem<Item> NIGHTMARE_FUEL = ITEMS.registerSimpleItem("nightmare_fuel");
    public static final DeferredItem<Item> PEN_AND_INK = ITEMS.registerSimpleItem("pen_and_ink");
    public static final DeferredItem<Item> PERGAMYN = ITEMS.registerSimpleItem("pergamyn");
    public static final DeferredItem<Item> PROTECT_DECK = ITEMS.registerSimpleItem("protect_deck");
    public static final DeferredItem<Item> PULSE_WINDRUNNER_CRYSTAL = ITEMS.registerSimpleItem("pulse_windrunner_crystal");
    public static final DeferredItem<Item> PURE_HORROR = ITEMS.registerSimpleItem("pure_horror");
    public static final DeferredItem<Item> RAW_MOLTENGOLD = ITEMS.registerSimpleItem("raw_moltengold");
    public static final DeferredItem<Item> RAW_TITANIUM = ITEMS.registerSimpleItem("raw_titanium");
    public static final DeferredItem<Item> REEDROD = ITEMS.registerSimpleItem("reedrod");
    public static final DeferredItem<Item> RUST_BLACK_METAL_GRAIN = ITEMS.registerSimpleItem("rust_black_metal_grain");
    public static final DeferredItem<Item> RYESEED = ITEMS.registerSimpleItem("ryeseed");
    public static final DeferredItem<Item> SALT = ITEMS.registerSimpleItem("salt");
    public static final DeferredItem<Item> SCULK_HEART = ITEMS.registerSimpleItem("sculk_heart");
    public static final DeferredItem<Item> SCULK_UPGRADE = ITEMS.registerSimpleItem("sculk_upgrade");
    public static final DeferredItem<Item> SHADOW_DUNGEON_KEY = ITEMS.registerSimpleItem("shadow_dungeon_key");
    public static final DeferredItem<Item> SHADOW_EROSION_AXE_EMBRYO = ITEMS.registerSimpleItem("shadow_erosion_axe_embryo");
    public static final DeferredItem<Item> SHADOW_EROSION_HOE_EMBRYO = ITEMS.registerSimpleItem("shadow_erosion_hoe_embryo");
    public static final DeferredItem<Item> SHADOW_EROSION_PICKAXE_EMBRYO = ITEMS.registerSimpleItem("shadow_erosion_pickaxe_embryo");
    public static final DeferredItem<Item> SHADOW_EROSION_SHOVEL_EMBRYO = ITEMS.registerSimpleItem("shadow_erosion_shovel_embryo");
    public static final DeferredItem<Item> SHADOW_EROSION_SWORD_EMBRYO = ITEMS.registerSimpleItem("shadow_erosion_sword_embryo");
    public static final DeferredItem<Item> SHADOW_HILT = ITEMS.registerSimpleItem("shadow_hilt");
    public static final DeferredItem<Item> SHADOW_SWORD_EMBRYO = ITEMS.registerSimpleItem("shadow_sword_embryo");
    public static final DeferredItem<Item> SILVER_BELL = ITEMS.registerSimpleItem("silver_bell");
    public static final DeferredItem<Item> SORBENT = ITEMS.registerSimpleItem("sorbent");
    public static final DeferredItem<Item> SOUL_DUST = ITEMS.registerSimpleItem("soul_dust");
    public static final DeferredItem<Item> SOUL_ESSENCE = ITEMS.registerSimpleItem("soul_essence");
    public static final DeferredItem<Item> SPOOL = ITEMS.registerSimpleItem("spool");
    public static final DeferredItem<Item> STAR_WISH_ROD_EMBRYO = ITEMS.registerSimpleItem("star_wish_rod_embryo");
    public static final DeferredItem<Item> SWORD_EMBRYO_0 = ITEMS.registerSimpleItem("sword_embryo_0");
    public static final DeferredItem<Item> TERRASWORD_EMBRYO = ITEMS.registerSimpleItem("terrasword_embryo");
    public static final DeferredItem<Item> TITANIUM_NUGGET = ITEMS.registerSimpleItem("titanium_nugget");
    public static final DeferredItem<Item> TITANIUM_UPGRADE = ITEMS.registerSimpleItem("titanium_upgrade");
    public static final DeferredItem<Item> UNKNOWNNOTES_0 = ITEMS.registerSimpleItem("unknownnotes_0");
    public static final DeferredItem<Item> WHITE_COROLLA = ITEMS.registerSimpleItem("white_corolla");
    public static final DeferredItem<Item> WHITE_CRYSTAL = ITEMS.registerSimpleItem("white_crystal");
    public static final DeferredItem<Item> WHITE_SWORD_EMBRYO = ITEMS.registerSimpleItem("white_sword_embryo");
    public static final DeferredItem<Item> WIND_IRON_INGOT = ITEMS.registerSimpleItem("wind_iron_ingot");
    public static final DeferredItem<Item> WIND_PLANT_EXTRACT = ITEMS.registerSimpleItem("wind_plant_extract");
    public static final DeferredItem<Item> WINDRUNNER_CRYSTAL = ITEMS.registerSimpleItem("windrunner_crystal");
    public static final DeferredItem<Item> YEAST = ITEMS.registerSimpleItem("yeast");

    /**
     * 基础材料物品 - UNCOMMON 稀有度
     */
    public static final DeferredItem<Item> DYEDREAM_INGOT = ITEMS.registerSimpleItem("dyedream_ingot", new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON));
    // ==================== API物品移植测试 ====================
    //
    // 以下物品使用 ItemMigrationAPI 进行注册，验证API的编译正确性和可用性。
    // 覆盖 SimpleItemBuilder / FoodItemBuilder / ToolItemBuilder / CurioItemBuilder 四种类型。
    //

    /**
     * 玻璃杯 (glass_cup)
     * 使用 API SimpleItemBuilder 注册
     * 原模组：new Item()，COMMON 稀有度，64堆叠
     */
    public static final DeferredItem<Item> GLASS_CUP =
            ItemMigrationAPI.simpleItem("glass_cup").build();

    /**
     * 生面团 (dough)
     * 使用 API FoodItemBuilder 注册
     * 原模组：营养 1，饱食度 0.1f
     */
    public static final DeferredItem<Item> DOUGH =
            ItemMigrationAPI.foodItem("dough")
                    .nutrition(1).saturationModifier(0.1f)
                    .build();

    /**
     * 铜斧 (copper_axe)
     * 使用 API ToolItemBuilder 注册
     * 原模组：耐久 225，速度 5.0，伤害+7，攻速 -3.15
     */
    public static final DeferredItem<Item> COPPER_AXE =
            ItemMigrationAPI.toolItem("copper_axe")
                    .type(ToolType.AXE)
                    .durability(225).miningSpeed(5.0f)
                    .attackDamage(7.0f).attackSpeed(-3.15f)
                    .enchantment(12)
                    .repairWith(new ItemStack(Items.COPPER_INGOT))
                    .build();

    /**
     * 铜锹 (copper_shovel)
     * 使用 API ToolItemBuilder 注册
     * 原模组：耐久 225，速度 5.0，伤害+2，攻速 -3.0
     */
    public static final DeferredItem<Item> COPPER_SHOVEL =
            ItemMigrationAPI.toolItem("copper_shovel")
                    .type(ToolType.SHOVEL)
                    .durability(225).miningSpeed(5.0f)
                    .attackDamage(2.0f).attackSpeed(-3.0f)
                    .enchantment(12)
                    .repairWith(new ItemStack(Items.COPPER_INGOT))
                    .build();

    /**
     * 铜锄 (copper_hoe)
     * 使用 API ToolItemBuilder 注册
     * 原模组：耐久 225，速度 5.0，伤害+0，攻速 -1.5
     */
    public static final DeferredItem<Item> COPPER_HOE =
            ItemMigrationAPI.toolItem("copper_hoe")
                    .type(ToolType.HOE)
                    .durability(225).miningSpeed(5.0f)
                    .attackDamage(0.0f).attackSpeed(-1.5f)
                    .enchantment(12)
                    .repairWith(new ItemStack(Items.COPPER_INGOT))
                    .build();

    /**
     * 幸运四叶草 (fourleaf_clover_curio)
     * 使用 API CurioItemBuilder 注册
     * 原模组：stacksTo(1)，COMMON，+1 最大生命值，+6 幸运
     */
    public static final DeferredItem<Item> FOURLEAF_CLOVER_CURIO =
            ItemMigrationAPI.curioItem("fourleaf_clover_curio")
                    .slot("charm")
                    .attribute("minecraft:generic.max_health",
                            "055dac74-49cf-474c-9078-f658a61f7047", 1.0, 0)
                    .attribute("pasterdream:luck",
                            "f723cde2-ecbf-45d1-b985-8670b2f00fd2", 6.0, 0)
                    .tooltip("§a品质：优秀 ★★", "§7§o哪片叶子代表着幸运？")
                    .build();

    // ==================== 剑类武器 ====================

    public static final DeferredItem<Item> BROKEN_HERO_SWORD =
            ItemMigrationAPI.toolItem("broken_hero_sword")
                    .type(ToolType.SWORD).durability(131)
                    .attackDamage(4.0f).attackSpeed(-2.4f)
                    .repairWith(new ItemStack(Items.COBBLESTONE))
                    .build();
    public static final DeferredItem<Item> COPPER_SWORD =
            ItemMigrationAPI.toolItem("copper_sword")
                    .type(ToolType.SWORD).durability(59)
                    .attackDamage(3.0f).attackSpeed(-2.4f)
                    .repairWith(new ItemStack(Items.COBBLESTONE))
                    .build();
    public static final DeferredItem<Item> CREATIVE_SWORD =
            ItemMigrationAPI.toolItem("creative_sword")
                    .type(ToolType.SWORD).durability(131)
                    .attackDamage(4.0f).attackSpeed(-2.4f)
                    .build();
    public static final DeferredItem<Item> DESERT_SWORD =
            ItemMigrationAPI.toolItem("desert_sword")
                    .type(ToolType.SWORD).durability(131)
                    .attackDamage(4.0f).attackSpeed(-3.1f)
                    .build();
    public static final DeferredItem<Item> DYEDREAM_SWORD_0 =
            ItemMigrationAPI.toolItem("dyedream_sword_0")
                    .type(ToolType.SWORD).durability(131)
                    .attackDamage(4.0f).attackSpeed(-2.4f)
                    .build();
    public static final DeferredItem<Item> DYEDREAM_SWORD =
            ItemMigrationAPI.toolItem("dyedream_sword")
                    .type(ToolType.SWORD).durability(131)
                    .attackDamage(4.0f).attackSpeed(-2.4f)
                    .build();
    public static final DeferredItem<Item> GRASS_SWORD =
            ItemMigrationAPI.toolItem("grass_sword")
                    .type(ToolType.SWORD).durability(131)
                    .attackDamage(4.0f).attackSpeed(-2.5f)
                    .build();
    public static final DeferredItem<Item> ICESHADOW_HAMMER =
            ItemMigrationAPI.toolItem("iceshadow_hammer")
                    .type(ToolType.SWORD).durability(131)
                    .attackDamage(4.0f).attackSpeed(-3.3f)
                    .build();
    public static final DeferredItem<Item> MOLTENGOLD_SWORD =
            ItemMigrationAPI.toolItem("moltengold_sword")
                    .type(ToolType.SWORD).durability(131)
                    .attackDamage(4.0f).attackSpeed(-2.3f)
                    .build();
    public static final DeferredItem<Item> SHADOW_EROSION_SWORD =
            ItemMigrationAPI.toolItem("shadow_erosion_sword")
                    .type(ToolType.SWORD).durability(131)
                    .attackDamage(4.0f).attackSpeed(-1.0f)
                    .build();
    public static final DeferredItem<Item> SHADOW_SWORD =
            ItemMigrationAPI.toolItem("shadow_sword")
                    .type(ToolType.SWORD).durability(131)
                    .attackDamage(4.0f).attackSpeed(-2.4f)
                    .build();
    public static final DeferredItem<Item> TERRA_SWORD =
            ItemMigrationAPI.toolItem("terra_sword")
                    .type(ToolType.SWORD).durability(131)
                    .attackDamage(4.0f).attackSpeed(-2.4f)
                    .build();
    public static final DeferredItem<Item> THERMAL_DAGGER =
            ItemMigrationAPI.toolItem("thermal_dagger")
                    .type(ToolType.SWORD).durability(59)
                    .attackDamage(3.0f).attackSpeed(-2.3f)
                    .build();
    public static final DeferredItem<Item> TIDE_SWORD =
            ItemMigrationAPI.toolItem("tide_sword")
                    .type(ToolType.SWORD).durability(131)
                    .attackDamage(4.0f).attackSpeed(-2.8f)
                    .build();
    public static final DeferredItem<Item> TITANIUM_SWORD =
            ItemMigrationAPI.toolItem("titanium_sword")
                    .type(ToolType.SWORD).durability(131)
                    .attackDamage(4.0f).attackSpeed(-2.4f)
                    .build();
    public static final DeferredItem<Item> TRUE_DESERT_SWORD =
            ItemMigrationAPI.toolItem("true_desert_sword")
                    .type(ToolType.SWORD).durability(131)
                    .attackDamage(4.0f).attackSpeed(-3.1f)
                    .build();
    public static final DeferredItem<Item> TRUE_GRASS_SWORD =
            ItemMigrationAPI.toolItem("true_grass_sword")
                    .type(ToolType.SWORD).durability(131)
                    .attackDamage(4.0f).attackSpeed(-2.5f)
                    .build();
    public static final DeferredItem<Item> TRUE_MOLTENGOLD_SWORD =
            ItemMigrationAPI.toolItem("true_moltengold_sword")
                    .type(ToolType.SWORD).durability(131)
                    .attackDamage(4.0f).attackSpeed(-2.2f)
                    .build();
    public static final DeferredItem<Item> TRUE_TIDE_SWORD =
            ItemMigrationAPI.toolItem("true_tide_sword")
                    .type(ToolType.SWORD).durability(131)
                    .attackDamage(4.0f).attackSpeed(-2.8f)
                    .build();
    public static final DeferredItem<Item> TRUEST_MOLTENGOLD_SWORD =
            ItemMigrationAPI.toolItem("truest_moltengold_sword")
                    .type(ToolType.SWORD).durability(131)
                    .attackDamage(4.0f).attackSpeed(-2.15f)
                    .build();
    public static final DeferredItem<Item> WHITE_SWORD =
            ItemMigrationAPI.toolItem("white_sword")
                    .type(ToolType.SWORD).durability(131)
                    .attackDamage(4.0f).attackSpeed(-2.4f)
                    .build();

    // ==================== 镐类/锤类工具 ====================

    public static final DeferredItem<Item> COPPER_PICKAXE =
            ItemMigrationAPI.toolItem("copper_pickaxe")
                    .type(ToolType.PICKAXE).durability(131).miningSpeed(4.0f)
                    .attackDamage(2.0f).attackSpeed(-2.8f)
                    .incorrectTag("minecraft:incorrect_for_stone_tool")
                    .repairWith(new ItemStack(Items.COBBLESTONE))
                    .build();
    public static final DeferredItem<Item> DYEDREAM_HAMMER =
            ItemMigrationAPI.toolItem("dyedream_hammer")
                    .type(ToolType.HAMMER).durability(2031).miningSpeed(9.0f)
                    .attackDamage(5.0f).attackSpeed(-2.8f)
                    .incorrectTag("minecraft:incorrect_for_netherite_tool")
                    .repairWith(new ItemStack(Items.NETHERITE_INGOT))
                    .build();
    public static final DeferredItem<Item> DYEDREAM_PICKAXE =
            ItemMigrationAPI.toolItem("dyedream_pickaxe")
                    .type(ToolType.PICKAXE).durability(2031).miningSpeed(9.0f)
                    .attackDamage(5.0f).attackSpeed(-2.8f)
                    .incorrectTag("minecraft:incorrect_for_netherite_tool")
                    .repairWith(new ItemStack(Items.NETHERITE_INGOT))
                    .build();
    public static final DeferredItem<Item> MELTDREAM_PICKAXE =
            ItemMigrationAPI.toolItem("meltdream_pickaxe")
                    .type(ToolType.PICKAXE).durability(250).miningSpeed(6.0f)
                    .attackDamage(3.0f).attackSpeed(-2.8f)
                    .incorrectTag("minecraft:incorrect_for_iron_tool")
                    .repairWith(new ItemStack(Items.IRON_INGOT))
                    .build();
    public static final DeferredItem<Item> MOLTENGOLD_PICKAXE =
            ItemMigrationAPI.toolItem("moltengold_pickaxe")
                    .type(ToolType.PICKAXE).durability(131).miningSpeed(4.0f)
                    .attackDamage(2.0f).attackSpeed(-2.7f)
                    .incorrectTag("minecraft:incorrect_for_stone_tool")
                    .repairWith(new ItemStack(Items.COBBLESTONE))
                    .build();
    public static final DeferredItem<Item> SHADOW_EROSION_PICKAXE =
            ItemMigrationAPI.toolItem("shadow_erosion_pickaxe")
                    .type(ToolType.PICKAXE).durability(2031).miningSpeed(9.0f)
                    .attackDamage(5.0f).attackSpeed(-2.8f)
                    .incorrectTag("minecraft:incorrect_for_netherite_tool")
                    .repairWith(new ItemStack(Items.NETHERITE_INGOT))
                    .build();
    public static final DeferredItem<Item> TITANIUM_PICKAXE =
            ItemMigrationAPI.toolItem("titanium_pickaxe")
                    .type(ToolType.PICKAXE).durability(2031).miningSpeed(9.0f)
                    .attackDamage(5.0f).attackSpeed(-2.8f)
                    .incorrectTag("minecraft:incorrect_for_netherite_tool")
                    .repairWith(new ItemStack(Items.NETHERITE_INGOT))
                    .build();
    public static final DeferredItem<Item> TRUE_MOLTENGOLD_PICKAXE =
            ItemMigrationAPI.toolItem("true_moltengold_pickaxe")
                    .type(ToolType.PICKAXE).durability(131).miningSpeed(4.0f)
                    .attackDamage(2.0f).attackSpeed(-2.6f)
                    .incorrectTag("minecraft:incorrect_for_stone_tool")
                    .repairWith(new ItemStack(Items.COBBLESTONE))
                    .build();

    // ==================== 食物类物品 ====================

    public static final DeferredItem<Item> APPLE_JUICE = ITEMS.registerSimpleItem("apple_juice",
            new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationModifier(0.2f).alwaysEdible().build()));
    public static final DeferredItem<Item> BACONE_EGG = ITEMS.registerSimpleItem("bacone_egg",
            new Item.Properties().food(new FoodProperties.Builder().nutrition(10).saturationModifier(1.2f).build()));
    public static final DeferredItem<Item> BERRY_BUNCAKE = ITEMS.registerSimpleItem("berry_buncake",
            new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationModifier(0.5f).alwaysEdible().fast().build()));
    public static final DeferredItem<Item> BUBBLE_GUM = ITEMS.registerSimpleItem("bubble_gum",
            new Item.Properties().food(new FoodProperties.Builder().nutrition(1).saturationModifier(0f).alwaysEdible().fast().build()));
    public static final DeferredItem<Item> CANDY_CANE = ITEMS.registerSimpleItem("candy_cane",
            new Item.Properties().food(new FoodProperties.Builder().nutrition(3).saturationModifier(0.5f).build()));
    public static final DeferredItem<Item> CHOCOLATE = ITEMS.registerSimpleItem("chocolate",
            new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationModifier(0.375f).build()));
    public static final DeferredItem<Item> CHOCOLATE_MATCHA_CAKE = ITEMS.registerSimpleItem("chocolate_matcha_cake",
            new Item.Properties().food(new FoodProperties.Builder().nutrition(8).saturationModifier(1.0f).build()));
    public static final DeferredItem<Item> CREAM_BUNCAKE = ITEMS.registerSimpleItem("cream_buncake",
            new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationModifier(0.5f).alwaysEdible().fast().build()));
    public static final DeferredItem<Item> DREAM_COTTON_CANDY = ITEMS.registerSimpleItem("dream_cotton_candy",
            new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationModifier(0.625f).alwaysEdible().build()));
    public static final DeferredItem<Item> DYEDREAM_FLOWER_TEA = ITEMS.registerSimpleItem("dyedream_flower_tea",
            new Item.Properties().food(new FoodProperties.Builder().nutrition(0).saturationModifier(0f).alwaysEdible().build()));
    public static final DeferredItem<Item> DYEDREAM_FRUIT_BUNCAKE = ITEMS.registerSimpleItem("dyedream_fruit_buncake",
            new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationModifier(0.5f).alwaysEdible().fast().build()));
    public static final DeferredItem<Item> DYEDREAM_JUICE = ITEMS.registerSimpleItem("dyedream_juice",
            new Item.Properties().food(new FoodProperties.Builder().nutrition(1).saturationModifier(0.2f).alwaysEdible().build()));
    public static final DeferredItem<Item> DYEDREAM_POPSICLE = ITEMS.registerSimpleItem("dyedream_popsicle",
            new Item.Properties().food(new FoodProperties.Builder().nutrition(1).saturationModifier(0.4f).build()));
    public static final DeferredItem<Item> FRIED_EGG = ITEMS.registerSimpleItem("fried_egg",
            new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationModifier(0.2f).build()));
    public static final DeferredItem<Item> GINGERBREAD_MAN = ITEMS.registerSimpleItem("gingerbread_man",
            new Item.Properties().food(new FoodProperties.Builder().nutrition(5).saturationModifier(0.6f).build()));
    public static final DeferredItem<Item> GLOW_BERRY_BUNCAKE = ITEMS.registerSimpleItem("glow_berry_buncake",
            new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationModifier(0.5f).alwaysEdible().fast().build()));
    public static final DeferredItem<Item> GOLDENROD_TEA = ITEMS.registerSimpleItem("goldenrod_tea",
            new Item.Properties().food(new FoodProperties.Builder().nutrition(1).saturationModifier(0f).alwaysEdible().build()));
    public static final DeferredItem<Item> HONEY_JUICE = ITEMS.registerSimpleItem("honey_juice",
            new Item.Properties().food(new FoodProperties.Builder().nutrition(6).saturationModifier(0.1f).alwaysEdible().build()));
    public static final DeferredItem<Item> JELLYFISH_JELLO = ITEMS.registerSimpleItem("jellyfish_jello",
            new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationModifier(0.5f).alwaysEdible().build()));
    public static final DeferredItem<Item> JELLYFISH_MUD = ITEMS.registerSimpleItem("jellyfish_mud",
            new Item.Properties().food(new FoodProperties.Builder().nutrition(1).saturationModifier(0f).build()));
    public static final DeferredItem<Item> LEGEND_DRAGON_HORN_ICE_CREAM = ITEMS.registerSimpleItem("legend_dragon_horn_ice_cream",
            new Item.Properties().food(new FoodProperties.Builder().nutrition(10).saturationModifier(1.2f).alwaysEdible().build()));
    public static final DeferredItem<Item> LIGHT_ORGAN = ITEMS.registerSimpleItem("light_organ",
            new Item.Properties().food(new FoodProperties.Builder().nutrition(1).saturationModifier(0f).build()));
    public static final DeferredItem<Item> MELON_BUNCAKE = ITEMS.registerSimpleItem("melon_buncake",
            new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationModifier(0.5f).alwaysEdible().fast().build()));
    public static final DeferredItem<Item> MELTDREAM_ELIXIR_BOTTLE = ITEMS.registerSimpleItem("meltdream_elixir_bottle",
            new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationModifier(0.25f).alwaysEdible().build()));
    public static final DeferredItem<Item> MILK_GLASSJAR = ITEMS.registerSimpleItem("milk_glassjar",
            new Item.Properties().food(new FoodProperties.Builder().nutrition(0).saturationModifier(0f).alwaysEdible().build()));
    public static final DeferredItem<Item> ODD_BACONE_EGG = ITEMS.registerSimpleItem("odd_bacone_egg",
            new Item.Properties().food(new FoodProperties.Builder().nutrition(11).saturationModifier(1.5f).build()));
    public static final DeferredItem<Item> PINEAPPLE_LOVE_SEA = ITEMS.registerSimpleItem("pineapple_love_sea",
            new Item.Properties().food(new FoodProperties.Builder().nutrition(3).saturationModifier(0.5f).build()));
    public static final DeferredItem<Item> POTATO_BUNCAKE = ITEMS.registerSimpleItem("potato_buncake",
            new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationModifier(0.5f).alwaysEdible().fast().build()));
    public static final DeferredItem<Item> PUMPKIN_BUNCAKE = ITEMS.registerSimpleItem("pumpkin_buncake",
            new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationModifier(0.5f).alwaysEdible().fast().build()));
    public static final DeferredItem<Item> QUEER_SOUP = ITEMS.registerSimpleItem("queer_soup",
            new Item.Properties().food(new FoodProperties.Builder().nutrition(0).saturationModifier(0f).alwaysEdible().build()));
    public static final DeferredItem<Item> RAGE_ELIXIR_0 = ITEMS.registerSimpleItem("rage_elixir_0",
            new Item.Properties().food(new FoodProperties.Builder().nutrition(0).saturationModifier(0f).alwaysEdible().build()));
    public static final DeferredItem<Item> RICECAKE = ITEMS.registerSimpleItem("ricecake",
            new Item.Properties().food(new FoodProperties.Builder().nutrition(6).saturationModifier(0.4f).build()));
    public static final DeferredItem<Item> SANDWICH = ITEMS.registerSimpleItem("sandwich",
            new Item.Properties().food(new FoodProperties.Builder().nutrition(10).saturationModifier(0.9f).build()));
    public static final DeferredItem<Item> STUFFED_WAFER_COOKIES = ITEMS.registerSimpleItem("stuffed_wafer_cookies",
            new Item.Properties().food(new FoodProperties.Builder().nutrition(7).saturationModifier(1.0f).build()));
    public static final DeferredItem<Item> SWISS_ROLL = ITEMS.registerSimpleItem("swiss_roll",
            new Item.Properties().food(new FoodProperties.Builder().nutrition(6).saturationModifier(0.8f).build()));
    public static final DeferredItem<Item> UNCOOKED_DYEDREAM_FLOWER_TEA = ITEMS.registerSimpleItem("uncooked_dyedream_flower_tea",
            new Item.Properties().food(new FoodProperties.Builder().nutrition(0).saturationModifier(0f).alwaysEdible().build()));
    public static final DeferredItem<Item> WATER_GLASSJAR = ITEMS.registerSimpleItem("water_glassjar",
            new Item.Properties().food(new FoodProperties.Builder().nutrition(0).saturationModifier(0f).alwaysEdible().build()));
    public static final DeferredItem<Item> WATERMELON_JUICE = ITEMS.registerSimpleItem("watermelon_juice",
            new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationModifier(0.1f).alwaysEdible().build()));
    public static final DeferredItem<Item> SILVER_FOX_COTTON_CANDY = ITEMS.registerSimpleItem("silver_fox_cotton_candy",
            new Item.Properties().food(new FoodProperties.Builder().nutrition(6).saturationModifier(0.75f).alwaysEdible().build()));

    // ==================== 需要自定义类的物品（tooltip/交互） ====================

    public static final DeferredItem<Item> AMBER_CANDY = ITEMS.register("amber_candy",
            () -> new AmberCandyItem(new Item.Properties()));
    public static final DeferredItem<Item> BLUE_DEW = ITEMS.register("blue_dew",
            () -> new BlueDewItem(new Item.Properties()));
    public static final DeferredItem<Item> BREAD_SLICE = ITEMS.register("bread_slice",
            () -> new BreadSliceItem(new Item.Properties()));
    public static final DeferredItem<Item> BUBBLE_TEA = ITEMS.register("bubble_tea",
            () -> new BubbleTeaItem(new Item.Properties()));
    public static final DeferredItem<Item> CAKE_BASE = ITEMS.register("cake_base",
            () -> new CakeBaseItem(new Item.Properties()));
    public static final DeferredItem<Item> CRADLE_IN_ONES_ARMS = ITEMS.register("cradle_in_ones_arms",
            () -> new CradleInOnesArmsItem(new Item.Properties()));
    public static final DeferredItem<Item> DREAM_COIN_0 = ITEMS.register("dream_coin_0",
            () -> new DreamCoin0Item(new Item.Properties()));
    public static final DeferredItem<Item> DREAM_COIN_1 = ITEMS.register("dream_coin_1",
            () -> new DreamCoin1Item(new Item.Properties()));
    public static final DeferredItem<DreamFertilizerItem> DREAM_FERTILIZER = ITEMS.register("dream_fertilizer",
            () -> new DreamFertilizerItem(new Item.Properties(), PDParticles.DREAMFERTILITER_PARTICLE));
    public static final DeferredItem<Item> DYEDREAM_FRUIT = ITEMS.register("dyedream_fruit",
            () -> new DyedreamFruitItem(new Item.Properties()));
    public static final DeferredItem<Item> DYEDREAM_TELEPORT_CRYSTAL = ITEMS.register("dyedream_teleport_crystal",
            () -> new DyedreamTeleportCrystal(new Item.Properties().stacksTo(16)));
    public static final DeferredItem<Item> DYEDREAM_PERFUME = ITEMS.register("dyedream_perfume",
            () -> new DyedreamPerfumeItem(new Item.Properties()));
    public static final DeferredItem<Item> ELIXIR_BOTTLE = ITEMS.register("elixir_bottle",
            () -> new ElixirBottleItem(new Item.Properties()));
    public static final DeferredItem<Item> FIG = ITEMS.register("fig",
            () -> new FigItem(new Item.Properties()));
    public static final DeferredItem<Item> GLASSJAR = ITEMS.register("glassjar",
            () -> new GlassjarItem(new Item.Properties()));
    public static final DeferredItem<Item> GUIDING_DRUG = ITEMS.register("guiding_drug",
            () -> new GuidingDrugItem(new Item.Properties()));
    public static final DeferredItem<Item> HEART_CHOCOLATE_0 = ITEMS.register("heart_chocolate_0",
            () -> new HeartChocolate0Item(new Item.Properties()));
    public static final DeferredItem<Item> HEART_CHOCOLATE_1 = ITEMS.register("heart_chocolate_1",
            () -> new HeartChocolate1Item(new Item.Properties()));
    public static final DeferredItem<Item> HEART_CHOCOLATE_2 = ITEMS.register("heart_chocolate_2",
            () -> new HeartChocolate2Item(new Item.Properties()));
    public static final DeferredItem<Item> LIGHT_MOSS_PHANTOM_MEMBRANE = ITEMS.register("light_moss_phantom_membrane",
            () -> new LightMossPhantomMembraneItem(new Item.Properties()));
    public static final DeferredItem<Item> MELTDREAM_CRYSTAL_0 = ITEMS.register("meltdream_crystal_0",
            () -> new MeltdreamCrystal0Item(new Item.Properties()));
    public static final DeferredItem<Item> MEMENTO_ITEM_01 = ITEMS.register("memento_item_01",
            () -> new MementoItem01Item(new Item.Properties()));
    public static final DeferredItem<Item> MEMORY_GEM_0 = ITEMS.register("memory_gem_0",
            () -> new MemoryGem0Item(new Item.Properties()));
    public static final DeferredItem<Item> MOSS_PHANTOM_MEMBRANE = ITEMS.register("moss_phantom_membrane",
            () -> new MossPhantomMembraneItem(new Item.Properties()));
    public static final DeferredItem<Item> POPPING_CANDY = ITEMS.register("popping_candy",
            () -> new PoppingCandyItem(new Item.Properties()));
    public static final DeferredItem<Item> RED_DEW_0 = ITEMS.register("red_dew_0",
            () -> new RedDew0Item(new Item.Properties()));
    public static final DeferredItem<Item> SHADOW_BREATH = ITEMS.register("shadow_breath",
            () -> new ShadowBreathItem(new Item.Properties()));
    public static final DeferredItem<Item> SQUEAL_WAVE = ITEMS.register("squeal_wave",
            () -> new SquealWaveItem(new Item.Properties()));
    public static final DeferredItem<Item> STRAWBERRY_HEART = ITEMS.register("strawberry_heart",
            () -> new StrawberryHeartItem(new Item.Properties()));
    public static final DeferredItem<Item> WAFER_BISCUIT = ITEMS.register("wafer_biscuit",
            () -> new WaferBiscuitItem(new Item.Properties()));

    // ==================== Curio饰品/特殊物品 ====================

    public static final DeferredItem<Item> ANGEL_WING = ITEMS.registerSimpleItem("angel_wing");
    public static final DeferredItem<CalaisSpiceBottleCurioItem> CALAIS_SPICE_BOTTLE_CURIO = ITEMS.register("calais_spice_bottle_curio",
            () -> new CalaisSpiceBottleCurioItem());
    public static final DeferredItem<CarapaxCharmItem> CARAPAX_CHARM = ITEMS.register("carapax_charm",
            () -> new CarapaxCharmItem());
    public static final DeferredItem<CeciliacareCharmItem> CECILIACARE_CHARM = ITEMS.register("ceciliacare_charm",
            () -> new CeciliacareCharmItem());
    public static final DeferredItem<CounterRingItem> COUNTER_RING = ITEMS.register("counter_ring",
            () -> new CounterRingItem());
    public static final DeferredItem<DarkAlllegoryCurioItem> DARK_ALLLEGORY_CURIO = ITEMS.register("dark_alllegory_curio",
            () -> new DarkAlllegoryCurioItem());
    public static final DeferredItem<EmbryoBeltItem> EMBRYO_BELT = ITEMS.register("embryo_belt",
            () -> new EmbryoBeltItem());
    public static final DeferredItem<EmbryoCharmItem> EMBRYO_CHARM = ITEMS.register("embryo_charm",
            () -> new EmbryoCharmItem());
    public static final DeferredItem<EmbryoNecklaceItem> EMBRYO_NECKLACE = ITEMS.register("embryo_necklace",
            () -> new EmbryoNecklaceItem());
    public static final DeferredItem<EmbryoRingItem> EMBRYO_RING = ITEMS.register("embryo_ring",
            () -> new EmbryoRingItem());
    public static final DeferredItem<Item> FORSAKENS_WING = ITEMS.registerSimpleItem("forsakens_wing");
    public static final DeferredItem<GhostFaceHeadItem> GHOST_FACE_HEAD = ITEMS.register("ghost_face_head",
            () -> new GhostFaceHeadItem());
    public static final DeferredItem<Item> GROUND_WING = ITEMS.registerSimpleItem("ground_wing");
    public static final DeferredItem<Hithard0RingItem> HITHARD_0_RING = ITEMS.register("hithard_0_ring",
            () -> new Hithard0RingItem());
    public static final DeferredItem<Hithard1RingItem> HITHARD_1_RING = ITEMS.register("hithard_1_ring",
            () -> new Hithard1RingItem());
    public static final DeferredItem<Item> MACHINE_WING = ITEMS.registerSimpleItem("machine_wing");
// ===== 实用工具类物品 =====

    public static final DeferredItem<Item> PALE_BONENEEDLE = ITEMS.registerSimpleItem("pale_boneneedle");
    public static final DeferredItem<RedDew0RingItem> RED_DEW_0_RING = ITEMS.register("red_dew_0_ring",
            () -> new RedDew0RingItem());
    public static final DeferredItem<RedDew1RingItem> RED_DEW_1_RING = ITEMS.register("red_dew_1_ring",
            () -> new RedDew1RingItem());
    public static final DeferredItem<RedDew2RingItem> RED_DEW_2_RING = ITEMS.register("red_dew_2_ring",
            () -> new RedDew2RingItem());
    public static final DeferredItem<RedDew3RingItem> RED_DEW_3_RING = ITEMS.register("red_dew_3_ring",
            () -> new RedDew3RingItem());
    public static final DeferredItem<SeaCharmItem> SEA_CHARM = ITEMS.register("sea_charm",
            () -> new SeaCharmItem());
    public static final DeferredItem<WindKnightFlagItem> WIND_KNIGHT_FLAG = ITEMS.register("wind_knight_flag",
            () -> new WindKnightFlagItem());
    public static final DeferredItem<Item> WINGS_OF_FANG = ITEMS.registerSimpleItem("wings_of_fang");

    // ==================== 补全注册：从item文件夹整理出的未注册物品 ====================

    /**
     * 万相之戒 (allkinds_ring)
     * Curio 戒指饰品，传说品质
     */
    public static final DeferredItem<AllkindsRingItem> ALLKINDS_RING = ITEMS.register("allkinds_ring",
            () -> new AllkindsRingItem());

    /**
     * 啵啵鸡饰品 (boboji_curio)
     * Curio 饰品，传说品质，装备鞘翅时增强瞬身术
     */
    public static final DeferredItem<BobojiCurioItem> BOBOJI_CURIO = ITEMS.register("boboji_curio",
            () -> new BobojiCurioItem());

    /**
     * 光明蝴蝶饰品 (bright_butterfly_curio)
     * Curio 饰品，上古品质，低亮度获得夜视
     */
    public static final DeferredItem<BrightButterflyCurioItem> BRIGHT_BUTTERFLY_CURIO = ITEMS.register("bright_butterfly_curio",
            () -> new BrightButterflyCurioItem());

    /**
     * 十字项链 (cross_necklace)
     * Curio 项链饰品，精良品质，延长瞬身术回避时间
     */
    public static final DeferredItem<CrossNecklaceItem> CROSS_NECKLACE = ITEMS.register("cross_necklace",
            () -> new CrossNecklaceItem());

    /**
     * 堕落之躯 (degenerate_bodys)
     * Curio 饰品，史诗品质，精神值归零不再损失生命
     */
    public static final DeferredItem<DegenerateBodysItem> DEGENERATE_BODYS = ITEMS.register("degenerate_bodys",
            () -> new DegenerateBodysItem());

    /**
     * 梦境旅者腰带 (dream_traveler_belt)
     * Curio 腰带饰品，精良品质
     */
    public static final DeferredItem<DreamTravelerBeltItem> DREAM_TRAVELER_BELT = ITEMS.register("dream_traveler_belt",
            () -> new DreamTravelerBeltItem());

    /**
     * 公爵硬币 (duke_coin_curio)
     * Curio 饰品，优秀品质
     */
    public static final DeferredItem<DukeCoinCurioItem> DUKE_COIN_CURIO = ITEMS.register("duke_coin_curio",
            () -> new DukeCoinCurioItem());

    /**
     * 末影之眼护符 (endeye_charm)
     * Curio 护符，使末影人保持中立
     */
    public static final DeferredItem<EndeyeCharmItem> ENDEYE_CHARM = ITEMS.register("endeye_charm",
            () -> new EndeyeCharmItem());

    /**
     * 闪避斗篷 (evasion_cloak)
     * Curio 背部饰品，传说品质，延长瞬身术回避时间
     */
    public static final DeferredItem<EvasionCloakItem> EVASION_CLOAK = ITEMS.register("evasion_cloak",
            () -> new EvasionCloakItem());

    /**
     * 羽毛项链 (feather_necklace)
     * Curio 项链饰品，优秀品质
     */
    public static final DeferredItem<FeatherNecklaceItem> FEATHER_NECKLACE = ITEMS.register("feather_necklace",
            () -> new FeatherNecklaceItem());

    /**
     * 烈火项链 (fire_0_necklace)
     * Curio 项链饰品，行走留下火焰
     */
    public static final DeferredItem<Fire0NecklaceItem> FIRE_0_NECKLACE = ITEMS.register("fire_0_necklace",
            () -> new Fire0NecklaceItem());

    /**
     * 花环 (garland)
     * Curio 饰品，随时间缓慢消耗耐久
     */
    public static final DeferredItem<GarlandItem> GARLAND = ITEMS.register("garland",
            () -> new GarlandItem());

    /**
     * 金护符 (gold_charm)
     * Curio 护符，使猪灵保持中立
     */
    public static final DeferredItem<GoldCharmItem> GOLD_CHARM = ITEMS.register("gold_charm",
            () -> new GoldCharmItem());

    /**
     * 生命项链 (health_0_necklace)
     * Curio 项链饰品，优秀品质
     */
    public static final DeferredItem<Health0NecklaceItem> HEALTH_0_NECKLACE = ITEMS.register("health_0_necklace",
            () -> new Health0NecklaceItem());

    /**
     * 日和头饰 (hiyori_head)
     * Curio 头部饰品，史诗品质
     */
    public static final DeferredItem<HiyoriHeadItem> HIYORI_HEAD = ITEMS.register("hiyori_head",
            () -> new HiyoriHeadItem());

    /**
     * 冰影饰品 (iceshadow_curio)
     * Curio 饰品，大师品质，冰影战锤额外释放水晶体
     */
    public static final DeferredItem<IceshadowCurioItem> ICESHADOW_CURIO = ITEMS.register("iceshadow_curio",
            () -> new IceshadowCurioItem());

    /**
     * 光蝶饰品 (light_butterfly_curio)
     * Curio 饰品，精良品质，低亮度获得夜视（带持续判断）
     */
    public static final DeferredItem<LightButterflyCurioItem> LIGHT_BUTTERFLY_CURIO = ITEMS.register("light_butterfly_curio",
            () -> new LightButterflyCurioItem());

    /**
     * 自然腰带 (nature_belt)
     * Curio 腰带饰品，随时间缓慢消耗耐久
     */
    public static final DeferredItem<NatureBeltItem> NATURE_BELT = ITEMS.register("nature_belt",
            () -> new NatureBeltItem());

    /**
     * 纸飞机 (paper_plane)
     * Curio 饰品，增大风向影响
     */
    public static final DeferredItem<PaperPlaneItem> PAPER_PLANE = ITEMS.register("paper_plane",
            () -> new PaperPlaneItem());

    /**
     * Qym 头饰 (qym_head)
     * Curio 头部饰品，神迹品质，施法无视冷却时间
     */
    public static final DeferredItem<QymHeadItem> QYM_HEAD = ITEMS.register("qym_head",
            () -> new QymHeadItem());

    /**
     * 兔子项链 (rabbit_0_necklace)
     * Curio 项链饰品，获得跳跃提升效果
     */
    public static final DeferredItem<Rabbit0NecklaceItem> RABBIT_0_NECKLACE = ITEMS.register("rabbit_0_necklace",
            () -> new Rabbit0NecklaceItem());

    /**
     * 雪誓头饰 (snow_vow_head)
     * Curio 头部饰品，精良品质，附近玩家获得幸运加成
     */
    public static final DeferredItem<SnowVowHeadItem> SNOW_VOW_HEAD = ITEMS.register("snow_vow_head",
            () -> new SnowVowHeadItem());

    /**
     * 大地护符 (terra_charm)
     * Curio 护符，史诗品质，大地之刃战技冷却缩减
     */
    public static final DeferredItem<TerraCharmItem> TERRA_CHARM = ITEMS.register("terra_charm",
            () -> new TerraCharmItem());

    /**
     * 测试饰品 (test_curio)
     * Curio 测试物品
     */
    public static final DeferredItem<TestCurioItem> TEST_CURIO = ITEMS.register("test_curio",
            () -> new TestCurioItem());

    /**
     * 旅者腰带 (traveler_belt)
     * Curio 腰带饰品
     */
    public static final DeferredItem<TravelerBeltItem> TRAVELER_BELT = ITEMS.register("traveler_belt",
            () -> new TravelerBeltItem());

    /**
     * 回返斗篷 (turnback_cloak)
     * Curio 背部饰品，传说品质，自动回避多次伤害
     */
    public static final DeferredItem<TurnbackCloakItem> TURNBACK_CLOAK = ITEMS.register("turnback_cloak",
            () -> new TurnbackCloakItem());

    /**
     * 白花之躯 (white_flower_body)
     * Curio 饰品，史诗品质，不再受环境影响 san 值
     */
    public static final DeferredItem<WhiteFlowerBodyItem> WHITE_FLOWER_BODY = ITEMS.register("white_flower_body",
            () -> new WhiteFlowerBodyItem());

    /**
     * 世界树种荚 (worldtree_seedpod)
     * Curio 饰品，史诗品质，在染梦世界特定环境下获得融梦能量
     */
    public static final DeferredItem<WorldtreeSeedpodItem> WORLDTREE_SEEDPOD = ITEMS.register("worldtree_seedpod",
            () -> new WorldtreeSeedpodItem());

    // ==================== 音乐唱片（使用 API registerCustom 注册） ====================

    /**
     * 甜蜜的梦唱片 (sweetdream_disc)
     * PasterDream - 甜蜜的梦，时长 2220 tick（约 111 秒）
     */
    public static final DeferredItem<PastedreamMusicDiscItem> SWEETDREAM_DISC =
            ItemMigrationAPI.registerCustom("sweetdream_disc",
                    () -> new PastedreamMusicDiscItem(PasterDreamMod.MOD_ID, "sweetdream_disc", "sweetdream"));

    /**
     * 落雪之梦唱片 (snowfalldream_disc)
     * PasterDream - 落雪之梦，时长 2520 tick（约 126 秒）
     */
    public static final DeferredItem<PastedreamMusicDiscItem> SNOWFALLDREAM_DISC =
            ItemMigrationAPI.registerCustom("snowfalldream_disc",
                    () -> new PastedreamMusicDiscItem(PasterDreamMod.MOD_ID, "snowfalldream_disc", "snowfalldream"));

    /**
     * 亚伦柯斯之触唱片 (aaroncos_disc)
     * PasterDream - 亚伦柯斯之触，时长 2980 tick（约 149 秒）
     */
    public static final DeferredItem<PastedreamMusicDiscItem> AARONCOS_DISC =
            ItemMigrationAPI.registerCustom("aaroncos_disc",
                    () -> new PastedreamMusicDiscItem(PasterDreamMod.MOD_ID, "aaroncos_disc", "aaroncos"));

    /**
     * 风之旅途唱片 (wind_journey_disc)
     * PasterDream - 风之旅途，时长 4240 tick（约 212 秒）
     */
    public static final DeferredItem<PastedreamMusicDiscItem> WIND_JOURNEY_DISC =
            ItemMigrationAPI.registerCustom("wind_journey_disc",
                    () -> new PastedreamMusicDiscItem(PasterDreamMod.MOD_ID, "wind_journey_disc", "wind_journey"));

    // ==================== 染梦群系背景音乐唱片（使用 API registerCustom 注册） ====================

    /**
     * 梦幻草原唱片 (dream_meadow_disc)
     * PasterDream - 梦幻草原，时长 2220 tick（约 111 秒）
     */
    public static final DeferredItem<PastedreamMusicDiscItem> DREAM_MEADOW_DISC =
            ItemMigrationAPI.registerCustom("dream_meadow_disc",
                    () -> new PastedreamMusicDiscItem(PasterDreamMod.MOD_ID, "dream_meadow_disc", "dream_meadow"));

    /**
     * 梦幻荒原唱片 (dream_heath_disc)
     * PasterDream - 梦幻荒原，时长 2400 tick（约 120 秒）
     */
    public static final DeferredItem<PastedreamMusicDiscItem> DREAM_HEATH_DISC =
            ItemMigrationAPI.registerCustom("dream_heath_disc",
                    () -> new PastedreamMusicDiscItem(PasterDreamMod.MOD_ID, "dream_heath_disc", "dream_heath"));

    /**
     * 梦幻雪林唱片 (dream_taiga_disc)
     * PasterDream - 梦幻雪林，时长 2600 tick（约 130 秒）
     */
    public static final DeferredItem<PastedreamMusicDiscItem> DREAM_TAIGA_DISC =
            ItemMigrationAPI.registerCustom("dream_taiga_disc",
                    () -> new PastedreamMusicDiscItem(PasterDreamMod.MOD_ID, "dream_taiga_disc", "dream_taiga"));

    /**
     * 梦幻三角洲唱片 (dream_delta_disc)
     * PasterDream - 梦幻三角洲，时长 2800 tick（约 140 秒）
     */
    public static final DeferredItem<PastedreamMusicDiscItem> DREAM_DELTA_DISC =
            ItemMigrationAPI.registerCustom("dream_delta_disc",
                    () -> new PastedreamMusicDiscItem(PasterDreamMod.MOD_ID, "dream_delta_disc", "dream_delta"));

    // 标记唱片迁移状态
    static {
        ItemMigrationAPI.markMigrated(MigrationCategory.MUSIC_DISC,
                "sweetdream_disc", "snowfalldream_disc", "aaroncos_disc", "wind_journey_disc",
                "dream_meadow_disc", "dream_heath_disc", "dream_taiga_disc", "dream_delta_disc");
    }

    // ==================== 自定义模型方块 BlockItem ====================

    public static final DeferredItem<BlockItem> DYEDREAM_PLANKS_PANE = ITEMS.registerSimpleBlockItem("dyedream_planks_pane", PDBlocks.DYEDREAM_PLANKS_PANE);
    public static final DeferredItem<BlockItem> PINKAGARIC_0 = ITEMS.registerSimpleBlockItem("pinkagaric_0", PDBlocks.PINKAGARIC_0);
    public static final DeferredItem<BlockItem> PINKAGARIC_1 = ITEMS.registerSimpleBlockItem("pinkagaric_1", PDBlocks.PINKAGARIC_1);
    public static final DeferredItem<BlockItem> PINKAGARIC_2 = ITEMS.registerSimpleBlockItem("pinkagaric_2", PDBlocks.PINKAGARIC_2);
    public static final DeferredItem<BlockItem> PINKAGARIC_3 = ITEMS.registerSimpleBlockItem("pinkagaric_3", PDBlocks.PINKAGARIC_3);
    public static final DeferredItem<BlockItem> DYEDREAM_BUD_0 = ITEMS.registerSimpleBlockItem("dyedream_bud_0", PDBlocks.DYEDREAM_BUD_0);
    public static final DeferredItem<BlockItem> DYEDREAM_BUD_1 = ITEMS.registerSimpleBlockItem("dyedream_bud_1", PDBlocks.DYEDREAM_BUD_1);
    public static final DeferredItem<BlockItem> DYEDREAM_BUD_2 = ITEMS.registerSimpleBlockItem("dyedream_bud_2", PDBlocks.DYEDREAM_BUD_2);
    public static final DeferredItem<BlockItem> ICE_BUD_0 = ITEMS.registerSimpleBlockItem("ice_bud_0", PDBlocks.ICE_BUD_0);
    public static final DeferredItem<BlockItem> DYEDREAM_LILY_PAD = ITEMS.registerSimpleBlockItem("dyedream_lily_pad", PDBlocks.DYEDREAM_LILY_PAD);
    public static final DeferredItem<BlockItem> DYEDREAM_LOTUS = ITEMS.registerSimpleBlockItem("dyedream_lotus", PDBlocks.DYEDREAM_LOTUS);
    public static final DeferredItem<BlockItem> DYEDREAM_SEAGRASS = ITEMS.registerSimpleBlockItem("dyedream_seagrass", PDBlocks.DYEDREAM_SEAGRASS);
    public static final DeferredItem<BlockItem> DYEDREAM_SAPLING = ITEMS.registerSimpleBlockItem("dyedream_sapling", PDBlocks.DYEDREAM_SAPLING);
    public static final DeferredItem<BlockItem> DYEDREAM_CRACK = ITEMS.registerSimpleBlockItem("dyedream_crack", PDBlocks.DYEDREAM_CRACK);

    // ==================== 云朵方块 BlockItem ====================
    public static final DeferredItem<BlockItem> CLOUD = ITEMS.registerSimpleBlockItem("cloud", PDBlocks.CLOUD);
    public static final DeferredItem<BlockItem> DARK_CLOUD = ITEMS.registerSimpleBlockItem("dark_cloud", PDBlocks.DARK_CLOUD);
    public static final DeferredItem<BlockItem> THICK_CLOUD = ITEMS.registerSimpleBlockItem("thick_cloud", PDBlocks.THICK_CLOUD);

    // ==================== 染梦花草 BlockItem ====================
    public static final DeferredItem<BlockItem> FLOWER_1 = ITEMS.registerSimpleBlockItem("flower_1", PDBlocks.FLOWER_1);
    public static final DeferredItem<BlockItem> FLOWER_2 = ITEMS.registerSimpleBlockItem("flower_2", PDBlocks.FLOWER_2);
    public static final DeferredItem<BlockItem> FLOWER_3 = ITEMS.registerSimpleBlockItem("flower_3", PDBlocks.FLOWER_3);
    public static final DeferredItem<BlockItem> FLOWER_5 = ITEMS.registerSimpleBlockItem("flower_5", PDBlocks.FLOWER_5);
    public static final DeferredItem<BlockItem> FLOWER_6 = ITEMS.registerSimpleBlockItem("flower_6", PDBlocks.FLOWER_6);
    public static final DeferredItem<BlockItem> FLOWER_7 = ITEMS.registerSimpleBlockItem("flower_7", PDBlocks.FLOWER_7);
    public static final DeferredItem<BlockItem> FLOWER_8 = ITEMS.registerSimpleBlockItem("flower_8", PDBlocks.FLOWER_8);
    public static final DeferredItem<BlockItem> FLOWER_9 = ITEMS.registerSimpleBlockItem("flower_9", PDBlocks.FLOWER_9);
    public static final DeferredItem<BlockItem> FLOWER_10 = ITEMS.registerSimpleBlockItem("flower_10", PDBlocks.FLOWER_10);
    public static final DeferredItem<BlockItem> FLOWER_11 = ITEMS.registerSimpleBlockItem("flower_11", PDBlocks.FLOWER_11);
    public static final DeferredItem<BlockItem> FLOWER_12 = ITEMS.registerSimpleBlockItem("flower_12", PDBlocks.FLOWER_12);
    public static final DeferredItem<BlockItem> FLOWER_13 = ITEMS.registerSimpleBlockItem("flower_13", PDBlocks.FLOWER_13);
    public static final DeferredItem<BlockItem> FLOWER_14 = ITEMS.registerSimpleBlockItem("flower_14", PDBlocks.FLOWER_14);
    public static final DeferredItem<BlockItem> FLOWER_15 = ITEMS.registerSimpleBlockItem("flower_15", PDBlocks.FLOWER_15);
    public static final DeferredItem<BlockItem> FLOWER_16 = ITEMS.registerSimpleBlockItem("flower_16", PDBlocks.FLOWER_16);
    public static final DeferredItem<BlockItem> FLOWER_17 = ITEMS.registerSimpleBlockItem("flower_17", PDBlocks.FLOWER_17);
    public static final DeferredItem<BlockItem> FLOWER_18 = ITEMS.registerSimpleBlockItem("flower_18", PDBlocks.FLOWER_18);
    public static final DeferredItem<BlockItem> GRASS_1 = ITEMS.registerSimpleBlockItem("grass_1", PDBlocks.GRASS_1);
    public static final DeferredItem<BlockItem> GRASS_2 = ITEMS.registerSimpleBlockItem("grass_2", PDBlocks.GRASS_2);
    public static final DeferredItem<BlockItem> GRASS_3 = ITEMS.registerSimpleBlockItem("grass_3", PDBlocks.GRASS_3);
    public static final DeferredItem<BlockItem> GRASS_4 = ITEMS.registerSimpleBlockItem("grass_4", PDBlocks.GRASS_4);
    public static final DeferredItem<BlockItem> GRASS_5 = ITEMS.registerSimpleBlockItem("grass_5", PDBlocks.GRASS_5);
    public static final DeferredItem<BlockItem> GRASS_6 = ITEMS.registerSimpleBlockItem("grass_6", PDBlocks.GRASS_6);
    public static final DeferredItem<BlockItem> GRASS_7 = ITEMS.registerSimpleBlockItem("grass_7", PDBlocks.GRASS_7);
    public static final DeferredItem<BlockItem> GRASS_8 = ITEMS.registerSimpleBlockItem("grass_8", PDBlocks.GRASS_8);
    public static final DeferredItem<BlockItem> GRASS_9 = ITEMS.registerSimpleBlockItem("grass_9", PDBlocks.GRASS_9);
    public static final DeferredItem<BlockItem> GRASS_10 = ITEMS.registerSimpleBlockItem("grass_10", PDBlocks.GRASS_10);
    public static final DeferredItem<BlockItem> GRASS_11 = ITEMS.registerSimpleBlockItem("grass_11", PDBlocks.GRASS_11);
    public static final DeferredItem<BlockItem> GRASS_12 = ITEMS.registerSimpleBlockItem("grass_12", PDBlocks.GRASS_12);
    public static final DeferredItem<BlockItem> GRASS_13 = ITEMS.registerSimpleBlockItem("grass_13", PDBlocks.GRASS_13);
    public static final DeferredItem<BlockItem> GRASS_14 = ITEMS.registerSimpleBlockItem("grass_14", PDBlocks.GRASS_14);
    public static final DeferredItem<BlockItem> GRASS_15 = ITEMS.registerSimpleBlockItem("grass_15", PDBlocks.GRASS_15);

    /**
     * 静态初始化块 —— 输出所有物品注册的统计信息
     */
    static {
        int itemCount = ITEMS.getEntries().size();
        int blockItemCount = 0;
        for (var entry : ITEMS.getEntries()) {
            String id = entry.getId().getPath();
            if (id.contains("dyedream_block") || id.contains("dyedream_dirt") || 
                id.contains("dyedream_sand") || id.contains("dyedream_planks") ||
                id.contains("dyedream_glass") || id.contains("dyedream_ice") ||
                id.contains("flower_") || id.contains("grass_") ||
                id.contains("cloud") || id.contains("dyedream_log") ||
                id.contains("dyedream_wood") || id.contains("stripped_") ||
                id.contains("ore") || id.contains("_stairs") || id.contains("_slab") ||
                id.contains("_wall") || id.contains("_fence") || id.contains("_door") ||
                id.contains("_trapdoor") || id.contains("_pane") || id.contains("_button") ||
                id.contains("_pressure_plate") || id.contains("carve_") || id.contains("gold_") ||
                id.contains("pinkagaric") || id.contains("dyedream_bud") || id.contains("ice_bud") ||
                id.contains("dyedream_lily") || id.contains("dyedream_lotus") ||
                id.contains("dyedream_seagrass") || id.contains("dyedream_sapling") ||
                id.contains("dyedream_crack") || id.contains("dyedream_lartern") ||
                id.contains("pillar_") || id.contains("bricks_") || id.contains("chiseled_") ||
                id.contains("smooth_") || id.contains("pinkslime") || id.contains("icestone") ||
                id.contains("leaves") || id.contains("worldtree") || id.contains("quartz") ||
                id.contains("amber_candy") || id.contains("dream_accumulator") ||
                id.contains("dyedream_desk") || id.contains("shadow_chest")) {
                blockItemCount++;
            }
        }
        PasterDreamMod.LOGGER.info("[PDItems] ✅ 物品注册统计：共 {} 个物品，其中约 {} 个 BlockItem", 
            itemCount, blockItemCount);
        PasterDreamMod.LOGGER.info("[PDItems]     DeferredRegister.Items 注册表已就绪");
    }
}
