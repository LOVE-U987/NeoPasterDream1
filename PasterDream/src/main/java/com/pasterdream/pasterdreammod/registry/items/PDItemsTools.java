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
 * 工具与武器注册。
 *
 * @see PDItems
 */
public class PDItemsTools {


    // ==================== 剑类武器 ====================

    public static final DeferredItem<Item> BROKEN_HERO_SWORD =
            ItemAPI.toolItem("broken_hero_sword")
                    .type(ToolType.SWORD).durability(100)
                    .attackDamage(6.0f).attackSpeed(-2.4f)
                    .enchantment(0)
                    .build();
    public static final DeferredItem<Item> COPPER_SWORD =
            ItemAPI.toolItem("copper_sword")
                    .type(ToolType.SWORD).durability(225)
                    .attackDamage(4.5f).attackSpeed(-2.4f)
                    .enchantment(12)
                    .repairWith(new ItemStack(Items.COPPER_INGOT))
                    .build();
    public static final DeferredItem<Item> CREATIVE_SWORD =
            ItemAPI.toolItem("creative_sword")
                    .type(ToolType.SWORD).durability(100)
                    .attackDamage(9.0f).attackSpeed(6.0f)
                    .enchantment(2)
                    .build();
    public static final DeferredItem<Item> DESERT_SWORD =
            ItemAPI.toolItem("desert_sword")
                    .type(ToolType.SWORD).durability(1561)
                    .attackDamage(10.0f).attackSpeed(-3.1f)
                    .enchantment(8)
                    .build();
    public static final DeferredItem<Item> DYEDREAM_SWORD_0 =
            ItemAPI.toolItem("dyedream_sword_0")
                    .type(ToolType.SWORD).durability(1314)
                    .attackDamage(8.0f).attackSpeed(-2.4f)
                    .enchantment(22)
                    .build();
    public static final DeferredItem<Item> DYEDREAM_SWORD =
            ItemAPI.toolItem("dyedream_sword")
                    .type(ToolType.SWORD).durability(1314)
                    .attackDamage(7.0f).attackSpeed(-2.4f)
                    .enchantment(22)
                    .build();
    public static final DeferredItem<Item> GRASS_SWORD =
            ItemAPI.toolItem("grass_sword")
                    .type(ToolType.SWORD).durability(874)
                    .attackDamage(6.0f).attackSpeed(-2.5f)
                    .enchantment(16)
                    .build();
    public static final DeferredItem<Item> ICESHADOW_HAMMER =
            ItemAPI.toolItem("iceshadow_hammer")
                    .type(ToolType.SWORD).durability(835)
                    .attackDamage(12.0f).attackSpeed(-3.3f)
                    .enchantment(2)
                    .build();
    public static final DeferredItem<Item> MOLTENGOLD_SWORD =
            ItemAPI.toolItem("moltengold_sword")
                    .type(ToolType.SWORD).durability(251)
                    .attackDamage(5.0f).attackSpeed(-2.3f)
                    .enchantment(23)
                    .build();
    public static final DeferredItem<Item> SHADOW_EROSION_SWORD =
            ItemAPI.toolItem("shadow_erosion_sword")
                    .type(ToolType.SWORD).durability(1725)
                    .attackDamage(5.5f).attackSpeed(-1.0f)
                    .enchantment(2)
                    .build();
    public static final DeferredItem<Item> SHADOW_SWORD =
            ItemAPI.toolItem("shadow_sword")
                    .type(ToolType.SWORD).durability(1771)
                    .attackDamage(11.0f).attackSpeed(-2.4f)
                    .enchantment(10)
                    .build();
    /**
     * 大地之刃 (terra_sword) — W2-D 战技接入
     * 改用自定义 {@link TerraSwordItem}（属性与原 ItemAPI 注册逐项一致），
     * 补上泰拉剑技：右键开启后 3 次挥剑各挥出一道大地之刃剑气
     */
    public static final DeferredItem<Item> TERRA_SWORD =
            PDItems.ITEMS.register("terra_sword",
                    () -> new TerraSwordItem(new Item.Properties()));
    public static final DeferredItem<Item> THERMAL_DAGGER =
            ItemAPI.toolItem("thermal_dagger")
                    .type(ToolType.SWORD).durability(1721)
                    .attackDamage(5.5f).attackSpeed(-2.3f)
                    .enchantment(2)
                    .build();
    public static final DeferredItem<Item> TIDE_SWORD =
            ItemAPI.toolItem("tide_sword")
                    .type(ToolType.SWORD).durability(1561)
                    .attackDamage(7.5f).attackSpeed(-2.8f)
                    .enchantment(11)
                    .build();
    public static final DeferredItem<Item> TITANIUM_SWORD =
            ItemAPI.toolItem("titanium_sword")
                    .type(ToolType.SWORD).durability(1721)
                    .attackDamage(6.5f).attackSpeed(-2.4f)
                    .enchantment(17)
                    .build();
    public static final DeferredItem<Item> TRUE_DESERT_SWORD =
            ItemAPI.toolItem("true_desert_sword")
                    .type(ToolType.SWORD).durability(1561)
                    .attackDamage(11.0f).attackSpeed(-3.1f)
                    .enchantment(8)
                    .build();
    public static final DeferredItem<Item> TRUE_GRASS_SWORD =
            ItemAPI.toolItem("true_grass_sword")
                    .type(ToolType.SWORD).durability(1311)
                    .attackDamage(6.5f).attackSpeed(-2.5f)
                    .enchantment(16)
                    .build();
    public static final DeferredItem<Item> TRUE_MOLTENGOLD_SWORD =
            ItemAPI.toolItem("true_moltengold_sword")
                    .type(ToolType.SWORD).durability(1255)
                    .attackDamage(6.0f).attackSpeed(-2.2f)
                    .enchantment(23)
                    .build();
    public static final DeferredItem<Item> TRUE_TIDE_SWORD =
            ItemAPI.toolItem("true_tide_sword")
                    .type(ToolType.SWORD).durability(1561)
                    .attackDamage(8.0f).attackSpeed(-2.8f)
                    .enchantment(11)
                    .build();
    public static final DeferredItem<Item> TRUEST_MOLTENGOLD_SWORD =
            ItemAPI.toolItem("truest_moltengold_sword")
                    .type(ToolType.SWORD).durability(1255)
                    .attackDamage(6.0f).attackSpeed(-2.15f)
                    .enchantment(23)
                    .build();
    /**
     * 白色灾厄 (white_sword) — W2-D 战技接入
     * 改用自定义 {@link WhiteSwordItem}（属性与原 ItemAPI 注册逐项一致，按原版补 fireResistant），
     * 补上白厄剑雨：右键在视点落点召唤 8 轮下落光剑
     */
    public static final DeferredItem<Item> WHITE_SWORD =
            PDItems.ITEMS.register("white_sword",
                    () -> new WhiteSwordItem(new Item.Properties()));


