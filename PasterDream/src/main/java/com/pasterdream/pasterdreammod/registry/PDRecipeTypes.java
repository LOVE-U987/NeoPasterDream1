package com.pasterdream.pasterdreammod.registry;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.recipe.ShadowBlastFurnaceRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 自定义配方类型注册类
 * <p>
 * 还原原版 {@code PasterdreamModRecipeType} / {@code PasterdreamModRecipeSerializers}：
 * 目前仅有暗影高炉的 {@code pasterdream:shadow_blasting} 数据驱动配方
 * （数据包路径 data/pasterdream/recipe/shadow_blasting/）。
 */
public class PDRecipeTypes {

    /** 配方类型注册器 */
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, PasterDreamMod.MOD_ID);

    /** 配方序列化器注册器 */
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, PasterDreamMod.MOD_ID);

    /** 暗影高炉冶炼配方类型（原版 SHADOW_BLAST_FURNACE，注册名 shadow_blasting） */
    public static final DeferredHolder<RecipeType<?>, RecipeType<ShadowBlastFurnaceRecipe>> SHADOW_BLASTING =
            RECIPE_TYPES.register("shadow_blasting", () -> RecipeType.simple(
                    net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "shadow_blasting")));

    /** 暗影高炉冶炼配方序列化器 */
    public static final DeferredHolder<RecipeSerializer<?>, ShadowBlastFurnaceRecipe.Serializer> SHADOW_BLASTING_SERIALIZER =
            RECIPE_SERIALIZERS.register("shadow_blasting", ShadowBlastFurnaceRecipe.Serializer::new);

    /**
     * 注册配方类型与序列化器到模组事件总线
     *
     * @param modEventBus 模组事件总线
     */
    public static void register(IEventBus modEventBus) {
        RECIPE_TYPES.register(modEventBus);
        RECIPE_SERIALIZERS.register(modEventBus);
    }
}
