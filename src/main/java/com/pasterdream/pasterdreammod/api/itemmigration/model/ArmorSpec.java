package com.pasterdream.pasterdreammod.api.itemmigration.model;

import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Rarity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 盔甲属性规范 record
 * <p>
 * 封装了盔甲类物品的完整属性，包括盔甲类型、材料、防御力、韧性、
 * 附魔能力、耐久度倍率以及属性修饰器列表。
 * 提供 Builder 模式以支持链式构建。
 * </p>
 *
 * @param armorType         盔甲部位类型（HELMET、CHESTPLATE、LEGGINGS、BOOTS）
 * @param armorMaterial     盔甲材料名称（如 "pasterdream:dyedream"），用于查找实际材料注册
 * @param defense           基础防御值
 * @param toughness         盔甲韧性值
 * @param enchantmentValue  附魔能力
 * @param durabilityMultiplier 耐久度倍率（相对于基础值）
 * @param knockbackResistance 击退抗性（0.0 ~ 1.0）
 * @param fireResistant     是否防火
 * @param rarity            稀有度，默认 COMMON
 * @param attributeMods     属性修饰器列表
 */
public record ArmorSpec(
        ArmorItem.Type armorType,
        String armorMaterial,
        int defense,
        float toughness,
        int enchantmentValue,
        int durabilityMultiplier,
        float knockbackResistance,
        boolean fireResistant,
        Rarity rarity,
        List<AttributeModSpec> attributeMods
) {

    /**
     * 创建一个新的 Builder 实例，用于链式构建 ArmorSpec
     *
     * @param armorType     盔甲部位类型
     * @param armorMaterial 盔甲材料名称
     * @return 新的 Builder 实例
     */
    public static Builder builder(ArmorItem.Type armorType, String armorMaterial) {
        return new Builder(armorType, armorMaterial);
    }

    /**
     * ArmorSpec 的构建器类，支持链式调用
     */
    public static final class Builder {
        private final ArmorItem.Type armorType;
        private final String armorMaterial;
        private int defense = 0;
        private float toughness = 0.0f;
        private int enchantmentValue = 5;
        private int durabilityMultiplier = 1;
        private float knockbackResistance = 0.0f;
        private boolean fireResistant = false;
        private Rarity rarity = Rarity.COMMON;
        private List<AttributeModSpec> attributeMods = Collections.emptyList();

        private Builder(ArmorItem.Type armorType, String armorMaterial) {
            this.armorType = armorType;
            this.armorMaterial = armorMaterial;
        }

        /**
         * 设置基础防御值
         *
         * @param defense 防御值
         * @return 当前 Builder 实例
         */
        public Builder defense(int defense) {
            this.defense = defense;
            return this;
        }

        /**
         * 设置盔甲韧性值
         *
         * @param toughness 韧性值
         * @return 当前 Builder 实例
         */
        public Builder toughness(float toughness) {
            this.toughness = toughness;
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
         * 设置耐久度倍率
         *
         * @param durabilityMultiplier 耐久度倍率
         * @return 当前 Builder 实例
         */
        public Builder durabilityMultiplier(int durabilityMultiplier) {
            this.durabilityMultiplier = durabilityMultiplier;
            return this;
        }

        /**
         * 设置击退抗性
         *
         * @param knockbackResistance 击退抗性（0.0 ~ 1.0）
         * @return 当前 Builder 实例
         */
        public Builder knockbackResistance(float knockbackResistance) {
            this.knockbackResistance = knockbackResistance;
            return this;
        }

        /**
         * 设置是否防火
         *
         * @param fireResistant 是否防火
         * @return 当前 Builder 实例
         */
        public Builder fireResistant(boolean fireResistant) {
            this.fireResistant = fireResistant;
            return this;
        }

        /**
         * 设置稀有度
         *
         * @param rarity 稀有度枚举值
         * @return 当前 Builder 实例
         */
        public Builder rarity(Rarity rarity) {
            this.rarity = rarity;
            return this;
        }

        /**
         * 设置属性修饰器列表
         *
         * @param attributeMods 属性修饰器规范列表
         * @return 当前 Builder 实例
         */
        public Builder attributeMods(List<AttributeModSpec> attributeMods) {
            this.attributeMods = attributeMods != null
                    ? Collections.unmodifiableList(new ArrayList<>(attributeMods))
                    : Collections.emptyList();
            return this;
        }

        /**
         * 构建最终的 ArmorSpec 实例
         *
         * @return 构建完成的 ArmorSpec
         */
        public ArmorSpec build() {
            return new ArmorSpec(
                    armorType, armorMaterial, defense, toughness,
                    enchantmentValue, durabilityMultiplier, knockbackResistance,
                    fireResistant, rarity, attributeMods
            );
        }
    }
}
