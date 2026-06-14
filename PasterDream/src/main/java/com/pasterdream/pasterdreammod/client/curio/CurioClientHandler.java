package com.pasterdream.pasterdreammod.client.curio;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.api.curio.CurioAPI;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

/**
 * 饰品客户端处理器 —— 负责初始化饰品客户端桥接
 * <p>
 * 在客户端初始化时调用 {@link #init()} 自动注册桥接实例，
 * 同时将所有配置了自定义渲染器的饰品注册到 Curios API 的渲染体系中。
 * </p>
 * <p>
 * 渲染器注册时机（按 Curios API 9.x 规范）：
 * <ol>
 *   <li>在 {@code FMLClientSetupEvent} 中调用 {@link #init()}</li>
 *   <li>该方法会遍历所有已配置 {@code .renderer(...)} 的饰品，
 *       并调用 {@link CuriosRendererRegistry#register(Item, java.util.function.Supplier)}</li>
 *   <li>Curios API 会在 {@code EntityRenderersEvent.AddLayers} 中实例化并加载渲染器</li>
 * </ol>
 * </p>
 */
public class CurioClientHandler implements CurioAPI.CurioClientBridge {

    /** 单例 */
    private static final CurioClientHandler INSTANCE = new CurioClientHandler();

    private CurioClientHandler() {}

    /**
     * 初始化饰品客户端系统。
     * <p>
     * 应在客户端初始化时调用：
     * <pre>{@code
     * CurioClientHandler.init();
     * }</pre>
     * </p>
     */
    public static void init() {
        CurioAPI.setClientBridge(INSTANCE);
        INSTANCE.registerAll();
    }

    @Override
    public void registerAll() {
        // 1. 遍历所有配置了渲染器的饰品，注册到 CuriosRendererRegistry
        for (var entry : CurioAPI.getRendererSuppliers().entrySet()) {
            String fullName = entry.getKey();
            ResourceLocation id = ResourceLocation.parse(fullName);
            Item item = BuiltInRegistries.ITEM.get(id);

            if (item != Items.AIR) {
                CuriosRendererRegistry.register(item, entry.getValue());
                PasterDreamMod.LOGGER.info(
                        "[CurioClient] 已注册饰品渲染器: {}",
                        fullName);
            } else {
                PasterDreamMod.LOGGER.warn(
                        "[CurioClient] 饰品渲染器注册失败，未找到物品: {}",
                        fullName);
            }
        }

        // 2. 遍历所有配置了渲染的饰品并输出日志
        for (var registration : CurioAPI.getRegisteredCurios()) {
            if (!"none".equals(registration.renderType())) {
                PasterDreamMod.LOGGER.info(
                        "[CurioClient] 饰品 {} 配置了渲染类型: {}",
                        registration.fullName(),
                        registration.renderType());
            }
        }
    }
}