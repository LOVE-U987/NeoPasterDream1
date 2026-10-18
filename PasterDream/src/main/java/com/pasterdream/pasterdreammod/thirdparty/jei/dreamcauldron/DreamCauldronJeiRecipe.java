package com.pasterdream.pasterdreammod.thirdparty.jei.dreamcauldron;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.recipe.DreamBrewingRecipe;
import com.pasterdream.pasterdreammod.registry.PDItems;
import com.pasterdream.pasterdreammod.registry.PDRecipeTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.common.brewing.BrewingRecipe;
import net.neoforged.neoforge.common.brewing.IBrewingRecipe;

import java.util.ArrayList;
import java.util.List;

/**
 * 梦之坩埚（法术工厂）JEI 展示配方数据。
 *
 * <p>展示双数据源（同 {@code DreamCauldronBlockEntity} 的双通道酿造）：</p>
 * <ol>
 *   <li><b>法术配方</b>：从运行时 RecipeManager 读取 {@code pasterdream:dream_brewing}
 *       数据包配方（同暗影高炉 JEI 集成模式，数据包/KubeJS 增删改自动跟随）；</li>
 *   <li><b>通用药水酿造</b>：从 {@code Level#potionBrewing()} 枚举——
 *       原版全部药水混合（potion registry × 材料笛卡尔积经 {@code hasMix}/{@code mix} 试探）
 *       与 NeoForge {@link IBrewingRecipe}（模组配方自动兼容），转换展示为
 *       「坩埚酿造」配方——药水的 JEI 配方页因此同时关联原版酿造台与坩埚分类。</li>
 * </ol>
 *
 * <p>字段语义（与坩埚 GUI 槽位一一对应）：</p>
 * <ul>
 *   <li>input1 —— 槽 0 通用材料槽（法术配方中为引导药剂，药水酿造中为酿造材料）</li>
 *   <li>input2/input3/input4 —— 槽 1-3（药水酿造仅用 input2 展示药水容器）</li>
 *   <li>input5 —— 融梦液桶（催化剂展示位：每次酿造消耗 100mB）</li>
 *   <li>output1 —— 空桶（仅法术配方场景展示；药水酿造不退桶，为空栈不渲染）</li>
 *   <li>output2 —— 成品</li>
 *   <li>{@link #dreamEnhanced} —— 药水酿造配方为 true（坩埚产出会附加梦之强化，
 *       Category 据此为成品槽追加提示）</li>
 * </ul>
 */
public class DreamCauldronJeiRecipe {

    final ItemStack input1;
    final ItemStack input2;
    final ItemStack input3;
    final ItemStack input4;
    final ItemStack input5;
    final ItemStack output1;
    final ItemStack output2;

    /** 是否为药水酿造配方（坩埚产出会附加梦之强化） */
    final boolean dreamEnhanced;

    /**
     * 构造法术配方展示数据（产出为法术物品，不附带梦之强化）
     *
     * @param guiding 引导药剂（槽 0）
     * @param m1      材料 1（槽 1）
     * @param m2      材料 2（槽 2）
     * @param m3      材料 3（槽 3）
     * @param result  法术成品
     */
    public DreamCauldronJeiRecipe(ItemStack guiding, ItemStack m1, ItemStack m2, ItemStack m3, ItemStack result) {
        this.input1 = guiding.copy();
        this.input2 = m1.copy();
        this.input3 = m2.copy();
        this.input4 = m3.copy();
        this.input5 = new ItemStack(PDItems.MELTDREAM_LIQUID_BUCKET.get());
        this.output1 = new ItemStack(Items.BUCKET);
        this.output2 = result.copy();
        this.dreamEnhanced = false;
    }

    /**
     * 构造药水酿造展示数据（坩埚产出会附加梦之强化；不退空桶）
     *
     * @param material 酿造材料（槽 0）
     * @param bottle   药水容器（槽 1 展示位）
     * @param result   成品药水（未经强化的原版展示实例）
     */
    private DreamCauldronJeiRecipe(ItemStack material, ItemStack bottle, ItemStack result, boolean unused) {
        this.input1 = material.copy();
        this.input2 = bottle.copy();
        this.input3 = ItemStack.EMPTY;
        this.input4 = ItemStack.EMPTY;
        this.input5 = new ItemStack(PDItems.MELTDREAM_LIQUID_BUCKET.get());
        this.output1 = ItemStack.EMPTY;
        this.output2 = result.copy();
        this.dreamEnhanced = true;
    }

    /**
     * 从运行时 RecipeManager 与 PotionBrewing 构建全部坩埚配方展示数据。
     * 客户端未进入世界（无 RecipeManager）时返回空列表，
     * JEI 在配方重载后会重新调用本方法补全。
     *
     * @return JEI 展示配方列表
     */
    public static List<DreamCauldronJeiRecipe> build() {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (level == null) {
            return List.of();
        }
        List<DreamCauldronJeiRecipe> recipes = new ArrayList<>();
        collectSpellRecipes(minecraft, recipes);
        collectPotionBrewingRecipes(level, recipes);
        return recipes;
    }

