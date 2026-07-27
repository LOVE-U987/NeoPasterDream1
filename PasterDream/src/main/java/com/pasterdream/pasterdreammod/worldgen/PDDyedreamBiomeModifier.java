package com.pasterdream.pasterdreammod.worldgen;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo;

/**
 * 染梦维度 BiomeModifier（已停用）
 * <p>
 * 原用于通过代码向染梦生物群系注入特征，现已迁移至
 * {@code data/pasterdream/neoforge/biome_modifier/} 下的 JSON 文件，
 * 使用 NeoForge 内置的 {@code neoforge:add_features} 类型实现。
 * <p>
 * 迁移原因：避免服务器生命周期依赖问题，并遵循项目硬性约束。
 * 保留此类及 {@link PDBiomeModifiers} 中的注册，以兼容旧存档中可能存在的序列化引用。
 */
public class PDDyedreamBiomeModifier implements BiomeModifier {

    /** 空编解码器 —— 保留以兼容旧数据 */
    public static final MapCodec<PDDyedreamBiomeModifier> CODEC = MapCodec.unit(new PDDyedreamBiomeModifier());

    /**
     * 修改生物群系信息（已停用）
     *
     * @param biome   生物群系持有者引用
     * @param phase   修改阶段
     * @param builder 生物群系信息构建器
     */
    @Override
    public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        // 特征注入已迁移至 JSON biome modifier，此处为空实现。
    }

    @Override
    public MapCodec<? extends BiomeModifier> codec() {
        return CODEC;
    }
}