    // ==================== 镐类/锤类工具 ====================

    public static final DeferredItem<Item> COPPER_PICKAXE =
            ItemAPI.toolItem("copper_pickaxe")
                    .type(ToolType.PICKAXE).durability(225).miningSpeed(4.0f)
                    .attackDamage(2.5f).attackSpeed(-2.8f)
                    .enchantment(12)
                    .incorrectTag("minecraft:incorrect_for_stone_tool")
                    .repairWith(new ItemStack(Items.COPPER_INGOT))
                    .build();
    public static final DeferredItem<Item> DYEDREAM_HAMMER =
            ItemAPI.toolItem("dyedream_hammer")
                    .type(ToolType.HAMMER).durability(6570).miningSpeed(3.0f)
                    .attackDamage(10.0f).attackSpeed(-3.3f)
                    .enchantment(22)
                    .incorrectTag("minecraft:incorrect_for_netherite_tool")
                    .repairWith(() -> new ItemStack(PDItems.DYEDREAM_INGOT.get()))
                    .build();
    public static final DeferredItem<Item> DYEDREAM_PICKAXE =
            ItemAPI.toolItem("dyedream_pickaxe")
                    .type(ToolType.PICKAXE).durability(1314).miningSpeed(11.0f)
                    .attackDamage(5.0f).attackSpeed(-2.8f)
                    .enchantment(22)
                    .incorrectTag("minecraft:incorrect_for_netherite_tool")
                    .repairWith(() -> new ItemStack(PDItems.DYEDREAM_INGOT.get()))
                    .build();
    /**
     * 融梦水晶镐 (meltdream_pickaxe) — 融梦修补
     * 改用自定义 {@link MeltdreamPickaxeItem}（属性与原 ItemAPI 注册逐项一致），
     * 补上原版融梦修补：手持时每 10 tick 消耗 0.01 融梦能量修复 1 耐久；
     * 铁砧修复材料为融梦水晶碎片（原版行为，取代铁锭）
     */
    public static final DeferredItem<Item> MELTDREAM_PICKAXE =
            PDItems.ITEMS.register("meltdream_pickaxe",
                    () -> new MeltdreamPickaxeItem(new Item.Properties()));
    public static final DeferredItem<Item> MOLTENGOLD_PICKAXE =
            ItemAPI.toolItem("moltengold_pickaxe")
                    .type(ToolType.PICKAXE).durability(251).miningSpeed(14.0f)
                    .attackDamage(3.0f).attackSpeed(-2.7f)
                    .enchantment(23)
                    .incorrectTag("minecraft:incorrect_for_stone_tool")
                    .repairWith(() -> new ItemStack(PDItems.MOLTENGOLD_INGOT.get()))
                    .build();
    public static final DeferredItem<Item> SHADOW_EROSION_PICKAXE =
            ItemAPI.toolItem("shadow_erosion_pickaxe")
                    .type(ToolType.PICKAXE).durability(1725).miningSpeed(13.0f)
                    .attackDamage(5.0f).attackSpeed(-2.8f)
                    .enchantment(16)
                    .incorrectTag("minecraft:incorrect_for_netherite_tool")
                    .repairWith(() -> new ItemStack(PDItems.BLACKMETAL_INGOT.get()))
                    .build();
    public static final DeferredItem<Item> TITANIUM_PICKAXE =
            ItemAPI.toolItem("titanium_pickaxe")
                    .type(ToolType.PICKAXE).durability(1721).miningSpeed(9.0f)
                    .attackDamage(4.5f).attackSpeed(-2.8f)
                    .enchantment(17)
                    .incorrectTag("minecraft:incorrect_for_netherite_tool")
                    .repairWith(() -> new ItemStack(PDItems.TITANIUM_INGOT.get()))
                    .build();

