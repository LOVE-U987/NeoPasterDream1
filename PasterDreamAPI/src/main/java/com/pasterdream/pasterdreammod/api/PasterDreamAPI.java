package com.pasterdream.pasterdreammod.api;

import com.pasterdream.pasterdreammod.api.block.BlockAPI;
import com.pasterdream.pasterdreammod.api.blockentity.BlockEntityAPI;
import com.pasterdream.pasterdreammod.api.curio.CurioAPI;
import com.pasterdream.pasterdreammod.api.effect.MobEffectAPI;
import com.pasterdream.pasterdreammod.api.entity.EntityAPI;
import com.pasterdream.pasterdreammod.api.fluid.FluidAPI;
import com.pasterdream.pasterdreammod.api.fluid.FluidTypeAPI;
import com.pasterdream.pasterdreammod.api.item.ItemAPI;
import com.pasterdream.pasterdreammod.api.menu.MenuAPI;
import com.pasterdream.pasterdreammod.api.particle.ParticleAPI;
import com.pasterdream.pasterdreammod.api.ruin.RuinAPI;
import com.pasterdream.pasterdreammod.api.worldgen.decor.DecorationRegistry;
import net.neoforged.bus.api.IEventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PasterDreamAPI 前置模组常量定义。
 * 提供 MOD_ID、LOGGER 以及统一的注册入口 {@link #registerAll(IEventBus)}，
 * 供所有 API 子模块和主模组使用。
 * 主模组应引用此常量确保命名空间一致，并在构造器中调用 {@code registerAll}。
 */
public final class PasterDreamAPI {

    /** 模组命名空间，须与主模组 {@code PasterDreamAPI.MOD_ID} 一致 */
    public static final String MOD_ID = "pasterdream";

    /** API 模块日志记录器 */
    public static final Logger LOGGER = LoggerFactory.getLogger("PasterDreamAPI");

    private PasterDreamAPI() {
        throw new UnsupportedOperationException("PasterDreamAPI 是常量类，不可实例化");
    }

    /**
     * 统一注册 PasterDreamAPI 模块下所有 DeferredRegister。
     * <p>
     * 主模组应在构造器开头调用此方法，替代分别调用各 API 注册器的 {@code register(modEventBus)}。
     * 调用后仍需确保主模块的注册类（如 {@code PDBlocks}、{@code PDEntities}、
     * {@code PDParticles}、{@code PDItems}、{@code PDEffects}）在注册事件触发前被加载，
     * 以便其静态字段将条目填充到对应的 DeferredRegister 中。
     *
     * @param modEventBus NeoForge 模组事件总线
     */
    public static void registerAll(IEventBus modEventBus) {
        BlockAPI.REGISTRY.register(modEventBus);
        BlockEntityAPI.REGISTRY.register(modEventBus);
        ItemAPI.REGISTRY.register(modEventBus);
        EntityAPI.REGISTRY.register(modEventBus);
        MobEffectAPI.REGISTRY.register(modEventBus);
        ParticleAPI.REGISTRY.register(modEventBus);
        RuinAPI.REGISTRY.register(modEventBus);
        CurioAPI.REGISTRY.register(modEventBus);
        MenuAPI.REGISTRY.register(modEventBus);
        FluidAPI.REGISTRY.register(modEventBus);
        FluidTypeAPI.REGISTRY.register(modEventBus);
        ApiSoundRegistry.DIMENSION_SOUNDS.register(modEventBus);
        DecorationRegistry.FEATURES.register(modEventBus);

        LOGGER.debug("[PasterDreamAPI] 已统一注册 13 个 API 注册器到事件总线");
    }
}
