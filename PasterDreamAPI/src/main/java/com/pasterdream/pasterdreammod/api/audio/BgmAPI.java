package com.pasterdream.pasterdreammod.api.audio;

import com.pasterdream.pasterdreammod.api.PasterDreamAPI;

/**
 * BGM 交叉淡化框架门面（server-safe）。
 * <p>
 * API 提供：
 * <ul>
 *   <li>纯逻辑：{@link FadeState}、{@link CooldownManager}、{@link LoopRestartManager}、{@link BiomeMusicTable}</li>
 *   <li>契约：{@link IMusicEventLookup}</li>
 * </ul>
 * 客户端播放实现（VolumeSoundInstance / CrossfadeManager / MusicPlaybackController /
 * ModMusicManager）保留在主模 {@code client.audio}，避免 API 加载 {@code net.minecraft.client.*}。
 * <p>
 * 主模职责：资产列表、维度白名单、配置开关、SoundEvent 注册与 tick 胶水。
 * <p>
 * 可选 {@link BgmClientBridge}：若未来需要从 API 侧触发客户端初始化，可 set 后调用
 * {@link #initClientIfPresent()}（当前主模仍由 PDClientEvents 直接组装即可）。
 */
public final class BgmAPI {

    /**
     * 客户端桥接：仅在客户端安装实现；专用服务器保持 null。
     */
    public interface BgmClientBridge {
        /** 客户端初始化 BGM 系统（注册维度白名单、默认 biome 表等） */
        void init();
    }

    private static BgmClientBridge clientBridge;

    private BgmAPI() {
        throw new UnsupportedOperationException("BgmAPI 是纯静态门面，不可实例化");
    }

    /**
     * 安装客户端桥接（应在客户端 setup 调用一次）。
     */
    public static void setClientBridge(BgmClientBridge bridge) {
        clientBridge = bridge;
    }

    /**
     * 若已安装桥接则调用其 init；否则 no-op。
     */
    public static void initClientIfPresent() {
        if (clientBridge != null) {
            clientBridge.init();
            PasterDreamAPI.LOGGER.debug("[BgmAPI] 已通过 BgmClientBridge 初始化客户端 BGM");
        }
    }
}
