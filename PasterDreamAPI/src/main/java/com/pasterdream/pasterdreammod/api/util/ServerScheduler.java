package com.pasterdream.pasterdreammod.api.util;

import com.pasterdream.pasterdreammod.api.PasterDreamAPI;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

/**
 * 服务端延迟任务调度器 (Server Scheduler)
 * <p>
 * 等价还原原版 MCreator 的 {@code queueServerWork(delay, task)} 语义：
 * 任务在<b>恰好 delay 个服务端 tick 之后</b>执行。
 * <p>
 * <b>为什么不用 {@code server.tell(new TickTask(tick, task))}</b>：
 * vanilla 的 TickTask 队列不是定时器——{@code MinecraftServer#shouldRun} 在服务器
 * 本 tick 尚有空闲时间（{@code haveTime()}）时会立即执行队列中的任意任务，
 * 目标 tick 只在过载时起延后作用。轻载环境下所有"延迟"任务会被压缩到提交
 * 当 tick 同时执行（法术时间轴塌缩缺陷的根因，已由客户端实测取证）。
 * <p>
 * 线程模型：仅在服务端主线程调用（法术命中、实体 tick 等上下文），
 * 由 {@link ServerTickEvent.Post} 在每 tick 末尾统一派发到期任务。
 */
public final class ServerScheduler {

    /** 待执行任务（按到期 tick 排序的小顶堆） */
    private static final PriorityQueue<ScheduledTask> PENDING =
            new PriorityQueue<>(Comparator.comparingLong(ScheduledTask::dueTick));

    /** 单调递增的服务端 tick 计数（跨维度全局，与 MinecraftServer#getTickCount 同步推进） */
    private static long currentTick;

    /** 延迟任务记录 */
    private record ScheduledTask(long dueTick, Runnable task) {
    }

    private ServerScheduler() {
    }

    /**
     * 注册调度器到 NeoForge 游戏事件总线。
     * <p>
     * 主模组构造器中调用：{@code ServerScheduler.register(NeoForge.EVENT_BUS);}
     *
     * @param forgeBus NeoForge.EVENT_BUS
     */
    public static void register(IEventBus forgeBus) {
        forgeBus.addListener(ServerScheduler::onServerTick);
        forgeBus.addListener(ServerScheduler::onServerStopped);
        PasterDreamAPI.LOGGER.debug("[ServerScheduler] 已注册到 NeoForge 事件总线");
    }

    /**
     * 延迟指定 tick 后在服务端主线程执行任务
     * （等价原版 MCreator 的 queueServerWork）
     *
     * @param delay 延迟 tick 数（≤0 时下一 tick 执行）
     * @param task  要执行的任务
     */
    public static void schedule(int delay, Runnable task) {
        PENDING.add(new ScheduledTask(currentTick + Math.max(1, delay), task));
    }

    /**
     * 每服务端 tick 末派发全部到期任务
     *
     * @param event 服务端 tick 事件
     */
    public static void onServerTick(ServerTickEvent.Post event) {
        currentTick++;
        if (PENDING.isEmpty()) {
            return;
        }
        // 先收集再执行：任务体内可继续 schedule()，避免遍历中修改队列
        List<Runnable> due = new ArrayList<>();
        while (!PENDING.isEmpty() && PENDING.peek().dueTick() <= currentTick) {
            due.add(PENDING.poll().task());
        }
        for (Runnable task : due) {
            try {
                task.run();
            } catch (Exception e) {
                PasterDreamAPI.LOGGER.error("[ServerScheduler] 延迟任务执行异常", e);
            }
        }
    }

    /**
     * 服务器关闭时清空未执行任务（防止跨存档泄漏）
     *
     * @param event 服务器停止事件
     */
    public static void onServerStopped(ServerStoppedEvent event) {
        PENDING.clear();
    }

    /**
     * VERIFY / 单测用：不经完整 server tick，仅推进调度器时钟并执行到期任务。
     * <p>
     * 勿在正常游戏逻辑调用。
     *
     * @param ticks 推进 tick 数
     */
    public static void advanceForTest(int ticks) {
        for (int i = 0; i < ticks; i++) {
            currentTick++;
            if (PENDING.isEmpty()) {
                continue;
            }
            List<Runnable> due = new ArrayList<>();
            while (!PENDING.isEmpty() && PENDING.peek().dueTick() <= currentTick) {
                due.add(PENDING.poll().task());
            }
            for (Runnable task : due) {
                try {
                    task.run();
                } catch (Exception e) {
                    PasterDreamAPI.LOGGER.error("[ServerScheduler] advanceForTest 任务异常", e);
                }
            }
        }
    }
}
