package net.pasterdream.jei.dreamcauldron;

import com.google.common.collect.Lists;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.tags.ItemTags;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.pasterdream.init.PasterdreamModItems;

import java.util.List;

public class DreamCauldronRecipe {
    ItemStack input1;
    ItemStack input2;
    ItemStack input3;
    ItemStack input4;
    ItemStack input5;
    ItemStack output1;
    ItemStack output2;

    public DreamCauldronRecipe(Item input1, Item input2, Item input3, Item input4, Item input5, Item output1, Item output2) {
        this.input1 = input1.getDefaultInstance();
        this.input2 = input2.getDefaultInstance();
        this.input3 = input3.getDefaultInstance();
        this.input4 = input4.getDefaultInstance();
        this.input5 = input5.getDefaultInstance();
        this.output1 = output1.getDefaultInstance();
        this.output2 = output2.getDefaultInstance();
    }

    public static List<DreamCauldronRecipe> build() {
        return Lists.newArrayList(
            new DreamCauldronRecipe(PasterdreamModItems.GUIDING_DRUG.get(), Items.CORNFLOWER, Items.REDSTONE, PasterdreamModItems.DARK_CLOUD.get(), PasterdreamModItems.MELTDREAM_LIQUID_BUCKET.get(), Items.BUCKET, PasterdreamModItems.LIGHTNING_SPELL.get()),
            new DreamCauldronRecipe(PasterdreamModItems.GUIDING_DRUG.get(), PasterdreamModItems.FLOWER_2.get(), Items.SPIDER_EYE, Items.POISONOUS_POTATO, PasterdreamModItems.MELTDREAM_LIQUID_BUCKET.get(), Items.BUCKET, PasterdreamModItems.POISON_SPELL.get()),
            new DreamCauldronRecipe(PasterdreamModItems.GUIDING_DRUG.get(), Items.GOLDEN_APPLE, Items.GLISTERING_MELON_SLICE, Items.SUNFLOWER, PasterdreamModItems.MELTDREAM_LIQUID_BUCKET.get(), Items.BUCKET, PasterdreamModItems.HEALING_SPELL.get()),
            new DreamCauldronRecipe(PasterdreamModItems.GUIDING_DRUG.get(), Items.ALLIUM, Items.DRAGON_BREATH, Items.AMETHYST_SHARD, PasterdreamModItems.MELTDREAM_LIQUID_BUCKET.get(), Items.BUCKET, PasterdreamModItems.FURY_SPELL.get()),
            new DreamCauldronRecipe(PasterdreamModItems.GUIDING_DRUG.get(), Items.BLUE_ORCHID, Items.SNOWBALL, PasterdreamModItems.ICE_BUD_0.get(), PasterdreamModItems.MELTDREAM_LIQUID_BUCKET.get(), Items.BUCKET, PasterdreamModItems.ICE_SPELL.get())
            );
    }

}
