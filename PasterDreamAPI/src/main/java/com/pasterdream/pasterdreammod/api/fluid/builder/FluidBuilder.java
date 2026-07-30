package com.pasterdream.pasterdreammod.api.fluid.builder;

import com.pasterdream.pasterdreammod.api.fluid.FluidAPI;
import com.pasterdream.pasterdreammod.api.fluid.FluidTypeAPI;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * 流体构建器 —— 支持单 Fluid 注册，或 FluidType + source + flowing 一站式注册。
 * <p>
 * 使用示例：
 * <pre>{@code
 * // 仅注册一个 Fluid（兼容旧用法）
 * FluidAPI.createFluid("meltdream_liquid")
 *     .factory(MeltdreamLiquidFluid.Source::new)
 *     .build();
 *
 * // 一站式：Type + source + flowing
 * FluidAPI.createFluid("meltdream_liquid")
 *     .fluidType(MeltdreamLiquidFluidType::new)
 *     .source(MeltdreamLiquidFluid.Source::new)
 *     .flowing("flowing_meltdream_liquid", MeltdreamLiquidFluid.Flowing::new)
 *     .buildPair();
 * }</pre>
 * <p>
 * 注意：{@link net.neoforged.neoforge.fluids.BaseFlowingFluid.Properties}、桶/方块绑定
 * 仍由主模具体 Fluid 类维护（内容耦合），本 Builder 只负责注册表条目。
 *
 * @param <T> 主（source）流体类型参数
 */
public class FluidBuilder<T extends Fluid> {

    private final String name;
    private Supplier<T> factory;
    private Supplier<? extends FluidType> typeFactory;
    private String flowingName;
    private Supplier<? extends Fluid> flowingFactory;

    /**
     * @param name 主流体注册名（通常也是 FluidType 名）
     */
    public FluidBuilder(String name) {
        this.name = name;
    }

    /**
     * 设置流体工厂（单 Fluid 注册路径，与 {@link #source} 等价）。
     */
    public FluidBuilder<T> factory(Supplier<T> factory) {
        this.factory = factory;
        return this;
    }

    /**
     * 设置 source 流体工厂（一站式路径的别名，语义更清晰）。
     */
    public FluidBuilder<T> source(Supplier<T> factory) {
        this.factory = factory;
        return this;
    }

    /**
     * 设置并注册关联的 {@link FluidType}（注册名与 {@link #name} 相同）。
     */
    public FluidBuilder<T> fluidType(Supplier<? extends FluidType> typeFactory) {
        this.typeFactory = typeFactory;
        return this;
    }

    /**
     * 设置 flowing 流体（注册名可与 source 不同，常见前缀 {@code flowing_}）。
     *
     * @param flowingName    flowing 注册名
     * @param flowingFactory flowing 工厂
     */
    public FluidBuilder<T> flowing(String flowingName, Supplier<? extends Fluid> flowingFactory) {
        this.flowingName = flowingName;
        this.flowingFactory = flowingFactory;
        return this;
    }

    /**
     * 仅注册主流体（兼容旧 API）。
     *
     * @return source 的 DeferredHolder
     * @throws NullPointerException 如果 factory/source 未设置
     */
    public DeferredHolder<Fluid, T> build() {
        Objects.requireNonNull(factory, "[FluidBuilder] factory/source 不能为空");
        return FluidAPI.register(name, factory);
    }

    /**
     * 一站式结果：可选 FluidType + source Fluid + 可选 flowing Fluid。
     *
     * @param sourceHolder  source DeferredHolder
     * @param flowingHolder flowing DeferredHolder，未配置时为 null
     * @param typeHolder    FluidType DeferredHolder，未配置时为 null
     * @param <S>           source 流体类型
     * @param <F>           flowing 流体类型
     * @param <FT>          FluidType 类型
     */
    public record FluidPairResult<S extends Fluid, F extends Fluid, FT extends FluidType>(
            DeferredHolder<Fluid, S> sourceHolder,
            DeferredHolder<Fluid, F> flowingHolder,
            DeferredHolder<FluidType, FT> typeHolder
    ) {}

    /**
     * 注册可选的 FluidType、source，以及可选的 flowing。
     * <p>
     * 至少需要 {@link #factory}/{@link #source}；Type 与 flowing 按需配置。
     *
     * @return 注册结果三元组（未配置项对应字段为 null）
     */
    @SuppressWarnings("unchecked")
    public <F extends Fluid, FT extends FluidType> FluidPairResult<T, F, FT> buildPair() {
        Objects.requireNonNull(factory, "[FluidBuilder] factory/source 不能为空（buildPair）");

        DeferredHolder<FluidType, FT> typeHolder = null;
        if (typeFactory != null) {
            typeHolder = (DeferredHolder<FluidType, FT>) FluidTypeAPI.register(name, typeFactory);
        }

        DeferredHolder<Fluid, T> sourceHolder = FluidAPI.register(name, factory);

        DeferredHolder<Fluid, F> flowingHolder = null;
        if (flowingFactory != null) {
            Objects.requireNonNull(flowingName, "[FluidBuilder] flowingName 不能为空");
            flowingHolder = (DeferredHolder<Fluid, F>) FluidAPI.register(flowingName, flowingFactory);
        }

        return new FluidPairResult<>(sourceHolder, flowingHolder, typeHolder);
    }
}
