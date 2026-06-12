package com.pasterdream.pasterdreammod.api.effect.base;

import com.pasterdream.pasterdreammod.api.PasterDreamAPI;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.ObjIntConsumer;

/**
 * PasterDream 自定义药水效果基类
 * <p>
 * 扩展原版 {@link MobEffect}，支持：
 * <ul>
 *   <li><b>着色器覆盖</b>：自定义屏幕着色器纹理</li>
 *   <li><b>粒子联动</b>：与 ParticleAPI 联动的自定义粒子效果</li>
 *   <li><b>应用回调</b>：效果被应用时执行自定义逻辑</li>
 *   <li><b>移除回调</b>：效果被移除时执行自定义逻辑</li>
 *   <li><b>叠加交互</b>：效果叠加时自定义行为</li>
 * </ul>
 * <p>
 * 使用示例：
 * <pre>{@code
 * // 直接构造（一般来说通过 MobEffectBuilder 构建）
 * PasterDreamEffect effect = new PasterDreamEffect(
 *     MobEffectCategory.BENEFICIAL, 0xFF69B4,
 *     PasterDreamEffect.EffectConfig.builder()
 *         .shaderTexture(new ResourceLocation("pasterdream", "shaders/post/dreamwish.json"))
 *         .particleType(ParticleTypes.END_ROD)
 *         .onApply((entity, amp) -> entity.heal(5))
 *         .onRemove((entity, amp) -> entity.hurt(entity.damageSources().magic(), 2))
 *         .stackingHandler((existing, newInstance) -> {
 *             // 叠加时延长持续时间
 *             int totalDuration = existing.getDuration() + newInstance.getDuration();
 *             existing.duration = Math.min(totalDuration, 6000);
 *             return existing;
 *         })
 *         .build()
 * ) {};
 * }</pre>
 *
 * @see com.pasterdream.pasterdreammod.api.effect.builder.MobEffectBuilder
 * @see com.pasterdream.pasterdreammod.api.effect.MobEffectAPI
 */
public class PasterDreamEffect extends MobEffect {

    /** 效果配置 */
    private final EffectConfig config;

    /**
     * 构造 PasterDream 自定义效果
     *
     * @param category       效果分类（BENEFICIAL / HARMFUL / NEUTRAL）
     * @param color          效果颜色（十六进制，如 0xFF69B4）
     * @param config         效果配置（包含着色器、粒子、回调等）
     */
    public PasterDreamEffect(MobEffectCategory category, int color, EffectConfig config) {
        super(category, color);
        this.config = config != null ? config : EffectConfig.DEFAULT;
        PasterDreamAPI.LOGGER.debug("[PasterDreamEffect] 创建效果: category={}, color=#{}", category.name(), Integer.toHexString(color));
    }

    /**
     * 获取效果配置
     *
     * @return 效果配置
     */
    public EffectConfig getConfig() {
        return config;
    }

    // ======================== 着色器 ========================

    /**
     * 获取着色器纹理资源位置
     * <p>
     * 返回 {@code null} 表示不使用自定义着色器。
     *
     * @return 着色器资源位置，未配置返回 null
     */
    @Nullable
    public ResourceLocation getShaderTexture() {
        return config.shaderTexture;
    }

    // ======================== 粒子 ========================

    /**
     * 获取效果的自定义粒子类型
     *
     * @return 粒子类型，未配置返回 null
     */
    @Nullable
    public ParticleType<?> getEffectParticleType() {
        return config.particleType;
    }

    /**
     * 生成效果粒子
     * <p>
     * 每 tick 在实体周围生成配置的粒子效果。
     * 可通过重写此方法自定义粒子生成逻辑。
     *
     * @param entity     受影响的实体
     * @param amplifier  效果等级
     */
    public void spawnEffectParticles(LivingEntity entity, int amplifier) {
        if (config.particleType == null) return;
        if (!(entity.level() instanceof ServerLevel serverLevel)) return;

        double x = entity.getX() + (entity.getRandom().nextDouble() - 0.5) * entity.getBbWidth() * 1.5;
        double y = entity.getY() + entity.getRandom().nextDouble() * entity.getBbHeight();
        double z = entity.getZ() + (entity.getRandom().nextDouble() - 0.5) * entity.getBbWidth() * 1.5;

        serverLevel.sendParticles(
                (ParticleOptions) config.particleType,
                x, y, z,
                1 + amplifier,  // 等级越高粒子越多
                0.1, 0.1, 0.1, 0.01
        );
    }

