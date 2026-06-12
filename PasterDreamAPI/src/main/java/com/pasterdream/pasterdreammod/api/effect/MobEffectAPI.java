package com.pasterdream.pasterdreammod.api.effect;

import com.pasterdream.pasterdreammod.api.PasterDreamAPI;
import com.pasterdream.pasterdreammod.api.effect.base.PasterDreamEffect;
import com.pasterdream.pasterdreammod.api.effect.builder.MobEffectBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 药水效果注册 API —— 将繁琐的药水效果注册集中管理，提供高效简洁的注册方式
 * <p>
 * 采用 Facade 模式 + Builder 模式设计，与 {@link com.pasterdream.pasterdreammod.api.block.BlockAPI}
 * {@link com.pasterdream.pasterdreammod.api.entity.EntityAPI} 风格一致，
 * 覆盖药水效果注册的完整流程：
 * <ul>
 *   <li><b>效果类型注册</b>：通过 Builder 链式配置效果属性并注册到 DeferredRegister</li>
 *   <li><b>着色器支持</b>：自定义屏幕后期着色器效果</li>
 *   <li><b>粒子联动</b>：与 ParticleAPI 联动，效果作用时生成自定义粒子</li>
 *   <li><b>回调系统</b>：应用/移除时执行自定义逻辑</li>
 *   <li><b>叠加交互</b>：效果叠加时自定义行为（延长、升级等）</li>
 *   <li><b>查询管理</b>：缓存已注册的效果结果，方便后续查询</li>
 * </ul>
 * <p>
 * 注意：此类不包含任何客户端专属类型引用，确保服务端兼容。
 * <p>
 * 使用示例：
 * <pre>{@code
 * // ====== 在 PDEffects 或任意注册类中 ======
 * MobEffectResult dreamwish = MobEffectAPI.createEffect("dreamwish_buff")
 *     .beneficial()
 *     .color(0xFF69B4)
 *     .shaderTexture(new ResourceLocation("pasterdream", "shaders/post/dreamwish.json"))
 *     .particleType(ParticleTypes.END_ROD)
 *     .onApply((entity, amp) -> entity.heal(5))
 *     .build();
 *
 * // ====== 在其他地方获取效果引用 ======
 * MobEffectResult result = MobEffectAPI.getEffect("dreamwish_buff");
 * if (result != null) {
 *     MobEffect effect = result.effect();
 *     // 应用效果...
 * }
 *
 * // ====== 获取所有已注册效果 ======
 * Map<String, MobEffectResult> all = MobEffectAPI.getRegisteredEffects();
 * }</pre>
 *
 * @see com.pasterdream.pasterdreammod.api.effect.builder.MobEffectBuilder
 * @see com.pasterdream.pasterdreammod.api.effect.MobEffectResult
 * @see com.pasterdream.pasterdreammod.api.effect.base.PasterDreamEffect
 */
public final class MobEffectAPI {

    /**
     * API 专属的药水效果注册器
     * <p>
     * 注意：需要在 {@code PasterDreamMod} 构造函数中注册到事件总线：
     * <pre>{@code
     * MobEffectAPI.REGISTRY.register(modEventBus);
     * }</pre>
     */
    public static final DeferredRegister<MobEffect> REGISTRY =
            DeferredRegister.create(Registries.MOB_EFFECT, PasterDreamAPI.MOD_ID);

    /** 已注册的效果结果缓存 */
    private static final Map<String, MobEffectResult> REGISTERED_EFFECTS = new HashMap<>();

    private MobEffectAPI() {
        throw new UnsupportedOperationException("MobEffectAPI 是纯静态门面类，不可实例化");
    }

    // ======================== Builder 工厂方法 ========================

    /**
     * 创建一个药水效果构建器
     * <p>
     * 采用链式调用配置效果属性，
     * 最终通过 {@link MobEffectBuilder#build()} 完成注册并返回 {@link MobEffectResult}。
     *
     * @param effectName 效果注册名称（snake_case 格式，如 "dreamwish_buff"）
     * @return {@link MobEffectBuilder} 实例
     */
    public static MobEffectBuilder createEffect(String effectName) {
        PasterDreamAPI.LOGGER.info("[MobEffectAPI] 开始创建效果构建器: {}", effectName);
        return new MobEffectBuilder(effectName);
    }

