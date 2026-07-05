package com.pasterdream.pasterdreammod.registry;

import com.mojang.serialization.MapCodec;
import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.worldgen.chunkgen.DyedreamBiomeSource;
import com.pasterdream.pasterdreammod.worldgen.chunkgen.DyedreamChunkGenerator;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 染梦世界生成注册类 —— 注册自定义 ChunkGenerator 和 BiomeSource 类型
 * <p>
 * 使用 {@link DeferredRegister} 将 {@link DyedreamChunkGenerator} 和
 * {@link DyedreamBiomeSource} 注册到 Minecraft 的注册表中，
 * 使维度 JSON 中可以通过 {@code "type": "pasterdream:dyedream_chunk_generator"}
 * 引用自定义区块生成器。
 * <p>
 * 注册方式：
 * <ul>
 *   <li>ChunkGenerator 类型 → {@link Registries#CHUNK_GENERATOR}</li>
 *   <li>BiomeSource 类型 → {@link net.minecraft.core.registries.BuiltInRegistries#BIOME_SOURCE}</li>
 * </ul>
 * <p>
 * 必须在 {@link com.pasterdream.pasterdreammod.PasterDreamMod} 构造器中
 * 调用 {@link DeferredRegister#register} 完成注册。
 *
 * @author PasterDream Team
 */
public class PDWorldgenRegistries {

    /** ChunkGenerator 类型的 DeferredRegister */
    public static final DeferredRegister<MapCodec<? extends ChunkGenerator>> CHUNK_GENERATORS =
            DeferredRegister.create(Registries.CHUNK_GENERATOR, PasterDreamMod.MOD_ID);

    /** BiomeSource 类型的 DeferredRegister */
    public static final DeferredRegister<MapCodec<? extends BiomeSource>> BIOME_SOURCES =
            DeferredRegister.create(Registries.BIOME_SOURCE, PasterDreamMod.MOD_ID);

    // ==================== ChunkGenerator 类型注册 ====================

    /**
     * 染梦世界浮岛区块生成器
     * <p>
     * 在维度 JSON 中使用 {@code "type": "pasterdream:dyedream_chunk_generator"}
     * 引用此生成器。
     */
    public static final DeferredHolder<MapCodec<? extends ChunkGenerator>, MapCodec<DyedreamChunkGenerator>>
            DYEDREAM_CHUNK_GENERATOR = CHUNK_GENERATORS.register("dyedream_chunk_generator",
            () -> DyedreamChunkGenerator.CODEC);

    // ==================== BiomeSource 类型注册 ====================

    /**
     * 染梦世界自定义群系源
     * <p>
     * 在维度 JSON 中使用 {@code "type": "pasterdream:dyedream_biome_source"}
     * 引用此群系源。
     */
    public static final DeferredHolder<MapCodec<? extends BiomeSource>, MapCodec<DyedreamBiomeSource>>
            DYEDREAM_BIOME_SOURCE = BIOME_SOURCES.register("dyedream_biome_source",
            () -> DyedreamBiomeSource.CODEC);
}
