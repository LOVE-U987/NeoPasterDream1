package com.pasterdream.pasterdreammod.world;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.config.PDCommonConfig;
import com.pasterdream.pasterdreammod.registry.PDBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;

/**
 * 灯影之下风格的地形感染工具。
 * <p>
 * 将地面、水体与低矮植被按类型转化为灯影之下系列方块，并在转化前通过
 * {@link PortalInfectionData} 记录原始状态，供 BOSS 击败后的地形回滚使用。
 * <p>
 * <b>感染门控（全部在此统一把关）</b>：
 * <ol>
 *   <li>配置 {@code arena ruin infection enabled} 开启（默认关闭）；</li>
 *   <li>仅主世界维度；</li>
 *   <li>竞技场 BOSS 未被击败（击败后感染永久退化）；</li>
 *   <li>目标位置的生物群系属于竞技场群系标签 {@code pasterdream:is_aaroncos_arena}
 *       —— 感染严格限制在竞技场群系范围内，不会越界蔓延
 *       （标签同时包含新旧群系键，兼容旧存档已刷写的群系）。</li>
 * </ol>
 * 该逻辑同时被传送门方块（小范围）与竞技场遗迹（群系级大范围）复用，
 * 保证两处感染的表现、门控与回滚记录完全一致。
 */
public final class ArenaInfectionUtils {

