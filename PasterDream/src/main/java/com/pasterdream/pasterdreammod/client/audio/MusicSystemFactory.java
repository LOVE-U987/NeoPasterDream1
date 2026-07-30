package com.pasterdream.pasterdreammod.client.audio;


/**
 * 音频系统工厂类 —— 负责组装和创建音频系统的各个组件
 * <p>
 * 纯逻辑子系统（冷却、循环间隔）来自 {@code api.audio}；
 * 客户端播放/交叉淡化保留在本包。
 */
public class MusicSystemFactory {

    /**
     * 创建完整的音频系统
     *
     * @return 配置好的 ModMusicManager 实例
     */
    public static ModMusicManager createMusicSystem() {
        BiomeMusicRegistry biomeMusicRegistry = new BiomeMusicRegistry();
        SoundEventLookup soundEventLookup = new SoundEventLookup();

        MusicPlaybackController playbackController =
                new MusicPlaybackController(soundEventLookup);

        CrossfadeManager crossfadeManager =
                new CrossfadeManager(playbackController, soundEventLookup);

        BgmDeduplication deduplication =
                new BgmDeduplication(playbackController, crossfadeManager);

        CooldownManager cooldownManager =
                new CooldownManager(ModMusicManager.DEFAULT_SWITCH_COOLDOWN_TICKS);

        LoopRestartManager loopRestartManager =
                new LoopRestartManager(1200, 1800, 600, 1200);

        return new ModMusicManager(
                biomeMusicRegistry,
                soundEventLookup,
                playbackController,
                crossfadeManager,
                cooldownManager,
                loopRestartManager,
                deduplication
        );
    }

    /**
     * 创建用于测试的音频系统（可注入 Mock 对象）
     */
    public static ModMusicManager createTestMusicSystem(
            BiomeMusicRegistry biomeMusicRegistry,
            SoundEventLookup soundEventLookup,
            MusicPlaybackController playbackController,
            CrossfadeManager crossfadeManager,
            CooldownManager cooldownManager,
            LoopRestartManager loopRestartManager,
            BgmDeduplication deduplication) {
        return new ModMusicManager(
                biomeMusicRegistry,
                soundEventLookup,
                playbackController,
                crossfadeManager,
                cooldownManager,
                loopRestartManager,
                deduplication
        );
    }
}
