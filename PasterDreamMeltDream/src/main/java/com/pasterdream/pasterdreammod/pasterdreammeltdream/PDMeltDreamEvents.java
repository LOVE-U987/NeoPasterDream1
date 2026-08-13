package com.pasterdream.pasterdreammod.pasterdreammeltdream;

import com.pasterdream.pasterdreammod.api.meltdream.MeltDreamEnergyAPI;
import com.pasterdream.pasterdreammod.api.meltdream.MeltDreamEnergyConfigRegistry;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 融梦能量自然恢复事件处理器。
 * <p>
 * 实现 {@code recover interval} / {@code recover amount} 两项配置：
 * 玩家在线期间每间隔 {@code recover interval}（tick，默认 1200 = 60 秒）
 * 自然恢复 {@code recover amount}（默认 0.1）融梦能量。
 * <p>
 * 挂载于 PasterDreamMeltDream 附属模组（配置定义所在模块），
 * 仅在融梦能量系统总开关开启时生效；能量已满时由
 * {@link MeltDreamEnergyAPI#addEnergy(Player, double)} 内部钳制到上限。
 * <p>
 * 恢复计时使用各玩家独立的 {@code tickCount} 差值（跨会话累计，
 * 玩家重新登录后从 0 重新计时），登录事件负责清理残留记录，
 * 避免旧会话的计时残留导致新会话恢复异常。
 *
 * @author PasterDream
 */
public final class PDMeltDreamEvents {

    /** 各玩家上次触发自然恢复时的 tickCount（服务端线程访问，仅在线玩家） */
    private static final Map<UUID, Integer> LAST_RECOVER_TICK = new ConcurrentHashMap<>();

    private PDMeltDreamEvents() {
        throw new UnsupportedOperationException("PDMeltDreamEvents 是静态事件处理器，不可实例化");
    }

    /**
     * 玩家每 tick 事件：按配置间隔周期恢复融梦能量。
     * <p>
     * 仅服务端生效；系统总开关关闭、间隔或恢复量非正数时直接跳过。
     *
     * @param event 玩家 tick 事件（Post 阶段）
     */
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer sp)) return;
        if (sp.level().isClientSide) return;

        var config = MeltDreamEnergyConfigRegistry.get();
        if (!config.enabled().get()) return;
        int interval = config.recoverInterval().get();
        if (interval <= 0) return;
        double amount = config.recoverAmount().get();
        if (amount <= 0) return;

        UUID uuid = sp.getUUID();
        int lastTick = LAST_RECOVER_TICK.getOrDefault(uuid, 0);
        if (sp.tickCount - lastTick >= interval) {
            // 无论能量是否已满都推进计时，保证下次恢复点稳定可预期
            LAST_RECOVER_TICK.put(uuid, sp.tickCount);
            MeltDreamEnergyAPI.addEnergy(sp, amount);
        }
    }

    /**
     * 玩家登录事件：清理该玩家的恢复计时残留。
     * <p>
     * 玩家下线后 {@code tickCount} 不再增长，重新登录会创建新实体实例
     * （tickCount 从 0 重新计数），若不清理旧记录会导致计时错乱。
     *
     * @param event 玩家登录事件
     */
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer sp) {
            LAST_RECOVER_TICK.remove(sp.getUUID());
        }
    }
}
