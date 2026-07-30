package com.pasterdream.pasterdreammod.api.audio;

/**
 * 淡入淡出状态枚举 —— BGM 交叉淡化框架的过渡生命周期。
 * <p>
 * 状态说明：
 * <ul>
 *   <li>{@link #IDLE} — 空闲，无过渡进行中，音乐正常播放</li>
 *   <li>{@link #CROSSFADE} — 交叉淡化过渡中，旧音乐逐 tick 渐弱、新音乐逐 tick 渐强，
 *       期间淡入目标可被重定向（群系再次变化时）</li>
 *   <li>{@link #FADING_OUT} — 全体淡出中，所有音乐渐弱至静音后停止
 *       （离开自定义维度、停止播放时使用）</li>
 * </ul>
 * <p>
 * 本枚举无客户端依赖，可安全存在于 API 公共 classpath（含专用服务器）。
 */
public enum FadeState {

    /** 空闲，无过渡进行中 */
    IDLE,

    /** 交叉淡化过渡中：旧音乐渐弱 + 新音乐渐强同时进行，淡入目标可重定向 */
    CROSSFADE,

    /** 全体淡出中：所有音乐渐弱至静音后停止 */
    FADING_OUT
}
