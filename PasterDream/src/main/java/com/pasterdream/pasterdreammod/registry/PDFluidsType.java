package com.pasterdream.pasterdreammod.registry;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.fluid.types.MeltdreamLiquidFluidType;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;

/**
 * 流体类型注册器
 * 使用 DeferredRegister 模式注册所有自定义流体类型
 */
public class PDFluidsType {

    /**
     * 流体类型注册器（使用 NeoForgeRegistries.FLUID_TYPES 注册表键）
     */
    public static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, PasterDreamMod.MOD_ID);

    /**
     * 融梦涌泉流体类型
     * 不可游泳、路径类型为熔岩、光照等级12、粘度100、温度10
     */
    public static final DeferredHolder<FluidType, MeltdreamLiquidFluidType> MELTDREAM_LIQUID_TYPE =
            FLUID_TYPES.register("meltdream_liquid", MeltdreamLiquidFluidType::new);
}
