package com.pasterdream.pasterdreammod.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.pasterdream.pasterdreammod.registry.PDBlocks;
import com.pasterdream.pasterdreammod.registry.PDItems;
import com.pasterdream.pasterdreammod.registry.PDRecipeTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * 梦境炼药锅法术酿造配方 (dream_brewing)
 * 数据驱动配方（JSON 位于 data/pasterdream/recipe/dream_brewing/），
 * 结构与 {@link ShadowBlastFurnaceRecipe} 同款（MapCodec + StreamCodec），
 * 可通过数据包覆盖，也可由 KubeJS 按配方 ID 增删改。
 *
 * JSON 字段：
 * <ul>
 *   <li>{@code group} —— 配方分组（可选，默认空串）；</li>
 *   <li>{@code guiding_drug} —— 引导药剂（可选，默认 {@code pasterdream:guiding_drug}，
 *       对应通用材料槽 0）；</li>
 *   <li>{@code materials} —— 三种材料（必填，按序对应瓶槽 1-3）；</li>
 *   <li>{@code result} —— 产物（必填，1.21 物品栈格式 {@code {"id": ...}}）；</li>
 *   <li>{@code craft_tick} —— 酿造总时长（可选，默认 60 tick；材料消耗与产物写入
 *       固定发生在最后 2 tick，与原版 t=58/59/60 时序语义一致）。</li>
 * </ul>
 *
 * 注意：产物若引用 PasterDreamSpells 的法术物品，则在未安装该模组时
 * 本配方 JSON 解析失败并被 RecipeManager 跳过（伴随一条 error 日志），
 * 属于数据包生态的预期行为。
 */
public class DreamBrewingRecipe implements Recipe<DreamBrewingRecipe.DreamBrewingInput> {

    private final String group;
    private final Ingredient guidingDrug;
    private final List<Ingredient> materials;
    private final ItemStack result;
    private final int craftTick;

    /**
     * 构造梦境炼药锅法术配方
     *
     * @param group       配方分组（可为空串）
     * @param guidingDrug 引导药剂（对应通用材料槽 0）
     * @param materials   三种材料（按序对应瓶槽 1-3，数量必须为 3）
     * @param result      产物
     * @param craftTick   酿造总时长（tick，下限 4 以容纳关键时序点）
     */
    public DreamBrewingRecipe(String group, Ingredient guidingDrug, List<Ingredient> materials,
                              ItemStack result, int craftTick) {
        if (materials.size() != 3) {
            throw new IllegalArgumentException(
                    "dream_brewing recipe requires exactly 3 materials, got " + materials.size());
        }
        this.group = group;
        this.guidingDrug = guidingDrug;
        this.materials = List.copyOf(materials);
        this.result = result;
        this.craftTick = Math.max(4, craftTick);
    }

    @Override
    public boolean matches(DreamBrewingInput input, Level level) {
        return this.guidingDrug.test(input.guidingDrug())
                && this.materials.get(0).test(input.material1())
                && this.materials.get(1).test(input.material2())
                && this.materials.get(2).test(input.material3());
    }

    @Override
    public ItemStack assemble(DreamBrewingInput input, HolderLookup.Provider registries) {
        return this.result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(this.guidingDrug);
        list.addAll(this.materials);
        return list;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return this.result;
    }

    @Override
    public String getGroup() {
        return this.group;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return PDRecipeTypes.DREAM_BREWING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return PDRecipeTypes.DREAM_BREWING.get();
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(PDBlocks.DREAM_CAULDRON.get());
    }

    /**
     * 标记为特殊配方
     * <p>同暗影高炉：自定义配方类型不进入原版配方书（避免 "Unknown recipe category"
     * 警告），展示由 JEI 分类承担。</p>
     *
     * @return 恒为 true
     */
    @Override
    public boolean isSpecial() {
        return true;
    }

    /**
     * 获取引导药剂
     *
     * @return 引导药剂 Ingredient
     */
    public Ingredient getGuidingDrug() {
        return this.guidingDrug;
    }

