package com.pasterdream.pasterdreammod.api.effect.screen;

/**
 * 屏幕特效工厂 —— 由数据与生命周期参数创建特效实例
 * <p>
 * 借鉴开源模组 FDLib（作者 FINDERFEED）的 {@code ScreenEffectFactory} 设计思路
 * （独立实现，非复制）。工厂在客户端反查类型后调用，返回客户端特效对象；
 * 为保持 API 通用包不引用客户端抽象类，返回类型用无界类型参数 {@code S}。
 *
 * @param <S> 客户端特效类型
 */
@FunctionalInterface
public interface ScreenEffectFactory<S> {

    /**
     * 创建屏幕特效实例
     *
     * @param data     特效数据
     * @param inTime   渐入 tick 数
     * @param stayTime 持续 tick 数
     * @param outTime  渐出 tick 数
     * @return 特效实例（客户端侧具体类型）
     */
    S create(ScreenEffectData data, int inTime, int stayTime, int outTime);
}
