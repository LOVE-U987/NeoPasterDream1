package com.pasterdream.pasterdreammod.worldgen.decor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

import javax.annotation.Nullable;
import java.util.stream.Stream;


/**
 * 装饰物统一配置 —— 所有装饰物类型的参数集合
 * <p>
 * 通过一个统一的配置记录支持柱形、团块、尖刺、门框、散布、水下等所有结构类型。
 * 各类型按需取用对应字段，未使用字段保持默认值即可。
 *
 * @param type              装饰物类型
 * @param bodyBlock         主体方块提供器
 * @param topBlock          顶部/特殊方块提供器
 * @param oreBlock          嵌入矿物/晶体提供器
 * @param debrisBlock       碎片方块提供器
 * @param minHeight         最小高度
 * @param maxHeight         最大高度
 * @param baseWidth         底部宽度（柱形方柱用，方块数）
 * @param topWidth          顶部宽度（柱形用，0=尖顶）
 * @param baseRadius        底部半径（尖刺/圆形用）
 * @param topRadius         顶部半径（尖刺用，0=尖顶）
 * @param clusterSize       团块总方块数
 * @param yRadius           团块垂直半径
 * @param irregularity      团块不规则度（0~1）
 * @param gateMinWidth      门框最小间距
 * @param gateMaxWidth      门框最大间距
 * @param pillarRadius      门框柱半径
 * @param beamThickness     门框横梁厚度
 * @param crystalChance     表面嵌入晶体概率（0~1）
 * @param debrisCount       碎片散落数量
 * @param debrisRadius      碎片散落半径
 * @param decorationChance  额外装饰概率（门框用）
 * @param crystalOnlyOnTop  晶体是否仅放置于最顶层（顶部高度打断）
 * @param checkHang         是否启用悬空检测
 * @param fillHang          是否启用悬空填充（下坠+路径填充）
 * @param occupiedCheck     是否启用占用检测
 * @param regionCheck       是否启用区域重叠检测
 * @param regionThreshold   区域重叠阈值（0~1）
 * @param undergroundCheck  是否启用地下空间检测
 * @param waterRequired     是否必须在水中
 * @param replaceable       可被替换的方块判定条件
 * @param tiltIntensity     尖刺倾斜程度（0=垂直，越大越倾斜）
 * @param customGeneratorKey 自定义生成器键（CUSTOM 类型专用）
 */