    /**
     * 获取三种材料
     *
     * @return 材料列表（按序对应瓶槽 1-3，固定 3 个）
     */
    public List<Ingredient> getMaterials() {
        return this.materials;
    }

    /**
     * 获取酿造总时长
     *
     * @return 酿造时长（tick）
     */
    public int getCraftTick() {
        return this.craftTick;
    }

    /**
     * 法术配方输入（坩埚槽 0-3 的物品快照）
     *
     * @param guidingDrug 槽 0 通用材料槽物品（法术配方应为引导药剂）
     * @param material1   槽 1 瓶槽物品（法术配方中为材料位）
     * @param material2   槽 2 瓶槽物品
     * @param material3   槽 3 瓶槽物品
     */
    public record DreamBrewingInput(ItemStack guidingDrug, ItemStack material1,
                                    ItemStack material2, ItemStack material3) implements RecipeInput {

        /**
         * 获取指定槽位物品
         *
         * @param index 槽位索引（0-3）
         * @return 对应物品
         */
        @Override
        public ItemStack getItem(int index) {
            return switch (index) {
                case 0 -> this.guidingDrug;
                case 1 -> this.material1;
                case 2 -> this.material2;
                case 3 -> this.material3;
                default -> throw new IllegalArgumentException("Recipe input index out of range: " + index);
            };
        }

        /**
         * 获取输入槽位数
         *
         * @return 恒为 4
         */
        @Override
        public int size() {
            return 4;
        }
    }

    /**
     * 梦境炼药锅配方序列化器（MapCodec + StreamCodec）
     */
    public static class Serializer implements RecipeSerializer<DreamBrewingRecipe> {

        /** JSON 编解码器：materials 数量校验在构造器中执行（数量不符则解析失败跳过） */
        public static final MapCodec<DreamBrewingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.optionalFieldOf("group", "").forGetter(DreamBrewingRecipe::getGroup),
                Ingredient.CODEC_NONEMPTY.optionalFieldOf("guiding_drug",
                        Ingredient.of(PDItems.GUIDING_DRUG.get())).forGetter(DreamBrewingRecipe::getGuidingDrug),
                Ingredient.CODEC_NONEMPTY.listOf().fieldOf("materials").forGetter(DreamBrewingRecipe::getMaterials),
                ItemStack.OPTIONAL_CODEC.fieldOf("result").forGetter(r -> r.result),
                Codec.INT.optionalFieldOf("craft_tick", 60).forGetter(DreamBrewingRecipe::getCraftTick)
        ).apply(instance, DreamBrewingRecipe::new));

        /** 网络同步编解码器（引导药剂 + 三材料逐个传输，规避列表长度可变） */
        public static final StreamCodec<RegistryFriendlyByteBuf, DreamBrewingRecipe> STREAM_CODEC = StreamCodec.of(
                Serializer::toNetwork, Serializer::fromNetwork);

        @Override
        public MapCodec<DreamBrewingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, DreamBrewingRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        /**
         * 从网络缓冲区读取配方
         *
         * @param buffer 网络缓冲区
         * @return 配方实例
         */
        private static DreamBrewingRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            String group = buffer.readUtf();
            Ingredient guiding = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
            Ingredient m1 = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
            Ingredient m2 = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
            Ingredient m3 = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
            ItemStack result = ItemStack.OPTIONAL_STREAM_CODEC.decode(buffer);
            int craftTick = buffer.readVarInt();
            return new DreamBrewingRecipe(group, guiding, List.of(m1, m2, m3), result, craftTick);
        }

        /**
         * 把配方写入网络缓冲区
         *
         * @param buffer 网络缓冲区
         * @param recipe 配方实例
         */
        private static void toNetwork(RegistryFriendlyByteBuf buffer, DreamBrewingRecipe recipe) {
            buffer.writeUtf(recipe.group);
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.guidingDrug);
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.materials.get(0));
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.materials.get(1));
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.materials.get(2));
            ItemStack.OPTIONAL_STREAM_CODEC.encode(buffer, recipe.result);
            buffer.writeVarInt(recipe.craftTick);
        }
    }
}
