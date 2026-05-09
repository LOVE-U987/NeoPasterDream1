package net.pasterdream.jei.weaponworkshop;

import com.google.common.collect.Lists;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.tags.ItemTags;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.pasterdream.init.PasterdreamModItems;


import java.util.List;

public class WeaponworkshopDataRecipe {
    Ingredient item1;
    Ingredient item2;
    Ingredient item3;
    Ingredient item4;
    Ingredient item5;
    Ingredient inlay;
    ItemStack output1;
    ItemStack output2;

    public WeaponworkshopDataRecipe(Ingredient item1, Ingredient item2, Ingredient item3, Ingredient item4, Ingredient item5, Ingredient inlay, ItemStack output1, ItemStack output2) {
        this.item1 = item1;
        this.item2 = item2;
        this.item3 = item3;
        this.item4 = item4;
        this.item5 = item5;

        this.inlay = inlay;
        this.output1 = output1;
        this.output2 = output2;
    }

    public static List<WeaponworkshopDataRecipe> build() {
        return Lists.newArrayList(
                create(PasterdreamModItems.TRUEST_MOLTENGOLD_SWORD.get(), PasterdreamModItems.TRUE_GRASS_SWORD.get(), PasterdreamModItems.TRUE_TIDE_SWORD.get(), PasterdreamModItems.TRUE_DESERT_SWORD.get(), PasterdreamModItems.BROKEN_HERO_SWORD.get(), Items.AIR, PasterdreamModItems.TERRASWORD_EMBRYO.get(), PasterdreamModItems.TERRA_SWORD.get()),
                create(PasterdreamModItems.MELTDREAM_CRYSTAL_0.get(), PasterdreamModItems.TITANIUM_INGOT.get(), PasterdreamModItems.BLACKSTICK.get(), PasterdreamModItems.DYEDREAM_DUST.get(), PasterdreamModItems.DYEDREAMQUARTZ.get(), Items.AIR, PasterdreamModItems.DREAM_WAND_EMBRYO.get(), PasterdreamModItems.DREAM_WAND.get()),
                create(new ItemStack(Items.IRON_SWORD,1), new ItemStack(PasterdreamModItems.BLACKMETAL_INGOT.get(),2), new ItemStack(PasterdreamModItems.NIGHTMARE_FUEL.get(),1), new ItemStack(Items.CRYING_OBSIDIAN,3), new ItemStack(PasterdreamModItems.BLACKSTICK.get(),1), Ingredient.of(ItemTags.create(new ResourceLocation("pasterdream:enhance_stone"))), new ItemStack(PasterdreamModItems.SHADOW_EROSION_SWORD_EMBRYO.get(),1), new ItemStack(PasterdreamModItems.SHADOW_EROSION_SWORD.get(),1)),
                create(new ItemStack(Items.IRON_PICKAXE,1), new ItemStack(PasterdreamModItems.BLACKMETAL_INGOT.get(),2), new ItemStack(PasterdreamModItems.NIGHTMARE_FUEL.get(),1), new ItemStack(Items.CRYING_OBSIDIAN,3), new ItemStack(PasterdreamModItems.BLACKSTICK.get(),1), Ingredient.of(ItemTags.create(new ResourceLocation("pasterdream:enhance_stone"))), new ItemStack(PasterdreamModItems.SHADOW_EROSION_PICKAXE_EMBRYO.get(),1), new ItemStack(PasterdreamModItems.SHADOW_EROSION_PICKAXE.get(),1)),
                create(new ItemStack(Items.IRON_AXE,1), new ItemStack(PasterdreamModItems.BLACKMETAL_INGOT.get(),2), new ItemStack(PasterdreamModItems.NIGHTMARE_FUEL.get(),1), new ItemStack(Items.CRYING_OBSIDIAN,3), new ItemStack(PasterdreamModItems.BLACKSTICK.get(),1), Ingredient.of(ItemTags.create(new ResourceLocation("pasterdream:enhance_stone"))), new ItemStack(PasterdreamModItems.SHADOW_EROSION_AXE_EMBRYO.get(),1), new ItemStack(PasterdreamModItems.SHADOW_EROSION_AXE.get(),1)),
                create(new ItemStack(Items.IRON_SHOVEL,1), new ItemStack(PasterdreamModItems.BLACKMETAL_INGOT.get(),2), new ItemStack(PasterdreamModItems.NIGHTMARE_FUEL.get(),1), new ItemStack(Items.CRYING_OBSIDIAN,3), new ItemStack(PasterdreamModItems.BLACKSTICK.get(),1), Ingredient.of(ItemTags.create(new ResourceLocation("pasterdream:enhance_stone"))), new ItemStack(PasterdreamModItems.SHADOW_EROSION_SHOVEL_EMBRYO.get(),1), new ItemStack(PasterdreamModItems.SHADOW_EROSION_SHOVEL.get(),1)),
                create(new ItemStack(Items.IRON_HOE,1), new ItemStack(PasterdreamModItems.BLACKMETAL_INGOT.get(),2), new ItemStack(PasterdreamModItems.NIGHTMARE_FUEL.get(),1), new ItemStack(Items.CRYING_OBSIDIAN,3), new ItemStack(PasterdreamModItems.BLACKSTICK.get(),1), Ingredient.of(ItemTags.create(new ResourceLocation("pasterdream:enhance_stone"))), new ItemStack(PasterdreamModItems.SHADOW_EROSION_HOE_EMBRYO.get(),1), new ItemStack(PasterdreamModItems.SHADOW_EROSION_HOE.get(),1)),
                create(new ItemStack(PasterdreamModItems.TITANIUM_SWORD.get(),1), new ItemStack(PasterdreamModItems.SHADOW_LIGHT_0.get(),9), new ItemStack(PasterdreamModItems.WHITE_CRYSTAL.get(),1), new ItemStack(Items.NETHER_STAR,1), new ItemStack(PasterdreamModItems.MELTDREAM_CRYSTAL_0.get(),1), Ingredient.of(ItemTags.create(new ResourceLocation("pasterdream:enhance_stone"))), new ItemStack(PasterdreamModItems.WHITE_SWORD_EMBRYO.get(),1), new ItemStack(PasterdreamModItems.WHITE_SWORD.get(),1)),
                create(new ItemStack(PasterdreamModItems.SHADOW_EROSION_SWORD.get(),1), new ItemStack(PasterdreamModItems.NIGHTMARE_FUEL.get(),17), new ItemStack(PasterdreamModItems.SHADOW_HILT.get(),1), new ItemStack(PasterdreamModItems.BLACKMETAL_INGOT.get(),1), new ItemStack(PasterdreamModItems.PERGAMYN.get(),3), Ingredient.of(ItemTags.create(new ResourceLocation("pasterdream:enhance_stone"))), new ItemStack(PasterdreamModItems.SHADOW_SWORD_EMBRYO.get(),1), new ItemStack(PasterdreamModItems.SHADOW_SWORD.get(),1)),
                create(new ItemStack(Items.FISHING_ROD,1), new ItemStack(Items.POWDER_SNOW_BUCKET,1), new ItemStack(Items.DIAMOND,1), new ItemStack(PasterdreamModItems.MELTDREAM_CRYSTAL_0.get(),1), new ItemStack(PasterdreamModItems.SNOW_VOW_HEAD.get(),1), Ingredient.of(ItemTags.create(new ResourceLocation("pasterdream:enhance_stone"))), new ItemStack(PasterdreamModItems.STAR_WISH_ROD_EMBRYO.get(),1), new ItemStack(PasterdreamModItems.STAR_WISH_ROD.get(),1)),
                create(new ItemStack(Items.BOOK,1), new ItemStack(PasterdreamModItems.NIGHTMARE_FUEL.get(),7), new ItemStack(PasterdreamModItems.BLACKMETAL_INGOT.get(),1), new ItemStack(PasterdreamModItems.PURE_HORROR.get(),1), new ItemStack(PasterdreamModItems.PEN_AND_INK.get(),1), new ItemStack(Items.AIR,1), new ItemStack(PasterdreamModItems.SHADOW_VORTEX_BOOK.get(),1), new ItemStack(Items.AIR,1)),
                create(new ItemStack(Items.DIAMOND,8), new ItemStack(Items.BLUE_ICE,1), new ItemStack(PasterdreamModItems.BLACKMETAL_INGOT.get(),2), new ItemStack(PasterdreamModItems.BLACKSTICK.get(),1), new ItemStack(PasterdreamModItems.PURE_HORROR.get(),1), Ingredient.of(ItemTags.create(new ResourceLocation("pasterdream:enhance_stone"))), new ItemStack(PasterdreamModItems.ICESHADOW_HAMMER_EMBRYO.get(),1), new ItemStack(PasterdreamModItems.ICESHADOW_HAMMER.get(),1)),
                create(new ItemStack(PasterdreamModItems.SILVER_BELL.get(),1), new ItemStack(PasterdreamModItems.DYEDREAM_INGOT.get(),1), new ItemStack(Items.STRING,5), new ItemStack(PasterdreamModItems.DYEDREAM_PLANKS.get(),5), new ItemStack(PasterdreamModItems.WHITE_CRYSTAL.get(),1), Ingredient.of(ItemTags.create(new ResourceLocation("pasterdream:enhance_stone"))), new ItemStack(PasterdreamModItems.STRAWBERRY_HEART.get(),1), new ItemStack(Items.AIR,1))

        );
    }

    private static WeaponworkshopDataRecipe create(Object ... items) {
        Ingredient[] ingredients = new Ingredient[6];
        for (int i = 0; i < 6; i ++) {
            if (items[i] instanceof Item item) ingredients[i] = Ingredient.of(item.getDefaultInstance());
            else if (items[i] instanceof ItemStack item) ingredients[i] = Ingredient.of(item);
            else if (items[i] instanceof Ingredient item) ingredients[i] = item;
        }
        var output = items[6] instanceof ItemStack item ? item : items[6] instanceof Item item ? item.getDefaultInstance() : ItemStack.EMPTY;
        var output2 = items[7] instanceof ItemStack item ? item : items[7] instanceof Item item ? item.getDefaultInstance() : ItemStack.EMPTY;
        return new WeaponworkshopDataRecipe(ingredients[0], ingredients[1], ingredients[2], ingredients[3], ingredients[4], ingredients[5], output, output2);
    }
}
