package com.pasterdream.pasterdreammod.api.itemmigration.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 饰品（Curio）属性规范 record
 * <p>
 * 封装了 Curios API 饰品的完整属性，包括槽位类型、翻译键和属性修饰器列表。
 * 提供 Builder 模式以支持链式构建。
 * </p>
 *
 * @param curioSlot      饰品槽位标识，如 "ring"、"necklace"、"belt" 等
 * @param translationKey 翻译键，如果为空则根据注册名自动生成
 * @param attributeMods  属性修饰器列表，可选，默认空列表
 */
public record CurioSpec(
        String curioSlot,
        String translationKey,
        List<AttributeModSpec> attributeMods
) {

    /**
     * 创建一个新的 Builder 实例，用于链式构建 CurioSpec
     *
     * @param curioSlot 饰品槽位标识
     * @return 新的 Builder 实例
     */
    public static Builder builder(String curioSlot) {
        return new Builder(curioSlot);
    }

    /**
     * CurioSpec 的构建器类，支持链式调用
     */
    public static final class Builder {
        private final String curioSlot;
        private String translationKey = "";
        private List<AttributeModSpec> attributeMods = Collections.emptyList();

        private Builder(String curioSlot) {
            this.curioSlot = curioSlot;
        }

        /**
         * 设置翻译键。如果为空，则在使用时根据注册名自动生成。
         *
         * @param translationKey 翻译键
         * @return 当前 Builder 实例
         */
        public Builder translationKey(String translationKey) {
            this.translationKey = translationKey != null ? translationKey : "";
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
         * 构建最终的 CurioSpec 实例
         *
         * @return 构建完成的 CurioSpec
         */
        public CurioSpec build() {
            return new CurioSpec(curioSlot, translationKey, attributeMods);
        }
    }
}
