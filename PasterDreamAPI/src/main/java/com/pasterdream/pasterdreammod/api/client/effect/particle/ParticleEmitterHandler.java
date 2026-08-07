package com.pasterdream.pasterdreammod.api.client.effect.particle;

import com.pasterdream.pasterdreammod.api.effect.particle.ParticleEmitterData;
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
     * 加入一个新粒子发射器
     *
     * @param data 发射器数据
     */
    public static void add(ParticleEmitterData data) {
        if (Minecraft.getInstance().level == null) {
            return;
        }
        ParticleEmitter emitter = new ParticleEmitter(data);
        ACTIVE_EMITTERS.add(emitter);
    }

    /**
     * 客户端每 tick 驱动全部活跃发射器（无客户端世界时跳过）
     */
    public static void tickAll() {
        if (Minecraft.getInstance().level == null || ACTIVE_EMITTERS.isEmpty()) {
            return;
        }
        Iterator<ParticleEmitter> iterator = ACTIVE_EMITTERS.iterator();
        while (iterator.hasNext()) {
            ParticleEmitter emitter = iterator.next();
            if (emitter.isRemoved()) {
                iterator.remove();
                continue;
            }
            emitter.tick();
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
