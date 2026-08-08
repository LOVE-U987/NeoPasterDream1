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
                // 原模组防御数组 {1,3,5,2} 按 [靴,腿,胸,头] 顺序 => 头2 胸5 腿3 靴1
                map.put(ArmorItem.Type.HELMET, 2);
                map.put(ArmorItem.Type.CHESTPLATE, 5);
                map.put(ArmorItem.Type.LEGGINGS, 3);
                map.put(ArmorItem.Type.BOOTS, 1);
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
                // 原模组防御数组 {3,6,8,3} 按 [靴,腿,胸,头] 顺序 => 头3 胸8 腿6 靴3
                map.put(ArmorItem.Type.HELMET, 3);
                map.put(ArmorItem.Type.CHESTPLATE, 8);
                map.put(ArmorItem.Type.LEGGINGS, 6);
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
                // 原模组防御数组 {3,6,8,3} 按 [靴,腿,胸,头] 顺序 => 头3 胸8 腿6 靴3
                map.put(ArmorItem.Type.HELMET, 3);
                map.put(ArmorItem.Type.CHESTPLATE, 8);
                map.put(ArmorItem.Type.LEGGINGS, 6);
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
                // 原模组防御数组 {3,6,8,3} 按 [靴,腿,胸,头] 顺序 => 头3 胸8 腿6 靴3
                map.put(ArmorItem.Type.HELMET, 3);
                map.put(ArmorItem.Type.CHESTPLATE, 8);
                map.put(ArmorItem.Type.LEGGINGS, 6);
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
                // 原模组防御数组 {2,10,10,10} 按 [靴,腿,胸,头] 顺序 => 头10 胸10 腿10 靴2
                map.put(ArmorItem.Type.HELMET, 10);
                map.put(ArmorItem.Type.CHESTPLATE, 10);
                map.put(ArmorItem.Type.LEGGINGS, 10);
                map.put(ArmorItem.Type.BOOTS, 2);
                map.put(ArmorItem.Type.BODY, 10);
            }),
            99,
            SoundEvents.ARMOR_EQUIP_DIAMOND,
            () -> Ingredient.of(MEMORY_GEMS),
            List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "qin"))),
            10f,
            1.0f
            ));

    /** 天使之翼材质（GeckoLib 接管贴图） */
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> ANGEL_WING = ARMOR_MATERIALS.register("angel_wing",
            () -> new ArmorMaterial(Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                map.put(ArmorItem.Type.HELMET, 2);
                map.put(ArmorItem.Type.CHESTPLATE, 6);
                map.put(ArmorItem.Type.LEGGINGS, 5);
                map.put(ArmorItem.Type.BOOTS, 2);
                map.put(ArmorItem.Type.BODY, 6);
            }),
            9,
            SoundEvents.ARMOR_EQUIP_ELYTRA,
            () -> Ingredient.of(Items.FEATHER),
            List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "angel_wing"))),
            2f,
            0f
            ));

    /** 遗落之翼材质（GeckoLib 接管贴图） */
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> FORSAKENS_WING = ARMOR_MATERIALS.register("forsakens_wing",
            () -> new ArmorMaterial(Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                map.put(ArmorItem.Type.HELMET, 2);
                map.put(ArmorItem.Type.CHESTPLATE, 7);
                map.put(ArmorItem.Type.LEGGINGS, 5);
                map.put(ArmorItem.Type.BOOTS, 2);
                map.put(ArmorItem.Type.BODY, 7);
            }),
            7,
            SoundEvents.ARMOR_EQUIP_ELYTRA,
            () -> Ingredient.of(com.pasterdream.pasterdreammod.registry.PDItems.NIGHTMARE_FUEL.get()),
            List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "forsakens_wing"))),
            3f,
            0f
            ));

    /** 机械之翼材质（GeckoLib 接管贴图） */
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> MACHINE_WING = ARMOR_MATERIALS.register("machine_wing",
            () -> new ArmorMaterial(Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                map.put(ArmorItem.Type.HELMET, 2);
                map.put(ArmorItem.Type.CHESTPLATE, 7);
                map.put(ArmorItem.Type.LEGGINGS, 5);
                map.put(ArmorItem.Type.BOOTS, 2);
                map.put(ArmorItem.Type.BODY, 7);
            }),
            9,
            SoundEvents.ARMOR_EQUIP_NETHERITE,
            () -> Ingredient.EMPTY,
            List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "machine_wing"))),
            1f,
            0f
            ));
}