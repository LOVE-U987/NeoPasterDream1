package com.pasterdream.pasterdreammod.integration.kubejs;

import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.BooleanComponent;
import dev.latvian.mods.kubejs.recipe.component.IngredientComponent;
import dev.latvian.mods.kubejs.recipe.component.ItemStackComponent;
import dev.latvian.mods.kubejs.recipe.component.NumberComponent;
import dev.latvian.mods.kubejs.recipe.component.StringComponent;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import dev.latvian.mods.kubejs.util.IntBounds;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

/**
 * PasterDream 自定义配方类型的 KubeJS RecipeSchema 定义
 * <p>
 * 为 {@code pasterdream:shadow_blasting}（暗影高炉）、{@code pasterdream:weapon_forge}
 * （精铸工坊）与 {@code pasterdream:dream_brewing}（梦境炼药锅）注册 schema，
 * 使玩家可在 {@code ServerEvents.recipes} 中书写：
 * <pre>{@code
 * ServerEvents.recipes(event => {
 *     // 暗影高炉：主产物 + 原料，其余参数链式可选
 *     event.recipes.pasterdream.shadow_blasting('minecraft:diamond', 'minecraft:coal_block')
 *         .byResult('pasterdream:dream_dust').blastingTick(50);
 *     // 精铸工坊：产物 + 5 材料数组
 *     event.recipes.pasterdream.weapon_forge('minecraft:netherite_scrap',
 *         ['minecraft:iron_ingot', 'minecraft:gold_ingot', 'minecraft:copper_ingot',
 *          'minecraft:diamond', 'minecraft:emerald']).inlay(true);
 *     // 梦境炼药锅：产物 + 3 材料数组（引导药剂/时长可选）
 *     event.recipes.pasterdream.dream_brewing('minecraft:golden_apple',
 *         ['minecraft:apple', 'minecraft:gold_ingot', 'minecraft:glowstone_dust'])
 *         .craftTick(40);
 * });
 * }</pre>
 * schema 生成的 JSON 与配方 MapCodec 字段一一对应，经 KubeJS 合成数据包进入
 * vanilla RecipeManager 加载链（数据包覆盖 / JEI / 配方查询全部自然生效）。
 * <p>
 * 本类仅被 {@link PasterDreamKubeJSPlugin} 引用：KubeJS 缺席时不会被类加载，
 * 满足可选依赖约束（compileOnly，不进发布 jar）。
 */
public final class PDRecipeSchemas {

    // ==================== 暗影高炉 (pasterdream:shadow_blasting) ====================

    /** 主产物（JSON 字段 result） */
    public static final RecipeKey<ItemStack> SB_RESULT =
            ItemStackComponent.ITEM_STACK.outputKey("result");

    /** 原料（JSON 字段 input_ingredient；JS 方法名简化为 input） */
    public static final RecipeKey<Ingredient> SB_INPUT =
            IngredientComponent.INGREDIENT.inputKey("input_ingredient").functionNames(List.of("input"));

    /** 副产物（可选，默认空栈） */
    public static final RecipeKey<ItemStack> SB_BY_RESULT =
            ItemStackComponent.OPTIONAL_ITEM_STACK.otherKey("by_result")
                    .optional(ItemStack.EMPTY).functionNames(List.of("byResult"));

    /** 副产物概率（可选，默认 1.0） */
    public static final RecipeKey<Double> SB_BY_PROBABILITY =
            NumberComponent.DOUBLE.otherKey("by_result_probability")
                    .optional(1.0).functionNames(List.of("byResultProbability"));

    /** 冶炼时长（可选，默认 100 tick） */
    public static final RecipeKey<Integer> SB_BLASTING_TICK =
            NumberComponent.INT.otherKey("blasting_tick")
                    .optional(100).functionNames(List.of("blastingTick", "time"));

    /** 每次消耗暗影液体量 mB（可选，默认 500） */
    public static final RecipeKey<Integer> SB_SPEND_FLUID_FUEL =
            NumberComponent.INT.otherKey("spend_fluid_fuel")
                    .optional(500).functionNames(List.of("spendFluidFuel"));

    /** 每次消耗梦魇燃料数（可选，默认 1） */
    public static final RecipeKey<Integer> SB_SPEND_FUEL =
            NumberComponent.INT.otherKey("spend_fuel")
                    .optional(1).functionNames(List.of("spendFuel"));

    /** 配方分组（可选，默认空串；不参与匹配，仅保留数据完整性） */
    public static final RecipeKey<String> SB_GROUP =
            StringComponent.OPTIONAL_STRING.otherKey("group").optional("");

