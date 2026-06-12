package com.pasterdream.pasterdreammod.registry;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.fluid.MeltdreamLiquidFluid;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

/**
 * 流体注册器
 * 使用 DeferredRegister 模式注册所有自定义流体
 */
public class PDFluids {

    /**
     * 流体注册器（使用 Minecraft 原版 Registries.FLUID 注册表键）
     */
    public static final DeferredRegister<Fluid> FLUIDS =
            DeferredRegister.create(Registries.FLUID, PasterDreamMod.MOD_ID);

    /**
     * 融梦涌泉流体源（meltdream_liquid）
     * 静止状态的流体源方块
     */
    public static final DeferredHolder<Fluid, MeltdreamLiquidFluid.Source> MELTDREAM_LIQUID =
            FLUIDS.register("meltdream_liquid", MeltdreamLiquidFluid.Source::new);

    /**
     * 融梦涌泉流体流动（flowing_meltdream_liquid）
     * 流动状态的流体
     */
    public static final DeferredHolder<Fluid, MeltdreamLiquidFluid.Flowing> FLOWING_MELTDREAM_LIQUID =
            FLUIDS.register("flowing_meltdream_liquid", MeltdreamLiquidFluid.Flowing::new);
}
