package com.pasterdream.pasterdreammod.client;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.client.model.Modelslime;
import com.pasterdream.pasterdreammod.client.particle.*;
import com.pasterdream.pasterdreammod.client.particle.AuroraGlowParticle;
import com.pasterdream.pasterdreammod.client.particle.CrystalSnowflakeParticle;
import com.pasterdream.pasterdreammod.client.particle.DreamSporeParticle;
import com.pasterdream.pasterdreammod.client.particle.StardustParticle;
import com.pasterdream.pasterdreammod.client.renderer.RendererRegistry;
import com.pasterdream.pasterdreammod.client.screen.DreamCauldronScreen;
import com.pasterdream.pasterdreammod.client.screen.DyedreamDeskScreen;
import com.pasterdream.pasterdreammod.client.screen.MeltdreamChestScreen;
import com.pasterdream.pasterdreammod.client.screen.ShadowChestScreen;
import com.pasterdream.pasterdreammod.client.screen.TheEndlessBookOfDreamSeekersScreen;
import com.pasterdream.pasterdreammod.client.curio.CurioClientHandler;
import com.pasterdream.pasterdreammod.registry.PDMenus;
import com.pasterdream.pasterdreammod.registry.PDParticles;
import com.pasterdream.pasterdreammod.registry.PDFluidsType;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterDimensionSpecialEffectsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import org.jetbrains.annotations.Nullable;

/**
 * 客户端设置类
 * 负责注册客户端特有的渲染器、屏幕、粒子和维度特效
 *
 * <p>注意：此类仅在客户端加载（Dist.CLIENT）</p>
 * <p>渲染器注册已委托给 {@link RendererRegistry}，此类仅保留其他客户端初始化逻辑</p>
 */
@EventBusSubscriber(modid = PasterDreamMod.MOD_ID, value = Dist.CLIENT)
public class ClientSetup {

