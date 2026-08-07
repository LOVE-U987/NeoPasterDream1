package com.pasterdream.pasterdreammod.client.effect;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.api.client.effect.post.PostShaderManager;
import com.pasterdream.pasterdreammod.api.client.effect.screen.instances.BuiltinScreenEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;

import java.io.IOException;

/**
 * 后处理着色器（PostChain）加载引导 —— 客户端 MOD 总线
 * <p>
 * 在客户端初始化时把主模自带的后处理链加载器注册进 API 的
 * {@link PostShaderManager}。加载器为惰性 {@code Supplier}，真正
 * {@code new PostChain(...)} 在渲染线程首次访问时才执行（OpenGL
 * 上下文就绪），并由 {@code PostShaderManager} 缓存实例。
 * <p>
 * 内置链：{@code pasterdream:impact_frame}（打击帧灰闪）。
 * <p>
 * F3+T 资源重载时经 {@link RegisterClientReloadListenersEvent} 触发的
 * 重载逻辑由 {@code PDEffectClientEvents} 调 {@code PostShaderManager.reloadAll()}。
 */
@EventBusSubscriber(modid = PasterDreamMod.MOD_ID, value = Dist.CLIENT)
public class PDShaderBootstrap {

    /** impact_frame 后处理链资源位置 */
    public static final ResourceLocation IMPACT_FRAME_CHAIN =
            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "shaders/post/impact_frame.json");

    private PDShaderBootstrap() {
    }

    /**
     * 客户端设置完成后注册后处理链加载器与内置特效类型
     *
     * @param event 客户端设置事件
     */
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        // 惰性加载器：渲染线程首次 getChain 时才执行 new PostChain
        PostShaderManager.registerChain(IMPACT_FRAME_CHAIN, PDShaderBootstrap::createImpactFrameChain);
        // 内置屏幕特效类型注册（客户端专用）
        BuiltinScreenEffects.registerAll();
    }

    /**
     * 创建 impact_frame 后处理链（惰性调用，须在渲染线程执行）
     *
     * @return 已实例化的 PostChain
     */
    private static PostChain createImpactFrameChain() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.getMainRenderTarget() == null) {
            throw new IllegalStateException("主渲染目标未就绪，延迟到下次渲染");
        }
        try {
            return new PostChain(
                    mc.getTextureManager(),
                    mc.getResourceManager(),
                    mc.getMainRenderTarget(),
                    IMPACT_FRAME_CHAIN
            );
        } catch (IOException e) {
            throw new RuntimeException("[PasterDream] 无法加载 impact_frame 后处理链", e);
        }
    }
}
