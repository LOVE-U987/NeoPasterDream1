package com.pasterdream.pasterdreammod.api.itemmigration.builder;

import com.pasterdream.pasterdreammod.api.itemmigration.model.FoodSpec;
import com.pasterdream.pasterdreammod.api.itemmigration.model.FoodSpec.FoodEffectSpec;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;

/**
 * 食物物品构建器 —— 用于注册带有食物属性的物品
 * <p>
 * 支持设置营养值、饱和度、是否始终可食、是否快速食用，
 * 以及食用后附加的状态效果（通过效果 ID 引用）。
 * 继承 {@link BaseItemBuilder}，同样支持 tooltip 描述文本。
 * </p>
 *
 * 使用示例：
 * <pre>{@code
 * new FoodItemBuilder(ITEMS, "apple_juice")
 *     .nutrition(4).saturationModifier(0.2f)
 *     .alwaysEdible()
 *     .effect("minecraft:regeneration", 100, 0, 1.0f)
 *     .build();
 * }</pre>
 */
public class FoodItemBuilder extends BaseItemBuilder<FoodItemBuilder> {

    /** 食物属性规范，由链式方法逐步填充 */
    private FoodSpec foodSpec;

    /**
     * 构造食物物品构建器
     *
     * @param registry     物品注册器
     * @param registryName 物品注册名（snake_case 格式）
     */
    public FoodItemBuilder(DeferredRegister.Items registry, String registryName) {
        super(registry, registryName);
        this.foodSpec = new FoodSpec(0, 0.0f, false, false, List.of());
    }

    /**
     * 设置营养值
     *
     * @param nutrition 营养值（半鸡腿数），如 4 表示 2 个鸡腿
     * @return 当前构建器实例
     */
    public FoodItemBuilder nutrition(int nutrition) {
        this.foodSpec = new FoodSpec(nutrition, foodSpec.saturationModifier(),
                foodSpec.alwaysEdible(), foodSpec.fastFood(), foodSpec.effects());
        return this;
    }

    /**
     * 设置饱和度修正系数
     *
     * @param saturationModifier 饱和度修正系数
     * @return 当前构建器实例
     */
    public FoodItemBuilder saturationModifier(float saturationModifier) {
        this.foodSpec = new FoodSpec(foodSpec.nutrition(), saturationModifier,
                foodSpec.alwaysEdible(), foodSpec.fastFood(), foodSpec.effects());
        return this;
    }

    /**
     * 设置食物始终可食（饥饿值满时也可食用）
     *
     * @return 当前构建器实例
     */
    public FoodItemBuilder alwaysEdible() {
        this.foodSpec = new FoodSpec(foodSpec.nutrition(), foodSpec.saturationModifier(),
                true, foodSpec.fastFood(), foodSpec.effects());
        return this;
    }

    /**
     * 设置食物可快速食用（不播放进食动画）
     *
     * @return 当前构建器实例
     */
    public FoodItemBuilder fastFood() {
        this.foodSpec = new FoodSpec(foodSpec.nutrition(), foodSpec.saturationModifier(),
                foodSpec.alwaysEdible(), true, foodSpec.effects());
        return this;
    }

    /**
     * 添加食用后触发的状态效果
     *
     * @param effectId    效果注册 ID，如 "minecraft:regeneration"
     * @param duration    效果持续时间（单位为 tick，20 tick = 1 秒）
     * @param amplifier   效果等级（0 为 I 级，1 为 II 级）
     * @param probability 触发概率（0.0 ~ 1.0），1.0 表示必定触发
     * @return 当前构建器实例
     */
    public FoodItemBuilder effect(String effectId, int duration, int amplifier, float probability) {
        List<FoodEffectSpec> newEffects = new ArrayList<>(foodSpec.effects());
        newEffects.add(new FoodEffectSpec(effectId, duration, amplifier, probability));
        this.foodSpec = new FoodSpec(foodSpec.nutrition(), foodSpec.saturationModifier(),
                foodSpec.alwaysEdible(), foodSpec.fastFood(), List.copyOf(newEffects));
        return this;
    }

    /**
     * 执行食物物品的创建和注册
     * <p>
     * 根据 {@link FoodSpec} 构建 {@link FoodProperties}，
     * 将效果 ID 转换为 {@link MobEffect} 实例，
     * 如果设置了 tooltip 行则添加描述文本。
     * </p>
     *
     * @return 注册完成的 DeferredItem，泛型为 {@link Item}
     */
    @Override
    public DeferredItem<Item> build() {
        FoodProperties.Builder foodBuilder = new FoodProperties.Builder()
                .nutrition(foodSpec.nutrition())
                .saturationModifier(foodSpec.saturationModifier());
        if (foodSpec.alwaysEdible()) {
            foodBuilder.alwaysEdible();
        }
        if (foodSpec.fastFood()) {
            foodBuilder.fast();
        }
        for (FoodEffectSpec effect : foodSpec.effects()) {
            Holder<MobEffect> mobEffect = BuiltInRegistries.MOB_EFFECT
                    .getHolder(ResourceLocation.parse(effect.effectId()))
                    .orElse(null);
            if (mobEffect != null) {
                foodBuilder.effect(() -> new MobEffectInstance(mobEffect, effect.duration(), effect.amplifier()),
                        effect.probability());
            }
        }

        FoodProperties foodProperties = foodBuilder.build();
        Item.Properties props = applyProperties().food(foodProperties);

        if (tooltipLines.isEmpty()) {
            return registry.register(registryName, () -> new Item(props));
        }

        List<String> lines = List.copyOf(this.tooltipLines);
        return registry.register(registryName, () -> new Item(props) {
            @Override
            public void appendHoverText(ItemStack stack, Item.TooltipContext context,
                                        List<Component> tooltip, TooltipFlag flag) {
                super.appendHoverText(stack, context, tooltip, flag);
                for (String line : lines) {
                    tooltip.add(Component.literal(line));
                }
            }
        });
    }

    /**
     * 快速创建并注册食物物品的静态工厂方法
     * <p>
     * 使用指定的营养值和饱和度创建一个食物物品并立即注册。
     * 如需自定义属性（效果、tooltip 等）请使用构造器 + 链式方法。
     * </p>
     *
     * @param registry           物品注册器
     * @param registryName       物品注册名
     * @param nutrition          营养值
     * @param saturationModifier 饱和度修正系数
     * @return 注册完成的 DeferredItem
     */
    public static DeferredItem<Item> create(DeferredRegister.Items registry, String registryName,
                                            int nutrition, float saturationModifier) {
        return new FoodItemBuilder(registry, registryName)
                .nutrition(nutrition)
                .saturationModifier(saturationModifier)
                .build();
    }
}