    /** 竞技场群系标签（同时包含 aaroncos_arena 与旧键 aaroncos_arena_biome，兼容旧存档） */
    private static final TagKey<Biome> AARONCOS_ARENA_BIOME_TAG =
            TagKey.create(Registries.BIOME,
                    ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "is_aaroncos_arena"));

    /** 单次候选位置的最大尝试次数，避免有机形状边缘导致过多空转 */
    private static final int MAX_ATTEMPTS_MULTIPLIER = 6;

    private ArenaInfectionUtils() {
    }

    /**
     * 以指定源位置为中心感染周围方块。
     * <p>
     * 以有机、不规则的范围逐步将地面、水体与植被转化为灯影之下风格；
     * 每次最多转换 {@code maxCandidates} 个方块，并在 {@code sourcePos} 名下记录回滚数据。
     * <p>
     * 感染源（传送门方块/遗迹中心）可能埋于地下，因此不直接围绕源坐标采样，
     * 而是按列取地表高度（{@link Heightmap.Types#WORLD_SURFACE}），
     * 在该列地表附近自上而下探测可感染方块，确保感染始终作用于可见地表。
     * <p>
     * 统一门控：配置关闭 / 非主世界 / BOSS 已击败 / 目标不在竞技场群系内时，
     * 本方法不产生任何转化（详见类注释）。
     *
     * @param level         服务端世界
     * @param sourcePos     感染源位置（传送门方块或遗迹中心）
     * @param radius        感染半径
     * @param maxCandidates 本次最多处理的候选数
     * @param random        随机源
     */
    public static void infectSurroundingBlocks(ServerLevel level, BlockPos sourcePos,
                                               int radius, int maxCandidates, RandomSource random) {
        // 门控：感染功能配置开关（默认关闭）
        if (!PDCommonConfig.ARENA_RUIN_INFECTION_ENABLED.get()) {
            return;
        }
        // 门控：感染仅作用于主世界
        if (!level.dimension().equals(Level.OVERWORLD)) {
            return;
        }
        // 门控：BOSS 已击败 → 感染永久退化，不再产生任何新转化
        if (PDAaroncosArenaSpawnData.get(level).isDefeated()) {
            return;
        }

        int maxAttempts = maxCandidates * MAX_ATTEMPTS_MULTIPLIER;
        int converted = 0;
        int attempts = 0;

        while (converted < maxCandidates && attempts < maxAttempts) {
            attempts++;

            int dx = random.nextInt(radius * 2 + 1) - radius;
            int dz = random.nextInt(radius * 2 + 1) - radius;

            if (!isWithinOrganicShape(dx, dz, radius, random)) {
                continue;
            }

            int x = sourcePos.getX() + dx;
            int z = sourcePos.getZ() + dz;
            if (!level.isLoaded(new BlockPos(x, sourcePos.getY(), z))) {
                continue;
            }

            // 以该列地表高度为基准，自上而下探测可感染方块（植被/水/地面）
            int surfaceY = level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z);
            BlockPos surfacePos = new BlockPos(x, surfaceY, z);
            // 门控：感染限制在竞技场群系范围内——目标列的群系不属于竞技场群系标签则跳过，
            // 有机形状的随机抖动即使越过群系边界也不会真正转化群系外的方块
            if (!level.getBiome(surfacePos).is(AARONCOS_ARENA_BIOME_TAG)) {
                continue;
            }
            for (int y = surfaceY + 2; y >= surfaceY - 1; y--) {
                BlockPos targetPos = new BlockPos(x, y, z);
                if (canInfect(level, targetPos)) {
                    infectBlock(level, targetPos, random, sourcePos);
                    converted++;
                    break;
                }
            }
        }
    }

    /**
     * 判断目标位置是否处于有机的感染形状内（水平方向）。
     * 使用基于坐标的伪噪声叠加随机抖动，使边界呈自然渗出的不规则形态。
     *
     * @param dx     相对 X
     * @param dz     相对 Z
     * @param radius 当前感染半径
     * @param random 随机源
     * @return true 若该位置位于当前有机边界内
     */
    private static boolean isWithinOrganicShape(int dx, int dz, int radius, RandomSource random) {
        double distance = Math.sqrt(dx * dx + dz * dz);
        double noise = Math.sin(dx * 1.7 + dz * 0.5) * 0.6
                + Math.sin((dx + dz) * 0.9) * 0.4;
        double effectiveRadius = radius + noise * 1.5 + random.nextFloat() * 1.2;
        return distance <= effectiveRadius;
    }

    /**
     * 判断方块是否可以被感染。
     * 包含地面、水源与低矮植被；已转化的方块会跳过以避免重复操作。
     *
     * @param level 服务端世界
     * @param pos   目标位置
     * @return true 若该位置可被转化
     */
    private static boolean canInfect(ServerLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();

        if (isAlreadyInfected(block)) {
            return false;
        }

        if (isInfectableWater(state)) {
            return true;
        }

        if (isInfectablePlant(block)) {
            return true;
        }

        return isInfectableGround(block) && level.isEmptyBlock(pos.above());
    }

    /**
     * 判断方块是否已经被感染为灯影之下风格。
     */
    private static boolean isAlreadyInfected(Block block) {
        return block == PDBlocks.SHADOW_BLOCK.get()
                || block == PDBlocks.THICK_SHADOW_BLOCK.get()
                || block == PDBlocks.SHADOW_STONE.get()
                || block == PDBlocks.SHADOW_STONE_BRICK.get()
                || block == PDBlocks.SHADOW_NYLIUM.get()
                || block == PDBlocks.SHADOW_FUNGUS.get()
                || block == PDBlocks.SHADOW_LIQUID.get();
    }

    /**
     * 判断方块类型是否属于可感染的地面方块。
     */
    private static boolean isInfectableGround(Block block) {
        return block == Blocks.GRASS_BLOCK
                || block == Blocks.DIRT
                || block == Blocks.STONE
                || block == Blocks.COBBLESTONE
                || block == Blocks.GRAVEL
                || block == Blocks.SAND;
    }

    /**
     * 判断方块是否为可感染的静止水源。
     */
    private static boolean isInfectableWater(BlockState state) {
        return state.getFluidState().is(net.minecraft.world.level.material.Fluids.WATER)
                && state.getFluidState().isSource();
    }

    /**
     * 判断方块是否为可感染的低矮植被。
     */
    private static boolean isInfectablePlant(Block block) {
        return block == Blocks.SHORT_GRASS
                || block == Blocks.TALL_GRASS
                || block == Blocks.FERN
                || block == Blocks.LARGE_FERN
                || block == Blocks.DEAD_BUSH;
    }

    /**
     * 感染单个方块，根据原类型转化为对应的灯影之下风格方块。
     *
     * @param level     服务端世界
     * @param pos       目标位置
     * @param random    随机源
     * @param sourcePos 产生本次转化的感染源坐标
     */
    private static void infectBlock(ServerLevel level, BlockPos pos, RandomSource random, BlockPos sourcePos) {
        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();

        if (isInfectablePlant(block)) {
            convertPlant(level, pos, random, sourcePos);
        } else if (isInfectableWater(state)) {
            recordAndSetBlock(level, sourcePos, pos, PDBlocks.SHADOW_LIQUID.get().defaultBlockState());
        } else if (isInfectableGround(block)) {
            convertGround(level, pos, block, random, sourcePos);
        }
    }

    /**
     * 将地面方块转化为灯影之下风格。
     * 草方块有几率转为阴影菌丝，以支持植被的灯影化。
     *
     * @param level     服务端世界
     * @param pos       目标位置
     * @param block     原方块
     * @param random    随机源
     * @param sourcePos 产生本次转化的感染源坐标
     */
    private static void convertGround(ServerLevel level, BlockPos pos, Block block,
                                      RandomSource random, BlockPos sourcePos) {
        BlockState replacement;

        if (block == Blocks.GRASS_BLOCK) {
            float roll = random.nextFloat();
            if (roll < 0.15f) {
                replacement = PDBlocks.SHADOW_NYLIUM.get().defaultBlockState();
            } else if (roll < 0.55f) {
                replacement = PDBlocks.SHADOW_BLOCK.get().defaultBlockState();
            } else {
                replacement = PDBlocks.THICK_SHADOW_BLOCK.get().defaultBlockState();
            }
        } else if (block == Blocks.DIRT) {
            replacement = random.nextBoolean()
                    ? PDBlocks.SHADOW_BLOCK.get().defaultBlockState()
                    : PDBlocks.THICK_SHADOW_BLOCK.get().defaultBlockState();
        } else if (block == Blocks.STONE) {
            replacement = PDBlocks.SHADOW_STONE.get().defaultBlockState();
        } else if (block == Blocks.COBBLESTONE) {
            replacement = PDBlocks.SHADOW_STONE_BRICK.get().defaultBlockState();
        } else if (block == Blocks.GRAVEL) {
            replacement = PDBlocks.SHADOW_BLOCK.get().defaultBlockState();
        } else if (block == Blocks.SAND) {
            replacement = PDBlocks.THICK_SHADOW_BLOCK.get().defaultBlockState();
        } else {
            return;
        }

        recordAndSetBlock(level, sourcePos, pos, replacement);
    }

    /**
     * 将低矮植被转化为灯影之下风格的阴影蘑菇。
     * 若下方地面仍为可转化方块，会先把它转成阴影菌丝以支撑蘑菇。
     *
     * @param level     服务端世界
     * @param pos       植被位置
     * @param random    随机源
     * @param sourcePos 产生本次转化的感染源坐标
     */
    private static void convertPlant(ServerLevel level, BlockPos pos, RandomSource random, BlockPos sourcePos) {
        BlockPos belowPos = pos.below();
        BlockState belowState = level.getBlockState(belowPos);
        Block belowBlock = belowState.getBlock();

        if (isInfectableGround(belowBlock)) {
            recordAndSetBlock(level, sourcePos, belowPos, PDBlocks.SHADOW_NYLIUM.get().defaultBlockState());
        } else if (belowBlock == PDBlocks.SHADOW_BLOCK.get() || belowBlock == PDBlocks.THICK_SHADOW_BLOCK.get()) {
            recordAndSetBlock(level, sourcePos, belowPos, PDBlocks.SHADOW_NYLIUM.get().defaultBlockState());
        }

        BlockState newBelow = level.getBlockState(belowPos);
        if (newBelow.is(PDBlocks.SHADOW_NYLIUM.get())) {
            recordAndSetBlock(level, sourcePos, pos, PDBlocks.SHADOW_FUNGUS.get().defaultBlockState());
        } else {
            level.removeBlock(pos, false);
        }
    }

    /**
     * 先记录原始方块状态，再设置新方块。
     * 这是回滚系统的数据源头，确保每个被转化的位置都能被精确恢复。
     *
     * @param level       服务端世界
     * @param sourcePos   产生转化的感染源坐标
     * @param targetPos   被转化的目标坐标
     * @param newState    要设置的新方块状态
     */
    private static void recordAndSetBlock(ServerLevel level, BlockPos sourcePos, BlockPos targetPos, BlockState newState) {
        BlockState originalState = level.getBlockState(targetPos);
        PortalInfectionData.get(level).recordConversion(sourcePos, targetPos, originalState);
        level.setBlock(targetPos, newState, 3);
    }
}
