package com.pasterdream.pasterdreammod.client.audio;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;

/**
 * 音乐播放控制器 —— 负责 BGM 的播放、停止和重播
 * <p>
 * 职责：
 * <ul>
 *   <li>直接播放指定音乐（无过渡）</li>
 *   <li>重新播放当前音乐（循环重播用）</li>
 *   <li>停止当前音乐并清除状态</li>
 *   <li>查询当前音乐播放状态</li>
 * </ul>
 * <p>
 * 不包含交叉淡化、冷却、循环间隔等逻辑。
 */
public class MusicPlaybackController {

    private final SoundEventLookup soundEventLookup;

    /** 当前正在播放的音乐声音实例 */
    private SoundInstance currentSound;

    /** 当前音乐名称 */
    private String currentMusicName;

    public MusicPlaybackController(SoundEventLookup soundEventLookup) {
        this.soundEventLookup = soundEventLookup;
    }

    /**
     * 直接播放音乐（无过渡）
     * <p>
     * 如果当前已有音乐在播放，会先停止再播放新音乐。
     *
     * @param musicName 音乐名称
     */
    public void play(String musicName) {
        if (musicName == null) return;
        stop();
        SoundEvent soundEvent = soundEventLookup.lookup(musicName);
        if (soundEvent == null) return;
        currentMusicName = musicName;
        currentSound = VolumeSoundInstance.forMusic(soundEvent, ModMusicManager.TARGET_VOLUME);
        Minecraft.getInstance().getSoundManager().play(currentSound);
    }

    /**
     * 重新播放当前音乐（循环重播用）
     * <p>
     * 当非循环 BGM 播放完毕后调用此方法重新播放。
     */
    public void restart() {
        if (currentMusicName == null) return;
        if (currentSound != null) {
            Minecraft.getInstance().getSoundManager().stop(currentSound);
            currentSound = null;
        }
        SoundEvent soundEvent = soundEventLookup.lookup(currentMusicName);
        if (soundEvent == null) return;
        currentSound = VolumeSoundInstance.forMusic(soundEvent, ModMusicManager.TARGET_VOLUME);
        Minecraft.getInstance().getSoundManager().play(currentSound);
    }

    /**
     * 停止当前音乐并清除状态
     */
    public void stop() {
        if (currentSound != null) {
            Minecraft.getInstance().getSoundManager().stop(currentSound);
            currentSound = null;
        }
        currentMusicName = null;
    }

    /**
     * 当前是否有音乐正在播放
     *
     * @return 如果有音乐正在播放返回 true
     */
    public boolean isPlaying() {
        return currentSound != null;
    }

    /**
     * 检查指定音乐是否正在本控制器中播放
     *
     * @param musicName 音乐名称
     * @return 如果该音乐正在播放返回 true
     */
    public boolean isActive(String musicName) {
        return musicName != null
                && musicName.equals(currentMusicName)
                && currentSound != null
                && Minecraft.getInstance().getSoundManager().isActive(currentSound);
    }

    public SoundInstance getCurrentSound() {
        return currentSound;
    }

    public String getCurrentMusicName() {
        return currentMusicName;
    }

    /**
     * 设置当前声音实例（交叉淡化时由 CrossfadeManager 调用）
     */
    void setCurrentSound(SoundInstance sound) {
        this.currentSound = sound;
    }

    /**
     * 设置当前音乐名称（交叉淡化时由 CrossfadeManager 调用）
     */
    void setCurrentMusicName(String musicName) {
        this.currentMusicName = musicName;
    }
}