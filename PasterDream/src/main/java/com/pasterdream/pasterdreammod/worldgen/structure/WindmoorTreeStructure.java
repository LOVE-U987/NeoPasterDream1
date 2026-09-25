package com.pasterdream.pasterdreammod.worldgen.structure;

import com.pasterdream.pasterdreammod.registry.PDRuinsRegistration;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;

import java.util.Optional;
import java.util.function.IntBinaryOperator;

/** 风泊树自然生成：树干承重核心必须位于连续岛面内部，树冠允许伸出岛缘。 */
public final class WindmoorTreeStructure extends Structure {
    public static final MapCodec<WindmoorTreeStructure> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            JigsawStructure.CODEC.forGetter((WindmoorTreeStructure s) -> s.delegate),
            Codec.intRange(1, 16).fieldOf("support_radius").forGetter(s -> s.supportRadius),
            Codec.intRange(1, 16).fieldOf("sample_step").forGetter(s -> s.sampleStep),
            Codec.INT.fieldOf("min_surface_y").forGetter(s -> s.minSurfaceY),
            Codec.intRange(0, 64).fieldOf("max_surface_difference").forGetter(s -> s.maxSurfaceDifference)
    ).apply(instance, WindmoorTreeStructure::new));

    private final JigsawStructure delegate;
    private final int supportRadius;
    private final int sampleStep;
    private final int minSurfaceY;
    private final int maxSurfaceDifference;

    /**
     * @param delegate 原版拼图放置器
     * @param supportRadius 树干中心周围要求连续承重的半径
     * @param sampleStep 地表采样步长
     * @param minSurfaceY 最低有效岛面高度
     * @param maxSurfaceDifference 样本与中心岛面的最大高差
     */
    private WindmoorTreeStructure(JigsawStructure delegate, int supportRadius, int sampleStep,
                                  int minSurfaceY, int maxSurfaceDifference) {
        super(delegate.getModifiedStructureSettings());
        this.delegate = delegate;
        this.supportRadius = supportRadius;
        this.sampleStep = sampleStep;
        this.minSurfaceY = minSurfaceY;
        this.maxSurfaceDifference = maxSurfaceDifference;
    }

    /**
     * 检查实际旋转后的树干承重区域；复用已组装部件，避免再次随机生成改变检查位置。
     * @param context 生成上下文
     * @return 岛屿内部的生成结果，边缘或虚空返回空
     */
    @Override
    public Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        Optional<GenerationStub> candidate = delegate.findGenerationPoint(context);
        if (candidate.isEmpty()) {
            return Optional.empty();
        }
        StructurePiecesBuilder pieces = candidate.get().getPiecesBuilder();
        if (pieces.isEmpty() || !hasStableTrunkSupport(pieces.getBoundingBox(), (x, z) ->
                context.chunkGenerator().getFirstFreeHeight(x, z, Heightmap.Types.WORLD_SURFACE_WG,
                        context.heightAccessor(), context.randomState()))) {
            return Optional.empty();
        }
        return Optional.of(new GenerationStub(candidate.get().position(), Either.right(pieces)));
    }

    /**
     * 地形策略同时供运行时生成和 VERIFY 使用。模板包围盒包含大幅外伸的树冠，
     * 因此只检查中心附近的树干承重区域，避免把岛屿外围的大量有效位置误判为空中。
     * @param bounds 旋转后的实际模板包围盒
     * @param surfaceHeight 无需加载区块的地表高度查询
     * @return 树干承重区域均为接近中心高度的岛面
     */
    public boolean hasStableTrunkSupport(BoundingBox bounds, IntBinaryOperator surfaceHeight) {
        int centerX = bounds.getCenter().getX();
        int centerZ = bounds.getCenter().getZ();
        int center = surfaceHeight.applyAsInt(centerX, centerZ);
        if (center < minSurfaceY) {
            return false;
        }
        int maxX = centerX + supportRadius;
        int maxZ = centerZ + supportRadius;
        for (int x = centerX - supportRadius; ; x = Math.min(x + sampleStep, maxX)) {
            for (int z = centerZ - supportRadius; ; z = Math.min(z + sampleStep, maxZ)) {
                int height = surfaceHeight.applyAsInt(x, z);
                if (height < minSurfaceY || Math.abs((long) height - center) > maxSurfaceDifference) {
                    return false;
                }
                if (z == maxZ) {
                    break;
                }
            }
            if (x == maxX) {
                return true;
            }
        }
    }

    /** @return 自定义结构类型，确保保存和重新编码仍保留岛面限制。 */
    @Override
    public StructureType<?> type() {
        return BuiltInRegistries.STRUCTURE_TYPE.get(PDRuinsRegistration.getRegisteredStructure("windmoor_tree_0")
                .typeKey().location());
    }
}
