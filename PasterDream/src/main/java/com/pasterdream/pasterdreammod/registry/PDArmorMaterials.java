package com.pasterdream.pasterdreammod.registry;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.EnumMap;
import java.util.List;

public class PDArmorMaterials {

    public static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS = 
            DeferredRegister.create(BuiltInRegistries.ARMOR_MATERIAL, PasterDreamMod.MOD_ID);

    public static final TagKey<Item> INGOTS_TITANIUM = TagKey.create(BuiltInRegistries.ITEM.key(), 
            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "ingots_titanium"));
    public static final TagKey<Item> SCULK_HEARTS = TagKey.create(BuiltInRegistries.ITEM.key(), 
            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "sculk_hearts"));
    public static final TagKey<Item> DYEDREAM_INGOTS = TagKey.create(BuiltInRegistries.ITEM.key(), 
            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "dyedream_ingots"));
    public static final TagKey<Item> MEMORY_GEMS = TagKey.create(BuiltInRegistries.ITEM.key(), 
            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "memory_gems"));

    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> COPPER_ARMOR = ARMOR_MATERIALS.register("copper_armor",
            () -> new ArmorMaterial(Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                map.put(ArmorItem.Type.HELMET, 1);
                map.put(ArmorItem.Type.CHESTPLATE, 3);
                map.put(ArmorItem.Type.LEGGINGS, 5);
                map.put(ArmorItem.Type.BOOTS, 2);
                map.put(ArmorItem.Type.BODY, 3);
            }),
            8,
            SoundEvents.ARMOR_EQUIP_IRON,
            () -> Ingredient.of(Items.COPPER_INGOT),
            List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "copper"))),
            0f,
            0f
            ));

    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> TITANIUM_ARMOR = ARMOR_MATERIALS.register("titanium_armor",
            () -> new ArmorMaterial(Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                map.put(ArmorItem.Type.HELMET, 3);
                map.put(ArmorItem.Type.CHESTPLATE, 6);
                map.put(ArmorItem.Type.LEGGINGS, 8);
                map.put(ArmorItem.Type.BOOTS, 3);
                map.put(ArmorItem.Type.BODY, 6);
            }),
            17,
            SoundEvents.ARMOR_EQUIP_DIAMOND,
            () -> Ingredient.of(INGOTS_TITANIUM),
            List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "titanium"))),
            3f,
            0.1f
            ));

    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> SCULK_ARMOR = ARMOR_MATERIALS.register("sculk_armor",
            () -> new ArmorMaterial(Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                map.put(ArmorItem.Type.HELMET, 3);
                map.put(ArmorItem.Type.CHESTPLATE, 6);
                map.put(ArmorItem.Type.LEGGINGS, 8);
                map.put(ArmorItem.Type.BOOTS, 3);
                map.put(ArmorItem.Type.BODY, 6);
            }),
            9,
            SoundEvents.ARMOR_EQUIP_GENERIC,
            () -> Ingredient.of(SCULK_HEARTS),
            List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "sculk"))),
            3.5f,
            0.15f
            ));

    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> DYEDREAM_ARMOR = ARMOR_MATERIALS.register("dyedream_armor",
            () -> new ArmorMaterial(Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                map.put(ArmorItem.Type.HELMET, 3);
                map.put(ArmorItem.Type.CHESTPLATE, 6);
                map.put(ArmorItem.Type.LEGGINGS, 8);
                map.put(ArmorItem.Type.BOOTS, 3);
                map.put(ArmorItem.Type.BODY, 6);
            }),
            22,
            SoundEvents.ARMOR_EQUIP_DIAMOND,
            () -> Ingredient.of(DYEDREAM_INGOTS),
            List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "dyedream"))),
            3f,
            0.1f
            ));

    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> QIN_ARMOR = ARMOR_MATERIALS.register("qin",
            () -> new ArmorMaterial(Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                map.put(ArmorItem.Type.HELMET, 2);
                map.put(ArmorItem.Type.CHESTPLATE, 10);
                map.put(ArmorItem.Type.LEGGINGS, 10);
                map.put(ArmorItem.Type.BOOTS, 10);
                map.put(ArmorItem.Type.BODY, 10);
            }),
            99,
            SoundEvents.ARMOR_EQUIP_DIAMOND,
            () -> Ingredient.of(MEMORY_GEMS),
            List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "qin"))),
            10f,
            1.0f
            ));
}