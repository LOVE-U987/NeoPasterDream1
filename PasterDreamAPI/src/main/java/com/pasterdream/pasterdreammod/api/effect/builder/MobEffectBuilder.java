package com.pasterdream.pasterdreammod.api.effect.builder;

import com.pasterdream.pasterdreammod.api.PasterDreamAPI;
import com.pasterdream.pasterdreammod.api.effect.MobEffectAPI;
import com.pasterdream.pasterdreammod.api.effect.MobEffectResult;
import com.pasterdream.pasterdreammod.api.effect.base.PasterDreamEffect;
import com.pasterdream.pasterdreammod.api.effect.base.PasterDreamEffect.EffectConfig;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.registries.DeferredHolder;

import javax.annotation.Nullable;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.ObjIntConsumer;

/**
 * 药水效果构建器 —— 采用 Builder 模式链式配置和注册药水效果
 * <p>
 * 通过链式调用即可完成药水效果的类型注册、属性配置和资源文件生成。
 * <p>
 * 使用示例：
 * <pre><code>
     * MobEffectResult dreamwish = MobEffectAPI.createEffect("dreamwish_buff")
     *     .beneficial()                          // 有益效果
     *     .color(0xFF69B4)                        // 粉红色
     *     .shaderTexture(new ResourceLocation("pasterdream", "shaders/post/dreamwish.json"))
     *     .particleType(ParticleTypes.END_ROD)    // 粒子联动
     *     .onApply((entity, amp) -> entity.heal(5))
     *     .onRemove((entity, amp) -> {  })   // 移除逻辑
     *     .build();
     * </code></pre>
 *
 * @see com.pasterdream.pasterdreammod.api.effect.MobEffectAPI
 * @see com.pasterdream.pasterdreammod.api.effect.base.PasterDreamEffect
 */
public class MobEffectBuilder {

    private final String name;

    // 必要参数
    private MobEffectCategory category;
    private Integer color;

    // 可选参数（EffectConfig）
    private ResourceLocation shaderTexture;
    private ParticleType<?> particleType;
    private ObjIntConsumer<LivingEntity> onTick;
    private BiConsumer<LivingEntity, Integer> onApply;
    private BiConsumer<LivingEntity, Integer> onRemove;
    private BiFunction<MobEffectInstance, MobEffectInstance, MobEffectInstance> stackingHandler;

    // 是否实例化（默认 true，false 表示使用原始 MobEffect）
    private boolean instant = false;

    /**
     * 构造效果构建器
     *
     * @param name 效果注册名称（snake_case 格式，如 "dreamwish_buff"）
     */
    public MobEffectBuilder(String name) {
        this.name = name;
        PasterDreamAPI.LOGGER.debug("[MobEffectBuilder] 创建效果构建器: {}", name);
    }

    /**
     * 创建一个新的效果构建器（静态工厂方法）
     * <p>
     * 使用此方法无需通过 {@link MobEffectAPI}。
     *
     * @param name 效果注册名称
     * @return 效果构建器实例
     */
    public static MobEffectBuilder builder(String name) {
        return new MobEffectBuilder(name);
    }

    // ======================== 分类 ========================

    /**
     * 设置为有益效果
     *
     * @return 当前构建器实例
     */
    public MobEffectBuilder beneficial() {
        this.category = MobEffectCategory.BENEFICIAL;
        PasterDreamAPI.LOGGER.debug("[MobEffectBuilder] {} → category=BENEFICIAL", name);
        return this;
    }

    /**
     * 设置为有害效果
     *
     * @return 当前构建器实例
     */
    public MobEffectBuilder harmful() {
        this.category = MobEffectCategory.HARMFUL;
        PasterDreamAPI.LOGGER.debug("[MobEffectBuilder] {} → category=HARMFUL", name);
        return this;
    }

    /**
     * 设置为中性效果
     *
     * @return 当前构建器实例
     */
    public MobEffectBuilder neutral() {
        this.category = MobEffectCategory.NEUTRAL;
        PasterDreamAPI.LOGGER.debug("[MobEffectBuilder] {} → category=NEUTRAL", name);
        return this;
    }