    /**
     * 钛斧 (titanium_axe)
     * 使用 API ToolItemBuilder 注册
     * 原模组：耐久 1721，速度 9.0，伤害+9，攻速 -3.0
     */
    public static final DeferredItem<Item> TITANIUM_AXE =
            ItemAPI.toolItem("titanium_axe")
                    .type(ToolType.AXE)
                    .durability(1721).miningSpeed(9.0f)
                    .attackDamage(9.0f).attackSpeed(-3.0f)
                    .enchantment(17)
                    .incorrectTag("minecraft:incorrect_for_netherite_tool")
                    .repairWith(() -> new ItemStack(PDItems.TITANIUM_INGOT.get()))
                    .build();

    /**
     * 钛锹 (titanium_shovel)
     * 使用 API ToolItemBuilder 注册
     * 原模组：耐久 1721，速度 9.0，伤害+5.5，攻速 -3.0
     */
    public static final DeferredItem<Item> TITANIUM_SHOVEL =
            ItemAPI.toolItem("titanium_shovel")
                    .type(ToolType.SHOVEL)
                    .durability(1721).miningSpeed(9.0f)
                    .attackDamage(5.5f).attackSpeed(-3.0f)
                    .enchantment(17)
                    .incorrectTag("minecraft:incorrect_for_netherite_tool")
                    .repairWith(() -> new ItemStack(PDItems.TITANIUM_INGOT.get()))
                    .build();

