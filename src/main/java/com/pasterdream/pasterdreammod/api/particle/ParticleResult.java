package com.pasterdream.pasterdreammod.api.particle;

import com.pasterdream.pasterdreammod.api.particle.builder.ParticleBuilder;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.function.Supplier;

/**
 * 粒子注册结果 —— 包含粒子注册后的所有引用信息
 * <p>
 * 由 {@link ParticleBuilder#build()} 返回，
 * 持有粒子类型的 DeferredHolder 和直接引用，
 * 方便在客户端注册 Provider 以及在代码中生成粒子。
 * <p>
 * 使用示例：
 * <pre>{@code
 * ParticleResult result = ParticleAPI.createParticle("sparkle")
 *     .alwaysShow()
 *     .texture("pasterdream:sparkle")
 *     .build();
 *
 * // 获取粒子类型（用于生成粒子）
 * ParticleType<?> type = result.particleType();
 *
 * // 获取注册句柄（用于注册 Provider）
 * DeferredHolder<ParticleType<?>, SimpleParticleType> holder = result.holder();
 * }</pre>
 *
 * @param name   粒子注册名称（如 "sparkle"）
 * @param holder 粒子类型的 DeferredHolder 引用
 */
public record ParticleResult(
        String name,
        DeferredHolder<ParticleType<?>, SimpleParticleType> holder
) {

    /**
     * 获取粒子类型的 Supplier 形式
     * <p>
     * 适用于延迟获取场景，如方块/物品的属性中引用粒子类型。
     *
     * @return 粒子类型的 Supplier
     */
    public Supplier<ParticleType<?>> typeSupplier() {
        return holder::get;
    }

    /**
     * 获取粒子类型直接引用
     * <p>
     * 注意：此方法只能在注册阶段之后调用，
     * 否则会抛出 NullPointerException。
     *
     * @return 粒子类型实例
     */
    public ParticleType<?> particleType() {
        return holder.get();
    }

    /**
     * 获取粒子类型的 ResourceLocation 字符串
     *
     * @return 如 "pasterdream:sparkle"
     */
    public String textureId() {
        return holder.getId().toString();
    }
}