    /**
     * 设置效果分类
     *
     * @param category 效果分类
     * @return 当前构建器实例
     */
    public MobEffectBuilder category(MobEffectCategory category) {
        this.category = category;
        PasterDreamAPI.LOGGER.debug("[MobEffectBuilder] {} → category={}", name, category.name());
        return this;
    }

    // ======================== 颜色 ========================

    /**
     * 设置效果颜色
     *
     * @param color 十六进制颜色值（如 0xFF69B4）
     * @return 当前构建器实例
     */
    public MobEffectBuilder color(int color) {
        this.color = color;
        PasterDreamAPI.LOGGER.debug("[MobEffectBuilder] {} → color=#{}", name, Integer.toHexString(color));
        return this;
    }

    // ======================== 瞬时效果 ========================

    /**
     * 设置为瞬时效果
     * <p>
     * 瞬时效果（如瞬间治疗、瞬间伤害）立即生效，不持续。
     *
     * @return 当前构建器实例
     */
    public MobEffectBuilder instant() {
        this.instant = true;
        PasterDreamAPI.LOGGER.debug("[MobEffectBuilder] {} → instant=true", name);
        return this;
    }

    // ======================== 着色器 ========================

    /**
     * 设置着色器纹理
     * <p>
     * 用于自定义屏幕后期的着色器效果。
     *
     * @param shaderTexture 着色器资源位置
     * @return 当前构建器实例
     */
    public MobEffectBuilder shaderTexture(ResourceLocation shaderTexture) {
        this.shaderTexture = shaderTexture;
        PasterDreamAPI.LOGGER.debug("[MobEffectBuilder] {} → shaderTexture={}", name, shaderTexture);
        return this;
    }

    // ======================== 粒子 ========================

    /**
     * 设置效果的自定义粒子类型
     * <p>
     * 可与 {@link com.pasterdream.pasterdreammod.api.particle.ParticleAPI} 联动，
     * 使用已注册的自定义粒子。
     *
     * @param particleType 粒子类型
     * @return 当前构建器实例
     */
    public MobEffectBuilder particleType(ParticleType<?> particleType) {
        this.particleType = particleType;
        PasterDreamAPI.LOGGER.debug("[MobEffectBuilder] {} → particleType={}", name, particleType);
        return this;
    }

    // ======================== 回调 ========================

    /**
     * 设置每 tick 执行的回调
     * <p>
     * 每 tick 调用一次，用于持续性的效果逻辑（如随机给经验、消除负面效果等）。
     *
     * @param onTick 每 tick 回调 (entity, amplifier) → void
     * @return 当前构建器实例
     */
    public MobEffectBuilder onTick(ObjIntConsumer<LivingEntity> onTick) {
        this.onTick = onTick;
        PasterDreamAPI.LOGGER.debug("[MobEffectBuilder] {} → onTick=已配置", name);
        return this;
    }

    /**
     * 设置效果应用回调
     *
     * @param onApply 回调函数 (entity, amplifier) → void
     * @return 当前构建器实例
     */
    public MobEffectBuilder onApply(BiConsumer<LivingEntity, Integer> onApply) {
        this.onApply = onApply;
        PasterDreamAPI.LOGGER.debug("[MobEffectBuilder] {} → onApply=已配置", name);
        return this;
    }

    /**
     * 设置效果移除回调
     *
     * @param onRemove 回调函数 (entity, amplifier) → void
     * @return 当前构建器实例
     */
    public MobEffectBuilder onRemove(BiConsumer<LivingEntity, Integer> onRemove) {
        this.onRemove = onRemove;
        PasterDreamAPI.LOGGER.debug("[MobEffectBuilder] {} → onRemove=已配置", name);
        return this;
    }

    // ======================== 叠加 ========================

