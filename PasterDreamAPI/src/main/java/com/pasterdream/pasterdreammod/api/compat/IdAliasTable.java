package com.pasterdream.pasterdreammod.api.compat;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 按注册表分类的别名表。
 * <p>
 * 纯数据容器，不含任何具体 ID：由调用方（如主模块）填充旧→新映射，
 * 再交给 {@link IdAliasRegistry#registerAll}。
 * <p>
 * 使用 {@link LinkedHashMap} 保证登记顺序稳定，便于日志与自检输出可复现。
 */
public final class IdAliasTable {

    /** 注册表键 → (旧名 → 新名) */
    private final Map<ResourceKey<? extends Registry<?>>, Map<ResourceLocation, ResourceLocation>> table =
            new LinkedHashMap<>();

    /**
     * 追加一条别名。
     *
     * @param registryKey 目标注册表键
     * @param from        旧注册名
     * @param to          新注册名
     * @return this（链式调用）
     * @throws IllegalStateException 若同一旧名已指向不同的新名
     */
    public IdAliasTable add(ResourceKey<? extends Registry<?>> registryKey, ResourceLocation from, ResourceLocation to) {
        Objects.requireNonNull(registryKey, "registryKey 不能为空");
        Objects.requireNonNull(from, "from 不能为空");
        Objects.requireNonNull(to, "to 不能为空");
        if (from.equals(to)) {
            return this;
        }
        Map<ResourceLocation, ResourceLocation> aliases =
                this.table.computeIfAbsent(registryKey, key -> new LinkedHashMap<>());
        ResourceLocation existing = aliases.get(from);
        if (existing != null && !existing.equals(to)) {
            throw new IllegalStateException("注册表 " + registryKey.location() + " 中别名 " + from
                    + " 已指向 " + existing + "，无法改为 " + to);
        }
        aliases.put(from, to);
        return this;
    }

    /**
     * 追加一条别名。
     *
     * @param registryKey 目标注册表键
     * @param alias       别名映射
     * @return this（链式调用）
     */
    public IdAliasTable add(ResourceKey<? extends Registry<?>> registryKey, IdAlias alias) {
        Objects.requireNonNull(alias, "alias 不能为空");
        return add(registryKey, alias.from(), alias.to());
    }

    /**
     * 获取指定注册表的全部别名。
     *
     * @param registryKey 注册表键
     * @return 不可变的旧名 → 新名映射（无则为空 Map）
     */
    public Map<ResourceLocation, ResourceLocation> aliasesFor(ResourceKey<? extends Registry<?>> registryKey) {
        Map<ResourceLocation, ResourceLocation> aliases = this.table.get(registryKey);
        return aliases == null ? Map.of() : Collections.unmodifiableMap(aliases);
    }

    /**
     * 获取已登记的注册表键集合。
     *
     * @return 不可变的注册表键集合
     */
    public Set<ResourceKey<? extends Registry<?>>> registryKeys() {
        return Collections.unmodifiableSet(this.table.keySet());
    }

    /**
     * @return 是否为空表
     */
    public boolean isEmpty() {
        return this.table.isEmpty();
    }

    /**
     * @return 别名总条数
     */
    public int size() {
        int total = 0;
        for (Map<ResourceLocation, ResourceLocation> aliases : this.table.values()) {
            total += aliases.size();
        }
        return total;
    }
}
