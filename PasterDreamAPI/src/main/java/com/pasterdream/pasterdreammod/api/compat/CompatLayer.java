package com.pasterdream.pasterdreammod.api.compat;

import com.pasterdream.pasterdreammod.api.entity.EntityResult;
import com.pasterdream.pasterdreammod.api.particle.ParticleAPI;
import com.pasterdream.pasterdreammod.api.particle.ParticleResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.registries.DeferredHolder;

/**
 * 兼容层 —— 集中管理所有已废弃的向后兼容方法
 * <p>
 * 此类的存在是为了集中展示所有过渡期兼容 API，
 * 每个方法都委托给对应的新 API 实现。
 * <b>此类将在下个主版本中移除。</b>
 * <p>
 * 请勿在新代码中使用此类的方法，应直接调用对应的 Facade API。
 *
 * @deprecated 此兼容层类为过渡期产物，将在下个主版本移除。
 *             请直接使用各 {@code *API} Facade 类。
 * @see com.pasterdream.pasterdreammod.api.entity.EntityAPI
 * @see com.pasterdream.pasterdreammod.api.particle.ParticleAPI
 * @see com.pasterdream.pasterdreammod.api.effect.MobEffectAPI
 */
@Deprecated(forRemoval = true, since = "0.0.3.2")
public final class CompatLayer {

    private CompatLayer() {
        throw new UnsupportedOperationException("CompatLayer 是纯静态工具类，不可实例化");
    }

    // ==================== 实体注册兼容方法 ====================

    /**
     * 获取实体注册结果的 DeferredHolder 引用（旧式 API）
     *
     * @param result 实体注册结果
     * @param <T>    实体类型
     * @return DeferredHolder 引用
     * @deprecated 请直接使用 {@link EntityResult#entityTypeSupplier()} 获取 Supplier，
     *             不再需要强转为 DeferredHolder。
     */
    @Deprecated(forRemoval = true, since = "0.0.3.2")
    @SuppressWarnings("unchecked")
    public static <T extends Entity> DeferredHolder<EntityType<?>, EntityType<T>> getDeferredHolder(
            EntityResult<T> result) {
        return result.deferredHolder();
    }

    // ==================== 粒子注册兼容方法 ====================

    /**
     * 将旧式直接注册的粒子同步到 API 缓存（旧式 API）
     *
     * @param particleName 粒子注册名
     * @param holder       粒子的 DeferredHolder 引用
     * @deprecated 请使用 {@link ParticleAPI#createParticle(String)} Builder 模式注册新粒子，
     *             不再需要手动缓存。
     */
    @Deprecated(forRemoval = true, since = "0.0.3.2")
    public static void cacheLegacyParticle(String particleName,
                                           DeferredHolder<?, ?> holder) {
        @SuppressWarnings("unchecked")
        DeferredHolder<net.minecraft.core.particles.ParticleType<?>,
                net.minecraft.core.particles.SimpleParticleType> typedHolder =
                (DeferredHolder<net.minecraft.core.particles.ParticleType<?>,
                        net.minecraft.core.particles.SimpleParticleType>) holder;
        ParticleAPI.cacheParticle(new ParticleResult(particleName, typedHolder));
    }
}
