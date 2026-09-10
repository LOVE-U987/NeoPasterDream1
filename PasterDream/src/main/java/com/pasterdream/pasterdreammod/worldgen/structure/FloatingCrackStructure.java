package com.pasterdream.pasterdreammod.worldgen.structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pools.DimensionPadding;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasBinding;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasLookup;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;

import java.util.List;
import java.util.Optional;

/**
 * 浮岛染梦裂隙结构 —— 高空浮岛样式的 jigsaw 结构。
 * <p>
 * 与直接使用 {@link JigsawStructure} 不同，本类把起始高度从"投影到地表"改为
 * <b>动态浮空</b>：读取候选区块的地表高度，地表不高于 {@code float_low_y} 时置于
 * {@code float_low_y}，否则置于 {@code float_high_y}，对齐 Java 出生点裂隙
 * （{@code PDOverworldOriginCrackWorldgen}）的 Y=110/160 逻辑。
 * <p>
 * 同时支持<b>原点避让</b>：当候选区块落入以 {@code origin_chunk_x/z} 为中心、
 * {@code origin_exclusion_radius} 为半径（区块）的方形区域时直接返回空，
 * 避免随机裂隙与原点浮岛裂隙重叠。
 * <p>
 * 由于 {@link JigsawStructure} 为 final 类，本类不继承它，而是复制其字段结构
 * （codec）并直接调用 {@link JigsawPlacement#addPieces} 完成生成。
 */
public final class FloatingCrackStructure extends Structure {

    /** 序列化编解码器：字段结构对齐 {@link JigsawStructure}，并追加浮岛/避让参数 */
    public static final MapCodec<FloatingCrackStructure> CODEC =
            RecordCodecBuilder.mapCodec(instance -> instance.group(
                    settingsCodec(instance),
                    StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter(s -> s.startPool),
                    ResourceLocation.CODEC.optionalFieldOf("start_jigsaw_name").forGetter(s -> s.startJigsawName),
                    Codec.intRange(0, 20).fieldOf("size").forGetter(s -> s.maxDepth),
                    HeightProvider.CODEC.fieldOf("start_height").forGetter(s -> s.startHeight),
                    Codec.BOOL.fieldOf("use_expansion_hack").forGetter(s -> s.useExpansionHack),
                    Heightmap.Types.CODEC.optionalFieldOf("project_start_to_heightmap").forGetter(s -> s.projectStartToHeightmap),
                    Codec.intRange(1, 128).fieldOf("max_distance_from_center").forGetter(s -> s.maxDistanceFromCenter),
                    Codec.list(PoolAliasBinding.CODEC).optionalFieldOf("pool_aliases", List.of()).forGetter(s -> s.poolAliases),
                    DimensionPadding.CODEC.optionalFieldOf("dimension_padding", JigsawStructure.DEFAULT_DIMENSION_PADDING).forGetter(s -> s.dimensionPadding),
                    LiquidSettings.CODEC.optionalFieldOf("liquid_settings", JigsawStructure.DEFAULT_LIQUID_SETTINGS).forGetter(s -> s.liquidSettings),
                    Codec.INT.optionalFieldOf("float_low_y", 110).forGetter(s -> s.floatLowY),
                    Codec.INT.optionalFieldOf("float_high_y", 160).forGetter(s -> s.floatHighY),
                    Codec.INT.optionalFieldOf("origin_exclusion_radius", 0).forGetter(s -> s.originExclusionRadius),
                    Codec.INT.optionalFieldOf("origin_chunk_x", 0).forGetter(s -> s.originChunkX),
                    Codec.INT.optionalFieldOf("origin_chunk_z", 0).forGetter(s -> s.originChunkZ)
            ).apply(instance, FloatingCrackStructure::new));

    private final Holder<StructureTemplatePool> startPool;
    private final Optional<ResourceLocation> startJigsawName;
    private final int maxDepth;
    private final HeightProvider startHeight;
    private final boolean useExpansionHack;
    private final Optional<Heightmap.Types> projectStartToHeightmap;
    private final int maxDistanceFromCenter;
    private final List<PoolAliasBinding> poolAliases;
    private final DimensionPadding dimensionPadding;
    private final LiquidSettings liquidSettings;

