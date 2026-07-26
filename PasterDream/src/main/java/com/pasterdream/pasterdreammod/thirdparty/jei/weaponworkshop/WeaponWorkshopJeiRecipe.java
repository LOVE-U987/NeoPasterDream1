package com.pasterdream.pasterdreammod.thirdparty.jei.weaponworkshop;

import com.pasterdream.pasterdreammod.registry.PDItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.List;

/**
 * 精铸工坊 JEI 展示配方数据。
 * <p>
 * 移植自原版 {@code WeaponworkshopDataRecipe}，13 组硬编码配方：
 * 5 输入 + 1 镶嵌物 + 胚体/成品双产出。
 */
public class WeaponWorkshopJeiRecipe {

    final Ingredient item1;
    final Ingredient item2;
    final Ingredient item3;
    final Ingredient item4;
    final Ingredient item5;
    final Ingredient inlay;
    final ItemStack output1;
    final ItemStack output2;

    public WeaponWorkshopJeiRecipe(Ingredient item1, Ingredient item2, Ingredient item3,
                                   Ingredient item4, Ingredient item5, Ingredient inlay,
                                   ItemStack output1, ItemStack output2) {
        this.item1 = item1;
        this.item2 = item2;
        this.item3 = item3;
        this.item4 = item4;
        this.item5 = item5;
        this.inlay = inlay;
        this.output1 = output1;
        this.output2 = output2;
    }

    private static final TagKey<Item> ENHANCE_STONE = TagKey.create(Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath("pasterdream", "enhance_stone"));