    // ======================== 查询方法 ========================

    /**
     * 获取已注册的效果结果
     *
     * @param effectName 效果注册名称
     * @return {@link MobEffectResult}，如果未找到返回 null
     */
    public static MobEffectResult getEffect(String effectName) {
        MobEffectResult result = REGISTERED_EFFECTS.get(effectName);
        PasterDreamAPI.LOGGER.debug("[MobEffectAPI] 🔍 查询效果: {} → {}", effectName, result != null ? "已找到" : "未找到");
        return result;
    }

    /**
     * 获取已注册的效果类型
     * <p>
     * 便捷方法，直接返回 MobEffect 引用。
     *
     * @param effectName 效果注册名称
     * @return {@link MobEffect}，如果未找到返回 null
     */
    public static MobEffect getEffectType(String effectName) {
        MobEffectResult result = REGISTERED_EFFECTS.get(effectName);
        MobEffect effect = result != null ? result.effect() : null;
        PasterDreamAPI.LOGGER.debug("[MobEffectAPI] 🔍 查询效果类型: {} → {}", effectName, effect != null ? effect : "null");
        return effect;
    }

    /**
     * 获取效果类型的 Supplier
     *
     * @param effectName 效果注册名称
     * @return 效果类型的 Supplier，如果未找到返回 null
     */
    public static Supplier<MobEffect> getEffectSupplier(String effectName) {
        MobEffectResult result = REGISTERED_EFFECTS.get(effectName);
        if (result != null) {
            PasterDreamAPI.LOGGER.debug("[MobEffectAPI] 🔍 查询效果 Supplier: {}", effectName);
            return result::effect;
        }
        PasterDreamAPI.LOGGER.debug("[MobEffectAPI] 🔍 查询效果 Supplier: {} → null（未找到）", effectName);
        return null;
    }

    /**
     * 获取效果作为 PasterDreamEffect 的强类型引用
     * <p>
     * 用于访问自定义效果配置（着色器、粒子、回调等）。
     *
     * @param effectName 效果注册名称
     * @return {@link PasterDreamEffect}，如果未找到或不是自定义效果返回 null
     */
    public static PasterDreamEffect getPasterDreamEffect(String effectName) {
        MobEffectResult result = REGISTERED_EFFECTS.get(effectName);
        if (result != null) {
            PasterDreamEffect pde = result.asPasterDreamEffect();
            PasterDreamAPI.LOGGER.debug("[MobEffectAPI] 🔍 查询 PasterDreamEffect: {} → {}", effectName, pde != null ? "是自定义效果" : "不是自定义效果");
            return pde;
        }
        PasterDreamAPI.LOGGER.debug("[MobEffectAPI] 🔍 查询 PasterDreamEffect: {} → null（未找到）", effectName);
        return null;
    }

    /**
     * 获取所有已注册效果的不可变视图
     *
     * @return 效果注册名到结果的映射
     */
    public static Map<String, MobEffectResult> getRegisteredEffects() {
        int count = REGISTERED_EFFECTS.size();
        PasterDreamAPI.LOGGER.debug("[MobEffectAPI] 📊 获取所有已注册效果: 共 {} 个", count);
        return Collections.unmodifiableMap(REGISTERED_EFFECTS);
    }

    // ======================== 内部方法 ========================

    /**
     * 缓存效果注册结果（内部使用）
     *
     * @param result 效果注册结果
     */
    public static void cacheEffect(MobEffectResult result) {
        REGISTERED_EFFECTS.put(result.name(), result);
        int total = REGISTERED_EFFECTS.size();
        PasterDreamAPI.LOGGER.info("[MobEffectAPI] 📦 已缓存效果: {} | 缓存总数: {} | holder={}",
                result.name(), total, result.holder());
    }
}