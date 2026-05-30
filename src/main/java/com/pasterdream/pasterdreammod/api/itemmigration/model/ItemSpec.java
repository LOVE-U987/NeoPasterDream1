package com.pasterdream.pasterdreammod.api.itemmigration.model;

import net.minecraft.world.item.Rarity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 物品移植时的基础属性规范
 * <p>
 * 封装了物品注册所需的核心属性，包括注册名、堆叠数、稀有度、防火性、
 * 物品描述文本和翻译键等。提供 Builder 模式以支持链式构建。
 * </p>
 *
 * @param registryName    注册名（snake_case 格式），如 "amber_candy"
 * @param stackSize       最大堆叠数，默认 64
 * @param rarity          稀有度，默认 {@link Rarity#COMMON}
 * @param fireResistant   是否防火（不会被熔岩/火焰摧毁），默认 false
 * @param tooltipLines    物品描述文本行，可选，默认空列表
 * @param translationKey  翻译键，如果为空则根据 registryName 自动生成
 */
public record ItemSpec(
        String registryName,
        int stackSize,
        Rarity rarity,
        boolean fireResistant,
        List<String> tooltipLines,
        String translationKey
) {

    /**
     * 创建一个新的 Builder 实例，用于链式构建 ItemSpec
     *
     * @param registryName 注册名（snake_case 格式），不可为空
     * @return 新的 Builder 实例
     */
    public static Builder builder(String registryName) {
        return new Builder(registryName);
    }

    /**
     * ItemSpec 的构建器类，支持链式调用
     */
    public static final class Builder {
        private final String registryName;
        private int stackSize = 64;
        private Rarity rarity = Rarity.COMMON;
        private boolean fireResistant = false;
        private List<String> tooltipLines = Collections.emptyList();
        private String translationKey = "";

        private Builder(String registryName) {
            this.registryName = registryName;
        }

        /**
         * 设置最大堆叠数
         *
         * @param stackSize 最大堆叠数
         * @return 当前 Builder 实例
         */
        public Builder stackSize(int stackSize) {
            this.stackSize = stackSize;
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
         * 设置是否防火（不会被熔岩/火焰摧毁）
         *
         * @param fireResistant 是否防火
         * @return 当前 Builder 实例
         */
        public Builder fireResistant(boolean fireResistant) {
            this.fireResistant = fireResistant;
            return this;
        }

        /**
         * 设置物品描述文本行
         *
         * @param tooltipLines 描述文本行列表
         * @return 当前 Builder 实例
         */
        public Builder tooltipLines(List<String> tooltipLines) {
            this.tooltipLines = tooltipLines != null
                    ? Collections.unmodifiableList(new ArrayList<>(tooltipLines))
                    : Collections.emptyList();
            return this;
        }

        /**
         * 设置翻译键。如果为空，则在使用时根据 registryName 自动生成。
         *
         * @param translationKey 翻译键
         * @return 当前 Builder 实例
         */
        public Builder translationKey(String translationKey) {
            this.translationKey = translationKey != null ? translationKey : "";
            return this;
        }

        /**
         * 构建最终的 ItemSpec 实例
         *
         * @return 构建完成的 ItemSpec
         */
        public ItemSpec build() {
            return new ItemSpec(
                    registryName,
                    stackSize,
                    rarity,
                    fireResistant,
                    tooltipLines,
                    translationKey
            );
        }
    }
}
