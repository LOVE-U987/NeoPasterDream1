package net.pasterdream.jei.shadowblastfurnace;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.pasterdream.PasterdreamMod;
import net.pasterdream.init.PasterdreamModItems;
import net.pasterdream.init.PasterdreamModRecipeType;
import net.pasterdream.recipes.dark_smithing.ShadowBlastFurnaceRecipe;
import net.pasterdream.utils.RecipeUtils;

import java.util.Arrays;

public final class BlastCategory implements IRecipeCategory<ShadowBlastFurnaceRecipe> {
    public static final RecipeType<ShadowBlastFurnaceRecipe> BLAST_DATA_RECIPE_RECIPE_TYPE = RecipeType.create(PasterdreamMod.MODID, "shadow_blasting", ShadowBlastFurnaceRecipe.class);
    private final IGuiHelper helper;

    public BlastCategory(IGuiHelper helper) {
        this.helper = helper;
    }

    @Override
    public RecipeType<ShadowBlastFurnaceRecipe> getRecipeType() {
        return BLAST_DATA_RECIPE_RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.literal("暗影高炉");
    }

    @Override
    public IDrawable getBackground() {
        // 渲染背景
        return helper.createDrawable(new ResourceLocation(PasterdreamMod.MODID, "textures/screens/shadow_blast_furnace_jei.png"), 0, 0, 128, 103);
    }

    @Override
    public IDrawable getIcon() {
        // 影响上方的贴图，可以渲染物品也可以渲染一个自定义的图片
        return helper.createDrawableItemStack(PasterdreamModItems.SHADOW_BLAST_FURNACE.get().getDefaultInstance());
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ShadowBlastFurnaceRecipe recipe, IFocusGroup group) {
        /**
         * INPUT: 原料
         * OUTPUT: 产出物语
         * CATALYST: 燃料
         * READONLY: 只读
         */
        ItemStack spendfuel = new ItemStack(PasterdreamModItems.NIGHTMARE_FUEL.get(),recipe.getSpendFuel());
        ItemStack spendfluidfuel = recipe.getSpendFluidFuel() > 0?new ItemStack(PasterdreamModItems.SHADOW_LIQUID_BUCKET.get()):ItemStack.EMPTY;
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 5).addItemStacks(Arrays.asList(recipe.getIngredients().get(0).getItems()));
        builder.addSlot(RecipeIngredientRole.CATALYST, 1, 50).addItemStack(spendfuel);
        builder.addSlot(RecipeIngredientRole.OUTPUT, 37, 86).addItemStack(RecipeUtils.getResultItem(recipe));
        builder.addSlot(RecipeIngredientRole.OUTPUT, 73, 86)
                .addItemStack(RecipeUtils.getByResultItem(recipe))
                .addTooltipCallback((iRecipeSlotView,list)->{
                    if(recipe.getByoutput_probability() > 0 && recipe.getByoutput_probability() < 1 && !iRecipeSlotView.isEmpty())
                    {
                        list.add(Component.translatable("jei.pasterdream.shadow_blast_furnace.probability",Math.floor(recipe.getByoutput_probability() * 100)).withStyle(ChatFormatting.GOLD));
                    }
                });
        builder.addSlot(RecipeIngredientRole.CATALYST, 109, 5)
                .addItemStack(spendfluidfuel)
                .addTooltipCallback((iRecipeSlotView,list)->{
                    list.add(Component.translatable("jei.pasterdream.shadow_blast_furnace.consumefluid",recipe.getSpendFluidFuel()).withStyle(ChatFormatting.GOLD));
                });
    }
}
