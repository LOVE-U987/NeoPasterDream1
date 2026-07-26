package com.pasterdream.pasterdreammod.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.pasterdream.pasterdreammod.registry.PDBlocks;
import com.pasterdream.pasterdreammod.registry.PDRecipeTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

/**
 * 暗影高炉冶炼配方 (shadow_blasting)
 * <p>
 * 移植自原版 {@code net.pasterdream.recipes.dark_smithing.ShadowBlastFurnaceRecipe}
 * （Forge 1.20.1 的 fromJson/fromNetwork 手写序列化），1.21.1 改为
 * MapCodec + StreamCodec 数据驱动。JSON 字段与原版保持一致：
 * <ul>
 *   <li>{@code input_ingredient} —— 原料（必填）；</li>
 *   <li>{@code result} —— 主产物（可选，默认空；1.21 物品栈格式 {@code {"id": ...}}）；</li>
 *   <li>{@code by_result} —— 副产物（可选，默认空）；</li>
 *   <li>{@code by_result_probability} —— 副产物概率（可选，默认 1.0，写入时夹取到 0-1）；</li>
 *   <li>{@code blasting_tick} —— 冶炼时长（可选，默认 100 tick）；</li>
 *   <li>{@code spend_fluid_fuel} —— 每次消耗的暗影液体量（可选，默认 500 mB）；</li>
 *   <li>{@code spend_fuel} —— 每次消耗的梦魇燃料数（可选，默认 1）。</li>
 * </ul>
 * 配方输入使用原版 {@link SingleRecipeInput}（仅匹配输入槽 0 的物品）。
 */
public class ShadowBlastFurnaceRecipe implements Recipe<SingleRecipeInput> {

    private final String group;
    private final Ingredient inputItem;
    private final ItemStack output;
    private final ItemStack byOutput;
    private final double byOutputProbability;
    private final int blastingTick;
    private final int spendFluidFuel;
    private final int spendFuel;

    /**
     * 构造暗影高炉配方
     *
     * @param group               配方分组（可为空串）
     * @param inputItem           输入原料
     * @param output              主产物（可为空栈）
     * @param byOutput            副产物（可为空栈）
     * @param byOutputProbability 副产物概率（写入时夹取到 0-1，与原版一致）
     * @param blastingTick        冶炼所需 tick 数
     * @param spendFluidFuel      每次冶炼消耗的暗影液体（mB）
     * @param spendFuel           每次冶炼消耗的梦魇燃料数量
     */
    public ShadowBlastFurnaceRecipe(String group, Ingredient inputItem, ItemStack output, ItemStack byOutput,
                                    double byOutputProbability, int blastingTick, int spendFluidFuel, int spendFuel) {
        this.group = group;
        this.inputItem = inputItem;
        this.output = output;
        this.byOutput = byOutput;
        this.byOutputProbability = Mth.clamp(byOutputProbability, 0.0, 1.0);
        this.blastingTick = blastingTick;
        this.spendFluidFuel = spendFluidFuel;
        this.spendFuel = spendFuel;
    }

    @Override
    public boolean matches(SingleRecipeInput input, Level level) {
        return this.inputItem.test(input.item());
    }

    @Override
    public ItemStack assemble(SingleRecipeInput input, HolderLookup.Provider registries) {
        return getResultItem(registries).copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.withSize(1, this.inputItem);
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return this.output;
    }

    /**
     * 获取副产物（原版 getByResultItem）
     *
     * @return 副产物物品栈（可为空栈）
     */
    public ItemStack getByResultItem() {
        return this.byOutput;
    }

    @Override
    public String getGroup() {
        return this.group;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return PDRecipeTypes.SHADOW_BLASTING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return PDRecipeTypes.SHADOW_BLASTING.get();
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(PDBlocks.SHADOW_BLAST_FURNACE.get());
    }

    /**
     * 获取副产物概率
     *
     * @return 概率（0-1）
     */
    public double getByOutputProbability() {
        return this.byOutputProbability;
    }