    /** 暗影高炉配方 schema：位置参数（主产物, 原料），配方 id 由主产物 + 原料生成 */
    public static final RecipeSchema SHADOW_BLASTING = new RecipeSchema(
            SB_RESULT, SB_INPUT, SB_BY_RESULT, SB_BY_PROBABILITY,
            SB_BLASTING_TICK, SB_SPEND_FLUID_FUEL, SB_SPEND_FUEL, SB_GROUP)
            .constructor(SB_RESULT, SB_INPUT)
            .uniqueId(SB_RESULT);

    // ==================== 精铸工坊 (pasterdream:weapon_forge) ====================

    /** 锻造产物（JSON 字段 result） */
    public static final RecipeKey<ItemStack> WW_RESULT =
            ItemStackComponent.ITEM_STACK.outputKey("result");

    /** 5 格材料（JSON 字段 ingredients；固定 5 元素数组） */
    public static final RecipeKey<List<Ingredient>> WW_INGREDIENTS =
            IngredientComponent.INGREDIENT.instance().asList()
                    .withBounds(IntBounds.of(5, 5))
                    .inputKey("ingredients");

    /** JEI 镶嵌槽展示标记（可选，默认 false；实际镶嵌由工坊强化石槽驱动） */
    public static final RecipeKey<Boolean> WW_INLAY =
            BooleanComponent.BOOLEAN.otherKey("inlay").optional(false);

    /** 原胚最终成品预览（可选，默认空栈；仅 JEI 第二产出槽展示） */
    public static final RecipeKey<ItemStack> WW_FINAL_RESULT =
            ItemStackComponent.OPTIONAL_ITEM_STACK.otherKey("final_result")
                    .optional(ItemStack.EMPTY).functionNames(List.of("finalResult"));

    /** 精铸工坊配方 schema：位置参数（产物, 5 材料数组），配方 id 由产物生成 */
    public static final RecipeSchema WEAPON_FORGE = new RecipeSchema(
            WW_RESULT, WW_INGREDIENTS, WW_INLAY, WW_FINAL_RESULT)
            .constructor(WW_RESULT, WW_INGREDIENTS)
            .uniqueId(WW_RESULT);

    // ==================== 梦境炼药锅 (pasterdream:dream_brewing) ====================

    /** 法术产物（JSON 字段 result） */
    public static final RecipeKey<ItemStack> DB_RESULT =
            ItemStackComponent.ITEM_STACK.outputKey("result");

    /** 三种材料（JSON 字段 materials；固定 3 元素数组，按序对应瓶槽 1-3） */
    public static final RecipeKey<List<Ingredient>> DB_MATERIALS =
            IngredientComponent.INGREDIENT.instance().asList()
                    .withBounds(IntBounds.of(3, 3))
                    .inputKey("materials");

    /**
     * 引导药剂（JSON 字段 guiding_drug）
     * <p>使用 {@code defaultOptional()} 而非 {@code optional(Ingredient.of(...))}：
     * 默认值 {@code pasterdream:guiding_drug} 在 MapCodec 侧定义（引用 DeferredHolder），
     * 本类在 KubeJS 注册期初始化，不可触碰注册表；脚本未设置时不写字段，由 codec 默认兜底。</p>
     */
    public static final RecipeKey<Ingredient> DB_GUIDING_DRUG =
            IngredientComponent.INGREDIENT.otherKey("guiding_drug")
                    .defaultOptional().functionNames(List.of("guidingDrug"));

    /** 酿造总时长（可选，默认 60 tick，下限 4 由构造器夹取） */
    public static final RecipeKey<Integer> DB_CRAFT_TICK =
            NumberComponent.INT.otherKey("craft_tick")
                    .optional(60).functionNames(List.of("craftTick", "time"));

    /** 配方分组（可选，默认空串；不参与匹配，仅保留数据完整性） */
    public static final RecipeKey<String> DB_GROUP =
            StringComponent.OPTIONAL_STRING.otherKey("group").optional("");

    /** 梦境炼药锅配方 schema：位置参数（产物, 3 材料数组），配方 id 由产物生成 */
    public static final RecipeSchema DREAM_BREWING = new RecipeSchema(
            DB_RESULT, DB_MATERIALS, DB_GUIDING_DRUG, DB_CRAFT_TICK, DB_GROUP)
            .constructor(DB_RESULT, DB_MATERIALS)
            .uniqueId(DB_RESULT);

    /** 工具类禁止实例化 */
    private PDRecipeSchemas() {
    }
}
