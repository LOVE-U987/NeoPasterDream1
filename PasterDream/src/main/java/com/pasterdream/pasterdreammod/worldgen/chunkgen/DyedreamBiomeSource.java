package com.pasterdream.pasterdreammod.worldgen.chunkgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.pasterdream.pasterdreammod.thirdparty.noise.FastNoise;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * 染梦世界自定义 BiomeSource —— 基于噪声的群系分配器
 * <p>
 * 使用大陆性噪声（Continentalness）将世界划分为海洋/岛屿/陆地群系，
 * 再叠加温度、山脊噪声与独立特殊噪声进行次生群系细分。
 * <p>
 * 核心特色：采用简单直接的噪声值判定生成自然河流，
 * 通过 domain warp 噪声实现流畅弯曲的河流走向；
 * 同时使用温度噪声划分雪原、独立稀有噪声划分蘑菇平原。
 * <p>
 * 群系分配逻辑（与 JSON 中 biomes 列表顺序一致）：
 * <ol>
 *   <li>大陆性噪声 < -0.45 → 深海群系（biomes[0]）</li>
 *   <li>大陆性噪声 < -0.3 → 浅海群系（biomes[1]）</li>
 *   <li>大陆性噪声 < -0.12 → 海岸群系（biomes[2]）</li>
 *   <li>蘑菇平原独立噪声命中 → 蘑菇平原群系（biomes[7]）</li>
 *   <li>温度噪声 < -0.35 → 雪原群系（biomes[6]）</li>
 *   <li>山脊噪声 > 0.3 且湿度较高 → 密林群系（biomes[5]）</li>
 *   <li>山脊噪声 > 0.3 → 森林/高地群系（biomes[4]）</li>
 *   <li>其余 → 平原群系（biomes[3]）</li>
 * </ol>
 *
 * @author PasterDream Team
 */
public class DyedreamBiomeSource extends BiomeSource {

