package com.pasterdream.pasterdreammod.api.client.effect.particle;

import com.pasterdream.pasterdreammod.api.effect.particle.ParticleEmitterData;
import com.pasterdream.pasterdreammod.api.effect.particle.ParticleLimits;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 客户端粒子发射器处理器 —— 维护活跃发射器列表并驱动其 tick
 * <p>
 * 借鉴开源模组 FDLib（作者 FINDERFEED）的 {@code ParticleEmitterHandler}
 * 设计思路（独立实现，非复制）。由主模 {@code PDEffectClientEvents} 在
 * 客户端 {@code LevelTickEvent.Pre} 时调用 {@link #tickAll()}；网络包
 * 落地时经 {@link #add(ParticleEmitterData)} 加入新发射器。
 * <p>
 * 本类为客户端专用（引用 {@code net.minecraft.client.Minecraft}），
 * 仅由 {@code api/client/**} 路径持有。
 */
@OnlyIn(Dist.CLIENT)
public final class ParticleEmitterHandler {

    private static final List<ParticleEmitter> ACTIVE_EMITTERS = new ArrayList<>();

    private ParticleEmitterHandler() {
        throw new UnsupportedOperationException("ParticleEmitterHandler 是纯静态门面类，不可实例化");
    }

    /**
     * 加入一个新粒子发射器。超过活跃上限时丢弃。
     *
     * @param data 发射器数据
     */
    public static void add(ParticleEmitterData data) {
        if (Minecraft.getInstance().level == null) {
            return;
        }
        if (!ParticleLimits.canAcceptEmitter(ACTIVE_EMITTERS.size())) {
            return;
        }
        ACTIVE_EMITTERS.add(new ParticleEmitter(data));
    }

    /**
     * 客户端每 tick 驱动全部活跃发射器（带全局粒子预算与存量上限）。
     */
    public static void tickAll() {
        if (Minecraft.getInstance().level == null || ACTIVE_EMITTERS.isEmpty()) {
            return;
        }

        int activeParticles = 0;
        for (ParticleEmitter emitter : ACTIVE_EMITTERS) {
            activeParticles += emitter.activeParticleCount();
        }
        int tickBudget = ParticleLimits.MAX_TOTAL_PARTICLES_PER_TICK;
        int activeCapacity = Math.max(ParticleLimits.MAX_ACTIVE_PARTICLES - activeParticles, 0);

        Iterator<ParticleEmitter> iterator = ACTIVE_EMITTERS.iterator();
        while (iterator.hasNext()) {
            ParticleEmitter emitter = iterator.next();
            if (emitter.isRemoved()) {
                iterator.remove();
                continue;
            }
            int allowed = Math.min(tickBudget, activeCapacity);
            int attempted = emitter.tickBounded(allowed);
            tickBudget -= attempted;
            activeCapacity -= attempted;
        }
    }

    /**
     * 玩家登出/世界卸载时清空全部发射器
     */
    public static void clearAll() {
        ACTIVE_EMITTERS.clear();
    }

    /**
     * 测试辅助：清空活跃列表
     */
    public static void resetForTesting() {
        ACTIVE_EMITTERS.clear();
    }
}
