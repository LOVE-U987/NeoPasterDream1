package com.pasterdream.pasterdreammod.api.fluid;

import com.pasterdream.pasterdreammod.api.PasterDreamAPI;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * 流体类型注册 API —— 集中管理 {@link FluidType} 的注册与查询。
 * <p>
 * 与 {@link FluidAPI} 对称：一个 FluidType 通常由 source + flowing 一对 Fluid 共享。
 * <p>
 * 使用示例：
 * <pre>{@code
 * FluidTypeAPI.register("meltdream_liquid", MeltdreamLiquidFluidType::new);
 *
 * // 或与 FluidBuilder 一站式：
 * FluidAPI.createFluid("meltdream_liquid")
 *     .fluidType(MeltdreamLiquidFluidType::new)
 *     .source(MeltdreamLiquidFluid.Source::new)
 *     .flowing("flowing_meltdream_liquid", MeltdreamLiquidFluid.Flowing::new)
 *     .buildPair();
 * }</pre>
 */
public final class FluidTypeAPI {

    /**
     * API 专属 FluidType 注册器（NeoForge {@code FLUID_TYPES}）。
     * 由 {@link PasterDreamAPI#registerAll(IEventBus)} 统一挂到 mod 总线。
     */
    public static final DeferredRegister<FluidType> REGISTRY =
            DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, PasterDreamAPI.MOD_ID);

    private static final Map<String, DeferredHolder<FluidType, ? extends FluidType>> HOLDERS = new HashMap<>();

    private FluidTypeAPI() {
        throw new UnsupportedOperationException("FluidTypeAPI 是纯静态门面类，不可实例化");
    }

    /**
     * 注册一个流体类型。
     *
     * @param name     注册名（snake_case，通常与 source 流体同名）
     * @param supplier FluidType 工厂
     * @param <T>      FluidType 子类
     * @return 注册后的 DeferredHolder
     */
    public static <T extends FluidType> DeferredHolder<FluidType, T> register(String name, Supplier<T> supplier) {
        DeferredHolder<FluidType, T> holder = REGISTRY.register(name, supplier);
        HOLDERS.put(name, holder);
        PasterDreamAPI.LOGGER.debug("[FluidTypeAPI] 已注册流体类型: {}", name);
        return holder;
    }

    /**
     * 按注册名查询 FluidType 实例（注册完成后可用）。
     */
    public static Optional<FluidType> getFluidType(String name) {
        return Optional.ofNullable(HOLDERS.get(name)).map(DeferredHolder::get);
    }

    /**
     * 按注册名查询 DeferredHolder。
     */
    public static Optional<DeferredHolder<FluidType, ? extends FluidType>> getHolder(String name) {
        return Optional.ofNullable(HOLDERS.get(name));
    }

    /**
     * 已注册流体类型的不可变视图。
     */
    public static Map<String, DeferredHolder<FluidType, ? extends FluidType>> getRegisteredFluidTypes() {
        return Collections.unmodifiableMap(HOLDERS);
    }

    /**
     * 将 FluidType 注册器挂到模组事件总线。
     * 一般无需单独调用——{@link PasterDreamAPI#registerAll} 已包含。
     */
    public static void registerAll(IEventBus modEventBus) {
        REGISTRY.register(modEventBus);
        PasterDreamAPI.LOGGER.debug("[FluidTypeAPI] 已注册 FluidType DeferredRegister 到事件总线");
    }

    /**
     * 清空 API 层 holder 缓存（测试用）。
     */
    public static void resetForTesting() {
        HOLDERS.clear();
    }
}
