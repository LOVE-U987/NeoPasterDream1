package com.pasterdream.pasterdreammod.worldgen;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.pasterdream.pasterdreammod.smoketest.PDPortingVerifyTest;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo;

/**
 * 历史 VERIFY-only 水色湖挂接序列化器（{@code pasterdream:wind_lake_verify}）。
 * <p>
 * 2026-07-30 起水色湖已常驻：
 * {@code neoforge/biome_modifier/wind_journey_ground_surface.json} 含
 * {@code ground_feature_wind_journey_1}（type={@code pasterdream:safe_lake}）。
 * 本 modifier 保留 codec/注册以免旧 datapack 反序列化失败；{@link #modify} 恒为 no-op，
 * 避免与正式挂接双重注入。套件门控请用 {@link #isVerifyLakeEnabled()}。
 */
public record PDWindLakeBiomeModifier(
        HolderSet<Biome> biomes,
        HolderSet<PlacedFeature> features,
        GenerationStep.Decoration step
) implements BiomeModifier {

    public static final MapCodec<PDWindLakeBiomeModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Biome.LIST_CODEC.fieldOf("biomes").forGetter(PDWindLakeBiomeModifier::biomes),
            PlacedFeature.LIST_CODEC.fieldOf("features").forGetter(PDWindLakeBiomeModifier::features),
            GenerationStep.Decoration.CODEC.fieldOf("step").forGetter(PDWindLakeBiomeModifier::step)
    ).apply(instance, PDWindLakeBiomeModifier::new));

    /**
     * wind-lake 专项套件是否启用（建档 NORMAL+structures、hooks 入口门控）。
     * 与 {@link PDPortingVerifyTest#SELECTED_SUITES} 同源。
     */
    public static boolean isVerifyLakeEnabled() {
        return PDPortingVerifyTest.ENABLED
                && PDPortingVerifyTest.SELECTED_SUITES.contains(PDPortingVerifyTest.Suite.WIND_LAKE);
    }

    @Override
    public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        // 正式包已常驻挂接 _1；此处不再注入，防止双重 addFeature
    }

    @Override
    public MapCodec<? extends BiomeModifier> codec() {
        return CODEC;
    }
}
