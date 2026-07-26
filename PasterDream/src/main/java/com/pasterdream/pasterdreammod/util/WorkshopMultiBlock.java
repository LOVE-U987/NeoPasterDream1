package com.pasterdream.pasterdreammod.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.List;
import java.util.Map;

/**
 * 武器工坊多方块结构工具 (Workshop Multi-Block)
 * <p>
 * 逐层还原原版 {@code net.pasterdream.MultiBlock}：
 * 以 5×5 展平列表描述每层结构（索引 0-24，null 表示任意方块），
 * 按玩家朝向旋转匹配；校验通过后整体清除并铺设工坊方块。
 * <ul>
 *   <li>{@link #checkMultiBlock} —— 校验 maxY 层结构；全部匹配后逐格
 *       {@code destroyBlock}（不掉落）清场并把基准点置为 target 方块。</li>
 *   <li>{@link #setMultiBlock} —— 按 map 第 0 层铺设非空方块
 *       （使用玩家朝向的放置状态，工坊方块因此获得正确 FACING）。</li>
 * </ul>
 */
public final class WorkshopMultiBlock {

    private WorkshopMultiBlock() {
    }

    /**
     * 校验多方块结构；全部匹配则清场（destroyBlock 不掉落）并把基准点置为 target
     *
     * @param maps    每层的 5×5 展平方块表（null=任意）
     * @param player  触发玩家（用于放置上下文）
     * @param world   世界
     * @param basePos 基准位置（核心方块下方一格）
     * @param ox      前向偏移
     * @param oz      侧向偏移
     * @param face    玩家朝向
     * @param target  校验成功后基准点放置的方块（原版传 AIR）
     * @param maxY    校验层数
     * @return 结构是否完整
     */
    public static boolean checkMultiBlock(Map<Integer, List<Block>> maps, Player player, Level world,
                                          BlockPos basePos, int ox, int oz, Direction face, Block target, int maxY) {
        if (maps.size() < maxY) {
            return false;
        }
        BlockPos originPos = originFor(basePos, ox, oz, face);
        // 第一阶段：全量校验
        for (int y = 0; y < maxY; y++) {
            int i = 0;
            for (int[] off : cellOrder(face)) {
                // cellOrder 已按朝向给出 25 个 (dx, dz) 偏移
                BlockPos pos = new BlockPos(originPos.getX() + off[0], originPos.getY() + y, originPos.getZ() + off[1]);
                Block expect = maps.get(y).get(i);
                if (expect != null && expect != Blocks.AIR && !world.getBlockState(pos).is(expect)) {
                    return false;
                }
                i++;
            }
        }
        // 第二阶段：清场（与原版一致：destroyBlock 不掉落），基准点置为 target
        for (int y = 0; y < maxY; y++) {
            for (int[] off : cellOrder(face)) {
                BlockPos pos = new BlockPos(originPos.getX() + off[0], originPos.getY() + y, originPos.getZ() + off[1]);
                world.destroyBlock(pos, false);
            }
        }
        BlockState targetState = target.getStateForPlacement(new BlockPlaceContext(player, InteractionHand.MAIN_HAND,
                player.getMainHandItem(), BlockHitResult.miss(player.position(), face, basePos)));
        if (targetState != null) {
            world.setBlockAndUpdate(basePos, targetState);
        }
        return true;
    }

    /**
     * 按 map 铺设多方块结构（每层 5×5，null/AIR 跳过）
     *
     * @param map    每层的 5×5 展平方块表
     * @param player 触发玩家（提供放置朝向上下文）
     * @param world  世界
     * @param pos    基准位置
     * @param ox     前向偏移
     * @param oz     侧向偏移
     * @param face   玩家朝向
     * @param maxY   铺设层数
     */
    public static void setMultiBlock(Map<Integer, List<Block>> map, Player player, Level world,
                                     BlockPos pos, int ox, int oz, Direction face, int maxY) {
        BlockPos originPos = originFor(pos, ox, oz, face);
        for (int y = 0; y < maxY; y++) {
            int i = 0;
            for (int[] off : cellOrder(face)) {
                Block block = map.get(y).get(i);
                i++;
                if (block == null || block == Blocks.AIR) {
                    continue;
                }
                BlockPos cell = new BlockPos(originPos.getX() + off[0], originPos.getY() + y, originPos.getZ() + off[1]);
                BlockState state = block.getStateForPlacement(new BlockPlaceContext(player, InteractionHand.MAIN_HAND,
                        player.getMainHandItem(), BlockHitResult.miss(player.position(), face, cell)));
                if (state != null) {
                    world.setBlockAndUpdate(cell, state);
                }
            }
        }
    }

    /**
     * 计算各朝向的原点（与原版逐分支一致）
     *
     * @param basePos 基准位置
     * @param ox      前向偏移
     * @param oz      侧向偏移
     * @param face    朝向
     * @return 结构原点
     */
    private static BlockPos originFor(BlockPos basePos, int ox, int oz, Direction face) {
        return switch (face) {
            case EAST -> new BlockPos(basePos.getX() + ox, basePos.getY(), basePos.getZ() - oz);
            case SOUTH -> new BlockPos(basePos.getX() + oz, basePos.getY(), basePos.getZ() + ox);
            case WEST -> new BlockPos(basePos.getX() - ox, basePos.getY(), basePos.getZ() + oz);
            default -> new BlockPos(basePos.getX() - oz, basePos.getY(), basePos.getZ() - ox); // NORTH
        };
    }

    /**
     * 生成各朝向下 25 个格子的 (dx, dz) 遍历序（与原版循环顺序逐一对应）
     *
     * @param face 朝向
     * @return 25 个偏移对
     */
    private static int[][] cellOrder(Direction face) {
        int[][] order = new int[25][2];
        int i = 0;
        switch (face) {
            case EAST -> { // for x 0..-4, for z 0..4
                for (int x = 0; x > -5; x--)
                    for (int z = 0; z < 5; z++)
                        order[i++] = new int[]{x, z};
            }
            case SOUTH -> { // for z 0..-4, for x 0..-4
                for (int z = 0; z > -5; z--)
                    for (int x = 0; x > -5; x--)
                        order[i++] = new int[]{x, z};
            }
            case WEST -> { // for x 0..4, for z 0..-4
                for (int x = 0; x < 5; x++)
                    for (int z = 0; z > -5; z--)
                        order[i++] = new int[]{x, z};
            }
            default -> { // NORTH: for z 0..4, for x 0..4
                for (int z = 0; z < 5; z++)
                    for (int x = 0; x < 5; x++)
                        order[i++] = new int[]{x, z};
            }
        }
        return order;
    }
}