    /**
     * 收集法术配方展示数据（dream_brewing 数据包配方）
     *
     * @param minecraft 客户端实例
     * @param out       输出列表
     */
    private static void collectSpellRecipes(Minecraft minecraft, List<DreamCauldronJeiRecipe> out) {
        for (RecipeHolder<DreamBrewingRecipe> holder : minecraft.level.getRecipeManager()
                .getAllRecipesFor(PDRecipeTypes.DREAM_BREWING.get())) {
            DreamBrewingRecipe recipe = holder.value();
            // getIngredients() 返回 [引导药剂, 材料1, 材料2, 材料3]，与 GUI 槽位顺序一致
            List<Ingredient> ingredients = recipe.getIngredients();
            out.add(new DreamCauldronJeiRecipe(
                    firstStack(ingredients.get(0)),
                    firstStack(ingredients.get(1)),
                    firstStack(ingredients.get(2)),
                    firstStack(ingredients.get(3)),
                    recipe.getResultItem(minecraft.level.registryAccess())));
        }
    }

    /**
     * 收集通用药水酿造展示数据（原版 mixes + 模组 IBrewingRecipe 自动兼容）
     *
     * @param level 客户端世界（取 potionBrewing 实例）
     * @param out   输出列表
     */
    private static void collectPotionBrewingRecipes(ClientLevel level, List<DreamCauldronJeiRecipe> out) {
        PotionBrewing brewing = level.potionBrewing();

        // 药水容器候选：potion registry 全量 × 3 种容器（展示实例）
        List<ItemStack> bottleCandidates = new ArrayList<>();
        for (Potion potion : BuiltInRegistries.POTION) {
            Holder<Potion> holder = BuiltInRegistries.POTION.wrapAsHolder(potion);
            for (Item container : CONTAINERS) {
                bottleCandidates.add(PotionContents.createItemStack(container, holder));
            }
        }
        // 材料候选：全物品默认实例（后续用 isIngredient/hasMix 过滤）
        List<ItemStack> ingredientCandidates = new ArrayList<>();
        for (Item item : BuiltInRegistries.ITEM) {
            if (item != Items.AIR) {
                ingredientCandidates.add(new ItemStack(item));
            }
        }

        // 1) 原版药水混合：材料过滤 → 药水 hasMix/mix 试探
        List<ItemStack> potionIngredients = new ArrayList<>();
        for (ItemStack candidate : ingredientCandidates) {
            if (brewing.isIngredient(candidate) || brewing.isContainerIngredient(candidate)) {
                potionIngredients.add(candidate);
            }
        }
        for (ItemStack material : potionIngredients) {
            for (ItemStack bottle : bottleCandidates) {
                if (brewing.hasMix(bottle, material)) {
                    ItemStack result = brewing.mix(material, bottle);
                    if (!result.isEmpty()) {
                        out.add(new DreamCauldronJeiRecipe(material, bottle, result, true));
                    }
                }
            }
        }

        // 2) NeoForge IBrewingRecipe（模组配方，含玄岩药水等）
        for (IBrewingRecipe recipe : brewing.getRecipes()) {
            collectBrewingRecipe(recipe, bottleCandidates, ingredientCandidates, out);
        }
    }

    /**
     * 收集单条 IBrewingRecipe 的展示数据：
     * NeoForge {@link BrewingRecipe} 具体类直接走 getter（精确），其余实现
     * 在药水容器候选域内试探（覆盖绝大多数模组酿造：input 为药水容器）。
     *
     * @param recipe             酿造配方
     * @param bottleCandidates   药水容器候选集
     * @param ingredientCandidates 材料候选集
     * @param out                输出列表
     */
    private static void collectBrewingRecipe(IBrewingRecipe recipe, List<ItemStack> bottleCandidates,
                                             List<ItemStack> ingredientCandidates, List<DreamCauldronJeiRecipe> out) {
        if (recipe instanceof BrewingRecipe concrete) {
            ItemStack input = firstStack(concrete.getInput());
            ItemStack material = firstStack(concrete.getIngredient());
            ItemStack result = concrete.getOutput();
            if (!material.isEmpty() && !input.isEmpty() && !result.isEmpty()) {
                out.add(new DreamCauldronJeiRecipe(material, input, result, true));
            }
            return;
        }
        // 通用 IBrewingRecipe：药水域试探，每配方只取首条匹配（避免同配方多药水重复展示）
        for (ItemStack bottle : bottleCandidates) {
            if (!recipe.isInput(bottle)) {
                continue;
            }
            for (ItemStack material : ingredientCandidates) {
                if (!recipe.isIngredient(material)) {
                    continue;
                }
                ItemStack result = recipe.getOutput(bottle, material);
                if (!result.isEmpty()) {
                    out.add(new DreamCauldronJeiRecipe(material, bottle, result, true));
                    return;
                }
            }
        }
    }

    /** 药水容器物品（普通/喷溅/滞留） */
    private static final Item[] CONTAINERS = {Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION};

    /**
     * 取 Ingredient 的首个代表物品（用于 JEI 展示位）
     *
     * @param ingredient 配方材料
     * @return 首个代表物品（无匹配物品时为空栈）
     */
    private static ItemStack firstStack(Ingredient ingredient) {
        ItemStack[] stacks = ingredient.getItems();
        return stacks.length > 0 ? stacks[0] : ItemStack.EMPTY;
    }

    /**
     * 查询本模组法术配方数据包目录（供调试/文档引用）
     *
     * @return 数据包配方目录路径
     */
    public static ResourceLocation getRecipeDirectory() {
        return ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "dream_brewing");
    }
}
