package com.pasterdream.pasterdreammod.client.audio;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.SelectMusicEvent;

/**
 * 暮影之笼事件 BGM 协调器（客户端）。
 * <p>
 * 暮影之笼激活时服务端播放 {@code shadow_music_0}（SoundSource.MUSIC），
 * 但 1.21.1 的 {@link net.minecraft.client.sounds.SoundEngine} 播放 MUSIC 源声音时
 * 不会停止原版 {@link net.minecraft.client.sounds.MusicManager} 正在播放的音乐，
 * 导致「暮影之笼 BGM + 原版 BGM（如主世界 music.game）」双 BGM 叠加。
 * <p>
 * 本类通过监听 {@link SelectMusicEvent} 在事件激活期间将音乐置为 {@code null}：
 * NeoForge 在 {@code MusicManager.tick} 中调用
 * {@code ClientHooks.selectMusic(...)}，返回 null 时原版 MusicManager 会
 * 停止当前音乐并保持静音（nextSongDelay=0），事件结束后恢复原曲立即重播。
 * <p>
 * 激活标志由 S2C {@link com.pasterdream.pasterdreammod.network.TwilightLanternMusicPayload}
 * 驱动（PDClientVfx 转发），服务端在事件 +55t 下发 true、+2600t 下发 false。
 */
@EventBusSubscriber(modid = PasterDreamMod.MOD_ID, value = Dist.CLIENT)
public final class TwilightLanternMusicHandler {

    /** 暮影之笼事件 BGM 激活标志：true 时静音原版 MusicManager 音乐 */
    private static volatile boolean shadowMusicActive = false;

    private TwilightLanternMusicHandler() {
        throw new UnsupportedOperationException("TwilightLanternMusicHandler 是客户端工具类，不可实例化");
    }

    /**
     * 设置/清除暮影之笼事件 BGM 激活标志（由网络包处理器调用，主线程）。
     *
     * @param active true=事件 BGM 激活中（静音原版音乐）；false=事件结束（恢复原版音乐）
     */
    public static void setActive(boolean active) {
        shadowMusicActive = active;
    }

    /**
     * 查询暮影之笼事件 BGM 是否激活中。
     *
     * @return true 表示原版音乐应保持静音
     */
    public static boolean isActive() {
        return shadowMusicActive;
    }

    /**
     * 音乐选择事件：事件激活期间强制返回 null，使原版 MusicManager 停止并保持静音。
     *
     * @param event 音乐选择事件
     */
    @SubscribeEvent
    public static void onSelectMusic(SelectMusicEvent event) {
        if (shadowMusicActive) {
            // 置 null 会取消任何正在播放的原版背景音乐（MusicManager.tick 中 stopPlaying + return）
            event.setMusic(null);
        }
    }

    /**
     * 玩家登出/断开连接：重置激活标志，防止退出世界后残留导致下次进入原版 BGM 被静音。
     * <p>
     * 重新登录后由服务端 {@code PlayerLoggedInEvent} 补发当前维度真实状态。
     *
     * @param event 客户端玩家网络事件
     */
    @SubscribeEvent
    public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        shadowMusicActive = false;
    }
}