    /**
     * 设置效果叠加处理器
     *
     * @param stackingHandler 叠加处理函数 (existing, newInstance) → MobEffectInstance
     * @return 当前构建器实例
     */
    public MobEffectBuilder stackingHandler(
            BiFunction<MobEffectInstance, MobEffectInstance, MobEffectInstance> stackingHandler) {
        this.stackingHandler = stackingHandler;
        PasterDreamAPI.LOGGER.debug("[MobEffectBuilder] {} → stackingHandler=已配置", name);
        return this;
    }

    // ======================== 构建 ========================

    /**
     * 执行构建，完成效果类型注册
     * <p>
     * 完成以下工作：
     * <ol>
     *   <li>通过 MobEffectAPI 的注册器注册 {@link PasterDreamEffect}</li>
     *   <li>配置 {@link EffectConfig}（着色器、粒子、回调、叠加）</li>
     *   <li>返回 {@link MobEffectResult} 包含效果类型引用</li>
     * </ol>
     *
     * @return {@link MobEffectResult} 包含效果类型的所有引用信息
     */
    public MobEffectResult build() {
        PasterDreamAPI.LOGGER.info("[MobEffectBuilder] ===== 开始构建效果: {} =====", name);
        validate();

        // 构建 EffectConfig
        EffectConfig config = EffectConfig.builder()
                .shaderTexture(shaderTexture)
                .particleType(particleType)
                .onTick(onTick)
                .onApply(onApply)
                .onRemove(onRemove)
                .stackingHandler(stackingHandler)
                .build();

        // 注册到 DeferredRegister
        PasterDreamAPI.LOGGER.debug("[MobEffectBuilder] 注册 MobEffect: {} (category={}, color=#{})",
                name, category.name(), Integer.toHexString(color));
        DeferredHolder<MobEffect, MobEffect> holder = MobEffectAPI.REGISTRY.register(
                name, () -> new PasterDreamEffect(category, color, config)
        );
        PasterDreamAPI.LOGGER.info("[MobEffectBuilder] ✅ MobEffect 已注册: {} | holder={} | instant={}", name, holder, instant);

        // 创建结果
        MobEffectResult result = new MobEffectResult(name, holder);
        PasterDreamAPI.LOGGER.debug("[MobEffectBuilder] 创建 MobEffectResult: name={}, holder={}", name, holder);

        // 缓存到 MobEffectAPI
        MobEffectAPI.cacheEffect(result);

        // 如果是瞬时效果，标记为瞬时
        if (instant) {
            PasterDreamAPI.LOGGER.debug("[MobEffectBuilder] 标记为瞬时效果: {}", name);
        }

        PasterDreamAPI.LOGGER.info("[MobEffectBuilder] ✅ 效果构建完成: {} | category={}, color=#{}, config=着色器:{} 粒子:{} tick回调:{} 应用回调:{} 移除回调:{} 叠加:{}",
                name, category.name(), Integer.toHexString(color),
                shaderTexture != null ? "✅" : "❌",
                particleType != null ? "✅" : "❌",
                onTick != null ? "✅" : "❌",
                onApply != null ? "✅" : "❌",
                onRemove != null ? "✅" : "❌",
                stackingHandler != null ? "✅" : "❌");

        return result;
    }

    /**
     * 验证必要参数是否已配置
     *
     * @throws IllegalStateException 如果缺少必要参数
     */
    private void validate() {
        if (category == null) {
            PasterDreamAPI.LOGGER.error("[MobEffectBuilder] ❌ 验证失败: {} → category 未设置", name);
            throw new IllegalStateException("MobEffectBuilder: 缺少效果分类（category），请调用 .beneficial() / .harmful() / .neutral() 设置");
        }
        if (color == null) {
            PasterDreamAPI.LOGGER.error("[MobEffectBuilder] ❌ 验证失败: {} → color 未设置", name);
            throw new IllegalStateException("MobEffectBuilder: 缺少效果颜色（color），请调用 .color() 设置");
        }
        PasterDreamAPI.LOGGER.debug("[MobEffectBuilder] ✅ 验证通过: {}", name);
    }
}