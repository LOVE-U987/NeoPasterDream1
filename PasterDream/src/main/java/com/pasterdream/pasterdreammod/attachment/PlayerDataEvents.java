package com.pasterdream.pasterdreammod.attachment;

import com.pasterdream.pasterdreammod.registry.PDGameRules;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameRules;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

/**
 * 玩家数据生命周期事件处理器（游戏总线）
 * <p>
 * 移植自原版 {@code SanCapability#init} / {@code MeltDreamEnergyCapability#init}
 * 注册的四组事件监听：登录、重生、跨维度、克隆。均由主类构造器通过
 * {@code NeoForge.EVENT_BUS.addListener} 显式接线。
 * <p>
 * 同步时机与原版一致：四个事件均对两类数据做<b>全量</b> S2C 同步。
 */
public class PlayerDataEvents {

    /**
     * 玩家登录：全量同步 San 与融梦能量（对应原版 playerLoggedIn → sync）
     *
     * @param event 登录事件
     */
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer sp) {
            PDAttachments.syncSan(sp);
            PDAttachments.syncMeltDreamEnergy(sp);
        }
    }

    /**
     * 玩家重生：全量同步（对应原版 playerRespawn → sync）
     *
     * @param event 重生事件
     */
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer sp) {
            PDAttachments.syncSan(sp);
            PDAttachments.syncMeltDreamEnergy(sp);
        }
    }

    /**
     * 玩家跨维度：全量同步（对应原版 playerChangeDimension → sync）
     *
     * @param event 跨维度事件
     */
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer sp) {
            PDAttachments.syncSan(sp);
            PDAttachments.syncMeltDreamEnergy(sp);
        }
    }

    /**
     * 玩家克隆（死亡重生 / 末地返回等）
     * <p>
     * 对照原版 clone 逻辑：
     * <ul>
     *   <li>死亡：San 重置为游戏规则 {@code pasterdreamStartSanOnRevive}（默认 90），
     *       开关取 {@code pasterdreamSanSystem}；融梦能量由 attachment 的 copyOnDeath 自动保留</li>
     *   <li>非死亡：两类数据均由 NeoForge attachment 机制自动复制（等价原版 reviveCaps + 拷贝）</li>
     * </ul>
     * 随后全量同步（与原版 playerClone 末尾的 sync 一致）。
     *
     * @param event 克隆事件
     */
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (!(event.getEntity() instanceof ServerPlayer sp)) {
            return;
        }
        if (event.isWasDeath()) {
            GameRules rules = sp.serverLevel().getGameRules();
            sp.setData(PDAttachments.PLAYER_SAN, new SanData(
                    rules.getInt(PDGameRules.START_SAN_ON_REVIVE),
                    rules.getBoolean(PDGameRules.SAN_CHECK_SYSTEM)));
        }
        PDAttachments.syncSan(sp);
        PDAttachments.syncMeltDreamEnergy(sp);
    }
}