    /**
     * 注册渲染器
     * <p>委托给 {@link RendererRegistry#registerAll(EntityRenderersEvent.RegisterRenderers)} 统一处理</p>
     *
     * @param event 渲染器注册事件
     */
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        RendererRegistry.registerAll(event);
    }

    /**
     * 客户端初始化 —— 注册饰品身体渲染器
     *
     * @param event 客户端初始化事件
     */
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            CurioClientHandler.init();
            PasterDreamMod.LOGGER.debug("[ClientSetup] 饰品身体渲染器初始化完成");
        });
    }

    /**
     * 注册模型层
     *
     * @param event 模型层注册事件
     */
    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(Modelslime.LAYER_LOCATION, Modelslime::createBodyLayer);
        PasterDreamMod.LOGGER.debug("[ClientSetup] 注册模型层: {}", Modelslime.LAYER_LOCATION);
    }

    /**
     * 注册 GUI 屏幕
     *
     * @param event 菜单屏幕注册事件
     */
    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(PDMenus.SHADOW_CHEST.get(), ShadowChestScreen::new);
        PasterDreamMod.LOGGER.debug("[ClientSetup] 注册 GUI 屏幕: shadow_chest → ShadowChestScreen");

        event.register(PDMenus.MELTDREAM_CHEST.get(), MeltdreamChestScreen::new);
        PasterDreamMod.LOGGER.debug("[ClientSetup] 注册 GUI 屏幕: meltdream_chest → MeltdreamChestScreen");

        event.register(PDMenus.DYEDREAM_DESK.get(), DyedreamDeskScreen::new);
        PasterDreamMod.LOGGER.debug("[ClientSetup] 注册 GUI 屏幕: dyedream_desk → DyedreamDeskScreen");

        event.register(PDMenus.DREAM_CAULDRON.get(), DreamCauldronScreen::new);
        PasterDreamMod.LOGGER.debug("[ClientSetup] 注册 GUI 屏幕: dream_cauldron → DreamCauldronScreen");

        event.register(PDMenus.THE_ENDLESS_BOOK_OF_DREAM_SEEKERS.get(), TheEndlessBookOfDreamSeekersScreen::new);
        PasterDreamMod.LOGGER.debug("[ClientSetup] 注册 GUI 屏幕: the_endless_book_of_dream_seekers → TheEndlessBookOfDreamSeekersScreen");
    }

    /**
     * 注册粒子提供器
     *
     * @param event 粒子提供器注册事件
     */
    @SubscribeEvent
    public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        PasterDreamMod.LOGGER.debug("[ClientSetup] 开始注册粒子提供器...");

        event.registerSpriteSet((SimpleParticleType) PDParticles.MELTDREAM_CRYSTAL_PARTICLE.particleType(), LifeCrystalParticle.Provider::new);
        event.registerSpriteSet((SimpleParticleType) PDParticles.DREAM_AMBIENT_PARTICLE.particleType(), DreamAmbientParticle.Provider::new);
        event.registerSpriteSet((SimpleParticleType) PDParticles.LEAVES_PARTICLE.particleType(), LeavesParticle.Provider::new);
        event.registerSpriteSet((SimpleParticleType) PDParticles.DREAMFERTILITER_PARTICLE.particleType(), DreamfertiliterFallingParticle.Provider::new);
        event.registerSpriteSet((SimpleParticleType) PDParticles.CALLE_PARTICLE.particleType(), CalleParticle.Provider::new);
        event.registerSpriteSet((SimpleParticleType) PDParticles.SILVER_PARTICLE.particleType(), SilverParticle.Provider::new);
        event.registerSpriteSet((SimpleParticleType) PDParticles.CRACK_0_PARTICLE.particleType(), CrackParticle.Provider::new);
        event.registerSpriteSet((SimpleParticleType) PDParticles.WHITE_STAR_PARTICLE.particleType(), WhiteStarParticle.Provider::new);
        event.registerSpriteSet((SimpleParticleType) PDParticles.SNOWFLAKE_0_PARTICLE.particleType(), SnowflakeParticle.Provider::new);
        event.registerSpriteSet((SimpleParticleType) PDParticles.FEATHER_WHITE_PARTICLE.particleType(), FeatherWhiteParticle.Provider::new);
        event.registerSpriteSet((SimpleParticleType) PDParticles.DYEDREAM_0_PARTICLE.particleType(), DyedreamParticle.Provider::new);
        event.registerSpriteSet((SimpleParticleType) PDParticles.SHADOW_STONE_PARTICLE.particleType(), ShadowStoneParticle.Provider::new);
        event.registerSpriteSet((SimpleParticleType) PDParticles.SPORE_PARTICLE.particleType(), SporeParticle.Provider::new);
        event.registerSpriteSet((SimpleParticleType) PDParticles.FOX_FIRE_0_PARTICLE.particleType(), FoxFire0Particle.Provider::new);
        event.registerSpriteSet((SimpleParticleType) PDParticles.FOX_FIRE_1_PARTICLE.particleType(), FoxFire1Particle.Provider::new);

        // ===== 4.3 染梦世界动态环境粒子 Provider 注册 =====
        event.registerSpriteSet((SimpleParticleType) PDParticles.DREAM_SPORE.particleType(), DreamSporeParticle.Provider::new);
        event.registerSpriteSet((SimpleParticleType) PDParticles.CRYSTAL_SNOWFLAKE.particleType(), CrystalSnowflakeParticle.Provider::new);
        event.registerSpriteSet((SimpleParticleType) PDParticles.AURORA_GLOW.particleType(), AuroraGlowParticle.Provider::new);
        event.registerSpriteSet((SimpleParticleType) PDParticles.STARDUST.particleType(), StardustParticle.Provider::new);

        PasterDreamMod.LOGGER.debug("[ClientSetup] 粒子提供器注册完成，共 19 个粒子类型");
    }

    /**
     * 注册客户端流体扩展（融梦涌泉纹理）
     *
     * @param event 客户端扩展注册事件
     */
    @SubscribeEvent
    public static void registerFluidTypeExtensions(RegisterClientExtensionsEvent event) {
        event.registerFluidType(new IClientFluidTypeExtensions() {
            @Override
            public ResourceLocation getStillTexture() {
                return ResourceLocation.fromNamespaceAndPath("pasterdream", "block/meltdream_liquid_still");
            }

            @Override
            public ResourceLocation getFlowingTexture() {
                return ResourceLocation.fromNamespaceAndPath("pasterdream", "block/meltdream_liquid_flowing");
            }
        }, PDFluidsType.MELTDREAM_LIQUID_TYPE.get());
        PasterDreamMod.LOGGER.debug("[ClientSetup] 注册融梦涌泉流体类型客户端纹理");
    }

    /** 染梦维度群系的 ResourceKey 常量 */
    private static final ResourceKey<Biome> BIOME_DYEDREAM_0 = ResourceKey.create(
            Registries.BIOME, ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "biome_dyedream_0"));
    private static final ResourceKey<Biome> BIOME_DYEDREAM_1 = ResourceKey.create(
            Registries.BIOME, ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "biome_dyedream_1"));
    private static final ResourceKey<Biome> BIOME_DYEDREAM_2 = ResourceKey.create(
            Registries.BIOME, ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "biome_dyedream_2"));
    private static final ResourceKey<Biome> BIOME_DYEDREAM_3 = ResourceKey.create(
            Registries.BIOME, ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "biome_dyedream_3"));
    private static final ResourceKey<Biome> BIOME_DYEDREAM_DEEP_OCEAN = ResourceKey.create(
            Registries.BIOME, ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "biome_dyedream_deep_ocean"));
    private static final ResourceKey<Biome> BIOME_DYEDREAM_MUSHROOM_PLAINS = ResourceKey.create(
            Registries.BIOME, ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "biome_dyedream_mushroom_plains"));
    private static final ResourceKey<Biome> BIOME_DYEDREAM_SHORE = ResourceKey.create(
            Registries.BIOME, ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "biome_dyedream_shore"));
    private static final ResourceKey<Biome> BIOME_DYEDREAM_RIVER = ResourceKey.create(
            Registries.BIOME, ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "biome_dyedream_river"));
    private static final ResourceKey<Biome> BIOME_DYEDREAM_DENSE_FOREST = ResourceKey.create(
            Registries.BIOME, ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "biome_dyedream_dense_forest"));

    /**
     * 在三色间插值（白天色 → 黄昏色 → 夜色）
     *
     * @param day       白天雾色
     * @param sunset    黄昏雾色
     * @param night     夜晚雾色
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
     * 注册维度特殊效果（天空、雾色）
     */
    @SubscribeEvent
    public static void registerDimensionSpecialEffects(RegisterDimensionSpecialEffectsEvent event) {
        registerDyedreamWorldEffects(event);
        registerAaroncosArenaEffects(event);
    }

    /**
     * 注册染梦世界维度特殊效果
     */
    private static void registerDyedreamWorldEffects(RegisterDimensionSpecialEffectsEvent event) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "dyedream_world");
        event.register(id, new DimensionSpecialEffects(
                        192.0f,
                        true,
                        DimensionSpecialEffects.SkyType.NORMAL,
                        false,
                        false
                ) {
                    @Override
                    public Vec3 getBrightnessDependentFogColor(Vec3 fogColor, float sunHeight) {
                        ResourceKey<Biome> biome = PDClientEvents.currentBiomeKey;
                        Vec3 dayColor, sunsetColor, nightColor;

                        if (BIOME_DYEDREAM_0.equals(biome)) {
                            dayColor = new Vec3(1.0, 0.71, 0.85);
                            sunsetColor = new Vec3(1.0, 0.56, 0.64);
                            nightColor = new Vec3(0.29, 0.10, 0.36);
                        } else if (BIOME_DYEDREAM_1.equals(biome)) {
                            dayColor = new Vec3(0.66, 0.90, 0.64);
                            sunsetColor = new Vec3(0.83, 0.64, 0.45);
                            nightColor = new Vec3(0.10, 0.23, 0.16);
                        } else if (BIOME_DYEDREAM_2.equals(biome)) {
                            dayColor = new Vec3(0.71, 0.85, 1.0);
                            sunsetColor = new Vec3(0.64, 0.71, 0.83);
                            nightColor = new Vec3(0.10, 0.16, 0.36);
                        } else if (BIOME_DYEDREAM_3.equals(biome)) {
                            dayColor = new Vec3(0.64, 0.83, 0.90);
                            sunsetColor = new Vec3(0.83, 0.64, 0.64);
                            nightColor = new Vec3(0.04, 0.16, 0.23);
                        } else if (BIOME_DYEDREAM_DEEP_OCEAN.equals(biome)) {
                            dayColor = new Vec3(0.76, 0.64, 0.90);
                            sunsetColor = new Vec3(0.83, 0.53, 0.74);
                            nightColor = new Vec3(0.12, 0.04, 0.28);
                        } else if (BIOME_DYEDREAM_MUSHROOM_PLAINS.equals(biome)) {
                            dayColor = new Vec3(1.0, 0.82, 0.64);
                            sunsetColor = new Vec3(0.90, 0.64, 0.45);
                            nightColor = new Vec3(0.28, 0.16, 0.04);
                        } else if (BIOME_DYEDREAM_SHORE.equals(biome)) {
                            dayColor = new Vec3(0.71, 0.85, 1.0);
                            sunsetColor = new Vec3(0.83, 0.71, 0.83);
                            nightColor = new Vec3(0.16, 0.23, 0.36);
                        } else if (BIOME_DYEDREAM_RIVER.equals(biome)) {
                            dayColor = new Vec3(0.64, 0.78, 0.85);
                            sunsetColor = new Vec3(0.78, 0.64, 0.78);
                            nightColor = new Vec3(0.10, 0.16, 0.28);
                        } else if (BIOME_DYEDREAM_DENSE_FOREST.equals(biome)) {
                            dayColor = new Vec3(0.56, 0.71, 0.56);
                            sunsetColor = new Vec3(0.71, 0.56, 0.64);
                            nightColor = new Vec3(0.08, 0.16, 0.10);
                        } else {
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
                }
        );
    }

    /**
     * 注册亚伦柯斯竞技场维度特殊效果
     */
    private static void registerAaroncosArenaEffects(RegisterDimensionSpecialEffectsEvent event) {
        ResourceLocation arenaId = ResourceLocation.fromNamespaceAndPath(
                PasterDreamMod.MOD_ID, "aaroncos_arena_world");
        event.register(arenaId, new DimensionSpecialEffects(
                        Float.NaN,
                        true,
                        DimensionSpecialEffects.SkyType.NONE,
                        false,
                        false
                ) {
                    @Override
                    public Vec3 getBrightnessDependentFogColor(Vec3 fogColor, float sunHeight) {
                        return new Vec3(0.2, 0.2, 0.2);
                    }

                    @Override
                    public boolean isFoggyAt(int x, int y) {
                        return true;
                    }

                    @Override
                    @Nullable
                    public float[] getSunriseColor(float timeOfDay, float partialTick) {
                        return null;
                    }
                }
        );
    }
}