    /**
     * 钛锄 (titanium_hoe)
     * 使用 API ToolItemBuilder 注册
     * 原模组：耐久 1721，速度 9.0，伤害+0.5，攻速 0.0
     */
    public static final DeferredItem<Item> TITANIUM_HOE =
            ItemAPI.toolItem("titanium_hoe")
                    .type(ToolType.HOE)
                    .durability(1721).miningSpeed(9.0f)
                    .attackDamage(0.5f).attackSpeed(0.0f)
                    .enchantment(17)
                    .incorrectTag("minecraft:incorrect_for_netherite_tool")
                    .repairWith(() -> new ItemStack(PDItems.TITANIUM_INGOT.get()))
                    .build();

    public static final DeferredItem<Item> TRUE_MOLTENGOLD_PICKAXE =
            ItemAPI.toolItem("true_moltengold_pickaxe")
                    .type(ToolType.PICKAXE).durability(1255).miningSpeed(16.0f)
                    .attackDamage(4.0f).attackSpeed(-2.6f)
                    .enchantment(23)
                    .incorrectTag("minecraft:incorrect_for_stone_tool")
                    .repairWith(() -> new ItemStack(PDItems.MOLTENGOLD_INGOT.get()))
                    .build();

    // === Dyedream 工具 ===
    public static final DeferredItem<Item> DYEDREAM_AXE =
            ItemAPI.toolItem("dyedream_axe")
                    .type(ToolType.AXE).durability(1314).miningSpeed(11.0f)
                    .attackDamage(9.5f).attackSpeed(-3.0f)
                    .enchantment(22)
                    .incorrectTag("minecraft:incorrect_for_netherite_tool")
                    .repairWith(() -> new ItemStack(PDItems.DYEDREAM_INGOT.get()))
                    .build();
    public static final DeferredItem<Item> DYEDREAM_SHOVEL =
            ItemAPI.toolItem("dyedream_shovel")
                    .type(ToolType.SHOVEL).durability(1314).miningSpeed(11.0f)
                    .attackDamage(5.5f).attackSpeed(-3.0f)
                    .enchantment(22)
                    .incorrectTag("minecraft:incorrect_for_netherite_tool")
                    .repairWith(() -> new ItemStack(PDItems.DYEDREAM_INGOT.get()))
                    .build();
    public static final DeferredItem<Item> DYEDREAM_HOE =
            ItemAPI.toolItem("dyedream_hoe")
                    .type(ToolType.HOE).durability(1314).miningSpeed(11.0f)
                    .attackDamage(1.0f).attackSpeed(0.0f)
                    .enchantment(22)
                    .incorrectTag("minecraft:incorrect_for_netherite_tool")
                    .repairWith(() -> new ItemStack(PDItems.DYEDREAM_INGOT.get()))
                    .build();

    // === Moltengold 工具 ===
    public static final DeferredItem<Item> MOLTENGOLD_AXE =
            ItemAPI.toolItem("moltengold_axe")
                    .type(ToolType.AXE).durability(251).miningSpeed(14.0f)
                    .attackDamage(8.0f).attackSpeed(-3.0f)
                    .enchantment(23)
                    .incorrectTag("minecraft:incorrect_for_stone_tool")
                    .repairWith(() -> new ItemStack(PDItems.MOLTENGOLD_INGOT.get()))
                    .build();
    public static final DeferredItem<Item> MOLTENGOLD_SHOVEL =
            ItemAPI.toolItem("moltengold_shovel")
                    .type(ToolType.SHOVEL).durability(251).miningSpeed(14.0f)
                    .attackDamage(3.5f).attackSpeed(-2.9f)
                    .enchantment(23)
                    .incorrectTag("minecraft:incorrect_for_stone_tool")
                    .repairWith(() -> new ItemStack(PDItems.MOLTENGOLD_INGOT.get()))
                    .build();
    public static final DeferredItem<Item> MOLTENGOLD_HOE =
            ItemAPI.toolItem("moltengold_hoe")
                    .type(ToolType.HOE).durability(251).miningSpeed(14.0f)
                    .attackDamage(0.0f).attackSpeed(-0.5f)
                    .enchantment(23)
                    .incorrectTag("minecraft:incorrect_for_stone_tool")
                    .repairWith(() -> new ItemStack(PDItems.MOLTENGOLD_INGOT.get()))
                    .build();

