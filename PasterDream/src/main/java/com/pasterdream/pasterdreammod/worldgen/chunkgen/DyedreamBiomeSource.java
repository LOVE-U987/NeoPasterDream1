package com.pasterdream.pasterdreammod.worldgen.chunkgen;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.RandomState;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Stream;

/**
 * 染梦世界自定义 BiomeSource —— 基于噪声的简化群系分配器
 * <p>
 * 使用大陆性噪声（Continentalness）将世界划分为海洋/岛屿/陆地群系，
 * 再叠加侵蚀度和山脊噪声进行次生群系细分。
 * <p>
 * 设计为 {@code minecraft:multi_noise} 的简化替代方案，
 * 不需要复杂的 JSON 参数配置，适合浮岛世界游戏风格。
 * <p>
 * 群系分配逻辑：
 * <ol>
 *   <li>大陆性噪声 < -0.35 → 深海群系</li>
 *   <li>大陆性噪声 < -0.19 → 浅海/海岸群系</li>
 *   <li>侵蚀度 > 0.7 → 河流群系</li>
 *   <li>山脊噪声 > 0.3 → 丘陵群系</li>
 *   <li>其余 → 平原/森林群系</li>
 * </ol>
 *
 * @author PasterDream Team
 */
public class DyedreamBiomeSource extends BiomeSource {

    /** BiomeSource 的 MapCodec —— 用于 JSON 反序列化和网络同步 */
    public static final MapCodec<DyedreamBiomeSource> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Biome.CODEC.listOf().fieldOf("biomes").forGetter(o -> o.biomes)
            ).apply(instance, DyedreamBiomeSource::new)
    );

    /** 大陆性噪声岛屿判定阈值（与 DyedreamNoises.ISLAND_THRESHOLD 一致） */
    private static final double DEEP_OCEAN_THRESHOLD = -0.35;

    /** 浅海/海岸阈值 */
    private static final double SHALLOW_OCEAN_THRESHOLD = -0.19;

    /** 河流侵蚀度阈值 */
    private static final double RIVER_EROSION_THRESHOLD = 0.7;

    /** 丘陵山脊阈值 */
    private static final double HILLS_RIDGE_THRESHOLD = 0.3;

    /** 群系列表（按顺序：深海、浅海、平原、丘陵、河流） */
    private final List<Holder<Biome>> biomes;

    /**
     * 构造函数
     *
     * @param biomes 群系列表。至少需要 5 个条目，分别对应：
     *               [深海, 浅海/海岸, 平原/森林, 丘陵, 河流]
     *               如果条目不足，缺失的索引将回退到最后一个可用条目。
     */
    public DyedreamBiomeSource(List<Holder<Biome>> biomes) {
        this.biomes = biomes;
    }

    /**
     * 返回此 BiomeSource 的 CODEC
     *
     * @return MapCodec 实例
     */
    @Override
    protected MapCodec<? extends BiomeSource> codec() {
        return CODEC;
    }

    /**
     * 收集此 BiomeSource 可能使用的所有群系（用于世界创建时的群系列表生成）
     *
     * @return 群系 Holder 流
     */
    @Override
    protected @NotNull Stream<Holder<Biome>> collectPossibleBiomes() {
        return biomes.stream();
    }

    /**
     * 根据噪声采样获取指定位置的群系
     * <p>
     * 此方法由 Minecraft 的世界生成系统在生成区块时调用。
     * 传入的 {@code x}, {@code y}, {@code z} 是四分之一分辨率坐标
     * （即实际方块坐标除以 4）。
     *
     * @param x       四分之一分辨率 X
     * @param y       四分之一分辨率 Y
     * @param z       四分之一分辨率 Z
     * @param sampler 气候采样器（来自 RandomState）
     * @return 该位置对应的群系 Holder
     */
    @Override
    public @NotNull Holder<Biome> getNoiseBiome(int x, int y, int z, @NotNull Climate.Sampler sampler) {
        // 将四分之一分辨率还原为方块坐标进行噪声采样
        int blockX = x << 2;
        int blockZ = z << 2;

        // 获取大陆性噪声和侵蚀度噪声（从 sampler 间接获得）
        Climate.TargetPoint target = sampler.sample(x, y, z);
        double continentalness = target.continentalness();
        double erosion = target.erosion();
        double ridges = target.weirdness();

        return selectBiome(continentalness, erosion, ridges);
    }

    /**
     * 根据噪声值从群系列表中选择对应的群系
     *
     * @param continentalness 大陆性噪声值
     * @param erosion         侵蚀度噪声值
     * @param ridges          山脊/奇异性噪声值
     * @return 匹配的群系 Holder
     */
    private @NotNull Holder<Biome> selectBiome(double continentalness, double erosion, double ridges) {
        // 深海
        if (continentalness < DEEP_OCEAN_THRESHOLD) {
            return getBiomeSafe(0);
        }
        // 浅海/海岸
        if (continentalness < SHALLOW_OCEAN_THRESHOLD) {
            return getBiomeSafe(1);
        }
        // 河流（高侵蚀度区域）
        if (erosion > RIVER_EROSION_THRESHOLD) {
            return getBiomeSafe(4);
        }
        // 丘陵（高山脊区域）
        if (ridges > HILLS_RIDGE_THRESHOLD) {
            return getBiomeSafe(3);
        }
        // 默认：平原/森林
        return getBiomeSafe(2);
    }

    /**
     * 安全获取指定索引的群系，索引超出范围时回退到最后一个条目
     *
     * @param index 群系列表索引
     * @return 群系 Holder
     */
    private @NotNull Holder<Biome> getBiomeSafe(int index) {
        if (biomes.isEmpty()) {
            throw new IllegalStateException("DyedreamBiomeSource: 群系列表为空！");
        }
        int safeIndex = Math.min(index, biomes.size() - 1);
        return biomes.get(safeIndex);
    }
}
