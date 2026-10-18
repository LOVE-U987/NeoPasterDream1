package com.pasterdream.pasterdreammod.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.pasterdream.pasterdreammod.registry.PDBlocks;
import com.pasterdream.pasterdreammod.registry.PDRecipeTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

/**
 * 精铸工坊锻造配方 (weapon_forge)
 * <p>
 * 将原硬编码于 {@code WeaponWorkshopBlockEntity} 的 13 项锻造配方表重构为
 * vanilla 数据驱动配方（参照 {@link ShadowBlastFurnaceRecipe} 的 MapCodec + StreamCodec 模式），
 * JSON 字段：
 * <ul>
 *   <li>{@code ingredients} —— 5 格材料（必填，逐槽精确匹配，每槽消耗 1 个）；</li>
 *   <li>{@code result} —— 锻造产物（必填，1.21 物品栈格式 {@code {"id": ...}}）；</li>
 *   <li>{@code inlay} —— 是否展示强化石镶嵌槽（可选，默认 false；仅 JEI 展示，
 *       实际镶嵌由工坊强化石槽 5 驱动）；</li>
 *   <li>{@code final_result} —— 原胚最终成品预览（可选，默认空；仅 JEI 第二产出槽展示）。</li>
 * </ul>
 * 配方输入使用 {@link WeaponWorkshopRecipeInput}（工坊材料槽 0-4）。
 * 附魔合并与镶嵌结算为锻造通用行为，保留在 {@code WeaponWorkshopBlockEntity} 中。
 */
public class WeaponWorkshopRecipe implements Recipe<WeaponWorkshopRecipeInput> {

    private final List<Ingredient> ingredients;
    private final ItemStack result;
    private final boolean inlay;
    private final ItemStack finalResult;

    /**
     * 构造精铸工坊锻造配方
     *
     * @param ingredients 5 格材料（逐槽匹配，多余/不足 5 个的配方永不匹配）
     * @param result      锻造产物
     * @param inlay       JEI 是否展示强化石镶嵌槽
     * @param finalResult 原胚最终成品预览（仅 JEI 展示，可为空栈）
     */
    public WeaponWorkshopRecipe(List<Ingredient> ingredients, ItemStack result, boolean inlay, ItemStack finalResult) {
        this.ingredients = ingredients;
        this.result = result;
        this.inlay = inlay;
        this.finalResult = finalResult;
    }

    @Override
    public boolean matches(WeaponWorkshopRecipeInput input, Level level) {
        if (this.ingredients.size() != WeaponWorkshopRecipeInput.SLOT_COUNT) {
            // 防御：材料数不是 5 的配方视为无效
            return false;
        }
        for (int i = 0; i < WeaponWorkshopRecipeInput.SLOT_COUNT; i++) {
            if (!this.ingredients.get(i).test(input.getItem(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack assemble(WeaponWorkshopRecipeInput input, HolderLookup.Provider registries) {
        return this.result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.copyOf(this.ingredients);
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return this.result;
    }

    @Override
    public String getGroup() {
        return "";
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return PDRecipeTypes.WEAPON_FORGE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return PDRecipeTypes.WEAPON_FORGE.get();
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(PDBlocks.WEAPON_WORKSHOP.get());
    }

    /**
     * 标记为特殊配方
     * <p>与暗影高炉配方同理：自定义 RecipeType 不被原版配方书识别，
     * 返回 true 跳过配方书归档以消除 "Unknown recipe category" 警告
     * （工坊配方已由 JEI 分类展示）。</p>
     *
     * @return 恒为 true
     */
    @Override
    public boolean isSpecial() {
        return true;
    }

    /**
     * JEI 是否展示强化石镶嵌槽
     *
     * @return inlay 标记
     */
    public boolean isInlay() {
        return this.inlay;
    }

    /**
     * 获取原胚最终成品预览（仅 JEI 展示）
     *
     * @return 成品物品栈（可为空栈）
     */
    public ItemStack getFinalResult() {
        return this.finalResult;
    }

    /**
     * 精铸工坊锻造配方序列化器（MapCodec + StreamCodec）
     */
    public static class Serializer implements RecipeSerializer<WeaponWorkshopRecipe> {

        /** 材料列表编解码器：固定 5 格，长度不符时报错而非静默失效 */
        private static final Codec<List<Ingredient>> INGREDIENTS_CODEC = Codec.list(Ingredient.CODEC_NONEMPTY)
                .validate(list -> list.size() == WeaponWorkshopRecipeInput.SLOT_COUNT
                        ? DataResult.success(list)
                        : DataResult.error(() -> "weapon_forge 配方 ingredients 需要恰好 "
                                + WeaponWorkshopRecipeInput.SLOT_COUNT + " 个材料，实际 " + list.size()));

        /** JSON 编解码器：字段名与 KubeJS RecipeSchema 保持一致 */
        public static final MapCodec<WeaponWorkshopRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                INGREDIENTS_CODEC.fieldOf("ingredients").forGetter(r -> r.ingredients),
                ItemStack.CODEC.fieldOf("result").forGetter(r -> r.result),
                Codec.BOOL.optionalFieldOf("inlay", false).forGetter(WeaponWorkshopRecipe::isInlay),
                ItemStack.OPTIONAL_CODEC.optionalFieldOf("final_result", ItemStack.EMPTY)
                        .forGetter(WeaponWorkshopRecipe::getFinalResult)
        ).apply(instance, WeaponWorkshopRecipe::new));

        /** 网络同步编解码器 */
        public static final StreamCodec<RegistryFriendlyByteBuf, WeaponWorkshopRecipe> STREAM_CODEC = StreamCodec.of(
                Serializer::toNetwork, Serializer::fromNetwork);

        @Override
        public MapCodec<WeaponWorkshopRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, WeaponWorkshopRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        /** 从网络缓冲区读取配方 */
        private static WeaponWorkshopRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            int size = buffer.readVarInt();
            List<Ingredient> ingredients = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                ingredients.add(Ingredient.CONTENTS_STREAM_CODEC.decode(buffer));
            }
            ItemStack result = ItemStack.OPTIONAL_STREAM_CODEC.decode(buffer);
            boolean inlay = buffer.readBoolean();
            ItemStack finalResult = ItemStack.OPTIONAL_STREAM_CODEC.decode(buffer);
            return new WeaponWorkshopRecipe(ingredients, result, inlay, finalResult);
        }

        /** 把配方写入网络缓冲区 */
        private static void toNetwork(RegistryFriendlyByteBuf buffer, WeaponWorkshopRecipe recipe) {
            buffer.writeVarInt(recipe.ingredients.size());
            for (Ingredient ingredient : recipe.ingredients) {
                Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, ingredient);
            }
            ItemStack.OPTIONAL_STREAM_CODEC.encode(buffer, recipe.result);
            buffer.writeBoolean(recipe.inlay);
            ItemStack.OPTIONAL_STREAM_CODEC.encode(buffer, recipe.finalResult);
        }
    }
}
