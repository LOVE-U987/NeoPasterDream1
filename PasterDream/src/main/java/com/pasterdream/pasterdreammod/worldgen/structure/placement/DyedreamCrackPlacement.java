package com.pasterdream.pasterdreammod.worldgen.structure.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.pasterdream.pasterdreammod.config.PDCommonConfig;
import com.pasterdream.pasterdreammod.registry.PDStructurePlacements;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacementType;

import java.util.Optional;

/**
 * 染梦裂隙结构放置策略 —— 受配置 {@code DYEDREAM_CRACK_GENERATE} 控制。
 * <p>
 * 继承 {@link RandomSpreadStructurePlacement}，配置关闭时通过两个拦截点禁掉生成与定位：
 * <ul>
 *   <li>{@link #getPotentialStructureChunk}：配置关闭时返回<b>世界边界外</b>坐标。
 *       {@code /locate} 的候选点因此全部落到界外，{@code StructureCheck.checkStart} 中
 *       {@code tryLoadFromStorage → storageAccess.scanChunk(...)} 对界外 chunk 快速返回
 *       （region 文件不存在，无真实磁盘 I/O），随后 {@code applyAdditionalChunkRestrictions}
 *       返回 false → 立即 {@code START_NOT_PRESENT}，不再生成 chunk。</li>
 *   <li>{@link #applyAdditionalChunkRestrictions}：配置关闭时返回 false。
 *       {@code isStructureChunk = isPlacementChunk && applyAdditionalChunkRestrictions && ...}
 *       → 世界生成不产生候选；并作为 locate 候选判定的兜底。</li>
 * </ul>
 * 配置开启时两个方法均委托父类，裂隙正常生成且可被 {@code /locate} 定位。
 */
public class DyedreamCrackPlacement extends RandomSpreadStructurePlacement {

    /** 配置关闭时返回的界外候选坐标（region 文件必然不存在，令存储扫描快速失败） */
    private static final ChunkPos OUT_OF_BOUNDS = new ChunkPos(Integer.MAX_VALUE, Integer.MAX_VALUE);

    /** 序列化编解码器：复用 {@link RandomSpreadStructurePlacement} 的字段结构 */
    public static final MapCodec<DyedreamCrackPlacement> CODEC =
            RecordCodecBuilder.mapCodec(instance -> {
                var base = StructurePlacement.placementCodec(instance);
                var spacing = Codec.intRange(0, 4096)
                        .fieldOf("spacing").forGetter(DyedreamCrackPlacement::spacing);
                var separation = Codec.intRange(0, 4096)
                        .fieldOf("separation").forGetter(DyedreamCrackPlacement::separation);
                var spreadType = RandomSpreadType.CODEC
                        .optionalFieldOf("spread_type", RandomSpreadType.LINEAR)
                        .forGetter(DyedreamCrackPlacement::spreadType);
                return base.and(spacing).and(separation).and(spreadType)
                        .apply(instance, DyedreamCrackPlacement::new);
            });

    /**
     * 包装构造（codec 解码入口，字段顺序与 {@link RandomSpreadStructurePlacement} 完整构造一致）。
     *
     * @param locateOffset              定位偏移
     * @param frequencyReductionMethod  频率削减方式
     * @param frequency                 生成频率
     * @param salt                      随机种子盐值
     * @param exclusionZone             排除区
     * @param spacing                   生成间距（区块）
     * @param separation                最小分离（区块）
     * @param spreadType                扩散类型
     */
    private DyedreamCrackPlacement(Vec3i locateOffset,
                                   StructurePlacement.FrequencyReductionMethod frequencyReductionMethod,
                                   float frequency,
                                   int salt,
                                   Optional<StructurePlacement.ExclusionZone> exclusionZone,
                                   int spacing,
                                   int separation,
                                   RandomSpreadType spreadType) {
        super(locateOffset, frequencyReductionMethod, frequency, salt, exclusionZone,
                spacing, separation, spreadType);
    }

    /**
     * 候选点生成：配置关闭时返回<b>世界边界外</b>坐标。
     * <p>
     * 使 {@code /locate}（{@code getNearestGeneratedStructure}）的候选点全部落在界外，
     * {@code scanChunk} 对界外 chunk 不做真实 region 读取，从源头消除大量磁盘 I/O 卡顿。
     *
     * @param seed  世界种子
     * @param gridX 网格 X
     * @param gridZ 网格 Z
     * @return 界外坐标当配置关闭；否则按父类 spacing/separation/salt 网格生成
     */
    @Override
    public ChunkPos getPotentialStructureChunk(long seed, int gridX, int gridZ) {
        if (!Boolean.TRUE.equals(PDCommonConfig.DYEDREAM_CRACK_GENERATE.get())) {
            return OUT_OF_BOUNDS;
        }
        return super.getPotentialStructureChunk(seed, gridX, gridZ);
    }

    /**
     * 额外限制：配置关闭时所有候选点直接不通过。
     * <p>
     * 同时被 {@code isStructureChunk}（世界生成）与 {@code StructureCheck.checkStart}（locate）调用，
     * 是配置控制裂隙生成与避免 locate 卡死的兜底拦截。
     *
     * @param x    区块 X
     * @param z    区块 Z
     * @param seed 世界种子
     * @return false 当配置关闭；否则按父类频率/盐值判定
     */
    @Override
    public boolean applyAdditionalChunkRestrictions(int x, int z, long seed) {
        if (!Boolean.TRUE.equals(PDCommonConfig.DYEDREAM_CRACK_GENERATE.get())) {
            return false;
        }
        return super.applyAdditionalChunkRestrictions(x, z, seed);
    }

    /**
     * 返回本放置策略的类型。
     *
     * @return 自定义 StructurePlacementType
     */
    @Override
    public StructurePlacementType<?> type() {
        return PDStructurePlacements.DYEDREAM_CRACK_SPREAD.get();
    }
}
