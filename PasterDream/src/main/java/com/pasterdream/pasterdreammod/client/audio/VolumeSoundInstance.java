package com.pasterdream.pasterdreammod.client.audio;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;

/**
 * 可调音量声音实例 —— 继承 AbstractTickableSoundInstance 实现运行时音量渐变
 * <p>
 * Minecraft 的 SoundEngine 仅对 {@link net.minecraft.client.resources.sounds.TickableSoundInstance}
 * 在每个 tick 重新读取 {@link #getVolume()} 并应用到声道，普通声音实例的音量在开播时即被固定。
 * 因此本类继承 {@link AbstractTickableSoundInstance}，配合每 tick 向目标音量步进的渐变逻辑，
 * 实现 BGM 的真实淡入/淡出效果（由 {@link CrossfadeManager} 驱动使用）。
 * <p>
 * 渐变为自驱动：调用 {@link #fadeTo(float, int)} 或 {@link #fadeOutAndStop(int)} 设定目标后，
 * 音量在 SoundEngine 的每个 tick 中自动逼近目标；淡出至静音后可自动停止并被引擎移除。
 */
public class VolumeSoundInstance extends AbstractTickableSoundInstance {

    /**
     * 共享随机源 —— 避免每次创建声音实例都分配新的 RandomSource。
     * 仅用于 AbstractSoundInstance 内部音高/音量随机偏移，不涉及世界种子一致性。
     */
    private static final RandomSource SHARED_RANDOM = RandomSource.create();

    /** 当前实际音量（每 tick 向 targetVolume 步进） */
    private float currentVolume;

    /** 渐变目标音量 */
    private float targetVolume;

    /** 每 tick 的音量步进量（由 fadeTo 根据音量差与 tick 数计算） */
    private float volumeStepPerTick;

    /** 淡出至静音后是否自动停止（fadeOutAndStop 时为 true） */
    private boolean stopWhenSilent;

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
        super(event, source, SHARED_RANDOM);
        this.currentVolume = volume;
        this.targetVolume = volume;
        this.volumeStepPerTick = 0.0f;
        this.stopWhenSilent = false;
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
     * 为淡入用途快速创建实例 —— 从零音量开始播放
     * <p>
     * 配合 {@link #canStartSilent()} 返回 true，零音量开播不会被 SoundEngine 跳过。
     *
     * @param event 声音事件
     * @return 初始音量为 0 的 VolumeSoundInstance 实例
     */
    public static VolumeSoundInstance forMusicFadeIn(SoundEvent event) {
        return new VolumeSoundInstance(
                event, SoundSource.MUSIC,
                0.0f, 1.0f,
                false, true
        );
    }

    /**
     * 立即设置当前音量（同时清除进行中的渐变）
     *
     * @param volume 目标音量（0.0 ~ 1.0）
     */
    public void setVolume(float volume) {
        this.currentVolume = volume;
        this.targetVolume = volume;
        this.volumeStepPerTick = 0.0f;
        this.stopWhenSilent = false;
    }

    /**
     * 在指定 tick 数内从当前音量渐变到目标音量
     *
     * @param target 目标音量（0.0 ~ 1.0）
     * @param ticks  渐变所需 tick 数（至少 1）
     */
    public void fadeTo(float target, int ticks) {
        this.targetVolume = Math.max(0.0f, target);
        this.volumeStepPerTick = Math.abs(this.targetVolume - this.currentVolume) / Math.max(1, ticks);
        this.stopWhenSilent = false;
    }

    /**
     * 在指定 tick 数内渐弱至静音，静音后自动停止（由 SoundEngine 移除本实例）
     *
     * @param ticks 渐变所需 tick 数（至少 1）
     */
    public void fadeOutAndStop(int ticks) {
        fadeTo(0.0f, ticks);
        this.stopWhenSilent = true;
    }

    /**
     * 渐变是否已完成（当前音量已到达目标音量）
     *
     * @return 渐变完成返回 true
     */
    public boolean isFadeComplete() {
        return currentVolume == targetVolume;
    }

    /**
     * 获取渐变目标音量。
     * <p>
     * 由 {@link ModMusicManager} 用于检测配置变更后是否需更新当前播放音量。
     *
     * @return 目标音量值（0.0 ~ 1.0）
     */
    public float getTargetVolume() {
        return targetVolume;
    }

    /**
     * 每 tick 由 SoundEngine 调用 —— 步进音量渐变
     * <p>
     * SoundEngine 在调用本方法后会重新读取 {@link #getVolume()} 并应用到声道，
     * 借此实现真实的运行时音量渐变。
     */
    @Override
    public void tick() {
        if (currentVolume < targetVolume) {
            currentVolume = Math.min(targetVolume, currentVolume + volumeStepPerTick);
        } else if (currentVolume > targetVolume) {
            currentVolume = Math.max(targetVolume, currentVolume - volumeStepPerTick);
        }
        // 淡出完毕 → 标记停止，SoundEngine 将自动移除本实例
        if (stopWhenSilent && currentVolume <= 0.0f) {
            stop();
        }
    }

    /**
     * 允许以零音量开始播放（淡入起点），避免被 SoundEngine 以"音量为零"为由跳过
     */
    @Override
    public boolean canStartSilent() {
        return true;
    }

    @Override
    public float getVolume() {
        return currentVolume;
    }
}
