package com.pasterdream.pasterdreammod.pasterdreamspells;

import com.pasterdream.pasterdreammod.api.util.PDDebugLogger;
import com.pasterdream.pasterdreammod.pasterdreamspells.client.PDSpellsClientSetup;
import com.pasterdream.pasterdreammod.pasterdreamspells.config.PDSpellsConfig;
import com.pasterdream.pasterdreammod.pasterdreamspells.registry.PDSpellsEffects;
import com.pasterdream.pasterdreammod.pasterdreamspells.registry.PDSpellsEntities;
import com.pasterdream.pasterdreammod.pasterdreamspells.registry.PDSpellsItems;
import com.pasterdream.pasterdreammod.pasterdreamspells.registry.PDSpellsParticles;
import com.pasterdream.pasterdreammod.pasterdreamspells.registry.PDSpellsSounds;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;

/**
 * PasterDreamSpells 模组主类。
 * <p>
 * 负责注册法术系统相关的物品、效果、实体、事件与配置。
 * 仅依赖 PasterDreamAPI，可选依赖 PasterDreamSanity / PasterDreamMeltDream。
 *
 * @author PasterDream
 */
@Mod(PasterDreamSpellsMod.MOD_ID)
public class PasterDreamSpellsMod {

    /** 法术系统附属模组 ID */
    public static final String MOD_ID = "pasterdreamspells";

    /**
     * 构造函数。
     *
     * @param modEventBus  NeoForge 模组事件总线
     * @param modContainer 当前模组容器
     */
    public PasterDreamSpellsMod(IEventBus modEventBus, ModContainer modContainer) {
        // PasterDreamAPI 已作为独立前置 mod 加载，由其主类 PasterDreamAPIMod 统一注册 API 层 DeferredRegister。

        PDSpellsItems.ITEMS.register(modEventBus);
        PDSpellsEffects.MOB_EFFECTS.register(modEventBus);
        PDSpellsEntities.ENTITY_TYPES.register(modEventBus);
        // PDSpellsParticles.PARTICLE_TYPES 即 ParticleAPI.REGISTRY，已由 PasterDreamAPI.registerAll() 统一注册
        // 但仍需触发 PDSpellsParticles.<clinit> 确保粒子在 RegisterEvent 前填充到注册器
        Object unusedSpellsParticles = PDSpellsParticles.POISON_GAS_PARTICLE;
        PDSpellsSounds.SOUND_EVENTS.register(modEventBus);

        modContainer.registerConfig(ModConfig.Type.COMMON, PDSpellsConfig.SPEC);

        // 客户端事件注册（替代已弃用的 @EventBusSubscriber(bus = Bus.MOD, value = Dist.CLIENT)）
        if (FMLEnvironment.dist == Dist.CLIENT) {
            modEventBus.addListener(PDSpellsClientSetup::onRegisterParticleProviders);
            modEventBus.addListener(PDSpellsClientSetup::onRegisterEntityRenderers);
        }

        modEventBus.addListener(this::onCommonSetup);

        PDDebugLogger.mainInfo("PasterDreamSpells 模组已初始化");
    }

    /**
     * 公共设置阶段：注册法术到 SpellAPI。
     *
     * @param event 公共设置事件
     */
    private void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(PDSpellsItems::registerSpells);
    }
}
