package com.pasterdream.pasterdreammod.api.particle;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.api.particle.builder.ParticleBuilder;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 粒子注册 API —— 将繁琐的粒子类型注册集中管理，提供高效简洁的注册方式
 * <p>
 * 采用 Facade 模式 + Builder 模式设计，与 {@link com.pasterdream.pasterdreammod.api.block.BlockAPI}
 * 风格一致，覆盖粒子注册的完整流程：
 * <ul>
 *   <li><b>粒子类型注册</b>：通过 Builder 链式配置粒子属性并注册到 DeferredRegister</li>
 *   <li><b>资源文件自动生成</b>：自动生成 {@code particles/{name}.json} 粒子定义文件</li>
 *   <li><b>纹理元数据生成</b>：自动生成 {@code textures/particle/{name}.json} 纹理描述文件</li>
 *   <li><b>Provider 注册辅助</b>：在客户端便捷注册 {@link ParticleProvider}</li>
 *   <li><b>查询管理</b>：缓存已注册的粒子结果，方便后续查询</li>
 * </ul>
 * <p>
 * 使用示例：
 * <pre>{@code
 * // ====== 在 PDParticles 或任意注册类中 ======
 * ParticleResult sparkle = ParticleAPI.createParticle("sparkle")
 *     .alwaysShow()
 *     .texture("pasterdream:sparkle")
 *     .withGravity(0.05f)
 *     .build();
 *
 * // ====== 在 ClientSetup 中注册 Provider ======
 * @SubscribeEvent
 * public static void registerParticles(RegisterParticleProvidersEvent event) {
 *     ParticleAPI.registerProviderSprite(event, "sparkle", SparkleParticle.Provider::new);
 * }
 *
 * // ====== 查询粒子类型 ======
 * ParticleResult result = ParticleAPI.getParticle("sparkle");
 * if (result != null) {
 *     ParticleType<?> type = result.particleType();
 *     // 使用 type 生成粒子...
 * }
 * }</pre>
 *
 * @see com.pasterdream.pasterdreammod.api.particle.builder.ParticleBuilder
 * @see com.pasterdream.pasterdreammod.api.particle.ParticleResult
 */
public final class ParticleAPI {

    /**
     * API 专属的粒子类型注册器
     * <p>
     * 注意：需要在 {@code PasterDreamMod} 构造函数中注册到事件总线：
     * <pre>{@code
     * ParticleAPI.REGISTRY.register(modEventBus);
     * }</pre>
     */
    public static final DeferredRegister<ParticleType<?>> REGISTRY =
            DeferredRegister.create(Registries.PARTICLE_TYPE, PasterDreamMod.MOD_ID);

    /** 已注册的粒子结果缓存 */
    private static final Map<String, ParticleResult> REGISTERED_PARTICLES = new HashMap<>();

    private ParticleAPI() {
        throw new UnsupportedOperationException("ParticleAPI 是纯静态门面类，不可实例化");
    }

    // ======================== Builder 工厂方法 ========================

    /**
     * 创建一个粒子构建器
     * <p>
     * 采用链式调用配置粒子类型属性，
     * 最终通过 {@link ParticleBuilder#build()} 完成注册并返回 {@link ParticleResult}。
     *
     * @param particleName 粒子注册名称（snake_case 格式，如 "sparkle"）
     * @return {@link ParticleBuilder} 实例
     */
    public static ParticleBuilder createParticle(String particleName) {
        PasterDreamMod.LOGGER.info("[ParticleAPI] 🎨 开始创建粒子构建器: {}", particleName);
        return new ParticleBuilder(PasterDreamMod.MOD_ID, particleName);
    }

    // ======================== Provider 注册辅助 ========================

