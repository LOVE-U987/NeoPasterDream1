package net.pasterdream.jei.shadowblastfurnace;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.item.crafting.RecipeManager;
import net.pasterdream.init.PasterdreamModRecipeType;
import net.pasterdream.recipes.dark_smithing.ShadowBlastFurnaceRecipe;

import java.util.List;

public class DataRecipe {
    private final RecipeManager recipeManager;

    public DataRecipe() {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;

        if (level != null) {
            this.recipeManager = level.getRecipeManager();
        } else {
            throw new NullPointerException("minecraft world must not be null.");
        }
    }

    public List<ShadowBlastFurnaceRecipe> buildListofShadowBlastFurnaceRecipe() {
        return recipeManager.getAllRecipesFor(PasterdreamModRecipeType.SHADOW_BLAST_FURNACE.get()).stream().toList();
    }
}
