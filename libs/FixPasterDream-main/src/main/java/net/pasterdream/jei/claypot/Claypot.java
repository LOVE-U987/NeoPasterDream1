package net.pasterdream.jei.claypot;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.pasterdream.PasterdreamMod;
import net.pasterdream.init.PasterdreamModItems;

public final class Claypot implements IRecipeCategory<ClaypotDataRecipe> {
    public static final RecipeType<ClaypotDataRecipe> CLAYPOT_DATA_RECIPE_RECIPE_TYPE = RecipeType.create(PasterdreamMod.MODID, "slatpot", ClaypotDataRecipe.class);
    private final IGuiHelper helper;

    public Claypot(IGuiHelper helper) {
        this.helper = helper;
    }

    @Override
    public RecipeType<ClaypotDataRecipe> getRecipeType() {
        return CLAYPOT_DATA_RECIPE_RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.literal("陶盆");
    }

    @Override
    public IDrawable getBackground() {
        return helper.createDrawable(new ResourceLocation(PasterdreamMod.MODID, "textures/screens/clay_pot_jei.png"), 0, 0, 128, 64);
    }

    @Override
    public IDrawable getIcon() {
        return helper.createDrawableItemStack(PasterdreamModItems.CLAYPAN_0.get().getDefaultInstance());
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ClaypotDataRecipe recipe, IFocusGroup group) {

        builder.addSlot(RecipeIngredientRole.INPUT, 16, 24)          .addItemStack(recipe.item1);
        builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 52, 24)    .addItemStack(recipe.item2);
        builder.addSlot(RecipeIngredientRole.OUTPUT, 88, 24)         .addItemStack(recipe.item3);

    }
}