public record DecorationConfig(
    DecorationType type,
    BlockStateProvider bodyBlock,
    BlockStateProvider topBlock,
    BlockStateProvider oreBlock,
    BlockStateProvider debrisBlock,
    int minHeight,
    int maxHeight,
    int baseWidth,
    int topWidth,
    int baseRadius,
    int topRadius,
    int clusterSize,
    int yRadius,
    float irregularity,
    int gateMinWidth,
    int gateMaxWidth,
    int pillarRadius,
    int beamThickness,
    float crystalChance,
    int debrisCount,
    int debrisRadius,
    float decorationChance,
    boolean crystalOnlyOnTop,
    boolean checkHang,
    boolean fillHang,
    boolean occupiedCheck,
    boolean regionCheck,
    float regionThreshold,
    boolean undergroundCheck,
    boolean waterRequired,
    @Nullable BlockPredicate replaceable,
    float tiltIntensity,
    String customGeneratorKey
) implements FeatureConfiguration {

    @SuppressWarnings("deprecation")
    public static final MapCodec<DecorationConfig> CODEC = new MapCodec<>() {
        @Override
        public <T> RecordBuilder<T> encode(DecorationConfig config, DynamicOps<T> ops, RecordBuilder<T> prefix) {
            prefix.add("type", DecorationType.CODEC.encodeStart(ops, config.type()));
            prefix.add("body_block", BlockStateProvider.CODEC.encodeStart(ops, config.bodyBlock()));
            if (config.topBlock() != null) {
                prefix.add("top_block", BlockStateProvider.CODEC.encodeStart(ops, config.topBlock()));
            }
            if (config.oreBlock() != null) {
                prefix.add("ore_block", BlockStateProvider.CODEC.encodeStart(ops, config.oreBlock()));
            }
            if (config.debrisBlock() != null) {
                prefix.add("debris_block", BlockStateProvider.CODEC.encodeStart(ops, config.debrisBlock()));
            }
            prefix.add("min_height", Codec.INT.encodeStart(ops, config.minHeight()));
            prefix.add("max_height", Codec.INT.encodeStart(ops, config.maxHeight()));
            prefix.add("base_width", Codec.INT.encodeStart(ops, config.baseWidth()));
            prefix.add("top_width", Codec.INT.encodeStart(ops, config.topWidth()));
            prefix.add("base_radius", Codec.INT.encodeStart(ops, config.baseRadius()));
            prefix.add("top_radius", Codec.INT.encodeStart(ops, config.topRadius()));
            prefix.add("cluster_size", Codec.INT.encodeStart(ops, config.clusterSize()));
            prefix.add("y_radius", Codec.INT.encodeStart(ops, config.yRadius()));
            prefix.add("irregularity", Codec.FLOAT.encodeStart(ops, config.irregularity()));
            prefix.add("gate_min_width", Codec.INT.encodeStart(ops, config.gateMinWidth()));
            prefix.add("gate_max_width", Codec.INT.encodeStart(ops, config.gateMaxWidth()));
            prefix.add("pillar_radius", Codec.INT.encodeStart(ops, config.pillarRadius()));
            prefix.add("beam_thickness", Codec.INT.encodeStart(ops, config.beamThickness()));
            prefix.add("crystal_chance", Codec.FLOAT.encodeStart(ops, config.crystalChance()));
            prefix.add("debris_count", Codec.INT.encodeStart(ops, config.debrisCount()));
            prefix.add("debris_radius", Codec.INT.encodeStart(ops, config.debrisRadius()));
            prefix.add("decoration_chance", Codec.FLOAT.encodeStart(ops, config.decorationChance()));
            prefix.add("crystal_only_on_top", Codec.BOOL.encodeStart(ops, config.crystalOnlyOnTop()));
            prefix.add("check_hang", Codec.BOOL.encodeStart(ops, config.checkHang()));
            prefix.add("fill_hang", Codec.BOOL.encodeStart(ops, config.fillHang()));
            prefix.add("occupied_check", Codec.BOOL.encodeStart(ops, config.occupiedCheck()));
            prefix.add("region_check", Codec.BOOL.encodeStart(ops, config.regionCheck()));
            prefix.add("region_threshold", Codec.FLOAT.encodeStart(ops, config.regionThreshold()));
            prefix.add("underground_check", Codec.BOOL.encodeStart(ops, config.undergroundCheck()));
            prefix.add("water_required", Codec.BOOL.encodeStart(ops, config.waterRequired()));
            prefix.add("tilt_intensity", Codec.FLOAT.encodeStart(ops, config.tiltIntensity()));
            if (config.replaceable() != null) {
                prefix.add("replaceable", BlockPredicate.CODEC.encodeStart(ops, config.replaceable()));
            }
            if (config.customGeneratorKey() != null && !config.customGeneratorKey().isEmpty()) {
                prefix.add("custom_generator_key", Codec.STRING.encodeStart(ops, config.customGeneratorKey()));
            }
            return prefix;
        }

        @Override
        public <T> DataResult<DecorationConfig> decode(DynamicOps<T> ops, MapLike<T> input) {
            DataResult<DecorationType> type = DecorationType.CODEC.parse(ops, input.get("type"));
            DataResult<BlockStateProvider> bodyBlock = BlockStateProvider.CODEC.parse(ops, input.get("body_block"));
            DataResult<BlockStateProvider> topBlock = decodeOptional(ops, input, "top_block", BlockStateProvider.CODEC, null);
            DataResult<BlockStateProvider> oreBlock = decodeOptional(ops, input, "ore_block", BlockStateProvider.CODEC, null);
            DataResult<BlockStateProvider> debrisBlock = decodeOptional(ops, input, "debris_block", BlockStateProvider.CODEC, null);
            DataResult<Integer> minHeight = decodeOptional(ops, input, "min_height", Codec.INT, 3);
            DataResult<Integer> maxHeight = decodeOptional(ops, input, "max_height", Codec.INT, 8);
            DataResult<Integer> baseWidth = decodeOptional(ops, input, "base_width", Codec.INT, 2);
            DataResult<Integer> topWidth = decodeOptional(ops, input, "top_width", Codec.INT, 1);
            DataResult<Integer> baseRadius = decodeOptional(ops, input, "base_radius", Codec.INT, 2);
            DataResult<Integer> topRadius = decodeOptional(ops, input, "top_radius", Codec.INT, 0);
            DataResult<Integer> clusterSize = decodeOptional(ops, input, "cluster_size", Codec.INT, 50);
            DataResult<Integer> yRadius = decodeOptional(ops, input, "y_radius", Codec.INT, 4);
            DataResult<Float> irregularity = decodeOptional(ops, input, "irregularity", Codec.FLOAT, 0.3f);
            DataResult<Integer> gateMinWidth = decodeOptional(ops, input, "gate_min_width", Codec.INT, 4);
            DataResult<Integer> gateMaxWidth = decodeOptional(ops, input, "gate_max_width", Codec.INT, 8);
            DataResult<Integer> pillarRadius = decodeOptional(ops, input, "pillar_radius", Codec.INT, 2);
            DataResult<Integer> beamThickness = decodeOptional(ops, input, "beam_thickness", Codec.INT, 2);
            DataResult<Float> crystalChance = decodeOptional(ops, input, "crystal_chance", Codec.FLOAT, 0.0f);
            DataResult<Integer> debrisCount = decodeOptional(ops, input, "debris_count", Codec.INT, 0);
            DataResult<Integer> debrisRadius = decodeOptional(ops, input, "debris_radius", Codec.INT, 0);
            DataResult<Float> decorationChance = decodeOptional(ops, input, "decoration_chance", Codec.FLOAT, 0.0f);
            DataResult<Boolean> crystalOnlyOnTop = decodeOptional(ops, input, "crystal_only_on_top", Codec.BOOL, false);
            DataResult<Boolean> checkHang = decodeOptional(ops, input, "check_hang", Codec.BOOL, true);
            DataResult<Boolean> fillHang = decodeOptional(ops, input, "fill_hang", Codec.BOOL, false);
            DataResult<Boolean> occupiedCheck = decodeOptional(ops, input, "occupied_check", Codec.BOOL, true);
            DataResult<Boolean> regionCheck = decodeOptional(ops, input, "region_check", Codec.BOOL, false);
            DataResult<Float> regionThreshold = decodeOptional(ops, input, "region_threshold", Codec.FLOAT, 0.3f);
            DataResult<Boolean> undergroundCheck = decodeOptional(ops, input, "underground_check", Codec.BOOL, false);
            DataResult<Boolean> waterRequired = decodeOptional(ops, input, "water_required", Codec.BOOL, false);
            DataResult<Float> tiltIntensity = decodeOptional(ops, input, "tilt_intensity", Codec.FLOAT, 0.0f);
            DataResult<BlockPredicate> replaceable = decodeOptional(ops, input, "replaceable", BlockPredicate.CODEC, null);
            DataResult<String> customGeneratorKey = decodeOptional(ops, input, "custom_generator_key", Codec.STRING, "");

            return DataResult.success(new DecorationConfig(
                type.getOrThrow(), bodyBlock.getOrThrow(),
                topBlock.getOrThrow(), oreBlock.getOrThrow(), debrisBlock.getOrThrow(),
                minHeight.getOrThrow(), maxHeight.getOrThrow(),
                baseWidth.getOrThrow(), topWidth.getOrThrow(),
                baseRadius.getOrThrow(), topRadius.getOrThrow(),
                clusterSize.getOrThrow(), yRadius.getOrThrow(), irregularity.getOrThrow(),
                gateMinWidth.getOrThrow(), gateMaxWidth.getOrThrow(),
                pillarRadius.getOrThrow(), beamThickness.getOrThrow(),
                crystalChance.getOrThrow(),
                debrisCount.getOrThrow(), debrisRadius.getOrThrow(),
                decorationChance.getOrThrow(), crystalOnlyOnTop.getOrThrow(),
                checkHang.getOrThrow(), fillHang.getOrThrow(),
                occupiedCheck.getOrThrow(), regionCheck.getOrThrow(),
                regionThreshold.getOrThrow(), undergroundCheck.getOrThrow(),
                waterRequired.getOrThrow(), replaceable.getOrThrow(), tiltIntensity.getOrThrow(),
                customGeneratorKey.getOrThrow()
            ));
        }

        @Override
        public <T> Stream<T> keys(DynamicOps<T> ops) {
            return Stream.of("type", "body_block", "top_block", "ore_block", "debris_block",
                "min_height", "max_height", "base_width", "top_width", "base_radius", "top_radius",
                "cluster_size", "y_radius", "irregularity", "gate_min_width", "gate_max_width",
                "pillar_radius", "beam_thickness", "crystal_chance", "debris_count", "debris_radius",
                "decoration_chance", "crystal_only_on_top", "check_hang", "fill_hang", "occupied_check", "region_check",
                "region_threshold", "underground_check", "water_required", "tilt_intensity",
                "replaceable",
                "custom_generator_key"
            ).map(ops::createString);
        }

        /**
         * 从 MapLike 中解码可选字段，若字段不存在则返回默认值
         */
        private <T, V> DataResult<V> decodeOptional(DynamicOps<T> ops, MapLike<T> input,
                                                      String key, Codec<V> codec, V defaultValue) {
            T value = input.get(key);
            if (value == null) {
                return DataResult.success(defaultValue);
            }
            return codec.parse(ops, value);
        }
    };
}