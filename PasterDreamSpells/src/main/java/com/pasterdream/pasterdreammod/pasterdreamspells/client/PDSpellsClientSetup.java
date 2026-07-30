package com.pasterdream.pasterdreammod.pasterdreamspells.client;

import com.pasterdream.pasterdreammod.api.util.PDDebugLogger;
import com.pasterdream.pasterdreammod.pasterdreamspells.client.particle.*;
import com.pasterdream.pasterdreammod.pasterdreamspells.client.renderer.entity.FurySpellFieldRenderer;
import com.pasterdream.pasterdreammod.pasterdreamspells.client.renderer.entity.HealingSpellFieldRenderer;
import com.pasterdream.pasterdreammod.pasterdreamspells.registry.PDSpellsEntities;
import com.pasterdream.pasterdreammod.pasterdreamspells.registry.PDSpellsParticles;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

/**
 * PasterDreamSpells 客户端设置。
 * <p>
 * 注册法术系统的粒子提供器、实体渲染器等客户端资源。
 * 仅在客户端侧加载。
 *
 * @author PasterDream
 */
public class PDSpellsClientSetup {

    /**
     * 注册粒子提供器
     *
     * @param event 粒子提供器注册事件
     */
    public static void onRegisterParticleProviders(RegisterParticleProvidersEvent event) {
        // 剧毒法术粒子
        event.registerSpriteSet((SimpleParticleType) PDSpellsParticles.POISON_GAS_PARTICLE.particleType(),
                PoisonGasParticle.Provider::new);
        event.registerSpriteSet((SimpleParticleType) PDSpellsParticles.POISON_SOUL_PARTICLE.particleType(),
                PoisonSoulParticle.Provider::new);

        // 治疗法术粒子
        event.registerSpriteSet((SimpleParticleType) PDSpellsParticles.HEALING_SPELL_PARTICLE.particleType(),
                HealingSpellParticle.Provider::new);
        event.registerSpriteSet((SimpleParticleType) PDSpellsParticles.YELLOW_SMOKE_PARTICLE.particleType(),
                YellowSmokeParticle.Provider::new);

        // 狂暴法术粒子
        event.registerSpriteSet((SimpleParticleType) PDSpellsParticles.FURY_SPELL_PARTICLE.particleType(),
                FurySpellParticle.Provider::new);

        // 冰冻法术粒子
        event.registerSpriteSet((SimpleParticleType) PDSpellsParticles.SPELL_SNOWFLAKE_0_PARTICLE.particleType(),
                Snowflake1Particle.Provider::new);
        event.registerSpriteSet((SimpleParticleType) PDSpellsParticles.SNOWFLAKE_1_PARTICLE.particleType(),
                Snowflake1Particle.Provider::new);

        PDDebugLogger.mainDebug("[PDSpellsClientSetup] 法术粒子提供器注册完成");
    }

    /**
     * 注册实体渲染器
     *
     * @param event 实体渲染器注册事件
     */
    public static void onRegisterEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // 法术投射物（5 个）：使用 ThrownItemRenderer 渲染为飞行中的物品
        event.registerEntityRenderer(PDSpellsEntities.LIGHTNING_SPELL_PROJECTILE.get(),
                context -> new ThrownItemRenderer<>(context, 1.0f, false));
        event.registerEntityRenderer(PDSpellsEntities.POISON_SPELL_PROJECTILE.get(),
                context -> new ThrownItemRenderer<>(context, 1.0f, false));
        event.registerEntityRenderer(PDSpellsEntities.HEALING_SPELL_PROJECTILE.get(),
                context -> new ThrownItemRenderer<>(context, 1.0f, false));
        event.registerEntityRenderer(PDSpellsEntities.FURY_SPELL_PROJECTILE.get(),
                context -> new ThrownItemRenderer<>(context, 1.0f, false));
        event.registerEntityRenderer(PDSpellsEntities.ICE_SPELL_PROJECTILE.get(),
                context -> new ThrownItemRenderer<>(context, 1.0f, false));

        // 法术立场实体（2 个）：GeckoLib 半透明渲染
        event.registerEntityRenderer(PDSpellsEntities.FURY_SPELL_ENTITY.get(),
                FurySpellFieldRenderer::new);
        event.registerEntityRenderer(PDSpellsEntities.HEALING_SPELL_ENTITY.get(),
                HealingSpellFieldRenderer::new);

        PDDebugLogger.mainDebug("[PDSpellsClientSetup] 法术实体渲染器注册完成");
    }
}
