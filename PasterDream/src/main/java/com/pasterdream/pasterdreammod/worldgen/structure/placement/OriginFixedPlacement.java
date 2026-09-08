package com.pasterdream.pasterdreammod.worldgen.structure.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.pasterdream.pasterdreammod.registry.PDStructurePlacements;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacementType;

import java.util.Optional;

/**
 * 原点固定放置策略 —— 保证指定区块必定生成结构。
 * <p>
 * 与 {@link DyedreamCrackPlacement}（随机格点）不同，此策略将结构<strong>锁定</strong>
 * 到一个固定区块位置，不受 salt / gridX / gridZ 影响。
 * 用于染梦维度 (0,0) 原点裂隙，确保玩家从主世界传送时有确定的落点锚点。
 */
public class OriginFixedPlacement extends StructurePlacement {

    private final int targetChunkX;
    private final int targetChunkZ;

    /** 序列化编解码器 */
    public static final MapCodec<OriginFixedPlacement> CODEC =
            RecordCodecBuilder.mapCodec(instance -> {
                var base = StructurePlacement.placementCodec(instance);
                var targetChunkX = Codec.INT
                        .fieldOf("target_chunk_x")
                        .forGetter(OriginFixedPlacement::targetChunkX);
                var targetChunkZ = Codec.INT
                        .fieldOf("target_chunk_z")
                        .forGetter(OriginFixedPlacement::targetChunkZ);
                return base.and(targetChunkX).and(targetChunkZ)
                        .apply(instance, OriginFixedPlacement::new);
            });

    private OriginFixedPlacement(Vec3i locateOffset,
                                 StructurePlacement.FrequencyReductionMethod frequencyReductionMethod,
                                 float frequency,
                                 int salt,
                                 Optional<StructurePlacement.ExclusionZone> exclusionZone,
                                 int targetChunkX,
                                 int targetChunkZ) {
        super(locateOffset, frequencyReductionMethod, frequency, salt, exclusionZone);
        this.targetChunkX = targetChunkX;
        this.targetChunkZ = targetChunkZ;
    }

    private int targetChunkX() {
        return this.targetChunkX;
    }

    private int targetChunkZ() {
        return this.targetChunkZ;
    }

    /**
     * 仅在目标区块返回 true，确保结构只在指定位置生成。
     */
    @Override
    protected boolean isPlacementChunk(ChunkGeneratorStructureState structureState, int x, int z) {
        return x == this.targetChunkX && z == this.targetChunkZ;
    }

    /**
     * locate 路径：始终返回目标区块，无论 gridX/gridZ。
     * <p>
     * 因为只有一个实例，locate 语义天然正确——所有候选点都指向同一位置，
     * 系统取距离查询点最近的那个即可。
     */
    public ChunkPos getPotentialStructureChunk(long seed, int gridX, int gridZ) {
        return new ChunkPos(this.targetChunkX, this.targetChunkZ);
    }

    /**
     * 返回本放置策略的类型。
     */
    @Override
    public StructurePlacementType<?> type() {
        return PDStructurePlacements.ORIGIN_FIXED.get();
    }
}
