package com.pasterdream.pasterdreammod.api.client.effect.post;

import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * 后处理链（{@link PostChain}）管理器 —— 统一注册、惰性实例化、缩放与重载
 * <p>
 * 借鉴开源模组 FDLib（作者 FINDERFEED）的 {@code FDPostShadersHandler} 设计思路
 * （独立实现，非复制）：
 * <ul>
 *   <li><b>registerChain</b>：记录 {@code id → 加载器}，由主模在客户端初始化时注册；
 *       加载器为 {@link Supplier}，<b>惰性执行</b>——只在渲染线程首次访问时才
 *       {@code new PostChain(...)}，确保 OpenGL 上下文就绪；</li>
 *   <li><b>getChain</b>：按 id 获取已实例化链（未实例化则惰性创建）；</li>
 *   <li><b>resizeAllIfNeeded</b>：窗口尺寸变化时对每个 PostChain 调用 {@code resize}；</li>
 *   <li><b>requestReload / processPendingReload</b>：资源重载（F3+T）时
 *       置位标记，由渲染线程 tick 消费并销毁/重实例化 PostChain；
 *       {@code PostChain.close()} 内部调用 {@code RenderTarget.destroyBuffers()}，
 *       属 OpenGL 操作，严禁在资源加载线程执行；</li>
 *   <li><b>processLevel / processScreen</b>：渲染阶段驱动，供特效 handler 调用。</li>
 * </ul>
 * <p>
 * 本类为客户端专用（引用 {@code net.minecraft.client.renderer.PostChain}），
 * 仅由 {@code api/client/**} 路径持有，主模客户端挂载点负责把事件转发进来。
 *
 * @see PostShaderEvent
 */
@OnlyIn(Dist.CLIENT)
public final class PostShaderManager {

    /** 后处理链加载器注册表：id → 加载器（惰性，仅记录） */
    private static final Map<ResourceLocation, Supplier<PostChain>> CHAIN_LOADERS = new ConcurrentHashMap<>();

    /** 已实例化的后处理链缓存：id → PostChain */
    private static final Map<ResourceLocation, PostChain> CHAINS = new ConcurrentHashMap<>();

    /** 当前窗口尺寸（用于检测 resize） */
    private static int lastWidth = -1;
    private static int lastHeight = -1;

    /** 资源重载待处理标记：资源加载线程置位，渲染线程 tick 消费 */
    private static volatile boolean reloadPending = false;

    private PostShaderManager() {
        throw new UnsupportedOperationException("PostShaderManager 是纯静态门面类，不可实例化");
    }

    /**
     * 注册一个后处理链加载器（客户端初始化时调用）
     * <p>
     * 加载器应为纯惰性 {@code Supplier}，在首次渲染线程访问时才
     * {@code new PostChain(...)}。切勿在注册时立即执行，否则 OpenGL
     * 上下文可能未就绪。
     *
     * @param id     后处理链唯一标识（如 {@code pasterdream:shaders/post/impact_frame.json}）
     * @param loader PostChain 惰性加载器
     */
    public static void registerChain(ResourceLocation id, Supplier<PostChain> loader) {
        CHAIN_LOADERS.put(id, loader);
    }

    /**
     * 获取已实例化的后处理链（未实例化则惰性创建）
     *
     * @param id 后处理链标识
     * @return PostChain 或 {@code null}（未注册 / 创建失败）
     */
    public static PostChain getChain(ResourceLocation id) {
        PostChain chain = CHAINS.get(id);
        if (chain == null) {
            Supplier<PostChain> loader = CHAIN_LOADERS.get(id);
            if (loader == null) {
                return null;
            }
            try {
                chain = loader.get();
                CHAINS.put(id, chain);
                resizeChain(id, chain);
            } catch (RuntimeException e) {
                // 创建失败（如 shader 语法错误）：从加载器表移除，避免每帧重试
                CHAIN_LOADERS.remove(id);
                com.pasterdream.pasterdreammod.api.util.PDDebugLogger.apiInfo(
                        "[PostShaderManager] 后处理链加载失败: {} ({})", id, e.getMessage());
                return null;
            }
        }
        return chain;
    }

    /**
     * 是否已注册指定后处理链
     *
     * @param id 后处理链标识
     * @return 已注册返回 {@code true}
     */
    public static boolean isRegistered(ResourceLocation id) {
        return CHAIN_LOADERS.containsKey(id) || CHAINS.containsKey(id);
    }

    /**
     * 窗口尺寸变化时统一缩放所有已实例化后处理链（客户端 tick 时调用）
     */
    public static void resizeAllIfNeeded() {
        Window window = Minecraft.getInstance().getWindow();
        int width = window.getWidth();
        int height = window.getHeight();
        if (width != lastWidth || height != lastHeight) {
            for (PostChain chain : CHAINS.values()) {
                chain.resize(width, height);
            }
            lastWidth = width;
            lastHeight = height;
        }
    }

    /**
     * 请求资源重载（任意线程可调用，如 {@code AddReloadListenerEvent} 的资源加载线程）
     * <p>
     * 仅置位待处理标记，真正的 PostChain 销毁由渲染线程的
     * {@link #processPendingReload()} 执行。切勿在此直接销毁：
     * {@code PostChain.close()} 内部会调用 {@code RenderTarget.destroyBuffers()}，
     * 属于 OpenGL 操作，必须在渲染线程执行，否则在资源加载线程（
     * {@code Util.backgroundExecutor()} 的 Worker 线程）触发会因 GL 上下文
     * 归属校验失败而崩溃。
     *
     * @see #processPendingReload()
     */
    public static void requestReload() {
        reloadPending = true;
    }

    /**
     * 渲染线程 tick 中消费待处理的重载请求（客户端 tick 时调用）
     * <p>
     * 销毁全部已实例化后处理链并清空缓存、重置窗口尺寸记录；下次
     * {@link #getChain} 访问时用重载后的资源惰性重建。本方法必须在
     * 渲染线程执行（{@code PostChain.close()} 属 OpenGL 操作）。
     */
    public static void processPendingReload() {
        if (!reloadPending) {
            return;
        }
        reloadPending = false;
        for (PostChain chain : CHAINS.values()) {
            chain.close();
        }
        CHAINS.clear();
        lastWidth = -1;
        lastHeight = -1;
    }

    /**
     * 清除全部后处理链（玩家登出/客户端卸载时清理）
     * <p>
     * 注意：{@code PostChain.close()} 属 OpenGL 操作，本方法必须在渲染线程执行。
     */
    public static void clearAll() {
        for (PostChain chain : CHAINS.values()) {
            chain.close();
        }
        CHAINS.clear();
        CHAIN_LOADERS.clear();
        lastWidth = -1;
        lastHeight = -1;
    }

    /**
     * 测试辅助：清空注册表与实例缓存（不关闭 PostChain，仅清引用）
     */
    public static void resetForTesting() {
        CHAINS.clear();
        CHAIN_LOADERS.clear();
        lastWidth = -1;
        lastHeight = -1;
    }

    /** 为单个链应用当前窗口尺寸 */
    private static void resizeChain(ResourceLocation id, PostChain chain) {
        Window window = Minecraft.getInstance().getWindow();
        chain.resize(window.getWidth(), window.getHeight());
    }
}
