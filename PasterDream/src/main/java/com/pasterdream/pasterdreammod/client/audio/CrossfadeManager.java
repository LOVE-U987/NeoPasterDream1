package com.pasterdream.pasterdreammod.client.audio;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.sounds.SoundEvent;

/**
 * 交叉淡化管理器 —— 负责群系切换时的音乐交叉淡化过渡
 * <p>
 * 职责：
 * <ul>
 *   <li>触发交叉淡化（新音乐渐强 + 旧音乐渐弱）</li>
 *   <li>交叉淡化期间重定向淡入目标（过渡中群系再次变化时）</li>
 *   <li>触发全体淡出（停止播放 / 离开维度时的淡出过渡）</li>
 *   <li>按 tick 检查过渡完成情况并管理状态（{@link FadeState}）</li>
 * </ul>
 * <p>
 * 过渡策略：
 * <ol>
 *   <li>旧音乐通过 {@link VolumeSoundInstance#fadeOutAndStop} 逐 tick 渐弱，静音后自动停止</li>
 *   <li>新音乐以零音量开始播放，逐 tick 渐强至 {@link ModMusicManager#TARGET_VOLUME}</li>
 *   <li>两侧渐变均在 {@link ModMusicManager#CROSSFADE_STEPS} tick 内完成</li>
 * </ol>
 * <p>
 * 音量渐变依赖 {@link VolumeSoundInstance}（AbstractTickableSoundInstance 子类）：
 * SoundEngine 仅对 tickable 声音实例每 tick 重新读取音量，普通实例的音量在开播时即固定。
 */
public class CrossfadeManager {

    private final MusicPlaybackController playbackController;
    private final SoundEventLookup soundEventLookup;

    /** 当前淡入淡出状态 */
    private FadeState fadeState = FadeState.IDLE;

    /** 正在淡出的旧音乐声音实例 */
    private VolumeSoundInstance fadingOutSound;

    /** 正在淡出的旧音乐名称 */
    private String fadingOutMusicName;

    /** 正在淡入的新音乐声音实例（同一实例同时由 playbackController 作为当前音乐持有） */
    private VolumeSoundInstance fadingInSound;

    public CrossfadeManager(MusicPlaybackController playbackController, SoundEventLookup soundEventLookup) {
        this.playbackController = playbackController;
        this.soundEventLookup = soundEventLookup;
    }

    /**
     * 触发交叉淡化
     * <p>
     * 旧音乐逐 tick 渐弱至静音后自动停止；新音乐从零音量开始逐 tick 渐强至目标音量。
     * 若已有交叉淡化在进行中，则转为重定向淡入目标。
     *
     * @param newMusicName 目标音乐名称，null 表示淡出并停止所有音乐
     */
    public void startCrossfade(String newMusicName) {
        // 相同音乐 → 跳过
        if (newMusicName != null && newMusicName.equals(playbackController.getCurrentMusicName())) {
            return;
        }

        // 新音乐为 null → 全体淡出后停止（不再瞬间静音）
        if (newMusicName == null) {
            beginFadeOutAll();
            return;
        }

        // 交叉淡化已在进行中 → 重定向淡入目标
        if (fadeState == FadeState.CROSSFADE) {
            redirectCrossfade(newMusicName);
            return;
        }

        // 无当前音乐 → 直接播放，无需过渡
        if (!playbackController.isPlaying()) {
            playbackController.play(newMusicName);
            return;
        }

        // 先校验新音乐可查找，再提交任何过渡状态：
        // 查找失败时保持现场原样，避免残留淡出状态误停当前音乐
        SoundEvent soundEvent = soundEventLookup.lookup(newMusicName);
        if (soundEvent == null) return;

        // 旧音乐进入逐 tick 渐弱，静音后自动停止
        SoundInstance currentSound = playbackController.getCurrentSound();
        if (currentSound instanceof VolumeSoundInstance volumeSound) {
            volumeSound.fadeOutAndStop(ModMusicManager.CROSSFADE_STEPS);
            fadingOutSound = volumeSound;
            fadingOutMusicName = playbackController.getCurrentMusicName();
        } else if (currentSound != null) {
            // 防御：非可调音量实例无法渐变，直接停止
            Minecraft.getInstance().getSoundManager().stop(currentSound);
            fadingOutSound = null;
            fadingOutMusicName = null;
        }

        // 新音乐从零音量开始渐强
        VolumeSoundInstance newSound = VolumeSoundInstance.forMusicFadeIn(soundEvent);
        newSound.fadeTo(ModMusicManager.getEffectiveVolume(), ModMusicManager.CROSSFADE_STEPS);
        playbackController.setCurrentSound(newSound);
        playbackController.setCurrentMusicName(newMusicName);
        Minecraft.getInstance().getSoundManager().play(newSound);
        fadingInSound = newSound;

        fadeState = FadeState.CROSSFADE;
    }

