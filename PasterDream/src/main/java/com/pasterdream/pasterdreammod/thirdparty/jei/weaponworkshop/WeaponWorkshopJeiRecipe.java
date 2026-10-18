package com.pasterdream.pasterdreammod.thirdparty.jei.weaponworkshop;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.recipe.WeaponWorkshopRecipe;
import com.pasterdream.pasterdreammod.registry.PDRecipeTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 精铸工坊 JEI 展示配方数据。
 * <p>
 * 配方数据不再硬编码：从客户端 RecipeManager 的
 * {@code pasterdream:weapon_forge}（{@link WeaponWorkshopRecipe}）动态读取，
 * 与数据包 / KubeJS 注册的配方实时保持一致（与暗影高炉 JEI 同模式）。
 * 展示结构：5 输入 + 镶嵌物 + 胚体/成品双产出。
 */
public class WeaponWorkshopJeiRecipe {

    final Ingredient item1;
    final Ingredient item2;
    final Ingredient item3;
    final Ingredient item4;
    final Ingredient item5;
    final Ingredient inlay;
    final ItemStack output1;
    final ItemStack output2;

    public WeaponWorkshopJeiRecipe(Ingredient item1, Ingredient item2, Ingredient item3,
                                   Ingredient item4, Ingredient item5, Ingredient inlay,
                                   ItemStack output1, ItemStack output2) {
        this.item1 = item1;
        this.item2 = item2;
        this.item3 = item3;
        this.item4 = item4;
        this.item5 = item5;
        this.inlay = inlay;
        this.output1 = output1;
        this.output2 = output2;
    }

    private static final TagKey<Item> ENHANCE_STONE = TagKey.create(Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "enhance_stone"));

    /**
     * 从客户端 RecipeManager 读取全部 weapon_forge 配方构建 JEI 展示列表
     * <p>客户端世界未就绪时返回空列表，由 JEI 配方重载自动补全（与暗影高炉同模式）。</p>
     *
     * @return JEI 展示配方列表
     */
    public static List<WeaponWorkshopJeiRecipe> fromRecipeManager() {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            PasterDreamMod.LOGGER.debug("[JEI] 客户端世界未就绪，精铸工坊配方待配方重载后自动补全");
            return Collections.emptyList();
        }
        List<WeaponWorkshopJeiRecipe> list = new ArrayList<>();
        for (RecipeHolder<WeaponWorkshopRecipe> holder
                : level.getRecipeManager().getAllRecipesFor(PDRecipeTypes.WEAPON_FORGE.get())) {
            list.add(fromRecipe(holder.value(), level.registryAccess()));
        }
        return list;
    }

    /**
     * 把数据驱动锻造配方转换为 JEI 展示配方
     *
     * @param recipe     锻造配方
     * @param registries 注册表访问（解析产物物品栈）
     * @return JEI 展示配方
     */
    private static WeaponWorkshopJeiRecipe fromRecipe(WeaponWorkshopRecipe recipe, HolderLookup.Provider registries) {
        List<Ingredient> inputs = recipe.getIngredients();
        Ingredient air = Ingredient.of(Items.AIR);
        // 材料不足 5 格时以 AIR 补齐（防御：坏配方仍可展示不崩溃）
        Ingredient p0 = inputs.size() > 0 ? inputs.get(0) : air;
        Ingredient p1 = inputs.size() > 1 ? inputs.get(1) : air;
        Ingredient p2 = inputs.size() > 2 ? inputs.get(2) : air;
        Ingredient p3 = inputs.size() > 3 ? inputs.get(3) : air;
        Ingredient p4 = inputs.size() > 4 ? inputs.get(4) : air;
        Ingredient inlay = recipe.isInlay() ? Ingredient.of(ENHANCE_STONE) : air;
        return new WeaponWorkshopJeiRecipe(p0, p1, p2, p3, p4, inlay,
                recipe.getResultItem(registries), recipe.getFinalResult());
    }
}
