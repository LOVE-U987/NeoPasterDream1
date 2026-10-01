package com.pasterdream.pasterdreammod.world;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.api.util.ServerScheduler;
import com.pasterdream.pasterdreammod.config.PDCommonConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;

/**
 * 竞技场遗迹感染源。
 * <p>
 * 遗迹本身作为感染源：真实放置确认后（见
 * {@code PDAaroncosArenaWorldgen#confirmArenaPlacement}）以遗迹中心为原点，
 * 持续将 {@link #INFECTION_RADIUS} 范围内（按地表高度）的地面/水体/植被
 * 渐进转化为灯影之下风格方块。
 * <p>
 * 感染受多重门控约束（统一在 {@link ArenaInfectionUtils} 把关）：
 * 配置 {@code arena ruin infection enabled} 开启（默认关闭）、仅主世界、
 * 感染限制在竞技场群系范围内、BOSS 击败后永久退化。
 * <p>
 * 通过 {@link ServerScheduler} 自递归调度实现周期批量感染；
 * BOSS 胜利时由 {@link PortalRestorationHandler} 触发 {@link #stop()} 停止感染，
 * 避免与地形回滚互相拉锯。
 * <p>
 * 遗迹中心记录在 {@link PDAaroncosArenaSpawnData} 中；服务器启动时由
 * {@code PDAroncosArenaWorldgen#onServerStarting} 按配置与击败状态决定是否恢复感染
 * （配置关闭或 BOSS 已击败时不恢复 —— 对旧存档即"强制停止已有感染"）。
 */
public final class ArenaRuinInfection {

    /** 遗迹感染半径（方块），与竞技场群系覆盖半径一致 */
    private static final int INFECTION_RADIUS = 48;
    /** 每批最多处理的候选数，保证能在合理时间内覆盖大半径 */
    private static final int CANDIDATES_PER_BATCH = 80;
    /** 批处理调度间隔（tick），缩短以加快群系级覆盖 */
    private static final int BATCH_INTERVAL = 3;

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
     * <p>
     * 守卫：感染功能配置关闭或竞技场 BOSS 已被击败时不启动（静默返回），
     * 防止任何调用路径（服务器启动恢复、放置确认等）绕过开关。
     *
     * @param overworld 主世界服务端世界
     * @param center    遗迹中心坐标
     */
    public static void start(ServerLevel overworld, BlockPos center) {
        if (center == null) {
            return;
        }
        // 门控：感染功能关闭（默认）时任何路径都不启动
        if (!PDCommonConfig.ARENA_RUIN_INFECTION_ENABLED.get()) {
            return;
        }
        // 门控：BOSS 已击败 → 感染永久退化，不再启动
        if (PDAaroncosArenaSpawnData.get(overworld).isDefeated()) {
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
     * 停止持续感染（BOSS 击败回滚开始时调用，或配置关闭时由强制停止流程调用）。
     */
    public static void stop() {
        active = false;
    }

    /**
     * 感染是否处于运行状态（供诊断/日志使用）。
     *
     * @return true 若感染循环正在运行
     */
    public static boolean isActive() {
        return active;
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
     * <p>
     * 每批执行前检查运行条件（active / 配置开关 / BOSS 击败状态），
     * 配置中途关闭或 BOSS 胜利后循环自行终止，不再空转。
     *
     * @param level 主世界服务端世界
     */
    private static void scheduleBatch(ServerLevel level) {
        ServerScheduler.schedule(BATCH_INTERVAL, () -> {
            if (!active || level.getServer() == null) {
                return;
            }
            // 配置中途关闭 → 终止自循环（对运行中的存档即时强制停止）
            if (!PDCommonConfig.ARENA_RUIN_INFECTION_ENABLED.get()) {
                active = false;
                PasterDreamMod.LOGGER.info("[ArenaRuinInfection] 感染功能已关闭，遗迹感染循环终止");
                return;
            }
            // BOSS 已击败 → 终止自循环（正常退化路径）
            if (PDAaroncosArenaSpawnData.get(level).isDefeated()) {
                active = false;
                PasterDreamMod.LOGGER.info("[ArenaRuinInfection] 竞技场BOSS已被击败，遗迹感染循环终止");
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
