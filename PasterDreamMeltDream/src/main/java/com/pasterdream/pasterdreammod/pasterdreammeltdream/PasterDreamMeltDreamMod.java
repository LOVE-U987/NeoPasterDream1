package com.pasterdream.pasterdreammod.pasterdreammeltdream;

import com.pasterdream.pasterdreammod.api.meltdream.MeltDreamEnergyConfigRegistry;
import com.pasterdream.pasterdreammod.api.util.PDDebugLogger;
import com.pasterdream.pasterdreammod.pasterdreammeltdream.config.PDMeltDreamConfig;
import com.pasterdream.pasterdreammod.pasterdreammeltdream.registry.PDMeltDreamEffects;
import com.pasterdream.pasterdreammod.pasterdreammeltdream.registry.PDMeltDreamItems;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

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

    /**
     * 构造函数。
     *
     * @param modEventBus  NeoForge 模组事件总线
     * @param modContainer 当前模组容器
     */
    public PasterDreamMeltDreamMod(IEventBus modEventBus, ModContainer modContainer) {
        // PasterDreamAPI 已作为独立前置 mod 加载，由其主类 PasterDreamAPIMod 统一注册 API 层 DeferredRegister。

        PDMeltDreamItems.ITEMS.register(modEventBus);
        // PDMeltDreamEffects.MOB_EFFECTS 引用的是 MobEffectAPI.REGISTRY（API 共享注册表），
        // 该注册表已由 PasterDreamAPI 主类注册到 API 的事件总线，此处不再重复注册。
        // 效果条目通过 MobEffectAPI.REGISTRY.register() 直接注册到共享注册表。

        modContainer.registerConfig(ModConfig.Type.COMMON, PDMeltDreamConfig.SPEC);

        modEventBus.addListener(this::onCommonSetup);

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
