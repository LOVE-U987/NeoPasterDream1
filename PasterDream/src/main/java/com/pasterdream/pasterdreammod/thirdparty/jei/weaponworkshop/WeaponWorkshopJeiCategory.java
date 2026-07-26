package com.pasterdream.pasterdreammod.thirdparty.jei.weaponworkshop;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.registry.PDItems;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

/**
 * 精铸工坊（武器工坊）JEI 配方分类。
 * <p>
 * 移植自原版 {@code WeaponworkshopCategory}：背景 176×128，
 * 5 输入 + 镶嵌物 + 胚体/成品双产出槽位。
 */
public final class WeaponWorkshopJeiCategory implements IRecipeCategory<WeaponWorkshopJeiRecipe> {

    public static final RecipeType<WeaponWorkshopJeiRecipe> WEAPON_WORKSHOP_JEI_RECIPE_TYPE =
            RecipeType.create(PasterDreamMod.MOD_ID, "weaponworkshop", WeaponWorkshopJeiRecipe.class);

    private static final ResourceLocation BACKGROUND_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "textures/screens/weaponworkshop_jei.png");
    private static final int WIDTH = 176;
    private static final int HEIGHT = 128;

    private final IDrawableStatic background;
    private final IDrawable icon;

    public WeaponWorkshopJeiCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(BACKGROUND_TEXTURE, 0, 0, WIDTH, HEIGHT);
        this.icon = helper.createDrawableItemStack(PDItems.WEAPON_WORKSHOP.get().getDefaultInstance());
    }

    @Override
    public RecipeType<WeaponWorkshopJeiRecipe> getRecipeType() {
        return WEAPON_WORKSHOP_JEI_RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatableWithFallback("jei.pasterdream.category.weapon_workshop", "精铸工坊");
    }

    @Override
    public int getWidth() {
        return WIDTH;
    }

    @Override
    public int getHeight() {
        return HEIGHT;
    }

    @Override
    public void draw(WeaponWorkshopJeiRecipe recipe, IRecipeSlotsView recipeSlotsView,
                     GuiGraphics guiGraphics, double mouseX, double mouseY) {
        background.draw(guiGraphics);
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, WeaponWorkshopJeiRecipe recipe, IFocusGroup focuses) {
        // 坐标与原版一致
        builder.addSlot(RecipeIngredientRole.INPUT, 6, 8).addIngredients(recipe.item1);
        builder.addSlot(RecipeIngredientRole.INPUT, 24, 8).addIngredients(recipe.item2);
        builder.addSlot(RecipeIngredientRole.INPUT, 42, 8).addIngredients(recipe.item3);
        builder.addSlot(RecipeIngredientRole.INPUT, 60, 8).addIngredients(recipe.item4);
        builder.addSlot(RecipeIngredientRole.INPUT, 78, 8).addIngredients(recipe.item5);
        builder.addSlot(RecipeIngredientRole.CATALYST, 132, 8).addIngredients(recipe.inlay);
        if (!recipe.output1.isEmpty()) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 132, 53).addItemStack(recipe.output1);
        } else {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 132, 53).addItemStack(ItemStack.EMPTY);
        }
        if (!recipe.output2.isEmpty()) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 132, 102).addItemStack(recipe.output2);
        } else {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 132, 102).addItemStack(ItemStack.EMPTY);
        }
    }
}
