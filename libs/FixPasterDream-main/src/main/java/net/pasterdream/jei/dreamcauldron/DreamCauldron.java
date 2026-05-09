package net.pasterdream.jei.dreamcauldron;

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

public final class DreamCauldron implements IRecipeCategory<DreamCauldronRecipe> {
    public static final RecipeType<DreamCauldronRecipe> DREAM_CAULDRON_RECIPE_RECIPE_TYPE = RecipeType.create(PasterdreamMod.MODID, "dream_cauldron", DreamCauldronRecipe.class);
    private final IGuiHelper helper;

    public DreamCauldron(IGuiHelper helper) {
        this.helper = helper;
    }

    @Override
    public RecipeType<DreamCauldronRecipe> getRecipeType() {
        return DREAM_CAULDRON_RECIPE_RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.literal("法术工厂");
    }

    @Override
    public IDrawable getBackground() {
        return helper.createDrawable(new ResourceLocation(PasterdreamMod.MODID, "textures/screens/dream_cauldron_gui_jei.png"), 0, 0, 192, 112);
    }

    @Override
    public IDrawable getIcon() {
        return helper.createDrawableItemStack(PasterdreamModItems.DREAM_CAULDRON.get().getDefaultInstance());
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, DreamCauldronRecipe recipe, IFocusGroup group) {

        builder.addSlot(RecipeIngredientRole.INPUT, 61, 19)          .addItemStack(recipe.input2);
        builder.addSlot(RecipeIngredientRole.INPUT, 89, 19)          .addItemStack(recipe.input3);
        builder.addSlot(RecipeIngredientRole.INPUT, 117, 19)         .addItemStack(recipe.input4);
        builder.addSlot(RecipeIngredientRole.INPUT, 17, 50)          .addItemStack(recipe.input1);
        builder.addSlot(RecipeIngredientRole.CATALYST, 170, 23)      .addItemStack(recipe.input5);
        builder.addSlot(RecipeIngredientRole.OUTPUT, 89, 50)         .addItemStack(recipe.output2);
        builder.addSlot(RecipeIngredientRole.OUTPUT, 170, 77)        .addItemStack(recipe.output1);

    }
}
