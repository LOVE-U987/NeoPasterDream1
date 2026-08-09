package com.pasterdream.pasterdreammod.registry.items;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.api.curio.CurioAPI;
import com.pasterdream.pasterdreammod.api.curio.model.CurioSlot;
import com.pasterdream.pasterdreammod.api.effect.MobEffectAPI;
import com.pasterdream.pasterdreammod.api.entity.EntityAPI;
import com.pasterdream.pasterdreammod.api.item.ItemAPI;
import com.pasterdream.pasterdreammod.api.item.model.MigrationCategory;
import com.pasterdream.pasterdreammod.api.item.model.ToolSpec.ToolType;
import com.pasterdream.pasterdreammod.item.*;
import com.pasterdream.pasterdreammod.registry.PDBlocks;
import com.pasterdream.pasterdreammod.registry.PDEffects;
import com.pasterdream.pasterdreammod.registry.PDEntities;
import com.pasterdream.pasterdreammod.registry.PDItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PlaceOnWaterBlockItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.food.FoodProperties;
import net.neoforged.neoforge.registries.DeferredItem;


/**
 * 食物类物品注册。
 *
 * @see PDItems
 */
public class PDItemsFoods {


    // ==================== 食物类物品 ====================

    public static final DeferredItem<Item> APPLE_JUICE = PDItems.ITEMS.register("apple_juice",
            () -> new GlassDrinkItem(new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationModifier(0.2f).alwaysEdible().build()), PDItems.GLASS_CUP::get));
    public static final DeferredItem<Item> BACONE_EGG = PDItems.ITEMS.registerSimpleItem("bacone_egg",
            new Item.Properties().food(new FoodProperties.Builder().nutrition(10).saturationModifier(1.2f)
                    .effect(() -> new MobEffectInstance(PDEffects.COOK_BUFF, 1200, 0), 1.0f).build()));
    public static final DeferredItem<Item> BERRY_BUNCAKE = PDItems.ITEMS.registerSimpleItem("berry_buncake",
            new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationModifier(0.5f).alwaysEdible().fast().build()));
    public static final DeferredItem<Item> BUBBLE_GUM = PDItems.ITEMS.registerSimpleItem("bubble_gum",
            new Item.Properties().food(new FoodProperties.Builder().nutrition(1).saturationModifier(0f).alwaysEdible().fast().build()));
    public static final DeferredItem<Item> CANDY_CANE = PDItems.ITEMS.registerSimpleItem("candy_cane",
            new Item.Properties().food(new FoodProperties.Builder().nutrition(3).saturationModifier(0.5f).build()));
    public static final DeferredItem<Item> CHOCOLATE = PDItems.ITEMS.registerSimpleItem("chocolate",
            new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationModifier(0.375f).build()));
    public static final DeferredItem<Item> CHOCOLATE_MATCHA_CAKE = PDItems.ITEMS.registerSimpleItem("chocolate_matcha_cake",
            new Item.Properties().food(new FoodProperties.Builder().nutrition(8).saturationModifier(1.0f).build()));
    public static final DeferredItem<Item> CREAM_BUNCAKE = PDItems.ITEMS.registerSimpleItem("cream_buncake",
            new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationModifier(0.5f).alwaysEdible().fast().build()));
    public static final DeferredItem<Item> DREAM_COTTON_CANDY = PDItems.ITEMS.registerSimpleItem("dream_cotton_candy",
            new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationModifier(0.625f).alwaysEdible().build()));
    public static final DeferredItem<Item> DYEDREAM_FLOWER_TEA = PDItems.ITEMS.register("dyedream_flower_tea",
            () -> new GlassDrinkItem(new Item.Properties().food(new FoodProperties.Builder().nutrition(0).saturationModifier(0f).alwaysEdible().build()), PDItems.GLASS_CUP::get));
    public static final DeferredItem<Item> DYEDREAM_FRUIT_BUNCAKE = PDItems.ITEMS.registerSimpleItem("dyedream_fruit_buncake",
            new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationModifier(0.5f).alwaysEdible().fast()
                    .effect(() -> new MobEffectInstance(PDEffects.COOK_BUFF, 1200, 0), 1.0f)
                    .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 200, 0), 1.0f).build()));
    public static final DeferredItem<Item> DYEDREAM_JUICE = PDItems.ITEMS.register("dyedream_juice",
            () -> new GlassDrinkItem(new Item.Properties().food(new FoodProperties.Builder().nutrition(1).saturationModifier(0.2f).alwaysEdible().build()), PDItems.GLASS_CUP::get));
    public static final DeferredItem<Item> DYEDREAM_POPSICLE = PDItems.ITEMS.registerSimpleItem("dyedream_popsicle",
            new Item.Properties().food(new FoodProperties.Builder().nutrition(1).saturationModifier(0.4f).build()));
    public static final DeferredItem<Item> FRIED_EGG = PDItems.ITEMS.registerSimpleItem("fried_egg",
            new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationModifier(0.2f).build()));
    public static final DeferredItem<Item> GINGERBREAD_MAN = PDItems.ITEMS.registerSimpleItem("gingerbread_man",
            new Item.Properties().food(new FoodProperties.Builder().nutrition(5).saturationModifier(0.6f).build()));
    public static final DeferredItem<Item> GLOW_BERRY_BUNCAKE = PDItems.ITEMS.registerSimpleItem("glow_berry_buncake",
            new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationModifier(0.5f).alwaysEdible().fast().build()));
    public static final DeferredItem<Item> GOLDENROD_TEA = PDItems.ITEMS.register("goldenrod_tea",
            () -> new GlassDrinkItem(new Item.Properties().food(new FoodProperties.Builder().nutrition(1).saturationModifier(0f).alwaysEdible()
                    .effect(() -> new MobEffectInstance(PDEffects.GOLDENROD_TEA_BUFF.holder(), 3600, 0), 1.0f)
                    .build()), PDItems.GLASSJAR::get));
    public static final DeferredItem<Item> HONEY_JUICE = PDItems.ITEMS.register("honey_juice",
            () -> new GlassDrinkItem(new Item.Properties().food(new FoodProperties.Builder().nutrition(6).saturationModifier(0.1f).alwaysEdible().build()), PDItems.GLASS_CUP::get));
    public static final DeferredItem<Item> JELLYFISH_JELLO = PDItems.ITEMS.registerSimpleItem("jellyfish_jello",
            new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationModifier(0.4f).alwaysEdible()
                    .effect(() -> new MobEffectInstance(PDEffects.WINDPROOF_BUFF.holder(), 12000, 0), 1.0f).build()));
    public static final DeferredItem<Item> JELLYFISH_MUD = PDItems.ITEMS.registerSimpleItem("jellyfish_mud",
            new Item.Properties().food(new FoodProperties.Builder().nutrition(3).saturationModifier(0.3f).alwaysEdible()
                    .effect(() -> new MobEffectInstance(PDEffects.WINDPROOF_BUFF.holder(), 1200, 0), 1.0f).build()));
    public static final DeferredItem<Item> LEGEND_DRAGON_HORN_ICE_CREAM = PDItems.ITEMS.registerSimpleItem("legend_dragon_horn_ice_cream",
            new Item.Properties().food(new FoodProperties.Builder().nutrition(10).saturationModifier(1.2f).alwaysEdible().build()));
    public static final DeferredItem<Item> LIGHT_ORGAN = PDItems.ITEMS.registerSimpleItem("light_organ",
            new Item.Properties().food(new FoodProperties.Builder().nutrition(1).saturationModifier(0f).build()));
    public static final DeferredItem<Item> MELON_BUNCAKE = PDItems.ITEMS.registerSimpleItem("melon_buncake",
            new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationModifier(0.5f).alwaysEdible().fast()
                    .effect(() -> new MobEffectInstance(PDEffects.COOK_BUFF, 1200, 0), 1.0f)
                    .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 400, 0), 1.0f).build()));
    public static final DeferredItem<Item> MELTDREAM_ELIXIR_BOTTLE = PDItems.ITEMS.register("meltdream_elixir_bottle",
            () -> new GlassDrinkItem(new Item.Properties().stacksTo(1).food(new FoodProperties.Builder().nutrition(4).saturationModifier(0.25f).alwaysEdible().build()), PDItems.ELIXIR_BOTTLE::get));
    public static final DeferredItem<Item> MILK_GLASSJAR = PDItems.ITEMS.register("milk_glassjar",
            () -> new GlassDrinkItem(new Item.Properties().food(new FoodProperties.Builder().nutrition(0).saturationModifier(0f).alwaysEdible().build()), PDItems.GLASSJAR::get));
    public static final DeferredItem<Item> ODD_BACONE_EGG = PDItems.ITEMS.registerSimpleItem("odd_bacone_egg",
            new Item.Properties().food(new FoodProperties.Builder().nutrition(11).saturationModifier(1.5f).build()));
    public static final DeferredItem<Item> PINEAPPLE_LOVE_SEA = PDItems.ITEMS.register("pineapple_love_sea",
            () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(3).saturationModifier(0.5f).build())) {
                @Override
                public UseAnim getUseAnimation(ItemStack stack) {
                    return UseAnim.DRINK;
                }
            });
    public static final DeferredItem<Item> POTATO_BUNCAKE = PDItems.ITEMS.registerSimpleItem("potato_buncake",
            new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationModifier(0.5f).alwaysEdible().fast().build()));
    public static final DeferredItem<Item> PUMPKIN_BUNCAKE = PDItems.ITEMS.registerSimpleItem("pumpkin_buncake",
            new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationModifier(0.5f).alwaysEdible().fast().build()));
    /** 奇异炖菜 — 原版：迷梦 6000t + 融梦能量 +1 瞬时；食用时长 24；返还碗。风之旅途入口前置。 */
    public static final DeferredItem<Item> QUEER_SOUP = PDItems.ITEMS.register("queer_soup",
            () -> new GlassDrinkItem(new Item.Properties()
                    .craftRemainder(Items.BOWL)
                    .stacksTo(64)
                    .food(buildQueerSoupFood().build()), () -> Items.BOWL) {
                @Override
                public int getUseDuration(ItemStack stack, LivingEntity entity) {
                    return 24;
                }
            });
    public static final DeferredItem<Item> RAGE_ELIXIR_0 = PDItems.ITEMS.register("rage_elixir_0",
            () -> new GlassDrinkItem(new Item.Properties().stacksTo(1).food(new FoodProperties.Builder().nutrition(0).saturationModifier(0f).alwaysEdible().build()), PDItems.ELIXIR_BOTTLE::get));
    public static final DeferredItem<Item> RICECAKE = PDItems.ITEMS.registerSimpleItem("ricecake",
            new Item.Properties().food(new FoodProperties.Builder().nutrition(6).saturationModifier(0.4f).build()));
    public static final DeferredItem<Item> SANDWICH = PDItems.ITEMS.registerSimpleItem("sandwich",
            new Item.Properties().food(new FoodProperties.Builder().nutrition(10).saturationModifier(0.9f)
                    .effect(() -> new MobEffectInstance(PDEffects.COOK_BUFF, 1200, 0), 1.0f).build()));
    public static final DeferredItem<Item> STUFFED_WAFER_COOKIES = PDItems.ITEMS.registerSimpleItem("stuffed_wafer_cookies",
            new Item.Properties().food(new FoodProperties.Builder().nutrition(7).saturationModifier(1.0f).build()));
    public static final DeferredItem<Item> SWISS_ROLL = PDItems.ITEMS.registerSimpleItem("swiss_roll",
            new Item.Properties().food(new FoodProperties.Builder().nutrition(6).saturationModifier(0.8f)
                    .effect(() -> new MobEffectInstance(PDEffects.COOK_BUFF, 1200, 0), 1.0f).build()));
    public static final DeferredItem<Item> UNCOOKED_DYEDREAM_FLOWER_TEA = PDItems.ITEMS.register("uncooked_dyedream_flower_tea",
            () -> new GlassDrinkItem(new Item.Properties().food(new FoodProperties.Builder().nutrition(0).saturationModifier(0f).alwaysEdible().build()), PDItems.GLASS_CUP::get));
    public static final DeferredItem<Item> WATER_GLASSJAR = PDItems.ITEMS.register("water_glassjar",
            () -> new GlassDrinkItem(new Item.Properties().food(new FoodProperties.Builder().nutrition(0).saturationModifier(0f).alwaysEdible().build()), PDItems.GLASSJAR::get));
    public static final DeferredItem<Item> WATERMELON_JUICE = PDItems.ITEMS.register("watermelon_juice",
            () -> new GlassDrinkItem(new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationModifier(0.1f).alwaysEdible().build()), PDItems.GLASS_CUP::get));
    public static final DeferredItem<Item> SILVER_FOX_COTTON_CANDY = PDItems.ITEMS.register("silver_fox_cotton_candy",
            () -> {
                FoodProperties.Builder builder = new FoodProperties.Builder()
                        .nutrition(6).saturationModifier(0.75f).alwaysEdible();
                // San 相关效果已拆分到 PasterDreamSanity；未安装时不附加该效果
                BuiltInRegistries.MOB_EFFECT.getHolder(ResourceLocation.fromNamespaceAndPath("pasterdream", "san_increase"))
                        .ifPresent(holder -> builder.effect(() -> new MobEffectInstance(holder, 1, 9), 1.0f));
                // 融梦能量效果已拆分到 PasterDreamMeltDream；未安装时不附加该效果
                BuiltInRegistries.MOB_EFFECT.getHolder(ResourceLocation.fromNamespaceAndPath("pasterdream", "melt_dream_energy_increase"))
                        .ifPresent(holder -> builder.effect(() -> new MobEffectInstance(holder, 1, 19), 1.0f));
                return new Item(new Item.Properties().food(builder.build()));
            });

    /**
     * 构建奇异炖菜的食物属性。
     * <p>
     * 融梦能量增加效果已拆分到 PasterDreamMeltDream；未安装该附属模组时不附加该效果。
     *
     * @return 奇异炖菜食物属性构建器
     */
    private static FoodProperties.Builder buildQueerSoupFood() {
        FoodProperties.Builder builder = new FoodProperties.Builder()
                .nutrition(0)
                .saturationModifier(0f)
                .alwaysEdible()
                .effect(() -> new MobEffectInstance(PDEffects.FONDILLUSION_BUFF.holder(), 6000, 0), 1.0f);
        BuiltInRegistries.MOB_EFFECT.getHolder(ResourceLocation.fromNamespaceAndPath("pasterdream", "melt_dream_energy_increase"))
                .ifPresent(holder -> builder.effect(() -> new MobEffectInstance(holder, 1, 0), 1.0f));
        return builder;
    }

}
