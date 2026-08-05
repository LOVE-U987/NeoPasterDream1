package com.pasterdream.pasterdreammod.world;

import com.pasterdream.pasterdreammod.network.PDNetwork;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * 暮影之笼事件 BGM 状态追踪（服务端）。
 * <p>
 * 以「维度 → 激活中的笼子事件计数」维护暮影之笼事件状态，解决单次广播的边界问题：
 * <ul>
 *   <li><b>多笼并发</b>：同一维度多个笼子同时激活时，计数 > 0 持续静音，直到全部结束才恢复原版 BGM</li>
 *   <li><b>玩家中途换维度/重连</b>：登录或切换维度时按目标维度计数补发状态，
 *       离开事件维度 → 立即恢复原版 BGM；进入事件维度 → 立即静音</li>
 * </ul>
 * <p>
 * 服务端计数正确的前提：{@link #begin} 与 {@link #end} 严格成对调用
 * （暮影之笼 +55t 激活、+2600t 结束），均由 {@code TwilightLanternBlock} 驱动。
 */
public final class TwilightLanternMusicState {

    /** 维度 Key → 该维度当前激活中的暮影之笼事件计数 */
    private static final Map<ResourceKey<Level>, Integer> ACTIVE_COUNT = new HashMap<>();

    private TwilightLanternMusicState() {
        throw new UnsupportedOperationException("TwilightLanternMusicState 是服务端工具类，不可实例化");
    }

    /**
     * 事件激活：该维度计数 +1 并向维度内所有玩家广播「静音原版 BGM」。
     *
     * @param level 暮影之笼所在服务端维度
     */
    public static synchronized void begin(ServerLevel level) {
        if (level == null) return;
        ResourceKey<Level> dim = level.dimension();
        ACTIVE_COUNT.merge(dim, 1, Integer::sum);
        PDNetwork.sendTwilightLanternMusic(level, true);
    }

    /**
     * 事件结束：该维度计数 -1，归零后向维度内所有玩家广播「恢复原版 BGM」。
     *
     * @param level 暮影之笼所在服务端维度
     */
    public static synchronized void end(ServerLevel level) {
        if (level == null) return;
        ResourceKey<Level> dim = level.dimension();
        int next = ACTIVE_COUNT.getOrDefault(dim, 0) - 1;
        if (next <= 0) {
            ACTIVE_COUNT.remove(dim);
            PDNetwork.sendTwilightLanternMusic(level, false);
        } else {
            // 还有其他笼子事件激活中 → 保持静音，不广播
            ACTIVE_COUNT.put(dim, next);
        }
    }

    /**
     * 查询指定维度是否仍有激活中的笼子事件。
     *
     * @param level 服务端维度
     * @return true 表示该维度有激活中的事件（客户端应静音原版 BGM）
     */
    public static synchronized boolean isActiveIn(ServerLevel level) {
        return level != null && ACTIVE_COUNT.getOrDefault(level.dimension(), 0) > 0;
    }

    /**
     * 向单个玩家补发其当前所在维度的 BGM 状态（登录、切换维度时调用）。
     * <p>
     * 目标维度有激活事件 → 静音原版 BGM；否则恢复（清除残留标志）。
     *
     * @param player 目标玩家（服务端）
     */
    public static synchronized void syncToPlayer(ServerPlayer player) {
        if (player == null || player.level() == null) return;
        boolean active = isActiveIn((ServerLevel) player.level());
        PDNetwork.sendTwilightLanternMusicToPlayer(player, active);
    }

    /**
     * 玩家登录：补发其所在维度的当前 BGM 状态（防断线/退出后标志残留）。
     *
     * @param event 玩家登录事件
     */
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            syncToPlayer(player);
        }
    }

    /**
     * 玩家切换维度：按目标维度补发 BGM 状态。
     * <p>
     * 离开事件维度 → 补发 false（立即恢复原版 BGM）；进入事件维度 → 补发 true（立即静音）。
     *
     * @param event 玩家跨维度事件
     */
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            syncToPlayer(player);
        }
    }
}
