package com.pasterdream.pasterdreammod.api.effect.particle;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

/**
 * 粒子发射器处理器 —— 控制发射器与单个粒子的行为
 * <p>
 * 借鉴开源模组 FDLib（作者 FINDERFEED）的 {@code EmitterProcessor} 设计思路
 * （独立实现，非复制）。处理器在发射器生命周期各阶段被调用：
 * <ul>
 *   <li><b>initEmitter</b>：发射器创建时初始化（如预计算旋转矩阵）；</li>
 *   <li><b>tickEmitter</b>：发射器每 tick（如更新绑定实体的位置）；</li>
 *   <li><b>initParticle</b>：单个粒子生成时（如设置初始位置/速度）；</li>
 *   <li><b>tickParticle</b>：单个粒子每 tick（如改变颜色/轨迹）。</li>
 * </ul>
 * 实现类通常配合内部 {@code Type implements EmitterProcessorType} 提供网络编解码。
 *
 * @param <T> 处理器自身类型（用于泛型自引用）
 * @see EmitterProcessorType
 * @see EmitterProcessorRegistry
 */
public interface EmitterProcessor<T extends EmitterProcessor<T>> {

    /**
     * 发射器创建时初始化
     *
     * @param emitter 发射器实例
     */
    default void initEmitter(ParticleEmitterData emitter) {
    }

    /**
     * 发射器每 tick 回调
     *
     * @param emitter 发射器实例
     */
    default void tickEmitter(ParticleEmitterData emitter) {
    }

    /**
     * 单个粒子生成时初始化
     *
     * @param particle 客户端粒子（类型为 Minecraft 客户端 {@code Particle}，此处以
     *                 {@code Object} 承接以避免 API 服务端侧引用客户端符号）
     */
    default void initParticle(Object particle) {
    }

    /**
     * 单个粒子每 tick 回调
     *
     * @param particle 客户端粒子
     */
    default void tickParticle(Object particle) {
    }

    /**
     * 获取处理器类型
     *
     * @return 处理器类型（含编解码与 id）
     */
    EmitterProcessorType<T> type();

    /**
     * 多态处理器编解码器 —— 先写类型 id，再写类型私有数据
     * <p>
     * 编码：{@code ResourceLocation.STREAM_CODEC} 写 {@code type().id()}，
     * 再以 {@code type().codec()} 写处理器数据。
     * 解码：读 id → {@link EmitterProcessorRegistry} 反查类型 → 以该类型 codec 解码。
     */
    StreamCodec<FriendlyByteBuf, EmitterProcessor<?>> STREAM_CODEC =
            new StreamCodec<>() {
                @Override
                public EmitterProcessor<?> decode(FriendlyByteBuf buf) {
                    ResourceLocation id = ResourceLocation.STREAM_CODEC.decode(buf);
                    EmitterProcessorType<?> type = EmitterProcessorRegistry.get(id);
                    if (type == null) {
                        // 未知处理器类型：降级为空处理器，避免解码崩溃
                        return new com.pasterdream.pasterdreammod.api.effect.particle.processors.EmptyEmitterProcessor();
                    }
                    return decodeProcessor(type, buf);
                }

                @Override
                public void encode(FriendlyByteBuf buf, EmitterProcessor<?> processor) {
                    ResourceLocation.STREAM_CODEC.encode(buf, processor.type().id());
                    encodeProcessor(processor, buf);
                }

                @SuppressWarnings("unchecked")
                private <T extends EmitterProcessor<T>> EmitterProcessor<?> decodeProcessor(
                        EmitterProcessorType<T> type, FriendlyByteBuf buf) {
                    return type.codec().decode(buf);
                }

                @SuppressWarnings("unchecked")
                private <T extends EmitterProcessor<T>> void encodeProcessor(
                        EmitterProcessor<?> processor, FriendlyByteBuf buf) {
                    EmitterProcessorType<T> type = (EmitterProcessorType<T>) processor.type();
                    type.codec().encode(buf, (T) processor);
                }
            };
}
