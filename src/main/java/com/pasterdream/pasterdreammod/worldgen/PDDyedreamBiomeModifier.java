package com.pasterdream.pasterdreammod.worldgen;

import com.mojang.serialization.MapCodec;
import com.pasterdream.pasterdreammod.PasterDreamMod;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo;

/**
 * 自定义染梦维度 BiomeModifier —— 负责向染梦生物群系注入自定义特征
 * <p>
 * 采用纯代码方式实现，通过检测生物群系标签 "pasterdream:is_dyedream"
 * 来识别染梦维度的目标群系，后续扩展时只需在此类中添加特征注入逻辑即可。
 * <p>
 * 该修改器序列化器已在 {@link PDBiomeModifiers} 中注册，
 * 无需额外的 JSON 配置文件。
 */
public class PDDyedreamBiomeModifier implements BiomeModifier {

    /**
     * 染梦生物群系标签 ID —— 对应 data/pasterdream/tags/worldgen/biome/is_dyedream.json
     */
    private static final ResourceLocation DYEDREAM_BIOME_TAG =
            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "is_dyedream");

    /**
     * 空编解码器 —— 该修改器实例无需额外的配置参数
     */
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
        if (phase == Phase.ADD) {
            // 通过标签检查当前群系是否为染梦维度群系
            if (biome.tags().anyMatch(tag -> tag.location().equals(DYEDREAM_BIOME_TAG))) {
                // TODO: 后续在此处通过 builder.getGenerationSettings().addFeature(step, feature) 添加特征
                // 示例:
                // builder.getGenerationSettings().addFeature(
                //     GenerationStep.Decoration.UNDERGROUND_ORES,
                //     HolderHolder.direct(PDPlacedFeatures.ORE_AMBER_CANDY)
                // );
            }
        }
    }

    /**
     * 获取该修改器的编解码器，用于序列化/反序列化
     *
     * @return MapCodec<PDDyedreamBiomeModifier>
     */
    @Override
    public MapCodec<? extends BiomeModifier> codec() {
        return CODEC;
    }
}