    /** BiomeSource 的 MapCodec —— 用于 JSON 反序列化和网络同步 */
    public static final MapCodec<DyedreamBiomeSource> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Biome.CODEC.listOf().fieldOf("biomes").forGetter(o -> o.biomes),
                    RiverEntry.CODEC.listOf().fieldOf("rivers").forGetter(o -> o.rivers)
            ).apply(instance, instance.stable(DyedreamBiomeSource::new))
    );

    /** 大陆性噪声深海判定阈值 */
    private static final double DEEP_OCEAN_THRESHOLD = -0.45;

    /**
     * 大陆性噪声浅海判定阈值
     * <p>
     * 与 {@link DyedreamNoises#ISLAND_THRESHOLD}（-0.3）严格对齐：
     * 噪声值低于此阈值的位置地形恒为水下（isIsland = false），
     * 避免"浅海群系区域被地形判定为陆地、长出沙地"的阈值错位问题。
     */
    private static final double SHALLOW_OCEAN_THRESHOLD = -0.3;

    /**
     * 大陆性噪声海岸判定阈值
     * <p>
     * 与浅海阈值（-0.3）拉开足够间隔（0.18），确保海岸群系形成可被
     * /locate biome 命中的连续斑块，避免群系带过窄导致采样遗漏。
     */
    private static final double SHORE_THRESHOLD = -0.12;

    /** 丘陵山脊阈值 */
    private static final double HILLS_RIDGE_THRESHOLD = 0.3;

    /** 雪原温度阈值，温度噪声低于此值时判定为寒冷雪原 */
    private static final double SNOW_TEMPERATURE_THRESHOLD = -0.35;

    /** 密林湿度阈值，湿度噪声高于此值时山脊区域判定为密林而非普通森林 */
    private static final double DENSE_FOREST_HUMIDITY_THRESHOLD = 0.15;

    /** 蘑菇平原噪声频率 */
    private static final float MUSHROOM_NOISE_FREQUENCY = 0.0022f;

    /** 蘑菇平原噪声阈值，噪声绝对值超过此值时判定为蘑菇平原（值越大越稀有） */
    private static final double MUSHROOM_PLAINS_THRESHOLD = 0.86;

    /**
     * 河流群系判定带宽 —— 仿 vanilla {@code overworld/river} 群系的 weirdness 窄带
     * <p>
     * vanilla 的 river 群系由 MultiNoise 参数点决定：weirdness 落在极窄的带内，
     * 形成蜿蜒的窄带河流（水道 + 两岸约 3-5 格河岸）。
     * <p>
     * 0.045 带宽实测对应蜿蜒河道带总宽约 8-24 格（水道约 4-8 格 + 两岸沙地各 4-8 格）。
     * 该值与 {@code dyedream_river_f.json} 的挖空起始带宽（0.010）配合：
     * 群系带（|w|&lt;0.045）> 挖空带（|w|&lt;0.010），使两岸地面不挖空、只铺沙，
     * 形成"窄水道 + 沙质河岸"的自然河流形态。
     * <p>
     * 注意：不能用 riverFactor 雕刻公式（|erosion-0.4| / |ridges-0.9|）做群系判定，
     * 那会使 river 群系大面积占据地表（曾导致 46% 区块都是河流）。
     */
    private static final double RIVER_WEIRDNESS_BAND = 0.045;

    /** 河流噪声频率 */
    private static final float RIVER_NOISE_FREQUENCY = 0.0012f;

    /** 群系缓存大小 */
    private static final int CACHE_SIZE = 8192;

    /** 群系列表（按顺序：深海、浅海、海岸、平原、森林/高地、密林、雪原、蘑菇平原） */
    private final List<Holder<Biome>> biomes;

    /** 河流配置列表 */
    private final List<RiverEntry> rivers;

    /** 种子值 */
    private long seed = 0;

    /** 河流噪声实例 */
    private final FastNoise riverNoise;

    /** 蘑菇平原稀有噪声实例 */
    private final FastNoise mushroomNoise;

    /** 群系缓存（用于优化性能） */
    private final Long2ObjectLinkedOpenHashMap<Holder<Biome>> biomeCache =
            new Long2ObjectLinkedOpenHashMap<>(CACHE_SIZE + 1, 0.75f);

    /**
     * 构造函数
     *
     * @param biomes 群系列表。按顺序：[深海, 浅海, 海岸, 平原, 森林/高地, 密林, 雪原, 蘑菇平原]
     * @param rivers 河流配置列表
     */
    public DyedreamBiomeSource(List<Holder<Biome>> biomes, List<RiverEntry> rivers) {
        this.biomes = biomes;
        this.rivers = rivers;
        this.riverNoise = makeRiverNoise(seed);
        this.mushroomNoise = makeMushroomNoise(seed);
    }

    /**
     * 设置种子并初始化噪声生成器
     *
     * @param seed 世界种子
     */
    public void setSeed(long seed) {
        if (this.seed != seed) {
            this.seed = seed;
            this.riverNoise.setSeed((int) seed);
            this.mushroomNoise.setSeed((int) seed);
            this.biomeCache.clear();
        }
    }

    /**
     * 创建河流噪声实例
     * 使用 domain warp FBM 噪声实现自然弯曲的河流走向
     */
    private FastNoise makeRiverNoise(long seed) {
        FastNoise noise = new FastNoise((int) seed);
        noise.setFrequency(RIVER_NOISE_FREQUENCY);
        noise.setFractalType(FastNoise.FractalType.FBM);
        noise.setFractalOctaves(4);
        noise.setDomainWarpAmp(60.0f);
        noise.setDomainWarpType(FastNoise.DomainWarpType.OPEN_SIMPLEX_2_REDUCED);
        return noise;
    }

    /**
     * 创建蘑菇平原稀有噪声实例
     * 使用低频率 FBM 噪声生成大面积稀有斑块
     */
    private FastNoise makeMushroomNoise(long seed) {
        FastNoise noise = new FastNoise((int) seed);
        noise.setFrequency(MUSHROOM_NOISE_FREQUENCY);
        noise.setFractalType(FastNoise.FractalType.FBM);
        noise.setFractalOctaves(3);
        return noise;
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
        List<Holder<Biome>> riverBiomes = this.rivers.stream()
                .map(RiverEntry::riverData)
                .toList();
        List<Holder<Biome>> transitionBiomes = this.rivers.stream()
                .map(RiverEntry::transitionData)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();

        return Stream.concat(
                biomes.stream(),
                Stream.concat(riverBiomes.stream(), transitionBiomes.stream())
        );
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
        int blockX = x << 2;
        int blockZ = z << 2;
        long key = posAsLong(blockX, blockZ);

        synchronized (biomeCache) {
            Holder<Biome> cached = biomeCache.get(key);
            if (cached != null) {
                return cached;
            }
        }

        Holder<Biome> result = computeBiome(blockX, y << 2, blockZ, sampler);

        synchronized (biomeCache) {
            biomeCache.putAndMoveToLast(key, result);
            if (biomeCache.size() > CACHE_SIZE) {
                biomeCache.removeFirst();
            }
        }

        return result;
    }

    /**
     * 计算指定位置的群系
     * <p>
     * 在常规群系判定前增加河流判定：复刻原版 riverFactor 的 f 值
     * （{@code f = clamp(max(|erosion-0.4|-0.6, |ridges-0.9|*0.85), 0, 0.75)}），
     * f 越大越接近河道中心。f 超过 {@link #RIVER_BIOME_THRESHOLD} 时返回
     * {@code biome_dyedream_river} 河流群系，触发 surface_rule 的河流铺沙规则。
     *
     * @param bx      方块坐标 X
     * @param by      方块坐标 Y
     * @param bz      方块坐标 Z
     * @param sampler 气候采样器
     * @return 群系 Holder
     */
    private @NotNull Holder<Biome> computeBiome(int bx, int by, int bz, Climate.Sampler sampler) {
        Climate.TargetPoint target = sampler.sample(bx >> 2, by >> 2, bz >> 2);
        // TargetPoint 内部经 quantizeCoord（×10000）量化，必须先转回原始 float 值再与阈值比较
        double continentalness = Climate.unquantizeCoord(target.continentalness());
        double ridges = Climate.unquantizeCoord(target.weirdness());
        double temperature = Climate.unquantizeCoord(target.temperature());
        double humidity = Climate.unquantizeCoord(target.humidity());

        // 深海
        if (continentalness < DEEP_OCEAN_THRESHOLD) {
            return getBiomeSafe(0);
        }

        // 浅海
        if (continentalness < SHALLOW_OCEAN_THRESHOLD) {
            return getBiomeSafe(1);
        }

        // 河流：仿 vanilla overworld/river 群系的 weirdness 窄带判定。
        // |weirdness| < RIVER_WEIRDNESS_BAND 且陆地时为河道（窄条，不占据大片地表）。
        // 仅真陆地（大陆性 >= SHORE_THRESHOLD）才生成河流——海洋/海岸带是水下区域，
        // 不应出现河流群系（否则水下会铺出染梦沙河床带）。
        // 放在海洋/海岸判定之后、其他陆地群系之前，使河道优先覆盖陆地群系。
        if (!rivers.isEmpty() && continentalness >= SHORE_THRESHOLD) {
            if (Math.abs(ridges) < RIVER_WEIRDNESS_BAND) {
                return rivers.get(0).riverData();
            }
        }

        // 海岸
        if (continentalness < SHORE_THRESHOLD) {
            return getBiomeSafe(2);
        }

        // 蘑菇平原：稀有特殊群系，基于独立噪声判定
        if (isMushroomPlains(bx, bz)) {
            return getBiomeSafe(7);
        }

        // 雪原：低温区域
        if (temperature < SNOW_TEMPERATURE_THRESHOLD) {
            return getBiomeSafe(6);
        }

        // 山脊区域：森林/高地，湿度较高时生成为密林
        if (ridges > HILLS_RIDGE_THRESHOLD) {
            return humidity > DENSE_FOREST_HUMIDITY_THRESHOLD
                    ? getBiomeSafe(5)
                    : getBiomeSafe(4);
        }

        // 默认：平原
        return getBiomeSafe(3);
    }

    /**
     * 判断指定位置是否为蘑菇平原
     * <p>
     * 使用独立低频率噪声生成稀有斑块，避免与主群系过渡过于混杂。
     *
     * @param bx 方块坐标 X
     * @param bz 方块坐标 Z
     * @return 是否为蘑菇平原
     */
    private boolean isMushroomPlains(int bx, int bz) {
        float value = mushroomNoise.getNoise(bx, bz);
        return Math.abs(value) > MUSHROOM_PLAINS_THRESHOLD;
    }

    /**
     * 使用噪声检测指定位置是否为河流区域（公开方法，供 ChunkGenerator 使用）
     * <p>
     * 核心算法：
     * 1. 使用 domain warp FBM 噪声采样获取噪声值
     * 2. 直接使用 |noiseValue| 与河流宽度阈值比较
     * 3. 噪声值越接近 0，越靠近河流中心
     * 4. 支持河流宽度 size 和过渡区域 transitionSize 参数
     *
     * @param bx              方块坐标 X
     * @param bz              方块坐标 Z
     * @param continentalness 大陆性噪声值
     * @return 该位置是否处于河流区域
     */
    public boolean isRiverAt(int bx, int bz, double continentalness) {
        if (rivers.isEmpty()) {
            return false;
        }

        boolean isOcean = continentalness < SHALLOW_OCEAN_THRESHOLD;

        for (RiverEntry river : rivers) {
            if ((isOcean && !river.canGenerateInOcean()) ||
                    (!isOcean && river.canGenerateInOceanOnly())) {
                continue;
            }

            int off = river.offset();
            int riverX = bx + off;
            int riverZ = bz + off;

            // 使用 domain warp 噪声采样
            float noiseValue = riverNoise.getNoise(riverX, riverZ);
            double absNoise = Math.abs(noiseValue);

            // 核心河流区域或过渡区域都视为河流
            if (absNoise < river.size() || absNoise < river.transitionSize()) {
                return true;
            }
        }

        return false;
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

    /**
     * 将坐标转换为长整型键（用于缓存）
     *
     * @param x X 坐标
     * @param z Z 坐标
     * @return 长整型键
     */
    public static long posAsLong(int x, int z) {
        return ((long) x << 32) | (z & 0xffffffffL);
    }

    /**
     * 河流配置记录类
     * <p>
     * 参考 ES 模组的 RiverEntry 设计，支持河流宽度、过渡区域、偏移量等参数配置。
     */
    public record RiverEntry(
            Holder<Biome> riverData,
            float size,
            Optional<Holder<Biome>> transitionData,
            float transitionSize,
            int offset,
            boolean canGenerateInOcean,
            boolean canGenerateInOceanOnly
    ) {
        /** CODEC 用于 JSON 序列化 */
        public static final Codec<RiverEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Biome.CODEC.fieldOf("river").forGetter(RiverEntry::riverData),
                Codec.FLOAT.fieldOf("size").forGetter(RiverEntry::size),
                Biome.CODEC.optionalFieldOf("transition").forGetter(RiverEntry::transitionData),
                Codec.FLOAT.optionalFieldOf("transition_size", 0f).forGetter(RiverEntry::transitionSize),
                Codec.INT.fieldOf("offset").forGetter(RiverEntry::offset),
                Codec.BOOL.optionalFieldOf("can_generate_in_ocean", false).forGetter(RiverEntry::canGenerateInOcean),
                Codec.BOOL.optionalFieldOf("can_generate_in_ocean_only", false).forGetter(RiverEntry::canGenerateInOceanOnly)
        ).apply(instance, RiverEntry::new));
    }
}