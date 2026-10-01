package com.pasterdream.pasterdreammod.worldgen.structure;

import com.mojang.serialization.MapCodec;
import com.pasterdream.pasterdreammod.world.PDAaroncosArenaSpawnData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.Optional;

/**
 * 亚伦柯斯竞技场传送门遗迹结构（主世界专用，每世界仅生成一次）。
 * <p>
 * 通过结构集正常随机生成，与其他遗迹一致；覆写 {@link #findGenerationPoint}
 * 实现"关门"逻辑：主世界已<b>真实放置</b>过竞技场后，后续所有候选点返回空
 * （不再生成），保证竞技场只生成一次。
 * <p>
 * <b>本方法必须是纯查询，禁止任何持久化副作用</b>（历史恶性 BUG）：
 * {@code findGenerationPoint} 是 Minecraft 的结构查询/预测方法，
 * 任何需要了解结构信息的场合都会调用它——包括第三方结构搜索工具
 * （如探险家指南针的 {@code Structure.generate} / {@code findGenerationPoint} 预览）、
 * {@code /locate} 等。早期版本曾在此处 {@code markPlaced()} 并延迟启动遗迹感染，
 * 导致玩家仅用指南针搜索一次就被永久感染、且结构因提前关门永远不再生成。
 * <p>
 * 正确做法：本类只做只读关门判定（读 {@link PDAroncosArenaSpawnData#isPlaced}）；
 * "结构已真实放置"的确认由传送门方块实际落入世界触发，经
 * {@code PDAroncosArenaWorldgen#offerPendingPlacement} 入队、
 * 主线程确认器 {@code PDAroncosArenaWorldgen#confirmArenaPlacement} 落库，
 * 结构查询/预览路径永远不会产生任何写入。
 * <p>
 * <b>仅对主世界生效</b>：其他维度（如灯影世界）由原版 {@code minecraft:jigsaw}
 * 结构照常生成，不受关门影响。
 * <p>
 * 由于 {@link JigsawStructure} 为 final 类，本类采用<b>组合</b>方式包装
 * 一个 JigsawStructure 委托实例完成实际生成逻辑。
 */
public final class AaroncosArenaPortalStructure extends Structure {

    /** 关门判定的全局锁：结构查询可能被区块生成工作线程并发调用，SavedData 读取需串行化 */
    private static final Object CLAIM_LOCK = new Object();

    /** 序列化编解码器：复用 JigsawStructure 的字段结构，解码为本类实例 */
    public static final MapCodec<AaroncosArenaPortalStructure> CODEC =
            JigsawStructure.CODEC.xmap(AaroncosArenaPortalStructure::new, AaroncosArenaPortalStructure::unwrap);

    /** 被包装的原版 jigsaw 结构实例（负责实际生成逻辑） */
    private final JigsawStructure delegate;

    /**
     * 包装构造函数。
     *
     * @param delegate 解码出的 jigsaw 结构实例
     */
    private AaroncosArenaPortalStructure(JigsawStructure delegate) {
        super(delegate.getModifiedStructureSettings());
        this.delegate = delegate;
    }

    /**
     * 拆包（CODEC 编码用）。
     *
     * @param structure 本类实例
     * @return 被包装的 jigsaw 结构实例
     */
    private static JigsawStructure unwrap(AaroncosArenaPortalStructure structure) {
        return structure.delegate;
    }

    /**
     * 生成点判定（纯查询，无副作用）。
     * <p>
     * 仅当主世界已真实放置过竞技场（持久化记录）时返回空以抑制后续生成；
     * 未放置时正常委托 jigsaw 结构。不做任何写入、不调度任何任务，
     * 第三方结构查询/预览调用本方法零副作用。
     *
     * @param context 结构生成上下文
     * @return 生成桩；已放置过或候选点不可用（海洋）时为空
     */
    @Override
    public Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        // 仅主世界关门：其他维度（灯影世界等）该结构照常生成
        if (!isOverworld(context)) {
            return delegate.findGenerationPoint(context);
        }

        // 候选点陆地预检：避免竞技场生成在海洋/水下（biomes 为 is_overworld 时海洋也匹配）
        if (isBelowSeaLevel(context, context.chunkPos())) {
            return Optional.empty();
        }

        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        ServerLevel overworld = server == null ? null : server.getLevel(Level.OVERWORLD);
        if (overworld == null) {
            return delegate.findGenerationPoint(context);
        }

        // 只读关门：已真实放置过 → 抑制本候选点。
        // 锁仅为串行化 SavedData 读取（computeIfAbsent 非线程安全），此处绝无写入
        synchronized (CLAIM_LOCK) {
            if (PDAaroncosArenaSpawnData.get(overworld).isPlaced()) {
                return Optional.empty();
            }
        }
        return delegate.findGenerationPoint(context);
    }

    /**
     * 判断候选点所在维度是否为主世界（通过区块生成器的 noise settings 判定）。
     *
     * @param context 结构生成上下文
     * @return true 若为主世界
     */
    private static boolean isOverworld(GenerationContext context) {
        if (context.chunkGenerator() instanceof NoiseBasedChunkGenerator noiseGenerator) {
            ResourceLocation settingsId = noiseGenerator.generatorSettings()
                    .unwrapKey()
                    .map(key -> key.location())
                    .orElse(null);
            return ResourceLocation.withDefaultNamespace("overworld").equals(settingsId);
        }
        return false;
    }

    /**
     * 判断候选 chunk 地表是否低于海平面（海洋/水下）。
     * <p>
     * 使用 {@link ChunkGenerator#getBaseHeight} 纯噪声预测，不生成 chunk。
     *
     * @param context 结构生成上下文
     * @param chunkPos 候选 chunk
     * @return true 若地表低于海平面
     */
    private static boolean isBelowSeaLevel(GenerationContext context, net.minecraft.world.level.ChunkPos chunkPos) {
        ChunkGenerator generator = context.chunkGenerator();
        if (generator instanceof NoiseBasedChunkGenerator noiseGenerator) {
            int x = chunkPos.getMiddleBlockX();
            int z = chunkPos.getMiddleBlockZ();
            int seaLevel = noiseGenerator.generatorSettings().value().seaLevel();
            int surfaceY = generator.getBaseHeight(x, z, Heightmap.Types.WORLD_SURFACE_WG,
                    context.heightAccessor(), context.randomState());
            return surfaceY < seaLevel + 2;
        }
        return false;
    }

    /**
     * 返回本结构的类型（沿用被包装的 jigsaw 类型）。
     *
     * @return StructureType
     */
    @Override
    public StructureType<?> type() {
        return delegate.type();
    }
}
