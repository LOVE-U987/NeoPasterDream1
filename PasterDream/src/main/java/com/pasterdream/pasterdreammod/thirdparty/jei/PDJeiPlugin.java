package com.pasterdream.pasterdreammod.thirdparty.jei;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.registry.PDItems;
import com.pasterdream.pasterdreammod.registry.PDRecipeTypes;
import com.pasterdream.pasterdreammod.thirdparty.jei.claypot.ClaypotJeiCategory;
import com.pasterdream.pasterdreammod.thirdparty.jei.claypot.ClaypotJeiRecipe;
import com.pasterdream.pasterdreammod.thirdparty.jei.debugcrystal.DebugCrystalJeiHelper;
import com.pasterdream.pasterdreammod.thirdparty.jei.debugcrystal.DebugCrystalJeiIngredient;
import com.pasterdream.pasterdreammod.thirdparty.jei.debugcrystal.DebugCrystalJeiRenderer;
import com.pasterdream.pasterdreammod.thirdparty.jei.dreamcauldron.DreamCauldronJeiCategory;
import com.pasterdream.pasterdreammod.thirdparty.jei.dreamcauldron.DreamCauldronJeiRecipe;
import com.pasterdream.pasterdreammod.thirdparty.jei.shadowblastfurnace.ShadowBlastFurnaceJeiCategory;
import com.pasterdream.pasterdreammod.thirdparty.jei.weaponworkshop.WeaponWorkshopJeiCategory;
import com.pasterdream.pasterdreammod.thirdparty.jei.weaponworkshop.WeaponWorkshopJeiRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IModIngredientRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * PasterDream 的 JEI 集成插件。
 * <p>
 * 分类：梦之坩埚 / 陶盆 / 暗影高炉 / 精铸工坊（13 组硬编码配方）。
 * <p>
 * 运行时收拢：调试水晶（{@code debug_wand_*}，130+ 个）在 JEI 物品列表中收拢为
 * 虚拟条目「调试水晶」（自定义原料类型，渲染沿用染梦列车贴图），避免挤占玩家 JEI 界面；
 * 创造模式标签页不受影响。
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

    /**
     * 注册 JEI 自定义原料类型：调试水晶虚拟条目「调试水晶」。
     * <p>
     * JEI 19.x 无法为单个 ItemStack 覆盖显示名，故以独立原料类型实现家族统一命名；
     * 渲染沿用代表物品（染梦列车，调试创造栏图标）的贴图，名称与获取提示走语言键。
     *
     * @param registration JEI 原料注册器
     */
    @Override
    public void registerIngredients(IModIngredientRegistration registration) {
        registration.register(DebugCrystalJeiIngredient.TYPE,
                List.of(DebugCrystalJeiIngredient.createRepresentative()),
                new DebugCrystalJeiHelper(),
                new DebugCrystalJeiRenderer(),
                DebugCrystalJeiIngredient.CODEC);
    }

    /**
     * JEI 运行时就绪回调：将调试水晶家族在 JEI 物品列表中收拢为单一入口。
     * <p>
     * JEI 19.x 的物品列表完全来自创造模式标签页，且注册期隐藏 API（如 hideIngredient）已被移除，
     * 因此在运行时就绪后移除全部调试水晶（见 {@link #collectDebugWandsToHide()}），
     * 由虚拟条目「调试水晶」代表整个家族（见 {@link #registerIngredients(IModIngredientRegistration)}）。
     * JEI 每次启动或资源重载都会重建运行时并重新回调本方法，收拢效果自动持续生效。
     * <p>
     * 仅影响 JEI 展示：创造模式标签页不受影响，开发团队仍可从调试栏正常取用水晶。
     *
     * @param runtime JEI 运行时（用于获取物品管理器执行移除）
     */
    @Override
    public void onRuntimeAvailable(IJeiRuntime runtime) {
        List<ItemStack> hiddenWands = collectDebugWandsToHide();
        if (hiddenWands.isEmpty()) {
            return;
        }
        IIngredientManager ingredientManager = runtime.getIngredientManager();
        ingredientManager.removeIngredientsAtRuntime(VanillaTypes.ITEM_STACK, hiddenWands);
        PasterDreamMod.LOGGER.debug("[JEI] 调试水晶已收拢为单一入口：隐藏 {} 个（虚拟条目「调试水晶」代表家族）",
                hiddenWands.size());
    }

    /**
     * 收集需要从 JEI 列表移除的调试水晶（{@code debug_wand_*} 家族全部成员）。
     * <p>
     * 按注册名前缀 {@code debug_wand_} 识别家族成员，后续新增的调试水晶自动纳入收拢范围，
     * 无需维护硬编码清单；家族在 JEI 中由虚拟条目「调试水晶」代表
     * （见 {@link #registerIngredients(IModIngredientRegistration)}）。
     *
     * @return 需要从 JEI 移除的物品堆列表
     */
    private static List<ItemStack> collectDebugWandsToHide() {
        List<ItemStack> hidden = new ArrayList<>();
        for (Item item : BuiltInRegistries.ITEM) {
            if (BuiltInRegistries.ITEM.getKey(item).getPath().startsWith("debug_wand_")) {
                hidden.add(new ItemStack(item));
            }
        }
        return hidden;
    }
}