    /**
     * 交叉淡化期间重定向淡入目标
     * <p>
     * 过渡中群系再次变化时调用，避免音乐切换滞后整个淡化周期。三种情形：
     * <ul>
     *   <li>目标与当前淡入音乐相同 → 无操作</li>
     *   <li>目标正是淡出中的旧音乐（玩家折返原群系）→ 反转淡入淡出方向，音量无跳变</li>
     *   <li>全新目标 → 停止当前淡入侧（音量尚低），改为淡入新目标</li>
     * </ul>
     *
     * @param newMusicName 新的淡入目标音乐名称
     */
    public void redirectCrossfade(String newMusicName) {
        if (fadeState != FadeState.CROSSFADE || newMusicName == null) {
            return;
        }

        // 目标与当前淡入音乐相同 → 无需重定向
        if (newMusicName.equals(playbackController.getCurrentMusicName())) {
            return;
        }

        // 目标正是淡出中的旧音乐（玩家折返原群系）→ 直接反转淡入淡出方向
        if (newMusicName.equals(fadingOutMusicName) && fadingOutSound != null) {
            VolumeSoundInstance reversedIn = fadingOutSound;
            String reversedInName = fadingOutMusicName;
            VolumeSoundInstance reversedOut = fadingInSound;
            String reversedOutName = playbackController.getCurrentMusicName();

            // 原淡入侧改为从当前音量渐弱至停止
            if (reversedOut != null) {
                reversedOut.fadeOutAndStop(ModMusicManager.CROSSFADE_STEPS);
                fadingOutSound = reversedOut;
                fadingOutMusicName = reversedOutName;
            } else {
                fadingOutSound = null;
                fadingOutMusicName = null;
            }

            // 原淡出侧改为从当前音量渐强，重新成为当前音乐
            reversedIn.fadeTo(ModMusicManager.getEffectiveVolume(), ModMusicManager.CROSSFADE_STEPS);
            fadingInSound = reversedIn;
            playbackController.setCurrentSound(reversedIn);
            playbackController.setCurrentMusicName(reversedInName);
            PasterDreamMod.LOGGER.debug("[CrossfadeManager] 交叉淡化方向反转: {} <-> {}",
                    reversedOutName, reversedInName);
            return;
        }

        // 全新目标：同样先校验可查找，再提交状态
        SoundEvent soundEvent = soundEventLookup.lookup(newMusicName);
        if (soundEvent == null) return;

        // 停止原淡入侧（音量尚低，直接停止的听感突兀度可接受）
        if (fadingInSound != null) {
            Minecraft.getInstance().getSoundManager().stop(fadingInSound);
        }

        VolumeSoundInstance newSound = VolumeSoundInstance.forMusicFadeIn(soundEvent);
        newSound.fadeTo(ModMusicManager.getEffectiveVolume(), ModMusicManager.CROSSFADE_STEPS);
        playbackController.setCurrentSound(newSound);
        playbackController.setCurrentMusicName(newMusicName);
        Minecraft.getInstance().getSoundManager().play(newSound);
        fadingInSound = newSound;
        PasterDreamMod.LOGGER.debug("[CrossfadeManager] 淡入目标重定向: -> {}", newMusicName);
    }

