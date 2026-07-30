package com.pasterdream.pasterdreammod.api;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

import com.pasterdream.pasterdreammod.api.util.PDDebugLogger;
/**
 * PasterDreamAPI 模组主类。
 * <p>
 * 作为前置库模组加载，负责统一注册 API 层的所有 DeferredRegister。
 * 下游模组（PasterDream / PasterDreamSanity / PasterDreamMeltDream / PasterDreamSpells）
 * 只需在 Gradle 中 {@code implementation project(':PasterDreamAPI')}，
 * 并在构造器中调用 {@link PasterDreamAPI#registerAll(IEventBus)} 即可完成注册。
 */
@Mod(PasterDreamAPI.MOD_ID)
public class PasterDreamAPIMod {

    /**
     * 构造函数。
     *
     * @param modEventBus NeoForge 模组事件总线
     */
    public PasterDreamAPIMod(IEventBus modEventBus) {
        PasterDreamAPI.registerAll(modEventBus);
        PDDebugLogger.apiInfo("PasterDreamAPI 前置库已加载");
    }
}
