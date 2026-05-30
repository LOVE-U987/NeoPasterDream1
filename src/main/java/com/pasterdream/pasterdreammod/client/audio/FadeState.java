package com.pasterdream.pasterdreammod.client.audio;

/**
 * 淡入淡出状态枚举 —— 定义 ModMusicManager 的过渡生命周期
 * <p>
 * 仅保留两个状态：
 * <ul>
 *   <li>{@link #IDLE} — 空闲，无过渡进行中，音乐正常播放</li>
 *   <li>{@link #FADING} — 交叉淡化过渡中，旧音乐逐级降低音量，新音乐逐级升高音量</li>
 * </ul>
 */
public enum FadeState {

    /** 空闲，无过渡进行中 */
    IDLE,

    /** 交叉淡化过渡中，旧音乐渐弱 + 新音乐渐强同时进行 */
    FADING
}
