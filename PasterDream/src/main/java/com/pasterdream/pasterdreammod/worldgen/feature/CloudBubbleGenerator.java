package com.pasterdream.pasterdreammod.worldgen.feature;

import com.pasterdream.pasterdreammod.registry.PDBlocks;
import com.pasterdream.pasterdreammod.worldgen.WorldGenUtils;
import com.pasterdream.pasterdreammod.worldgen.decor.DecorationConfig;
import com.pasterdream.pasterdreammod.worldgen.decor.ICustomDecorationGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

/**
 * 云泡泡生成器 —— 海面上空的大型中空球形云结构
 * <p>
 * 在海面上空生成中空球形云泡，半径 5~10 格。
 * 壳层随机稀疏化（约 40% 空缺），形成自然蓬松的云朵外观。
 */
public class CloudBubbleGenerator implements ICustomDecorationGenerator {

    @Override
    public boolean generate(FeaturePlaceContext<DecorationConfig> context) {
        DecorationConfig config = context.config();
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource random = context.random();

        int surfaceY = level.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, origin.getX(), origin.getZ());
        int seaLevel = level.getSeaLevel();
        int waterSurfaceY = Math.max(surfaceY, seaLevel);

        int centerY = waterSurfaceY + 25 + random.nextInt(20);
        int radius = 5 + random.nextInt(6);
        int innerRadius = Math.max(2, radius - 2);
        boolean placedAny = false;

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    float distSq = dx * dx + dy * dy + dz * dz;
                    float radiusSq = radius * radius;
                    float innerRadiusSq = innerRadius * innerRadius;

                    if (distSq > radiusSq) continue;
                    if (distSq < innerRadiusSq) continue;

                    if (random.nextFloat() < 0.4f) continue;

                    BlockPos pos = new BlockPos(origin.getX() + dx, centerY + dy, origin.getZ() + dz);
                    if (!WorldGenUtils.isReplaceable(level, config.replaceable(), pos)) continue;

                    level.setBlock(pos, config.bodyBlock().getState(random, pos), 3);
                    placedAny = true;
                }
            }
        }

        // 生成云丝拖尾 —— 在云泡下方飘散几缕细长的云丝，增加自然感
        int trailCount = 3 + random.nextInt(4);
        for (int t = 0; t < trailCount; t++) {
            int tx = origin.getX() + random.nextInt(radius * 2 + 1) - radius;
            int tz = origin.getZ() + random.nextInt(radius * 2 + 1) - radius;
            int tDistX = tx - origin.getX();
            int tDistZ = tz - origin.getZ();
            float tDistSq = tDistX * tDistX + tDistZ * tDistZ;
            if (tDistSq > radius * radius * 0.6f) continue;
            int trailLen = 2 + random.nextInt(3);
            for (int i = 1; i <= trailLen; i++) {
                int ty = centerY - radius - i;
                if (random.nextFloat() < 0.3f) continue;
                BlockPos tPos = new BlockPos(tx, ty, tz);
                if (!WorldGenUtils.isReplaceable(level, config.replaceable(), tPos)) continue;
                level.setBlock(tPos, config.bodyBlock().getState(random, tPos), 3);
            }
        }

        addGlowDecorations(level, random, origin, centerY, radius, config);

        return placedAny;
    }

    /**
     * 在云泡下方地面随机点缀冰蕾结晶，增加发光装饰效果
     *
     * @param level  世界生成级别
     * @param random 随机数源
     * @param origin 生成原点
     * @param centerY 云泡中心Y坐标
     * @param radius 云泡半径
     * @param config 装饰配置
     */
    private void addGlowDecorations(WorldGenLevel level, RandomSource random, BlockPos origin, int centerY, int radius, DecorationConfig config) {
        int glowCount = 1 + random.nextInt(3);
        for (int g = 0; g < glowCount; g++) {
            int gx = origin.getX() + random.nextInt(radius * 2 + 2) - radius;
            int gz = origin.getZ() + random.nextInt(radius * 2 + 2) - radius;
            int gy = level.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, gx, gz);
            if (gy >= centerY - 5) continue;
            BlockPos gPos = new BlockPos(gx, gy + 1, gz);
            if (!WorldGenUtils.isReplaceable(level, config.replaceable(), gPos)) continue;
            if (!WorldGenUtils.isWithinGenerationBounds(origin, gPos)) continue;
            level.setBlock(gPos, PDBlocks.ICE_BUD_0.get().defaultBlockState(), 3);
        }
    }
}