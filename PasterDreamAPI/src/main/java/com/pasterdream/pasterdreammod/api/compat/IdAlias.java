package com.pasterdream.pasterdreammod.api.compat;

import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

/**
 * 单条注册名别名映射（旧 ID → 新 ID）。
 * <p>
 * 仅描述「旧名指向新名」这一数据，不涉及任何具体注册表内容。
 * 由 {@link IdAliasTable} 按注册表分类收集，再由
 * {@link IdAliasRegistry#applyStatic} 交给 NeoForge 原生的
 * {@code IRegistryExtension#addAlias} 生效。
 *
 * @param from 旧注册名（源）
 * @param to   新注册名（目标）
 */
public record IdAlias(ResourceLocation from, ResourceLocation to) {

    /**
     * 紧凑构造器：校验非空且 from 与 to 不同。
     */
    public IdAlias {
        Objects.requireNonNull(from, "from 不能为空");
        Objects.requireNonNull(to, "to 不能为空");
        if (from.equals(to)) {
            throw new IllegalArgumentException("别名 from 与 to 不能相同: " + from);
        }
    }

    /**
     * 创建一条别名映射。
     *
     * @param from 旧注册名
     * @param to   新注册名
     * @return 别名映射
     */
    public static IdAlias of(ResourceLocation from, ResourceLocation to) {
        return new IdAlias(from, to);
    }
}
