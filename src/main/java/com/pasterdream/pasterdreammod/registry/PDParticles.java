package com.pasterdream.pasterdreammod.registry;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 粒子类型注册类
 * 使用 DeferredRegister 模式注册所有自定义粒子类型
 */
public class PDParticles {

    /**
     * 粒子类型注册器
     */
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(Registries.PARTICLE_TYPE, PasterDreamMod.MOD_ID);

    /**
     * 融梦水晶粒子（meltdream_crystal_particle）
     * 用于生命水晶的发光粒子效果
     */
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> MELTDREAM_CRYSTAL_PARTICLE =
            PARTICLE_TYPES.register("meltdream_crystal_particle", () -> new SimpleParticleType(false));

    /**
     * 暗影石粒子（shadow_stone_particle）
     * 用于暗影魔像技能爆炸的碎石粒子效果
     */
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SHADOW_STONE_PARTICLE =
            PARTICLE_TYPES.register("shadow_stone_particle", () -> new SimpleParticleType(false));
}