    /**
     * 注册粒子 Provider（精灵表模式）
     * <p>
     * 适用于使用 {@link net.minecraft.client.particle.SpriteSet} 的粒子，
     * 如 {@link com.pasterdream.pasterdreammod.client.particle.LifeCrystalParticle.Provider}。
     * <p>
     * 需要在 {@link RegisterParticleProvidersEvent} 中调用。
     * <p>
     * 使用示例：
     * <pre>{@code
     * @SubscribeEvent
     * public static void registerParticles(RegisterParticleProvidersEvent event) {
     *     ParticleAPI.registerProviderSprite(event, "meltdream_crystal_particle",
     *             LifeCrystalParticle.Provider::new);
     * }
     * }</pre>
     *
     * @param event           {@link RegisterParticleProvidersEvent}
     * @param particleName    粒子注册名称（与 {@link #createParticle} 传入的名称一致）
     * @param providerFactory 精灵表到 Provider 的工厂函数（{@link net.minecraft.client.particle.ParticleEngine.SpriteParticleRegistration}）
     */
    public static void registerProviderSprite(RegisterParticleProvidersEvent event,
                                               String particleName,
                                               ParticleEngine.SpriteParticleRegistration<SimpleParticleType> providerFactory) {
        ParticleResult result = REGISTERED_PARTICLES.get(particleName);
        if (result == null) {
            PasterDreamMod.LOGGER.warn("[ParticleAPI] ⚠️ 未找到粒子 [{}] 的注册记录，跳过 Provider 注册", particleName);
            return;
        }

        SimpleParticleType type = (SimpleParticleType) result.particleType();
        event.registerSpriteSet(type, providerFactory);
        PasterDreamMod.LOGGER.info("[ParticleAPI] ✅ 已注册粒子 Provider: {} | type={}", particleName, type);
    }

    // ======================== 查询方法 ========================

    /**
     * 获取已注册的粒子结果
     *
     * @param particleName 粒子注册名称
     * @return {@link ParticleResult}，如果未找到返回 null
     */
    public static ParticleResult getParticle(String particleName) {
        ParticleResult result = REGISTERED_PARTICLES.get(particleName);
        PasterDreamMod.LOGGER.debug("[ParticleAPI] 🔍 查询粒子: {} → {}", particleName, result != null ? "已找到" : "未找到");
        return result;
    }

    /**
     * 获取已注册的粒子类型
     * <p>
     * 便捷方法，直接返回 ParticleType 引用。
     *
     * @param particleName 粒子注册名称
     * @return {@link ParticleType}，如果未找到返回 null
     */
    public static ParticleType<?> getParticleType(String particleName) {
        ParticleResult result = REGISTERED_PARTICLES.get(particleName);
        ParticleType<?> type = result != null ? result.particleType() : null;
        PasterDreamMod.LOGGER.debug("[ParticleAPI] 🔍 查询粒子类型: {} → {}", particleName, type != null ? type : "null");
        return type;
    }

    /**
     * 获取粒子类型的 Supplier
     * <p>
     * 适用于需要在注册阶段引用粒子类型的场景。
     *
     * @param particleName 粒子注册名称
     * @return 粒子类型的 Supplier，如果未找到返回 null
     */
    public static Supplier<ParticleType<?>> getParticleSupplier(String particleName) {
        ParticleResult result = REGISTERED_PARTICLES.get(particleName);
        if (result != null) {
            PasterDreamMod.LOGGER.debug("[ParticleAPI] 🔍 查询粒子 Supplier: {}", particleName);
            return result::particleType;
        }
        PasterDreamMod.LOGGER.debug("[ParticleAPI] 🔍 查询粒子 Supplier: {} → null（未找到）", particleName);
        return null;
    }

    /**
     * 获取所有已注册粒子的不可变视图
     *
     * @return 粒子注册名到结果的映射
     */
    public static Map<String, ParticleResult> getRegisteredParticles() {
        int count = REGISTERED_PARTICLES.size();
        PasterDreamMod.LOGGER.debug("[ParticleAPI] 📊 获取所有已注册粒子: 共 {} 个", count);
        return Collections.unmodifiableMap(REGISTERED_PARTICLES);
    }

    // ======================== 内部方法 ========================

    /**
     * 缓存粒子注册结果（内部使用）
     *
     * @param result 粒子注册结果
     */
    public static void cacheParticle(ParticleResult result) {
        REGISTERED_PARTICLES.put(result.name(), result);
        int total = REGISTERED_PARTICLES.size();
        PasterDreamMod.LOGGER.info("[ParticleAPI] 📦 已缓存粒子: {} | 缓存总数: {} | holder={}", result.name(), total, result.holder());
    }
}