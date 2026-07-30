package com.pasterdream.pasterdreammod.pasterdreamsanity;

import com.pasterdream.pasterdreammod.api.san.SanConfigRegistry;
import com.pasterdream.pasterdreammod.api.util.PDDebugLogger;
import com.pasterdream.pasterdreammod.pasterdreamsanity.config.PDSanityConfig;
import com.pasterdream.pasterdreammod.pasterdreamsanity.registry.PDSanityEffects;
import com.pasterdream.pasterdreammod.pasterdreamsanity.registry.PDSanityItems;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

/**
 * PasterDreamSanity 模组主类。
 * <p>
 * 负责注册 San 值系统相关的物品、效果、事件与配置。
 * 仅依赖 PasterDreamAPI，不反向依赖 PasterDream 主模组。
 *
 * @author PasterDream
 */
@Mod(PasterDreamSanityMod.MOD_ID)
public class PasterDreamSanityMod {

    /** San 值系统附属模组 ID */
    public static final String MOD_ID = "pasterdreamsanity";

    /**
     * 构造函数。
     *
     * @param modEventBus  NeoForge 模组事件总线
     * @param modContainer 当前模组容器
     */
    public PasterDreamSanityMod(IEventBus modEventBus, ModContainer modContainer) {
        // PasterDreamAPI 已作为独立前置 mod 加载，由其主类 PasterDreamAPIMod 统一注册 API 层 DeferredRegister。

        PDSanityItems.ITEMS.register(modEventBus);
        PDSanityEffects.MOB_EFFECTS.register(modEventBus);

        modContainer.registerConfig(ModConfig.Type.COMMON, PDSanityConfig.SPEC);

        modEventBus.addListener(this::onCommonSetup);

        PDDebugLogger.mainInfo("PasterDreamSanity 模组已初始化");
    }

    /**
     * 公共设置阶段：向 API 注册 San 系统配置实现。
     *
     * @param event 公共设置事件
     */
    private void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> SanConfigRegistry.register(PDSanityConfig.getInstance()));
    }
}