    // ======================== 回调 ========================

    /**
     * 效果被应用时调用
     * <p>
     * 执行配置的应用回调 {@link EffectConfig#onApply}。
     *
     * @param entity    受影响的实体
     * @param amplifier 效果等级
     */
    public void onApply(LivingEntity entity, int amplifier) {
        PasterDreamAPI.LOGGER.debug("[PasterDreamEffect] onApply: effect={}, entity={}, amplifier={}",
                getDescriptionId(), entity, amplifier);
        if (config.onApply != null) {
            config.onApply.accept(entity, amplifier);
        }
    }

    /**
     * 效果被移除时调用
     * <p>
     * 执行配置的移除回调 {@link EffectConfig#onRemove}。
     *
     * @param entity    受影响的实体
     * @param amplifier 效果等级
     */
    public void onRemove(LivingEntity entity, int amplifier) {
        PasterDreamAPI.LOGGER.debug("[PasterDreamEffect] onRemove: effect={}, entity={}, amplifier={}",
                getDescriptionId(), entity, amplifier);
        if (config.onRemove != null) {
            config.onRemove.accept(entity, amplifier);
        }
    }

    // ======================== 叠加处理 ========================

    /**
     * 处理效果叠加
     * <p>
     * 默认行为：新的实例替换旧的实例（原版行为）。
     * 可通过配置 {@link EffectConfig#stackingHandler} 自定义叠加逻辑。
     * <p>
     * 自定义叠加逻辑示例：
     * <ul>
     *   <li>延长持续时间：新旧实例时间相加</li>
     *   <li>提升等级：效果等级 +1，持续时间重置</li>
     *   <li>合并：取较高等级和较长时间</li>
     * </ul>
     *
     * @param existing     已存在的效果实例
     * @param newInstance  新应用的效果实例
     * @return 处理后的效果实例
     */
    public MobEffectInstance handleStacking(MobEffectInstance existing, MobEffectInstance newInstance) {
        if (config.stackingHandler != null) {
            return config.stackingHandler.apply(existing, newInstance);
        }
        // 默认：新实例覆盖旧实例（原版行为）
        PasterDreamAPI.LOGGER.debug("[PasterDreamEffect] handleStacking: 使用默认覆盖行为");
        return newInstance;
    }

    // ======================== 每 tick 效果 ========================

