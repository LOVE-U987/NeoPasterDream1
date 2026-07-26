package com.pasterdream.pasterdreammod.thirdparty.jei;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.registry.PDItems;
import com.pasterdream.pasterdreammod.registry.PDRecipeTypes;
import com.pasterdream.pasterdreammod.thirdparty.jei.claypot.ClaypotJeiCategory;
import com.pasterdream.pasterdreammod.thirdparty.jei.claypot.ClaypotJeiRecipe;
import com.pasterdream.pasterdreammod.thirdparty.jei.dreamcauldron.DreamCauldronJeiCategory;
import com.pasterdream.pasterdreammod.thirdparty.jei.dreamcauldron.DreamCauldronJeiRecipe;
import com.pasterdream.pasterdreammod.thirdparty.jei.shadowblastfurnace.ShadowBlastFurnaceJeiCategory;
import com.pasterdream.pasterdreammod.thirdparty.jei.weaponworkshop.WeaponWorkshopJeiCategory;
import com.pasterdream.pasterdreammod.thirdparty.jei.weaponworkshop.WeaponWorkshopJeiRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;

/**
 * PasterDream 的 JEI 集成插件。
 * <p>
 * 分类：梦之坩埚 / 陶盆 / 暗影高炉 / 精铸工坊（13 组硬编码配方）。
 */
@JeiPlugin
public class PDJeiPlugin implements IModPlugin {

    private static final ResourceLocation PLUGIN_UID =
            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "pasterdream.jei_plugin");

    @Override
    public ResourceLocation getPluginUid() {
        return PLUGIN_UID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        var helper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(new DreamCauldronJeiCategory(helper));
        registration.addRecipeCategories(new ClaypotJeiCategory(helper));
        registration.addRecipeCategories(new ShadowBlastFurnaceJeiCategory(helper));
        registration.addRecipeCategories(new WeaponWorkshopJeiCategory(helper));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(DreamCauldronJeiCategory.DREAM_CAULDRON_JEI_RECIPE_TYPE, DreamCauldronJeiRecipe.build());
        registration.addRecipes(ClaypotJeiCategory.CLAYPOT_JEI_RECIPE_TYPE, ClaypotJeiRecipe.build());
        ClientLevel level = Minecraft.getInstance().level;
        if (level != null) {
            registration.addRecipes(ShadowBlastFurnaceJeiCategory.SHADOW_BLAST_FURNACE_JEI_RECIPE_TYPE,
                    level.getRecipeManager().getAllRecipesFor(PDRecipeTypes.SHADOW_BLASTING.get()));
        } else {
            PasterDreamMod.LOGGER.warn("[JEI] 客户端世界未就绪，暗影高炉配方列表本次为空");
        }
        registration.addRecipes(WeaponWorkshopJeiCategory.WEAPON_WORKSHOP_JEI_RECIPE_TYPE,
                WeaponWorkshopJeiRecipe.build());
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalysts(DreamCauldronJeiCategory.DREAM_CAULDRON_JEI_RECIPE_TYPE, PDItems.DREAM_CAULDRON.get());
        registration.addRecipeCatalysts(ClaypotJeiCategory.CLAYPOT_JEI_RECIPE_TYPE, PDItems.CLAYPAN_0.get());
        registration.addRecipeCatalysts(ShadowBlastFurnaceJeiCategory.SHADOW_BLAST_FURNACE_JEI_RECIPE_TYPE, PDItems.SHADOW_BLAST_FURNACE.get());
        registration.addRecipeCatalysts(WeaponWorkshopJeiCategory.WEAPON_WORKSHOP_JEI_RECIPE_TYPE, PDItems.WEAPON_WORKSHOP.get());
    }
}
