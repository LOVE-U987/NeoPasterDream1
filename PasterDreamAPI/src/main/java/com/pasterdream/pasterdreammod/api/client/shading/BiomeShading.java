package com.pasterdream.pasterdreammod.api.client.shading;

import net.minecraft.world.phys.Vec3;

/**
 * 生物群系着色接口 —— 提供日间/黄昏/夜间的雾气颜色
 * <p>
 * 实现此接口以定义特定生物群系在不同时间段的雾气颜色。
 * 颜色通过 {@link BiomeShadingAPI#interpolateColor} 根据太阳高度进行插值。
 *
 * @see BiomeShadingEntry
 * @see BiomeShadingAPI
 */
public interface BiomeShading {

    /**
     * 获取日间雾色（太阳高度 > 0）
     *
     * @return 日间雾色
     */
    Vec3 getDayColor();

    /**
     * 获取黄昏雾色（太阳高度 ≈ 0）
     *
     * @return 黄昏雾色
     */
    Vec3 getSunsetColor();

    /**
     * 获取夜间雾色（太阳高度 < 0）
     *
     * @return 夜间雾色
     */
    Vec3 getNightColor();
}
