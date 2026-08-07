package com.pasterdream.pasterdreammod.api.effect.particle;

import com.pasterdream.pasterdreammod.api.effect.particle.processors.EmptyEmitterProcessor;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 粒子发射器数据 —— 描述一次粒子发射的完整配置（数据驱动，可网络传输）
 * <p>
 * 借鉴开源模组 FDLib（作者 FINDERFEED）的 {@code ParticleEmitterData} 设计思路
 * （独立实现，非复制）。包含：
 * <ul>
 *   <li><b>position</b>：发射位置；</li>
 *   <li><b>lifetime</b>：发射器存活 tick 数；</li>
 *   <li><b>particlesPerTick</b>：每 tick 生成的粒子数；</li>
 *   <li><b>processor</b>：行为处理器（控制粒子生成位置/速度等）；</li>
 *   <li><b>particleTypes</b>：可生成的粒子类型池（每 tick 随机选一个）。</li>
 * </ul>
 * 通过 {@link #builder(ParticleOptions)} 链式构建；{@link #STREAM_CODEC}
 * 支持随网络包传输（服务端 → 客户端）。
 *
 * @see EmitterProcessor
 */
public final class ParticleEmitterData {

    /** 网络编解码器（RegistryFriendlyByteBuf：需注册表解析 ParticleOptions） */
    public static final StreamCodec<RegistryFriendlyByteBuf, ParticleEmitterData> STREAM_CODEC =
            StreamCodec.composite(
                    VectorStreamCodec.VEC3, v -> v.position,
                    ByteBufCodecs.INT, v -> v.lifetime,
                    ByteBufCodecs.INT, v -> v.particlesPerTick,
                    EmitterProcessor.STREAM_CODEC, v -> v.processor,
                    OptionsCodec.OPTIONS_CODEC, v -> v.particleTypes,
                    ParticleEmitterData::fromFields
            );

    private final Vec3 position;
    private final int lifetime;
    private final int particlesPerTick;
    private final EmitterProcessor<?> processor;
    private final List<ParticleOptions> particleTypes;

    private ParticleEmitterData(Vec3 position, int lifetime, int particlesPerTick,
                                EmitterProcessor<?> processor, List<ParticleOptions> particleTypes) {
        this.position = position;
        this.lifetime = lifetime;
        this.particlesPerTick = particlesPerTick;
        this.processor = processor;
        this.particleTypes = particleTypes;
    }

    /** StreamCodec 组合用的静态工厂（避免 record 构造器与 codec 泛型冲突） */
    private static ParticleEmitterData fromFields(Vec3 position, int lifetime, int particlesPerTick,
                                                  EmitterProcessor<?> processor, List<ParticleOptions> particleTypes) {
        return new ParticleEmitterData(position, lifetime, particlesPerTick, processor, particleTypes);
    }

    /**
     * 创建发射器数据构建器
     *
     * @param firstParticle 首个粒子类型（至少需要一个）
     * @return 构建器
     */
    public static Builder builder(ParticleOptions firstParticle) {
        return new Builder(firstParticle);
    }

    /**
     * 获取发射位置
     *
     * @return 世界坐标
     */
    public Vec3 position() {
        return position;
    }

    /**
     * 获取存活 tick 数
     *
     * @return 生命周期
     */
    public int lifetime() {
        return lifetime;
    }

    /**
     * 获取每 tick 粒子数
     *
     * @return 每 tick 生成量
     */
    public int particlesPerTick() {
        return particlesPerTick;
    }

    /**
     * 获取行为处理器
     *
     * @return 处理器（可空引用，用 {@link EmptyEmitterProcessor} 兜底）
     */
    public EmitterProcessor<?> processor() {
        return processor;
    }

    /**
     * 获取粒子类型池
     *
     * @return 粒子类型列表
     */
    public List<ParticleOptions> particleTypes() {
        return particleTypes;
    }

    /**
     * 随机选一个粒子类型（供客户端每 tick 生成）
     *
     * @param random 随机源
     * @return 选中的粒子类型
     */
    public ParticleOptions randomParticle(Random random) {
        if (particleTypes.isEmpty()) {
            throw new IllegalStateException("ParticleEmitterData 至少需要一个粒子类型");
        }
        return particleTypes.get(random.nextInt(particleTypes.size()));
    }

    /**
     * 粒子发射器数据构建器
     */
    public static class Builder {

        private Vec3 position = Vec3.ZERO;
        private int lifetime = 20;
        private int particlesPerTick = 1;
        private EmitterProcessor<?> processor = new EmptyEmitterProcessor();
        private final List<ParticleOptions> particleTypes;

        /**
         * 构造构建器
         *
         * @param firstParticle 首个粒子类型
         */
        public Builder(ParticleOptions firstParticle) {
            this.particleTypes = new ArrayList<>();
            this.particleTypes.add(firstParticle);
        }

        /**
         * 设置发射位置
         *
         * @param pos 世界坐标
         * @return 当前构建器
         */
        public Builder position(Vec3 pos) {
            this.position = pos;
            return this;
        }

        /**
         * 设置存活 tick 数
         *
         * @param lifetimeTicks 生命周期
         * @return 当前构建器
         */
        public Builder lifetime(int lifetimeTicks) {
            this.lifetime = lifetimeTicks;
            return this;
        }

        /**
         * 设置每 tick 粒子数
         *
         * @param amount 每 tick 生成量
         * @return 当前构建器
         */
        public Builder particlesPerTick(int amount) {
            this.particlesPerTick = amount;
            return this;
        }

        /**
         * 设置行为处理器
         *
         * @param processor 处理器
         * @return 当前构建器
         */
        public Builder processor(EmitterProcessor<?> processor) {
            this.processor = processor;
            return this;
        }

        /**
         * 追加一个粒子类型到池中
         *
         * @param options 粒子类型
         * @return 当前构建器
         */
        public Builder addParticle(ParticleOptions options) {
            this.particleTypes.add(options);
            return this;
        }

        /**
         * 构建发射器数据
         *
         * @return 不可变数据对象
         */
        public ParticleEmitterData build() {
            return new ParticleEmitterData(position, lifetime, particlesPerTick, processor,
                    List.copyOf(particleTypes));
        }
    }

    /** Vec3 的 StreamCodec */
    private static final class VectorStreamCodec {
        private static final StreamCodec<FriendlyByteBuf, Vec3> VEC3 = StreamCodec.composite(
                ByteBufCodecs.DOUBLE, v -> v.x,
                ByteBufCodecs.DOUBLE, v -> v.y,
                ByteBufCodecs.DOUBLE, v -> v.z,
                Vec3::new
        );
    }

    /** List&lt;ParticleOptions&gt; 的 StreamCodec（基于 BuiltInRegistries.PARTICLE_TYPE） */
    private static final class OptionsCodec {

        private static final StreamCodec<RegistryFriendlyByteBuf, List<ParticleOptions>> OPTIONS_CODEC =
                new StreamCodec<>() {
                    @Override
                    public List<ParticleOptions> decode(RegistryFriendlyByteBuf buf) {
                        int size = buf.readVarInt();
                        List<ParticleOptions> list = new ArrayList<>(size);
                        for (int i = 0; i < size; i++) {
                            int id = buf.readVarInt();
                            // 每项带数据长度前缀，未知类型也可安全跳过
                            int len = buf.readVarInt();
                            ParticleType<?> type = BuiltInRegistries.PARTICLE_TYPE.byId(id);
                            if (type == null) {
                                // 未知粒子类型：跳过其数据字节
                                buf.skipBytes(len);
                                continue;
                            }
                            try {
                                byte[] dataBytes = new byte[len];
                                buf.readBytes(dataBytes);
                                io.netty.buffer.ByteBuf slice = io.netty.buffer.Unpooled.wrappedBuffer(dataBytes);
                                try {
                                    RegistryFriendlyByteBuf sliceBuf = new RegistryFriendlyByteBuf(slice, buf.registryAccess());
                                    @SuppressWarnings("unchecked")
                                    ParticleOptions options = (ParticleOptions) type.streamCodec().decode(sliceBuf);
                                    list.add(options);
                                } finally {
                                    slice.release();
                                }
                            } catch (Exception e) {
                                // 单个粒子解码失败不影响整体
                            }
                        }
                        return list;
                    }

                    @Override
                    public void encode(RegistryFriendlyByteBuf buf, List<ParticleOptions> list) {
                        buf.writeVarInt(list.size());
                        for (ParticleOptions options : list) {
                            buf.writeVarInt(BuiltInRegistries.PARTICLE_TYPE.getId(options.getType()));
                            // 先把数据编码到临时 buffer，再写长度 + 数据
                            io.netty.buffer.ByteBuf tmp = io.netty.buffer.Unpooled.buffer();
                            try {
                                RegistryFriendlyByteBuf tmpBuf = new RegistryFriendlyByteBuf(tmp, buf.registryAccess());
                                @SuppressWarnings("unchecked")
                                StreamCodec<RegistryFriendlyByteBuf, ParticleOptions> codec =
                                        (StreamCodec<RegistryFriendlyByteBuf, ParticleOptions>) options.getType().streamCodec();
                                codec.encode(tmpBuf, options);
                                byte[] bytes = new byte[tmp.readableBytes()];
                                tmp.readBytes(bytes);
                                buf.writeVarInt(bytes.length);
                                buf.writeBytes(bytes);
                            } finally {
                                tmp.release();
                            }
                        }
                    }
                };
    }
}
