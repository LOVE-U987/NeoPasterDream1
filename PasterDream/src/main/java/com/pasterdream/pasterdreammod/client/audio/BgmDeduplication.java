package com.pasterdream.pasterdreammod.client.audio;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import net.minecraft.client.Minecraft;

/**
 * BGM 去重检测与修复器 —— 检测并修复音乐播放中的重复状态
 * <p>
 * 职责：
 * <ul>
 *   <li>检测交叉淡化同名冲突（fadingOutSound 和 currentSound 播放同一首 BGM）</li>
 *   <li>检测 currentSound 已失效但 currentMusicName 残留的状态不一致</li>
 *   <li>检测 fadingOutSound 已失效但 fadingOutMusicName 残留的状态不一致</li>
 * </ul>
 * <p>
 * 在每次 tick 主逻辑前调用，确保播放状态的一致性。
 */
public class BgmDeduplication {

    private final MusicPlaybackController playbackController;
    private final CrossfadeManager crossfadeManager;

    public BgmDeduplication(MusicPlaybackController playbackController, CrossfadeManager crossfadeManager) {
        this.playbackController = playbackController;
        this.crossfadeManager = crossfadeManager;
    }

    /**
     * 执行 BGM 去重检测与修复
     * <p>
     * 在每次 tick 主逻辑前调用，处理以下场景：
     * <ul>
     *   <li><b>场景1 — 交叉淡化同名冲突</b>：fadingOutSound 和 currentSound
     *       播放的是同一首 BGM，导致交叉淡化时同曲重叠</li>
     *   <li><b>场景2 — 状态不一致</b>：currentSound 已停止但 currentMusicName 未清除</li>
     *   <li><b>场景3 — 淡出状态不一致</b>：fadingOutSound 已失效但 fadingOutMusicName 残留</li>
     * </ul>
     */
    public void deduplicate() {
        fixSameMusicCrossfade();
        fixCurrentSoundInconsistency();
        fixFadingOutSoundInconsistency();
    }

    /**
     * 场景1：currentSound 和 fadingOutSound 播放同名 BGM
     * <p>
     * 交叉淡化本应为不同曲目设计，同名 BGM 不应进入淡出状态。
     */
    private void fixSameMusicCrossfade() {
        String currentMusicName = playbackController.getCurrentMusicName();
        String fadingOutMusicName = crossfadeManager.getFadingOutMusicName();

        if (playbackController.getCurrentSound() != null
                && crossfadeManager.getFadingOutSound() != null
                && fadingOutMusicName != null && currentMusicName != null
                && fadingOutMusicName.equals(currentMusicName)) {
            PasterDreamMod.LOGGER.warn(
                    "[BgmDeduplication] 检测到 BGM 重复播放[同名交叉淡化]: {}, 强制停止淡出实例",
                    currentMusicName);
            crossfadeManager.stopCrossfade();
        }
    }

    /**
     * 场景2：currentSound 已失效但 currentMusicName 残留
     * <p>
     * 当 currentSound 为 null 且 currentMusicName 不为 null
     * 且没有淡出中的声音时，说明状态不一致。
     */
    private void fixCurrentSoundInconsistency() {
        String currentMusicName = playbackController.getCurrentMusicName();

        if (playbackController.getCurrentSound() == null
                && currentMusicName != null
                && crossfadeManager.getFadingOutSound() == null) {
            PasterDreamMod.LOGGER.warn(
                    "[BgmDeduplication] 检测到状态不一致: currentSound=null, musicName={}, 正在重置状态",
                    currentMusicName);
            playbackController.stop();
        }
    }

    /**
     * 场景3：fadingOutSound 已失效但 fadingOutMusicName 残留
     */
    private void fixFadingOutSoundInconsistency() {
        if (crossfadeManager.getFadingOutSound() == null
                && crossfadeManager.getFadingOutMusicName() != null) {
            crossfadeManager.stopCrossfade();
        }
    }

    /**
     * 检查指定的 BGM 是否正在播放中（含淡出中）
     *
     * @param musicName 要检查的音乐名称
     * @return 如果该音乐当前正在播放（含淡出中）返回 true
     */
    public boolean isBgmActive(String musicName) {
        if (musicName == null) return false;
        return playbackController.isActive(musicName) || crossfadeManager.isFadingOut(musicName);
    }
}