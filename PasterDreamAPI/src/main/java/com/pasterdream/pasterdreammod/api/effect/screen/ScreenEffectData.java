package com.pasterdream.pasterdreammod.api.effect.screen;

/**
 * 屏幕特效数据标记接口 —— 具体特效数据（如纯色填充）的基类
 * <p>
 * 借鉴开源模组 FDLib（作者 FINDERFEED）的 {@code ScreenEffectData} 设计思路
 * （独立实现，非复制）。每个屏幕特效类型携带自己的数据实现，数据需可网络
 * 传输（经 {@link ScreenEffectType#dataCodec()}）。
 * <p>
 * 本接口位于 API 通用包（服务端可安全加载），不含客户端符号。
 *
 * @see ScreenEffectType
 */
public interface ScreenEffectData {
}