    /**
     * 触发全体淡出 —— 所有在播音乐渐弱至静音后停止
     * <p>
     * 用于停止播放 / 离开自定义维度的场景，替代瞬间停止。
     * 幂等：已处于全体淡出中时重复调用无副作用。
     * 触发后 playbackController 的当前音乐状态被清空，声音实例由本管理器接管直至淡出结束。
     */
    public void beginFadeOutAll() {
        // 已在全体淡出中 → 幂等，无需重复触发
        if (fadeState == FadeState.FADING_OUT) {
            return;
        }

        // 交叉淡化中的淡入侧 → 从当前音量转为淡出
        if (fadingInSound != null) {
            fadingInSound.fadeOutAndStop(ModMusicManager.CROSSFADE_STEPS);
        }

        // 当前正在播放的音乐 → 转为淡出并从播放控制器接管
        SoundInstance currentSound = playbackController.getCurrentSound();
        String currentMusicName = playbackController.getCurrentMusicName();
        if (currentSound != null && currentSound != fadingInSound) {
            if (currentSound instanceof VolumeSoundInstance volumeSound) {
                volumeSound.fadeOutAndStop(ModMusicManager.CROSSFADE_STEPS);
                // 淡出槽位空闲时接管；若已被交叉淡化的旧音乐占用则保持其继续淡出
                if (fadingOutSound == null) {
                    fadingOutSound = volumeSound;
                    fadingOutMusicName = currentMusicName;
                }
            } else {
                // 防御：非可调音量实例无法渐变，直接停止
                Minecraft.getInstance().getSoundManager().stop(currentSound);
            }
        }
        playbackController.setCurrentSound(null);
        playbackController.setCurrentMusicName(null);

        // 没有任何可淡出的声音 → 直接回到空闲
        if (fadingOutSound == null && fadingInSound == null) {
            fadeState = FadeState.IDLE;
            return;
        }
        fadeState = FadeState.FADING_OUT;
    }

    /**
     * 执行一步过渡检查
     * <p>
     * 音量渐变由 {@link VolumeSoundInstance#tick()} 自驱完成，
     * 此处负责检测各侧渐变是否结束（音量归零自动停止 / 曲目自然播完）并推进状态机。
     *
     * @return true 表示过渡仍在进行中，false 表示已完成
     */
    public boolean updateStep() {
        if (fadeState == FadeState.IDLE) return false;

        SoundManager soundManager = Minecraft.getInstance().getSoundManager();

        // 淡出侧：音量归零自动停止、或曲目自然播完 → 清理引用
        if (fadingOutSound != null
                && (fadingOutSound.isStopped() || !soundManager.isActive(fadingOutSound))) {
            fadingOutSound = null;
            fadingOutMusicName = null;
        }

        if (fadeState == FadeState.CROSSFADE) {
            // 淡入侧：达到目标音量（或异常失活）即视为淡入完成
            boolean fadeInDone = fadingInSound == null
                    || fadingInSound.isFadeComplete()
                    || !soundManager.isActive(fadingInSound);
            if (fadingOutSound == null && fadeInDone) {
                // 交叉淡化完成：淡入实例的所有权完全交还 playbackController
                fadingInSound = null;
                fadeState = FadeState.IDLE;
                return false;
            }
            return true;
        }

        // FADING_OUT：等待所有声音淡出结束
        if (fadingInSound != null
                && (fadingInSound.isStopped() || !soundManager.isActive(fadingInSound))) {
            fadingInSound = null;
        }
        if (fadingOutSound == null && fadingInSound == null) {
            fadeState = FadeState.IDLE;
            return false;
        }
        return true;
    }

    /**
     * 立即终止过渡并清理淡出状态（去重修复等场景使用）
     * <p>
     * 淡出中的旧音乐被直接停止；淡入中的音乐（若有）作为当前音乐
     * 由 playbackController 继续持有，其自驱渐变会继续完成。
     */
    public void stopCrossfade() {
        if (fadingOutSound != null) {
            Minecraft.getInstance().getSoundManager().stop(fadingOutSound);
        }
        fadingOutSound = null;
        fadingOutMusicName = null;
        fadingInSound = null;
        fadeState = FadeState.IDLE;
    }

    /**
     * 是否有过渡（交叉淡化或全体淡出）正在进行
     *
     * @return 如果过渡进行中返回 true
     */
    public boolean isCrossfading() {
        return fadeState != FadeState.IDLE;
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
