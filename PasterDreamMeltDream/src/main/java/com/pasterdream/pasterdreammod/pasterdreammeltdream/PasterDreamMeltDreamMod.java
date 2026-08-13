package com.pasterdream.pasterdreammod.pasterdreammeltdream;

import com.pasterdream.pasterdreammod.api.config.PDAddonConfigRegistry;
import com.pasterdream.pasterdreammod.api.meltdream.MeltDreamEnergyConfigRegistry;
import com.pasterdream.pasterdreammod.api.util.PDDebugLogger;
import com.pasterdream.pasterdreammod.pasterdreammeltdream.config.PDMeltDreamConfig;
import com.pasterdream.pasterdreammod.pasterdreammeltdream.registry.PDMeltDreamEffects;
import com.pasterdream.pasterdreammod.pasterdreammeltdream.registry.PDMeltDreamItems;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ConfigTracker;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;

/**
 * PasterDreamMeltDream 模组主类。
 * <p>
 * 负责注册融梦能量系统相关的物品、效果、事件与配置。
 * 仅依赖 PasterDreamAPI，不反向依赖 PasterDream 主模组。
 *
 * @author PasterDream
 */
@Mod(PasterDreamMeltDreamMod.MOD_ID)
public class PasterDreamMeltDreamMod {

    /** 融梦能量系统附属模组 ID */
    public static final String MOD_ID = "pasterdreammeltdream";

    /** 通用配置的 ModConfig 引用，供配置界面持久化保存 */
    public static ModConfig commonModConfig;

    /**
     * 构造函数。
     *
     * @param modEventBus  NeoForge 模组事件总线
     * @param modContainer 当前模组容器
     */
    public PasterDreamMeltDreamMod(IEventBus modEventBus, ModContainer modContainer) {
        // PasterDreamAPI 已作为独立前置 mod 加载，由其主类 PasterDreamAPIMod 统一注册 API 层 DeferredRegister。

        PDMeltDreamItems.ITEMS.register(modEventBus);
        // PDMeltDreamEffects 的条目挂在 MobEffectAPI.REGISTRY（API 共享、pasterdream 命名空间）上，
        // 注册表本身已由 PasterDreamAPI 挂到事件总线；此处必须触发类初始化，
        // 否则 DeferredHolder 静态字段不会执行 register()，效果会整段缺失。
        PDMeltDreamEffects.init();

        // 使用 ConfigTracker 注册并捕获 ModConfig 引用，以便主模组配置界面保存到 TOML
        commonModConfig = ConfigTracker.INSTANCE.registerConfig(ModConfig.Type.COMMON, PDMeltDreamConfig.SPEC, modContainer, "pasterdreammeltdream-common.toml");
        PDAddonConfigRegistry.registerCommonConfig(MOD_ID, commonModConfig);

        modEventBus.addListener(this::onCommonSetup);

        // 注册融梦能量自然恢复的玩家 tick / 登录事件（服务端）
        NeoForge.EVENT_BUS.addListener(PDMeltDreamEvents::onPlayerTick);
        NeoForge.EVENT_BUS.addListener(PDMeltDreamEvents::onPlayerLoggedIn);

        PDDebugLogger.mainInfo("PasterDreamMeltDream 模组已初始化");
    }

    /**
     * 公共设置阶段：向 API 注册融梦能量系统配置实现。
     *
     * @param event 公共设置事件
     */
    private void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> MeltDreamEnergyConfigRegistry.register(PDMeltDreamConfig.getInstance()));
    }
}
