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
import net.neoforged.neoforge.common.NeoForge;

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

        // 理智环境 tick（San 逐 tick 变化 + 低/高 San 效果 + 环境 SAN_VARIABILITY 修饰符刷新）：
        // 主模组已注释迁移至此模块，此处必须挂到游戏总线，否则 San 值永不变动（饰品的
        // SAN_VARIABILITY 属性无人消费）。挂在 NeoForge.EVENT_BUS（PlayerTickEvent.Post 为游戏总线事件）。
        NeoForge.EVENT_BUS.addListener(PDSanityHelper::onPlayerTick);

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
