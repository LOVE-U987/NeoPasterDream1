package com.pasterdream.pasterdreammod.data;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.registry.PDBlocks;
import com.pasterdream.pasterdreammod.registry.PDPlacedFeatures;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockStateMatchTest;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * 世界生成 DataGen Provider —— 统一生成 ConfiguredFeature / PlacedFeature / BiomeModifier 的 JSON
 * <p>
 * 使用 NeoForge {@link DatapackBuiltinEntriesProvider} 与 {@link RegistrySetBuilder}，
 * 在 {@code runData} 阶段把代码中注册的世界生成条目写入
 * {@code src/generated/resources/data/pasterdream/...}，避免手写 JSON 与代码定义漂移。
 * <p>
 * 当前已迁移：{@code soul_ore}（灵魂矿土，注入 {@code minecraft:soul_sand_valley} 的
 * {@code underground_ores} 阶段）。
 * <p>
 * TODO(0.11.x): 将剩余手写矿石/世界生成迁移到本 Provider 统一注册，迁移完成后删除对应手写 JSON：
 * <ul>
 *   <li>地表矿石：titanium_ore / deepslate_titanium_ore / moltengold_ore / windrunner_crystal_ore</li>
 *   <li>原版方块替换特征：white_sand / congeal_wind_ore</li>
 *   <li>染梦矿石：ore_amber_candy / ore_dyedreamdust / ore_dyedreamquartz</li>
 * </ul>
 * 旧手写世界生成对应的 Java 键见 {@code registry/PDPlacedFeatures.java} 中标记为
 * {@code @Deprecated} 的 ORE_* 键。
 */
public class PDWorldgenProvider extends DatapackBuiltinEntriesProvider {

    /** 灵魂矿土配置特征键 */
    private static final ResourceKey<ConfiguredFeature<?, ?>> SOUL_ORE_CONFIGURED =
            ResourceKey.create(Registries.CONFIGURED_FEATURE, id("soul_ore"));

    /** 灵魂矿土世界生成修饰符键 */
    private static final ResourceKey<BiomeModifier> SOUL_ORE_BIOME_MODIFIER =
            ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, id("soul_ore"));

    /** 注册表构建器 —— 按 configured → placed → biome_modifier 顺序满足跨表引用 */
    private static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.CONFIGURED_FEATURE, PDWorldgenProvider::bootstrapConfigured)
            .add(Registries.PLACED_FEATURE, PDWorldgenProvider::bootstrapPlaced)
            .add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, PDWorldgenProvider::bootstrapBiomeModifiers);

    /**
     * 构造世界生成 DataGen Provider
     *
     * @param output         数据包输出目标
     * @param lookupProvider 注册表查找提供器（来自 {@code GatherDataEvent}）
     */
    public PDWorldgenProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, BUILDER, Set.of(PasterDreamMod.MOD_ID));
    }

    /**
     * 注册配置特征：以灵魂矿土替换灵魂沙
     *
     * @param context 配置特征引导上下文
     */
    private static void bootstrapConfigured(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        context.register(SOUL_ORE_CONFIGURED, new ConfiguredFeature<>(Feature.ORE,
                new OreConfiguration(
                        List.of(OreConfiguration.target(
                                new BlockStateMatchTest(Blocks.SOUL_SOIL.defaultBlockState()),
                                PDBlocks.SOUL_ORE.get().defaultBlockState())),
                        16, 0.0f)));
    }

    /**
     * 注册放置特征：每区块 10 次尝试、y 0~120 均匀分布、仅目标群系
     *
     * @param context 放置特征引导上下文
     */
    private static void bootstrapPlaced(BootstrapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configured = context.lookup(Registries.CONFIGURED_FEATURE);
        context.register(PDPlacedFeatures.SOUL_ORE, new PlacedFeature(
                configured.getOrThrow(SOUL_ORE_CONFIGURED),
                List.of(
                        CountPlacement.of(10),
                        InSquarePlacement.spread(),
                        HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.absolute(120)),
                        BiomeFilter.biome())));
    }

    /**
     * 注册生物群系修饰符：将灵魂矿土 placed feature 注入灵魂沙峡谷的地下矿石阶段
     *
     * @param context 生物群系修饰符引导上下文
     */
    private static void bootstrapBiomeModifiers(BootstrapContext<BiomeModifier> context) {
        HolderGetter<PlacedFeature> placed = context.lookup(Registries.PLACED_FEATURE);
        HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);
        context.register(SOUL_ORE_BIOME_MODIFIER, new BiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(biomes.getOrThrow(Biomes.SOUL_SAND_VALLEY)),
                HolderSet.direct(placed.getOrThrow(PDPlacedFeatures.SOUL_ORE)),
                GenerationStep.Decoration.UNDERGROUND_ORES));
    }

    /**
     * 构造模组命名空间下的资源位置
     *
     * @param name 资源名（不含命名空间）
     * @return 资源位置
     */
    private static ResourceLocation id(String name) {
        return ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, name);
    }
}
