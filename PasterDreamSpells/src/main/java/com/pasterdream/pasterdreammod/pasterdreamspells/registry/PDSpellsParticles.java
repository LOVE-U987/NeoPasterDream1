package com.pasterdream.pasterdreammod.pasterdreamspells.registry;

import com.pasterdream.pasterdreammod.api.particle.ParticleAPI;
import com.pasterdream.pasterdreammod.api.particle.ParticleResult;
import net.minecraft.core.particles.ParticleType;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 法术系统粒子类型注册类。
 * <p>
 * 负责注册剧毒、狂暴、治疗、冰冻等法术相关的自定义粒子。
 * 所有粒子均使用 {@link ParticleAPI} 的 Builder 方式注册，命名空间为
 * {@link PasterDreamSpellsMod#MOD_ID}。
 *
 * @author PasterDream
 */
public class PDSpellsParticles {

    /** 粒子类型注册器（与 ParticleAPI 共享同一个注册器） */
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = ParticleAPI.REGISTRY;

    /**
     * 毒气粒子（poison_gas_particle）
     * 剧毒法术的大团毒雾（4 帧动画，半透明大尺寸）。
     */
    public static final ParticleResult POISON_GAS_PARTICLE = ParticleAPI.createParticle("poison_gas_particle")
            .alwaysShow(false)
            .texture("pasterdreamspells:poison_gas_particle_1")
            .generateJson(false)
            .build();

    /**
     * 毒魂粒子（poison_soul_particle）
     * 剧毒法术上升的毒魂点缀（随机单帧，缓慢上飘）。
     */
    public static final ParticleResult POISON_SOUL_PARTICLE = ParticleAPI.createParticle("poison_soul_particle")
            .alwaysShow(false)
            .texture("pasterdreamspells:poison_soul_particle")
            .generateJson(false)
            .build();

    /**
     * 狂暴法术粒子（fury_spell_particle）
     * 狂暴立场的能量符文（10 帧动画）。
     */
    public static final ParticleResult FURY_SPELL_PARTICLE = ParticleAPI.createParticle("fury_spell_particle")
            .alwaysShow(false)
            .texture("pasterdreamspells:fury_spell_particle_1")
            .generateJson(false)
            .build();

    /**
     * 治疗法术粒子（healing_spell_particle）
     * 治疗立场的十字光点（8 帧动画，缓慢上升）。
     */
    public static final ParticleResult HEALING_SPELL_PARTICLE = ParticleAPI.createParticle("healing_spell_particle")
            .alwaysShow(false)
            .texture("pasterdreamspells:healing_spell_particle_1")
            .generateJson(false)
            .build();

    /**
     * 黄色烟雾粒子（yellow_smoke_particle）
     * 治疗立场的暖黄烟雾（4 帧动画，缓缓下沉）。
     */
    public static final ParticleResult YELLOW_SMOKE_PARTICLE = ParticleAPI.createParticle("yellow_smoke_particle")
            .alwaysShow(false)
            .texture("pasterdreamspells:yellow_smoke_particle_1")
            .generateJson(false)
            .build();

    /**
     * 冰法雪花粒子 0（spell_snowflake_0_particle）
     * 冰冻法术的第一种雪花（4 帧动画，下落）。
     * <p>注意：使用后缀 {@code spell_} 前缀以避免与 {@code PDParticles.snowflake_0_particle} 名称冲突。
     */
    public static final ParticleResult SPELL_SNOWFLAKE_0_PARTICLE = ParticleAPI.createParticle("spell_snowflake_0_particle")
            .alwaysShow(false)
            .texture("pasterdreamspells:snowflake_0_particle_1")
            .generateJson(false)
            .build();

    /**
     * 雪花粒子 1（snowflake_1_particle）
     * 冰冻法术的第二种雪花（4 帧动画，下落）。
     */
    public static final ParticleResult SNOWFLAKE_1_PARTICLE = ParticleAPI.createParticle("snowflake_1_particle")
            .alwaysShow(false)
            .texture("pasterdreamspells:snowflake_1_particle_1")
            .generateJson(false)
            .build();

    private PDSpellsParticles() {
        throw new UnsupportedOperationException("PDSpellsParticles 是注册类，不可实例化");
    }
}
