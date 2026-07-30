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
import com.pasterdream.pasterdreammod.registry.PDEntities;
import com.pasterdream.pasterdreammod.registry.PDItems;
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
 * 原材料与杂物注册。
 *
 * @see PDItems
 */
public class PDItemsMaterials {


    // ==================== 测试材料物品 ====================

    /**
     * 钛锭 (titanium_ingot)
     * 基础材料，稀有度为 UNCOMMON
     * 使用 PDItems.ITEMS.register() 统一注册，而非 ItemAPI，
     * 避免静态初始化阶段 DeferredItem.get() 触发 "unbound value" 错误
     */
    public static final DeferredItem<Item> TITANIUM_INGOT = PDItems.ITEMS.register("titanium_ingot",
            () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)));

    /**
     * 染梦粉 (dyedream_dust)
     * 基础材料
     */
    public static final DeferredItem<Item> DYEDREAM_DUST = PDItems.ITEMS.registerSimpleItem("dyedream_dust");

    /**
     * 魔法石 (magic_stone)
     * 基础材料，带有特殊描述文本
     */
    public static final DeferredItem<Item> MAGIC_STONE =
            ItemAPI.simpleItem("magic_stone")
                    .tooltip("§7§o哪个法师的兜里不会踹几块魔法石呢？")
                    .build();

    /**
     * 粉色粘液球 (pink_slimeball)
     */
    public static final DeferredItem<Item> PINK_SLIMEBALL = PDItems.ITEMS.registerSimpleItem("pink_slimeball");

    /**
     * 染梦石英 (dyedreamquartz)
     */
    public static final DeferredItem<Item> DYEDREAMQUARTZ = PDItems.ITEMS.registerSimpleItem("dyedreamquartz");


    // ==================== 批量移植的材料物品 ====================

    /**
     * 基础材料物品 - COMMON 稀有度
     */
    public static final DeferredItem<Item> BASALT_SNAIL_SHELL = PDItems.ITEMS.registerSimpleItem("basalt_snail_shell");
    public static final DeferredItem<Item> BLACK_BEETLE_CARAPACE = PDItems.ITEMS.registerSimpleItem("black_beetle_carapace");
    public static final DeferredItem<Item> BLACK_BEETLE_VOCALCORD = PDItems.ITEMS.registerSimpleItem("black_beetle_vocalcord");
    public static final DeferredItem<Item> BLACKMETAL_GRAIN = PDItems.ITEMS.registerSimpleItem("blackmetal_grain");
    public static final DeferredItem<Item> BLACKMETAL_INGOT = PDItems.ITEMS.registerSimpleItem("blackmetal_ingot");
    public static final DeferredItem<Item> BLACKSTICK = PDItems.ITEMS.registerSimpleItem("blackstick");
    public static final DeferredItem<Item> BLUE_HEART_OF_THE_SEA = PDItems.ITEMS.registerSimpleItem("blue_heart_of_the_sea");
    public static final DeferredItem<Item> BROKENNOTES_0 = PDItems.ITEMS.registerSimpleItem("brokennotes_0");
    public static final DeferredItem<Item> CHARGED_AMETHYST = PDItems.ITEMS.registerSimpleItem("charged_amethyst");
    public static final DeferredItem<Item> COARSE_SALT = PDItems.ITEMS.registerSimpleItem("coarse_salt");
    public static final DeferredItem<Item> CONGEAL_WIND = PDItems.ITEMS.registerSimpleItem("congeal_wind");
    public static final DeferredItem<Item> COTTON = PDItems.ITEMS.registerSimpleItem("cotton");
    public static final DeferredItem<Item> DREAM_AURORIAN_STEEL = PDItems.ITEMS.registerSimpleItem("dream_aurorian_steel");
    /**
     * 忆梦魔导透镜 (dream_meter)
     * 使用 GeckoLib 实现完整 3D 手持模型渲染
     * 替换了原简单材料版本
     */
    public static final DeferredItem<DreamMeterItem> DREAM_METER = PDItems.ITEMS.register("dream_meter",
            () -> new DreamMeterItem());
    public static final DeferredItem<Item> DREAMWISH = PDItems.ITEMS.registerSimpleItem("dreamwish");
    public static final DeferredItem<Item> DYEDREAM_BASE = PDItems.ITEMS.registerSimpleItem("dyedream_base");
    public static final DeferredItem<Item> DYEDREAM_BUD_NUGGET = PDItems.ITEMS.registerSimpleItem("dyedream_bud_nugget");
    public static final DeferredItem<Item> DYEDREAM_COROLLA = PDItems.ITEMS.registerSimpleItem("dyedream_corolla");
    public static final DeferredItem<Item> DYEDREAM_DUST_PIECE = PDItems.ITEMS.registerSimpleItem("dyedream_dust_piece");
    public static final DeferredItem<Item> DYEDREAM_DYE = PDItems.ITEMS.registerSimpleItem("dyedream_dye");
    public static final DeferredItem<Item> DYEDREAM_NUGGET = PDItems.ITEMS.registerSimpleItem("dyedream_nugget");
    public static final DeferredItem<Item> DYEDREAM_UPGRADE = PDItems.ITEMS.registerSimpleItem("dyedream_upgrade", new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> EGGDOUGH = PDItems.ITEMS.registerSimpleItem("eggdough");
    public static final DeferredItem<Item> ELDER_GUARDIAN_SCALE = PDItems.ITEMS.registerSimpleItem("elder_guardian_scale");
    public static final DeferredItem<Item> ENHANCE_STONE_0 = PDItems.ITEMS.registerSimpleItem("enhance_stone_0");
    public static final DeferredItem<Item> ENHANCE_STONE_1 = PDItems.ITEMS.registerSimpleItem("enhance_stone_1");
    public static final DeferredItem<Item> FABRIC = PDItems.ITEMS.registerSimpleItem("fabric");
    public static final DeferredItem<Item> FLOUR = PDItems.ITEMS.registerSimpleItem("flour");
    public static final DeferredItem<Item> ICESHADOW_HAMMER_EMBRYO = PDItems.ITEMS.registerSimpleItem("iceshadow_hammer_embryo");
    public static final DeferredItem<Item> MANADUST = PDItems.ITEMS.registerSimpleItem("manadust");
    public static final DeferredItem<Item> MOLTENGOLD_DUST = PDItems.ITEMS.registerSimpleItem("moltengold_dust");
    public static final DeferredItem<Item> MOLTENGOLD_INGOT = PDItems.ITEMS.registerSimpleItem("moltengold_ingot");
    public static final DeferredItem<Item> MOLTENGOLD_NUGGET = PDItems.ITEMS.registerSimpleItem("moltengold_nugget");
    public static final DeferredItem<Item> MORTAR = PDItems.ITEMS.registerSimpleItem("mortar");
    public static final DeferredItem<Item> NIGHTMARE_FUEL = PDItems.ITEMS.registerSimpleItem("nightmare_fuel");
    public static final DeferredItem<Item> PEN_AND_INK = PDItems.ITEMS.registerSimpleItem("pen_and_ink");
    public static final DeferredItem<Item> PERGAMYN = PDItems.ITEMS.registerSimpleItem("pergamyn");
    public static final DeferredItem<Item> PROTECT_DECK = PDItems.ITEMS.registerSimpleItem("protect_deck");
    public static final DeferredItem<Item> PULSE_WINDRUNNER_CRYSTAL = PDItems.ITEMS.registerSimpleItem("pulse_windrunner_crystal");
    public static final DeferredItem<Item> PURE_HORROR = PDItems.ITEMS.registerSimpleItem("pure_horror");
    public static final DeferredItem<Item> RAW_MOLTENGOLD = PDItems.ITEMS.registerSimpleItem("raw_moltengold");
    public static final DeferredItem<Item> RAW_TITANIUM = PDItems.ITEMS.registerSimpleItem("raw_titanium");
    public static final DeferredItem<Item> REEDROD = PDItems.ITEMS.registerSimpleItem("reedrod");
    public static final DeferredItem<Item> RUST_BLACK_METAL_GRAIN = PDItems.ITEMS.registerSimpleItem("rust_black_metal_grain");
    public static final DeferredItem<Item> RYESEED = PDItems.ITEMS.registerSimpleItem("ryeseed");
    public static final DeferredItem<Item> SALT = PDItems.ITEMS.registerSimpleItem("salt");


    // ==================== Phase 2: 移植特殊物品 ====================

    public static final DeferredItem<JungleSporeItem> JUNGLE_SPORE = PDItems.ITEMS.registerItem("jungle_spore", JungleSporeItem::new,
            new Item.Properties().food(JungleSporeItem.createFoodProperties()));

    public static final DeferredItem<MeltdreamLiquidItem> MELTDREAM_LIQUID_BUCKET = PDItems.ITEMS.registerItem("meltdream_liquid_bucket", MeltdreamLiquidItem::new,
            new Item.Properties().stacksTo(1));

    public static final DeferredItem<PinkeggItem> PINKEGG = PDItems.ITEMS.registerItem("pinkegg", PinkeggItem::new,
            new Item.Properties().stacksTo(16));

    public static final DeferredItem<PliersItem> PLIERS = PDItems.ITEMS.registerItem("pliers", PliersItem::new,
            new Item.Properties().durability(160));

    public static final DeferredItem<Item> SCULK_HEART = PDItems.ITEMS.registerSimpleItem("sculk_heart");
    public static final DeferredItem<Item> SCULK_UPGRADE = PDItems.ITEMS.registerSimpleItem("sculk_upgrade");
    public static final DeferredItem<Item> SHADOW_DUNGEON_KEY = PDItems.ITEMS.registerSimpleItem("shadow_dungeon_key");
    public static final DeferredItem<Item> SHADOW_EROSION_AXE_EMBRYO = PDItems.ITEMS.registerSimpleItem("shadow_erosion_axe_embryo");
    public static final DeferredItem<Item> SHADOW_EROSION_HOE_EMBRYO = PDItems.ITEMS.registerSimpleItem("shadow_erosion_hoe_embryo");
    public static final DeferredItem<Item> SHADOW_EROSION_PICKAXE_EMBRYO = PDItems.ITEMS.registerSimpleItem("shadow_erosion_pickaxe_embryo");
    public static final DeferredItem<Item> SHADOW_EROSION_SHOVEL_EMBRYO = PDItems.ITEMS.registerSimpleItem("shadow_erosion_shovel_embryo");
    public static final DeferredItem<Item> SHADOW_EROSION_SWORD_EMBRYO = PDItems.ITEMS.registerSimpleItem("shadow_erosion_sword_embryo");
    public static final DeferredItem<Item> SHADOW_HILT = PDItems.ITEMS.registerSimpleItem("shadow_hilt");
    public static final DeferredItem<Item> SHADOW_SWORD_EMBRYO = PDItems.ITEMS.registerSimpleItem("shadow_sword_embryo");
    public static final DeferredItem<Item> SILVER_BELL = PDItems.ITEMS.registerSimpleItem("silver_bell");
    public static final DeferredItem<Item> SORBENT = PDItems.ITEMS.registerSimpleItem("sorbent");
    public static final DeferredItem<Item> SOUL_DUST = PDItems.ITEMS.registerSimpleItem("soul_dust");
    public static final DeferredItem<Item> SOUL_ESSENCE = PDItems.ITEMS.registerSimpleItem("soul_essence");
    public static final DeferredItem<Item> SPOOL = PDItems.ITEMS.registerSimpleItem("spool");
    public static final DeferredItem<Item> STAR_WISH_ROD_EMBRYO = PDItems.ITEMS.registerSimpleItem("star_wish_rod_embryo");
    public static final DeferredItem<Item> SWORD_EMBRYO_0 = PDItems.ITEMS.registerSimpleItem("sword_embryo_0");
    public static final DeferredItem<Item> TERRASWORD_EMBRYO = PDItems.ITEMS.registerSimpleItem("terrasword_embryo");
    public static final DeferredItem<Item> TITANIUM_NUGGET = PDItems.ITEMS.registerSimpleItem("titanium_nugget");
    public static final DeferredItem<Item> TITANIUM_UPGRADE = PDItems.ITEMS.registerSimpleItem("titanium_upgrade");
    public static final DeferredItem<Item> UNKNOWNNOTES_0 = PDItems.ITEMS.register("unknownnotes_0",
            () -> new com.pasterdream.pasterdreammod.item.Unknownnotes0Item());
    public static final DeferredItem<Item> WHITE_COROLLA = PDItems.ITEMS.registerSimpleItem("white_corolla");
    public static final DeferredItem<Item> WHITE_CRYSTAL = PDItems.ITEMS.registerSimpleItem("white_crystal");
    public static final DeferredItem<Item> WHITE_SWORD_EMBRYO = PDItems.ITEMS.registerSimpleItem("white_sword_embryo");
    public static final DeferredItem<Item> WIND_IRON_INGOT = PDItems.ITEMS.registerSimpleItem("wind_iron_ingot");
    public static final DeferredItem<Item> WIND_PLANT_EXTRACT = PDItems.ITEMS.registerSimpleItem("wind_plant_extract");
    public static final DeferredItem<Item> WINDRUNNER_CRYSTAL = PDItems.ITEMS.registerSimpleItem("windrunner_crystal");
    public static final DeferredItem<Item> YEAST = PDItems.ITEMS.registerSimpleItem("yeast");

    /**
     * 基础材料物品 - UNCOMMON 稀有度
     */
    public static final DeferredItem<Item> DYEDREAM_INGOT = PDItems.ITEMS.registerSimpleItem("dyedream_ingot", new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON));

    // ==================== API物品移植测试 ====================
    //
    // 以下物品使用 ItemAPI 进行注册，验证API的编译正确性和可用性。
    // 覆盖 SimpleItemBuilder / FoodItemBuilder / ToolItemBuilder / CurioItemBuilder 四种类型。
    //

    /**
     * 玻璃杯 (glass_cup)
     * 使用 API SimpleItemBuilder 注册
     * 原模组：new Item()，COMMON 稀有度，64堆叠
     */
    public static final DeferredItem<Item> GLASS_CUP =
            ItemAPI.simpleItem("glass_cup").build();

    /**
     * 生面团 (dough)
     * 使用 API FoodItemBuilder 注册
     * 原模组：营养 1，饱食度 0.1f
     */
    public static final DeferredItem<Item> DOUGH =
            ItemAPI.foodItem("dough")
                    .nutrition(1).saturationModifier(0.1f)
                    .build();

    /**
     * 铜斧 (copper_axe)
     * 使用 API ToolItemBuilder 注册
     * 原模组：耐久 225，速度 5.0，伤害+7，攻速 -3.15
     */
    public static final DeferredItem<Item> COPPER_AXE =
            ItemAPI.toolItem("copper_axe")
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
            ItemAPI.toolItem("copper_shovel")
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
            ItemAPI.toolItem("copper_hoe")
                    .type(ToolType.HOE)
                    .durability(225).miningSpeed(5.0f)
                    .attackDamage(0.0f).attackSpeed(-1.5f)
                    .enchantment(12)
                    .repairWith(new ItemStack(Items.COPPER_INGOT))
                    .build();

    // 四叶草护符已在下方 Curio 护符合集中使用 CurioAPI 注册


    // ==================== 杂项补全：简单物品（批量移植自原版） ====================

    /**
     * 标签图标物品 (tabitem_1 / tabitem_2)
     * 原版调试栏/拓展栏创造标签的图标物品，亦作为战利品表中的"空占位"条目使用
     */
    public static final DeferredItem<Item> TABITEM_1 =
            ItemAPI.simpleItem("tabitem_1").stacksTo(1).build();
    public static final DeferredItem<Item> TABITEM_2 =
            ItemAPI.simpleItem("tabitem_2").stacksTo(1).build();

    /**
     * 天赋抉择信物 (talent_light / talent_shadow)
     * 光/影天赋抉择的信物物品（原版即为无行为物品，天赋逻辑由成就/GUI 系统驱动）
     * 原版未加入任何创造标签，此处保持一致
     */
    public static final DeferredItem<Item> TALENT_LIGHT =
            ItemAPI.simpleItem("talent_light").stacksTo(1).build();
    public static final DeferredItem<Item> TALENT_SHADOW =
            ItemAPI.simpleItem("talent_shadow").stacksTo(1).build();

    /**
     * 夜明蝶 (lightning_item) 与 暗影旋涡投掷物 (shadow_vortex_item)
     * 原版即为无行为的材料/投射物贴图物品（法术渲染用），原版未加入创造标签
     */
    public static final DeferredItem<Item> LIGHTNING_ITEM =
            ItemAPI.simpleItem("lightning_item").build();
    public static final DeferredItem<Item> SHADOW_VORTEX_ITEM =
            ItemAPI.simpleItem("shadow_vortex_item").build();

    /**
     * 聚梦法杖原胚 (dream_wand_embryo)
     * 锻造材料。右击染梦书桌清空法杖数据见 {@code DreamWandItem.clearWandDataOnDesk}（已实现）。
     */
    public static final DeferredItem<Item> DREAM_WAND_EMBRYO =
            ItemAPI.simpleItem("dream_wand_embryo")
                    .stacksTo(1)
                    .tooltip("§7未加工原胚", "§7需要在精铸工坊内完成后续锻造")
                    .build();

}
