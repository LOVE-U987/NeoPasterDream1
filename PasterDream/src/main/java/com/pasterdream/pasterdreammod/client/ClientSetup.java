package com.pasterdream.pasterdreammod.client;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.client.model.Modelslime;
import com.pasterdream.pasterdreammod.client.model.SporeEntityModel;
import com.pasterdream.pasterdreammod.client.gui.config.PDConfigScreen;
import com.pasterdream.pasterdreammod.client.particle.*;
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
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
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

import com.pasterdream.pasterdreammod.api.client.block.BlockTintClient;
import com.pasterdream.pasterdreammod.api.client.shading.BiomeShadingAPI;
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

            // 注册生物群系着色默认值
            PDShadingRegistration.registerDefaults();
            PDDebugLogger.mainDebug("[ClientSetup] 生物群系着色注册完成");

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
     * 染梦树叶基础颜色（原彩色纹理的平均色 146,85,127 = 0x92557F）。
     * <p>注意：Minecraft 的 BlockColor/ItemColor 返回值为 ARGB 格式，
     * 必须携带 alpha 位（0xFF000000 | rgb），否则 alpha=0 会渲染为完全透明。
     * 用于物品图标显示与无世界位置时的回退（固定显示基础粉紫）。
     */
    /**
     * 注册方块颜色提供者：委托给 {@link BlockTintClient}，按 BlockAPI 配置自动注册染色方块。
     * <p>染梦树叶在 PDBlocks 中声明了 {@code tintFoliage()}，走原版 {@link BiomeColors#getAverageFoliageColor}
     * 取群系 foliage_color（数据驱动，兼容 Sodium/Iris）。
     *
     * @param event 颜色处理器注册事件
     */
    @SubscribeEvent
    public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
        BlockTintClient.registerBlockTints(event);
        PDDebugLogger.mainDebug("[ClientSetup] 注册方块颜色提供者（BlockAPI tint）");
    }

    /**
     * 注册物品颜色提供者：委托给 {@link BlockTintClient}（物品无世界位置，固定显示配置色）
     *
     * @param event 物品颜色处理器注册事件
     */
    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        BlockTintClient.registerItemTints(event);
        PDDebugLogger.mainDebug("[ClientSetup] 注册物品颜色提供者（BlockAPI tint）");
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
     * 注册维度特殊效果（天空、雾色）
     */
    @SubscribeEvent
    public static void registerDimensionSpecialEffects(RegisterDimensionSpecialEffectsEvent event) {
        registerDyedreamWorldEffects(event);
        registerAaroncosArenaEffects(event);
        registerColdDomainWorldEffects(event);
    }

    /**
     * 注册冷域维度特殊效果
     * <p>
     * 冷色天空/雾气：白天淡冰蓝，黄昏蓝紫，夜晚深蓝黑。
     */
    private static void registerColdDomainWorldEffects(RegisterDimensionSpecialEffectsEvent event) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "cold_domain_world");
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
                        return BiomeShadingAPI.interpolateColor(biome, sunHeight);
                    }

                    @Override
                    @Nullable
                    public float[] getSunriseColor(float timeOfDay, float partialTick) {
                        float sunHeight = (float) Math.sin(timeOfDay * 2.0 * Math.PI);
                        if (sunHeight < -0.1f || sunHeight > 0.2f) return null;

                        float fade = (sunHeight + 0.1f) / 0.3f;
                        float alpha = (float) Math.sin(fade * Math.PI) * 0.45f;

                        return new float[]{0.72f, 0.85f, 1.0f, alpha};
                    }

                    @Override
                    public boolean isFoggyAt(int x, int y) {
                        return false;
                    }
                }
        );
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
                        return BiomeShadingAPI.interpolateColor(biome, sunHeight);
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