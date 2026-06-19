package com.pasterdream.pasterdreammod.client.audio;

/**
 * 音频系统工厂类 —— 负责组装和创建音频系统的各个组件
 * <p>
 * 职责：
 * <ul>
 *   <li>创建音频系统的所有依赖项</li>
 *   <li>按正确的依赖顺序组装组件</li>
 *   <li>提供统一的系统创建入口</li>
 * </ul>
 * <p>
 * 使用工厂模式可以：
 * <ul>
 *   <li>集中管理依赖关系</li>
 *   <li>简化客户端代码</li>
 *   <li>便于单元测试时注入 Mock 对象</li>
 * </ul>
 */
public class MusicSystemFactory {

    /**
     * 创建完整的音频系统
     *
     * @return 配置好的 ModMusicManager 实例
     */
    public static ModMusicManager createMusicSystem() {
        // 按依赖顺序创建组件
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

        // 组装最终的管理器
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
     *
     * @param biomeMusicRegistry    群系音乐注册表（可为 Mock）
     * @param soundEventLookup      声音事件查找器（可为 Mock）
     * @param playbackController    音乐播放控制器（可为 Mock）
     * @param crossfadeManager      交叉淡化管理器（可为 Mock）
     * @param cooldownManager       冷却管理器（可为 Mock）
     * @param loopRestartManager    循环重播管理器（可为 Mock）
     * @param deduplication         BGM 去重检测器（可为 Mock）
     * @return 配置好的 ModMusicManager 实例
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
