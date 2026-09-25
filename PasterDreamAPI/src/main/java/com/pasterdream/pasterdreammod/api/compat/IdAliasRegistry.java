package com.pasterdream.pasterdreammod.api.compat;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * 旧→新注册名别名登记表（静态门面）。
 * <p>
 * 负责收集别名并转交 NeoForge 原生机制生效：
 * <ul>
 *   <li><b>静态注册表</b>（方块/物品/实体/菜单等）：通过
 *       {@link DeferredRegister#addAlias} 在 {@code RegisterEvent} 之前登记，
 *       随后由 NeoForge 注册表快照在旧存档加载时自动重映射。</li>
 *   <li><b>动态注册表</b>（群系/结构/地物）：本框架暂不覆盖（见设计文档）。</li>
 * </ul>
 * <p>
 * <b>重要</b>：NeoForge 的 {@code addAlias} 对同一注册表键重复登记同一映射会抛
 * {@code IllegalStateException("Infinite alias loop detected")}，因此
 * {@link #applyStatic} 按具体 {@code from} 映射去重，多次调用只补应用新增别名。
 *
 * @see IdCompatAPI
 */
public final class IdAliasRegistry {

    /** 注册表键 → (旧名 → 新名) */
    private static final Map<ResourceKey<? extends Registry<?>>, Map<ResourceLocation, ResourceLocation>> ALIASES =
            new LinkedHashMap<>();

    /**
     * 已应用的别名（注册表键 → 已应用的 from 集合）。
     * <p>
     * NeoForge 对同一注册表键重复 {@code addAlias} 同一映射会抛
     * {@code IllegalStateException("Infinite alias loop detected")}，
     * 故按具体 {@code from} 去重（而非整个注册表键）：后续 {@link #applyStatic}
     * 调用只会补应用新增的别名，不会丢弃合法的新映射。
     */
    private static final Map<ResourceKey<? extends Registry<?>>, Set<ResourceLocation>> APPLIED_ALIASES =
            new HashMap<>();

    private IdAliasRegistry() {
        throw new UnsupportedOperationException("IdAliasRegistry 是静态门面类，不可实例化");
    }

    /**
     * 登记一张别名表。
     *
     * @param table 别名表
     */
    public static synchronized void registerAll(IdAliasTable table) {
        if (table == null || table.isEmpty()) {
            return;
        }
        for (ResourceKey<? extends Registry<?>> registryKey : table.registryKeys()) {
            for (Map.Entry<ResourceLocation, ResourceLocation> entry : table.aliasesFor(registryKey).entrySet()) {
                register(registryKey, entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * 登记单条别名。
     *
     * @param registryKey 注册表键
     * @param from        旧注册名
     * @param to          新注册名
     * @throws IllegalStateException 若同一旧名已指向不同的新名
     */
    public static synchronized void register(ResourceKey<? extends Registry<?>> registryKey,
                                             ResourceLocation from, ResourceLocation to) {
        if (from.equals(to)) {
            return;
        }
        Map<ResourceLocation, ResourceLocation> aliases =
                ALIASES.computeIfAbsent(registryKey, key -> new LinkedHashMap<>());
        ResourceLocation existing = aliases.get(from);
        if (existing != null && !existing.equals(to)) {
            throw new IllegalStateException("注册表 " + registryKey.location() + " 中别名 " + from
                    + " 已指向 " + existing + "，无法改为 " + to);
        }
        aliases.put(from, to);
    }

    /**
     * 将已登记的别名应用到给定的 {@link DeferredRegister}（静态注册表）。
     * <p>
     * 必须在对应 {@code RegisterEvent} 触发之前调用（通常在模组构造器内、
     * 所有注册类静态初始化完成之后）。按具体 {@code from} 映射去重：多次调用
     * 只补应用新增别名，避免 NeoForge 重复 addAlias 触发 "Infinite alias loop"。
     *
     * @param registries 目标 DeferredRegister（可变参数）
     * @return 本次实际应用的别名条数
     */
    public static synchronized int applyStatic(DeferredRegister<?>... registries) {
        if (registries == null || registries.length == 0) {
            return 0;
        }
        int count = 0;
        for (DeferredRegister<?> deferred : registries) {
            if (deferred == null) {
                continue;
            }
            ResourceKey<? extends Registry<?>> registryKey = keyOf(deferred);
            Map<ResourceLocation, ResourceLocation> aliases = ALIASES.get(registryKey);
            if (aliases == null || aliases.isEmpty()) {
                continue;
            }
            Set<ResourceLocation> applied = APPLIED_ALIASES.computeIfAbsent(registryKey, key -> new HashSet<>());
            for (Map.Entry<ResourceLocation, ResourceLocation> entry : aliases.entrySet()) {
                ResourceLocation from = entry.getKey();
                if (applied.contains(from)) {
                    continue;
                }
                deferred.addAlias(from, entry.getValue());
                applied.add(from);
                count++;
            }
        }
        return count;
    }

    /**
     * 获取别名表快照（只读，供自检与文档生成使用）。
     *
     * @return 不可变的注册表键 → (旧名 → 新名) 映射
     */
    public static synchronized Map<ResourceKey<? extends Registry<?>>,
            Map<ResourceLocation, ResourceLocation>> snapshot() {
        Map<ResourceKey<? extends Registry<?>>, Map<ResourceLocation, ResourceLocation>> copy = new LinkedHashMap<>();
        ALIASES.forEach((key, value) ->
                copy.put(key, Collections.unmodifiableMap(new LinkedHashMap<>(value))));
        return Collections.unmodifiableMap(copy);
    }

    /**
     * 指定注册表是否登记了别名。
     *
     * @param registryKey 注册表键
     * @return 是否有别名
     */
    public static synchronized boolean hasAliases(ResourceKey<? extends Registry<?>> registryKey) {
        Map<ResourceLocation, ResourceLocation> aliases = ALIASES.get(registryKey);
        return aliases != null && !aliases.isEmpty();
    }

    /**
     * 提取 DeferredRegister 的注册表键（泛型擦除辅助）。
     *
     * @param deferred DeferredRegister
     * @return 注册表键
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static ResourceKey<? extends Registry<?>> keyOf(DeferredRegister<?> deferred) {
        return (ResourceKey<? extends Registry<?>>) (ResourceKey) deferred.getRegistryKey();
    }
}
