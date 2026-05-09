package com.pasterdream.pasterdreammod.client;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.client.model.Modelslime;
import com.pasterdream.pasterdreammod.client.particle.LifeCrystalParticle;
import com.pasterdream.pasterdreammod.client.renderer.block.DreamAccumulatorBlockRenderer;
import com.pasterdream.pasterdreammod.client.renderer.block.LifeCrystalBlockRenderer;
import com.pasterdream.pasterdreammod.client.renderer.block.ShadowChestBlockRenderer;
import com.pasterdream.pasterdreammod.client.renderer.entity.PinkSlimeRenderer;
import com.pasterdream.pasterdreammod.client.renderer.entity.ShadowGolemRenderer;
import com.pasterdream.pasterdreammod.client.screen.ShadowChestScreen;
import com.pasterdream.pasterdreammod.registry.PDBlockEntities;
import com.pasterdream.pasterdreammod.registry.PDEntities;
import com.pasterdream.pasterdreammod.registry.PDMenus;
import com.pasterdream.pasterdreammod.registry.PDParticles;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

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

        // 注册生命水晶方块实体渲染器
        event.registerBlockEntityRenderer(
                PDBlockEntities.LIFE_CRYSTAL.get(),
                LifeCrystalBlockRenderer::new
        );

        // 注册影之箱方块实体渲染器
        event.registerBlockEntityRenderer(
                PDBlockEntities.SHADOW_CHEST.get(),
                ShadowChestBlockRenderer::new
        );

        // 注册暗影魔像实体渲染器
        event.registerEntityRenderer(
                PDEntities.SHADOW_GOLEM.get(),
                ShadowGolemRenderer::new
        );

        // 注册粉色史莱姆实体渲染器
        event.registerEntityRenderer(
                PDEntities.PINK_SLIME.get(),
                PinkSlimeRenderer::new
        );
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
    }

    /**
     * 注册粒子提供器
     * 在 RegisterParticleProvidersEvent 事件时调用
     *
     * @param event 粒子提供器注册事件
     */
    @SubscribeEvent
    public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(PDParticles.MELTDREAM_CRYSTAL_PARTICLE.get(), LifeCrystalParticle.Provider::new);
    }
}
