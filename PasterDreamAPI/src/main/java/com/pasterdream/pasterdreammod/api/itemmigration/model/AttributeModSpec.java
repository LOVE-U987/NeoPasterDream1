package com.pasterdream.pasterdreammod.api.itemmigration.model;

/**
 * 属性修饰器规范 record
 * <p>
 * 描述 Curio 饰品或工具对玩家属性（如移动速度、攻击伤害等）的修饰效果。
 * 用于在 {@link CurioSpec} 中定义饰品佩戴时的属性加成。
 * </p>
 *
 * @param attributeName 属性注册名，如 "minecraft:generic.attack_damage" 或 "pasterdream:teleportation_cd"
 * @param id            修饰器的唯一标识符（UUID 字符串形式），用于区分同一属性的多个修饰器
 * @param amount        修饰数值，正数为增益，负数为减益
 * @param operation     运算类型：0 = ADDITION（加法），1 = MULTIPLY_BASE（基于基础值乘法），2 = MULTIPLY_TOTAL（基于总值乘法）
 */
public record AttributeModSpec(
        String attributeName,
        String id,
        double amount,
        int operation
) {

    /**
     * 创建一个新的 Builder 实例，用于链式构建 AttributeModSpec
     *
     * @param attributeName 属性注册名
     * @param id            修饰器唯一标识符（UUID 字符串）
     * @param amount        修饰数值
     * @param operation     运算类型
     * @return 新的 Builder 实例
     */
    public static Builder builder(String attributeName, String id, double amount, int operation) {
        return new Builder(attributeName, id, amount, operation);
    }

    /**
     * AttributeModSpec 的构建器类，支持链式调用
     */
    public static final class Builder {
        private final String attributeName;
        private final String id;
        private final double amount;
        private final int operation;

        private Builder(String attributeName, String id, double amount, int operation) {
            this.attributeName = attributeName;
            this.id = id;
            this.amount = amount;
            this.operation = operation;
        }

        /**
         * 构建最终的 AttributeModSpec 实例
         *
         * @return 构建完成的 AttributeModSpec
         */
        public AttributeModSpec build() {
            return new AttributeModSpec(attributeName, id, amount, operation);
        }
    }
}
