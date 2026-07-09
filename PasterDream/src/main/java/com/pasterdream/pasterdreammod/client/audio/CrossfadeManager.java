package com.pasterdream.pasterdreammod.client.audio;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;

/**
 * 交叉淡化管理器 —— 负责群系切换时的音乐交叉淡化过渡
 * <p>
 * 职责：
 * <ul>
 *   <li>触发交叉淡化（新音乐渐强 + 旧音乐渐弱）</li>
 *   <li>按 tick 步进交叉淡化过程</li>
 *   <li>管理交叉淡化状态（{@link FadeState}）</li>
 * </ul>
 * <p>
 * 过渡策略：
 * <ol>
 *   <li>新音乐立即以目标音量开始播放</li>
 *   <li>旧音乐继续播放</li>
 *   <li>经过 {@link ModMusicManager#CROSSFADE_STEPS} tick 后停止旧音乐</li>
 * </ol>
 * <p>
 * 注意：当前实现为简化版本，不逐 tick 调整音量，而是在指定步数后直接停止旧音乐。
 * 如需真正的音量渐变，可配合 {@link VolumeSoundInstance} 的 setVolume() 实现。
 */
public class CrossfadeManager {

    private final MusicPlaybackController playbackController;
    private final SoundEventLookup soundEventLookup;

    /** 当前淡入淡出状态 */
    private FadeState fadeState = FadeState.IDLE;

    /** 正在淡出的旧音乐声音实例 */
    private SoundInstance fadingOutSound;

    /** 正在淡出的旧音乐名称 */
    private String fadingOutMusicName;

    /** 当前交叉淡化步数（0 ~ CROSSFADE_STEPS） */
    private int crossfadeStep;

    public CrossfadeManager(MusicPlaybackController playbackController, SoundEventLookup soundEventLookup) {
        this.playbackController = playbackController;
        this.soundEventLookup = soundEventLookup;
    }

    /**
     * 触发交叉淡化
     * <p>
     * 新音乐立即以目标音量持续播放，旧音乐继续播放；
     * 经过 CROSSFADE_STEPS tick 后停止旧音乐。
     *
     * @param newMusicName 目标音乐名称
     */
    public void startCrossfade(String newMusicName) {
        // 相同音乐 → 跳过
        if (newMusicName != null && newMusicName.equals(playbackController.getCurrentMusicName())) {
            return;
        }

        // 无当前音乐 → 直接播放
        if (!playbackController.isPlaying()) {
            playbackController.play(newMusicName);
            return;
        }

        // 新音乐为 null → 停止所有
        if (newMusicName == null) {
            stopCrossfade();
            playbackController.stop();
            return;
        }

        // 开始交叉淡化
        // TODO: 旧音乐应从当前音量开始逐级递减（当前直接以原音量播放后瞬间停止，未实现真正的渐弱效果）
        fadingOutSound = playbackController.getCurrentSound();
        fadingOutMusicName = playbackController.getCurrentMusicName();

        SoundEvent soundEvent = soundEventLookup.lookup(newMusicName);
        if (soundEvent == null) return;

        SoundInstance newSound = VolumeSoundInstance.forMusic(soundEvent, ModMusicManager.TARGET_VOLUME);
        playbackController.setCurrentSound(newSound);
        playbackController.setCurrentMusicName(newMusicName);
        Minecraft.getInstance().getSoundManager().play(newSound);

        fadeState = FadeState.FADING;
        crossfadeStep = 0;
    }

    /**
     * 执行一步交叉淡化
     * <p>
     * 仅计时计数，到期后停止旧音乐。
     *
     * @return true 表示交叉淡化仍在进行中，false 表示已完成
     */
    public boolean updateStep() {
        if (fadeState != FadeState.FADING) return false;

        // TODO: 需要按步计算旧音乐音量并调用 fadingOutSound 的 setVolume() 实现渐弱
        //       当前只做计数器递增，旧音乐音量始终保持不变，到期后直接停止
        //       参考: float progress = (float) crossfadeStep / CROSSFADE_STEPS;
        //             float volume = TARGET_VOLUME * (1.0f - progress);

        if (crossfadeStep >= ModMusicManager.CROSSFADE_STEPS) {
            stopCrossfade();
            return false;
        }
        crossfadeStep++;
        return true;
    }

    /**
     * 停止交叉淡化并清理淡出状态
     */
    public void stopCrossfade() {
        // TODO: 理想情况下旧音乐应由渐弱自然结束（音量降为0），而非此处直接停止
        // TODO: 后续应当替换
        if (fadingOutSound != null) {
            Minecraft.getInstance().getSoundManager().stop(fadingOutSound);
        }
        fadingOutSound = null;
        fadingOutMusicName = null;
        fadeState = FadeState.IDLE;
        crossfadeStep = 0;
    }

    /**
     * 是否正在进行交叉淡化
     *
     * @return 如果在交叉淡出中返回 true
     */
    public boolean isCrossfading() {
        return fadeState == FadeState.FADING;
    }

    /**
     * 检查指定音乐是否正在淡出中
     *
     * @param musicName 音乐名称
     * @return 如果该音乐正在淡出返回 true
     */
    public boolean isFadingOut(String musicName) {
        return musicName != null
                && musicName.equals(fadingOutMusicName)
                && fadingOutSound != null;
    }

    public SoundInstance getFadingOutSound() {
        return fadingOutSound;
    }

    public String getFadingOutMusicName() {
        return fadingOutMusicName;
    }
}