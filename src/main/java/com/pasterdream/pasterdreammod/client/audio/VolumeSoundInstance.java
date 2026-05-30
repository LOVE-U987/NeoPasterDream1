package com.pasterdream.pasterdreammod.client.audio;

import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;

/**
 * 可调音量声音实例 —— 继承 AbstractSoundInstance 并重写 getVolume() 实现运行时音量控制
 * <p>
 * Minecraft 原版 {@link AbstractSoundInstance} 的音量在构造时固定，
 * 无法在播放过程中动态调整。本类通过可变的 volume 字段配合
 * ModMusicManager 的淡入淡出流程，实现流畅的音量渐变效果。
 * <p>
 * 注意：实际音效引擎可能无法实时响应音量变化（音量在播放时由 SoundEngine 缓存），
 * 建议在音量变化时停止旧实例并创建新实例播放（ModMusicManager 采用此策略）。
 */
public class VolumeSoundInstance extends AbstractSoundInstance {

    /** 当前实际音量（可运行时修改） */
    private float currentVolume;

    /**
     * 构造可调音量声音实例
     *
     * @param event      声音事件
     * @param source     声音分类
     * @param volume     初始音量（0.0 ~ 1.0）
     * @param pitch      音高（1.0 为原调）
     * @param looping    是否循环播放
     * @param relative   是否相对位置（true 表示跟随玩家）
     */
    public VolumeSoundInstance(SoundEvent event, SoundSource source,
                                float volume, float pitch,
                                boolean looping, boolean relative) {
        super(event, source, RandomSource.create());
        this.currentVolume = volume;
        this.volume = volume;
        this.pitch = pitch;
        this.looping = looping;
        this.relative = relative;
        this.attenuation = SoundInstance.Attenuation.NONE;
    }

    /**
     * 为音乐用途快速创建实例（相对位置、不循环、无衰减）
     *
     * @param event  声音事件
     * @param volume 音量
     * @return VolumeSoundInstance 实例
     */
    public static VolumeSoundInstance forMusic(SoundEvent event, float volume) {
        return new VolumeSoundInstance(
                event, SoundSource.MUSIC,
                volume, 1.0f,
                false, true
        );
    }

    /**
     * 设置当前音量
     *
     * @param volume 目标音量（0.0 ~ 1.0）
     */
    public void setVolume(float volume) {
        this.currentVolume = volume;
    }

    @Override
    public float getVolume() {
        return currentVolume;
    }
}
