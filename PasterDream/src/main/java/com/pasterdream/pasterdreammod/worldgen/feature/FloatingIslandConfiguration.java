package com.pasterdream.pasterdreammod.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

import java.util.List;

/**
 * 浮空群岛特征配置 —— 控制浮岛的尺寸、密度、方块权重和装饰行为
 * <p>
 * 浮岛将生成在 Y=160~220 高度区间，使用椭球体 + 随机游走算法
 * 形成有机形状的悬浮结构。
 *
 * @param minRadius       最小水平半径（方块数）
 * @param maxRadius       最大水平半径（方块数）
 * @param minHeight       最小垂直厚度（方块数）
 * @param maxHeight       最大垂直厚度（方块数）
 * @param densityThreshold 密度阈值（0~1），控制岛屿填满度，越高越密实
 * @param blockWeights    岛屿方块加权条目列表（如方解石 60%、云朵 25%、染梦石 10%、水晶 5%）
 * @param decorateCrystal 是否在大型岛屿底部生成倒垂水晶簇
 * @param decorateVines   是否在岛屿底部悬挂藤蔓装饰
 */
public record FloatingIslandConfiguration(
        int minRadius,
        int maxRadius,
        int minHeight,
        int maxHeight,
        float densityThreshold,
        List<WeightedBlockEntry> blockWeights,
        boolean decorateCrystal,
        boolean decorateVines
) implements FeatureConfiguration {

    /** 默认方块权重条目：方解石 60%，云朵 25%，染梦石英块 10%，融梦水晶灯 5% */
    public static final List<WeightedBlockEntry> DEFAULT_WEIGHTS = List.of(
            new WeightedBlockEntry(net.minecraft.world.level.block.Blocks.CALCITE, 60),
            new WeightedBlockEntry(com.pasterdream.pasterdreammod.registry.PDBlocks.CLOUD.get(), 25),
            new WeightedBlockEntry(com.pasterdream.pasterdreammod.registry.PDBlocks.DYEDREAMQUARTZ_BLOCK.get(), 10),
            new WeightedBlockEntry(com.pasterdream.pasterdreammod.registry.PDBlocks.MELTDREAM_CRYSTAL_LAMP.get(), 5)
    );

    /** 默认配置 —— 适用于染梦世界多数群系 */
    public static final FloatingIslandConfiguration DEFAULT = new FloatingIslandConfiguration(
            3, 12, 4, 10, 0.55f,
            DEFAULT_WEIGHTS, true, true
    );

    /** 最小配置（小岛群） */
    public static final FloatingIslandConfiguration SMALL = new FloatingIslandConfiguration(
            2, 6, 3, 7, 0.50f,
            DEFAULT_WEIGHTS, false, true
    );

    /** 最大配置（大岛 + 水晶装饰） */
    public static final FloatingIslandConfiguration LARGE = new FloatingIslandConfiguration(
            5, 16, 6, 14, 0.60f,
            DEFAULT_WEIGHTS, true, true
    );

    /** MapCodec 序列化 */
    public static final MapCodec<FloatingIslandConfiguration> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.intRange(1, 32).fieldOf("min_radius").forGetter(FloatingIslandConfiguration::minRadius),
                    Codec.intRange(1, 32).fieldOf("max_radius").forGetter(FloatingIslandConfiguration::maxRadius),
                    Codec.intRange(1, 32).fieldOf("min_height").forGetter(FloatingIslandConfiguration::minHeight),
                    Codec.intRange(1, 32).fieldOf("max_height").forGetter(FloatingIslandConfiguration::maxHeight),
                    Codec.floatRange(0.0f, 1.0f).fieldOf("density_threshold").forGetter(FloatingIslandConfiguration::densityThreshold),
                    WeightedBlockEntry.CODEC.listOf().fieldOf("block_weights").forGetter(FloatingIslandConfiguration::blockWeights),
                    Codec.BOOL.fieldOf("decorate_crystal").forGetter(FloatingIslandConfiguration::decorateCrystal),
                    Codec.BOOL.optionalFieldOf("decorate_vines", true).forGetter(FloatingIslandConfiguration::decorateVines)
            ).apply(instance, FloatingIslandConfiguration::new)
    );

    /**
     * 将加权条目列表转换为 {@link SimpleWeightedRandomList}，供运行时随机采样
     *
     * @return 可用于随机获取方块的加权列表
     */
    public SimpleWeightedRandomList<Block> toWeightedList() {
        SimpleWeightedRandomList.Builder<Block> builder = SimpleWeightedRandomList.builder();
        for (WeightedBlockEntry entry : blockWeights) {
            builder.add(entry.block(), entry.weight());
        }
        return builder.build();
    }

    /**
     * 加权方块条目 —— 一个方块 + 权重值
     *
     * @param block  方块类型
     * @param weight 权重（越大出现概率越高）
     */
    public record WeightedBlockEntry(Block block, int weight) {
        public static final Codec<WeightedBlockEntry> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        BuiltInRegistries.BLOCK.byNameCodec().fieldOf("data").forGetter(WeightedBlockEntry::block),
                        Codec.intRange(1, 10000).fieldOf("weight").forGetter(WeightedBlockEntry::weight)
                ).apply(instance, WeightedBlockEntry::new)
        );
    }
}
