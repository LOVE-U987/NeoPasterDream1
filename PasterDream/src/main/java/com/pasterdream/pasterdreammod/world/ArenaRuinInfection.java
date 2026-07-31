package com.pasterdream.pasterdreammod.world;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.api.util.ServerScheduler;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;

/**
 * 竞技场遗迹感染源。
 * <p>
 * 遗迹本身作为感染源：放置后以遗迹中心为原点，持续将 {@link #INFECTION_RADIUS}
 * 范围内（按地表高度）的地面/水体/植被渐进转化为灯影之下风格方块，
 * 直到 BOSS 击败触发回滚为止。
 * <p>
 * 通过 {@link ServerScheduler} 自递归调度实现周期批量感染；
 * BOSS 胜利时调用 {@link #stop()}（由 {@link PortalRestorationHandler} 触发）停止感染，
 * 避免与地形回滚互相拉锯。
 * <p>
 * 遗迹中心记录在 {@link PDAaroncosArenaSpawnData} 中；旧存档缺少中心记录时，
 * 由传送门方块在放置/区块 tick 时调用 {@link #tryRegisterCenter} 补注册。
 */
public final class ArenaRuinInfection {

    /** 遗迹感染半径（方块），与竞技场群系覆盖半径一致 */
    private static final int INFECTION_RADIUS = 48;
    /** 每批最多处理的候选数，保证能在合理时间内覆盖大半径 */
    private static final int CANDIDATES_PER_BATCH = 80;
    /** 批处理调度间隔（tick），缩短以加快群系级覆盖 */
    private static final int BATCH_INTERVAL = 3;
    /** 传送门注册遗迹中心时，与出生点的最大距离平方（避免远处手动放置的传送门顶掉中心） */
    private static final long SPAWN_GUARD_DISTANCE_SQ = 200L * 200L;

    /** 感染是否处于运行状态（服务器重启后由 ServerStoppedEvent 复位） */
    private static boolean active = false;
    /** 当前感染中心（可在运行中更新，例如结构原点优于传送门位置） */
    private static BlockPos currentCenter = null;

    private ArenaRuinInfection() {
    }

    /**
     * 以遗迹中心为原点启动/更新持续感染。
     * <p>
     * 幂等：感染已在运行时仅更新中心；服务器重启后（active 已被复位）会重新启动。
     *
     * @param overworld 主世界服务端世界
     * @param center    遗迹中心坐标
     */
    public static void start(ServerLevel overworld, BlockPos center) {
        if (center == null) {
            return;
        }
        currentCenter = center;
        if (active) {
            return;
        }
        active = true;
        PasterDreamMod.LOGGER.info("[ArenaRuinInfection] 遗迹感染已启动，中心 {}", center.toShortString());
        scheduleBatch(overworld);
    }

    /**
     * 尝试把传送门方块位置注册为遗迹中心（幂等）。
     * <p>
     * 结构放置时（onPlace）与区块加载后（tick 兜底，兼容旧存档）都会调用；
     * 仅在主世界、出生点附近、且尚未记录中心时生效，避免竞技场维度内的
     * 传送门或玩家远处手动放置的传送门误注册。
     *
     * @param overworld 主世界服务端世界
     * @param portalPos 传送门方块位置
     */
    public static void tryRegisterCenter(ServerLevel overworld, BlockPos portalPos) {
        if (!overworld.dimension().equals(Level.OVERWORLD)) {
            return;
        }
        PDAaroncosArenaSpawnData data = PDAaroncosArenaSpawnData.get(overworld);
        if (data.getCenter() != null) {
            return;
        }
        if (portalPos.distSqr(overworld.getSharedSpawnPos()) > SPAWN_GUARD_DISTANCE_SQ) {
            return;
        }
        data.setCenter(portalPos);
        PasterDreamMod.LOGGER.info("[ArenaRuinInfection] 已由传送门注册遗迹中心：{}", portalPos.toShortString());
        start(overworld, portalPos);
    }

    /**
     * 停止持续感染（BOSS 击败回滚开始时调用）。
     */
    public static void stop() {
        active = false;
    }

    /**
     * 服务器停止时复位运行状态，避免跨存档残留。
     *
     * @param event 服务器停止事件
     */
    public static void onServerStopped(ServerStoppedEvent event) {
        active = false;
        currentCenter = null;
    }

    /**
     * 调度下一批感染，并在执行完成后自我续期。
     *
     * @param level 主世界服务端世界
     */
    private static void scheduleBatch(ServerLevel level) {
        ServerScheduler.schedule(BATCH_INTERVAL, () -> {
            if (!active || level.getServer() == null) {
                return;
            }
            BlockPos center = currentCenter;
            if (center == null) {
                return;
            }
            ArenaInfectionUtils.infectSurroundingBlocks(
                    level, center, INFECTION_RADIUS, CANDIDATES_PER_BATCH, level.random);
            scheduleBatch(level);
        });
    }
}
