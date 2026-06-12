package com.pasterdream.pasterdreammod.api.ruin;

import com.pasterdream.pasterdreammod.api.dimension.terrain.TerrainRequirements;
import com.pasterdream.pasterdreammod.api.ruin.builder.RuinBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.StructureType;

import javax.annotation.Nullable;

/**
 * 遗迹/结构注册结果 —— 包含结构相关的所有 ResourceKey 引用
 * <p>
 * 由 {@link RuinBuilder#build()} 返回，
 * 持有结构类型、结构实例和结构集的 ResourceKey，
 * 方便在代码中引用已注册的结构资源。
 * <p>
 * 使用示例：
 * <pre>{@code
 * RuinResult result = RuinAPI.createRuin("dyedream_ruins")
 *     .biomeTag("pasterdream:is_dyedream")
 *     .templatePool("pasterdream:dyedream_ruins_pool")
 *     .structureClass(DyedreamRuinsStructure.class)
 *     .codec(DyedreamRuinsStructure.CODEC)
 *     .build();
 *
 * ResourceKey<Structure> structureKey = result.structureKey();
 * }</pre>
 *
 * @param name                 结构注册名称
 * @param typeKey              结构类型 ResourceKey
 * @param structureKey         结构实例 ResourceKey
 * @param setKey               结构集 ResourceKey，可能为 null
 * @param terrainRequirements  地形需求（大型结构用），可能为 null
 */
public record RuinResult(
        String name,
        ResourceKey<StructureType<?>> typeKey,
        ResourceKey<Structure> structureKey,
        @Nullable ResourceKey<StructureSet> setKey,
        @Nullable TerrainRequirements terrainRequirements
) {

    public RuinResult {
        if (name == null) {
            throw new IllegalArgumentException("RuinResult: name 不能为 null");
        }
        if (typeKey == null) {
            throw new IllegalArgumentException("RuinResult: typeKey 不能为 null");
        }
        if (structureKey == null) {
            throw new IllegalArgumentException("RuinResult: structureKey 不能为 null");
        }
    }

    /**
     * 创建一个仅包含结构和类型 Key 的初始结果（无结构集、无地形需求）
     */
    public static RuinResult of(String modId, String name) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(modId, name);
        return new RuinResult(
                name,
                ResourceKey.create(Registries.STRUCTURE_TYPE, id),
                ResourceKey.create(Registries.STRUCTURE, id),
                null,
                null
        );
    }

    /**
     * 创建一个包含地形需求的结果。
     */
    public static RuinResult of(String modId, String name, @Nullable TerrainRequirements terrainRequirements) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(modId, name);
        return new RuinResult(
                name,
                ResourceKey.create(Registries.STRUCTURE_TYPE, id),
                ResourceKey.create(Registries.STRUCTURE, id),
                null,
                terrainRequirements
        );
    }

    public boolean hasSetKey() {
        return setKey != null;
    }

    /**
     * 判断是否为大型结构（附带地形需求）
     */
    public boolean isLargeStructure() {
        return terrainRequirements != null;
    }

    public RuinResult withSetKey(String setName, String modId) {
        ResourceLocation setId = ResourceLocation.fromNamespaceAndPath(modId, setName);
        return new RuinResult(
                name, typeKey, structureKey,
                ResourceKey.create(Registries.STRUCTURE_SET, setId),
                terrainRequirements
        );
    }

    public RuinResult withSetKey(ResourceKey<StructureSet> setKey) {
        return new RuinResult(name, typeKey, structureKey, setKey, terrainRequirements);
    }
}