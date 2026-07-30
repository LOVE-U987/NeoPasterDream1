package com.pasterdream.pasterdreammod.registry;

import com.pasterdream.pasterdreammod.api.fluid.FluidTypeAPI;
import com.pasterdream.pasterdreammod.fluid.types.MeltdreamLiquidFluidType;
import com.pasterdream.pasterdreammod.fluid.types.ShadowLiquidFluidType;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;

/**
 * 流体类型注册入口（主模内容表）。
 * <p>
 * 实际 DeferredRegister 由 {@link FluidTypeAPI} 持有，并已在
 * {@link com.pasterdream.pasterdreammod.api.PasterDreamAPI#registerAll} 挂总线。
 * 本类仅保留公开 holder，供流体实现 / 客户端扩展引用。
 */
public class PDFluidsType {

    /**
     * 融梦涌泉流体类型
     * 不可游泳、路径类型为熔岩、光照等级12、粘度100、温度10
     */
    public static final DeferredHolder<FluidType, MeltdreamLiquidFluidType> MELTDREAM_LIQUID_TYPE =
            FluidTypeAPI.register("meltdream_liquid", MeltdreamLiquidFluidType::new);

    /**
     * 熔融阴影流体类型
     * 摔落缓冲、可灭火、可行船、可湿润、温度100（对照原版 ShadowLiquidFluidType）
     */
    public static final DeferredHolder<FluidType, ShadowLiquidFluidType> SHADOW_LIQUID_TYPE =
            FluidTypeAPI.register("shadow_liquid", ShadowLiquidFluidType::new);
}
