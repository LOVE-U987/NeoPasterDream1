package com.pasterdream.pasterdreammod.api.itemmigration.model;

import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Supplier;

/**
 * 工具属性规范 record
 * <p>
 * 封装了工具类物品的完整属性，包括工具类型、耐久度、挖掘速度、攻击伤害、
 * 攻击速度、附魔能力、不适用标签以及修复材料供应。提供 Builder 模式以支持链式构建。
 * </p>
 *
 * @param type             工具类型枚举
 * @param durability       耐久度，默认 250
 * @param miningSpeed      挖掘速度，默认 2.0
 * @param attackDamage     基础攻击伤害加成，默认 1.0
 * @param attackSpeed      攻击速度，默认 -2.4
 * @param enchantmentValue 附魔能力，默认 5
 * @param incorrectTag     不适用标签 ID（用于标记该工具无法有效挖掘的方块），默认 "minecraft:incorrect_for_wooden_tool"
 * @param repairIngredient 修复材料供应者（函数式接口），默认返回空 Ingredient
 */
public record ToolSpec(
        ToolType type,
        int durability,
        float miningSpeed,
        float attackDamage,
        float attackSpeed,
        int enchantmentValue,
        String incorrectTag,
        IngredientSupplier repairIngredient
) {

    /**
     * 创建一个新的 Builder 实例，用于链式构建 ToolSpec
     *
     * @param type 工具类型
     * @return 新的 Builder 实例
     */
    public static Builder builder(ToolType type) {
        return new Builder(type);
    }

    /**
     * ToolSpec 的构建器类，支持链式调用
     */
    public static final class Builder {
        private final ToolType type;
        private int durability = 250;
        private float miningSpeed = 2.0f;
        private float attackDamage = 1.0f;
        private float attackSpeed = -2.4f;
        private int enchantmentValue = 5;
        private String incorrectTag = "minecraft:incorrect_for_wooden_tool";
        private IngredientSupplier repairIngredient = () -> Ingredient.EMPTY;

        private Builder(ToolType type) {
            this.type = type;
        }

        /**
         * 设置工具耐久度
         *
         * @param durability 耐久度值
         * @return 当前 Builder 实例
         */
        public Builder durability(int durability) {
            this.durability = durability;
            return this;
        }

        /**
         * 设置挖掘速度
         *
         * @param miningSpeed 挖掘速度
         * @return 当前 Builder 实例
         */
        public Builder miningSpeed(float miningSpeed) {
            this.miningSpeed = miningSpeed;
            return this;
        }

        /**
         * 设置基础攻击伤害加成
         *
         * @param attackDamage 攻击伤害加成
         * @return 当前 Builder 实例
         */
        public Builder attackDamage(float attackDamage) {
            this.attackDamage = attackDamage;
            return this;
        }

        /**
         * 设置攻击速度
         *
         * @param attackSpeed 攻击速度
         * @return 当前 Builder 实例
         */
        public Builder attackSpeed(float attackSpeed) {
            this.attackSpeed = attackSpeed;
            return this;
        }

        /**
         * 设置附魔能力
         *
         * @param enchantmentValue 附魔能力值
         * @return 当前 Builder 实例
         */
        public Builder enchantmentValue(int enchantmentValue) {
            this.enchantmentValue = enchantmentValue;
            return this;
        }

        /**
         * 设置不适用标签 ID（用于标记该工具无法有效挖掘的方块）
         *
         * @param incorrectTag 不适用标签 ID
         * @return 当前 Builder 实例
         */
        public Builder incorrectTag(String incorrectTag) {
            this.incorrectTag = incorrectTag;
            return this;
        }

        /**
         * 设置修复材料供应者
         *
         * @param repairIngredient 修复材料供应者
         * @return 当前 Builder 实例
         */
        public Builder repairIngredient(IngredientSupplier repairIngredient) {
            this.repairIngredient = repairIngredient;
            return this;
        }

        /**
         * 构建最终的 ToolSpec 实例
         *
         * @return 构建完成的 ToolSpec
         */
        public ToolSpec build() {
            return new ToolSpec(
                    type,
                    durability,
                    miningSpeed,
                    attackDamage,
                    attackSpeed,
                    enchantmentValue,
                    incorrectTag,
                    repairIngredient
            );
        }
    }

    /**
     * 工具类型枚举
     * <p>
     * 定义了所有支持的工具类型，包括原版五种工具以及模组自定义的锤子和法杖。
     * </p>
     */
    public enum ToolType {
        /** 剑 */
        SWORD,
        /** 镐 */
        PICKAXE,
        /** 斧 */
        AXE,
        /** 锹 */
        SHOVEL,
        /** 锄 */
        HOE,
        /** 锤（模组自定义） */
        HAMMER,
        /** 法杖（模组自定义） */
        WAND
    }

    /**
     * 修复材料供应者函数式接口
     * <p>
     * 用于延迟提供修复工具所需的 Ingredient，通常在工具需要从多个物品中
     * 选择修复材料时使用。例如 {@code () -> Ingredient.of(Items.IRON_INGOT)}。
     * </p>
     */
    @FunctionalInterface
    public interface IngredientSupplier extends Supplier<Ingredient> {
    }
}
