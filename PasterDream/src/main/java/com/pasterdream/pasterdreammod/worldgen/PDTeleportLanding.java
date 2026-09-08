package com.pasterdream.pasterdreammod.worldgen;

import com.pasterdream.pasterdreammod.block.DyedreamCrackBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.Heightmap;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 维度传送落点安全计算工具。
 * <p>
 * 为裂隙（{@code DyedreamCrackBlock}）和传送水晶（{@code DyedreamTeleportCrystal}）
 * 提供统一的安全着陆位置查找，避免旧代码“自顶向下第一个非空气方块”导致的
 * 落在云顶/树冠/水面上方的问题。
 */
public final class PDTeleportLanding {

    /** 玩家碰撞箱尺寸（宽 x 高） */
    private static final EntityDimensions PLAYER_DIMS = EntityDimensions.scalable(0.6F, 1.8F);

    /** 最大螺旋搜索半径（区块） */
    private static final int MAX_SPIRAL_RADIUS = 24;

    private PDTeleportLanding() {
    }

    // =====================================================================
    // 主世界 → 染梦：出生在 (0,0) 原点裂隙结构旁
    // =====================================================================

    /**
     * 查找染梦维度 (0,0) 原点裂隙的安全出生位置。
     * <p>
     * 流程：
     * <ol>
     *   <li>确保 (0,0) 区块已生成（传送目的地允许同步加载）</li>
     *   <li>在 Origin 周围扫描裂隙方块，找到后在其面前 3 格处降落</li>
     *   <li>若未找到裂隙，执行通用安全地面螺旋搜索</li>
     * </ol>
     *
     * @param dyedream 染梦维度 ServerLevel
     * @return 安全出生位置（含朝向信息）
     */
    public static BlockPos findDyedreamOriginArrival(ServerLevel dyedream) {
        // 1. 确保 (0,0) 区块已生成
        dyedream.getChunk(0, 0);

        BlockPos origin = BlockPos.ZERO;

        // 2. 尝试在 Origin 附近找裂隙方块
        DyedreamCrackBlock crackBlock = null;
        BlockPos crackPos = null;
        float crackYaw = 0F;

        List<BlockPos> nearby = scanRadius(origin, 16);
        int surfaceY = dyedream.getHeight(Heightmap.Types.WORLD_SURFACE_WG, 0, 0);

        for (BlockPos pos : nearby) {
            if (pos.getY() < surfaceY - 8 || pos.getY() > surfaceY + 40) {
                continue;
            }
            BlockState state = dyedream.getBlockState(pos);
            if (state.getBlock() instanceof DyedreamCrackBlock block) {
                crackBlock = block;
                crackPos = pos;
                crackYaw = yawForFacing(state);
                break;
            }
        }

        // 3. 找到裂隙 → 在其面前 3 格降落
        if (crackBlock != null && crackPos != null) {
            Direction front = crackFront(crackBlock, crackPos, dyedream);
            if (front != null) {
                for (int dist = 2; dist <= 4; dist++) {
                    BlockPos candidate = crackPos.relative(front, dist);
                    if (isSafeLanding(dyedream, candidate)) {
                        return candidate;
                    }
                }
            }
            // 裂隙面前不安全 → 尝试裂隙两侧
            if (crackPos != null) {
                for (Direction side : new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST}) {
                    BlockPos sideCandidate = crackPos.relative(side, 2);
                    if (isSafeLanding(dyedream, sideCandidate)) {
                        return sideCandidate;
                    }
                }
            }
        }

        // 4. 未找到裂隙 / 裂隙附近不安全 → 螺旋搜索通用安全地面
        BlockPos spiralResult = spiralSafeGround(dyedream, origin);
        if (spiralResult != null) {
            return spiralResult;
        }

