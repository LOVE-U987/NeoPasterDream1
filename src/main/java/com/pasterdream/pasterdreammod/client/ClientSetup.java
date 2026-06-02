package com.pasterdream.pasterdreammod.client;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.api.entity.EntityAPI;
import com.pasterdream.pasterdreammod.api.entity.EntityResult;
import com.pasterdream.pasterdreammod.api.particle.ParticleAPI;
import com.pasterdream.pasterdreammod.api.particle.ParticleResult;
import com.pasterdream.pasterdreammod.client.model.Modelslime;
import com.pasterdream.pasterdreammod.client.particle.*;
import com.pasterdream.pasterdreammod.client.renderer.block.DreamAccumulatorBlockRenderer;
import com.pasterdream.pasterdreammod.client.renderer.block.LifeCrystalBlockRenderer;
import com.pasterdream.pasterdreammod.client.renderer.block.ShadowChestBlockRenderer;
import com.pasterdream.pasterdreammod.client.renderer.entity.PinkSlimeRenderer;
import com.pasterdream.pasterdreammod.client.renderer.entity.ShadowGolemRenderer;
import com.pasterdream.pasterdreammod.client.screen.ShadowChestScreen;
import com.pasterdream.pasterdreammod.entity.mob.PinkSlimeEntity;
import com.pasterdream.pasterdreammod.entity.mob.ShadowGolemEntity;
import com.pasterdream.pasterdreammod.registry.PDBlockEntities;
import com.pasterdream.pasterdreammod.registry.PDMenus;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterDimensionSpecialEffectsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import org.jetbrains.annotations.Nullable;

/**
 * 客户端设置类
 * 负责注册客户端特有的渲染器和事件处理
 *
 * 注意：此类仅在客户端加载（Dist.CLIENT）
 */
@EventBusSubscriber(modid = PasterDreamMod.MOD_ID, value = Dist.CLIENT)
public class ClientSetup {

    /**
     * 注册渲染器
     * 在 EntityRenderersEvent.RegisterRenderers 事件时调用
     *
     * @param event 渲染器注册事件
     */
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // 注册蓄梦池方块实体渲染器
        event.registerBlockEntityRenderer(
                PDBlockEntities.DREAM_ACCUMULATOR.get(),
                context -> new DreamAccumulatorBlockRenderer()
        );
        PasterDreamMod.LOGGER.info("[ClientSetup] 注册方块实体渲染器: dream_accumulator → DreamAccumulatorBlockRenderer");

        // 注册生命水晶方块实体渲染器
        event.registerBlockEntityRenderer(
                PDBlockEntities.LIFE_CRYSTAL.get(),
                LifeCrystalBlockRenderer::new
        );
        PasterDreamMod.LOGGER.info("[ClientSetup] 注册方块实体渲染器: life_crystal → LifeCrystalBlockRenderer");

        // 注册影之箱方块实体渲染器
        event.registerBlockEntityRenderer(
                PDBlockEntities.SHADOW_CHEST.get(),
                ShadowChestBlockRenderer::new
        );
        PasterDreamMod.LOGGER.info("[ClientSetup] 注册方块实体渲染器: shadow_chest → ShadowChestBlockRenderer");

        // 注册暗影魔像实体渲染器
        @SuppressWarnings("unchecked")
        EntityResult<ShadowGolemEntity> shadowGolem = (EntityResult<ShadowGolemEntity>) EntityAPI.getEntityResult("shadow_golem");
        if (shadowGolem != null) {
            event.registerEntityRenderer(shadowGolem.entityType(), ShadowGolemRenderer::new);
            PasterDreamMod.LOGGER.info("[ClientSetup] 注册实体渲染器: shadow_golem → ShadowGolemRenderer （GeckoLib）");
        }

