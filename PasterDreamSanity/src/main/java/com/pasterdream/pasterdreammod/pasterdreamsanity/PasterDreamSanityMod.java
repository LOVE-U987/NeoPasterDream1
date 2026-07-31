package com.pasterdream.pasterdreammod.pasterdreamsanity;

import com.pasterdream.pasterdreammod.api.config.PDAddonConfigRegistry;
import com.pasterdream.pasterdreammod.api.san.SanConfigRegistry;
import com.pasterdream.pasterdreammod.api.util.PDDebugLogger;
import com.pasterdream.pasterdreammod.pasterdreamsanity.config.PDSanityConfig;
import com.pasterdream.pasterdreammod.pasterdreamsanity.registry.PDSanityEffects;
import com.pasterdream.pasterdreammod.pasterdreamsanity.registry.PDSanityItems;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ConfigTracker;
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

    /** 通用配置的 ModConfig 引用，供配置界面持久化保存 */
    public static ModConfig commonModConfig;

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

        // 使用 ConfigTracker 注册并捕获 ModConfig 引用，以便主模组配置界面保存到 TOML
        commonModConfig = ConfigTracker.INSTANCE.registerConfig(ModConfig.Type.COMMON, PDSanityConfig.SPEC, modContainer, "pasterdreamsanity-common.toml");
        PDAddonConfigRegistry.registerCommonConfig(MOD_ID, commonModConfig);

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
