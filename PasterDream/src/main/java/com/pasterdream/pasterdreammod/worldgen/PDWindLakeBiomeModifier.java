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
 * VERIFY-only 水色湖挂接：JSON 可常驻 datapack，但 {@link #modify} 仅在
 * {@code PASTERDREAM_VERIFY=1} 且套件含 {@link PDPortingVerifyTest.Suite#WIND_LAKE} 时
 * 向目标 biome 注入 {@code ground_feature_wind_journey_1}。
 * <p>
 * 正式游玩 / 其它 VERIFY 套件路径下为 no-op。
 * 特征本体已改为 {@code pasterdream:safe_lake}（无 getBiome 结冰），仍默认仅 VERIFY 挂接，
 * 正式包是否常驻挂湖另议。
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

    /** 与 {@link PDPortingVerifyTest#SELECTED_SUITES} 同源，避免二次解析 env。 */
    public static boolean isVerifyLakeEnabled() {
        return PDPortingVerifyTest.ENABLED
                && PDPortingVerifyTest.SELECTED_SUITES.contains(PDPortingVerifyTest.Suite.WIND_LAKE);
    }

    @Override
    public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        if (phase != Phase.ADD || !isVerifyLakeEnabled()) {
            return;
        }
        if (!this.biomes.contains(biome)) {
            return;
        }
        var generation = builder.getGenerationSettings();
        this.features.forEach(holder -> generation.addFeature(this.step, holder));
    }

    @Override
    public MapCodec<? extends BiomeModifier> codec() {
        return CODEC;
    }
}
