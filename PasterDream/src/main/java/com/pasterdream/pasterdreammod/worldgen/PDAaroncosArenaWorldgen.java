package com.pasterdream.pasterdreammod.worldgen;

import com.google.common.base.Suppliers;
import com.mojang.datafixers.util.Pair;
import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.registry.PDBiomes;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.FeatureSorter;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 亚伦柯斯竞技场主世界群系注入器
 * <p>
 * 将 {@code pasterdream:aaroncos_arena_biome} 注入主世界的 MultiNoise 群系源，
 * 使包含 BOSS 传送门废墟的遗迹群系能在主世界自然生成。
 * 群系参数使用较窄的气候区间，确保其生成范围小巧而稀有，同时保证 /locate 可搜到。
 * <p>
 * 注入时机：{@link ServerStartingEvent}，在主世界维度与结构状态都已创建后执行，
 * 同时刷新 {@link ChunkGeneratorStructureState} 使结构生成系统能识别新群系。
 */
public class PDAaroncosArenaWorldgen {

    /**
     * 主世界群系注入入口
     * <p>
     * 仅在主世界为 {@link NoiseBasedChunkGenerator} 且使用 {@link MultiNoiseBiomeSource} 时执行。
     *
     * @param event 服务器启动中事件
     */
    public static void onServerStarting(ServerStartingEvent event) {
        ServerLevel overworld = event.getServer().overworld();
        if (overworld == null) {
            return;
        }

        ServerChunkCache chunkSource = overworld.getChunkSource();
        ChunkGenerator generator = chunkSource.getGenerator();
        if (!(generator instanceof NoiseBasedChunkGenerator)) {
            PasterDreamMod.LOGGER.debug("[PDAaroncosArenaWorldgen] 主世界生成器不是 NoiseBasedChunkGenerator，跳过注入");
            return;
        }

        BiomeSource source = generator.getBiomeSource();
        if (!(source instanceof MultiNoiseBiomeSource)) {
            PasterDreamMod.LOGGER.debug("[PDAaroncosArenaWorldgen] 主世界群系源不是 MultiNoiseBiomeSource，跳过注入");
            return;
        }

        Registry<Biome> biomeRegistry = event.getServer().registryAccess().registryOrThrow(Registries.BIOME);
        Holder<Biome> arenaBiome = biomeRegistry.getHolderOrThrow(PDBiomes.BIOME_AARONCOS_ARENA);

        // 以原版 OVERWORLD 预设为基础，追加竞技场群系条目
        // 使用 knownPresets() 获取 ResourceKey<Biome> 形式的原版参数列表，再转换为 Holder
        Climate.ParameterList<ResourceKey<Biome>> overworldKeyParams = MultiNoiseBiomeSourceParameterList.knownPresets()
                .get(MultiNoiseBiomeSourceParameterList.Preset.OVERWORLD);
        List<Pair<Climate.ParameterPoint, Holder<Biome>>> values = new ArrayList<>(overworldKeyParams.values().size() + 1);
        for (Pair<Climate.ParameterPoint, ResourceKey<Biome>> entry : overworldKeyParams.values()) {
            values.add(Pair.of(entry.getFirst(), biomeRegistry.getHolderOrThrow(entry.getSecond())));
        }
        values.add(Pair.of(createArenaParameterPoint(), arenaBiome));
        Climate.ParameterList<Holder<Biome>> newParams = new Climate.ParameterList<>(values);
        MultiNoiseBiomeSource newSource = MultiNoiseBiomeSource.createFromList(newParams);

        try {
            // 替换生成器的群系源并刷新特征排序缓存
            injectBiomeSource(generator, newSource);

            // 重建结构生成状态，使 aaroncos_arena_portals 结构能在新群系中生成
            ChunkGeneratorStructureState oldState = chunkSource.getGeneratorState();
            int oldStructureSetCount = oldState.possibleStructureSets().size();
            ChunkGeneratorStructureState newState = generator.createState(
                    overworld.registryAccess().lookupOrThrow(Registries.STRUCTURE_SET),
                    oldState.randomState(),
                    overworld.getSeed()
            );
            injectGeneratorState(chunkSource, newState);

            PasterDreamMod.LOGGER.info("[PDAaroncosArenaWorldgen] 已将亚伦柯斯竞技场群系注入主世界，当前群系数: {}，可生成结构集: {} -> {}",
                    newSource.possibleBiomes().size(), oldStructureSetCount, newState.possibleStructureSets().size());
        } catch (Exception e) {
            PasterDreamMod.LOGGER.error("[PDAaroncosArenaWorldgen] 注入亚伦柯斯竞技场群系失败", e);
        }
    }

