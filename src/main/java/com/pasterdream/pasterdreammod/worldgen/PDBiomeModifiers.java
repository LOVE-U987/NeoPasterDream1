package com.pasterdream.pasterdreammod.worldgen;

import com.mojang.serialization.MapCodec;
import com.pasterdream.pasterdreammod.PasterDreamMod;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

/**
 * 染梦维度 BiomeModifier 注册类
 * <p>
 * 负责注册自定义的生物群系修改器序列化器，
 * 将 {@link PDDyedreamBiomeModifier} 注册到 NeoForge 的 BiomeModifier 序列化器注册表中。
 * <p>
 * 注册后，NeoForge 会在世界加载时自动调用该修改器，
 * 向带有 "pasterdream:is_dyedream" 标签的生物群系注入特征。
 */
public class PDBiomeModifiers {

    /**
     * BiomeModifier 序列化器注册表
     */
    public static final DeferredRegister<MapCodec<? extends BiomeModifier>> BIOME_MODIFIER_SERIALIZERS =
            DeferredRegister.create(NeoForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, PasterDreamMod.MOD_ID);

    /**
     * 染梦维度特征修改器序列化器
     */
    public static final DeferredHolder<MapCodec<? extends BiomeModifier>, MapCodec<PDDyedreamBiomeModifier>> DYEDREAM_FEATURES =
            BIOME_MODIFIER_SERIALIZERS.register("dyedream_features", () -> PDDyedreamBiomeModifier.CODEC);
}