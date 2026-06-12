package com.pasterdream.pasterdreammod.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

/**
 * 方解石之柱特征配置
 * 控制从地下长出地面的方解石柱子的形状、晶体嵌入、碎片散落等参数
 *
 * @param pillarBlock   柱体主要方块（方解石）
 * @param crystalBlock  嵌入在露出地面的柱子侧边的晶体/矿物方块（加权随机）
 * @param minHeight     柱子最小总高度（包括地下部分）
 * @param maxHeight     柱子最大总高度
 * @param baseWidth     底部宽度（方块数，2=2×2，3=3×3）
 * @param topWidth      顶部宽度（1=1×1，0=尖顶）
 * @param crystalChance 在露出地面的柱子侧边生成晶体的概率（0~1）
 * @param debrisBlock   柱子周围散落的碎片方块
 * @param debrisCount   散落碎片数量
 * @param debrisRadius  碎片散落半径（以柱子为中心）
 * @param replaceable   可被替换的方块判定条件
 */
public record CalcitePillarConfiguration(
    BlockStateProvider pillarBlock,
    BlockStateProvider crystalBlock,
    int minHeight,
    int maxHeight,
    int baseWidth,
    int topWidth,
    float crystalChance,
    BlockStateProvider debrisBlock,
    int debrisCount,
    int debrisRadius,
    BlockPredicate replaceable
) implements FeatureConfiguration {

    public static final MapCodec<CalcitePillarConfiguration> CODEC = RecordCodecBuilder.mapCodec(instance ->
        instance.group(
            BlockStateProvider.CODEC.fieldOf("pillar_block").forGetter(CalcitePillarConfiguration::pillarBlock),
            BlockStateProvider.CODEC.fieldOf("crystal_block").forGetter(CalcitePillarConfiguration::crystalBlock),
            Codec.intRange(3, 64).fieldOf("min_height").forGetter(CalcitePillarConfiguration::minHeight),
            Codec.intRange(3, 64).fieldOf("max_height").forGetter(CalcitePillarConfiguration::maxHeight),
            Codec.intRange(1, 5).fieldOf("base_width").forGetter(CalcitePillarConfiguration::baseWidth),
            Codec.intRange(0, 3).fieldOf("top_width").forGetter(CalcitePillarConfiguration::topWidth),
            Codec.floatRange(0.0f, 1.0f).fieldOf("crystal_chance").forGetter(CalcitePillarConfiguration::crystalChance),
            BlockStateProvider.CODEC.fieldOf("debris_block").forGetter(CalcitePillarConfiguration::debrisBlock),
            Codec.intRange(0, 32).fieldOf("debris_count").forGetter(CalcitePillarConfiguration::debrisCount),
            Codec.intRange(1, 8).fieldOf("debris_radius").forGetter(CalcitePillarConfiguration::debrisRadius),
            BlockPredicate.CODEC.fieldOf("replaceable").forGetter(CalcitePillarConfiguration::replaceable)
        ).apply(instance, CalcitePillarConfiguration::new)
    );
}