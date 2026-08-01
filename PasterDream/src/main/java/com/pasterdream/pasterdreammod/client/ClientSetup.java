package com.pasterdream.pasterdreammod.client;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.client.model.Modelslime;
import com.pasterdream.pasterdreammod.client.model.SporeEntityModel;
import com.pasterdream.pasterdreammod.client.gui.config.PDConfigScreen;
import com.pasterdream.pasterdreammod.client.particle.*;
import com.pasterdream.pasterdreammod.client.particle.AuroraGlowParticle;
import com.pasterdream.pasterdreammod.client.particle.CrystalSnowflakeParticle;
import com.pasterdream.pasterdreammod.client.particle.DreamSporeParticle;
import com.pasterdream.pasterdreammod.client.particle.StardustParticle;
import com.pasterdream.pasterdreammod.client.renderer.RendererRegistry;
import com.pasterdream.pasterdreammod.client.screen.DreamAccumulatorScreen;
import com.pasterdream.pasterdreammod.client.screen.DreamCauldronScreen;
import com.pasterdream.pasterdreammod.client.screen.ResearchTableScreen;
import com.pasterdream.pasterdreammod.client.screen.ShadowBlastFurnaceScreen;
import com.pasterdream.pasterdreammod.client.screen.ShadowSelectEndScreen;
import com.pasterdream.pasterdreammod.client.screen.StorageBagScreen;
import com.pasterdream.pasterdreammod.registry.PDBiomes;
import com.pasterdream.pasterdreammod.registry.PDBlocks;
import com.pasterdream.pasterdreammod.client.screen.DyedreamDeskScreen;
import com.pasterdream.pasterdreammod.client.screen.MeltdreamChestScreen;
import com.pasterdream.pasterdreammod.client.screen.ShadowChestScreen;
import com.pasterdream.pasterdreammod.client.screen.TheEndlessBookOfDreamSeekersScreen;
import com.pasterdream.pasterdreammod.client.screen.PlayerBookScreen;
import com.pasterdream.pasterdreammod.client.screen.WeaponWorkshopScreen;
import com.pasterdream.pasterdreammod.client.screen.WorkshopAnvilScreen;
import com.pasterdream.pasterdreammod.client.screen.WorkshopBlastScreen;
import com.pasterdream.pasterdreammod.client.curio.CurioClientHandler;
import com.pasterdream.pasterdreammod.registry.PDMenus;
import com.pasterdream.pasterdreammod.registry.PDParticles;
import com.pasterdream.pasterdreammod.registry.PDFluidsType;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterDimensionSpecialEffectsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.jetbrains.annotations.Nullable;

