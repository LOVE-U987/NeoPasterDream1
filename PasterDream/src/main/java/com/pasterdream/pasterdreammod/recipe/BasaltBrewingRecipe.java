package com.pasterdream.pasterdreammod.recipe;

import com.pasterdream.pasterdreammod.registry.PDPotions;
import com.pasterdream.pasterdreammod.registry.items.PDItemsMaterials;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.neoforged.neoforge.common.brewing.IBrewingRecipe;

/**
 * 玄岩药水酿造配方 (basalt brewing)
 * <p>
 * 移植自原模组 {@code net.pasterdream.recipes.brewing.Brewing0BrewingRecipe}
 * （Forge 1.20.1 的 BrewingRecipeRegistry 注册），语义保持一致：
 * <b>粗制药水（awkward）+ 玄武岩蜗牛壳 → 玄岩药水</b>，容器类型（普通/喷溅/滞留）保持不变。
 * <p>
 * 注册至 NeoForge {@code BREWING_RECIPES} 注册表后，原版酿造台、JEI 酿造展示、
 * 梦境炼药锅通用酿造通道（{@code Level#potionBrewing()} 三件套委托 registry）
 * 三处自动生效，无需额外接入。
 */
public class BasaltBrewingRecipe implements IBrewingRecipe {

    /**
     * 构造玄岩药水酿造配方
     */
    public BasaltBrewingRecipe() {
    }

    /**
     * 判定输入是否为合法基底：任意药水容器且内容为粗制药水（与原模组一致）
     *
     * @param input 药水容器物品
     * @return 可作为酿造基底返回 true
     */
    @Override
    public boolean isInput(ItemStack input) {
        if (!input.is(Items.POTION) && !input.is(Items.SPLASH_POTION) && !input.is(Items.LINGERING_POTION)) {
            return false;
        }
        PotionContents contents = input.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
        return contents.is(Potions.AWKWARD);
    }

    /**
     * 判定材料是否为玄武岩蜗牛壳
     *
     * @param ingredient 材料物品
     * @return 是蜗牛壳返回 true
     */
    @Override
    public boolean isIngredient(ItemStack ingredient) {
        return ingredient.is(PDItemsMaterials.BASALT_SNAIL_SHELL.get());
    }

    /**
     * 计算酿造产物：保持容器类型，药水内容替换为玄岩（与原模组 PotionUtils.setPotion 等效）
     *
     * @param input      药水容器物品
     * @param ingredient 材料物品
     * @return 玄岩药水（不匹配时返回空栈）
     */
    @Override
    public ItemStack getOutput(ItemStack input, ItemStack ingredient) {
        if (isInput(input) && isIngredient(ingredient)) {
            return PotionContents.createItemStack(input.getItem(), PDPotions.BASALT);
        }
        return ItemStack.EMPTY;
    }
}
