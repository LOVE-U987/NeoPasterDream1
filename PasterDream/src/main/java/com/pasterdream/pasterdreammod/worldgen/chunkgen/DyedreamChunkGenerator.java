package com.pasterdream.pasterdreammod.worldgen.chunkgen;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.pasterdream.pasterdreammod.registry.PDBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import org.jetbrains.annotations.NotNull;

import java.util.Random;
import java.util.concurrent.CompletableFuture;

/**
 * 染梦世界自定义 ChunkGenerator —— 浮岛地形生成器
 * <p>
 * 继承 {@link NoiseBasedChunkGenerator}，重写 {@link #fillFromNoise} 方法
 * 以实现染梦世界特有的浮岛/海洋混合地形。
 * <p>
 * 核心生成逻辑：
 * <ol>
 *   <li>使用大陆性噪声确定每个柱子是岛屿还是海洋</li>
 *   <li>岛屿区域：海平面以上生成地形，表面高度由噪声决定</li>
 *   <li>海洋区域：仅有海平面以下的水</li>
 *   <li>洞穴：使用三维噪声在地下挖出空洞</li>
 * </ol>
 * <p>
 * <b>设计说明</b>：在 NeoForge 21.1.219 中 {@code NoiseBasedChunkGenerator.doFill}
 * 被声明为 {@code private}，无法被子类重写。
 * 因此本类重写 {@link #fillFromNoise} 并先调用 {@code super.fillFromNoise()}，
 * 确保父类完成 NoiseChunk 创建和 blend 状态初始化，
 * 然后使用 {@link #applyDyedreamTerrain} 覆盖为自定义浮岛地形。
 * <p>
 * 参考自 Eternal Starlight 的 ESChunkGenerator 实现模式（其 NeoForge 21.1.230
 * 已将 doFill 改为 protected），
 * 但噪声系统全部使用原版 Minecraft API（DensityFunction / NormalNoise）。
 *
 * @author PasterDream Team
 */
public class DyedreamChunkGenerator extends NoiseBasedChunkGenerator {

    /** 空气方块 */
    private static final BlockState AIR = Blocks.AIR.defaultBlockState();

    /** 水体方块 */
    private static final BlockState WATER = Blocks.WATER.defaultBlockState();

    /** 岩浆方块 */
    private static final BlockState LAVA = Blocks.LAVA.defaultBlockState();

    /** 基底岩层方块 */
    private static final BlockState BEDROCK = Blocks.BEDROCK.defaultBlockState();

