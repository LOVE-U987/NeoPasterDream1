package net.pasterdream.jei.claypot;

import com.google.common.collect.Lists;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.pasterdream.init.PasterdreamModItems;

import java.util.List;

public class ClaypotDataRecipe {
    ItemStack item1;
    ItemStack item2;
    ItemStack item3;

    public ClaypotDataRecipe(Item item1, Item item2, Item item3) {
        this.item1 = item1.getDefaultInstance();
        this.item2 = item2.getDefaultInstance();
        this.item3 = item3.getDefaultInstance();
    }

    public static List<ClaypotDataRecipe> build() {
        return Lists.newArrayList(
            new ClaypotDataRecipe(Items.WATER_BUCKET, PasterdreamModItems.CLAYPAN_0.get(), PasterdreamModItems.COARSE_SALT.get()),
            new ClaypotDataRecipe(Items.POTION, PasterdreamModItems.CLAYPAN_0.get(), PasterdreamModItems.COARSE_SALT.get()),
            new ClaypotDataRecipe(PasterdreamModItems.WATER_GLASSJAR.get(), PasterdreamModItems.CLAYPAN_0.get(), PasterdreamModItems.COARSE_SALT.get())
        );
    }

}
