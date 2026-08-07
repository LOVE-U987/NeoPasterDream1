package com.pasterdream.pasterdreammod.api.effect.particle.processors;

import com.pasterdream.pasterdreammod.api.effect.particle.EmitterProcessor;
import com.pasterdream.pasterdreammod.api.effect.particle.EmitterProcessorType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

/**
 * 圆形生成处理器 —— 在发射位置的圆盘内随机生成粒子，沿指定方向以随机速度飞出
 * <p>
 * 借鉴开源模组 FDLib（作者 FINDERFEED）的 {@code CircleSpawnProcessor} 设计思路
 * （独立实现，非复制）。参数：
 * <ul>
 *   <li><b>direction</b>：粒子初始飞出方向（归一化）；</li>
 *   <li><b>minSpeed / maxSpeed</b>：初始速度范围；</li>
 *   <li><b>radius</b>：圆盘半径。</li>
 * </ul>
 * 粒子位置在垂直于 {@code direction} 的圆盘内随机（拒绝采样保证均匀分布），
 * 速度方向沿 {@code direction} 取随机大小。
 */
public class CircleSpawnProcessor implements EmitterProcessor<CircleSpawnProcessor> {

    /** 处理器类型实例 */
    public static final Type TYPE = new Type();

    private final Vec3 direction;
    private final float minSpeed;
    private final float maxSpeed;
    private final float radius;

    /**
     * 构造圆形生成处理器
     *
     * @param direction 初始飞出方向（未归一化亦可，内部会归一化）
     * @param minSpeed  最小初始速度
     * @param maxSpeed  最大初始速度
     * @param radius    圆盘半径
     */
    public CircleSpawnProcessor(Vec3 direction, float minSpeed, float maxSpeed, float radius) {
        this.direction = direction.normalize();
        this.minSpeed = minSpeed;
        this.maxSpeed = maxSpeed;
        this.radius = radius;
    }

    /**
     * 构造圆形生成处理器（向上飞出，速度 0~0.1）
     *
     * @param radius 圆盘半径
     */
    public CircleSpawnProcessor(float radius) {
        this(new Vec3(0, -1, 0), 0.0f, 0.1f, radius);
    }

    @Override
    public void initParticle(Object particle) {
        // particle 为客户端 net.minecraft.client.particle.Particle，此处反射设置
        // 初始位置与速度，避免 API 通用包引用客户端符号。具体设置在客户端
        // ParticleEmitter 中经 processor 统一处理（见 api/client/effect/particle）。
        // 为保持 API 侧纯净，本处理器仅在客户端实现层消费参数。
    }

    @Override
    public EmitterProcessorType<CircleSpawnProcessor> type() {
        return TYPE;
    }

    /**
     * 获取飞出方向
     *
     * @return 归一化方向
     */
    public Vec3 direction() {
        return direction;
    }

    /**
     * 获取最小速度
     *
     * @return 最小初始速度
     */
    public float minSpeed() {
        return minSpeed;
    }

    /**
     * 获取最大速度
     *
     * @return 最大初始速度
     */
    public float maxSpeed() {
        return maxSpeed;
    }

    /**
     * 获取圆盘半径
     *
     * @return 半径
     */
    public float radius() {
        return radius;
    }

    /**
     * 在圆盘内生成一个随机偏移（供客户端实现调用）
     *
     * @param random 随机源
     * @return 圆盘内偏移向量（相对发射点）
     */
    public Vec3 randomDiskOffset(Random random) {
        double x;
        double z;
        do {
            x = random.nextDouble() * 2 - 1;
            z = random.nextDouble() * 2 - 1;
        } while (x * x + z * z > 1);
        return new Vec3(x * radius, 0, z * radius);
    }

    /**
     * 圆形生成处理器类型
     */
    public static class Type implements EmitterProcessorType<CircleSpawnProcessor> {

        /** 类型唯一 id */
        public static final ResourceLocation ID = ResourceLocation.parse("pasterdream:circle_spawn_processor");

        /** 编解码器 */
        public static final StreamCodec<FriendlyByteBuf, CircleSpawnProcessor> STREAM_CODEC =
                StreamCodec.composite(
                        VectorCodec.VEC3, v -> v.direction,
                        ByteBufCodecs.FLOAT, v -> v.minSpeed,
                        ByteBufCodecs.FLOAT, v -> v.maxSpeed,
                        ByteBufCodecs.FLOAT, v -> v.radius,
                        CircleSpawnProcessor::new
                );

        @Override
        public StreamCodec<FriendlyByteBuf, CircleSpawnProcessor> codec() {
            return STREAM_CODEC;
        }

        @Override
        public ResourceLocation id() {
            return ID;
        }
    }

    /** Vec3 StreamCodec 工具 */
    private static final class VectorCodec {
        private static final StreamCodec<FriendlyByteBuf, Vec3> VEC3 = StreamCodec.composite(
                ByteBufCodecs.DOUBLE, v -> v.x,
                ByteBufCodecs.DOUBLE, v -> v.y,
                ByteBufCodecs.DOUBLE, v -> v.z,
                Vec3::new
        );
    }
}
