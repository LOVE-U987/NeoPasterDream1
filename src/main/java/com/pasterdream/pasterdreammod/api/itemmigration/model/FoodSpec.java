package com.pasterdream.pasterdreammod.api.itemmigration.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 食物属性规范 record
 * <p>
 * 封装了 Minecraft 食物物品的完整属性，包括营养值、饱和度、食用方式
 * 以及食用后附加的状态效果列表。提供 Builder 模式以支持链式构建。
 * </p>
 *
 * @param nutrition        营养值（半鸡腿数），如 4 表示 2 个鸡腿
 * @param saturationModifier 饱和度修正系数
 * @param alwaysEdible     是否始终可食（饥饿值满时也可食用），默认 false
 * @param fastFood         是否快速食用（不播放进食动画），默认 false
 * @param effects          食用后触发的状态效果列表，可选，默认空列表
 */
public record FoodSpec(
        int nutrition,
        float saturationModifier,
        boolean alwaysEdible,
        boolean fastFood,
        List<FoodEffectSpec> effects
) {

    /**
     * 创建一个新的 Builder 实例，用于链式构建 FoodSpec
     *
     * @param nutrition          营养值
     * @param saturationModifier 饱和度修正系数
     * @return 新的 Builder 实例
     */
    public static Builder builder(int nutrition, float saturationModifier) {
        return new Builder(nutrition, saturationModifier);
    }

    /**
     * FoodSpec 的构建器类，支持链式调用
     */
    public static final class Builder {
        private final int nutrition;
        private final float saturationModifier;
        private boolean alwaysEdible = false;
        private boolean fastFood = false;
        private List<FoodEffectSpec> effects = Collections.emptyList();

        private Builder(int nutrition, float saturationModifier) {
            this.nutrition = nutrition;
            this.saturationModifier = saturationModifier;
        }

        /**
         * 设置是否始终可食（饥饿值满时也可食用）
         *
         * @param alwaysEdible 是否始终可食
         * @return 当前 Builder 实例
         */
        public Builder alwaysEdible(boolean alwaysEdible) {
            this.alwaysEdible = alwaysEdible;
            return this;
        }

        /**
         * 设置是否快速食用（不播放进食动画，适合饮料类物品）
         *
         * @param fastFood 是否快速食用
         * @return 当前 Builder 实例
         */
        public Builder fastFood(boolean fastFood) {
            this.fastFood = fastFood;
            return this;
        }

        /**
         * 设置食用后触发的状态效果列表
         *
         * @param effects 状态效果规范列表
         * @return 当前 Builder 实例
         */
        public Builder effects(List<FoodEffectSpec> effects) {
            this.effects = effects != null
                    ? Collections.unmodifiableList(new ArrayList<>(effects))
                    : Collections.emptyList();
            return this;
        }

        /**
         * 构建最终的 FoodSpec 实例
         *
         * @return 构建完成的 FoodSpec
         */
        public FoodSpec build() {
            return new FoodSpec(
                    nutrition,
                    saturationModifier,
                    alwaysEdible,
                    fastFood,
                    effects
            );
        }
    }

    /**
     * 食用后状态效果规范
     * <p>
     * 描述食物被食用后触发的状态效果，包括效果 ID、持续时间、等级和触发概率。
     * </p>
     *
     * @param effectId    效果注册 ID，如 "minecraft:regeneration"
     * @param duration    效果持续时间（单位为 tick，20 tick = 1 秒）
     * @param amplifier   效果等级（0 为 I 级，1 为 II 级，以此类推）
     * @param probability 触发概率（0.0 ~ 1.0），默认 1.0 表示必定触发
     */
    public record FoodEffectSpec(
            String effectId,
            int duration,
            int amplifier,
            float probability
    ) {

        /**
         * 创建一个新的 Builder 实例，用于链式构建 FoodEffectSpec
         *
         * @param effectId 效果注册 ID
         * @param duration 效果持续时间（tick）
         * @param amplifier 效果等级
         * @return 新的 Builder 实例
         */
        public static Builder builder(String effectId, int duration, int amplifier) {
            return new Builder(effectId, duration, amplifier);
        }

        /**
         * FoodEffectSpec 的构建器类，支持链式调用
         */
        public static final class Builder {
            private final String effectId;
            private final int duration;
            private final int amplifier;
            private float probability = 1.0f;

            private Builder(String effectId, int duration, int amplifier) {
                this.effectId = effectId;
                this.duration = duration;
                this.amplifier = amplifier;
            }

            /**
             * 设置状态效果触发概率
             *
             * @param probability 触发概率（0.0 ~ 1.0）
             * @return 当前 Builder 实例
             */
            public Builder probability(float probability) {
                this.probability = probability;
                return this;
            }

            /**
             * 构建最终的 FoodEffectSpec 实例
             *
             * @return 构建完成的 FoodEffectSpec
             */
            public FoodEffectSpec build() {
                return new FoodEffectSpec(effectId, duration, amplifier, probability);
            }
        }
    }
}
