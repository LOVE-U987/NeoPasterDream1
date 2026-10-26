package com.pasterdream.pasterdreammod.api.effect.particle;

import io.netty.handler.codec.DecoderException;

/**
 * 粒子发射器网络安全上限与解码校验
 * <p>
 * 统一约束网络解码、构建器与客户端 tick 预算，避免恶意服务端通过
 * {@code ParticleEmitterPayload} 触发巨量内存分配或长时间主线程卡顿。
 * 常量与方法保持 common，不含客户端符号。
 * <p>
 * 异常契约：解码层方法抛 {@link DecoderException}；公开构建路径由
 * {@link ParticleEmitterData.Builder#build()} 抛 {@link IllegalArgumentException}；
 * {@code ParticleEmitterData.fromFields} 是两者之间的唯一转换点。
 */
public final class ParticleLimits {

    /** 单个发射器允许的粒子类型数上限。 */
    public static final int MAX_PARTICLE_TYPES = 32;
    /** 单个粒子选项编码后的最大字节数。 */
    public static final int MAX_PARTICLE_DATA_BYTES = 64 * 1024;
    /** 发射器生命周期上限（5 分钟，按 20 TPS 计）。 */
    public static final int MAX_LIFETIME_TICKS = 6000;
    /** 单发射器每 tick 粒子数上限。 */
    public static final int MAX_PARTICLES_PER_TICK = 64;
    /** 客户端同时存在的发射器上限。 */
    public static final int MAX_ACTIVE_EMITTERS = 128;
    /** 客户端每 tick 全部发射器的粒子总预算。 */
    public static final int MAX_TOTAL_PARTICLES_PER_TICK = 1024;
    /** 单发射器持有的活跃粒子上限。 */
    public static final int MAX_ACTIVE_PARTICLES_PER_EMITTER = 1024;
    /** 客户端全部发射器（含排空中的发射器）持有的活跃粒子总数上限。 */
    public static final int MAX_ACTIVE_PARTICLES = 4096;
    /** 发射坐标允许的绝对值上限（与世界边界同量级）。 */
    public static final double MAX_ABSOLUTE_COORDINATE = 3.0E7;

    private ParticleLimits() {
        throw new UnsupportedOperationException("ParticleLimits 是纯静态常量类，不可实例化");
    }

    /**
     * 校验粒子类型数量与剩余字节数。
     *
     * <p>调用前提：粒子类型段必须是当前报文的最后一个字段；若将来在该段
     * 之后追加字段，必须改为传入该段自身的剩余长度。
     *
     * @param count         声明的类型数量
     * @param readableBytes 当前可读字节数
     * @throws DecoderException 数量非法或不可能被剩余字节容纳
     */
    public static void requireTypeCount(int count, int readableBytes) {
        if (count < 1 || count > MAX_PARTICLE_TYPES) {
            throw new DecoderException("Invalid particle type count: " + count);
        }
        // 每个条目至少包含 id 与 len 两个 varint
        if (count > readableBytes / 2) {
            throw new DecoderException("Particle type count exceeds remaining bytes: " + count);
        }
    }

    /**
     * 校验单个粒子选项的数据长度。
     *
     * @param length        声明的数据长度
     * @param readableBytes 当前可读字节数
     * @throws DecoderException 长度为负、超上限或超过剩余字节
     */
    public static void requireDataLength(int length, int readableBytes) {
        if (length < 0 || length > MAX_PARTICLE_DATA_BYTES || length > readableBytes) {
            throw new DecoderException("Invalid particle data length: " + length);
        }
    }

    /**
     * 夹取本 tick 实际尝试生成数。
     *
     * @param requested      发射器请求的每 tick 数量
     * @param tickBudget     全局每 tick 剩余预算
     * @param activeCapacity 全局或单发射器剩余存量容量
     * @return 三者与单发射器上限的最小值，且不小于 0
     */
    public static int clampSpawnCount(int requested, int tickBudget, int activeCapacity) {
        int limit = Math.min(requested, MAX_PARTICLES_PER_TICK);
        limit = Math.min(limit, Math.max(tickBudget, 0));
        limit = Math.min(limit, Math.max(activeCapacity, 0));
        return Math.max(limit, 0);
    }

    /**
     * 判断是否还能接受新发射器。
     *
     * @param currentSize 当前活跃发射器数量
     * @return 未达到上限返回 true
     */
    public static boolean canAcceptEmitter(int currentSize) {
        return currentSize < MAX_ACTIVE_EMITTERS;
    }
}