import com.pasterdream.pasterdreammod.api.util.PDDebugLogger;
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
            PDDebugLogger.mainDebug("[ClientSetup] 饰品身体渲染器初始化完成");

            // playeranimator 为 optional：仅在场时再触达 PDPlayerAnimation（类上有硬依赖符号）。
            // 判断必须用字面量 modId，不可写 PDPlayerAnimation.常量——否则 getstatic 会先加载该类。
            if (ModList.get().isLoaded("playeranimator")) {
                PDPlayerAnimation.bootstrapIfPresent();
            } else {
                PDDebugLogger.mainDebug("[ClientSetup] 未检测到 playeranimator，跳过闪避姿势集成");
            }

            // 内嵌 UI 资源包状态同步已移至 PDPackHandler.onPlayerLogin（玩家登录后执行）：
            // FMLClientSetup 阶段 PackRepository 尚未就绪，早期调用会静默无效。

            // 注册模组配置界面：在 Mod 列表点击“配置”按钮时打开 PDConfigScreen
            ModList.get().getModContainerById(PasterDreamMod.MOD_ID).ifPresent(container ->
                    container.registerExtensionPoint(IConfigScreenFactory.class,
                            (IConfigScreenFactory) (modContainer, modListScreen) -> new PDConfigScreen(modListScreen))
            );
            PDDebugLogger.mainDebug("[ClientSetup] 配置界面工厂已注册");
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
        PDDebugLogger.mainDebug("[ClientSetup] 注册模型层: {}", Modelslime.LAYER_LOCATION);

        event.registerLayerDefinition(SporeEntityModel.LAYER_LOCATION, SporeEntityModel::createBodyLayer);
        PDDebugLogger.mainDebug("[ClientSetup] 注册模型层: {}", SporeEntityModel.LAYER_LOCATION);
    }

    /**
     * 注册树叶颜色提供者，使树叶根据群系显示不同颜色
     *
     * @param event 颜色处理器注册事件
     */
    @SubscribeEvent
    public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
        event.register((state, level, pos, tintIndex) -> {
            if (level == null || pos == null) {
                return -145678;
            }
            if (level instanceof Level) {
                return ((Level) level).getBiome(pos).value().getFoliageColor();
            }
            return -145678;
        }, PDBlocks.DYEDREAM_LEAVES.get());
        PDDebugLogger.mainDebug("[ClientSetup] 注册树叶颜色提供者: dyedream_leaves");
    }

    /**
     * 注册 GUI 屏幕
     *
     * @param event 菜单屏幕注册事件
     */
    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(PDMenus.SHADOW_CHEST.get(), ShadowChestScreen::new);
        PDDebugLogger.mainDebug("[ClientSetup] 注册 GUI 屏幕: shadow_chest → ShadowChestScreen");

        event.register(PDMenus.MELTDREAM_CHEST.get(), MeltdreamChestScreen::new);
        PDDebugLogger.mainDebug("[ClientSetup] 注册 GUI 屏幕: meltdream_chest → MeltdreamChestScreen");

        event.register(PDMenus.DYEDREAM_DESK.get(), DyedreamDeskScreen::new);
        PDDebugLogger.mainDebug("[ClientSetup] 注册 GUI 屏幕: dyedream_desk → DyedreamDeskScreen");

        event.register(PDMenus.DREAM_CAULDRON.get(), DreamCauldronScreen::new);
        PDDebugLogger.mainDebug("[ClientSetup] 注册 GUI 屏幕: dream_cauldron → DreamCauldronScreen");

        event.register(PDMenus.THE_ENDLESS_BOOK_OF_DREAM_SEEKERS.get(), TheEndlessBookOfDreamSeekersScreen::new);
        PDDebugLogger.mainDebug("[ClientSetup] 注册 GUI 屏幕: the_endless_book_of_dream_seekers → TheEndlessBookOfDreamSeekersScreen");

        event.register(PDMenus.PLAYER_BOOK.get(), PlayerBookScreen::new);
        PDDebugLogger.mainDebug("[ClientSetup] 注册 GUI 屏幕: player_book → PlayerBookScreen");

        // ==================== [分区W] 武器工坊群 ====================

        event.register(PDMenus.WEAPON_WORKSHOP.get(), WeaponWorkshopScreen::new);
        PDDebugLogger.mainDebug("[ClientSetup] 注册 GUI 屏幕: weapon_workshop → WeaponWorkshopScreen");

        event.register(PDMenus.WORKSHOP_ANVIL.get(), WorkshopAnvilScreen::new);
        PDDebugLogger.mainDebug("[ClientSetup] 注册 GUI 屏幕: workshop_anvil → WorkshopAnvilScreen");

        event.register(PDMenus.WORKSHOP_BLAST.get(), WorkshopBlastScreen::new);
        PDDebugLogger.mainDebug("[ClientSetup] 注册 GUI 屏幕: workshop_blast → WorkshopBlastScreen");

        // ==================== [分区R] 研究台组 ====================

        event.register(PDMenus.RESEARCH_TABLE.get(), ResearchTableScreen::new);
        PDDebugLogger.mainDebug("[ClientSetup] 注册 GUI 屏幕: research_table → ResearchTableScreen");

        event.register(PDMenus.SHADOW_BLAST_FURNACE.get(), ShadowBlastFurnaceScreen::new);
        PDDebugLogger.mainDebug("[ClientSetup] 注册 GUI 屏幕: shadow_blast_furnace → ShadowBlastFurnaceScreen");

        event.register(PDMenus.DREAM_ACCUMULATOR.get(), DreamAccumulatorScreen::new);
        PDDebugLogger.mainDebug("[ClientSetup] 注册 GUI 屏幕: dream_accumulator → DreamAccumulatorScreen");

        event.register(PDMenus.SHADOW_SELECT_END.get(), ShadowSelectEndScreen::new);
        PDDebugLogger.mainDebug("[ClientSetup] 注册 GUI 屏幕: shadow_select_end → ShadowSelectEndScreen");

        event.register(PDMenus.STORAGE_BAG.get(), StorageBagScreen::new);
        event.register(PDMenus.STORAGE_BAG_0.get(), StorageBagScreen::new);
        PDDebugLogger.mainDebug("[ClientSetup] 注册 GUI 屏幕: storage_bag / storage_bag_0 → StorageBagScreen");
    }

    /**
     * 注册粒子提供器
     *
     * @param event 粒子提供器注册事件
     */
    @SubscribeEvent
    public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        PDDebugLogger.mainDebug("[ClientSetup] 开始注册粒子提供器...");

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
        // 萤火虫粒子（wind_journey_biome_0 环境粒子，10 帧全亮度萤光）
        event.registerSpriteSet((SimpleParticleType) PDParticles.FIREFLY_PARTICLE.particleType(), FireflyParticle.Provider::new);

        // ===== 4.3 染梦世界动态环境粒子 Provider 注册 =====
        event.registerSpriteSet((SimpleParticleType) PDParticles.DREAM_SPORE.particleType(), DreamSporeParticle.Provider::new);
        event.registerSpriteSet((SimpleParticleType) PDParticles.CRYSTAL_SNOWFLAKE.particleType(), CrystalSnowflakeParticle.Provider::new);
        event.registerSpriteSet((SimpleParticleType) PDParticles.AURORA_GLOW.particleType(), AuroraGlowParticle.Provider::new);
        event.registerSpriteSet((SimpleParticleType) PDParticles.STARDUST.particleType(), StardustParticle.Provider::new);

        // ===== 4.4 梦境炼药锅炼制粒子 =====
        event.registerSpriteSet((SimpleParticleType) PDParticles.DUST_0_PARTICLE.particleType(), Dust0Particle.Provider::new);

        // ===== 4.5 法杖武器与战斗粒子（W2-D，还原自原版法杖战斗模块） =====
        event.registerSpriteSet((SimpleParticleType) PDParticles.ATTACK_0_PARTICLE.particleType(), Attack0Particle.Provider::new);
        event.registerSpriteSet((SimpleParticleType) PDParticles.BUFF_0_PARTICLE.particleType(), Buff0Particle.Provider::new);
        event.registerSpriteSet((SimpleParticleType) PDParticles.FIREFLY_GLASS_JAR_PARTICLE.particleType(), FireflyGlassJarParticle.Provider::new);
        event.registerSpriteSet((SimpleParticleType) PDParticles.GOLDEN_PARTICLE.particleType(), GoldenParticle.Provider::new);
        event.registerSpriteSet((SimpleParticleType) PDParticles.LIGHT_FIREFLY_GLASS_JAR_PARTICLE.particleType(), LightFireflyGlassJarParticle.Provider::new);
        event.registerSpriteSet((SimpleParticleType) PDParticles.LIGHTNING_PARTICLE.particleType(), LightningParticle.Provider::new);
        event.registerSpriteSet((SimpleParticleType) PDParticles.MELTDREAM_CRYSTAL_BIG_PARTICLE.particleType(), MeltdreamCrystalBigParticle.Provider::new);
        event.registerSpriteSet((SimpleParticleType) PDParticles.SCULK_PARTICLE.particleType(), SculkParticle.Provider::new);
        event.registerSpriteSet((SimpleParticleType) PDParticles.SOUL_PARTICLE.particleType(), SoulParticle.Provider::new);
        event.registerSpriteSet((SimpleParticleType) PDParticles.SQUEAL_WAVE_PARTICLE.particleType(), SquealWaveParticle.Provider::new);
        event.registerSpriteSet((SimpleParticleType) PDParticles.STARCALL_PARTICLE.particleType(), StarcallParticle.Provider::new);
        event.registerSpriteSet((SimpleParticleType) PDParticles.TERRASWORD_WAVE_PARTICLE.particleType(), TerraswordWaveParticle.Provider::new);

        PDDebugLogger.mainDebug("[ClientSetup] 粒子提供器注册完成，共 39 个粒子类型");
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
        PDDebugLogger.mainDebug("[ClientSetup] 注册融梦涌泉流体类型客户端纹理");

        // ===== 熔融阴影流体纹理（波次C） =====
        event.registerFluidType(new IClientFluidTypeExtensions() {
            @Override
            public ResourceLocation getStillTexture() {
                return ResourceLocation.fromNamespaceAndPath("pasterdream", "block/shadow_liquid_still");
            }

            @Override
            public ResourceLocation getFlowingTexture() {
                return ResourceLocation.fromNamespaceAndPath("pasterdream", "block/shadow_liquid_flowing");
            }
        }, PDFluidsType.SHADOW_LIQUID_TYPE.get());
        PDDebugLogger.mainDebug("[ClientSetup] 注册熔融阴影流体类型客户端纹理");
    }

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

                        if (PDBiomes.BIOME_DYEDREAM_0.equals(biome)) {
                            dayColor = new Vec3(1.0, 0.71, 0.85);
                            sunsetColor = new Vec3(1.0, 0.56, 0.64);
                            nightColor = new Vec3(0.29, 0.10, 0.36);
                        } else if (PDBiomes.BIOME_DYEDREAM_1.equals(biome)) {
                            dayColor = new Vec3(0.66, 0.90, 0.64);
                            sunsetColor = new Vec3(0.83, 0.64, 0.45);
                            nightColor = new Vec3(0.10, 0.23, 0.16);
                        } else if (PDBiomes.BIOME_DYEDREAM_2.equals(biome)) {
                            dayColor = new Vec3(0.71, 0.85, 1.0);
                            sunsetColor = new Vec3(0.64, 0.71, 0.83);
                            nightColor = new Vec3(0.10, 0.16, 0.36);
                        } else if (PDBiomes.BIOME_DYEDREAM_3.equals(biome)) {
                            dayColor = new Vec3(0.64, 0.83, 0.90);
                            sunsetColor = new Vec3(0.83, 0.64, 0.64);
                            nightColor = new Vec3(0.04, 0.16, 0.23);
                        } else if (PDBiomes.BIOME_DYEDREAM_DEEP_OCEAN.equals(biome)) {
                            dayColor = new Vec3(0.76, 0.64, 0.90);
                            sunsetColor = new Vec3(0.83, 0.53, 0.74);
                            nightColor = new Vec3(0.12, 0.04, 0.28);
                        } else if (PDBiomes.BIOME_DYEDREAM_MUSHROOM_PLAINS.equals(biome)) {
                            dayColor = new Vec3(1.0, 0.82, 0.64);
                            sunsetColor = new Vec3(0.90, 0.64, 0.45);
                            nightColor = new Vec3(0.28, 0.16, 0.04);
                        } else if (PDBiomes.BIOME_DYEDREAM_SHORE.equals(biome)) {
                            dayColor = new Vec3(0.71, 0.85, 1.0);
                            sunsetColor = new Vec3(0.83, 0.71, 0.83);
                            nightColor = new Vec3(0.16, 0.23, 0.36);
                        } else if (PDBiomes.BIOME_DYEDREAM_RIVER.equals(biome)) {
                            dayColor = new Vec3(0.64, 0.78, 0.85);
                            sunsetColor = new Vec3(0.78, 0.64, 0.78);
                            nightColor = new Vec3(0.10, 0.16, 0.28);
                        } else if (PDBiomes.BIOME_DYEDREAM_DENSE_FOREST.equals(biome)) {
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
     * <p>
     * 对齐原模组 {@code AaroncosArenaWorldDimension}：无天空盒（SkyType.NONE）+ 灰色雾，
     * 雾色取原模组配置的 (0.2, 0.2, 0.2)，与主世界遗迹区的暗色 {@code aaroncos_arena_biome} 区分开。
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
                        // 灰色雾色（对齐原模组 AaroncosArenaWorldDimension）
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