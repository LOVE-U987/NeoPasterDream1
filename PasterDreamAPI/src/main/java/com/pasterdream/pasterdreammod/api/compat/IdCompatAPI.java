package com.pasterdream.pasterdreammod.api.compat;

import com.pasterdream.pasterdreammod.api.PasterDreamAPI;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Map;

/**
 * ID 兼容层对外门面。
 * <p>
 * 统一处理「旧注册名 → 新注册名」的兼容解析，底层复用 NeoForge 原生
 * {@code IRegistryExtension#addAlias} / {@code resolve}：
 * <ul>
 *   <li>登记：{@link #registerAll(IdAliasTable)}</li>
 *   <li>生效（静态注册表）：{@link #applyStatic(DeferredRegister[])}</li>
 *   <li>解析：{@link #resolve(Registry, ResourceLocation)}</li>
 *   <li>自检：{@link #selfCheck()}（须在 {@code FMLCommonSetupEvent} 即注册完成后调用）</li>
 * </ul>
 * <p>
 * 本类零具体 ID、零事件处理器，具体映射表由调用方（主模块）提供。
 */
public final class IdCompatAPI {

    private IdCompatAPI() {
        throw new UnsupportedOperationException("IdCompatAPI 是门面类，不可实例化");
    }

    /**
     * 登记一张别名表。
     *
     * @param table 别名表
     */
    public static void registerAll(IdAliasTable table) {
        IdAliasRegistry.registerAll(table);
    }

    /**
     * 登记单条别名。
     *
     * @param registryKey 注册表键
     * @param from        旧注册名
     * @param to          新注册名
     */
    public static void register(ResourceKey<? extends Registry<?>> registryKey,
                                ResourceLocation from, ResourceLocation to) {
        IdAliasRegistry.register(registryKey, from, to);
    }

    /**
     * 将已登记别名应用到给定的 DeferredRegister（静态注册表）。
     * <p>
     * 必须在 {@code RegisterEvent} 之前调用。按具体 {@code from} 映射去重，
     * 多次调用只补应用新增别名。
     *
     * @param registries 目标 DeferredRegister
     * @return 本次实际应用的别名条数
     */
    public static int applyStatic(DeferredRegister<?>... registries) {
        return IdAliasRegistry.applyStatic(registries);
    }

    /**
     * 解析注册名（别名链式解析，未命中则原样返回）。
     *
     * @param registry 注册表
     * @param id       待解析注册名
     * @param <T>      注册表元素类型
     * @return 解析后的注册名
     */
    public static <T> ResourceLocation resolve(Registry<T> registry, ResourceLocation id) {
        return registry.resolve(id);
    }

    /**
     * 解析注册键（别名链式解析，未命中则原样返回）。
     *
     * @param registry 注册表
     * @param key      待解析注册键
     * @param <T>      注册表元素类型
     * @return 解析后的注册键
     */
    public static <T> ResourceKey<T> resolve(Registry<T> registry, ResourceKey<T> key) {
        return registry.resolve(key);
    }

    /**
     * 别名自检（须在 {@code FMLCommonSetupEvent} 中调用，此时注册已完成）。
     * <p>
     * 逐条校验：目标必须已注册；否则记为失败。若旧名仍注册（别名被遮蔽、
     * 实际为 no-op），记为遮蔽并告警——这通常意味着旧条目尚未移除。
     */
    public static void selfCheck() {
        Map<ResourceKey<? extends Registry<?>>, Map<ResourceLocation, ResourceLocation>> snapshot =
                IdAliasRegistry.snapshot();
        if (snapshot.isEmpty()) {
            PasterDreamAPI.LOGGER.info("[IdCompat] 未登记任何别名，跳过自检");
            return;
        }

        int checked = 0;
        int ok = 0;
        int shadowed = 0;
        int broken = 0;

        for (Map.Entry<ResourceKey<? extends Registry<?>>,
                Map<ResourceLocation, ResourceLocation>> registryEntry : snapshot.entrySet()) {
            ResourceKey<? extends Registry<?>> registryKey = registryEntry.getKey();
            Registry<?> registry = BuiltInRegistries.REGISTRY.get(registryKey.location());
            if (registry == null) {
                PasterDreamAPI.LOGGER.warn("[IdCompat] 注册表 {} 不存在，跳过其 {} 条别名自检",
                        registryKey.location(), registryEntry.getValue().size());
                continue;
            }
            for (Map.Entry<ResourceLocation, ResourceLocation> alias : registryEntry.getValue().entrySet()) {
                ResourceLocation from = alias.getKey();
                ResourceLocation to = alias.getValue();
                checked++;

                Object toValue = valueOf(registry, to);
                if (toValue == null) {
                    broken++;
                    PasterDreamAPI.LOGGER.error("[IdCompat] 别名自检失败：目标 {} 未注册（from {}）", to, from);
                    continue;
                }
                Object fromValue = valueOf(registry, from);
                if (fromValue == toValue && registry.getId(from) == registry.getId(to)) {
                    ok++;
                } else {
                    shadowed++;
                    PasterDreamAPI.LOGGER.warn("[IdCompat] 别名被遮蔽：{} -> {}（旧名仍注册，当前为 no-op）",
                            from, to);
                }
            }
        }
        PasterDreamAPI.LOGGER.info("[IdCompat] 别名自检完成：共 {} 条，生效 {} 条，遮蔽 {} 条，失败 {} 条",
                checked, ok, shadowed, broken);
    }

    /**
     * 读取注册表中某注册名对应的值（经 NeoForge 别名解析）。
     * <p>
     * {@code Registry#get(ResourceLocation)} 在 NeoForge 补丁中已走 {@code resolve}，
     * 因此别名对真实反序列化路径生效。
     *
     * @param registry 注册表
     * @param id       注册名
     * @return 对应值；未找到返回 null
     */
    private static Object valueOf(Registry<?> registry, ResourceLocation id) {
        return registry.get(id);
    }
}
