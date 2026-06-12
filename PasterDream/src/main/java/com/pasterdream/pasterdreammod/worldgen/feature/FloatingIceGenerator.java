package com.pasterdream.pasterdreammod.worldgen.feature;

import com.pasterdream.pasterdreammod.worldgen.WorldGenUtils;
import com.pasterdream.pasterdreammod.worldgen.decor.DecorationConfig;
import com.pasterdream.pasterdreammod.worldgen.decor.ICustomDecorationGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import com.pasterdream.pasterdreammod.registry.PDBlocks;
import java.util.HashSet;
import java.util.Set;

/**
 * 海面浮冰生成器 —— 在海面位置生成扁平碟状浮冰结构
 * <p>
 * 在海面高度（seaLevel）生成扁平的冰盘，
 * 上部覆盖雪块，下部浸入水中。
 * 半径 3~6 格，总厚度 2~4 格（海面上下各一半）。
 * 底部略厚形成自然过渡，顶部平坦覆盖白雪。
 */
public class FloatingIceGenerator implements ICustomDecorationGenerator {

    @Override
    public boolean generate(FeaturePlaceContext<DecorationConfig> context) {
        DecorationConfig config = context.config();
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource random = context.random();

        int seaLevel = level.getSeaLevel();
        int radius = 3 + random.nextInt(4);
        int thicknessBelow = 1 + random.nextInt(2);
        int thicknessAbove = 1 + random.nextInt(2);
        boolean placedAny = false;
        Set<BlockPos> placedPositions = new HashSet<>();

        int centerX = origin.getX();
        int centerZ = origin.getZ();

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                float distSq = dx * dx + dz * dz;
                float radiusSq = radius * radius;
                if (distSq > radiusSq * 0.85f + random.nextFloat() * radiusSq * 0.15f) continue;

                int edgeDist = (int) Math.sqrt(distSq);
                int localAbove = (edgeDist > radius * 0.6f) ? 0 : thicknessAbove;
                int localBelow = (edgeDist > radius * 0.6f)
                        ? Math.max(0, thicknessBelow - 1)
                        : thicknessBelow;

                for (int dy = -localBelow; dy <= localAbove; dy++) {
                    BlockPos pos = new BlockPos(centerX + dx, seaLevel + dy, centerZ + dz);
                    if (pos.getY() < level.getMinBuildHeight()) continue;
                    if (!WorldGenUtils.isWithinGenerationBounds(origin, pos)) continue;
                    if (!WorldGenUtils.isReplaceable(level, config.replaceable(), pos)) continue;

                    if (dy > -localBelow) {
                        if (!WorldGenUtils.hasSupportBelow(level, config.replaceable(), pos, placedPositions)) {
                            continue;
                        }
                    }

                    boolean isTopLayer = dy == localAbove;
                    BlockState state = (isTopLayer && config.topBlock() != null)
                            ? config.topBlock().getState(random, pos)
                            : config.bodyBlock().getState(random, pos);
                    level.setBlock(pos, state, 3);
                    placedPositions.add(pos);
                    placedAny = true;
                }
            }
        }

        // 在冰盘顶部随机点缀冰蕾结晶 ✨
        int crystalCount = 1 + random.nextInt(3);
        for (int c = 0; c < crystalCount; c++) {
            int cx = centerX + random.nextInt(radius * 2 + 1) - radius;
            int cz = centerZ + random.nextInt(radius * 2 + 1) - radius;
            int cDistX = cx - centerX;
            int cDistZ = cz - centerZ;
            float cDistSq = cDistX * cDistX + cDistZ * cDistZ;
            if (cDistSq > radius * radius * 0.5f) continue;
            BlockPos cPos = new BlockPos(cx, seaLevel + thicknessAbove + 1, cz);
            if (!WorldGenUtils.isReplaceable(level, config.replaceable(), cPos)) continue;
            if (!WorldGenUtils.isSolidSurface(level, cPos.below())) continue;
            if (!WorldGenUtils.isWithinGenerationBounds(origin, cPos)) continue;
            level.setBlock(cPos, PDBlocks.ICE_BUD_0.get().defaultBlockState(), 3);
        }

        addGlowDecorations(level, random, origin, centerX, centerZ, seaLevel, radius);

        return placedAny;
    }

    /**
     * 在浮冰周围水面随机点缀冰蕾结晶，增加发光装饰效果
     *
     * @param level   世界生成级别
     * @param random  随机数源
     * @param origin  生成原点
     * @param centerX 浮冰中心X坐标
     * @param centerZ 浮冰中心Z坐标
     * @param seaLevel 海平面高度
     * @param radius  浮冰半径
     */
    private void addGlowDecorations(WorldGenLevel level, RandomSource random, BlockPos origin, int centerX, int centerZ, int seaLevel, int radius) {
        int glowCount = 1 + random.nextInt(2);
        for (int g = 0; g < glowCount; g++) {
            int gx = centerX + random.nextInt(radius * 2 + 2) - radius;
            int gz = centerZ + random.nextInt(radius * 2 + 2) - radius;
            int gy = level.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, gx, gz);
            if (Math.abs(gy - seaLevel) > 4) continue;
            BlockPos gPos = new BlockPos(gx, gy + 1, gz);
            BlockState existing = level.getBlockState(gPos);
            if (!existing.isAir() && !existing.liquid()) continue;
            if (!WorldGenUtils.isWithinGenerationBounds(origin, gPos)) continue;
            level.setBlock(gPos, PDBlocks.ICE_BUD_0.get().defaultBlockState(), 3);
        }
    }
}