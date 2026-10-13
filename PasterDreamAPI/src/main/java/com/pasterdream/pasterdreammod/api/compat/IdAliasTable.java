package com.pasterdream.pasterdreammod.api.compat;

import com.pasterdream.pasterdreammod.api.PasterDreamAPI;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
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
     * 同时向方块与物品注册表追加同一条别名（BlockItem 场景）。
     * <p>
     * 注册路径使用 API 数据命名空间 {@link PasterDreamAPI#DATA_NAMESPACE} 构造，
     * 等价于分别对 {@code Registries.BLOCK} 与 {@code Registries.ITEM} 调用 {@link #add}。
     *
     * @param from 旧注册路径
     * @param to   新注册路径
     * @return this（链式调用）
     */
    public IdAliasTable addBlockAndItem(String from, String to) {
        ResourceLocation fromId = path(from);
        ResourceLocation toId = path(to);
        add(Registries.BLOCK, fromId, toId);
        add(Registries.ITEM, fromId, toId);
        return this;
    }

    /**
     * 向指定注册表批量追加「同族旧名 → 单一新名」别名（合并场景）。
     * <p>
     * 适用于早期注册多个编号变体、后合并为单一内容的情况（如 {@code foo_0、foo_1 合并为 foo}）；
     * 一对一的语义化改名请直接使用 {@link #add}。
     *
     * @param registryKey 目标注册表键
     * @param to          新注册路径
     * @param fromPaths   旧注册路径（同族编号列表，可变参数）
     * @return this（链式调用）
     * @throws IllegalArgumentException 若旧名列表为空
     */
    public IdAliasTable addFamily(ResourceKey<? extends Registry<?>> registryKey, String to, String... fromPaths) {
        Objects.requireNonNull(to, "to 不能为空");
        if (fromPaths == null || fromPaths.length == 0) {
            throw new IllegalArgumentException("addFamily 至少需要一个旧注册路径");
        }
        ResourceLocation toId = path(to);
        for (String from : fromPaths) {
            add(registryKey, path(from), toId);
        }
        return this;
    }

    /**
     * 同时向方块与物品注册表批量追加「同族旧名 → 单一新名」别名（合并场景）。
     * <p>
     * 等价于对 {@code Registries.BLOCK} 与 {@code Registries.ITEM} 各调用一次
     * {@link #addFamily}，适用于 BlockItem 同族编号变体合并。
     *
     * @param to        新注册路径
     * @param fromPaths 旧注册路径（同族编号列表，可变参数）
     * @return this（链式调用）
     * @throws IllegalArgumentException 若旧名列表为空
     */
    public IdAliasTable addBlockAndItemFamily(String to, String... fromPaths) {
        Objects.requireNonNull(to, "to 不能为空");
        if (fromPaths == null || fromPaths.length == 0) {
            throw new IllegalArgumentException("addBlockAndItemFamily 至少需要一个旧注册路径");
        }
        ResourceLocation toId = path(to);
        for (String from : fromPaths) {
            add(Registries.BLOCK, path(from), toId);
            add(Registries.ITEM, path(from), toId);
        }
        return this;
    }

    /**
     * 构造 API 数据命名空间下的注册名。
     *
     * @param name 注册路径
     * @return ResourceLocation
     */
    private static ResourceLocation path(String name) {
        return ResourceLocation.fromNamespaceAndPath(PasterDreamAPI.DATA_NAMESPACE, name);
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