    /** 地表不高于该值时裂隙置于此高度 */
    private final int floatLowY;
    /** 地表高于 floatLowY 时裂隙置于此高度 */
    private final int floatHighY;
    /** 原点避让半径（区块），0 表示不避让 */
    private final int originExclusionRadius;
    /** 原点避让中心区块 X */
    private final int originChunkX;
    /** 原点避让中心区块 Z */
    private final int originChunkZ;

    /**
     * 编解码器构造函数。
     *
     * @param settings                 结构通用设置
     * @param startPool                起始模板池
     * @param startJigsawName          起始拼图名
     * @param maxDepth                 最大深度
     * @param startHeight              起始高度提供器（无浮岛投影时回退使用）
     * @param useExpansionHack         是否使用扩展 hack
     * @param projectStartToHeightmap  起始投影高度图类型（存在即启用动态浮岛）
     * @param maxDistanceFromCenter    距中心最大距离
     * @param poolAliases              模板池别名
     * @param dimensionPadding         维度内边距
     * @param liquidSettings           流体设置
     * @param floatLowY                低浮空高度
     * @param floatHighY               高浮空高度
     * @param originExclusionRadius    原点避让半径（区块）
     * @param originChunkX             原点避让中心区块 X
     * @param originChunkZ             原点避让中心区块 Z
     */
    private FloatingCrackStructure(StructureSettings settings,
                                   Holder<StructureTemplatePool> startPool,
                                   Optional<ResourceLocation> startJigsawName,
                                   int maxDepth,
                                   HeightProvider startHeight,
                                   boolean useExpansionHack,
                                   Optional<Heightmap.Types> projectStartToHeightmap,
                                   int maxDistanceFromCenter,
                                   List<PoolAliasBinding> poolAliases,
                                   DimensionPadding dimensionPadding,
                                   LiquidSettings liquidSettings,
                                   int floatLowY,
                                   int floatHighY,
                                   int originExclusionRadius,
                                   int originChunkX,
                                   int originChunkZ) {
        super(settings);
        this.startPool = startPool;
        this.startJigsawName = startJigsawName;
        this.maxDepth = maxDepth;
        this.startHeight = startHeight;
        this.useExpansionHack = useExpansionHack;
        this.projectStartToHeightmap = projectStartToHeightmap;
        this.maxDistanceFromCenter = maxDistanceFromCenter;
        this.poolAliases = poolAliases;
        this.dimensionPadding = dimensionPadding;
        this.liquidSettings = liquidSettings;
        this.floatLowY = floatLowY;
        this.floatHighY = floatHighY;
        this.originExclusionRadius = originExclusionRadius;
        this.originChunkX = originChunkX;
        this.originChunkZ = originChunkZ;
    }

    /**
     * 生成点判定：原点避让 + 动态浮空高度。
     *
     * @param context 结构生成上下文
     * @return 生成桩；落入原点避让区时为空
     */
    @Override
    public Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        ChunkPos chunkPos = context.chunkPos();

        // 原点避让：候选区块落入排除区则不生成
        if (originExclusionRadius > 0
                && Math.abs(chunkPos.x - originChunkX) <= originExclusionRadius
                && Math.abs(chunkPos.z - originChunkZ) <= originExclusionRadius) {
            return Optional.empty();
        }

        int y;
        if (projectStartToHeightmap.isPresent()) {
            // 动态浮空：按候选区块地表高度选择低/高浮空高度
            int surface = context.chunkGenerator().getFirstFreeHeight(
                    chunkPos.getMiddleBlockX(),
                    chunkPos.getMiddleBlockZ(),
                    projectStartToHeightmap.get(),
                    context.heightAccessor(),
                    context.randomState());
            y = surface <= floatLowY ? floatLowY : floatHighY;
        } else {
            y = startHeight.sample(context.random(),
                    new WorldGenerationContext(context.chunkGenerator(), context.heightAccessor()));
        }

        BlockPos startPos = new BlockPos(chunkPos.getMinBlockX(), y, chunkPos.getMinBlockZ());
        return JigsawPlacement.addPieces(
                context,
                startPool,
                startJigsawName,
                maxDepth,
                startPos,
                useExpansionHack,
                Optional.empty(),
                maxDistanceFromCenter,
                PoolAliasLookup.create(poolAliases, startPos, context.seed()),
                dimensionPadding,
                liquidSettings);
    }

    /**
     * 返回结构类型（沿用原版 jigsaw 类型；反序列化由本类 codec 承担）。
     *
     * @return 结构类型
     */
    @Override
    public StructureType<?> type() {
        return StructureType.JIGSAW;
    }
}
