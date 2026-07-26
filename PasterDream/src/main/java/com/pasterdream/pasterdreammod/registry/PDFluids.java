package com.pasterdream.pasterdreammod.registry;

import com.pasterdream.pasterdreammod.fluid.MeltdreamLiquidFluid;
import com.pasterdream.pasterdreammod.fluid.ShadowLiquidFluid;
import com.pasterdream.pasterdreammod.api.fluid.FluidAPI;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.registries.DeferredHolder;

/**
 * 流体注册器
 * <p>
 * 使用 {@link FluidAPI} 统一注册所有自定义流体，避免维护独立的 DeferredRegister。
 */
public class PDFluids {

    /**
     * 融梦涌泉流体源（meltdream_liquid）
     * 静止状态的流体源方块
     */
    public static final DeferredHolder<Fluid, MeltdreamLiquidFluid.Source> MELTDREAM_LIQUID =
            FluidAPI.register("meltdream_liquid", MeltdreamLiquidFluid.Source::new);

    /**
     * 融梦涌泉流体流动（flowing_meltdream_liquid）
     * 流动状态的流体
     */
    public static final DeferredHolder<Fluid, MeltdreamLiquidFluid.Flowing> FLOWING_MELTDREAM_LIQUID =
            FluidAPI.register("flowing_meltdream_liquid", MeltdreamLiquidFluid.Flowing::new);

    /**
     * 熔融阴影流体源（shadow_liquid）
     * 静止状态的流体源方块（阴影维度湖泊，世界生成硬依赖）
     */
    public static final DeferredHolder<Fluid, ShadowLiquidFluid.Source> SHADOW_LIQUID =
            FluidAPI.register("shadow_liquid", ShadowLiquidFluid.Source::new);

    /**
     * 熔融阴影流体流动（flowing_shadow_liquid）
     * 流动状态的流体
     */
    public static final DeferredHolder<Fluid, ShadowLiquidFluid.Flowing> FLOWING_SHADOW_LIQUID =
            FluidAPI.register("flowing_shadow_liquid", ShadowLiquidFluid.Flowing::new);
}
