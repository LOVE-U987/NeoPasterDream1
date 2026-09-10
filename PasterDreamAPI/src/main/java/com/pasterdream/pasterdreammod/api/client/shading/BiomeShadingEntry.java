package com.pasterdream.pasterdreammod.api.client.shading;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.Vec3;

/**
 * 单个生物群系的着色配置
 *
 * @param biome       群系 Key
 * @param dayColor    日间雾色（太阳高度 > 0）
 * @param sunsetColor 黄昏雾色（太阳高度 ≈ 0）
 * @param nightColor  夜间雾色（太阳高度 < 0）
 */
public record BiomeShadingEntry(
        ResourceKey<Biome> biome,
        Vec3 dayColor,
        Vec3 sunsetColor,
        Vec3 nightColor
) implements BiomeShading {

    @Override
    public Vec3 getDayColor() {
        return dayColor;
    }

    @Override
    public Vec3 getSunsetColor() {
        return sunsetColor;
    }

    @Override
    public Vec3 getNightColor() {
        return nightColor;
    }
}
