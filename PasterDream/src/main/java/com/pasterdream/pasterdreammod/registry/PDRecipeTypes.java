package com.pasterdream.pasterdreammod.registry;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.recipe.DreamBrewingRecipe;
import com.pasterdream.pasterdreammod.recipe.ShadowBlastFurnaceRecipe;
import com.pasterdream.pasterdreammod.recipe.WeaponWorkshopRecipe;
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
 * <ul>
 *   <li>暗影高炉 {@code pasterdream:shadow_blasting} 数据驱动配方
 *       （数据包路径 data/pasterdream/recipe/shadow_blasting/）；</li>
 *   <li>梦境炼药锅 {@code pasterdream:dream_brewing} 法术酿造数据驱动配方
 *       （数据包路径 data/pasterdream/recipe/dream_brewing/，替代原硬编码配方，
 *       支持数据包覆盖与 KubeJS 增删改）；</li>
 *   <li>精铸工坊 {@code pasterdream:weapon_forge} 锻造数据驱动配方
 *       （数据包路径 data/pasterdream/recipe/weapon_forge/，由原 BE 硬编码
 *       13 项配方表重构而来）。</li>
 * </ul>
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

    /** 梦境炼药锅法术酿造配方类型（注册名 dream_brewing，数据包 dream_brewing 目录） */
    public static final DeferredHolder<RecipeType<?>, RecipeType<DreamBrewingRecipe>> DREAM_BREWING =
            RECIPE_TYPES.register("dream_brewing", () -> RecipeType.simple(
                    net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "dream_brewing")));

    /** 梦境炼药锅法术酿造配方序列化器 */
    public static final DeferredHolder<RecipeSerializer<?>, DreamBrewingRecipe.Serializer> DREAM_BREWING_SERIALIZER =
            RECIPE_SERIALIZERS.register("dream_brewing", DreamBrewingRecipe.Serializer::new);

    /** 精铸工坊锻造配方类型（注册名 weapon_forge，数据包 weapon_forge 目录） */
    public static final DeferredHolder<RecipeType<?>, RecipeType<WeaponWorkshopRecipe>> WEAPON_FORGE =
            RECIPE_TYPES.register("weapon_forge", () -> RecipeType.simple(
                    net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "weapon_forge")));

    /** 精铸工坊锻造配方序列化器 */
    public static final DeferredHolder<RecipeSerializer<?>, WeaponWorkshopRecipe.Serializer> WEAPON_FORGE_SERIALIZER =
            RECIPE_SERIALIZERS.register("weapon_forge", WeaponWorkshopRecipe.Serializer::new);

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
