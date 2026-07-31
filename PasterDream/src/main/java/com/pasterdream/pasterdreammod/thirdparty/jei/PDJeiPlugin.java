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

import java.util.List;

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
        // 暗影高炉配方来自运行时 RecipeManager（datapack 加载）：JEI 在每次配方重载后都会重新
        // 调用本方法，因此客户端世界未就绪时仅注册空列表，待下次重载自动补全，不会长期为空。
        registration.addRecipes(ShadowBlastFurnaceJeiCategory.SHADOW_BLAST_FURNACE_JEI_RECIPE_TYPE,
                supplierShadowBlastRecipes());
        registration.addRecipes(WeaponWorkshopJeiCategory.WEAPON_WORKSHOP_JEI_RECIPE_TYPE,
                WeaponWorkshopJeiRecipe.build());
    }

    /**
     * 延迟读取暗影高炉配方列表（客户端世界未就绪时返回空列表，由 JEI 重载自动补全）。
     *
     * @return 当前 RecipeManager 中的暗影高炉配方
     */
    private static List<net.minecraft.world.item.crafting.RecipeHolder<com.pasterdream.pasterdreammod.recipe.ShadowBlastFurnaceRecipe>> supplierShadowBlastRecipes() {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            PasterDreamMod.LOGGER.debug("[JEI] 客户端世界未就绪，暗影高炉配方待配方重载后自动补全");
            return java.util.Collections.emptyList();
        }
        return level.getRecipeManager().getAllRecipesFor(PDRecipeTypes.SHADOW_BLASTING.get());
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalysts(DreamCauldronJeiCategory.DREAM_CAULDRON_JEI_RECIPE_TYPE, PDItems.DREAM_CAULDRON.get());
        registration.addRecipeCatalysts(ClaypotJeiCategory.CLAYPOT_JEI_RECIPE_TYPE, PDItems.CLAYPAN_0.get());
        registration.addRecipeCatalysts(ShadowBlastFurnaceJeiCategory.SHADOW_BLAST_FURNACE_JEI_RECIPE_TYPE, PDItems.SHADOW_BLAST_FURNACE.get());
        registration.addRecipeCatalysts(WeaponWorkshopJeiCategory.WEAPON_WORKSHOP_JEI_RECIPE_TYPE, PDItems.WEAPON_WORKSHOP.get());
    }
}
