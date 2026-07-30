package com.pasterdream.pasterdreammod.api.fluid;

import com.pasterdream.pasterdreammod.api.PasterDreamAPI;
import com.pasterdream.pasterdreammod.api.fluid.builder.FluidBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * 流体注册 API —— 集中管理 Fluid 的注册与查询
 * <p>
 * 采用 Facade 模式 + Builder 模式设计，与 {@link com.pasterdream.pasterdreammod.api.block.BlockAPI} 风格一致。
 * FluidType 由对称的 {@link FluidTypeAPI} 管理；一站式 Type+source+flowing 见 {@link FluidBuilder#buildPair()}。
 * <p>
 * 使用示例：
 * <pre>{@code
 * FluidAPI.register("meltdream_liquid", MeltdreamLiquidFluid.Source::new);
 *
 * // Builder：仅 Fluid
 * FluidAPI.createFluid("meltdream_liquid")
 *     .factory(MeltdreamLiquidFluid.Source::new)
 *     .build();
 *
 * // Builder：Type + source + flowing
 * FluidAPI.createFluid("meltdream_liquid")
 *     .fluidType(MeltdreamLiquidFluidType::new)
 *     .source(MeltdreamLiquidFluid.Source::new)
 *     .flowing("flowing_meltdream_liquid", MeltdreamLiquidFluid.Flowing::new)
 *     .buildPair();
 * }</pre>
 */
public final class FluidAPI {

    /**
     * API 专属的流体注册器。
     * 需要在 {@code PasterDreamMod} 构造函数中通过 {@link #registerAll(IEventBus)} 注册到事件总线。
     */
    public static final DeferredRegister<Fluid> REGISTRY =
            DeferredRegister.create(Registries.FLUID, PasterDreamAPI.MOD_ID);

    /** 已注册流体的 DeferredHolder 缓存 */
    private static final Map<String, DeferredHolder<Fluid, ? extends Fluid>> HOLDERS = new HashMap<>();

    private FluidAPI() {
        throw new UnsupportedOperationException("FluidAPI 是纯静态门面类，不可实例化");
    }

    /**
     * 注册一个流体。
     *
     * @param name     流体注册名（snake_case 格式）
     * @param supplier 流体 Supplier
     * @param <T>      流体类型参数
     * @return 注册后的 DeferredHolder
     */
    public static <T extends Fluid> DeferredHolder<Fluid, T> register(String name, Supplier<T> supplier) {
        DeferredHolder<Fluid, T> holder = REGISTRY.register(name, supplier);
        HOLDERS.put(name, holder);
        PasterDreamAPI.LOGGER.debug("[FluidAPI] 已注册流体: {}", name);
        return holder;
    }

    /**
     * 创建一个流体构建器。
     *
     * @param name 流体注册名
     * @param <T>  流体类型参数
     * @return {@link FluidBuilder} 实例
     */
    public static <T extends Fluid> FluidBuilder<T> createFluid(String name) {
        return new FluidBuilder<>(name);
    }

    /**
     * 根据注册名查询流体。
     *
     * @param name 流体注册名
     * @return {@link Fluid} 的 Optional，未找到返回 {@link Optional#empty()}
     */
    public static Optional<Fluid> getFluid(String name) {
        return Optional.ofNullable(HOLDERS.get(name)).map(DeferredHolder::get);
    }

    /**
     * 根据注册名查询流体的 DeferredHolder。
     *
     * @param name 流体注册名
     * @return DeferredHolder 的 Optional，未找到返回 {@link Optional#empty()}
     */
    public static Optional<DeferredHolder<Fluid, ? extends Fluid>> getHolder(String name) {
        return Optional.ofNullable(HOLDERS.get(name));
    }

    /**
     * 获取所有已注册流体的不可变视图。
     *
     * @return 注册名到 DeferredHolder 的映射
     */
    public static Map<String, DeferredHolder<Fluid, ? extends Fluid>> getRegisteredFluids() {
        return Collections.unmodifiableMap(HOLDERS);
    }

    /**
     * 将流体注册器注册到模组事件总线。
     *
     * @param modEventBus NeoForge 模组事件总线
     */
    public static void registerAll(IEventBus modEventBus) {
        REGISTRY.register(modEventBus);
        PasterDreamAPI.LOGGER.debug("[FluidAPI] 已注册流体 DeferredRegister 到事件总线");
    }

    /**
     * 重置所有静态缓存，供测试使用。
     * <p>
     * 清空已缓存的流体 DeferredHolder，使每次测试都在干净的缓存状态下运行。
     * 注意：此方法不会取消 DeferredRegister 中的已注册流体，仅清除 API 层面的缓存数据。
     */
    public static void resetForTesting() {
        HOLDERS.clear();
    }
}
