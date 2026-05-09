package net.pasterdream.jei.weaponworkshop;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.pasterdream.PasterdreamMod;
import net.pasterdream.init.PasterdreamModItems;

public final class WeaponworkshopCategory implements IRecipeCategory<WeaponworkshopDataRecipe> {
    public static final RecipeType<WeaponworkshopDataRecipe> WEAPONWORKSHOP_DATA_RECIPE_RECIPE_TYPE = RecipeType.create(PasterdreamMod.MODID, "weaponworkshop", WeaponworkshopDataRecipe.class);
    private final IGuiHelper helper;

    public WeaponworkshopCategory(IGuiHelper helper) {
        this.helper = helper;
    }

    @Override
    public RecipeType<WeaponworkshopDataRecipe> getRecipeType() {
        return WEAPONWORKSHOP_DATA_RECIPE_RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.literal("精铸工坊");
    }

    @Override
    public IDrawable getBackground() {
        return helper.createDrawable(new ResourceLocation(PasterdreamMod.MODID, "textures/screens/weaponworkshop_jei.png"), 0, 0, 176,128);
    }

    @Override
    public IDrawable getIcon() {
        return helper.createDrawableItemStack(PasterdreamModItems.WEAPON_WORKSHOP.get().getDefaultInstance());
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, WeaponworkshopDataRecipe recipe, IFocusGroup group) {

        builder.addSlot(RecipeIngredientRole.INPUT, 6, 8)           .addIngredients(recipe.item1);
        builder.addSlot(RecipeIngredientRole.INPUT, 24, 8)          .addIngredients(recipe.item2);
        builder.addSlot(RecipeIngredientRole.INPUT, 42, 8)          .addIngredients(recipe.item3);
        builder.addSlot(RecipeIngredientRole.INPUT, 60, 8)          .addIngredients(recipe.item4);
        builder.addSlot(RecipeIngredientRole.INPUT, 78, 8)          .addIngredients(recipe.item5);
        builder.addSlot(RecipeIngredientRole.CATALYST, 132, 8)      .addIngredients(recipe.inlay);
        builder.addSlot(RecipeIngredientRole.OUTPUT, 132, 53)       .addItemStack(recipe.output1);
        builder.addSlot(RecipeIngredientRole.OUTPUT, 132,102)       .addItemStack(recipe.output2);
    }
}