    /**
     * 获取冶炼所需 tick 数
     *
     * @return 冶炼时长
     */
    public int getBlastingTick() {
        return this.blastingTick;
    }

    /**
     * 获取每次冶炼消耗的梦魇燃料数量
     *
     * @return 燃料数
     */
    public int getSpendFuel() {
        return this.spendFuel;
    }

    /**
     * 获取每次冶炼消耗的暗影液体量
     *
     * @return 液体量（mB）
     */
    public int getSpendFluidFuel() {
        return this.spendFluidFuel;
    }

    /**
     * 暗影高炉配方序列化器（MapCodec + StreamCodec）
     */
    public static class Serializer implements RecipeSerializer<ShadowBlastFurnaceRecipe> {

        /** JSON 编解码器：字段名与原版 fromJson 保持一致 */
        public static final MapCodec<ShadowBlastFurnaceRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.optionalFieldOf("group", "").forGetter(ShadowBlastFurnaceRecipe::getGroup),
                Ingredient.CODEC_NONEMPTY.fieldOf("input_ingredient").forGetter(r -> r.inputItem),
                ItemStack.OPTIONAL_CODEC.optionalFieldOf("result", ItemStack.EMPTY).forGetter(r -> r.output),
                ItemStack.OPTIONAL_CODEC.optionalFieldOf("by_result", ItemStack.EMPTY).forGetter(r -> r.byOutput),
                Codec.DOUBLE.optionalFieldOf("by_result_probability", 1.0).forGetter(ShadowBlastFurnaceRecipe::getByOutputProbability),
                Codec.INT.optionalFieldOf("blasting_tick", 100).forGetter(ShadowBlastFurnaceRecipe::getBlastingTick),
                Codec.INT.optionalFieldOf("spend_fluid_fuel", 500).forGetter(ShadowBlastFurnaceRecipe::getSpendFluidFuel),
                Codec.INT.optionalFieldOf("spend_fuel", 1).forGetter(ShadowBlastFurnaceRecipe::getSpendFuel)
        ).apply(instance, ShadowBlastFurnaceRecipe::new));

        /** 网络同步编解码器（字段顺序与原版 toNetwork 一致） */
        public static final StreamCodec<RegistryFriendlyByteBuf, ShadowBlastFurnaceRecipe> STREAM_CODEC = StreamCodec.of(
                Serializer::toNetwork, Serializer::fromNetwork);

        @Override
        public MapCodec<ShadowBlastFurnaceRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ShadowBlastFurnaceRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        /** 从网络缓冲区读取配方 */
        private static ShadowBlastFurnaceRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            String group = buffer.readUtf();
            Ingredient input = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
            ItemStack output = ItemStack.OPTIONAL_STREAM_CODEC.decode(buffer);
            ItemStack byOutput = ItemStack.OPTIONAL_STREAM_CODEC.decode(buffer);
            double byOutputProbability = buffer.readDouble();
            int blastingTick = buffer.readVarInt();
            int spendFluidFuel = buffer.readVarInt();
            int spendFuel = buffer.readVarInt();
            return new ShadowBlastFurnaceRecipe(group, input, output, byOutput,
                    byOutputProbability, blastingTick, spendFluidFuel, spendFuel);
        }

        /** 把配方写入网络缓冲区 */
        private static void toNetwork(RegistryFriendlyByteBuf buffer, ShadowBlastFurnaceRecipe recipe) {
            buffer.writeUtf(recipe.group);
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.inputItem);
            ItemStack.OPTIONAL_STREAM_CODEC.encode(buffer, recipe.output);
            ItemStack.OPTIONAL_STREAM_CODEC.encode(buffer, recipe.byOutput);
            buffer.writeDouble(recipe.byOutputProbability);
            buffer.writeVarInt(recipe.blastingTick);
            buffer.writeVarInt(recipe.spendFluidFuel);
            buffer.writeVarInt(recipe.spendFuel);
        }
    }
}
