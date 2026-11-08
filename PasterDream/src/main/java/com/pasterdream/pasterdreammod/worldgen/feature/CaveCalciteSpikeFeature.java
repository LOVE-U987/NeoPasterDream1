package com.pasterdream.pasterdreammod.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

/** 方解石小笋的逐点洞穴生成，检查 random_patch 随机偏移后的实际落点。 */
public final class CaveCalciteSpikeFeature extends Feature<CaveCalciteSpikeFeature.Config> {

    /** 使用数据包提供的小笋、围岩白名单及搜索范围。 */
    public CaveCalciteSpikeFeature() {
        super(Config.CODEC);
    }

    /**
     * 寻找有石质顶盖的干燥洞底，再检查真实小笋状态的支撑约束。
     *
     * @param context 实际随机偏移后的放置上下文
     * @return 是否生成一株小笋
     */
    @Override
    public boolean place(FeaturePlaceContext<Config> context) {
        WorldGenLevel level = context.level();
        Config config = context.config();
        for (int dy = 0; dy <= config.floorSearchRange(); dy++) {
            BlockPos pos = context.origin().below(dy);
            if (pos.getY() <= level.getMinBuildHeight()) {
                break;
            }
            if (!level.getBlockState(pos).isAir() || !isRockFace(level, config, pos.below(), Direction.UP)
                    || !hasCeiling(level, config, pos)) {
                continue;
            }
            BlockState state = config.toPlace().getState(context.random(), pos);
            if (state.canSurvive(level, pos)) {
                return level.setBlock(pos, state, 2);
            }
        }
        return false;
    }

    /**
     * 向上只穿过空气，第一处障碍必须为天然石质顶盖，排除树冠、液体和露天。
     *
     * @param level 世界生成访问
     * @param config 数据配置
     * @param pos 洞底空气格
     * @return 是否找到石质顶盖
     */
    private boolean hasCeiling(WorldGenLevel level, Config config, BlockPos pos) {
        for (int dy = 1; dy <= config.ceilingSearchRange(); dy++) {
            BlockPos above = pos.above(dy);
            if (above.getY() >= level.getMaxBuildHeight()) {
                return false;
            }
            if (!level.getBlockState(above).isAir()) {
                return isRockFace(level, config, above, Direction.DOWN);
            }
        }
        return false;
    }

    /**
     * 同时检查围岩白名单与支撑面的坚固性。
     *
     * @param level 世界生成访问
     * @param config 数据配置
     * @param pos 围岩位置
     * @param face 朝向小笋的表面
     * @return 是否为允许的坚固岩面
     */
    private boolean isRockFace(WorldGenLevel level, Config config, BlockPos pos, Direction face) {
        return !level.isOutsideBuildHeight(pos) && config.substrate().test(level, pos)
                && level.getBlockState(pos).isFaceSturdy(level, pos, face);
    }

    /**
     * 小笋配置，数值和围岩判定由数据包提供，不绑定固定海拔。
     *
     * @param toPlace 小笋状态提供器
     * @param substrate 允许的天然石质洞底和顶盖
     * @param floorSearchRange 向下寻找洞底的最大距离
     * @param ceilingSearchRange 向上寻找顶盖的最大距离
     */
    public record Config(BlockStateProvider toPlace, BlockPredicate substrate,
                         int floorSearchRange, int ceilingSearchRange) implements FeatureConfiguration {
        public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BlockStateProvider.CODEC.fieldOf("to_place").forGetter(Config::toPlace),
                BlockPredicate.CODEC.fieldOf("substrate").forGetter(Config::substrate),
                Codec.intRange(0, 32).fieldOf("floor_search_range").forGetter(Config::floorSearchRange),
                Codec.intRange(1, 64).fieldOf("ceiling_search_range").forGetter(Config::ceilingSearchRange)
        ).apply(instance, Config::new));
    }
}
