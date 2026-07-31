package com.pasterdream.pasterdreammod.worldgen.structure;

import com.mojang.serialization.MapCodec;
import com.pasterdream.pasterdreammod.api.util.ServerScheduler;
import com.pasterdream.pasterdreammod.world.PDAaroncosArenaSpawnData;
import com.pasterdream.pasterdreammod.worldgen.PDAaroncosArenaWorldgen;
import net.minecraft.core.BlockPos;
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
 * 通过结构集正常随机生成，与其他遗迹一致；但覆写 {@link #findGenerationPoint}
 * 实现"关门"逻辑：主世界第一个候选点生成成功后，用
 * {@link PDAaroncosArenaSpawnData} 记录坐标，之后所有候选点直接返回空（不再生成）。
 * <p>
 * <b>仅对主世界生效</b>：其他维度（如灯影世界）由原版 {@code minecraft:jigsaw}
 * 结构照常生成，不受关门影响。
 * <p>
 * 由于 {@link JigsawStructure} 为 final 类，本类采用<b>组合</b>方式包装
 * 一个 JigsawStructure 委托实例完成实际生成逻辑。
 */
public final class AaroncosArenaPortalStructure extends Structure {

    /** 关门逻辑全局锁（chunk 生成线程并发访问） */
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
     * 生成点判定：主世界"关门"逻辑 + 委托 jigsaw 结构实际生成。
     *
     * @param context 结构生成上下文
     * @return 生成桩；已生成过或候选点不可用（海洋）时为空
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

        PDAaroncosArenaSpawnData spawnData;
        boolean claimed;
        synchronized (CLAIM_LOCK) {
            spawnData = PDAaroncosArenaSpawnData.get(overworld);
            if (spawnData.isPlaced()) {
                return Optional.empty(); // 已生成过，关门
            }
            spawnData.markPlaced(); // 先占位，防止并发 chunk 生成两座
            claimed = true;
        }

        Optional<GenerationStub> stub = delegate.findGenerationPoint(context);
        if (claimed && stub.isEmpty()) {
            synchronized (CLAIM_LOCK) {
                spawnData.rollback(); // 本次候选生成失败，回滚占位，允许后续候选尝试
            }
            return stub;
        }

        if (stub.isPresent()) {
            final ServerLevel overworldRef = overworld;
            final BlockPos position = stub.get().position();
            // 延迟到服务端主线程：记录精确中心、分帧刷竞技场群系、启动感染
            ServerScheduler.schedule(1,
                    () -> PDAaroncosArenaWorldgen.onArenaGenerated(overworldRef, position));
        }
        return stub;
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
