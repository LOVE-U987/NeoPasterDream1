package com.pasterdream.pasterdreammod.api.client.effect.particle;

import com.pasterdream.pasterdreammod.api.effect.particle.EmitterProcessor;
import com.pasterdream.pasterdreammod.api.effect.particle.ParticleEmitterData;
import com.pasterdream.pasterdreammod.api.effect.particle.ParticleLimits;
import com.pasterdream.pasterdreammod.api.effect.particle.processors.BoundToEntityProcessor;
import com.pasterdream.pasterdreammod.api.effect.particle.processors.CircleSpawnProcessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 客户端粒子发射器实例 —— 每 tick 生成粒子并驱动处理器
 * <p>
 * 借鉴开源模组 FDLib（作者 FINDERFEED）的 {@code ParticleEmitter} 设计思路
 * （独立实现，非复制）。由 {@link ParticleEmitterHandler} 持有并 tick。
 * 生命周期为 {@code lifetime} tick，到期后停止生成新粒子并进入排空状态，
 * 待已有粒子全部消亡才从活跃列表移除。
 * <p>
 * 处理器回调（{@link EmitterProcessor#initParticle(Object)} 等）在此处传入
 * 真实的客户端 {@link Particle} 对象；内置处理器参数由本类按类型分支消费。
 */
@OnlyIn(Dist.CLIENT)
public class ParticleEmitter {

    private static final Random RANDOM = new Random();

    private final ParticleEmitterData data;
    private final List<Particle> activeParticles = new ArrayList<>();
    private final Vec3 position;

    private int age;
    private boolean finished;
    private boolean removed;

    /**
     * 构造客户端发射器
     *
     * @param data 发射器数据
     */
    public ParticleEmitter(ParticleEmitterData data) {
        this.data = data;
        this.position = data.position();
        this.age = 0;
        this.finished = false;
        this.removed = false;
    }

    /**
     * 是否已移除（已排空并可从活跃列表摘除）
     *
     * @return 已移除返回 {@code true}
     */
    public boolean isRemoved() {
        return removed;
    }

    /**
     * 发射器每 tick
     */
    public void tick() {
        tickBounded(ParticleLimits.MAX_PARTICLES_PER_TICK);
    }

    /**
     * 带预算的每 tick 推进。生命周期结束后进入排空状态：
     * 停止生成新粒子，但继续清理并统计已有粒子，直到全部消亡才移除。
     * <p>
     * 处理器回调窗口与原实现一致：到期当 tick 仍执行一次
     * {@code tickEmitter}、生成与 {@code tickParticle}；之后的排空 tick
     * 只移除已死亡粒子，不调用处理器。
     *
     * @param maxNewParticles 本 tick 允许尝试生成的粒子上限
     * @return 尝试次数（包含 createParticle 返回 null 的尝试），用于保守扣减预算
     */
    int tickBounded(int maxNewParticles) {
        age++;

        EmitterProcessor<?> processor = data.processor();
        // 本 tick 是否仍处于原实现的处理器回调窗口内。
        // 到期当 tick 仍为 true，之后的排空 tick 为 false。
        boolean active = !finished;

        int attempted = 0;
        if (active) {
            processor.tickEmitter(data);

            int activeCapacity = ParticleLimits.MAX_ACTIVE_PARTICLES_PER_EMITTER - activeParticles.size();
            int limit = ParticleLimits.clampSpawnCount(data.particlesPerTick(), maxNewParticles, activeCapacity);

            ParticleEngine engine = Minecraft.getInstance().particleEngine;
            for (int i = 0; i < limit; i++) {
                attempted++;
                ParticleOptions options = data.randomParticle(RANDOM);
                Particle particle = engine.createParticle(options, position.x, position.y, position.z, 0, 0, 0);
                if (particle != null) {
                    applyProcessorInit(processor, particle);
                    activeParticles.add(particle);
                }
            }

            if (age > data.lifetime()) {
                this.finished = true;
            }
        }

        // 清理已死亡粒子；排空 tick 不再调用 tickParticle。
        var iterator = activeParticles.listIterator();
        while (iterator.hasNext()) {
            Particle particle = iterator.next();
            if (!particle.isAlive()) {
                iterator.remove();
            } else if (active) {
                processor.tickParticle(particle);
            }
        }

        if (finished && activeParticles.isEmpty()) {
            this.removed = true;
        }
        return attempted;
    }

    /**
     * 当前持有的活跃粒子数（含排空中的粒子，供全局预算统计）。
     *
     * @return 活跃粒子数
     */
    int activeParticleCount() {
        return activeParticles.size();
    }

    /**
     * 根据处理器类型对粒子做初始化
     *
     * @param processor 处理器
     * @param particle  客户端粒子
     */
    private void applyProcessorInit(EmitterProcessor<?> processor, Particle particle) {
        if (processor instanceof CircleSpawnProcessor circle) {
            // 圆形生成：在圆盘内随机偏移，沿 direction 方向随机速度
            Vec3 offset = circle.randomDiskOffset(RANDOM);
            double sp = minMax(circle.minSpeed(), circle.maxSpeed(), RANDOM);
            Vec3 dir = circle.direction();
            particle.setPos(position.x + offset.x, position.y + offset.y, position.z + offset.z);
            particle.setParticleSpeed(dir.x * sp, dir.y * sp, dir.z * sp);
        } else if (processor instanceof BoundToEntityProcessor bound) {
            // 绑定实体：位置跟随实体（由 tickEmitter 处理位置，此处仅处理偏移）
            var level = Minecraft.getInstance().level;
            if (level != null && level.getEntity(bound.entityId()) != null) {
                Vec3 epos = level.getEntity(bound.entityId()).position();
                particle.setPos(epos.x, epos.y + bound.yOffset(), epos.z);
            }
        }
        // 其它处理器：交由 processor.initParticle 反射消费
        processor.initParticle(particle);
    }

    /** 在 [min,max] 区间取随机值 */
    private static double minMax(float min, float max, Random random) {
        return min + (max - min) * random.nextDouble();
    }

    /**
     * 获取发射器当前位置（绑定实体时随 tick 更新，简单实现返回初始位置）
     *
     * @return 世界坐标
     */
    public Vec3 position() {
        return position;
    }
}
