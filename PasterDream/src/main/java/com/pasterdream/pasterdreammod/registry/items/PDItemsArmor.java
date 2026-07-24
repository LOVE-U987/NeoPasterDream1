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
 * 盔甲套装注册。
 *
 * @see PDItems
 */
public class PDItemsArmor {


    // ==================== 盔甲套装 ====================

    // === Copper Armor (铜盔甲) ===
    public static final DeferredItem<Item> COPPER_ARMOR_HELMET = PDItems.ITEMS.register("copper_armor_helmet",
            () -> new com.pasterdream.pasterdreammod.item.CopperArmorItem(ArmorItem.Type.HELMET));
    public static final DeferredItem<Item> COPPER_ARMOR_CHESTPLATE = PDItems.ITEMS.register("copper_armor_chestplate",
            () -> new com.pasterdream.pasterdreammod.item.CopperArmorItem(ArmorItem.Type.CHESTPLATE));
    public static final DeferredItem<Item> COPPER_ARMOR_LEGGINGS = PDItems.ITEMS.register("copper_armor_leggings",
            () -> new com.pasterdream.pasterdreammod.item.CopperArmorItem(ArmorItem.Type.LEGGINGS));
    public static final DeferredItem<Item> COPPER_ARMOR_BOOTS = PDItems.ITEMS.register("copper_armor_boots",
            () -> new com.pasterdream.pasterdreammod.item.CopperArmorItem(ArmorItem.Type.BOOTS));

    // === Titanium Armor (钛盔甲) ===
    public static final DeferredItem<Item> TITANIUM_ARMOR_HELMET = PDItems.ITEMS.register("titanium_armor_helmet",
            () -> new com.pasterdream.pasterdreammod.item.TitaniumArmorItem(ArmorItem.Type.HELMET));
    public static final DeferredItem<Item> TITANIUM_ARMOR_CHESTPLATE = PDItems.ITEMS.register("titanium_armor_chestplate",
            () -> new com.pasterdream.pasterdreammod.item.TitaniumArmorItem(ArmorItem.Type.CHESTPLATE));
    public static final DeferredItem<Item> TITANIUM_ARMOR_LEGGINGS = PDItems.ITEMS.register("titanium_armor_leggings",
            () -> new com.pasterdream.pasterdreammod.item.TitaniumArmorItem(ArmorItem.Type.LEGGINGS));
    public static final DeferredItem<Item> TITANIUM_ARMOR_BOOTS = PDItems.ITEMS.register("titanium_armor_boots",
            () -> new com.pasterdream.pasterdreammod.item.TitaniumArmorItem(ArmorItem.Type.BOOTS));

    // === Sculk Armor (潜声盔甲) ===
    public static final DeferredItem<Item> SCULK_ARMOR_HELMET = PDItems.ITEMS.register("sculk_armor_helmet",
            () -> new com.pasterdream.pasterdreammod.item.SculkArmorItem(ArmorItem.Type.HELMET));
    public static final DeferredItem<Item> SCULK_ARMOR_CHESTPLATE = PDItems.ITEMS.register("sculk_armor_chestplate",
            () -> new com.pasterdream.pasterdreammod.item.SculkArmorItem(ArmorItem.Type.CHESTPLATE));
    public static final DeferredItem<Item> SCULK_ARMOR_LEGGINGS = PDItems.ITEMS.register("sculk_armor_leggings",
            () -> new com.pasterdream.pasterdreammod.item.SculkArmorItem(ArmorItem.Type.LEGGINGS));
    public static final DeferredItem<Item> SCULK_ARMOR_BOOTS = PDItems.ITEMS.register("sculk_armor_boots",
            () -> new com.pasterdream.pasterdreammod.item.SculkArmorItem(ArmorItem.Type.BOOTS));

    // === Dyedream Armor (染梦盔甲) ===
    public static final DeferredItem<Item> DYEDREAM_ARMOR_HELMET = PDItems.ITEMS.register("dyedream_armor_helmet",
            () -> new com.pasterdream.pasterdreammod.item.DyedreamArmorItem(ArmorItem.Type.HELMET));
    public static final DeferredItem<Item> DYEDREAM_ARMOR_CHESTPLATE = PDItems.ITEMS.register("dyedream_armor_chestplate",
            () -> new com.pasterdream.pasterdreammod.item.DyedreamArmorItem(ArmorItem.Type.CHESTPLATE));
    public static final DeferredItem<Item> DYEDREAM_ARMOR_LEGGINGS = PDItems.ITEMS.register("dyedream_armor_leggings",
            () -> new com.pasterdream.pasterdreammod.item.DyedreamArmorItem(ArmorItem.Type.LEGGINGS));
    public static final DeferredItem<Item> DYEDREAM_ARMOR_BOOTS = PDItems.ITEMS.register("dyedream_armor_boots",
            () -> new com.pasterdream.pasterdreammod.item.DyedreamArmorItem(ArmorItem.Type.BOOTS));

    // === Qin Armor (Qin盔甲) ===
    public static final DeferredItem<Item> QIN_ARMOR_HELMET = PDItems.ITEMS.register("qin_armor_helmet",
            () -> new com.pasterdream.pasterdreammod.item.QymArmorItem(ArmorItem.Type.HELMET));
    public static final DeferredItem<Item> QIN_ARMOR_CHESTPLATE = PDItems.ITEMS.register("qin_armor_chestplate",
            () -> new com.pasterdream.pasterdreammod.item.QymArmorItem(ArmorItem.Type.CHESTPLATE));
    public static final DeferredItem<Item> QIN_ARMOR_LEGGINGS = PDItems.ITEMS.register("qin_armor_leggings",
            () -> new com.pasterdream.pasterdreammod.item.QymArmorItem(ArmorItem.Type.LEGGINGS));
    public static final DeferredItem<Item> QIN_ARMOR_BOOTS = PDItems.ITEMS.register("qin_armor_boots",
            () -> new com.pasterdream.pasterdreammod.item.QymArmorItem(ArmorItem.Type.BOOTS));

}
