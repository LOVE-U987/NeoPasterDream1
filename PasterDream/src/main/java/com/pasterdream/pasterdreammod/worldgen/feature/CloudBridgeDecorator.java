package com.pasterdream.pasterdreammod.worldgen.feature;

import com.pasterdream.pasterdreammod.registry.PDBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.Set;

/**
 * 云桥装饰器 —— 在浮空群岛之间生成连接用的云朵桥梁
 * <p>
 * 云桥由云朵方块构成，连接两个浮岛的边缘位置。
 * 桥体呈拱形，宽 7 格、中心 3 层厚，两端有云柱装饰。
 * <p>
 * 这是 {@link FloatingIslandFeature} 的辅助类，
 * 采用静态工具方法模式，无状态、无实例。
 *
 * @author PasterDream Team
 */
public final class CloudBridgeDecorator {

    /** 云桥宽度（半宽）：7 格宽 = 半宽 3 */
    private static final int BRIDGE_HALF_WIDTH = 3;

    /** 桥面云朵装饰概率 */
    private static final float DECORATION_CHANCE = 0.30f;

    /** 桥面装饰间隔步数 */
    private static final int DECORATION_STEP = 3;

    /** 发光水晶点缀概率 */
    private static final float GLOW_CRYSTAL_CHANCE = 0.35f;

    /** 桥下悬挂藤蔓概率 */
    private static final float VINE_CHANCE = 0.15f;

    /** 桥头云柱生成概率 */
    private static final float PILLAR_CHANCE = 0.40f;

    private CloudBridgeDecorator() {}

    /**
     * 在两个位置之间生成云桥
     * <p>
     * 云桥是一条略带弧度、多层结构的通道：
     * <ul>
     *   <li>宽 7 格（半宽 3），中间 3 格为厚云朵，两侧为普通云朵</li>
     *   <li>中心区域三层厚（上中下三层），边缘两层厚</li>
     *   <li>两端有概率生成云柱作为桥头堡</li>
     *   <li>桥面装饰云朵/水晶灯，桥下悬挂藤蔓</li>
     * </ul>
     *
     * @param level     世界生成级别访问
     * @param random    随机数源
     * @param startX    起点 X
     * @param startZ    起点 Z
     * @param endX      终点 X
     * @param endZ      终点 Z
     * @param bridgeY   桥面 Y 高度
     * @param startRadius 起点岛屿半径（用于计算桥头位置偏移）
     * @param placedSet 已放置方块集合（用于避免重复放置）
     */
    public static void buildBridge(WorldGenLevel level, RandomSource random,
                                   int startX, int startZ, int endX, int endZ,
                                   int bridgeY, int startRadius,
                                   @Nullable Set<BlockPos> placedSet) {
        // 计算两个岛屿之间的向量和距离
        int dx = endX - startX;
        int dz = endZ - startZ;
        int dist = (int) Math.round(Math.sqrt(dx * dx + dz * dz));

        if (dist < 3) {
            return; // 距离太近不需要桥
        }

        // 计算桥头偏移：从岛屿边缘开始
        float startOffset = startRadius * 0.8f;
        int bridgeStartX = startX + (int) (dx * startOffset / dist);
        int bridgeStartZ = startZ + (int) (dz * startOffset / dist);

        // 沿直线从起点到终点铺设桥面
        BlockPos.MutableBlockPos bridgePos = new BlockPos.MutableBlockPos();
        BlockPos.MutableBlockPos tempPos = new BlockPos.MutableBlockPos();

        // 桥头云柱（起点和终点各一个）
        if (random.nextFloat() < PILLAR_CHANCE) {
            placePillar(level, random, bridgeStartX, bridgeStartZ, bridgeY, placedSet);
        }
        if (random.nextFloat() < PILLAR_CHANCE) {
            placePillar(level, random, endX, endZ, bridgeY, placedSet);
        }

        for (int step = 0; step <= dist; step++) {
            float t = (float) step / dist;

            // 计算当前桥段的位置
            int bx = bridgeStartX + (int) ((endX - bridgeStartX) * t);
            int bz = bridgeStartZ + (int) ((endZ - bridgeStartZ) * t);

            // 拱形弧度：中间稍高，两端稍低，弧度增大到 3.0 更明显
            float archHeight = (float) Math.sin(t * Math.PI) * 3.0f;
            int yOffset = Math.round(archHeight);

            // === 桥面铺设：7 格宽，中心三层厚 ===
            for (int w = -BRIDGE_HALF_WIDTH; w <= BRIDGE_HALF_WIDTH; w++) {
                // 垂直于桥方向的偏移
                int perpX = (int) Math.round(-dz * w / (double) dist);
                int perpZ = (int) Math.round(dx * w / (double) dist);

                // 第一层（主桥面）
                bridgePos.set(bx + perpX, bridgeY + yOffset, bz + perpZ);
                if (!canPlaceAt(level, bridgePos, placedSet)) {
                    continue;
                }

                // 中间 3 格用厚云朵，两侧各 2 格用普通云朵
                BlockState mainState = (Math.abs(w) <= 1)
                        ? PDBlocks.THICK_CLOUD.get().defaultBlockState()
                        : PDBlocks.CLOUD.get().defaultBlockState();
                level.setBlock(bridgePos, mainState, 3);
                if (placedSet != null) {
                    placedSet.add(bridgePos.immutable());
                }

                // 第二层（中间 5 格的下层支撑）：形成较宽的双层桥体
                if (Math.abs(w) <= 2) {
                    bridgePos.set(bx + perpX, bridgeY + yOffset - 1, bz + perpZ);
                    if (canPlaceAt(level, bridgePos, placedSet)) {
                        level.setBlock(bridgePos, PDBlocks.CLOUD.get().defaultBlockState(), 3);
                        if (placedSet != null) {
                            placedSet.add(bridgePos.immutable());
                        }
                    }
                }

                // 第三层（中间 3 格的底层）：让桥体看起来更厚实
                if (Math.abs(w) <= 1) {
                    bridgePos.set(bx + perpX, bridgeY + yOffset - 2, bz + perpZ);
                    if (canPlaceAt(level, bridgePos, placedSet)) {
                        level.setBlock(bridgePos, PDBlocks.CLOUD.get().defaultBlockState(), 3);
                        if (placedSet != null) {
                            placedSet.add(bridgePos.immutable());
                        }
                    }
                }
            }

            // === 桥面装饰 ===
            if (step % DECORATION_STEP == 0 && random.nextFloat() < DECORATION_CHANCE) {
                // 在桥两侧边缘放装饰
                int sideW = random.nextBoolean() ? BRIDGE_HALF_WIDTH : -BRIDGE_HALF_WIDTH;
                int perpX = (int) Math.round(-dz * sideW / (double) dist);
                int perpZ = (int) Math.round(dx * sideW / (double) dist);
                tempPos.set(bx + perpX, bridgeY + yOffset + 1, bz + perpZ);
                if (canPlaceAt(level, tempPos, placedSet)) {
                    BlockState decorState = random.nextFloat() < GLOW_CRYSTAL_CHANCE
                            ? PDBlocks.MELTDREAM_CRYSTAL_LAMP.get().defaultBlockState()
                            : PDBlocks.CLOUD.get().defaultBlockState();
                    level.setBlock(tempPos, decorState, 3);
                    if (placedSet != null) {
                        placedSet.add(tempPos.immutable());
                    }
                }

                // 桥头/桥尾较宽的边缘加第二层装饰
                if ((step == 0 || step == dist) && random.nextFloat() < 0.5f) {
                    int outerW = sideW > 0 ? BRIDGE_HALF_WIDTH + 1 : -(BRIDGE_HALF_WIDTH + 1);
                    perpX = (int) Math.round(-dz * outerW / (double) dist);
                    perpZ = (int) Math.round(dx * outerW / (double) dist);
                    tempPos.set(bx + perpX, bridgeY + yOffset, bz + perpZ);
                    if (canPlaceAt(level, tempPos, placedSet)) {
                        level.setBlock(tempPos, PDBlocks.CLOUD.get().defaultBlockState(), 3);
                        if (placedSet != null) {
                            placedSet.add(tempPos.immutable());
                        }
                    }
                }
            }

            // === 桥下悬挂藤蔓 ===
            if (random.nextFloat() < VINE_CHANCE && step > dist / 4 && step < dist * 3 / 4) {
                tempPos.set(bx, bridgeY + yOffset - 1, bz);
                if (canPlaceAt(level, tempPos, placedSet)) {
                    level.setBlock(tempPos, PDBlocks.VINE_0.get().defaultBlockState(), 3);
                    if (placedSet != null) {
                        placedSet.add(tempPos.immutable());
                    }
                }
            }
        }
    }

