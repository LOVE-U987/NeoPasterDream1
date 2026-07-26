package com.pasterdream.pasterdreammod.fluid.types;

import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidType;
import net.minecraft.sounds.SoundEvents;

/**
 * 熔融阴影流体类型
 * <p>
 * 定义流体的物理属性，完全对照原版 ShadowLiquidFluidType：
 * 摔落距离修正 0（缓冲摔伤）、可灭火、可行船、可湿润海绵类判定、
 * 运动缩放 0.007、温度 100，水桶音效。
 */
public class ShadowLiquidFluidType extends FluidType {

    /**
     * 构造熔融阴影流体类型
     */
    public ShadowLiquidFluidType() {
        super(FluidType.Properties.create()
                .fallDistanceModifier(0F)
                .canExtinguish(true)
                .supportsBoating(true)
                .canHydrate(true)
                .motionScale(0.007D)
                .temperature(100)
                .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
                .sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH));
    }
}
