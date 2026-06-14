package com.pasterdream.pasterdreammod.api.curio.model;

import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import java.util.Objects;

/**
 * 饰品属性修饰器规范 —— 描述一个属性修饰器所需的所有信息
 * <p>
 * 使用 record 保证不可变性，通过 {@link CurioAttributeModBuilder} 构建。
 * </p>
 *
 * @param attributeId 属性注册名（如 "minecraft:generic.attack_damage"）
 * @param uuid        修饰器唯一标识 UUID 字符串
 * @param amount      修饰值
 * @param operation   修饰运算方式（0=加法, 1=倍率加法, 2=倍率乘法）
 */
public record CurioAttributeMod(
        String attributeId,
        String uuid,
        double amount,
        int operation
) {

    /**
     * 校验参数合法性
     */
    public CurioAttributeMod {
        Objects.requireNonNull(attributeId, "[CurioAttributeMod] attributeId 不能为空");
        Objects.requireNonNull(uuid, "[CurioAttributeMod] uuid 不能为空");
        if (operation < 0 || operation > 2) {
            throw new IllegalArgumentException(
                    "[CurioAttributeMod] operation 必须在 0~2 之间: " + operation);
        }
    }

    /**
     * 获取对应的 {@link AttributeModifier.Operation} 枚举值。
     *
     * @return 属性修饰器运算方式
     */
    public AttributeModifier.Operation toOperation() {
        return switch (operation) {
            case 0 -> AttributeModifier.Operation.ADD_VALUE;
            case 1 -> AttributeModifier.Operation.ADD_MULTIPLIED_BASE;
            case 2 -> AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL;
            default -> AttributeModifier.Operation.ADD_VALUE;
        };
    }

    // ======================== Builder ========================

    /**
     * 创建一个新的属性修饰器构建器。
     *
     * @param attributeId 属性注册名
     * @return 构建器实例
     */
    public static CurioAttributeModBuilder builder(String attributeId) {
        return new CurioAttributeModBuilder(attributeId);
    }

    /**
     * CurioAttributeMod 构建器 —— 链式设置属性修饰器参数。
     */
    public static class CurioAttributeModBuilder {
        private final String attributeId;
        private String uuid;
        private double amount;
        private int operation;

        CurioAttributeModBuilder(String attributeId) {
            this.attributeId = Objects.requireNonNull(attributeId, "attributeId 不能为空");
        }

        /**
         * 设置修饰器唯一标识。
         *
         * @param uuid UUID 字符串
         * @return 当前构建器
         */
        public CurioAttributeModBuilder uuid(String uuid) {
            this.uuid = uuid;
            return this;
        }

        /**
         * 设置修饰值。
         *
         * @param amount 修饰值
         * @return 当前构建器
         */
        public CurioAttributeModBuilder amount(double amount) {
            this.amount = amount;
            return this;
        }

        /**
         * 设置修饰运算方式。
         *
         * @param operation 0=加法, 1=倍率加法, 2=倍率乘法
         * @return 当前构建器
         */
        public CurioAttributeModBuilder operation(int operation) {
            this.operation = operation;
            return this;
        }

        /**
         * 构建 {@link CurioAttributeMod} 实例。
         *
         * @return 属性修饰器规范
         */
        public CurioAttributeMod build() {
            Objects.requireNonNull(uuid, "[CurioAttributeMod] uuid 未设置");
            return new CurioAttributeMod(attributeId, uuid, amount, operation);
        }
    }
}