    /**
     * 创建竞技场群系的气候参数点
     * <p>
     * 使用极窄的 continentalness / erosion / weirdness 区间，使群系在主世界中呈
     * 小而离散的分布；区间保留一定宽度，避免 /locate 在默认搜索半径内找不到该群系。
     *
     * @return 竞技场群系的气候参数点
     */
    private static Climate.ParameterPoint createArenaParameterPoint() {
        return Climate.parameters(
                Climate.Parameter.span(-0.05F, 0.05F),   // temperature：温带核心，更窄以缩小群系
                Climate.Parameter.span(-0.05F, 0.05F),   // humidity：中等湿度核心，更窄以缩小群系
                Climate.Parameter.span(-0.04F, 0.04F),   // continentalness：极窄内陆点，显著缩小群系
                Climate.Parameter.span(-0.04F, 0.04F),   // erosion：极窄低侵蚀点，显著缩小群系
                Climate.Parameter.point(0.0F),            // depth：地表
                Climate.Parameter.span(-0.04F, 0.04F),   // weirdness：极窄山谷点，显著缩小群系
                0.0F                                     // offset：无偏移补偿
        );
    }

    /**
     * 通过反射替换 ChunkGenerator 的 biomeSource 与 featuresPerStep
     *
     * @param generator 目标区块生成器
     * @param newSource 新的群系源
     * @throws Exception 反射操作异常
     */
    private static void injectBiomeSource(ChunkGenerator generator, BiomeSource newSource) throws Exception {
        Field biomeSourceField = ChunkGenerator.class.getDeclaredField("biomeSource");
        biomeSourceField.setAccessible(true);
        biomeSourceField.set(generator, newSource);

        Field generationSettingsGetterField = ChunkGenerator.class.getDeclaredField("generationSettingsGetter");
        generationSettingsGetterField.setAccessible(true);
        @SuppressWarnings("unchecked")
        Function<Holder<Biome>, BiomeGenerationSettings> getter =
                (Function<Holder<Biome>, BiomeGenerationSettings>) generationSettingsGetterField.get(generator);

        Field featuresPerStepField = ChunkGenerator.class.getDeclaredField("featuresPerStep");
        featuresPerStepField.setAccessible(true);
        Supplier<List<FeatureSorter.StepFeatureData>> newFeaturesPerStep = Suppliers.memoize(
                () -> FeatureSorter.buildFeaturesPerStep(
                        List.copyOf(newSource.possibleBiomes()),
                        biomeHolder -> getter.apply(biomeHolder).features(),
                        true
                )
        );
        featuresPerStepField.set(generator, newFeaturesPerStep);
    }

    /**
     * 通过反射替换 ServerChunkCache 中 ChunkMap 的 chunkGeneratorState
     *
     * @param chunkSource 主世界区块缓存
     * @param newState    新的结构生成状态
     * @throws Exception 反射操作异常
     */
    private static void injectGeneratorState(ServerChunkCache chunkSource, ChunkGeneratorStructureState newState) throws Exception {
        Field chunkMapField = ServerChunkCache.class.getDeclaredField("chunkMap");
        chunkMapField.setAccessible(true);
        Object chunkMap = chunkMapField.get(chunkSource);

        Field generatorStateField = chunkMap.getClass().getDeclaredField("chunkGeneratorState");
        generatorStateField.setAccessible(true);
        generatorStateField.set(chunkMap, newState);
    }
}