        // 5. 兜底：Origin 表面（最差情况）
        return BlockPos.containing(0.5, surfaceY + 1, 0.5);
    }

    // =====================================================================
    // 染梦 → 主世界：重生点/世界出生点安全降落
    // =====================================================================

    /**
     * 查找目标维度（通常是主世界）的重生点/世界出生点安全降落位置。
     * <p>
     * 旧代码缺陷：自顶向下找“第一个非空气方块”，命中洞穴天花板或云顶；
     * 兜底 {@code above(3)} 无安全检测。修复为 Heightmap + 安全校验 + 螺旋搜索。
     *
     * @param target 目标维度 ServerLevel
     * @param player 传送的玩家
     * @return 安全降落位置
     */
    public static BlockPos findSafeRespawnLanding(ServerLevel target, ServerPlayer player) {
        BlockPos basePos = findRespawnBase(target, player);
        int baseX = basePos.getX();
        int baseZ = basePos.getZ();

        // 确保目标区块已生成
        target.getChunk(baseX >> 4, baseZ >> 4);

        int surfaceY = target.getHeight(Heightmap.Types.WORLD_SURFACE_WG, baseX, baseZ);
        BlockPos heightmapPos = new BlockPos(baseX, surfaceY, baseZ);

        if (isSafeLanding(target, heightmapPos)) {
            return heightmapPos;
        }

        // Heightmap 落点不安全 → 螺旋搜索
        BlockPos spiralResult = spiralSafeGround(target, basePos);
        if (spiralResult != null) {
            return spiralResult;
        }

        // 兜底：basePos 上方（保持原行为兼容性）
        return basePos.above(3);
    }

    // =====================================================================
    // 内部工具方法
    // =====================================================================

    /**
     * 确定重生点基础坐标（重生床 / 世界出生点）。
     */
    private static BlockPos findRespawnBase(ServerLevel target, ServerPlayer player) {
        if (player.getRespawnPosition() != null
                && player.getRespawnDimension().equals(target.dimension())) {
            return player.getRespawnPosition();
        }
        return target.getSharedSpawnPos();
    }

    /**
     * 安全着陆校验：下方实体方块 + 玩家碰撞箱范围内 2 格无流体/非空气。
     */
    private static boolean isSafeLanding(ServerLevel level, BlockPos pos) {
        // 下方必须是实体方块
        if (!level.getBlockState(pos.below()).canOcclude()) {
            return false;
        }

        // 玩家碰撞箱范围内必须为空（空气或无碰撞方块）
        // 玩家占据 pos 本身 + pos.above(1)
        for (int dy = 0; dy <= 1; dy++) {
            BlockState checkState = level.getBlockState(pos.offset(0, dy, 0));
            if (!checkState.isAir() && checkState.getCollisionShape(level, pos).isEmpty()) {
                continue; // noCollission 方块（如玻璃）可通过
            }
            if (!checkState.isAir()) {
                return false;
            }
        }

        // 非流体
        if (!level.getFluidState(pos).isEmpty() || !level.getFluidState(pos.above()).isEmpty()) {
            return false;
        }

        return true;
    }

    /**
     * 螺旋搜索安全地面：从中心向外逐圈搜索。
     */
    @Nullable
    private static BlockPos spiralSafeGround(ServerLevel level, BlockPos center) {
        int centerX = center.getX();
        int centerZ = center.getZ();

        for (int radius = 1; radius <= MAX_SPIRAL_RADIUS; radius++) {
            List<BlockPos> ring = new ArrayList<>();

            // 上下边
            for (int dx = -radius; dx <= radius; dx++) {
                ring.add(new BlockPos(centerX + dx, 0, centerZ - radius));
                ring.add(new BlockPos(centerX + dx, 0, centerZ + radius));
            }
            // 左右边（不含角点，避免重复）
            for (int dz = -radius + 1; dz <= radius - 1; dz++) {
                ring.add(new BlockPos(centerX - radius, 0, centerZ + dz));
                ring.add(new BlockPos(centerX + radius, 0, centerZ + dz));
            }

            // 按距中心距离排序（优先近处）
            ring.sort(Comparator.comparingDouble(p -> p.distSqr(center)));

            for (BlockPos candidate : ring) {
                int surfaceY = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, candidate.getX(), candidate.getZ());
                BlockPos landing = new BlockPos(candidate.getX(), surfaceY, candidate.getZ());
                if (isSafeLanding(level, landing)) {
                    return landing;
                }
            }
        }

        return null;
    }

    /**
     * 以指定中心向外螺旋扫描所有 XZ 位置（返回最近的优先）。
     */
    private static List<BlockPos> scanRadius(BlockPos center, int radius) {
        List<BlockPos> result = new ArrayList<>();
        int cx = center.getX();
        int cz = center.getZ();

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                result.add(new BlockPos(cx + dx, 0, cz + dz));
            }
        }

        result.sort(Comparator.comparingDouble(p -> p.distSqr(center)));
        return result;
    }

    /**
     * 获取裂隙方块的面前方向。
     */
    private static Direction crackFront(Block crackBlock, BlockPos pos, ServerLevel level) {
        BlockState state = level.getBlockState(pos);
        if (crackBlock instanceof DyedreamCrackBlock) {
            return state.getValue(HorizontalDirectionalBlock.FACING);
        }
        return null;
    }

    /**
     * 根据方块朝向计算玩家 yaw（面朝裂隙方向）。
     */
    private static float yawForFacing(BlockState state) {
        if (state.hasProperty(HorizontalDirectionalBlock.FACING)) {
            return switch (state.getValue(HorizontalDirectionalBlock.FACING)) {
                case NORTH -> 180F;
                case SOUTH -> 0F;
                case EAST -> 270F;
                case WEST -> 90F;
                default -> 0F;
            };
        }
        return 0F;
    }
}