    /** 构建原版全部 13 组精铸工坊配方 */
    public static List<WeaponWorkshopJeiRecipe> build() {
        Ingredient enhance = Ingredient.of(ENHANCE_STONE);
        Ingredient air = Ingredient.of(Items.AIR);
        List<WeaponWorkshopJeiRecipe> list = new ArrayList<>(13);

        // 1 泰拉之剑
        list.add(create(
                PDItems.TRUEST_MOLTENGOLD_SWORD.get(),
                PDItems.TRUE_GRASS_SWORD.get(),
                PDItems.TRUE_TIDE_SWORD.get(),
                PDItems.TRUE_DESERT_SWORD.get(),
                PDItems.BROKEN_HERO_SWORD.get(),
                air,
                stack(PDItems.TERRASWORD_EMBRYO.get()),
                stack(PDItems.TERRA_SWORD.get())));

        // 2 梦境法杖
        list.add(create(
                PDItems.MELTDREAM_CRYSTAL_0.get(),
                PDItems.TITANIUM_INGOT.get(),
                PDItems.BLACKSTICK.get(),
                PDItems.DYEDREAM_DUST.get(),
                PDItems.DYEDREAMQUARTZ.get(),
                air,
                stack(PDItems.DREAM_WAND_EMBRYO.get()),
                stack(PDItems.DREAM_WAND.get())));

        // 3-7 暗影侵蚀工具族
        list.add(create(
                stack(Items.IRON_SWORD, 1),
                stack(PDItems.BLACKMETAL_INGOT.get(), 2),
                stack(PDItems.NIGHTMARE_FUEL.get(), 1),
                stack(Items.CRYING_OBSIDIAN, 3),
                stack(PDItems.BLACKSTICK.get(), 1),
                enhance,
                stack(PDItems.SHADOW_EROSION_SWORD_EMBRYO.get()),
                stack(PDItems.SHADOW_EROSION_SWORD.get())));
        list.add(create(
                stack(Items.IRON_PICKAXE, 1),
                stack(PDItems.BLACKMETAL_INGOT.get(), 2),
                stack(PDItems.NIGHTMARE_FUEL.get(), 1),
                stack(Items.CRYING_OBSIDIAN, 3),
                stack(PDItems.BLACKSTICK.get(), 1),
                enhance,
                stack(PDItems.SHADOW_EROSION_PICKAXE_EMBRYO.get()),
                stack(PDItems.SHADOW_EROSION_PICKAXE.get())));
        list.add(create(
                stack(Items.IRON_AXE, 1),
                stack(PDItems.BLACKMETAL_INGOT.get(), 2),
                stack(PDItems.NIGHTMARE_FUEL.get(), 1),
                stack(Items.CRYING_OBSIDIAN, 3),
                stack(PDItems.BLACKSTICK.get(), 1),
                enhance,
                stack(PDItems.SHADOW_EROSION_AXE_EMBRYO.get()),
                stack(PDItems.SHADOW_EROSION_AXE.get())));
        list.add(create(
                stack(Items.IRON_SHOVEL, 1),
                stack(PDItems.BLACKMETAL_INGOT.get(), 2),
                stack(PDItems.NIGHTMARE_FUEL.get(), 1),
                stack(Items.CRYING_OBSIDIAN, 3),
                stack(PDItems.BLACKSTICK.get(), 1),
                enhance,
                stack(PDItems.SHADOW_EROSION_SHOVEL_EMBRYO.get()),
                stack(PDItems.SHADOW_EROSION_SHOVEL.get())));
        list.add(create(
                stack(Items.IRON_HOE, 1),
                stack(PDItems.BLACKMETAL_INGOT.get(), 2),
                stack(PDItems.NIGHTMARE_FUEL.get(), 1),
                stack(Items.CRYING_OBSIDIAN, 3),
                stack(PDItems.BLACKSTICK.get(), 1),
                enhance,
                stack(PDItems.SHADOW_EROSION_HOE_EMBRYO.get()),
                stack(PDItems.SHADOW_EROSION_HOE.get())));

        // 8 白剑
        list.add(create(
                stack(PDItems.TITANIUM_SWORD.get(), 1),
                stack(PDItems.SHADOW_LIGHT_0.get(), 9),
                stack(PDItems.WHITE_CRYSTAL.get(), 1),
                stack(Items.NETHER_STAR, 1),
                stack(PDItems.MELTDREAM_CRYSTAL_0.get(), 1),
                enhance,
                stack(PDItems.WHITE_SWORD_EMBRYO.get()),
                stack(PDItems.WHITE_SWORD.get())));

        // 9 暗影之剑
        list.add(create(
                stack(PDItems.SHADOW_EROSION_SWORD.get(), 1),
                stack(PDItems.NIGHTMARE_FUEL.get(), 17),
                stack(PDItems.SHADOW_HILT.get(), 1),
                stack(PDItems.BLACKMETAL_INGOT.get(), 1),
                stack(PDItems.PERGAMYN.get(), 3),
                enhance,
                stack(PDItems.SHADOW_SWORD_EMBRYO.get()),
                stack(PDItems.SHADOW_SWORD.get())));

        // 10 星愿钓竿
        list.add(create(
                stack(Items.FISHING_ROD, 1),
                stack(Items.POWDER_SNOW_BUCKET, 1),
                stack(Items.DIAMOND, 1),
                stack(PDItems.MELTDREAM_CRYSTAL_0.get(), 1),
                stack(PDItems.SNOW_VOW_HEAD.get(), 1),
                enhance,
                stack(PDItems.STAR_WISH_ROD_EMBRYO.get()),
                stack(PDItems.STAR_WISH_ROD.get())));

        // 11 暗影漩涡之书（无第二产出）
        list.add(create(
                stack(Items.BOOK, 1),
                stack(PDItems.NIGHTMARE_FUEL.get(), 7),
                stack(PDItems.BLACKMETAL_INGOT.get(), 1),
                stack(PDItems.PURE_HORROR.get(), 1),
                stack(PDItems.PEN_AND_INK.get(), 1),
                air,
                stack(PDItems.SHADOW_VORTEX_BOOK.get()),
                ItemStack.EMPTY));

        // 12 冰影锤
        list.add(create(
                stack(Items.DIAMOND, 8),
                stack(Items.BLUE_ICE, 1),
                stack(PDItems.BLACKMETAL_INGOT.get(), 2),
                stack(PDItems.BLACKSTICK.get(), 1),
                stack(PDItems.PURE_HORROR.get(), 1),
                enhance,
                stack(PDItems.ICESHADOW_HAMMER_EMBRYO.get()),
                stack(PDItems.ICESHADOW_HAMMER.get())));

        // 13 草莓之心
        list.add(create(
                stack(PDItems.SILVER_BELL.get(), 1),
                stack(PDItems.DYEDREAM_INGOT.get(), 1),
                stack(Items.STRING, 5),
                stack(PDItems.DYEDREAM_PLANKS.get(), 5),
                stack(PDItems.WHITE_CRYSTAL.get(), 1),
                enhance,
                stack(PDItems.STRAWBERRY_HEART.get()),
                ItemStack.EMPTY));

        return list;
    }

    private static ItemStack stack(Item item) {
        return item.getDefaultInstance();
    }

    private static ItemStack stack(Item item, int count) {
        return new ItemStack(item, count);
    }

    private static WeaponWorkshopJeiRecipe create(Object a, Object b, Object c, Object d, Object e,
                                                   Object inlay, ItemStack out1, ItemStack out2) {
        return new WeaponWorkshopJeiRecipe(
                toIngredient(a), toIngredient(b), toIngredient(c),
                toIngredient(d), toIngredient(e), toIngredient(inlay),
                out1, out2);
    }

    private static Ingredient toIngredient(Object o) {
        if (o instanceof Ingredient ingredient) {
            return ingredient;
        }
        if (o instanceof Item item) {
            return Ingredient.of(item);
        }
        if (o instanceof ItemStack stack) {
            return Ingredient.of(stack);
        }
        return Ingredient.EMPTY;
    }
}