    /**
     * 每 tick 执行效果逻辑
     * <p>
     * 原版的 {@link #applyEffectTick(LivingEntity, int)} 会在此调用。
     * 扩展了粒子生成逻辑。
     *
     * @param entity    受影响的实体
     * @param amplifier 效果等级
     */
    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        // 生成粒子效果
        if (config.particleType != null) {
            spawnEffectParticles(entity, amplifier);
        }
        // 执行每 tick 回调
        if (config.onTick != null) {
            config.onTick.accept(entity, amplifier);
        }
        return true;
    }

    /**
     * 判断是否为每 tick 执行的效果
     * <p>
     * 默认为 true，子类可重写为 false 以实现非持续效果。
     */
    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    // ======================== 效果配置 ========================

    /**
     * 效果配置 —— 封装 PasterDreamEffect 的所有可选配置
     * <p>
     * 使用 {@link #builder()} 创建：
     * <pre><code>
     * EffectConfig config = EffectConfig.builder()
     *     .shaderTexture(new ResourceLocation("pasterdream", "shaders/post/dreamwish.json"))
     *     .particleType(ParticleTypes.END_ROD)
     *     .onApply((entity, amp) -> entity.heal(5))
     *     .onRemove((entity, amp) -> {  })   // 清理逻辑
     *     .stackingHandler((existing, newInstance) -> {
     *         existing.duration += newInstance.duration;
     *         return existing;
     *     })
     *     .build();
     * </code></pre>
     */
    public static final class EffectConfig {
        /** 默认配置（无着色器、无粒子、无回调） */
        public static final EffectConfig DEFAULT = builder().build();

        @Nullable
        private final ResourceLocation shaderTexture;

        @Nullable
        private final ParticleType<?> particleType;

        @Nullable
        private final ObjIntConsumer<LivingEntity> onTick;

        @Nullable
        private final BiConsumer<LivingEntity, Integer> onApply;

        @Nullable
        private final BiConsumer<LivingEntity, Integer> onRemove;

        @Nullable
        private final BiFunction<MobEffectInstance, MobEffectInstance, MobEffectInstance> stackingHandler;

        private EffectConfig(
                @Nullable ResourceLocation shaderTexture,
                @Nullable ParticleType<?> particleType,
                @Nullable ObjIntConsumer<LivingEntity> onTick,
                @Nullable BiConsumer<LivingEntity, Integer> onApply,
                @Nullable BiConsumer<LivingEntity, Integer> onRemove,
                @Nullable BiFunction<MobEffectInstance, MobEffectInstance, MobEffectInstance> stackingHandler
        ) {
            this.shaderTexture = shaderTexture;
            this.particleType = particleType;
            this.onTick = onTick;
            this.onApply = onApply;
            this.onRemove = onRemove;
            this.stackingHandler = stackingHandler;
        }

        /**
         * 创建配置构建器
         *
         * @return 配置构建器
         */
        public static Builder builder() {
            return new Builder();
        }

        // ======================== Getters ========================

        @Nullable
        public ResourceLocation getShaderTexture() { return shaderTexture; }

        @Nullable
        public ParticleType<?> getParticleType() { return particleType; }

        @Nullable
        public ObjIntConsumer<LivingEntity> getOnTick() { return onTick; }

        @Nullable
        public BiConsumer<LivingEntity, Integer> getOnApply() { return onApply; }

        @Nullable
        public BiConsumer<LivingEntity, Integer> getOnRemove() { return onRemove; }

        @Nullable
        public BiFunction<MobEffectInstance, MobEffectInstance, MobEffectInstance> getStackingHandler() { return stackingHandler; }

        /**
         * 配置构建器
         */
        public static final class Builder {
            private ResourceLocation shaderTexture;
            private ParticleType<?> particleType;
            private ObjIntConsumer<LivingEntity> onTick;
            private BiConsumer<LivingEntity, Integer> onApply;
            private BiConsumer<LivingEntity, Integer> onRemove;
            private BiFunction<MobEffectInstance, MobEffectInstance, MobEffectInstance> stackingHandler;

            private Builder() {}

            /**
             * 设置着色器纹理
             *
             * @param shaderTexture 着色器资源位置
             * @return 当前构建器
             */
            public Builder shaderTexture(ResourceLocation shaderTexture) {
                this.shaderTexture = shaderTexture;
                return this;
            }

            /**
             * 设置效果的自定义粒子类型
             *
             * @param particleType 粒子类型
             * @return 当前构建器
             */
            public Builder particleType(ParticleType<?> particleType) {
                this.particleType = particleType;
                return this;
            }

            /**
             * 设置每 tick 执行的回调
             * <p>
             * 每 tick 调用一次，用于持续性的效果逻辑（如随机给经验、消除负面效果等）。
             *
             * @param onTick 每 tick 回调 (entity, amplifier) → void
             * @return 当前构建器
             */
            public Builder onTick(ObjIntConsumer<LivingEntity> onTick) {
                this.onTick = onTick;
                return this;
            }

            /**
             * 设置效果应用回调
             *
             * @param onApply 回调函数 (entity, amplifier) → void
             * @return 当前构建器
             */
            public Builder onApply(BiConsumer<LivingEntity, Integer> onApply) {
                this.onApply = onApply;
                return this;
            }

            /**
             * 设置效果移除回调
             *
             * @param onRemove 回调函数 (entity, amplifier) → void
             * @return 当前构建器
             */
            public Builder onRemove(BiConsumer<LivingEntity, Integer> onRemove) {
                this.onRemove = onRemove;
                return this;
            }

            /**
             * 设置效果叠加处理器
             *
             * @param stackingHandler 叠加处理函数 (existing, newInstance) → MobEffectInstance
             * @return 当前构建器
             */
            public Builder stackingHandler(BiFunction<MobEffectInstance, MobEffectInstance, MobEffectInstance> stackingHandler) {
                this.stackingHandler = stackingHandler;
                return this;
            }

            /**
             * 构建配置
             *
             * @return 效果配置实例
             */
            public EffectConfig build() {
                return new EffectConfig(shaderTexture, particleType, onTick, onApply, onRemove, stackingHandler);
            }
        }
    }
}