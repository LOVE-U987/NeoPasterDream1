package com.pasterdream.pasterdreammod.world;

import com.pasterdream.pasterdreammod.api.util.ServerScheduler;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

/**
 * 亚伦柯斯传送门区域地形回滚调度器。
 * <p>
 * BOSS 击败后调用，以可控速率将 {@link PortalInfectionData} 中记录的
 * 灯影之下方块逐块恢复为原始状态，营造“能量退去、世界复原”的演出效果。
 */
public final class PortalRestorationHandler {

    private PortalRestorationHandler() {
    }

    /** 每 tick 恢复的方块数，数值过大可能导致瞬间卡顿 */
    private static final int RESTORE_BLOCKS_PER_TICK = 24;

    /**
     * 启动所有传送门区域的回滚。
     * <p>
     * 将指定传送门的所有记录合并为一个待恢复队列，按每 tick 固定数量逐步还原。
     * 所有记录恢复完毕后自动从 {@link PortalInfectionData} 中清理。
     * 回滚开始时同步停止遗迹持续感染，避免与回滚互相拉锯。
     *
     * @param level          主世界服务端世界
     * @param portalPositions 需要回滚的传送门位置列表
     */
    public static void startRestoration(ServerLevel level, List<BlockPos> portalPositions) {
        if (portalPositions.isEmpty()) {
            return;
        }

        // 停止遗迹持续感染，防止回滚过程中地形再次被灯影化
        ArenaRuinInfection.stop();

        PortalInfectionData data = PortalInfectionData.get(level);
        List<PendingRestore> pending = new ArrayList<>();

        for (BlockPos portalPos : portalPositions) {
            for (PortalInfectionData.ConversionRecord record : data.getRecords(portalPos)) {
                pending.add(new PendingRestore(portalPos, record.pos(), record.originalState()));
            }
        }

        if (pending.isEmpty()) {
            return;
        }

        scheduleNextBatch(level, data, pending, 0);
    }

    /**
     * 递归调度下一批方块恢复。
     *
     * @param level    主世界服务端世界
     * @param data     感染数据存储
     * @param pending  剩余待恢复列表
     * @param index    当前处理到的下标
     */
    private static void scheduleNextBatch(ServerLevel level, PortalInfectionData data,
                                          List<PendingRestore> pending, int index) {
        if (index >= pending.size()) {
            return;
        }

        ServerScheduler.schedule(1, () -> {
            int end = Math.min(index + RESTORE_BLOCKS_PER_TICK, pending.size());
            for (int i = index; i < end; i++) {
                PendingRestore restore = pending.get(i);
                restoreBlock(level, data, restore);
            }
            scheduleNextBatch(level, data, pending, end);
        });
    }

    /**
     * 恢复单个方块并清理对应记录。
     *
     * @param level    主世界服务端世界
     * @param data     感染数据存储
     * @param restore  待恢复条目
     */
    private static void restoreBlock(ServerLevel level, PortalInfectionData data, PendingRestore restore) {
        BlockState current = level.getBlockState(restore.pos);
        if (!isInfectedBlock(current)) {
            data.removeRecord(restore.portalPos, restore.pos);
            return;
        }

        level.setBlock(restore.pos, restore.originalState, 3);
        data.removeRecord(restore.portalPos, restore.pos);
    }

    /**
     * 判断当前方块是否仍属于灯影之下风格（需要被恢复）。
     * 若玩家已手动替换为其他方块，则跳过恢复以尊重玩家改动。
     */
    private static boolean isInfectedBlock(BlockState state) {
        // 这里不依赖 PDBlocks，避免循环引用；直接通过注册名判断。
        String name = state.getBlockHolder().getRegisteredName();
        return name.startsWith("pasterdream:shadow_") || name.startsWith("pasterdream:thick_shadow_");
    }

    /**
     * 待恢复条目：记录该由哪个传送门负责、恢复到什么位置、恢复成什么状态。
     */
    private record PendingRestore(BlockPos portalPos, BlockPos pos, BlockState originalState) {
    }
}