    // === Meltdream 工具（融梦修补：手持消耗融梦能量自动修复）===
    /**
     * 融梦水晶斧 (meltdream_axe) — 融梦修补
     * 改用自定义 {@link MeltdreamAxeItem}（属性与原 ItemAPI 注册逐项一致），
     * 补上原版融梦修补：手持时每 10 tick 消耗 0.01 融梦能量修复 1 耐久；
     * 铁砧修复材料为融梦水晶碎片（原版行为，取代铁锭）
     */
    public static final DeferredItem<Item> MELTDREAM_AXE =
            PDItems.ITEMS.register("meltdream_axe",
                    () -> new MeltdreamAxeItem(new Item.Properties()));
    /**
     * 融梦水晶锹 (meltdream_shovel) — 融梦修补
     * 改用自定义 {@link MeltdreamShovelItem}（属性与原 ItemAPI 注册逐项一致），
     * 补上原版融梦修补：手持时每 10 tick 消耗 0.01 融梦能量修复 1 耐久；
     * 铁砧修复材料为融梦水晶碎片（原版行为，取代铁锭）
     */
    public static final DeferredItem<Item> MELTDREAM_SHOVEL =
            PDItems.ITEMS.register("meltdream_shovel",
                    () -> new MeltdreamShovelItem(new Item.Properties()));
    /**
     * 融梦水晶锄 (meltdream_hoe) — 融梦修补
     * 改用自定义 {@link MeltdreamHoeItem}（属性与原 ItemAPI 注册逐项一致），
     * 补上原版融梦修补：手持时每 10 tick 消耗 0.01 融梦能量修复 1 耐久；
     * 铁砧修复材料为融梦水晶碎片（原版行为，取代铁锭）
     */
    public static final DeferredItem<Item> MELTDREAM_HOE =
            PDItems.ITEMS.register("meltdream_hoe",
                    () -> new MeltdreamHoeItem(new Item.Properties()));

    // === Shadow Erosion 工具 ===
    public static final DeferredItem<Item> SHADOW_EROSION_AXE =
            ItemAPI.toolItem("shadow_erosion_axe")
                    .type(ToolType.AXE).durability(1725).miningSpeed(13.0f)
                    .attackDamage(10.0f).attackSpeed(-3.0f)
                    .enchantment(16)
                    .incorrectTag("minecraft:incorrect_for_netherite_tool")
                    .repairWith(() -> new ItemStack(PDItems.BLACKMETAL_INGOT.get()))
                    .build();
    public static final DeferredItem<Item> SHADOW_EROSION_SHOVEL =
            ItemAPI.toolItem("shadow_erosion_shovel")
                    .type(ToolType.SHOVEL).durability(1725).miningSpeed(13.0f)
                    .attackDamage(6.0f).attackSpeed(-3.0f)
                    .enchantment(16)
                    .incorrectTag("minecraft:incorrect_for_netherite_tool")
                    .repairWith(() -> new ItemStack(PDItems.BLACKMETAL_INGOT.get()))
                    .build();
    public static final DeferredItem<Item> SHADOW_EROSION_HOE =
            ItemAPI.toolItem("shadow_erosion_hoe")
                    .type(ToolType.HOE).durability(1725).miningSpeed(13.0f)
                    .attackDamage(1.5f).attackSpeed(0.0f)
                    .enchantment(16)
                    .incorrectTag("minecraft:incorrect_for_netherite_tool")
                    .repairWith(() -> new ItemStack(PDItems.BLACKMETAL_INGOT.get()))
                    .build();

}
