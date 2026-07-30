package com.pasterdream.pasterdreammod.client.curio;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.api.curio.CurioAPI;
import com.pasterdream.pasterdreammod.api.curio.DefaultCurioClientBridge;

import com.pasterdream.pasterdreammod.api.util.PDDebugLogger;
/**
 * 饰品客户端处理器 —— 注册默认桥接并触发渲染器挂接。
 * <p>
 * 遍历逻辑在 API {@link DefaultCurioClientBridge}；此处仅接主模日志。
 */
public final class CurioClientHandler {

    private CurioClientHandler() {
    }

    /**
     * 在 {@code FMLClientSetupEvent} 中调用。
     */
    public static void init() {
        DefaultCurioClientBridge bridge = new DefaultCurioClientBridge(
                name -> PasterDreamMod.LOGGER.debug("[CurioClient] 已注册饰品渲染器: {}", name),
                name -> PasterDreamMod.LOGGER.warn("[CurioClient] 饰品渲染器注册失败，未找到物品: {}", name)
        );
        CurioAPI.setClientBridge(bridge);
        bridge.registerAll();


        for (var registration : CurioAPI.getRegisteredCurios()) {
            if (!"none".equals(registration.renderType())) {
                PDDebugLogger.mainDebug(
                        "[CurioClient] 饰品 {} 配置了渲染类型: {}",
                        registration.fullName(),
                        registration.renderType());
            }
        }
    }
}
