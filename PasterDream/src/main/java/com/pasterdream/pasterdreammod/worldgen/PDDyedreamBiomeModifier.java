package com.pasterdream.pasterdreammod.worldgen;

import com.mojang.serialization.MapCodec;
import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.registry.PDPlacedFeatures;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * 自定义染梦维度 BiomeModifier —— 负责向染梦生物群系注入自定义特征
 * <p>
 * 采用纯代码方式实现，通过检测生物群系标签 "pasterdream:is_dyedream"
 * 来识别染梦维度的目标群系，在此处统一注入树木、植被、矿石等特征。
 * <p>
 * 该修改器序列化器已在 {@link PDBiomeModifiers} 中注册，
 * 同时需要在 data/pasterdream/&lt;modid&gt;/neoforge/biome_modifier/ 目录下放置 JSON 激活文件。
 */
public class PDDyedreamBiomeModifier implements BiomeModifier {

    private static final Logger LOGGER = LoggerFactory.getLogger(PasterDreamMod.MOD_ID + "/BiomeModifier");

    /** 染梦生物群系标签 ID —— 对应 data/pasterdream/tags/worldgen/biome/is_dyedream.json */
    private static final ResourceLocation DYEDREAM_BIOME_TAG =
            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "is_dyedream");

    /** 过渡群系 ResourceKey */
    private static final ResourceKey<Biome> BIOME_DYEDREAM_SHORE = ResourceKey.create(
            Registries.BIOME, ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "biome_dyedream_shore"));
    private static final ResourceKey<Biome> BIOME_DYEDREAM_RIVER = ResourceKey.create(
            Registries.BIOME, ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "biome_dyedream_river"));
    private static final ResourceKey<Biome> BIOME_DYEDREAM_DENSE_FOREST = ResourceKey.create(
            Registries.BIOME, ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "biome_dyedream_dense_forest"));

    /** 过渡群系装饰物 ResourceKey */
    private static final ResourceKey<PlacedFeature> SHORE_SPIKE = ResourceKey.create(
            Registries.PLACED_FEATURE, ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "shore_spike"));
    private static final ResourceKey<PlacedFeature> SHORE_SCATTER = ResourceKey.create(
            Registries.PLACED_FEATURE, ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "shore_scatter"));
    private static final ResourceKey<PlacedFeature> RIVER_CRYSTAL_CLUSTER = ResourceKey.create(
            Registries.PLACED_FEATURE, ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "river_crystal_cluster"));
    private static final ResourceKey<PlacedFeature> RIVER_SCATTER = ResourceKey.create(
            Registries.PLACED_FEATURE, ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "river_scatter"));
    private static final ResourceKey<PlacedFeature> DENSE_FOREST_MUSHROOM = ResourceKey.create(
            Registries.PLACED_FEATURE, ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "dense_forest_mushroom"));
    private static final ResourceKey<PlacedFeature> DENSE_FOREST_CRYSTAL_SPIKE = ResourceKey.create(
            Registries.PLACED_FEATURE, ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "dense_forest_crystal_spike"));

    /** 空编解码器 —— 该修改器实例无需额外的配置参数 */
    public static final MapCodec<PDDyedreamBiomeModifier> CODEC = MapCodec.unit(new PDDyedreamBiomeModifier());

    /**
     * 修改生物群系信息 —— 在 Phase.ADD 阶段向染梦群系注入特征
     *
     * @param biome   生物群系持有者引用
     * @param phase   修改阶段（ADD / REMOVE）
     * @param builder 生物群系信息构建器，用于添加特征、生成设置等
     */
    @Override
    public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        // 日志①：不管什么阶段、什么群系，先确认这个方法有没有被调用
        Optional<ResourceKey<Biome>> biomeKey = biome.unwrapKey();
        LOGGER.debug("[modify] 被调用 — phase={}, biome={}, biomeTags={}",
                phase,
                biomeKey.map(key -> key.location().toString()).orElse("unknown"),
                biome.tags().map(tag -> tag.location().toString()).toList());

        if (phase == Phase.ADD) {
            // 日志②：检查群系标签是否匹配
            boolean tagMatched = biome.tags().anyMatch(tag -> tag.location().equals(DYEDREAM_BIOME_TAG));
            LOGGER.debug("[modify] 标签匹配检查 — targetTag={}, matched={}", DYEDREAM_BIOME_TAG, tagMatched);

            if (tagMatched) {
                // 日志③：检查服务器实例
                var server = ServerLifecycleHooks.getCurrentServer();
                if (server == null) {
                    LOGGER.warn("[modify] ❌ 服务器实例不可用（ServerLifecycleHooks.getCurrentServer() 返回 null），无法注入特征到染梦群系");
                    return;
                }
                LOGGER.debug("[modify] ✅ 服务器实例可用: {}", server);

                // 日志④：检查 PlacedFeature 注册表
                var placedFeatureLookup = server.registryAccess().lookupOrThrow(Registries.PLACED_FEATURE);
                LOGGER.debug("[modify] ✅ PlacedFeature 注册表获取成功");

                // ==================== 矿石（所有染梦群系） ====================
                LOGGER.debug("[modify] --- 开始注入矿石 ---");
                addFeature(builder, GenerationStep.Decoration.UNDERGROUND_ORES, placedFeatureLookup, PDPlacedFeatures.ORE_AMBER_CANDY);
                addFeature(builder, GenerationStep.Decoration.UNDERGROUND_ORES, placedFeatureLookup, PDPlacedFeatures.ORE_DYEDREAMDUST);
                addFeature(builder, GenerationStep.Decoration.UNDERGROUND_ORES, placedFeatureLookup, PDPlacedFeatures.ORE_DYEDREAMQUARTZ);

                // ==================== 树木 ====================
                LOGGER.debug("[modify] --- 开始注入树木 ---");
                addFeature(builder, GenerationStep.Decoration.VEGETAL_DECORATION, placedFeatureLookup, PDPlacedFeatures.DYEDREAM_TREES);

                // ==================== 植被 ====================
                LOGGER.debug("[modify] --- 开始注入植被 ---");
                addFeature(builder, GenerationStep.Decoration.VEGETAL_DECORATION, placedFeatureLookup, PDPlacedFeatures.PATCH_DYEDREAM_GRASS);
                addFeature(builder, GenerationStep.Decoration.VEGETAL_DECORATION, placedFeatureLookup, PDPlacedFeatures.PATCH_DYEDREAM_BUDS);
                addFeature(builder, GenerationStep.Decoration.VEGETAL_DECORATION, placedFeatureLookup, PDPlacedFeatures.PATCH_PINKAGARIC);
                addFeature(builder, GenerationStep.Decoration.VEGETAL_DECORATION, placedFeatureLookup, PDPlacedFeatures.PATCH_DYEDREAM_SEAGRASS);

                // ==================== 过渡群系专属装饰物 ====================
                LOGGER.debug("[modify] --- 开始注入过渡群系装饰物 ---");
                if (biomeKey.isPresent()) {
                    ResourceKey<Biome> key = biomeKey.get();
                    if (BIOME_DYEDREAM_SHORE.equals(key)) {
                        addFeature(builder, GenerationStep.Decoration.SURFACE_STRUCTURES, placedFeatureLookup, SHORE_SPIKE);
                        addFeature(builder, GenerationStep.Decoration.SURFACE_STRUCTURES, placedFeatureLookup, SHORE_SCATTER);
                    } else if (BIOME_DYEDREAM_RIVER.equals(key)) {
                        addFeature(builder, GenerationStep.Decoration.SURFACE_STRUCTURES, placedFeatureLookup, RIVER_CRYSTAL_CLUSTER);
                        addFeature(builder, GenerationStep.Decoration.SURFACE_STRUCTURES, placedFeatureLookup, RIVER_SCATTER);
                    } else if (BIOME_DYEDREAM_DENSE_FOREST.equals(key)) {
                        addFeature(builder, GenerationStep.Decoration.SURFACE_STRUCTURES, placedFeatureLookup, DENSE_FOREST_MUSHROOM);
                        addFeature(builder, GenerationStep.Decoration.SURFACE_STRUCTURES, placedFeatureLookup, DENSE_FOREST_CRYSTAL_SPIKE);
                    }
                }

                LOGGER.debug("[modify] ✅ 染梦群系特征注入完成！");
            }
        }
    }

    /**
     * 向生物群系生成设置中添加特征
     *
     * @param builder     生物群系信息构建器
     * @param step        生成阶段（如 UNDERGROUND_ORES、VEGETAL_DECORATION）
     * @param lookup      PlacedFeature 的 Holder 查找器
     * @param featureKey  已放置特征的 ResourceKey
     */
    private static void addFeature(ModifiableBiomeInfo.BiomeInfo.Builder builder,
                                   GenerationStep.Decoration step,
                                   HolderLookup<PlacedFeature> lookup,
                                   ResourceKey<PlacedFeature> featureKey) {
        ResourceLocation featureId = featureKey.location();
        LOGGER.debug("[addFeature] 正在添加 feature={}, step={}", featureId, step);

        lookup.get(featureKey).ifPresentOrElse(
                holder -> {
                    builder.getGenerationSettings().addFeature(step, holder);
                    LOGGER.debug("[addFeature] ✅ 成功添加 feature={}, step={}", featureId, step);
                },
                () -> LOGGER.warn("[addFeature] ❌ 找不到 PlacedFeature: {}（请检查对应的 JSON 文件是否存在且格式正确）", featureId)
        );
    }

    @Override
    public MapCodec<? extends BiomeModifier> codec() {
        return CODEC;
    }
}