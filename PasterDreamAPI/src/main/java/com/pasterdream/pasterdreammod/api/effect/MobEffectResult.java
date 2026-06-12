package com.pasterdream.pasterdreammod.api.effect;

import com.pasterdream.pasterdreammod.api.effect.builder.MobEffectBuilder;
import com.pasterdream.pasterdreammod.api.effect.base.PasterDreamEffect;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.function.Supplier;

/**
 * 药水效果注册结果 —— 包含效果注册后的所有引用信息
 * <p>
 * 由 {@link MobEffectBuilder#build()} 返回，
 * 持有效果类型的 DeferredHolder 和直接引用，
 * 方便在后续流程中获取效果实例。
 * <p>
 * 使用示例：
 * <pre>{@code
 * MobEffectResult result = MobEffectAPI.createEffect("dreamwish_buff")
 *     .beneficial()
 *     .color(0xFF69B4)
 *     .build();
 *
 * // 获取效果类型
 * MobEffect effect = result.effect();
 *
 * // 获取注册句柄
 * DeferredHolder<MobEffect, MobEffect> holder = result.holder();
 * }</pre>
 *
 * @param name   效果注册名称（如 "dreamwish_buff"）
 * @param holder 效果类型的 DeferredHolder 引用
 */
public record MobEffectResult(
        String name,
        DeferredHolder<MobEffect, MobEffect> holder
) {

    /**
     * 获取效果类型的 Supplier 形式
     * <p>
     * 适用于延迟获取场景。
     *
     * @return 效果类型的 Supplier
     */
    public Supplier<MobEffect> typeSupplier() {
        return holder::get;
    }

    /**
     * 获取效果类型直接引用
     * <p>
     * 注意：此方法只能在注册阶段之后调用。
     *
     * @return 效果类型实例
     */
    public MobEffect effect() {
        return holder.get();
    }

    /**
     * 如果效果是 {@link PasterDreamEffect} 子类，返回其强类型引用
     *
     * @return {@link PasterDreamEffect} 实例，如果不是则返回 null
     */
    public PasterDreamEffect asPasterDreamEffect() {
        MobEffect effect = holder.get();
        return effect instanceof PasterDreamEffect pde ? pde : null;
    }
}