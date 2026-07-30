package com.pasterdream.pasterdreammod.client.audio;

/**
 * 淡入淡出状态枚举 —— 与 API {@link com.pasterdream.pasterdreammod.api.audio.FadeState} 取值一致。
 * <p>
 * 主模客户端代码继续使用本类型；权威定义在 API。枚举无法继承，故在主模保留同名常量镜像。
 *
 * @see com.pasterdream.pasterdreammod.api.audio.FadeState
 */
public enum FadeState {

    /** 空闲，无过渡进行中 */
    IDLE,

    /** 交叉淡化过渡中：旧音乐渐弱 + 新音乐渐强同时进行，淡入目标可重定向 */
    CROSSFADE,

    /** 全体淡出中：所有音乐渐弱至静音后停止 */
    FADING_OUT;

    /** 映射到 API 枚举 */
    public com.pasterdream.pasterdreammod.api.audio.FadeState toApi() {
        return switch (this) {
            case IDLE -> com.pasterdream.pasterdreammod.api.audio.FadeState.IDLE;
            case CROSSFADE -> com.pasterdream.pasterdreammod.api.audio.FadeState.CROSSFADE;
            case FADING_OUT -> com.pasterdream.pasterdreammod.api.audio.FadeState.FADING_OUT;
        };
    }

    /** 自 API 枚举映射 */
    public static FadeState fromApi(com.pasterdream.pasterdreammod.api.audio.FadeState api) {
        return switch (api) {
            case IDLE -> IDLE;
            case CROSSFADE -> CROSSFADE;
            case FADING_OUT -> FADING_OUT;
        };
    }
}