    /**
     * DyedreamChunkGenerator 的 MapCodec —— 用于 JSON 反序列化
     * <p>
     * JSON 格式示例：
     * <pre>
     * {
     *   "type": "pasterdream:dyedream_chunk_generator",
     *   "biome_source": { "type": "minecraft:multi_noise", ... },
     *   "settings": { "name": "pasterdream:dyedream_world", ... }
     * }
     * </pre>
     */
    public static final MapCodec<DyedreamChunkGenerator> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    BiomeSource.CODEC.fieldOf("biome_source")
                            .forGetter(gen -> ((DyedreamChunkGenerator) gen).biomeSource),
                    NoiseGeneratorSettings.CODEC.fieldOf("settings")
                            .forGetter(NoiseBasedChunkGenerator::generatorSettings)
            ).apply(instance, instance.stable(DyedreamChunkGenerator::new))
    );

    /** 默认方块（从 settings 中读取） */
    private final BlockState defaultBlock;

    /** 默认流体（从 settings 中读取） */
    private final BlockState defaultFluid;

    /** 海平面高度（从 settings 中读取） */
    private final int seaLevel;

    /** 洞穴噪声判定阈值 —— 低于此值的噪声值为洞穴 */
    private static final double CAVE_THRESHOLD = -0.35;

    /** 洞穴生成最小 Y（避免挖穿基岩底部） */
    private static final int CAVE_MIN_Y = -60;

    /** 基岩层厚度（从世界底部向上的格数） */
    private static final int BEDROCK_DEPTH = 5;

    /**
     * 构造函数
     *
     * @param biomeSource 群系源（可以是任意已注册的 BiomeSource 类型）
     * @param settings    NoiseGeneratorSettings 配置（包含 default_block、sea_level 等）
     */
    public DyedreamChunkGenerator(BiomeSource biomeSource, Holder<NoiseGeneratorSettings> settings) {
        super(biomeSource, settings);
        if (settings.isBound()) {
            this.defaultBlock = settings.value().defaultBlock();
            this.defaultFluid = settings.value().defaultFluid();
            this.seaLevel = settings.value().seaLevel();
        } else {
            this.defaultBlock = Blocks.STONE.defaultBlockState();
            this.defaultFluid = WATER;
            this.seaLevel = 55;
        }
    }

    /**
     * 返回 ChunkGenerator 的 CODEC
     *
     * @return MapCodec 实例
     */
    @Override
    protected @NotNull MapCodec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    /**
     * 从噪声生成区块地形 —— 核心入口方法
     * <p>
     * 策略：先调用 {@code super.fillFromNoise()} 让父类完成必要的初始化
     * （创建 NoiseChunk、初始化 blend 状态、含水层系统等），
     * 然后在 {@link CompletableFuture#thenApply} 回调中用浮岛地形覆盖原版方块。
     * <p>
     * 不直接重写 {@code doFill} 的原因是：在 NeoForge 21.1.219 中
     * {@code doFill} 为 private 方法，子类无法访问或重写。
     * <p>
     * 浮岛生成逻辑：
     * <ol>
     *   <li>遍历区块内所有列（16x16）</li>
     *   <li>对于每列，使用大陆性噪声判定是否为岛屿</li>
     *   <li>计算该列的表面高度</li>
     *   <li>从最低 Y 到表面高度填充默认方块</li>
     *   <li>在深层应用洞穴噪声挖出空洞</li>
     *   <li>海洋区域填充水</li>
     *   <li>底部生成基岩层</li>
     *   <li>雕刻梦河河流网络</li>
     * </ol>
     *
     * @param blender          混合器（用于区块间平滑过渡）
     * @param randomState      随机状态（含噪声路由器和采样器）
     * @param structureManager 结构管理器
     * @param chunkAccess      要填充的区块
     * @return 包含填充后区块的 CompletableFuture
     */
    @Override
    public @NotNull CompletableFuture<ChunkAccess> fillFromNoise(@NotNull Blender blender,
                                                                  @NotNull RandomState randomState,
                                                                  @NotNull StructureManager structureManager,
                                                                  @NotNull ChunkAccess chunkAccess) {
        // 先让父类完成初始化（创建 NoiseChunk、blend 状态、含水层等）
        // 父类 fillFromNoise 会调用其 private doFill 生成原版地形
        // 然后在 thenApply 回调中用我们的自定义浮岛地形覆盖
        return super.fillFromNoise(blender, randomState, structureManager, chunkAccess)
                .thenApply(chunk -> applyDyedreamTerrain(chunk, randomState));
    }

    /**
     * 用染梦世界浮岛地形覆盖区块中的方块
     * <p>
     * 在父类完成原版地形生成后调用，用浮岛+洞穴+河流系统替换所有方块。
     *
     * @param chunkAccess 区块访问（已被父类填充原版地形）
     * @param randomState 随机状态
     * @return 填充后的区块访问
     */
    private @NotNull ChunkAccess applyDyedreamTerrain(@NotNull ChunkAccess chunkAccess,
                                                       @NotNull RandomState randomState) {
        ChunkPos chunkPos = chunkAccess.getPos();
        // 初始化高度图
        Heightmap oceanFloorMap = chunkAccess.getOrCreateHeightmapUnprimed(Heightmap.Types.OCEAN_FLOOR_WG);
        Heightmap worldSurfaceMap = chunkAccess.getOrCreateHeightmapUnprimed(Heightmap.Types.WORLD_SURFACE_WG);

        int minBlockX = chunkPos.getMinBlockX();
        int minBlockZ = chunkPos.getMinBlockZ();
        int minY = getMinY();
        int minYSec = Math.floorDiv(minY, 16);
        int numSec = chunkAccess.getSections().length;
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

        // 预计算每列的地形高度，避免在多层循环中重复计算噪声
        int[][] surfaceHeights = new int[16][16];
        for (int dx = 0; dx < 16; dx++) {
            for (int dz = 0; dz < 16; dz++) {
                int worldX = minBlockX + dx;
                int worldZ = minBlockZ + dz;
                surfaceHeights[dx][dz] = DyedreamNoises.computeSurfaceHeight(randomState, worldX, worldZ, seaLevel);
            }
        }

        // 逐层填充方块
        for (int secY = 0; secY < numSec; secY++) {
            LevelChunkSection section = chunkAccess.getSection(secY);
            int sectionBaseY = (minYSec + secY) * 16;

            for (int localY = 0; localY < 16; localY++) {
                int worldY = sectionBaseY + localY;

                for (int localX = 0; localX < 16; localX++) {
                    int worldX = minBlockX + localX;

                    for (int localZ = 0; localZ < 16; localZ++) {
                        int worldZ = minBlockZ + localZ;

                        int surfaceHeight = surfaceHeights[localX][localZ];
                        BlockState blockState = getBlockForPosition(
                                worldX, worldY, worldZ, surfaceHeight, minY, randomState
                        );

                        if (blockState != AIR) {
                            section.setBlockState(localX, localY, localZ, blockState, false);
                            oceanFloorMap.update(localX, worldY, localZ, blockState);
                            worldSurfaceMap.update(localX, worldY, localZ, blockState);
                            if (!blockState.getFluidState().isEmpty()) {
                                mutablePos.set(worldX, worldY, worldZ);
                                chunkAccess.markPosForPostprocessing(mutablePos);
                            }
                        }
                    }
                }
            }
        }

        // ==================== 梦河河流系统 ====================
        // 在填充完基础地形后，使用侵蚀度噪声采样判定河流位置
        // 河流会在地形上冲刷出河床，替换为砂质地，河岸点缀发光水晶灯
        carveRivers(chunkAccess, chunkPos, minBlockX, minBlockZ, minY, surfaceHeights, randomState);

        // 高空表面处理（Y>100 浮岛区域）由 buildSurface() 中统一完成
        // 地面区域由 super.buildSurface() 的 surface_rule 处理

        return chunkAccess;
    }

    /**
     * 在区块中雕刻梦河河流网络
     * <p>
     * 使用 {@link DyedreamNoises#sampleRiverNoise} 的侵蚀度噪声判定河流位置。
     * 预计算 16x16 河流遮罩，统一处理河床挖掘和河岸装饰。
     * <p>
     * 河流特征：
     * <ul>
     *   <li>利用噪声自然形成的宽度 7~15 格变化</li>
     *   <li>深度：地表高度减去 3~5 格随机深度</li>
     *   <li>河床替换为 {@link PDBlocks#DYEDREAM_SAND} 染梦沙</li>
     *   <li>河岸点缀 {@link PDBlocks#MELTDREAM_CRYSTAL_LAMP} 融梦水晶灯</li>
     * </ul>
     *
     * @param chunkAccess    区块访问
     * @param chunkPos       区块坐标
     * @param minBlockX      区块最小方块 X
     * @param minBlockZ      区块最小方块 Z
     * @param minY           世界最低 Y
     * @param surfaceHeights 预计算的地表高度矩阵 [16][16]
     * @param randomState    随机状态
     */
    private void carveRivers(ChunkAccess chunkAccess, ChunkPos chunkPos,
                              int minBlockX, int minBlockZ, int minY,
                              int[][] surfaceHeights, RandomState randomState) {
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        Random localRand = new Random(chunkPos.x * 3129871L ^ chunkPos.z * 116129781L);

        // 预计算 16x16 河流遮罩
        int riverThreshold = 0; // 超过阈值标记为河流
        // 使用 river noise 生成河流遮罩
        double[][] riverMask = new double[16][16];
        boolean[][] isRiver = new boolean[16][16];

        for (int dx = 0; dx < 16; dx++) {
            for (int dz = 0; dz < 16; dz++) {
                int worldX = minBlockX + dx;
                int worldZ = minBlockZ + dz;
                double riverNoise = DyedreamNoises.sampleRiverNoise(randomState, worldX, worldZ);
                riverMask[dx][dz] = riverNoise;
                isRiver[dx][dz] = riverNoise > DyedreamNoises.RIVER_THRESHOLD;
                if (isRiver[dx][dz]) {
                    riverThreshold++;
                }
            }
        }

        // 如果整块都没有河流，跳过
        if (riverThreshold == 0) {
            return;
        }

        // 第一遍：挖掘河床
        for (int dx = 0; dx < 16; dx++) {
            for (int dz = 0; dz < 16; dz++) {
                if (!isRiver[dx][dz]) continue;

                int worldX = minBlockX + dx;
                int worldZ = minBlockZ + dz;
                int surfaceHeight = surfaceHeights[dx][dz];

                // 只在岛屿区域挖河（海洋区域没有挖河的意义）
                if (!DyedreamNoises.isIsland(randomState, worldX, worldZ)) {
                    continue;
                }

                // 河流宽度掩膜：使用位置哈希和噪声值生成自然宽度变化
                double widthFactor = (riverMask[dx][dz] - DyedreamNoises.RIVER_THRESHOLD) / (1.0 - DyedreamNoises.RIVER_THRESHOLD);
                int riverDepth = 3 + localRand.nextInt(3); // 3~5 格深
                if (widthFactor > 0.5) {
                    // 噪声值越接近 1，河流越深
                    riverDepth += localRand.nextInt(2);
                }

                // 从地表向下挖掘
                int bedY = surfaceHeight - riverDepth;
                if (bedY < minY + 1) bedY = minY + 1;

                for (int y = surfaceHeight; y >= bedY; y--) {
                    mutablePos.set(worldX, y, worldZ);
                    BlockState existing = chunkAccess.getBlockState(mutablePos);
                    // 仅替换非空气、非流体方块
                    if (!existing.isAir() && existing.getFluidState().isEmpty()) {
                        if (y == bedY) {
                            // 河床底部替换为染梦沙
                            chunkAccess.setBlockState(mutablePos,
                                    PDBlocks.DYEDREAM_SAND.get().defaultBlockState(), false);
                        } else {
                            // 河床侧壁全部填水（不依赖海平面，高空浮岛河流也要有水）
                            BlockState fillState = defaultFluid;
                            chunkAccess.setBlockState(mutablePos, fillState, false);
                            // 标记流体后处理
                            if (!fillState.getFluidState().isEmpty()) {
                                chunkAccess.markPosForPostprocessing(mutablePos);
                            }
                        }
                    }
                }
            }
        }

        // 第二遍：河岸装饰（在河流边缘的非河流位置放置水晶灯）
        for (int dx = 0; dx < 16; dx++) {
            for (int dz = 0; dz < 16; dz++) {
                if (isRiver[dx][dz]) continue; // 只在河岸上放

                // 检查 8 邻域是否有河流
                boolean adjacentToRiver = false;
                for (int nx = -1; nx <= 1 && !adjacentToRiver; nx++) {
                    for (int nz = -1; nz <= 1 && !adjacentToRiver; nz++) {
                        if (nx == 0 && nz == 0) continue;
                        int ndx = dx + nx;
                        int ndz = dz + nz;
                        if (ndx >= 0 && ndx < 16 && ndz >= 0 && ndz < 16) {
                            if (isRiver[ndx][ndz]) {
                                adjacentToRiver = true;
                            }
                        }
                    }
                }

                if (!adjacentToRiver) continue;

                int worldX = minBlockX + dx;
                int worldZ = minBlockZ + dz;
                int surfaceHeight = surfaceHeights[dx][dz];

                // 在地表随机放置水晶灯（30% 概率）
                if (localRand.nextFloat() < 0.3f) {
                    mutablePos.set(worldX, surfaceHeight, worldZ);
                    BlockState existing = chunkAccess.getBlockState(mutablePos);
                    // 确保地表方块是固体且上方是空气
                    if (!existing.isAir() && existing.getFluidState().isEmpty()
                            && chunkAccess.getBlockState(mutablePos.above()).isAir()) {
                        // 在水晶灯下方垫一个基础方块防止悬空
                        chunkAccess.setBlockState(mutablePos.above(),
                                PDBlocks.MELTDREAM_CRYSTAL_LAMP.get().defaultBlockState(), false);
                    }
                }
            }
        }
    }

    /**
     * 恢复高空浮岛表面方块（仅 Y>100 区域）
     * <p>
     * 高空区域超出父类 preliminary surface 的计算范围，surface_rule 无法正常生效，
     * 因此在此处手动替换。地面区域（Y≤100）由 {@code super.buildSurface()} 的
     * surface_rule 处理。
     *
     * @param chunkAccess 区块访问
     * @param minBlockX   区块最小 X
     * @param minBlockZ   区块最小 Z
     * @param minY        世界最低 Y
     */
    private void restoreHighAltitudeSurface(ChunkAccess chunkAccess, int minBlockX, int minBlockZ,
                                             int minY) {
        BlockPos.MutableBlockPos surfacePos = new BlockPos.MutableBlockPos();
        BlockState grassBlock = PDBlocks.DYEDREAM_GRASS.get().defaultBlockState();
        BlockState dirtBlock = PDBlocks.DYEDREAM_DIRT.get().defaultBlockState();

        for (int dx = 0; dx < 16; dx++) {
            for (int dz = 0; dz < 16; dz++) {
                int worldX = minBlockX + dx;
                int worldZ = minBlockZ + dz;

                // 从最高处向下扫描，找到第一个固体方块
                int topY = chunkAccess.getMaxBuildHeight() - 1;
                for (int y = topY; y > minY; y--) {
                    BlockState blockState = chunkAccess.getBlockState(surfacePos.set(worldX, y, worldZ));
                    if (!blockState.isAir() && blockState.getFluidState().isEmpty()) {
                        // 只处理高空浮岛（Y > 100）
                        if (y > 100) {
                            // 替换表面为草方块
                            chunkAccess.setBlockState(surfacePos, grassBlock, false);
                            // 替换次表面为泥土（如果下方是固体且非沙子）
                            surfacePos.move(0, -1, 0);
                            BlockState belowState = chunkAccess.getBlockState(surfacePos);
                            if (!belowState.isAir() && belowState.getFluidState().isEmpty()
                                    && !belowState.is(PDBlocks.DYEDREAM_SAND.get())) {
                                chunkAccess.setBlockState(surfacePos, dirtBlock, false);
                            }
                        }
                        break; // 找到表面后结束当前列
                    }
                }
            }
        }
    }

    /**
     * 恢复河道水体（在 buildSurface 之后重新填充河水）
     * <p>
     * surface_rule 可能在河道位置放置了表面方块覆盖水体，
     * 此方法恢复河道中的水，确保河流可见。
     *
     * @param chunkAccess 区块访问
     * @param chunkPos    区块坐标
     * @param minBlockX   区块最小 X
     * @param minBlockZ   区块最小 Z
     * @param minY        世界最低 Y
     * @param randomState 随机状态
     */
    private void restoreRiverWater(ChunkAccess chunkAccess, ChunkPos chunkPos,
                                    int minBlockX, int minBlockZ, int minY,
                                    RandomState randomState) {
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

        for (int dx = 0; dx < 16; dx++) {
            for (int dz = 0; dz < 16; dz++) {
                int worldX = minBlockX + dx;
                int worldZ = minBlockZ + dz;

                // 只有岛屿位置才可能有河流
                if (!DyedreamNoises.isIsland(randomState, worldX, worldZ)) {
                    continue;
                }

                int surfaceHeight = DyedreamNoises.computeSurfaceHeight(
                        randomState, worldX, worldZ, seaLevel);

                // 河流噪声采样：只在侵蚀度高的位置雕刻
                double riverErosion = DyedreamNoises.sampleRiverNoise(randomState, worldX, worldZ);
                if (riverErosion < 0.15) {
                    continue; // 侵蚀度不够，不是河流
                }

                // 河流深度：地表高度往下挖 3~5 格
                int riverDepth = 3 + (int) (riverErosion * 2.0);
                int bedY = surfaceHeight - riverDepth;

                // 从河床往上到地表扫描，填充水体
                for (int y = bedY + 1; y < surfaceHeight; y++) {
                    if (y < minY || y >= chunkAccess.getMaxBuildHeight()) {
                        continue;
                    }
                    mutablePos.set(worldX, y, worldZ);
                    BlockState existing = chunkAccess.getBlockState(mutablePos);
                    // 如果 surface_rule 填了固体方块，恢复为水
                    if (!existing.isAir() && existing.getFluidState().isEmpty()) {
                        chunkAccess.setBlockState(mutablePos, defaultFluid, false);
                        chunkAccess.markPosForPostprocessing(mutablePos);
                    }
                }

                // 河床底部确保是沙子
                if (bedY >= minY && bedY < chunkAccess.getMaxBuildHeight()) {
                    mutablePos.set(worldX, bedY, worldZ);
                    chunkAccess.setBlockState(mutablePos,
                            PDBlocks.DYEDREAM_SAND.get().defaultBlockState(), false);
                }
            }
        }
    }

    /**
     * 构建地表（Surface）
     * <p>
     * 策略：先调用 {@code super.buildSurface()} 应用 JSON 中的 surface_rule
     * （地面区域正确的生物群系表面方块），然后执行以下后处理：
     * <ol>
     *   <li>恢复高空区域（Y>100 浮岛）的表面为染梦草方块 + 泥土</li>
     *   <li>重新填充河道水体，避免被 surface_rule 覆盖</li>
     * </ol>
     *
     * @param worldGenRegion   世界生成区域
     * @param structureManager 结构管理器
     * @param randomState      随机状态
     * @param chunkAccess      区块访问
     */
    @Override
    public void buildSurface(@NotNull WorldGenRegion worldGenRegion, @NotNull StructureManager structureManager,
                              @NotNull RandomState randomState, @NotNull ChunkAccess chunkAccess) {
        // 1. 应用 JSON surface_rule（地面区域的正确表面方块）
        super.buildSurface(worldGenRegion, structureManager, randomState, chunkAccess);

        // 2. 恢复高空浮岛表面（Y>100）
        ChunkPos chunkPos = chunkAccess.getPos();
        int minBlockX = chunkPos.getMinBlockX();
        int minBlockZ = chunkPos.getMinBlockZ();
        int minY = chunkAccess.getMinBuildHeight();
        restoreHighAltitudeSurface(chunkAccess, minBlockX, minBlockZ, minY);

        // 3. 恢复河道水体（surface_rule 可能覆盖了水）
        restoreRiverWater(chunkAccess, chunkPos, minBlockX, minBlockZ, minY, randomState);
    }

    /**
     * 获取指定位置的基础高度（用于结构生成等查询）
     *
     * @param x                   方块坐标 X
     * @param z                   方块坐标 Z
     * @param type                高度图类型
     * @param levelHeightAccessor 高度访问器
     * @param randomState         随机状态
     * @return 该位置的地形表面高度
     */
    @Override
    public int getBaseHeight(int x, int z, @NotNull Heightmap.Types type,
                              @NotNull LevelHeightAccessor levelHeightAccessor, @NotNull RandomState randomState) {
        return DyedreamNoises.computeSurfaceHeight(randomState, x, z, seaLevel);
    }

    /**
     * 获取指定列的基础方块列（用于光照等系统查询）
     *
     * @param x      方块坐标 X
     * @param z      方块坐标 Z
     * @param level  高度访问器
     * @param randomState 随机状态
     * @return 该列的噪声柱
     */
    @Override
    public @NotNull NoiseColumn getBaseColumn(int x, int z, @NotNull LevelHeightAccessor level,
                                               @NotNull RandomState randomState) {
        int minY = getMinY();
        int maxY = level.getMaxBuildHeight();
        int height = maxY - minY;
        BlockState[] states = new BlockState[height];

        int surfaceHeight = DyedreamNoises.computeSurfaceHeight(randomState, x, z, seaLevel);

        for (int y = minY; y < maxY; y++) {
            states[y - minY] = getBlockForPosition(x, y, z, surfaceHeight, minY, randomState);
        }
        return new NoiseColumn(minY, states);
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 获取指定位置的方块状态
     * <p>
     * 包含完整的浮岛地形生成逻辑：岛屿判定、洞穴挖掘、基岩生成、流体填充。
     *
     * @param x             世界坐标 X
     * @param y             世界坐标 Y
     * @param z             世界坐标 Z
     * @param surfaceHeight 该列的表面高度
     * @param minY          世界最低 Y
     * @param randomState   随机状态
     * @return 该位置应放置的方块状态
     */
    private @NotNull BlockState getBlockForPosition(int x, int y, int z, int surfaceHeight, int minY,
                                                     @NotNull RandomState randomState) {
        // 1. 基岩层（世界最底部）
        int bottomOffset = y - minY;
        if (bottomOffset < BEDROCK_DEPTH) {
            // 基岩渐变：底部完全基岩，向上逐渐变为普通方块
            if (bottomOffset == 0) {
                return BEDROCK;
            }
            if (bottomOffset < bedrockThreshold(x, z, bottomOffset)) {
                return BEDROCK;
            }
            // 基岩之上继续地形生成
        }

        // 2. 地形填充
        if (y <= surfaceHeight) {
            // 在表面高度以下：填充默认方块
            if (y < surfaceHeight - 10 && y > CAVE_MIN_Y) {
                // 深层洞穴：使用三维噪声挖空
                double caveNoise = DyedreamNoises.sampleCaveNoise(randomState, x, y, z);
                if (caveNoise < CAVE_THRESHOLD) {
                    // 洞穴内部：高于海平面为空气，低于为岩浆
                    return y > seaLevel ? AIR : LAVA;
                }
            }
            return defaultBlock;
        } else if (y <= seaLevel) {
            // 在海平面与表面高度之间：海洋区域填充水
            if (!DyedreamNoises.isIsland(randomState, x, z)) {
                return defaultFluid;
            }
            // 岛屿区域的"水下"部分保持空气（浮岛底部以下是空气）
            return AIR;
        } else {
            // 高于表面高度：空气
            return AIR;
        }
    }

    /**
     * 计算基岩层阈值（用于基岩渐变分布）
     * <p>
     * 使用方块坐标哈希产生伪随机，使基岩层呈现自然的不规则分布。
     *
     * @param x            世界坐标 X
     * @param z            世界坐标 Z
     * @param bottomOffset 距世界底部的偏移量
     * @return 该位置的基岩渐变阈值
     */
    private static int bedrockThreshold(int x, int z, int bottomOffset) {
        // 使用方块坐标哈希产生伪随机分布
        long hash = (x * 3129871L) ^ (z * 116129781L);
        hash = hash * 0x9E3779B97F4A7C15L + bottomOffset;
        int noise = (int) ((hash >> 32) & 0xFL) % BEDROCK_DEPTH;
        // 越靠近底部，noise 对阈值的贡献越小（更多基岩）
        return Math.max(1, Math.abs(noise) + (BEDROCK_DEPTH - bottomOffset) / 2);
    }
}
