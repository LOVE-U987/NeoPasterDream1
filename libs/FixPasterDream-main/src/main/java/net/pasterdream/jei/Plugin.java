package net.pasterdream.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.pasterdream.PasterdreamMod;
import net.pasterdream.init.PasterdreamModItems;

import net.pasterdream.jei.dreamcauldron.DreamCauldron;
import net.pasterdream.jei.dreamcauldron.DreamCauldronRecipe;
import net.pasterdream.jei.shadowblastfurnace.BlastCategory;
import net.pasterdream.jei.shadowblastfurnace.DataRecipe;
import net.pasterdream.jei.weaponworkshop.WeaponworkshopCategory;
import net.pasterdream.jei.weaponworkshop.WeaponworkshopDataRecipe;
import net.pasterdream.jei.claypot.Claypot;
import net.pasterdream.jei.claypot.ClaypotDataRecipe;

@JeiPlugin
public class Plugin implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(PasterdreamMod.MODID, "pasterdream.jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        var helper = registration.getJeiHelpers().getGuiHelper();
        // 向Jei注册我们的功能页面
        registration.addRecipeCategories(new BlastCategory(helper));
        registration.addRecipeCategories(new WeaponworkshopCategory(helper));
        registration.addRecipeCategories(new Claypot(helper));
        registration.addRecipeCategories(new DreamCauldron(helper));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        // 登记食谱，新增食谱到 BlastDataRecipe.build 中为列表新增内容，这个地方无需改动
        DataRecipe dataRecipe = new DataRecipe();
        registration.addRecipes(BlastCategory.BLAST_DATA_RECIPE_RECIPE_TYPE, dataRecipe.buildListofShadowBlastFurnaceRecipe());
        registration.addRecipes(WeaponworkshopCategory.WEAPONWORKSHOP_DATA_RECIPE_RECIPE_TYPE, WeaponworkshopDataRecipe.build());
        registration.addRecipes(Claypot.CLAYPOT_DATA_RECIPE_RECIPE_TYPE, ClaypotDataRecipe.build());
        registration.addRecipes(DreamCauldron.DREAM_CAULDRON_RECIPE_RECIPE_TYPE, DreamCauldronRecipe.build());
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        // 这将会影响左侧显示的功能方块，可以有多个
        registration.addRecipeCatalyst(PasterdreamModItems.SHADOW_BLAST_FURNACE.get().getDefaultInstance(), BlastCategory.BLAST_DATA_RECIPE_RECIPE_TYPE);
        registration.addRecipeCatalyst(PasterdreamModItems.WEAPON_WORKSHOP.get().getDefaultInstance(), WeaponworkshopCategory.WEAPONWORKSHOP_DATA_RECIPE_RECIPE_TYPE);
        registration.addRecipeCatalyst(PasterdreamModItems.CLAYPAN_0.get().getDefaultInstance(), Claypot.CLAYPOT_DATA_RECIPE_RECIPE_TYPE);
        registration.addRecipeCatalyst(PasterdreamModItems.DREAM_CAULDRON.get().getDefaultInstance(), DreamCauldron.DREAM_CAULDRON_RECIPE_RECIPE_TYPE);
    }
}