        // 注册粉色史莱姆实体渲染器
        @SuppressWarnings("unchecked")
        EntityResult<PinkSlimeEntity> pinkSlime = (EntityResult<PinkSlimeEntity>) EntityAPI.getEntityResult("pink_slime");
        if (pinkSlime != null) {
            event.registerEntityRenderer(pinkSlime.entityType(), PinkSlimeRenderer::new);
            PasterDreamMod.LOGGER.info("[ClientSetup] 注册实体渲染器: pink_slime → PinkSlimeRenderer （原生模型）");
        }
    }

    /**
     * 注册模型层
     * 在 EntityRenderersEvent.RegisterLayerDefinitions 事件时调用
     *
     * @param event 模型层注册事件
     */
    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(Modelslime.LAYER_LOCATION, Modelslime::createBodyLayer);
        PasterDreamMod.LOGGER.info("[ClientSetup] 注册模型层: {}", Modelslime.LAYER_LOCATION);
    }

    /**
     * 注册 GUI 屏幕
     * 在 RegisterMenuScreensEvent 事件时调用
     *
     * @param event 菜单屏幕注册事件
     */
    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(PDMenus.SHADOW_CHEST.get(), ShadowChestScreen::new);
        PasterDreamMod.LOGGER.info("[ClientSetup] 注册 GUI 屏幕: shadow_chest → ShadowChestScreen");
    }

    /**
     * 注册粒子提供器
     * 在 RegisterParticleProvidersEvent 事件时调用
     *
     * @param event 粒子提供器注册事件
     */
    @SubscribeEvent
    public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        PasterDreamMod.LOGGER.info("[ClientSetup] 开始注册粒子提供器...");

        registerParticleProvider(event, "meltdream_crystal_particle", LifeCrystalParticle.Provider::new);
        registerParticleProvider(event, "dream_ambient_particle", DreamAmbientParticle.Provider::new);
        registerParticleProvider(event, "leaves_particle", LeavesParticle.Provider::new);
        registerParticleProvider(event, "dreamfertiliter_particle", DreamfertiliterFallingParticle.Provider::new);
        registerParticleProvider(event, "calle_particle", CalleParticle.Provider::new);
        registerParticleProvider(event, "silver_particle", SilverParticle.Provider::new);
        registerParticleProvider(event, "crack_0_particle", CrackParticle.Provider::new);
        registerParticleProvider(event, "white_star_particle", WhiteStarParticle.Provider::new);
        registerParticleProvider(event, "snowflake_0_particle", SnowflakeParticle.Provider::new);

        PasterDreamMod.LOGGER.info("[ClientSetup] 粒子提供器注册完成，共 9 个粒子类型");
    }

    /**
     * 注册单个粒子 Provider（从 ParticleAPI 缓存中查找粒子类型）
     */
    private static void registerParticleProvider(RegisterParticleProvidersEvent event,
                                                  String name,
                                                  net.minecraft.client.particle.ParticleEngine.SpriteParticleRegistration<SimpleParticleType> factory) {
        ParticleResult result = ParticleAPI.getParticle(name);
        if (result == null) {
            PasterDreamMod.LOGGER.warn("[ClientSetup] 未找到粒子 [{}] 的注册记录，跳过 Provider 注册", name);
            return;
        }
        SimpleParticleType type = (SimpleParticleType) result.particleType();
        event.registerSpriteSet(type, factory);
        PasterDreamMod.LOGGER.info("[ClientSetup] 已注册粒子 Provider: {} | type={}", name, type);
    }

    /** 染梦维度四个群系的 ResourceKey 常量（与 PDClientEvents 保持一致） */
    private static final ResourceKey<Biome> BIOME_DYEDREAM_0 = ResourceKey.create(
            Registries.BIOME, ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "biome_dyedream_0"));
    private static final ResourceKey<Biome> BIOME_DYEDREAM_1 = ResourceKey.create(
            Registries.BIOME, ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "biome_dyedream_1"));
    private static final ResourceKey<Biome> BIOME_DYEDREAM_2 = ResourceKey.create(
            Registries.BIOME, ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "biome_dyedream_2"));
    private static final ResourceKey<Biome> BIOME_DYEDREAM_3 = ResourceKey.create(
            Registries.BIOME, ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "biome_dyedream_3"));

    /**
     * 在三色间插值（白天色 → 黄昏色 → 夜色）
     *
     * @param day      白天雾色
     * @param sunset   黄昏雾色
     * @param night    夜晚雾色
     * @param sunHeight 太阳高度（-1 ~ 1），负值=夜晚，0=地平线，正值=白天
     * @return 插值后的雾色
     */
    private static Vec3 interpolateTriColor(Vec3 day, Vec3 sunset, Vec3 night, float sunHeight) {
        if (sunHeight > 0.0f) {
            float t = Math.min(sunHeight * 6.0f, 1.0f);
            return new Vec3(
                    sunset.x + (day.x - sunset.x) * t,
                    sunset.y + (day.y - sunset.y) * t,
                    sunset.z + (day.z - sunset.z) * t
            );
        } else {
            float t = Math.min(-sunHeight * 5.0f, 1.0f);
            return new Vec3(
                    sunset.x + (night.x - sunset.x) * t,
                    sunset.y + (night.y - sunset.y) * t,
                    sunset.z + (night.z - sunset.z) * t
            );
        }
    }

    /**
     * 注册染梦世界维度特殊效果（天空、雾色）
     * 对应 dimension_type JSON 中的 "effects": "pasterdream:dyedream_world"
     */
    @SubscribeEvent
    public static void registerDimensionSpecialEffects(RegisterDimensionSpecialEffectsEvent event) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "dyedream_world");
        event.register(id, new DimensionSpecialEffects(
                192.0f, true, DimensionSpecialEffects.SkyType.NORMAL, false, false
        ) {
            @Override
            public Vec3 getBrightnessDependentFogColor(Vec3 fogColor, float sunHeight) {
                ResourceKey<Biome> biome = PDClientEvents.currentBiomeKey;

                Vec3 dayColor, sunsetColor, nightColor;

                if (BIOME_DYEDREAM_0.equals(biome)) {
                    // 温暖平原 — 梦幻粉雾
                    dayColor = new Vec3(1.0, 0.71, 0.85);
                    sunsetColor = new Vec3(1.0, 0.56, 0.64);
                    nightColor = new Vec3(0.29, 0.10, 0.36);
                } else if (BIOME_DYEDREAM_1.equals(biome)) {
                    // 炎热森林 — 翠绿迷雾
                    dayColor = new Vec3(0.66, 0.90, 0.64);
                    sunsetColor = new Vec3(0.83, 0.64, 0.45);
                    nightColor = new Vec3(0.10, 0.23, 0.16);
                } else if (BIOME_DYEDREAM_2.equals(biome)) {
                    // 寒冷冰雪 — 冰蓝极雾
                    dayColor = new Vec3(0.71, 0.85, 1.0);
                    sunsetColor = new Vec3(0.64, 0.71, 0.83);
                    nightColor = new Vec3(0.10, 0.16, 0.36);
                } else if (BIOME_DYEDREAM_3.equals(biome)) {
                    // 温暖海洋 — 海蓝薄雾
                    dayColor = new Vec3(0.64, 0.83, 0.90);
                    sunsetColor = new Vec3(0.83, 0.64, 0.64);
                    nightColor = new Vec3(0.04, 0.16, 0.23);
                } else {
                    // 后备：温暖平原色
                    dayColor = new Vec3(1.0, 0.71, 0.85);
                    sunsetColor = new Vec3(1.0, 0.56, 0.64);
                    nightColor = new Vec3(0.29, 0.10, 0.36);
                }

                return interpolateTriColor(dayColor, sunsetColor, nightColor, sunHeight);
            }

            @Override
            @Nullable
            public float[] getSunriseColor(float timeOfDay, float partialTick) {
                float sunHeight = (float) Math.sin(timeOfDay * 2.0 * Math.PI);
                if (sunHeight < -0.1f || sunHeight > 0.2f) return null;

                float fade = (sunHeight + 0.1f) / 0.3f;
                float alpha = (float) Math.sin(fade * Math.PI) * 0.55f;

                return new float[]{1.0f, 0.41f, 0.71f, alpha};
            }

            @Override
            public boolean isFoggyAt(int x, int y) {
                return false;
            }
        });
        PasterDreamMod.LOGGER.info("[ClientSetup] 已注册维度特效: {}", id);
    }
}