    /**
     * 在桥头生成云柱装饰（桥头堡）
     * <p>
     * 云柱从桥面向上延伸 2~4 格，顶部点缀发光水晶，
     * 使桥头看起来更醒目、更美观。
     *
     * @param level     世界生成级别访问
     * @param random    随机数源
     * @param x         桥头 X 坐标
     * @param z         桥头 Z 坐标
     * @param bridgeY   桥面 Y 高度
     * @param placedSet 已放置方块集合
     */
    private static void placePillar(WorldGenLevel level, RandomSource random,
                                     int x, int z, int bridgeY,
                                     @Nullable Set<BlockPos> placedSet) {
        BlockPos.MutableBlockPos pillarPos = new BlockPos.MutableBlockPos();
        int pillarHeight = random.nextIntBetweenInclusive(2, 4);

        for (int dy = 1; dy <= pillarHeight; dy++) {
            pillarPos.set(x, bridgeY + dy, z);
            if (!canPlaceAt(level, pillarPos, placedSet)) {
                break;
            }
            level.setBlock(pillarPos, PDBlocks.CLOUD.get().defaultBlockState(), 3);
            if (placedSet != null) {
                placedSet.add(pillarPos.immutable());
            }

            // 顶部放水晶灯
            if (dy == pillarHeight && random.nextFloat() < 0.6f) {
                pillarPos.set(x, bridgeY + dy + 1, z);
                if (canPlaceAt(level, pillarPos, placedSet)) {
                    level.setBlock(pillarPos, PDBlocks.MELTDREAM_CRYSTAL_LAMP.get().defaultBlockState(), 3);
                    if (placedSet != null) {
                        placedSet.add(pillarPos.immutable());
                    }
                }
            }
        }
    }

    /**
     * 检查指定位置是否可以放置桥方块
     *
     * @param level     世界生成级别访问
     * @param pos       要检查的位置
     * @param placedSet 已放置方块集合
     * @return true 表示可以放置
     */
    private static boolean canPlaceAt(WorldGenLevel level, BlockPos pos,
                                       @Nullable Set<BlockPos> placedSet) {
        if (placedSet != null && placedSet.contains(pos)) {
            return false;
        }
        return level.getBlockState(pos).isAir();
    }
}
